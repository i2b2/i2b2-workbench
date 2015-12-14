/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 *  
 * Contributors: 
 *     Wensong Pan
 */
/**
 * 
 */
package edu.harvard.i2b2.patientSet.serviceClient;

import java.io.StringReader;
import java.io.StringWriter;

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
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.patientSet.data.MessageUtil;
import edu.harvard.i2b2.patientSet.dataModel.PDORequestMessageFactory;

public class QueryListNamesClient {
	private static final Log log = LogFactory
			.getLog(QueryListNamesClient.class);
	private static EndpointReference targetEPR;

	public static OMElement getQueryPayLoad(String XMLstr) throws Exception {
		StringReader strReader = new StringReader(XMLstr);
		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLStreamReader reader = xif.createXMLStreamReader(strReader);

		StAXOMBuilder builder = new StAXOMBuilder(reader);
		OMElement lineItem = builder.getDocumentElement();
		// System.out.println("Line item string " + lineItem.toString());
		return lineItem;
	}

	private static String getQueryServiceName() {

		return UserInfoBean.getInstance().getCellDataUrl("CRC") + "request";
	}

	private static String getPdoServiceName() {

		return UserInfoBean.getInstance().getCellDataUrl("CRC") + "pdorequest";
	}
	
	private static String getFindQueryServiceName() {

		return UserInfoBean.getInstance().getCellDataUrl("CRC") + "getNameInfo";
	}
	
	public static String sendFindQueryRequestREST(String XMLstr) {
		try {
			SAXBuilder parser = new SAXBuilder();
			String xmlContent = XMLstr;
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			XMLOutputter o = new XMLOutputter();
			o.setFormat(Format.getPrettyFormat());
			StringWriter str = new StringWriter();
			o.output(tableDoc, str);
			
			MessageUtil.getInstance().setRequest(
					"URL: " + getFindQueryServiceName() + "\n" + str);//XMLstr);

			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();
			targetEPR = new EndpointReference(getFindQueryServiceName());
			options.setTo(targetEPR);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setProperty(Constants.Configuration.ENABLE_REST,
					Constants.VALUE_TRUE);
			options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(10000));
			options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(
					10000));

			ServiceClient sender = QueryServiceClient.getServiceClient();
			sender.setOptions(options);

