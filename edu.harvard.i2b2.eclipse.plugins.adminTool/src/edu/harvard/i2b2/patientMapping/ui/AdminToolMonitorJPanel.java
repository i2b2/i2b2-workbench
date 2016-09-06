/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */
package edu.harvard.i2b2.patientMapping.ui;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.adminTool.dataModel.PDOResponseMessageModel;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.im.query.AuditType;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.patientMapping.serviceClient.IMQueryClient;

@SuppressWarnings("serial")
public class AdminToolMonitorJPanel extends javax.swing.JPanel {
	
	private static final Log log = LogFactory.getLog(AdminToolMonitorJPanel.class);
	
    /** Creates new form AdminToolMonitorJPanel */
    public AdminToolMonitorJPanel() {
        initComponents();
        
        //String projectID = UserInfoBean.getInstance().getProjectId();
        //this.jProjectIdComboBox.addItem(projectID);
        
        for(int i=0; i<UserInfoBean.getInstance().getProjectList().size(); i++) {
        	String pid = UserInfoBean.getInstance().getProjectList().get(i);
        	jProjectIdComboBox.addItem(pid);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
    	java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jProjectIdComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jKeyTextField = new javax.swing.JTextField();
        jSetKeyButton = new javax.swing.JButton();
        jValidateKeyButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jPatientIdTextField = new javax.swing.JTextField();
        jAuditButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jUserIdTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jSiteNameTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel2.setLayout(null);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(jProjectIdComboBox);
        jProjectIdComboBox.setBounds(60, 10, 150, 20);

        jLabel1.setText("Project id:");
        jPanel2.add(jLabel1);
        jLabel1.setBounds(10, 10, 50, 20);

        jLabel2.setText("Key:");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(220, 10, 30, 20);

        jPanel2.add(jKeyTextField);
        jKeyTextField.setBounds(250, 10, 170, 20);

        jSetKeyButton.setText("Set Key");
        jSetKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSetKeyButtonActionPerformed(evt);
            }
        });

        jPanel2.add(jSetKeyButton);
        jSetKeyButton.setBounds(430, 10, 71, 23);

        jValidateKeyButton.setText("Validate Key");
        jValidateKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValidateKeyButtonActionPerformed(evt);
            }
        });

        jPanel2.add(jValidateKeyButton);
        jValidateKeyButton.setBounds(510, 10, 100, 23);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        //add(jPanel2, gridBagConstraints);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
            		"Project ID", "User ID", "Patient ID", "Site Name", "Import Time", "Comments"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jScrollPane1, gridBagConstraints);

        jPanel1.setLayout(null);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.add(jPatientIdTextField);
        jPatientIdTextField.setBounds(400, 10, 120, 20);

        jAuditButton.setText("Audit");
        jAuditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAuditButtonActionPerformed(evt);
            }
        });

        jPanel1.add(jAuditButton);
        jAuditButton.setBounds(550, 10, 59, 20);

        jLabel4.setText("Patient id:");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(340, 10, 60, 20);

        jLabel3.setText("User id:");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 10, 40, 20);

        //jUserIdTextField.setText("jTextField1");
        jPanel1.add(jUserIdTextField);
        jUserIdTextField.setBounds(55, 10, 100, 20);

        jLabel5.setText("Site Name:");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(165, 10, 60, 20);

        //jUserIdTextField.setText("jTextField1");
        jPanel1.add(jSiteNameTextField);
        jSiteNameTextField.setBounds(230, 10, 100, 20);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        add(jPanel1, gridBagConstraints);

    }

    private void jValidateKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	//String key = this.jKeyTextField.getText();
    	String projectID = UserInfoBean.getInstance().getProjectId();
    	//int i = UserInfoBean.getInstance().getProjectList().size();
    	String result = IMQueryClient.isKeySet(projectID);
    	
    	PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
    	try {
    		StatusType status = pdoresponsefactory.getStatusFromResponseXML(result);
    		final String info = status.getValue();
    		java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(jLabel2, info+".");
				}
			});
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    private void jSetKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	String key = this.jKeyTextField.getText();
    	String projectID = UserInfoBean.getInstance().getProjectId();
    	//int i = UserInfoBean.getInstance().getProjectList().size();
    	String result = IMQueryClient.setKey(key, projectID);
    	
    	PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
    	try {
    		StatusType status = pdoresponsefactory.getStatusFromResponseXML(result);
    		final String info = status.getValue();
    		java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(jLabel2, info+".");
				}
			});
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    //private void jAuditCommentsButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                     
    	
    //}                                                    

    private void jAuditButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
    	String userID = jUserIdTextField.getText();//UserInfoBean.getInstance().getUserName();
    	String patientID = jPatientIdTextField.getText();
    	String site = jSiteNameTextField.getText();
    	String projectID = (String) jProjectIdComboBox.getSelectedItem();
    		//UserInfoBean.getInstance().getProjectId();
    	
    	String result = IMQueryClient.getAudit(userID, patientID, projectID, site);
    	
    	PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
    	
    	//PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
    	try {
    		StatusType status = pdoresponsefactory.getStatusFromResponseXML(result);
    		if (!status.getType().equalsIgnoreCase("DONE")) {
	    		final String info = status.getValue();
	    		java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						JOptionPane.showMessageDialog(jLabel2, info+".");
					}
				});
	    		return;
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return;
    	}
    	
    	try {
	    	List<AuditType> factSets = pdoresponsefactory.getAuditsFromResponseXML(result);
			if (factSets != null) {
				log.info("Total audits: "+ factSets.size());
				jTable1.removeAll();
				DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
				model.setRowCount(factSets.size());
		        
				for(int i=0; i<factSets.size(); i++) {
					AuditType audit = factSets.get(i);
					jTable1.setValueAt(audit.getProjectId(), i, 0);
					jTable1.setValueAt(audit.getUserId(), i, 1);
					jTable1.setValueAt(audit.getPid(), i, 2);
					jTable1.setValueAt(audit.getSource(), i, 3);
					jTable1.setValueAt(audit.getImportDate(), i, 4);
					jTable1.setValueAt(audit.getComment(), i, 5);
				}
				// for(int i=0;
				// i<patientDimensionSet.getPatientDimension().size();i++) {
				// PatientDimensionType patientType =
				// patientDimensionSet.getPatientDimension().get(i);
				// System.out.println("PatientNum: " +
				// patientType.getPatientNum());
				// }
			} else {
				//return "error";
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		
		// / testing the visit set
		// PatientDataType.VisitDimensionSet visitSet =
		// pdoresponsefactory.getVisitSetFromResponseXML(result);
		// System.out.println("Total visits: "+visitSet.getVisitDimension().
		// size());
    }                                            
    
    
    // Variables declaration
    private javax.swing.JButton jAuditButton;
    private javax.swing.JTextField jKeyTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jPatientIdTextField;
    private javax.swing.JTextField jSiteNameTextField;
    private javax.swing.JComboBox jProjectIdComboBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jSetKeyButton;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jUserIdTextField;
    private javax.swing.JButton jValidateKeyButton;
    // End of variables declaration
    
}
