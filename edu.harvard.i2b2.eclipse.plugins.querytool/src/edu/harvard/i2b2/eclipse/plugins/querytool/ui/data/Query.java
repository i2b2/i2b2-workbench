/*
 * Copyright (c) 2006-2016 Partners HealthCare 
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

import java.util.concurrent.Callable;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.QueryResultObtainedDelegator;
import edu.harvard.i2b2.query.serviceClient.QueryRequestClient;
import edu.harvard.i2b2.query.serviceClient.QueryResultHandler;

public class Query implements Callable<QueryTaskResult>
{
	
	private String 	myName	= null;
	private String 	myXML 	= null;
	private String	myMode	= null;
	
	private String	myResponse = null;
	private QueryTaskResult myResult = null;
	private QueryResultObtainedDelegator myDelegator;	// delegates the responsibility of changing the UI client after query result is obtained (whether failed or succeeded)
	 
	private boolean myHasEmptyGroup;
	private boolean myIsUsingGraphicalAnalysis;
	private boolean myIsUsingTimeline;
	
	public Query( String name, String queryMode, String xml, boolean isUsingGraphicalAnalysis, boolean isUsingTimeline, QueryResultObtainedDelegator delegator )
	{
		myName	= name;
		myXML 	= xml;
		myMode	= queryMode;
		myDelegator 				= delegator;

		myIsUsingGraphicalAnalysis 	= isUsingGraphicalAnalysis;
		myIsUsingTimeline			= isUsingTimeline;
	}
	
	public String getName()
	{ return myName; }
	
	public String getXML()
	{ return myXML; }
	
	public String getQueryMode()
	{ return myMode; }

	@Override
	public QueryTaskResult call() throws Exception 
	{
		myResult = null;
		try
		{
			myResponse = QueryRequestClient.sendQueryRequestREST( this.getXML() );
			System.err.println( "Query.call(): Response:\n" + myResponse );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		
		myResult = QueryResultHandler.getInstance().handleQueryResult( this );
		myDelegator.queryResultObtained( myResult );
		
		return myResult;
	}
	
	
	public boolean isDone()
	{ return (myResult != null); }
	
	
	public QueryTaskResult getResult()
	{ return myResult; }
	
	
	public String getResponse()
	{ return myResponse; }

	public boolean isUsingGraphicalAnalysis()	{ return this.myIsUsingGraphicalAnalysis; }
	public boolean isUsingTimeline()			{ return this.myIsUsingTimeline; }

}
