/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	
 */
/*
 * example snippet: embed a JTable in SWT (no flicker)
 *
 * For a list of all SWT example snippets see
 * http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/platform-swt-home/dev.html#snippets
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.JRootPane;
import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PackSWT {

  public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    /**
     * top level composite is created
     */
    Composite timelineComposite = new Composite(shell,SWT.NONE);

    Composite composite = new Composite(shell, SWT.NO_BACKGROUND
        | SWT.EMBEDDED);

    /*
     * Set a Windows specific AWT property that prevents heavyweight
     * components from erasing their background. Note that this is a global
     * property and cannot be scoped. It might not be suitable for your
     * application.
     */
    try {
      //System.setProperty("sun.awt.noerasebackground", "true");
    } catch (NoSuchMethodError error) {
    }

    /* Create and setting up frame */
	////for mac fix
	//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
		//SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";
    Frame frame = SWT_AWT.new_Frame(composite);
    Panel panel = new Panel(new BorderLayout()) {
      @Override
	public void update(java.awt.Graphics g) {
        /* Do not erase the background */
        paint(g);
      }
    };
    frame.add(panel);
    JRootPane root = new JRootPane();
    panel.add(root);
    java.awt.Container contentPane = root.getContentPane();
    System.out.println("got to here");

    /* Creating components */
    /*int nrows = 1000, ncolumns = 10;
    Vector rows = new Vector();
    for (int i = 0; i < nrows; i++) {
      Vector row = new Vector();
      for (int j = 0; j < ncolumns; j++) {
        row.addElement("Item " + i + "-" + j);
      }
      rows.addElement(row);
    }
    Vector columns = new Vector();
    for (int i = 0; i < ncolumns; i++) {
      columns.addElement("Column " + i);
    }
    JTable table = new JTable(rows, columns);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.createDefaultColumnsFromModel();
    JScrollPane scrollPane = new JScrollPane(table);*/
    record record1 = new record();
    record1.start();
    record1.init();
    //record1.resize(400,500);
    JScrollPane scrollPane = new JScrollPane(record1);
    contentPane.setLayout(new BorderLayout());
    contentPane.add(scrollPane);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
} 