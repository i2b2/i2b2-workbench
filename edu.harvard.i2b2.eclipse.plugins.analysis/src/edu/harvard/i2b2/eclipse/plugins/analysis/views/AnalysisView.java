/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     Janice Donahoe (on-line help only)
 *     
 */
package edu.harvard.i2b2.eclipse.plugins.analysis.views;

import java.util.ArrayList;

//import org.eclipse.swt.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import edu.harvard.i2b2.analysis.ui.AnalysisComposite;
import edu.harvard.i2b2.analysis.ui.ExplorerC;
import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.analysis.data.QueryInstanceData;
import edu.harvard.i2b2.analysis.data.QueryMasterData;
import edu.harvard.i2b2.timeline.lifelines.record;

/**
 *  Class: AnalysisView 
 *  
 *  This class defines the Analysis View to the
 *  Eclipse workbench
 *  
 */


public class AnalysisView extends ViewPart implements ICommonMethod {
	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.analysis.views.AnalysisView";
    private static final Log log = LogFactory.getLog(AnalysisView.class);
	
	private ExplorerC explorer = null;
	public ExplorerC explorer() {return explorer;}
	
	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.analysis"; 
	public static final String ANALYSIS_VIEW_CONTEXT_ID = PREFIX + ".analysis_view_help_context";
	
	private AnalysisComposite analysis;
	
	/**
	 * The constructor.
	 */
	public AnalysisView() {
		
	}

	public record getRecord() {
		return explorer.getRecord();
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
	    	analysis = new AnalysisComposite(parent, SWT.NONE);

		
		// Setup help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, ANALYSIS_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
	}
	
	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action() {
			public void run(){
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.analysis/html/i2b2_analysis_index.htm");
			}
		};	
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(AnalysisView.class, "/icons/help.png"));
		
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}
	
	@Override
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
    		super.setInitializationData(cfig, propertyName, data);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		analysis.setFocus();
	}
	
	/**
	 * This is a callback that will allow the i2b2 views to communicate
	 * with each other.
	 */
    public void doSomething(Object obj) {
    	String msg = (String) obj;
    	String[] msgs = msg.split("#i2b2seperater#");
    	
    	QueryInstanceData nameNode = new QueryInstanceData();
    	nameNode.visualAttribute("CA");
    	nameNode.userId(UserInfoBean.getInstance().getUserName());
    	nameNode.tooltip("A query run by "+nameNode.userId());
    	nameNode.id(msgs[1]);
    	nameNode.name(msgs[0]+" ["+UserInfoBean.getInstance().getUserName()+"]");
		
    	analysis.addNode(nameNode);
    	//analysis.clearTree();
	//analysis.insertNodes(nameNode);
	//analysis.setSelection(0);
	}

	//public void addNode(QueryInstanceData node) {
	//    analysis.clearTree();
	//    analysis.insertNodes(node);
	//    analysis.setSelection(0);
	//}
    
    public void processQuery(String id) {}
}