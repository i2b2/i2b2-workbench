/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */
package edu.harvard.i2b2.eclipse.plugins.explorer.views;

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

import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.explorer.data.QueryMasterData;
import edu.harvard.i2b2.explorer.ui.MainComposite;
import edu.harvard.i2b2.timeline.lifelines.Record;

/**
 * Class: ExplorerView
 * 
 * This class defines the Explorer View to the Eclipse workbench
 * 
 */

public class ExplorerView extends ViewPart implements ICommonMethod {
	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.explorer.views.ExplorerView";
	private static final Log log = LogFactory.getLog(ExplorerView.class);

	private MainComposite explorer = null;

	public MainComposite explorer() {
		return explorer;
	}

	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.explorer";
	public static final String TIMELINE_VIEW_CONTEXT_ID = PREFIX
			+ ".timeline_view_help_context";
	private Composite timelineComposite;

	/**
	 * The constructor.
	 */
	public ExplorerView() {

	}

	public Record getRecord() {
		return explorer.getRecord();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		log.info("Explorer plugin version 1.7.03");
		timelineComposite = parent;

		if (!(UserInfoBean.getInstance().isRoleInProject("DATA_LDS"))) {
			new NoAccessComposite(parent, SWT.NONE);
			return;
		}

		explorer = new MainComposite(parent, false);

		// Setup help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				TIMELINE_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
	}

	/**
	 * This is a callback that will allow the i2b2 views to communicate with
	 * each other.
	 */
	@SuppressWarnings("unchecked")
	public void doSomething(Object data) {
		if (data.getClass().getSimpleName().equalsIgnoreCase("String")) {
			String[] msgs = ((String) data).split("-");
			log.debug(msgs[0] + " RefId: " + msgs[1]);
			explorer.setPatientSetText("Patient Set: " + msgs[0] + " Patients");
			explorer.setPatientMinNumText("1");
			explorer.patientRefId(msgs[1]);
			explorer.setPatientSetSize(msgs[0]);
		} else {
			ArrayList<String> msgs = (ArrayList<String>) data;
			log.debug("Explorer View: " + msgs.get(0));
			explorer.populateTableString(msgs);
			explorer.generateTimeLine();
		}
	}

	public void processQuery(String id) {

		QueryMasterData data = new QueryMasterData();

		data.userId(UserInfoBean.getInstance().getUserName());
		data.id(id);
		explorer.processQueryData((QueryMasterData) data);
		explorer.generateTimeLine();

	}

	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench()
				.getHelpSystem();
		Action helpAction = new Action() {
			public void run() {
				helpSystem
						.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.explorer/html/i2b2_timeline_index.htm");
			}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(
				ExplorerView.class, "/icons/help.png"));

		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}

	@Override
	public void setInitializationData(IConfigurationElement cfig,
			String propertyName, Object data) {
		super.setInitializationData(cfig, propertyName, data);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		timelineComposite.setFocus();
	}
}