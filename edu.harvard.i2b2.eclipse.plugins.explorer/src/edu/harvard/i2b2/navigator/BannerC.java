/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 * 		
 * 			wwg0
 *     
 */
package edu.harvard.i2b2.navigator;

/** This is the control that is used in the visual interface to perform some 
 * housekeeping functions and general selections, specifically:
 *  1) presents the TITLE and if one is in a know development, test, 
 *  or production environment by painting the background dark gray, 
 *  gray, or white.
 *  2) Manage the logon process, for Linux this means presenting the logon 
 *  dialog and retrieving the correct configuration file.
 *  3) present the options from the configuation file to the user and 
 *  allow them to pick a general application area.  The default area comes up 
 *  automatically.
 *  4) The proper connections for that application area are tested to see if
 *  they are all "up", and a canvas light is shown to indicate the results.
 *  5) There is a number of buttons (up to three) that come up for each 
 *  application area, generally pointing to a URL.
 *  6) The persons name is retrieved from the user database and presented
 *  in the control.
 */

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*; //import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.*;

//import org.eclipse.jface.action.StatusLineManager;
//import org.eclipse.jface.window.*;

/**
 * @author wwg0 Changes added Login Button
 * 
 * 
 */

public class BannerC extends Composite {
    // Global variables for Entire Application test
    public String msTitle = "I2B2 CRC Navigator in";
    public String msUsername = "";
    public String msPassword = "";

    // usermode from bannerC menu
    private String userMode = "-Select Mode-";

    public String getUserMode() {
	return userMode;
    }

    public void setUserMode(String userMode) {
	this.userMode = userMode;
    }

    // Global variables for this class
    private static final String BANNERC_MSG = "Banner C";

    // private Composite oTheParent;

    public Label authorizationLabel;
    public Label statusLabel;
    public StatusLabelPaintListener statusLabelPaintListener;
    public ToolItem titleToolItem;

    public BannerC(Composite parent) {
	super(parent, SWT.BORDER);
	// this.setSize(800,100);
	// oTheParent = parent;
	// createContents();
    }

    // allow users to override style
    public BannerC(Composite parent, int style) {
	super(parent, style);
	// this.setSize(800,100);
	// oTheParent = parent;
    }

    public void run() {

	// this.setBlockOnOpen(true);
	// this.open();
	// *****createContents();
	// Display.getCurrent().dispose();

	/*
	 * Login loginDialog = new Login(); loginDialog.init();
	 * loginDialog.prompt();
	 * 
	 * Display display = new Display(); Shell shell = new Shell(display);
	 * shell.setLayout(new FillLayout()); shell.setText("i2b2 Database Query
	 * Tool"); shell.setSize(850,450); createContents(shell); shell.open();
	 * while (!shell.isDisposed()) { if (!display.readAndDispatch()) {
	 * display.sleep(); } } display.dispose();
	 */
    }

