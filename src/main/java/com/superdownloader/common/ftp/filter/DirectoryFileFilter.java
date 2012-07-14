/**
 * 
 */
package com.superdownloader.common.ftp.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author harley
 *
 */
public class DirectoryFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}

}
