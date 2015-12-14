/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 *     
 */

/**
 * 
 */
package edu.harvard.i2b2.query.serviceClient;

import java.io.StringReader;

import javax.swing.JOptionPane;
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
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.querytool.views.MessageUtil;

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

	private static String getCRCNavigatorQueryProcessorServiceName() {
		return UserInfoBean.getInstance().getCellDataUrl("CRC") + "request";
	}

	public static String sendQueryRequestREST(String XMLstr) {
		try {
			MessageUtil.getInstance().setRequest("URL: " + getCRCNavigatorQueryProcessorServiceName(), XMLstr);
			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();
			targetEPR = new EndpointReference(
					getCRCNavigatorQueryProcessorServiceName());
			options.setTo(targetEPR);
			options.setTo(targetEPR);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setProperty(Constants.Configuration.ENABLE_REST,
					Constants.VALUE_TRUE);
			options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(10000));
			options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(
					10000));

			ServiceClient sender = new ServiceClient();
			sender.setOptions(options);

			OMElement result = sender.sendReceive(payload);
			// System.out.println("Response XML: "+result.toString());
			MessageUtil.getInstance().setResponse("URL: " + getCRCNavigatorQueryProcessorServiceName(), result.toString());
			return result.toString();
		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					JOptionPane
							.showMessageDialog(
									null,
									"Trouble with connection to the remote server, "
											+ "this is often a network error, please try again",
									"Network Error",
									JOptionPane.INFORMATION_MESSAGE);
				}
			});

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sendQueryRequestSOAP(String XMLstr) {
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
			opts.setTimeOutInMilliSeconds(20000);

			log.debug(XMLstr);

			SOAPEnvelope envelope = null;
			SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
			envelope = fac.getDefaultEnvelope();
			OMNamespace omNs = fac.createOMNamespace(
					"http://rpdr.partners.org/", "rpdr");

			// creating the SOAP payload
			OMElement method = fac
					.createOMElement("GetPreviousQueryList", omNs);
			OMElement value = fac.createOMElement("RequestXmlString", omNs);
			value.setText(XMLstr);
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

	/*
	 * public static void main(String[] args) throws Exception { try {
	 * HttpTransportProperties.Authenticator basicAuthentication = new
	 * HttpTransportProperties.Authenticator();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * basicAuthentication.setUsername(UserInfoBean.getInstance().getUserName());
	 * basicAuthentication
	 * .setPassword(UserInfoBean.getInstance().getUserPassword());
	 * 
	 * OMElement payload = getQueryPayLoad(""); Options options = new Options();
	 * 
	 * // options.setProperty(HTTPConstants.PROXY, proxyProperties);
	 * options.setTo(targetEPR);
	 * options.setProperty(org.apache.axis2.transport.http
	 * .HTTPConstants.AUTHENTICATE, basicAuthentication);
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
	 * //System.out.println(result.toString());
	 * 
	 * } catch (AxisFault axisFault) { axisFault.printStackTrace(); } }
	 */
}
