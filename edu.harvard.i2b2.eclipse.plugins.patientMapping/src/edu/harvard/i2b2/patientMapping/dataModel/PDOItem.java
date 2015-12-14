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

public class PDOItem {
	private String panelName;

    public void panelName(String s) {
    	panelName = new String(s);
    }

    public String panelName() {
    	return panelName;
    }

	public String fullPath;

	public String dimcode;

	public String tableType; // fact, visit, provider or patient

	public boolean hasValueDisplayProperty = false;
	
	public boolean hasModifierValueDisplayProperty = false;

	public String height;

	public String color;

	public ArrayList<PDOValueModel> valDisplayProperties;
	
	public ArrayList<PDOValueModel> modifierValDisplayProperties;

	private QueryModel queryModel;

	public void queryModel(QueryModel q) {
		queryModel = q;
	}

	public QueryModel queryModel() {
		return queryModel;
	}

	public PDOItem() {
		valDisplayProperties = new ArrayList<PDOValueModel>();
		modifierValDisplayProperties = new ArrayList<PDOValueModel>();
	}

}
