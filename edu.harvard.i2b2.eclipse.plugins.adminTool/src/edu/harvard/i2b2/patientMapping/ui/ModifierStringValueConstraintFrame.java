/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */
package edu.harvard.i2b2.patientMapping.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import edu.harvard.i2b2.adminTool.dataModel.KTable;
import edu.harvard.i2b2.adminTool.dataModel.ModifierData;
//import edu.harvard.i2b2.adminTool.dataModel.QueryModel;
import edu.harvard.i2b2.eclipse.UserInfoBean;



/*
 * EnumValueConstrainFrame.java
 * 
 * Created on December 7, 2007, 11:58 AM
 */

@SuppressWarnings("serial")
public class ModifierStringValueConstraintFrame extends javax.swing.JFrame {
	private ModifierData data;
	private KTable table;

	/** Creates new form EnumValueConstrainFrame */
	public ModifierStringValueConstraintFrame(ModifierData data_, KTable table_) {
		data = data_;
		table = table_;

		initComponents();

		//jEnumValueTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		//jEnumValueTable.getColumnModel().getColumn(0).setMaxWidth(30);
		//jEnumValueTable.getColumnModel().getColumn(0).setMinWidth(30);

		buttonGroup.add(jNoValueRadioButton);
		buttonGroup.add(jFlagRadioButton);
		buttonGroup.add(jTextValueRadioButton);

		jNoValueRadioButton.setSelected(true);
		//jEnumValueTable.setEnabled(false);
		//jEnumValueTable.setBackground(Color.LIGHT_GRAY);
		jSearchOptionComboBox.setEnabled(false);
		jSearchTextField.setEditable(false);
		jDatabaseCheckBox.setVisible(false);
		
		jSearchTextField.setDocument(new JTextFieldLimit(data_.modifierValuePropertyData().searchStrLength()));

		//ModifierData data = (ModifierData)parent_.currentData();
		//jEnumValueTable.removeAll();
		//DefaultTableModel tableModel = (DefaultTableModel) jEnumValueTable
		//		.getModel();
		//tableModel.setRowCount(data.modifierValuePropertyData().enumValueNames.size());
		//for (int i = 0; i < data.modifierValuePropertyData().enumValueNames.size(); i++) {
			//String val = data.modifierValuePropertyData().enumValueNames.get(i);
			//jEnumValueTable.setValueAt(false, i, 0);
			//jEnumValueTable.setValueAt(val, i, 1);
		//}
		
		if(data.modifierValuePropertyData().isLongText()) {
			//jSearchOptionComboBox.setEnabled(false);
			jFlagRadioButton.setEnabled(false);
			jSearchOptionComboBox.setVisible(false);//setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Containing" }));
			jSearchTextField.setBounds(30, 128, 300, 20);
			jDatabaseCheckBox.setVisible(true);
			jDatabaseCheckBox.setEnabled(false);
		}
		setPreviousValues();
		
		if (!(UserInfoBean.getInstance().isRoleInProject("DATA_DEID"))) {
			jTextValueRadioButton.setEnabled(false);
		}
	}

