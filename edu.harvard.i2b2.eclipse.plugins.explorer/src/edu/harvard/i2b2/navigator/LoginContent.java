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
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class LoginContent {

    private Button myButton;

    public String getMyButtonText() {
	return myButton.getText();
    }

    public void setMyButtonText(String myText) {
	this.myButton.setText(myText);
    }

    /**
     * constructors for query fake
     * 
     * @param args
     */

    public LoginContent() {

    }

    /**
     * constructor for query fake when parent exists
     * 
     * @param parent
     *            for parent window or control
     */
    public LoginContent(Composite parent) {
	createContents(parent);

    }

    /**
     * creates the main contents for this control
     * 
     * @param parent
     */
    public void createContents(Composite parent) {
	Composite composite = new Composite(parent, SWT.NONE);
	composite.setLayout(new FillLayout());
	myButton = new Button(composite, SWT.PUSH);
	myButton.setText("");

    }

    public void run() {
	Display display = new Display();
	Shell shell = new Shell(display);
	shell.setText("Logon Contents Fake");
	shell.setLayout(new FillLayout());
	createContents(shell);
	shell.open();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	display.dispose();
    }

    public static void main(String[] args) {
	// TODO Auto-generated method stub
	new LoginContent().run();

    }

}
