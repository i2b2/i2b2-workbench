/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 *     Wensong Pan
 */
package edu.harvard.i2b2.patientSet.dataModel;

import java.util.Iterator;

import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.i2b2.patientSet.ui.QueryConstraints;

public class QueryConceptTreeNodeModel implements QueryConstraints {

	private boolean inverted = false;

	public void inverted(boolean b) {
		inverted = b;
	}

	public boolean inverted() {
		return inverted;
	}

	private String hlevel;

	public void hlevel(String str) {
		hlevel = new String(str);
	}

	public String hlevel() {
		return hlevel;
	}

	private String fullname;

	public void fullname(String str) {
		fullname = new String(str);
	}

	public String fullname() {
		return fullname;
	}

	private String name = "";

	public void name(String str) {
		name = new String(str);
	}

	public String name() {
		return name;
	}

	private String visualAttribute;

	public void visualAttribute(String str) {
		visualAttribute = new String(str);
	}

	public String visualAttribute() {
		return visualAttribute;
	}

	private String factTableColumn;

	public void factTableColumn(String str) {
		factTableColumn = new String(str);
	}

	public String factTableColumn() {
		return factTableColumn;
	}

	private String tableName;

	public void tableName(String str) {
		tableName = new String(str);
	}

	public String tableName() {
		return tableName;
	}

	private String columnName;

	public void columnName(String str) {
		columnName = new String(str);
	}

	public String columnName() {
		return columnName;
	}

	private String columnDataType;

	public void columnDataType(String str) {
		columnDataType = new String(str);
	}

	public String columnDataType() {
		return columnDataType;
	}

	private String operator;

	public void operator(String str) {
		operator = new String(str);
	}

	public String operator() {
		return operator;
	}

	private String dimcode;

	public void dimcode(String str) {
		dimcode = new String(str);
	}

	public String dimcode() {
		return dimcode;
	}

	private String tooltip;

	public void tooltip(String str) {
		tooltip = new String(str);
	}

	public String tooltip() {
		return tooltip;
	}

	private String lookupdb;

	public void lookupdb(String str) {
		lookupdb = new String(str);
	}

	public String lookupdb() {
		return lookupdb;
	}

	private String lookuptable;

	public void lookuptable(String str) {
		lookuptable = new String(str);
	}

	public String lookuptable() {
		return lookuptable;
	}

	private String selectservice;

	public void selectservice(String str) {
		selectservice = new String(str);
	}

	public String selectservice() {
		return selectservice;
	}

	private int startYear = -1;

	public void startYear(int i) {
		startYear = i;
	}

	public int startYear() {
		return startYear;
	}

	private int startMonth = -1;

	public void startMonth(int i) {
		startMonth = i;
	}

	public int startMonth() {
		return startMonth;
	}

	private int startDay = -1;

	public void startDay(int i) {
		startDay = i;
	}

	public int startDay() {
		return startDay;
	}

	private long startTime = -1;

	public void startTime(long l) {
		startTime = l;
	}

	public long startTime() {
		return startTime;
	}

	private int endYear = -1;

	public void endYear(int i) {
		endYear = i;
	}

	public int endYear() {
		return endYear;
	}

	private int endMonth = -1;

	public void endMonth(int i) {
		endMonth = i;
	}

	public int endMonth() {
		return endMonth;
	}

	private int endDay = -1;

	public void endDay(int i) {
		endDay = i;
	}

	public int endDay() {
		return endDay;
	}

	private long endTime = -1;

	public void endTime(long l) {
		endTime = l;
	}

	public long endTime() {
		return endTime;
	}

	private boolean includePrincipleVisit = true;

	public void includePrincipleVisit(boolean b) {
		includePrincipleVisit = b;
	}

	public boolean includePrincipleVisit() {
		return includePrincipleVisit;
	}

	private boolean includeSecondaryVisit = true;

	public void includeSecondaryVisit(boolean b) {
		includeSecondaryVisit = b;
	}

	public boolean includeSecondaryVisit() {
		return includeSecondaryVisit;
	}