	private void setPreviousValues() {
		if (data.modifierValuePropertyData().hasStringValue()) {
			if (data.modifierValuePropertyData().useValueFlag()) {
				jFlagRadioButton.setSelected(true);
			} else if (data.modifierValuePropertyData().useStringValue()) {

				jTextValueRadioButton.setSelected(true);
				jTextValueRadioButtonActionPerformed(null);
				jSearchTextField.setText(data.modifierValuePropertyData().value());
				
				if(data.modifierValuePropertyData().isLongText()) {
					if(data.modifierValuePropertyData().operator().equalsIgnoreCase("CONTAINS")) {
						jDatabaseCheckBox.setSelected(false);
					}
					else if(data.modifierValuePropertyData().operator().equalsIgnoreCase("CONTAINS[database]")) {
						jDatabaseCheckBox.setSelected(true);
					}
				}
				else {
					if(data.modifierValuePropertyData().operator().equalsIgnoreCase("LIKE[contains]")) {
						jSearchOptionComboBox.setSelectedIndex(0);
					}
					else if(data.modifierValuePropertyData().operator().equalsIgnoreCase("LIKE[exact]")) {
						jSearchOptionComboBox.setSelectedIndex(1);
					}
					else if(data.modifierValuePropertyData().operator().equalsIgnoreCase("LIKE[begin]")) {
						jSearchOptionComboBox.setSelectedIndex(2);
					}
					else if(data.modifierValuePropertyData().operator().equalsIgnoreCase("LIKE[end]")) {
						jSearchOptionComboBox.setSelectedIndex(3);
					}
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
		//jTableScrollPane = new javax.swing.JScrollPane();
		//jEnumValueTable = new javax.swing.JTable();
		jSearchOptionComboBox = new javax.swing.JComboBox();
        jSearchTextField = new javax.swing.JTextField();
        jDatabaseCheckBox = new javax.swing.JCheckBox();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jLabel1
				.setText("You are allowed to search within the narrative text associated");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(30, 10, 330, 20);

		jLabel2
				.setText("with the term "+data.name()+".");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(30, 30, 330, 20);

		jNoValueRadioButton.setText("No Search Requested");
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
		jNoValueRadioButton.setBounds(30, 58, 150, 18);

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

		jTextValueRadioButton.setText("Search within Text");
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
		jTextValueRadioButton.setBounds(30, 103, 150, 18);
		
		jDatabaseCheckBox.setText("Use Database Operators (Advanced Search)");
		jDatabaseCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jDatabaseCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		//jTextValueRadioButton
		//		.addActionListener(new java.awt.event.ActionListener() {
		//			public void actionPerformed(java.awt.event.ActionEvent evt) {
		//				jTextValueRadioButtonActionPerformed(evt);
		//			}
		//		});

		getContentPane().add(jDatabaseCheckBox);
		jDatabaseCheckBox.setBounds(30, 155, 250, 18);

		jOKButton.setText("OK");
		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jOKButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jOKButton);
		jOKButton.setBounds(110, 180, 65, 23);

		jCancelButton.setText("Cancel");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jCancelButton);
		jCancelButton.setBounds(220, 180, 80, 23);


		jSearchOptionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Containing", "Exact", "Starting with", "Ending with" }));
        getContentPane().add(jSearchOptionComboBox);
        jSearchOptionComboBox.setBounds(30, 130, 100, 22);

        getContentPane().add(jSearchTextField);
        jSearchTextField.setBounds(132, 130, 190, 20);

		pack();
	}

	/*private void clearTableSelections() {
		jEnumValueTable.setEnabled(false);
		jEnumValueTable.setBackground(Color.LIGHT_GRAY);

		for (int i = 0; i < jEnumValueTable.getRowCount(); i++) {
			jEnumValueTable.setValueAt(false, i, 0);
		}
	}*/

	private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
	}

	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		//QueryConceptTreeNodeData data = parent_.currentData();
		data.modifierValuePropertyData().selectedValues.clear();

