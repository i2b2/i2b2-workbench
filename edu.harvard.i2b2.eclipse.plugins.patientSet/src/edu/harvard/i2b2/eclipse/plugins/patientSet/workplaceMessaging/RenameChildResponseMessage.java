/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.patientSet.workplaceMessaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.crcxmljaxb.datavo.wdo.GetChildrenType;


/**
 * @author Lori Phillips
 *
 */
public class RenameChildResponseMessage extends WorkplaceResponseData {
	
	public static final String THIS_CLASS_NAME = RenameChildResponseMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

	public RenameChildResponseMessage() {}
	
}
	
	