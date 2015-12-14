/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 *     
 */
package edu.harvard.i2b2.smlib;

import java.util.ArrayList;

public class QueryConcept {
	public String ConceptName;
	public ArrayList EntryList;
	public QueryConcept(){
		EntryList = new ArrayList();
	}
}
