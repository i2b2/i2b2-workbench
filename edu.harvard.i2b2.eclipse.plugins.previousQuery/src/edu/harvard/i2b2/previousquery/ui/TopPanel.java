/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

package edu.harvard.i2b2.previousquery.ui;

/*
 * QueryTopPanel.java
 *
 * Created on August 2, 2006, 9:04 AM
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Cursor;
import java.util.ArrayList;

import javax.swing.*;
import javax.xml.bind.JAXBElement;

//import edu.harvard.i2b2.navigator.CRCNavigator;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.previousquery.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.previousquery.data.QueryMasterData;
import edu.harvard.i2b2.previousquery.dataModel.TopPanelModel;
import edu.harvard.i2b2.previousquery.datavo.PreviousQueryJAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.*;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MasterInstanceResultResponseType;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.eclipse.UserInfoBean;

public class TopPanel extends javax.swing.JPanel {

	private QueryPanel parentPanel;
	private TopPanelModel dataModel;
	private String response = null;

	/** Creates new form QueryTopPanel */
	public TopPanel(QueryPanel parent) {
		parentPanel = parent;
		dataModel = parent.dataModel();
		initComponents();
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
		jNameLabel = new javax.swing.JLabel();
		jNameTextField = new javax.swing.JTextField();
		jDeleteButton = new javax.swing.JButton();
		jScrollPane1 = new ConceptTreePanel("Group 1", this);
		jRunQueryButton = new javax.swing.JButton();
		jRemoveAllButton = new javax.swing.JButton();
		jScrollPane2 = new ConceptTreePanel("Group 2", this);
		jScrollPane3 = new ConceptTreePanel("Group 3", this);
		jScrollPane4 = new javax.swing.JScrollPane();
		jPanel1 = new javax.swing.JPanel();
		// jVisitComboBox = new javax.swing.JComboBox();
		jAndOrLabel1 = new javax.swing.JLabel();
		jAndOrLabel2 = new javax.swing.JLabel();
		jMorePanelsButton = new javax.swing.JButton();
		// jSlider1 = new javax.swing.JSlider();
		// jLabel1 = new javax.swing.JLabel();
		// jLabel2 = new javax.swing.JLabel();

		setLayout(null);

		// jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.
		// ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jPanel1.setLayout(null);

		jPanel1.add(jScrollPane1);
		jScrollPane1.setBounds(0, 0, 190, 200);

		jPanel1.add(jScrollPane2);
		jScrollPane2.setBounds(220, 0, 190, 200);

		jPanel1.add(jScrollPane3);
		jScrollPane3.setBounds(440, 0, 190, 200);

		// jAndOrLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
		// ;
		jAndOrLabel1.setText("and");
		// jAndOrLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder())
		// ;
		jPanel1.add(jAndOrLabel1);
		jAndOrLabel1.setBounds(190, 90, 30, 18);

		// jAndOrLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)
		// ;
		jAndOrLabel2.setText("and");
		// jAndOrLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder())
		// ;
		jPanel1.add(jAndOrLabel2);
		jAndOrLabel2.setBounds(410, 90, 30, 18);

		jNameLabel.setText("Query Name: ");
		jNameLabel.setBounds(25, 10, 70, 23);
		add(jNameLabel);

		jNameTextField.setText("");
		jNameTextField.setBounds(95, 10, 560, 20);
		add(jNameTextField);

		jScrollPane4.setViewportView(jPanel1);
		add(jScrollPane4);
		jScrollPane4.setBounds(20, 35, 635, 220);

		jRemoveAllButton.setText("Reset");
		jRemoveAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRemoveAllButtonActionPerformed(evt);
			}
		});
		add(jRemoveAllButton);
		jRemoveAllButton.setBounds(20, 255, 70, 23);

		jRunQueryButton.setText("Run Query");
		jRunQueryButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRunQueryButtonActionPerformed(evt);
			}
		});
		add(jRunQueryButton);
		jRunQueryButton.setBounds(90, 255, 625, 23);

		jMorePanelsButton.setText("<html><center>Add<br>" + "<left>Group");
		jMorePanelsButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jMorePanelsButtonActionPerformed(evt);
					}
				});
		add(jMorePanelsButton);
		jMorePanelsButton.setBounds(655, 35, 60, 220);

		/*
		 * jDeleteButton.setText("Delete"); jDeleteButton.addActionListener(new
		 * java.awt.event.ActionListener() { public void
		 * actionPerformed(java.awt.event.ActionEvent evt) {
		 * jDeleteButtonActionPerformed(evt); } });
		 * 
		 * jVisitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new
		 * String[] { "Groups don't have to occur in the same visit",
		 * "Groups must all occur in the same visit" })); add(jVisitComboBox);
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
		 * jRemoveAllButton.setText("Remove All");
		 * jRemoveAllButton.addActionListener(new
		 * java.awt.event.ActionListener() { public void
		 * actionPerformed(java.awt.event.ActionEvent evt) {
		 * jRemoveAllButtonActionPerformed(evt); } });
		 * 
		 * add(jRemoveAllButton); jRemoveAllButton.setBounds(115, 10, 90, 23);
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

		jPanel1.add(jAndOrLabel1);

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

		jPanel1.add(jAndOrLabel2);

		/*
		 * jPanel1.add(jScrollPane3); jScrollPane3.setBounds(420, 0, 170, 350);
		 * 
		 * jScrollPane4.setViewportView(jPanel1);
		 * 
		 * add(jScrollPane4); jScrollPane4.setBounds(20, 70, 594, 370);
		 * 
		 * jMorePanelsButton.setText("Add Panel");
		 * jMorePanelsButton.addActionListener(new
		 * java.awt.event.ActionListener() { public void
		 * actionPerformed(java.awt.event.ActionEvent evt) {
		 * jMorePanelsButtonActionPerformed(evt); } });
		 * 
		 * add(jMorePanelsButton); jMorePanelsButton.setBounds(215, 10, 90, 23);
		 */

		// jTree1.addTreeWillExpandListener(this);
		// jTree1.addTreeExpansionListener(this);
		// jScrollPane1.setViewportView(new QueryConceptTreePanel("Group 1"));
		// jScrollPane1.setToolTipText(
		// "Double click on a folder to view the items inside");
		// jTree2.addTreeExpansionListener(this);
		// jScrollPane2.setViewportView(new QueryConceptTreePanel("Group 2"));
		// jTree3.addTreeExpansionListener(this);
		// treepanel = new QueryConceptTreePanel("", this);
		// jScrollPane3.setViewportView(new QueryConceptTreePanel("Group 3"));
		// jSlider1.setMajorTickSpacing(20);
		/*
		 * jSlider1.setPaintTicks(true); jSlider1.setValue(0);
		 * jSlider1.setMinorTickSpacing(10);
		 * jSlider1.setToolTipText("Slider on left is more Sensitive Query, " +
		 * "on right is more Specific"); add(jSlider1); jSlider1.setBounds(380,
		 * 40, 140, 18);
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
		dataModel.addPanel(jScrollPane3, jAndOrLabel2, 630);
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
		java.net.URL imgURL = TopPanel.class.getResource(path);
		return new ImageIcon(imgURL);
	}

	private void jRunQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// System.out.println("value set on the slider: "+ jSlider1.getValue());
		if (dataModel.isAllPanelEmpty()) {
			JOptionPane.showMessageDialog(this, "All groups are empty.");
			return;
		}

		String queryNametmp = jNameTextField.getText();
		if (queryNametmp.equals("") || queryNametmp == null) {
			queryNametmp = dataModel.getTmpQueryName();
		}
		Object selectedValue = JOptionPane.showInputDialog(this,
				"Please supply a name for this query: ", "Query Name Dialog",
				JOptionPane.PLAIN_MESSAGE, null, null, queryNametmp);

		if (selectedValue == null) {
			return;
		} else {
			queryNametmp = (String) selectedValue;
		}

		dataModel.queryName(queryNametmp);
		final String queryName = queryNametmp;
		System.out.println("Provided query name: " + queryName);

		ImageIcon buttonIcon = createImageIcon("indicator_18.gif");
		this.jRunQueryButton.setIcon(buttonIcon);
		this.jRunQueryButton.setText("         Running ......");
		final Color defaultcolor = jRunQueryButton.getBackground();
		// this.jRunQueryButton.setBackground(Color.green);

		dataModel.specificity(0);// jSlider1.getValue());
		final String xmlStr = dataModel.wirteQueryXML();
		parentPanel.setPatientCount("");
		parentPanel.setRequestText(xmlStr);
		parentPanel.setResponseText("Waiting for response ...");
		System.out.println("Query request: " + xmlStr);
		jNameTextField.setText(queryName);

		Thread queryThread = new Thread() {
			@Override
			public void run() {
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				// response = QueryRequestClient.sendQueryRequest(xmlStr);
				if (response != null) {
					// response =
					// response.substring(response.indexOf("<ns2:response"),
					// response.indexOf("</i2b2:response>"));
					parentPanel.setResponseText(response);
					JAXBUtil jaxbUtil = PreviousQueryJAXBUtil.getJAXBUtil();

					try {
						JAXBElement jaxbElement = jaxbUtil
								.unMashallFromString(response);
						ResponseMessageType messageType = (ResponseMessageType) jaxbElement
								.getValue();
						BodyType bt = messageType.getMessageBody();
						MasterInstanceResultResponseType masterInstanceResultResponseType = (MasterInstanceResultResponseType) new JAXBUnWrapHelper()
								.getObjectByClass(bt.getAny(),
										MasterInstanceResultResponseType.class);
						String queryId = null;
						// ResponseMessageType messageType =
						// jaxbUtil.unMashallResponseMessageTypeFromString
						// (response);
						StatusType statusType = messageType.getResponseHeader()
								.getResultStatus().getStatus();
						String status = statusType.getType();
						queryId = new Integer(masterInstanceResultResponseType
								.getQueryMaster().getQueryMasterId())
								.toString();// messageType.getResponseHeader().
						// getInfo().getValue();
						System.out.println("Get query id: " + queryId);

						QueryMasterData nameNode = new QueryMasterData();
						nameNode.name(queryName);
						nameNode.visualAttribute("CA");
						nameNode.userId(UserInfoBean.getInstance()
								.getUserName());
						nameNode.tooltip("A query run by " + nameNode.userId());
						nameNode.id(queryId);
						// nameNode.xmlContent(xmlStr);

						// CRCNavigator.APP.runTreePanel.addNode(nameNode);
						parentPanel.getParentC().runTreePanel().addNode(
								nameNode);
						// CRCNavigator.APP.previousQueries.add(nameNode);
						// if(CRCNavigator.APP.explorer != null) {
						// CRCNavigator.APP.explorer.runTreePanel().addNode(
						// nameNode);
						// }

						String count = "";
						if (status.equalsIgnoreCase("DONE")) {
							QueryResultInstanceType queryResultInstanceType = masterInstanceResultResponseType
									.getQueryResultInstance().get(0);
							String refId = new Integer(queryResultInstanceType
									.getResultInstanceId()).toString();
							System.out.println("Set Ref id: " + refId);
							count = new Integer(queryResultInstanceType
									.getSetSize()).toString();
							parentPanel.setPatientCount(count);

							// if(parentPanel.getParentC().bottomC() != null) {
							// parentPanel.getParentC().bottomC().
							// setPatientSetText
							// ("Patient Set: "+count+" Patients");
							// parentPanel.getParentC().bottomC().
							// setPatientMinNumText("0");
							// parentPanel.getParentC().bottomC().patientRefId(
							// refId);
							// }

							ArrayList<String> nodeXmls = new ArrayList<String>();
							for (int i = 0; i < dataModel
									.getCurrentPanelCount(); i++) {
								ArrayList<QueryConceptTreeNodeData> nodelist = dataModel
										.getTreePanel(i).getItems();
								for (int j = 0; j < nodelist.size(); j++) {
									QueryConceptTreeNodeData nodedata = nodelist
											.get(j);
									nodeXmls.add(nodedata.xmlContent());
								}
							}

							// if(parentPanel.getParentC().bottomC() != null) {
							// parentPanel.getParentC().bottomC().populateTable(
							// nodeXmls);
							// parentPanel.getParentC().bottomC().generateTimeLine
							// ();
							// }
						} else {
							JOptionPane
									.showMessageDialog(
											parentPanel,
											"Query is still running, you may check the result later using the previous query panel.");
							parentPanel.setPatientCount(status);
						}
					} catch (Exception e) {
						e.printStackTrace();
						parentPanel.setResponseText(e.getMessage());
					}
				}
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				jRunQueryButton.setIcon(null);
				jRunQueryButton.setText("Run Query");
				jRunQueryButton.setBackground(defaultcolor);
			}
		};

		try {
			queryThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			parentPanel.setResponseText(e.getMessage());
		}
	}

	private void jRemoveAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
		reset();
	}

	public void reset() {
		jNameTextField.setText("");

		dataModel.clearConceptTrees();
		dataModel.removeAdditionalPanels();
		dataModel.lastLabelPosition(630);

		jPanel1.setPreferredSize(new Dimension(690, 150));
		jScrollPane4.setViewportView(jPanel1);
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

		jPanel1.add(label);
		label.setBounds(rightmostPosition, 90, 30, 18);

		ConceptTreePanel panel = new ConceptTreePanel("Group "
				+ (dataModel.getCurrentPanelCount() + 1), this);
		jPanel1.add(panel);
		panel.setBounds(rightmostPosition + 30, 0, 190, 200);
		jPanel1.setPreferredSize(new Dimension(rightmostPosition + 30 + 191,
				150));
		jScrollPane4.setViewportView(jPanel1);

		dataModel.addPanel(panel, label, rightmostPosition + 30 + 190);

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

		jPanel1.add(label);
		label.setBounds(rightmostPosition, 90, 30, 18);

		ConceptTreePanel panel = new ConceptTreePanel("Group "
				+ (dataModel.getCurrentPanelCount() + 1), this);
		jPanel1.add(panel);
		panel.setBounds(rightmostPosition + 30, 0, 190, 200);
		jPanel1.setPreferredSize(new Dimension(rightmostPosition + 30 + 191,
				150));
		jScrollPane4.setViewportView(jPanel1);

		dataModel.addPanel(panel, label, rightmostPosition + 30 + 190);

		jScrollPane4.getHorizontalScrollBar().setValue(
				jScrollPane4.getHorizontalScrollBar().getMaximum());
		jScrollPane4.getHorizontalScrollBar().setUnitIncrement(40);
	}

	public void setQueryName(String str) {
		jNameTextField.setText(str);
	}

	// Variables declaration
	private javax.swing.JLabel jAndOrLabel1;
	private javax.swing.JLabel jAndOrLabel2;
	private javax.swing.JLabel jNameLabel;
	private javax.swing.JTextField jNameTextField;
	private javax.swing.JButton jDeleteButton;
	private javax.swing.JButton jMorePanelsButton;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JButton jRemoveAllButton;
	private javax.swing.JButton jRunQueryButton;
	private ConceptTreePanel jScrollPane1;
	private ConceptTreePanel jScrollPane2;
	private ConceptTreePanel jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	// private javax.swing.JSlider jSlider1;
	// private javax.swing.JComboBox jVisitComboBox;

	public javax.swing.JTree jTree1;
	public javax.swing.JTree jTree2;
	public javax.swing.JTree jTree3;
	public ConceptTreePanel treepanel;
	// End of variables declaration//GEN-END:variables
}
