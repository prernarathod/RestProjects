/**
 * 
 */
package com.restproject.controller;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.restproject.common.Constants;
import com.restproject.common.Utility;
import com.restproject.model.FileInfo;

/**
 * @author prerna.rathore
 *
 */
@RestController
public class FileUploadController {
	private static final String EndCharacter = "\r\n";
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);
	private static final String FILENAME = "fileName";

	@Autowired
	ServletContext context;

	@RequestMapping(value = "/uploadmultiplefiles", method = RequestMethod.POST)
	public ResponseEntity<FileInfo[]> upload(HttpServletRequest request, @RequestParam("resource") CommonsMultipartFile[] files) {
		ResponseEntity<FileInfo[]> response = null;
		FileInfo[] ulrMap = new FileInfo[files.length];
		HttpHeaders headers = new HttpHeaders();
		String originalFileName = request.getParameter(FILENAME).replace(EndCharacter, "");
		LOGGER.info("Recieved file name: {}",originalFileName);
		int index = 0;
		try {
			for (CommonsMultipartFile inputFile : files) {
				FileInfo fileInfo = new FileInfo();
				String filePersistName = Utility.getPersistedFileId(originalFileName);
				File destinationFile = new File(Constants.RESOURCE_FOLDER_BASE_PATH + filePersistName);
				LOGGER.debug("Persisting file at : {}, path: {}",filePersistName, destinationFile);
				if (!destinationFile.exists()) {
					boolean fileCreated = destinationFile.mkdirs();
					if(!fileCreated){
						LOGGER.error("Unable to create file :{}", destinationFile.getAbsoluteFile());
						throw new Exception("Failed to create file: "+originalFileName);
					}
				}
				LOGGER.debug("Transferring data to new file");
				inputFile.transferTo(destinationFile);
				LOGGER.debug("Transfer completed...");
				fileInfo.setFileName(originalFileName);
				fileInfo.setFileSize(inputFile.getSize());
				fileInfo.setUri(filePersistName);
				ulrMap[index] = fileInfo;
				index++;
			}
			LOGGER.debug("Sending response to Client: {}",HttpStatus.OK);
			response = new ResponseEntity<>(ulrMap, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception occured while uploading Exception:{}", e);
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return response;
	}
	
	@RequestMapping(value = "/uploadsinglefile", method = RequestMethod.POST)
	public ResponseEntity<FileInfo> uploadSingleFile(HttpServletRequest request, @RequestParam("resource") CommonsMultipartFile inputFile) {
		ResponseEntity<FileInfo> response = null;
		HttpHeaders headers = new HttpHeaders();
		FileInfo fileInfo = new FileInfo();
		String originalFileName = request.getParameter(FILENAME).replace(EndCharacter, "");
		LOGGER.info("Recieved file name: {}",originalFileName);
		try {
				if (!inputFile.isEmpty()) {

					File destinationFile = new File(context.getRealPath("/resources")+ File.separator + originalFileName);
					if (!destinationFile.exists()) {
						boolean fileCreated = destinationFile.mkdirs();
						if(!fileCreated){
							LOGGER.error("Unable to create file :{}", destinationFile.getAbsoluteFile());
							throw new Exception("Failed to create file:"+originalFileName);
						}
					}
					LOGGER.debug("Transferring data to new file");
					inputFile.transferTo(destinationFile);
					LOGGER.debug("Transfer completed...");
					fileInfo.setFileName(originalFileName);
					fileInfo.setFileSize(inputFile.getSize());
					fileInfo.setUri(Utility.getURIForResource(originalFileName, request));
				} else {
					throw new IllegalAccessException("No File Found");
				}
			response = new ResponseEntity<>(fileInfo, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception occured while uploading Exception:{}", e);
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return response;
	}
}
