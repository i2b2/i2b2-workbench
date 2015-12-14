package edu.harvard.i2b2.eclipse;

import javax.swing.JOptionPane;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.pm.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.pm.datavo.i2b2message.StatusType;



public class SetPasswordJDialog extends javax.swing.JDialog {

    /**
     * Creates new form SetPasswordJDialog
     */
    public SetPasswordJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    
    @SuppressWarnings("unchecked")
    
    private void initComponents() {

    	jLabel1 = new javax.swing.JLabel();
        jCurrentPasswordField = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jNewPasswordField = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jRetypePasswordField = new javax.swing.JPasswordField();
        jOKButton = new javax.swing.JButton();
        jCancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Set Password for "+UserInfoBean.getInstance().getUserName());
        setAlwaysOnTop(true);
        setModal(true);
        setResizable(false);
        getContentPane().setLayout(null);

        jLabel1.setText("Current Password:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(24, 23, 112, 30);
        getContentPane().add(jCurrentPasswordField);
        jCurrentPasswordField.setBounds(141, 27, 127, 22);

        jLabel2.setText("New Password:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(24, 71, 111, 25);
        getContentPane().add(jNewPasswordField);
        jNewPasswordField.setBounds(140, 72, 128, 22);

        jLabel3.setText("Retype Password:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(24, 103, 111, 24);
        getContentPane().add(jRetypePasswordField);
        jRetypePasswordField.setBounds(140, 104, 128, 22);

        jOKButton.setText("OK");
        jOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOKButtonActionPerformed(evt);
            }
        });
        getContentPane().add(jOKButton);
        jOKButton.setBounds(70, 162, 49, 25);

        jCancelButton.setText("Cancel");
        jCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCancelButtonActionPerformed(evt);
            }
        });
        getContentPane().add(jCancelButton);
        jCancelButton.setBounds(167, 162, 71, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOKButtonActionPerformed
    	String newPassword = new String(jNewPasswordField.getPassword());
    	String curPassword = new String(jCurrentPasswordField.getPassword());
    	String projectID = UserInfoBean.getInstance().getProjectId();
    	//String user = (String) this.jUserIdComboBox.getEditor().getItem();
    	//int i = UserInfoBean.getInstance().getProjectList().size();
    	//String result = IMQueryClient.setKey(key, projectID);
    	//String result=null;
    	
    	String retypePassword = new String(jRetypePasswordField.getPassword());
    	if(!retypePassword.equals(newPassword)) {
	    	java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(jLabel2, "The password retyped is not the same as the new password you entered.");
				}
			});
	    	return;
    	}
    	else if(newPassword.equals("") || newPassword.equals(" ")) {
    		java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(jLabel2, "The new password can't be empty.");
				}
			});
	    	return;
    	}
    	
    	PmServiceController pms = new PmServiceController();
		try {
			PasswordType ptype = new PasswordType();
			ptype.setIsToken(false);//UserInfoBean.getInstance().getUserPasswordIsToken());
			ptype.setTokenMsTimeout(UserInfoBean.getInstance()
					.getUserPasswordTimeout());
			ptype.setValue(curPassword);//UserInfoBean.getInstance().getUserPassword());
			
			String result = null;
			//if(UserInfoBean.getInstance().isAdmin()) {
			//	result = pms.setUserPassword(UserInfoBean.getInstance().getUserName(), ptype, UserInfoBean.getInstance().getSelectedProjectUrl(), 
			//		UserInfoBean.getInstance().getUserDomain(), key, user);
			//}
			//else {
				result = pms.setUserPassword(UserInfoBean.getInstance().getUserName(), 
						ptype, 
						UserInfoBean.getInstance().getSelectedProjectUrl(), 
						UserInfoBean.getInstance().getUserDomain(), newPassword);
			//}
    	
			PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
    	
    		StatusType status = pdoresponsefactory.getStatusFromResponseXML(result);
    		
    		String infoToDisplay = status.getValue();
    		if(status.getType().equalsIgnoreCase("Done")) {
    			infoToDisplay = "Password Changed";
    		}
    		final String info = infoToDisplay;
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
    	
    	this.setVisible(false);
    }//GEN-LAST:event_jOKButtonActionPerformed

    private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jCancelButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        /*try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SetPasswordJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SetPasswordJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SetPasswordJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SetPasswordJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }*/
        

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SetPasswordJDialog dialog = new SetPasswordJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration 
    private javax.swing.JButton jCancelButton;
    private javax.swing.JPasswordField jCurrentPasswordField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField jNewPasswordField;
    private javax.swing.JButton jOKButton;
    private javax.swing.JPasswordField jRetypePasswordField;
    // End of variables declaration
}
