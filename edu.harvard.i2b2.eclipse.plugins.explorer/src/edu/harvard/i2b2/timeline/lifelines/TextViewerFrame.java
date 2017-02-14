/*
 * Copyright (c) 2006-2017 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 * 		Wensong Pan
 * 		Heekyong Park (hpark25)
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import edu.harvard.i2b2.explorer.ui.TimeLinePanel;


public class TextViewerFrame extends javax.swing.JFrame {
	private String note_ = null;
	private String pdoData_ = null;
	private int markStar; 
	private GenRecord thisRecord=null; 
	private TimeLinePanel tmLnDisplay;
	private SearchTextFrame searchBox;
	ImageIcon iconBlank, iconStarred;
	
	
	
	
	
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
		Font thisFont = null;
		if (OS.startsWith("mac"))
			thisFont = new Font("Monaco", Font.PLAIN, 14);
		else
			thisFont = new Font("Courier New", Font.PLAIN, 14);

		jTextArea2.setFont(thisFont);

		jTextArea2.select(0, 40);
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
				JComponent c = (JComponent) e.getSource();
				TransferHandler th = c.getTransferHandler();
				th.exportAsDrag(c, e, TransferHandler.COPY);
			}
		};
		jTextArea2.addMouseMotionListener(mml);

	}
	
	
	public TextViewerFrame(String note, String pdoData, TimeLinePanel displayPanel, GenRecord selectedRecord) { //, int x, int y) {
				thisRecord=selectedRecord;
				tmLnDisplay=displayPanel;
				
		
				if(thisRecord.mark_status.equalsIgnoreCase("S"))
						markStar=1;
				else
					markStar=-1;
		
				note_ = new String(note);
				pdoData_ = new String(pdoData);

				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					System.out.println("Error setting native LAF: " + e);
				}

				initComponents();
				jTextArea2.setText(note.replaceAll("/n", "\n"));
				Font thisFont = null;
				if (OS.startsWith("mac"))
					thisFont = new Font("Monaco", Font.PLAIN, 14);
				else
					thisFont = new Font("Courier New", Font.PLAIN, 14);

				jTextArea2.setFont(thisFont);

				jTextArea2.select(0, 40);
				jTextArea2.setCaretPosition(0);
				
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				setBounds(100, 15, 700, (int)screenSize.getHeight()-70);				

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
		jStarMenu = new javax.swing.JMenu(); 
		jSaveMenuItem = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		jExitMenuItem = new javax.swing.JMenuItem();
		jSearchMenuItem = new javax.swing.JMenuItem();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				disposeTextViewerFrame();
			}
		}); 
		
		setTitle("Notes Viewer");
		jPanel1.setLayout(new java.awt.BorderLayout());

		jTextArea2.setColumns(20);
		jTextArea2.setRows(5);
		jTextArea2.setLineWrap(true);
		jScrollPane2.setViewportView(jTextArea2);

		jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

		getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
		
		java.awt.Image imgBlnkStar = this.getToolkit().getImage(
				TextViewerFrame.class.getResource("/icons/outlinedStar.gif"));
		iconBlank = new ImageIcon(imgBlnkStar);	
		java.awt.Image imgStarred = this.getToolkit().getImage(
				TextViewerFrame.class.getResource("/icons/yellowOutlinedStar.gif"));
		iconStarred = new ImageIcon(imgStarred);	

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
		jSearchMenuItem.setAccelerator(KeyStroke.getKeyStroke("control F"));
		jSearchMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jSearchMenuItemActionPerformed(evt);
			}
		});
		
		
		jEditMenu.add(jSearchMenuItem);
		jMenuBar.add(jEditMenu);
		

		
		if(markStar==1)
			jStarMenu.setIcon(iconStarred);
		else
			jStarMenu.setIcon(iconBlank);		
		jStarMenu.addMouseListener(new MouseListener() {

		        public void mouseReleased(MouseEvent e) {}
		        
		        public void mousePressed(MouseEvent e) {}

		        public void mouseExited(MouseEvent e) {}
		        
		        public void mouseEntered(MouseEvent e) {}
		        
		        public void mouseClicked(MouseEvent e) {
	            	jStarMenuActionPerformed();
		        }
		    });			
		jMenuBar.add(jStarMenu);
		
		setJMenuBar(jMenuBar);
		pack();
	}

	private void jExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		disposeTextViewerFrame();				
	}
	
	public void disposeTextViewerFrame() {
		if(searchBox!=null)
			searchBox.setVisible(false);
		setVisible(false);
	}

	private void jSearchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {		
		searchBox = new SearchTextFrame(this, jTextArea2);
		searchBox.setVisible(true);
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
	
	private void jStarMenuActionPerformed() {
		
		markStar=markStar*-1;
		if(markStar==1)
			thisRecord.mark_status="S";
		else
			thisRecord.mark_status="R";
		tmLnDisplay.repaint();
		update_jStarMenuItem();
		
		
	}
	
	private void update_jStarMenuItem()
	{
		if(markStar==1)
			jStarMenu.setIcon(iconStarred);
		else
			jStarMenu.setIcon(iconBlank);
		jStarMenu.updateUI();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
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
	private javax.swing.JMenu jStarMenu;
	// End of variables declaration

	/**
	 * Creates highlights around all occurrences of pattern in textComp
	 */
	public int highlight(JTextComponent textComp, String pattern, int searchOpt) {
		// First remove all old highlights
		removeHighlights(textComp);
		int firstPosition = -1;
		try {
			Highlighter hilite = textComp.getHighlighter();
			Document doc = textComp.getDocument();
			String text = doc.getText(0, doc.getLength());
			int pos = 0;
			switch(searchOpt){
				case 0: //default: 0		
					while ((pos = text.toLowerCase()
					.indexOf(pattern.toLowerCase(), pos)) >= 0) {
						hilite.addHighlight(pos, pos + pattern.length(),
								myHighlightPainter);
						pos += pattern.length();
					}
					break;
				case 1: // case sensitive: 1
					while ((pos = text.indexOf(pattern, pos)) >= 0) {
						hilite.addHighlight(pos, pos + pattern.length(),
								myHighlightPainter);
						pos += pattern.length();
					}
					break;
				case 2: // case sensitive + whole word: 2
				{
					pattern="\\b"+pattern+"\\b";
					Matcher matcher =Pattern.compile(pattern).matcher(text);
					while (matcher.find()) {
						hilite.addHighlight(matcher.start(), matcher.end(),
								myHighlightPainter);
						pos += pattern.length();
					}
					break;
				}
				case 3: // case insensitive + whole word: 3
				{
					pattern="\\b"+pattern+"\\b";
					Matcher matcher =Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text);
					while (matcher.find()) {
						hilite.addHighlight(matcher.start(), matcher.end(),
								myHighlightPainter);
						pos += pattern.length();
					}
					break;
				}
				case 4: // regular expression: 4
				{
					try{
						System.out.println("case sensitive + regular expression: 5");
						
						Matcher matcher =Pattern.compile(pattern).matcher(text);
						while (matcher.find()) {
							hilite.addHighlight(matcher.start(), matcher.end(),
									myHighlightPainter);
							pos += pattern.length();
						}
					} catch(Exception e) {System.out.println("Enter right expression!");}
					break;
				}
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return firstPosition;
	}


	public void highlight(JTextComponent textComp, int start, int end) {
		try {
			Highlighter hilite = textComp.getHighlighter();
			hilite.addHighlight(start, end, curntHighlightPainter);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public int highlight(JTextComponent textComp, String pattern, int searchOpt, int pos) {
		int firstPosition = -1;
		// First remove all old highlights
		removeHighlights(textComp);
		
		try {
			Highlighter hilite = textComp.getHighlighter();
			Document doc = textComp.getDocument();
			String text = doc.getText(0, doc.getLength());

			// Search for pattern
			if(pos<text.length() && ((pos = text.toLowerCase()
					.indexOf(pattern.toLowerCase(), pos)) >= 0))
			{
				// Remove previous highlight
				hilite.addHighlight(pos, pos + pattern.length(),
						curntHighlightPainter);
				pos += pattern.length();
			}
			else
				pos=-1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return pos;
	}
	
	public int highlightPrev(JTextComponent textComp, int cnt) {
		int[] startPos;
		int keywordLength=0, nFound=0;
		
		try {
			Highlighter hilite = textComp.getHighlighter();
			Highlighter.Highlight[] hilites = hilite.getHighlights();
			
			keywordLength = hilites[0].getEndOffset() - hilites[0].getStartOffset();
			nFound = hilites.length;
			startPos = new int[nFound];
			for (int i = 0; i < nFound; i++) {
				if (hilites[i].getPainter() instanceof MyHighlightPainter) 
					startPos[i] = hilites[i].getStartOffset();		
			}
			removeHighlights(textComp);
			if(cnt == -1 || cnt == 0)
				cnt=nFound-1;
			else
				cnt--;
			for (int i = 0; i < nFound; i++) {
				if(i != cnt)
					hilite.addHighlight(startPos[i], startPos[i]+keywordLength, myHighlightPainter);
				else
					hilite.addHighlight(startPos[i], startPos[i]+keywordLength, curntHighlightPainter);
			}
			
			Rectangle viewRect = textComp.modelToView(hilites[cnt].getStartOffset());
			textComp.scrollRectToVisible(viewRect);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return cnt;				
	}
	
	public int highlightNext(JTextComponent textComp, int cnt) {
		int[] startPos;
		int keywordLength=0, nFound=0;
		
		try {
			Highlighter hilite = textComp.getHighlighter();
			Highlighter.Highlight[] hilites = hilite.getHighlights();
			
			keywordLength = hilites[0].getEndOffset() - hilites[0].getStartOffset();
			nFound = hilites.length;
			startPos = new int[nFound];
			for (int i = 0; i < nFound; i++) {
				if (hilites[i].getPainter() instanceof MyHighlightPainter) 
					startPos[i] = hilites[i].getStartOffset();		
			}
			removeHighlights(textComp);
			if(cnt == nFound-1)
				cnt=0;
			else
				cnt++;
			for (int i = 0; i < nFound; i++) {
				if(i != cnt)
					hilite.addHighlight(startPos[i], startPos[i]+keywordLength, myHighlightPainter);
				else
					hilite.addHighlight(startPos[i], startPos[i]+keywordLength, curntHighlightPainter);
			}
			
			Rectangle viewRect = textComp.modelToView(hilites[cnt].getStartOffset());
			textComp.scrollRectToVisible(viewRect);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return cnt;		

	}
	
		public int highlight(JTextComponent textComp, String pattern, int searchOpt, int pos, int cntFound) {
			try {
				Highlighter hilite = textComp.getHighlighter();
				Highlighter.Highlight[] hilites = hilite.getHighlights();
				if (cntFound>0 && (hilites[cntFound-1].getPainter() instanceof MyHighlightPainter)) {
					int start = hilites[cntFound-1].getStartOffset();
					int end = hilites[cntFound-1].getEndOffset();
					hilite.addHighlight(start, end, myHighlightPainter);
						
				}
				
				Document doc = textComp.getDocument();
				String text = doc.getText(0, doc.getLength());
	
				// Search for pattern
				if(pos<text.length() && ((pos = text.toLowerCase()
						.indexOf(pattern.toLowerCase(), pos)) >= 0))
				{
					hilite.addHighlight(pos, pos + pattern.length(),
							curntHighlightPainter);
					pos += pattern.length();
					cntFound++;
				}
				else
					pos=-1;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return pos;
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
			Color.lightGray);
	Highlighter.HighlightPainter curntHighlightPainter = new MyHighlightPainter(
			Color.decode("0x2febbc"));

	/**
	 * A private subclass of the default highlight painter
	 */
	class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
		public MyHighlightPainter(Color color) {
			super(color);
		}
	}
}
