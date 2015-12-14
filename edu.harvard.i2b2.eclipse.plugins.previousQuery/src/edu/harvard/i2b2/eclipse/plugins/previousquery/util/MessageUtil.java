/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 *     
 */
package edu.harvard.i2b2.eclipse.plugins.previousquery.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.harvard.i2b2.previousquery.data.StackData;

public class MessageUtil {
	// to make this class singleton

	private static final int MAX_STACK_SIZE = 28;

	private static MessageUtil thisInstance;
	private String request;
	private String response;
	private List<StackData> xmlStack = new ArrayList<StackData>();

	static {
		thisInstance = new MessageUtil();
	}

	public static MessageUtil getInstance() {
		return thisInstance;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage(request);
		stackData.setName("Sent" + getTimestamp());
		xmlStack.add(stackData);
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage(response);
		stackData.setName("Received" + getTimestamp());
		xmlStack.add(stackData);
	}

	public List<StackData> getXmlStack() {
		return xmlStack;
	}

	public int getXmlStackSize() {
		return xmlStack.size();
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

	private void checkXmlStackSize() {
		if (xmlStack.size() == MAX_STACK_SIZE) {
			xmlStack.remove(0);
			// following line not needed as remove() performs the left shift of
			// the list
			// xmlStack = xmlStack.subList(1,MAX_STACK_SIZE-1);
		}
	}

}
