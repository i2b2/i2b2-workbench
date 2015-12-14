/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */
package edu.harvard.i2b2.patientMapping.dataModel;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.common.util.jaxb.DTOFactory;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainDateType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByDate;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetTermInfoType;
import edu.harvard.i2b2.eclipse.plugins.patientMapping.ontologyMessaging.GetTermInfoResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.patientMapping.ontologyMessaging.OntServiceDriver;
import edu.harvard.i2b2.patientMapping.datavo.PatientMappingJAXBUtil;

public class QueryModel {
	private static final Log log = LogFactory.getLog(QueryModel.class);

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
		if (str != null) {
			tooltip = new String(str);
		}
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
	
	private boolean isModifier = false;

	public void isModifier(boolean b) {
		isModifier = b;
	}

	public boolean isModifier() {
		return isModifier;
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

	private String xmlContent = "";

	public void xmlContent(String str) {
		xmlContent = str;
	}

	public String xmlContent() {
		return xmlContent;
	}

	private boolean hasValue = false;

	public void hasValue(boolean b) {
		hasValue = b;
	}

	public boolean hasValue() {
		return hasValue;
	}

	private boolean hasValueSet = false;

	public void hasValueSet(boolean b) {
		hasValueSet = b;
	}

	public boolean hasValueSet() {
		return hasValueSet;
	}

	private PSMValueModel valueModel;

	public PSMValueModel valueModel() {
		return valueModel;
	}

	public void valueModel(PSMValueModel d) {
		valueModel = d;
	}

	private ConceptTableRow tableRow;

	public void tableRow(ConceptTableRow row) {
		tableRow = row;
	}

	public ConceptTableRow tableRow() {
		return tableRow;
	}

	private String valueName = "";

	public void valueName(String str) {
		valueName = new String(str);
	}

	public String valueName() {
		return valueName;
	}

	public QueryModel() {
		valueModel = new PSMValueModel();
		tableRow = new ConceptTableRow();
	}

	public void valueModel(String Xmlcontent) {

	}
	
	public void updateNodeMetaDataXML() {
		try {
			boolean hasValue = false;

			SAXBuilder parser = new SAXBuilder();
			GetTermInfoType vocab = new GetTermInfoType();
			vocab.setHiddens(true);
			vocab.setSynonyms(false);
			vocab.setMax(200);
			vocab.setType("default");
			vocab.setBlob(true);

			vocab.setSelf(this.fullname());
			String xmlContent = OntServiceDriver.getTermInfo(vocab, "");
			
			GetTermInfoResponseMessage msg = new GetTermInfoResponseMessage();
			StatusType procStatus = null;
			// while(procStatus == null ||
			// !procStatus.getType().equals("DONE")){
			//String response = OntServiceDriver.getTermInfo(termInfoType, "");
			//log.debug("Ontology service getTermInfo response: " + response);

			procStatus = msg.processResult(xmlContent);
			// log.info(procStatus.getType());
			// log.info(procStatus.getValue());
			//if (!procStatus.getType().equals("DONE")) {
				//return "error";
			//}
			ConceptsType allConcepts = msg.doReadConcepts();
			StringWriter strWriter = new StringWriter();
			try {
				// strWriter = new StringWriter();
				DndType dnd = new DndType();
				edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory vdoOf = new edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
				dnd.getAny().add(vdoOf.createConcepts(allConcepts));

				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
				PatientMappingJAXBUtil.getJAXBUtil().marshaller(
						of.createPluginDragDrop(dnd), strWriter);

			} catch (JAXBUtilException e) {
				// log.error("Error marshalling Ont drag text");
				//return "error";
			}

			log.debug("Node xml set to: " + strWriter.toString());
			xmlContent(strWriter.toString());
			
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);

			org.jdom.Element elementMsgBody = tableDoc.getRootElement()
					.getChild("message_body"); // , Namespace.getNamespace(
			// "http://www.i2b2.org/xsd/cell/ont/1.1/"
			// ));
			org.jdom.Element tableXml = elementMsgBody
					.getChild(
							"concepts",
							Namespace
									.getNamespace("http://www.i2b2.org/xsd/cell/ont/1.1/"));

			List conceptChildren = tableXml.getChildren();
			for (Iterator itr = conceptChildren.iterator(); itr.hasNext();) {
				Element conceptXml = (org.jdom.Element) itr.next();
				//String conceptText = conceptXml.getText().trim();

				Element conTableXml = conceptXml;// .getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("name");
				String c_name = nameXml.getText();
				//titleName(c_name);

				//Element visualAttribs = conTableXml
						//.getChild("visualattributes");
				//String sVisualAttribs = visualAttribs.getText().trim();

				Element metadataAttribs = conTableXml.getChild("metadataxml");
				Element valuedataAttribs = metadataAttribs
						.getChild("ValueMetadata");

				if ((metadataAttribs != null) && (valuedataAttribs != null)) {
					log.debug("Has value");
					hasValue = true;
					this.hasValue(hasValue);
					Element dataTypeElement = valuedataAttribs
							.getChild("DataType");
					if (dataTypeElement != null
							&& dataTypeElement.getTextTrim().equalsIgnoreCase(
									"Enum")) {
						// add text values to node data
						this.valueModel().hasEnumValue(true);
						Element enumElement = valuedataAttribs
								.getChild("EnumValues");
						for (int i = 0; i < enumElement.getChildren().size(); i++) {
							Element valElement = (Element) enumElement
									.getChildren().get(i);
							String valString = new String(valElement
									.getTextTrim());
							this.valueModel().enumValues.add(valString);
						}
						log.debug("Got vals: "
								+ this.valueModel().enumValues.size());
					} else if (dataTypeElement != null
							&& (dataTypeElement.getTextTrim().equalsIgnoreCase(
							"String")||dataTypeElement.getTextTrim().equalsIgnoreCase(
							"LargeString"))) {
						// add text values to node data
						this.valueModel().hasStringValue(true);
						Element maxLengthElement = valuedataAttribs
							.getChild("MaxStringLength");
						String valString = new String(maxLengthElement.getTextTrim());
						if(!valString.equalsIgnoreCase("")) {
							valueModel().searchStrLength(Integer.parseInt(valString));
						}
						if(dataTypeElement.getTextTrim().equalsIgnoreCase(
						"LargeString")) {
							this.valueModel().isLongText(true);
						}
						/*Element enumElement = valuedataAttribs
								.getChild("EnumValues");
						for (int i = 0; i < enumElement.getChildren().size(); i++) {
							Element valElement = (Element) enumElement
									.getChildren().get(i);
							String valString = new String(valElement
									.getTextTrim());
							String valDisplayString = new String(valElement
									.getAttributeValue("description"));
							if(valDisplayString.equalsIgnoreCase("")) {
								valDisplayString = valString;
							}
							modifierValuePropertyData.enumValues.add(valString);
							modifierValuePropertyData.enumValueNames.add(valDisplayString);
						}
						log.debug("Got vals: "
								+ this.modifierValuePropertyData.enumValues.size());*/
					}

					if (valuedataAttribs.getChild("Oktousevalues") != null
							&& valuedataAttribs.getChild("Oktousevalues")
									.getText().equalsIgnoreCase("Y")) {
						this.valueModel().okToUseValue(true);
					}

					if (valuedataAttribs.getChild("Flagstouse") == null
							|| valuedataAttribs.getChild("Flagstouse")
									.getText().equalsIgnoreCase("")) {
						this.valueModel().okToUseValueFlag(false);
					}

					Element unitElement = valuedataAttribs
							.getChild("UnitValues");
					if (unitElement != null) {
						for (int i = 0; i < unitElement.getChildren().size(); i++) {
							Element element = (Element) unitElement
									.getChildren().get(i);
							if (element.getName().equalsIgnoreCase(
									"NormalUnits") || element.getName().equalsIgnoreCase(
									"EqualUnits")) {
								String unitString = new String(element
										.getTextTrim());
								//this.valueModel().units.add(new UnitsData(unitString, 1, false));
							}
							else if (element.getName().equalsIgnoreCase("ConvertingUnits")) {
								Element cunitElement = element.getChild("Units");
								String unitString = new String(cunitElement
										.getTextTrim());
								Element mfElement = element.getChild("MultiplyingFactor");
								//double mf = Double.parseDouble(mfElement.getTextTrim());
								//this.valueModel.units.add(new UnitsData(unitString, mf, true));
					}
						}
						log.debug("Got vals: "
								+ this.valueModel().enumValues.size());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String setXmlContent() {
		//if (!xmlContent.equals("")) {
			//return "";
		//}
		// calling getTermInfo to get the xml content
		try {
			GetTermInfoType termInfoType = new GetTermInfoType();

			termInfoType.setMax(null);// Integer.parseInt(System.getProperty(
			// "OntMax")));
			termInfoType.setHiddens(Boolean.parseBoolean("true"));
			termInfoType.setSynonyms(Boolean.parseBoolean("false"));

			// log.info("sent : " + parentType.getMax() +
			// System.getProperty("OntMax") + System.getProperty("OntHiddens")
			// + System.getProperty("OntSynonyms") );

			// parentType.setMax(150);
			termInfoType.setBlob(true);
			// parentType.setType("all");

			termInfoType.setSelf(fullname());

			// Long time = System.currentTimeMillis();
			// log.info("making web service call " + time);
			GetTermInfoResponseMessage msg = new GetTermInfoResponseMessage();
			StatusType procStatus = null;
			// while(procStatus == null ||
			// !procStatus.getType().equals("DONE")){
			String response = OntServiceDriver.getTermInfo(termInfoType, "");
			log.debug("Ontology service getTermInfo response: " + response);

			procStatus = msg.processResult(response);
			// log.info(procStatus.getType());
			// log.info(procStatus.getValue());
			if (!procStatus.getType().equals("DONE")) {
				return "error";
			}
			ConceptsType allConcepts = msg.doReadConcepts();
			// List<ConceptType> concepts = allConcepts.getConcept();
			StringWriter strWriter = new StringWriter();
			try {
				// strWriter = new StringWriter();
				DndType dnd = new DndType();
				edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory vdoOf = new edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
				dnd.getAny().add(vdoOf.createConcepts(allConcepts));

				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
				PatientMappingJAXBUtil.getJAXBUtil().marshaller(
						of.createPluginDragDrop(dnd), strWriter);

			} catch (JAXBUtilException e) {
				// log.error("Error marshalling Ont drag text");
				return "error";
			}

			log.debug("Node xml set to: " + strWriter.toString());
			xmlContent(strWriter.toString());
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	public void setTimeConstrain(ConstrainDateType from, ConstrainDateType to) {

		Calendar cal = Calendar.getInstance();
		if (from != null) {

			startYear = from.getValue().getYear();
			startMonth = from.getValue().getMonth() - 1;
			startDay = from.getValue().getDay();
			cal.set(startYear, startMonth, startDay);
			startTime = cal.getTimeInMillis();
		}
		if (to != null) {

			endYear = to.getValue().getYear();
			endMonth = to.getValue().getMonth() - 1;
			endDay = to.getValue().getDay();
			cal.set(endYear, endMonth, endDay);
			endTime = cal.getTimeInMillis();

		}
	}

	public ConstrainByDate writeTimeConstrain() {
		ConstrainByDate timeConstrain = new ConstrainByDate();
		DTOFactory dtoFactory = new DTOFactory();

		if (startTime() != -1) {
			ConstrainDateType constraindateType = new ConstrainDateType();
			constraindateType.setValue(dtoFactory.getXMLGregorianCalendarDate(
					startYear(), startMonth(), startDay()));

			timeConstrain.setDateFrom(constraindateType);
		}
		if (endTime() != -1) {
			ConstrainDateType constraindateType = new ConstrainDateType();
			constraindateType.setValue(dtoFactory.getXMLGregorianCalendarDate(
					endYear(), endMonth(), endDay()));
			timeConstrain.setDateTo(constraindateType);
		}
		return timeConstrain;
	}

	public void setValueConstrains(List<ConstrainByValue> list) {
		
		if (list != null && list.size() > 0) {
			hasValue(true);
			ConstrainByValue cons = list.get(0);
			if (cons.getValueConstraint() == null) {
				// hasValue(false);
				return;
			}
			if (cons.getValueType().equals(ConstrainValueType.NUMBER)) {
				this.valueModel().useNumericValue(true);
			} else if (cons.getValueType().equals(ConstrainValueType.TEXT)
					|| cons.getValueType().equals(ConstrainValueType.LARGETEXT)) {
				this.valueModel().useTextValue(true);
			} else if (cons.getValueType().equals(ConstrainValueType.FLAG)) {
				this.valueModel().useValueFlag(true);
			} else if (cons.getValueType().equals(ConstrainValueType.MODIFIER)) {
				// this.valuePropertyData().u(true);
			}
			
			updateNodeMetaDataXML();
			if(this.valueModel().hasStringValue() 
					&& this.valueModel().useTextValue()) {
				this.valueModel().useStringValue(true);
				this.valueModel().useTextValue(false);
			}
			
			valueModel().noValue(false);
			if (cons.getValueOperator() != null) {
				valueModel().operator(cons.getValueOperator().value());
			}
			valueModel().value(cons.getValueConstraint());
			if (cons.getValueUnitOfMeasure() != null) {
				valueModel.unit(cons.getValueUnitOfMeasure());
			}

			valueModel().okToUseValue(true);
			valueName(getOperator(valueModel().operator()).toLowerCase() + " "
					+ valueModel().value());

		} else {
			hasValue(false);
		}
	}

	protected String getOperator(String opStr) {
		String result = "";
		if (opStr == null) {
			return result;
		}

		if (opStr.equalsIgnoreCase("LT")) {
			result = "<";
		} else if (opStr.equalsIgnoreCase("LE")) {
			result = "<=";
		} else if (opStr.equalsIgnoreCase("=")) {
			result = "=";
		} else if (opStr.equalsIgnoreCase("GT")) {
			result = ">";
		} else if (opStr.equalsIgnoreCase("GE")) {
			result = ">=";
		} else if (opStr.equalsIgnoreCase("BETWEEN")) {
			return opStr;
		}

		return result;
	}

	@Override
	public String toString() {
		return name;
	}

	public List<ConstrainByValue> constrainByValue;

	public void constrainByValue(List<ConstrainByValue> conByValue) {
		constrainByValue = conByValue;
	}
}
