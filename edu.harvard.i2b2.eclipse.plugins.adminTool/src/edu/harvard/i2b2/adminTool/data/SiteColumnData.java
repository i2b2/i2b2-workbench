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
package edu.harvard.i2b2.adminTool.data;

import java.util.ArrayList;
import java.util.List;

import edu.harvard.i2b2.common.datavo.pdo.ParamType;

/**
 * class: PatientMappingData
 * 
 * 
 */

public class SiteColumnData {
	private String columnName;

	public void columnName(String str) {
		columnName = new String(str);
	}

	public String columnName() {
		return columnName;
	}
	
	private String columnId;

	public void columnId(String str) {
		columnId = new String(str);
	}

	public String columnId() {
		return columnId;
	}
	
	private int columnNumber;

	public void columnNumber(int i) {
		columnNumber = i;
	}

	public int columnNumber() {
		return columnNumber;
	}

	//public ArrayList<String> siteIDs;
	/*private String gender;

	public void gender(String str) {
		gender = new String(str);
	}

	public String gender() {
		return gender;
	}

	private String vitalStatus;

	public void vitalStatus(String str) {
		vitalStatus = new String(str);
	}

	public String vitalStatus() {
		return vitalStatus;
	}

	private String race;

	public void race(String str) {
		race = new String(str);
	}

	public String race() {
		return race;
	}

	private String patientNumber;

	public void patientNumber(String str) {
		patientNumber = new String(str);
	}

	public String patientNumber() {
		return patientNumber;
	}

	private String lastName = "xxxxx";

	public String lastName() {
		return lastName;
	}

	public void lastName(String str) {
		lastName = new String(str);
	}

	private String firstName = "xxxxx";

	public String firstName() {
		return firstName;
	}

	public void firstName(String str) {
		firstName = new String(str);
	}*/

	public SiteColumnData() {
		columnName("");
		columnNumber(-1);
		//siteIDs = new ArrayList<String>();
	}

	public void setParamData(List<ParamType> list) {
		/*for (int i = 0; i < list.size(); i++) {
			ParamType param = list.get(i);
			if (param.getColumn().equalsIgnoreCase("lastName")) {
				lastName(param.getValue());
			} else if (param.getColumn().equalsIgnoreCase("firstName")) {
				firstName(param.getValue());
			} else if (param.getColumn().equalsIgnoreCase("age_in_years_num")) {
				age(param.getValue());
			} else if (param.getColumn().equalsIgnoreCase("race_cd")) {
				race(param.getValue());
			} else if (param.getColumn().equalsIgnoreCase("sex_cd")) {
				gender(param.getValue());
			} else if (param.getColumn().equalsIgnoreCase("vital_status_cd")) {
				vitalStatus(param.getValue());
			}
		}*/
	}
}
