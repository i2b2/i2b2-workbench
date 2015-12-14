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

package edu.harvard.i2b2.query.data;

import java.util.ArrayList;

public class QueryConceptTreeNodeFactoryProduct 
{
	private boolean myHasError 							= false;
	private ArrayList<QueryConceptTreeNodeData> myData	= null;
	private String	myErrorMessage						= null;
	private String	myErrorReason						= null;

	
	public QueryConceptTreeNodeFactoryProduct( ArrayList<QueryConceptTreeNodeData> data)
	{
		myData 			= data;
	}
	
	public QueryConceptTreeNodeFactoryProduct( ArrayList<QueryConceptTreeNodeData> data, boolean hasError, String message, String reason )
	{
		myHasError 		= hasError;
		myData 			= data;
		myErrorMessage 	= message;
		myErrorReason	= reason;
	}

	public QueryConceptTreeNodeFactoryProduct( boolean hasError, String message, String reason )
	{
		myHasError 		= hasError;
		myErrorMessage 	= message;
		myErrorReason	= reason;
	}

	
	public ArrayList<QueryConceptTreeNodeData> getData()
	{ return this.myData; }
	
	public boolean hasError()
	{ return this.myHasError; }
	
	public String getErrorMessage()
	{ return myErrorMessage; }
	
	public String getErrorReason()
	{ return myErrorReason; }

}
