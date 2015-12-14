/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 *     Wensong Pan
 */

/*
 * QueryDisplayOptionsDialog.java
 * 
 *
 * Created on February 20, 2007, 10:14 AM
 */

package edu.harvard.i2b2.query.ui;

import edu.harvard.i2b2.eclipse.plugins.query.views.QueryView;

public class DisplayOptionsDialog extends javax.swing.JFrame {

	private QueryView queryView_;

	/**
	 * Creates new form QueryDisplayOptionsDialog
	 */
	public DisplayOptionsDialog(QueryView queryView) {

		queryView_ = queryView;

		initComponents();
		
		//queryView_.queryToolPanel().getTopPanel().max_child();
		jNumberOfQueryTextField.setText(new Integer(queryView_.queryToolPanel().getTopPanel().max_child()).toString());
		String str = System.getProperty("QueryToolMaxWaitingTime");
		if(str != null) {
			jMaxWaitingTimeTextField.setText(str);
		}

		setSize(350, 140);
		setLocation(300, 100);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * 
	 */
	private void initComponents() {

		jOKButton = new javax.swing.JButton();
		jCancelButton = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jNumberOfQueryTextField = new javax.swing.JTextField();
		jMaxWaitingTimeTextField = new javax.swing.JTextField();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Query Tool Options Dialog");
		java.awt.Image img = this.getToolkit().getImage(
				DisplayOptionsDialog.class.getResource("core-cell.gif"));
		setIconImage(img);

		jOKButton.setText("OK");
		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jOKButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jOKButton);
		jOKButton.setBounds(50, 80, 60, 23);

		jCancelButton.setText("Close");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jCancelButton);
		jCancelButton.setBounds(190, 80, 80, 23);

		jLabel1.setText("Maximum number of children to be displayed: ");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(18, 10, 240, 20);

		jNumberOfQueryTextField.setText("200");
		getContentPane().add(jNumberOfQueryTextField);
		jNumberOfQueryTextField.setBounds(280, 10, 60, 20);
		jNumberOfQueryTextField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jNumberOfQueryTextFieldActionPerformed(evt);
					}
				});

		jLabel2.setText("Maximum waiting time (seconds) for XML response: ");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(18, 40, 260, 20);

		jMaxWaitingTimeTextField.setText("180");
		getContentPane().add(jMaxWaitingTimeTextField);
		jMaxWaitingTimeTextField.setBounds(280, 40, 60, 20);
		jMaxWaitingTimeTextField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jMaxWaitingTimeTextFieldActionPerformed(evt);
					}
				});

		pack();
	}

	private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
	}

	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		String num = jNumberOfQueryTextField.getText();
		String seconds = jMaxWaitingTimeTextField.getText();

		queryView_.queryToolPanel().getTopPanel().max_child(
				new Integer(num).intValue());
		System.setProperty("QueryToolMaxWaitingTime", seconds);
		setVisible(false);
	}

	private void jMaxWaitingTimeTextFieldActionPerformed(
			java.awt.event.ActionEvent evt) {
		String num = jMaxWaitingTimeTextField.getText();
		// previousQueryView_.runTreePanel().reset(new Integer(num).intValue(),
		// jSortByNameCheckBox.isSelected());
	}

	private void jNumberOfQueryTextFieldActionPerformed(
			java.awt.event.ActionEvent evt) {
		String num = jNumberOfQueryTextField.getText();
		// previousQueryView_.runTreePanel().reset(new Integer(num).intValue(),
		// jSortByNameCheckBox.isSelected());
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new DisplayOptionsDialog(null).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JTextField jNumberOfQueryTextField;
	private javax.swing.JTextField jMaxWaitingTimeTextField;
	private javax.swing.JButton jOKButton;
	private javax.swing.JButton jCancelButton;
	// End of variables declaration

}
