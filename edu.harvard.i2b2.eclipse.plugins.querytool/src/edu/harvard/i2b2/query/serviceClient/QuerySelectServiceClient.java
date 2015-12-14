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

import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;

//import edu.harvard.i2b2.select.soap.Select;
//import edu.harvard.i2b2.select.soap.SelectService;
//import edu.harvard.i2b2.select.soap.SelectServiceLocator;

/**
 * 
 * Class: QuerySelectServiceClient.
 * 
 */

public class QuerySelectServiceClient {

	public static org.w3c.dom.Document getXMLResults(
			QueryConceptTreeNodeData data, int mode) {
		/*
		 * // send web services message to obtain children // for a given node
		 * try { // Make a service SelectService service = new
		 * SelectServiceLocator(); // Use service to get stub that implement SDI
		 * 
		 * java.net.URL endpoint = new
		 * java.net.URL(System.getProperty("selectservice")); // call is going
		 * out here System.out.println(endpoint.toString()); Select port =
		 * service.getSelect(endpoint); // Form the query org.w3c.dom.Document
		 * queryDoc = sendQuery(data, mode);
		 * 
		 * // Make the call org.w3c.dom.Document queryResultDoc =
		 * port.getDataMartRecords(queryDoc);
		 * 
		 * // System.out.println(this.data.getWebserviceName() +
		 * " Service returned"); if (queryResultDoc == null) {
		 * System.out.println("Web service call failed");
		 * //System.setProperty("statusMessage", "Web service call failed");
		 * return null; }
		 * 
		 * int nodecount =
		 * queryResultDoc.getElementsByTagName("patientData").getLength();
		 * System.out.println("total node count: "+nodecount);
		 * 
		 * return queryResultDoc; } catch(Exception e) { e.printStackTrace();
		 * System.err.println("Get Nodes: " + e.getMessage()); return null; }
		 */
		return null;
	}
}
