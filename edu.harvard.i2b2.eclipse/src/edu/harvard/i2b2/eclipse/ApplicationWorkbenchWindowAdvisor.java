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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;
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
		/* tdw9: removing unwanted menu items. There is probably a better way to do this (via xml -- need plugin and component IDs). Should look into it in the future.*/
	    try 
	    {
	        IWorkbenchWindow workbenchWindow =  PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	        IContributionItem[] items = ((WorkbenchWindow)workbenchWindow).getMenuBarManager().getItems();
	        for (IContributionItem item : items)
	        {
	        	// clear edit and navigate manu items
	        	if (item.getId().equals("edit")) 		{ item.setVisible( false ); continue; }
	        	if (item.getId().equals("navigate")) 	{ item.setVisible( false ); continue; } 
	        	IContributionItem [] subItems = ((MenuManager)item).getItems();
	        	for (IContributionItem subItem : subItems)
	        	{
	        		if (item.getId().equals("file"))
	        		{
	        			if (!subItem.getId().equals("quit"))  
	        				subItem.setVisible( false ); 
	        		}
	        		else if (item.getId().equals("help"))
	        		{
	        			if (subItem.getId().equals("edu.harvard.i2b2.newUpdates")) subItem.setVisible( false );
	        			else if (subItem.getId().equals("edu.harvard.i2b2.searchUpdates")) subItem.setVisible( false );
	        			else if (subItem.toString().contains("org.eclipse.ui.actionSet.keyBindings") )subItem.setVisible( false );
	        		}
	        	}
	            //item.setVisible(false);
	        }
	        //((WorkbenchWindow)workbenchWindow).getMenuBarManager().setVisible(false);
	    } 
	    catch (Exception e) 
	    {
	        //handle error
	    }
	    
	    
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
