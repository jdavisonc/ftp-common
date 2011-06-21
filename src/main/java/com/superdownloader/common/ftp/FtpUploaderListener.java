package com.superdownloader.common.ftp;

public interface FtpUploaderListener {

	void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize);

}
