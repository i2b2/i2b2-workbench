/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.views;

import java.util.List;

import javax.xml.bind.JAXBElement;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.common.datavo.pdo.EventSet;
import edu.harvard.i2b2.common.datavo.pdo.ObservationSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientDataType;
import edu.harvard.i2b2.common.datavo.pdo.PatientSet;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.PFTJAXBUtil;

public class PDOResponseMessageFactory {
	
	public List<ObservationSet> getFactSetsFromResponseXML(String responseXML) throws Exception {
		JAXBUtil jaxbUtil = PFTJAXBUtil.getJAXBUtil();
		
        JAXBElement jaxbElement  = jaxbUtil.unMashallFromString(responseXML);
        ResponseMessageType messageType = (ResponseMessageType)jaxbElement.getValue();       
        BodyType bodyType = messageType.getMessageBody();
        PatientDataType patientDataType = 
			(PatientDataType) new JAXBUnWrapHelper().getObjectByClass(bodyType.getAny(),PatientDataType.class);
		

		return patientDataType.getObservationSet();
	}
	
	public PatientDataType getPatientDataTypeFromResponseXML(String responseXML) throws Exception {
		JAXBUtil jaxbUtil = PFTJAXBUtil.getJAXBUtil();
		
        JAXBElement jaxbElement  = jaxbUtil.unMashallFromString(responseXML);
        ResponseMessageType messageType = (ResponseMessageType)jaxbElement.getValue();       
        BodyType bodyType = messageType.getMessageBody();
        
        PatientDataType patientDataType = 
			(PatientDataType) new JAXBUnWrapHelper().getObjectByClass(bodyType.getAny(),PatientDataType.class);
		
        return patientDataType;
	}
	
	public PatientSet getPatientSetFromResponseXML(String responseXML) throws Exception {
		
		JAXBUtil jaxbUtil = PFTJAXBUtil.getJAXBUtil();
		
        JAXBElement jaxbElement  = jaxbUtil.unMashallFromString(responseXML);
        ResponseMessageType messageType = (ResponseMessageType)jaxbElement.getValue();       
        BodyType bodyType = messageType.getMessageBody();
        PatientDataType patientDataType = 
			(PatientDataType) new JAXBUnWrapHelper().getObjectByClass(bodyType.getAny(),PatientDataType.class);

		PatientSet patientFactSet =  patientDataType.getPatientSet();
		
		return patientFactSet;
	}
	
	public EventSet getVisitSetFromResponseXML(String responseXML) throws Exception {
		JAXBUtil jaxbUtil = PFTJAXBUtil.getJAXBUtil();
		
        JAXBElement jaxbElement  = jaxbUtil.unMashallFromString(responseXML);
        ResponseMessageType messageType = (ResponseMessageType)jaxbElement.getValue();       
        BodyType bodyType = messageType.getMessageBody();
        PatientDataType patientDataType = 
			(PatientDataType) new JAXBUnWrapHelper().getObjectByClass(bodyType.getAny(),PatientDataType.class);
		
		EventSet visitSet =  patientDataType.getEventSet();
		
		return visitSet;
	}
	
	public static void main(String args[]) throws Exception { 
		//PDOResponseMessageFactory reqTest = new PDOResponseMessageFactory();
		//reqTest.doBuildXML();
		//reqTest.doReadXML();
		//reqTest.doReadResponseXML();
	}
}
