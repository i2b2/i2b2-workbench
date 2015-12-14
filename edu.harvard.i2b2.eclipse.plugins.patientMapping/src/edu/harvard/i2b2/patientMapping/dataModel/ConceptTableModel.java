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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.graphics.RGB;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.patientMapping.ui.DateConstraintEditorText;
import edu.harvard.i2b2.patientMapping.ui.ModifierValueEditorText;
import edu.harvard.i2b2.patientMapping.ui.NumericValueEditorText;

public class ConceptTableModel implements KTableModel {

	private static final Log log = LogFactory.getLog(ConceptTableModel.class);

	private int[] colWidths;
	private int rowHeight;
	private int rowCount;
	private HashMap<String, Object> content;
	private static KTableCellRenderer colorRenderer = new KTableColorCellRenderer();
	private HashMap<RGB, String> colorMap = new HashMap<RGB, String>(150);

	/**
	   * 
	   */
	public ConceptTableModel() {
		colWidths = new int[getColumnCount()];
		colWidths[0] = 40;
		colWidths[1] = 278;
		colWidths[2] = 100;
		colWidths[3] = 120;
		colWidths[4] = 120;
		colWidths[5] = 66;
		colWidths[6] = 37;

		rowHeight = 18;
		rowCount = 1;
		content = new HashMap<String, Object>();

		content.put("0/0", "Row #");
		content.put("1/0", "Name of Terms");
		content.put("2/0", "Date Constraint");
		content.put("3/0", "Value Constraint");
		content.put("4/0", "Modifier Constraint");
		content.put("5/0", "Shape");
		content.put("6/0", "Color");

		populateColorMap();
	}

	public Object getContentAt(int col, int row) {

		if (col == 8 && row > 0) {
			Object obj = content.get(col + "/" + row);
			return obj;
		} else if (col == 6 && row > 0) {
			Object erg = content.get(col + "/" + row);
			if (erg != null)
				return (RGB) erg;
			else
				return new RGB(0, 255, 0);
		} else {
			String erg = (String) content.get(col + "/" + row);
			if (erg != null)
				return erg;
			else
				return "";
		}
	}

	/*
	 * overridden from superclass
	 */
	public KTableCellEditor getCellEditor(int col, int row) {
		if ((col == 6) && (row > 0)) {
			KTableCellEditorColor e = new KTableCellEditorColor();
			return e;
		} else if ((col == 2) && (row > 0)) {
			return new DateConstraintEditorText();
		} else if ((col == 3) && (row > 0)) {
			String str = (String) getContentAt(col, row);
			if (!str.equalsIgnoreCase("N/A")) {
				return new NumericValueEditorText();
			} else {
				return null;
			}
		} else if ((col == 4) && (row > 0)) {
			String str = (String) getContentAt(col, row);
			if (!str.equalsIgnoreCase("N/A")) {
				return new ModifierValueEditorText();
			} else {
				return null;
			}
		}else if ((col == 5) && (row > 0)) {
			KTableCellEditorCombo combo = new KTableCellEditorCombo();
			combo.setItems(new String[] { "Very Tall", "Tall", "Medium", "Low",
					"Very Low" });
			return combo;
		} else {
			return new KTableCellEditorText();
		}
	}

	/*
	 * overridden from superclass
	 */
	public void setContentAt(int col, int row, Object value) {
		if (row > rowCount)
			rowCount = row;
		else if (row == rowCount)
			rowCount = row + 1;
		content.put(col + "/" + row, value);
	}

	@SuppressWarnings("unchecked")
	public void populateTable(ArrayList list) {
		int newRow = 0;
		for (int i = 0; i < list.size(); i++) {
			ArrayList alist = (ArrayList) list.get(i);
			for (int j = 0; j < alist.size(); j++) {
				ConceptTableRow r = (ConceptTableRow) alist.get(j);
				newRow++;
				r.rowId = newRow;
				setContentAt(0, newRow, new Integer(r.rowNumber).toString());
				//if(r.data.isModifier()) {				
				//	setContentAt(1, newRow, r.conceptName+" ["+((ModifierData)r.data).modifier_name()+"]");
				//}
				//else {
					setContentAt(1, newRow, r.conceptName);
				//}
				setContentAt(2, newRow, r.dateText);
				setContentAt(3, newRow, r.valueText);
				setContentAt(4, newRow, r.modifierText);
				setContentAt(5, newRow, r.height);
				setContentAt(6, newRow, r.color);
				setContentAt(7, newRow, r.conceptXml);
				setContentAt(8, newRow, r.data);
			}
		}
	}

	public void deleteAllRows() {
		for (int i = 1; i < rowCount; i++) {
			content.remove("0/" + i);
			content.remove("1/" + i);
			content.remove("2/" + i);
			content.remove("3/" + i);
			content.remove("4/" + i);
			content.remove("5/" + i);
			content.remove("6/" + i);
			content.remove("7/" + i);
			content.remove("8/" + i);
		}
		rowCount = 1;
	}

	public void fillDataFromTable(ArrayList<ArrayList<ConceptTableRow>> list) {
		list.clear();
		ConceptTableRow row = null;
		ArrayList<ConceptTableRow> group = null;
		Integer curRow = null;
		LinkedHashMap<Integer, ArrayList<ConceptTableRow>> rowMap = new LinkedHashMap<Integer, ArrayList<ConceptTableRow>>();

		for (int i = 1; i < rowCount; i++) {
			row = new ConceptTableRow();
			curRow = new Integer((String) content.get("0/" + i));
			row.rowNumber = curRow.intValue();
			if (!rowMap.containsKey(curRow)) {
				group = new ArrayList<ConceptTableRow>();
				list.add(group);
				rowMap.put(curRow, group);
			} else {
				group = rowMap.get(curRow);
			}
			row.conceptName = (String) content.get("1/" + i);
			row.dateText = (String) content.get("2/" + i);
			row.valueText = (String) content.get("3/" + i);
			row.modifierText = (String) content.get("4/" + i);
			row.height = (String) content.get("5/" + i);
			row.color = (RGB) content.get("6/" + i);
			row.conceptXml = (String) content.get("7/" + i);
			row.data = (QueryModel) content.get("8/" + i);
			row.rowId = i;
			group.add(row);
		}
	}

