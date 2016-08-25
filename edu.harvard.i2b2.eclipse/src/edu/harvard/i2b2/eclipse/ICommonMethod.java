/*
* Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

/** 
 *  Interface: ICommonMethod
 * 
 *  This an interface will allow the i2b2 views to communicate with
 *  each othere.
 *   
 */

package edu.harvard.i2b2.eclipse;

public interface ICommonMethod {
	public void doSomething(Object obj);
	public void processQuery(String id);
}
