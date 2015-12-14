/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 
 *     Lori Phillips - initial API and implementation
 *     Mike Mendis
 *     Wensong Pan
 *     
 */
package edu.harvard.i2b2.eclipse;

import java.io.File;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "edu.harvard.i2b2.eclipse.perspective";

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer){
		configurer.setSaveAndRestore(true);
		
		System.out.println("Before starting the workbench");
		File dir = new File("temp");
		if(!dir.exists()) {
			dir.mkdir();
		}
	}

	@Override
	public void postShutdown() {
		System.out.println("Before closing the workbench");
		
		File dir = new File("temp");
		File[] files = dir.listFiles();
		
		for(int i=0; i<files.length; i++) {
			deleteDir(files[i]);
		}
		
		super.postShutdown();
	}
	
	private void deleteDir(File dir) {
		if(!dir.exists()) {
			return;
		}
		
		if(dir.isDirectory()) {
						
			File[] files = dir.listFiles();
			
			for(int i=0; i<files.length; i++) {
				deleteDir(files[i]);
			}
			
			if(dir.listFiles().length == 0) {
				dir.delete();
			}
		}
		else {
			dir.delete();
		}
	}
}
