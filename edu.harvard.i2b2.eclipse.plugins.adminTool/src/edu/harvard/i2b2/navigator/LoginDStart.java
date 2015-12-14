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

package edu.harvard.i2b2.navigator;

import org.eclipse.swt.widgets.*;

/**
 * @author wwg0 This class is entry point for application Creates a main shell
 *         and dialog which authenticates user configuration
 */
public class LoginDStart {

    public void run() {
	Display display = new Display();
	// create main shell
	Shell mainShell = new Shell(display);
	// create child shell (Dialog)
	// inputDialog = createInputDialog(mainShell);
	// open mainshell Shell
	mainShell.open();

	while (!mainShell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	display.dispose();

    }

    // public Shell createMainShell(Display parent) {

    // create main window and fill contents with bannerC
    // Shell shell = new Shell(parent);
    // shell.setLayout(new FillLayout(SWT.HORIZONTAL));
    // shell.setText(I2B2_APPLICATION_MSG);
    // shell.setSize(900,700);
    // LoginContentFake loginContentFake = new LoginContentFake(shell);
    // return shell;
    // }

    // public LoginDNew createInputDialog(Shell parent) {
    // LoginDNew inputDialog = new LoginDNew(parent);
    // return inputDialog;
    // }

    public static void main(String[] args) {
	// TODO Auto-generated method stub
	new LoginDStart().run();

    }

}
