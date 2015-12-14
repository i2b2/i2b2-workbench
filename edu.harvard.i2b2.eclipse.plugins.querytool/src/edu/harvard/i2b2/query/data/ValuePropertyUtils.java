package edu.harvard.i2b2.query.data;

import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.ConstrainOperatorType;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class ValuePropertyUtils implements UIConst
{
	
	
	
	// opStr is one of UIConst.VALUE_OPERATORS[]
	public static String getNumericOperatorDisplayString(String opStr) 
	{
		String result = "";
		if (opStr == null) 
			return result;
		
		// HANDLE allowable values from the combobox in the NumericValueRestrictionEditor 
		if (opStr.equalsIgnoreCase( VALUE_OPERATORS[0] )) // 0 is Less than 
			result = LESS_THAN_SYM;
		else if (opStr.equalsIgnoreCase( VALUE_OPERATORS[1] )) // 1 is less than equal to 
			result = LESS_THAN_EQUALS_TO_SYM;
		else if (opStr.equalsIgnoreCase( VALUE_OPERATORS[2] )) // 2 is equal to (and 3 is between)
			result = EQUAL_SYM;									   
		else if (opStr.equalsIgnoreCase( VALUE_OPERATORS[4] )) // 4 is greater than equal to 
			result = GREATER_THAN_EQUALS_TO_SYM;
		else if (opStr.equalsIgnoreCase( VALUE_OPERATORS[5] )) // 5 is greater than 
			result = GREATER_THAN_SYM;
		
		// HANDLE allowable values from the ItemType's ConstrainoperatorType: {EQ, LT, LE, GT, GE}
		else if (opStr.equalsIgnoreCase( ConstrainOperatorType.LT.value() ))
			result = LESS_THAN_SYM;
		else if (opStr.equalsIgnoreCase( ConstrainOperatorType.LE.value() ))
			result = LESS_THAN_EQUALS_TO_SYM;
		else if (opStr.equalsIgnoreCase( ConstrainOperatorType.EQ.value() ))
			result = EQUAL_SYM;
		else if (opStr.equalsIgnoreCase( ConstrainOperatorType.GE.value() ))
			result = GREATER_THAN_EQUALS_TO_SYM;
		else if (opStr.equalsIgnoreCase( ConstrainOperatorType.GT.value() ))
			result = GREATER_THAN_SYM;
		
		return result;
	}
	
	
	// operator is the value operator in a ValuePropertyData for text blob value property
	public static String getTextualOperatorDisplayString(String operator) 
	{
		String result = operator;
		if (operator == null)
			return result;
		if (operator.equalsIgnoreCase( LIKE_CONTAINS ))
			result = LABEL_LIKE_CONTAINS;
		else if (operator.equalsIgnoreCase( LIKE_EXACT ))
			result = LABEL_LIKE_EXACT;
		else if (operator.equalsIgnoreCase( LIKE_BEGINS ))
			result = LABEL_LIKE_BEGINS;
		else if (operator.equalsIgnoreCase( LIKE_ENDS ))
			result = LABEL_LIKE_ENDS;
		else if (operator.equalsIgnoreCase( CONTAINS_DB ))
			result = LABEL_CONTAINS_DB;
		else if (operator.equalsIgnoreCase( CONTAINS ))
			result = LABEL_CONTAINS;
		return result;
	}

}
