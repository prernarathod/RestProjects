/**
 * 
 */
package com.restproject.model;


/**
 * @author prerna.rathore
 *
 */

public class FileInfo {

	private String fileName;
	private long fileSize;
	private String uri;
	private long expirationPeriod;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the expirationPeriod
	 */
	public long getExpirationPeriod() {
		return expirationPeriod;
	}

	/**
	 * @param expirationPeriod the expirationPeriod to set
	 */
	public void setExpirationPeriod(long expirationPeriod) {
		this.expirationPeriod = expirationPeriod;
	}
}