/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.query.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessageUtil {
    //to make this class singleton
	
    private static  final int MAX_STACK_SIZE = 28;
	
    private static MessageUtil thisInstance;
    private String navRequest;
    private String navResponse;
    private String findRequest;
    private String findResponse;
    private String editRequest;
    private String editResponse;
    private String request;
	private String response;
    private List<StackData> xmlStack = new ArrayList<StackData>();
    
    static {
            thisInstance = new MessageUtil();
    }
    
    public static MessageUtil getInstance() {
        return thisInstance;
    }

    public String getEditRequest() {
		return editRequest;
	}

	public void setEditRequest(String request) {
		this.editRequest = request;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage(request);
		stackData.setName("Sent" + getTimestamp());
		xmlStack.add( stackData );
	}

	public String getEditResponse() {
		return editResponse;
	}

	public void setEditResponse(String response) {
		this.editResponse = response;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage(response);
		stackData.setName("Received" + getTimestamp());
		xmlStack.add( stackData );
	}
    
    
    
	public String getFindRequest() {
		return findRequest;
	}

	public void setFindRequest(String findRequest) {
		this.findRequest = findRequest;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage(findRequest);
		stackData.setName("Sent" + getTimestamp());
		xmlStack.add( stackData );
	}

	public String getFindResponse() {
		return findResponse;
	}

	public void setFindResponse(String findResponse) {
		this.findResponse = findResponse;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage(findResponse);
		stackData.setName("Received" + getTimestamp());
		xmlStack.add( stackData );
	}

	public String getNavRequest() {
		return navRequest;
	}

	public void setNavRequest(String navRequest) {
		this.navRequest = navRequest;
		checkXmlStackSize();
//if(xmlStack.size() == 11){
//	xmlStack.remove(0);
//    xmlStack = xmlStack.subList(1,10);
//}
		StackData stackData = new StackData();
		stackData.setMessage(navRequest);
		
		stackData.setName("Sent" + getTimestamp());
		xmlStack.add( stackData );
		
//		System.out.println(xmlStack.size() + " last " + xmlStack.get(xmlStack.size()-1).getName() );
	}

	public String getNavResponse() {
		return navResponse;
	}
	
	public List<StackData> getXmlStack(){
		return xmlStack;
	}

	public int getXmlStackSize(){
		return xmlStack.size();
	}
	
	public void setNavResponse(String navResponse) {
		this.navResponse = navResponse;
		checkXmlStackSize();
//if(xmlStack.size() == 11){
//	xmlStack.remove(0);
//	xmlStack = xmlStack.subList(1,10);
//}
		StackData stackData = new StackData();
		stackData.setMessage(navResponse);
		stackData.setName("Received" + getTimestamp());
		xmlStack.add( stackData );
		
//		System.out.println(xmlStack.size() + " last " + xmlStack.get(xmlStack.size()-1).getName() );
	}

	private String getTimestamp(){
		Calendar cldr = Calendar.getInstance(Locale.getDefault());
		
	//	Calendar cldr = Calendar.getInstance(TimeZone
	//			.getTimeZone("America/New_York"));
		String atTimestamp = "@"
				+ addZero(cldr.get(Calendar.HOUR_OF_DAY)) + ":"
				+ addZero(cldr.get(Calendar.MINUTE)) + ":"
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
	
	private void checkXmlStackSize(){
		if(xmlStack.size() == MAX_STACK_SIZE) {
			xmlStack.remove(0);
// following line not needed as remove() performs the left shift of the list			
//		    xmlStack = xmlStack.subList(1,MAX_STACK_SIZE-1);
		}
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
	
	
}
