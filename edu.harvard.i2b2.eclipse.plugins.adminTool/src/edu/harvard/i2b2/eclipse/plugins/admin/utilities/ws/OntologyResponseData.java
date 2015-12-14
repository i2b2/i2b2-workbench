/*
 * Copyright (c) 2006-2007 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptsType;


abstract public class OntologyResponseData {

	public static final String THIS_CLASS_NAME = OntologyResponseData.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	
    private ResponseMessageType respMessageType = null;
    
	public OntologyResponseData() {}
	
	
	public StatusType processResult(String response){	
		StatusType status = null;
		try {
			JAXBElement jaxbElement = PFTJAXBUtil.getJAXBUtil().unMashallFromString(response);
			respMessageType  = (ResponseMessageType) jaxbElement.getValue();
			
			// Get response message status 
			ResponseHeaderType responseHeader = respMessageType.getResponseHeader();
			status = responseHeader.getResultStatus().getStatus();
			String procStatus = status.getType();
			String procMessage = status.getValue();
			
			if(procStatus.equals("ERROR")){
				log.info("Error reported by Ont web Service " + procMessage);				
			}
			else if(procStatus.equals("WARNING")){
				log.info("Warning reported by Ont web Service" + procMessage);
			}	
			
		} catch (JAXBUtilException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	public ConceptsType doReadConcepts(){
		ConceptsType concepts = null;
			try {
				BodyType bodyType = respMessageType.getMessageBody();
				JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
				concepts = (ConceptsType)helper.getObjectByClass(bodyType.getAny(), ConceptsType.class);
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
				return null;
			}
		return concepts;		
	}	
	
//	public String getXMLString(ConceptType concept)throws Exception{ 
//		StringWriter strWriter =  new StringWriter();
//		try {
//			PFTJAXBUtil.getJAXBOntUtil().marshaller((Object)concept, strWriter);
//		} catch (JAXBUtilException e) {
//			log.error("Error marshalling Ont concept");
//			throw new JAXBUtilException(e.getMessage(), e);
//		} 
//		return strWriter.toString();
//	}
}

	
