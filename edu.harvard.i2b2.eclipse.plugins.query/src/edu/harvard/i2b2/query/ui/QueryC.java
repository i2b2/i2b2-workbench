/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 *     Wensong Pan
 */
package edu.harvard.i2b2.query.ui;  

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.*;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.*;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class QueryC extends Composite {
	// private static final Log log = LogFactory.getLog(QueryC.class);

	public String msTitle = "i2b2 CRC Navigator in";
	public String msUsername = "";
	public String msPassword = "";
	public boolean bWantStatusLine = false;

	private StatusLineManager slm = new StatusLineManager();
	private java.awt.Container oAwtContainer;
	private java.awt.Container oAwtContainer_left;

	public java.awt.Container getAWTContainer() {
		return oAwtContainer;
	}

	private QueryToolPanel queryToolPanel;

	public QueryToolPanel queryToolPanel() {
		return queryToolPanel;
	}

	private int mode_;

	public QueryC(Composite parent, int mode) {
		super(parent, SWT.FLAT);

		mode_ = mode;
		createContents(parent);
	}

	/**
	 * @param args
	 */
	protected Control createContents(Composite parent) {
		// log.info("Starting Query Mode");
		GridLayout topGridLayout = new GridLayout(1, false);
		topGridLayout.numColumns = 1;
		topGridLayout.marginWidth = 2;
		topGridLayout.marginHeight = 2;
		setLayout(topGridLayout);

		Composite queryComposite = new Composite(this, SWT.NONE);
		queryComposite.setLayout(new FillLayout(SWT.VERTICAL));
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		queryComposite.setLayoutData(gridData2);

		// the horizontal sash form
		SashForm horizontalForm = new SashForm(queryComposite, SWT.HORIZONTAL);
		horizontalForm.setOrientation(SWT.HORIZONTAL);
		horizontalForm.setLayout(new GridLayout());

		// left sash form
		SashForm leftVerticalForm = new SashForm(horizontalForm, SWT.VERTICAL);
		leftVerticalForm.setOrientation(SWT.VERTICAL);
		leftVerticalForm.setLayout(new GridLayout());

		if (bWantStatusLine) {
			slm.createControl(this, SWT.NULL);
		}
		slm.setMessage("i2b2 Explorer Version 1.6.0");
		slm.update(true);

		// Create the tab folder
		final TabFolder oTabFolder = new TabFolder(leftVerticalForm, SWT.NONE);

		// Create each tab and set its text, tool tip text,
		// image, and control
		TabItem oTreeTab = new TabItem(oTabFolder, SWT.NONE);
		oTreeTab.setText("Concept trees");
		oTreeTab
				.setToolTipText("Hierarchically organized patient characteristics");
		oTreeTab.setControl(getQueryTabControl(oTabFolder));

		// TabItem oFindTab = new TabItem(oTabFolder, SWT.NONE);
		// oFindTab.setText("Find");
		// oFindTab.setToolTipText(
		// "Free-form find tool for patient characteristics");
		// FindTool find = new FindTool(slm);

		// oFindTab.setControl(find.getFindTabControl(oTabFolder));

		// Select the first tab (index is zero-based)
		oTabFolder.setSelection(0);

		// Create the tab folder
		final TabFolder queryRunFolder = new TabFolder(leftVerticalForm,
				SWT.NONE);

		TabItem previousRunTab = new TabItem(queryRunFolder, SWT.NONE);
		previousRunTab.setText("Patient Sets and Previous Queries");
		previousRunTab.setToolTipText("Patient Sets & Previous Queries");
		final Composite composite = new Composite(queryRunFolder, SWT.EMBEDDED);
		previousRunTab.setControl(composite);

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
		oAwtContainer_left = runRoot.getContentPane();

		// runTreePanel = new QueryPreviousRunsPanel(this, null);
		// oAwtContainer_left.add(runTreePanel);

		// Select the first tab (index is zero-based)
		queryRunFolder.setSelection(0);

		final SashForm verticalForm = new SashForm(horizontalForm, SWT.VERTICAL);
		verticalForm.setOrientation(SWT.VERTICAL);
		verticalForm.setLayout(null);

		Composite rightComp = new Composite(verticalForm, SWT.BORDER
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
			queryToolPanel = new QueryToolPanel(this);
		}

		oAwtContainer.add(queryToolPanel);
		queryToolPanel.setSplitBounds(oAwtContainer.getBounds());

		if (mode_ == 0) {
			// bottomC = new ExplorerC(verticalForm, false);
			verticalForm.setWeights(new int[] { 40, 50 });
		}
		verticalForm.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				if (mode_ == 0) {
					int height = verticalForm.getBounds().height;
					// System.out.println("Height: "+height);
					if (height > 285) {
						try {
							verticalForm.setWeights(new int[] { 285,
									height - 285 });
						} catch (Exception e) {
							return;
						}
					}
				}
			}
		});

		horizontalForm.setWeights(new int[] { 20, 70 });
		return parent;
	}

	protected Control getQueryTabControl(TabFolder tabFolder) {
		Composite compositeQueryTree = new Composite(tabFolder, SWT.NONE);
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
		// *compositeQueryTreeTop.setLayout(gridLayoutTree);
		GridData fromTreeGridData = new GridData(GridData.FILL_BOTH);
		fromTreeGridData.widthHint = 300;
		// *compositeQueryTreeTop.setLayoutData(fromTreeGridData);
		compositeQueryTree.setLayoutData(fromTreeGridData);

		return compositeQueryTree;
	}

	public static void main(String[] args) {
		final String ssFakeApplicationConfigurationXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<contents>\r\n"
				+ "    <table>\r\n"
				+ "        <name>Demographics</name>\r\n"
				+ "        <tableName>Demographics</tableName>\r\n"
				+ "        <status/>\r\n"
				+ "        <description/>\r\n"
				+ "        <lookupDB>metadata</lookupDB>\r\n"
				+ "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
				+ "    </table>\r\n"
				+ "    <table>\r\n"
				+ "        <name>Diagnoses</name>\r\n"
				+ "        <tableName>Diagnoses</tableName>\r\n"
				+ "        <status/>\r\n"
				+ "        <description/>\r\n"
				+ "        <lookupDB>metadata</lookupDB>\r\n"
				+ "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
				+ "    </table>\r\n"
				+ "    <table>\r\n"
				+ "        <name>Medications</name>\r\n"
				+ "        <tableName>Medications</tableName>\r\n"
				+ "        <status/>\r\n"
				+ "        <description/>\r\n"
				+ "        <lookupDB>metadata</lookupDB>\r\n"
				+ "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
				+ "    </table>\r\n"
				+ "    <table>\r\n"
				+ "        <name>I2B2</name>\r\n"
				+ "        <tableName>i2b2</tableName>\r\n"
				+ "        <status/>\r\n"
				+ "        <description/>\r\n"
				+ "        <lookupDB>metadata</lookupDB>\r\n"
				+ "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
				+ "    </table>\r\n" + "</contents>";
		System.setProperty("ApplicationConfigurationXML",
				ssFakeApplicationConfigurationXML);
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		shell.setText("ExplorerC Test");
		shell.setSize(1000, 800);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}