/*
 * Copyright (c) 2006-2015 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */
package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

public class TaskResult 
{
	protected boolean		isSuccess 	= true;
	
	protected Exception	myException	= null;
	protected String		myMessage	= null;
	protected String		myReason	= null;
	
	// constructing the 'success' query result
	public TaskResult()
	{}
	
	// construction a query result with errors
	public TaskResult( Exception exception, String message, String reason )
	{
		isSuccess 	= false;
		myException	= exception;
		myMessage	= message;
		myReason	= reason;
	}
	
	public boolean isSuccess()
	{ return isSuccess; }
	
	public Exception getException()
	{ return myException; }
	
	public String	getMessage()
	{ return myMessage; }
	
	public String	getReason()
	{ return myReason; }
}
