/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */

/*
 * PatientMappingJPanel.java
 *
 * Created on May 17, 2012, 3:54 PM
 */
package edu.harvard.i2b2.patientMapping.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.uima.pear.util.StringUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.common.datavo.pdo.PatientIdType;
import edu.harvard.i2b2.common.datavo.pdo.PatientSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientType;
import edu.harvard.i2b2.common.datavo.pdo.PidSet;
import edu.harvard.i2b2.common.datavo.pdo.PidType;
import edu.harvard.i2b2.common.datavo.pdo.PidType.PatientMapId;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
//import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientSet;
//import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.PageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.pdo.query.PatientDataResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.InstanceResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PanelType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionRequestType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryInstanceType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByDate;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.patientMapping.utils.SecurityUtil;
import edu.harvard.i2b2.patientMapping.data.QueryInstanceData;
import edu.harvard.i2b2.patientMapping.data.QueryMasterData;
import edu.harvard.i2b2.patientMapping.data.PatientMappingData;
import edu.harvard.i2b2.patientMapping.data.SiteColumnData;
import edu.harvard.i2b2.patientMapping.dataModel.PDOResponseMessageModel;
import edu.harvard.i2b2.patientMapping.dataModel.PatientMappingFactory;
import edu.harvard.i2b2.patientMapping.datavo.PatientMappingJAXBUtil;
import edu.harvard.i2b2.patientMapping.serviceClient.PatientMappingQueryClient;

public class PatientMappingJPanel extends javax.swing.JPanel {
	
	private static final Log log = LogFactory.getLog(PatientMappingJPanel.class);
	
	private long lEventTime = 0;
	private ArrayList<SiteColumnData> columns;
    private int currentIndex = 1;
    private String key = null;
    private String siteID = "";
    private boolean hasEncryptedCol = false;
    private PatientMappingJPanel thisPanel;
    private java.awt.Container parent;
    private WaitPanel pl = null;
    private Thread athread;
    private String saveFileName=" ";
    private String pidFile;
    private RandomAccessFile f;
    private String autoSaveFilePath = "";
    
    //String[] cols;
    
    /** Creates new form PatientMappingJPanel */
    public PatientMappingJPanel(java.awt.Container oAwtContainer) {
    	columns = new ArrayList<SiteColumnData>();
    	
        initComponents();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        //model.addColumn("addedColumn1");
        //model.addColumn("addedColumn2");
        
        String[] columns = new String[3];
        columns[0] = new String("HIVE");
        columns[1] = new String("");
        columns[2] = new String("");
        //columns[3] = new String("");
        DefaultTableModel model1 = new DefaultTableModel(columns, 100);
        jTable1.setModel(model1);
        jTable1.setCellSelectionEnabled(true);
        TableColumn col = jTable1.getColumnModel().getColumn(0);   
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();     
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);   
        col.setCellRenderer(dtcr);
        //jTable1.setCellSelectionEnabled(false);
        //model.setRowCount(100);
        //model.setValueAt(235600004, 0, 4);
        //model.setValueAt(235600005, 1, 5);
        //model.setValueAt(235600006, 5, 4);
        //model.setValueAt(235600007, 5, 5);
        //model.setValueAt(235600008, 6, 5);
        
        thisPanel = this;
        parent = oAwtContainer;
        
