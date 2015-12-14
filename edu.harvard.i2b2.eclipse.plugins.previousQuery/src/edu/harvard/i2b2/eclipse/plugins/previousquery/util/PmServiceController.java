/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *    
 */

package edu.harvard.i2b2.eclipse.plugins.previousquery.util;

import java.io.BufferedReader;
import java.io.FileReader;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.context.MessageContext;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.previousquery.dataModel.Messages;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
//import edu.harvard.i2b2.crcxmljaxb.datavo.pm.PasswordType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pm.ConfigureType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pm.ParamType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pm.RoleType;
//import edu.harvard.i2b2.pm.datavo.pm.ResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pm.GetUserConfigurationType;
//import edu.harvard.i2b2.common.pm.UserInfoBean;


public class PmServiceController {

	private static Log log = LogFactory.getLog(PmServiceController.class.getName());

	private String msg;

	// class to hold userInfo
	public UserInfoBean userInfoBean;

	// constructor
	public PmServiceController() {

		// class instance to hold user session variables
		userInfoBean = UserInfoBean.getInstance();
	}


	/**
	 * Function to convert pm request to OMElement
	 * 
	 * @param requestVdo   String request to send to pm web service
	 * @return An OMElement containing the pm web service request
	 */
	public static OMElement getPmPayLoad(String requestVdo) throws Exception {
		OMElement method  = null;
		try {
            StringReader strReader = new StringReader(requestVdo);
            XMLInputFactory xif = XMLInputFactory.newInstance();
            XMLStreamReader reader = xif.createXMLStreamReader(strReader);

            StAXOMBuilder builder = new StAXOMBuilder(reader);
            method = builder.getDocumentElement();      			

			/*
			OMFactory fac = OMAbstractFactory.getOMFactory();
			OMNamespace omNs = fac.createOMNamespace("http://www.i2b2.org/xsd/hive/msg",
			"i2b2");

			method = fac.createOMElement("request", omNs);

			StringReader strReader = new StringReader(requestVdo);
			XMLInputFactory xif = XMLInputFactory.newInstance();
			XMLStreamReader reader = xif.createXMLStreamReader(strReader);

			StAXOMBuilder builder = new StAXOMBuilder(reader);
			//method = builder.getDocumentElement();
			OMElement lineItem = builder.getDocumentElement();
			method.addChild(lineItem);
			*/
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
			throw new Exception(e);
		}
		return method;
	}

	/**
	 * Function to send getUserInfo request to PM web service
	 * 
	 * @param userid
	 * @param password
	 * @param projectID
	 * @param project
	 * @param demo      Flag to indicate if we are in demo mode
	 * @return A String containing the PM web service response 
	 */

	public  String getUserInfo(String userid, PasswordType password, String projectURL, String project, boolean demo, String pid) throws Exception{
		String response = null;
		try {
			GetUseridsRequestMessage reqMsg = new GetUseridsRequestMessage(userid, password, project);
			
			
			RoleType userConfig = new RoleType();
			//userConfig.getProject().add(project);
			userConfig.setProjectId(pid);
			String getUserInfoRequestString = null;
			if (demo)
			{
				setUserInfo(getPMDemoString());
				System.setProperty("user", UserInfoBean.getInstance().getUserName()); //$NON-NLS-1$
				System.setProperty("pass", UserInfoBean.getInstance().getUserPassword()); //$NON-NLS-1$
			}
			else
			{
				getUserInfoRequestString = reqMsg.doBuildXML(userConfig);
				MessageUtil.getInstance().setRequest(
						"URL: " + projectURL + "getServices" + "\n" + getUserInfoRequestString);
				//log.info("PM request: /n"+getUserInfoRequestString);
				if(System.getProperty("webServiceMethod").equals("SOAP")) { //$NON-NLS-1$ //$NON-NLS-2$
					response = sendSOAP(new EndpointReference(projectURL), getUserInfoRequestString, "http://rpdr.partners.org/GetUserConfiguration", "GetUserConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					response = sendREST(new EndpointReference(projectURL + "getServices"), getUserInfoRequestString); //$NON-NLS-1$
				}
				if(response == null) {
					log.info("no pm response received"); //$NON-NLS-1$
					return "error";
				}
				MessageUtil.getInstance().setResponse(
						"URL: " + projectURL + "getServices" + "\n" + response);
			}
		} catch (AxisFault e) {
			log.error(e.getMessage());
			setMsg(Messages.getString("LoginHelper.PMUnavailable")); //$NON-NLS-1$
		} catch (Exception e) {
			log.error(e.getMessage());
			setMsg(e.getMessage());
		}
		return response;
	}

	public static String sendREST(EndpointReference restEPR, String requestString) throws Exception{	
		OMElement getPM = getPmPayLoad(requestString);

		Options options = new Options();
		log.debug(restEPR.toString());
		options.setTo(restEPR);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

		options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
		options.setProperty(HTTPConstants.SO_TIMEOUT,new Integer(125000));
		options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,new Integer(125000));

		ServiceClient sender = PmServiceClient.getServiceClient();
		sender.setOptions(options);

		OMElement result = sender.sendReceive(getPM);
		//String response = result.getFirstElement().toString();
		//return response;
		return result.toString();
	}

