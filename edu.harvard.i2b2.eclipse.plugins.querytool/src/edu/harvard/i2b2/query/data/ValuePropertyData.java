/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 *     Taowei David Wang
 */

package edu.harvard.i2b2.query.data;

import java.util.ArrayList;

import org.eclipse.swt.SWT;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainOperatorType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByModifier;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.EnumValueRestrictionEditorPanel;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.NumericValueRestrictionEditorPanel;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.TextBlobValueRestrictionEditorPanel;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Copyable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;


/*  This class should be refactored.
 * 		There are too many if statements here.
 *  
 *		Each of the different value restrictions should have its own class (Numeric, Text, String, Enum)
 *      and use this as a base class.
 */

public class ValuePropertyData implements Copyable<ValuePropertyData>
{
	public enum Type { ENUM, TEXTBLOB, NUMERIC };
	
	private String operator;
	private String valueFlag;
	private String value;
	private String lowValue;
	private String highValue;
	private boolean noValue 		= true;
	private boolean useNumericValue = false;
	private boolean useValueFlag 	= false;
	private boolean useTextValue 	= false;
	
	private boolean useStringValue 		= false;
	private boolean hasEnumValue 		= false;
	private boolean hasStringValue 		= false;
	private boolean okToUseValueFlag 	= true;
	private boolean okToUseValue 		= false;
	private int searchStrLength 		= -1;
	private boolean isLongText 			= false;
	private String unit 				= "";
	
	public ArrayList<String> enumValues;
	public ArrayList<String> selectedValues;
	public ArrayList<UnitsData> units;

	
	public String operator() 				{ return operator; }
	public void operator(String str) 		{ operator = new String(str); }

	public String valueFlag() 				{ return valueFlag; }
	public void valueFlag(String str) 		{ valueFlag = new String(str); }

	public String value() 					{ return value; }
	public void value(String str) 			{ value = new String(str); }

	public String lowValue() 				{ return lowValue; }
	public void lowValue(String str) 		{ lowValue = new String(str);}

	public String highValue() 				{ return highValue; }
	public void highValue(String str) 		{ highValue = new String(str); }

	public boolean noValue() 				{ return noValue; }
	public void noValue(boolean b) 			{ noValue = b;}

	public boolean useNumericValue() 		{ return useNumericValue; }
	public void useNumericValue(boolean b)	{ useNumericValue = b; }

	public boolean useValueFlag() 			{ return useValueFlag;}
	public void useValueFlag(boolean b) 	{ useValueFlag = b; }

	public boolean useTextValue() 			{ return useTextValue; }
	public void useTextValue(boolean b) 	{ useTextValue = b; }
	
	public boolean useStringValue() 		{ return useStringValue; }
	public void useStringValue(boolean b) 	{ useStringValue = b; }

	public boolean hasEnumValue() 			{ return hasEnumValue; }
	public void hasEnumValue(boolean b) 	{ hasEnumValue = b; }
	
	public boolean hasStringValue() 		{ return hasStringValue; } 
	public void hasStringValue(boolean b) 	{ hasStringValue = b; }

	public boolean okToUseValueFlag() 		{ return okToUseValueFlag;}
	public void okToUseValueFlag(boolean b) { okToUseValueFlag = b;}

	public boolean okToUseValue() 			{ return okToUseValue;}
	public void okToUseValue(boolean b)		{ okToUseValue = b; }
	
	public int searchStrLength() 			{ return searchStrLength;}
	public void searchStrLength(int n) 		{ searchStrLength = n; }
	
	public boolean isLongText() 			{ return isLongText; }
	public void isLongText(boolean b) 		{ isLongText = b; }

	public String unit() 					{ return unit; }
	public void unit(String str) 			{ unit = new String(str);}

	public ValuePropertyData() 
	{
		enumValues = new ArrayList<String>();
		selectedValues = new ArrayList<String>();
		units = new ArrayList<UnitsData>();
	}

	public ConstrainByValue makeValueConstraint() 
	{
		ConstrainByValue valueConstrain = new ConstrainByValue();
		// set the fields according to the data
		if (useNumericValue) 
		{
			valueConstrain.setValueType(ConstrainValueType.NUMBER);
			if (operator.equalsIgnoreCase( UIConst.BETWEEN )) 
			{
				valueConstrain.setValueConstraint(lowValue + " and " + highValue);
				valueConstrain.setValueOperator(ConstrainOperatorType.BETWEEN);
			} 
			else 
			{
				valueConstrain.setValueConstraint(value);
				valueConstrain.setValueOperator(getOperator());
			}
		} 
		else if (useValueFlag) 
		{
			valueConstrain.setValueType(ConstrainValueType.FLAG);
			valueConstrain.setValueConstraint(value);
			valueConstrain.setValueOperator(ConstrainOperatorType.EQ);
		} 
		else if (useTextValue) 
		{
			valueConstrain.setValueType(ConstrainValueType.TEXT);
			valueConstrain.setValueConstraint(getSelectedTexts());
			valueConstrain.setValueOperator(ConstrainOperatorType.IN);
		} 
		else if (useStringValue) 
		{
			if(isLongText) 
			{
				valueConstrain.setValueType(ConstrainValueType.LARGETEXT);
				valueConstrain.setValueConstraint(this.value());
				valueConstrain.setValueOperator(getOperator());
			} 
			else 
			{
				valueConstrain.setValueType(ConstrainValueType.TEXT);
				valueConstrain.setValueConstraint(this.value());
				valueConstrain.setValueOperator(getOperator());
			}
		}
		valueConstrain.setValueUnitOfMeasure(unit);
		return valueConstrain;
	}
	
