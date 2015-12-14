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

import org.eclipse.swt.*;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * @author wwg0 display help contents in broswer window as a child of parent
 *         shell
 * 
 * 
 */
public class Help {

    public String helpURL = "";

    // http://phsi2b2appdev/i2b2help/index.html";

    /**
     * @param parent
     *            parent shell
     */
    public Help(Shell parent) {
	Shell shell = new Shell(parent);
	shell.setLayout(new FillLayout());
	shell.setText("Help Browser");
	shell.setSize(800, 600);
	Browser browser = new Browser(shell, SWT.NONE);
	browser.setUrl(helpURL);
	shell.open();

    }

    /**
     * runs appliation
     */
    public void run() {
	Display display = new Display();
	Shell shell = new Shell(display);
	shell.setText("Help Browser");
	shell.setSize(600, 400);
	createContents(shell);
	shell.open();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	display.dispose();
    }

    public void createContents(final Shell shell) {
	shell.setLayout(new FillLayout());
	Browser browser = new Browser(shell, SWT.NONE);
	browser.setUrl(helpURL);

    }

    public static void main(String[] args) {
	// TODO Auto-generated method stub
	// new HelpD().run();

    }

}
