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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * this class displays a dialog with the contents of i2b2 log4j file file
 * currently located in base class path i2b2.log
 * 
 * @author wwg0
 * 
 */
public class LoggerDialog extends Dialog {

    private static final Log log = LogFactory.getLog(LoggerDialog.class);
    // path to log file (currently in base class path)
    private String filePath = "i2b2.log";

    public String getFilePath() {
	return filePath;
    }

    public void setFilePath(String filePath) {
	this.filePath = filePath;
    }

    private String LOGGER_ERROR_MSG = "Log file empty or not available- "
	    + filePath;

    /**
     * constructor
     * 
     * @param parent
     */
    public LoggerDialog(Shell parent) {
	super(parent, SWT.DIALOG_TRIM | SWT.MODELESS);
	setText("I2B2 Error Log");
    }

    public void open() {
	// create dialog window
	Shell shell = new Shell(getParent(), getStyle());
	shell.setText("I2B2 Error Log");
	createContents(shell);
	shell.setSize(800, 600);
	shell.open();
	Display display = getParent().getDisplay();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }

	}

    }

    private void createContents(final Shell shell) {
	shell.setLayout(new FillLayout());
	final Text text = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
		| SWT.V_SCROLL);

	// String filePath = "i2b2.log";
	File testFile = new File(filePath);

	String contents = getContents(testFile);

	if (contents.equals("")) {
	    text.setText(LOGGER_ERROR_MSG);
	} else {
	    text.setText(contents);
	}
	// System.out.println("contents=" + contents);

    }

    /**
     * reads the i2b2.log file in the classpath directory
     * 
     * @param aFile
     *            file
     * @return
     */
    public static String getContents(File file) {

	// file contents
	StringBuffer contents = new StringBuffer();
	// declared here to make visible to finally clause
	BufferedReader input = null;
	try {
	    // this implementation reads one line at a time
	    // FileReader always assumes default encoding
	    input = new BufferedReader(new FileReader(file));
	    String line = null;
	    while ((line = input.readLine()) != null) {
		contents.append(line);
		contents.append(System.getProperty("line.separator"));
	    }
	} catch (FileNotFoundException ex) {
	    // ex.printStackTrace();
	    // System.out.println(ex.getMessage());
	    log.error(ex.getMessage());

	} catch (IOException ex) {
	    // ex.printStackTrace();
	    // System.out.println(ex.getMessage());
	    log.error(ex.getMessage());

	} finally {
	    try {
		if (input != null) {
		    // flush and close input streams and readers
		    input.close();
		}
	    } catch (IOException ex) {
		// ex.printStackTrace();
		// System.out.println(ex.getMessage());
		log.error(ex.getMessage());
	    }
	}
	return contents.toString();

    }

}
