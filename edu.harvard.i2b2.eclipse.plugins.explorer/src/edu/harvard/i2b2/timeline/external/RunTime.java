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

import java.io.*;

import edu.harvard.i2b2.navigator.Application;

public class RunTime implements Executor {

	BufferedReader stdOutput, stdError;
	Application app = null;
	Process p;

	public void execute() throws Exception {

		if (app == null)
			throw new Exception("Failed to call init first");

		if (app.getArguments() == null) {
			p = Runtime.getRuntime().exec(app.getCommand());
		} else if (app.getWorkingDirectory() == null) {
			p = Runtime.getRuntime().exec(app.getCommand(),
					app.getArguments().split(" "));
		} else {
			p = Runtime.getRuntime().exec(app.getCommand(),
					app.getArguments().split(" "),
					new File(app.getWorkingDirectory()));
		}

		stdOutput = new BufferedReader(
				new InputStreamReader(p.getInputStream()));

		stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		// TODO Auto-generated method stub

	}

	public boolean activated() throws Exception {
		return true;
	}

	public void init(Application app, String filename) throws Exception {
		// TODO Auto-generated method stub

		this.app = app;

	}

	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		p.destroy();
	}

	public BufferedReader getStdOutput() throws Exception {
		// TODO Auto-generated method stub

		return stdOutput;
	}

	public BufferedReader getStdError() throws Exception {
		// TODO Auto-generated method stub
		return stdError;
	}

}
