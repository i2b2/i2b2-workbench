/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */


package edu.harvard.i2b2.eclipse.plugins.workplace.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;




/**
 * The Workplace View class provides the Workplace UI View to the
 *  Eclipse framework  --- 
 * @author Lori Phillips   
 */

public class WorkplaceView extends ViewPart {

	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.workplace.views.workplaceView";
	public static final String THIS_CLASS_NAME = WorkplaceView.class.getName();
	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.workplace"; 
	public static final String WORKPLACE_VIEW_CONTEXT_ID = PREFIX + ".workplace_view_help_context";

	private Composite compositeQueryTree;
	private Log log = LogFactory.getLog(THIS_CLASS_NAME);
	public boolean bWantStatusLine = false;
	private StatusLineManager slm = new StatusLineManager();	


	/**
	 * The constructor.
	 */
	public WorkplaceView() {
		///get set Cell Param data if it exists
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */

	public void createPartControl(Composite parent) {
		log.info("Workplace plugin version 1.6.0");
		// Drag "from" tree
		compositeQueryTree = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		compositeQueryTree.setLayout(gridLayout);

		GridLayout gridLayoutTree = new GridLayout(1, false);
		gridLayoutTree.numColumns = 1;
		gridLayoutTree.marginHeight = 0;
		GridData fromTreeGridData = new GridData (GridData.FILL_BOTH);
		fromTreeGridData.widthHint = 300;
		compositeQueryTree.setLayoutData(fromTreeGridData);

		TreeComposite dragTree = new TreeComposite(compositeQueryTree, 1, slm);
		dragTree.setLayoutData(new GridData (GridData.FILL_BOTH));
		dragTree.setLayout(gridLayout);
		
		// Setup help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(dragTree, WORKPLACE_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
	}


	
	/**
	 * Passing the focus request 
	 */
	public void setFocus() {
		compositeQueryTree.setFocus();
	}
	
	
	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action() {
			public void run(){
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.workplace/html/i2b2_wp_index.htm");
			}
		};	
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(WorkplaceView.class, "/icons/help.png"));
		
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}
}
