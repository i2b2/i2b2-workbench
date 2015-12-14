/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.workplace.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.ExportChildType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.DeleteChildType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.RenameChildType;


/**
 * @author Lori Phillips
 *
 */
public class ExportChildRequestMessage extends WorkplaceRequestData {
	
	public static final String THIS_CLASS_NAME = ExportChildRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

    private ExportChildType exportChildType;

	public ExportChildRequestMessage() {}
	
	/**
	 * Function to build deleteChild type for a given request
	 * 
	 * @return DeleteChildType object
	 */
	public ExportChildType exportChildType() { 
		ExportChildType childType = new ExportChildType();		
		return childType;
	}
	
	
	/**
	 * Function to build workData body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType() {
		ExportChildType childType = new ExportChildType();
		edu.harvard.i2b2.wkplclient.datavo.wdo.ObjectFactory of = new edu.harvard.i2b2.wkplclient.datavo.wdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createExportChild(childType));
		return bodyType;
	}
	
	/**
	 * Function to build workData body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(ExportChildType childType) {
		edu.harvard.i2b2.wkplclient.datavo.wdo.ObjectFactory of = new edu.harvard.i2b2.wkplclient.datavo.wdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createExportChild(childType));
		return bodyType;
	}

	/**
	 * Function to build Work Request message type and return it as an XML string
	 * 
	 * @param DeleteChildType nodeData (delete this node)
	 * @return A String data type containing the Work RequestMessage in XML format
	 */
	public String doBuildXML(ExportChildType nodeData){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(nodeData) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 
		return requestString;
	}
}


	
	