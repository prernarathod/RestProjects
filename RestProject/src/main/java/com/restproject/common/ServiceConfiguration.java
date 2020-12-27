/**
 * 
 */
package com.restproject.common;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author prerna.rathore
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "serviceConfiguration")
public class ServiceConfiguration {
	private static ServiceConfiguration config = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfiguration.class);
	
	private ServiceConfiguration(){}
	
	public static synchronized ServiceConfiguration getConfiguration() {
		if (config == null) {
			File file = new File(Utility.getServiceConfigPath());
			JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance(ServiceConfiguration.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				config = (ServiceConfiguration) jaxbUnmarshaller.unmarshal(file);
			} catch (JAXBException e) {
				LOGGER.error("Exception : ", e);
			}
		}
		return config;
	}
	
	@XmlElement(required = false)
	public long cleanupIntervalInMillisec = 1440 * 60000;// default 24 hours
	
	@XmlElement(required = false)
	public long cleanupStartDelay = 600000;// default 10 mins. delay after service Startup
	
	@XmlElement(required = false)
	public long persistentResourceDurationInDays = 30;//Default 30 days

}
