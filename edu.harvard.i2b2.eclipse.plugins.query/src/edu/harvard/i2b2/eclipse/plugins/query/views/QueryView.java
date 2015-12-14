/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.  
 * 
 * Contributors: 
 *     Wensong Pan
 *     Janice Donahoe (documentation for on-line help)
 */

package edu.harvard.i2b2.eclipse.plugins.query.views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.JRootPane;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;

import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.plugins.query.views.QueryView;
import edu.harvard.i2b2.query.ui.QueryToolPanel;
import edu.harvard.i2b2.query.ui.QueryToolInvestigatorPanel;

/**
 * Class: QueryView
 * 
 * This class defines the Query View to the Eclipse workbench
 * 
 */

public class QueryView extends ViewPart implements ICommonMethod {
	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.query.views.QueryView";
	public static final String THIS_CLASS_NAME = QueryView.class.getName();

	private static final Log log = LogFactory.getLog(QueryView.class);

	private java.awt.Container oAwtContainer;

	private int mode_ = 0;

	// setup context help
	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.query";
	public static final String QUERY_VIEW_CONTEXT_ID = PREFIX
			+ ".queryTool_view_help_context";
	private Composite queryComposite;

	private QueryToolPanel queryToolPanel;

	public QueryToolPanel queryToolPanel() {
		return queryToolPanel;
	}

	/**
	 * The constructor.
	 */
	public QueryView() {

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {

		// setup context help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				QUERY_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();

		log.info("Query Tool plugin version 1.7.0");
		GridLayout topGridLayout = new GridLayout(1, false);
		topGridLayout.numColumns = 1;
		topGridLayout.marginWidth = 2;
		topGridLayout.marginHeight = 2;
		parent.setLayout(topGridLayout);

		queryComposite = new Composite(parent, SWT.NONE);
		queryComposite.setLayout(new FillLayout(SWT.VERTICAL));
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		queryComposite.setLayoutData(gridData2);

		Composite rightComp = new Composite(queryComposite, SWT.BORDER
				| SWT.EMBEDDED | SWT.DragDetect);
		/* Create and setting up frame */
		////for mac fix
		//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
			//SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";
		Frame frame = SWT_AWT.new_Frame(rightComp);
		Panel panel = new Panel(new BorderLayout());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}

		frame.add(panel);
		JRootPane root = new JRootPane();
		panel.add(root);
		oAwtContainer = root.getContentPane();

		if (mode_ == 0) {
			queryToolPanel = new QueryToolInvestigatorPanel(this);
		} else {
			queryToolPanel = new QueryToolPanel();
		}

		oAwtContainer.add(queryToolPanel);

	}

	// add help button
	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench()
				.getHelpSystem();
		Action helpAction = new Action() {
			public void run() {
				helpSystem
						.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.query/html/i2b2_qt_index.htm");
			}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(
				QueryView.class, "/icons/help.png"));
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}

	/**
	 * This is a callback that will allow the i2b2 views to communicate with
	 * each other.
	 */
	public void doSomething(Object obj) {
		String msg = (String) obj;
		// System.out.println("Query View: "+ msg);

		queryToolPanel().getTopPanel().reset();
		queryToolPanel().dataModel().redrawPanelFromXml(msg);
	}

	@Override
	public void setInitializationData(IConfigurationElement cfig,
			String propertyName, Object data) {
		// if(cfig!=null && propertyName!=null ) {
		super.setInitializationData(cfig, propertyName, data);
		// }
		// else {
		// String msg = (String)data;
		// System.out.println("Query View: "+ msg);
		// queryToolPanel().getTopPanel().reset();
		// queryToolPanel().dataModel().redrawPanelFromXml(msg);
		// }
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		queryComposite.setFocus();
	}

	public void processQuery(String id) {
	}
}