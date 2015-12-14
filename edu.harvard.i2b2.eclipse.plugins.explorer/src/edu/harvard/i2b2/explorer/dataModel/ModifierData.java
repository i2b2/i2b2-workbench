/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

package edu.harvard.i2b2.explorer.dataModel;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByModifier;
//import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetModifierInfoType;
//import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetTermInfoType;
import edu.harvard.i2b2.eclipse.plugins.explorer.ontologyMessaging.OntServiceDriver;

public class ModifierData extends QueryModel {
	private static final Log log = LogFactory.getLog(QueryModel.class);
	
	private String modifier_name;

	public void modifier_name(String str) {
		modifier_name = new String(str);
	}

	public String modifier_name() {
		return modifier_name;
	}
	
	private String modifier_key;
	
	public void modifier_key(String str) {
		modifier_key = new String(str);
	}

	public String modifier_key() {
		return modifier_key;
	}
	
	private String modifier_display_name = "";
	
	public void modifier_display_name(String str) {
		modifier_display_name = new String(str);
	}

	public String modifier_display_name() {
		return modifier_display_name;
	}
	
	private String applied_path;
	
	public void applied_path(String str) {
		applied_path = new String(str);
	}

	public String applied_path() {
		return applied_path;
	}
	
	private PSMValueModel modifierValuePropertyData;
	
	public PSMValueModel modifierValuePropertyData() {
		return modifierValuePropertyData;
	}
	
	private boolean hasModifierValue = false;
	
	public ModifierData() {
		super();
		modifierValuePropertyData = new PSMValueModel();
	}
	
	public boolean hasModifierValue() {
		return hasModifierValue;
	}
	