	public static String sendSOAP(EndpointReference soapEPR, String requestString, String action, String operation) throws Exception{	

		ServiceClient sender = PmServiceClient.getServiceClient();
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
					"http://rpdr.partners.org/",                                    //$NON-NLS-1$
					"rpdr"); //$NON-NLS-1$

			
			// creating the SOAP payload
			OMElement method = fac.createOMElement(operation, omNs);
			OMElement value = fac.createOMElement("RequestXmlString", omNs); //$NON-NLS-1$
			value.setText(requestString);
			method.addChild(value);
			envelope.getBody().addChild(method);
		}
		catch (FactoryConfigurationError e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}

		outMsgCtx.setEnvelope(envelope);
		
		operationClient.addMessageContext(outMsgCtx);
		operationClient.execute(true);
		
		MessageContext inMsgtCtx = operationClient.getMessageContext("In"); //$NON-NLS-1$
		SOAPEnvelope responseEnv = inMsgtCtx.getEnvelope();
		
		OMElement soapResponse = responseEnv.getBody().getFirstElement();
//		System.out.println("Sresponse: "+ soapResponse.toString());
		OMElement soapResult = soapResponse.getFirstElement();
//		System.out.println("Sresult: "+ soapResult.toString());

		String i2b2Response = soapResult.getText();
		log.debug(i2b2Response);

		return i2b2Response;		
	}
	
	public void setUserInfo(String responseXML) throws Exception {
		//log.info("PM response message: /n"+responseXML);
		UserInfoBean.pmResponse(responseXML);
		
		JAXBUtil jaxbUtil = new JAXBUtil(new String[] {
				"edu.harvard.i2b2.pm.datavo.pm", //$NON-NLS-1$
				"edu.harvard.i2b2.pm.datavo.i2b2message" //$NON-NLS-1$
		});
		JAXBElement jaxbElement = jaxbUtil.unMashallFromString(responseXML);
		ResponseMessageType responseMessageType = (ResponseMessageType) jaxbElement.getValue();

		String procStatus = responseMessageType.getResponseHeader().getResultStatus().getStatus().getType();
		String procMessage = responseMessageType.getResponseHeader().getResultStatus().getStatus().getValue();

		//String serverVersion = responseMessageType.getMessageHeader()
		//.getSendingApplication().getApplicationVersion();
		//System.setProperty("serverVersion", serverVersion);
		
		if(procStatus.equals("ERROR")){ //$NON-NLS-1$
			setMsg(procMessage);				
		}
		else if(procStatus.equals("WARNING")){ //$NON-NLS-1$
			setMsg(procMessage);
		}	
		else {
			BodyType bodyType = responseMessageType.getMessageBody();
			JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
			ConfigureType response = (ConfigureType)helper.getObjectByClass(bodyType.getAny(), ConfigureType.class);

			userInfoBean.setEnvironment(response.getEnvironment());
			userInfoBean.setUserName(response.getUser().getUserName());
			userInfoBean.setUserFullName(response.getUser().getFullName());
			//userInfoBean.setUserPassword(response.getUser().getPassword());
			userInfoBean.setUserKey(response.getUser().getKey());
			userInfoBean.setUserDomain(response.getUser().getDomain());
			userInfoBean.setHelpURL(response.getHelpURL());

			//Save Global variables in properties
			if(response.getGlobalData() != null) {
				for (ParamType param :response.getGlobalData().getParam())
					userInfoBean.setGlobals(param.getName(), param.getValue());
			}
			//Save projects			
			if(response.getUser().getProject() != null)
				//userInfoBean.setProjects( response.getUser().getProject());

			//Save Cell
			if(response.getCellDatas() != null) {}
				//userInfoBean.setCellDatas(response.getCellDatas());
		}
	}


	/**
	 * Test code to generate a PM requestPdo String for a sample PM report
	 * called by main below
	 *
	 * @return A String containing the sample PM report
	 */
	public static String getPMDemoString() throws Exception {
		BufferedReader bufRead = new BufferedReader(new FileReader("i2b2workbench.xml")); //$NON-NLS-1$
		StringBuffer queryStr = new StringBuffer();
		String line = null;

		while ((line = bufRead.readLine()) != null) {
			queryStr.append(line + "\n"); //$NON-NLS-1$
		}
		bufRead.close();


		return queryStr.toString();
	}

	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}

}
