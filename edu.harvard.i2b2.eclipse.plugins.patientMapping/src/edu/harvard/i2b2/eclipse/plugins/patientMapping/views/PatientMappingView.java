/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */
package edu.harvard.i2b2.eclipse.plugins.patientMapping.views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.JRootPane;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.patientMapping.data.QueryMasterData;
import edu.harvard.i2b2.patientMapping.ui.MainComposite;
import edu.harvard.i2b2.patientMapping.ui.PatientMappingJPanel;

/**
 * Class: ExplorerView
 * 
 * This class defines the Explorer View to the Eclipse workbench
 * 
 */

public class PatientMappingView extends ViewPart implements ICommonMethod {
	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.patientMapping.views.PatientMappingView";
	private static final Log log = LogFactory.getLog(PatientMappingView.class);

	private MainComposite explorer = null;

	public MainComposite explorer() {
		return explorer;
	}

	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.patientMapping";
	public static final String PATIENTMAPPING_VIEW_CONTEXT_ID = PREFIX
			+ ".patientmapping_view_help_context";
	private Composite timelineComposite;
	private java.awt.Container oAwtContainer;

	private PatientMappingJPanel runTreePanel;

	/**
	 * The constructor.
	 */
	public PatientMappingView() {

	}

	//public Record getRecord() {
		//return explorer.getRecord();
	//}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		log.info("Patient Mapping plugin version 1.7.0");
		timelineComposite = parent;

		if (!(UserInfoBean.getInstance().isRoleInProject("DATA_LDS"))) {
			new NoAccessComposite(parent, SWT.NONE);
			return;
		}

		//explorer = new MainComposite(parent, false);*/
		
		Composite composite = new Composite(parent, SWT.EMBEDDED);

		/* Create and setting up frame */
		////for mac fix
		//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
			//SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";
		Frame runFrame = SWT_AWT.new_Frame(composite);
		Panel runPanel = new Panel(new BorderLayout());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}

		runFrame.add(runPanel);
		JRootPane runRoot = new JRootPane();
		runPanel.add(runRoot);
		oAwtContainer = runRoot.getContentPane();

		runTreePanel = new PatientMappingJPanel(oAwtContainer);//PreviousQueryPanel(this);
		//runTreePanel.setBackground(Color.BLUE);

		oAwtContainer.add(runTreePanel);

		// Setup help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				PATIENTMAPPING_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
	}

	/**
	 * This is a callback that will allow the i2b2 views to communicate with
	 * each other.
	 */
	public void doSomething(Object data) {
		/*if (data.getClass().getSimpleName().equalsIgnoreCase("String")) {
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
		}*/
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
						.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.patientMapping/html/i2b2_pm_index.html");
			}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(
				PatientMappingView.class, "/icons/help.png"));

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