		if (jNoValueRadioButton.isSelected()) {
			data.modifierValuePropertyData().noValue(true);
			data.modifierValuePropertyData().useValueFlag(false);
			data.modifierValuePropertyData().useNumericValue(false);
			data.modifierValuePropertyData().useTextValue(false);
			data.modifierValuePropertyData().useStringValue(false);
			data.valueName("");
		} else if (jFlagRadioButton.isSelected()) {
			data.modifierValuePropertyData().noValue(false);
			data.modifierValuePropertyData().useValueFlag(true);
			data.modifierValuePropertyData().useNumericValue(false);
			data.modifierValuePropertyData().useTextValue(false);
			data.modifierValuePropertyData().useStringValue(false);
			data.modifierValuePropertyData().value("A");
			data.valueName(" = Abnormal");
		} else if (jTextValueRadioButton.isSelected()) {
			data.modifierValuePropertyData().noValue(false);
			data.modifierValuePropertyData().useValueFlag(false);
			data.modifierValuePropertyData().useNumericValue(false);
			data.modifierValuePropertyData().useTextValue(false);
			data.modifierValuePropertyData().useStringValue(true);
			data.modifierValuePropertyData().selectedValues.clear();

			/*for (int i = 0; i < jEnumValueTable.getRowCount(); i++) {
				Boolean selected = (Boolean) jEnumValueTable.getValueAt(i, 0);
				if (selected.equals(Boolean.TRUE)) {
					//String valStr = (String) jEnumValueTable.getValueAt(i, 1);
					String valStr = data.modifiervalueModel().enumValues.get(i);
					data.modifiervalueModel().selectedValues.add(valStr);
				}
			}*/
			data.modifierValuePropertyData().value(jSearchTextField.getText().trim());
			
			if(data.modifierValuePropertyData().isLongText()) {
				if(!jDatabaseCheckBox.isSelected()) {
					data.modifierValuePropertyData().operator("CONTAINS");
				}
				else {// if(jDatabaseCheckBox.isSelected()) {
					data.modifierValuePropertyData().operator("CONTAINS[database]");
				}
			} else {
				if(jSearchOptionComboBox.getSelectedIndex() == 0) {
					data.modifierValuePropertyData().operator("LIKE[contains]");
				}
				else if(jSearchOptionComboBox.getSelectedIndex() == 1) {
					data.modifierValuePropertyData().operator("LIKE[exact]");
				}
				else if(jSearchOptionComboBox.getSelectedIndex() == 2) {
					data.modifierValuePropertyData().operator("LIKE[begin]");
				}
				else if(jSearchOptionComboBox.getSelectedIndex() == 3) {
					data.modifierValuePropertyData().operator("LIKE[end]");
				}
			}
			data.valueName(" "+data.modifierValuePropertyData().operator()+ " "
					+ data.modifierValuePropertyData().value());
			/*if (data.modifierValuePropertyData().selectedValues.size() > 1) {
				for (int j = 1; j < data.modifierValuePropertyData().selectedValues
						.size(); j++) {
					data.valueName(data.valueName() + ","
							+ data.modifierValuePropertyData().selectedValues.get(j));
				}
			}*/
		}
		//data.name(parent_.currentData().titleName());
		//data.setValueDisplay();
		table.getModel().setContentAt(4, table.selectedRow, data.valueName());
		table.getDisplay().syncExec(new Runnable() {
			public void run() {
				table.redraw();
			}
		});
		setVisible(false);
	}

	private void jTextValueRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (jTextValueRadioButton.isSelected()) {
			jSearchOptionComboBox.setEnabled(true);
			jSearchTextField.setEditable(true);
			jDatabaseCheckBox.setEnabled(true);
		}
	}

	private void jFlagRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (jFlagRadioButton.isSelected()) {
			jSearchOptionComboBox.setEnabled(false);
			jSearchTextField.setEditable(false);
		}
	}

	private void jNoValueRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (jNoValueRadioButton.isSelected()) {
			jSearchOptionComboBox.setEnabled(false);
			jSearchTextField.setEditable(false);
			jDatabaseCheckBox.setEnabled(false);
		}
	}
	
	class JTextFieldLimit extends PlainDocument {
		  private int limit;
		  JTextFieldLimit(int limit) {
		    super();
		    this.limit = limit;
		  }

		  JTextFieldLimit(int limit, boolean upper) {
		    super();
		    this.limit = limit;
		  }

		  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		    if (str == null)
		      return;

		    if (limit < 0 ||(getLength() + str.length()) <= limit) {
		      super.insertString(offset, str, attr);
		    }
		  }
		}



	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ModifierStringValueConstraintFrame(null, null).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.ButtonGroup buttonGroup;
	private javax.swing.JButton jCancelButton;
	//private javax.swing.JTable jEnumValueTable;
	private javax.swing.JRadioButton jFlagRadioButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JRadioButton jNoValueRadioButton;
	private javax.swing.JButton jOKButton;
	//private javax.swing.JScrollPane jTableScrollPane;
	private javax.swing.JRadioButton jTextValueRadioButton;
	private javax.swing.JComboBox jSearchOptionComboBox;
	private javax.swing.JTextField jSearchTextField;
	private javax.swing.JCheckBox jDatabaseCheckBox;
	// End of variables declaration

}
