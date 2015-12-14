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

package edu.harvard.i2b2.eclipse.plugins.querytool.views;

public class QueryToolViewAccessor 
{
	private static QueryToolViewAccessor myInstance;
	
	private QueryToolView myView;
	
	public static void setInstance( QueryToolView view )
	{
		if (myInstance == null) 
			myInstance = new QueryToolViewAccessor( view );		
	}
	
	public static QueryToolViewAccessor getInstance()
	{ return myInstance; }

	private QueryToolViewAccessor( QueryToolView view )
	{ myView = view; }
	
	// Do not call this method during UI set up. the QueryToolView is not yet fully initialized, and its components are likely null.
	public QueryToolView getQueryToolView()
	{ return myView; }
	
}
