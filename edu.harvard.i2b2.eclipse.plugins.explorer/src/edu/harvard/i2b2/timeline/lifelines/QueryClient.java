/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 * 		Wensong Pan
 *     
 */
package edu.harvard.i2b2.timeline.lifelines;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
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
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QueryClient {
	private static final Log log = LogFactory.getLog(QueryClient.class);
	private static EndpointReference targetEPR;

	private static String servicename = null;

	public static OMElement getQueryPayLoad(String queryXML)
			throws XMLStreamException {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace("http://mgh.harvard.edu/i2b2",
				"");
		OMElement method = fac.createOMElement("queryPatient", omNs);

		StringReader strReader = new StringReader(queryXML);
		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLStreamReader reader = xif.createXMLStreamReader(strReader);

		StAXOMBuilder builder = new StAXOMBuilder(reader);
		OMElement lineItem = builder.getDocumentElement();
		method.addChild(lineItem);
		return method;
	}

	public static String query(String queryXML, String user, String password) {
		try {
			HttpTransportProperties.Authenticator basicAuthentication = new HttpTransportProperties.Authenticator();

			basicAuthentication.setUsername(user);
			basicAuthentication.setPassword(password);

			targetEPR = new EndpointReference(
					getCRCNavigatorIdentityServiceName());
			OMElement payload = getQueryPayLoad(queryXML);
			Options options = new Options();
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
			// System.out.println(result.toString());

			return result.toString();
		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
			// JOptionPane.showMessageDialog(null, axisFault.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		try {

			OMElement payload = getQueryPayLoad(" ");
			Options options = new Options();
			options.setTo(targetEPR);

			// Blocking invocation
			ServiceClient sender = new ServiceClient();
			sender.setOptions(options);
			OMElement result = sender.sendReceive(payload);
			System.out.println(result.toString());

		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
		}
	}

	private static String getCRCNavigatorIdentityServiceName() {
		// if(servicename != null) {
		// return servicename;
		// }

		servicename = System.getProperty("identityService");
		/*
		 * Properties properties = new Properties(); //String
		 * identityServiceName=""; String filename="crcnavigator.properties";
		 * try { properties.load(new FileInputStream(filename)); servicename =
		 * properties.getProperty("identityserviceName");
		 * System.out.println("Properties Identity Service Name = " +
		 * servicename); } catch (IOException e) { log.error(e.getMessage());
		 * servicename=""; }
		 */
		// log.info("identityservicename = "+servicename);

		return servicename;
	}
}
