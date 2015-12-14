/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * 
 * Contributors: 
 *     Wensong Pan
 *     Christopher D. Herrick 
 */
package edu.harvard.i2b2.analysis.ui;

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

public class AdditionalItemsDialog extends Dialog {
	// private String dialogText;
	private String[] input;

	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 */
	public AdditionalItemsDialog(Shell parent) {
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
	public AdditionalItemsDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Choose Additional Items");
	}

	/**
	 * Sets the input
	 * 
	 * @param input
	 *            the new input
	 */
	public void setInput(String[] input) {
		this.input = input;
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String[] open() {
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
		return input;
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
		label.setText("Choose additional items to add to query:");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		// Display the input box
		/*
		 * final Text text = new Text(shell, SWT.BORDER); data = new
		 * GridData(GridData.FILL_HORIZONTAL); data.horizontalSpan = 2;
		 * text.setLayoutData(data);
		 */

		final org.eclipse.swt.widgets.List addList = new org.eclipse.swt.widgets.List(
				shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.horizontalSpan = 2;
		data.minimumHeight = 150;
		addList.setLayoutData(data);

		addList.add("Encounter Range Line");
		addList.add("Vital Status Line");

		if ((input != null) || (input.length > 0)) {
			for (int i = 0; i < input.length; i++) {
				if ((input[i] != null)
						&& (input[i].equals("Encounter Range Line")))
					addList.remove("Encounter Range Line");
				else if ((input[i] != null)
						&& (input[i].equals("Vital Status Line")))
					addList.remove("Vital Status Line");
			}
		}

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
