/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

/*
 * QueryDisplayOptionsDialog.java
 * 
 *
 * Created on February 20, 2007, 10:14 AM
 */

package edu.harvard.i2b2.previousquery.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBElement;

import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.previousquery.util.PmServiceController;
import edu.harvard.i2b2.eclipse.plugins.previousquery.views.PreviousQueryView;
import edu.harvard.i2b2.previousquery.data.QueryMasterData;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pm.ConfigureType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pm.RoleType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pm.RolesType;

@SuppressWarnings("serial")
public class DisplayOptionsDialog extends javax.swing.JFrame {

	private PreviousQueryView previousQueryView_;
	private boolean isManager = false;
	private boolean hasProtectedAccess = false;

	/**
	 * Creates new form QueryDisplayOptionsDialog
	 */
	public DisplayOptionsDialog(PreviousQueryView previousQueryView) {

		previousQueryView_ = previousQueryView;

		initComponents();

		this.setSize(320, 325);
		setLocation(400, 100);

		// set up default properties if not previously set
		if (System.getProperty("PQSortByTimeCheckBox") == null)
			System.setProperty("PQSortByTimeCheckBox", "true");
		jSortByTimeCheckBox.setSelected(Boolean
				.getBoolean("PQSortByTimeCheckBox"));

		if ((System.getProperty("QueryToolMaxQueryNumber") == null))
			System.setProperty("QueryToolMaxQueryNumber", "20");
		jNumberOfQueryTextField.setText(System
				.getProperty("QueryToolMaxQueryNumber"));

		if ((System.getProperty("PQMaxPatientsNumber") == null))
			System.setProperty("PQMaxPatientsNumber", "200");
		jNumberOfPatientsTextField.setText(System
				.getProperty("PQMaxPatientsNumber"));

		if (System.getProperty("PQSortByNameCheckBox") == null)
			System.setProperty("PQSortByNameCheckBox", "false");
		jSortByNameCheckBox.setSelected(Boolean
				.getBoolean("PQSortByNameCheckBox"));

		//Boolean ascending = previousQueryView_.runTreePanel().ascending();
		//System.setProperty("PQDescending", String.valueOf(!ascending));
		//jDescendingRadioButton.setSelected(!ascending);
		//System.setProperty("PQAscending", String.valueOf(ascending));
		//jAscendingRadioButton.setSelected(ascending);

		ArrayList<String> roles = (ArrayList<String>) UserInfoBean
				.getInstance().getProjectRoles();

		for (String param : roles) {
			if (param.equalsIgnoreCase("manager")) {
				isManager = true;
				if (System.getProperty("PQDisplayGroup") == null)
					System.setProperty("PQDisplayGroup", "true");
				//jDisplayGroupCheckBox.setSelected(Boolean
						//.getBoolean("PQDisplayGroup"));
				break;
			}
		}

		for (String param : roles) {
			if (param.equalsIgnoreCase("protected_access")) {
				hasProtectedAccess = true;
				break;
			}
		}

		if (!isManager) {
			//jDisplayGroupCheckBox.setEnabled(false);
			jUserComboBox.setEnabled(false);
		}

		if (hasProtectedAccess) {
			// Disable the show name button/feature for now as we have no names
			// in our db...
			// jShowNameRadioButton.setSelected(previousQueryView_.runTreePanel()
			// .showName());
			// Be sure to activate jShowDemographicsRadioButtonActionPerformed
			// method also
			jShowNameRadioButton.setEnabled(false);
			previousQueryView_.runTreePanel().showName(false);

			jShowDemographicsRadioButton.setSelected(!(previousQueryView_
					.runTreePanel().showName()));
		} else {
			jShowNameRadioButton.setSelected(false);
			jShowDemographicsRadioButton.setSelected(true);
			jShowNameRadioButton.setEnabled(false);
			jShowDemographicsRadioButton.setEnabled(false);
		}
		
		if(!isManager) {
			return;
		}
		
		if(previousQueryView_.runTreePanel().users == null) {
			previousQueryView_.runTreePanel().users = new ArrayList<String>();
			PmServiceController pms = new PmServiceController();
			try {
				PasswordType ptype = new PasswordType();
				ptype.setIsToken(UserInfoBean.getInstance().getUserPasswordIsToken());
				ptype.setTokenMsTimeout(UserInfoBean.getInstance()
						.getUserPasswordTimeout());
				ptype.setValue(UserInfoBean.getInstance().getUserPassword());
				String response = pms.getUserInfo(UserInfoBean.getInstance().getUserName(), ptype, UserInfoBean.getInstance().getSelectedProjectUrl(), 
						UserInfoBean.getInstance().getUserDomain(), false, UserInfoBean.getInstance().getProjectId());
			
				////
				JAXBUtil jaxbUtil = new JAXBUtil(new String[] {
						"edu.harvard.i2b2.crcxmljaxb.datavo.pm", //$NON-NLS-1$
						"edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message" //$NON-NLS-1$
				});
				JAXBElement jaxbElement = jaxbUtil.unMashallFromString(response);
				ResponseMessageType responseMessageType = (ResponseMessageType) jaxbElement.getValue();

				String procStatus = responseMessageType.getResponseHeader().getResultStatus().getStatus().getType();
				//String procMessage = responseMessageType.getResponseHeader().getResultStatus().getStatus().getValue();

				//String serverVersion = responseMessageType.getMessageHeader()
				//.getSendingApplication().getApplicationVersion();
				//System.setProperty("serverVersion", serverVersion);
				
				if(procStatus.equals("ERROR")){ //$NON-NLS-1$
					//setMsg(procMessage);				
				}
				else if(procStatus.equals("WARNING")){ //$NON-NLS-1$
					//setMsg(procMessage);
				}	
				else {
					BodyType bodyType = responseMessageType.getMessageBody();
					JAXBUnWrapHelper helper = new JAXBUnWrapHelper(); 
					RolesType rolesType = (RolesType)helper.getObjectByClass(bodyType.getAny(), RolesType.class);
					ArrayList<String> tmpArr = new ArrayList<String>();
					for(int i=0; i<rolesType.getRole().size(); i++) {
						RoleType role = rolesType.getRole().get(i);
						tmpArr.add(role.getUserName());												
					}
					
					Collections.sort(tmpArr, new Comparator<String>() {
						public int compare(String d1, String d2) {
							return java.text.Collator.getInstance().compare(d1, d2);
						}
					});
					String curStr = tmpArr.get(0);
					previousQueryView_.runTreePanel().users.add(curStr);
					jUserComboBox.addItem(curStr);
					for(int j=0; j<tmpArr.size(); j++) {
						String user = tmpArr.get(j);
						if(user.equalsIgnoreCase(curStr)) {
							continue;
						}
						previousQueryView_.runTreePanel().users.add(user);
						jUserComboBox.addItem(user);
						curStr = user;
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			for(int i=0; i<previousQueryView_.runTreePanel().users.size(); i++) {
				jUserComboBox.addItem(previousQueryView_.runTreePanel().users.get(i));						
			}
		}
		
		jUserComboBox.setSelectedIndex(previousQueryView_.runTreePanel().userIndex());
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * 
	 */
	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();
		jSortByTimeCheckBox = new javax.swing.JCheckBox();
		jSortByNameCheckBox = new javax.swing.JCheckBox();
		jOKButton = new javax.swing.JButton();
		jCancelButton = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jNumberOfQueryTextField = new javax.swing.JTextField();
		jNumberOfPatientsTextField = new javax.swing.JTextField();
		jPanel2 = new javax.swing.JPanel();
		jShowNameRadioButton = new javax.swing.JRadioButton();
		jShowDemographicsRadioButton = new javax.swing.JRadioButton();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Previous Queries Options Dialog");
		java.awt.Image img = this.getToolkit().getImage(
				DisplayOptionsDialog.class.getResource("core-cell.gif"));
		this.setIconImage(img);
		jPanel1.setLayout(null);

		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Order of display"));
		jSortByTimeCheckBox.setText("sort by time when the query was created");
		jSortByTimeCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jSortByTimeCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jSortByTimeCheckBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jSortByTimeCheckBoxActionPerformed(evt);
					}
				});

		jPanel1.add(jSortByTimeCheckBox);
		jSortByTimeCheckBox.setBounds(20, 30, 230, 15);

		jSortByNameCheckBox.setText("sort by query name");
		jSortByNameCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jSortByNameCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jSortByNameCheckBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jSortByNameCheckBoxActionPerformed(evt);
					}
				});

		jPanel1.add(jSortByNameCheckBox);
		jSortByNameCheckBox.setBounds(20, 60, 220, 15);

		//getContentPane().add(jPanel1);
		//jPanel1.setBounds(10, 60, 285, 88);

		jOKButton.setText("OK");
		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jOKButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jOKButton);
		jOKButton.setBounds(53, 241, 60, 23);

		jCancelButton.setText("Close");
		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCancelButtonActionPerformed(evt);
			}
		});

		getContentPane().add(jCancelButton);
		jCancelButton.setBounds(174, 241, 80, 23);

		jLabel1.setText("Maximum number of queries to be displayed: ");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(18, 10, 228, 20);

		getContentPane().add(jNumberOfQueryTextField);
		jNumberOfQueryTextField.setBounds(248, 10, 45, 20);

		jLabel2.setText("Maximum number of patients to be displayed: ");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(18, 30, 228, 20);

		getContentPane().add(jNumberOfPatientsTextField);
		jNumberOfPatientsTextField.setBounds(248, 30, 45, 20);

		jPanel2.setLayout(null);

		jPanel2.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Patient labels"));
		jShowNameRadioButton
				.setText("Show Names (Protected health information)");
		jShowNameRadioButton.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jShowNameRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jShowNameRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jShowNameRadioButtonActionPerformed(evt);
					}
				});

		jPanel2.add(jShowNameRadioButton);
		jShowNameRadioButton.setBounds(20, 30, 260, 16);

		jShowDemographicsRadioButton
				.setText("Show Demographics (De-identified data)");
		jShowDemographicsRadioButton.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		jShowDemographicsRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jShowDemographicsRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jShowDemographicsRadioButtonActionPerformed(evt);
					}
				});

		jPanel2.add(jShowDemographicsRadioButton);
		jShowDemographicsRadioButton.setBounds(20, 60, 250, 16);

		getContentPane().add(jPanel2);
		jPanel2.setBounds(12, 62, 284, 102);
		{
			jLabel3 = new JLabel();
			getContentPane().add(jLabel3);
			jLabel3.setText("Get previous queries for");
			jLabel3.setBounds(18, 192, 125, 16);
		}
		{
			jUserComboBox = new JComboBox();
			getContentPane().add(jUserComboBox);
			jUserComboBox.setBounds(143, 190, 129, 21);
			jUserComboBox.addItem("all users");
			/*jUserComboBox.addItem("demo");
			jUserComboBox.addItem("wp1");
			jUserComboBox.addItem("demo");
			jUserComboBox.addItem("wp1");
			jUserComboBox.addItem("demo");
			jUserComboBox.addItem("wp1");
			jUserComboBox.addItem("demo");
			jUserComboBox.addItem("wp1");
			jUserComboBox.addItem("demo");
			jUserComboBox.addItem("wp1");
			jUserComboBox.addItem("demo");
			jUserComboBox.addItem("wp1");*/
		}

		pack();
	}

	private void jDisplayGroupCheckBoxActionPerformed(
			java.awt.event.ActionEvent evt) {
		//Boolean isSelected = jDisplayGroupCheckBox.isSelected();
		//System.setProperty("PQDisplayGroup", String.valueOf(isSelected));
		/*
		 * String num = jNumberOfQueryTextField.getText();
		 * System.setProperty("QueryToolMaxQueryNumber", num); String status =
		 * previousQueryView_
		 * .runTreePanel().loadPreviousQueries(jDisplayGroupCheckBox
		 * .isSelected());
		 * 
		 * if(status.equalsIgnoreCase("")) {
		 * previousQueryView_.runTreePanel().reset(new Integer(num).intValue(),
		 * jSortByNameCheckBox.isSelected()); } else
		 * if(status.equalsIgnoreCase("CellDown")){ final JFrame parent = this;
		 * java.awt.EventQueue.invokeLater(new Runnable() { public void run() {
		 * JOptionPane.showMessageDialog(parent, "Trouble with connection to the
		 * remote server, " + "this is often a network error, please try again",
		 * "Network Error", JOptionPane.INFORMATION_MESSAGE); } }); }
		 */
	}

	private void jShowDemographicsRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		// enable this when show name button is enabled.
		/*
		 * if (jShowDemographicsRadioButton.isSelected()) {
		 * jShowNameRadioButton.setSelected(false); } else {
		 * jShowNameRadioButton.setSelected(true); }
		 */
	}

	private void jShowNameRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (jShowNameRadioButton.isSelected()) {
			jShowDemographicsRadioButton.setSelected(false);
		} else {
			jShowDemographicsRadioButton.setSelected(true);
		}
	}

	private void jDescendingRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		//Boolean isSelected = jDescendingRadioButton.isSelected();
		//System.setProperty("PQDescending", String.valueOf(isSelected));
		//jAscendingRadioButton.setSelected(!isSelected);
		//previousQueryView_.runTreePanel().ascending(!isSelected);

		// String num = jNumberOfQueryTextField.getText();
		// previousQueryView_.runTreePanel().reset(new Integer(num).intValue(),
		// jSortByNameCheckBox.isSelected());
	}

	private void jAscendingRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		//Boolean isSelected = jAscendingRadioButton.isSelected();
		//System.setProperty("PQAscending", String.valueOf(isSelected));
		//jDescendingRadioButton.setSelected(!isSelected);
		//previousQueryView_.runTreePanel().ascending(isSelected);

		// String num = jNumberOfQueryTextField.getText();
		// previousQueryView_.runTreePanel().reset(new Integer(num).intValue(),
		// jSortByNameCheckBox.isSelected());
	}

	private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				previousQueryView_.getViewSite().getShell().getDisplay()
						.syncExec(new Runnable() {
							public void run() {
								setVisible(false);
							}
						});
			}
		});
	}

	private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// setVisible(false);

		previousQueryView_.runTreePanel().showName(
				jShowNameRadioButton.isSelected());
		System.out.println("Show Name: "
				+ (previousQueryView_.runTreePanel().showName() ? "true"
						: "false"));
		
		previousQueryView_.runTreePanel().userIndex(jUserComboBox.getSelectedIndex());
		final String num = jNumberOfQueryTextField.getText();
		System.setProperty("QueryToolMaxQueryNumber", num);

		String numPat = jNumberOfPatientsTextField.getText();
		System.setProperty("PQMaxPatientsNumber", numPat);
		final JFrame parent = this;

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				String user = UserInfoBean.getInstance().getUserName();
				if(isManager) {
					user = (String)jUserComboBox.getSelectedItem();
				}
				String status = previousQueryView_.runTreePanel().loadPreviousQueries(user);//jDisplayGroupCheckBox.isSelected());
				//String status = previousQueryView_.runTreePanel();
				if (status.equalsIgnoreCase("")) {

					previousQueryView_.runTreePanel().reset(
							new Integer(num).intValue(),
							jSortByNameCheckBox.isSelected(),
							false);

					previousQueryView_.getViewSite().getShell().getDisplay()
							.syncExec(new Runnable() {
								public void run() {
									setVisible(false);
								}
							});
				} else if (status.equalsIgnoreCase("CellDown")) {

					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane
									.showMessageDialog(
											parent,
											"Trouble with connection to the remote server, "
													+ "this is often a network error, please try again",
											"Network Error",
											JOptionPane.INFORMATION_MESSAGE);
						}
					});
				}
			}
		});
	}

	private void jSortByNameCheckBoxActionPerformed(
			java.awt.event.ActionEvent evt) {
		Boolean isSelected = jSortByNameCheckBox.isSelected();
		System.setProperty("PQSortByNameCheckBox", String.valueOf(isSelected));
		System.setProperty("PQSortByTimeCheckBox", String.valueOf(!isSelected));
		jSortByTimeCheckBox.setSelected(!isSelected);

		// previousQueryView_.runTreePanel().reset(new Integer(num).intValue(),
		// true);
	}

	private void jSortByTimeCheckBoxActionPerformed(
			java.awt.event.ActionEvent evt) {
		Boolean isSelected = jSortByTimeCheckBox.isSelected();
		System.setProperty("PQSortByTimeCheckBox", String.valueOf(isSelected));
		System.setProperty("PQSortByNameCheckBox", String.valueOf(!isSelected));
		jSortByNameCheckBox.setSelected(!isSelected);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new DisplayOptionsDialog(null).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.JButton jCancelButton;
	private JLabel jLabel3;
	private JComboBox jUserComboBox;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JTextField jNumberOfQueryTextField;
	private javax.swing.JTextField jNumberOfPatientsTextField;
	private javax.swing.JButton jOKButton;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JCheckBox jSortByNameCheckBox;
	private javax.swing.JCheckBox jSortByTimeCheckBox;
	private javax.swing.JRadioButton jShowDemographicsRadioButton;
	private javax.swing.JRadioButton jShowNameRadioButton;
	// End of variables declaration
}
