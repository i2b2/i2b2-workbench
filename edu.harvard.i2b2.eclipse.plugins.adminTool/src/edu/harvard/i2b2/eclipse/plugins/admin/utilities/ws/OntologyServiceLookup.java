/*
 * Copyright (c) 2006-2007 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.ws;

public class OntologyServiceLookup {

	// Ontology Cell Stub
	
	/**
	 * The constructor
	 */
	public OntologyServiceLookup() {
	}
	
	/**
	 * Maps concept code to readable name
	 * 
	 * @param String concept code
	 * @return String mapped name
	 */
	public String getConceptName(String code){
		String name = null;
		if (code.equals("LCS-I2B2:pulheight"))
			name = "Height";
		else if (code.equals("LCS-I2B2:pulweight"))
			name = "Weight";
		else if (code.equals("LCS-I2B2:pulfev1obs"))
			name = "FEV1 Observed";		
		else if (code.equals("LCS-I2B2:pulfev1obspost"))
			name = "FEV1 Observed Post BD";	
		else if (code.equals("LCS-I2B2:pulfev1pred"))
				name = "FEV1 % of Predicted";
		else if (code.equals("LCS-I2B2:pulfvcobs"))
			name = "FVC Observed";		
		else if (code.equals("LCS-I2B2:pulfvcobspost"))
			name = "FVC Observed Post BD";
		else if (code.equals("LCS-I2B2:pulfvcpred"))
				name = "FVC % of Predicted";
		else if (code.equals("LCS-I2B2:pulfev1prcpredpost"))
			name = "FEV1 % of Predicted Post BD";	
		else if (code.equals("LCS-I2B2:pulfev1prcchangepost"))
				name = "FEV1 % Change in Post BD from Pre BD";
		else if (code.equals("LCS-I2B2:pulfvcprcpredpost"))
			name = "FVC % of Predicted Post BD";	
		else if (code.equals("LCS-I2B2:pulfvcprcchangepost"))
				name = "FVC % Change in Post BD from Pre BD";
		else
			name = "Unknown";
		return name;
	}
	
}