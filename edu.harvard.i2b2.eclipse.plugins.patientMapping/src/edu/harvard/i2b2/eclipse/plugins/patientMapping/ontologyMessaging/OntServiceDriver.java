/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 *      Raj Kuttan
 * 		Wensong Pan (ported it here from ontology plug-in)
 */

package edu.harvard.i2b2.eclipse.plugins.patientMapping.ontologyMessaging;

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
import edu.harvard.i2b2.eclipse.plugins.patientMapping.utils.MessageUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.DeleteChildType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetCategoriesType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetChildrenType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetModifierChildrenType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetModifierInfoType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetModifiersType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetOntProcessStatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetReturnType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetTermInfoType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ModifyChildType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.UpdateCrcConceptType;
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
	
	private static EndpointReference addEPR = new EndpointReference(
			serviceURL + "addChild");
	
	private static EndpointReference deleteEPR = new EndpointReference(
			serviceURL + "deleteChild");
	
	private static EndpointReference modifyEPR = new EndpointReference(
			serviceURL + "modifyChild");
	
	private static EndpointReference synchronizeEPR = new EndpointReference(
			serviceURL + "updateCRCConcept");
	
	private static EndpointReference getStatusEPR = new EndpointReference(
			serviceURL + "getProcessStatus");
	
	private static EndpointReference dirtyStateEPR = new EndpointReference(
			serviceURL + "getDirtyState");
	
	private static EndpointReference modifiersEPR = new EndpointReference(
			serviceURL + "getModifiers");
	
	private static EndpointReference modifierChildrenEPR = new EndpointReference(
			serviceURL + "getModifierChildren");
	
	private static EndpointReference modifierInfoEPR = new EndpointReference(	
			serviceURL + "getModifierInfo");
	
	
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
	//			 log.debug(getChildrenRequestString);
				 				 
				 if(serviceMethod.equals("SOAP")) {
					 response = sendSOAP(getChildrenRequestString, "http://rpdr.partners.org/GetChildren", "GetChildren", type );
				 }
				 else {
					 response = sendREST(childrenEPR, getChildrenRequestString, type);
				 }
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
	//			log.debug(getCategoriesRequestString); 
				if(serviceMethod.equals("SOAP")) {
					response = sendSOAP(getCategoriesRequestString,"http://rpdr.partners.org/GetCategories", "GetCategories", type );
				}
				else {
					response = sendREST(categoriesEPR, getCategoriesRequestString, type);
				}
	//			log.debug("Ont response = " + response);
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

	//			log.debug(getSchemesRequestString);
				
				if(serviceMethod.equals("SOAP")) {
					response = sendSOAP(getSchemesRequestString, "http://rpdr.partners.org/GetSchemes", "GetSchemes", type );
				}
				else {
					response = sendREST(schemesEPR, getSchemesRequestString, type);
				}
//				log.debug("Ont response = " + response);
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

	//			log.debug(getTermInfoRequestString);
				
	//			 if(serviceMethod.equals("SOAP")) {
	//				 response = sendSOAP(getTermInfoRequestString, "http://rpdr.partners.org/GetTermInfo", "GetTermInfo", type );
	//			 }
	//			 else {
					 response = sendREST(termInfoEPR, getTermInfoRequestString, type);
		//		 }

//				log.debug("Ont response = " + response);
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

//				 log.debug(getNameInfoRequestString);
				 
					if(serviceMethod.equals("SOAP")) {
						response = sendSOAP(getNameInfoRequestString,"http://rpdr.partners.org/GetNameInfo", "GetNameInfo", type );
					}
					else {
						response = sendREST(nameInfoEPR, getNameInfoRequestString, type);
					}

//				log.debug("Ont response = " + response);
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

