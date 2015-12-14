/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 *     Wensong Pan
 */

package edu.harvard.i2b2.query.serviceClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HttpTransportProperties;

import edu.harvard.i2b2.eclipse.UserInfoBean;

public class QueryResultClient {

	private static EndpointReference targetEPR;// = new EndpointReference(
	// "http://wxp26488:9093/axis2/services/QueryProcessor/getResultSet");
	private static String servicename = null;

	public static OMElement getQueryPayLoad(String sessionId) throws Exception {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://example1.org/example1", "example1");

		OMElement value = fac.createOMElement("sessionId", omNs);
		value.addChild(fac.createOMText(value, sessionId));

		// value.addChild(fac.createOMText(value, "1157568516156"));
		return value;
	}

	private static String getCRCNavigatorQueryProcessorServiceName() {

		servicename = System.getProperty("getResultSetService");
		if (servicename == null || servicename == "") {
			servicename = "http://phsi2b2appdev.mgh.harvard.edu:9093/axis2/services/QueryToolService/request";
		}

		return servicename;
	}

	public static String getCount(String sessionId) {
		try {
			HttpTransportProperties.Authenticator basicAuthentication = new HttpTransportProperties.Authenticator();

			basicAuthentication.setUsername(UserInfoBean.getInstance()
					.getUserName());
			basicAuthentication.setPassword(UserInfoBean.getInstance()
					.getUserPassword());

			OMElement payload = getQueryPayLoad(sessionId);
			Options options = new Options();

			targetEPR = new EndpointReference(
					getCRCNavigatorQueryProcessorServiceName());
			// options.setProperty(HTTPConstants.PROXY, proxyProperties);
			options.setTo(targetEPR);
			options.setProperty(
					org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
					basicAuthentication);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

			ConfigurationContext configContext = ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(null, null);

			// Blocking invocation
			ServiceClient sender = new ServiceClient(configContext, null);
			sender.setOptions(options);

			OMElement result = sender.sendReceive(payload);
			OMElement attachmentElement = result
					.getFirstChildWithName(new QName("attachment"));
			if (attachmentElement != null) {
				OMText dataElement = (OMText) attachmentElement
						.getFirstOMChild();
				dataElement.setOptimize(true);

				// System.out.println("Dataelement " + dataElement.getText());
				DataHandler attachDataHandler = (DataHandler) dataElement
						.getDataHandler();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(attachDataHandler
								.getInputStream()));
				String count = reader.readLine();
				// System.out.println("Data read " + count);
				return count;
			}
			return "0";
		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			return "0";
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
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
	 * OMElement payload = getQueryPayLoad("1158085997546"); Options options =
	 * new Options();
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
	 * OMElement attachmentElement = result.getFirstChildWithName(new
	 * QName("attachment")); if (attachmentElement != null) { OMText dataElement
	 * = (OMText)attachmentElement.getFirstOMChild();
	 * dataElement.setOptimize(true);
	 * 
	 * //System.out.println("Dataelement " + dataElement.getText()); DataHandler
	 * attachDataHandler = (DataHandler)dataElement.getDataHandler();
	 * BufferedReader reader = new BufferedReader(new
	 * InputStreamReader(attachDataHandler.getInputStream()));
	 * //System.out.println("Data read " + reader.readLine()); } } catch
	 * (AxisFault axisFault) { axisFault.printStackTrace(); } }
	 */
}
