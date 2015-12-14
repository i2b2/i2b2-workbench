/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 * 		Raj Kuttan
 * 		Lori Phillips
 * 		Wensong Pan (ported it here from ontology plug-in)
 */

package edu.harvard.i2b2.eclipse.plugins.patientMapping.ontologyMessaging;

import java.io.StringWriter;
import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.patientMapping.datavo.PatientMappingJAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptsType;

abstract public class OntologyResponseData {

	public static final String THIS_CLASS_NAME = OntologyResponseData.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	
    private ResponseMessageType respMessageType = null;
    
	public OntologyResponseData() {}
	
	public StatusType processResult(String response){	
		StatusType status = null;
		try {
			JAXBElement jaxbElement = PatientMappingJAXBUtil.getJAXBUtil().unMashallFromString(response);
			respMessageType  = (ResponseMessageType) jaxbElement.getValue();
			
			// Get response message status 
			ResponseHeaderType responseHeader = respMessageType.getResponseHeader();
			status = responseHeader.getResultStatus().getStatus();
			String procStatus = status.getType();
			String procMessage = status.getValue();
			
			if(procStatus.equals("ERROR")){
				log.error("Error reported by Ont web Service " + procMessage);				
			}
			else if(procStatus.equals("WARNING")){
				log.error("Warning reported by Ont web Service" + procMessage);
			}	
			
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());
		}
		return status;
	}

	public ConceptsType doReadConcepts(){
		ConceptsType concepts = null;
		try {
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			if(bodyType != null)
				concepts = (ConceptsType)helper.getObjectByClass(bodyType.getAny(), ConceptsType.class);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());;
		}
		return concepts;		
	}	
	
	public String getXMLString(ConceptType concept)throws Exception{ 
		StringWriter strWriter =  new StringWriter();
		try {
			PatientMappingJAXBUtil.getJAXBUtil().marshaller(concept, strWriter);
		} catch (JAXBUtilException e) {
			log.error("Error marshalling Ont concept");
			throw new JAXBUtilException(e.getMessage(), e);
		} 
		return strWriter.toString();
	}
}

	
