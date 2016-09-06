/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 *      Raj Kuttan
 */

package edu.harvard.i2b2.eclipse.plugins.ontology.ws;

import java.io.StringReader;
import java.net.SocketTimeoutException;

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
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.MessageUtil;
import edu.harvard.i2b2.ontclient.datavo.psm.query.AnalysisDefinitionRequestType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.AnalysisPluginMetadataRequestType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.MasterDeleteRequestType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.QueryMasterType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.ResultRequestType;
import edu.harvard.i2b2.common.exception.I2B2Exception;


public class CRCServiceDriver {

	public static final String THIS_CLASS_NAME = CRCServiceDriver.class.getName();
    private static Log log = LogFactory.getLog(THIS_CLASS_NAME);
    private static String serviceURL = UserInfoBean.getInstance().getCellDataUrl("crc");
    private static String serviceMethod = UserInfoBean.getInstance().getCellDataMethod("crc").toUpperCase();
	private static EndpointReference soapEPR = new EndpointReference(serviceURL);
	
	private static EndpointReference crcEPR = new EndpointReference(
			serviceURL + "request");
	
    public static OMElement getVersion() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://axisversion.sample/xsd", "tns");

        OMElement method = fac.createOMElement("getVersion", omNs);

        return method;
    }
	
	/**
	 * Function to send getChildrenCount request to CRC web service
	 * 
	 * @param String containing name of parentNode we wish to get data for
	 * @return A String containing the CRC web service response 
	 */
	
	public static String getChildrenCount(String parentNode) throws Exception{
		String response = null;
		
			 try {
				
				 GetPsmRequestMessage reqMsg = new GetPsmRequestMessage();
				 AnalysisDefinitionRequestType analysisData = reqMsg.getAnalysisDefinitionRequestType(parentNode);
				 String getCountRequestString = reqMsg.doBuildXML(analysisData);
	//			 log.info(getCountRequestString);
				 				 
				 if(serviceMethod.equals("SOAP")) {
			//		 response = sendSOAP(getCountRequestString, "http://rpdr.partners.org/GetChildren", "GetChildren", type );
				 }
				 else {
					 response = sendREST(crcEPR, getCountRequestString);
				 }
			} catch(SocketTimeoutException e){
					log.info("Got timeout interrupt");
					 throw e;
			} catch (AxisFault e) {
				log.error(e.getMessage());
				//throw new AxisFault(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	
	/**
	 * Function to send deleteQueryMaster request to CRC web service
	 * 
	 * @param QueryMasterType containing the query master we want to delete
	 * @return A String containing the CRC web service response 
	 */
	
	public static String deleteQueryMaster(QueryMasterType master) throws Exception{
		String response = null;	
			 try {
				
				 GetPsmRequestMessage reqMsg = new GetPsmRequestMessage();
				 MasterDeleteRequestType masterDelete = reqMsg.getMasterDeleteRequestType(master);
				 String deleteMasterRequestString = reqMsg.doBuildXML(masterDelete);

				 				 
				 if(serviceMethod.equals("SOAP")) {
			//		 response = sendSOAP(deleteMasterRequestString, "http://rpdr.partners.org/GetChildren", "GetChildren", type );
				 }
				 else {
					 response = sendREST(crcEPR, deleteMasterRequestString);
				 }
			} catch (AxisFault e) {
				log.error(e.getMessage());
				//throw new AxisFault(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
			
	}
    
	/**
	 * Function to send getChildrenCount request to CRC web service
	 * 
	 * @param QueryResultInstanceType containing resultInstance we wish to get data for
	 * @return A String containing the CRC web service response 
	 */
	public static String getChildrenCount(QueryResultInstanceType resultInstance) throws Exception{
		String response = null;
		
			 try {
				
				 GetPsmRequestMessage reqMsg = new GetPsmRequestMessage();
				 ResultRequestType resultData = reqMsg.getResultRequestType(resultInstance);
				 String getCountRequestString = reqMsg.doBuildXML(resultData);
		//		 log.info(getCountRequestString);
				 				 
				 if(serviceMethod.equals("SOAP")) {
		//			 response = sendSOAP(getChildrenRequestString, "http://rpdr.partners.org/GetChildren", "GetChildren", type );
				 }
				 else {
					 response = sendREST(crcEPR, getCountRequestString);
				 }
			 } catch(SocketTimeoutException e){
				 log.info("Got timeout interrupt");
				 throw e;
			 } catch (AxisFault e) {
				 log.error(e.getMessage());
				 //throw new AxisFault(e);

			 } catch (Exception e) {
				 log.error(e.getMessage());
				 throw new Exception(e);
			 }
		return response;
	}
	
    
	/**
	 * Function to send getAnalysisPluginsMetadata request to CRC web service
	 * 
	 * @return A String containing the CRC web service response 
	 */
	public static String getAnalysisPlugins() throws Exception{
		String response = null;
		
			 try {
				
				 GetPsmRequestMessage reqMsg = new GetPsmRequestMessage();
				 AnalysisPluginMetadataRequestType request = reqMsg.getAnalysisPluginMetadataRequestType();
				 String requestString = reqMsg.doBuildXML(request);
		//		 log.info(requestString);
				 				 
				 if(serviceMethod.equals("SOAP")) {
		//			 response = sendSOAP(getChildrenRequestString, "http://rpdr.partners.org/GetChildren", "GetChildren", type );
				 }
				 else {
					 response = sendREST(crcEPR, requestString);
				 }
			 } catch(SocketTimeoutException e){
				 log.info("Got timeout interrupt");
				 throw e;
			 } catch (AxisFault e) {
				 log.error(e.getMessage());
				 //throw new AxisFault(e);

			 } catch (Exception e) {
				 log.error(e.getMessage());
				 throw new Exception(e);
			 }
		//	 log.info(response);
		return response;
	}
	
	
	
	/**
	 * Function to convert CRC request to OMElement
	 * 
	 * @param requestVdo   String requestVdo to send to CRC web service
	 * @return An OMElement containing the Ont CRC service requestVdo
	 */
	public static OMElement getCrcPayLoad(String requestVdo) throws Exception {
		OMElement lineItem  = null;
		try {
			StringReader strReader = new StringReader(requestVdo);
			XMLInputFactory xif = XMLInputFactory.newInstance();
			XMLStreamReader reader = xif.createXMLStreamReader(strReader);

			StAXOMBuilder builder = new StAXOMBuilder(reader);
			lineItem = builder.getDocumentElement();
		} catch (FactoryConfigurationError e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}
		return lineItem;
	}
	
	
	public static String sendREST(EndpointReference restEPR, String requestString) throws Exception{	
		if(UserInfoBean.getInstance().getCellDataUrl("crc") == null){
			throw new I2B2Exception("Data Repository cell (CRC) not configured in PM");
		}
		
		OMElement getCrc = getCrcPayLoad(requestString);

//		if(type != null){
//			if(type.equals("ONT"))
//				MessageUtil.getInstance().setNavRequest("URL: " + restEPR + "\n" + getOnt.toString());
//			else 
//				MessageUtil.getInstance().setFindRequest("URL: " + restEPR + "\n" + getOnt.toString());
//		}
		MessageUtil.getInstance().setNavRequest("URL: " + crcEPR + "\n" + getCrc.toString());
		Options options = new Options();
		log.debug(restEPR.toString());
		options.setTo(restEPR);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

		options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
		options.setProperty(HTTPConstants.SO_TIMEOUT,new Integer(185000));
		options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,new Integer(185000));

		ServiceClient sender = OntServiceClient.getServiceClient();
		sender.setOptions(options);

		OMElement result = sender.sendReceive(getCrc);
		String response = result.toString();
		MessageUtil.getInstance().setNavResponse("URL: " + crcEPR + "\n" + response);
	//	if(type != null){
	//		if(type.equals("ONT"))
	//			MessageUtil.getInstance().setNavResponse("URL: " + restEPR + "\n" + response);
	//		else 
	//			MessageUtil.getInstance().setFindResponse("URL: " + restEPR + "\n" + response);
	//	}
		
		return response;

	}
	
/*	public static String sendSOAP(String requestString, String action, String operation, String type) throws Exception{	

		ServiceClient sender = OntServiceClient.getServiceClient();
		OperationClient operationClient = sender
				.createClient(ServiceClient.ANON_OUT_IN_OP);

		// creating message context
		MessageContext outMsgCtx = new MessageContext();
		// assigning message context's option object into instance variable
		Options opts = outMsgCtx.getOptions();
		// setting properties into option
		log.debug(soapEPR);
		opts.setTo(soapEPR);
		opts.setAction(action);
		opts.setTimeOutInMilliSeconds(180000);
		
		log.debug(requestString);

		SOAPEnvelope envelope = null;
		
		try {
			SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
			envelope = fac.getDefaultEnvelope();
			OMNamespace omNs = fac.createOMNamespace(
					"http://rpdr.partners.org/",                                   
					"rpdr");

			
			// creating the SOAP payload
			OMElement method = fac.createOMElement(operation, omNs);
			OMElement value = fac.createOMElement("RequestXmlString", omNs);
			value.setText(requestString);
			method.addChild(value);
			envelope.getBody().addChild(method);
		}
		catch (FactoryConfigurationError e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}
 
		outMsgCtx.setEnvelope(envelope);
		
		// used to be envelope.getBody().getFirstElement().toString()
		if(type != null){
			String request = envelope.toString();
			String formattedRequest = XMLUtil.StrFindAndReplace("&lt;", "<", request);
			if (type.equals("ONT")){
				MessageUtil.getInstance().setNavRequest("URL: " + soapEPR + "\n" + formattedRequest);
			}

			else {
				MessageUtil.getInstance().setFindRequest("URL: " + soapEPR + "\n" + formattedRequest);
			}
		}
		
		operationClient.addMessageContext(outMsgCtx);
		operationClient.execute(true);
		
		
		MessageContext inMsgtCtx = operationClient.getMessageContext("In");
		SOAPEnvelope responseEnv = inMsgtCtx.getEnvelope();
		
		OMElement soapResponse = responseEnv.getBody().getFirstElement();
		
		if(type != null){
			if(type.equals("ONT")){
				String formattedResponse = XMLUtil.StrFindAndReplace("&lt;", "<", responseEnv.toString());
				String indentedResponse = XMLUtil.convertDOMToString(XMLUtil.convertStringToDOM(formattedResponse) );
				MessageUtil.getInstance().setNavResponse("URL: " + soapEPR + "\n" + indentedResponse);
			}else{
				String formattedResponse = XMLUtil.StrFindAndReplace("&lt;", "<", responseEnv.toString());
				String indentedResponse = XMLUtil.convertDOMToString(XMLUtil.convertStringToDOM(formattedResponse) );
				MessageUtil.getInstance().setFindResponse("URL: " + soapEPR + "\n" + indentedResponse);
			}
		}
		
//		System.out.println("Sresponse: "+ soapResponse.toString());
		OMElement soapResult = soapResponse.getFirstElement();
//		System.out.println("Sresult: "+ soapResult.toString());

		String i2b2Response = soapResult.getText();
		log.debug(i2b2Response);

		return i2b2Response;		
	}*/
	
}
