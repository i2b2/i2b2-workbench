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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Control;

public class UIEnabledManager 
{
	private Control			myRootUI;
	private Set<Control> 	myUnchangedControls;
	
	public UIEnabledManager( Control rootUI )
	{
		myRootUI = rootUI;
		myUnchangedControls = new HashSet<Control>();
	}
	
	public Set<Control> getUnchangedControls()
	{ return myUnchangedControls; }
	
	
	public void setEnabledAndSaveUnchanged( boolean enabledFlag )
	{
		myUnchangedControls.clear();
		UIUtils.recursiveSetEnabledAndRememberUnchangedControls( myRootUI, enabledFlag, myUnchangedControls );
	}
	
	public void restoreEnabledStates( boolean enabledFlag )
	{
		UIUtils.recursiveSetEnabled( myRootUI, enabledFlag, myUnchangedControls );
	}

}
