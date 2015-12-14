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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import org.apache.axis2.AxisFault;

public class MessageErrorHandler 
{
	
	/*
	 * Handles generic exceptions resulting from sending/receiving message to/from server
	 */
	public static void handleMessagingException( Exception e )
	{
		e.printStackTrace();
		if (  e instanceof AxisFault )
		{
			UIUtils.popupError("Error", "Trouble with connection to the remote server,\nthis is often a network error, please try again", "Network Error" );
		}
		else
		{
			UIUtils.popupError("Error", "An error has occurred sending message to the remote server.", e.getMessage() );
		}
	}
	
}
