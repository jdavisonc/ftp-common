package com.superdownloader.common.ftp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.commons.net.io.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.superdownloader.common.ftp.filter.DirectoryFileFilter;
import com.superdownloader.common.ftp.filter.NormalFileFilter;

/**
 * Implementation of {@link FtpUploader} with Apache Commons NET Library
 * @author harley
 *
 */
public class FtpUploaderCommons implements FtpUploader {

	private final static Logger LOGGER = LoggerFactory.getLogger(FtpUploaderCommons.class);

	private final static int TIMEOUT = 2 * 60 * 1000;

	private final static FileFilter directoryFileFilter = new DirectoryFileFilter();

	private final static FileFilter normalFileFilter = new NormalFileFilter();

	private String server;

	private String username;

	private String password;

	private String remotePath;

	private final FTPClient ftpClient;

	private String type;

	public FtpUploaderCommons() {
		ftpClient = new FTPClient();
	}

	@Override
	public void configure(String server, String username, String password, String remotePath) {
		this.server = server;
		this.username = username;
		this.password = password;
		this.remotePath = remotePath;
	}

	@Override
	public void connect() {
		try {

			ftpClient.setDataTimeout(TIMEOUT);
			ftpClient.setConnectTimeout(TIMEOUT);
			ftpClient.setDefaultTimeout(TIMEOUT);

			ftpClient.connect(server);
			ftpClient.enterLocalPassiveMode();
			ftpClient.login(username, password);

			int reply = ftpClient.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply)){

				type = ftpClient.getSystemName();
				if (type == null) {
					type = "UNIX";
				}

				if (remotePath != null) {
					LOGGER.debug("Moving to directory {}", remotePath);
					ftpClient.changeWorkingDirectory(remotePath);
				}
				// Set ftp client configurations
				ftpClient.setSoTimeout(TIMEOUT);
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			} else {
				ftpClient.disconnect();
				throw new Exception("Login Fail");
			}
		} catch (Exception e) {
			throw new RuntimeException("Error at connect to the server.", e);
		}
	}

	@Override
	public void disconnect() {
		try {
			ftpClient.logout();
			if (ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		} catch (IOException e) {
			LOGGER.warn("Error at closing the connection", e);
		}
	}

	@Override
	public void abort() {
		try {
			ftpClient.abort();
		} catch (Exception e) {
			throw new RuntimeException("There was an error at aborting", e);
		}
	}

	@Override
	public void upload(File fileToUpload, FtpUploaderListener listener){
		try {
			Map<String, Long> filesInServer = listFiles();
			if (fileToUpload.isDirectory()) {
				uploadDirectory(fileToUpload, filesInServer, listener);
			} else {
				uploadFile(fileToUpload, filesInServer, listener);
			}
		} catch (Exception e) {
			throw new RuntimeException("There was an error at uploading the file", e);
		}
	}

	private void uploadDirectory(File directoryToUpload, Map<String, Long> filesInServer, FtpUploaderListener listener) throws Exception {
		// Create the directory if it was not created
		boolean exist = filesInServer.containsKey(directoryToUpload.getName());
		if (!exist) {
			try {
				LOGGER.debug(""+ftpClient.makeDirectory(directoryToUpload.getName()));
				LOGGER.debug("Directory created! {}", directoryToUpload.getName());
			} catch (Exception e){
				LOGGER.debug("Could not create directory, may be it already exist");
			}
		}
		// Change to the directory
		LOGGER.debug("Moving to directory {}", directoryToUpload.getName());
		ftpClient.changeWorkingDirectory(directoryToUpload.getName());

		// Check the files inside the directory
		Map<String, Long> filesInServerDirectory;
		if (!exist){
			filesInServerDirectory = Collections.emptyMap();
		}else{
			filesInServerDirectory = listFiles();
		}

		// Upload all directories first
		for (File childFile : directoryToUpload.listFiles(directoryFileFilter)){
			uploadDirectory(childFile, filesInServerDirectory, listener);
		}
		// Upload all files
		for (File childFile : directoryToUpload.listFiles(normalFileFilter)){
			uploadFile(childFile, filesInServerDirectory, listener);
		}

		// Back to the original directory
		LOGGER.debug("Moving to directory up");
		ftpClient.changeToParentDirectory();
	}

	private void uploadFile(File fileToUpload, Map<String, Long> filesListInServer, FtpUploaderListener listener) throws Exception {
		String fileName = fileToUpload.getName();
		Long size = filesListInServer.get(fileName);
		if (size != null && size != 0L) {
			// Tell listener that already exist and tranfers (part) of the file
			listener.bytesTransferred(size);
			if (size == fileToUpload.length()) {
				LOGGER.debug("File already exists {}", fileName);
			} else {
				LOGGER.debug("Resuming file {} from {}MB", fileName, (size / (1024*1024)));
				// Set the offset
				ftpClient.setRestartOffset(size);
				// Create stream and skip first SIZE bytes
				InputStream ins = new FileInputStream(fileToUpload);
				ins.skip(size);
				// Upload file
				storeFile(fileName, ins, fileToUpload.length()-size, listener);

				LOGGER.debug("File {} successfully uploaded", fileName);
			}
		} else {
			storeFile(fileName, new FileInputStream(fileToUpload), fileToUpload.length(), listener);
			LOGGER.debug("File {} successfully uploaded", fileName);
		}
	}

	private void storeFile(String fileName, InputStream ins, Long size, final FtpUploaderListener listener) throws IOException {
		OutputStream outs = ftpClient.storeFileStream(fileName);

		CopyStreamAdapter adapter = null;
		if (listener != null) {
			adapter = new CopyStreamAdapter() {
				@Override
				public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
					listener.bytesTransferred(bytesTransferred);
				}
			};
		}

		try {
			Util.copyStream(ins, outs, ftpClient.getBufferSize(), size, adapter);
		} finally {
			outs.close();
			ins.close();
		}

		try {
			if (!ftpClient.completePendingCommand()) {
				throw new RuntimeException("Transfer failed.");
			}
		} catch (MalformedServerReplyException e) {
			if (!e.getMessage().contains("OK")) {
				throw e;
			}
		}
	}


	/**
	 * Auxiliar methods
	 */
	private Map<String, Long> listFiles(){
		int attempts = 0;
		Map<String, Long> files = new LinkedHashMap<String, Long>();
		while (true){
			try {
				FTPListParseEngine engine = null;
				if (type.startsWith("UNIX")) {
					engine = ftpClient.initiateListParsing(FTPClientConfig.SYST_UNIX, null);
				} else {
					engine = ftpClient.initiateListParsing();
				}

				FTPFile[] list = engine.getFiles();
				if (list != null){
					for (FTPFile ftpFile : list){
						files.put(ftpFile.getName(), ftpFile.getSize());
					}
				}
				return files;
			} catch (Exception e) {
				attempts++;
				if (attempts > 3) {
					LOGGER.warn("Error at listing ftp server files", e);
					throw new RuntimeException("Error at listing ftp server files", e);
				} else {
					LOGGER.debug("First attempt to get list of files FAILED! attempt={}", attempts);
				}
			}
		}
	}

}