        //this.jLeftButton.setEnabled(false);
        this.jRightButton.setEnabled(false);
        this.jStartTextField.setEditable(false);
        this.jIncrementTextField.setEditable(false);
        //jAutoSaveCheckBox.setSelected(true);
    }
    
    private void initComponents() {
    	 java.awt.GridBagConstraints gridBagConstraints;

         jLabel1 = new javax.swing.JLabel();
         jScrollPane1 = new javax.swing.JScrollPane();
         jTable1 = new javax.swing.JTable();
         jPanel1 = new javax.swing.JPanel();
         jSaveButton = new javax.swing.JButton();
         jImportButton = new javax.swing.JButton();
         jConvertButton = new javax.swing.JButton();
         jAutoSaveCheckBox = new javax.swing.JCheckBox();
         jPanel2 = new javax.swing.JPanel();
         jRightButton = new javax.swing.JButton();
         jLabel2 = new javax.swing.JLabel();
         jLeftButton = new javax.swing.JButton();
         jStartTextField = new javax.swing.JTextField();
         jLabel3 = new javax.swing.JLabel();
         jLabel4 = new javax.swing.JLabel();
         jIncrementTextField = new javax.swing.JTextField();

         setLayout(new java.awt.GridBagLayout());

         setMinimumSize(new java.awt.Dimension(0, 0));
         jLabel1.setText("  Patient Mapping Table:");
         jLabel1.setAlignmentX(0.5F);
         jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
         jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
         jLabel1
		.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseMoved(java.awt.event.MouseEvent evt) {
				jNameLabelMouseMoved(evt);
			}
		});
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseExited(java.awt.event.MouseEvent evt) {
				jNameLabelMouseExited(evt);
			}
		});
        jLabel1.addMouseListener(new DragMouseAdapter());
        jLabel1.setTransferHandler(new NameLabelTextHandler());
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.ipadx = 390;
         gridBagConstraints.ipady = 4;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(3, 2, 0, 3);
         add(jLabel1, gridBagConstraints);

         jTable1.setModel(new javax.swing.table.DefaultTableModel(
             new Object [][] {
                 {null, null, null, null},
                 {null, null, null, null},
                 {null, null, null, null},
                 {null, null, null, null}
             },
             new String [] {
                 "Title 1", "Title 2", "Title 3", "Title 4"
             }
         ));
         jScrollPane1.setViewportView(jTable1);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.ipadx = 486;
         gridBagConstraints.ipady = 160;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 1.0;
         gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
         add(jScrollPane1, gridBagConstraints);

         jPanel1.setLayout(new java.awt.GridBagLayout());

         jSaveButton.setText("Save Patient Set");
         jSaveButton.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jSaveButtonActionPerformed(evt);
             }
         });

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 1;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.ipady = -3;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(0, 40, 1, 0);
         jPanel1.add(jSaveButton, gridBagConstraints);

         jImportButton.setText("Open Patient Set");
         jImportButton.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jImportButtonActionPerformed(evt);
             }
         });

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 2;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.ipadx = 3;
         gridBagConstraints.ipady = -3;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(0, 21, 1, 0);
         jPanel1.add(jImportButton, gridBagConstraints);

         jConvertButton.setText("Convert Between Patient Sets");
         jConvertButton.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jConvertButtonActionPerformed(evt);
             }
         });

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 3;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.ipady = -3;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(0, 10, 1, 1);
         jPanel1.add(jConvertButton, gridBagConstraints);

         jAutoSaveCheckBox.setText("save patients to a file automatically");
         jAutoSaveCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
         jAutoSaveCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.ipadx = 33;
         gridBagConstraints.ipady = 5;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(0, 10, 1, 0);
         jPanel1.add(jAutoSaveCheckBox, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 3;
         gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.ipadx = 509;
         gridBagConstraints.ipady = 1;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(1, 11, 5, 0);
         add(jPanel1, gridBagConstraints);

         jPanel2.setLayout(new java.awt.GridBagLayout());

         jRightButton.setText(">");
         jRightButton.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jRightButtonActionPerformed(evt);
             }
         });

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 6;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.gridheight = 3;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(10, 10, 8, 5);
         //jPanel2.add(jRightButton, gridBagConstraints);

         jLabel2.setText("Patient Set Size: ---                      ");
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.gridheight = 2;
         gridBagConstraints.ipady = 6;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(10, 17, 0, 0);
         jPanel2.add(jLabel2, gridBagConstraints);

         jLeftButton.setText("Cancel");
         jLeftButton.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jLeftButtonActionPerformed(evt);
             }
         });

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 1;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.gridheight = 3;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(10, 24, 8, 0);
         jPanel2.add(jLeftButton, gridBagConstraints);
         //jLeftButton.setBackground(Color.LIGHT_GRAY);
         
         jPanel2.setBackground(Color.LIGHT_GRAY);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 3;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.gridheight = 2;
         gridBagConstraints.ipadx = 44;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(10, 3, 0, 0);
         //jPanel2.add(jStartTextField, gridBagConstraints);

         jLabel3.setText("start:");
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 2;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(10, 17, 0, 0);
         //jPanel2.add(jLabel3, gridBagConstraints);

         jLabel4.setText("increment:");
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 4;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
         //jPanel2.add(jLabel4, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 5;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.gridheight = 2;
         gridBagConstraints.ipadx = 44;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         gridBagConstraints.insets = new java.awt.Insets(10, 9, 0, 0);
         //jPanel2.add(jIncrementTextField, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 2;
         gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.ipadx = 33;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
         add(jPanel2, gridBagConstraints);

    }

    private void jImportButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	Display.getDefault().syncExec(new Runnable() {
			public void run() {
				String fileName = openFileDialog();
				//if (fileName != null) {
					//String fileName = dialog.getFileName();
					if (fileName != null && fileName.trim().length() > 0) {
						log.info("Selected file: "+fileName);
						FileReader fr;
						BufferedReader br = null;
						try{
							fr = new FileReader(new File(fileName));
							br = new BufferedReader(fr);
							String line = br.readLine();
							//if(!line.startsWith("@@i2b2 patient mapping file@@")) {
							//	java.awt.EventQueue.invokeLater(new Runnable() {
							//		public void run() {
										//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							//			JOptionPane.showMessageDialog(jLabel1, "The file is not in a valid format.", "Error importing", JOptionPane.ERROR_MESSAGE);
							//		}
							//	});
								//JOptionPane.showMessageDialog(null, "The file is not a valid.", "Error importing", JOptionPane.ERROR_MESSAGE);
							//	return;
							//}
							//line = br.readLine();
							log.info("column name: "+line);
							String[] cols = line.split(",");
							
							DefaultTableModel model = new DefaultTableModel(cols, 200){

								@Override
								public boolean isCellEditable(int arg0, int arg1) {
									return false;
								}
					        	
					        };   //{
					        	//@SuppressWarnings("unchecked")
								//Class[] types = new Class[] { java.lang.Boolean.class,
										//java.lang.String.class };

								//@SuppressWarnings("unchecked")
								//public Class getColumnClass(int columnIndex) {
									//if(columnIndex ==0) {
										//return java.lang.Boolean.class;
									//}
									//return java.lang.Object.class;
								//}
					       // };
					        jTable1.setModel(model);
					        
					        String[] row;
					        int rowCount = 0;
							line = br.readLine();
							while(line!=null) {
								log.info(line);
								row = line.split(",");
								String id = "";
								for(int i=0; i<row.length; i++) {
									id = row[i];
									//if(cols[i].indexOf("_E") > 0) {
										//////id = decryptID(id);
    								//}
									//decryptID(row[i]);
									jTable1.setValueAt(id, rowCount, i);
								}
								rowCount++;
								line = br.readLine();
							}
							model.setRowCount(rowCount);
							br.close();
						} catch(Exception e) {
							e.printStackTrace();
							if(br!=null) {
								try {
									br.close();
								}catch(Exception e1) {}
							}
						}
					}
				//}		
			}
		});
    }
    
    private void jConvertButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PatientIDConversionJFrame frame = new PatientIDConversionJFrame();
                frame.setLocation(200, 300);
                frame.setSize(500, 320);
                frame.setVisible(true);
                
                //log.info("Encrypt U500004027 to:"+encryptID("U500004027"));
                //log.info("Encrypt 2000001964 to:"+encryptID("2000001964"));
                //log.info("Encrypt 3000001847 to:"+encryptID("3000001847"));
                //log.info("Encrypt S500003057 to:"+encryptID("S500003057"));
            }
        });
    }

    private void jSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	//final Display display = Display.getDefault();
    	Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Shell shell = new Shell(Display.getDefault());

				  FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				    dialog
				        .setFilterNames(new String[] { "Text Files", "All Files (*.*)" });
				    dialog.setFilterExtensions(new String[] { "*.txt", "*.*" }); // Windows
				                                    // wild
				                                    // cards
				    dialog.setFilterPath("c:\\"); // Windows path
				    dialog.setFileName(saveFileName+".txt");
				    String filename = dialog.open();
				    //System.out.println("Save to: " + filename);
				    if(filename == null) {
				    	return;
				    }
				    
				    File oDelete = new File(filename);
					if (oDelete != null && oDelete.exists()) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				        
				        messageBox.setText("Warning");
				        messageBox.setMessage(filename+" already exists,\nDo you want to replace it?");
				        int buttonID = messageBox.open();
				        switch(buttonID) {
				          case SWT.YES:
				        	  oDelete.delete();
				        	  break;
				          case SWT.NO:
				            return;
				          case SWT.CANCEL:
				            // does nothing ...

				        }
					}
					
				    StringBuilder resultFile = new StringBuilder();
				    // resultFile.append("@@i2b2 patient mapping file@@ Total columns: "+jTable1.getColumnCount()+"\n");
				    for(int i=0; i<jTable1.getColumnCount(); i++) {
				    	resultFile.append(jTable1.getColumnName(i)+",");
				    }
				    
				    for(int i=0; i<jTable1.getRowCount(); i++) {
				    	resultFile.append("\n");
				    	for(int j=0; j<jTable1.getColumnCount(); j++) {
				    		if(jTable1.getValueAt(i, j) !=null) {
				    			resultFile.append(""+jTable1.getValueAt(i, j)+",");
				    		}
				    		else {
				    			resultFile.append(""/*+jTable1.getValueAt(i, j)*/+",");
				    		}
				    	}
				    }
				    try {
					    RandomAccessFile f = new RandomAccessFile(filename, "rw");
						append(f, resultFile.toString());
						f.close();	
				    } catch(Exception e) {
				    	e.printStackTrace();
				    }
			}
		});
    }
    
    private void append(RandomAccessFile f, String outString) throws IOException {
		try {
			f.seek(f.length());
			f.writeBytes(outString);
		}
        catch (IOException e) {
			throw new IOException("trouble writing to random access file.");
		}
		return;
	}
    
    private void jNameLabelMouseExited(java.awt.event.MouseEvent evt) {
		jLabel1.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
	}

	private void jNameLabelMouseMoved(java.awt.event.MouseEvent evt) {
		jLabel1.setBorder(javax.swing.BorderFactory
				.createLineBorder(Color.YELLOW));
		jLabel1.paintImmediately(jLabel1.getVisibleRect());

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}

		jLabel1.setBorder(javax.swing.BorderFactory
				.createLineBorder(Color.BLACK));
	}
    
	private class DragMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);

			jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(0, 0, 0)));

			// reading the system time to a long
			lEventTime = System.currentTimeMillis();
		}
	}
	
	private class TableDragMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			//super.mousePressed(e);
			
			
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);

			/*jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(0, 0, 0)));

			// reading the system time to a long
			lEventTime = System.currentTimeMillis();*/
			//super.mousePressed(e);
		}
	}
	
	class NameLabelTextHandler extends TransferHandler {
		public NameLabelTextHandler() {
			super("text");
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavor) {
			jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.YELLOW));
			jLabel1.paintImmediately(jLabel1.getVisibleRect());

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.BLACK));

			if ((System.currentTimeMillis() - lEventTime) > 2000) {

				return true;
			}
			return false;
		}

		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
		}

		public boolean importData(JComponent comp, Transferable t) {
			
			try {
				final String text = (String) t.getTransferData(DataFlavor.stringFlavor);
				
			//java.awt.EventQueue.invokeLater(new Runnable() {
				//public void run() {
					//////////////////
					//parent.removeAll();
					
					pl = new WaitPanel((int) (jScrollPane1.getWidth() * 0.40),
							(int) (jScrollPane1.getHeight() * 0.40),
							(1000));
					//oAwtContainer.add(p);
					//jScrollPane1.removeAll();
					jScrollPane1.setViewportView(pl);
					jScrollPane1.revalidate();
					//parent.add(pl);
					
					pl.setBounds(0, 0, pl.getParent().getWidth(), pl.getParent().getHeight());
					pl.init((int) (pl.getParent().getWidth() * 0.40), (int) (pl.getParent()
							.getHeight() * 0.40));
					pl.go();
					pl.setVisible(true);
					//jScrollPane1.revalidate();
					//parent.validate();
					///////////////////
					//jTable1.setCellSelectionEnabled(true);
					jLabel1.setBorder(javax.swing.BorderFactory
							.createLineBorder(new java.awt.Color(0, 0, 0)));
					setCursor(new Cursor(Cursor.WAIT_CURSOR));
					//pl.revalidate();
					//jScrollPane1.revalidate();
					//thisPanel.revalidate();
					//parent.validate();
					
					hasEncryptedCol = false;
					columns.clear();
										
					athread = new Thread() {
						public void run() {
							try {    		
								if(jAutoSaveCheckBox.isSelected()) {
									Display.getDefault().syncExec(new Runnable() {
									public void run() {
										Shell shell = new Shell(Display.getDefault());

										  FileDialog dialog = new FileDialog(shell, SWT.SAVE);
										    dialog
										        .setFilterNames(new String[] { "Text Files", "All Files (*.*)" });
										    dialog.setFilterExtensions(new String[] { "*.txt", "*.*" }); // Windows
										                                    // wild
										                                    // cards
										    dialog.setFilterPath("c:\\"); // Windows path
										    dialog.setFileName(saveFileName+".txt");
										    autoSaveFilePath = dialog.open();
										    //System.out.println("Save to: " + filename);
										    if(autoSaveFilePath == null) {
										    	MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
								        
										        messageBox.setText("Warning");
										        messageBox.setMessage("No valid file path is selected.");
										    	return;
										    }
										    
										    File oDelete = new File(autoSaveFilePath);
											if (oDelete != null && oDelete.exists()) {
												MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
										        
										        messageBox.setText("Warning");
										        messageBox.setMessage(autoSaveFilePath+" already exists,\nDo you want to replace it?");
										        int buttonID = messageBox.open();
										        switch(buttonID) {
										          case SWT.YES:
										        	  oDelete.delete();
										        	  break;
										          case SWT.NO:
										        	  if (athread != null) {
										      			athread.stop();
										      			athread = null;
										        	  }
										      			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
										      	
										      			// jRunQueryButton.setBackground(defaultcolor);
										      			try {
										      			    //RandomAccessFile f = new RandomAccessFile(filename, "rw");
										      				//append(f, resultFile.toString());
										      				f.close();	
										      				pl.stop();
										      				pl.setVisible(false);
										      				jLabel2.setText("Patient Set Size: ");//+setsize+ " ("+setsize+" loaded)");
										      				//jStartTextField.setText(""+(k+increment));
										      				//jIncrementTextField.setText(""+increment);
										      				//jScrollPane1.setViewportView(jTable1);
										      				//jScrollPane1.revalidate();
										      				jPanel1.revalidate();
										      			} catch(Exception e) {
										      			  	e.printStackTrace();
										      			}
										      		
										            return;
										          case SWT.CANCEL:
										        	  //return;
										            // does nothing ...

										        }
											}
											
									}
								});
								}
							
								System.out.println(text);
								String description = "";
								String id = null;
								int setsize = 0;
								int increment = 1000;
								
								String tmp = System.getProperty("PMVIncrement");
								if( tmp!= null) { 
									increment = Integer.parseInt(tmp);
								}
								
								SAXBuilder parser = new SAXBuilder();
								String xmlContent = text;
								java.io.StringReader xmlStringReader = new java.io.StringReader(xmlContent);
								final org.jdom.Document tableDoc = parser.build(xmlStringReader);
								org.jdom.Element tableXml1 = null;
								for (int i = 0; i < tableDoc.getRootElement().getContent()
										.size(); i++) {
									if (tableDoc.getRootElement().getContent().get(i)
											.getClass().getSimpleName().equalsIgnoreCase(
													"Element")) {
										tableXml1 = (org.jdom.Element) tableDoc
												.getRootElement().getContent().get(i);
										break;
									}
								}
								
								boolean enter = false;
								if(enter && tableXml1.getName().equalsIgnoreCase("query_master")) {
									QueryMasterData ndata = new QueryMasterData();
									ndata.name(tableXml1.getChildText("name"));
									//queryNamemrnlistText.setText("Query Name: " + ndata.name());
									//groupNameText.setText("Panel Name: All items of Query " + ndata.name());
									ndata.xmlContent(null);
									ndata.id(tableXml1.getChildTextTrim("query_master_id"));
									ndata.userId(tableXml1.getChildTextTrim("user_id"));
									
									// get query instance
									String xmlRequest = ndata.writeContentQueryXML();
									//lastRequestMessage(xmlRequest);
									String xmlResponse = PatientMappingQueryClient
											.sendPDQQueryRequestREST(xmlRequest);
									//lastResponseMessage(xmlResponse);
									JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();
									JAXBElement jaxbElement = jaxbUtil
											.unMashallFromString(xmlResponse);
									ResponseMessageType messageType = (ResponseMessageType) jaxbElement
											.getValue();
									BodyType bt = messageType.getMessageBody();
									InstanceResponseType instanceResponseType = (InstanceResponseType) new JAXBUnWrapHelper()
											.getObjectByClass(bt.getAny(),
													InstanceResponseType.class);

									QueryInstanceData instanceData = null;
									XMLGregorianCalendar startDate = null;
									for (QueryInstanceType queryInstanceType : instanceResponseType
											.getQueryInstance()) {
										QueryInstanceData runData = new QueryInstanceData();

										runData.visualAttribute("FA");
										runData.tooltip("The results of the query run");
										runData.id(new Integer(queryInstanceType
												.getQueryInstanceId()).toString());
										XMLGregorianCalendar cldr = queryInstanceType
												.getStartDate();
										runData.name("Results of " + "["
												+ cldr.getMonth() + "-" + cldr.getDay()
												+ "-" + cldr.getYear() + " "
												+ cldr.getHour() + ":"
												+ cldr.getMinute() + ":"
												+ cldr.getSecond() + "]");

										if (instanceData == null) {
											startDate = cldr;
											instanceData = runData;
										} else {
											if (cldr.toGregorianCalendar().compareTo(
													startDate.toGregorianCalendar()) > 0) {
												startDate = cldr;
												instanceData = runData;
											}
										}
									}
									// get patient set
									if (instanceData == null) {
										//event.detail = DND.DROP_NONE;
										return;
									}
									log.info("Got query instance: "
											+ instanceData.name());

									xmlRequest = instanceData.writeContentQueryXML();
									//lastRequestMessage(xmlRequest);

									xmlResponse = PatientMappingQueryClient
											.sendPDQQueryRequestREST(xmlRequest);
									//lastResponseMessage(xmlResponse);

									jaxbElement = jaxbUtil
											.unMashallFromString(xmlResponse);
									messageType = (ResponseMessageType) jaxbElement
											.getValue();
									bt = messageType.getMessageBody();
									ResultResponseType resultResponseType = (ResultResponseType) new JAXBUnWrapHelper()
											.getObjectByClass(bt.getAny(),
													ResultResponseType.class);

									for (QueryResultInstanceType queryResultInstanceType : resultResponseType
											.getQueryResultInstance()) {
										if (!(queryResultInstanceType
												.getQueryResultType().getName()
												.equalsIgnoreCase("PATIENTSET"))) {
											continue;
										}

										String status = queryResultInstanceType
												.getQueryStatusType().getName();

										if (status.equalsIgnoreCase("FINISHED")) {

											String setId = new Integer(
													queryResultInstanceType
															.getResultInstanceId())
													.toString();
											String setSize = new Integer(
													queryResultInstanceType
															.getSetSize()).toString();
											/*String description = queryResultInstanceType
													.getDescription();
											if (description != null) {
												patientSetText.setText(description);
											} else {
												patientSetText.setText("Patient Set: "
														+ setSize + " patients");
											}*/
											String patientRefId = new String(setId);
											//patientMinNumText.setText("1");
											//leftArrowButton.setEnabled(false);

											/*int maxPatientNum = new Integer(
													patientMaxNumText.getText())
													.intValue();
											patientSetSize = queryResultInstanceType
													.getSetSize();
											if (patientSetSize > maxPatientNum) {
												rightArrowButton.setEnabled(true);
												patientMaxNumText.setText("10");
											} else {
												rightArrowButton.setEnabled(false);
												if (patientSetSize > 0) {
													patientMaxNumText.setText(setSize);
												}
											}*/

											log.info("Dropped set of: " + setSize
													+ " patients"/* strs[0] */
													+ " with refId: " + setId/*
																			 * strs[ 1 ]
																			 */);
										} else {
											// message
											return;
										}
									}
								}
								else if (tableXml1.getName().equalsIgnoreCase(
										"query_result_instance")) {
									List children = tableXml1.getChildren();
									//QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
									String resultTypeDescription = "";
									for (Iterator itr = children.iterator(); itr.hasNext();) {
										Element element = (org.jdom.Element) itr.next();

										if (element.getName().equalsIgnoreCase(
												"result_instance_id")) {
											id = element.getText().trim();
											//node.fullname("patient_set_coll_id:" + id);
											System.out.println("key: " + id);
										} else if (element.getName().equalsIgnoreCase(
												"description")) {
											description = element.getText().trim();
											//node.name(description);
											//node.tooltip(description);
											log.info("Description: "
													+ description);

										}
										else if (element.getName().equalsIgnoreCase(
										"query_result_type")) {
											resultTypeDescription = element.getChildTextTrim("name");
											if(!resultTypeDescription.equalsIgnoreCase("PATIENTSET")) {
												java.awt.EventQueue.invokeLater(new Runnable() {
													public void run() {
														setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
														JOptionPane
																.showMessageDialog(jLabel1,
																		"Please note, You can not drop this item here.");
													}
												});
												return;// true;
											}
											//resultType = element;
											log.info("keep set id: " + id);
										}
										else if (element.getName().equalsIgnoreCase(
										"set_size")) {
											setsize = Integer.parseInt(element.getText().trim());
											//node.name(description);
											//node.tooltip(description);
											log.info("set_size: "+ setsize);
								}
										
									}
									//node.originalXml(text);
									//node.visualAttribute("L");
									//if (description!=null && description.indexOf("Encounter Set") >= 0) {
										//node.fullname("patient_set_enc_id:" + id);
									//}
									
									//if(description==null) {
										//node.name(resultTypeDescription);
									//}

									//addNode(node);
									//panelData.getItems().add(node);
									//parentPanel.getRunQueryButton().requestFocus();
									if(setsize == 0) {
										java.awt.EventQueue.invokeLater(new Runnable() {
											public void run() {
												setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
												JOptionPane.showMessageDialog(jLabel1,
																"This patient set does not have any patients associated with it.");
											}
										});
										//MessageBox messageBox = new MessageBox(new Shell(Display.getDefault()), SWT.ICON_WARNING | SWT.OK);
								        
								       // messageBox.setText("Warning");
								        //messageBox.setMessage("This patient set does not have any patients associated with it.");
								    	
								        //if (athread != null) {
											//athread.stop();
											//athread = null;
											//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
									
											// jRunQueryButton.setBackground(defaultcolor);
											try {
											    //RandomAccessFile f = new RandomAccessFile(filename, "rw");
												//append(f, resultFile.toString());
												//f.close();	
												pl.stop();
												pl.setVisible(false);
												jLabel2.setText("Patient Set Size: ");//+setsize+ " ("+setsize+" loaded)");
												//jStartTextField.setText(""+(k+increment));
												//jIncrementTextField.setText(""+increment);
												//jScrollPane1.setViewportView(jTable1);
												//jScrollPane1.revalidate();
												jPanel1.revalidate();
											} catch(Exception e) {
											  	e.printStackTrace();
											}
										//}
								        return;
									}
										
									
									if(id != null) {
										//if (writeFile) {
										if(jAutoSaveCheckBox.isSelected()) {
											pidFile = autoSaveFilePath;
										}
										else {
											pidFile = System.getProperty("user.dir") + "/temp/"
												+ "pid-"+generateMessageId()+".txt";
										}
										//File oDelete = new File(pidFile);
										//if (oDelete != null) {
											//oDelete.delete();
										//}
										f = new RandomAccessFile(pidFile, "rw");
										StringBuilder resultFile = new StringBuilder();
										//resultFile.append("@@i2b2 patient mapping file@@ Total columns: "+jTable1.getColumnCount()+"\n");
										//for(int i=0; i<jTable1.getColumnCount(); i++) {
											//resultFile.append(jTable1.getColumnName(i)+",");
										//}	
											//append(f, resultFile.toString());
											//f.close();
										//}
										jLabel2.setText("Patient Set Size: "+setsize+ " (0 loaded)");
										for(int k=1; k<setsize; k+=(increment)) {
											//if(k>6000) {
												//log.info("testing k: "+k);
											//}
										if(k>1) {	
											resultFile = new StringBuilder();
											jScrollPane1.setViewportView(pl);
											jScrollPane1.revalidate();
											//parent.add(pl);
											
											/*pl = new WaitPanel((int) (jScrollPane1.getWidth() * 0.40),
													(int) (jScrollPane1.getHeight() * 0.40),
													(1000));
											pl.setBounds(0, 0, pl.getParent().getWidth(), pl.getParent().getHeight());
											pl.init((int) (pl.getParent().getWidth() * 0.40), (int) (pl.getParent()
													.getHeight() * 0.40));*/
											//pl.go();
											pl.setVisible(true);
										}
											
										String result = PatientMappingQueryClient.getPidString(id, k, k+increment-1);
										
										PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
								    	try {
								    		StatusType status = pdoresponsefactory.getStatusFromResponseXML(result);
								    		if (!status.getType().equalsIgnoreCase("DONE")) {
									    		final String info = status.getValue();
									    		java.awt.EventQueue.invokeLater(new Runnable() {
													public void run() {
														//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
														JOptionPane.showMessageDialog(null, info+".");
													}
												});
									    		return; //true;
								    		}
								    	}
								    	catch(Exception e) {
								    		e.printStackTrace();
								    		return;// true;
								    	}
								    	
										getSiteColumnNames(result);
										log.info("Get columns: "+columns.size());
										
										if(hasEncryptedCol) {
											String selection = getKey();
											if(selection.equalsIgnoreCase("canceled")) {
												setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
												return;// true;
											}
										}
										
										ArrayList<PatientMappingData> list = new ArrayList<PatientMappingData>();
										new PatientMappingFactory().generateTableData(result, list);
										
										//DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
								        //model.addColumn("addedColumn1");
								        //model.addColumn("addedColumn2");
										//model.removeRow(0);
								        //model.setRowCount(list.size());
								        
								        String[] cols = new String[columns.size()+1];
								        //Class[] types = new Class[columns.size()+2];
								        //cols[0] = new String("");
								        cols[0] = new String("HIVE");
								        //types[0] = java.lang.Boolean.class;
								        //types[1] = java.lang.String.class;
								        for(int i=0; i<columns.size(); i++) {
								        	cols[i+1] = new String(columns.get(i).columnName());
								        	//types[i+2] = java.lang.String.class;
								        }
								        DefaultTableModel model = new DefaultTableModel(cols, list.size()){

											@Override
											public boolean isCellEditable(int arg0, int arg1) {
												return false;
											}
								        	
								        };   //{
								        	//@SuppressWarnings("unchecked")
											//Class[] types = new Class[] { java.lang.Boolean.class,
													//java.lang.String.class };

											//@SuppressWarnings("unchecked")
											//public Class getColumnClass(int columnIndex) {
												//if(columnIndex ==0) {
													//return java.lang.Boolean.class;
												//}
												//return java.lang.Object.class;
											//}
								       // };
								        
								        jTable1.setModel(model);
								        jTable1.setCellSelectionEnabled(true);
								        //jTable1.setSelectionModel(arg0);
								        //model = new DefaultTableModel();
								        														
										for(int i=0; i<list.size(); i++) {
											PatientMappingData pData = list.get(i);
											//model.setValueAt(new Boolean(false), i, 0);
											model.setValueAt(pData.hiveID(), i, 0);
											for(int p=0; p<columns.size(); p++) {
												String cName = columns.get(p).columnName();
												
												for(int j=0; j<pData.sites.size(); j++) {
													////getColumnIndex(pData.siteIDs.get(j).siteName());
													if(pData.sites.get(j).siteName().indexOf(cName)>=0) {
														siteID = pData.sites.get(j).siteId();
														final String siteName = pData.sites.get(j).siteName();
														////decrypt if _e 
														//java.awt.EventQueue.invokeLater(new Runnable() {
															//public void run() {
																if(siteName.toUpperCase().indexOf("_E") > 0) {
																	siteID = decryptID(siteID);
																}
																
															//}
														//});
														model.setValueAt(siteID, i, p+1);
														break;
													};
													model.setValueAt("", i, p+1);
												}
											}
										}
									//}
									
									for(int i=0; i<jTable1.getColumnCount();i++) {
										TableColumn col = jTable1.getColumnModel().getColumn(i);  
										DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();     
										dtcr.setHorizontalAlignment(SwingConstants.CENTER);   
										col.setCellRenderer(dtcr);
									}
									
									String name = description.substring(description.indexOf("\""));
									jLabel1.setText("  Patient Set Identifier: "+name);
									saveFileName = name.replaceAll("[@:\"]", "");//description.substring(description.indexOf("\"")));
									setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
									//pl.stop();
									//pl.setVisible(false);
									jLabel2.setText("Patient Set Size: "+setsize+ " ("+(k+increment-1)+" loaded)");
									//jStartTextField.setText(""+(k+increment));
									//jIncrementTextField.setText(""+increment);
									//jScrollPane1.setViewportView(jTable1);
									//jScrollPane1.revalidate();
									jPanel1.revalidate();
									
									/////////////////////////
									
									////////////////////////
									//StringBuilder resultFile = new StringBuilder();
								    //resultFile.append("@@i2b2 patient mapping file@@ Total columns: "+jTable1.getColumnCount()+"\n");
								    //for(int i=0; i<jTable1.getColumnCount(); i++) {
								    	//resultFile.append(jTable1.getColumnName(i)+",");
								    //}
									if(k==1) {
										//resultFile.append("@@i2b2 patient mapping file@@ Total columns: "+jTable1.getColumnCount()+"\n");
										for(int i=0; i<jTable1.getColumnCount(); i++) {
											resultFile.append(jTable1.getColumnName(i)+",");
										}	
									}
								    for(int i=0; i<jTable1.getRowCount(); i++) {
								    	resultFile.append("\n");
								    	for(int j=0; j<jTable1.getColumnCount(); j++) {
								    		resultFile.append(""+jTable1.getValueAt(i, j)+",");
								    	}
								    }
								    try {
									    //RandomAccessFile f = new RandomAccessFile(filename, "rw");
										append(f, resultFile.toString());
										//f.close();	
								    } catch(Exception e) {
								    	e.printStackTrace();
								    }
									/*jScrollPane1.setViewportView(pl);
									jScrollPane1.revalidate();
									//parent.add(pl);
									
									pl.setBounds(0, 0, pl.getParent().getWidth(), pl.getParent().getHeight());
									pl.init((int) (pl.getParent().getWidth() * 0.40), (int) (pl.getParent()
											.getHeight() * 0.40));
									pl.go();
									pl.setVisible(true);*/
								}
									//parent.add(thisPanel);
								try {
								    //RandomAccessFile f = new RandomAccessFile(filename, "rw");
									//append(f, resultFile.toString());
									f.close();	
								} catch(Exception e) {
								  	e.printStackTrace();
								}
								////
								log.info("Selected file: "+pidFile);
								FileReader fr;
								BufferedReader br = null;
								try{
									fr = new FileReader(new File(pidFile));
									br = new BufferedReader(fr);
									String line = br.readLine();
									//if(!line.startsWith("@@i2b2 patient mapping file@@")) {
										//java.awt.EventQueue.invokeLater(new Runnable() {
											//public void run() {
												////setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
												//JOptionPane.showMessageDialog(jLabel1, "The file is not in a valid format.", "Error importing", JOptionPane.ERROR_MESSAGE);
											//}
										//});
										//JOptionPane.showMessageDialog(null, "The file is not a valid.", "Error importing", JOptionPane.ERROR_MESSAGE);
										//return;
									//}
									//line = br.readLine();
									//log.info("column name: "+cols.length);
									//String[] cols = line.split(",");
									String[] cols = new String[columns.size()+1];
							        //Class[] types = new Class[columns.size()+2];
							        //cols[0] = new String("");
							        cols[0] = new String("HIVE");
							        //types[0] = java.lang.Boolean.class;
							        //types[1] = java.lang.String.class;
							        for(int i=0; i<columns.size(); i++) {
							        	cols[i+1] = new String(columns.get(i).columnName());
							        	//types[i+2] = java.lang.String.class;
							        }
							        log.info("column name: "+cols.length);
									
									DefaultTableModel model = new DefaultTableModel(cols, setsize+200){

										@Override
										public boolean isCellEditable(int arg0, int arg1) {
											return false;
										}
							        	
							        };   //{
							        	//@SuppressWarnings("unchecked")
										//Class[] types = new Class[] { java.lang.Boolean.class,
												//java.lang.String.class };

										//@SuppressWarnings("unchecked")
										//public Class getColumnClass(int columnIndex) {
											//if(columnIndex ==0) {
												//return java.lang.Boolean.class;
											//}
											//return java.lang.Object.class;
										//}
							       // };
							        jTable1.setModel(model);
							        
							        String[] row;
							        int rowCount = 0;
									line = br.readLine();
									while(line!=null) {
										log.info(line);
										row = line.split(",");
										log.info(line+"row length: "+row.length);
										String pid = "";
										if(row.length > jTable1.getColumnCount()) {
											log.info("skip "+line+"row length: "+row.length);
										}
										else {
											for(int i=0; i<row.length; i++) {
												pid = row[i];
												//if(cols[i].indexOf("_E") > 0) {
													//////id = decryptID(id);
			    								//}
												//decryptID(row[i]);
												jTable1.setValueAt(pid, rowCount, i);
											}
											rowCount++;
										}
										line = br.readLine();
									}
									model.setRowCount(rowCount);
									br.close();
									
									pl.stop();
									pl.setVisible(false);
									jLabel2.setText("Patient Set Size: "+setsize+ " ("+setsize+" loaded)");
									//jStartTextField.setText(""+(k+increment));
									//jIncrementTextField.setText(""+increment);
									jScrollPane1.setViewportView(jTable1);
									jScrollPane1.revalidate();
									jPanel1.revalidate();
								} catch(Exception e) {
									e.printStackTrace();
									if(br!=null) {
										try {
											br.close();
										}catch(Exception e1) {}
									}
								}	
								return;// true;
								}

								}
								////
								else if (tableXml1.getName().equalsIgnoreCase(
									"patient_set")) {
								List children = tableXml1.getChildren();
								//QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
								String resultTypeDescription = "";
								for (Iterator itr = children.iterator(); itr.hasNext();) {
									Element element = (org.jdom.Element) itr.next();
							
									if (element.getName().equalsIgnoreCase(
											"patient")) {
										id = element.getChildText("patient_id").trim();
										//node.fullname("patient_set_coll_id:" + id);
										System.out.println("patient id: " + id);
									} else if (element.getName().equalsIgnoreCase(
											"description")) {
										description = element.getText().trim();
										//node.name(description);
										//node.tooltip(description);
										log.info("Description: "
												+ description);
							
									}
									else if (element.getName().equalsIgnoreCase(
									"query_result_type")) {
										resultTypeDescription = element.getChildTextTrim("name");
										if(!resultTypeDescription.equalsIgnoreCase("PATIENTSET")) {
											java.awt.EventQueue.invokeLater(new Runnable() {
												public void run() {
													setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
													JOptionPane
															.showMessageDialog(jLabel1,
																	"Please note, You can not drop this item here.");
												}
										});
											return;// true;
										}
										//resultType = element;
										log.info("keep set id: " + id);
									}
								}
								//node.originalXml(text);
								//node.visualAttribute("L");
								//if (description!=null && description.indexOf("Encounter Set") >= 0) {
									//node.fullname("patient_set_enc_id:" + id);
								//}
								
								//if(description==null) {
									//node.name(resultTypeDescription);
								//}
							
								//addNode(node);
								//panelData.getItems().add(node);
								//parentPanel.getRunQueryButton().requestFocus();
								
								if(id != null) {
									String result = PatientMappingQueryClient.getPidStringFromPid(id);
									getSiteColumnNames(result);
									log.info("Get columns: "+columns.size());
									
									if(hasEncryptedCol) {
										String selection = getKey();
										if(selection.equalsIgnoreCase("canceled")) {
											setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
											return;// true;
										}
									}
									
									ArrayList<PatientMappingData> list = new ArrayList<PatientMappingData>();
									new PatientMappingFactory().generateTableData(result, list);
									
									//DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
							        //model.addColumn("addedColumn1");
							        //model.addColumn("addedColumn2");
									//model.removeRow(0);
							        //model.setRowCount(list.size());
							        
							        String[] cols = new String[columns.size()+1];
							        //Class[] types = new Class[columns.size()+2];
							        //cols[0] = new String("");
							        cols[0] = new String("HIVE");
							        //types[0] = java.lang.Boolean.class;
							        //types[1] = java.lang.String.class;
							        for(int i=0; i<columns.size(); i++) {
							        	cols[i+1] = new String(columns.get(i).columnName());
							        	//types[i+2] = java.lang.String.class;
							        }
							        DefaultTableModel model = new DefaultTableModel(cols, list.size()){
							
										@Override
										public boolean isCellEditable(int arg0, int arg1) {
											return false;
										}
							        	
							        };   //{
							        	//@SuppressWarnings("unchecked")
										//Class[] types = new Class[] { java.lang.Boolean.class,
												//java.lang.String.class };
							
										//@SuppressWarnings("unchecked")
										//public Class getColumnClass(int columnIndex) {
											//if(columnIndex ==0) {
												//return java.lang.Boolean.class;
											//}
											//return java.lang.Object.class;
										//}
							       // };
							        jTable1.setModel(model);
							        jTable1.setCellSelectionEnabled(true);
							        //jTable1.setSelectionModel(arg0);
							        //model = new DefaultTableModel();
									for(int i=0; i<list.size(); i++) {
										PatientMappingData pData = list.get(i);
										//model.setValueAt(new Boolean(false), i, 0);
										model.setValueAt(pData.hiveID(), i, 0);
										for(int p=0; p<columns.size(); p++) {
											String cName = columns.get(p).columnName();
											
											for(int j=0; j<pData.sites.size(); j++) {
												////getColumnIndex(pData.siteIDs.get(j).siteName());
												if(pData.sites.get(j).siteName().indexOf(cName)>=0) {
													siteID = pData.sites.get(j).siteId();
													final String siteName = pData.sites.get(j).siteName();
													////decrypt if _e 
													//java.awt.EventQueue.invokeLater(new Runnable() {
														//public void run() {
															if(siteName.toUpperCase().indexOf("_E") > 0) {
																siteID = decryptID(siteID);
															}
															
														//}
													//});
													model.setValueAt(siteID, i, p+1);
													break;
												};
												model.setValueAt("", i, p+1);
											}
										}
									}
								}
								
								for(int i=0; i<jTable1.getColumnCount();i++) {
									TableColumn col = jTable1.getColumnModel().getColumn(i);  
									DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();     
									dtcr.setHorizontalAlignment(SwingConstants.CENTER);   
									col.setCellRenderer(dtcr);
								}
								
								jLabel1.setText("Patient Set Identifier: ");//+description.substring(description.indexOf("\"")));
								setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								
								return;// true;
							}
								////
								else {
									java.awt.EventQueue.invokeLater(new Runnable() {
										public void run() {
											setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
											JOptionPane
													.showMessageDialog(jLabel1,
															"Please note, You can not drop this item here.");
										}
									});
									setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
									pl.stop();
									pl.setVisible(false);
									return;// true;
								}
										
										
									} catch (Exception e) {
										java.awt.EventQueue.invokeLater(new Runnable() {
											public void run() {
												setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
												JOptionPane.showMessageDialog(jLabel1,
																"Please note, You can not drop this item here.");
											}
										});
									}
								//}
							//});
						}
					};
					
					try {						
						athread.start();
					} catch(Exception e) {
						e.printStackTrace();
					}
					
			
			} catch (Exception e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						JOptionPane.showMessageDialog(jLabel1,
										"Please note, You can not drop this item here.");
					}
				});
			}

			return true;
		}

		protected Transferable createTransferable(JComponent c) {

			Transferable t = null;
			String str = jLabel1.getText();

			// t = new QueryDataTransferable(str);
			// return t;
			//return new StringSelection(str);

			//Transferable t = new Transferable();

			//QueryDefinitionType queryDefinitionType = new QueryDefinitionType();

			/*QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();
			for (int i = 0; i < jPanel1.getComponentCount() - 1; i++) {
				if (jPanel1.getComponent(i) instanceof GroupPanel) {
					GroupPanel panel = (GroupPanel) jPanel1.getComponent(i);// getTreePanel(i);

					if ((panel != null) && (panel.data().getItems().size() > 0)) {
						queryDefinitionType.setQueryName(jNameLabel.getText()
								.replace(" Query Name: ", ""));
						// queryDefinitionType.setQueryName(panel.data()
						// .getItems().get(0).name());
						// + "_" + generateMessageId().substring(0, 4));
						ArrayList<QueryConceptTreeNodeData> nodelist = panel
								.data().getItems();
						if ((nodelist != null) && (nodelist.size() > 0)) {
							// System.out.println("Panel: "+panel.getGroupName()+
							// " Excluded:
							// "+((panel.data().exclude())?"yes":"no"));
							PanelType panelType = new PanelType();
							panelType.setInvert((panel.data().exclude()) ? 1
									: 0);
							PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();
							totalOccurrences.setValue(panel
									.getOccurrenceTimes());
							panelType.setTotalItemOccurrences(totalOccurrences);
							panelType.setPanelNumber(i + 1);

							for (int j = 0; j < nodelist.size(); j++) {
								QueryConceptTreeNodeData node = nodelist.get(j);
								// System.out.println("\tItem: "+node.fullname())
								// ;

								// create item
								ItemType itemType = new ItemType();

								itemType.setItemKey(node.fullname());
								itemType.setItemName(node.name());
								// mm removed
								// itemType.setItemTable(node.lookuptable());
								itemType.setTooltip(node.tooltip());
								itemType.setHlevel(Integer.parseInt(node
										.hlevel()));
								itemType.setClazz("ENC");

								// handle time constrain
								if (panel.data().startTime() != -1
										|| panel.data().endTime() != -1) {
									ConstrainByDate timeConstrain = panel
											.data().writeTimeConstrain();
									itemType.getConstrainByDate().add(
											timeConstrain);
								}

								// handle value constrain
								if (!node.valuePropertyData().noValue()) {
									ConstrainByValue valueConstrain = node
											.valuePropertyData()
											.writeValueConstrain();
									itemType.getConstrainByValue().add(
											valueConstrain);
								}

								panelType.getItem().add(itemType);
							}
							queryDefinitionType.getPanel().add(panelType);

						}
					}
				}

			}
			StringWriter strWriter = new StringWriter();

			try {

				DndType dnd = new DndType();

				edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();

				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
				// psmOf = new
				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
				dnd.getAny().add(
						psmOf.createQueryDefinition(queryDefinitionType));

				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
				QueryJAXBUtil.getJAXBUtil().marshaller(
						of.createPluginDragDrop(dnd), strWriter);
				str = strWriter.toString();

			} catch (Exception e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										jNameLabel,
										"You can not use this item in a query, "
												+ "it is only used for organizing the lists.");
					}
				});
			}*/

			// edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory vdoOf = new
			// edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
			/*
			 * //edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
			 * psmOf = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
			 * dnd.getAny().add(vdoOf.c.c(queryDefinitionType));
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
			 * QueryJAXBUtil.getJAXBUtil().marshaller(
			 * of.createPluginDragDrop(dnd), strWriter);
			 */

			//t = new QueryDataTransferable(str);
			return t;
		}
	}
	
	class TableTextHandler extends TransferHandler {
		public TableTextHandler() {
			super("text");
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavor) {
			/*jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.YELLOW));
			jLabel1.paintImmediately(jLabel1.getVisibleRect());

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			jLabel1.setBorder(javax.swing.BorderFactory
					.createLineBorder(Color.BLACK));

			if ((System.currentTimeMillis() - lEventTime) > 2000) {

				return true;
			}*/
			return false;
		}

		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
		}

		public boolean importData(JComponent comp, Transferable t) {
			
			return false;
		}

		protected Transferable createTransferable(JComponent c) {

			Transferable t = null;
			String str = null;//"";//jLabel1.getText();

			// t = new QueryDataTransferable(str);
			// return t;
			//return new StringSelection(str);

			//Transferable t = new Transferable();

			//QueryDefinitionType queryDefinitionType = new QueryDefinitionType();

			/*QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();
			for (int i = 0; i < jPanel1.getComponentCount() - 1; i++) {
				if (jPanel1.getComponent(i) instanceof GroupPanel) {
					GroupPanel panel = (GroupPanel) jPanel1.getComponent(i);// getTreePanel(i);

					if ((panel != null) && (panel.data().getItems().size() > 0)) {
						queryDefinitionType.setQueryName(jNameLabel.getText()
								.replace(" Query Name: ", ""));
						// queryDefinitionType.setQueryName(panel.data()
						// .getItems().get(0).name());
						// + "_" + generateMessageId().substring(0, 4));
						ArrayList<QueryConceptTreeNodeData> nodelist = panel
								.data().getItems();
						if ((nodelist != null) && (nodelist.size() > 0)) {
							// System.out.println("Panel: "+panel.getGroupName()+
							// " Excluded:
							// "+((panel.data().exclude())?"yes":"no"));
							PanelType panelType = new PanelType();
							panelType.setInvert((panel.data().exclude()) ? 1
									: 0);
							PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();
							totalOccurrences.setValue(panel
									.getOccurrenceTimes());
							panelType.setTotalItemOccurrences(totalOccurrences);
							panelType.setPanelNumber(i + 1);

							for (int j = 0; j < nodelist.size(); j++) {
								QueryConceptTreeNodeData node = nodelist.get(j);
								// System.out.println("\tItem: "+node.fullname())
								// ;

								// create item
								ItemType itemType = new ItemType();

								itemType.setItemKey(node.fullname());
								itemType.setItemName(node.name());
								// mm removed
								// itemType.setItemTable(node.lookuptable());
								itemType.setTooltip(node.tooltip());
								itemType.setHlevel(Integer.parseInt(node
										.hlevel()));
								itemType.setClazz("ENC");

								// handle time constrain
								if (panel.data().startTime() != -1
										|| panel.data().endTime() != -1) {
									ConstrainByDate timeConstrain = panel
											.data().writeTimeConstrain();
									itemType.getConstrainByDate().add(
											timeConstrain);
								}

								// handle value constrain
								if (!node.valuePropertyData().noValue()) {
									ConstrainByValue valueConstrain = node
											.valuePropertyData()
											.writeValueConstrain();
									itemType.getConstrainByValue().add(
											valueConstrain);
								}

								panelType.getItem().add(itemType);
							}
							queryDefinitionType.getPanel().add(panelType);

						}
					}
				}

			}
			StringWriter strWriter = new StringWriter();

			try {

				DndType dnd = new DndType();

				edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();

				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
				// psmOf = new
				// edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
				dnd.getAny().add(
						psmOf.createQueryDefinition(queryDefinitionType));

				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
				QueryJAXBUtil.getJAXBUtil().marshaller(
						of.createPluginDragDrop(dnd), strWriter);
				str = strWriter.toString();

			} catch (Exception e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane
								.showMessageDialog(
										jNameLabel,
										"You can not use this item in a query, "
												+ "it is only used for organizing the lists.");
					}
				});
			}*/

			// edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory vdoOf = new
			// edu.harvard.i2b2.crcxmljaxb.datavo.vdo.ObjectFactory();
			/*
			 * //edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory
			 * psmOf = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
			 * dnd.getAny().add(vdoOf.c.c(queryDefinitionType));
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new
			 * edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
			 * QueryJAXBUtil.getJAXBUtil().marshaller(
			 * of.createPluginDragDrop(dnd), strWriter);
			 */

			//t = new QueryDataTransferable(str);
			//return t;
			
			//PatientData nodedata = (PatientData) node.getUserObject();
			//str = nodedata.name();
			//if (str.equalsIgnoreCase("working ......")) {
				//str = "logicquery";
			//}

			StringWriter strWriter = new StringWriter();
			try {
				JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

				// JAXBElement jaxbElement =
				// jaxbUtil.unMashallFromString(nodedata.xmlContent());
				// ResponseMessageType messageType =
				// (ResponseMessageType)jaxbElement.getValue();
				// BodyType bt = messageType.getMessageBody();
				// ResultResponseType resultResponseType =
				// (ResultResponseType) new
				// JAXBUnWrapHelper().getObjectByClass(bt.getAny(),
				// ResultResponseType.class);
				// QueryResultInstanceType queryResultInstanceType =
				// resultResponseType.getQueryResultInstance().get(0);
				// strWriter = new StringWriter();
				
				//log.info(jTable1.getSelectedColumn());
				String hid = (String) jTable1.getValueAt(jTable1.getSelectedRow(), jTable1.getSelectedColumn());
				String site = jTable1.getColumnName(jTable1.getSelectedColumn());;
				if(hid == null || hid.equals("") || hid.isEmpty()) {
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							JOptionPane
									.showMessageDialog(jLabel1,
											"Please note, the cell selected is empty.");
						}
					});
					//log.info("no hive id was found");
					//for(int i=1; i<jTable1.getColumnCount(); i++) {
					//	String temp = (String) jTable1.getValueAt(jTable1.getSelectedRow(), i);
					//	if(temp != null && !temp.equals("") && !temp.isEmpty()) {	
					//		site = jTable1.getColumnName(i);
					//		hid = /*jTable1.getColumnName(i)+":"+*/temp;
					//		break;
					//	}
					//}
				}
				//else {
					//site = "HIVE";
				//}
				
				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientType patientType = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientType();
				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientSet patientSet = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.PatientSet();
				patientType.setPatientId(hid);//nodedata.patientID());
				// patientType.setUploadId(nodedata.patientSetID());
				//patientSet.setPatientSetId(nodedata.patientSetID());
				//patientSet.setPatientSetName(nodedata.queryName());
				patientSet.getPatient().add(patientType);
				
				PidType pidType = new PidType();
				PidType.PatientId pid = new PidType.PatientId();
				pid.setValue(hid);//nodedata.patientID());
				pidType.setPatientId(pid);
				
				PidSet pset = new PidSet();
				pset.getPid().add(pidType);
				
				PatientSet pdoPatientSet = new PatientSet();
				PatientType pdoPatientType = new PatientType();
				PatientIdType pdoPidType = new PatientIdType();
				pdoPidType.setValue(hid);
				pdoPidType.setSource(site);
				pdoPatientType.setPatientId(pdoPidType);
				pdoPatientSet.getPatient().add(pdoPatientType);

				DndType dnd = new DndType();
				// edu.harvard.i2b2.crcxmljaxb.datavo.pdo.ObjectFactory
				// pdoOf = new
				// edu.harvard.i2b2.crcxmljaxb.datavo.pdo.ObjectFactory();
				// dnd.getAny().add(patientType);
				
				//dnd.getAny().add(patientSet);
				//dnd.getAny().add(pset);
				dnd.getAny().add(pdoPatientSet);
				edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
				PatientMappingJAXBUtil.getJAXBUtil().marshaller(
						of.createPluginDragDrop(dnd), strWriter);
			} catch (JAXBUtilException e) {
				// log.error("Error marshalling Ont drag text");
				// throw e;
				e.printStackTrace();
			}

			// log.info("Ont Client dragged "+ strWriter.toString());
			str = strWriter.toString();
			//System.out.println("Node xml set to: " + strWriter.toString());

		t = new QueryDataTransferable(str);
		return t;
		}
	}
	
	private String getSiteColumnNames(String result) {
		try {
			PDOResponseMessageModel pdoresponsefactory = new PDOResponseMessageModel();
			StatusType statusType = pdoresponsefactory
					.getStatusFromResponseXML(result);
			if (!statusType.getType().equalsIgnoreCase("DONE")) {
				return "error";
			}

			JAXBUtil jaxbUtil = PatientMappingJAXBUtil.getJAXBUtil();

			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(result);
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement
					.getValue();
			BodyType bodyType = messageType.getMessageBody();
			PatientDataResponseType responseType = (PatientDataResponseType) new JAXBUnWrapHelper()
					.getObjectByClass(bodyType.getAny(),
							PatientDataResponseType.class);
			
			//PageType pageType = responseType.getPage();
			//if (pageType != null) {
				//final int returnLastIndex = pageType.getPagingByPatients()
				//		.getPatientsReturned().getLastIndex().intValue();
				//final int returnFirstIndex = pageType.getPagingByPatients()
				//		.getPatientsReturned().getFirstIndex().intValue();
				//final int requestLastIndex = pageType.getPagingByPatients()
				//		.getPatientsRequested().getLastIndex().intValue();
				/*if (returnLastIndex < requestLastIndex) {
					// System.out.println("Can't return all the requested "+
					// requestIndex+" patients, only "+returnIndex+" patients returned");
					explorer.getDisplay().syncExec(new Runnable() {
						public void run() {
							// MessageBox mBox = new MessageBox(explorer
							// .getShell(), SWT.ICON_INFORMATION
							// | SWT.OK );
							// mBox.setText("Please Note ...");
							// mBox.setMessage("Only "+(returnLastIndex-returnFirstIndex+1)+" patients returned");
							// mBox.open();
							if (explorer.runMode() >= 0) {
								explorer.setIncrementNumber(returnLastIndex
										- returnFirstIndex + 1);
							} else if (explorer.runMode() == -1) {
								explorer.setDecreaseNumber(returnLastIndex
										- returnFirstIndex + 1);
							}
						}
					});
					explorer.returnedNumber(returnLastIndex - returnFirstIndex
							+ 1);
				} else {
					explorer.returnedNumber(-1);
				}*/
			//}
			//else {
				/*explorer.getDisplay().syncExec(new Runnable() {
					public void run() {
						// MessageBox mBox = new MessageBox(explorer
						// .getShell(), SWT.ICON_INFORMATION
						// | SWT.OK );
						// mBox.setText("Please Note ...");
						// mBox.setMessage("Only "+(returnLastIndex-returnFirstIndex+1)+" patients returned");
						// mBox.open();
						if (explorer.runMode() >= 0) {
							explorer.setIncrementNumber(-1);
						} else if (explorer.runMode() == -1) {
							explorer.setDecreaseNumber(-1);
						}
					}
				});*/
			//}

			//StringBuilder resultFile = new StringBuilder();
			//resultFile.append(GetTimelineHeader());
			
			//columns.clear();
			ArrayList<SiteColumnData> tmp = new ArrayList<SiteColumnData>();
			currentIndex = 1;
			PidSet patientMappingSet = pdoresponsefactory.getPidSetFromResponseXML(result);
			if (patientMappingSet != null) {
				//log.debug("Total patient: "
						//+ patientMappingSet.getPid().size());
				for(int i=0; i<patientMappingSet.getPid().size(); i++) {
					PidType pidType = patientMappingSet.getPid().get(i);
					//PatientMappingData pmData = new PatientMappingData();
					List<PatientMapId> ids = pidType.getPatientMapId();
					for(int p=0; p<ids.size(); p++) {
						PatientMapId mapId = ids.get(p);
						String orgsiteId = mapId.getSource();
						String siteId = orgsiteId;
						if(orgsiteId.lastIndexOf("_E") >0) {
							siteId = orgsiteId.substring(0, orgsiteId.lastIndexOf("_E"));
							hasEncryptedCol = true;
						}
						boolean found = false;
						for(int j=0; j<tmp.size(); j++) {
							SiteColumnData cData = tmp.get(j);
							if(cData.columnName().equals(siteId)) {
								found = true;
								break;
							}
						}
						if(!found) {
							SiteColumnData newData = new SiteColumnData();
							newData.columnName(new String(siteId));
							newData.columnNumber(currentIndex);
							
							//columns.add(newData);
							tmp.add(newData);
							currentIndex++;
						}
						//list.add(pmData);
						
						if(tmp.size() > columns.size()) {
							columns.clear();
							//cols = new String[tmp.size()];
							for(int k=0; k<tmp.size(); k++) {
								columns.add(tmp.get(k));
								//cols[k] = new String(tmp.get(k).columnName());
							}
						}
					}
				}
			} else {
				return "error";
			}

			// / testing the visit set
			// PatientDataType.VisitDimensionSet visitSet =
			// pdoresponsefactory.getVisitSetFromResponseXML(result);
			// System.out.println("Total visits: "+visitSet.getVisitDimension().
			// size());

			//log.debug("\nGenerate lld:");
			
			// log.debug("\nThe lld file: \n"+resultFile.toString());
			return "";//resultFile.toString();
		} catch (org.apache.axis2.AxisFault e) {
			e.printStackTrace();
			//log.error(e.getMessage());
			return null;
		} catch (Exception e) {
			//log.error(e.getMessage());
			e.printStackTrace();
			return "error";
		}
		//for(int i=0; i<list.size(); i++) {
		//	Patientlist.get
		//}
	}
	
	private String openFileDialog() {
		FileDialog dialog = new FileDialog(new Shell(), SWT.OPEN);
		//String filterPath = "c://";
		//String platform = SWT.getPlatform();
		dialog.setText("select file");
		dialog.setFilterNames(new String[] { "Text Files", "All Files (*.*)" });
	    dialog.setFilterExtensions(new String[] { "*.txt", "*.*" }); // Windows
	                                    // wild
	                                    // cards
		//dialog.setFilterPath(filterPath);
		String returnValue = dialog.open();
		//if (returnValue == null) {
		//	return null;
		//} else {
			return returnValue;
		//}

	}
	
	private String decryptID(String id) {
		key = UserInfoBean.getInstance().getKey();
		if (key == null || key.length() == 0 || id.equalsIgnoreCase("")) {
			//getKey();
			return id;
		}
		SecurityUtil util = new SecurityUtil(key);
		if (util == null) {
			JOptionPane.showMessageDialog(this, "Not a valid key");
			return id;
		}
		String deID = util.decryptNotes(id);
		// System.out.println("notes: " + deNote);

		if (deID.equalsIgnoreCase("[I2B2-Error] Invalid key") || deID.equalsIgnoreCase("")) {
			//JOptionPane.showMessageDialog(this, "Not a valid key");
			return id;
		}

		return deID;
	}
	
	private String encryptID(String id) {
		key = UserInfoBean.getInstance().getKey();
		if (key == null || key.length() == 0 || id.equalsIgnoreCase("")) {
			getKey();
			//return id;
		}
		SecurityUtil util = new SecurityUtil(key);
		if (util == null) {
			JOptionPane.showMessageDialog(this, "Not a valid key");
			return id;
		}
		String deID = util.encryptNotes(id);
		// System.out.println("notes: " + deNote);

		if (deID.equalsIgnoreCase("[I2B2-Error] Invalid key") || deID.equalsIgnoreCase("")) {
			//JOptionPane.showMessageDialog(this, "Not a valid key");
			return id;
		}

		return deID;
	}
	
	private void jRightButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
	}

	private void jLeftButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (athread != null) {
			athread.stop();
			athread = null;
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	
			// jRunQueryButton.setBackground(defaultcolor);
			try {
			    //RandomAccessFile f = new RandomAccessFile(filename, "rw");
				//append(f, resultFile.toString());
				f.close();	
				pl.stop();
				pl.setVisible(false);
				jLabel2.setText("Patient Set Size: ");//+setsize+ " ("+setsize+" loaded)");
				//jStartTextField.setText(""+(k+increment));
				//jIncrementTextField.setText(""+increment);
				//jScrollPane1.setViewportView(jTable1);
				//jScrollPane1.revalidate();
				jPanel1.revalidate();
			} catch(Exception e) {
			  	e.printStackTrace();
			}
		}
	}
	
	private String getNoteKeyDrive() {
		File[] drives = File.listRoots();
		String filename = "i2b2patientidkey.txt";
		for (int i = drives.length - 1; i >= 0; i--) {
			if (drives[i].getPath().startsWith("A")
					|| drives[i].getPath().startsWith("B")) {
				continue;
			}

			File tmp = new File(drives[i]/* +File.separator */+ filename);
			if (tmp.exists()) {
				return drives[i]/* +File.separator */+ filename;
			}
			// else {
			// return null;
			// }
		}

		File testFile = new File("i2b2patientidkey.txt");
		System.out.println("file dir: " + testFile.getAbsolutePath());
		if (testFile.exists()) {
			return testFile.getAbsolutePath();
		}

		return null;
	}
	
	private String getKey() {
		String path = null;
		key = UserInfoBean.getInstance().getKey();
		if (key == null) {
			if ((path = getNoteKeyDrive()) == null) {
				Object[] possibleValues = { "Type in the key",
						"Browse to find the file containing the key" };
				String selectedValue = (String) JOptionPane
						.showInputDialog(
								this,
								
								"The data you have selected to view contains protected \n"
										+ "health information and has been encrypted.\n"
										+ "In order to view this information you must enter \n"
										+ "the decryption key for this project. \n"
										+ "The key can be entered by either manually entering \n"
										+ "the key or selecting the file that contains the key.",
								"Decryption Key", JOptionPane.QUESTION_MESSAGE,
								null, possibleValues, possibleValues[0]);
				if (selectedValue == null) {
					return "canceled";
				}
				if (selectedValue.equalsIgnoreCase("Type in the key")) {
					key = JOptionPane.showInputDialog(this,
							"Please input the decryption key");
					if (key == null) {
						return "canceled";
					}
				} else {
					JFileChooser chooser = new JFileChooser();
					int returnVal = chooser.showOpenDialog(this);
					if (returnVal == JFileChooser.CANCEL_OPTION) {
						return "canceled";
					}

					File f = null;
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						f = chooser.getSelectedFile();
						System.out.println("Open this file: "
								+ f.getAbsolutePath());

						BufferedReader in = null;
						try {
							in = new BufferedReader(new FileReader(f
									.getAbsolutePath()));
							String line = null;
							while ((line = in.readLine()) != null) {
								if (line.length() > 0) {
									key = line.substring(
											line.indexOf("\"") + 1, line
													.lastIndexOf("\""));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (in != null) {
								try {
									in.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} else {
				System.out.println("Found key file: " + path);
				BufferedReader in = null;
				try {
					in = new BufferedReader(new FileReader(path));
					String line = null;
					while ((line = in.readLine()) != null) {
						if (line.length() > 0) {
							key = line.substring(line.indexOf("\"") + 1, line
									.lastIndexOf("\""));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		if (key == null) {
			return null;
		} else {
			UserInfoBean.getInstance().setKey(key);
		}
		return key;
	}
	
	protected String generateMessageId() {
		StringWriter strWriter = new StringWriter();
		for (int i = 0; i < 20; i++) {
			int num = getValidAcsiiValue();
			strWriter.append((char) num);
		}
		return strWriter.toString();
	}

	private int getValidAcsiiValue() {
		int number = 48;
		while (true) {
			number = 48 + (int) Math.round(Math.random() * 74);
			if ((number > 47 && number < 58) || (number > 64 && number < 91)
					|| (number > 96 && number < 123)) {
				break;
			}
		}
		return number;

	}
	
	class QueryDataTransferable implements Transferable {
		public QueryDataTransferable(Object data) {
			super();
			this.data = data;
			flavors[0] = DataFlavor.stringFlavor;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return true;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			return data;
		}

		private Object data;
		private final DataFlavor[] flavors = new DataFlavor[1];
	}
	
	 	private javax.swing.JCheckBox jAutoSaveCheckBox;
	    private javax.swing.JButton jConvertButton;
	    private javax.swing.JButton jImportButton;
	    private javax.swing.JTextField jIncrementTextField;
	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JLabel jLabel2;
	    private javax.swing.JLabel jLabel3;
	    private javax.swing.JLabel jLabel4;
	    private javax.swing.JButton jLeftButton;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JPanel jPanel2;
	    private javax.swing.JButton jRightButton;
	    private javax.swing.JButton jSaveButton;
	    private javax.swing.JScrollPane jScrollPane1;
	    private javax.swing.JTextField jStartTextField;
	    private javax.swing.JTable jTable1;
 
    
}
