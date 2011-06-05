package com.superdownloader.common.ftp;

import java.io.File;

/**
 * Interface to implement a FTP Client
 * @author harley
 *
 */
public interface FtpUploader {
	
	public void configure(String server, String username, String password, String remotePath);
	
	public void connect();
	
	public void disconnect();
	
	public void abort();
	
	public void upload(File fileToUpload);

}
