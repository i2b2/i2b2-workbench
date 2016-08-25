/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

package edu.harvard.i2b2.analysis.data;

import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.ConstrainOperatorType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.ConstrainValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.ItemType.ConstrainByValue;

public class PDOValueData {

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

    public PDOValueData() {

    }

    public boolean inRange(double val) {
	if (val >= left && val <= right) {
	    return true;
	}

	return false;
    }

    public ConstrainByValue writeValueConstrain() {
	ConstrainByValue valueConstrain = new ConstrainByValue();

	// set the fields according to the data
	if (useNumericValue) {
	    valueConstrain.setValueType(ConstrainValueType.NUMBER);

	    if (operator.equalsIgnoreCase("BETWEEN")) {
		valueConstrain.setValueConstraint(left + " and " + right);
		valueConstrain.setValueOperator(ConstrainOperatorType.BETWEEN);
	    } else {
		valueConstrain.setValueConstraint(value);
		valueConstrain.setValueOperator(getOperator());
	    }
	} else if (useTextValue) {
	    valueConstrain.setValueType(ConstrainValueType.TEXT);
	    valueConstrain.setValueConstraint(value);
	    valueConstrain.setValueOperator(ConstrainOperatorType.IN);
	} else if (useValueFlag) {
	    valueConstrain.setValueType(ConstrainValueType.FLAG);
	    valueConstrain.setValueConstraint(value);
	    valueConstrain.setValueOperator(ConstrainOperatorType.EQ);
	}
	// valueConstrain.setValueUnitOfMeasure(unit);

	return valueConstrain;
    }

    private ConstrainOperatorType getOperator() {
	ConstrainOperatorType result = null;
	if (operator == null) {
	    return result;
	}

	if (operator.equalsIgnoreCase("LESS THAN (<)")) {
	    result = ConstrainOperatorType.LT;
	} else if (operator.equalsIgnoreCase("LESS THAN OR EQUAL TO (<=)")) {
	    result = ConstrainOperatorType.LE;
	} else if (operator.equalsIgnoreCase("EQUAL TO (=)")) {
	    result = ConstrainOperatorType.EQ;
	} else if (operator.equalsIgnoreCase("GREATER THAN (>)")) {
	    result = ConstrainOperatorType.GT;
	} else if (operator.equalsIgnoreCase("GREATER THAN OR EQUAL TO (>=)")) {
	    result = ConstrainOperatorType.GE;
	}

	return result;
    }
}
