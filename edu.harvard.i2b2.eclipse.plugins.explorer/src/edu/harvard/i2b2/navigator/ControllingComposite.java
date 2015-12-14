/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     
 */
/** This is the control that is used in the visual interface to perform some 
 * housekeeping functions and general selections, specifically:
 *  1) presents the TITLE and if one is in a know development, test, 
 *  or production environment by painting the background dark gray, 
 *  gray, or white.
 *  2) Manage the logon process, for Linux this means presenting the logon 
 *  dialog and retrieving the correct configuration file.
 *  3) present the options forom the configuation file to the user and 
 *  allow them to pick a general application area.  The default area comes up 
 *  automatically.
 *  4) The proper connections for that application area are tested to see if
 *  they are all "up".
 *  5) There is a number of buttons (up to three) that come up for each 
 *  application area, generally pointing to a URL.
 *  6) The persons name is retrieved from the user database and presented
 *  in the control.
 */

package edu.harvard.i2b2.navigator;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*; //import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.*;

//import org.eclipse.jface.action.StatusLineManager;
//import org.eclipse.jface.window.*;

public class ControllingComposite extends Composite {
    public Composite TheParent;

    public ControllingComposite(Composite parent) {
	super(parent, SWT.NONE);
	TheParent = parent;
    }

    public void run() {

	// Login loginDialog = new Login();
	// loginDialog.setBlockOnOpen(true);
	// loginDialog.open();
	// Display.getCurrent().dispose();

	// this.setBlockOnOpen(true);
	// this.open();
	createContents(TheParent);
	// Display.getCurrent().dispose();

	/*
	 * Login loginDialog = new Login(); loginDialog.init();
	 * loginDialog.prompt();
	 * 
	 * Display display = new Display(); Shell shell = new Shell(display);
	 * shell.setLayout(new FillLayout());
	 * shell.setText("i2b2 Database Query Tool"); shell.setSize(850,450);
	 * createContents(shell); shell.open(); while (!shell.isDisposed()) { if
	 * (!display.readAndDispatch()) { display.sleep(); } }
	 * display.dispose();
	 */
    }

    // private void createContents(Shell shell) {
    protected Control createContents(Composite parent) {
	// very top level composite is created
	Composite top = new Composite(parent, SWT.NONE);
	GridLayout topGridLayout = new GridLayout(1, false);
	topGridLayout.numColumns = 1;
	topGridLayout.marginWidth = 2;
	topGridLayout.marginHeight = 2;
	top.setLayout(topGridLayout);
	// Create the header/status line
	// shell.setLayout(new GridLayout(1, false));
	// final Canvas topCanvas = new Canvas(top, SWT.NO_REDRAW_RESIZE);
	final Canvas topCanvas = new Canvas(top, SWT.NONE);
	topCanvas.setBounds(5, 5, 5, 5);
	topCanvas.setSize(5, 5);
	// Color canvasColor = new Color();
	final Display display;

	display = parent.getDisplay();
	topCanvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
	topCanvas.setLayoutData(new GridData());
	GridData gridData = new GridData();
	gridData.heightHint = 30;
	gridData.horizontalAlignment = GridData.FILL;
	gridData.grabExcessHorizontalSpace = true;
	topCanvas.setLayoutData(gridData);

	topCanvas.addPaintListener(new PaintListener() {
	    public void paintControl(PaintEvent e) {
		Rectangle canvasBounds = topCanvas.getBounds();
		canvasBounds.x = canvasBounds.x - 2;
		canvasBounds.y = canvasBounds.y - 2;
		canvasBounds.height = canvasBounds.height - 1;
		canvasBounds.width = canvasBounds.width - 1;
		e.gc.drawRectangle(canvasBounds);
		Font canvasFont = new Font(display, "Ariel", 12, SWT.BOLD);
		e.gc.setFont(canvasFont);
		e.gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		e.gc.drawString("i2b2 CRC Discoverer", 5, 5);
		canvasFont.dispose();
	    }
	});

	return parent;
    }

    public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell(display);
	shell.setLayout(new FillLayout());
	shell.setText("ControllingComposite Test");
	shell.setSize(500, 58);
	new ControllingComposite(shell).run();
	// createContents(shell);
	shell.open();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	display.dispose();

    }
}