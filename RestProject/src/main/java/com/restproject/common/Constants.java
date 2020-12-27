/**
 * 
 */
package com.restproject.common;

import java.io.File;
import java.nio.file.Paths;

/**
 * @author prerna.rathore
 *
 */
public class Constants {

	private Constants() {

	}

	
	
	public static final String FILE_RESOURCEDOWNLOAD_URL = "/resourcedownload";
	public static final String FILE_URI = "/file";
	public static final String DOWNLOAD_URI_SUFFIX = ".";
	public static final String FILENAMESEPARATOR = "_";
	public static final String RESOURCE_FOLDER_BASE_PATH = Paths.get("..", "RestProject", "webapps", "resources", "resource").toString() + File.separator;
	public static final String POST_PROCESS_FILE_HANDLE = "/postprocessfilehandle";
}
