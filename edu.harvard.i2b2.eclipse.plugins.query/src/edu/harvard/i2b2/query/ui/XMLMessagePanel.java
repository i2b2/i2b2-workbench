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
 * QueryXMLMessagePanel.java
 *
 * Created on September 19, 2006, 9:46 AM
 */

public class XMLMessagePanel extends javax.swing.JPanel {

	/** Creates new form QueryXMLMessagePanel */
	public XMLMessagePanel() {
		initComponents();
	}

	public void setRequestXMLAreaText(String str) {
		jRequestTextArea.setText(str);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jRequestTextArea = new javax.swing.JTextArea();
		jLabel2 = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		jResponseTextArea = new javax.swing.JTextArea();

		setLayout(null);

		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent evt) {
				formComponentResized(evt);
			}
		});

		jLabel1.setText("Request Message:");
		add(jLabel1);
		jLabel1.setBounds(30, 10, 110, 20);

		jRequestTextArea.setColumns(20);
		jScrollPane1.setViewportView(jRequestTextArea);

		add(jScrollPane1);
		jScrollPane1.setBounds(30, 40, 700, 190);

		jLabel2.setText("Response Message:");
		add(jLabel2);
		jLabel2.setBounds(30, 250, 110, 20);

		jResponseTextArea.setColumns(20);
		jScrollPane2.setViewportView(jResponseTextArea);

		add(jScrollPane2);
		jScrollPane2.setBounds(30, 280, 700, 170);
	}

	private void formComponentResized(java.awt.event.ComponentEvent evt) {
		int height = getHeight() / 2;
		jScrollPane1.setBounds(30, 35, getWidth() - 60, height - 40);
		jLabel2.setBounds(30, height + 5, 110, 20);
		jScrollPane2.setBounds(30, height + 30, getWidth() - 60, getHeight()
				- height - 50);
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JTextArea jRequestTextArea;
	private javax.swing.JTextArea jResponseTextArea;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	// End of variables declaration//GEN-END:variables

}
