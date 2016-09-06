/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 	    Raj Kuttan
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.ontology.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetChildrenType;


/**
 * @author Lori Phillips
 *
 */
public class GetChildrenRequestMessage extends OntologyRequestData {
	
	public static final String THIS_CLASS_NAME = GetChildrenRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

    public GetChildrenRequestMessage() {}
	
	/**
	 * Function to build getChildren type for a given request
	 * 
	 * @return GetChildrenType object
	 */
	public GetChildrenType getChildrenType() { 
		GetChildrenType childrenType = new GetChildrenType();		
		return childrenType;
	}
	
	
	/**
	 * Function to build vocabData body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType() {
		GetChildrenType childrenType = getChildrenType();
		edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetChildren(childrenType));
		return bodyType;
	}
	
	/**
	 * Function to build vocabData body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(GetChildrenType childrenType) {
		edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetChildren(childrenType));
		return bodyType;
	}

	/**
	 * Function to build Ont Request message type and return it as an XML string
	 * 
	 * @param GetChildrenType parentData (get children of this parent node)
	 * @return A String data type containing the Ont RequestMessage in XML format
	 */
	public String doBuildXML(GetChildrenType parentData, String type){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				if(Boolean.parseBoolean(
						System.getProperty("OntDisableModifiers")) == true){
					messageHeader.getSendingApplication().setApplicationVersion("1.5");
				}
				 if(type.equals("EDIT")){
					 messageHeader.getSendingApplication().setApplicationVersion("1.5");
				 }
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


	
	