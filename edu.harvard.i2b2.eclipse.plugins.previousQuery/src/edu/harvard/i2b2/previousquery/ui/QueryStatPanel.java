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
 * QueryStatPanel.java
 *
 * Created on August 28, 2006, 9:11 AM
 */

public class QueryStatPanel extends javax.swing.JPanel {

	/** Creates new form QueryStatPanel */
	public QueryStatPanel() {
		initComponents();
	}

	public void setPatientCount(String number) {
		jPatientCountTextField.setText(number);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */

	private void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		jPatientCountTextField = new javax.swing.JTextField();

		setLayout(null);

		jLabel1.setText("Patient Count: ");
		add(jLabel1);
		jLabel1.setBounds(40, 20, 80, 20);

		add(jPatientCountTextField);
		jPatientCountTextField.setBounds(120, 20, 130, 20);
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel jLabel1;
	private javax.swing.JTextField jPatientCountTextField;
	// End of variables declaration//GEN-END:variables

}