    // private void createContents() {
    protected void createContents(Composite parent) {
	// some important UI variables
	// final Display display;
	Display display = parent.getDisplay();
	// final Shell shell;
	// shell = oTheParent.getShell();
	final Font headerFont = new Font(display, "Tahoma", 12, SWT.BOLD);
	final Font normalFont = new Font(display, "Tahoma", 12, SWT.NORMAL);
	final Font buttonFont = new Font(display, "Tahoma", 9, SWT.NORMAL);
	final Color backColor = display.getSystemColor(SWT.COLOR_WHITE);
	final Color foreColor = display.getSystemColor(SWT.COLOR_BLACK);
	final Color warningColor = display.getSystemColor(SWT.COLOR_YELLOW);
	// final Color textColor = display.getSystemColor(SWT.COLOR_BLACK);
	// final Color goColor = display.getSystemColor(SWT.COLOR_GREEN);
	// final Color badColor = display.getSystemColor(SWT.COLOR_RED);

	// The Banner itself is configured and layout is set
	FormLayout topFormLayout = new FormLayout();
	topFormLayout.marginWidth = 2;
	topFormLayout.marginHeight = 2;
	topFormLayout.spacing = 5;
	parent.setLayout(topFormLayout);
	parent.setBackground(backColor);
	parent.setForeground(foreColor);

	// the label on the left is added
	Label titleLabel = new Label(parent, SWT.NO_FOCUS);
	titleLabel.setBackground(backColor);
	titleLabel.setText(msTitle);
	titleLabel.setFont(headerFont);
	titleLabel.setForeground(foreColor);
	parent.pack();
	int titleLabelHeight = titleLabel.getBounds().height;
	System.out.println(titleLabelHeight);
	FormData titleLabelFormData = new FormData();
	titleLabelFormData.top = new FormAttachment(50, -(titleLabelHeight / 2));
	// titleLabelFormData.bottom = new FormAttachment(100);
	titleLabelFormData.left = new FormAttachment(0, 10);
	titleLabel.setLayoutData(titleLabelFormData);

	// the general application area toolbar is added
	ToolBar titleToolBar = new ToolBar(parent, SWT.FLAT);
	titleToolBar.setBackground(backColor);
	titleToolBar.setFont(headerFont);
	FormData titleToolBarFormData = new FormData();
	titleToolBarFormData.left = new FormAttachment(titleLabel);
	titleToolBarFormData.top = new FormAttachment(titleLabel,
		-titleLabelHeight - 10);
	titleToolBar.setLayoutData(titleToolBarFormData);

	// add query mode dropdown tool item
	titleToolItem = new ToolItem(titleToolBar, SWT.DROP_DOWN);
	titleToolItem.setText("Exploration Mode");

	// create menu for dropdown, create menu items, and add listeners for
	// dropdown tool item
	// hard code replace with webservice login values
	String[] modes = { "Exploration Mode", "Query Mode", "Ontology Mode" };
	final Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
	for (int i = 0; i < 3; i++) {
	    MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
	    menuItem.setText(modes[i]);
	    menuItem.addSelectionListener(new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent event) {

		    MenuItem selected = (MenuItem) event.widget;
		    System.out.println("titleToolItem=" + selected.getText());
		    titleToolItem.setText(selected.getText());

		}

	    });

	}
	// add listener for toolbaritem
	titleToolItem.addListener(SWT.Selection, new DropDownListener(
		titleToolBar, menu));

	// right button is made
	Button rightButton = new Button(parent, SWT.PUSH | SWT.LEFT);
	rightButton.setFont(buttonFont);
	rightButton.setText(" Help ");
	// These don't work on Windows
	// rightButton.setBackground(backColor);
	// rightButton.setForeground(foreColor);
	FormData rightButtonFormData = new FormData();
	rightButtonFormData.right = new FormAttachment(100, -10);
	rightButtonFormData.top = new FormAttachment(50,
		-(titleLabelHeight / 2) - 2);
	rightButton.setLayoutData(rightButtonFormData);

	// Login button is made
	final Button loginButton = new Button(parent, SWT.PUSH | SWT.LEFT);
	loginButton.setFont(buttonFont);
	loginButton.setText(" Login ");
	// These don't work on Windows
	// rightButton.setBackground(backColor);
	// rightButton.setForeground(foreColor);
	FormData loginButtonFormData = new FormData();
	// loginButtonFormData.right = new FormAttachment(100,-10);
	loginButtonFormData.right = new FormAttachment(rightButton);
	loginButtonFormData.top = new FormAttachment(50,
		-(titleLabelHeight / 2) - 2);
	loginButton.setLayoutData(loginButtonFormData);
	// add selection listener for login Button

	// loginButton.addSelectionListener(new SelectionAdapter() {

	// @Override
	// public void widgetSelected(SelectionEvent event) {

	// call login dialog here to get
	// LoginDNew loginDNew = new LoginDNew(loginButton.getShell());
	// String input = loginDNew.open();
	// return from dialog here
	// null means user pressed cancel closes application
	// if (input == null) {
	// authorizationLabel.setText("Login Cancelled ...");
	// mainShell.close();
	// statusLabelPaintListener.setOvalColor(badColor);
	// statusLabel.redraw();
	// } else {
	// authorizationLabel.setText(input);
	// statusLabelPaintListener.setOvalColor(goColor);
	// statusLabel.redraw();

	// }
	// }

	// });

	// the staus indicator is shown
	statusLabel = new Label(parent, SWT.NO_FOCUS);
	statusLabel.setBackground(backColor);
	statusLabel.setText("Status:      ");
	statusLabel.setFont(normalFont);
	statusLabel.setForeground(foreColor);
	FormData statusLabelFormData = new FormData();
	// statusLabelFormData.right = new FormAttachment(rightButton,0);
	statusLabelFormData.right = new FormAttachment(loginButton, 0);
	statusLabelFormData.top = new FormAttachment(loginButton, 2, SWT.TOP);
	statusLabel.setLayoutData(statusLabelFormData);

	// Authorization label is made
	authorizationLabel = new Label(parent, SWT.NO_FOCUS);
	authorizationLabel.setBackground(backColor);
	authorizationLabel.setText("Awaiting Authorization...");
	authorizationLabel.setFont(normalFont);
	authorizationLabel.setForeground(foreColor);
	FormData authorizationLabelFormData = new FormData();
	authorizationLabelFormData.right = new FormAttachment(statusLabel, -10);
	// authorizationLabelFormData.top = new
	// FormAttachment(topCanvas,-titleLabelHeight-10);
	authorizationLabelFormData.top = new FormAttachment(statusLabel, 0,
		SWT.TOP);
	authorizationLabel.setLayoutData(authorizationLabelFormData);

	parent.pack();
	/*
	 * final LoginD loginDialog = new LoginD(shell);
	 * 
	 * Thread getAuthorization = new Thread () { Runnable runnable; public
	 * //loginDialog.setBlockOnOpen(true); }; }; getAuthorization.start();
	 */
	// wwg0 added nested class
	/*
	 * statusLabel.addPaintListener(new PaintListener() { public void
	 * paintControl(PaintEvent e) { // Rectangle canvasBounds =
	 * topCanvas.getBounds(); // canvasBounds.x = canvasBounds.x-2; //
	 * canvasBounds.y = canvasBounds.y-2; // canvasBounds.height =
	 * canvasBounds.height-1; // canvasBounds.width = canvasBounds.width-1;
	 * // e.gc.drawRectangle(canvasBounds); e.gc.setFont(normalFont);
	 * e.gc.setBackground(warningColor); // e.gc.drawOval(0,0,12,12);
	 * e.gc.fillOval(50, 2, 16, 16); // e.gc.drawString("Status:", 5,5); }
	 * });
	 */
	// add listener
	statusLabelPaintListener = new StatusLabelPaintListener();
	statusLabelPaintListener.setOvalColor(warningColor);
	statusLabel.addPaintListener(statusLabelPaintListener);
	return;
    }

    // inner class for statusLabel paint listener to enable it to be redrawn
    private class StatusLabelPaintListener implements PaintListener {

	private Color ovalColor;

	public Color getOvalColor() {
	    return ovalColor;
	}

	public void setOvalColor(Color ovalColor) {
	    this.ovalColor = ovalColor;
	}

	public StatusLabelPaintListener() {

	}

	public void paintControl(PaintEvent e) {

	    e.gc.setBackground(ovalColor);
	    e.gc.fillOval(50, 2, 16, 16);

	}

    }

    // inner class for dropdown toolbar item listener
    private class DropDownListener implements Listener {
	private final ToolBar bar;

	private final Menu menu;

	private DropDownListener(ToolBar bar, Menu menu) {
	    super();
	    this.bar = bar;
	    this.menu = menu;
	}

	public void handleEvent(Event event) {

	    if (event.detail == SWT.ARROW) {
		Point point = new Point(event.x, event.y);
		point = bar.getParent().getDisplay().map(bar, null, point);
		menu.setLocation(point);
		menu.setVisible(true);
	    }
	}
    }

    public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell(display);
	shell.setLayout(new FillLayout(SWT.HORIZONTAL));
	shell.setText(BANNERC_MSG);
	shell.setSize(800, 60);
	BannerC bannerC = new BannerC(shell);
	bannerC.createContents(bannerC);
	// shell.pack();
	shell.open();
	// bannerC.run();
	// bannerC.authorizationLabel.setText("Changed Authorization");
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	display.dispose();

    }
}