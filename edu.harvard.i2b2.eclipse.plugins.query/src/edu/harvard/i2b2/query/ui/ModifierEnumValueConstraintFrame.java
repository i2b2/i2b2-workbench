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

import java.awt.Color;
import javax.swing.table.DefaultTableModel;

import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;

/*
 * EnumValueConstrainFrame.java
 * 
 * Created on December 7, 2007, 11:58 AM
 */

@SuppressWarnings("serial")
public class ModifierEnumValueConstraintFrame extends javax.swing.JFrame {
	private GroupPanel parent_;

	/** Creates new form EnumValueConstrainFrame */
	public ModifierEnumValueConstraintFrame(GroupPanel parent) {
		parent_ = parent;

		initComponents();

		jEnumValueTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		jEnumValueTable.getColumnModel().getColumn(0).setMaxWidth(30);
		jEnumValueTable.getColumnModel().getColumn(0).setMinWidth(30);

		buttonGroup.add(jNoValueRadioButton);
		buttonGroup.add(jFlagRadioButton);
		buttonGroup.add(jTextValueRadioButton);

		jNoValueRadioButton.setSelected(true);
		jEnumValueTable.setEnabled(false);
		jEnumValueTable.setBackground(Color.LIGHT_GRAY);

		ModifierData data = (ModifierData)parent_.currentData();
		jEnumValueTable.removeAll();
		DefaultTableModel tableModel = (DefaultTableModel) jEnumValueTable
				.getModel();
		tableModel.setRowCount(data.modifierValuePropertyData().enumValueNames.size());
		for (int i = 0; i < data.modifierValuePropertyData().enumValueNames.size(); i++) {
			String val = data.modifierValuePropertyData().enumValueNames.get(i);
			jEnumValueTable.setValueAt(false, i, 0);
			jEnumValueTable.setValueAt(val, i, 1);
		}
		setPreviousValues();

	}

	private void setPreviousValues() {
		if (((ModifierData)parent_.currentData()).modifierValuePropertyData().hasEnumValue()) {
			if (((ModifierData)parent_.currentData()).modifierValuePropertyData().useValueFlag()) {
				jFlagRadioButton.setSelected(true);
			} else if (((ModifierData)parent_.currentData()).modifierValuePropertyData().useTextValue()) {

				jTextValueRadioButton.setSelected(true);
				jTextValueRadioButtonActionPerformed(null);

				for (int i = 0; i < ((ModifierData)parent_.currentData()).modifierValuePropertyData().enumValues.size(); i++) {
					String enumVal = ((ModifierData)parent_.currentData()).modifierValuePropertyData().enumValues.get(i);
					String testEnumVal = "'"+enumVal+"'";
					
					if(((ModifierData)parent_.currentData()).modifierValuePropertyData().selectedValues.size()==0) {
						if (((ModifierData)parent_.currentData()).modifierValuePropertyData().value().indexOf(testEnumVal)>=0) {
							jEnumValueTable.setValueAt(true, i, 0);
							//break;
						}
					}
					for (String selectedValue : ((ModifierData)parent_.currentData()).modifierValuePropertyData().selectedValues) {
						if (selectedValue.equals(enumVal)) {
							jEnumValueTable.setValueAt(true, i, 0);
							//break;
						}

					}
					//Boolean selected = (Boolean) jEnumValueTable.getValueAt(i,
					//		0);
					//if (selected.equals(Boolean.TRUE)) {
						//String valStr = (String) jEnumValueTable.getValueAt(i,
								//1);
					//}
				}
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		buttonGroup = new javax.swing.ButtonGroup();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jNoValueRadioButton = new javax.swing.JRadioButton();
		jFlagRadioButton = new javax.swing.JRadioButton();
		jTextValueRadioButton = new javax.swing.JRadioButton();
		jOKButton = new javax.swing.JButton();
		jCancelButton = new javax.swing.JButton();
		jTableScrollPane = new javax.swing.JScrollPane();
		jEnumValueTable = new javax.swing.JTable();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jLabel1
				.setText("Searches by Lab values can be constrained by the abnormal flag");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(30, 10, 330, 20);

		jLabel2
				.setText("set by the performing laboratory, or by the values themselves.");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(30, 30, 330, 20);

		jNoValueRadioButton.setText("No value");
		jNoValueRadioButton.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jNoValueRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jNoValueRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jNoValueRadioButtonActionPerformed(evt);
					}
				});

		getContentPane().add(jNoValueRadioButton);
		jNoValueRadioButton.setBounds(30, 58, 90, 18);

