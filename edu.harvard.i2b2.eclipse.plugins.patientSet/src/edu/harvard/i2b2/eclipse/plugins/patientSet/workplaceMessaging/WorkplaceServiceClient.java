/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Raj Kuttan
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.patientSet.workplaceMessaging;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class WorkplaceServiceClient {
	public static final String THIS_CLASS_NAME = WorkplaceServiceClient.class.getName();
    private static Log log = LogFactory.getLog(THIS_CLASS_NAME);	
	
    private static ServiceClient sender = null;
	private WorkplaceServiceClient() { 
	}
	
	public static  ServiceClient getServiceClient() throws AxisFault{
		if (sender == null) {
			try {
				sender = new ServiceClient();
			} catch (AxisFault e) {
				log.error(e.getMessage());
				throw e;
			}

		}
		return sender;
	}
	
	
}