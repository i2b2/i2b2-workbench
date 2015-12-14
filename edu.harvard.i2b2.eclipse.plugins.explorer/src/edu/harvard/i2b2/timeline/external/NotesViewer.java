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
import edu.harvard.i2b2.timeline.lifelines.TextViewerFrame;

public class NotesViewer implements Executor {

	String note;

	public void execute() throws Exception {

		if (note == null)
			throw new Exception("Failed to call init first, or note is empty");

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TextViewerFrame(note, "").setVisible(true);
			}
		});

	}

	public void init(Application app, String data) throws Exception {
		note = data;
	}

	public void destroy() throws Exception {
		// TODO Auto-generated method stub
	}

	public boolean activated() throws Exception {
		return true;
	}

	public BufferedReader getStdOutput() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public BufferedReader getStdError() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