//				log.debug(getCodeInfoRequestString);
				
				 if(serviceMethod.equals("SOAP")) {
					 response = sendSOAP(getCodeInfoRequestString, "http://rpdr.partners.org/GetCodeInfo", "GetCodeInfo", type );
				 }
				 else {
					 response = sendREST(codeInfoEPR, getCodeInfoRequestString, type);
				 }

	//			log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				//throw new AxisFault(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	
	public static String sendREST(EndpointReference restEPR, String requestString, String type) throws Exception{	
		if(UserInfoBean.getInstance().getCellDataUrl("ont") == null){
			throw new I2B2Exception("Ontology cell (ONT) not configured in PM");
		}
//		requestString.replaceAll("\\p{Cntrl}", "");  did not fix illegal control char error
		OMElement getOnt = getOntPayLoad(requestString);

		if(type != null){
			if(type.equals("ONT"))
				MessageUtil.getInstance().setNavRequest("URL: " + restEPR + "\n" + getOnt.toString());
			else if(type.equals("EDIT"))
				MessageUtil.getInstance().setEditRequest("URL: " + restEPR + "\n" + getOnt.toString());
			else 
				MessageUtil.getInstance().setFindRequest("URL: " + restEPR + "\n" + getOnt.toString());
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

		OMElement result;
		try {
			result = sender.sendReceive(getOnt);
		} catch (java.lang.OutOfMemoryError e) {
			System.gc();
			throw new I2B2Exception("Out of memory");
//			return null;
		}
		String response = result.toString();
		
		if(type != null){
			if(type.equals("ONT"))
				MessageUtil.getInstance().setNavResponse("URL: " + restEPR + "\n" + response);
			else if(type.equals("EDIT"))
				MessageUtil.getInstance().setEditResponse("URL: " + restEPR + "\n" + response);
			
			else 
				MessageUtil.getInstance().setFindResponse("URL: " + restEPR + "\n" + response);
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
	}

	/**
	 * Function to send addChild requestVdo to ONT web service
	 * 
	 * @param ConceptType  childNode we wish to add
	 * @return A String containing the ONT web service response 
	 */
	
	public static String addChild(ConceptType childNode) throws Exception{
		String response = null;

		try {
			AddChildRequestMessage reqMsg = new AddChildRequestMessage();

			String addChildRequestString = reqMsg.doBuildXML(childNode);
			log.debug(addChildRequestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(addChildRequestString,"http://rpdr.partners.org/AddChild", "AddChild", type );
				log.error("SOAP version of addChild has not been implemented");
				response = sendREST(addEPR, addChildRequestString, "EDIT");
			}
			else {
				response = sendREST(addEPR, addChildRequestString, "EDIT");
			}
		} catch (AxisFault e) {
			log.error(e.getMessage());
			//throw new AxisFault(e);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}
//		System.out.println(response);
		return response;
	}
	
	/**
	 * Function to send modifyChild requestVdo to ONT web service
	 * 
	 * @param ModifyChildType  childNode we wish to modify
	 * @return A String containing the ONT web service response 
	 */
	
	public static String modifyChild(ModifyChildType childNode) throws Exception{
		String response = null;

		try {
			ModifyChildRequestMessage reqMsg = new ModifyChildRequestMessage();

			String modifyChildRequestString = reqMsg.doBuildXML(childNode);
			log.debug(modifyChildRequestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(addChildRequestString,"http://rpdr.partners.org/AddChild", "AddChild", type );
				log.error("SOAP version of modifyChild has not been implemented");
				response = sendREST(modifyEPR, modifyChildRequestString, "EDIT");
			}
			else {
				response = sendREST(modifyEPR, modifyChildRequestString, "EDIT");
			}
		} catch (AxisFault e) {
			log.error(e.getMessage());
			//throw new AxisFault(e);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}
//		System.out.println(response);
		return response;
	}
	
	/**
	 * Function to send deleteChild requestVdo to ONT web service
	 * 
	 * @param ConceptType  childNode we wish to delete
	 * @return A String containing the ONT web service response 
	 */
	
	public static String deleteChild(DeleteChildType childNode) throws Exception{
		String response = null;

		try {
			DeleteChildRequestMessage reqMsg = new DeleteChildRequestMessage();

			String delChildRequestString = reqMsg.doBuildXML(childNode);
			log.debug(delChildRequestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(addChildRequestString,"http://rpdr.partners.org/AddChild", "AddChild", type );
				log.error("SOAP version of deleteChild has not been implemented");
				response = sendREST(deleteEPR, delChildRequestString, "EDIT");
			}
			else {
				response = sendREST(deleteEPR, delChildRequestString, "EDIT");
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
	 * Function to send synchronize concepts requestVdo to ONT web service
	 * 
	 *
	 * @return A String containing the ONT web service response 
	 */
	
	public static String synchronize(String operationType) throws Exception{
		String response = null;

		try {
			SynchronizeRequestMessage reqMsg = new SynchronizeRequestMessage();
			UpdateCrcConceptType requestType = reqMsg.getUpdateCrcConceptType(operationType);
			
			String requestString = reqMsg.doBuildXML(requestType);
			log.debug(requestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(.....);
				log.error("SOAP version of synchronize has not been implemented");
				response = sendREST(synchronizeEPR, requestString, "EDIT");
			}
			else {
				response = sendREST(synchronizeEPR, requestString, "EDIT");
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
	 * Function to send get process status requestVdo to ONT web service
	 * 
	 *
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getProcessStatus(GetOntProcessStatusType process) throws Exception{
		String response = null;

		try {
			GetProcessStatusRequestMessage reqMsg = new GetProcessStatusRequestMessage();

			String requestString = reqMsg.doBuildXML(process);
			log.debug(requestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(... );
				log.error("SOAP version of getProcessStatus has not been implemented");
				response = sendREST(getStatusEPR, requestString, "EDIT");
			}
			else {
				response = sendREST(getStatusEPR, requestString, "EDIT");
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
	 * Function to send getDirtyState requestVdo to ONT web service
	 * 
	 * @param GetReturnType  return parameters 
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getDirtyState(GetReturnType returnData, String type) throws Exception {
		String response = null;
			 try {
				 GetDirtyStateRequestMessage reqMsg = new GetDirtyStateRequestMessage();
				 String requestString = reqMsg.doBuildXML(returnData);

				if(serviceMethod.equals("SOAP")) {
					response = sendSOAP(requestString,"http://rpdr.partners.org/GetCategories", "GetCategories", type );
				}
				else {
					response = sendREST(dirtyStateEPR, requestString, type);
				}
	//			log.debug("Ont response = " + response);
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
	 * Function to send getModifiers requestVdo to ONT web service
	 * 
	 * @param GetModifiersType  node we wish to get data for
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getModifiers(GetModifiersType self, String type) {
		String response = null;

		try {
			GetModifiersRequestMessage reqMsg = new GetModifiersRequestMessage();

			String getModifiersRequestString = reqMsg.doBuildXML(self);
			//			 log.debug(getModifiersRequestString);

			//	 if(serviceMethod.equals("SOAP")) {
			//		 response = sendSOAP(getChildrenRequestString, "http://rpdr.partners.org/GetChildren", "GetChildren", type );
			//	 }
			//	 else {
			response = sendREST(modifiersEPR, getModifiersRequestString, type);
			//	 }
		} catch (AxisFault e) {
			log.error(e.getMessage());
			log.error("Unable to make connection to remote server");
//			//throw new AxisFault(e);
		} catch (I2B2Exception e) {
			log.error("Not enough memory to display modifiers");
			log.error(e.getMessage());
//			throw new I2B2Exception(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error("Error returned from remote server");
//			throw new Exception(e);
		}

		return response;
	}
	/**
	 * Function to send getChildren requestVdo to ONT web service
	 * 
	 * @param GetChildrenType  parentNode we wish to get data for
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getModifierChildren(GetModifierChildrenType parentNode, String type) {
		String response = null;
		
			 try {
				 GetModifierChildrenRequestMessage reqMsg = new GetModifierChildrenRequestMessage();

				 String getChildrenRequestString = reqMsg.doBuildXML(parentNode);
	//			 log.debug(getChildrenRequestString);
				 				 
				 if(serviceMethod.equals("SOAP")) {
					 response = sendSOAP(getChildrenRequestString, "http://rpdr.partners.org/GetChildren", "GetChildren", type );
				 }
				 else {
					 response = sendREST(modifierChildrenEPR, getChildrenRequestString, type);
				 }
			} catch (AxisFault e) {
				log.error(e.getMessage());
				log.error("Unable to make connection to remote server");
	//			//throw new AxisFault(e);
			} catch (I2B2Exception e) {
				log.error("Not enough memory to display modifiers");
				log.error(e.getMessage());
	//			throw new I2B2Exception(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage());
				log.error("Error returned from remote server");
	//			throw new Exception(e);
			}
	//		response = response.replace("<ValueMetadata>","<ns6:ValueMetadata xmlns:ns6=\"http://www.i2b2.org/xsd/cell/ont/1.1/\">");
	//		response = response.replace("</ValueMetadata>","</ns6:ValueMetadata>");
			return response;
	}
	/**
	 * Function to send getTermInfo requestVdo to ONT web service
	 * 
	 * @param GetTermInfoType  node (self) we wish to get data for
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getModifierInfo(GetModifierInfoType self, String type) {
		String response = null;
			 try {
				 GetModifierInfoRequestMessage reqMsg = new GetModifierInfoRequestMessage();

				 String getModifierInfoRequestString = reqMsg.doBuildXML(self);		

	//			log.debug(getTermInfoRequestString);
				
	//			 if(serviceMethod.equals("SOAP")) {
	//				 response = sendSOAP(getTermInfoRequestString, "http://rpdr.partners.org/GetTermInfo", "GetTermInfo", type );
	//			 }
	//			 else {
					 response = sendREST(modifierInfoEPR, getModifierInfoRequestString, type);
		//		 }

//				log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				log.error("Unable to make connection to remote server");
	//			//throw new AxisFault(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				log.error("Error returned from remote server");
	//			throw new Exception(e);
			}
		return response;
	}
}
