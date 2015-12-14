/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */

/*
 * TimelineSearchFrame.java
 *
 * Created on February 28, 2007, 10:09 AM
 */

package edu.harvard.i2b2.explorer.ui;

import edu.harvard.i2b2.timeline.lifelines.Record;


/**
 * 
 * @author wp066
 */
public class TimelineSearchFrame extends javax.swing.JFrame {

	/** Creates new form TimelineSearchFrame */
	public TimelineSearchFrame(Record r) {
		initComponents();
		setSize(300, 120);
		setLocation(500, 50);
		// //jSearchTextField.requestFocus();
		// jSearchButton.requestFocus();
		// jSearchTextField.requestFocus();
		r.requestFocus();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		jSearchTextField = new javax.swing.JTextField();
		jSearchButton = new javax.swing.JButton();
		jCloseButton = new javax.swing.JButton();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Search Timelines");

		java.awt.Image img = this.getToolkit().getImage(
				TimelineSearchFrame.class.getResource("core-cell.gif"));
		this.setIconImage(img);

		jLabel1.setText("Search String: ");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(20, 20, 200, 14);

		// jSearchTextField.setFocusCycleRoot(true);
		jSearchTextField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jSearchTextFieldActionPerformed(evt);
			}
		});

		getContentPane().add(jSearchTextField);
		jSearchTextField.setBounds(20, 50, 180, 23);

		jSearchButton.setText("Search");
		jSearchButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jSearchButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jSearchButton);
		jSearchButton.setBounds(200, 50, 80, 23);

		jCloseButton.setText("Close");
		jCloseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCloseButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jCloseButton);
		jCloseButton.setBounds(200, 18, 80, 23);

		pack();
	}

	private void jCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
		dispose();
	}

	private void jSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {
		MainPanel.theTimeLinePanel.search = true;
		MainPanel.theTimeLinePanel.grep(jSearchTextField.getText());
	}

	private void jSearchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
		MainPanel.theTimeLinePanel.search = true;
		MainPanel.theTimeLinePanel.grep(jSearchTextField.getText());
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TimelineSearchFrame(null).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.JButton jCloseButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JButton jSearchButton;
	private javax.swing.JTextField jSearchTextField;
	// End of variables declaration

}
