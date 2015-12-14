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

//import java.util.ArrayList;
import java.util.Hashtable;

public class QueryRow{
	public String RowName;
	public Hashtable QueryConcepts = null;
	public QueryRow(){
		QueryConcepts = new Hashtable();
	}
}