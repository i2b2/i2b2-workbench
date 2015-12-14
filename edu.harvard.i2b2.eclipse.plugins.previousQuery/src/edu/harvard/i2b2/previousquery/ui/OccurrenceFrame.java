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
 * QueryOccurrenceFrame.java
 * 
 * Created on September 14, 2006, 10:59 AM
 */

import javax.swing.SpinnerNumberModel;

public class OccurrenceFrame extends javax.swing.JFrame {

	private ConceptTreePanel parentPanel = null;

	/** Creates new form QueryOccurrenceFrame */
	public OccurrenceFrame(ConceptTreePanel parent) {
		parentPanel = parent;

		initComponents();
		SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 20, 1);
		jOccurTimesSpinner.setModel(model);
		jOccurTimesSpinner.setValue(new Integer(parent.getOccurrenceTimes()));

		setSize(330, 200);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		jOKButton = new javax.swing.JButton();
		jCancelButton = new javax.swing.JButton();
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jOccurTimesSpinner = new javax.swing.JSpinner();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		jOKButton.setText("OK");
		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jOKButtonActionPerformed(evt);
			}
		});
		getContentPane().add(jOKButton);
		jOKButton.setBounds(70, 130, 49, 23);

		jCancelButton.setText("Cancel");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});
		getContentPane().add(jCancelButton);
		jCancelButton.setBounds(190, 130, 67, 23);

		jPanel1.setLayout(null);

		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Constrain by occurrences"));
		jLabel1.setText("Event Occurrence Times:");
		jPanel1.add(jLabel1);
		jLabel1.setBounds(30, 30, 130, 30);

		jPanel1.add(jOccurTimesSpinner);
		jOccurTimesSpinner.setBounds(170, 30, 55, 30);

		jLabel2.setText("20");
		jPanel1.add(jLabel2);
		jLabel2.setBounds(210, 10, 20, 20);

		jLabel3.setText("1");
		jPanel1.add(jLabel3);
		jLabel3.setBounds(210, 60, 10, 14);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(20, 20, 280, 90);

		pack();
	}

	private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
	}

	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		parentPanel.setOccurrenceTimes(new Integer(
				(Integer) (jOccurTimesSpinner.getValue())).intValue());
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (parentPanel.getOccurrenceTimes() == 1) {
					parentPanel.setOccurrenceText("Occurs 1x");
				} else {
					String str = "Occurs " + parentPanel.getOccurrenceTimes()
							+ "x";
					parentPanel.setOccurrenceText("<html><u>" + str + "</u>");
				}
			}
		});

		setVisible(false);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new OccurrenceFrame(null).setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jCancelButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JButton jOKButton;
	private javax.swing.JSpinner jOccurTimesSpinner;
	private javax.swing.JPanel jPanel1;
	// End of variables declaration//GEN-END:variables

}
