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

/**
 * Class: PatientIDConversionFactory
 * 
 * 
 */

package edu.harvard.i2b2.adminTool.dataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.adminTool.utils.SecurityUtil;

public class PatientIDConversionFactory {

	public String newline = System.getProperty("line.separator");

	private static final Log log = LogFactory.getLog(PatientIDConversionFactory.class);
	
	private String sitename = "Unknown";
	public void sitename(String str) {
		sitename = str;
	}
	
	/**
	 * 
	 */
	public PatientIDConversionFactory() {
		log.info("a new instance of conversion factory.");
	}
	
	public String convertLine(String input, int inputIndex, int outputIndex) {
		String output = "";
		
		if(inputIndex == 1 && outputIndex == 1) {
			output = convert11(input);
		}
		else if(inputIndex == 1 && outputIndex == 2) {
			output = convert12(input);
		}
		else if(inputIndex == 1 && outputIndex == 3) {
			output = convert13(input);
		}
		else if(inputIndex == 2 && outputIndex == 2) {
			output = convert22(input);
		}
		else if(inputIndex == 3 && outputIndex == 4) {
				output = convert34(input);
		}
		else if(inputIndex == 3 && outputIndex == 5) {
			output = convert35(input);
		}
		else if(inputIndex == 3 && outputIndex == 6) {
			output = convert36(input);
		}
		else if(inputIndex == 4 && outputIndex == 5) {
			output = convert45(input);
		}
		
		return output;
	}

	private String convert11(String input) {
		String output = "";
		String[] cols = input.split(",");
		String id;
		if(cols.length < 2) {
			id = "";
		}
		else {
			id = encryptID(cols[1]);//, 1, 1);					
		}
		output = new String(cols[0]+","+id+"\n");
		
		return output; //encryptID(input);
	}
	
	private String convert12(String input) {
		String output = "";
		String[] cols = input.split(",");
		String id;
		if(cols.length < 2) {
			id = "";
		}
		else {
			id = encryptID(cols[1]);//, 1, 1);					
		}
		output = new String(cols[0]+","+sitename+","+id+"\n");
		
		return output; //encryptID(input);
	}
	
	private String convert13(String input) {
		String output = "";
		String[] cols = input.split(",");
		String id;
		if(cols.length < 2) {
			id = "";
		}
		else {
			id = encryptID(cols[1]);//, 1, 1);					
		}
		output = new String(cols[0]+",HIVE,"+cols[0]+"\n"+cols[0]+","+sitename+","+id+"\n");
		
		return output; //encryptID(input);
	}
	
	private String convert22(String input) {
		String output = "";
		String[] cols = input.split(",");
		String id;
		if(cols.length < 3) {
			id = "";
		}
		else {
			id = encryptID(cols[2]);//, 1, 1);					
		}
		output = new String(cols[0]+","+cols[1]+","+id+"\n");
		
		return output; //encryptID(input);
	}
	
	private String convert34(String input) {
		String output = "";
		String[] cols = input.split(",");
		String id;
		if(cols.length < 2) {
			id = "";
		}
		else {
			id = decryptID(cols[1]);//, 1, 1);					
		}
		output = new String(cols[0]+","+id+"\n");
		
		return output; //encryptID(input);
	}
	
	private String convert35(String input) {
		String output = "";
		String[] cols = input.split(",");
		String id;
		if(cols.length < 2) {
			id = "";
		}
		else {
			id = decryptID(cols[1]);//, 1, 1);					
		}
		output = new String(cols[0]+","+sitename+","+id+"\n");
		
		return output; //encryptID(input);
	}
	
	private String convert36(String input) {
		String output = "";
		String[] cols = input.split(",");
		String id;
		if(cols.length < 2) {
			id = "";
		}
		else {
			id = decryptID(cols[1]);//, 1, 1);					
		}
		output = new String(cols[0]+",HIVE,"+cols[0]+"\n"+cols[0]+","+sitename+","+id+"\n");
		
		return output; //encryptID(input);
	}
	
	private String convert45(String input) {
		String output = "";
		String[] cols = input.split(",");
		String id;
		if(cols.length < 3) {
			id = "";
		}
		else {
			id = decryptID(cols[2]);//, 1, 1);					
		}
		output = new String(cols[0]+","+cols[1]+","+id+"\n");
		
		return output; //encryptID(input);
	}
	
	private String decryptID(String id) {
		String key = UserInfoBean.getInstance().getKey();
		if (key == null || key.length() == 0 || id.equalsIgnoreCase("")) {
			getKey();
			//return id;
		}
		SecurityUtil util = new SecurityUtil(key);
		if (util == null) {
			JOptionPane.showMessageDialog(null, "Not a valid key");
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
		String key = UserInfoBean.getInstance().getKey();
		if (key == null || key.length() == 0 || id.equalsIgnoreCase("")) {
			key = getKey();
			//return id;
		}
		SecurityUtil util = new SecurityUtil(key);
		if (util == null) {
			JOptionPane.showMessageDialog(null, "Not a valid key");
			return id;
		}
		String enID = util.encryptNotes(id);
		// System.out.println("notes: " + deNote);

		if (enID.equalsIgnoreCase("[I2B2-Error] Invalid key") || enID.equalsIgnoreCase("")) {
			//JOptionPane.showMessageDialog(this, "Not a valid key");
			return id;
		}

		return enID;
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
			String key = UserInfoBean.getInstance().getKey();
			if (key == null) {
				if ((path = getNoteKeyDrive()) == null) {
					Object[] possibleValues = { "Type in the key",
							"Browse to find the file containing the key" };
					String selectedValue = (String) JOptionPane
							.showInputDialog(
									null,
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
						int returnVal = chooser.showOpenDialog(null);
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
		}
	}
