/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.  
 * 
 * Contributors: 
 *     Wensong Pan
 *     
 */

package edu.harvard.i2b2.query.data;

import java.io.StringWriter;
import java.util.ArrayList;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterRequestType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmQryHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmRequestTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.UserType;
import edu.harvard.i2b2.query.jaxb.utils.QueryJAXBUtil;

/**
 * Class: QueryMasterData A data holder for a querytool logic query.
 */

public class QueryMasterData extends QueryData {

	public ArrayList<QueryInstanceData> runs = null;

	public QueryMasterData() {
		runs = new ArrayList<QueryInstanceData>();
	}

	@Override
	public String writeContentQueryXML() {

		MasterRequestType masterQueryType = new MasterRequestType();

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();
		UserType userType = new UserType();
		userType.setLogin(userId());
		userType.setValue(userId());
		headerType.setUser(userType);
		headerType
				.setRequestType(PsmRequestTypeType.CRC_QRY_DELETE_QUERY_MASTER);

		masterQueryType.setQueryMasterId(id());

		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);
		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(masterQueryType));

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil
					.marshaller(of.createRequest(requestMessageType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("Generated content XML request: " +
		// strWriter.toString());
		return strWriter.toString();
	}

	public String writeDefinitionQueryXML() {
		MasterRequestType masterQueryType = new MasterRequestType();

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();
		UserType userType = new UserType();
		userType.setLogin(userId());
		userType.setValue(userId());
		headerType.setUser(userType);
		headerType
				.setRequestType(PsmRequestTypeType.CRC_QRY_GET_REQUEST_XML_FROM_QUERY_MASTER_ID);

		masterQueryType.setQueryMasterId(id());

		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);
		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(masterQueryType));

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);
		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil
					.marshaller(of.createRequest(requestMessageType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("Generated content XML request: " +
		// strWriter.toString());
		return strWriter.toString();
	}
}
