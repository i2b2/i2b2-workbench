/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */
package edu.harvard.i2b2.explorer.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TemplatesDialog extends Dialog {

	private String[] input;

	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 */
	public TemplatesDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public TemplatesDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Choose from Templates");
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return input[0];
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {

		GridLayout shellLayout = new GridLayout(2, true);
		shell.setLayout(shellLayout);

		// Show the message
		Label label = new Label(shell, SWT.NONE);
		label.setText("Choose a template:");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		final org.eclipse.swt.widgets.List addList = new org.eclipse.swt.widgets.List(
				shell, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.horizontalSpan = 2;
		data.minimumHeight = 150;
		addList.setLayoutData(data);

		// addList.add("Template 1 - \"FVC Observed\"");
		addList.add("Template 1 - \"All i2b2 NLP with notes\"");
		addList.add("Template 2 - \"All %FVC, %FEV1, and BD data\"");
		addList.add("Template 3 - \"All i2b2 derived data\"");
		addList.add("Template 4 - \"Compare RPDR and i2b2 asthma diagnosis\"");
		addList.add("Template 5 - \"Compare RPDR and i2b2 smoking\"");
		addList.add("Template 6 - \"Compare RPDR and i2b2 medications\"");
		addList.add("Template 7 - \"Careful look at i2b2 smoking\"");

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// input = text.getText();
				input = addList.getSelection();
				shell.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.minimumWidth = 40;
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				input = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}
}