		jFlagRadioButton.setText("By abnormal flag");
		jFlagRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 0, 0, 0));
		jFlagRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jFlagRadioButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jFlagRadioButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jFlagRadioButton);
		jFlagRadioButton.setBounds(30, 80, 150, 18);

		jTextValueRadioButton.setText("By text value");
		jTextValueRadioButton.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jTextValueRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jTextValueRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jTextValueRadioButtonActionPerformed(evt);
					}
				});

		getContentPane().add(jTextValueRadioButton);
		jTextValueRadioButton.setBounds(30, 103, 100, 18);

		jOKButton.setText("OK");
		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jOKButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jOKButton);
		jOKButton.setBounds(110, 260, 65, 23);

		jCancelButton.setText("Cancel");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jCancelButton);
		jCancelButton.setBounds(220, 260, 80, 23);

		jEnumValueTable.setModel(new DefaultTableModel(new Object[][] {
				{ null, "No Detection" }, { null, null }, { null, null },
				{ null, null } }, new String[] { "", "" }) {
			Class[] types = new Class[] { java.lang.Boolean.class,
					java.lang.String.class };
			boolean[] canEdit = new boolean[] { true, false };

			@Override
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		jEnumValueTable.setShowHorizontalLines(false);
		jEnumValueTable.setShowVerticalLines(false);
		jEnumValueTable.setTableHeader(null);
		// jEnumValueTable.setPreferredSize(new Dimension(310, 110));
		jTableScrollPane.setViewportView(jEnumValueTable);

		getContentPane().add(jTableScrollPane);
		jTableScrollPane.setBounds(50, 130, 310, 110);

		pack();
	}

	private void clearTableSelections() {
		jEnumValueTable.setEnabled(false);
		jEnumValueTable.setBackground(Color.LIGHT_GRAY);

		for (int i = 0; i < jEnumValueTable.getRowCount(); i++) {
			jEnumValueTable.setValueAt(false, i, 0);
		}
	}

	private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
	}

	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		ModifierData data = (ModifierData)parent_.currentData();
		data.modifierValuePropertyData().selectedValues.clear();

		if (jNoValueRadioButton.isSelected()) {
			data.modifierValuePropertyData().noValue(true);
			data.modifierValuePropertyData().useValueFlag(false);
			data.modifierValuePropertyData().useNumericValue(false);
			data.modifierValuePropertyData().useTextValue(false);
			data.valueName("");
		} else if (jFlagRadioButton.isSelected()) {
			data.modifierValuePropertyData().noValue(false);
			data.modifierValuePropertyData().useValueFlag(true);
			data.modifierValuePropertyData().useNumericValue(false);
			data.modifierValuePropertyData().useTextValue(false);
			data.modifierValuePropertyData().value("A");
			data.valueName(" = Abnormal");
		} else if (jTextValueRadioButton.isSelected()) {
			data.modifierValuePropertyData().noValue(false);
			data.modifierValuePropertyData().useValueFlag(false);
			data.modifierValuePropertyData().useNumericValue(false);
			data.modifierValuePropertyData().useTextValue(true);
			data.modifierValuePropertyData().selectedValues.clear();

			for (int i = 0; i < jEnumValueTable.getRowCount(); i++) {
				Boolean selected = (Boolean) jEnumValueTable.getValueAt(i, 0);
				if (selected.equals(Boolean.TRUE)) {
					//String valStr = (String) jEnumValueTable.getValueAt(i, 1);
					String valStr = data.modifierValuePropertyData().enumValues.get(i);
					data.modifierValuePropertyData().selectedValues.add(valStr);
				}
			}

			data.valueName(" Is "
					+ data.modifierValuePropertyData().selectedValues.get(0));
			if (data.modifierValuePropertyData().selectedValues.size() > 1) {
				for (int j = 1; j < data.modifierValuePropertyData().selectedValues
						.size(); j++) {
					data.valueName(data.valueName() + ","
							+ data.modifierValuePropertyData().selectedValues.get(j));
				}
			}
		}
		data.name(parent_.currentData().titleName());
		parent_.setValueDisplay();

		setVisible(false);
	}

	private void jTextValueRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (jTextValueRadioButton.isSelected()) {
			jEnumValueTable.setEnabled(true);
			jEnumValueTable.setBackground(Color.WHITE);
		}
	}

	private void jFlagRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (jFlagRadioButton.isSelected()) {
			clearTableSelections();
		}
	}

	private void jNoValueRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (jNoValueRadioButton.isSelected()) {
			clearTableSelections();
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ModifierEnumValueConstraintFrame(null).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.ButtonGroup buttonGroup;
	private javax.swing.JButton jCancelButton;
	private javax.swing.JTable jEnumValueTable;
	private javax.swing.JRadioButton jFlagRadioButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JRadioButton jNoValueRadioButton;
	private javax.swing.JButton jOKButton;
	private javax.swing.JScrollPane jTableScrollPane;
	private javax.swing.JRadioButton jTextValueRadioButton;
	// End of variables declaration

}