	public ConstrainByModifier.ConstrainByValue makeModifierValueConstraint() 
	{
		ConstrainByModifier.ConstrainByValue valueConstrain = new ConstrainByModifier.ConstrainByValue();
		// set the fields according to the data
		if (useNumericValue) 
		{
			valueConstrain.setValueType(ConstrainValueType.NUMBER);
			if (operator.equalsIgnoreCase( UIConst.BETWEEN )) 
			{
				valueConstrain.setValueConstraint(lowValue + " and " + highValue);
				valueConstrain.setValueOperator(ConstrainOperatorType.BETWEEN);
			}
			else 
			{
				valueConstrain.setValueConstraint(value);
				valueConstrain.setValueOperator(getOperator());
			}
		} 
		else if (useValueFlag) 
		{
			valueConstrain.setValueType(ConstrainValueType.FLAG);
			valueConstrain.setValueConstraint(value);
			valueConstrain.setValueOperator(ConstrainOperatorType.EQ);
		} 
		else if (useTextValue) 
		{
			valueConstrain.setValueType(ConstrainValueType.TEXT);
			valueConstrain.setValueConstraint(getSelectedTexts());
			valueConstrain.setValueOperator(ConstrainOperatorType.IN);
		} 
		else if (useStringValue) 
		{
			if(isLongText) 
			{
				valueConstrain.setValueType(ConstrainValueType.LARGETEXT);
				valueConstrain.setValueConstraint(this.value());
				valueConstrain.setValueOperator(getOperator());
			} 
			else 
			{
				valueConstrain.setValueType(ConstrainValueType.TEXT);
				valueConstrain.setValueConstraint(this.value());
				valueConstrain.setValueOperator(getOperator());
			}
		}
		valueConstrain.setValueUnitOfMeasure(unit);
		return valueConstrain;
	}

	private String getSelectedTexts() 
	{
		String result = null;
		if (selectedValues.size() <= 0) 
			return result;
		result = "(";
		if (selectedValues.size() > 1) 
			for (int i = 0; i < selectedValues.size() - 1; i++) 
				result += "'" + selectedValues.get(i) + "',";
		return result + "'" + selectedValues.get(selectedValues.size() - 1) + "')";
	}

	private ConstrainOperatorType getOperator() 
	{
		ConstrainOperatorType result = null;
		if (operator == null) 
			return result;
		if (operator.equalsIgnoreCase("LESS THAN (<)") || operator.equalsIgnoreCase( ConstrainOperatorType.LT.value() )) 
			result = ConstrainOperatorType.LT;
		else if (operator.equalsIgnoreCase("LESS THAN OR EQUAL TO (" + UIConst.LESS_THAN_EQUALS_TO_SYM + ")")) 
			result = ConstrainOperatorType.LE;
		else if (operator.equalsIgnoreCase("EQUAL TO (=)") || operator.equalsIgnoreCase( ConstrainOperatorType.EQ.value() )) 
			result = ConstrainOperatorType.EQ;
		else if (operator.equalsIgnoreCase("GREATER THAN (>)") || operator.equalsIgnoreCase( ConstrainOperatorType.GT.value() )) 
			result = ConstrainOperatorType.GT;
		else if (operator.equalsIgnoreCase("GREATER THAN OR EQUAL TO (" + UIConst.GREATER_THAN_EQUALS_TO_SYM + ")"))
			result = ConstrainOperatorType.GE;
		else if (operator.equalsIgnoreCase("LIKE[contains]")) 
			result = ConstrainOperatorType.LIKE_CONTAINS;
		else if (operator.equalsIgnoreCase("LIKE[begin]"))
			result = ConstrainOperatorType.LIKE_BEGIN;
		else if (operator.equalsIgnoreCase("LIKE[end]")) 
			result = ConstrainOperatorType.LIKE_END;
		else if (operator.equalsIgnoreCase("LIKE[exact]"))			
			result = ConstrainOperatorType.LIKE_EXACT;
		else if (operator.equalsIgnoreCase("CONTAINS[database]"))
			result = ConstrainOperatorType.CONTAINS_DATABASE;
		else if (operator.equalsIgnoreCase("Contains"))
			result = ConstrainOperatorType.CONTAINS;
		return result;
	}


