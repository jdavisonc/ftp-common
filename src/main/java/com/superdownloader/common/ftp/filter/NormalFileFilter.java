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
public class NormalFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isFile();
	}

}
