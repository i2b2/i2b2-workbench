/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */

package edu.harvard.i2b2.previousquery.data;

import java.util.List;

import edu.harvard.i2b2.common.datavo.pdo.ParamType;

public class PatientData extends QueryData {
	private String patientID;

	public void patientID(String str) {
		patientID = new String(str);
	}

	public String patientID() {
		return patientID;
	}

	private String patientSetID;

	public void patientSetID(String str) {
		patientSetID = new String(str);
	}

	public String patientSetID() {
		return patientSetID;
	}

	private String lastName;

	public void lastName(String str) {
		lastName = new String(str);
	}

	public String lastName() {
		return lastName;
	}

	private String firstName;

	public void firstName(String str) {
		firstName = new String(str);
	}

	public String firstName() {
		return firstName;
	}

	private String gender;

	public void gender(String str) {
		gender = new String(str);
	}

	public String gender() {
		return gender;
	}

	private String race;

	public void race(String str) {
		race = new String(str);
	}

	public String race() {
		return race;
	}

	private String age;

	public void age(String str) {
		age = new String(str);
	}

	public String age() {
		return age;
	}

	public PatientData() {
	}

	public void setParamData(List<ParamType> list) {
		for (int i = 0; i < list.size(); i++) {
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
			}
		}
	}

	@Override
	public String writeContentQueryXML() {

		return null;
	}

}
