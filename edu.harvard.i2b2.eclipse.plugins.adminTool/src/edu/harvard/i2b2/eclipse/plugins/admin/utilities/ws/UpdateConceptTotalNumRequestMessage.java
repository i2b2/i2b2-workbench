/*
 * Copyright (c) 2006-2007 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetOntProcessStatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.UpdateConceptTotalNumType;

public class UpdateConceptTotalNumRequestMessage extends OntologyRequestData {
	public static final String THIS_CLASS_NAME = UpdateConceptTotalNumRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

    public UpdateConceptTotalNumRequestMessage() {}
	
	/**
	 * Function to build get return type for a given request
	 * 
	 * @return GetOntProcessStatusType object
	 */
	public UpdateConceptTotalNumType getVocabType() { 
		UpdateConceptTotalNumType vocabType = new UpdateConceptTotalNumType();		
		return vocabType;
	}
	
	
	
	/**
	 * Function to build GetOntProcessStatusType body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType() {
		UpdateConceptTotalNumType vocabType = getVocabType();
		edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createUpdateConceptTotalnum(vocabType));
		return bodyType;
	}
	
	/**
	 * Function to build GetOntProcessStatusType body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(UpdateConceptTotalNumType vocabType) {
		edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createUpdateConceptTotalnum(vocabType));
		return bodyType;
	}
	
	/**
	 * Function to build Ont Request message type and return it as an XML string
	 * 
	 * @param GetOntProcessStatusType 
	 * @return A String data type containing the Ont RequestMessage in XML format
	 */
	public String doBuildXML(UpdateConceptTotalNumType vocabData){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(vocabData) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 
		return requestString;
	}
}
