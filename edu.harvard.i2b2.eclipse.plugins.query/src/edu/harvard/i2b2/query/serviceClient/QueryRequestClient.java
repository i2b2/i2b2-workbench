/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 *     Wensong Pan
 *     Rajesh Kuttan
 *     Lori C. Phillips
 */

package edu.harvard.i2b2.query.serviceClient;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;

import javax.swing.JOptionPane;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.query.utils.MessageUtil;
import edu.harvard.i2b2.query.datavo.QueryJAXBUtil;

public class QueryRequestClient {
	private static final Log log = LogFactory.getLog(QueryRequestClient.class);
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
	
	public static int processSecurityResult(String response) {
		int timeout = -1;
		try {
			JAXBElement jaxbElement = QueryJAXBUtil.getJAXBUtil()
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

		} catch (JAXBUtilException e) {
			log.error(e.getMessage());
		}
		return timeout;
	}

	public static String sendQueryRequestREST(String XMLstr) {
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
					"URL: " + getCRCNavigatorQueryProcessorServiceName() + "\n"
							+ str);

			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();
			targetEPR = new EndpointReference(
					getCRCNavigatorQueryProcessorServiceName());
			options.setTo(targetEPR);
			options.setTo(targetEPR);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setProperty(Constants.Configuration.ENABLE_REST,
					Constants.VALUE_TRUE);
			options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(200000));
			options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(
					200000));

			ServiceClient sender = new ServiceClient();
			sender.setOptions(options);

			OMElement result = sender.sendReceive(payload);
			// System.out.println(result.toString());
			
			xmlStringReader = new java.io.StringReader(
					result.toString());
			tableDoc = parser.build(xmlStringReader);
			o = new XMLOutputter();
			o.setFormat(Format.getPrettyFormat());
			str = new StringWriter();
			o.output(tableDoc, str);
			MessageUtil.getInstance().setResponse(
					"URL: " + getCRCNavigatorQueryProcessorServiceName() + "\n"
							+ str);//result.toString());
			int timeout = processSecurityResult(result.toString());
			log.info("get timeout from server: "+ timeout + " at: "+Calendar.getInstance().getTime());
			if(timeout != -1) {
				UserInfoBean.setLastActivityTime(Calendar.getInstance().getTime());
				UserInfoBean.getInstance().setUserPasswordTimeout(timeout);
				//log.info("get timeout from server: "+ timeout + " at: "+Calendar.getInstance().getTime());
			}

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

			HttpTransportProperties.Authenticator basicAuthentication = new HttpTransportProperties.Authenticator();

			basicAuthentication.setUsername(UserInfoBean.getInstance()
					.getUserName());
			basicAuthentication.setPassword(UserInfoBean.getInstance()
					.getUserPassword());

			OMElement payload = getQueryPayLoad(XMLstr);
			Options options = new Options();

			// options.setProperty(HTTPConstants.PROXY, proxyProperties);
			targetEPR = new EndpointReference(
					getCRCNavigatorQueryProcessorServiceName());
			options.setTo(targetEPR);
			options.setProperty(
					org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
					basicAuthentication);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setTimeOutInMilliSeconds(200000);
			ConfigurationContext configContext = ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(null, null);

			// Blocking invocation
			ServiceClient sender = new ServiceClient(configContext, null);
			sender.setOptions(options);

			OMElement result = sender.sendReceive(payload);
			// System.out.println(result.toString());
			return result.toString();
		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			if (axisFault.getMessage().indexOf("No route to host") >= 0) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										null,
										"Unable to make a connection to the remote server,\n this is often a network error, please try again",
										"Network Error",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			} else if (axisFault.getMessage().indexOf("Read timed out") >= 0) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										null,
										"Unable to obtain a response from the remote server, this is often a network error, please try again",
										"Network Error",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
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
	 * .setPassword(UserInfoBean.getInstance().getUserPasswordValue());
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
	 * // Blocking invocation ServiceClient sender = new
	 * ServiceClient(configContext, null); sender.setOptions(options);
	 * 
	 * OMElement result = sender.sendReceive(payload);
	 * //System.out.println(result.toString());
	 * 
	 * } catch (AxisFault axisFault) { axisFault.printStackTrace(); } }
	 */
}
