/*******************************************************************************
 * FtpUploader.java
 * 
 * Copyright (c) 2012 Team SeedBoxer.
 * 
 * This file is part of SeedBoxer FTPCommon.
 * 
 * SeedBoxer FTPCommon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SeedBoxer FTPCommon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SeedBoxer FTPCommon.  If not, see <http ://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.seedboxer.common.ftp;

import java.io.File;

import net.seedboxer.common.ftp.exception.FtpException;


/**
 * Interface to implement a FTP Client
 * 
 * @author Jorge Davison (jdavisonc)
 *
 */
public interface FtpUploader {

	public void configure(String server, String username, String password, String remotePath, boolean ssl) throws Exception ;

	public void connect() throws FtpException;

	public void disconnect() throws FtpException;

	public void abort() throws FtpException;

	public void upload(File fileToUpload, FtpUploaderListener listener) throws FtpException;

}
