/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 	    Raj Kuttan
 * 		Lori Phillips
 * 		Wensong Pan (ported it here from ontology plug-in)
 */

package edu.harvard.i2b2.eclipse.plugins.explorer.ontologyMessaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ModifyChildType;

/**
 * @author Lori Phillips
 *
 */
public class ModifyChildRequestMessage extends OntologyRequestData {
	
	public static final String THIS_CLASS_NAME = ModifyChildRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

	public ModifyChildRequestMessage() {}
	
	/**
	 * Function to build addChild type for a given request
	 * 
	 * @return FolderType object
	 */
	public ModifyChildType getChildType() { 
		ModifyChildType childType = new ModifyChildType();		
		return childType;
	}
	
	
	/**
	 * Function to build ont Data body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType() {
		ModifyChildType childType = getChildType();
		edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createModifyChild(childType));
		return bodyType;
	}
	
	/**
	 * Function to build  body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(ModifyChildType childType) {
		edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createModifyChild(childType));
		return bodyType;
	}

	/**
	 * Function to build Ont Request message type and return it as an XML string
	 * 
	 * @param ConceptType childData (modify this child node)
	 * @return A String data type containing the Ont RequestMessage in XML format
	 */
	public String doBuildXML(ModifyChildType childData){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(childData) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 
		return requestString;
	}
}


	
	