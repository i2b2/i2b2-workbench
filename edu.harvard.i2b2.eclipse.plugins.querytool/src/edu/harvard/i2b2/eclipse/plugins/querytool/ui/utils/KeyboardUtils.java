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
import org.eclipse.swt.events.KeyEvent;

public class KeyboardUtils 
{
	
	// Check to see if the key pressed are directional keys or home/end keys for navigatiioing
	public static boolean isTextNavigationKey( KeyEvent ke )
	{
		return( ( ke.keyCode == SWT.ARROW_DOWN ) || 
				( ke.keyCode == SWT.ARROW_UP ) || 
				( ke.keyCode == SWT.ARROW_LEFT ) ||
				( ke.keyCode == SWT.ARROW_RIGHT ) ||
				( ke.keyCode == SWT.HOME) ||
				( ke.keyCode == SWT.END ));
	}
	
	public static boolean isDeletion( KeyEvent ke )
	{
		return( ( ke.keyCode == SWT.DEL ) || 
				( ke.keyCode == SWT.BS ));
	}

}
