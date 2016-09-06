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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBElement;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.adminTool.dataModel.PDOResponseMessageModel;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.im.query.AuditType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pm.RoleType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pm.RolesType;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.adminTool.utils.GetUseridsRequestMessage;
import edu.harvard.i2b2.eclipse.plugins.adminTool.utils.MessageUtil;
import edu.harvard.i2b2.eclipse.plugins.adminTool.utils.PmServiceController;
import edu.harvard.i2b2.eclipse.plugins.adminTool.utils.SetUserPasswordRequestMessage;
import edu.harvard.i2b2.patientMapping.serviceClient.IMQueryClient;

@SuppressWarnings("serial")
public class AdminToolSetPasswordJPanel extends javax.swing.JPanel {
	
	private static final Log log = LogFactory.getLog(AdminToolSetPasswordJPanel.class);
	
	private ArrayList<String> users;
	
    /** Creates new form PatientMappingJPanel */
    public AdminToolSetPasswordJPanel() {
        initComponents();
        
        String userID = UserInfoBean.getInstance().getUserName();
        //jUserIdComboBox.addItem(projectID);
        jUserIdComboBox.setModel(new javax.swing.DefaultComboBoxModel());//new String[] {projectID}));
        //jUserIdComboBox.setEditable(true);
        
        if(UserInfoBean.getInstance().isAdmin()) {
        	jUserIdComboBox.setEditable(true);
	        if(users == null) {
				users = new ArrayList<String>();
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
						users.add(curStr);
						jUserIdComboBox.addItem(curStr);
						for(int j=0; j<tmpArr.size(); j++) {
							String user = tmpArr.get(j);
							if(user.equalsIgnoreCase(curStr)) {
								continue;
							}
							users.add(user);
							jUserIdComboBox.addItem(user);
							curStr = user;
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
        }
        else {
        	jUserIdComboBox.addItem(userID);
        }
        	
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
    	java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jUserIdComboBox = new javax.swing.JComboBox();
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
        jPanel2.add(jUserIdComboBox);
        jUserIdComboBox.setBounds(60, 10, 150, 20);

        jLabel1.setText("User id:");
        jPanel2.add(jLabel1);
        jLabel1.setBounds(10, 10, 50, 20);

        jLabel2.setText("Password:");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(220, 10, 60, 20);

        jPanel2.add(jKeyTextField);
        jKeyTextField.setBounds(280, 10, 170, 20);

        jSetKeyButton.setText("Set Password");
        jSetKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSetKeyButtonActionPerformed(evt);
            }
        });

        jPanel2.add(jSetKeyButton);
        jSetKeyButton.setBounds(460, 10, 100, 23);

        jValidateKeyButton.setText("Validate Key");
        jValidateKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValidateKeyButtonActionPerformed(evt);
            }
        });

        //jPanel2.add(jValidateKeyButton);
        //jValidateKeyButton.setBounds(510, 10, 100, 23);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 40;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        add(jPanel2, gridBagConstraints);

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
        //add(jScrollPane1, gridBagConstraints);

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
        //add(jPanel1, gridBagConstraints);
    }

    private void jValidateKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	String key = this.jKeyTextField.getText();
    	String projectID = UserInfoBean.getInstance().getProjectId();
    	int i = UserInfoBean.getInstance().getProjectList().size();
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
    	String user = (String) this.jUserIdComboBox.getEditor().getItem();
    	//int i = UserInfoBean.getInstance().getProjectList().size();
    	//String result = IMQueryClient.setKey(key, projectID);
    	//String result=null;
    	
    	PmServiceController pms = new PmServiceController();
		try {
			PasswordType ptype = new PasswordType();
			ptype.setIsToken(UserInfoBean.getInstance().getUserPasswordIsToken());
			ptype.setTokenMsTimeout(UserInfoBean.getInstance()
					.getUserPasswordTimeout());
			ptype.setValue(UserInfoBean.getInstance().getUserPassword());
			
			String result = null;
			if(UserInfoBean.getInstance().isAdmin()) {
				result = pms.setUserPassword(UserInfoBean.getInstance().getUserName(), ptype, UserInfoBean.getInstance().getSelectedProjectUrl(), 
					UserInfoBean.getInstance().getUserDomain(), key, user);
			}
			else {
				result = pms.setUserPassword(UserInfoBean.getInstance().getUserName(), ptype, UserInfoBean.getInstance().getSelectedProjectUrl(), 
						UserInfoBean.getInstance().getUserDomain(), key);
			}
    	
			PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
    	
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
    	String projectID = UserInfoBean.getInstance().getProjectId();
    	
    	String result = IMQueryClient.getAudit(userID, patientID, projectID, site);
    	
    	PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
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
    private javax.swing.JComboBox jUserIdComboBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jSetKeyButton;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jUserIdTextField;
    private javax.swing.JButton jValidateKeyButton;
    // End of variables declaration
    
}
