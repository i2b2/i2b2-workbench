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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.*;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class DotNetClient {

	private static EndpointReference targetEPR = new EndpointReference(
	// "http://rpdrdev3/RPDRServices/webservicetest.asmx");
			// "https://rpdrssl.partners.org/RPDRServices/webservicetest.asmx");
			"http://rpdrdev3/RPDRServices/PatientDataServices.asmx");

	public static OMElement getQueryPayLoad(String XMLstr) throws Exception {
		StringReader strReader = new StringReader(XMLstr);
		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLStreamReader reader = xif.createXMLStreamReader(strReader);

		StAXOMBuilder builder = new StAXOMBuilder(reader);
		OMElement lineItem = builder.getDocumentElement();
		System.out.println("Line item string " + lineItem.toString());
		return lineItem;
	}

	/**
	 * Function to convert soap requestVdo to OMElement
	 * 
	 * @param requestVdo
	 *            String requestVdo to send to web service
	 * @return An OMElement containing the web service requestVdo
	 */
	public static OMElement getSoapPayLoad(String requestVdo) throws Exception {
		OMElement method = null;
		try {
			OMFactory fac = OMAbstractFactory.getOMFactory();
			OMNamespace omNs = fac.createOMNamespace(
					"http://rpdr.partners.org/", "rpdr");

			method = fac.createOMElement("GetSinglePatientData", omNs);
			OMElement value = fac
					.createOMElement("PatientDataRequestXml", omNs);

			/*
			 * StringReader strReader = new StringReader(requestVdo);
			 * XMLInputFactory xif = XMLInputFactory.newInstance();
			 * XMLStreamReader reader = xif.createXMLStreamReader(strReader);
			 * 
			 * StAXOMBuilder builder = new StAXOMBuilder(reader); //method =
			 * builder.getDocumentElement(); OMElement lineItem =
			 * builder.getDocumentElement();
			 */
			// System.out.println("Request string: " + requestVdo);
			// method.addChild(lineItem);
			value.setText(requestVdo);
			method.addChild(value);
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// log.error(e.getMessage());
			throw new Exception(e);
		}
		return method;
	}

	public static String getQueryString() throws Exception {
		StringBuffer queryStr = new StringBuffer();
		DataInputStream dataStream = new DataInputStream(new FileInputStream(
				"c:\\test\\pdo_request_of_a_single_patient.xml"));
		while (dataStream.available() > 0) {
			queryStr.append(dataStream.readLine());// + "\n");
		}
		// System.out.println("queryStr: " + queryStr);
		return // "<HelloWorld xmlns=\"http://rpdr.partners.org/\">"
		// +"<PatientDataRequestXml>"
		// +"<string>"
		// +"<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
		// +"<string xmlns=\"http://rpdr.partners.org/\">"
		// +"<TestXMLString>"
		queryStr.toString();
		// +"</TestXMLString>"
		// +""
		// +"</string>"
		// +"</PatientDataRequestXml>"
		// +"</HelloWorld >";
		// return queryStr.toString();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// HttpTransportProperties.Authenticator basicAuthentication = new
			// HttpTransportProperties.Authenticator();

			// basicAuthentication.setUsername("partners\\lcs6");
			// basicAuthentication.setPassword("2222xscz");
			String request = getQueryString();
			System.out.println("Request string: " + request);
			OMElement payload = getSoapPayLoad(// "<HelloWorld
			// xmlns=\"http://rpdr.partners.org/\"
			// />");
			request);
			// System.out.println("Payload string: " + payload.toString());
			// "<HelloWorld xmlns=\"http://tempuri.org/\" />");
			Options options = new Options();

			// options.setProperty(HTTPConstants.PROXY, proxyProperties);
			options.setTo(targetEPR);
			options.setAction(// "http://rpdr.partners.org/HelloWorld");
					"http://rpdr.partners.org/GetSinglePatientData");
			// "http://tempuri.org/HelloWorld");
			// options.setProperty(org.apache.axis2.transport.http.HTTPConstants.
			// AUTHENTICATE,
			// basicAuthentication);

			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

			// ConfigurationContext configContext = ConfigurationContextFactory
			// .createConfigurationContextFromFileSystem(null, null);

			// Blocking invocation
			ServiceClient sender = new ServiceClient();
			sender.setOptions(options);

			OMElement result = sender.sendReceive(payload);
			// getQueryPayLoad(result.toString());
			System.out.println(result.toString());

		} catch (AxisFault axisFault) {
			axisFault.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
