package com.restproject.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restproject.common.Constants;
import com.restproject.common.Utility;

@RestController
@RequestMapping(Constants.FILE_RESOURCEDOWNLOAD_URL)
public class FileDownloadController {

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	private static final String EXPIRES = "Expires";

	private static final String NO_CACHE_NO_STORE_MUST_REVALIDATE = "no-cache, no-store, must-revalidate";

	private static final String CACHE_CONTROL = "Cache-Control";

	private static final String NO_CACHE = "no-cache";

	private static final String PRAGMA = "Pragma";

	private static final String CONTENT_TYPE = "Content-Type";

	private static final String GET = "GET";

	private static final String FILENAME2 = "filename=";
	
	private static final String CONTENT_DISPOSITION = "Content-Disposition";

	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

	private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	private static final String UPLOADED_FILE = "Uploaded_File";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadController.class);
	
	@Autowired
	ServletContext context;

	@RequestMapping(value = Constants.FILE_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> download(@RequestParam("fileName") String fileName) throws IOException {
		ResponseEntity<InputStreamResource> response = null;
		String finalFileName = fileName;
		File targetFile = new File(Constants.RESOURCE_FOLDER_BASE_PATH + finalFileName);
		LOGGER.debug("Looking for target file with ID: {}",finalFileName);
		LOGGER.debug("File exists:{}",targetFile.exists());
		if(targetFile.exists()){
			try {
				String originalFileName = Utility.getOriginalFileName(finalFileName);
				LOGGER.info("Original file name: {}",originalFileName);
				LOGGER.debug("");
				HttpHeaders headers = prepareHeader(finalFileName, originalFileName);
				headers.setContentLength(targetFile.length());
				response = new ResponseEntity<>(new InputStreamResource(new FileInputStream(targetFile)), headers, HttpStatus.OK);
			}catch(Exception e){
				LOGGER.error("Exception occured while uploading resource: {}, Exception:{}",finalFileName,e);
				response = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
			}

		} else {
			response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return response;
	}
	/**Method process post file handling request if persistSourceFile flag is false 
	 * 
	 * @param
	 * */
	@RequestMapping(value = Constants.POST_PROCESS_FILE_HANDLE, method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> postProcessFileHandle(@RequestParam("fileName") String fileName, @RequestParam("persistSourceFile") boolean persistSourceFile) throws IOException {
		ResponseEntity<String> response = null;
		String finalFileName = fileName;
		File targetFile = new File(Constants.RESOURCE_FOLDER_BASE_PATH + finalFileName);
		LOGGER.debug("Looking for target file with ID: {}",finalFileName);
		LOGGER.debug("File exists:{}",targetFile.exists());
		boolean fileDeleted = false;
		if(targetFile.exists()){
			try {
				if (!persistSourceFile) {
					fileDeleted = targetFile.delete();
					if (fileDeleted) {
						response = new ResponseEntity<>(HttpStatus.OK);
					} else {
						LOGGER.error("Requested file not deleted: {}", finalFileName);
						response = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
					}
				}
				else {
					response = new ResponseEntity<>(HttpStatus.OK);
				}
			} catch (Exception e) {
				LOGGER.error("Exception occured while processing delete operation on resource: {}, Exception:{}",
						finalFileName, e);
				response = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
			}

		} else {
			response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return response;
	}

	/**
	 * @param fileName
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private HttpHeaders prepareHeader(String fileName, String originalFileName) throws UnsupportedEncodingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(APPLICATION_OCTET_STREAM));
		headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		headers.add(ACCESS_CONTROL_ALLOW_METHODS, GET);
		headers.add(ACCESS_CONTROL_ALLOW_HEADERS, CONTENT_TYPE);
		headers.add(CONTENT_DISPOSITION, FILENAME2 + fileName);
		headers.add(CACHE_CONTROL, NO_CACHE_NO_STORE_MUST_REVALIDATE);
		headers.add(PRAGMA, NO_CACHE);
		headers.add(EXPIRES, "0");
		String orgFileName= URLEncoder.encode(originalFileName, "UTF-8");
		headers.add(UPLOADED_FILE, orgFileName);
		return headers;
	}
}  