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
package edu.harvard.i2b2.explorer.ui;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.ImageIcon;

import edu.harvard.i2b2.common.datavo.pdo.ObservationType;
import edu.harvard.i2b2.timeline.lifelines.GenRecord;
import edu.harvard.i2b2.timeline.lifelines.TextViewerFrame;

public class InfoJFrame extends javax.swing.JFrame {
	
	private TimeLinePanel panel_;
	private String notes;
	private int markStar; 
	private GenRecord thisRecord=null; 
	private ImageIcon iconBlank, iconStarred;

    /**
     * Creates new form InfoJFrame
     * 
     */
    public InfoJFrame(TimeLinePanel panel, ObservationType ob, GenRecord selectedRecord) {
    	panel_ = panel;
        thisRecord=selectedRecord;
        if(thisRecord!=null)
        {
	        if(thisRecord.mark_status.equalsIgnoreCase("S"))
				markStar=1;
	        else
	        	markStar=-1;
        }
        initComponents();
        
        //jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        //jTable1.setTableHeader(null);
        //jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
        //jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        //jTable1.getModel().setValueAt("Test cccbbbb", 0, 0);
        //jTable1.getModel().setValueAt("Test dddddddddddddddd", 0, 1);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        
        //notes = (String) ob.getObservationBlob()
				//.getContent().get(0);
		// System.out.println("notes: "+eNotes);
        
		//return new String[] { eNotes, obsFactType.getValuetypeCd(),
				//obsFactType.getValueflagCd().getValue(), result }; 
		
		if (ob.getValuetypeCd() == null
				|| (!ob.getValuetypeCd().equals("B"))) {
			jNotesButton.setEnabled(false);
		}
		
		String[] cols = new String[2];
        //Class[] types = new Class[columns.size()+2];
        //cols[0] = new String("");
        cols[0] = "";
        cols[1] = "";
		DefaultTableModel model = new DefaultTableModel(cols, 10){

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
        	
        };
        jTable1.setModel(model);
        
		jTable1.setValueAt("Concept CD", 0, 0);
		jTable1.setValueAt(ob.getConceptCd().getValue(), 0, 1);
		
		// hkpark
		// modified date format
		Date start_date, end_date;
		DateFormat  formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		
		jTable1.setValueAt("Start Date", 1, 0);
		if(ob.getStartDate() == null)
			jTable1.setValueAt(null, 1, 1);
		else
		{
			start_date = ob.getStartDate().toGregorianCalendar().getTime();
			jTable1.setValueAt(formatter.format(start_date), 1, 1);
		}
		
		jTable1.setValueAt("End Date", 2, 0);
		if(ob.getEndDate() == null)
			jTable1.setValueAt(null, 2, 1);
		else
		{
			end_date = ob.getEndDate().toGregorianCalendar().getTime();
			jTable1.setValueAt(formatter.format(end_date), 2, 1);
		}		
		///
		
		jTable1.setValueAt("Event ID", 3, 0);
		jTable1.setValueAt(ob.getEventId().getValue(), 3, 1);
		
		jTable1.setValueAt("Observer ID", 4, 0);
		jTable1.setValueAt(ob.getObserverCd().getValue(), 4, 1);
		
		jTable1.setValueAt("Instance Number", 5, 0);
		jTable1.setValueAt(ob.getInstanceNum().getValue(), 5, 1);
		
		jTable1.setValueAt("Modifier CD", 6, 0);
		jTable1.setValueAt(ob.getModifierCd().getValue(), 6, 1);

		jTable1.setValueAt("Text Value", 7, 0);
		jTable1.setValueAt(ob.getTvalChar(), 7, 1);

		jTable1.setValueAt("Numeric Value", 8, 0);
		jTable1.setValueAt(ob.getNvalNum().getValue(), 8, 1);

		jTable1.setValueAt("Units", 9, 0);
		jTable1.setValueAt(ob.getUnitsCd(), 9, 1);

		
		
		jTable1.setTableHeader(null);
		int total = jTable1.getColumnModel().getTotalColumnWidth();
		jTable1.getColumnModel().getColumn(0).setPreferredWidth(8);
		jTable1.getColumnModel().getColumn(1).setPreferredWidth(total-8);
    }
    
    
    private void initComponents() {
    	//setUndecorated(true);
    	getContentPane().setBackground(Color.decode("0xfaf4ce")); //eae9e6")); //f0e8dc")); //f6e5da"));//b4af95"));
    	getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
    	//this.getContentPane().set
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jCloseButton = new javax.swing.JButton();
        jNotesButton = new javax.swing.JButton();

        //setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(null);

        jPanel1.setLayout(new java.awt.BorderLayout());
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1);
        //jPanel1.setBounds(0, 30, 380, 130);
       // jPanel1.setBorder(BorderFactory.createLineBorder(Color.black));
        //modified by hkpark
         //jPanel1.setBounds(0, 30, 380, 200);
        jPanel1.setBounds(7, 30, 366, 162);

