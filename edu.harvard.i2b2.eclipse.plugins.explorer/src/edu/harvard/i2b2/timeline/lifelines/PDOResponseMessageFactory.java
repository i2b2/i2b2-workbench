/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 * 		Wensong Pan
 *      
 */
package edu.harvard.i2b2.timeline.lifelines;

import java.util.List;

import javax.xml.bind.JAXBElement;

//import edu.harvard.i2b2.querytool.datavo.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.explorer.datavo.ExplorerJAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType; //import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.PatientDataJAXBUnWrapHelper;
import edu.harvard.i2b2.common.datavo.pdo.EventSet;
import edu.harvard.i2b2.common.datavo.pdo.ObservationSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientDataType;
import edu.harvard.i2b2.common.datavo.pdo.PatientSet;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.PatientDataResponseType;

public class PDOResponseMessageFactory {

	public List<ObservationSet> getFactSetsFromResponseXML(String responseXML)
			throws Exception {
		JAXBUtil jaxbUtil = ExplorerJAXBUtil.getJAXBUtil();

		JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseXML);
		ResponseMessageType messageType = (ResponseMessageType) jaxbElement
				.getValue();
		BodyType bodyType = messageType.getMessageBody();
		PatientDataResponseType responseType = (PatientDataResponseType) new JAXBUnWrapHelper()
				.getObjectByClass(bodyType.getAny(),
						PatientDataResponseType.class);
		PatientDataType patientDataType = responseType.getPatientData();

		return patientDataType.getObservationSet();
	}

	public PatientSet getPatientSetFromResponseXML(String responseXML)
			throws Exception {
		JAXBUtil jaxbUtil = ExplorerJAXBUtil.getJAXBUtil();

		JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseXML);
		ResponseMessageType messageType = (ResponseMessageType) jaxbElement
				.getValue();
		BodyType bodyType = messageType.getMessageBody();
		PatientDataResponseType responseType = (PatientDataResponseType) new JAXBUnWrapHelper()
				.getObjectByClass(bodyType.getAny(),
						PatientDataResponseType.class);
		PatientDataType patientDataType = responseType.getPatientData();
		PatientSet patientFactSet = patientDataType.getPatientSet();

		return patientFactSet;
	}

	public EventSet getVisitSetFromResponseXML(String responseXML)
			throws Exception {
		JAXBUtil jaxbUtil = ExplorerJAXBUtil.getJAXBUtil();

		JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseXML);
		ResponseMessageType messageType = (ResponseMessageType) jaxbElement
				.getValue();
		BodyType bodyType = messageType.getMessageBody();
		PatientDataResponseType responseType = (PatientDataResponseType) new JAXBUnWrapHelper()
				.getObjectByClass(bodyType.getAny(),
						PatientDataResponseType.class);
		PatientDataType patientDataType = responseType.getPatientData();
		EventSet visitSet = patientDataType.getEventSet();
		// Event evt = visitSet.getEvent().get(0);
		return visitSet;
	}

	public static void main(String args[]) throws Exception {

	}

}
