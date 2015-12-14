/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 * 	    
 */
package edu.harvard.i2b2.eclipse.plugins.patientMapping.utils;

import java.util.Calendar;
import java.util.Locale;

public class StackData {
	private String name;
	private String message;

	public StackData() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimestamp() {
		Calendar cldr = Calendar.getInstance(Locale.getDefault());
		String atTimestamp = "@" + addZero(cldr.get(Calendar.HOUR_OF_DAY))
				+ ":" + addZero(cldr.get(Calendar.MINUTE)) + ":"
				+ addZero(cldr.get(Calendar.SECOND));

		return atTimestamp;
	}

	private String addZero(int number) {
		String result = new Integer(number).toString();
		if (number < 10 && number >= 0) {
			result = "0" + result;
		}
		return result;
	}

}
