/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 * 		Wensong Pan
 *     
 */
/*
 * TextViewerFrame.java
 *
 * Created on May 31, 2006, 12:41 PM
 */
package edu.harvard.i2b2.timeline.lifelines;

/**
 *
 * @author  wp066
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.text.*;
import javax.swing.JFileChooser;
import java.io.*;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

public class TextViewerFrame extends javax.swing.JFrame {
	private String note_ = null;
	private String pdoData_ = null;
	public static final String OS = System.getProperty("os.name").toLowerCase();

	/** Creates new form TextViewerFrame */
	public TextViewerFrame(String note, String pdoData) {
		note_ = new String(note);
		pdoData_ = new String(pdoData);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}

		initComponents();
		jTextArea2.setText(note.replaceAll("/n", "\n"));
		// jTextArea2.setBackground(new Color(240,240,240));//Color.lightGray);
		// Font currentFont = getFont();
		Font thisFont = null;// new Font("Times New Roman", Font.PLAIN, 14);
		if (OS.startsWith("mac"))
			thisFont = new Font("Monaco", Font.PLAIN, 14);
		else
			thisFont = new Font("Courier New", Font.PLAIN, 14);

		jTextArea2.setFont(thisFont);

		// jTextArea2.setSelectionColor(Color.YELLOW);
		jTextArea2.select(0, 40);
		// highlight(jTextArea2, "smoke");
		// highlight(jTextArea2, 0, 40);
		jTextArea2.setCaretPosition(0);
		setBounds(100, 100, 600, 500);

		class myTransferHandler extends TransferHandler {
			protected myTransferHandler() {
				// Sets up new TransferHandler to initialise
				// the mechanics
				super("text");
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				// Creates a new Transferable object
				// with the correct DataFlavors etc.
				return new StringSelection(pdoData_);
			}
		}

		jTextArea2.setTransferHandler(new myTransferHandler());

		// Mouse click used as a Drag gesture recogniser
		MouseListener ml = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				jTextArea2.setSelectionStart(0);
				jTextArea2.setSelectionEnd(jTextArea2.getText().length() - 1);

				JComponent c = (JComponent) e.getSource();
				TransferHandler th = c.getTransferHandler();
				th.exportAsDrag(c, e, TransferHandler.COPY);
			}
		};

		MouseMotionListener mml = new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// jTextArea2.setSelectionStart(0);
				// jTextArea2.setSelectionEnd(jTextArea2.getText().length()-1);

				JComponent c = (JComponent) e.getSource();
				TransferHandler th = c.getTransferHandler();
				th.exportAsDrag(c, e, TransferHandler.COPY);
			}
		};
		jTextArea2.addMouseMotionListener(mml);

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();
		jScrollPane2 = new javax.swing.JScrollPane();
		jTextArea2 = new javax.swing.JTextArea();
		jMenuBar = new javax.swing.JMenuBar();
		jFileMenu = new javax.swing.JMenu();
		jEditMenu = new javax.swing.JMenu();
		jSaveMenuItem = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		jExitMenuItem = new javax.swing.JMenuItem();
		jSearchMenuItem = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Notes Viewer");
		jPanel1.setLayout(new java.awt.BorderLayout());

		jTextArea2.setColumns(20);
		jTextArea2.setRows(5);
		jTextArea2.setLineWrap(true);
		jScrollPane2.setViewportView(jTextArea2);

		jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

		getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

		jFileMenu.setText("File");
		jFileMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jFileMenuActionPerformed(evt);
			}
		});

		jSaveMenuItem.setText("Save ...");
		jSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jSaveMenuItemActionPerformed(evt);
			}
		});

		jFileMenu.add(jSaveMenuItem);

		jFileMenu.add(jSeparator1);

		jExitMenuItem.setText("Exit");
		jExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jExitMenuItemActionPerformed(evt);
			}
		});

		jFileMenu.add(jExitMenuItem);

		jMenuBar.add(jFileMenu);

		jEditMenu.setText("Edit");

		jSearchMenuItem.setText("Search ...");
		jSearchMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jSearchMenuItemActionPerformed(evt);
			}
		});

		jEditMenu.add(jSearchMenuItem);

		jMenuBar.add(jEditMenu);

		setJMenuBar(jMenuBar);

		pack();
	}

	private void jExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		setVisible(false);
	}

	private void jSearchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		String find = JOptionPane.showInputDialog(this,
				"Type in the string for searching: ");
		if (find != null && !find.equals("")) {
			int newCaretPosition = highlight(jTextArea2, find);
			if (newCaretPosition != -1)
				jTextArea2.setCaretPosition(newCaretPosition);
		}
	}

	private void jSaveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		PrintStream out = null;

		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			System.out.println("Save to this file: " + f.getAbsolutePath());

			try {
				out = new PrintStream(f);
				out.print(note_);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void jFileMenuActionPerformed(java.awt.event.ActionEvent evt) {

	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TextViewerFrame(" ", "pdo xml string").setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.JMenuItem jExitMenuItem;
	private javax.swing.JMenu jFileMenu;
	private javax.swing.JMenu jEditMenu;
	private javax.swing.JMenuBar jMenuBar;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JMenuItem jSaveMenuItem;
	private javax.swing.JMenuItem jSearchMenuItem;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JTextArea jTextArea2;

	// End of variables declaration

	/**
	 * Creates highlights around all occurrences of pattern in textComp
	 */
	public int highlight(JTextComponent textComp, String pattern) {
		// First remove all old highlights
		removeHighlights(textComp);
		int firstPosition = -1;
		try {
			Highlighter hilite = textComp.getHighlighter();
			Document doc = textComp.getDocument();
			String text = doc.getText(0, doc.getLength());
			int pos = 0;

			// Search for pattern
			while ((pos = text.toLowerCase()
					.indexOf(pattern.toLowerCase(), pos)) >= 0) {
				// Create highlighter using private painter and apply around
				// pattern
				if (firstPosition == -1)
					firstPosition = pos;
				hilite.addHighlight(pos, pos + pattern.length(),
						myHighlightPainter);
				pos += pattern.length();
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return firstPosition;
	}

	public void highlight(JTextComponent textComp, int start, int end) {
		// First remove all old highlights
		removeHighlights(textComp);

		try {
			Highlighter hilite = textComp.getHighlighter();
			// Document doc = textComp.getDocument();
			// String text = doc.getText(0, doc.getLength());
			// int pos = 0;

			// Search for pattern
			// while ((pos = text.indexOf(pattern, pos)) >= 0) {
			// Create highlighter using private painter and apply around pattern
			hilite.addHighlight(start, end, myHighlightPainter);
			// pos += pattern.length();
			// }
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes only private highlights
	 */
	public void removeHighlights(JTextComponent textComp) {
		Highlighter hilite = textComp.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();

		for (int i = 0; i < hilites.length; i++) {
			if (hilites[i].getPainter() instanceof MyHighlightPainter) {
				hilite.removeHighlight(hilites[i]);
			}
		}
	}

	// An instance of the private subclass of the default highlight painter
	Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(
			Color.red);

	/**
	 * A private subclass of the default highlight painter
	 */
	class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
		public MyHighlightPainter(Color color) {
			super(color);
		}
	}
}
