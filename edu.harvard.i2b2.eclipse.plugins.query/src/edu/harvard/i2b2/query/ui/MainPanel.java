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

/*
 * QueryTopPanel.java
 * 
 * Created on August 2, 2006, 9:04 AM
 */

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.swing.*;
import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.QueryInstanceData;
import edu.harvard.i2b2.query.data.QueryMasterData; //import edu.harvard.i2b2.query.data.QueryResultData;
import edu.harvard.i2b2.query.datavo.QueryJAXBUtil; //import edu.harvard.i2b2.query.ui.ConceptTreePanel.QueryDataTransferable;
import edu.harvard.i2b2.query.serviceClient.QueryRequestClient;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.*;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterInstanceResultResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PanelType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionRequestType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByDate;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean;

@SuppressWarnings("serial")
public class MainPanel extends javax.swing.JPanel {
	private static final Log log = LogFactory.getLog(MainPanel.class);
	private MainPanelModel dataModel;
	private String response = null;
	public QueryToolPanel parentPanel;

	private int max_child = 1000;
	private long lEventTime = 0;

	public void max_child(int i) {
		max_child = i;
	}

	public int max_child() {
		return max_child;
	}

	private Thread queryThread = null;
	private boolean firsttime = true; // for the start up bug on windows 2000

