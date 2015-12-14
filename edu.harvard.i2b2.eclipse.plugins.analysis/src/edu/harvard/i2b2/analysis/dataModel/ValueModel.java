/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

package edu.harvard.i2b2.analysis.dataModel;

import java.util.ArrayList;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainOperatorType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;

public class ValueModel {

	private String operator="";

	public String operator() {
		return operator;
	}

	public void operator(String str) {
		operator = new String(str);
	}

	private String valueFlag;

	public String valueFlag() {
		return valueFlag;
	}

	public void valueFlag(String str) {
		valueFlag = new String(str);
	}

	private String value;

	public String value() {
		return value;
	}

	public void value(String str) {
	    value = new String(str);
	    
	    if(operator().equalsIgnoreCase("between")&&value.indexOf("and")>0) {
		String[] tmp = str.split(" ");
		lowValue(tmp[0]);
		highValue(tmp[2]);
	    }
	}

	private String lowValue;

	public String lowValue() {
		return lowValue;
	}

	public void lowValue(String str) {
		lowValue = new String(str);
	}

	private String highValue;

	public String highValue() {
		return highValue;
	}

	public void highValue(String str) {
		highValue = new String(str);
	}

	private boolean noValue = true;

	public boolean noValue() {
		return noValue;
	}

	public void noValue(boolean b) {
		noValue = b;
	}

	private boolean useNumericValue = false;

	public boolean useNumericValue() {
		return useNumericValue;
	}

	public void useNumericValue(boolean b) {
		useNumericValue = b;
	}

	private boolean useValueFlag = false;

	public boolean useValueFlag() {
		return useValueFlag;
	}

	public void useValueFlag(boolean b) {
		useValueFlag = b;
	}

	private boolean useTextValue = false;

	public boolean useTextValue() {
		return useTextValue;
	}

	public void useTextValue(boolean b) {
		useTextValue = b;
	}

	private boolean hasEnumValue = false;

	public boolean hasEnumValue() {
		return hasEnumValue;
	}

	public void hasEnumValue(boolean b) {
		hasEnumValue = b;
	}

	private boolean okToUseValueFlag = true;

	public boolean okToUseValueFlag() {
		return okToUseValueFlag;
	}

	public void okToUseValueFlag(boolean b) {
		okToUseValueFlag = b;
	}

	private boolean okToUseValue = false;

	public boolean okToUseValue() {
		return okToUseValue;
	}

	public void okToUseValue(boolean b) {
		okToUseValue = b;
	}

	public ArrayList<String> enumValues;
	public ArrayList<String> selectedValues;

	public ArrayList<String> units;

	private String unit;

	public String unit() {
		return unit;
	}

	public void unit(String str) {
		unit = new String(str);
	}

	public ValueModel() {
		enumValues = new ArrayList<String>();
		selectedValues = new ArrayList<String>();
		units = new ArrayList<String>();
	}

	public ConstrainByValue writeValueConstrain() {
		ConstrainByValue valueConstrain = new ConstrainByValue();

		// set the fields according to the data
		if (useNumericValue) {
			valueConstrain.setValueType(ConstrainValueType.NUMBER);

			if (operator.equalsIgnoreCase("BETWEEN")) {
				valueConstrain.setValueConstraint(lowValue + " and "
						+ highValue);
				valueConstrain.setValueOperator(ConstrainOperatorType.BETWEEN);
			} else {
				valueConstrain.setValueConstraint(value);
				valueConstrain.setValueOperator(getOperator());
			}
		} else if (useValueFlag) {
			valueConstrain.setValueType(ConstrainValueType.FLAG);
			valueConstrain.setValueConstraint(value);
			valueConstrain.setValueOperator(ConstrainOperatorType.EQ);
		} else if (useTextValue) {
			valueConstrain.setValueType(ConstrainValueType.TEXT);
			valueConstrain.setValueConstraint(getSelectedTexts());
			valueConstrain.setValueOperator(ConstrainOperatorType.IN);
		}

		valueConstrain.setValueUnitOfMeasure(unit);

		return valueConstrain;
	}

	public String getSelectedTexts() {
		String result = null;

		if (selectedValues.size() <= 0) {
			return result;
		}

		result = "(";
		if (selectedValues.size() > 1) {
			for (int i = 0; i < selectedValues.size() - 1; i++) {
				result += "'" + selectedValues.get(i) + "',";
			}
		}

		return result + "'" + selectedValues.get(selectedValues.size() - 1)
				+ "')";
	}

	private ConstrainOperatorType getOperator() {
		ConstrainOperatorType result = null;
		if (operator == null) {
			return result;
		}

		if (operator.equalsIgnoreCase("LESS THAN (<)") 
			|| operator.equalsIgnoreCase("LT")) {
			result = ConstrainOperatorType.LT;
		} else if (operator.equalsIgnoreCase("LESS THAN OR EQUAL TO (<=)")) {
			result = ConstrainOperatorType.LE;
		} else if (operator.equalsIgnoreCase("EQUAL TO (=)")
			|| operator.equalsIgnoreCase("EQ")) {
			result = ConstrainOperatorType.EQ;
		} else if (operator.equalsIgnoreCase("GREATER THAN (>)") 
			|| operator.equalsIgnoreCase("GT")) {
			result = ConstrainOperatorType.GT;
		} else if (operator.equalsIgnoreCase("GREATER THAN OR EQUAL TO (>=)")) {
			result = ConstrainOperatorType.GE;
		}

		return result;
	}
}
