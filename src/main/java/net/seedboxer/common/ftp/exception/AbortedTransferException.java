/*******************************************************************************
 * AbortedTransferException.java
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
package net.seedboxer.common.ftp.exception;

/**
 * 
 * @author Jorge Davison (jdavisonc)
 *
 */
public class AbortedTransferException extends RuntimeException {

	private static final long serialVersionUID = 835630742861242296L;

	public AbortedTransferException() { }

	@Override
	public String getMessage() {
		return "The transfer was aborted";
	}

}
