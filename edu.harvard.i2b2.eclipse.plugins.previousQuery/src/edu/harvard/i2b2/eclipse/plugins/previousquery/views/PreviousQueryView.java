/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

package edu.harvard.i2b2.eclipse.plugins.previousquery.views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.JRootPane;
import javax.swing.UIManager;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.previousquery.data.QueryMasterData;
import edu.harvard.i2b2.previousquery.ui.PreviousQueryPanel;

/**
 * Class: PreviousQueryView
 * 
 * This class defines the Previous Query View to the Eclipse workbench
 * 
 */

public class PreviousQueryView extends ViewPart implements ICommonMethod {

	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.previousquery.views.PreviousQueryView";
	public static final String THIS_CLASS_NAME = PreviousQueryView.class
			.getName();

	private java.awt.Container oAwtContainer;

	private PreviousQueryPanel runTreePanel;

	public PreviousQueryPanel runTreePanel() {
		return runTreePanel;
	}

	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.previousQuery";
	public static final String PREVIOUSQUERY_VIEW_CONTEXT_ID = PREFIX
			+ ".previousQuery_view_help_context";
	private Composite previousQueryComposite;

	/**
	 * The constructor
	 */
	public PreviousQueryView() {

	}

	/**
	 * This is a callback that will allow the i2b2 views to communicate with
	 * each other.
	 */
	public void doSomething(Object obj) {
		String msg = (String) obj;
		if (msg.equalsIgnoreCase("refresh")) {
			runTreePanel.refresh();
		} else {
			String[] msgs = msg.split("#i2b2seperater#");

			QueryMasterData nameNode = new QueryMasterData();
			nameNode.visualAttribute("CA");
			nameNode.userId(UserInfoBean.getInstance().getUserName());
			nameNode.tooltip("A query run by " + nameNode.userId());
			nameNode.id(msgs[1]);
			nameNode.name(msgs[0] + " ["
					+ UserInfoBean.getInstance().getUserName() + "]");
			if(nameNode.name().startsWith("(t)")) {
				nameNode.queryType("TEMPORAL");
			}
			addNode(nameNode);
		}
	}

	public void addNode(QueryMasterData node) {
		runTreePanel.insertNode(node);
	}

	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench()
				.getHelpSystem();
		Action helpAction = new Action() {
			public void run() {
				helpSystem
						.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.previousQuery/html/i2b2_pq_index.htm");
			}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(
				PreviousQueryView.class, "/icons/help.png"));

		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}

	/**
	 * This is a callback that will allow us to create the tabbed viewers and
	 * initialize them.
	 */
	@Override
	public void createPartControl(Composite parent) {

		previousQueryComposite = parent;
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

		runTreePanel = new PreviousQueryPanel(this);
		oAwtContainer.add(runTreePanel);

		// Setup help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				PREVIOUSQUERY_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		previousQueryComposite.setFocus();
	}

	public void processQuery(String id) {
	}
}
