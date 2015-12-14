/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;

import edu.harvard.i2b2.pm.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.pm.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.pm.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.pm.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.pm.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.pm.datavo.i2b2message.SecurityType;
import edu.harvard.i2b2.pm.datavo.pm.ConfigureType;
//import edu.harvard.i2b2.pm.datavo.pm.GetUserConfigurationType;
import edu.harvard.i2b2.pm.datavo.pm.RoleType;


public class GetUseridsRequestMessage extends ProjectManagementRequestData {
	
	public static final String THIS_CLASS_NAME = GetUseridsRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

    
    private RoleType UserConfigurationType;

    private SecurityType securityType = null;

	public GetUseridsRequestMessage(String uname, PasswordType pword, String project) {
		securityType = new SecurityType();
		PasswordType ptype = new PasswordType();
		ptype.setIsToken(pword.isIsToken());
		ptype.setTokenMsTimeout(pword.getTokenMsTimeout());
		ptype.setValue(pword.getValue());
		
		securityType.setPassword(ptype);
		securityType.setDomain(project);
		securityType.setUsername(uname);
	}
	
	/**
	 * Function to build getChildren type for a given request
	 * 
	 * @return GetConfigurationType object
	 */
	public ConfigureType ConfigureType() { 
		ConfigureType ConfigurationType = new ConfigureType();		
		return ConfigurationType;
	}
	
	/**
	 * Function to build vocabData body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType() {
		edu.harvard.i2b2.pm.datavo.pm.ObjectFactory of = new edu.harvard.i2b2.pm.datavo.pm.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetAllRole(UserConfigurationType));
		//bodyType.getAny().add(of.createConfigureType(ConfigurationType));
		return bodyType;
	}
	
	/**
	 * Function to build vocabData body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(RoleType UserConfigurationType) {
		edu.harvard.i2b2.pm.datavo.pm.ObjectFactory of = new edu.harvard.i2b2.pm.datavo.pm.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetAllRole(UserConfigurationType));
		return bodyType;
	}

	/**
	 * Function to build Ont Request message type and return it as an XML string
	 * 
	 * @param ConfigureType parentData (get children of this parent node)
	 * @return A String data type containing the Ont RequestMessage in XML format
	 */
	public String doBuildXML(RoleType parentData){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				
				messageHeader.setSecurity(securityType);
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(parentData) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 
		return requestString;
	}
}


	
	