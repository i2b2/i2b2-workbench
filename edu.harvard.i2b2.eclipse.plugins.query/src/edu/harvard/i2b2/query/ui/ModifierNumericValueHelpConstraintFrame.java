/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * Contributors: 
 * 
 *     Wensong Pan
 *     
 */

package edu.harvard.i2b2.query.ui;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.query.data.UnitsData;

public class ModifierNumericValueHelpConstraintFrame extends javax.swing.JFrame {
	private GroupPanel parent_;

	/** Creates new form ValueConstrainFrame */
	public ModifierNumericValueHelpConstraintFrame(GroupPanel parent) {
		parent_ = parent;

		initComponents();

		buttonGroup.add(jNoValueRadioButton);
		buttonGroup.add(jFlagRadioButton);
		buttonGroup.add(jNumericRadioButton);

		// jNoValueRadioButton.setSelected(true);
		// jFlagPanel.setVisible(false);
		// jBetweenValuePanel.setVisible(false);
		// jNumericPanel.setVisible(false);

		if (!((ModifierData)parent_.currentData()).modifierValuePropertyData().okToUseValueFlag()) {
			jFlagRadioButton.setEnabled(false);
		}

		if (!((ModifierData)parent_.currentData()).modifierValuePropertyData().okToUseValue()) {
			jNumericRadioButton.setEnabled(false);
		}

		// jUnitsComboBox.removeAllItems();
		for (int i = 0; i < ((ModifierData)parent_.currentData()).modifierValuePropertyData().units
				.size(); i++) {
			jUnitsComboBox
					.addItem(((ModifierData)parent_.currentData()).modifierValuePropertyData().units
							.get(i));
		}
		setPreviousValues();
		// jUnitsComboBox.setSelectedIndex(0);
	}

	private void setPreviousValues() {
		if (((ModifierData)parent_.currentData()).modifierValuePropertyData().useNumericValue()) {
			jOperatorComboBox.setSelectedItem(((ModifierData)parent_.currentData()).modifierValuePropertyData().operator());

			jNumericRadioButton.setSelected(true);
			jNumericRadioButtonActionPerformed(null);
			jOperatorComboBoxActionPerformed(null);
			if (((ModifierData)parent_.currentData()).modifierValuePropertyData().operator().equals(
					"BETWEEN")) {
				jLowValueTextField.setText(parent_.currentData()
						.valuePropertyData().lowValue());
				jHighTextField.setText(parent_.currentData()
						.valuePropertyData().highValue());
			} else // if (
			// ((ModifierData)parent_.currentData()).modifierValuePropertyData().value().equals
			// ("L"))
			{
				jValueTextField.setText(((ModifierData)parent_.currentData()).modifierValuePropertyData().value());
			}
		} else if (((ModifierData)parent_.currentData()).modifierValuePropertyData().useValueFlag()) {
			if (((ModifierData)parent_.currentData()).modifierValuePropertyData().value().equals("H")) {
				jFlagComboBox.setSelectedIndex(0);
				jFlagRadioButton.setSelected(true);
				jFlagRadioButtonActionPerformed(null);
			} else if (((ModifierData)parent_.currentData()).modifierValuePropertyData().value()
					.equals("L")) {
				jFlagComboBox.setSelectedIndex(1);
				jFlagRadioButton.setSelected(true);
				jFlagRadioButtonActionPerformed(null);
			}
		}

		if (((ModifierData)parent_.currentData()).hasValue()
				&& ((ModifierData)parent_.currentData()).modifierValuePropertyData().unit() != null) {
			jUnitsComboBox.setSelectedItem(parent_.currentData()
					.valuePropertyData().unit());
		}

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		buttonGroup = new javax.swing.ButtonGroup();
		jOKButton = new javax.swing.JButton();
		jCancelButton = new javax.swing.JButton();
		jHelpButton = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jNoValueRadioButton = new javax.swing.JRadioButton();
		jFlagRadioButton = new javax.swing.JRadioButton();
		jNumericRadioButton = new javax.swing.JRadioButton();
		jNoValueButton = new javax.swing.JButton();
		jFlagButton = new javax.swing.JButton();
		jNumericButton = new javax.swing.JButton();
		jLabel3 = new javax.swing.JLabel();
		jUnitsComboBox = new javax.swing.JComboBox();
		jFlagPanel = new javax.swing.JPanel();
		jLabel4 = new javax.swing.JLabel();
		jFlagComboBox = new javax.swing.JComboBox();
		jNumericPanel = new javax.swing.JPanel();
		jLabel5 = new javax.swing.JLabel();
		jOperatorComboBox = new javax.swing.JComboBox();
		jLabel6 = new javax.swing.JLabel();
		jValuePanel = new javax.swing.JPanel();
		jValueTextField = new javax.swing.JTextField();
		jBetweenValuePanel = new javax.swing.JPanel();
		jLowValueTextField = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();
		jHighTextField = new javax.swing.JTextField();

		// Set all panels to off
		// jNoValueRadioButton.setSelected(true);
		jFlagPanel.setVisible(false);
		jBetweenValuePanel.setVisible(false);
		jNumericPanel.setVisible(false);

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jOKButton.setText("OK");
		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jOKButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jOKButton);
//		jOKButton.setBounds(220, 150, 65, 23);
		jOKButton.setBounds(110, 180, 65, 23);


