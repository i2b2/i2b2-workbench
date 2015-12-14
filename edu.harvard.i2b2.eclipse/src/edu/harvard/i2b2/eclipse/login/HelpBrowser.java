/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *     Mike Mendis - initial API and implementation
 */
package edu.harvard.i2b2.eclipse.login;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.harvard.i2b2.eclipse.util.Messages;

/**
 * This class implements a web browser
 */
public class HelpBrowser {
  // The "at rest" text of the throbber
  private static final String AT_REST = Messages.getString("HelpBrowser.Ready"); //$NON-NLS-1$

  /**
   * Runs the application
   * 
   * @param location the initial location to display
   */
  public void run(String location, Shell parent) {
    //Display display = new Display();
    Shell shell = new Shell(parent);
    shell.setText(Messages.getString("HelpBrowser.Title")); //$NON-NLS-1$
    createContents(shell, location);
    shell.open();
  }

  /**
   * Creates the main window's contents
   * 
   * @param shell the main window
   * @param location the initial location
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
    browser.back();


    // Create the animated "throbber"
    Label throbber = new Label(controls, SWT.NONE);
    throbber.setText(AT_REST);

    // Add event handlers
    browser.addCloseWindowListener(new AdvancedCloseWindowListener());
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
     * @param text the address text box to update
     */
    public AdvancedLocationListener(Text text) {
      // Store the address box for updates
      location = text;
    }

    /**
     * Called before the location changes
     * 
     * @param event the event
     */
    public void changing(LocationEvent event) {
      // Show the location that's loading
      location.setText(Messages.getString("HelpBrowser.Loading") + event.location + "..."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Called after the location changes
     * 
     * @param event the event
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
     * @param label the label on which to report progress
     */
    public AdvancedProgressListener(Label label) {
      // Store the label on which to report updates
      progress = label;
    }

    /**
     * Called when progress is made
     * 
     * @param event the event
     */
    public void changed(ProgressEvent event) {
      // Avoid divide-by-zero
      if (event.total != 0) {
        // Calculate a percentage and display it
        int percent = (event.current / event.total);
        progress.setText(percent + "%"); //$NON-NLS-1$
      } else {
        // Since we can't calculate a percent, show confusion :-)
        progress.setText("    "); //$NON-NLS-1$
      }
    }

    /**
     * Called when load is complete
     * 
     * @param event the event
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
     * @param label the label on which to report status
     */
    public AdvancedStatusTextListener(Label label) {
      // Store the label on which to report status
      status = label;
    }

    /**
     * Called when the status changes
     * 
     * @param event the event
     */
    public void changed(StatusTextEvent event) {
      // Report the status
    	//WG added code to trap error
    	if (!status.isDisposed())
    		status.setText(event.text);
    }
  }

}

