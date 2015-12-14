/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 * 		Wensong Pan
 *     
 */
/*
 * PatientNameFrame.java
 *
 * Created on July 7, 2006, 9:11 AM
 */
package edu.harvard.i2b2.timeline.lifelines;

/**
 *
 * @author  wp066
 */
import java.util.*;

public class PatientNameFrame extends javax.swing.JFrame {
	private String[] strings;

	/** Creates new form PatientNameFrame */
	public PatientNameFrame(String id, String name, ArrayList<String> mrns) {

		strings = new String[mrns.size()];
		for (int i = 0; i < mrns.size(); i++) {
			strings[i] = mrns.get(i);
		}

		initComponents();

		jGroupIDTextField.setText(id);
		jNameTextField.setText(name);
		setSize(350, 340);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */

	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jGroupIDTextField = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		jNameTextField = new javax.swing.JTextField();
		jCloseButton = new javax.swing.JButton();
		jLabel3 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jMRNList = new javax.swing.JList();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Patient Information Frame");
		jPanel1.setLayout(null);

		jLabel1.setText("Group ID:");
		jPanel1.add(jLabel1);
		jLabel1.setBounds(20, 20, 60, 20);

		jPanel1.add(jGroupIDTextField);
		jGroupIDTextField.setBounds(110, 20, 160, 20);

		jLabel2.setText("Patient Name:");
		jPanel1.add(jLabel2);
		jLabel2.setBounds(20, 60, 85, 14);

		jPanel1.add(jNameTextField);
		jNameTextField.setBounds(110, 60, 160, 20);

		jCloseButton.setText("Close");
		jCloseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCloseButtonActionPerformed(evt);
			}
		});

		jPanel1.add(jCloseButton);
		jCloseButton.setBounds(115, 260, 70, 23);

		jLabel3.setText("MRN list:");
		jPanel1.add(jLabel3);
		jLabel3.setBounds(20, 100, 70, 20);

		jMRNList.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jMRNList.setModel(new javax.swing.AbstractListModel() {
			// String[] strings = { "7323455", "9258766", "5689875", "1234532",
			// "1489789", "4589666",
			// "7323455", "9258766", "5689875", "1234532", "1489789",
			// "4589666"};
			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane1.setViewportView(jMRNList);

		jPanel1.add(jScrollPane1);
		jScrollPane1.setBounds(110, 100, 130, 130);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(20, 10, 290, 300);

		pack();
	}

	private void jCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				setVisible(false);
			}
		});
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// new PatientNameFrame(" ").setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.JButton jCloseButton;
	private javax.swing.JTextField jGroupIDTextField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JList jMRNList;
	private javax.swing.JTextField jNameTextField;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	// End of variables declaration

}
