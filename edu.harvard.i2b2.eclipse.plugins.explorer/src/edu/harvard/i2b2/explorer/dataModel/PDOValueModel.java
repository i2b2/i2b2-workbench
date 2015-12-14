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

package edu.harvard.i2b2.explorer.dataModel;

import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.ConstrainOperatorType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.ConstrainValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.ItemType.ConstrainByModifier;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.ItemType.ConstrainByValue;

public class PDOValueModel {

	public double left;
	public double right;
	public String height;
	public String color;

	private String operator;

	public String operator() {
		return operator;
	}

	public void operator(String str) {
		operator = new String(str);
	}

	private String value;

	public String value() {
		return value;
	}

	public void value(String str) {
		value = new String(str);
	}

	private String valueFlag;

	public String valueFlag() {
		return valueFlag;
	}

	public void valueFlag(String str) {
		valueFlag = new String(str);
	}

	private boolean useValueFlag = false;

	public boolean useValueFlag() {
		return useValueFlag;
	}

	public void useValueFlag(boolean b) {
		useValueFlag = b;
	}

	private String unit;

	public String unit() {
		return unit;
	}

	public void unit(String str) {
		unit = new String(str);
	}

	private boolean useNumericValue = false;

	public boolean useNumericValue() {
		return useNumericValue;
	}

	public void useNumericValue(boolean b) {
		useNumericValue = b;
	}

	private boolean useTextValue = false;

	public boolean useTextValue() {
		return useTextValue;
	}

	public void useTextValue(boolean b) {
		useTextValue = b;
	}
	
	private boolean isLongText = false;

	public boolean isLongText() {
		return isLongText;
	}

	public void isLongText(boolean b) {
		isLongText = b;
	}

	public PDOValueModel() {

	}

	public boolean inRange(double val) {
		if (val >= left && val <= right) {
			return true;
		}

		return false;
	}

	public ConstrainByValue writeValueConstraint(PSMValueModel model) {
		ConstrainByValue valueConstraint = new ConstrainByValue();

		if (model.useNumericValue()) {
			valueConstraint.setValueType(ConstrainValueType.NUMBER);

			if (model.operator() != null
					&& model.operator().equalsIgnoreCase("BETWEEN")) {
				valueConstraint.setValueConstraint(model.lowValue() + " and "
						+ model.highValue());
				valueConstraint.setValueOperator(ConstrainOperatorType.BETWEEN);
			} else {
				valueConstraint.setValueConstraint(model.value());
				valueConstraint.setValueOperator(getOperator(model.operator()));
			}
		} else if (model.useTextValue()) {
			valueConstraint.setValueType(ConstrainValueType.TEXT);
			valueConstraint.setValueConstraint(model.getSelectedTexts());
			valueConstraint.setValueOperator(ConstrainOperatorType.IN);
		} else if (model.useValueFlag()) {
			valueConstraint.setValueType(ConstrainValueType.FLAG);
			valueConstraint.setValueConstraint(model.value());
			valueConstraint.setValueOperator(ConstrainOperatorType.EQ);
		} else if (model.useStringValue()) {
			if(model.isLongText()) {
				valueConstraint.setValueType(ConstrainValueType.LARGETEXT);
				valueConstraint.setValueConstraint(model.value());
				valueConstraint.setValueOperator(getOperator(model.operator()));
			}
			else {
				valueConstraint.setValueType(ConstrainValueType.TEXT);
				valueConstraint.setValueConstraint(model.value());
				valueConstraint.setValueOperator(getOperator(model.operator()));
			}
		}
		valueConstraint.setValueUnitOfMeasure(model.unit());

		return valueConstraint;
	}
	
	public ConstrainByModifier.ConstrainByValue writeModifierValueConstraint(PSMValueModel model) {
		ConstrainByModifier.ConstrainByValue valueConstraint = new ConstrainByModifier.ConstrainByValue();

		if (model.useNumericValue()) {
			valueConstraint.setValueType(ConstrainValueType.NUMBER);

			if (model.operator() != null
					&& model.operator().equalsIgnoreCase("BETWEEN")) {
				valueConstraint.setValueConstraint(model.lowValue() + " and "
						+ model.highValue());
				valueConstraint.setValueOperator(ConstrainOperatorType.BETWEEN);
			} else {
				valueConstraint.setValueConstraint(model.value());
				valueConstraint.setValueOperator(getOperator(model.operator()));
			}
		} else if (model.useTextValue()) {
			valueConstraint.setValueType(ConstrainValueType.TEXT);
			valueConstraint.setValueConstraint(model.getSelectedTexts());
			valueConstraint.setValueOperator(ConstrainOperatorType.IN);
		} else if (model.useValueFlag()) {
			valueConstraint.setValueType(ConstrainValueType.FLAG);
			valueConstraint.setValueConstraint(model.value());
			valueConstraint.setValueOperator(ConstrainOperatorType.EQ);
		} else if (model.useStringValue()) {
			if(model.isLongText()) {
				valueConstraint.setValueType(ConstrainValueType.LARGETEXT);
				valueConstraint.setValueConstraint(model.value());
				valueConstraint.setValueOperator(getOperator(model.operator()));
			}
			else {
				valueConstraint.setValueType(ConstrainValueType.TEXT);
				valueConstraint.setValueConstraint(model.value());
				valueConstraint.setValueOperator(getOperator(model.operator()));
			}
		}
		valueConstraint.setValueUnitOfMeasure(model.unit());

		return valueConstraint;
	}

	public ConstrainOperatorType getOperator(String op) {
		ConstrainOperatorType result = null;
		if (op == null) {
			return result;
		}

		if (op.equalsIgnoreCase("LESS THAN (<)") || op.equalsIgnoreCase("LT")) {
			result = ConstrainOperatorType.LT;
		} else if (op.equalsIgnoreCase("LESS THAN OR EQUAL TO (<=)")|| op.equalsIgnoreCase("LE")) {
			result = ConstrainOperatorType.LE;
		} else if (op.equalsIgnoreCase("EQUAL TO (=)")
				|| op.equalsIgnoreCase("EQ")) {
			result = ConstrainOperatorType.EQ;
		} else if (op.equalsIgnoreCase("GREATER THAN (>)")
				|| op.equalsIgnoreCase("GT")) {
			result = ConstrainOperatorType.GT;
		} else if (op.equalsIgnoreCase("GREATER THAN OR EQUAL TO (>=)")
				|| op.equalsIgnoreCase("GE")) {
			result = ConstrainOperatorType.GE;
		} else if (op.equalsIgnoreCase("LIKE[contains]")) {
			result = ConstrainOperatorType.LIKE_CONTAINS;
		} else if (op.equalsIgnoreCase("LIKE[begin]")) {
			result = ConstrainOperatorType.LIKE_BEGIN;
		} else if (op.equalsIgnoreCase("LIKE[end]")) {
			result = ConstrainOperatorType.LIKE_END;
		} else if (op.equalsIgnoreCase("LIKE[exact]")) {
			result = ConstrainOperatorType.LIKE_EXACT;
		} else if (op.equalsIgnoreCase("CONTAINS[database]")) {
			result = ConstrainOperatorType.CONTAINS_DATABASE;
		} else if (op.equalsIgnoreCase("Contains")) {
			result = ConstrainOperatorType.CONTAINS;
		} 

		return result;
	}

}