	public String toString()
	{
		if ( this.noValue() ) // this ValueProperty has no value, return empty String
			return "";
				
		if (this.getValuePropertyType() == Type.NUMERIC) 
		{
			if ( this.useValueFlag ) // Using Flag Values
			{
				if ( this.value.equalsIgnoreCase( DataConst.LOW_FLAG_VALUE ) )
					return DataConst.LOW_FLAG_NAME;
				else if ( this.value.equalsIgnoreCase( DataConst.HIGH_FLAG_VALUE ))
					return DataConst.HIGH_FLAG_NAME;
				else
					assert false: "ValuePropertyData.toString(): a numerical ValuePropertyData uses Flag Value, but Flag Value '" + this.value + "' is not recognized.";
			}
			else if ( this.useNumericValue )	// Using Numeric Values
			{
				if (operator.equalsIgnoreCase( UIConst.BETWEEN )) 
					return  "= [" + lowValue() + " - " + highValue() + "]";
				else 
					return ValuePropertyUtils.getNumericOperatorDisplayString(this.operator()) + " " + this.value() + " " + this.unit;
			}
			else 
				assert false: "ValuePropertyData.toString(): ValuePropertyData is not using one of {Numeric Value, Flag Value, or No Value}.";
		}
		else if ( this.getValuePropertyType() == Type.ENUM )
		{
			if ( this.useValueFlag ) // is using Abnormal Flag
				return DataConst.ABNORMAL_FLAG_NAME;
			else if ( this.useTextValue )
			{
				if ( this.selectedValues.size() == 0 ) // no text values, return empty String
					return "";
				else
				{
					if ( selectedValues.size() == 1 )								// only one enumerated value
						return "= " + selectedValues.get(0);					
					StringBuffer displayBuffer = new StringBuffer("= one of: {"); 	// has more than one enumerated values
					for ( int i = 0; i < selectedValues.size(); i++ )
					{
						displayBuffer.append( selectedValues.get(i) );
						if ( i < selectedValues.size()-1)
							displayBuffer.append(",");
					}
					displayBuffer.append("}");
					return displayBuffer.toString();
				}
			}
			else 
				assert false: "ValuePropertyData.toString(): an enumerated ValuePropertyData is not using one of {Enumerated Text Value, Flag Value, or No Value}.";
		}
		else if ( this.getValuePropertyType() == Type.TEXTBLOB )
		{
			if ( this.useValueFlag ) // is using Abnormal Flag
				return DataConst.ABNORMAL_FLAG_NAME;
			else if ( this.useStringValue )
				return " [" + ValuePropertyUtils.getTextualOperatorDisplayString( this.operator())+ " " + "\""+this.value()+"\""+"]";
			else 
				assert false: "ValuePropertyData.toString(): a text blobValuePropertyData is not using one of {String Value, Flag Value, or No Value}.";
		}		
		return null;
	}


	public ValuePropertyData.Type getValuePropertyType()
	{
		if ( this.hasEnumValue() ) 			// enum value
			return ValuePropertyData.Type.ENUM;
		else if ( this.hasStringValue() ) 	// text blob value
			return ValuePropertyData.Type.TEXTBLOB; 
		else 								// must have numeric Value
			return ValuePropertyData.Type.NUMERIC;
	}
	
	
	@Override
	public ValuePropertyData makeCopy() 
	{
		ValuePropertyData vpd = new ValuePropertyData();
		vpd.operator 	= this.operator;
		vpd.valueFlag	= this.valueFlag;
		vpd.value		= this.value;
		vpd.lowValue	= this.lowValue;
		vpd.highValue	= this.highValue;
		
		vpd.noValue			= this.noValue;
		vpd.useNumericValue = this.useNumericValue;
		vpd.useValueFlag	= this.useValueFlag;
		vpd.useTextValue	= this.useTextValue;
		
		vpd.useStringValue	= this.useStringValue;
		vpd.hasEnumValue	= this.hasEnumValue;
		vpd.hasStringValue	= this.hasStringValue;

		vpd.okToUseValueFlag= this.okToUseValueFlag;
		vpd.okToUseValue	= this.okToUseValue;

		vpd.searchStrLength	= this.searchStrLength;
		vpd.isLongText		= this.isLongText;
		
		vpd.unit			= this.unit;

		for ( String enumValue : this.enumValues )
			vpd.enumValues.add( enumValue );
		for ( String selectedValue : selectedValues )
			vpd.selectedValues.add( selectedValue );		
		for ( UnitsData unit : units )
			vpd.units.add( unit.makeCopy() );

		return vpd;
	}
	
	 
	
}
