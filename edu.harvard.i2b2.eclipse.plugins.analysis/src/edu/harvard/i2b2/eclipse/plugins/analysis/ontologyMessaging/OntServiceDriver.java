/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 *      Raj Kuttan
 *      Wensong Pan (ported it here from ontology plug-in)
 */

package edu.harvard.i2b2.eclipse.plugins.analysis.ontologyMessaging;

import java.io.StringReader;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.analysis.utils.MessageUtil;

import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetCategoriesType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetChildrenType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetReturnType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetTermInfoType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.VocabRequestType;
import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.common.util.xml.*;

public class OntServiceDriver {

	public static final String THIS_CLASS_NAME = OntServiceDriver.class.getName();
    private static Log log = LogFactory.getLog(THIS_CLASS_NAME);
    private static String serviceURL = UserInfoBean.getInstance().getCellDataUrl("ont");
    private static String serviceMethod = UserInfoBean.getInstance().getCellDataMethod("ont").toUpperCase();
	private static EndpointReference soapEPR = new EndpointReference(serviceURL);
	
	private static EndpointReference childrenEPR = new EndpointReference(
			serviceURL + "getChildren");

	private static EndpointReference categoriesEPR = new EndpointReference(
		serviceURL + "getCategories");
	
	private static EndpointReference nameInfoEPR = new EndpointReference(
			serviceURL + "getNameInfo");
	
	private static EndpointReference codeInfoEPR = new EndpointReference(
		serviceURL + "getCodeInfo");
	
	private static EndpointReference termInfoEPR = new EndpointReference(	
			serviceURL + "getTermInfo");
	
	private static EndpointReference schemesEPR = new EndpointReference(
					serviceURL + "getSchemes");
	
	
    public static OMElement getVersion() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://axisversion.sample/xsd", "tns");

        OMElement method = fac.createOMElement("getVersion", omNs);