        jCloseButton.setText("Close");
        jCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCloseButtonActionPerformed(evt);
            }
        });
        add(jCloseButton);
        jCloseButton.setBounds(73, 5, 80, 20);

        jNotesButton.setText("View Notes");
        jNotesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	jNotesButtonActionPerformed(evt);
            }
        });
        add(jNotesButton);
        jNotesButton.setBounds(210, 5, 100, 20);
        
        java.awt.Image imgBlnkStar = this.getToolkit().getImage(
				InfoJFrame.class.getResource("/icons/outlinedStar.gif"));
		iconBlank = new ImageIcon(imgBlnkStar);	
		java.awt.Image imgStarred = this.getToolkit().getImage(
				InfoJFrame.class.getResource("/icons/yellowOutlinedStar.gif"));
		iconStarred = new ImageIcon(imgStarred);			
        if(markStar==1)
		{
			//starTagMsg="Starred";
        	jStarButton = new JButton(iconStarred);
		}
		else
		{
			//starTagMsg="Tag Star";
			//jStarButton.setIcon(iconBlank);
			jStarButton = new JButton(iconBlank);
		}
        jStarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStarButtonActionPerformed(evt);
            }
        });
        add(jStarButton);
        jStarButton.setBounds(330, 8, 15, 15);
        
    }// 

    private void jNotesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNotesButtonActionPerformed
    	setVisible(false);
        dispose();
    	
    	panel_.showNotesViewer();
    }

    private void jCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCloseButtonActionPerformed
        setVisible(false);
        dispose();
    }
    
	private void jStarButtonActionPerformed(java.awt.event.ActionEvent evt) {
		markStar=markStar*-1;
		if(markStar==1)
			thisRecord.mark_status="S";
		else
			thisRecord.mark_status="R";
		panel_.repaint();
		update_jStarButton();		
	}
	
	private void update_jStarButton()
	{
		if(markStar==1)
			jStarButton.setIcon(iconStarred);
		else
			jStarButton.setIcon(iconBlank);
		jStarButton.updateUI();
	}
	
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
      
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new InfoJFrame(null, null).setVisible(true);
            	//hkpark
            	//modified.
            	//check if it is correct
            	System.out.println("hkpark} invokeLater");
            	new InfoJFrame(null, null, null).setVisible(true);
            	//new InfoJFrame(null, null, selectedRecord).setVisible(true);
            }
        });
    }
    
    public void setInfo(String str) {
    	//jTextArea1.setText(str);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jCloseButton;
    private javax.swing.JButton jNotesButton;
    private javax.swing.JButton jStarButton;
    private javax.swing.JScrollPane jScrollPane1;
    //private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTable jTable1;
    //private javax.swing.JPanel jPanel2;
    //private javax.swing.JButton jButton1;
    //private javax.swing.JButton jButton2;
    // End of variables declaration//GEN-END:variables
}
