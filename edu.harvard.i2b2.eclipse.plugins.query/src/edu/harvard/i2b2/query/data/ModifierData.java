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
import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.OntServiceDriver;

public class ModifierData extends QueryConceptTreeNodeData {
	private static final Log log = LogFactory.getLog(QueryConceptTreeNodeData.class);
	
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
	
	private String applied_path;
	
	public void applied_path(String str) {
		applied_path = new String(str);
	}

	public String applied_path() {
		return applied_path;
	}
	
	private ValuePropertyData modifierValuePropertyData;
	
	public ValuePropertyData modifierValuePropertyData() {
		return modifierValuePropertyData;
	}
	
	private boolean hasModifierValue = false;
	
	public ModifierData() {
		super();
		modifierValuePropertyData = new ValuePropertyData();
	}
	
	public boolean hasModifierValue() {
		return hasModifierValue;
	}
	
	private boolean hasModifierHelp = false;
	
	public boolean hasModifierHelp() {
		return hasModifierHelp;
	}
	
	private String modifier_helpName;
	public void modifier_helpName(String str) {
		modifier_helpName = new String(str);
	}

	public String modifier_helpName() {
		return modifier_helpName;
	}
	
	private String modifier_helpURL;
	public void modifier_helpURL(String str) {
		modifier_helpURL = new String(str);
		hasModifierHelp = true;
	}

	public String modifier_helpURL() {
		return modifier_helpURL;
	}
	
	public ConstrainByModifier writeModifierConstraint() {
		
		ConstrainByModifier modifierConstraint = new ConstrainByModifier();
		modifierConstraint.setAppliedPath(applied_path);
		modifierConstraint.setModifierKey(modifier_key);
		modifierConstraint.setModifierName(modifier_name);
		
		// handle value constraint
		if (!modifierValuePropertyData.noValue()) {
			ConstrainByModifier.ConstrainByValue valueConstrain = modifierValuePropertyData.writeModifierValueConstrain();
			modifierConstraint.getConstrainByValue().add(valueConstrain);
		}
		
		return modifierConstraint;
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
					Element helpElement = valuedataAttribs
							.getChild("Help");
					if(helpElement != null){
						Element buttonNameElement = helpElement.getChild("ButtonName");
						if(buttonNameElement != null){
							modifier_helpName(buttonNameElement.getTextTrim());
						}
						Element helpURLElement = helpElement.getChild("URL");
						if(helpURLElement != null){
							modifier_helpURL(helpURLElement.getTextTrim());
							hasModifierHelp = true;
						}
					}
					
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
							String valDisplayString = new String(valElement
									.getAttributeValue("description"));
							if(valDisplayString.equalsIgnoreCase("")) {
								valDisplayString = valString;
							}
							modifierValuePropertyData.enumValues.add(valString);
							modifierValuePropertyData.enumValueNames.add(valDisplayString);
						}
						log.debug("Got vals: "
								+ this.modifierValuePropertyData.enumValues.size());
					} else if (dataTypeElement != null
							&& (dataTypeElement.getTextTrim().equalsIgnoreCase(
									"String" )
									|| dataTypeElement.getTextTrim().equalsIgnoreCase(
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
									"NormalUnits")|| element.getName().equalsIgnoreCase(
									"EqualUnits")) {
								String unitString = new String(element
										.getTextTrim());
								modifierValuePropertyData.units.add(new UnitsData(unitString, 1, false));
							}
							else if (element.getName().equalsIgnoreCase("ConvertingUnits")) {
								Element cunitElement = element.getChild("Units");
								String unitString = new String(cunitElement
										.getTextTrim());
								Element mfElement = element.getChild("MultiplyingFactor");
								double mf = Double.parseDouble(mfElement.getTextTrim());
								modifierValuePropertyData.units.add(new UnitsData(unitString, mf, true));
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
	
	public void setModifierValueConstraint(List<ConstrainByModifier.ConstrainByValue> list) {
		if (list != null && list.size() > 0) {
			hasValue(true);
			ConstrainByModifier.ConstrainByValue cons = list.get(0);
			if (cons.getValueConstraint() == null) {
				// hasValue(false);
				return;
			}
			if (cons.getValueType().equals(ConstrainValueType.NUMBER)) {
				this.modifierValuePropertyData().useNumericValue(true);
			} else if (cons.getValueType().equals(ConstrainValueType.TEXT)
					|| cons.getValueType().equals(ConstrainValueType.LARGETEXT)) {
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
			for (int i = 0; i < modifierValuePropertyData.enumValues.size(); i++) {
				String eval = modifierValuePropertyData.enumValues.get(i);
				String teval = "'"+eval+"'";
				if (modifierValuePropertyData().value().indexOf(teval)>=0) {
					//String valStr = (String) jEnumValueTable.getValueAt(i, 1);
					//String valStr = data.modifierValuePropertyData().enumValues.get(i);
					modifierValuePropertyData().selectedValues.add(eval);
				}
			}
			
			if (this.modifierValuePropertyData().selectedValues.size() > 0) {
				this.valueName(" Is " + modifierValuePropertyData().selectedValues.get(0));
			}
			
			if (this.modifierValuePropertyData().selectedValues.size() > 1) {
				for (int j = 1; j < this.modifierValuePropertyData().selectedValues
						.size(); j++) {
					this.valueName(this.valueName() + ","
							+ this.modifierValuePropertyData().selectedValues.get(j));
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
			if (this.modifierValuePropertyData().selectedValues.size() <= 0) {
				this.valueName(" ["+getOperator(this.modifierValuePropertyData().operator())
				 + " "+"\""+this.modifierValuePropertyData().value()+"\"]");
			}

		} else {
			hasValue(false);
		}
	}
	
	@Override
	public String toString() {
		return name() + " ["+this.modifier_name()+valueName()+" "+this.modifierValuePropertyData().unit()+"]";
	}
}