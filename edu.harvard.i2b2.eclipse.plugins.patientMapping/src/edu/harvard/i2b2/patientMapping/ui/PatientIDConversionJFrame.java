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
/*
 * PatientIDConversionJFrame.java
 *
 * Created on October 25, 2012, 1:52 PM
 */

package edu.harvard.i2b2.patientMapping.ui;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

//import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
//import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

//import edu.harvard.i2b2.eclipse.UserInfoBean;
//import edu.harvard.i2b2.eclipse.plugins.patientMapping.utils.NoteCryptUtil;
import edu.harvard.i2b2.patientMapping.dataModel.PatientIDConversionFactory;

@SuppressWarnings("serial")
public class PatientIDConversionJFrame extends javax.swing.JFrame {
	
	private static final Log log = LogFactory.getLog(PatientIDConversionJFrame.class);
	
    /** Creates new form ConvertJFrame */
    public PatientIDConversionJFrame() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    
    private void initComponents() {
        jInputPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jInputFilePathTextField = new javax.swing.JTextField();
        jSelectInputButton = new javax.swing.JButton();
        jInputComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jOutputPanel = new javax.swing.JPanel();
        jSiteTextField = new javax.swing.JTextField();
        jSiteLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jOutputFilePathTextField = new javax.swing.JTextField();
        jSelectOutputButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jOutputComboBox = new javax.swing.JComboBox();
        jButtonPanel = new javax.swing.JPanel();
        jConvertButton = new javax.swing.JButton();
        jCloseButton = new javax.swing.JButton();

        getContentPane().setLayout(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Convert File");
        setResizable(false);
        jInputPanel.setLayout(null);

        jInputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Input File"));
        jLabel1.setText("Input File Name: ");
        jInputPanel.add(jLabel1);
        jLabel1.setBounds(20, 30, 90, 20);

        jInputPanel.add(jInputFilePathTextField);
        jInputFilePathTextField.setBounds(110, 30, 250, 20);

        jSelectInputButton.setText("Select File");
        jSelectInputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSelectInputButtonActionPerformed(evt);
            }
        });

        jInputPanel.add(jSelectInputButton);
        jSelectInputButton.setBounds(370, 30, 90, 23);

        jInputComboBox.setModel(new javax.swing.DefaultComboBoxModel(
        		new String[] { 
        			"Two columns - patient number and unencrypted MRN", 
        			"Three columns - patient number, site name and unencrypted MRN",
        			"Two columns - patient number and encrypted MRN", 
        			"Three columns - patient number, site name and encrypted MRN",
        			"Ten Columns (i2b2 1.6 Patient Mapping) Unencrypted MRN",
        			"Eleven Columns (i2b2 1.7 Patient Mapping) Unencrypted MRN"}));
        jInputComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jInputComboBoxActionPerformed(evt);
            }
        });
        jInputPanel.add(jInputComboBox);
        jInputComboBox.setBounds(120, 70, 340, 22);

        jLabel3.setText("Input File Format:");
        jInputPanel.add(jLabel3);
        jLabel3.setBounds(20, 70, 90, 20);

        getContentPane().add(jInputPanel);
        jInputPanel.setBounds(10, 0, 470, 110);

        jOutputPanel.setLayout(null);

        jOutputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Output File"));
        jOutputPanel.add(jSiteTextField);
        jSiteTextField.setBounds(80, 80, 310, 20);

        jSiteLabel.setText("Site Name:");
        jOutputPanel.add(jSiteLabel);
        jSiteLabel.setBounds(20, 80, 60, 20);

        jLabel2.setText("Output File Name:");
        jOutputPanel.add(jLabel2);
        jLabel2.setBounds(20, 20, 90, 14);

        jOutputPanel.add(jOutputFilePathTextField);
        jOutputFilePathTextField.setBounds(110, 20, 250, 20);

        jSelectOutputButton.setText("Select File");
        jSelectOutputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSelectOutputButtonActionPerformed(evt);
            }
        });

        jOutputPanel.add(jSelectOutputButton);
        jSelectOutputButton.setBounds(370, 20, 90, 23);

        jLabel4.setText("Output File Format:");
        jOutputPanel.add(jLabel4);
        jLabel4.setBounds(20, 50, 100, 20);

        jOutputComboBox.setModel(new javax.swing.DefaultComboBoxModel(
        		new String[] { 
        			"Two columns - patient number and encrypted MRN", 
        			"Three columns - patient number, site name and encrypted MRN", 
        			"Three columns with Hive number row - encrypted MRN", 
        			"Two columns - patient number and unencrypted MRN", 
        			"Three columns - patient number, site name and unencrypted MRN", 
        			"Three columns with Hive numer row - unencrypted MRN"}));
        jOutputPanel.add(jOutputComboBox);
        jOutputComboBox.setBounds(120, 50, 340, 22);

        getContentPane().add(jOutputPanel);
        jOutputPanel.setBounds(10, 120, 470, 120);

        jButtonPanel.setLayout(null);

        jConvertButton.setText("Convert");
        jConvertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jConvertButtonActionPerformed(evt);
            }
        });

        jButtonPanel.add(jConvertButton);
        jConvertButton.setBounds(110, 0, 73, 23);

        jCloseButton.setText("Close");
        jCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCloseButtonActionPerformed(evt);
            }
        });

        jButtonPanel.add(jCloseButton);
        jCloseButton.setBounds(260, 0, 61, 23);

        getContentPane().add(jButtonPanel);
        jButtonPanel.setBounds(10, 260, 430, 30);

        pack();
    }

    private void jCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	setVisible(false);
    	dispose();
    }

    private void jConvertButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	int inputIndex = jInputComboBox.getSelectedIndex()+1;
		int outputIndex = jOutputComboBox.getSelectedIndex()+1;
		
		if((inputIndex == 2 && outputIndex != 2) ||
				(inputIndex == 4 && outputIndex != 5)) {
			Display.getDefault().syncExec(new Runnable() {
	    		public void run() {						
					MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK);
				        
				    messageBox.setText("Not valid selections");
				    messageBox.setMessage("Input and output pair selected is not valid.");
				    messageBox.open();
					
	    		}
	    	});
			return;
		}
    	//jConvertButton.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	String inputFile = this.jInputFilePathTextField.getText();
    	if(inputFile == null || inputFile.equalsIgnoreCase("")) {
    		JOptionPane.showMessageDialog(this, "Please select a input file.");
    		return;
    	}
    	
    	final String outputFile = this.jOutputFilePathTextField.getText();
    	if(outputFile == null || outputFile.equalsIgnoreCase("")) {
    		JOptionPane.showMessageDialog(this, "Please select an output file.");
    		return;
    	}
    	
    	final File oDelete = new File(outputFile);
    	
    	Display.getDefault().syncExec(new Runnable() {
    		public void run() {
				if (oDelete != null && oDelete.exists()) {
					MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
			        
			        messageBox.setText("Warning");
			        messageBox.setMessage(outputFile+" already exists,\nDo you want to replace it?");
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
    		}
    	});
		log.info("Selected output file: "+outputFile);
	    
    	//if (fileName != null && fileName.trim().length() > 0) {
			log.info("Selected input file: "+inputFile);
			
			PatientIDConversionFactory converter = new PatientIDConversionFactory();
			if((inputIndex == 1 && (outputIndex == 2 || outputIndex == 3))
					||(inputIndex == 3 && (outputIndex == 5 || outputIndex == 6))) {
				converter.sitename(jSiteTextField.getText());
			}
			FileReader fr;
			BufferedReader inbr = null;
			RandomAccessFile f = null;
			//append(f, resultFile.toString());
			jConvertButton.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			try {
				f = new RandomAccessFile(outputFile, "rw");
				fr = new FileReader(new File(inputFile));
				inbr = new BufferedReader(fr);
				//String line = inbr.readLine();
				/*if(!line.startsWith("@@i2b2 patient mapping file@@")) {
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							JOptionPane.showMessageDialog(jLabel1, "The file is not in a valid format.", "Error importing", JOptionPane.ERROR_MESSAGE);
						}
					});
					//JOptionPane.showMessageDialog(null, "The file is not a valid.", "Error importing", JOptionPane.ERROR_MESSAGE);
					return;
				}*/
				String line = inbr.readLine();
				//log.info("column name: "+line);			
					
				int rowCount = 0;
				
				while(line!=null) {
					log.info(line);
					String outputline = "";
					/*String[] cols = line.split(",");
					String id;
					if(cols.length < 2) {
						id = "";
					}
					else {
						id = converter.convert(cols[1], 1, 1);					
					}*/
					outputline = converter.convertLine(line, inputIndex, outputIndex);
					append(f, outputline);//cols[0]+","+id+"\n");
					rowCount++;
					line = inbr.readLine();
				}
				
				log.info("From "+inputIndex+" to "+outputIndex+" total lines: "+rowCount);
				inbr.close();
				f.close();
			} catch(Exception e) {
				e.printStackTrace();
				if(inbr!=null) {
					try {
						inbr.close();
					}catch(Exception e1) {}
				}
				
				if(f!=null) {
					try {
						f.close();
					}catch(Exception e1) {}
				}
			}
			
			jConvertButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    private void jInputComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
    	int inputIndex = jInputComboBox.getSelectedIndex();
		int outputIndex = jOutputComboBox.getSelectedIndex()+1;
		
		if(inputIndex == 4) {
			Display.getDefault().syncExec(new Runnable() {
	    		public void run() {						
					/*MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK);
				        
				    messageBox.setText("Not valid selections");
				    messageBox.setMessage("Input and output pair selected is not valid.");
				    messageBox.open();*/
	    			jSiteTextField.setEditable(false);
	    			jOutputComboBox.setModel(new javax.swing.DefaultComboBoxModel(
	    	        		new String[] { 
	    	        			"Ten Columns (i2b2 1.6 Patient Mapping) Encrypted MRN"}));
	    		}
	    	});
		}
		else if(inputIndex == 5) {
			Display.getDefault().syncExec(new Runnable() {
	    		public void run() {						
					/*MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK);
				        
				    messageBox.setText("Not valid selections");
				    messageBox.setMessage("Input and output pair selected is not valid.");
				    messageBox.open();*/
	    			jSiteTextField.setEditable(false);
	    			jOutputComboBox.setModel(new javax.swing.DefaultComboBoxModel(
	    	        		new String[] { 
	    	        			"Eleven Columns (i2b2 1.7 Patient Mapping) Encrypted MRN"}));
	    		}
	    	});
		}
		else {
			Display.getDefault().syncExec(new Runnable() {
	    		public void run() {						
					/*MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK);
				        
				    messageBox.setText("Not valid selections");
				    messageBox.setMessage("Input and output pair selected is not valid.");
				    messageBox.open();*/
	    			jSiteTextField.setEditable(true);
	    			jOutputComboBox.setModel(new javax.swing.DefaultComboBoxModel(
	    	        		new String[] { 
	    	        			"Two columns - patient number and encrypted MRN", 
	    	        			"Three columns - patient number, site name and encrypted MRN", 
	    	        			"Three columns with Hive number row - encrypted MRN", 
	    	        			"Two columns - patient number and unencrypted MRN", 
	    	        			"Three columns - patient number, site name and unencrypted MRN", 
	    	        			"Three columns with Hive numer row - unencrypted MRN"}));
	    		}
	    	});
		}
		
    }

    private void jSelectOutputButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Shell shell = new Shell(Display.getDefault());
				FileDialog dialog = new FileDialog(shell);//, SWT.MULTI);
				dialog.setText("select output file");
			    dialog.setFilterNames(new String[] { "CSV Files", "Text Files", "All Files (*.*)" });
			    dialog.setFilterExtensions(new String[] { "*.csv", "*.txt", "*.*" }); // Windows
			                                    // wild
			                                    // cards
			    dialog.setFilterPath("c:\\"); // Windows path
			    dialog.setFileName("mrn.txt");
			    String fileName = dialog.open();
			    
					if (fileName != null && fileName.trim().length() > 0) {
						log.info("Selected output file: "+fileName);
						jOutputFilePathTextField.setText(fileName);
					}
				//}		
			}
		});
    }
    
    private String openFileDialog() {
		FileDialog dialog = new FileDialog(new Shell(), SWT.OPEN);
		//String filterPath = "c://";
		//String platform = SWT.getPlatform();
		dialog.setText("select input file");
		dialog.setFilterNames(new String[] { "CSV Files", "Text Files", "All Files (*.*)" });
	    dialog.setFilterExtensions(new String[] { "*.csv", "*.txt",  "*.*" }); // Windows
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

    private void jSelectInputButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	Display.getDefault().syncExec(new Runnable() {
			public void run() {
				String fileName = openFileDialog();
				//if (fileName != null) {
					//String fileName = dialog.getFileName();
					if (fileName != null && fileName.trim().length() > 0) {
						log.info("Selected input file: "+fileName);
						jInputFilePathTextField.setText(fileName);
					}
				//}		
			}
		});
    }
    
    @SuppressWarnings("unused")
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
    
    /*private String getKey() {
		String path = null;
		String key = UserInfoBean.getInstance().getKey();
		if (key == null) {
			if ((path = getNoteKeyDrive()) == null) {
				Object[] possibleValues = { "Type in the key",
						"Browse to find the file containing the key" };
				String selectedValue = (String) JOptionPane
						.showInputDialog(
								this,
								"You have selected an item associated with a report\n"
										+ "which contains protected health information.\n"
										+ "You need a decryption key to perform this operation.\n"
										+ "How would you like to enter the key?\n"
										+ "(If the key is on a floppy disk, insert the disk then\n select "
										+ "\"Browse to find the file containing the key\")",
								"Notes Viewer", JOptionPane.QUESTION_MESSAGE,
								null, possibleValues, possibleValues[0]);
				if (selectedValue == null) {
					return "Not a valid key";
				}
				if (selectedValue.equalsIgnoreCase("Type in the key")) {
					key = JOptionPane.showInputDialog(this,
							"Please input the decryption key");
					if (key == null) {
						return "Not a valid key";
					}
				} else {
					JFileChooser chooser = new JFileChooser();
					int returnVal = chooser.showOpenDialog(this);
					if (returnVal == JFileChooser.CANCEL_OPTION) {
						return "Not a valid key";
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
	}*/
    
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PatientIDConversionJFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration 
    private javax.swing.JPanel jButtonPanel;
    private javax.swing.JButton jCloseButton;
    private javax.swing.JButton jConvertButton;
    private javax.swing.JPanel jInputPanel;
    private javax.swing.JComboBox jInputComboBox;
    private javax.swing.JTextField jInputFilePathTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox jOutputComboBox;
    private javax.swing.JTextField jOutputFilePathTextField;
    private javax.swing.JButton jSelectInputButton;
    private javax.swing.JButton jSelectOutputButton;
    private javax.swing.JLabel jSiteLabel;
    private javax.swing.JTextField jSiteTextField;
    private javax.swing.JPanel jOutputPanel;
    // End of variables declaration
    
}
