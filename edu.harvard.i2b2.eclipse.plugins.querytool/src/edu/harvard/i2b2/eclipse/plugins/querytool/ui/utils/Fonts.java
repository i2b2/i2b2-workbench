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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class Fonts 
{
	public static final Font	LARGE_FONT 		= new Font( Display.getCurrent(), "Arial", 28, SWT.BOLD );
	
	public static final Font	LARGE_TAHOMA 	= new Font( Display.getCurrent(), "Tahoma", 20, SWT.NONE );	
	public static final Font 	MEDIUM_TAHOMA	= new Font( Display.getCurrent(), "Tahoma", 16, SWT.NONE );
}
