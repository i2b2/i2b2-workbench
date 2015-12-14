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

import org.eclipse.swt.graphics.RGB;

public class ConceptTableRow {
	public int rowId;
	public int rowNumber;
	public String conceptName;
	public String dateText;
	public String valueType = "";
	public String valueText;
	public String modifierText;
	public String height;
	public RGB color;
	public String conceptXml;
	public QueryModel data;

	public void data(QueryModel data_) {
		if (!data_.hasValue()) {
			valueText = "Not Applicable";
		} else {
			if (data_.valueModel().noValue() && !data_.hasValueSet()) {
				valueText = "All values";
			}
		}
		
		if(data_.isModifier()) {
			ModifierData mdata = (ModifierData)data_;
			if (!mdata.hasModifierValue()) {
				modifierText = "Not Applicable";
			} else {
				if (mdata.modifierValuePropertyData().noValue()) {
					modifierText = "All values";
				}
			}
		}
		
		data = data_;
	}

	public QueryModel data() {
		return data;
	}

	public ConceptTableRow() {
	}
}