		jCancelButton.setText("Cancel");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jCancelButton);
		//	jCancelButton.setBounds(220, 180, 80, 23);
		jCancelButton.setBounds(175, 180, 80, 23);

		ModifierData data = (ModifierData)parent_.currentData();
		String text = data.modifier_helpName();
		if(text == null)
			text = "Help";
		jHelpButton.setText(text);
		jHelpButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jHelpButtonActionPerformed(evt).start();
			}
		});
		getContentPane().add(jHelpButton);
		jHelpButton.setBounds(255, 180, 140, 23);

	
		jLabel1
				.setText("Searches by Lab values can be constrained by the high/low flag set");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(20, 10, 340, 20);

		jLabel2
				.setText(" by the performing laboratory, or by the values themselves.");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(20, 30, 340, 20);

		jNoValueRadioButton.setText("No Value");
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
		jNoValueRadioButton.setBounds(20, 60, 79, 18);

		jFlagRadioButton.setText("By high/low flag");
		jFlagRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 0, 0, 0));
		jFlagRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jFlagRadioButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jFlagRadioButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jFlagRadioButton);
		jFlagRadioButton.setBounds(20, 90, 110, 18);

		jNumericRadioButton.setText("By numeric value");
		jNumericRadioButton.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jNumericRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jNumericRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jNumericRadioButtonActionPerformed(evt);
					}
				});

		getContentPane().add(jNumericRadioButton);
		jNumericRadioButton.setBounds(20, 120, 130, 18);

		jNoValueButton.setText("?");
		jNoValueButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jNoValueButton.setIconTextGap(1);
		jNoValueButton.setInheritsPopupMenu(true);
		jNoValueButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jNoValueButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jNoValueButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jNoValueButton);
		jNoValueButton.setBounds(140, 60, 45, 20);

		jFlagButton.setText("?");
		jFlagButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jFlagButton.setIconTextGap(1);
		jFlagButton.setInheritsPopupMenu(true);
		jFlagButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jFlagButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jFlagButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jFlagButton);
		jFlagButton.setBounds(140, 90, 45, 20);

		jNumericButton.setText("?");
		jNumericButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jNumericButton.setIconTextGap(1);
		jNumericButton.setInheritsPopupMenu(true);
		jNumericButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jNumericButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jNumericButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jNumericButton);
		jNumericButton.setBounds(140, 120, 45, 20);

		jLabel3.setText("units =");
		getContentPane().add(jLabel3);
		jLabel3.setBounds(20, 150, 40, 20);

		// jUnitsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new
		// String[] { "mg%", "mg/dl" }));
		jUnitsComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jUnitsComboBoxActionPerformed(evt);
			}
		});

		getContentPane().add(jUnitsComboBox);
		jUnitsComboBox.setBounds(60, 150, 80, 22);

		jFlagPanel.setLayout(null);

		jFlagPanel.setEnabled(false);
		jLabel4.setText("Please select range:");
		jFlagPanel.add(jLabel4);
		jLabel4.setBounds(10, 10, 130, 20);

		jFlagComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "HIGH", "LOW" }));
		jFlagComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jFlagComboBoxActionPerformed(evt);
			}
		});

		jFlagPanel.add(jFlagComboBox);
		jFlagComboBox.setBounds(10, 32, 140, 22);

		getContentPane().add(jFlagPanel);
		jFlagPanel.setBounds(192, 60, 168, 70);

		jNumericPanel.setLayout(null);

		jLabel5.setText("Please select operator:");
		jNumericPanel.add(jLabel5);
		jLabel5.setBounds(10, 0, 170, 20);

		jOperatorComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "LESS THAN (<)", "LESS THAN OR EQUAL TO (<=)",
						"EQUAL TO (=)", "BETWEEN", "GREATER THAN (>)",
						"GREATER THAN OR EQUAL TO (>=)" }));
		jOperatorComboBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jOperatorComboBoxActionPerformed(evt);
					}
				});
		jNumericPanel.add(jOperatorComboBox);
		jOperatorComboBox.setBounds(10, 20, 200, 22);

		jLabel6.setText("Please enter value:");
		jNumericPanel.add(jLabel6);
		jLabel6.setBounds(10, 50, 200, 14);

		jValuePanel.setLayout(null);

		jValuePanel.add(jValueTextField);
		jValueTextField.setBounds(0, 0, 200, 20);

		jNumericPanel.add(jValuePanel);
		jValuePanel.setBounds(10, 70, 200, 20);

		jBetweenValuePanel.setLayout(null);

		jBetweenValuePanel.add(jLowValueTextField);
		jLowValueTextField.setBounds(0, 0, 80, 20);

		jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18));
		jLabel7.setText("-");
		jBetweenValuePanel.add(jLabel7);
		jLabel7.setBounds(90, 0, 10, 22);

		jBetweenValuePanel.add(jHighTextField);
		jHighTextField.setBounds(109, 0, 90, 20);

		jNumericPanel.add(jBetweenValuePanel);
		jBetweenValuePanel.setBounds(10, 70, 200, 20);

		getContentPane().add(jNumericPanel);
		jNumericPanel.setBounds(190, 50, 223, 100);

		pack();
	}

	private void jOperatorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		String operator = (String) jOperatorComboBox.getSelectedItem();
		if (operator.equalsIgnoreCase("Between")) {
			jValuePanel.setVisible(false);
			jBetweenValuePanel.setVisible(true);
		} else {
			jValuePanel.setVisible(true);
			jBetweenValuePanel.setVisible(false);
		}
	}

	private void jFlagComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		// String operator = (String) jFlagComboBox.getSelectedItem();
	}

	private void jUnitsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {

	}

	private void jNumericRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (jNumericRadioButton.isSelected()) {
			jNumericPanel.setVisible(true);
			jFlagPanel.setVisible(false);
		} else {
			jNumericPanel.setVisible(false);
		}
	}

	private void jFlagRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
		jNumericPanel.setVisible(false);
		jFlagPanel.setVisible(true);
	}

	private void jNoValueRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		jNumericPanel.setVisible(false);
		jFlagPanel.setVisible(false);
	}

	private void jNumericButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JOptionPane
				.showMessageDialog(
						null,
						"Picking a numeric value will select those tests that, according to "
								+ "an operator, will be above, below, or between certain values. This\ngives fine control over the "
								+ "value that the test result must have in order for it to be selected. The disadvantage of using "
								+ "numeric\ncomparisons is that differences in calibration and errors in units can create erroneous results.");
	}

	private void jFlagButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JOptionPane
				.showMessageDialog(
						null,
						"Picking a value flag will select those tests determined to be "
								+ "abnormal by the laboratory that performed the test. This has an\nadvantage over selecting by "
								+ "numeric comparisons because differences in calibration and errors in units are eliminated. The\n"
								+ "disadvantage to numeric comparisons is that the laboratory may not be reliable in setting this flag.");
	}

	private void jNoValueButtonActionPerformed(java.awt.event.ActionEvent evt) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				JOptionPane
						.showMessageDialog(
								null,
								"Picking \"no value\" for the test value will select "
										+ "all of the tests irrespective of its associated value.");
			}
		});
	}

	private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
	}

	private Thread jHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {
		return new Thread() {
			public void run() {
				Display display = new Display();
				Shell shell = new Shell(display);
				Browser browser = new Browser(shell, SWT.NONE);

				shell.setLayout(new FillLayout());
				shell.setText("Genomic Help Browser");
				shell.open();

				//		String url = 
				//		"http://www.genenames.org/cgi-bin/quick_search.pl";

				//		String url = "http://genome.ucsc.edu/cgi-bin/hgGateway?hgsid=329368461";

				ModifierData data = (ModifierData)parent_.currentData();
				browser.setUrl(data.modifier_helpURL());

				while(!shell.isDisposed()) {
					if(!display.readAndDispatch()) {
						display.sleep();
					}
				}

				display.dispose();
			}
		};
	}
	
	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		ModifierData data = (ModifierData)parent_.currentData();

		if (jNoValueRadioButton.isSelected()) {
			data.modifierValuePropertyData().noValue(true);
			data.modifierValuePropertyData().useValueFlag(false);
			data.modifierValuePropertyData().useNumericValue(false);
			data.valueName("");
		} else if (jFlagRadioButton.isSelected()) {
			data.modifierValuePropertyData().noValue(false);
			data.modifierValuePropertyData().useValueFlag(true);
			data.modifierValuePropertyData().useNumericValue(false);
			if (jFlagComboBox.getSelectedIndex() == 0) {
				data.valueName(" = HIGH");
				data.modifierValuePropertyData().value("H");
			} else {
				data.valueName(" = LOW");
				data.modifierValuePropertyData().value("L");
			}
		} else if (jNumericRadioButton.isSelected()) {
			data.modifierValuePropertyData().noValue(false);
			data.modifierValuePropertyData().useValueFlag(false);
			data.modifierValuePropertyData().useNumericValue(true);
			
			UnitsData ud = null;
			if (jUnitsComboBox.getSelectedItem() != null) {
				ud = (UnitsData) jUnitsComboBox.getSelectedItem();
				data.modifierValuePropertyData().unit(ud.name());
			}
			
			String val = jValueTextField.getText();
			
			if(ud != null && ud.needConvertion()) {
				if(!val.equals("")){
					Double nval = Double.parseDouble(val);
					data.modifierValuePropertyData().value(""+nval*ud.mFactor());
				}
			}
			else {
				data.modifierValuePropertyData().value(val);
			}

			String operator = (String) jOperatorComboBox.getSelectedItem();
			data.modifierValuePropertyData().operator(operator);

			// deal with between...
			if (operator.equalsIgnoreCase("between")) {
				data.modifierValuePropertyData().lowValue(""+Double.parseDouble(jLowValueTextField.getText())*ud.mFactor());
				data.modifierValuePropertyData().highValue(""+Double.parseDouble(jHighTextField.getText())*ud.mFactor());
				data.valueName("(" + data.modifierValuePropertyData().lowValue()
						+ " - " + data.modifierValuePropertyData().highValue() + ")");
			} else {
				data.valueName(getOperator(data.modifierValuePropertyData().operator())
						+ data.modifierValuePropertyData().value());
			}
		}

		if (jUnitsComboBox.getSelectedItem() != null) {
			UnitsData ud = (UnitsData) jUnitsComboBox.getItemAt(0);
			data.modifierValuePropertyData().unit(ud.name());
		}

		data.name(parent_.currentData().titleName());
		// + parent_.currentData().valueName());
		parent_.setValueDisplay();
		// data.name(parent_.currentData().titleName() + data.valueName());

		setVisible(false);
	}

	private String getOperator(String opStr) {
		String result = "";
		if (opStr == null) {
			return result;
		}

		if (opStr.equalsIgnoreCase("LESS THAN (<)")) {
			result = "<";
		} else if (opStr.equalsIgnoreCase("LESS THAN OR EQUAL TO (<=)")) {
			result = "<=";
		} else if (opStr.equalsIgnoreCase("EQUAL TO (=)")) {
			result = "=";
		} else if (opStr.equalsIgnoreCase("GREATER THAN (>)")) {
			result = ">";
		} else if (opStr.equalsIgnoreCase("GREATER THAN OR EQUAL TO (>=)")) {
			result = ">=";
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
				new ModifierNumericValueHelpConstraintFrame(null).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.ButtonGroup buttonGroup;
	private javax.swing.JPanel jBetweenValuePanel;
	private javax.swing.JButton jCancelButton;
	private javax.swing.JButton jHelpButton;
	private javax.swing.JButton jFlagButton;
	private javax.swing.JComboBox jFlagComboBox;
	private javax.swing.JPanel jFlagPanel;
	private javax.swing.JRadioButton jFlagRadioButton;
	private javax.swing.JTextField jHighTextField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JTextField jLowValueTextField;
	private javax.swing.JButton jNoValueButton;
	private javax.swing.JRadioButton jNoValueRadioButton;
	private javax.swing.JButton jNumericButton;
	private javax.swing.JPanel jNumericPanel;
	private javax.swing.JRadioButton jNumericRadioButton;
	private javax.swing.JButton jOKButton;
	private javax.swing.JComboBox jOperatorComboBox;
	private javax.swing.JComboBox jUnitsComboBox;
	private javax.swing.JPanel jValuePanel;
	private javax.swing.JTextField jValueTextField;
	// End of variables declaration
}
