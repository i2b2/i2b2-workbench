/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Raj Kuttan
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.ontology.ws;

import java.io.StringWriter;
import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.OntologyJAXBUtil;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.ResponseHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.ontclient.datavo.vdo.DirtyValueType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifiersType;
import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyProcessStatusListType;
import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyProcessStatusType;

abstract public class OntologyResponseData {

	public static final String THIS_CLASS_NAME = OntologyResponseData.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	
    private ResponseMessageType respMessageType = null;
    
	public OntologyResponseData() {}
	
	public StatusType processResult(String response){	
		StatusType status = null;
		try {
			JAXBElement jaxbElement = OntologyJAXBUtil.getJAXBUtil().unMashallFromString(response);
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
			OntologyJAXBUtil.getJAXBUtil().marshaller(concept, strWriter);
		} catch (JAXBUtilException e) {
			log.error("Error marshalling Ont concept");
			throw new JAXBUtilException(e.getMessage(), e);
		} 
		return strWriter.toString();
	}
	
	public OntologyProcessStatusType doReadStatus(){
		OntologyProcessStatusType status = null;
		try {
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			if(bodyType != null)
	//			status = (OntologyProcessStatusListType)helper.getObjectByClass(bodyType.getAny(), OntologyProcessStatusListType.class);
				status = (OntologyProcessStatusType)helper.getObjectByClass(bodyType.getAny(), OntologyProcessStatusType.class);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());;
		}
		return status;		
	}	
	
	public OntologyProcessStatusType doReadListStatus(){
		OntologyProcessStatusListType status = null;
		try {
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			if(bodyType != null)
				status = (OntologyProcessStatusListType)helper.getObjectByClass(bodyType.getAny(), OntologyProcessStatusListType.class);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());;
		}
		return (OntologyProcessStatusType) status.getOntologyProcessStatus().get(0);		
	}	
	
	public DirtyValueType doReadDirtyType(){
		DirtyValueType status = null;
		try {
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			if(bodyType != null)
				status = (DirtyValueType)helper.getObjectByClass(bodyType.getAny(), DirtyValueType.class);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());;
		}
		return status;		
	}	
	
	public ModifiersType doReadModifiers(){
		ModifiersType modifiers = null;
		try {
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			if(bodyType != null)
				modifiers = (ModifiersType)helper.getObjectByClass(bodyType.getAny(), ModifiersType.class);
		} catch (JAXBUtilException e) {
			log.error(e.getMessage());;
		}
		return modifiers;		
	}	
}

	
