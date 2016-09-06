/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 	     Raj Kuttan
 * 		 Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.ontology.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.AnalysisDefinitionType;
import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.AnalysisParamType;
import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.AnalysisResultOptionListType;
import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.AnalysisResultOptionType;
import org.i2b2.xsd.cell.crc.psm.analysisdefinition._1.CrcAnalysisInputParamType;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.ontclient.datavo.psm.query.MasterDeleteRequestType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.AnalysisDefinitionRequestType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.AnalysisPluginMetadataRequestType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.PsmQryHeaderType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.PsmRequestTypeType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.QueryMasterType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.ResultRequestType;


public class GetPsmRequestMessage extends CRCRequestData {
	public static final String THIS_CLASS_NAME = GetPsmRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

    public GetPsmRequestMessage() {}
		
	
	/**
	 * Function to build patientSetRequestType body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(AnalysisDefinitionRequestType requestType) {
		
		PsmQryHeaderType headerType = buildPsmHeaderType(PsmRequestTypeType.CRC_QRY_RUN_QUERY_INSTANCE_FROM_ANALYSIS_DEFINITION);
		edu.harvard.i2b2.ontclient.datavo.psm.query.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.psm.query.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createPsmheader(headerType));
		bodyType.getAny().add(of.createRequest(requestType));
		
		return bodyType;
	}
	
	
	public AnalysisDefinitionRequestType getAnalysisDefinitionRequestType(String parent){
		AnalysisDefinitionRequestType parentNode = new AnalysisDefinitionRequestType();
		AnalysisDefinitionType value = new AnalysisDefinitionType();
		value.setAnalysisPluginName("CALCULATE_PATIENTCOUNT_FROM_CONCEPTPATH");

		CrcAnalysisInputParamType input = new CrcAnalysisInputParamType();
		input.setName("ONT request");
		AnalysisParamType param = new AnalysisParamType();
		param.setColumn("item_key");
		param.setType("int");
		param.setValue(parent);
		input.getParam().add(param);
		value.setCrcAnalysisInputParam(input);

		AnalysisResultOptionType output = new AnalysisResultOptionType();
		output.setName("XML");
		output.setPriorityIndex(1);
		output.setFullName("XML");

		AnalysisResultOptionListType option = new AnalysisResultOptionListType();
		option.getResultOutput().add(output);
		value.setCrcAnalysisResultList(option);

		parentNode.setAnalysisDefinition(value);
		return parentNode;
	}
	
	public ResultRequestType getResultRequestType(QueryResultInstanceType resultInstance){
		ResultRequestType resultRequest = new ResultRequestType();
	
		resultRequest.setQueryResultInstanceId(resultInstance.getResultInstanceId());
		
		return resultRequest;
	}
	
	public BodyType getBodyType(MasterDeleteRequestType requestType) {
		
		PsmQryHeaderType headerType = buildPsmHeaderType(PsmRequestTypeType.CRC_QRY_DELETE_QUERY_MASTER);
		edu.harvard.i2b2.ontclient.datavo.psm.query.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.psm.query.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createPsmheader(headerType));
		bodyType.getAny().add(of.createRequest(requestType));
		
		return bodyType;
	}
	
	
	public MasterDeleteRequestType getMasterDeleteRequestType(QueryMasterType master){
		MasterDeleteRequestType masterDeleteRequest = new MasterDeleteRequestType();

		masterDeleteRequest.setQueryMasterId(master.getQueryMasterId());
		masterDeleteRequest.setUserId(UserInfoBean.getInstance().getUserName());
		
		return masterDeleteRequest;
	}
	
	public String doBuildXML(MasterDeleteRequestType queryMaster){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 	
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(queryMaster) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
				messageHeader = null;
				reqHeader = null;
				reqMessageType = null;
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 

		return requestString;
	}
	
	
	private PsmQryHeaderType buildPsmHeaderType(PsmRequestTypeType type){ 
		PsmQryHeaderType psmHeader = new PsmQryHeaderType();

		psmHeader.setEstimatedTime(180000);
		psmHeader.setRequestType(type);
		return psmHeader;
	}
	

	/**
	 * Function to build Psm query Request message type and return it as an XML string
	 * 
	 * @param AnalysisDefinitionRequestType 
	 * @return A String data type containing the Psm query RequestMessage in XML format
	 */
	public String doBuildXML(AnalysisDefinitionRequestType analysisData){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 	
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(analysisData) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
				messageHeader = null;
				reqHeader = null;
				reqMessageType = null;
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 

		return requestString;
	}

	/**
	 * Function to build Psm query Request message type and return it as an XML string
	 * 
	 * @param AnalysisDefinitionRequestType 
	 * @return A String data type containing the Psm query RequestMessage in XML format
	 */
	public String doBuildXML(ResultRequestType resultData){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 	
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(resultData) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
				messageHeader = null;
				reqHeader = null;
				reqMessageType = null;
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 

		return requestString;
	}

	public BodyType getBodyType(ResultRequestType requestType) {
		
		PsmQryHeaderType headerType = buildPsmHeaderType(PsmRequestTypeType.CRC_QRY_GET_RESULT_DOCUMENT_FROM_RESULT_INSTANCE_ID);
		edu.harvard.i2b2.ontclient.datavo.psm.query.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.psm.query.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createPsmheader(headerType));
		bodyType.getAny().add(of.createRequest(requestType));
		
		return bodyType;
	}

public BodyType getBodyType(AnalysisPluginMetadataRequestType requestType) {
		
		PsmQryHeaderType headerType = buildPsmHeaderType(PsmRequestTypeType.CRC_QRY_GET_ANALYSIS_PLUGIN_METADATA);
		edu.harvard.i2b2.ontclient.datavo.psm.query.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.psm.query.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createPsmheader(headerType));
		bodyType.getAny().add(of.createRequest(requestType));
		
		return bodyType;
	}
	
	
	public  AnalysisPluginMetadataRequestType getAnalysisPluginMetadataRequestType(){
		
		AnalysisPluginMetadataRequestType request = new AnalysisPluginMetadataRequestType();
		request.setPluginName("CALCULATE_PATIENTCOUNT_FROM_CONCEPTPATH");
		
		return request;
	}
	
	/**
	 * Function to build Psm query Request message type and return it as an XML string
	 * 
	 * @param AnalysisPluginMetadataRequestType 
	 * @return A String data type containing the Psm query RequestMessage in XML format
	 */
	public String doBuildXML(  AnalysisPluginMetadataRequestType request){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 	
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(request) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
				messageHeader = null;
				reqHeader = null;
				reqMessageType = null;
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 

		return requestString;
	}
	
}




