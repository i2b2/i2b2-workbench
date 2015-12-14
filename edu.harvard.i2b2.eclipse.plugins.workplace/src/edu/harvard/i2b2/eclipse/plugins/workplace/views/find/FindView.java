/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 * 		Mike Mendis
 * 		Janice Donahoe (documentation for on-line help)
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.views.find;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.eclipse.UserInfoBean;
//import edu.harvard.i2b2.eclipse.plugins.ontology.views.TreeComposite;

public class FindView extends ViewPart {

	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.workplace.views.find.findView";
	public static final String THIS_CLASS_NAME = FindView.class.getName();
	
	//setup context help
	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.workplace";
	public static final String FIND_VIEW_CONTEXT_ID = PREFIX + ".workplaceFind_view_help_context";
	
	private Log log = LogFactory.getLog(THIS_CLASS_NAME);
	public boolean bWantStatusLine = false;
	private StatusLineManager slm = new StatusLineManager();
	static Composite compositeQueryTree;

	private static TabItem treeTab;

	/**
	 * The constructor.
	 */
	public FindView() {
		
		ArrayList<String> roles = (ArrayList<String>) UserInfoBean.getInstance().getProjectRoles();
	    for(String param :roles) {
	    	if(param.equalsIgnoreCase("manager")) {
	    		System.setProperty("WPManager", String.valueOf(true));
	    		break;
	    	}
	    }
		
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindMax") != null)
			System.setProperty("OntFindMax", UserInfoBean.getInstance().getCellDataParam("ont", "OntFindMax"));
		else 
			System.setProperty("OntFindMax","200");
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindHiddens") != null)	
			System.setProperty("OntFindHiddens", UserInfoBean.getInstance().getCellDataParam("ont","OntFindHiddens"));
		else
			System.setProperty("OntFindHiddens","false");
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindSynonyms") != null)
			System.setProperty("OntFindSynonyms",  UserInfoBean.getInstance().getCellDataParam("ont","OntFindSynonyms"));	
		else
			System.setProperty("OntFindSynonyms","true");		
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntDisableModifiers") != null)
			System.setProperty("OntDisableModifiers",  UserInfoBean.getInstance().getCellDataParam("ont","OntDisableModifiers"));	
		else
			System.setProperty("OntDisableModifiers","false"); 	
		
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		log.info("Find Terms plugin version 1.6.0");
		
		compositeQueryTree = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
	//	layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		compositeQueryTree.setLayout(layout);
		
		GridData fromTreeGridData = new GridData (GridData.FILL_BOTH);
	//	fromTreeGridData.widthHint = 300;
		fromTreeGridData.grabExcessHorizontalSpace = true;
		fromTreeGridData.grabExcessVerticalSpace = true;
		compositeQueryTree.setLayoutData(fromTreeGridData);

		
/*		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout(SWT.VERTICAL));
		
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		top.setLayoutData(layoutData);
*/		
	    // Create the tab folder
		final TabFolder tabFolder = new TabFolder(compositeQueryTree, SWT.NONE);
		tabFolder.setLayoutData(fromTreeGridData);
//		 Create each tab and set its text, tool tip text,
	    // image, and control
	    treeTab = new TabItem(tabFolder, SWT.BOTTOM);
	    treeTab.setText("Search by Names");
	    treeTab.setToolTipText("Free-form find tool for categories");
	    FindTool findtool = new FindTool(slm);
	    treeTab.setControl(findtool.getFindTabControl(tabFolder));
	    
	    //ignore find tab for now
	    //TabItem findTab = new TabItem(tabFolder, SWT.NONE);
	    //findTab.setText("Search by Codes");
	    //findTab.setToolTipText("Free-form find tool for schemes");
	    //SchemeTool schemes = new SchemeTool(slm);
	    //findTab.setControl(schemes.getFindTabControl(tabFolder));

	    // Select the first tab (index is zero-based)
	    tabFolder.setSelection(0);

	//	ModifierComposite.setInstance(compositeQueryTree); 
	    
		//setup context help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, FIND_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
	}

/*	protected Control getFindTabControl(TabFolder tabFolder)
	{
		// Drag "from" tree
		Composite compositeQueryTree = new Composite(tabFolder, SWT.NULL);
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
	//	fromTreeGridData.widthHint = 300;
		compositeQueryTree.setLayoutData(fromTreeGridData);

		TreeComposite dragTree = new TreeComposite(compositeQueryTree, 1, slm);
		dragTree.setLayoutData(new GridData (GridData.FILL_BOTH));
		dragTree.setLayout(gridLayout);
		
		return compositeQueryTree;
	}
	*/
	//
	// Passing the focus request
	 //
	@Override
	public void setFocus() {
		treeTab.getControl().setFocus();
	}
	

	//add help button
	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action(){
			@Override
			public void run() {
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.workplace/html/i2b2_wpf_index.html");
		}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(FindView.class, "/icons/help.png"));
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}


}
