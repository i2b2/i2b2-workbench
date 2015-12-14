/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * 
 * 
 * Contributors:
 * 		Lori Phillips
 * 		Wensong Pan (ported it here from ontology plug-in)
 */

package edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OntologyResponseMessage extends OntologyResponseData {

	public static final String THIS_CLASS_NAME = OntologyResponseMessage.class
			.getName();
	private Log log = LogFactory.getLog(THIS_CLASS_NAME);

	public OntologyResponseMessage() {
	}

}