	public ConstrainByModifier writeModifierConstraint() {
		
		ConstrainByModifier modifierConstraint = new ConstrainByModifier();
		modifierConstraint.setAppliedPath(applied_path);
		modifierConstraint.setModifierKey(modifier_key);
		modifierConstraint.setModifierName(modifier_name);
		
		// handle value constraint
		if (!modifierValuePropertyData.noValue()) {
			
			ConstrainByModifier.ConstrainByValue valueConstrain = modifierValuePropertyData.writeModifierValueConstraint();
			modifierConstraint.getConstrainByValue().add(valueConstrain);
		}
		
		return modifierConstraint;
	}
	
/*	public void updateModifierMetaDataXML() {
		try {
			//boolean hasValue = false;

			SAXBuilder parser = new SAXBuilder();
			GetModifierInfoType vocab = new GetModifierInfoType();
			vocab.setHiddens(true);
			vocab.setSynonyms(false);
			vocab.setMax(200);
			vocab.setType("default");
			vocab.setBlob(true);
			vocab.setAppliedPath(applied_path);
			vocab.setSelf(modifier_key);
			/String xmlContent = OntServiceDriver.getModifierInfo(vocab, "");
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);

			org.jdom.Element elementMsgBody = tableDoc.getRootElement()
					.getChild("message_body"); // , Namespace.getNamespace(
			// "http://www.i2b2.org/xsd/cell/ont/1.1/"
			// ));
			org.jdom.Element tableXml = elementMsgBody
					.getChild(
							"modifiers",
							Namespace
									.getNamespace("http://www.i2b2.org/xsd/cell/ont/1.1/"));

			List conceptChildren = tableXml.getChildren();
			for (Iterator itr = conceptChildren.iterator(); itr.hasNext();) {
				Element conceptXml = (org.jdom.Element) itr.next();
				String conceptText = conceptXml.getText().trim();

				Element conTableXml = conceptXml;// .getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("name");
				String c_name = nameXml.getText();
				titleName(c_name);

				Element visualAttribs = conTableXml
						.getChild("visualattributes");
				String sVisualAttribs = visualAttribs.getText().trim();

				Element metadataAttribs = conTableXml.getChild("metadataxml");
				Element valuedataAttribs = metadataAttribs
						.getChild("ValueMetadata");

				if ((metadataAttribs != null) && (valuedataAttribs != null)) {
					log.debug("Has value");
					hasModifierValue = true;
					Element dataTypeElement = valuedataAttribs
							.getChild("DataType");
					if (dataTypeElement != null
							&& dataTypeElement.getTextTrim().equalsIgnoreCase(
									"Enum")) {
						// add text values to node data
						modifierValuePropertyData.hasEnumValue(true);
						Element enumElement = valuedataAttribs
								.getChild("EnumValues");
						for (int i = 0; i < enumElement.getChildren().size(); i++) {
							Element valElement = (Element) enumElement
									.getChildren().get(i);
							String valString = new String(valElement
									.getTextTrim());
							modifierValuePropertyData.enumValues.add(valString);
						}
						log.debug("Got vals: "
								+ this.modifierValuePropertyData.enumValues.size());
					}

					if (valuedataAttribs.getChild("Oktousevalues") != null
							&& valuedataAttribs.getChild("Oktousevalues")
									.getText().equalsIgnoreCase("Y")) {
						modifierValuePropertyData.okToUseValue(true);
					}

					if (valuedataAttribs.getChild("Flagstouse") == null
							|| valuedataAttribs.getChild("Flagstouse")
									.getText().equalsIgnoreCase("")) {
						modifierValuePropertyData.okToUseValueFlag(false);
					}

					Element unitElement = valuedataAttribs
							.getChild("UnitValues");
					if (unitElement != null) {
						for (int i = 0; i < unitElement.getChildren().size(); i++) {
							Element element = (Element) unitElement
									.getChildren().get(i);
							if (element.getName().equalsIgnoreCase(
									"NormalUnits")) {
								String unitString = new String(element
										.getTextTrim());
								modifierValuePropertyData.units.add(unitString);
							}
						}
						log.debug("Got modifier vals: "
								+ modifierValuePropertyData.enumValues.size());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/	
	public void setModifierValueConstraint(List<ConstrainByModifier.ConstrainByValue> list) {
		if (list != null && list.size() > 0) {
			hasModifierValue = true;
			ConstrainByModifier.ConstrainByValue cons = list.get(0);
			if (cons.getValueConstraint() == null) {
				// hasValue(false);
				return;
			}
			if (cons.getValueType().equals(ConstrainValueType.NUMBER)) {
				this.modifierValuePropertyData().useNumericValue(true);
			} else if (cons.getValueType().equals(ConstrainValueType.TEXT)
					||cons.getValueType().equals(ConstrainValueType.LARGETEXT)) {
				this.modifierValuePropertyData().useTextValue(true);
			} else if (cons.getValueType().equals(ConstrainValueType.FLAG)) {
				this.modifierValuePropertyData().useValueFlag(true);
			} else if (cons.getValueType().equals(ConstrainValueType.MODIFIER)) {
				// this.modifierValuePropertyData().u(true);
			}
			
			updateModifierMetaDataXML();
			if(this.modifierValuePropertyData().hasStringValue()
					&& this.modifierValuePropertyData().useTextValue()) {
				this.modifierValuePropertyData().useStringValue(true);
				this.modifierValuePropertyData().useTextValue(false);
			}

			this.modifierValuePropertyData().noValue(false);
			// this.modifierValuePropertyData().useValueFlag(false);
			this.modifierValuePropertyData().value(cons.getValueConstraint());
			
			for(int i=0; i<this.modifierValuePropertyData().enumValues.size(); i++) {
				String eVal = this.modifierValuePropertyData().enumValues.get(i);
				String checkStr = "'"+eVal+"'";
				if(this.modifierValuePropertyData().value().indexOf(checkStr)>=0) {
					this.modifierValuePropertyData().selectedValues.add(new String(eVal));
				}
			}
			
			if (cons.getValueUnitOfMeasure() != null)
				this.modifierValuePropertyData().unit(cons.getValueUnitOfMeasure());
			if (cons.getValueOperator() != null) {
				this.modifierValuePropertyData().operator(
						cons.getValueOperator().value());

				// deal with between...
				if (this.modifierValuePropertyData().operator().equalsIgnoreCase(
						"between")) {
					String[] result = cons.getValueConstraint().split(" and ");
					if (result != null && result.length == 2) {
						this.modifierValuePropertyData().lowValue(result[0]);
						this.modifierValuePropertyData().highValue(result[1]);
					}
				}
			}
			this.modifierValuePropertyData().okToUseValue(true);
			this.modifier_display_name(getOperator(this.modifierValuePropertyData().operator())
			       + this.modifierValuePropertyData().value());

		} else {
			hasModifierValue = false;
		}
	}
	
	public void updateModifierMetaDataXML() {
		try {
			//boolean hasValue = false;

			SAXBuilder parser = new SAXBuilder();
			GetModifierInfoType vocab = new GetModifierInfoType();
			vocab.setHiddens(true);
			vocab.setSynonyms(false);
			vocab.setMax(200);
			vocab.setType("default");
			vocab.setBlob(true);
			vocab.setAppliedPath(applied_path);
			vocab.setSelf(modifier_key);
			String xmlContent = OntServiceDriver.getModifierInfo(vocab, "");
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);

			org.jdom.Element elementMsgBody = tableDoc.getRootElement()
					.getChild("message_body"); // , Namespace.getNamespace(
			// "http://www.i2b2.org/xsd/cell/ont/1.1/"
			// ));
			org.jdom.Element tableXml = elementMsgBody
					.getChild(
							"modifiers",
							Namespace
									.getNamespace("http://www.i2b2.org/xsd/cell/ont/1.1/"));

			List conceptChildren = tableXml.getChildren();
			for (Iterator itr = conceptChildren.iterator(); itr.hasNext();) {
				Element conceptXml = (org.jdom.Element) itr.next();
				String conceptText = conceptXml.getText().trim();

				Element conTableXml = conceptXml;// .getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("name");
				String c_name = nameXml.getText();
				////titleName(c_name);

				Element visualAttribs = conTableXml
						.getChild("visualattributes");
				String sVisualAttribs = visualAttribs.getText().trim();

				Element metadataAttribs = conTableXml.getChild("metadataxml");
				Element valuedataAttribs = metadataAttribs
						.getChild("ValueMetadata");

				if ((metadataAttribs != null) && (valuedataAttribs != null)) {
					log.debug("Has value");
					hasModifierValue = true;
					Element dataTypeElement = valuedataAttribs
							.getChild("DataType");
					if (dataTypeElement != null
							&& dataTypeElement.getTextTrim().equalsIgnoreCase(
									"Enum")) {
						// add text values to node data
						modifierValuePropertyData.hasEnumValue(true);
						Element enumElement = valuedataAttribs
								.getChild("EnumValues");
						for (int i = 0; i < enumElement.getChildren().size(); i++) {
							Element valElement = (Element) enumElement
									.getChildren().get(i);
							String valString = new String(valElement
									.getTextTrim());
							modifierValuePropertyData.enumValues.add(valString);
						}
						log.debug("Got vals: "
								+ this.modifierValuePropertyData.enumValues.size());
					} else if (dataTypeElement != null
							&& (dataTypeElement.getTextTrim().equalsIgnoreCase(
							"String")|| dataTypeElement.getTextTrim().equalsIgnoreCase(
							"LargeString"))) {
						// add text values to node data
						modifierValuePropertyData.hasStringValue(true);
						Element maxLengthElement = valuedataAttribs
						.getChild("MaxStringLength");
						String valString = new String(maxLengthElement.getTextTrim());
						if(!valString.equalsIgnoreCase("")) {
						modifierValuePropertyData.searchStrLength(Integer.parseInt(valString));
						}
						if(dataTypeElement.getTextTrim().equalsIgnoreCase(
							"LargeString")) {
							modifierValuePropertyData.isLongText(true);
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
						modifierValuePropertyData.okToUseValue(true);
					}

					if (valuedataAttribs.getChild("Flagstouse") == null
							|| valuedataAttribs.getChild("Flagstouse")
									.getText().equalsIgnoreCase("")) {
						modifierValuePropertyData.okToUseValueFlag(false);
					}

					Element unitElement = valuedataAttribs
							.getChild("UnitValues");
					if (unitElement != null) {
						for (int i = 0; i < unitElement.getChildren().size(); i++) {
							Element element = (Element) unitElement
									.getChildren().get(i);
							if (element.getName().equalsIgnoreCase(
									"NormalUnits")) {
								String unitString = new String(element
										.getTextTrim());
								modifierValuePropertyData.units.add(unitString);
							}
						}
						log.debug("Got modifier vals: "
								+ modifierValuePropertyData.enumValues.size());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return name() + " ["+this.modifier_name()+valueName()+"]";
	}
}