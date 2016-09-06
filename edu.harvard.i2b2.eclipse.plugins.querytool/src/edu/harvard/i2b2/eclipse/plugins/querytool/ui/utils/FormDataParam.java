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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import org.eclipse.swt.widgets.Control;

public class FormDataParam 
{
	
	private Integer myPercentage	= null;
	private Control	myControl		= null;
	private int		myOffset;

	
	public FormDataParam()
	{	
		myControl		= null;
		myPercentage 	= null;
		myOffset		= 0;
	}

	
	public FormDataParam( Integer percentage )
	{
		if ( percentage != null)
			myPercentage 	= percentage.intValue();
		myOffset		= 0;
		myControl		= null;
	}
	
	public FormDataParam( Integer percentage, int offset )
	{
		if ( percentage != null)
			myPercentage 	= percentage.intValue();
		myOffset		= offset;
		myControl		= null;
	}
	
	public FormDataParam( Control control )
	{
		myControl		= control;
		myOffset		= 0;
		myPercentage 	= null;
	}

	
	public FormDataParam( Control control, int offset )
	{
		myControl		= control;
		myOffset		= offset;
		myPercentage 	= null;
	}
	
	public Object getPlacementConstraint()
	{
		if ( myPercentage == null )
			return myControl;
		return myPercentage;
	}
	
	public int	getOffset()
	{ return myOffset; }
	
	public boolean isNull()
	{ return (myControl == null && myPercentage == null); }
	
	public boolean isControlConstraint()
	{ return myControl != null; }
}