			OMElement result = sender.sendReceive(payload);
			xmlStringReader = new java.io.StringReader(
					result.toString());
			tableDoc = parser.build(xmlStringReader);
			o = new XMLOutputter();
			o.setFormat(Format.getPrettyFormat());
			str = new StringWriter();
			o.output(tableDoc, str);
			MessageUtil.getInstance().setResponse(
					"URL: " + getFindQueryServiceName() + "\n" + str);//result.toString());
			// System.out.println("Response XML: "+result.toString());
			return result.toString();
		} catch (AxisFault axisFault) {
			// axisFault.printStackTrace();
			/*
			 * java.awt.EventQueue.invokeLater(new Runnable() { public void
			 * run() { JOptionPane.showMessageDialog(null,
			 * "Trouble with connection to the remote server, " +
			 * "this is often a network error, please try again",
			 * "Network Error", JOptionPane.INFORMATION_MESSAGE); } });
			 */
			log.error("CellDown");
			return "CellDown";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sendQueryRequestREST(String XMLstr, boolean show) {
		try {
			if(show) {
				MessageUtil.getInstance().setRequest(
						"URL: " + getQueryServiceName() + "\n" + XMLstr);
			}

			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();
			targetEPR = new EndpointReference(getQueryServiceName());
			options.setTo(targetEPR);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setProperty(Constants.Configuration.ENABLE_REST,
					Constants.VALUE_TRUE);
			options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(10000));
			options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(
					10000));

			ServiceClient sender = QueryServiceClient.getServiceClient();
			sender.setOptions(options);

			OMElement result = sender.sendReceive(payload);
			if(show) {
				MessageUtil.getInstance().setResponse(
						"URL: " + getQueryServiceName() + "\n" + result.toString());
			}
			// System.out.println("Response XML: "+result.toString());
			return result.toString();
		} catch (AxisFault axisFault) {
			// axisFault.printStackTrace();
			/*
			 * java.awt.EventQueue.invokeLater(new Runnable() { public void
			 * run() { JOptionPane.showMessageDialog(null,
			 * "Trouble with connection to the remote server, " +
			 * "this is often a network error, please try again",
			 * "Network Error", JOptionPane.INFORMATION_MESSAGE); } });
			 */
			log.error("CellDown");
			return "CellDown";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sendPdoRequestREST(String XMLstr) {
		try {
			MessageUtil.getInstance().setRequest(
					"URL: " + getPdoServiceName() + "\n" + XMLstr);

			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();
			targetEPR = new EndpointReference(getPdoServiceName());
			options.setTo(targetEPR);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setProperty(Constants.Configuration.ENABLE_REST,
					Constants.VALUE_TRUE);
			options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(100000));
			options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(
					100000));

			ServiceClient sender = QueryServiceClient.getServiceClient();
			sender.setOptions(options);

			OMElement result = sender.sendReceive(payload);
			// System.out.println("Response XML: "+result.toString());
			MessageUtil.getInstance().setResponse(
					"URL: " + getPdoServiceName() + "\n" + result.toString());
			return result.toString();
		} catch (AxisFault axisFault) {
			// axisFault.printStackTrace();
			/*
			 * java.awt.EventQueue.invokeLater(new Runnable() { public void
			 * run() { JOptionPane.showMessageDialog(null,
			 * "Trouble with connection to the remote server, " +
			 * "this is often a network error, please try again",
			 * "Network Error", JOptionPane.INFORMATION_MESSAGE); } });
			 */
			log.error("CellDown");
			return "CellDown";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sendQueryRequestSOAP(String requestString) {

		try {
			ServiceClient sender = QueryServiceClient.getServiceClient();
			OperationClient operationClient = sender
					.createClient(ServiceClient.ANON_OUT_IN_OP);

			// creating message context
			MessageContext outMsgCtx = new MessageContext();
			// assigning message context's option object into instance variable
			Options opts = outMsgCtx.getOptions();
			// setting properties into option

			targetEPR = new EndpointReference(UserInfoBean.getInstance()
					.getCellDataUrl("CRC"));
			log.debug(targetEPR);
			opts.setTo(targetEPR);
			opts.setAction("http://rpdr.partners.org/GetPreviousQueryList");
			opts.setTimeOutInMilliSeconds(180000);

			log.debug(requestString);

			SOAPEnvelope envelope = null;
			SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
			envelope = fac.getDefaultEnvelope();
			OMNamespace omNs = fac.createOMNamespace(
					"http://rpdr.partners.org/", "rpdr");

			// creating the SOAP payload
			OMElement method = fac
					.createOMElement("GetPreviousQueryList", omNs);
			OMElement value = fac.createOMElement("RequestXmlString", omNs);
			value.setText(requestString);
			method.addChild(value);
			envelope.getBody().addChild(method);

			outMsgCtx.setEnvelope(envelope);

			operationClient.addMessageContext(outMsgCtx);
			operationClient.execute(true);

			MessageContext inMsgtCtx = operationClient.getMessageContext("In");
			SOAPEnvelope responseEnv = inMsgtCtx.getEnvelope();

			OMElement soapResponse = responseEnv.getBody().getFirstElement();
			System.out.println("Sresponse: " + soapResponse.toString());
			OMElement soapResult = soapResponse.getFirstElement();
			// System.out.println("Sresult: "+ soapResult.toString());

			String i2b2Response = soapResult.getText();
			log.debug(i2b2Response);
			return i2b2Response;

		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return null;
		} catch (AxisFault e) {
			e.printStackTrace();
			log.error("CellDown");
			return "CellDown";
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return null;
		}
	}

	public static String sendPDORequestSOAP(String requestString,
			boolean showname) {

		try {
			ServiceClient sender = QueryServiceClient.getServiceClient();
			OperationClient operationClient = sender
					.createClient(ServiceClient.ANON_OUT_IN_OP);

			// creating message context
			MessageContext outMsgCtx = new MessageContext();
			// assigning message context's option object into instance variable
			Options opts = outMsgCtx.getOptions();
			// setting properties into option

			if (showname) {
				// IM
				targetEPR = new EndpointReference(UserInfoBean.getInstance()
						.getCellDataUrl("IM"));
			} else {
				// crc
				targetEPR = new EndpointReference(UserInfoBean.getInstance()
						.getCellDataUrl("CRC"));
			}
			log.debug(targetEPR);
			opts.setTo(targetEPR);
			opts.setAction("http://rpdr.partners.org/GetPatientSetByQueryId");
			opts.setTimeOutInMilliSeconds(900000);

			log.debug(requestString);

			SOAPEnvelope envelope = null;
			SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
			envelope = fac.getDefaultEnvelope();
			OMNamespace omNs = fac.createOMNamespace(
					"http://rpdr.partners.org/", "rpdr");

			// creating the SOAP payload
			OMElement method = fac.createOMElement("GetPatientSetByQueryId",
					omNs);
			OMElement value = fac.createOMElement("RequestXmlString", omNs);
			value.setText(requestString);
			method.addChild(value);
			envelope.getBody().addChild(method);

			outMsgCtx.setEnvelope(envelope);

			operationClient.addMessageContext(outMsgCtx);
			operationClient.execute(true);

			MessageContext inMsgtCtx = operationClient.getMessageContext("In");
			SOAPEnvelope responseEnv = inMsgtCtx.getEnvelope();

			OMElement soapResponse = responseEnv.getBody().getFirstElement();
			// System.out.println("Sresponse: "+ soapResponse.toString());
			OMElement soapResult = soapResponse.getFirstElement();
			// System.out.println("Sresult: "+ soapResult.toString());

			String i2b2Response = soapResult.getText();
			log.debug(i2b2Response);
			return i2b2Response;

		} catch (FactoryConfigurationError e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (AxisFault e) {
			log.error("CellDown");
			e.printStackTrace();
			return "CellDown";
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * 
	 * try { HttpTransportProperties.Authenticator basicAuthentication = new
	 * HttpTransportProperties.Authenticator();
	 * basicAuthentication.setUsername(UserInfoBean
	 * .getInstance().getUserName());
	 * basicAuthentication.setPassword(UserInfoBean
	 * .getInstance().getUserPassword());
	 * 
	 * OMElement payload = getQueryPayLoad(XMLstr); Options options = new
	 * Options();
	 * 
	 * // options.setProperty(HTTPConstants.PROXY, proxyProperties); targetEPR =
	 * new EndpointReference(getCRCNavigatorQueryProcessorServiceName());
	 * options.setTo(targetEPR);
	 * options.setProperty(org.apache.axis2.transport.http
	 * .HTTPConstants.AUTHENTICATE, basicAuthentication);
	 * options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
	 * options.setTimeOutInMilliSeconds(200000); ConfigurationContext
	 * configContext =
	 * ConfigurationContextFactory.createConfigurationContextFromFileSystem
	 * (null, null);
	 * 
	 * // Blocking invocation ServiceClient sender = new
	 * ServiceClient(configContext, null); sender.setOptions(options);
	 * 
	 * OMElement result = sender.sendReceive(payload);
	 * //System.out.println("Response XML: "+result.toString()); return
	 * result.toString(); } catch (AxisFault axisFault) {
	 * axisFault.printStackTrace();
	 * if(axisFault.getMessage().indexOf("No route to host")>=0 ||
	 * axisFault.getMessage().indexOf("Connection refused")>=0) {
	 * java.awt.EventQueue.invokeLater(new Runnable() { public void run() {
	 * JOptionPane.showMessageDialog(null,
	 * "Unable to make a connection to the remote server,\n this is often a network error, please try again"
	 * , "Network Error", JOptionPane.INFORMATION_MESSAGE); } }); } else
	 * if(axisFault.getMessage().indexOf("Read timed out")>=0) {
	 * java.awt.EventQueue.invokeLater(new Runnable() { public void run() {
	 * JOptionPane.showMessageDialog(null,
	 * "Unable to obtain a response from the remote server, this is often a network error, please try again"
	 * , "Network Error", JOptionPane.INFORMATION_MESSAGE); } }); }
	 * log.error("CellDown"); return null; } catch (Exception e) {
	 * e.printStackTrace(); return null; } }
	 */

	public static void main(String[] args) throws Exception {
		/*
		 * try { HttpTransportProperties.Authenticator basicAuthentication = new
		 * HttpTransportProperties.Authenticator();
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * basicAuthentication.setUsername(UserInfoBean.getInstance().getUserName
		 * ());basicAuthentication.setPassword(UserInfoBean.getInstance().
		 * getUserPassword());
		 * 
		 * OMElement payload = getQueryPayLoad(""); Options options = new
		 * Options();
		 * 
		 * // options.setProperty(HTTPConstants.PROXY, proxyProperties);
		 * options.setTo(targetEPR);
		 * options.setProperty(org.apache.axis2.transport
		 * .http.HTTPConstants.AUTHENTICATE, basicAuthentication);
		 * options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		 * 
		 * ConfigurationContext configContext = ConfigurationContextFactory
		 * .createConfigurationContextFromFileSystem(null, null);
		 * 
		 * 
		 * // Blocking invocation ServiceClient sender = new
		 * ServiceClient(configContext, null); sender.setOptions(options);
		 * 
		 * OMElement result = sender.sendReceive(payload);
		 * System.out.println(result.toString());
		 * 
		 * } catch (AxisFault axisFault) { axisFault.printStackTrace(); }
		 */

		PDORequestMessageFactory pdoFactory = new PDORequestMessageFactory();
		String xmlrequest = pdoFactory.requestXmlMessage(
				"zzp___050206101533684227.xml", new Integer(0),
				new Integer(15), false);
		System.out.println("Request: " + xmlrequest);
		String response = sendPDORequestSOAP(xmlrequest, true);
		System.out.println("Response: " + response);
	}
}
