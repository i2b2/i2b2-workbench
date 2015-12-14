/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */

package edu.harvard.i2b2.adminTool.dataModel;

import java.util.List;

import javax.xml.bind.JAXBElement;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.patientMapping.datavo.PatientMappingJAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.im.query.AuditType;
import edu.harvard.i2b2.crcxmljaxb.datavo.im.query.AuditsType;
import edu.harvard.i2b2.common.datavo.pdo.EventSet;
import edu.harvard.i2b2.common.datavo.pdo.ObservationSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientDataType;
import edu.harvard.i2b2.common.datavo.pdo.PatientSet;
import edu.harvard.i2b2.common.datavo.pdo.PidSet;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.PatientDataResponseType;

public class PDOResponseMessageModel {
	public StatusType getStatusFromResponseXML(String responseXML)
			throws Exception {
		JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

		JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseXML);
		ResponseMessageType messageType = (ResponseMessageType) jaxbElement
				.getValue();
		StatusType statusType = messageType.getResponseHeader()
				.getResultStatus().getStatus();

		return statusType;
	}
	
	public List<AuditType> getAuditsFromResponseXML(String responseXML)
			throws Exception {
		JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();
		
		JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseXML);
		ResponseMessageType messageType = (ResponseMessageType) jaxbElement
				.getValue();
		BodyType bodyType = messageType.getMessageBody();
		AuditsType responseType = (AuditsType) new JAXBUnWrapHelper()
				.getObjectByClass(bodyType.getAny(),
						AuditsType.class);
		//responseType.getAudit();
		//PatientDataType patientDataType = responseType.getPatientData();
		
		return responseType.getAudit();//patientDataType.getObservationSet();
	}

	public List<ObservationSet> getFactSetsFromResponseXML(String responseXML)
			throws Exception {
		JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

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

	public PatientDataType getPatientDataTypeFromResponseXML(String responseXML)
			throws Exception {
		JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

		JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseXML);
		ResponseMessageType messageType = (ResponseMessageType) jaxbElement
				.getValue();
		BodyType bodyType = messageType.getMessageBody();
		PatientDataResponseType responseType = (PatientDataResponseType) new JAXBUnWrapHelper()
				.getObjectByClass(bodyType.getAny(),
						PatientDataResponseType.class);
		PatientDataType patientDataType = responseType.getPatientData();

		return patientDataType;
	}

	public PatientSet getPatientSetFromResponseXML(String responseXML)
			throws Exception {

		JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

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
	
	public PidSet getPidSetFromResponseXML(String responseXML)throws Exception {

		JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();
		
		JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseXML);
		ResponseMessageType messageType = (ResponseMessageType) jaxbElement
				.getValue();
		BodyType bodyType = messageType.getMessageBody();
		PatientDataResponseType responseType = (PatientDataResponseType) new JAXBUnWrapHelper()
				.getObjectByClass(bodyType.getAny(),
						PatientDataResponseType.class);
		PatientDataType patientDataType = responseType.getPatientData();
		
		PidSet pidSet = patientDataType.getPidSet();
		
		return pidSet;
	}

	public EventSet getVisitSetFromResponseXML(String responseXML)
			throws Exception {
		JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

		JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseXML);
		ResponseMessageType messageType = (ResponseMessageType) jaxbElement
				.getValue();
		BodyType bodyType = messageType.getMessageBody();
		PatientDataResponseType responseType = (PatientDataResponseType) new JAXBUnWrapHelper()
				.getObjectByClass(bodyType.getAny(),
						PatientDataResponseType.class);
		PatientDataType patientDataType = responseType.getPatientData();

		EventSet visitSet = patientDataType.getEventSet();

		return visitSet;
	}

	public static void main(String args[]) throws Exception {
	}
}
