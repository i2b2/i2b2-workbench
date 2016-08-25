/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */
package edu.harvard.i2b2.query.ui;


//import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.eclipse.UserInfoBean;
//import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

//import org.eclipse.swt.SWT;



/*
 * EnumValueConstrainFrame.java
 * 
 * Created on December 7, 2007, 11:58 AM
 */

@SuppressWarnings("serial")
public class StringValueConstraintFrame extends javax.swing.JFrame {
	private GroupPanel parent_;

	/** Creates new form EnumValueConstrainFrame */
	public StringValueConstraintFrame(GroupPanel parent) {
		parent_ = parent;

		initComponents();

		buttonGroup.add(jNoValueRadioButton);
		buttonGroup.add(jFlagRadioButton);
		buttonGroup.add(jTextValueRadioButton);

		jNoValueRadioButton.setSelected(true);
		//jEnumValueTable.setEnabled(false);
		//jEnumValueTable.setBackground(Color.LIGHT_GRAY);
		jSearchOptionComboBox.setEnabled(false);
		jSearchTextField.setEditable(false);
		jDatabaseCheckBox.setVisible(false);
		
		jSearchTextField.setDocument(new JTextFieldLimit(parent_.currentData().valuePropertyData().searchStrLength()));

		if((parent_.currentData()).valuePropertyData().isLongText()) {
			//jSearchOptionComboBox.setEnabled(false);
			jFlagRadioButton.setEnabled(false);
			jSearchOptionComboBox.setVisible(false);//setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Containing",  "Containing[database]"}));
			jSearchTextField.setBounds(30, 128, 300, 20);
			jDatabaseCheckBox.setVisible(true);
			jDatabaseCheckBox.setEnabled(false);
		}
		
		setPreviousValues();
		
		if ((!(UserInfoBean.getInstance().isRoleInProject("DATA_DEID")))
				&& (parent_.currentData()).valuePropertyData().isLongText()) {
			jTextValueRadioButton.setEnabled(false);
		}
	}

