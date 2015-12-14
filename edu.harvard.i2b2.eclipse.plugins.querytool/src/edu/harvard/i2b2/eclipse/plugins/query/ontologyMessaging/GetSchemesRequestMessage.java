/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * 
 * 
 * Contributors:
 * 		Raj Kuttan
 * 		Lori Phillips
 * 		Wensong Pan (ported it here from ontology plug-in)
 */

package edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetReturnType;

public class GetSchemesRequestMessage extends OntologyRequestData {

	public static final String THIS_CLASS_NAME = GetSchemesRequestMessage.class
			.getName();
	private Log log = LogFactory.getLog(THIS_CLASS_NAME);

	public GetSchemesRequestMessage() {
	}

	/**
	 * Function to build get return type for a given request
	 * 
	 * @return GetReturnType object
	 */
	public GetReturnType getReturnType() {
		GetReturnType returnType = new GetReturnType();
		return returnType;
	}

	/**
	 * Function to build returnType body type
	 * 
	 * @param
	 * @return BodyType object
	 */

	public BodyType getBodyType() {
		GetReturnType returnType = getReturnType();
		edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();

		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetSchemes(returnType));
		return bodyType;
	}

	/**
	 * Function to build returnType body type
	 * 
	 * @param GetReturnType
	 *            returnType
	 * @return BodyType object
	 */

	public BodyType getBodyType(GetReturnType returnType) {
		edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();

		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createGetSchemes(returnType));
		return bodyType;
	}

	/**
	 * Function to build Ont Request message type and return it as an XML string
	 * 
	 * @param GetReturnType
	 *            returnData
	 * @return A String data type containing the Ont RequestMessage in XML
	 *         format
	 */
	public String doBuildXML(GetReturnType returnData) {
		String requestString = null;
		try {
			MessageHeaderType messageHeader = getMessageHeader();
			RequestHeaderType reqHeader = getRequestHeader();
			BodyType bodyType = getBodyType(returnData);
			RequestMessageType reqMessageType = getRequestMessageType(
					messageHeader, reqHeader, bodyType);
			requestString = getXMLString(reqMessageType);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());
		}
		return requestString;
	}

}
