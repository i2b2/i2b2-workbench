/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Raj Kuttan
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
import edu.harvard.i2b2.ontclient.datavo.vdo.GetTermInfoType;

public class GetTermInfoRequestMessage extends OntologyRequestData {
	public static final String THIS_CLASS_NAME = GetTermInfoRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

    public GetTermInfoRequestMessage() {}
	
	/**
	 * Function to build getTermInfo type for a given request
	 * 
	 * @return GetTermInfoType object
	 */
	public GetTermInfoType getTermInfoType() { 
		GetTermInfoType TermInfoType = new GetTermInfoType();		
		return TermInfoType;
	}
		
	/**
	 * Function to build body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType() {
		GetTermInfoType TermInfoType = getTermInfoType();
		edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetTermInfo(TermInfoType));
		return bodyType;
	}
	
	/**
	 * Function to build body type
	 * 
	 * @param GetTermInfoType TermInfoType
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(GetTermInfoType TermInfoType) {
		edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetTermInfo(TermInfoType));
		return bodyType;
	}

	/**
	 * Function to build Ont Request message type and return it as an XML string
	 * 
	 * @param GetTermInfoType self (get TermInfo of this node)
	 * @return A String data type containing the Ont RequestMessage in XML format
	 */
	public String doBuildXML(GetTermInfoType self){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(self) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 
		return requestString;
	}
}
