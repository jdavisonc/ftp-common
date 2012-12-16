/*******************************************************************************
 * FtpTransferException.java
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
package com.superdownloader.common.ftp.exception;


/**
 * 
 * @author Jorge Davison (jdavisonc)
 *
 */
public class FtpTransferException extends FtpException {

	private static final long serialVersionUID = 8481127136237928650L;

	public FtpTransferException(Exception e) {
		super(e);
	}

	public FtpTransferException() {
		super();
	}

	@Override
	public String getMessage() {
		return "There was an error at uploading the file";
	}

}
