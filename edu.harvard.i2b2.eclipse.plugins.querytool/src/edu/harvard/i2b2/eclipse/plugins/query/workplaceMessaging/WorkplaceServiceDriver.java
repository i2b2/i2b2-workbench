/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 *      Raj Kuttan
 */

package edu.harvard.i2b2.eclipse.plugins.query.workplaceMessaging;

import java.io.StringReader;
import java.util.Calendar;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
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
import edu.harvard.i2b2.eclipse.plugins.query.workplaceMessaging.GetFoldersByUserIdRequestMessage;
import edu.harvard.i2b2.eclipse.plugins.querytool.views.MessageUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.AnnotateChildType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.ChildType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.DeleteChildType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.FolderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.GetChildrenType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.GetReturnType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.RenameChildType;
import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.ExportChildType;
import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.common.util.xml.*;

public class WorkplaceServiceDriver {

	public static final String THIS_CLASS_NAME = WorkplaceServiceDriver.class.getName();
    private static Log log = LogFactory.getLog(THIS_CLASS_NAME);
    private static String serviceURL = UserInfoBean.getInstance().getCellDataUrl("work");
    private static String serviceMethod = UserInfoBean.getInstance().getCellDataMethod("work");
    	
    private static EndpointReference soapEPR = new EndpointReference(serviceURL);
	
	private static EndpointReference childrenEPR = new EndpointReference(
			serviceURL + "getChildren");

	private static EndpointReference foldersProjectEPR = new EndpointReference(
		serviceURL + "getFoldersByProject");
	
	private static EndpointReference foldersUserIdEPR = new EndpointReference(
			serviceURL + "getFoldersByUserId");
	
	private static EndpointReference deleteEPR = new EndpointReference(
			serviceURL + "deleteChild");

	private static EndpointReference renameEPR = new EndpointReference(
			serviceURL + "renameChild");
	
	private static EndpointReference annotateEPR = new EndpointReference(
			serviceURL + "annotateChild");
	
	private static EndpointReference exportEPR = new EndpointReference(
			serviceURL + "exportChild");

	
	private static EndpointReference moveEPR = new EndpointReference(
			serviceURL + "moveChild");
	
	private static EndpointReference addEPR = new EndpointReference(
			serviceURL + "addChild");
	/**
	 * Function to send getHomeFolders requestWdo to WORK web service
	 * 
	 * @param GetReturnType  return parameters 
	 * @return A String containing the WORK web service response 
	 */
	
