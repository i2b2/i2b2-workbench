/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.views;

import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.GetCodeInfoResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws.PFTJAXBUtil;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.DTOFactory;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.MatchStrType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.VocabRequestType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ApplicationType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.FacilityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageControlIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ProcessingIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseHeaderType;
import edu.harvard.i2b2.common.datavo.pdo.BlobType;
import edu.harvard.i2b2.common.datavo.pdo.ObservationSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientDataType;
import edu.harvard.i2b2.common.datavo.pdo.ObservationType;
import edu.harvard.i2b2.common.datavo.pdo.PatientIdType;
import edu.harvard.i2b2.common.datavo.pdo.ObservationType.ObserverCd;
import edu.harvard.i2b2.eclipse.UserInfoBean;

public class PatientDataMessage {
	public static final String THIS_CLASS_NAME = PatientDataMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);
	/**
	 * The constructor
	 */
	public PatientDataMessage() {
	}

	/**
	 * Function to build observation fact type for a given report/observation_blob
	 * 
	 * @param report   String containing report contents to be placed in observation_blob
	 * @return Observation object
	 */
	public ObservationType getObservationFactType(String report) { 
		ObservationType obType = new ObservationType();
				
		ObservationType.EventId event = new ObservationType.EventId();
		event.setValue("10000157");
		obType.setEventId(event);

		PatientIdType pid = new PatientIdType();
		pid.setValue("73511");
		obType.setPatientId(pid);

		ObservationType.ConceptCd concept = new ObservationType.ConceptCd();
		concept.setValue("LCS-I2B2:pul");
		obType.setConceptCd(concept);
		
		Date currentDate = new Date();
		DTOFactory factory = new DTOFactory();
		obType.setStartDate(factory.getXMLGregorianCalendar(currentDate.getTime()));

		ObserverCd observer = new ObserverCd();
		observer.setValue("@");
		obType.setObserverCd(observer);
		
		BlobType blob = new BlobType();
		blob.getContent().add(report);
     	obType.setObservationBlob(blob);
		obType.setUpdateDate(factory.getXMLGregorianCalendar(currentDate.getTime()));
		obType.setDownloadDate(factory.getXMLGregorianCalendar(currentDate.getTime()));
		obType.setImportDate(factory.getXMLGregorianCalendar(currentDate.getTime()));

		obType.setSourcesystemCd("RPDRPulmonary");
		
		return obType;
	}

	/**
	 * Function to build patientData body type
	 * 
	 * @param report   String containing report contents to be placed in observation_blob
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(String report, PatientDataType patientData) {
		if(patientData == null)
		{
			patientData = new PatientDataType();
			ObservationSet obsSet = new ObservationSet();
			obsSet.getObservation().add(getObservationFactType(report));
			patientData.getObservationSet().add(obsSet);
		}
		edu.harvard.i2b2.common.datavo.pdo.ObjectFactory of = new edu.harvard.i2b2.common.datavo.pdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createPatientData(patientData));
		return bodyType;
	}
	/**
	 * Function to build i2b2 Request message header
	 * 
	 * @return RequestHeader object
	 */
	public RequestHeaderType getRequestHeader() { 
		RequestHeaderType reqHeader = new RequestHeaderType();
		reqHeader.setResultWaittimeMs(120000);
		return reqHeader;
	}
	
	/**
	 * Function to build i2b2 message header
	 * 
	 * @return MessageHeader object
	 */
	public MessageHeaderType getMessageHeader() {
		MessageHeaderType messageHeader = new MessageHeaderType();
		
		messageHeader.setI2B2VersionCompatible(new BigDecimal("1.1"));
		messageHeader.setHl7VersionCompatible(new BigDecimal("2.4"));
		
		ApplicationType appType = new ApplicationType();
		appType.setApplicationName("i2b2 Workbench");
		appType.setApplicationVersion("1.2"); 
		messageHeader.setSendingApplication(appType);
		
		FacilityType facility = new FacilityType();
		facility.setFacilityName("i2b2 Hive");
		messageHeader.setSendingFacility(facility);
		
		ApplicationType appType2 = new ApplicationType();
		appType2.setApplicationVersion("1.0");
		appType2.setApplicationName("PFT Cell");		
		messageHeader.setReceivingApplication(appType2);
	
		FacilityType facility2 = new FacilityType();
		facility2.setFacilityName("i2b2 Hive");
		messageHeader.setReceivingFacility(facility2);

		Date currentDate = new Date();
		DTOFactory factory = new DTOFactory();
		messageHeader.setDatetimeOfMessage(factory.getXMLGregorianCalendar(currentDate.getTime()));
		
		MessageControlIdType mcIdType = new MessageControlIdType();
		mcIdType.setInstanceNum(0);
		mcIdType.setMessageNum(generateMessageId());
		messageHeader.setMessageControlId(mcIdType);

		ProcessingIdType proc = new ProcessingIdType();
		proc.setProcessingId("P");
		proc.setProcessingMode("I");
		messageHeader.setProcessingId(proc);
		
		messageHeader.setAcceptAcknowledgementType("AL");
		messageHeader.setApplicationAcknowledgementType("AL");
		messageHeader.setCountryCode("US");
		messageHeader.setProjectId(UserInfoBean.getInstance().getProjectId());
		return messageHeader;
	}
	
	/**
	 * Function to generate i2b2 message header message number
	 * 
	 * @return String
	 */
	protected String generateMessageId() {
		StringWriter strWriter = new StringWriter();
		for(int i=0; i<20; i++) {
			int num = getValidAcsiiValue();
			strWriter.append((char)num);
		}
		return strWriter.toString();
	}
	
	/**
	 * Function to generate random number used in message number
	 * 
	 * @return int 
	 */
	private int getValidAcsiiValue() {
		int number = 48;
		while(true) {
			number = 48+(int) Math.round(Math.random() * 74);
			if((number > 47 && number < 58) || (number > 64 && number < 91) 
				|| (number > 96 && number < 123)) {
					break;
				}
		}
		return number;
	}
	
	
	/**
	 * Function to build Request message type
	 * 
	 * @param messageHeader MessageHeader object  
	 * @param reqHeader     RequestHeader object
	 * @param bodyType      BodyType object 
	 * @return RequestMessageType object
	 */
	public RequestMessageType getRequestMessageType(MessageHeaderType messageHeader,
			RequestHeaderType reqHeader, BodyType bodyType) { 
		RequestMessageType reqMsgType = new RequestMessageType();
		reqMsgType.setMessageHeader(messageHeader);
		reqMsgType.setMessageBody(bodyType);
		reqMsgType.setRequestHeader(reqHeader);
		return reqMsgType;
	}
	
	/**
	 * Function to convert PFT Request message type to an XML string
	 * 
	 * @param reqMessageType   String containing PFT request message to be converted to string
	 * @return A String data type containing the PFT RequestMessage in XML format
	 */
	private String getXMLString(RequestMessageType reqMessageType)throws Exception{ 
		StringWriter strWriter = null;
		try {
			JAXBUtil jaxbUtil = PFTJAXBUtil.getJAXBUtil();
			strWriter = new StringWriter();
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil.marshaller(of.createRequest(reqMessageType), strWriter);
		} catch (JAXBUtilException e) {
			log.error("Error marshalling PFT request message");
			throw new JAXBUtilException(e.getMessage(), e);
		} 
		return strWriter.toString();
	}
	/**
	 * Function to build PFT Request message type and return it as an XML string
	 * 
	 * @param report   String containing report contents to be placed in observation_blob
	 * @return A String data type containing the PFT RequestMessage in XML format
	 */
	public String doBuildXML(String report, PatientDataType patientData) throws Exception{ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(report, patientData) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
			} catch (JAXBUtilException e) {
				throw new JAXBUtilException(e.getMessage(), e);	
			} 
		return requestString;
	}

	
	public PatientDataType getPatientDataType(String response){
		PatientDataType patientData = null;
		
		try {
			JAXBElement jaxbElement = PFTJAXBUtil.getJAXBUtil().unMashallFromString(response);
			ResponseMessageType respMessageType  = (ResponseMessageType) jaxbElement.getValue();
			BodyType bodyType = respMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			patientData = (PatientDataType)helper.getObjectByClass(bodyType.getAny(), PatientDataType.class);			
		} catch (JAXBUtilException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return patientData;
	}
	
}


		