	private boolean includeAdmissionVisit = true;

	public void includeAdmissionVisit(boolean b) {
		includeAdmissionVisit = b;
	}

	public boolean includeAdmissionVisit() {
		return includeAdmissionVisit;
	}

	private String xmlContent;

	public void xmlContent(String str) {
		xmlContent = str;
	}

	public String xmlContent() {
		return xmlContent;
	}

	public QueryConceptTreeNodeModel() {
	}

	public void setXmlContent() {
		org.w3c.dom.Document doc = null;
		// TODO select service removed
		// QuerySelectServiceClient.getXMLResult(this, 1);
		org.jdom.input.DOMBuilder builder = new org.jdom.input.DOMBuilder();
		org.jdom.Document jresultDoc = builder.build(doc);
		org.jdom.Namespace ns = jresultDoc.getRootElement().getNamespace();
		// System.out.println((new XMLOutputter()).outputString(jresultDoc));
		Iterator iterator = jresultDoc.getRootElement().getChildren(
				"patientData", ns).iterator();

		String c_xml = "";
		org.jdom.Element patientData = (org.jdom.Element) iterator.next();
		org.jdom.Element lookup = (org.jdom.Element) patientData.getChild(
				lookuptable().toLowerCase(), ns).clone();
		try {
			org.jdom.Element metaDataXml = lookup.getChild("c_metadataxml");
			c_xml = metaDataXml.getText();

			if ((c_xml != null) && (c_xml.trim().length() > 0)
					&& (!c_xml.equals("(null)"))) {
				SAXBuilder parser = new SAXBuilder();
				String xmlContent = c_xml;
				java.io.StringReader xmlStringReader = new java.io.StringReader(
						xmlContent);
				org.jdom.Document tableDoc = parser.build(xmlStringReader);
				org.jdom.Element rootElement = (org.jdom.Element) tableDoc
						.getRootElement().clone();
				metaDataXml.setText("");
				metaDataXml.getChildren().add(rootElement);
			}
		} catch (Exception e) {
			System.err
					.println("getNodesFromXML: parsing XML:" + e.getMessage());
		}

		String rawContent = new XMLOutputter().outputString(lookup);
		System.out.println("Getting raw xml content: \n" + rawContent);
		System.out.println("End getting raw xml content\n");

		// String content =
		// rawContent.substring(rawContent.indexOf("<patientData>")+13,
		// rawContent.indexOf("</patientData>"));
		// System.out.println("Setting xml content: \n"+content);
		// System.out.println("End setting xml content\n");
		String sRootConceptTag = "Concepts";
		String sIndividualConceptTag = "Concept";
		String sNodesXMLRepresentation = "";
		sNodesXMLRepresentation = "<" + sRootConceptTag + ">\r\n";
		sNodesXMLRepresentation += "\t<" + sIndividualConceptTag + ">\r\n";
		sNodesXMLRepresentation += rawContent;
		sNodesXMLRepresentation += "\t<lookupdb>" + lookupdb()
				+ "</lookupdb>\r\n";
		sNodesXMLRepresentation += "\t<lookuptable>" + lookuptable()
				+ "</lookuptable>\r\n";
		sNodesXMLRepresentation += "\t<selectservice>" + selectservice()
				+ "</selectservice>\r\n";
		sNodesXMLRepresentation += "\r\n\t</" + sIndividualConceptTag + ">\r\n";
		sNodesXMLRepresentation += "</" + sRootConceptTag + ">\r\n";

		// formatting of data to make it look nice
		try {
			SAXBuilder parser = new SAXBuilder();
			String xmlContent = sNodesXMLRepresentation;
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			XMLOutputter fmt = new XMLOutputter(Format.getPrettyFormat());
			sNodesXMLRepresentation = fmt.outputString(tableDoc
					.getRootElement());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Setting xml content: \n" + sNodesXMLRepresentation);
		System.out.println("End setting xml content\n");
		xmlContent(sNodesXMLRepresentation);
	}

	@Override
	public String toString() {
		return name;
	}
}
