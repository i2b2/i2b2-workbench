/*
 * Copyright (c) 2006-2007 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws;

import java.io.StringReader;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetOntProcessStatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.UpdateConceptTotalNumType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.VocabRequestType;


public class OntServiceDriver {

	public static final String THIS_CLASS_NAME = OntServiceDriver.class.getName();
    private static Log log = LogFactory.getLog(THIS_CLASS_NAME);
	
	
    public static OMElement getVersion() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://axisversion.sample/xsd", "tns");

        OMElement method = fac.createOMElement("getVersion", omNs);

        return method;
    }

	/**
	 * Function to send getStatusReport requestVdo to ONT web service
	 * 
	 * @param VocabRequestType  node we wish to get data for
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getStatusReport(GetOntProcessStatusType vocabType) throws Exception{
		String response = null;
			 try {
				 GetStatusReportRequestMessage reqMsg = new GetStatusReportRequestMessage();
				 String getCodeInfoRequestString = reqMsg.doBuildXML(vocabType);
			
				OMElement getOnt = getOntPayLoad(getCodeInfoRequestString);				
				MessageUtil.getInstance().setRequest("URL: " + UserInfoBean.getInstance().getCellDataUrl("ont") + "getProcessStatus" + "\n" + getOnt.toString());
				Options options = new Options();

				log.debug(UserInfoBean.getInstance().getCellDataUrl("ont") + "getProcessStatus");
				options.setTo(new EndpointReference(UserInfoBean.getInstance().getCellDataUrl("ont") + "getProcessStatus"));
				options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
				options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
				
				ServiceClient sender = PftServiceClient.getServiceClient();
				sender.setOptions(options);
				
				OMElement result = sender.sendReceive(getOnt);
				response = result.toString();
				MessageUtil.getInstance().setResponse("URL: " + UserInfoBean.getInstance().getCellDataUrl("ont") + "getProcessStatus" + "\n" + response);
				

			} catch (AxisFault e) {
				log.error(e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
    
    
	/**
	 * Function to send getStatusReport requestVdo to ONT web service
	 * 
	 * @param VocabRequestType  node we wish to get data for
	 * @return A String containing the ONT web service response 
	 */
	
	public static String updatePatientCount(UpdateConceptTotalNumType vocabType) throws Exception{
		String response = null;
			 try {
				 UpdateConceptTotalNumRequestMessage reqMsg = new UpdateConceptTotalNumRequestMessage();
				 String getCodeInfoRequestString = reqMsg.doBuildXML(vocabType);
			
				OMElement getOnt = getOntPayLoad(getCodeInfoRequestString);				
				MessageUtil.getInstance().setRequest("URL: " + UserInfoBean.getInstance().getCellDataUrl("ont") + "updateConceptTotalNum" + "\n" + getOnt.toString());
				Options options = new Options();

				log.debug(UserInfoBean.getInstance().getCellDataUrl("ont") + "updateConceptTotalNum");
				options.setTo(new EndpointReference(UserInfoBean.getInstance().getCellDataUrl("ont") + "updateConceptTotalNum"));
				options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
				options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
				
				ServiceClient sender = PftServiceClient.getServiceClient();
				sender.setOptions(options);
				
				OMElement result = sender.sendReceive(getOnt);
				response = result.toString();
				MessageUtil.getInstance().setResponse("URL: " + UserInfoBean.getInstance().getCellDataUrl("ont") + "updateConceptTotalNum" + "\n" + response);

			} catch (AxisFault e) {
				log.error(e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
    
	/**
	 * Function to convert Ont requestVdo to OMElement
	 * 
	 * @param requestVdo   String requestVdo to send to Ont web service
	 * @return An OMElement containing the Ont web service requestVdo
	 */
	public static OMElement getOntPayLoad(String requestVdo) throws Exception {
		OMElement lineItem  = null;
		try {
			StringReader strReader = new StringReader(requestVdo);
			XMLInputFactory xif = XMLInputFactory.newInstance();
			XMLStreamReader reader = xif.createXMLStreamReader(strReader);

			StAXOMBuilder builder = new StAXOMBuilder(reader);
			lineItem = builder.getDocumentElement();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
			throw new Exception(e);
		}
		return lineItem;
	}
	
}
