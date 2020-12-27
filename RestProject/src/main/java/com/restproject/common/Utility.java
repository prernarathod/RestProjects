/**
 * 
 */
package com.restproject.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author prerna.rathore
 *
 */
public class Utility {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Utility.class);
	
	private Utility(){
		boolean init = true;
		Runnable cleanupTask = () ->{
			try {
				Thread.sleep(ServiceConfiguration.getConfiguration().cleanupStartDelay);
			} catch (Exception e1) {
				LOGGER.error("Exception occured whille initializing cleanup thread: ",e1);
			}
			while(init) {
				try {
					cleanupOldResources();
					Thread.sleep(ServiceConfiguration.getConfiguration().cleanupIntervalInMillisec);
				} catch (Exception e) {
					LOGGER.error("Exception in cleanup thread resource hosting service: ",e);
				}
			}
		};
		Thread cleanupThread = new Thread(cleanupTask, "RESOURCEHOSTING_SERVICE_CLEANUP");
		cleanupThread.start();
	}
	
	private void cleanupOldResources() throws IOException {
		try {
			File resourseFolderPath = new File(Constants.RESOURCE_FOLDER_BASE_PATH);
			 if(resourseFolderPath.exists() && resourseFolderPath.isDirectory()) {
				 Instant instantDaysAgo = Instant.from(ZonedDateTime.now().minusDays(ServiceConfiguration.getConfiguration().persistentResourceDurationInDays));
	             BiPredicate<Path, BasicFileAttributes> fileIsOlderThanNDays = (path, attr) -> attr.lastModifiedTime().compareTo(FileTime.from(instantDaysAgo)) < 0;
	             try(Stream<Path> pathStream = Files.find(resourseFolderPath.toPath(), 1, fileIsOlderThanNDays)){
	            	 List<Path> filesToDelete = pathStream.collect(Collectors.toList());	            
		             for (Path path : filesToDelete) {
	                    Files.delete(path);
		             }
	             }
				 
			 }else {
	             LOGGER.info("Resourse folder path '{}' does not exist.",Constants.RESOURCE_FOLDER_BASE_PATH);
	      }
			
		}catch (Exception e) {
			LOGGER.error("Exception in cleaning resources: ",e);
		}
	}

	public static String getServiceConfigPath() {
		return Paths.get("..", "RestProject", "src", "main", "resources", "ServiceConfiguration.xml").toString();
	}
	
	public static String getPersistedFileId(String fileName){
		StringBuilder fileIdentifier = new StringBuilder(UUID.randomUUID().toString().replaceAll("-", ""));
		fileIdentifier.append(Constants.FILENAMESEPARATOR);
		fileIdentifier.append(fileName);
		return fileIdentifier.toString();
	}
	
	public static String getOriginalFileName(String fileName){
		int index = fileName.indexOf('_') + 1;
		return fileName.substring(index);
	}
	
	public static String getURIForResource(String resourceName, HttpServletRequest request) {
		StringBuilder prefix = new StringBuilder(request.getRequestURL().substring(0, request.getRequestURL().indexOf(request.getContextPath())));
		prefix.append(request.getContextPath());
		prefix.append(Constants.FILE_RESOURCEDOWNLOAD_URL);
		prefix.append(Constants.FILE_URI);
		prefix.append("/");
		prefix.append(resourceName);
		prefix.append(Constants.DOWNLOAD_URI_SUFFIX);
		return prefix.toString();
	}

	public static String getFileData(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder();
		try(BufferedReader dio = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))))) 
		{
			do {
				String line = dio.readLine();
				if(null == line) {
					break;
				}
				fileData.append(line);
			} while(true);
		} 
		catch (IOException e) 
		{
			LOGGER.error("getFileData: Exception for filePath : '{}' ", filePath);
			LOGGER.error("getFileData: Exception  '{}'",e);
			throw e;
		}
		return fileData.toString();
	}
	
}
