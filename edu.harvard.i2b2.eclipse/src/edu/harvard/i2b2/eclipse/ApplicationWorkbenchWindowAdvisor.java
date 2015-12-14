/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *     Lori Phillips - initial API and implementation
 *     Wensong Pan
 *     Mike Mendis
 */
package edu.harvard.i2b2.eclipse;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.part.IntroPart;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1200, 900));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowPerspectiveBar(false);
		configurer.setTitle(System.getProperty("applicationName") + " Workbench");
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		
		if(!System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			return;
		}
		
		IIntroManager manager = PlatformUI.getWorkbench().getIntroManager();
		final IntroPart part = (IntroPart) manager.getIntro();
		
		if(part != null) { 
			if(manager.isIntroStandby(part)) {
				manager.closeIntro(part);
				manager.showIntro(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), false);	
				manager.closeIntro(part);
				manager.showIntro(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), true);	
			}
			else {
				manager.closeIntro(part);
				manager.showIntro(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), true);	
				manager.closeIntro(part);
				manager.showIntro(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), false);	
			}
		}	
	}
}
