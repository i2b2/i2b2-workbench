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
/**
 *  Class: QueryInstanceData
 *  
 *  A data holder class for a run instance of a query.
 *  
 */

package edu.harvard.i2b2.patientMapping.data;

import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;

import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.InstanceRequestType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmQryHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmRequestTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.UserType;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.patientMapping.datavo.PatientMappingJAXBUtil;

public class QueryInstanceData extends QueryData {

	private static final Log log = LogFactory.getLog(QueryInstanceData.class);

	private String queryId;

	public void queryId(String str) {
		queryId = str;
	}

	public String queryId() {
		return queryId;
	}

	private String finishedTime;

	public void finishedTime(String str) {
		finishedTime = str;
	}

	public String finishedTime() {
		return finishedTime;
	}

	private String patientCount;

	public void patientCount(String str) {
		patientCount = str;
	}

	public String patientCount() {
		return patientCount;
	}

	private String patientRefId;

	public void patientRefId(String str) {
		patientRefId = str;
	}

	public String patientRefId() {
		return patientRefId;
	}

	public QueryInstanceData() {
	}

	@Override
	public String writeContentQueryXML() {

		InstanceRequestType instanceRequestType = new InstanceRequestType();
		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();
		UserType userType = new UserType();
		userType.setLogin(UserInfoBean.getInstance().getUserName());
		userType.setValue(UserInfoBean.getInstance().getUserName());
		headerType.setUser(userType);
		headerType
				.setRequestType(PsmRequestTypeType.CRC_QRY_GET_QUERY_RESULT_INSTANCE_LIST_FROM_QUERY_INSTANCE_ID);

		instanceRequestType.setQueryInstanceId(id());

		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);

		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(instanceRequestType));

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil
					.marshaller(of.createRequest(requestMessageType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.debug("Generated content XML request: " + strWriter.toString());
		return strWriter.toString();
	}

}
