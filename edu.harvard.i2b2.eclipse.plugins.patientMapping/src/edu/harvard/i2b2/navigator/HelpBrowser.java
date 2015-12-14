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

import java.io.*;
import java.net.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * This class implements a web browser
 */
public class HelpBrowser {
    // The "at rest" text of the throbber
    private static final String AT_REST = "Ready";

    /**
     * Runs the application
     * 
     * @param location
     *            the initial location to display
     */
    public void run(String location, Shell parent) {
	// Display display = new Display();
	Shell shell = new Shell(parent);
	shell.setText("i2b2 Browser");
	createContents(shell, location);
	shell.open();
	// while (!shell.isDisposed()) {
	// if (!display.readAndDispatch()) {
	// display.sleep();
	// }
	// }
	// display.dispose();
    }

    /**
     * Creates the main window's contents
     * 
     * @param shell
     *            the main window
     * @param location
     *            the initial location
     */
    public void createContents(Shell shell, String location) {
	shell.setLayout(new FormLayout());

	// Create the composite to hold the buttons and text field
	Composite controls = new Composite(shell, SWT.NONE);
	FormData data = new FormData();
	data.top = new FormAttachment(0, 0);
	data.left = new FormAttachment(0, 0);
	data.right = new FormAttachment(100, 0);
	controls.setLayoutData(data);

	// Create the status bar
	Label status = new Label(shell, SWT.NONE);
	data = new FormData();
	data.left = new FormAttachment(0, 0);
	data.right = new FormAttachment(100, 0);
	data.bottom = new FormAttachment(100, 0);
	status.setLayoutData(data);

	// Create the web browser
	final Browser browser = new Browser(shell, SWT.BORDER);
	data = new FormData();
	data.top = new FormAttachment(controls);
	data.bottom = new FormAttachment(status);
	data.left = new FormAttachment(0, 0);
	data.right = new FormAttachment(100, 0);
	browser.setLayoutData(data);

	// Create the controls and wire them to the browser
	controls.setLayout(new GridLayout(7, false));

	// Create the back button
	// Button button = new Button(controls, SWT.PUSH);
	// button.setText("Back");
	// button.addSelectionListener(new SelectionAdapter() {
	// public void widgetSelected(SelectionEvent event) {
	browser.back();
	// }
	// });

	// Create the forward button
	// button = new Button(controls, SWT.PUSH);
	// button.setText("Forward");
	// button.addSelectionListener(new SelectionAdapter() {
	// public void widgetSelected(SelectionEvent event) {
	// browser.forward();
	// }
	// });

	// Create the refresh button
	// button = new Button(controls, SWT.PUSH);
	// button.setText("Refresh");
	// button.addSelectionListener(new SelectionAdapter() {
	// public void widgetSelected(SelectionEvent event) {
	// browser.refresh();
	// }
	// });

	// Create the stop button
	// button = new Button(controls, SWT.PUSH);
	// button.setText("Stop");
	// button.addSelectionListener(new SelectionAdapter() {
	// public void widgetSelected(SelectionEvent event) {
	// browser.stop();
	// }
	// });

	// Create the address entry field and set focus to it
	// final Text url = new Text(controls, SWT.BORDER);
	// url.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	// url.setFocus();

	// Create the go button
	// button = new Button(controls, SWT.PUSH);
	// button.setText("Go");
	// button.addSelectionListener(new SelectionAdapter() {
	// public void widgetSelected(SelectionEvent event) {
	// browser.setUrl(url.getText());
	// }
	// });

	// Create the animated "throbber"
	Label throbber = new Label(controls, SWT.NONE);
	throbber.setText(AT_REST);

	// Allow users to hit enter to go to the typed URL
	// shell.setDefaultButton(button);

	// Add event handlers
	browser.addCloseWindowListener(new AdvancedCloseWindowListener());
	// browser.addLocationListener(new AdvancedLocationListener(url));
	browser.addProgressListener(new AdvancedProgressListener(throbber));
	browser.addStatusTextListener(new AdvancedStatusTextListener(status));

	// Go to the initial URL
	if (location != null) {
	    browser.setUrl(location);
	}
    }

