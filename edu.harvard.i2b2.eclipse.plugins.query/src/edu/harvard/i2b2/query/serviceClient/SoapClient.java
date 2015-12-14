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

import javax.xml.stream.FactoryConfigurationError;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;

public class SoapClient {

	private static EndpointReference targetEPR = new EndpointReference(
	// "http://rpdrdev3/RPDRServices/webservicetest.asmx"
			// "https://rpdrssl.partners.org/RPDRServices/webservicetest.asmx"
			// "http://rpdrdev3/RPDRServices/PatientDataServices.asmx"
			// "http://phsi2b2appdev.mgh.harvard.edu:9090/axis2/services/PMSoapService"
			"https://rpdrdevsecure.mgh.harvard.edu/RPDRServices/PatientDataServices.asmx");

	public static String getQueryString() throws Exception {
		StringBuffer queryStr = new StringBuffer();
		DataInputStream dataStream = new DataInputStream(new FileInputStream(
				"c:\\test\\pdo_request_of_a_single_patient.xml"));
		while (dataStream.available() > 0) {
			queryStr.append(dataStream.readLine());// + "\n");
		}
		// System.out.println("queryStr: " + queryStr);
		return // "<HelloWorld  xmlns=\"http://rpdr.partners.org/\">"
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

	public static void main(String[] args) throws Exception {
		ServiceClient client = new ServiceClient();
		OperationClient operationClient = client
				.createClient(ServiceClient.ANON_OUT_IN_OP);

		// creating message context
		MessageContext outMsgCtx = new MessageContext();
		// assigning message context's option object into instance variable
		Options opts = outMsgCtx.getOptions();
		// setting properties into option
		opts.setTo(targetEPR);
		opts.setAction("http://rpdr.partners.org/GetSinglePatientData");
		opts.setTimeOutInMilliSeconds(180000);

		SOAPEnvelope request = creatSOAPEnvelope(getQueryString());
		System.out.println("request: " + request);
		outMsgCtx.setEnvelope(request);

		operationClient.addMessageContext(outMsgCtx);
		operationClient.execute(true);

		MessageContext inMsgtCtx = operationClient.getMessageContext("In");
		SOAPEnvelope response = inMsgtCtx.getEnvelope();
		System.out.println("response: "
				+ response.getBody().getFirstElement().toStringWithConsume());
	}

	public static SOAPEnvelope creatSOAPEnvelope(String xmlStr) {
		SOAPEnvelope envelope = null;

		try {
			SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
			envelope = fac.getDefaultEnvelope();
			OMNamespace omNs = fac.createOMNamespace(
					"http://rpdr.partners.org/", "rpdr");
			// creating the payload
			OMElement method = fac
					.createOMElement("GetSinglePatientData", omNs);
			OMElement value = fac
					.createOMElement("PatientDataRequestXml", omNs);
			value.setText(xmlStr);
			method.addChild(value);
			envelope.getBody().addChild(method);
		} catch (FactoryConfigurationError e) {

			e.printStackTrace();
			return envelope;
			// log.error(e.getMessage());
			// throw new Exception(e);
		}

		return envelope;
	}
}