        return method;
    }
	
	/**
	 * Function to send getChildren requestVdo to ONT web service
	 * 
	 * @param GetChildrenType  parentNode we wish to get data for
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getChildren(GetChildrenType parentNode, String type) throws Exception{
		String response = null;
		
			 try {
				 GetChildrenRequestMessage reqMsg = new GetChildrenRequestMessage();

				 String getChildrenRequestString = reqMsg.doBuildXML(parentNode);
				 log.debug(getChildrenRequestString);
				 				 
				 if(serviceMethod.equals("SOAP")) {
					 response = sendSOAP(getChildrenRequestString, "http://rpdr.partners.org/GetChildren", "GetChildren", type );
				 }
				 else {
					 response = sendREST(childrenEPR, getChildrenRequestString, type);
				 }
			} catch (AxisFault e) {
				log.error(e.getMessage());
				//throw new AxisFault(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				//throw new Exception(e);
			}
		return response;
	}
    
	/**
	 * Function to send getCategories requestVdo to ONT web service
	 * 
	 * @param GetReturnType  return parameters 
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getCategories(GetCategoriesType returnData, String type) throws Exception {
		String response = null;
			 try {
				 GetCategoriesRequestMessage reqMsg = new GetCategoriesRequestMessage();
				 String getCategoriesRequestString = reqMsg.doBuildXML(returnData);
				log.debug(getCategoriesRequestString); 
				if(serviceMethod.equals("SOAP")) {
					response = sendSOAP(getCategoriesRequestString,"http://rpdr.partners.org/GetCategories", "GetCategories", type );
				}
				else {
					response = sendREST(categoriesEPR, getCategoriesRequestString, type);
				}
				log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				//throw new AxisFault(e);
			} catch (I2B2Exception e) {
				log.error(e.getMessage());
				//throw new I2B2Exception(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage());
				//throw new Exception(e);
			}
		return response;
	}
	
	/**
	 * Function to send getSchemes requestVdo to ONT web service
	 * 
	 * @param GetReturnType  return parameters 
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getSchemes(GetReturnType returnData, String type) throws Exception{
		String response = null;
			 try {
				 GetSchemesRequestMessage reqMsg = new GetSchemesRequestMessage();
				 String getSchemesRequestString = reqMsg.doBuildXML(returnData);

				log.debug(getSchemesRequestString);
				
				if(serviceMethod.equals("SOAP")) {
					response = sendSOAP(getSchemesRequestString, "http://rpdr.partners.org/GetSchemes", "GetSchemes", type );
				}
				else {
					response = sendREST(schemesEPR, getSchemesRequestString, type);
				}
				log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				//throw new AxisFault(e);
			} catch (I2B2Exception e) {
				log.error(e.getMessage());
				throw new I2B2Exception(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	
	
	/**
	 * Function to send getTermInfo requestVdo to ONT web service
	 * 
	 * @param GetTermInfoType  node (self) we wish to get data for
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getTermInfo(GetTermInfoType self, String type) throws Exception{
		String response = null;
			 try {
				 GetTermInfoRequestMessage reqMsg = new GetTermInfoRequestMessage();

				 String getTermInfoRequestString = reqMsg.doBuildXML(self);		

				log.debug(getTermInfoRequestString);
				
	//			 if(serviceMethod.equals("SOAP")) {
	//				 response = sendSOAP(getTermInfoRequestString, "http://rpdr.partners.org/GetTermInfo", "GetTermInfo", type );
	//			 }
	//			 else {
					 response = sendREST(termInfoEPR, getTermInfoRequestString, type);
		//		 }

				log.debug("Ont response = " + response);
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
	 * Function to send getNameInfo requestVdo to ONT web service
	 * 
	 * @param VocabRequestType  return parameters 
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getNameInfo(VocabRequestType vocabData, String type) throws Exception{
		String response = null;
			 try {
				 GetNameInfoRequestMessage reqMsg = new GetNameInfoRequestMessage();
				 String getNameInfoRequestString = reqMsg.doBuildXML(vocabData);

				 log.debug(getNameInfoRequestString);
				 
					if(serviceMethod.equals("SOAP")) {
						response = sendSOAP(getNameInfoRequestString,"http://rpdr.partners.org/GetNameInfo", "GetNameInfo", type );
					}
					else {
						response = sendREST(nameInfoEPR, getNameInfoRequestString, type);
					}

				log.debug("Ont response = " + response);
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
			log.error(e.getMessage());
			throw new Exception(e);
		}
		return lineItem;
	}
	
	/**
	 * Function to send getCodeInfo requestVdo to ONT web service
	 * 
	 * @param VocabRequestType vocabType we wish to get data for
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getCodeInfo(VocabRequestType vocabType, String type) throws Exception{
		String response = null;
			 try {
				 GetCodeInfoRequestMessage reqMsg = new GetCodeInfoRequestMessage();
				 String getCodeInfoRequestString = reqMsg.doBuildXML(vocabType);

				log.debug(getCodeInfoRequestString);
				
				 if(serviceMethod.equals("SOAP")) {
					 response = sendSOAP(getCodeInfoRequestString, "http://rpdr.partners.org/GetCodeInfo", "GetCodeInfo", type );
				 }
				 else {
					 response = sendREST(codeInfoEPR, getCodeInfoRequestString, type);
				 }

				log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				//throw new AxisFault(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				//throw new Exception(e);
			}
		return response;
	}
	
	public static String sendREST(EndpointReference restEPR, String requestString, String type) throws Exception{	
		if(UserInfoBean.getInstance().getCellDataUrl("ont") == null){
			throw new I2B2Exception("Ontology cell (ONT) not configured in PM");
		}
		
		OMElement getOnt = getOntPayLoad(requestString);

		if(type != null){
			if(type.equals("ONT"))
				MessageUtil.getInstance().setRequest("URL: " + restEPR + "\n" + getOnt.toString());
			else 
				MessageUtil.getInstance().setRequest("URL: " + restEPR + "\n" + getOnt.toString());
		}
		
		Options options = new Options();
		log.debug(restEPR.toString());
		options.setTo(restEPR);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

		options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
		options.setProperty(HTTPConstants.SO_TIMEOUT,new Integer(125000));
		options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,new Integer(125000));

		ServiceClient sender = OntServiceClient.getServiceClient();
		sender.setOptions(options);

		OMElement result = sender.sendReceive(getOnt);
		String response = result.toString();
		
		if(type != null){
			if(type.equals("ONT"))
				MessageUtil.getInstance().setResponse("URL: " + restEPR + "\n" + response);
			else 
				MessageUtil.getInstance().setResponse("URL: " + restEPR + "\n" + response);
		}
		
		return response;

	}
	
	public static String sendSOAP(String requestString, String action, String operation, String type) throws Exception{	

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
				MessageUtil.getInstance().setRequest("URL: " + soapEPR + "\n" + formattedRequest);
			}

			else {
				MessageUtil.getInstance().setRequest("URL: " + soapEPR + "\n" + formattedRequest);
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
				MessageUtil.getInstance().setResponse("URL: " + soapEPR + "\n" + indentedResponse);
			}else{
				String formattedResponse = XMLUtil.StrFindAndReplace("&lt;", "<", responseEnv.toString());
				String indentedResponse = XMLUtil.convertDOMToString(XMLUtil.convertStringToDOM(formattedResponse) );
				MessageUtil.getInstance().setResponse("URL: " + soapEPR + "\n" + indentedResponse);
			}
		}
		
//		System.out.println("Sresponse: "+ soapResponse.toString());
		OMElement soapResult = soapResponse.getFirstElement();
//		System.out.println("Sresult: "+ soapResult.toString());

		String i2b2Response = soapResult.getText();
		log.debug(i2b2Response);

		return i2b2Response;		
	}
	
}
