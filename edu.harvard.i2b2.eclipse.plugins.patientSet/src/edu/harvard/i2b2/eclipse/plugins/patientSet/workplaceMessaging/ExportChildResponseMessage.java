/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.patientSet.workplaceMessaging;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.common.util.xml.XMLUtil;
import edu.harvard.i2b2.patientSet.datavo.PatientSetJAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.GetChildrenType;


/**
 * @author Lori Phillips
 *
 */
public class ExportChildResponseMessage extends WorkplaceResponseData {
	
	public static final String THIS_CLASS_NAME = ExportChildResponseMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

	public ExportChildResponseMessage() {}

	public String processBody(String response){	
		String status = null;
		try {
			JAXBElement jaxbElement = PatientSetJAXBUtil.getJAXBUtil().unMashallFromString(response);
			ResponseMessageType respMessageType  = (ResponseMessageType) jaxbElement.getValue();
			
			// Get response message status 
			BodyType responseHeader = respMessageType.getMessageBody(); //.getResponseHeader();
			JAXBElement jaxb = (JAXBElement) responseHeader.getAny().get(0); //.toString(); //.g.getResultStatus().getStatus();
			status = jaxb.getValue().toString(); //XMLUtil.convertDOMElementToString(jaxb);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());
		}
		return status;
	}

}
	
	