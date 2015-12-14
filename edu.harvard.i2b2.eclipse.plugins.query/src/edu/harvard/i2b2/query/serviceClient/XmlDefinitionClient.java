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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory; //import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

//import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement; //import org.apache.axiom.om.OMFactory;
//import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory; //import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;

import edu.harvard.i2b2.eclipse.UserInfoBean;

public class XmlDefinitionClient {

	private static EndpointReference targetEPR = new EndpointReference(
			"http://phsi2b2appdev.mgh.harvard.edu:9093/axis2/services/QueryToolService/request");

	// "http://wxp26488:9093/axis2/services/QueryProcessor/queryPatient");
	// "http://infra6db.mgh.harvard.edu:8080/axis2/services/identity1");

	public static OMElement getQueryPayLoad(String queryStr) throws Exception {
		StringReader strReader = new StringReader(queryStr); // getQueryString())
		// ;
		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLStreamReader reader = xif.createXMLStreamReader(strReader);

		StAXOMBuilder builder = new StAXOMBuilder(reader);
		OMElement lineItem = builder.getDocumentElement();
		// System.out.println("Line item string " + lineItem.toString());
		return lineItem;
	}

	public static String getQueryString() throws Exception {
		StringBuffer queryStr = new StringBuffer();
		DataInputStream dataStream = new DataInputStream(
				new FileInputStream(""));
		while (dataStream.available() > 0) {
			queryStr.append(dataStream.readLine() + "\n");
		}
		// System.out.println("queryStr" + queryStr);
		return queryStr.toString();
	}

	public static String getXmlContent(String queryStr) {
		try {
			HttpTransportProperties.Authenticator basicAuthentication = new HttpTransportProperties.Authenticator();

			basicAuthentication.setUsername(UserInfoBean.getInstance()
					.getUserName());
			basicAuthentication.setPassword(UserInfoBean.getInstance()
					.getUserPassword());

			OMElement payload = getQueryPayLoad(queryStr);
			Options options = new Options();

			// options.setProperty(HTTPConstants.PROXY, proxyProperties);
			options.setTo(targetEPR);
			options.setProperty(
					org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
					basicAuthentication);

			options.setTimeOutInMilliSeconds(180000);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

			ConfigurationContext configContext =

			ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(null, null);

			// Blocking invocation
			ServiceClient sender = new ServiceClient(configContext, null);
			sender.setOptions(options);

			OMElement result = sender.sendReceive(payload);
			// System.out.println(result.toString());
			return result.toString();

		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
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
	 * .setPassword(UserInfoBean.getInstance().getUserPassword());
	 * 
	 * OMElement payload = getQueryPayLoad(""); Options options = new Options();
	 * 
	 * // options.setProperty(HTTPConstants.PROXY, proxyProperties);
	 * options.setTo(targetEPR);
	 * 
	 * 
	 * options.setProperty(org.apache.axis2.transport.http.HTTPConstants.
	 * AUTHENTICATE, basicAuthentication);
	 * 
	 * options.setTimeOutInMilliSeconds(180000);
	 * options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
	 * 
	 * ConfigurationContext configContext =
	 * 
	 * ConfigurationContextFactory
	 * .createConfigurationContextFromFileSystem(null, null);
	 * 
	 * 
	 * // Blocking invocation ServiceClient sender = new
	 * ServiceClient(configContext, null); sender.setOptions(options);
	 * 
	 * 
	 * OMElement result = sender.sendReceive(payload);
	 * //System.out.println(result.toString());
	 * 
	 * } catch (AxisFault axisFault) { axisFault.printStackTrace(); } }
	 */
}
