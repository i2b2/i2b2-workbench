/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.harvard.i2b2.wkplclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.InfoType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.SecurityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PanelType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmQryHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmRequestTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionRequestType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultOutputOptionListType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultOutputOptionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.UserType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByDate;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByModifier;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.ExportChildRequestMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceRequestData;

import edu.harvard.i2b2.wkplclient.datavo.wdo.XmlValueType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XmlUtil {

	//to make this class singleton
	private static XmlUtil thisInstance;
	private static Log log = LogFactory.getLog(XmlUtil.class.getName());	

	static {
		thisInstance = new XmlUtil();
	}

	public static XmlUtil getInstance() {
		return thisInstance;
	}



	public String writeQueryXML(String resultOptions) throws Exception {

	//	String domString = edu.harvard.i2b2.common.util.xml.XMLUtil
	//			.convertDOMElementToString(element);
		JAXBContext jc1 = JAXBContext
				.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory.class);
		Unmarshaller unMarshaller = jc1.createUnmarshaller();
		JAXBElement queryDefinitionJaxbElement = (JAXBElement) unMarshaller
				.unmarshal(new StringReader(resultOptions));

		QueryDefinitionType queryDefinitionType = (QueryDefinitionType) queryDefinitionJaxbElement
				.getValue();
		
		//Document queryDefinitionType =  edu.harvard.i2b2.common.util.xml.XMLUtil..convertStringToDOM(resultOptions);
				
				//new QueryDefinitionType();
		ResultOutputOptionListType resultOutputOptionListType = new ResultOutputOptionListType();
		// /loop thru the options
		ResultOutputOptionType resultOutputOptionType = new ResultOutputOptionType();
		resultOutputOptionType.setName("patient_count_xml");
		// );
		resultOutputOptionType.setPriorityIndex(1);
		resultOutputOptionListType.getResultOutput().add(
				resultOutputOptionType);

		QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();

		UserType userType = new UserType();
		//	userType.setLogin(UserInfoBean.getInstance().getUserName());
		//	userType.setGroup(System.getProperty("projectName"));
		//	userType.setValue(UserInfoBean.getInstance().getUserName());

		headerType.setUser(userType);
		headerType
		.setRequestType(PsmRequestTypeType.CRC_QRY_RUN_QUERY_INSTANCE_FROM_QUERY_DEFINITION);
		
		queryDefinitionRequestType.setQueryDefinition(queryDefinitionType);
		queryDefinitionRequestType
		.setResultOutputList(resultOutputOptionListType);

		RequestHeaderType requestHeader = new RequestHeaderType();

		if (System.getProperty("QueryToolMaxWaitingTime") != null) {
			requestHeader.setResultWaittimeMs((Integer.parseInt(System
					.getProperty("QueryToolMaxWaitingTime"))) * 1000);
		} else {
			requestHeader.setResultWaittimeMs(180000);
		}

		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(queryDefinitionRequestType));

		ExportChildRequestMessage reqMsg = new ExportChildRequestMessage();
		MessageHeaderType messageHeader = reqMsg.getMessageHeader();
		SecurityType security = messageHeader.getSecurity();
		security.setPassword(null);
		messageHeader.setSecurity(security);
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		edu.harvard.i2b2.common.util.jaxb.JAXBUtil jaxbUtil = WorkplaceJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try {
			edu.harvard.i2b2.wkplclient.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.wkplclient.datavo.i2b2message.ObjectFactory();
			jaxbUtil
			.marshaller(of.createRequest(requestMessageType), strWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strWriter.toString();
	}


	public static XmlValueType stringToXml(String c_xml) 
	{    	
		if(c_xml == null)	
			return null;
		else {
			// convert 
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(c_xml);
			Element rootElement = null;
			try {
				org.jdom.Document metadataDoc = parser.build(xmlStringReader);
				org.jdom.output.DOMOutputter out = new DOMOutputter(); 
				Document doc = out.output(metadataDoc);
				rootElement = doc.getDocumentElement();
			} catch (JDOMException e) {
				log.error(e.getMessage());
				return null;
			} catch (IOException e1) {
				log.error(e1.getMessage());
				return null;
			}
			if (rootElement != null) {		
				XmlValueType xml = new XmlValueType();
				xml.getAny().add(rootElement);	
				return xml;
			}
		}
		return null;
	}


	public static String getName(XmlValueType xml)
	{    	
		String name = null;
		Element rootElement = xml.getAny().get(0);
		NodeList nameElements = rootElement.getElementsByTagName("name");
		// Group templates dont have tag 'name'
		if (nameElements.getLength() == 0){
			nameElements = rootElement.getElementsByTagNameNS("*", "panel");
			if (nameElements.getLength() == 0){
				nameElements = rootElement.getElementsByTagName("query_name");
				if (nameElements.getLength() == 0){
					// if we get to here and no name has been found then its a PDO.
					// return generically -- change to obs or event etc one level up.
					return "PDO";
				}
				else {
					name = nameElements.item(0).getTextContent();
				}
			}
			else {
				name = nameElements.item(0).getAttributes().getNamedItem("name").getNodeValue();
			}
			if(name != null)
				return name;
			// Default to ABC if we cant find a name at all.
			else
				return "ABC"+MessageUtil.getInstance().getTimestamp();
		}
		// append result_instance_id to PATIENT_COUNT_XML <name> to create unique name

		else if ((nameElements.item(0).getTextContent().equals("PATIENT_COUNT_XML")) ){
			NodeList resultElements = rootElement.getElementsByTagName("result_instance_id");
			if(resultElements.getLength() > 0){
				String resultInstanceId = resultElements.item(0).getTextContent();
				return nameElements.item(0).getTextContent()+ "_" + resultInstanceId;
			}
		}

		else if ((nameElements.item(0).getTextContent().equals("PATIENTSET"))
				|| (nameElements.item(0).getTextContent().equals("PATIENT_ENCOUNTER_SET"))				
				|| (nameElements.item(0).getTextContent().equals("PATIENT_GENDER_COUNT_XML"))
				|| (nameElements.item(0).getTextContent().equals("PATIENT_AGE_COUNT_XML"))
				|| (nameElements.item(0).getTextContent().equals("PATIENT_VITALSTATUS_COUNT_XML"))
				|| (nameElements.item(0).getTextContent().equals("PATIENT_RACE_COUNT_XML"))
				){
			NodeList resultElements = rootElement.getElementsByTagName("description");
			if(resultElements.getLength() > 0){
				return resultElements.item(0).getTextContent();
			}
		}

		return nameElements.item(0).getTextContent();
	}


	public static String getPatientId(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		NodeList nameElements = rootElement.getElementsByTagName("patient_id");
		if (nameElements.getLength() != 0){
			return nameElements.item(0).getTextContent();
		}
		else
			return MessageUtil.getInstance().getTimestamp();

	}
	
	public static String getSiteId(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		NodeList nameElements = rootElement.getElementsByTagName("patient_id");
		if (nameElements.getLength() != 0 && nameElements.item(0).getAttributes().getLength() != 0){
			return nameElements.item(0).getAttributes().item(0).getTextContent();
		}
		else
			return "";//MessageUtil.getInstance().getTimestamp();

	}
	
	public static String getIndex(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		NodeList indexElements = rootElement.getElementsByTagName("index");
		if (indexElements.getLength() == 0)
			return null;

		return indexElements.item(0).getTextContent();
	}

	public static Boolean hasConceptTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		NodeList conceptElements = rootElement.getElementsByTagNameNS("*", "concepts");
		if (conceptElements.getLength() == 0)
			return false;
		else
			return true;
	}

	public static Boolean hasFolderTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		NodeList folderElements = rootElement.getElementsByTagName("folder");
		if (folderElements.getLength() == 0)
			return false;
		else
			return true;
	}

	public static Boolean hasBreakdownTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		// <name>PATIENTSET</name>
		NodeList psElements = rootElement.getElementsByTagName("name");
		if (psElements.getLength() == 0)
			return false;
		else {
			Boolean result = false;
			for(int i = 0 ; i< psElements.getLength(); i++) {
				String resultTypeName = psElements.item(i).getTextContent();
				//		log.info(resultTypeName);
				if(resultTypeName.equals("PATIENT_GENDER_COUNT_XML")
						|| resultTypeName.equals("PATIENT_AGE_COUNT_XML")
						|| resultTypeName.equals("PATIENT_VITALSTATUS_COUNT_XML")
						|| resultTypeName.equals("PATIENT_RACE_COUNT_XML")){	
					result = true;
				}
			}
			return result;
		}
	}

	public static Boolean hasPatientSetTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		// <name>PATIENTSET</name>
		NodeList psElements = rootElement.getElementsByTagName("name");
		if (psElements.getLength() == 0)
			return false;
		else {
			Boolean result = false;
			for(int i = 0 ; i< psElements.getLength(); i++) {
				String resultTypeName = psElements.item(i).getTextContent();
				//		log.info(resultTypeName);
				if(resultTypeName.equals("PATIENTSET")){	
					result = true;
				}
			}
			return result;
		}
	}

	public static String getXmlI2B2Type(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		// <name>PATIENTSET</name>
		NodeList pcElements = rootElement.getElementsByTagName("name");
		if (pcElements.getLength() == 0)
			return null;
		else {

				return  pcElements.item(0).getTextContent();
			}
	}
	
	public static Boolean hasPatientCountTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		// <name>PATIENTSET</name>
		NodeList pcElements = rootElement.getElementsByTagName("name");
		if (pcElements.getLength() == 0)
			return false;
		else {
			Boolean result = false;
			for(int i = 0 ; i< pcElements.getLength(); i++) {
				String resultTypeName = pcElements.item(i).getTextContent();
				//		log.info(resultTypeName);
				if(resultTypeName.equals("PATIENT_COUNT_XML")){	
					result = true;
				}
			}
			return result;
		}
	}

	public static Boolean hasEncounterSetTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		// <name>PATIENTSET</name>
		NodeList psElements = rootElement.getElementsByTagName("name");
		if (psElements.getLength() == 0)
			return false;
		else {
			Boolean result = false;
			for(int i = 0 ; i< psElements.getLength(); i++) {
				String resultTypeName = psElements.item(i).getTextContent();
				//		log.info(resultTypeName);
				if(resultTypeName.equals("PATIENT_ENCOUNTER_SET")){	
					result = true;
				}
			}
			return result;
		}
	}

	public static Boolean hasPrevQueryTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		// <query_master_id> indicates PrevQuery 
		NodeList pqElements = rootElement.getElementsByTagNameNS("*","query_master");
		if (pqElements.getLength() == 0)
			return false;
		else
			return true;
	}

	public static Boolean hasGroupTemplateTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		// <query_master_id> indicates PrevQuery 
		NodeList gtElements = rootElement.getElementsByTagNameNS("*", "panel");
		if (gtElements.getLength() == 0)
			return false;
		else
			return true;
	}

	public static Boolean hasQueryDefinitionTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		NodeList gtElements = rootElement.getElementsByTagNameNS("*", "query_definition");
		if (gtElements.getLength() == 0)
			return false;
		else
			return true;
	}

	public static Boolean hasObservationTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		NodeList gtElements = rootElement.getElementsByTagNameNS("*", "observation_set");
		if (gtElements.getLength() == 0)
			return false;
		else
			return true;
	}
	public static Boolean hasPatientDataTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		NodeList gtElements = rootElement.getElementsByTagNameNS("*", "patient_data");
		if (gtElements.getLength() == 0)
			return false;
		else
			return true;
	}
	public static Boolean hasPatientTag(XmlValueType xml)
	{    	
		Element rootElement = xml.getAny().get(0);
		NodeList gtElements = rootElement.getElementsByTagNameNS("*", "patient_set");
		if (gtElements.getLength() == 0)
			return false;
		else
			return true;
	}
}