	public static String getHomeFoldersByProject(GetReturnType returnData) throws Exception {
		String response = null;
		if((serviceURL == null) || (serviceMethod == null)){
			throw new I2B2Exception("Workplace Cell (WORK) not configured in PM");
		}
		try {
			GetFoldersByProjectRequestMessage reqMsg = new GetFoldersByProjectRequestMessage();
			String getFoldersRequestString = reqMsg.doBuildXML(returnData);
			//			 log.info(getFoldersRequestString); 
			if(serviceMethod.equals("SOAP")) {
				//	response = sendSOAP(getFoldersRequestString,"http://rpdr.partners.org/GetFolders", "GetFolders", type );
				log.error("SOAP version of getFolders has not been implemented");
				response = sendREST(foldersProjectEPR, getFoldersRequestString);
			}
			else {
				response = sendREST(foldersProjectEPR, getFoldersRequestString);
			}
			log.debug("Workplace response = " + response);
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
	 * Function to send getHomeFolders requestWdo to WORK web service
	 * 
	 * @param GetReturnType  return parameters 
	 * @return A String containing the WORK web service response 
	 */
	
	public static String getHomeFoldersByUserId(GetReturnType returnData) throws Exception {
		String response = null;
		if((serviceURL == null) || (serviceMethod == null)){
			throw new I2B2Exception("Workplace Cell (WORK) not configured in PM");
		}
		try {
			GetFoldersByUserIdRequestMessage reqMsg = new GetFoldersByUserIdRequestMessage();
			String getFoldersRequestString = reqMsg.doBuildXML(returnData);
			log.debug(getFoldersRequestString); 
			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(getFoldersRequestString,"http://rpdr.partners.org/GetFolders", "GetFolders", type );
				log.error("SOAP version of getFolders has not been implemented");
				response = sendREST(foldersUserIdEPR, getFoldersRequestString);
			}
			else {
				response = sendREST(foldersUserIdEPR, getFoldersRequestString);
			}
			log.debug("Workplace response = " + response);
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
	 * Function to send getChildren requestWdo to WORK web service
	 * 
	 * @param GetChildrenType  parentNode we wish to get data for
	 * @return A String containing the WORK web service response 
	 */
	
	public static String getChildren(GetChildrenType parentNode) throws Exception{
		String response = null;

		try {
			GetChildrenRequestMessage reqMsg = new GetChildrenRequestMessage();

			String getChildrenRequestString = reqMsg.doBuildXML(parentNode);
			log.debug(getChildrenRequestString);
			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(getChildrenRequestString,"http://rpdr.partners.org/GetChildren", "GetChildren", type );
				log.error("SOAP version of getChildren has not been implemented");
				response = sendREST(childrenEPR, getChildrenRequestString);
			}
			else {
				response = sendREST(childrenEPR, getChildrenRequestString);
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
	 * Function to send deleteChild requestWdo to WORK web service
	 * 
	 * @param DeleteChildType  childNode we wish to delete
	 * @return A String containing the WORK web service response 
	 */
	
	public static String deleteChild(DeleteChildType childNode) throws Exception{
		String response = null;

		try {
			DeleteChildRequestMessage reqMsg = new DeleteChildRequestMessage();

			String deleteChildRequestString = reqMsg.doBuildXML(childNode);
			log.debug(deleteChildRequestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(deleteChildRequestString,"http://rpdr.partners.org/DeleteChild", "DeleteChild", type );
				log.error("SOAP version of deleteChild has not been implemented");
				response = sendREST(deleteEPR, deleteChildRequestString);
			}
			else {
				response = sendREST(deleteEPR, deleteChildRequestString);
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
	 * Function to send moveChild requestWdo to WORK web service
	 * 
	 * @param ChildType node we want to give a new parentIndex to
	 * @return A String containing the WORK web service response 
	 */
	
	public static String moveChild(ChildType childNode) throws Exception{
		String response = null;

		try {
			MoveChildRequestMessage reqMsg = new MoveChildRequestMessage();

			String requestString = reqMsg.doBuildXML(childNode);
	//		log.info(requestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(requestString,"http://rpdr.partners.org/CleanupChildren", "CleanupChildren", type );
				log.error("SOAP version of cleanupChildren has not been implemented");
				response = sendREST(moveEPR, requestString);
			}
			else {
				response = sendREST(moveEPR, requestString);
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
	 * Function to send renameChild requestWdo to WORK web service
	 * 
	 * @param RenameChildType  childNode we wish to rename
	 * @return A String containing the WORK web service response 
	 */
	
	public static String renameChild(RenameChildType childNode) throws Exception{
		String response = null;

		try {
			RenameChildRequestMessage reqMsg = new RenameChildRequestMessage();

			String renameChildRequestString = reqMsg.doBuildXML(childNode);
			log.debug(renameChildRequestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(renameChildRequestString,"http://rpdr.partners.org/RenameChild", "RenameChild", type );
				log.error("SOAP version of renameChild has not been implemented");
				response = sendREST(renameEPR, renameChildRequestString);
			}
			else {
				response = sendREST(renameEPR, renameChildRequestString);
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
	 * Function to send annotateChild requestWdo to WORK web service
	 * 
	 * @param AnnotateChildType  childNode we wish to annotate
	 * @return A String containing the WORK web service response 
	 */
	
	public static String annotateChild(AnnotateChildType childNode) throws Exception{
		String response = null;

		try {
			AnnotateChildRequestMessage reqMsg = new AnnotateChildRequestMessage();

			String annotateChildRequestString = reqMsg.doBuildXML(childNode);
			log.debug(annotateChildRequestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(renameChildRequestString,"http://rpdr.partners.org/RenameChild", "RenameChild", type );
				log.error("SOAP version of annotateChild has not been implemented");
				response = sendREST(annotateEPR, annotateChildRequestString);
			}
			else {
				response = sendREST(annotateEPR, annotateChildRequestString);
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
	 * Function to send exportChild requestWdo to WORK web service
	 * 
	 * @param svaeChildType  childNode we wish to annotate
	 * @return A String containing the WORK web service response 
	 */
	
	public static String exportChild(ExportChildType childNode) throws Exception{
		String response = null;

		try {
			ExportChildRequestMessage reqMsg = new ExportChildRequestMessage();

			String exportChildRequestString = reqMsg.doBuildXML(childNode);
			log.debug(exportChildRequestString);

			if(serviceMethod.equals("SOAP")) { 
				log.error("SOAP version of exportChild has not been implemented");
				response = sendREST(exportEPR, exportChildRequestString);
			}
			else {
				response = sendREST(exportEPR, exportChildRequestString);
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
	 * Function to send addChild requestWdo to WORK web service
	 * 
	 * @param FolderType  childNode we wish to add
	 * @return A String containing the WORK web service response 
	 */
	
	public static String addChild(FolderType childNode) throws Exception{
		String response = null;

		try {
			AddChildRequestMessage reqMsg = new AddChildRequestMessage();

			String addChildRequestString = reqMsg.doBuildXML(childNode);
			log.debug(addChildRequestString);

			if(serviceMethod.equals("SOAP")) { 
				//	response = sendSOAP(addChildRequestString,"http://rpdr.partners.org/AddChild", "AddChild", type );
				log.error("SOAP version of addChild has not been implemented");
				response = sendREST(addEPR, addChildRequestString);
			}
			else {
				response = sendREST(addEPR, addChildRequestString);
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
	 * Function to convert Work requestWdo to OMElement
	 * 
	 * @param requestWdo   String requestWdo to send to Work web service
	 * @return An OMElement containing the Work web service requestWdo
	 */
	public static OMElement getWorkPayLoad(String requestWdo) throws Exception {
		OMElement lineItem  = null;
		try {
			StringReader strReader = new StringReader(requestWdo);
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
		if(UserInfoBean.getInstance().getCellDataUrl("work") == null){
			throw new I2B2Exception("Workplace cell (WORK) not configured in PM");
		}
		OMElement getWork = getWorkPayLoad(requestString);

		MessageUtil.getInstance().setRequest("URL: " + restEPR, getWork.toString());
		
		Options options = new Options();
		log.debug(restEPR.toString());
		options.setTo(restEPR);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

		options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
		options.setProperty(HTTPConstants.SO_TIMEOUT,new Integer(125000));
		options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,new Integer(125000));

		ServiceClient sender = WorkplaceServiceClient.getServiceClient();
		sender.setOptions(options);

		OMElement result = sender.sendReceive(getWork);
		String response = result.toString();
		
		MessageUtil.getInstance().setResponse("URL: " + restEPR, response);

		return response;

	}
	
	/*public static int processSecurityResult(String response) {
		int timeout = -1;
		try {
			JAXBElement jaxbElement = PreviousQueryJAXBUtil.getJAXBUtil()
					.unMashallFromString(response);
			ResponseMessageType respMessageType = (ResponseMessageType) jaxbElement.getValue();

			// Get response message status
			MessageHeaderType messageHeader = respMessageType.getMessageHeader();
			if(messageHeader.getSecurity() != null && messageHeader.getSecurity().getPassword() != null && messageHeader.getSecurity().getPassword().getTokenMsTimeout() != null) {
				timeout = messageHeader.getSecurity().getPassword().getTokenMsTimeout().intValue();
			}

			/*if (procStatus.equals("ERROR")) {
				log.error("Error reported by Ont web Service " + procMessage);
			} else if (procStatus.equals("WARNING")) {
				log.error("Warning reported by Ont web Service" + procMessage);
			}*/

		/*} catch (JAXBUtilException e) {
			log.error(e.getMessage());
		}
		return timeout;
	}*/
	
	public static String sendSOAP(String requestString, String action, String operation, String type) throws Exception{	

		ServiceClient sender = WorkplaceServiceClient.getServiceClient();
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
			MessageUtil.getInstance().setRequest("URL: " + soapEPR, formattedRequest);
		}
		
		operationClient.addMessageContext(outMsgCtx);
		operationClient.execute(true);
		
		
		MessageContext inMsgtCtx = operationClient.getMessageContext("In");
		SOAPEnvelope responseEnv = inMsgtCtx.getEnvelope();
		
		OMElement soapResponse = responseEnv.getBody().getFirstElement();
		
		if(type != null){
				String formattedResponse = XMLUtil.StrFindAndReplace("&lt;", "<", responseEnv.toString());
				String indentedResponse = XMLUtil.convertDOMToString(XMLUtil.convertStringToDOM(formattedResponse) );
				MessageUtil.getInstance().setResponse("URL: " + soapEPR, indentedResponse);
		}
		
//		System.out.println("Sresponse: "+ soapResponse.toString());
		OMElement soapResult = soapResponse.getFirstElement();
//		System.out.println("Sresult: "+ soapResult.toString());

		String i2b2Response = soapResult.getText();
		log.debug(i2b2Response);

		return i2b2Response;		
	}
	
}
