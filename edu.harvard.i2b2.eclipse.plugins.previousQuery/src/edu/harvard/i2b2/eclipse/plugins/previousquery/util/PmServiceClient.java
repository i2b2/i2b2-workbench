/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.plugins.previousquery.util;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;



public class PmServiceClient {
    private static ServiceClient sender = null;
	private PmServiceClient() { 
	}
	
	
	public static  ServiceClient getServiceClient() {
		if (sender == null) {
			try {
				sender = new ServiceClient();
			} catch (AxisFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sender;
	}
	
	
}