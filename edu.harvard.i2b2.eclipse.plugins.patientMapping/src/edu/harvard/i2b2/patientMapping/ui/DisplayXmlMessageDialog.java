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
 * QueryDisplayXmlMessageDialog.java
 *
 * Created on February 21, 2007, 4:22 PM
 */

package edu.harvard.i2b2.patientMapping.ui;

import java.io.StringWriter;
import java.util.List;

import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author wp066
 */
public class DisplayXmlMessageDialog extends javax.swing.JFrame {

	/** Creates new form QueryDisplayXmlMessageDialog */
	public DisplayXmlMessageDialog(String msg) {

		initComponents();
		setSize(800, 400);
		
		try {
			SAXBuilder parser = new SAXBuilder();
			String xmlContent = msg;
			java.io.StringReader xmlStringReader = new java.io.StringReader(
					xmlContent);
			final org.jdom.Document tableDoc = parser.build(xmlStringReader);
			XMLOutputter o = new XMLOutputter();
			o.setFormat(Format.getPrettyFormat());
			StringWriter str = new StringWriter();
			o.output(tableDoc, str);
			jMessageTextArea.setText(str.toString());
		}
		catch(Exception e) {
			
		}
	}

	/** Creates new form QueryDisplayXmlMessageDialog */
	public DisplayXmlMessageDialog(List<String> msgs) {

		initComponents();
		setSize(800, 400);
		int it = msgs.size();
		String allMessages = "";
		while (it > 0) {
			allMessages = allMessages + msgs.get(it - 1) + "\n\n";
			it = it - 1;
		}
		jMessageTextArea.setText(allMessages);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		jScrollPane = new javax.swing.JScrollPane();
		jMessageTextArea = new javax.swing.JTextArea();
		jMenuBar = new javax.swing.JMenuBar();
		jFileMenu = new javax.swing.JMenu();
		jCloseMenuItem = new javax.swing.JMenuItem();

		getContentPane().setLayout(
				new javax.swing.BoxLayout(getContentPane(),
						javax.swing.BoxLayout.X_AXIS));

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		java.awt.Image img = this.getToolkit().getImage(
				DisplayXmlMessageDialog.class.getResource("core-cell.gif"));
		this.setIconImage(img);

		jMessageTextArea.setColumns(20);
		jMessageTextArea.setEditable(false);
		jMessageTextArea.setRows(5);
		jScrollPane.setViewportView(jMessageTextArea);

		getContentPane().add(jScrollPane);

		jFileMenu.setText("File");
		jCloseMenuItem.setText("Close");
		jCloseMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCloseMenuItemActionPerformed(evt);
			}
		});

		jFileMenu.add(jCloseMenuItem);

		jMenuBar.add(jFileMenu);

		setJMenuBar(jMenuBar);

		pack();
	}

	private void jCloseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
		dispose();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			String empty = null;

			public void run() {
				new DisplayXmlMessageDialog(empty).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.JMenuItem jCloseMenuItem;
	private javax.swing.JMenu jFileMenu;
	private javax.swing.JMenuBar jMenuBar;
	private javax.swing.JTextArea jMessageTextArea;
	private javax.swing.JScrollPane jScrollPane;
	// End of variables declaration

}
