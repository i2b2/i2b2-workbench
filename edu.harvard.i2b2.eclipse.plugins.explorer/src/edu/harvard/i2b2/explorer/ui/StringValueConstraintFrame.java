/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */
package edu.harvard.i2b2.explorer.ui;


//import edu.harvard.i2b2.query.data.ModifierData;
//import edu.harvard.i2b2.query.data.ModifierData;
//import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.explorer.dataModel.KTable;
import edu.harvard.i2b2.explorer.dataModel.QueryModel;

/*
 * EnumValueConstrainFrame.java
 * 
 * Created on December 7, 2007, 11:58 AM
 */

@SuppressWarnings("serial")
public class StringValueConstraintFrame extends javax.swing.JFrame {
	private QueryModel data;
	private KTable table;

	/** Creates new form EnumValueConstrainFrame */
	public StringValueConstraintFrame(QueryModel data_, KTable table_) {
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
		
		jSearchTextField.setDocument(new JTextFieldLimit(data_.valueModel().searchStrLength()));

		if(data.valueModel().isLongText()) {
			//jSearchOptionComboBox.setEnabled(false);
			jFlagRadioButton.setEnabled(false);
			jSearchOptionComboBox.setVisible(false);//setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Containing",  "Containing[database]"}));
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
		if (data.valueModel().hasStringValue()) {
			if (data.valueModel().useValueFlag()) {
				jFlagRadioButton.setSelected(true);
			} else if (data.valueModel().useStringValue()) {

				jTextValueRadioButton.setSelected(true);
				jTextValueRadioButtonActionPerformed(null);
				jSearchTextField.setText(data.valueModel().value());
				
				if(data.valueModel().isLongText()) {
					if(data.valueModel().operator().equalsIgnoreCase("CONTAINS")) {
						jDatabaseCheckBox.setSelected(false);
					}
					else if(data.valueModel().operator().equalsIgnoreCase("CONTAINS[database]")) {
						jDatabaseCheckBox.setSelected(true);
					}
				}
				else {
					if(data.valueModel().operator().equalsIgnoreCase("LIKE[contains]")) {
						jSearchOptionComboBox.setSelectedIndex(0);
					}
					else if(data.valueModel().operator().equalsIgnoreCase("LIKE[exact]")) {
						jSearchOptionComboBox.setSelectedIndex(1);
					}
					else if(data.valueModel().operator().equalsIgnoreCase("LIKE[begin]")) {
						jSearchOptionComboBox.setSelectedIndex(2);
					}
					else if(data.valueModel().operator().equalsIgnoreCase("LIKE[end]")) {
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
		jHelpButton = new javax.swing.JButton();
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

		jOKButton.setText("OK");
		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jOKButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jOKButton);
		jOKButton.setBounds(55, 180, 65, 23);

		jCancelButton.setText("Cancel");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jCancelButton);
		jCancelButton.setBounds(140, 180, 80, 23);
		
		jHelpButton.setText("Help");
		jHelpButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jHelpButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jHelpButton);
		jHelpButton.setBounds(240, 180, 80, 23);

		/*jEnumValueTable.setModel(new DefaultTableModel(new Object[][] {
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
		jEnumValueTable.setTableHeader(null);*/
		// jEnumValueTable.setPreferredSize(new Dimension(310, 110));
		//jTableScrollPane.setViewportView(jEnumValueTable);

		//getContentPane().add(jTableScrollPane);
		//jTableScrollPane.setBounds(50, 130, 310, 110);
		jSearchOptionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Containing", "Exact", "Starting with", "Ending with" }));
        getContentPane().add(jSearchOptionComboBox);
        jSearchOptionComboBox.setBounds(30, 130, 100, 22);

        getContentPane().add(jSearchTextField);
        jSearchTextField.setBounds(140, 130, 190, 20);

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
	
	private void jHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {
		//JOptionPane.showMessageDialog(this, "\n\n\n\n\n\n\n\n\n\n\n\n\n\n", "Help", JOptionPane.PLAIN_MESSAGE);
	}

	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		//QueryConceptTreeNodeData data = parent_.currentData();
		data.valueModel().selectedValues.clear();

		if (jNoValueRadioButton.isSelected()) {
			data.valueModel().noValue(true);
			data.valueModel().useValueFlag(false);
			data.valueModel().useNumericValue(false);
			data.valueModel().useTextValue(false);
			data.valueModel().useStringValue(false);
			data.valueName("");
		} else if (jFlagRadioButton.isSelected()) {
			data.valueModel().noValue(false);
			data.valueModel().useValueFlag(true);
			data.valueModel().useNumericValue(false);
			data.valueModel().useTextValue(false);
			data.valueModel().useStringValue(false);
			data.valueModel().value("A");
			data.valueName(" = Abnormal");
		} else if (jTextValueRadioButton.isSelected()) {
			data.valueModel().noValue(false);
			data.valueModel().useValueFlag(false);
			data.valueModel().useNumericValue(false);
			data.valueModel().useTextValue(false);
			data.valueModel().useStringValue(true);
			data.valueModel().selectedValues.clear();

			/*for (int i = 0; i < jEnumValueTable.getRowCount(); i++) {
				Boolean selected = (Boolean) jEnumValueTable.getValueAt(i, 0);
				if (selected.equals(Boolean.TRUE)) {
					//String valStr = (String) jEnumValueTable.getValueAt(i, 1);
					String valStr = data.modifiervalueModel().enumValues.get(i);
					data.modifiervalueModel().selectedValues.add(valStr);
				}
			}*/
			data.valueModel().value(jSearchTextField.getText().trim());
			
			if(data.valueModel().isLongText()) {
				if(!jDatabaseCheckBox.isSelected()) {
					data.valueModel().operator("CONTAINS");
				}
				else {//if(jSearchOptionComboBox.getSelectedIndex() == 1) {
					data.valueModel().operator("CONTAINS[database]");
				}
			}
			else {
				if(jSearchOptionComboBox.getSelectedIndex() == 0) {
					data.valueModel().operator("LIKE[contains]");
				}
				else if(jSearchOptionComboBox.getSelectedIndex() == 1) {
					data.valueModel().operator("LIKE[exact]");
				}
				else if(jSearchOptionComboBox.getSelectedIndex() == 2) {
					data.valueModel().operator("LIKE[begin]");
				}
				else if(jSearchOptionComboBox.getSelectedIndex() == 3) {
					data.valueModel().operator("LIKE[end]");
				}
			}
			data.valueName(" "+data.valueModel().operator()+ " "
					+ data.valueModel().value());
			/*if (data.valueModel().selectedValues.size() > 1) {
				for (int j = 1; j < data.valueModel().selectedValues
						.size(); j++) {
					data.valueName(data.valueName() + ","
							+ data.valueModel().selectedValues.get(j));
				}
			}*/
		}
		//data.name(parent_.currentData().titleName());
		//data.setValueDisplay();

		table.getModel().setContentAt(3, table.selectedRow, data.valueName());
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

		    if (limit < 0 || (getLength() + str.length()) <= limit) {
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
				new StringValueConstraintFrame(null, null).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.ButtonGroup buttonGroup;
	private javax.swing.JButton jCancelButton;
	private javax.swing.JButton jHelpButton;
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
