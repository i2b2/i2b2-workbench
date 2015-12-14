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
 * QueryOccurrenceFrame.java
 *
 * Created on September 14, 2006, 10:59 AM
 */

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

public class OccurrenceFrame extends javax.swing.JFrame {

	private GroupPanel parentPanel = null;

	/** Creates new form QueryOccurrenceFrame */
	@SuppressWarnings("unchecked")
	public OccurrenceFrame(GroupPanel parent) {
		parentPanel = parent;

		initComponents();
		SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 19, 1);
		jOccurTimesSpinner.setModel(model);
		jOccurTimesSpinner
				.setValue(new Integer(parent.getOccurrenceTimes()) - 1);
		jSlider1
		.setValue(new Integer(parent.getAccuracyScale()));
		//jSlider1.setToolTipText("Percent of the matching documents which should be returned"+"\n" +
		//		" where documents with the highest relevance will be returned first");
		setSize(390, 200);
		setLocation(250, 200);
		/*Enumeration e = jSlider1.getLabelTable().keys();
		while(e.hasMoreElements()) {
			Integer i= (Integer) e.nextElement();
			JLabel l = (JLabel) jSlider1.getLabelTable().get(i);
			l.setText(i+"%");
			l.setFont(new Font("Tahoma", Font.PLAIN, 10));
		}*/
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
		jSlider1 = new javax.swing.JSlider();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		jOKButton.setText("OK");
		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jOKButtonActionPerformed(evt);
			}
		});
		getContentPane().add(jOKButton);
		jOKButton.setBounds(90, 130, 60, 23);

		jCancelButton.setText("Cancel");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});
		getContentPane().add(jCancelButton);
		jCancelButton.setBounds(210, 130, 85, 23);

		jPanel1.setLayout(null);

		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Constrain by occurrences"));
		jLabel1.setText("Event Occurs more than number of times in the box:");
		jPanel1.add(jLabel1);
		jLabel1.setBounds(20, 30, 270, 30);

		jPanel1.add(jOccurTimesSpinner);
		jOccurTimesSpinner.setBounds(280, 30, 40, 30);

		jLabel2.setText("19");
		jPanel1.add(jLabel2);
		jLabel2.setBounds(300, 10, 20, 20);

		jLabel3.setText("0");
		jPanel1.add(jLabel3);
		jLabel3.setBounds(300, 60, 10, 14);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(20, 20, 350, 90);
		
		 jSlider1.setPaintLabels(true);
	        jSlider1.setPaintTicks(true);
	        jSlider1.setMajorTickSpacing(10);
	        jSlider1.setMinorTickSpacing(5);
	        //jSlider1.setExtent(5);
	        //getContentPane().add(jSlider1);
	        jSlider1.setBounds(20, 195, 320, 45);
	        jSlider1.setValue(100);
	        
	        jLabel4.setText("Application of relevance for text searches only (%):");
	        //getContentPane().add(jLabel4);
	        jLabel4.setBounds(30, 130, 320, 20);
	        
	        jLabel5.setText("(Percent of the matching documents which should be returned");
	        //getContentPane().add(jLabel5);
	        jLabel5.setBounds(30, 155, 320, 15);

	        jLabel6.setText("where documents with the highest relevance will be returned first)");
	       // getContentPane().add(jLabel6);
	        jLabel6.setBounds(30, 165, 320, 20);

		pack();
	}

	private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
	}

	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		parentPanel.setOccurrenceTimes(new Integer(
				(Integer) (jOccurTimesSpinner.getValue())).intValue() + 1);
		parentPanel.setAccuracyScale(new Integer(
				(Integer) (jSlider1.getValue())).intValue());
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (parentPanel.getOccurrenceTimes() == 1) {
					parentPanel.setOccurrenceText("Occurs > 0x");
				} else {
					String str = "Occurs > "
							+ (parentPanel.getOccurrenceTimes() - 1) + "x";
					parentPanel.setOccurrenceText("<html><u>" + str + "</u>");
				}
				
				if (parentPanel.getAccuracyScale() == 100) {
					//parentPanel.setOccurrenceText("Occurs > 0x");
				} else {
					String str = "S = "
							+ (parentPanel.getAccuracyScale()) + "%";
					parentPanel.setOccurrenceText("<html><u>" + str + "</u>");
				}
			}
		});
		parentPanel.setAccuracyScale(new Integer(
				(Integer) (jSlider1.getValue())).intValue());

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

	// Variables declaration
	private javax.swing.JButton jCancelButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JButton jOKButton;
	private javax.swing.JSpinner jOccurTimesSpinner;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JSlider jSlider1;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	// End of variables declaration

}
