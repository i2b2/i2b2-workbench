/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 * 		Janice Donahoe (documentation for on-line help)
 */


package edu.harvard.i2b2.eclipse.plugins.ontology.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.CRCServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetPsmResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.AnalysisPluginMetadataTypeType;


/**
 * The Ontology View class provides the Ontology UI View to the
 *  Eclipse framework  --- This has been ported from the CRC Navigator project.
 * @author Lori Phillips   
 */

public class OntologyView extends ViewPart implements ICommonMethod {

	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.ontology.views.ontologyView";
	public static final String THIS_CLASS_NAME = OntologyView.class.getName();
	
	//setup context help
	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
	public static final String ONTOLOGY_VIEW_CONTEXT_ID = PREFIX + ".navigate_terms_view_help_context";
	
	private Composite compositeQueryTree;
	private Composite modifierComp;
	private Log log = LogFactory.getLog(THIS_CLASS_NAME);
	public boolean bWantStatusLine = false;
	private StatusLineManager slm = new StatusLineManager();	
    private TreeComposite dragTree;

	/**
	 * The constructor.
	 */
	public OntologyView() {
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntMax") != null)
			System.setProperty("OntMax", UserInfoBean.getInstance().getCellDataParam("ont", "OntMax"));
		else 
			System.setProperty("OntMax","200");
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntHiddens") != null)	
			System.setProperty("OntHiddens", UserInfoBean.getInstance().getCellDataParam("ont","OntHiddens"));
		else
			System.setProperty("OntHiddens","false");
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntSynonyms") != null)
			System.setProperty("OntSynonyms",  UserInfoBean.getInstance().getCellDataParam("ont","OntSynonyms"));	
		else
			System.setProperty("OntSynonyms","false");
		
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntPatientCount") != null)
			System.setProperty("OntPatientCount",  UserInfoBean.getInstance().getCellDataParam("ont","OntPatientCount"));	
		else			
			System.setProperty("OntPatientCount","false");
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntShortTooltips") != null)
			System.setProperty("OntShortTooltips",  UserInfoBean.getInstance().getCellDataParam("ont","OntShortTooltips"));	
		else
			System.setProperty("OntShortTooltips","false");
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntConceptCode") != null)
			System.setProperty("OntConceptCode",  UserInfoBean.getInstance().getCellDataParam("ont","OntConceptCode"));	
		else
			System.setProperty("OntConceptCode","false"); 		
		
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntDisableModifiers") != null)
			System.setProperty("OntDisableModifiers",  UserInfoBean.getInstance().getCellDataParam("ont","OntDisableModifiers"));	
		else
			System.setProperty("OntDisableModifiers","false"); 	
		//System.setProperty("user", UserInfoBean.getInstance().getUserName());
		//System.setProperty("pass", UserInfoBean.getInstance().getUserPassword());
		
		try {    		
    		String response = null;
    		GetPsmResponseMessage r_msg = new GetPsmResponseMessage();
    		StatusType procStatus = null;	

    		// send request to start the count process
    		response = CRCServiceDriver. getAnalysisPlugins();
    		procStatus = r_msg.processResult(response);		

    		if (procStatus.getType().equals("ERROR")){
    	//		System.setProperty("errorMessage",  procStatus.getValue());				
    			System.setProperty("patientCountVisible", "false");
    			return;
    		}	
    		AnalysisPluginMetadataTypeType plugin = r_msg.extractAnalysisPluginMetadata(response);

    		if(plugin == null)
    			System.setProperty("patientCountVisible", "false");
    		else 
    			System.setProperty("patientCountVisible", "true");
    	
    		
    		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		System.setProperty("patientCountVisible", "false");
		log.info("Problem accessing CRC to get list of analysis plugins");    		
	//	return null;
	}
}


	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */

	@Override
	public void createPartControl(Composite parent)  {
		log.info("Navigate Terms version 1.7.0");
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

		dragTree = new TreeComposite(compositeQueryTree, 1, slm);
		dragTree.setLayoutData(new GridData (GridData.FILL_BOTH));
		dragTree.setLayout(gridLayout);
		
		//setup context help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, ONTOLOGY_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
	}

	//add help button
	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action(){
			@Override
			public void run() {
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_navigate_terms_index.htm");
		}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(OntologyView.class, "/icons/help.png"));
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}

	
	/**
	 * Passing the focus request 
	 */
	@Override
	public void setFocus() {
		compositeQueryTree.setFocus();
	}
	
	/**
	 * This is a callback that will allow the i2b2 views to communicate with
	 * each other.
	 */
	public void doSomething(Object obj) {
		String msg = (String) obj;
		
		TreeNode node = dragTree.getBrowser().populateRootNode();
		dragTree.getBrowser().getViewer().setInput(node);
		
	}
	
	public void processQuery(String id) {
		dragTree.getBrowser().populateRootNode();
	}
	
}
