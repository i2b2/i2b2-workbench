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

import java.util.*;

/*
 * QueryConstrainFrame.java
 *
 * Created on August 31, 2006, 12:46 PM
 */

public class TimeConstraintFrame extends javax.swing.JFrame {

	private GroupPanel parentPanel;

	/** Creates new form QueryConstrainFrame */
	public TimeConstraintFrame(GroupPanel parent) {
		initComponents();
		// mode = Mode;
		parentPanel = parent;

		if (parentPanel.data().startTime() != -1) {
			jFromDateCheckBox.setSelected(true);
			jFromDateField.setEnabled(true);
			jFromDateField.setEditable(true);
			jFromDateField.setDateInMillis(parentPanel.data().startTime());
		} else {
			TimeZone pdt = TimeZone.getTimeZone("EST");
			Calendar calendar = new GregorianCalendar(pdt);
			calendar.set(1979, 11, 1);
			jFromDateField.setDateInMillis(calendar.getTimeInMillis());
		}

		if (parentPanel.data().endTime() != -1) {
			jToDateCheckBox.setSelected(true);
			jToDateField.setEnabled(true);
			jToDateField.setEditable(true);
			jToDateField.setDateInMillis(parentPanel.data().endTime());
		}

		if (parent.getClass().getName().equals(GroupPanel.class.getName())) {
			jExcludeSubitemCheckBox.setVisible(false);
			jExcludeSubitemCheckBox.setEnabled(false);
			jOKButton.setBounds(110, 130, 85, 23);
			jCancelButton.setBounds(240, 130, 105, 23);
			setSize(430, 200);
		} else {
			setSize(430, 250);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * 
	 */
	private void initComponents() {
		jPanel2 = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		jFromDateCheckBox = new javax.swing.JCheckBox();
		jFromDateField = new JDatePicker(jFromDateCheckBox);
		jPanel3 = new javax.swing.JPanel();
		jToDateCheckBox = new javax.swing.JCheckBox();
		jToDateField = new JDatePicker(jToDateCheckBox);
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		// jPanel4 = new javax.swing.JPanel();
		jPrincipalCheckBox = new javax.swing.JCheckBox();
		jSecondaryCheckBox = new javax.swing.JCheckBox();
		jAdmissionCheckBox = new javax.swing.JCheckBox();
		jExcludeSubitemCheckBox = new javax.swing.JCheckBox();
		jOKButton = new javax.swing.JButton();
		jCancelButton = new javax.swing.JButton();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jPanel2.setLayout(null);

		jPanel2.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Constrain by dates"));
		jPanel1.setLayout(null);

		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jFromDateCheckBox.setBorder(null);
		jFromDateCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jFromDateCheckBox.setMaximumSize(new java.awt.Dimension(18, 18));
		jFromDateCheckBox.setMinimumSize(new java.awt.Dimension(18, 18));
		jFromDateCheckBox.setPreferredSize(new java.awt.Dimension(18, 18));
		jFromDateCheckBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jFromDateCheckBoxActionPerformed(evt);
					}
				});

		jPanel1.add(jFromDateCheckBox);
		jFromDateCheckBox.setBounds(10, 10, 20, 20);

		jPanel1.add(jFromDateField);
		jFromDateField.setBounds(30, 10, 120, 20);
		jFromDateField.setEditable(false);

		jPanel2.add(jPanel1);
		jPanel1.setBounds(20, 40, 160, 40);

		jPanel3.setLayout(null);

		jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jToDateCheckBox.setBorder(null);
		jToDateCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jToDateCheckBox.setMaximumSize(new java.awt.Dimension(18, 18));
		jToDateCheckBox.setMinimumSize(new java.awt.Dimension(18, 18));
		jToDateCheckBox.setPreferredSize(new java.awt.Dimension(18, 18));
		jToDateCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jToDateCheckBoxActionPerformed(evt);
			}
		});

		jPanel3.add(jToDateCheckBox);
		jToDateCheckBox.setBounds(10, 10, 20, 20);

		jPanel3.add(jToDateField);
		jToDateField.setBounds(30, 10, 120, 20);
		jToDateField.setEditable(false);

		jPanel2.add(jPanel3);
		jPanel3.setBounds(220, 40, 160, 40);

		jLabel1.setText("From:");
		jPanel2.add(jLabel1);
		jLabel1.setBounds(20, 20, 80, 20);

		jLabel2.setText("To:");
		jPanel2.add(jLabel2);
		jLabel2.setBounds(220, 20, 16, 20);

		getContentPane().add(jPanel2);
		jPanel2.setBounds(10, 10, 400, 100);

		/*
		 * jPanel4.setLayout(null);
		 * 
		 * jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(
		 * "Contrain by priority in visit"));jPrincipalCheckBox.setText(
		 * "Items classified as principal diagnoses or procedures.");
		 * jPrincipalCheckBox
		 * .setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		 * jPrincipalCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		 * jPanel4.add(jPrincipalCheckBox); jPrincipalCheckBox.setBounds(30, 20,
		 * 320, 20); jPrincipalCheckBox.setSelected(true);
		 * 
		 * jSecondaryCheckBox.setText(
		 * "Items classified as secondary diagnoses or procedures.");
		 * jSecondaryCheckBox
		 * .setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		 * jSecondaryCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		 * jPanel4.add(jSecondaryCheckBox); jSecondaryCheckBox.setBounds(30, 50,
		 * 320, 20); jSecondaryCheckBox.setSelected(true);
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * jAdmissionCheckBox.setText("Items classified as admission diagnoses.")
		 * ;
		 * jAdmissionCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder
		 * (0, 0, 0, 0)); jAdmissionCheckBox.setMargin(new java.awt.Insets(0, 0,
		 * 0, 0)); jPanel4.add(jAdmissionCheckBox);
		 * jAdmissionCheckBox.setBounds(30, 80, 320, 20);
		 * jAdmissionCheckBox.setSelected(true);
		 * 
		 * getContentPane().add(jPanel4); jPanel4.setBounds(10, 110, 400, 120);
		 * 
		 * jExcludeSubitemCheckBox.setText(
		 * "Do not include the sub-items in this folder.");
		 * jExcludeSubitemCheckBox
		 * .setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		 * jExcludeSubitemCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		 * getContentPane().add(jExcludeSubitemCheckBox);
		 * jExcludeSubitemCheckBox.setBounds(40, 240, 300, 15);
		 */

		jOKButton.setText("OK");
		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jOKButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jOKButton);
		jOKButton.setBounds(110, 150, 185, 23);

		jCancelButton.setText("Cancel");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jCancelButton);
		jCancelButton.setBounds(240, 150, 125, 23);

		pack();
	}

	private void jToDateCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
		if (jToDateCheckBox.isSelected()) {
			jToDateField.setEditable(true);
		} else {
			jToDateField.setEditable(false);
		}
	}

	private void jFromDateCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
		if (jFromDateCheckBox.isSelected()) {
			jFromDateField.setEditable(true);
		} else {
			jFromDateField.setEditable(false);
		}
	}

	private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
	}

	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		TimeZone pdt = TimeZone.getTimeZone("EST");
		
		if (this.jFromDateCheckBox.isSelected()) {
			GregorianCalendar fromCalendar = new GregorianCalendar(pdt);
			fromCalendar.setTimeInMillis(jFromDateField.getDateInMillis());
			//fromCalendar.setTime(jFromDateField.getDate());
			Date date =jFromDateField.getDate();
			date.getDate();
			fromCalendar.setLenient(false);
			parentPanel.data().startYear(fromCalendar.get(Calendar.YEAR));
			parentPanel.data().startMonth(fromCalendar.get(Calendar.MONTH));
			parentPanel.data().startDay(date.getDate());//fromCalendar.get(Calendar.DAY_OF_MONTH));
			parentPanel.data().startTime(jFromDateField.getDateInMillis());
		} else {
			parentPanel.data().startYear(-1);
			parentPanel.data().startMonth(-1);
			parentPanel.data().startDay(-1);
			parentPanel.data().startTime(-1);
		}

		if (this.jToDateCheckBox.isSelected()) {
			GregorianCalendar toCalendar = new GregorianCalendar(pdt);
			toCalendar.setTimeInMillis(jToDateField.getDateInMillis());
			//toCalendar.setTime(jToDateField.getDate());
			toCalendar.setLenient(false);
			Date date =jToDateField.getDate();
			parentPanel.data().endYear(toCalendar.get(Calendar.YEAR));
			parentPanel.data().endMonth(toCalendar.get(Calendar.MONTH));
			parentPanel.data().endDay(date.getDate());//toCalendar.get(Calendar.DAY_OF_MONTH));
			parentPanel.data().endTime(jToDateField.getDateInMillis());
		} else {
			parentPanel.data().endYear(-1);
			parentPanel.data().endMonth(-1);
			parentPanel.data().endDay(-1);
			parentPanel.data().endTime(-1);
		}

		parentPanel.data().includePrincipleVisit(
				this.jPrincipalCheckBox.isSelected());
		parentPanel.data().includeSecondaryVisit(
				this.jSecondaryCheckBox.isSelected());
		parentPanel.data().includeAdmissionVisit(
				this.jAdmissionCheckBox.isSelected());
		
		System.out.println("Start day: :"+parentPanel.data().startDay());
		System.out.println("End day: :"+parentPanel.data().endDay());

		setVisible(false);

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (parentPanel.data().startTime() == -1
						&& parentPanel.data().endTime() == -1) {
					parentPanel.setDateConstrainText("Dates");
				} else {
					parentPanel.setDateConstrainText("<html><u>Dates</u>");
				}
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
				new TimeConstraintFrame(null).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.JCheckBox jAdmissionCheckBox;
	private javax.swing.JButton jCancelButton;
	private javax.swing.JCheckBox jExcludeSubitemCheckBox;
	private javax.swing.JCheckBox jFromDateCheckBox;
	private JDatePicker jFromDateField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JButton jOKButton;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	// private javax.swing.JPanel jPanel4;
	private javax.swing.JCheckBox jPrincipalCheckBox;
	private javax.swing.JCheckBox jSecondaryCheckBox;
	private javax.swing.JCheckBox jToDateCheckBox;
	private JDatePicker jToDateField;
	// End of variables declaration
}
