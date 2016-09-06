/*
 * Copyright (c) 2006-2016 Partners HealthCare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors:
 *      Wensong Pan
 * 		Lori Phillips
 * 		Taowei David Wang
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.views;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class MessageUtil 
{
    // to make this class singleton
    private static final int 	MAX_STACK_SIZE 	= 28;
    public static final int		INDENTATION		= 3;
    
    private static MessageUtil thisInstance;
    private String request;
    private String response;
    private List<StackData> xmlStack = new ArrayList<StackData>();

    private String navRequest;
    private String navResponse;
    private String findRequest;
    private String findResponse;
    private String editRequest;
    private String editResponse;

	
    static 
    {
    	thisInstance = new MessageUtil();
    }

    public static MessageUtil getInstance() {
	return thisInstance;
    }

    public String getRequest() {
	return request;
    }

    public void setRequest(String serviceName, String xml) 
    {
		this.request = serviceName + "\n" + xml;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage( serviceName + "\n" + prettyFormat(xml, INDENTATION) );
		stackData.setName("Sent" + getTimestamp());
		xmlStack.add(stackData);
    }

    public String getResponse() 
    { return response; }

    public void setResponse(String serviceName, String xml) 
    {
		this.response = serviceName + "\n" + xml;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage( serviceName + "\n" + prettyFormat(xml, INDENTATION) );
		stackData.setName("Received" + getTimestamp());
		xmlStack.add(stackData);
    }

    public List<StackData> getXmlStack() 
    {
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
	String result = Integer.toString(number);
	if (number < 10 && number >= 0) {
	    result = "0" + result;
	}
	return result;
    }

    private void checkXmlStackSize() {
		if (xmlStack.size() == MAX_STACK_SIZE) {
			xmlStack.remove(0);
		}
    }

    
    public String getEditRequest() {
		return editRequest;
	}

	public void setEditRequest( String serviceName, String xml ) 
	{
		this.editRequest = serviceName + "\n" + xml;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage( serviceName + "\n" + prettyFormat(xml, INDENTATION) );
		stackData.setName("Sent" + getTimestamp());
		xmlStack.add( stackData );
	}

	public String getEditResponse() {
		return editResponse;
	}

	public void setEditResponse( String serviceName, String xml ) 
	{
		this.editResponse = serviceName + "\n" + xml;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage( serviceName + "\n" + prettyFormat(xml, INDENTATION) );
		stackData.setName("Received" + getTimestamp());
		xmlStack.add( stackData );
	}
    
    
    
	public String getFindRequest() {
		return findRequest;
	}

	public void setFindRequest( String serviceName, String xml ) 
	{
		this.findRequest = serviceName + "\n" + xml;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage( serviceName + "\n" + prettyFormat(xml, INDENTATION) );
		stackData.setName("Sent" + getTimestamp());
		xmlStack.add( stackData );
	}

	public String getFindResponse() 
	{
		return findResponse;
	}

	public void setFindResponse( String serviceName, String xml ) 
	{
		this.findResponse = serviceName + "\n" + xml ;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage( serviceName + "\n" + prettyFormat(xml, INDENTATION) );
		stackData.setName("Received" + getTimestamp());
		xmlStack.add( stackData );
	}

	public String getNavRequest() 
	{
		return navRequest;
	}

	public void setNavRequest( String serviceName, String xml ) 
	{
		this.navRequest = serviceName + "\n" + xml;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage( serviceName + "\n" + prettyFormat(xml, INDENTATION) );
		stackData.setName("Sent" + getTimestamp());
		xmlStack.add( stackData );
	}

	public String getNavResponse() 
	{
		return navResponse;
	}

	public void setNavResponse( String serviceName, String xml ) 
	{
		this.navResponse = serviceName + "\n" + xml;
		checkXmlStackSize();
		StackData stackData = new StackData();
		stackData.setMessage( serviceName + "\n" + prettyFormat(xml, INDENTATION) );
		stackData.setName("Received" + getTimestamp());
		xmlStack.add( stackData );
	}

	
	/*
	 * Pretty formats XML given the indent (number of spaces). Does not handle errors
	 *  See: http://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
	 */
	public static String prettyFormat(String input, int indent) 
	{
	    try 
	    {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", indent);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } 
	    catch (Exception e) 
	    {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}

	
}