	private String getFullname(String xmlcontent, SAXBuilder parser) {
		String fullname = null;
		try {
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlcontent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();

			Element conTableXml = (Element) tableXml;
			org.jdom.Element fullnameXml = conTableXml.getChild("key");
			fullname = fullnameXml.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fullname;
	}

	private String getDimcode(String xmlcontent, SAXBuilder parser) {
		String fullname = null;
		try {
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlcontent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();

			Element conTableXml = (Element) tableXml;
			org.jdom.Element fullnameXml = conTableXml.getChild("dimcode");
			fullname = fullnameXml.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fullname;
	}

	public String getColorString(RGB color) {
		return colorMap.get(color);
	}

	public RGB getColor(String color) {
		Iterator<RGB> it = colorMap.keySet().iterator();
		while (it.hasNext()) {
			RGB rgb = it.next();
			if (color.equals(getColorString(rgb))) {
				return rgb;
			}
		}
		return null;
	}

	public QueryModel parseXMLData(int row) {
		QueryModel data = new QueryModel();

		try {
			SAXBuilder parser = new SAXBuilder();
			String xmlContent = (String) getContentAt(4, row);
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			Element conceptXml = tableDoc.getRootElement();

			String conceptText = conceptXml.getText().trim();
			if (conceptText.equals("null")) // this is root level node
			{
				return data;
			}

			Element conTableXml = conceptXml;
			Element visualAttribs = conTableXml.getChild("visualattributes");
			String sVisualAttribs = visualAttribs.getText().trim();
			if (sVisualAttribs.toUpperCase().startsWith("C")) {
				return data;
			}

			Element metadataAttribs = conTableXml.getChild("metadataxml");
			Element valuedataAttribs = null;
			if (metadataAttribs != null) {
				valuedataAttribs = metadataAttribs.getChild("ValueMetadata");
			}

			@SuppressWarnings("unused")
			boolean hasValue = false;
			if ((metadataAttribs != null) && (valuedataAttribs != null)) {
				log.debug("Has value");
				hasValue = true;
				data.hasValue(true);
				Element dataTypeElement = valuedataAttribs.getChild("DataType");
				if (dataTypeElement != null
						&& dataTypeElement.getTextTrim().equalsIgnoreCase(
								"Enum")) {
					// add text values to node data
					data.valueModel().hasEnumValue(true);
					Element enumElement = valuedataAttribs
							.getChild("EnumValues");
					for (int i = 0; i < enumElement.getChildren().size(); i++) {
						Element valElement = (Element) enumElement
								.getChildren().get(i);
						String valString = new String(valElement.getTextTrim());
						data.valueModel().enumValues.add(valString);
					}
					log.debug("Got vals: "
							+ data.valueModel().enumValues.size());
				}

				if (valuedataAttribs.getChild("Oktousevalues") != null
						&& valuedataAttribs.getChild("Oktousevalues").getText()
								.equalsIgnoreCase("Y")) {
					data.valueModel().okToUseValue(true);
				}

				if (valuedataAttribs.getChild("Flagstouse") == null
						|| valuedataAttribs.getChild("Flagstouse").getText()
								.equalsIgnoreCase("")) {
					data.valueModel().okToUseValueFlag(false);
				}

				Element unitElement = valuedataAttribs.getChild("UnitValues");
				if (unitElement != null) {
					for (int i = 0; i < unitElement.getChildren().size(); i++) {
						Element element = (Element) unitElement.getChildren()
								.get(i);
						if (element.getName().equalsIgnoreCase("NormalUnits")) {
							String unitString = new String(element
									.getTextTrim());
							data.valueModel().units.add(unitString);
						}
					}
					log.debug("Got vals: "
							+ data.valueModel().enumValues.size());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public QueryModel parseXMLData(String xmlContent) {
		//QueryModel data = new QueryModel();
		QueryModel data = null;
		
		try {
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			Element conceptXml = tableDoc.getRootElement();
			
			org.jdom.Element nameXml = conceptXml.getChild("modifier");
			
			if(nameXml == null) {
				data = new QueryModel();
			}
			else {
				data = new ModifierData();
				data.isModifier(true);
				
				org.jdom.Element modifierXml = nameXml.getChild("applied_path");
				String applied_path = modifierXml.getText();
				((ModifierData)data).applied_path(applied_path);
				
				modifierXml = nameXml.getChild("key");
				String key = modifierXml.getText();
				((ModifierData)data).modifier_key(key);
				
				modifierXml = nameXml.getChild("name");
				String name = modifierXml.getText();
				((ModifierData)data).modifier_name(name);
				
				((ModifierData)data).updateModifierMetaDataXML();
			}

			String conceptText = conceptXml.getText().trim();
			if (conceptText.equals("null")) // this is root level node
			{
				return data;
			}

			Element conTableXml = conceptXml;
			Element visualAttribs = conTableXml.getChild("visualattributes");
			String sVisualAttribs = visualAttribs.getText().trim();
			if (sVisualAttribs.toUpperCase().startsWith("C")) {
				return data;
			}

			Element metadataAttribs = conTableXml.getChild("metadataxml");
			Element valuedataAttribs = null;
			if (metadataAttribs != null) {
				valuedataAttribs = metadataAttribs.getChild("ValueMetadata");
			}

			boolean hasValue = false;
			if ((metadataAttribs != null) && (valuedataAttribs != null)) {
				hasValue = true;
				data.hasValue(true);
				Element dataTypeElement = valuedataAttribs.getChild("DataType");
				if (dataTypeElement != null
						&& dataTypeElement.getTextTrim().equalsIgnoreCase(
								"Enum")) {
					// add text values to node data
					data.valueModel().hasEnumValue(true);
					Element enumElement = valuedataAttribs
							.getChild("EnumValues");
					for (int i = 0; i < enumElement.getChildren().size(); i++) {
						Element valElement = (Element) enumElement
								.getChildren().get(i);
						String valString = new String(valElement.getTextTrim());
						data.valueModel().enumValues.add(valString);
					}
					log.debug("Got vals: "
							+ data.valueModel().enumValues.size());
				}

				if (valuedataAttribs.getChild("Oktousevalues") != null
						&& valuedataAttribs.getChild("Oktousevalues").getText()
								.equalsIgnoreCase("Y")) {
					data.valueModel().okToUseValue(true);
				}

				if (valuedataAttribs.getChild("Flagstouse") == null
						|| valuedataAttribs.getChild("Flagstouse").getText()
								.equalsIgnoreCase("")) {
					data.valueModel().okToUseValueFlag(false);
				}

				Element unitElement = valuedataAttribs.getChild("UnitValues");
				if (unitElement != null) {
					for (int i = 0; i < unitElement.getChildren().size(); i++) {
						Element element = (Element) unitElement.getChildren()
								.get(i);
						if (element.getName().equalsIgnoreCase("NormalUnits")) {
							String unitString = new String(element
									.getTextTrim());
							data.valueModel().units.add(unitString);
						}
					}
					log.debug("Got vals: "
							+ data.valueModel().enumValues.size());
				}
			}

			nameXml = conTableXml.getChild("name");
			String c_name = nameXml.getText();
			//nameXml = conTableXml.getChild("dimcode");
			//String c_dimcode = nameXml.getText();
			//nameXml = conTableXml.getChild("operator");
			//String c_operator = nameXml.getText();
			//nameXml = conTableXml.getChild("columndatatype");
			//String c_columndatatype = nameXml.getText();
			//nameXml = conTableXml.getChild("columnname");
			//String c_columnname = nameXml.getText();
			//nameXml = conTableXml.getChild("tablename");
			//String c_table = nameXml.getText();
			nameXml = conTableXml.getChild("tooltip");
			String c_tooltip = nameXml.getText();
			nameXml = conTableXml.getChild("visualattributes");
			String c_visual = nameXml.getText();
			nameXml = conTableXml.getChild("level");
			String hlevel = nameXml.getText();
			nameXml = conTableXml.getChild("key");
			String rawfullname = nameXml.getText();
			rawfullname.indexOf("\\\\");
			String fullname = rawfullname;// .substring(rawfullname.
			// indexOf("\\", 2));

			//nameXml = conTableXml.getChild("facttablecolumn");
			//String c_facttablecolumn = nameXml.getText();

			//node.factTableColumn(c_facttablecolumn);

			//if(node.isModifier()) {							
			//	node.name(c_name+" ["+((ModifierData)node).modifier_name()+"]");
			//}
			//else {
				data.name(c_name);
			//}
				//data.titleName(c_name);
				data.visualAttribute(c_visual);
				data.tooltip(c_tooltip);
				data.hlevel(hlevel);
				data.fullname(fullname);
			//node.dimcode(c_dimcode);
			////node.hasValue(hasValue);
			// xml content is conceptText; not text
			// this accounts for drags of multiple concepts
				data.xmlContent(conceptText);
			//node.columnName(c_columnname);
			//node.tableName(c_table);
			//node.columnDataType(c_columndatatype);
			//node.operator(c_operator);
			// System.out.println("nodes xml content: "+node.
			// xmlContent());
				//data.originalXml(text);
				data.updateNodeMetaDataXML();
				//data.setXmlContent();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}
	
	public void parseXMLData(QueryModel data, String xmlContent) {
		//QueryModel data = new QueryModel();

		try {
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			Element conceptXml = tableDoc.getRootElement();
			
			org.jdom.Element nameXml = conceptXml.getChild("modifier");
			
			if(nameXml == null) {
				data = new QueryModel();
			}
			else {
				data = new ModifierData();
				data.isModifier(true);
				
				org.jdom.Element modifierXml = nameXml.getChild("applied_path");
				String applied_path = modifierXml.getText();
				((ModifierData)data).applied_path(applied_path);
				
				modifierXml = nameXml.getChild("key");
				String key = modifierXml.getText();
				((ModifierData)data).modifier_key(key);
				
				modifierXml = nameXml.getChild("name");
				String name = modifierXml.getText();
				((ModifierData)data).modifier_name(name);
				
				((ModifierData)data).updateModifierMetaDataXML();
			}

			String conceptText = conceptXml.getText().trim();
			if (conceptText.equals("null")) // this is root level node
			{
				return;// data;
			}

			Element conTableXml = conceptXml;
			Element visualAttribs = conTableXml.getChild("visualattributes");
			String sVisualAttribs = visualAttribs.getText().trim();
			if (sVisualAttribs.toUpperCase().startsWith("C")) {
				return;// data;
			}

			Element metadataAttribs = conTableXml.getChild("metadataxml");
			Element valuedataAttribs = null;
			if (metadataAttribs != null) {
				valuedataAttribs = metadataAttribs.getChild("ValueMetadata");
			}

			boolean hasValue = false;
			if ((metadataAttribs != null) && (valuedataAttribs != null)) {
				hasValue = true;
				data.hasValue(true);
				Element dataTypeElement = valuedataAttribs.getChild("DataType");
				if (dataTypeElement != null
						&& dataTypeElement.getTextTrim().equalsIgnoreCase(
								"Enum")) {
					// add text values to node data
					data.valueModel().hasEnumValue(true);
					Element enumElement = valuedataAttribs
							.getChild("EnumValues");
					for (int i = 0; i < enumElement.getChildren().size(); i++) {
						Element valElement = (Element) enumElement
								.getChildren().get(i);
						String valString = new String(valElement.getTextTrim());
						data.valueModel().enumValues.add(valString);
					}
					log.debug("Got vals: "
							+ data.valueModel().enumValues.size());
				}

				if (valuedataAttribs.getChild("Oktousevalues") != null
						&& valuedataAttribs.getChild("Oktousevalues").getText()
								.equalsIgnoreCase("Y")) {
					data.valueModel().okToUseValue(true);
				}

				if (valuedataAttribs.getChild("Flagstouse") == null
						|| valuedataAttribs.getChild("Flagstouse").getText()
								.equalsIgnoreCase("")) {
					data.valueModel().okToUseValueFlag(false);
				}

				Element unitElement = valuedataAttribs.getChild("UnitValues");
				if (unitElement != null) {
					for (int i = 0; i < unitElement.getChildren().size(); i++) {
						Element element = (Element) unitElement.getChildren()
								.get(i);
						if (element.getName().equalsIgnoreCase("NormalUnits")) {
							String unitString = new String(element
									.getTextTrim());
							data.valueModel().units.add(unitString);
						}
					}
					log.debug("Got vals: "
							+ data.valueModel().enumValues.size());
				}
			}

			nameXml = conTableXml.getChild("name");
			String c_name = nameXml.getText();
			//nameXml = conTableXml.getChild("dimcode");
			//String c_dimcode = nameXml.getText();
			//nameXml = conTableXml.getChild("operator");
			//String c_operator = nameXml.getText();
			//nameXml = conTableXml.getChild("columndatatype");
			//String c_columndatatype = nameXml.getText();
			//nameXml = conTableXml.getChild("columnname");
			//String c_columnname = nameXml.getText();
			//nameXml = conTableXml.getChild("tablename");
			//String c_table = nameXml.getText();
			nameXml = conTableXml.getChild("tooltip");
			String c_tooltip = nameXml.getText();
			nameXml = conTableXml.getChild("visualattributes");
			String c_visual = nameXml.getText();
			nameXml = conTableXml.getChild("level");
			String hlevel = nameXml.getText();
			nameXml = conTableXml.getChild("key");
			String rawfullname = nameXml.getText();
			rawfullname.indexOf("\\\\");
			String fullname = rawfullname;// .substring(rawfullname.
			// indexOf("\\", 2));

			//nameXml = conTableXml.getChild("facttablecolumn");
			//String c_facttablecolumn = nameXml.getText();

			//node.factTableColumn(c_facttablecolumn);

			//if(node.isModifier()) {							
			//	node.name(c_name+" ["+((ModifierData)node).modifier_name()+"]");
			//}
			//else {
				data.name(c_name);
			//}
				//data.titleName(c_name);
				data.visualAttribute(c_visual);
				data.tooltip(c_tooltip);
				data.hlevel(hlevel);
				data.fullname(fullname);
			//node.dimcode(c_dimcode);
			////node.hasValue(hasValue);
			// xml content is conceptText; not text
			// this accounts for drags of multiple concepts
				data.xmlContent(conceptText);
			//node.columnName(c_columnname);
			//node.tableName(c_table);
			//node.columnDataType(c_columndatatype);
			//node.operator(c_operator);
			// System.out.println("nodes xml content: "+node.
			// xmlContent());
				//data.originalXml(text);
				//data.updateNodeMetaDataXML();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return;// data;
	}

	public String getContentXml() {

		StringBuilder sb = new StringBuilder(100);
		sb.append("<I2B2Query>\r\n");

		for (int i = 1; i < rowCount; i++) {
			sb.append("<QueryEntry>\r\n");
			String conceptName = (String) content.get("1/" + i);
			if (conceptName.equals("Encounter Range Line")) {
				sb.append("<Concept>\r\n");
				sb.append("<SpecialConcept>");
				sb.append("<c_name>" + conceptName + "</c_name>\r\n");
				sb.append("</SpecialConcept>");
				sb.append("</Concept>\r\n");
			} else if (conceptName.equals("Vital Status Line")) {
				sb.append("<Concept>\r\n");
				sb.append("<SpecialConcept>");
				sb.append("<c_name>" + conceptName + "</c_name>\r\n");
				sb.append("</SpecialConcept>");
				sb.append("</Concept>\r\n");
			} else {
				Object xmlContent = content.get("6/" + i);
				sb.append((String) xmlContent);
			}
			String tmp = ((String) content.get("2/" + i))
					+ (content.get("3/" + i));
			String tmp1 = tmp.replaceAll("<", "&lt;");
			String ModuleValue = tmp1.replaceAll(">", "&gt;");
			sb.append("\r\n<DisplayName>");
			sb.append(conceptName);
			sb.append("</DisplayName>");

			sb.append("\r\n<ModuleValue>");
			if (ModuleValue.indexOf("N/A") >= 0) {
				sb.append("</ModuleValue>");
			} else {
				sb.append(ModuleValue);
				sb.append("\r\n</ModuleValue>");
			}

			sb.append("\r\n<Height>");
			sb.append(content.get("4/" + i));
			sb.append("\r\n</Height>");

			sb.append("\r\n<RowNumber>");
			sb.append(content.get("0/" + i));
			sb.append("\r\n</RowNumber>");

			sb.append("\r\n<ConceptColor>");
			RGB contentColor = (RGB) content.get("5/" + i);
			if (colorMap.containsKey(contentColor))
				sb.append(colorMap.get(contentColor));
			else
				sb.append("lightbrown");
			sb.append("\r\n</ConceptColor>");
			sb.append("\r\n</QueryEntry>");
		}

		sb.append("\r\n</I2B2Query>");
		log.debug("\n" + sb.toString());

		return sb.toString();
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getFixedRowCount() {
		return 1;
	}

	public int getColumnCount() {
		return 7;
	}

	public int getFixedColumnCount() {
		return 0;
	}

	public int getColumnWidth(int col) {
		return colWidths[col];
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public boolean isColumnResizable(int col) {
		return true;
	}

	public int getFirstRowHeight() {
		return 22;
	}

	public boolean isRowResizable() {
		return true;
	}

	public int getRowHeightMinimum() {
		return 18;
	}

	public void setColumnWidth(int col, int value) {
		colWidths[col] = value;
	}

	public void setRowHeight(int value) {
		if (value < 2)
			value = 2;
		rowHeight = value;
	}

	public KTableCellRenderer getCellRenderer(int col, int row) {
		if ((col == 6) && (row > 0))
			return colorRenderer;
		else
			return KTableCellRenderer.defaultRenderer;
	}

	public ArrayList<TimelineRow> getTimelineRows(
			ArrayList<ArrayList<ConceptTableRow>> rowData) {
		ArrayList<TimelineRow> rows = new ArrayList<TimelineRow>();

		@SuppressWarnings("unused")
		String curFullPath = null;
		SAXBuilder parser = new SAXBuilder();
		PDOItem pdoItem = null;
		PDOValueModel valdp;

		for (int i = 0; i < rowData.size(); i++) {
			ArrayList<ConceptTableRow> timelineRowData = rowData.get(i);
			TimelineRow timelineRow = new TimelineRow();
			rows.add(timelineRow);
			String curName = "";

			for (int j = 0; j < timelineRowData.size(); j++) {
				ConceptTableRow tableRow = timelineRowData.get(j);
				if (curName.equalsIgnoreCase("")) {
					curName = tableRow.conceptName;
					timelineRow.displayName += curName;
				} else if (!tableRow.conceptName.equalsIgnoreCase(curName)) {
					curName = tableRow.conceptName;
					timelineRow.displayName += tableRow.conceptName;
				}

				String tmpcurFullPath = getFullname(tableRow.conceptXml, parser);
				String lookuptable = getLookupTable(tableRow.conceptXml, parser);
				String dimcode = getDimcode(tableRow.conceptXml, parser);

				curFullPath = new String(tmpcurFullPath);

				pdoItem = new PDOItem();
				pdoItem.fullPath = new String(tmpcurFullPath);
				pdoItem.panelName(pdoItem.fullPath);
				
				String version = System.getProperty("serverVersion");
				double vernum = Double.parseDouble(version);
				if(vernum < 1.6) {
					pdoItem.tableType = new String(lookuptable);
					pdoItem.dimcode = new String(dimcode);
				}
				
				pdoItem.queryModel(tableRow.data());
				timelineRow.pdoItems.add(pdoItem);

				if (colorMap.containsKey(tableRow.color)) {
					pdoItem.color = colorMap.get(tableRow.color);
				} else {
					pdoItem.color = "lightbrown";
				}

				pdoItem.height = tableRow.height;
				
				if(tableRow.data().isModifier()){
					ModifierData mdata = (ModifierData)tableRow.data();
					pdoItem.panelName(pdoItem.fullPath + mdata.modifier_key());
					if (!mdata.modifierValuePropertyData().hasEnumValue()
							&& mdata.modifierValuePropertyData().okToUseValue()) {
						valdp = new PDOValueModel();
						valdp.height = tableRow.height;
						valdp.useNumericValue(true);
						pdoItem.hasModifierValueDisplayProperty = true;
	
						RGB contentColor = tableRow.color;
						if (colorMap.containsKey(contentColor)) {
							valdp.color = colorMap.get(contentColor);
						} else {
							valdp.color = "lightbrown";
						}
	
						
						//if (mdata.modifierValuePropertyData().raw) {
							if (tableRow.modifierText.indexOf("<=") >= 0) {
								String max = tableRow.modifierText
										.substring(tableRow.modifierText.indexOf("<") + 2);
								valdp.left = 0.0 - Double.MAX_VALUE;
								valdp.right = Double.parseDouble(max);
								valdp.value(max);
								valdp.operator("LESS THAN OR EQUAL TO (<=)");
							} 
							else if (tableRow.modifierText.indexOf(">=") >= 0) {
								String min = tableRow.modifierText
										.substring(tableRow.modifierText.indexOf(">") + 2);
								valdp.right = Integer.MAX_VALUE;
								valdp.left = Double.parseDouble(min);
								valdp.value(min);
								valdp.operator("GREATER THAN OR EQUAL TO (>=)");
							}
							else if (tableRow.modifierText.indexOf("<") >= 0) {
								String max = tableRow.modifierText
										.substring(tableRow.modifierText.indexOf("<") + 1);
								valdp.left = 0.0 - Double.MAX_VALUE;
								valdp.right = Double.parseDouble(max);
								valdp.value(max);
								valdp.operator("LESS THAN (<)");
							} else if (tableRow.modifierText.indexOf(">") >= 0) {
								String min = tableRow.modifierText
										.substring(tableRow.modifierText.indexOf(">") + 1);
								valdp.right = Integer.MAX_VALUE;
								valdp.left = Double.parseDouble(min);
								valdp.value(min);
								valdp.operator("GREATER THAN (>)");
							} else if (tableRow.modifierText.indexOf("between") >= 0) {
								String min = tableRow.modifierText.substring(
										tableRow.modifierText.indexOf("between") + 7,
										tableRow.modifierText.indexOf("and"));
								String max = tableRow.modifierText
										.substring(tableRow.modifierText
												.indexOf("and") + 3);
								valdp.right = Double.parseDouble(max);
								valdp.left = Double.parseDouble(min);
								valdp.operator("BETWEEN");
							}
						//}
						pdoItem.panelName(pdoItem.panelName()+ getValueName(valdp));
						pdoItem.modifierValDisplayProperties.add(valdp);
						pdoItem.fullPath += (j > 0 ? "" + j : "");
					} else if (mdata.modifierValuePropertyData().hasEnumValue()) {
						valdp = new PDOValueModel();
						valdp.height = tableRow.height;
						pdoItem.hasModifierValueDisplayProperty = true;
	
						RGB contentColor = tableRow.color;
						if (colorMap.containsKey(contentColor)) {
							valdp.color = colorMap.get(contentColor);
						} else {
							valdp.color = "lightbrown";
						}
	
						valdp.operator("EQUAL TO (=)");
						valdp.value(tableRow.modifierText);
						valdp.useTextValue(true);
						
						pdoItem.panelName(pdoItem.panelName() + getValueName(valdp));
						pdoItem.modifierValDisplayProperties.add(valdp);
						pdoItem.fullPath += (j > 0 ? "" + j : "");
					}
				}
				//////
				
				if (!tableRow.data().valueModel().hasEnumValue()
						&& tableRow.data().valueModel().okToUseValue()) {
					valdp = new PDOValueModel();
					valdp.height = tableRow.height;
					valdp.useNumericValue(true);
					pdoItem.hasValueDisplayProperty = true;

					RGB contentColor = tableRow.color;
					if (colorMap.containsKey(contentColor)) {
						valdp.color = colorMap.get(contentColor);
					} else {
						valdp.color = "lightbrown";
					}
					
					boolean raw = true;
					if (tableRow.data().valueModel().raw && raw) {
						if (tableRow.valueText.indexOf("<=") >= 0) {
							String max = tableRow.valueText
									.substring(tableRow.valueText.indexOf("<") + 2);
							valdp.left = 0.0 - Double.MAX_VALUE;
							valdp.right = Double.parseDouble(max);
							valdp.value(max);
							valdp.operator("LESS THAN OR EQUAL TO (<=)");
						} 
						else if (tableRow.valueText.indexOf(">=") >= 0) {
							String min = tableRow.valueText
									.substring(tableRow.valueText.indexOf(">") + 2);
							valdp.right = Integer.MAX_VALUE;
							valdp.left = Double.parseDouble(min);
							valdp.value(min);
							valdp.operator("GREATER THAN OR EQUAL TO (>=)");
						}
						else if (tableRow.valueText.indexOf("<") >= 0) {
							String max = tableRow.valueText
									.substring(tableRow.valueText.indexOf("<") + 1);
							valdp.left = 0.0 - Double.MAX_VALUE;
							valdp.right = Double.parseDouble(max);
							valdp.value(max);
							valdp.operator("LESS THAN (<)");
						} else if (tableRow.valueText.indexOf(">") >= 0) {
							String min = tableRow.valueText
									.substring(tableRow.valueText.indexOf(">") + 1);
							valdp.right = Integer.MAX_VALUE;
							valdp.left = Double.parseDouble(min);
							valdp.value(min);
							valdp.operator("GREATER THAN (>)");
						} else if (tableRow.valueText.indexOf("between") >= 0) {
							String min = tableRow.valueText.substring(
									tableRow.valueText.indexOf("between") + 7,
									tableRow.valueText.indexOf("and"));
							String max = tableRow.valueText
									.substring(tableRow.valueText
											.indexOf("and") + 3);
							valdp.right = Double.parseDouble(max);
							valdp.left = Double.parseDouble(min);
							valdp.operator("BETWEEN");
						}
					}
					pdoItem.panelName(pdoItem.panelName() + getValueName(valdp));
					pdoItem.valDisplayProperties.add(valdp);
					pdoItem.fullPath += (j > 0 ? "" + j : "");
				} else if (tableRow.data().valueModel().hasEnumValue()
						|| tableRow.data().valueModel().hasStringValue()) {
					valdp = new PDOValueModel();
					valdp.height = tableRow.height;
					pdoItem.hasValueDisplayProperty = true;

					RGB contentColor = tableRow.color;
					if (colorMap.containsKey(contentColor)) {
						valdp.color = colorMap.get(contentColor);
					} else {
						valdp.color = "lightbrown";
					}

					valdp.operator("EQUAL TO (=)");
					valdp.value(tableRow.valueText);
					valdp.useTextValue(true);
					pdoItem.panelName(pdoItem.panelName() + getValueName(valdp));
					pdoItem.valDisplayProperties.add(valdp);
					pdoItem.fullPath += (j > 0 ? "" + j : "");
				}
			}
		}

		return rows;
	}
	
	 private String getValueName(PDOValueModel model) {
			String name = "";
			// if (model.noValue()) {
			// return name;
			// } else
			if (model.useValueFlag()) {
			    if (model.valueFlag().equalsIgnoreCase("H")) {
				name = "=HIGH";
			    } else {
				name = "=LOW";
			    }
			} else if (model.useNumericValue()) {

			    // deal with between...
			    //if (model.operator().equalsIgnoreCase("between")) {
				//name = "between " + model.left + " and " + model.right;
			    //} else {
				//name = /* getOperator( */model.operator()/* ) */+ " "
					//+ model.value();
			   // }
			}
			return name;
	}


	private String getLookupTable(String xmlcontent, SAXBuilder parser) {
		String lookuptablename = null;
		try {
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlcontent);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();

			org.jdom.Element lookuptablenameXml = tableXml
					.getChild("tablename");
			if (lookuptablenameXml == null) {
				lookuptablename = "i2b2";
			} else {
				lookuptablename = lookuptablenameXml.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lookuptablename;
	}

	private void populateColorMap() {
		colorMap.put(new RGB(255, 255, 255), "white");

		colorMap.put(new RGB(255, 250, 250), "snow");

		colorMap.put(new RGB(248, 248, 255), "ghostwhite");

		colorMap.put(new RGB(255, 255, 240), "ivory");

		colorMap.put(new RGB(245, 255, 250), "mintcream");

		colorMap.put(new RGB(240, 255, 255), "azure");

		colorMap.put(new RGB(255, 250, 240), "floralwhite");

		colorMap.put(new RGB(240, 248, 255), "aliceblue");

		colorMap.put(new RGB(255, 240, 245), "lavenderblush");

		colorMap.put(new RGB(255, 245, 238), "seashell");

		colorMap.put(new RGB(245, 245, 245), "whitesmoke");

		colorMap.put(new RGB(240, 255, 240), "honeydew");

		colorMap.put(new RGB(255, 255, 224), "lightyellow");

		colorMap.put(new RGB(224, 255, 255), "lightcyan");

		colorMap.put(new RGB(253, 245, 230), "oldlace");

		colorMap.put(new RGB(255, 248, 220), "cornsilk");

		colorMap.put(new RGB(250, 240, 230), "linen");

		colorMap.put(new RGB(255, 250, 205), "lemonchiffon");

		colorMap.put(new RGB(250, 250, 210), "lightgoldenrodyellow");

		colorMap.put(new RGB(245, 245, 220), "beige");

		colorMap.put(new RGB(230, 230, 250), "lavender");

		colorMap.put(new RGB(255, 228, 225), "mistyrose");

		colorMap.put(new RGB(255, 239, 213), "papayawhip");

		colorMap.put(new RGB(255, 245, 200), "lightbrown");

		colorMap.put(new RGB(250, 235, 215), "antiquewhite");

		colorMap.put(new RGB(255, 235, 205), "blanchedalmond");

		colorMap.put(new RGB(255, 228, 196), "bisque");

		colorMap.put(new RGB(255, 236, 175), "darkbrown");

		colorMap.put(new RGB(255, 228, 181), "moccasin");

		colorMap.put(new RGB(220, 220, 220), "gainsboro");

		colorMap.put(new RGB(255, 218, 185), "peachpuff");

		colorMap.put(new RGB(175, 238, 238), "paleturquoise");

		colorMap.put(new RGB(255, 222, 173), "navajowhite");

		colorMap.put(new RGB(255, 192, 203), "pink");

		colorMap.put(new RGB(245, 222, 179), "wheat");

		colorMap.put(new RGB(238, 232, 170), "palegoldenrod");

		colorMap.put(new RGB(211, 211, 211), "lightgray");

		colorMap.put(new RGB(211, 211, 211), "lightgrey");

		colorMap.put(new RGB(255, 182, 193), "lightpink");

		colorMap.put(new RGB(176, 224, 230), "powderblue");

		colorMap.put(new RGB(216, 191, 216), "thistle");

		colorMap.put(new RGB(173, 216, 230), "lightblue");

		colorMap.put(new RGB(240, 230, 140), "khaki");

		colorMap.put(new RGB(238, 130, 238), "violet");

		colorMap.put(new RGB(221, 160, 221), "plum");

		colorMap.put(new RGB(176, 196, 222), "lightsteelblue");

		colorMap.put(new RGB(127, 255, 212), "aquamarine");

		colorMap.put(new RGB(135, 206, 250), "lightskyblue");

		colorMap.put(new RGB(238, 221, 130), "lightgoldenrod");

		colorMap.put(new RGB(135, 206, 235), "skyblue");

		colorMap.put(new RGB(190, 190, 190), "gray");

		colorMap.put(new RGB(152, 251, 152), "palegreen");

		colorMap.put(new RGB(218, 112, 214), "orchid");

		colorMap.put(new RGB(222, 184, 135), "burlywood");

		colorMap.put(new RGB(255, 105, 180), "hotpink");

		colorMap.put(new RGB(255, 105, 180), "severe");

		colorMap.put(new RGB(255, 160, 122), "lightsalmon");

		colorMap.put(new RGB(210, 180, 140), "tan");

		colorMap.put(new RGB(255, 255, 0), "yellow");

		colorMap.put(new RGB(255, 0, 255), "magenta");

		colorMap.put(new RGB(0, 255, 255), "cyan");

		colorMap.put(new RGB(233, 150, 122), "darksalmon");

		colorMap.put(new RGB(244, 164, 96), "sandybrown");

		colorMap.put(new RGB(132, 112, 255), "lightslateblue");

		colorMap.put(new RGB(240, 128, 128), "lightcoral");

		colorMap.put(new RGB(64, 224, 208), "turquoise");

		colorMap.put(new RGB(250, 128, 114), "salmon");

		colorMap.put(new RGB(100, 149, 237), "cornflowerblue");

		colorMap.put(new RGB(72, 209, 204), "mediumturquoise");

		colorMap.put(new RGB(186, 85, 211), "mediumorchid");

		colorMap.put(new RGB(189, 183, 107), "darkkhaki");

		colorMap.put(new RGB(219, 112, 147), "palevioletred");

		colorMap.put(new RGB(147, 112, 219), "mediumpurple");

		colorMap.put(new RGB(102, 205, 170), "mediumaquamarine");

		colorMap.put(new RGB(188, 143, 143), "rosybrown");

		colorMap.put(new RGB(143, 188, 143), "darkseagreen");

		colorMap.put(new RGB(255, 215, 0), "gold");

		colorMap.put(new RGB(123, 104, 238), "mediumslateblue");

		colorMap.put(new RGB(255, 127, 80), "coral");

		colorMap.put(new RGB(0, 191, 255), "deepskyblue");

		colorMap.put(new RGB(160, 32, 240), "purple");

		colorMap.put(new RGB(30, 144, 255), "dodgerblue");

		colorMap.put(new RGB(255, 99, 71), "tomato");

		colorMap.put(new RGB(255, 20, 147), "deeppink");

		colorMap.put(new RGB(255, 165, 0), "orange");

		colorMap.put(new RGB(218, 165, 32), "goldenrod");

		colorMap.put(new RGB(0, 206, 209), "darkturquoise");

		colorMap.put(new RGB(95, 158, 160), "cadetblue");

		colorMap.put(new RGB(154, 205, 50), "yellowgreen");

		colorMap.put(new RGB(119, 136, 153), "lightslategray");

		colorMap.put(new RGB(119, 136, 153), "lightslategrey");

		colorMap.put(new RGB(153, 50, 204), "darkorchid");

		colorMap.put(new RGB(138, 43, 226), "blueviolet");

		colorMap.put(new RGB(0, 250, 154), "mediumspringgreen");

		colorMap.put(new RGB(205, 133, 63), "peru");

		colorMap.put(new RGB(106, 90, 205), "slateblue");

		colorMap.put(new RGB(255, 140, 0), "darkorange");

		colorMap.put(new RGB(65, 105, 225), "royalblue");

		colorMap.put(new RGB(205, 92, 92), "indianred");

		colorMap.put(new RGB(208, 32, 144), "violetred");

		colorMap.put(new RGB(112, 128, 144), "slategray");

		colorMap.put(new RGB(112, 128, 144), "slategrey");

		colorMap.put(new RGB(127, 255, 0), "chartreuse");

		colorMap.put(new RGB(0, 255, 127), "springgreen");

		colorMap.put(new RGB(70, 130, 180), "steelblue");

		colorMap.put(new RGB(32, 178, 170), "lightseagreen");

		colorMap.put(new RGB(124, 252, 0), "lawngreen");

		colorMap.put(new RGB(148, 0, 211), "darkviolet");

		colorMap.put(new RGB(199, 21, 133), "mediumvioletred");

		colorMap.put(new RGB(60, 179, 113), "mediumseagreen");

		colorMap.put(new RGB(210, 105, 30), "chocolate");

		colorMap.put(new RGB(184, 134, 11), "darkgoldenrod");

		colorMap.put(new RGB(255, 69, 0), "orangered");

		colorMap.put(new RGB(176, 48, 96), "maroon");

		colorMap.put(new RGB(105, 105, 105), "dimgray");

		colorMap.put(new RGB(105, 105, 105), "dimgrey");

		colorMap.put(new RGB(50, 205, 50), "limegreen");

		colorMap.put(new RGB(160, 82, 45), "sienna");

		colorMap.put(new RGB(107, 142, 35), "olivedrab");

		colorMap.put(new RGB(72, 61, 139), "darkslateblue");

		colorMap.put(new RGB(46, 139, 87), "seagreen");

		colorMap.put(new RGB(255, 0, 0), "red");

		colorMap.put(new RGB(0, 255, 0), "green");

		colorMap.put(new RGB(0, 0, 255), "blue");

		colorMap.put(new RGB(165, 42, 42), "brown");

		colorMap.put(new RGB(178, 34, 34), "firebrick");

		colorMap.put(new RGB(85, 107, 47), "darkolivegreen");

		colorMap.put(new RGB(139, 69, 19), "saddlebrown");

		colorMap.put(new RGB(34, 139, 34), "forestgreen");

		colorMap.put(new RGB(47, 79, 79), "darkslategray");

		colorMap.put(new RGB(47, 79, 79), "darkslategrey");

		colorMap.put(new RGB(0, 0, 205), "mediumblue");

		colorMap.put(new RGB(25, 25, 112), "midnightblue");

		colorMap.put(new RGB(0, 0, 128), "navy");

		colorMap.put(new RGB(0, 0, 128), "navyblue");

		colorMap.put(new RGB(0, 100, 0), "darkgreen");

		colorMap.put(new RGB(0, 0, 0), "black");
	}

	public boolean isColorUsable(RGB rgbValue) {
		if (colorMap.containsKey(rgbValue)) {
			return true;
		} else {
			return false;
		}
	}
}

class KTableForModel implements KTableModel {

	private int[] colWidths;

	private int rowHeight;

	private HashMap<String, Object> content;

	public KTableForModel() {
		colWidths = new int[getColumnCount()];
		for (int i = 0; i < colWidths.length; i++) {
			colWidths[i] = 270;
		}
		rowHeight = 18;
		content = new HashMap<String, Object>();
	}

	public Object getContentAt(int col, int row) {
		String erg = (String) content.get(col + "/" + row);
		if (erg != null)
			return erg;
		return col + "/" + row;
	}

	/*
	 * overridden from superclass
	 */
	public KTableCellEditor getCellEditor(int col, int row) {
		if (col % 2 == 0) {
			KTableCellEditorCombo e = new KTableCellEditorCombo();
			e
					.setItems(new String[] { "First text", "Second text",
							"third text" });
			return e;
		} else
			return new KTableCellEditorText();
	}

	/*
	 * overridden from superclass
	 */
	public void setContentAt(int col, int row, Object value) {
		content.put(col + "/" + row, value);
		//
	}

	// Umfang

	public int getRowCount() {
		return 5;
	}

	public int getFixedRowCount() {
		return 2;
	}

	public int getColumnCount() {
		return 5;
	}

	public int getFixedColumnCount() {
		return 1;
	}

	// GroBen

	public int getColumnWidth(int col) {
		return colWidths[col];
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public boolean isColumnResizable(int col) {
		return true;
	}

	public int getFirstRowHeight() {
		return 22;
	}

	public boolean isRowResizable() {
		return true;
	}

	public int getRowHeightMinimum() {
		return 18;
	}

	public void setColumnWidth(int col, int value) {
		colWidths[col] = value;
	}

	public void setRowHeight(int value) {
		if (value < 2)
			value = 2;
		rowHeight = value;
	}

	// Rendering

	public KTableCellRenderer getCellRenderer(int col, int row) {
		return KTableCellRenderer.defaultRenderer;
	}
}
