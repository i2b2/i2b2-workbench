/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

package edu.harvard.i2b2.query.data;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List; // import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder; // import java.util.List;

// import javax.swing.tree.DefaultMutableTreeNode;
// import javax.swing.tree.TreePath;

// import org.jdom.input.SAXBuilder;
// import org.jdom.output.Format;
// import org.jdom.output.XMLOutputter;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.GetTermInfoResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.OntServiceDriver;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType; // import
// edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ConceptsType; // import
//import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.MatchStrType;
//import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.VocabRequestType; // edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetChildrenType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetTermInfoType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainValueType;
import edu.harvard.i2b2.query.datavo.QueryJAXBUtil;
//import edu.harvard.i2b2.query.ui.GroupPanel;
import edu.harvard.i2b2.query.ui.QueryConstraints;

public class QueryConceptTreeNodeData implements QueryConstraints {
	private static final Log log = LogFactory
			.getLog(QueryConceptTreeNodeData.class);

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
	
	private boolean isModifier = false;

	public void isModifier(boolean b) {
		isModifier = b;
	}

	public boolean isModifier() {
		return isModifier;
	}

	private String name = "";

	public void name(String str) {
		name = new String(str);
	}

	public String name() {
		return name;
	}

	private String valueName = "";

	public void valueName(String str) {
		valueName = new String(str);
	}

	public String valueName() {
		return valueName;
	}

	private String titleName = "";

	public void titleName(String str) {
		titleName = new String(str);
	}

	public String titleName() {
		return titleName;
	}

	private String visualAttribute;