	private void setPreviousValues() {
		if ((parent_.currentData()).valuePropertyData().hasStringValue()) {
			if ((parent_.currentData()).valuePropertyData().useValueFlag()) {
				jFlagRadioButton.setSelected(true);
			} else if ((parent_.currentData()).valuePropertyData().useStringValue()) {

				jTextValueRadioButton.setSelected(true);
				jTextValueRadioButtonActionPerformed(null);
				jSearchTextField.setText((parent_.currentData()).valuePropertyData().value());
				
				if((parent_.currentData()).valuePropertyData().isLongText()) {
					if((parent_.currentData()).valuePropertyData().operator().equalsIgnoreCase("CONTAINS")) {
						jDatabaseCheckBox.setSelected(false);
					}
					else if((parent_.currentData()).valuePropertyData().operator().equalsIgnoreCase("CONTAINS[database]")) {
						jDatabaseCheckBox.setSelected(true);
					}
				} else {
					if((parent_.currentData()).valuePropertyData().operator().equalsIgnoreCase("LIKE[contains]")) {
						jSearchOptionComboBox.setSelectedIndex(0);
					}
					else if((parent_.currentData()).valuePropertyData().operator().equalsIgnoreCase("LIKE[exact]")) {
						jSearchOptionComboBox.setSelectedIndex(1);
					}
					else if((parent_.currentData()).valuePropertyData().operator().equalsIgnoreCase("LIKE[begin]")) {
						jSearchOptionComboBox.setSelectedIndex(2);
					}
					else if((parent_.currentData()).valuePropertyData().operator().equalsIgnoreCase("LIKE[end]")) {
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
				.setText("with the term "+(parent_.currentData()).titleName()+".");
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

		jSearchOptionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Containing", "Exact", "Starting with", "Ending with" }));
        getContentPane().add(jSearchOptionComboBox);
        jSearchOptionComboBox.setBounds(30, 130, 130, 22);

        getContentPane().add(jSearchTextField);
        jSearchTextField.setBounds(162, 130, 190, 20);

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
		//setVisible(false);
	}

	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		QueryConceptTreeNodeData data = parent_.currentData();
		data.valuePropertyData().selectedValues.clear();

		if (jNoValueRadioButton.isSelected()) {
			data.valuePropertyData().noValue(true);
			data.valuePropertyData().useValueFlag(false);
			data.valuePropertyData().useNumericValue(false);
			data.valuePropertyData().useTextValue(false);
			data.valuePropertyData().useStringValue(false);
			data.valueName("");
		} else if (jFlagRadioButton.isSelected()) {
			data.valuePropertyData().noValue(false);
			data.valuePropertyData().useValueFlag(true);
			data.valuePropertyData().useNumericValue(false);
			data.valuePropertyData().useTextValue(false);
			data.valuePropertyData().useStringValue(false);
			data.valuePropertyData().value("A");
			data.valueName(" = Abnormal");
		} else if (jTextValueRadioButton.isSelected()) {
			data.valuePropertyData().noValue(false);
			data.valuePropertyData().useValueFlag(false);
			data.valuePropertyData().useNumericValue(false);
			data.valuePropertyData().useTextValue(false);
			data.valuePropertyData().useStringValue(true);
			data.valuePropertyData().selectedValues.clear();
			
			/*for (int i = 0; i < jEnumValueTable.getRowCount(); i++) {
				Boolean selected = (Boolean) jEnumValueTable.getValueAt(i, 0);
				if (selected.equals(Boolean.TRUE)) {
					//String valStr = (String) jEnumValueTable.getValueAt(i, 1);
					String valStr = data.modifierValuePropertyData().enumValues.get(i);
					data.modifierValuePropertyData().selectedValues.add(valStr);
				}
			}*/
			data.valuePropertyData().value(jSearchTextField.getText().trim());
			
			if((parent_.currentData()).valuePropertyData().isLongText()) {
				if(!jDatabaseCheckBox.isSelected()) {
					data.valuePropertyData().operator("Contains");
				}
				else {// if(jDatabaseCheckBox.isSelected()) {
					data.valuePropertyData().operator("Contains[database]");
				}
			} else {
				if(jSearchOptionComboBox.getSelectedIndex() == 0) {
					data.valuePropertyData().operator("LIKE[contains]");
				}
				else if(jSearchOptionComboBox.getSelectedIndex() == 1) {
					data.valuePropertyData().operator("LIKE[exact]");
				}
				else if(jSearchOptionComboBox.getSelectedIndex() == 2) {
					data.valuePropertyData().operator("LIKE[begin]");
				}
				else if(jSearchOptionComboBox.getSelectedIndex() == 3) {
					data.valuePropertyData().operator("LIKE[end]");
				}
			}
			data.valueName(" ["+getOperatorDisplay(data.valuePropertyData().operator())+ " "
					+ "\""+data.valuePropertyData().value()+"\""+"]");
			/*if (data.valuePropertyData().selectedValues.size() > 1) {
				for (int j = 1; j < data.valuePropertyData().selectedValues
						.size(); j++) {
					data.valueName(data.valueName() + ","
							+ data.valuePropertyData().selectedValues.get(j));
				}
			}*/
		}
		data.name(parent_.currentData().titleName());
		parent_.setValueDisplay();

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

	private String getOperatorDisplay(String operator) {
		String result = operator;
		if (operator == null) {
			return result;
		}

		if (operator.equalsIgnoreCase("LIKE[contains]")) {
			result = "Contains";
		} else if (operator.equalsIgnoreCase("LIKE[begin]")) {
			result = "Begin with";
		} else if (operator.equalsIgnoreCase("LIKE[end]")) {
			result = "End with";
		} else if (operator.equalsIgnoreCase("LIKE[exact]")) {
			result = "Exact";
		} else if (operator.equalsIgnoreCase("CONTAINS[database]")) {
			result = "Contains";
		} else if (operator.equalsIgnoreCase("Contains")) {
			result = "Contains";
		} 

		return result;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new StringValueConstraintFrame(null).setVisible(true);
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