    /**
     * This class implements a CloseWindowListener for AdvancedBrowser
     */
    class AdvancedCloseWindowListener implements CloseWindowListener {
	/**
	 * Called when the parent window should be closed
	 */
	public void close(WindowEvent event) {
	    // Close the parent window
	    ((Browser) event.widget).getShell().close();
	}
    }

    /**
     * This class implements a LocationListener for AdvancedBrowser
     */
    class AdvancedLocationListener implements LocationListener {
	// The address text box to update
	private Text location;

	/**
	 * Constructs an AdvancedLocationListener
	 * 
	 * @param text
	 *            the address text box to update
	 */
	public AdvancedLocationListener(Text text) {
	    // Store the address box for updates
	    location = text;
	}

	/**
	 * Called before the location changes
	 * 
	 * @param event
	 *            the event
	 */
	public void changing(LocationEvent event) {
	    // Show the location that's loading
	    location.setText("Loading " + event.location + "...");
	}

	/**
	 * Called after the location changes
	 * 
	 * @param event
	 *            the event
	 */
	public void changed(LocationEvent event) {
	    // Show the loaded location
	    location.setText(event.location);
	}
    }

    /**
     * This class implements a ProgressListener for AdvancedBrowser
     */
    class AdvancedProgressListener implements ProgressListener {
	// The label on which to report progress
	private Label progress;

	/**
	 * Constructs an AdvancedProgressListener
	 * 
	 * @param label
	 *            the label on which to report progress
	 */
	public AdvancedProgressListener(Label label) {
	    // Store the label on which to report updates
	    progress = label;
	}

	/**
	 * Called when progress is made
	 * 
	 * @param event
	 *            the event
	 */
	public void changed(ProgressEvent event) {
	    // Avoid divide-by-zero
	    if (event.total != 0) {
		// Calculate a percentage and display it
		int percent = (event.current / event.total);
		progress.setText(percent + "%");
	    } else {
		// Since we can't calculate a percent, show confusion :-)
		progress.setText("    ");
	    }
	}

	/**
	 * Called when load is complete
	 * 
	 * @param event
	 *            the event
	 */
	public void completed(ProgressEvent event) {
	    // Reset to the "at rest" message
	    progress.setText(AT_REST);
	}
    }

    /**
     * This class implements a StatusTextListener for AdvancedBrowser
     */
    class AdvancedStatusTextListener implements StatusTextListener {
	// The label on which to report status
	private Label status;

	/**
	 * Constructs an AdvancedStatusTextListener
	 * 
	 * @param label
	 *            the label on which to report status
	 */
	public AdvancedStatusTextListener(Label label) {
	    // Store the label on which to report status
	    status = label;
	}

	/**
	 * Called when the status changes
	 * 
	 * @param event
	 *            the event
	 */
	public void changed(StatusTextEvent event) {
	    // Report the status
	    // WG added code to trap error
	    if (!status.isDisposed())
		status.setText(event.text);
	}
    }

    /**
     * The application entry point
     * 
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
	String myurl = System.getProperty("user.dir");
	// System.out.println("user.dir="+myurl);
	// File f=new File("i2b2log.html"); //"" should also work. Using ".",
	// Create a file object
	File file = new File("i2b2log.html");
	// Convert the file object to a URL
	URL url = null;
	try {
	    // The file need not exist. It is made into an absolute path
	    // by prefixing the current working directory
	    url = file.toURL(); // file:/d:/almanac1.4/java.io/filename
	} catch (MalformedURLException e) {
	}

	// you get a trailing "." that you
	// should truncate.
	// System.out.println("file.getAbsolutePath()="+f.getAbsolutePath());
	// new HelpBrowser().run(url.toString());

	// new HelpBrowser().run(args.length == 0 ? null : args[0]);
    }
}
