/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 * 		Mike Mendis
 *     
 */
package edu.harvard.i2b2.timeline.external;

import java.io.BufferedReader;

import edu.harvard.i2b2.navigator.Application;

public interface Executor {
	public void execute() throws Exception;

	public void init(Application app, String filename) throws Exception;

	public void destroy() throws Exception;

	public boolean activated() throws Exception;

	public BufferedReader getStdOutput() throws Exception;

	public BufferedReader getStdError() throws Exception;
}