	/** Creates new form QueryTopPanel */
	public MainPanel(QueryToolPanel parent) {
		parentPanel = parent;
		dataModel = parent.dataModel();

		UIManager.put("Label.font", new Font("Tahoma", Font.PLAIN, 11));

		UIManager.put("Button.font", new Font("Tahoma", Font.PLAIN, 11));

		UIManager.put("TextField.font", new Font("Tahoma", Font.PLAIN, 11));

		UIManager.put("CheckBox.font", new Font("Tahoma", Font.PLAIN, 11));

		UIManager.put("RadioButton.font", new Font("Tahoma", Font.PLAIN, 11));

		UIManager.put("Tree.font", new Font("Tahoma", Font.PLAIN, 11));

		UIManager.put("ComboBox.font", new Font("Tahoma", Font.PLAIN, 11));

		initComponents();
		// jGetPatientCountCheckBox.setSelected(true);
		// jShowTimelineCheckBox.setSelected(true);

		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentMoved(java.awt.event.ComponentEvent evt) {
				// formComponentMoved(evt);
			}

			@Override
			public void componentResized(java.awt.event.ComponentEvent evt) {
				// formComponentResized(evt);
				// System.out.println("waiting panel resizing ...");
				int width = (getParent().getWidth());
				int height = (getParent().getHeight());
				if (width < 5 || height < 5) {
					return;
				}

				resizePanels(width, height);
				// log.info("width: "+width+", height: "+height);

				if (firsttime) {
					firsttime = false;
					resizePanels(width, height + 3);
					// log.info("second width: "+width+", height: "+(height+3));
				}
			}
		});
	}

	public JButton getRunQueryButton() {
		return jRunQueryButton;
	}

	public JButton getDeleteButton() {
		return jDeleteButton;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		jAnalysisLabel = new javax.swing.JLabel();
		jTimingLabel = new javax.swing.JLabel();
		jNameLabel = new javax.swing.JLabel();
		jSetSizeLabel = new javax.swing.JLabel();
		jNameTextField = new javax.swing.JTextField();
		jSetSizeFiled = new javax.swing.JLabel();
		jDeleteButton = new javax.swing.JButton();
		jScrollPane1 = new GroupPanel("Group 1", this);
		jRunQueryButton = new javax.swing.JButton();
		jCancelButton = new javax.swing.JButton();
		jClearGroupsButton = new javax.swing.JButton();
		jScrollPane2 = new GroupPanel("Group 2", this);
		jScrollPane3 = new GroupPanel("Group 3", this);
		jScrollPane4 = new javax.swing.JScrollPane();
		jPanel1 = new javax.swing.JPanel();
		// jVisitComboBox = new javax.swing.JComboBox();
		jAndOrLabel1 = new javax.swing.JLabel();
		jAndOrLabel2 = new javax.swing.JLabel();
		jMorePanelsButton = new javax.swing.JButton();
		jWorkflowToolBar = new javax.swing.JToolBar();
		// jTimelineToggleButton = new javax.swing.JToggleButton();
		jPatientCountToggleButton = new javax.swing.JToggleButton();
		// jPatientSetToggleButton = new javax.swing.JToggleButton();
		jToolbarPanel = new javax.swing.JPanel();
		jQueryNamePanel = new javax.swing.JPanel();

		// jOptionsScrollPane = new javax.swing.JScrollPane();
		jOptionsPanel = new AnalysisPanel();
		jTimingPanel = new TimingPanel(this.dataModel);
		jShowTimelineCheckBox = new javax.swing.JCheckBox();
		jGetAllPatientsCheckBox = new javax.swing.JCheckBox();
		// jGetPatientCountCheckBox = new javax.swing.JCheckBox();
		// jGetPatientSetCheckBox = new javax.swing.JCheckBox();

		setLayout(null);

		// jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.
		// ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jPanel1.setLayout(null);

		jPanel1.add(jScrollPane1);
		jScrollPane1.setBounds(0, 0, 180, 200);

		jPanel1.add(jScrollPane2);
		jScrollPane2.setBounds(185, 0, 180, 200);

		jPanel1.add(jScrollPane3);
		jScrollPane3.setBounds(370, 0, 180, 200);

		// jAndOrLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
		// ;
		jAndOrLabel1.setText("and");
		// jAndOrLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder())
		// ;
		// jPanel1.add(jAndOrLabel1);
		// jAndOrLabel1.setBounds(190, 90, 30, 18);

		// jAndOrLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
		// ;
		// jAndOrLabel2.setText("and");
		// jAndOrLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder())
		// ;
		// jPanel1.add(jAndOrLabel2);
		// jAndOrLabel2.setBounds(410, 90, 30, 18);

		jQueryNamePanel.setLayout(null);
		jQueryNamePanel.setBorder(javax.swing.BorderFactory
				.createEtchedBorder());

		jNameLabel.setText(" Query Name: ");
		jNameLabel
				.setToolTipText("You may drag this item to workplace to save the query definition");
		jNameLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
		jNameLabel.setBounds(2, 2, 70, 23);
		add(jNameLabel);
		// jQueryNamePanel.add(jNameLabel);
		jNameLabel.setTransferHandler(new NameLabelTextHandler());
		jNameLabel.addMouseListener(new DragMouseAdapter());
		jNameLabel
				.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
					public void mouseMoved(java.awt.event.MouseEvent evt) {
						jNameLabelMouseMoved(evt);
					}
				});
		jNameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseExited(java.awt.event.MouseEvent evt) {
				jNameLabelMouseExited(evt);
			}
		});

		jNameTextField.setText("");
		jNameTextField.setBounds(95, 10, 370, 20);
		jNameTextField.setEditable(false);
		jNameTextField.setDragEnabled(true);
		jNameTextField.setTransferHandler(new TransferHandler("Text"));
		// jQueryNamePanel.add(jNameTextField);
		// add(jNameTextField);
		// add(jQueryNamePanel);
		// jQueryNamePanel.setBounds(5, 5, 400, 50);

		jClearGroupsButton.setFont(new java.awt.Font("Tahoma", 1, 10));
		jClearGroupsButton.setText("X");
		jClearGroupsButton
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jClearGroupsButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		jClearGroupsButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		jClearGroupsButton.setToolTipText("Reset all panels");
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
			jClearGroupsButton
					.setMargin(new java.awt.Insets(-10, -15, -10, -20));
		}
		jClearGroupsButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jClearGroupsButtonActionPerformed(evt);
					}
				});
		jClearGroupsButton.setBounds(600, 10, 30, 20);
		add(jClearGroupsButton);

		jAnalysisLabel.setText("Analysis Types");
		// jAnalysisLabel.setBackground(Color.WHITE);
		// jAnalysisLabel.setOpaque(true);
		jAnalysisLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
		jAnalysisLabel.setBounds(2, 2, 120, 23);
		jAnalysisLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jAnalysisLabel
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		add(jAnalysisLabel);

		jPanel1.setPreferredSize(new Dimension(700, 150));
		jScrollPane4.setViewportView(jPanel1);
		add(jScrollPane4);
		jScrollPane4.setBounds(20, 35, 635, 220);
		jScrollPane4
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane4
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		// jScrollPane4.setBorder(javax.swing.BorderFactory
		// .createLineBorder(new java.awt.Color(0, 0, 0)));
		jCancelButton.setText("Cancel");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});
		// add(jCancelButton);
		// jCancelButton.setBounds(20, 255, 90, 23);
		// jCancelButton.setFont(new Font("Tahoma", Font.PLAIN, 10));

		jGetAllPatientsCheckBox.setText("Get Everyone");
		jGetAllPatientsCheckBox.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
		// jGetAllPatientsCheckBox
		// .setBorder(new javax.swing.border.SoftBevelBorder(
		// javax.swing.border.BevelBorder.RAISED));
		jGetAllPatientsCheckBox.setBorderPainted(true);
		jGetAllPatientsCheckBox.setContentAreaFilled(false);
		jGetAllPatientsCheckBox
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jGetAllPatientsCheckBox
				.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		jGetAllPatientsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jGetAllPatientsCheckBox
				.setToolTipText("Get all the patients in datamart");
		add(jGetAllPatientsCheckBox);
		jGetAllPatientsCheckBox.setBounds(5, 255, 110, 15);
		jGetAllPatientsCheckBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jGetAllPatientsCheckBoxActionPerformed(evt);
					}
				});

		jRunQueryButton.setText("Run Query Above");
		jRunQueryButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRunQueryButtonActionPerformed(evt);
			}
		});
		add(jRunQueryButton);
		jRunQueryButton.setBounds(100, 255, 625, 23);

		jSetSizeLabel.setText(" Patient(s) returned:");
		add(jSetSizeLabel);
		jSetSizeLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
		jSetSizeLabel.setBounds(20, 275, 70, 23);

		// jSetSizeFiled.setText(" subjects");
		// jSetSizeFiled.setEditable(false);
		// jSetSizeFiled.setHorizontalAlignment(SwingConstants.LEFT);
		// add(jSetSizeFiled);
		// jSetSizeLabel.setBounds(20, 275, 70, 23);

		jToolbarPanel.setLayout(new java.awt.BorderLayout());

		// jWorkflowToolBar.setRollover(true);
		// jWorkflowToolBar.setInheritsPopupMenu(true);
		// jTimelineToggleButton
		// .setIcon(new javax.swing.ImageIcon(
		// "C:\\Documents and Settings\\wp066\\My Documents\\icons\\wb16.gif"));
		// /jTimelineToggleButton.setText("Show TimeLine");
		// jTimelineToggleButton.setFocusPainted(false);
		// jWorkflowToolBar.add(jTimelineToggleButton);

		jPatientCountToggleButton
				.setIcon(new javax.swing.ImageIcon(
						"C:\\Documents and Settings\\wp066\\My Documents\\icons\\wb16.gif"));
		jPatientCountToggleButton.setText("Get Patient Count");
		jPatientCountToggleButton.setFocusPainted(false);
		jWorkflowToolBar.add(jPatientCountToggleButton);

		// jPatientSetToggleButton
		// .setIcon(new javax.swing.ImageIcon(
		// "C:\\Documents and Settings\\wp066\\My Documents\\icons\\wb16.gif"));
		// jPatientSetToggleButton.setText("Get Patient Set");
		// jPatientSetToggleButton.setFocusPainted(false);
		// jWorkflowToolBar.add(jPatientSetToggleButton);
		jWorkflowToolBar.setPreferredSize(new Dimension(380, 40));

		jToolbarPanel.add(jWorkflowToolBar, java.awt.BorderLayout.PAGE_START);
		jToolbarPanel.add(jWorkflowToolBar, java.awt.BorderLayout.CENTER);

		// add(jToolbarPanel);
		jToolbarPanel.setBounds(20, 130, 240, 23);

		jMorePanelsButton.setText("<html><center>Add<br>" + "<left>Group");
		jMorePanelsButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jMorePanelsButtonActionPerformed(evt);
					}
				});
		// add(jMorePanelsButton);
		// jMorePanelsButton.setBounds(655, 35, 60, 220);

		jPanel1.add(jMorePanelsButton);
		jMorePanelsButton.setBounds(550, 0, 60, 200);

		// jOptionsPanel.setLayout(null);
		// jOptionsScrollPane.setBorder(javax.swing.BorderFactory
		// .createEtchedBorder());

		// jOptionsPanel.setPreferredSize(new java.awt.Dimension(100, 100));
		jShowTimelineCheckBox.setText("Timeline");
		jShowTimelineCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jShowTimelineCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jShowTimelineCheckBox
				.setToolTipText("Get the patient set and display it in timeline view");
		// jOptionsPanel.add(jShowTimelineCheckBox);
		jShowTimelineCheckBox.setBounds(5, 20, 110, 15);

		// jGetPatientCountCheckBox.setText("Patient Count");
		// jGetPatientCountCheckBox.setBorder(javax.swing.BorderFactory
		// .createEmptyBorder(0, 0, 0, 0));
		// jGetPatientCountCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		// jGetPatientCountCheckBox.setToolTipText("Get the patient count XML");
		// jOptionsPanel.add(jGetPatientCountCheckBox);
		// jGetPatientCountCheckBox.setBounds(5, 50, 110, 15);

		// jGetPatientSetCheckBox.setText("Patient Set");
		// jGetPatientSetCheckBox.setBorder(javax.swing.BorderFactory
		// .createEmptyBorder(0, 0, 0, 0));
		// jGetPatientSetCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		// jOptionsPanel.add(jGetPatientSetCheckBox);
		// jGetPatientSetCheckBox.setBounds(10, 80, 80, 15);

		// jOptionsScrollPane.setViewportView(jOptionsPanel);

		add(jOptionsPanel);
		jOptionsPanel.setBounds(500, 30, 120, 120);

		jTimingLabel.setText("Query Timing");
		// jAnalysisLabel.setBackground(Color.WHITE);
		// jAnalysisLabel.setOpaque(true);
		jTimingLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
		jTimingLabel.setBounds(2, 2, 120, 23);
		jTimingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jTimingLabel
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		add(jTimingLabel);
		jTimingLabel.setBounds(500, 30, 120, 120);

		add(jTimingPanel);
		jTimingPanel.setBounds(500, 122, 120, 70);

		/*
		 * jDeleteButton.setText("Delete"); jDeleteButton.addActionListener(new
		 * java.awt.event.ActionListener() { public void
		 * actionPerformed(java.awt.event.ActionEvent evt) {
		 * jDeleteButtonActionPerformed(evt); } });
		 * 
		 * jVisitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new
		 * String[] { "Groups don't have to occur in the same visit", "Groups
		 * must all occur in the same visit" })); add(jVisitComboBox);
		 * jVisitComboBox.setBounds(20, 40, 240, 22);
		 * 
		 * //add(jDeleteButton); //jDeleteButton.setBounds(20, 10, 65, 23);
		 * 
		 * add(jScrollPane1); jScrollPane1.setBounds(20, 70, 170, 320);
		 * 
		 * jRunQueryButton.setText("Run Query");
		 * jRunQueryButton.addActionListener(new java.awt.event.ActionListener()
		 * { public void actionPerformed(java.awt.event.ActionEvent evt) {
		 * jRunQueryButtonActionPerformed(evt); } });
		 * 
		 * add(jRunQueryButton); jRunQueryButton.setBounds(20, 10, 87, 23);
		 * 
		 * jCancelButton.setText("Remove All");
		 * jCancelButton.addActionListener(new java.awt.event.ActionListener() {
		 * public void actionPerformed(java.awt.event.ActionEvent evt) {
		 * jRemoveAllButtonActionPerformed(evt); } });
		 * 
		 * add(jCancelButton); jCancelButton.setBounds(115, 10, 90, 23);
		 * 
		 * //jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.
		 * ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		 * jPanel1.setLayout(null);
		 * jScrollPane4.getHorizontalScrollBar().setUnitIncrement(20);
		 * //jPanel1.setVisible(false);
		 * 
		 * //jScrollPane1.setToolTipText("scrollpane 1");
		 * jPanel1.add(jScrollPane1); jScrollPane1.setBounds(0, 0, 170, 350);
		 * 
		 * jPanel1.add(jScrollPane2); jScrollPane2.setBounds(210, 0, 170, 350);
		 */

		jAndOrLabel1.setBackground(new java.awt.Color(255, 255, 255));
		jAndOrLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jAndOrLabel1.setText("and");
		jAndOrLabel1.setToolTipText("Click to change the relationship");
		jAndOrLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jAndOrLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jAndOrLabel1MouseClicked(evt);
			}
		});

		// jPanel1.add(jAndOrLabel1);

		jAndOrLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jAndOrLabel2.setText("and");
		jAndOrLabel2.setToolTipText("Click to change the relationship");
		jAndOrLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jAndOrLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jAndOrLabel2MouseClicked(evt);
			}
		});

		// jPanel1.add(jAndOrLabel2);
		// jTree1.addTreeWillExpandListener(this);
		// jTree1.addTreeExpansionListener(this);
		// jScrollPane1.setViewportView(new QueryConceptTreePanel("Group 1"));
		// jScrollPane1.setToolTipText("Double click on a folder to view the
		// items inside");
		// jTree2.addTreeExpansionListener(this);
		// jScrollPane2.setViewportView(new QueryConceptTreePanel("Group 2"));
		// jTree3.addTreeExpansionListener(this);
		// treepanel = new QueryConceptTreePanel("", this);
		// jScrollPane3.setViewportView(new QueryConceptTreePanel("Group 3"));
		// jSlider1.setMajorTickSpacing(20);
		/*
		 * jSlider1.setPaintTicks(true); jSlider1.setValue(0);
		 * jSlider1.setMinorTickSpacing(10); jSlider1.setToolTipText("Slider on
		 * left is more Sensitive Query, " + "on right is more Specific");
		 * add(jSlider1); jSlider1.setBounds(380, 40, 140, 18);
		 * 
		 * //jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
		 * jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
		 * jLabel1.setText("Sensitivity <"); add(jLabel1);
		 * jLabel1.setBounds(290, 40, 80, 20);
		 * 
		 * //jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
		 * jLabel2.setText("> Specificity"); add(jLabel2);
		 * jLabel2.setBounds(525, 40, 70, 20);
		 */

		dataModel.addPanel(jScrollPane1, null, 0);
		dataModel.addPanel(jScrollPane2, jAndOrLabel1, 0);
		dataModel.addPanel(jScrollPane3, jAndOrLabel2, 555);
	}

	@SuppressWarnings("deprecation")
	private void cancel() {
		// System.out.println("Cancel action");
		JOptionPane
				.showMessageDialog(
						this,
						"The query will continue to run in the background. To cancel the query\n"
								+ "in it's entirety you also need to cancel it in Previous Query View.");

		if (queryThread != null) {
			queryThread.stop();
			queryThread = null;
			// setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			// jRunQueryButton.setBackground(defaultcolor);
		}
		jRunQueryButton.setIcon(null);
		if (jGetAllPatientsCheckBox.isSelected()) {
			jRunQueryButton.setText("Get All Patients");
		} else {
			jRunQueryButton.setText("Run Query Above");
		}

		final IWorkbenchPage page = ((QueryToolInvestigatorPanel) parentPanel).parentview
				.getViewSite().getPage();
		ViewPart previousqueryview = (ViewPart) page
				.findView("edu.harvard.i2b2.eclipse.plugins.previousquery.views.PreviousQueryView");
		((ICommonMethod) previousqueryview).doSomething("refresh");
	}

	/*
	 * private void jDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)
	 * { DefaultMutableTreeNode node = null; TreePath parentPath =
	 * jTree1.getSelectionPath();
	 * 
	 * if (parentPath == null) { //There's no selection. return; } else { node =
	 * (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
	 * System.out.println("Remove node: "+
	 * ((QueryTreeNodeData)node.getUserObject()).tooltip());
	 * treeModel.removeNodeFromParent(node); } }
	 */

	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = MainPanel.class.getResource(path);
		return new ImageIcon(imgURL);
	}

	private void jGetAllPatientsCheckBoxActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (jGetAllPatientsCheckBox.isSelected()) {
			jRunQueryButton.setText("Get All Patients");
		} else {
			jRunQueryButton.setText("Run Query Above");
		}
	}

	@SuppressWarnings("deprecation")
	private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// System.out.println("Cancel action");
		JOptionPane
				.showMessageDialog(
						this,
						"The query will continue to run in the background. To cancel the query\n"
								+ "in it's entirety you also need to cancel it in Previous Query View.");
		if (queryThread != null) {
			queryThread.stop();
			queryThread = null;
			// setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			jRunQueryButton.setIcon(null);
			jRunQueryButton.setText("Run Query Above");
			// jRunQueryButton.setBackground(defaultcolor);
		}

		final IWorkbenchPage page = ((QueryToolInvestigatorPanel) parentPanel).parentview
				.getViewSite().getPage();
		ViewPart previousqueryview = (ViewPart) page
				.findView("edu.harvard.i2b2.eclipse.plugins.previousquery.views.PreviousQueryView");
		((ICommonMethod) previousqueryview).doSomething("refresh");
	}

	private void jRunQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// System.out.println("value set on the slider: "+ jSlider1.getValue());
		if (dataModel.isAllPanelEmpty()
				&& !jGetAllPatientsCheckBox.isSelected()) {
			JOptionPane.showMessageDialog(this, "All groups are empty.");
			return;
		}

		// /create result option list from option selections
		boolean timelinetmp = false;// jShowTimelineCheckBox.isSelected();
		ArrayList<String> optionList = jOptionsPanel.getSelectedAnalysis();
		// for(int i=0; i<optionList.size(); i++) {
		// if(optionList.get(i).equalsIgnoreCase("Timeline")) {
		// timelinetmp = true;
		// }
		// }
		final boolean timeline = jOptionsPanel.isTimelineSelected();
		if (optionList.size() == 0) {
			JOptionPane.showMessageDialog(this, "No analysis is selected.");
			return;
		}

		if (jOptionsPanel.hasMissingInfo) {
			String message = "";
			for (int i = 0; i < jOptionsPanel.missingTypes.size(); i++) {
				message += "    " + jOptionsPanel.missingTypes.get(i);
				message += "\n";
			}
			log
					.error("The knowledge on how to run the query is missing for Analysis type(s):\n"
							+ message);
			JOptionPane.showMessageDialog(this,
					"The knowledge on how to run the query is missing for Analysis type(s):\n"
							+ message);
			return;
		}

		if (jRunQueryButton.getText().indexOf("Run Query") < 0
				&& jRunQueryButton.getText().indexOf("Get All") < 0) {
			cancel();
			return;
		}

		String queryNametmp = jNameTextField.getText();
		if (jGetAllPatientsCheckBox.isSelected()) {
			queryNametmp = "All Patients";
		} else {
			// if(queryNametmp.equals("") || queryNametmp == null) {
			queryNametmp = dataModel.getTmpQueryName();
			// }
			Object selectedValue = JOptionPane.showInputDialog(this,
					"Please supply a name for this query: ",
					"Query Name Dialog", JOptionPane.PLAIN_MESSAGE, null, null,
					queryNametmp);

			if (selectedValue == null) {
				return;
			} else {
				queryNametmp = (String) selectedValue;
			}
		}

		dataModel.queryName(queryNametmp);
		final String queryName = queryNametmp;
		// System.out.println("Provided query name: " + queryName);

		ImageIcon buttonIcon = createImageIcon("indicator_18.gif");
		this.jRunQueryButton.setIcon(buttonIcon);
		this.jRunQueryButton.setText("         Cancel    ");
		// final Color defaultcolor = jRunQueryButton.getBackground();
		dataModel.specificity(0);
		if (jTimingPanel.selectedTimingIndex() == 1) {
			dataModel.timing("ANY");
		} else if (jTimingPanel.selectedTimingIndex() == 2) {
			dataModel.timing("SAMEVISIT");
		} else {
			dataModel.timing("SAMEINSTANCENUM");
		}

		String tmp = "";
		if (jGetAllPatientsCheckBox.isSelected()) {
			tmp = dataModel.wirteAllQueryXML(optionList);
		} else {
			tmp = dataModel.wirteQueryXML(optionList);
		}
		final String xmlStr = tmp;

		// parentPanel.lastRequestMessage(xmlStr);
		jSetSizeLabel.setText(" Patient(s) returned:");
		parentPanel.setPatientCount("");
		parentPanel.setRequestText(xmlStr);
		parentPanel.setResponseText("Waiting for response ...");
		// System.out.println("Query request: "+xmlStr);
		// jNameTextField.setText(queryName);
		jNameLabel.setText(" Query Name: " + queryName);

		queryThread = new Thread() {
			@Override
			public void run() {
				// setCursor(new Cursor(Cursor.WAIT_CURSOR));
				response = QueryRequestClient.sendQueryRequestREST(xmlStr);
				// parentPanel.lastResponseMessage(response);
				if (response != null) {
					// response =
					// response.substring(response.indexOf("<ns2:response"),
					// response.indexOf("</i2b2:response>"));
					parentPanel.setResponseText(response);
					JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();

					try {
						JAXBElement jaxbElement = jaxbUtil
								.unMashallFromString(response);
						ResponseMessageType messageType = (ResponseMessageType) jaxbElement
								.getValue();
						BodyType bt = messageType.getMessageBody();
						MasterInstanceResultResponseType masterInstanceResultResponseType = (MasterInstanceResultResponseType) new JAXBUnWrapHelper()
								.getObjectByClass(bt.getAny(),
										MasterInstanceResultResponseType.class);
						if(masterInstanceResultResponseType.getStatus().getCondition().get(0).getType().equalsIgnoreCase("error")) {
							JOptionPane
									.showMessageDialog(
											parentPanel,
											"Error message delivered from the remote server, "
													+ "you may wish to retry your last action");
							
							jSetSizeLabel.setText(" Patient(s) returned: Error");
							jRunQueryButton.setIcon(null);
							if (jGetAllPatientsCheckBox.isSelected()) {
								jRunQueryButton.setText("Get All Patients");
							} else {
								jRunQueryButton.setText("Run Query Above");
							}
							return;
						}
						
						String queryId = null;
						// ResponseMessageType messageType =
						// jaxbUtil.unMashallResponseMessageTypeFromString(
						// response);
						StatusType statusType = messageType.getResponseHeader()
								.getResultStatus().getStatus();
						String status = statusType.getType();
						String count = "N/A";
						QueryMasterData nameNode = null;
						QueryInstanceData instanceNode = null;
						if (status.equalsIgnoreCase("DONE")) {
							String refId = null;
							try {
								edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.StatusType cellStatusType = masterInstanceResultResponseType
										.getStatus();
								if (cellStatusType.getCondition().get(0)
										.getType().equalsIgnoreCase("RUNNING")) {
									JOptionPane
											.showMessageDialog(
													parentPanel,
													"Query is still running, you may check its status later \n"
															+ "in the previous queries view by right clicking on a node\n"
															+ "then selecting refresh all.");
									jRunQueryButton.setIcon(null);
									if (jGetAllPatientsCheckBox.isSelected()) {
										jRunQueryButton
												.setText("Get All Patients");
									} else {
										jRunQueryButton
												.setText("Run Query Above");
									}
									return;
								} else if (cellStatusType.getCondition().get(0)
										.getType().equalsIgnoreCase("ERROR")) {
									JOptionPane
											.showMessageDialog(
													parentPanel,
													"Error message delivered from the remote server, "
															+ "you may wish to retry your last action");
									jRunQueryButton.setIcon(null);
									jSetSizeLabel.setText(" Patient(s) returned: Error");
									if (jGetAllPatientsCheckBox.isSelected()) {
										jRunQueryButton
												.setText("Get All Patients");
									} else {
										jRunQueryButton
												.setText("Run Query Above");
									}
									return;
								}

								boolean obfsc = false;
								queryId = new Integer(
										masterInstanceResultResponseType
												.getQueryMaster()
												.getQueryMasterId()).toString();
								// messageType.getResponseHeader().getInfo().
								// getValue();
								// System.out.println("Get query id: "+queryId);
								nameNode = new QueryMasterData();    
								nameNode.name(queryName);
								nameNode.visualAttribute("CA");
								nameNode.userId(UserInfoBean.getInstance()
										.getUserName());
								nameNode.tooltip("A query run by "
										+ nameNode.userId());
								nameNode.id(queryId);
								// nameNode.xmlContent(xmlStr);

								// /loop thru all the results
								for (int i = 0; i < masterInstanceResultResponseType
										.getQueryResultInstance().size(); i++) {
									QueryResultInstanceType queryResultInstanceType = masterInstanceResultResponseType
											.getQueryResultInstance().get(i);
									instanceNode = new QueryInstanceData();
									instanceNode.id(queryResultInstanceType
											.getQueryInstanceId());

									//if (queryResultInstanceType
											//.getQueryResultType().getName()
											//.equalsIgnoreCase("patientset")
											//|| queryResultInstanceType
													//.getQueryResultType()
													//.getName()
													//.equalsIgnoreCase(
															//"patient_count_xml")) {
										//refId = new Integer(
												//queryResultInstanceType
												//		.getResultInstanceId())
												//.toString();
										// System.out.println("Set Ref id: "+
										// refId);
										count = new Integer(
												queryResultInstanceType
														.getSetSize())
												.toString();
										if ((queryResultInstanceType
												.getObfuscateMethod() != null)
												&& (queryResultInstanceType
														.getObfuscateMethod()
														.equalsIgnoreCase(
																"OBTOTAL") || queryResultInstanceType
														.getObfuscateMethod()
														.equalsIgnoreCase(
																"OBSUBTOTAL"))) {
											obfsc = true;
										}
									//}

									if (queryResultInstanceType
											.getQueryResultType().getName()
											.equalsIgnoreCase("patientset")) {
										refId = new Integer(
												queryResultInstanceType
														.getResultInstanceId())
												.toString();
										// System.out.println("Set Ref id: "+
										// refId);
										/*count = new Integer(
												queryResultInstanceType
														.getSetSize())
												.toString();
										if ((queryResultInstanceType
												.getObfuscateMethod() != null)
												&& (queryResultInstanceType
														.getObfuscateMethod()
														.equalsIgnoreCase(
																"OBTOTAL") || queryResultInstanceType
														.getObfuscateMethod()
														.equalsIgnoreCase(
																"OBSUBTOTAL"))) {
											obfsc = true;
										}*/										
									}

									/*
									 * else if (queryResultInstanceType
									 * .getQueryResultType().getName()
									 * .equalsIgnoreCase( "patient_count_xml"))
									 * { count = new Integer(
									 * queryResultInstanceType .getSetSize())
									 * .toString();
									 * 
									 * QueryResultData resultData = new
									 * QueryResultData(); resultData
									 * .queryId(queryResultInstanceType
									 * .getResultInstanceId()); String
									 * xmlDocumentRequestStr = resultData
									 * .writeXMLDocumentQueryXML(); System.out .
									 * println
									 * ("Generated XML document request: " +
									 * xmlDocumentRequestStr); //
									 * parentPanel.lastRequestMessage( //
									 * xmlDocumentRequestStr); response =
									 * QueryRequestClient .sendQueryRequestREST
									 * (xmlDocumentRequestStr); System.out
									 * .println
									 * ("Generated XML document response: " +
									 * response); //
									 * parentPanel.lastResponseMessage( //
									 * response); }
									 */
									/*
									 * else if (queryResultInstanceType
									 * .getQueryResultType().getName()
									 * .equalsIgnoreCase(
									 * "PATIENT_AGE_COUNT_XML")) { //count = new
									 * Integer( // queryResultInstanceType //
									 * .getSetSize()) // .toString();
									 * 
									 * QueryResultData resultData = new
									 * QueryResultData(); resultData
									 * .queryId(queryResultInstanceType
									 * .getResultInstanceId()); String
									 * xmlDocumentRequestStr = resultData
									 * .writeXMLDocumentQueryXML(); System.out
									 * .println
									 * ("Generated Age XML document request: " +
									 * xmlDocumentRequestStr); //
									 * parentPanel.lastRequestMessage( //
									 * xmlDocumentRequestStr); response =
									 * QueryRequestClient
									 * .sendQueryRequestREST(xmlDocumentRequestStr
									 * ); System.out
									 * .println("Age XML document response: " +
									 * response); //
									 * parentPanel.lastResponseMessage( //
									 * response); }
									 */
								}
								parentPanel.setPatientCount(count);
								if (count.equalsIgnoreCase("N/A")) {
									jSetSizeLabel
											.setText(" Patient(s) returned: "
													+ count);
								} else {
									if (obfsc) {
										jSetSizeLabel
												.setText(" Patient(s) returned: "
														+ "~" + count);
									} else {
										jSetSizeLabel
												.setText(" Patient(s) returned: "
														+ count);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								JOptionPane
										.showMessageDialog(
												parentPanel,
												"Response delivered from the remote server could not be understood,\n"
														+ "you may wish to retry your last action.");
								jSetSizeLabel.setText(" Patient(s) returned: Error");
								jRunQueryButton.setIcon(null);
								if (jGetAllPatientsCheckBox.isSelected()) {
									jRunQueryButton.setText("Get All Patients");
								} else {
									jRunQueryButton.setText("Run Query Above");
								}
								return;
							}

							// setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							jRunQueryButton.setIcon(null);
							if (jGetAllPatientsCheckBox.isSelected()) {
								jRunQueryButton.setText("Get All Patients");
							} else {
								jRunQueryButton.setText("Run Query Above");
							}
							// jRunQueryButton.setBackground(defaultcolor);

							final IWorkbenchPage page = ((QueryToolInvestigatorPanel) parentPanel).parentview
									.getViewSite().getPage();
							ViewPart previousqueryview = (ViewPart) page
									.findView("edu.harvard.i2b2.eclipse.plugins.previousquery.views.PreviousQueryView");
							((ICommonMethod) previousqueryview)
									.doSomething(nameNode.name() + " ["
											+ dataModel.getDayString() + "]"
											+ "#i2b2seperater#" + nameNode.id());

							/*
							 * final ArrayList<String> nodeXmls = new
							 * ArrayList<String>(); for (int i = 0; i <
							 * dataModel .getCurrentPanelCount(); i++) {
							 * ArrayList<QueryConceptTreeNodeData> nodelist =
							 * dataModel .getTreePanel(i).getItems(); for (int j
							 * = 0; j < nodelist.size(); j++) {
							 * QueryConceptTreeNodeData nodedata = nodelist
							 * .get(j); String termStatus = nodedata
							 * .setXmlContent(); if
							 * (termStatus.equalsIgnoreCase("error")) {
							 * JOptionPane .showMessageDialog( parentPanel,
							 * "Response delivered from the remote server could not be understood,\n"
							 * + "you may wish to retry your last action.");
							 * jRunQueryButton.setIcon(null); if
							 * (jGetAllPatientsCheckBox .isSelected()) {
							 * jRunQueryButton .setText("Get All Patients"); }
							 * else { jRunQueryButton
							 * .setText("Run Query Above"); } return; }
							 * nodeXmls.add(nodedata.xmlContent()); } }
							 */
							ViewPart explorerview = null;
							if (timeline) {
								try {
									explorerview = (ViewPart) page
											.findView("edu.harvard.i2b2.eclipse.plugins.explorer.views.ExplorerView");
								} catch (Exception e) {
									if (jOptionsPanel.hasGraphicAnalysis()) {
										ViewPart analysisview = (ViewPart) page
												.findView("edu.harvard.i2b2.eclipse.plugins.analysis.views.AnalysisView");
										((ICommonMethod) analysisview)
												.doSomething(nameNode.name()
														+ " ["
														+ dataModel
																.getDayString()
														+ "]"
														+ "#i2b2seperater#"
														+ instanceNode.id());
									}
								}
							}
							if (explorerview != null) {
								// &&
								// timeline){//jShowTimelineCheckBox.isSelected())
								// {
								String str1 = "" + count;
								String str2 = "-" + refId;
								((ICommonMethod) explorerview).doSomething(str1
										+ str2);
								((ICommonMethod) explorerview)
										.processQuery(nameNode.id());
								// .doSomething(nameNode);//nodeXmls);
							}

							if (jOptionsPanel.hasGraphicAnalysis()) {
								ViewPart analysisview = (ViewPart) page
										.findView("edu.harvard.i2b2.eclipse.plugins.analysis.views.AnalysisView");
								((ICommonMethod) analysisview)
										.doSomething(nameNode.name() + " ["
												+ dataModel.getDayString()
												+ "]" + "#i2b2seperater#"
												+ instanceNode.id());
							}

						} else {
							if (statusType.getValue().startsWith("LOCKEDOUT")) {
								JOptionPane
										.showMessageDialog(
												parentPanel,
												"Unable to process the query because your account has been locked out,\n"
														+ "please contact your administrator.");
								// + "you may wish to retry your last action");
							} else {
								JOptionPane
										.showMessageDialog(
												parentPanel,
												"Error message delivered from the remote server, "
														+ "you may wish to retry your last action");
							}
							jRunQueryButton.setIcon(null);
							if (jGetAllPatientsCheckBox.isSelected()) {
								jRunQueryButton.setText("Get All Patients");
							} else {
								jRunQueryButton.setText("Run Query Above");
							}
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
						// JOptionPane.showMessageDialog(parentPanel,
						// "Response delivered from the remote server could not
						// be understood,\n" +
						// "you may wish to retry your last action.");

						jRunQueryButton.setIcon(null);
						if (jGetAllPatientsCheckBox.isSelected()) {
							jRunQueryButton.setText("Get All Patients");
						} else {
							jRunQueryButton.setText("Run Query Above");
						}
						return;
					}
				}

			}
		};

		try {
			queryThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			parentPanel.setResponseText(e.getMessage());
		}
	}

	private void jClearGroupsButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		reset();
	}

	public void reset() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// jNameTextField.setText("");
				jNameLabel.setText(" Query Name:");
				jSetSizeLabel.setText(" Patient(s) returned:");
				jTimingPanel.enableSameInstanceRadioButton(false);
				jTimingPanel.reset();
				jTimingPanel.repaint();

				dataModel.clearConceptTrees();
				dataModel.removeAdditionalPanels();
				dataModel.lastLabelPosition(555);
				resetAddPanelButton();
				jPanel1.setPreferredSize(new Dimension(615, 150));
				jScrollPane4.setViewportView(jPanel1);
			}
		});
	}

	public void setSame(final int index) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// jNameTextField.setText("");
				jTimingPanel.setSame(index);
				jTimingPanel.repaint();
			}
		});
	}

	private void jAndOrLabel2MouseClicked(java.awt.event.MouseEvent evt) {
		if (jAndOrLabel2.getText().equalsIgnoreCase("and")) {
			jAndOrLabel2.setText("or");
		} else if (jAndOrLabel2.getText().equalsIgnoreCase("or")) {
			jAndOrLabel2.setText("and");
		}
	}

	private void jAndOrLabelMouseClicked(java.awt.event.MouseEvent evt) {
		JLabel label = (JLabel) evt.getSource();
		if (label.getText().equalsIgnoreCase("and")) {
			label.setText("or");
		} else if (label.getText().equalsIgnoreCase("or")) {
			label.setText("and");
		}
	}

	private void jAndOrLabel1MouseClicked(java.awt.event.MouseEvent evt) {
		if (jAndOrLabel1.getText().equalsIgnoreCase("and")) {
			jAndOrLabel1.setText("or");
		} else if (jAndOrLabel1.getText().equalsIgnoreCase("or")) {
			jAndOrLabel1.setText("and");
		}
	}

	private void resetAddPanelButton() {
		int c = dataModel.getCurrentPanelCount();
		int height = jMorePanelsButton.getBounds().height;
		jMorePanelsButton.setBounds((c - 1) * 185 + 180, 0, 60, height);
	}

	private void jMorePanelsButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (dataModel.hasEmptyPanels()) {
			JOptionPane
					.showMessageDialog(this,
							"Please use an existing empty panel before adding a new one.");
			return;
		}
		int rightmostPosition = dataModel.lastLabelPosition();
		JLabel label = new JLabel();
		label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		label.setText("and");
		label.setToolTipText("Click to change the relationship");
		label.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		label.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jAndOrLabelMouseClicked(evt);
			}
		});

		GroupPanel panel = new GroupPanel("Group "
				+ (dataModel.getCurrentPanelCount() + 1), this);
		jPanel1.add(panel);
		panel.setBounds(rightmostPosition + 5, 0, 180,
				getParent().getHeight() - 100);
		jPanel1.setPreferredSize(new Dimension(
				rightmostPosition + 5 + 181 + 60, getHeight() - 100));
		jScrollPane4.setViewportView(jPanel1);

		dataModel.addPanel(panel, label, rightmostPosition + 5 + 180);

		/*
		 * System.out.println(jScrollPane4.getViewport().getExtentSize().width+":"
		 * + jScrollPane4.getViewport().getExtentSize().height);
		 * System.out.println
		 * (jScrollPane4.getHorizontalScrollBar().getVisibleRect().width+":"
		 * +jScrollPane4.getHorizontalScrollBar().getVisibleRect().height);
		 * System
		 * .out.println(jScrollPane4.getHorizontalScrollBar().getVisibleAmount
		 * ());
		 * System.out.println(jScrollPane4.getHorizontalScrollBar().getValue());
		 */
		jScrollPane4.getHorizontalScrollBar().setValue(
				jScrollPane4.getHorizontalScrollBar().getMaximum());
		jScrollPane4.getHorizontalScrollBar().setUnitIncrement(40);
		// this.jScrollPane4.removeAll();
		// this.jScrollPane4.setViewportView(jPanel1);
		// revalidate();
		// jScrollPane3.setBounds(420, 0, 170, 300);   
		// jScrollPane4.setBounds(20, 35, 335, 220);
		resizePanels(getParent().getWidth(), getParent().getHeight());
	}

	private void resizePanels(int width, int height) {
		jScrollPane4.setBounds(5, 35, width - 170, height - 65);
		// jPanel1.setPreferredSize(new Dimension(dataModel.lastLabelPosition(),
		// height-85));
		jScrollPane4.setViewportView(jPanel1);

		jOptionsPanel.setBounds(width - 162, 28, 158, height / 2 - 10);
		jTimingPanel.setBounds(width - 162, 41 + height / 2, 158,
				height / 2 - 71);
		// jOptionsScrollPane.setViewportView(jOptionsPanel);
		jGetAllPatientsCheckBox.setBounds(5, height - 24, 105, 23);
		jRunQueryButton.setBounds(115, height - 24, width - 280, 23);
		// jNameTextField.setBounds(5, 4, width-121, 26);
		jNameLabel.setBounds(6, 6, width - 200, 23);
		// jQueryNamePanel.setBounds(5, 4, width-121, 26);
		jClearGroupsButton.setBounds(width - 186, 6, 18, 23);
		jAnalysisLabel.setBounds(width - 162, 6, 158, 23);
		jTimingLabel.setBounds(width - 162, 20 + height / 2, 158, 21);
		// jCancelButton.setBounds(5, height-49, 80, 23);
		jSetSizeLabel.setBounds(width - 160, height - 23, 155, 21);
		// jSetSizeFiled.setBounds(156, height-24, 80, 21);
		// jNameLabel.setBounds(8, 2, 70, 23);
		// jToolbarPanel.setBounds(240, height-23, 380, 21);

		int c = dataModel.getCurrentPanelCount();
		jMorePanelsButton.setBounds((c - 1) * 185 + 180, 0, 60, height - 85);

		for (int i = 0; i < c; i++) {
			GroupPanel panel = dataModel.getTreePanel(i);
			panel.setBounds((i * 180) + (i * 5), 0, 180, height - 85);
			panel.invalidate();
		}
	}

	public void addPanel() {
		int rightmostPosition = dataModel.lastLabelPosition();
		JLabel label = new JLabel();
		label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		label.setText("and");
		label.setToolTipText("Click to change the relationship");
		label.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		label.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jAndOrLabelMouseClicked(evt);
			}
		});

		// jPanel1.add(label);
		// label.setBounds(rightmostPosition, 90, 30, 18);

		GroupPanel panel = new GroupPanel("Group "
				+ (dataModel.getCurrentPanelCount() + 1), this);
		jPanel1.add(panel);
		panel.setBounds(rightmostPosition + 5, 0, 180,
				getParent().getHeight() - 100);
		jPanel1.setPreferredSize(new Dimension(
				rightmostPosition + 5 + 181 + 60, getHeight() - 100));
		jScrollPane4.setViewportView(jPanel1);

		dataModel.addPanel(panel, label, rightmostPosition + 5 + 180);

		jScrollPane4.getHorizontalScrollBar().setValue(
				jScrollPane4.getHorizontalScrollBar().getMaximum());
		jScrollPane4.getHorizontalScrollBar().setUnitIncrement(40);
		resizePanels(getParent().getWidth(), getParent().getHeight());
	}

	private void jNameLabelMouseExited(java.awt.event.MouseEvent evt) {
		jNameLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
	}

	private void jNameLabelMouseMoved(java.awt.event.MouseEvent evt) {
		jNameLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(Color.YELLOW));
		jNameLabel.paintImmediately(jNameLabel.getVisibleRect());

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}

		jNameLabel.setBorder(javax.swing.BorderFactory
				.createLineBorder(Color.BLACK));
	}

	public void setQueryName(String str) {
		// jNameTextField.setText(str);
		jNameLabel.setText(str);
	}

	class QueryDataTransferable implements Transferable {
		public QueryDataTransferable(Object data) {
			super();
			this.data = data;
			flavors[0] = DataFlavor.stringFlavor;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return true;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			return data;
		}

		private Object data;
		private final DataFlavor[] flavors = new DataFlavor[1];
	}

	class NameLabelTextHandler extends TransferHandler {
		public NameLabelTextHandler() {
			super("text");
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavor) {
			jNameLabel.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.YELLOW));
			jNameLabel.paintImmediately(jNameLabel.getVisibleRect());

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			jNameLabel.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.BLACK));

			if ((System.currentTimeMillis() - lEventTime) > 2000) {

				return true;
			}
			return false;
		}

		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
		}

		public boolean importData(JComponent comp, Transferable t) {
			jNameLabel.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(0, 0, 0)));

			try {
				String text = (String) t
						.getTransferData(DataFlavor.stringFlavor);
				System.out.println(text);
				reset();
				dataModel.redrawPanelFromXml(text);
			} catch (Exception e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						JOptionPane
								.showMessageDialog(jNameLabel,
										"Please note, You can not drop this item here.");
					}
				});
			}

			return true;
		}

		protected Transferable createTransferable(JComponent c) {

			// Transferable t = null;
			String str = jNameLabel.getText();

			// t = new QueryDataTransferable(str);
			// return t;
			// return new StringSelection(str);

			Transferable t = null;

			QueryDefinitionType queryDefinitionType = new QueryDefinitionType();

			QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();
			for (int i = 0; i < jPanel1.getComponentCount() - 1; i++) {
				if (jPanel1.getComponent(i) instanceof GroupPanel) {
					GroupPanel panel = (GroupPanel) jPanel1.getComponent(i);// getTreePanel(i);

					if ((panel != null) && (panel.data().getItems().size() > 0)) {
						queryDefinitionType.setQueryName(jNameLabel.getText()
								.replace(" Query Name: ", ""));
						// queryDefinitionType.setQueryName(panel.data()
						// .getItems().get(0).name());
						// + "_" + generateMessageId().substring(0, 4));
						ArrayList<QueryConceptTreeNodeData> nodelist = panel
								.data().getItems();
						if ((nodelist != null) && (nodelist.size() > 0)) {
							// System.out.println("Panel: "+panel.getGroupName()+
							// " Excluded:
							// "+((panel.data().exclude())?"yes":"no"));
							PanelType panelType = new PanelType();
							panelType.setInvert((panel.data().exclude()) ? 1
									: 0);
							PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();
							totalOccurrences.setValue(panel
									.getOccurrenceTimes());
							panelType.setTotalItemOccurrences(totalOccurrences);
							panelType.setPanelNumber(i + 1);

							for (int j = 0; j < nodelist.size(); j++) {
								QueryConceptTreeNodeData node = nodelist.get(j);
								// System.out.println("\tItem: "+node.fullname())
								// ;

								// create item
								ItemType itemType = new ItemType();

								itemType.setItemKey(node.fullname());
								itemType.setItemName(node.name());
								// mm removed
								// itemType.setItemTable(node.lookuptable());
								itemType.setTooltip(node.tooltip());
								itemType.setHlevel(Integer.parseInt(node
										.hlevel()));
								itemType.setClazz("ENC");

								// handle time constrain
								if (panel.data().startTime() != -1
										|| panel.data().endTime() != -1) {
									ConstrainByDate timeConstrain = panel
											.data().writeTimeConstrain();
									itemType.getConstrainByDate().add(
											timeConstrain);
								}

								// handle value constrain
								if (!node.valuePropertyData().noValue()) {
									ConstrainByValue valueConstrain = node
											.valuePropertyData()
											.writeValueConstrain();
									itemType.getConstrainByValue().add(
											valueConstrain);
								}

								panelType.getItem().add(itemType);
							}
							queryDefinitionType.getPanel().add(panelType);

						}
					}
				}

			}
			StringWriter strWriter = new StringWriter();

			try {

				DndType dnd = new DndType();

				edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();

				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
				// psmOf = new
				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
				dnd.getAny().add(
						psmOf.createQueryDefinition(queryDefinitionType));

				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
				QueryJAXBUtil.getJAXBUtil().marshaller(
						of.createPluginDragDrop(dnd), strWriter);
				str = strWriter.toString();

			} catch (Exception e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										jNameLabel,
										"You can not use this item in a query, "
												+ "it is only used for organizing the lists.");
					}
				});
			}

			// edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory vdoOf = new
			// edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
			/*
			 * //edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
			 * psmOf = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
			 * dnd.getAny().add(vdoOf.c.c(queryDefinitionType));
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
			 * QueryJAXBUtil.getJAXBUtil().marshaller(
			 * of.createPluginDragDrop(dnd), strWriter);
			 */

			t = new QueryDataTransferable(str);
			return t;
		}
	}

	private class DragMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);

			jNameLabel.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(0, 0, 0)));

			// reading the system time to a long
			lEventTime = System.currentTimeMillis();
		}
	}

	private String generateMessageId() {
		StringWriter strWriter = new StringWriter();
		for (int i = 0; i < 20; i++) {
			int num = getValidAcsiiValue();
			// System.out.println("Generated number: " + num + " char:
			// "+(char)num);
			strWriter.append((char) num);
		}
		return strWriter.toString();
	}

	private int getValidAcsiiValue() {
		int number = 48;
		while (true) {
			number = 48 + (int) Math.round(Math.random() * 74);
			if ((number > 47 && number < 58) || (number > 64 && number < 91)
					|| (number > 96 && number < 123)) {
				break;
			}
		}
		return number;
	}
	
	public void enableSameInstanceVisit(boolean b) {
		jTimingPanel.enableSameInstanceRadioButton(b);
	}

	// Variables declaration
	private javax.swing.JLabel jAnalysisLabel;
	private javax.swing.JLabel jTimingLabel;
	private javax.swing.JLabel jAndOrLabel1;
	private javax.swing.JLabel jAndOrLabel2;
	private javax.swing.JLabel jNameLabel;
	private javax.swing.JLabel jSetSizeLabel;
	private javax.swing.JTextField jNameTextField;
	private javax.swing.JLabel jSetSizeFiled;
	private javax.swing.JButton jDeleteButton;
	private javax.swing.JButton jMorePanelsButton;
	private javax.swing.JButton jClearGroupsButton;
	private javax.swing.JPanel jPanel1;
	public javax.swing.JButton jCancelButton;
	private javax.swing.JButton jRunQueryButton;
	private GroupPanel jScrollPane1;
	private GroupPanel jScrollPane2;
	private GroupPanel jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JCheckBox jGetAllPatientsCheckBox;

	// private javax.swing.JCheckBox jGetPatientCountCheckBox;
	// private javax.swing.JCheckBox jGetPatientSetCheckBox;
	private AnalysisPanel jOptionsPanel;
	private TimingPanel jTimingPanel;
	// private javax.swing.JScrollPane jOptionsScrollPane;
	private javax.swing.JCheckBox jShowTimelineCheckBox;

	private javax.swing.JToggleButton jPatientCountToggleButton;
	private javax.swing.JPanel jQueryNamePanel;
	// private javax.swing.JToggleButton jTimelineToggleButton;
	// private javax.swing.JToggleButton jPatientSetToggleButton;
	private javax.swing.JToolBar jWorkflowToolBar;
	private javax.swing.JPanel jToolbarPanel;

	public javax.swing.JTree jTree1;
	public javax.swing.JTree jTree2;
	public javax.swing.JTree jTree3;
	public GroupPanel treepanel;
	// End of variables declaration
}