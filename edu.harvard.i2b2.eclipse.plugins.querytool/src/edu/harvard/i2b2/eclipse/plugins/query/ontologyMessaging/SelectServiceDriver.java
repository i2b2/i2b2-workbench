/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * 
 * 
 * Contributors:
 * 		Lori Phillips
 * 		Wensong Pan (ported it here from ontology plug-in)
 */
package edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging;

import java.util.Iterator;
import java.util.logging.Logger;

import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;

/**
 * The SelectServiceDriver class controls calls to/from the Select web service
 * 
 * @author Lori Phillips
 */

public class SelectServiceDriver {

	public static final String THIS_CLASS_NAME = SelectServiceDriver.class
			.getName();

	/**
	 * The constructor
	 */
	public SelectServiceDriver() {
	}

	/**
	 * Makes call to the Select Web Service
	 * 
	 * @param selectParameters
	 *            a document describing the input parameter to the Select Web
	 *            service
	 * @return a document containing the result of the Select web service
	 *         transaction
	 */

	public Document callSelects(Document selectParameters) {
		Document results = null;
		try {
			java.net.URL endpoint = new java.net.URL(System
					.getProperty("ontologywebservice"));
			// make web service call depending upon query type
			// SelectServiceLocator locator = new SelectServiceLocator();
			// Select facade = locator.getSelect(endpoint);
			// results = facade.getDataMartRecords(selectParameters);
			// }
			// catch(RemoteException e) {
			// Unauthorized service exceptions appear here.
			// Need a way to tell user to log in
			// Logger.getLogger(THIS_CLASS_NAME).severe(e.getMessage());
			// }
			// catch(javax.xml.rpc.ServiceException e) {
			// Logger.getLogger(THIS_CLASS_NAME).severe(e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(THIS_CLASS_NAME).severe(e.getMessage());
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * Generates input to the Select Web Service
	 * 
	 * @param code
	 *            the concept code we are inquiring about
	 * @return a document containing the input to send to the Select web service
	 */

	public Document buildSelectParameters(String code) {
		Document selectParameters = null;
		try {
			org.jdom.Element selectElement = new org.jdom.Element(
					"selectParameters");
			org.jdom.Document jqueryDoc = new org.jdom.Document(selectElement);
			org.jdom.Element dbElement = new org.jdom.Element("i2b2Mart");
			dbElement.setText("metadata");
			selectElement.addContent(dbElement);

			org.jdom.Element table = new org.jdom.Element("table");

			table.setText("i2b2");
			table.setAttribute("abbr", "l");
			table.setAttribute("numCols", "21");
			table.setAttribute("withBlob", "false");
			selectElement.addContent(table);
			org.jdom.Element where = new org.jdom.Element("where");
			String whereClause = "c_basecode = '" + code + "'";

			where.setText(whereClause);
			selectElement.addContent(where);
			org.jdom.Element orderBy = new org.jdom.Element("orderBy");
			orderBy.setText("c_name");
			selectElement.addContent(orderBy);

			org.jdom.output.DOMOutputter convertor = new org.jdom.output.DOMOutputter();
			selectParameters = convertor.output(jqueryDoc);
		} catch (Exception e) {
			Logger.getLogger(THIS_CLASS_NAME).severe(e.getMessage());
			e.printStackTrace();
		}

		return selectParameters;
	}

	/**
	 * Test code to print the result document from the Select web service in XML
	 * format
	 * 
	 * @param resultDoc
	 *            a document describing the results returned by the Select Web
	 *            service
	 * 
	 */
	private void printResultsAsXML(org.w3c.dom.Document resultDoc) {
		try {
			org.jdom.input.DOMBuilder builder = new org.jdom.input.DOMBuilder();
			org.jdom.Document jresultDoc = builder.build(resultDoc);
			org.jdom.Namespace ns = jresultDoc.getRootElement().getNamespace();
			System.out.println(new XMLOutputter().outputString(jresultDoc));
		} catch (Exception e) {
			Logger.getLogger(THIS_CLASS_NAME).severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Get the c_name returned from the Select web service result document
	 * 
	 * @param resultDoc
	 *            a document describing the results returned by the Select Web
	 *            service
	 * @return the c_name returned from the Select web service lookup
	 * 
	 */
	public String getNameFromDocument(org.w3c.dom.Document resultDoc) {
		String name = null;
		String code = null;
		try {
			org.jdom.input.DOMBuilder builder = new org.jdom.input.DOMBuilder();
			org.jdom.Document doc = builder.build(resultDoc);
			org.jdom.Element select = doc.getRootElement();
			org.jdom.Namespace ns = select.getNamespace();

			Iterator iterator = doc.getRootElement().getChildren("patientData",
					ns).iterator();
			while (iterator.hasNext()) {
				org.jdom.Element patientData = (org.jdom.Element) iterator
						.next();
				org.jdom.Element table = (org.jdom.Element) patientData
						.getChild("i2b2", ns).clone();
				name = table.getChildTextTrim("c_name", ns);
				code = table.getChildTextTrim("c_basecode", ns);
			}
		} catch (Exception e) {
			Logger.getLogger(THIS_CLASS_NAME).severe(e.getMessage());
			e.printStackTrace();
		}
		return name;
	}

	public static void main(String args[]) {
		System.setProperty("ontologywebservice",
				"http://localhost:8080/i2b2/services/Select");
		SelectServiceDriver driver = new SelectServiceDriver();
		Document input = driver.buildSelectParameters("I2B2");
		driver.printResultsAsXML(input);
	}
}