	public void visualAttribute(String str) {
		visualAttribute = str;
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

	public String originalXml;

	public void originalXml(String originalXml) {
		this.originalXml = originalXml;
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

	private ValuePropertyData valuePropertyData;

	public ValuePropertyData valuePropertyData() {
		return valuePropertyData;
	}

	public QueryConceptTreeNodeData() {
		valuePropertyData = new ValuePropertyData();
	}

	public String setXmlContent() {
		if (!xmlContent.equals("")) {
			return "";
		}
		// calling getTermInfo to get the xml content
		try {
			GetTermInfoType termInfoType = new GetTermInfoType();

			termInfoType.setMax(null);// Integer.parseInt(System.getProperty(
			// "OntMax")));
			termInfoType.setHiddens(Boolean.parseBoolean("true"));
			termInfoType.setSynonyms(Boolean.parseBoolean("false"));

			// parentType.setMax(150);
			termInfoType.setBlob(true);
			// parentType.setType("all");

			termInfoType.setSelf(fullname());

			// Long time = System.currentTimeMillis();
			GetTermInfoResponseMessage msg = new GetTermInfoResponseMessage();
			StatusType procStatus = null;
			String response = OntServiceDriver.getTermInfo(termInfoType, "");

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
				QueryJAXBUtil.getJAXBUtil().marshaller(
						of.createPluginDragDrop(dnd), strWriter);
			} catch (JAXBUtilException e) {
				return "error";
			}

			xmlContent(strWriter.toString());
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	public String setVisualAttributes() {
		if (!xmlContent.equals("")) {
			return "";
		}
		// calling getTermInfo to get the xml content
		try {
			GetTermInfoType termInfoType = new GetTermInfoType();

			termInfoType.setMax(null);// Integer.parseInt(System.getProperty(
			// "OntMax")));
			termInfoType.setHiddens(Boolean.parseBoolean("true"));
			termInfoType.setSynonyms(Boolean.parseBoolean("false"));

			// parentType.setMax(150);
			termInfoType.setBlob(true);
			// parentType.setType("all");

			termInfoType.setSelf(fullname());
			// System.out.println(parentType.getParent());

			// Long time = System.currentTimeMillis();
			GetTermInfoResponseMessage msg = new GetTermInfoResponseMessage();
			StatusType procStatus = null;
			String response = OntServiceDriver.getTermInfo(termInfoType, "");
			// System.out.println("Ontology service getTermInfo response:
			// "+response);

			procStatus = msg.processResult(response);
			// log.info(procStatus.getType());
			// log.info(procStatus.getValue());
			if (!procStatus.getType().equals("DONE")) {
				return "error";
			}

			ConceptsType allConcepts = msg.doReadConcepts();
			List<ConceptType> concepts = allConcepts.getConcept();
			ConceptType concept = concepts.get(0);
			if (concept.getVisualattributes() != null) {
				visualAttribute(concept.getVisualattributes().trim());
			} else {
				visualAttribute("LA");
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	public void setValueConstraints(List<ConstrainByValue> list) {
		if (list != null && list.size() > 0) {
			hasValue(true);
			ConstrainByValue cons = list.get(0);
			if (cons.getValueConstraint() == null) {
				// hasValue(false);
				return;
			}
			if (cons.getValueType().equals(ConstrainValueType.NUMBER)) {
				this.valuePropertyData().useNumericValue(true);
			} else if (cons.getValueType().equals(ConstrainValueType.TEXT) ||
					cons.getValueType().equals(ConstrainValueType.LARGETEXT)) {
				this.valuePropertyData().useTextValue(true);
			} else if (cons.getValueType().equals(ConstrainValueType.FLAG)) {
				this.valuePropertyData().useValueFlag(true);
			} else if (cons.getValueType().equals(ConstrainValueType.MODIFIER)) {
				// this.valuePropertyData().u(true);
			}

			// Update MetadataXML if needed
			updateNodeMetaDataXML();
			if(this.valuePropertyData().hasStringValue() 
					&& this.valuePropertyData().useTextValue()) {
				this.valuePropertyData().useStringValue(true);
				this.valuePropertyData().useTextValue(false);
			}

			this.valuePropertyData().noValue(false);
			// this.valuePropertyData().useValueFlag(false);
			this.valuePropertyData().value(cons.getValueConstraint());
			
			for (int i = 0; i < valuePropertyData.enumValues.size(); i++) {
				String eval = valuePropertyData.enumValues.get(i);
				String teval = "'"+eval+"'";
				if (valuePropertyData().value().indexOf(teval)>=0) {
					//String valStr = (String) jEnumValueTable.getValueAt(i, 1);
					//String valStr = data.modifierValuePropertyData().enumValues.get(i);
					valuePropertyData().selectedValues.add(eval);
				}
			}
			
			if (this.valuePropertyData().selectedValues.size() > 0) {
				this.valueName(" Is " + valuePropertyData().selectedValues.get(0));
			}
			
			if (this.valuePropertyData().selectedValues.size() > 1) {
				for (int j = 1; j < this.valuePropertyData().selectedValues
						.size(); j++) {
					this.valueName(this.valueName() + ","
							+ this.valuePropertyData().selectedValues.get(j));
				}
			}
			
			if (cons.getValueUnitOfMeasure() != null)
				this.valuePropertyData.unit(cons.getValueUnitOfMeasure());
			if (cons.getValueOperator() != null) {
				this.valuePropertyData().operator(
						cons.getValueOperator().value());

				// deal with between...
				if (this.valuePropertyData().operator().equalsIgnoreCase(
						"between")) {
					String[] result = cons.getValueConstraint().split(" and ");
					if (result != null && result.length == 2) {
						this.valuePropertyData().lowValue(result[0]);
						this.valuePropertyData().highValue(result[1]);
					}
				}

			}
			
			//if (this.valuePropertyData().selectedValues.size() > 0) {
			//	this.valueName(" Is " + valuePropertyData().selectedValues.get(0));
			//}
			
			//if (this.valuePropertyData().selectedValues.size() > 1) {
			//	for (int j = 1; j < this.valuePropertyData().selectedValues
			//			.size(); j++) {
			//		this.valueName(this.valueName() + ","
			//				+ this.valuePropertyData().selectedValues.get(j));
				//}
			//}
			this.valuePropertyData().okToUseValue(true);
			if (this.valuePropertyData().selectedValues.size() <= 0) {
				this.valueName(" ["+getOperator(this.valuePropertyData().operator())
				 + " \""+ this.valuePropertyData().value()+"\"]");
			}
			// this.valueName(getOperator(this.valuePropertyData().operator()));
			// + this.valuePropertyData().value());

		} else {
			hasValue(false);
		}
	}

	@SuppressWarnings("unchecked")
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
				titleName(c_name);

				//Element visualAttribs = conTableXml
						//.getChild("visualattributes");
				//String sVisualAttribs = visualAttribs.getText().trim();

				Element metadataAttribs = conTableXml.getChild("metadataxml");
				Element valuedataAttribs = null;
				if(metadataAttribs != null) {
					valuedataAttribs = metadataAttribs
						.getChild("ValueMetadata");
				}

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
						this.valuePropertyData().hasEnumValue(true);
						Element enumElement = valuedataAttribs
								.getChild("EnumValues");
						for (int i = 0; i < enumElement.getChildren().size(); i++) {
							Element valElement = (Element) enumElement
									.getChildren().get(i);
							String valString = new String(valElement
									.getTextTrim());
							this.valuePropertyData().enumValues.add(valString);
						}
						log.debug("Got vals: "
								+ this.valuePropertyData().enumValues.size());
					} else if (dataTypeElement != null
							&& (dataTypeElement.getTextTrim().equalsIgnoreCase(
							"String"))
							|| (dataTypeElement.getTextTrim().equalsIgnoreCase(
							"LargeString"))) {
						// add text values to node data
						valuePropertyData.hasStringValue(true);
						Element maxLengthElement = valuedataAttribs
						.getChild("MaxStringLength");
						String valString = new String(maxLengthElement.getTextTrim());
						if(!valString.equalsIgnoreCase("")) {
							valuePropertyData.searchStrLength(Integer.parseInt(valString));
						}
						if((dataTypeElement.getTextTrim().equalsIgnoreCase(
							"LargeString"))) {
								valuePropertyData.isLongText(true);
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
						this.valuePropertyData().okToUseValue(true);
					}

					if (valuedataAttribs.getChild("Flagstouse") == null
							|| valuedataAttribs.getChild("Flagstouse")
									.getText().equalsIgnoreCase("")) {
						this.valuePropertyData().okToUseValueFlag(false);
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
								this.valuePropertyData().units.add(new UnitsData(unitString, 1, false));
							}
							else if (element.getName().equalsIgnoreCase("ConvertingUnits")) {
								Element cunitElement = element.getChild("Units");
								String unitString = new String(cunitElement
										.getTextTrim());
								Element mfElement = element.getChild("MultiplyingFactor");
								if(mfElement !=null && !mfElement.getTextTrim().equalsIgnoreCase("")) {								
									double mf = Double.parseDouble(mfElement.getTextTrim());
									this.valuePropertyData().units.add(new UnitsData(unitString, mf, true));
								}
					}
						}
						log.debug("Got vals: "
								+ this.valuePropertyData().enumValues.size());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
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
		} else if (opStr.equalsIgnoreCase("LIKE[contains]")) {
			result = "Contains";
		} else if (opStr.equalsIgnoreCase("LIKE[begin]")) {
			result = "Begin with";
		} else if (opStr.equalsIgnoreCase("LIKE[end]")) {
			result = "End with";
		} else if (opStr.equalsIgnoreCase("LIKE[exact]")) {
			result = "Exact";
		} else if (opStr.equalsIgnoreCase("CONTAINS")) {
			result = "Contains";
		} else if (opStr.equalsIgnoreCase("Contains[database]")) {
			result = "Contains[database]";
		}

		return result;
	}

	/*
	 * old implementation using select service public void setXmlContent() {
	 * org.w3c.dom.Document doc = QuerySelectServiceClient.getXMLResult(this,
	 * 1); org.jdom.input.DOMBuilder builder = new org.jdom.input.DOMBuilder();
	 * org.jdom.Document jresultDoc = builder.build(doc); org.jdom.Namespace ns
	 * = jresultDoc.getRootElement().getNamespace(); // System.out.println((new
	 * XMLOutputter()).outputString(jresultDoc)); Iterator iterator =
	 * jresultDoc.getRootElement().getChildren("patientData", ns).iterator();
	 * 
	 * String c_xml = ""; org.jdom.Element patientData = (org.jdom.Element)
	 * iterator.next(); org.jdom.Element lookup = (org.jdom.Element)
	 * patientData.getChild(lookuptable().toLowerCase(), ns).clone(); try {
	 * org.jdom.Element metaDataXml = (org.jdom.Element)
	 * lookup.getChild("c_metadataxml"); c_xml = metaDataXml.getText();
	 * 
	 * if ((c_xml!=null)&&(c_xml.trim().length()>0)&&(!c_xml.equals("(null)")))
	 * { SAXBuilder parser = new SAXBuilder(); String xmlContent = c_xml;
	 * java.io.StringReader xmlStringReader = new
	 * java.io.StringReader(xmlContent); org.jdom.Document tableDoc =
	 * parser.build(xmlStringReader); org.jdom.Element rootElement =
	 * (org.jdom.Element) tableDoc.getRootElement().clone();
	 * metaDataXml.setText(""); metaDataXml.getChildren().add(rootElement); } }
	 * catch (Exception e) { System.err.println("getNodesFromXML: parsing XML:"
	 * + e.getMessage()); }
	 * 
	 * String rawContent = new XMLOutputter().outputString(lookup);
	 * System.out.println("Getting raw xml content: \n"+rawContent);
	 * System.out.println("End getting raw xml content\n");
	 * 
	 * //String content =
	 * rawContent.substring(rawContent.indexOf("<patientData>")+13, //
	 * rawContent.indexOf("</patientData>")); //System.out.println("Setting xml
	 * content: \n"+content); //System.out.println("End setting xml content\n");
	 * String sRootConceptTag = "Concepts"; String sIndividualConceptTag =
	 * "Concept"; String sNodesXMLRepresentation = ""; sNodesXMLRepresentation =
	 * "<" + sRootConceptTag + ">\r\n"; sNodesXMLRepresentation += "\t<" +
	 * sIndividualConceptTag + ">\r\n"; sNodesXMLRepresentation += rawContent;
	 * sNodesXMLRepresentation += "\t<lookupdb>" + lookupdb() +
	 * "</lookupdb>\r\n"; sNodesXMLRepresentation += "\t<lookuptable>" +
	 * lookuptable() + "</lookuptable>\r\n"; sNodesXMLRepresentation +=
	 * "\t<selectservice>" + selectservice() + "</selectservice>\r\n";
	 * sNodesXMLRepresentation += "\r\n\t</" + sIndividualConceptTag + ">\r\n";
	 * sNodesXMLRepresentation += "</" + sRootConceptTag + ">\r\n";
	 * 
	 * //formatting of data to make it look nice try { SAXBuilder parser = new
	 * SAXBuilder(); String xmlContent = sNodesXMLRepresentation;
	 * java.io.StringReader xmlStringReader = new
	 * java.io.StringReader(xmlContent); org.jdom.Document tableDoc =
	 * parser.build(xmlStringReader); XMLOutputter fmt = new
	 * XMLOutputter(Format.getPrettyFormat()); sNodesXMLRepresentation =
	 * fmt.outputString(tableDoc.getRootElement()); } catch(Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * System.out.println("Setting xml content: \n"+sNodesXMLRepresentation);
	 * System.out.println("End setting xml content\n");
	 * xmlContent(sNodesXMLRepresentation); }
	 */

	@Override
	public String toString() {
		return name + valueName()+" "+this.valuePropertyData.unit();
	}
}
