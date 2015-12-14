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

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

//import edu.harvard.i2b2.querytool.datavo.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.DTOFactory;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.explorer.data.Messages;
import edu.harvard.i2b2.explorer.datavo.ExplorerJAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ApplicationType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.FacilityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageControlIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ProcessingIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.SecurityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.FactPrimaryKeyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.GetObservationFactByPrimaryKeyRequestType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.ObjectFactory;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.OutputOptionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.PdoQryHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.PdoRequestTypeType;

public class PDORequestMessageFactory {

	private PdoQryHeaderType buildHeaderType() {
		PdoQryHeaderType pdoHeader = new PdoQryHeaderType();
		pdoHeader.setEstimatedTime(180000);
		pdoHeader
				.setRequestType(PdoRequestTypeType.GET_OBSERVATIONFACT_BY_PRIMARY_KEY);
		return pdoHeader;
	}

	public GetObservationFactByPrimaryKeyRequestType buildFactRequestTypeByPrimaryKey(
			String patientNum, String encounterNum, String concept_cd,
			String providerId, String modifier_cd, String start_date) {
		GetObservationFactByPrimaryKeyRequestType reqType = new GetObservationFactByPrimaryKeyRequestType();

		FactPrimaryKeyType primaryKey = new FactPrimaryKeyType();
		primaryKey.setConceptCd(concept_cd);
		primaryKey.setEventId(encounterNum);
		primaryKey.setPatientId(patientNum);
		primaryKey.setObserverId(providerId);
		// primaryKey.setModifierCd(modifier_cd);

		DatatypeFactory dataTypeFactory = null;
		XMLGregorianCalendar xmlCalendar = null;
		try {
			dataTypeFactory = DatatypeFactory.newInstance();
			String[] strs = start_date.split(" ")[0].split("-");
			int year = new Integer(strs[2]).intValue();
			int month = new Integer(strs[0]).intValue();
			int day = new Integer(strs[1]).intValue();
			xmlCalendar = dataTypeFactory.newXMLGregorianCalendar(year, month,
					day, 0, 0, 0, 0, -5 * 60);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		// primaryKey.setStartDate(xmlCalendar);

		OutputOptionType outputOptionType = new OutputOptionType();
		outputOptionType.setBlob(true);
		outputOptionType.setOnlykeys(false);

		reqType.setFactOutputOption(outputOptionType);
		reqType.setFactPrimaryKey(primaryKey);

		return reqType;
	}

	public String requestXmlMessage(String patientNum, String encounterNum,
			String concept_cd, String providerId, String modifier_cd,
			String start_date) throws Exception {

		PdoQryHeaderType headerType = buildHeaderType();

		GetObservationFactByPrimaryKeyRequestType patientSetRequestType = buildFactRequestTypeByPrimaryKey(
				patientNum, encounterNum, concept_cd, providerId, modifier_cd,
				start_date);
		ObjectFactory obsFactory = new ObjectFactory();

		BodyType bodyType = new BodyType();
		bodyType.getAny().add(obsFactory.createPdoheader(headerType));
		bodyType.getAny().add(obsFactory.createRequest(patientSetRequestType));

		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType reqMsgType = new RequestMessageType();
		reqMsgType.setMessageBody(bodyType);
		reqMsgType.setMessageHeader(messageHeader);
		reqMsgType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = ExplorerJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil.marshaller(of.createRequest(reqMsgType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.print("Request Xml String: " + strWriter.toString());
		return strWriter.toString();
	}

	protected MessageHeaderType getMessageHeader() {
		MessageHeaderType messageHeader = new MessageHeaderType();

		messageHeader.setI2B2VersionCompatible(new BigDecimal(Messages
				.getString("QueryData.i2b2VersionCompatible"))); //$NON-NLS-1$

		ApplicationType appType = new ApplicationType();
		appType.setApplicationName(Messages
				.getString("QueryData.SendingApplicationName")); //$NON-NLS-1$
		appType.setApplicationVersion(Messages
				.getString("QueryData.SendingApplicationVersion")); //$NON-NLS-1$
		messageHeader.setSendingApplication(appType);

		messageHeader.setAcceptAcknowledgementType(new String("messageId"));

		MessageTypeType messageTypeType = new MessageTypeType();
		messageTypeType.setEventType(Messages.getString("QueryData.EventType"));
		messageTypeType.setMessageCode(Messages
				.getString("QueryData.MessageCode"));
		messageHeader.setMessageType(messageTypeType);

		FacilityType facility = new FacilityType();
		facility.setFacilityName(Messages
				.getString("QueryData.SendingFacilityName")); //$NON-NLS-1$
		messageHeader.setSendingFacility(facility);

		ApplicationType appType2 = new ApplicationType();
		appType2.setApplicationVersion(Messages
				.getString("QueryData.ReceivingApplicationVersion")); //$NON-NLS-1$
		appType2.setApplicationName(Messages
				.getString("QueryData.ReceivingApplicationName")); //$NON-NLS-1$
		messageHeader.setReceivingApplication(appType2);

		FacilityType facility2 = new FacilityType();
		facility2.setFacilityName(Messages
				.getString("QueryData.ReceivingFacilityName")); //$NON-NLS-1$
		messageHeader.setReceivingFacility(facility2);

		Date currentDate = new Date();
		DTOFactory factory = new DTOFactory();
		messageHeader.setDatetimeOfMessage(factory
				.getXMLGregorianCalendar(currentDate.getTime()));

		SecurityType secType = new SecurityType();
		secType.setDomain(UserInfoBean.getInstance().getUserDomain());
		secType.setUsername(UserInfoBean.getInstance().getUserName());
		PasswordType ptype = new PasswordType();
		ptype.setIsToken(UserInfoBean.getInstance().getUserPasswordIsToken());
		ptype.setTokenMsTimeout(UserInfoBean.getInstance()
				.getUserPasswordTimeout());
		ptype.setValue(UserInfoBean.getInstance().getUserPassword());

		secType.setPassword(ptype);
		messageHeader.setSecurity(secType);

		MessageControlIdType mcIdType = new MessageControlIdType();
		mcIdType.setInstanceNum(0);
		mcIdType.setMessageNum(generateMessageId());
		messageHeader.setMessageControlId(mcIdType);

		ProcessingIdType proc = new ProcessingIdType();
		proc.setProcessingId(Messages.getString("QueryData.ProcessingId")); //$NON-NLS-1$
		proc.setProcessingMode(Messages.getString("QueryData.ProcessingMode")); //$NON-NLS-1$
		messageHeader.setProcessingId(proc);

		messageHeader.setAcceptAcknowledgementType(Messages
				.getString("QueryData.AcceptAcknowledgementType")); //$NON-NLS-1$
		messageHeader.setApplicationAcknowledgementType(Messages
				.getString("QueryData.ApplicationAcknowledgementType")); //$NON-NLS-1$
		messageHeader.setCountryCode(Messages
				.getString("QueryData.CountryCode")); //$NON-NLS-1$
		messageHeader.setProjectId(UserInfoBean.getInstance().getProjectId());
		return messageHeader;
	}

	protected String generateMessageId() {
		StringWriter strWriter = new StringWriter();
		for (int i = 0; i < 20; i++) {
			int num = getValidAcsiiValue();
			// System.out.println("Generated number: " + num +
			// " char: "+(char)num);
			strWriter.append((char) num);
		}
		return strWriter.toString();
	}

	private int getValidAcsiiValue() {
		int number = 48;
		while (true) {
			number = 48 + (int) Math.round(Math.random() * 74);
			if ((number > 47 && number < 58) || (number > 64 && number < 91)
					|| (number > 96 && number < 123)) {
				break;
			}
		}
		return number;

	}

	public static void main(String[] args) throws Exception {
		PDORequestMessageFactory pdoFactory = new PDORequestMessageFactory();

		pdoFactory.requestXmlMessage("52003", "2004005981", "LCS-I2B2:c1009c",
				"03840261", null, null);
	}
}
