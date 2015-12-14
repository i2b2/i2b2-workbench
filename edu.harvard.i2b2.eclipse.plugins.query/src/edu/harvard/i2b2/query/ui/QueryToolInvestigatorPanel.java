/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

/**
 *   Class: QueryToolInvestigatorPanel
 */

package edu.harvard.i2b2.query.ui;

import edu.harvard.i2b2.eclipse.plugins.query.views.QueryView;
import edu.harvard.i2b2.query.serviceClient.QueryStatPanel;

public class QueryToolInvestigatorPanel extends QueryToolPanel {

	public QueryView parentview = null;

	public QueryToolInvestigatorPanel(QueryC parentC) {
		super(parentC);
	}

	public QueryToolInvestigatorPanel(QueryView parent) {
		super();
		parentview = parent;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	@Override
	protected void initComponents() {
		jSplitPane1 = new javax.swing.JSplitPane();
		jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane2 = new javax.swing.JScrollPane();
		jScrollPane3 = new javax.swing.JScrollPane();
		jRequestMessageTextArea = new javax.swing.JTextArea();
		jResponseMessageTextArea = new javax.swing.JTextArea();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jStatPanel = new QueryStatPanel();
		jRequestMessagePanel = new javax.swing.JPanel();
		jResponseMessagePanel = new javax.swing.JPanel();
		// jPanel3 = new javax.swing.JPanel();
		// messagePanel = new QueryXMLMessagePanel();

		setLayout(new java.awt.BorderLayout());

		// jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		// jTextArea1.setColumns(20);
		// jTextArea1.setRows(5);
		mainPanel = new MainPanel(this);
		add(mainPanel);
		mainPanel.repaint();
		// jScrollPane1.setViewportView(mainPanel);
		// jSplitPane1.setLeftComponent(jScrollPane1);

		// jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
		// jStatPanel.setName("t1");

		// jPanel1.setLayout(null);//new java.awt.BorderLayout());

		// JTextArea area2 = new JTextArea();
		// jScrollPane2.setViewportView(jTextArea1);
		// jPanel1.add(jScrollPane2);
		// testing
		/*
		 * final JLabel myLabel = new
		 * JLabel("<Concepts><Concept>null</Concept></Concepts>");
		 * jPanel1.add(myLabel); class myTransferHandler extends TransferHandler
		 * { protected myTransferHandler() { // Sets up new TransferHandler to
		 * initialise // the mechanics super("text"); }
		 * 
		 * protected Transferable createTransferable(JComponent c) { // Creates
		 * a new Transferable object // with the correct DataFlavors etc. return
		 * new StringSelection(myLabel.getText()); } }
		 * 
		 * myLabel.setTransferHandler(new myTransferHandler());
		 * 
		 * // Mouse click used as a Drag gesture recogniser MouseListener ml =
		 * new MouseAdapter() { public void mousePressed(MouseEvent e) {
		 * JComponent c = (JComponent)e.getSource(); TransferHandler th =
		 * c.getTransferHandler(); th.exportAsDrag(c, e, TransferHandler.COPY);
		 * } }; myLabel.addMouseListener(ml);
		 */
		// org.jdesktop.layout.GroupLayout jPanel1Layout = new
		// org.jdesktop.layout.GroupLayout(jPanel1);
		// jPanel1.setLayout(jPanel1Layout);
		// jPanel1Layout.setHorizontalGroup(
		// jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.
		// LEADING)
		// .add(0, 613, Short.MAX_VALUE)
		// );
		// jPanel1Layout.setVerticalGroup(
		// jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.
		// LEADING)
		// .add(0, 354, Short.MAX_VALUE)
		// );
		// jTabbedPane1.addTab("tab1", jStatPanel);
		// jRequestMessagePanel.setLayout(new java.awt.BorderLayout());
		// JTextArea area2 = new JTextArea();
		// jScrollPane2.setViewportView(jRequestMessageTextArea);
		// jRequestMessagePanel.add(jScrollPane2);
		// jTabbedPane1.addTab("tab2", jRequestMessagePanel);
		// jResponseMessagePanel.setLayout(new java.awt.BorderLayout());
		// JTextArea area2 = new JTextArea();
		// jScrollPane3.setViewportView(jResponseMessageTextArea);
		// jResponseMessagePanel.add(jScrollPane3);
		// jTabbedPane1.addTab("tab3", jResponseMessagePanel);
		/*
		 * org.jdesktop.layout.GroupLayout jPanel2Layout = new
		 * org.jdesktop.layout.GroupLayout(jPanel2);
		 * jPanel2.setLayout(jPanel2Layout); jPanel2Layout.setHorizontalGroup(
		 * jPanel2Layout
		 * .createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING) .add(0,
		 * 613, Short.MAX_VALUE) ); jPanel2Layout.setVerticalGroup(
		 * jPanel2Layout
		 * .createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING) .add(0,
		 * 354, Short.MAX_VALUE) );
		 */
		// jPanel2.add(jTextArea1);
		// jTabbedPane1.addTab("tab2", jPanel2);
		/*
		 * org.jdesktop.layout.GroupLayout jPanel3Layout = new
		 * org.jdesktop.layout.GroupLayout(jPanel3);
		 * jPanel3.setLayout(jPanel3Layout); jPanel3Layout.setHorizontalGroup(
		 * jPanel3Layout
		 * .createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING) .add(0,
		 * 613, Short.MAX_VALUE) ); jPanel3Layout.setVerticalGroup(
		 * jPanel3Layout
		 * .createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING) .add(0,
		 * 354, Short.MAX_VALUE) );
		 */
		// jPanel3.add(new JTextArea());
		// jTabbedPane1.addTab("tab3", jPanel3);
		// jSplitPane1.setRightComponent(jTabbedPane1);
		// add(jSplitPane1);
		// jSplitPane1.setBounds(0,0,400,600);
	}
}
