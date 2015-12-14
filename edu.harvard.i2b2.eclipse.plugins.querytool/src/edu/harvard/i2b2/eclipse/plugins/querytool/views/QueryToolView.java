/*
 * Copyright (c) 2006-2015 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.QueryResultTypeSelectionDialog;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.QueryToolMainUI;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;


public class QueryToolView extends ViewPart 
{
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.querytool.views.QueryToolView";
	
	// setup context help
	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.querytool";
	public static final String QUERYTOOL_VIEW_CONTEXT_ID = PREFIX + ".queryToolTemporal_view_help_context";

	/*
	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	*/
	
	private QueryToolMainUI myMainUI;
	
	/**
	 * The constructor.
	 */
	public QueryToolView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) 
	{
		QueryResultTypeSelectionDialog.preBuildUI();				// contact server and pre-build the analysis type selection UI
		Settings.getInstance();										// initialize and load local Settings
		
		myMainUI = new QueryToolMainUI(this, parent, SWT.None );

		// set up context help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, QUERYTOOL_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
		
		// set up global accessor
		QueryToolViewAccessor.setInstance( this );
	}

	public QueryToolMainUI getMainUI()
	{ return this.myMainUI; }
	
	
	public Shell getWorkbenchShell()
	{ return this.getSite().getWorkbenchWindow().getShell(); }
	
	// add help button
	private void addHelpButtonToToolBar() 
	{
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action() 
		{
			public void run() 
			{
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.querytool/html/qtt_index.html");
			}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(QueryToolMainUI.class, "/icons/help.png"));
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() 
	{
		myMainUI.setFocus();
	}
	
	// dispose of things and save settings
	public void dispose()
	{
		super.dispose();
		Event.resetCounter();
		Settings.getInstance().saveSettings();
	}
}