/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 */

/*
 * ModifierFrame.java
 *
 * Created on March 22, 2011, 2:38 PM
 */
package edu.harvard.i2b2.query.ui;

/**
 *
 * @author  wp066
 */
public class ModifierFrame extends javax.swing.JFrame {
    
    /** Creates new form ModifierFrame */
    public ModifierFrame() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
    	jAllRadioButton = new javax.swing.JRadioButton();
        jSpecificRadioButton = new javax.swing.JRadioButton();
        jModifiersPanel = new ModifierSelectionPanel(null);
        jValueLabel = new javax.swing.JLabel();
        jValuePanel = new javax.swing.JPanel();
        jOKButton = new javax.swing.JButton();
        jCancelButton = new javax.swing.JButton();

        getContentPane().setLayout(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setTitle("Set Modifier");
        jAllRadioButton.setText("All modifiers");
        jAllRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jAllRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        getContentPane().add(jAllRadioButton);
        jAllRadioButton.setBounds(20, 10, 110, 20);

        jSpecificRadioButton.setText("Specific modifier:");
        jSpecificRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jSpecificRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        getContentPane().add(jSpecificRadioButton);
        jSpecificRadioButton.setBounds(20, 30, 110, 20);

        //jModifiersPanel.setLayout(null);

        //jModifiersPanel.setBackground(new java.awt.Color(255, 204, 204));
        getContentPane().add(jModifiersPanel);
        jModifiersPanel.setBounds(30, 60, 120, 130);

        jValueLabel.setText("Value:");
        getContentPane().add(jValueLabel);
        jValueLabel.setBounds(30, 200, 100, 20);

        jValuePanel.setLayout(null);

        jValuePanel.setBackground(new java.awt.Color(153, 255, 153));
        getContentPane().add(jValuePanel);
        jValuePanel.setBounds(30, 230, 120, 100);

        jOKButton.setText("OK");
        jOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOKButtonActionPerformed(evt);
            }
        });

        getContentPane().add(jOKButton);
        jOKButton.setBounds(20, 350, 60, 23);

        jCancelButton.setText("Cancel");
        getContentPane().add(jCancelButton);
        jCancelButton.setBounds(100, 350, 70, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOKButtonActionPerformed

    }//GEN-LAST:event_jOKButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ModifierFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration//GEN-BEGIN:variables
    private javax.swing.JRadioButton jAllRadioButton;
    private javax.swing.JButton jCancelButton;
    private ModifierSelectionPanel jModifiersPanel;
    private javax.swing.JButton jOKButton;
    private javax.swing.JRadioButton jSpecificRadioButton;
    private javax.swing.JLabel jValueLabel;
    private javax.swing.JPanel jValuePanel;
    // End of variables declaration//GEN-END:variables
    
}
