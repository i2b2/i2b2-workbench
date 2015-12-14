/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */

package edu.harvard.i2b2.patientMapping.ui;

//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.layout.FormData;
//import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
//import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormData;

public class DisplayOptionsDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Composite composite1;
	private Composite composite2;
	//private Label label1;
	private Button checkbox;
	private Button cancelButton;
	private Label label1;
	private Text incrementtext;
	private Button okButton;
	//private Text text1;

	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			DisplayOptionsDialog inst = new DisplayOptionsDialog(shell, SWT.NULL);
			inst.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DisplayOptionsDialog(Shell parent, int style) {
		super(parent, style);
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			GridLayout dialogShellLayout = new GridLayout();
			dialogShellLayout.makeColumnsEqualWidth = true;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setSize(206, 132);
			dialogShell.setText("Patient Mapping Options Dialog");
			{
				composite1 = new Composite(dialogShell, SWT.NONE);
				composite1.setLayout(null);
				GridData composite1LData = new GridData();
				composite1LData.verticalAlignment = GridData.FILL;
				composite1LData.horizontalAlignment = GridData.FILL;
				composite1LData.grabExcessHorizontalSpace = true;
				composite1LData.grabExcessVerticalSpace = true;
				composite1.setLayoutData(composite1LData);
				{
					checkbox = new Button(composite1, SWT.CHECK);
					checkbox.setText("Sending PDO requests to IM cell");
					
					if(System.getProperty("UseIMCell")!=null
							&& System.getProperty("UseIMCell").equalsIgnoreCase("true")) {
						checkbox.setSelection(true);
						checkbox.setBounds(3, 5, 198, 15);
					}
					else {
						checkbox.setSelection(false);
						checkbox.setBounds(3, 5, 198, 15);
					}
				}
				{
					label1 = new Label(composite1, SWT.NONE);
					label1.setText("Increment Size:");
					label1.setBounds(6, 32, 85, 15);
				}
				{
					incrementtext = new Text(composite1, SWT.BORDER);
					incrementtext.setText("1000");
					incrementtext.setBounds(91, 29, 88, 21);
				}
				/*{
					text1 = new Text(composite1, SWT.BORDER);
					GridData text1LData = new GridData();
					text1LData.widthHint = 52;
					text1LData.heightHint = 13;
					text1.setLayoutData(text1LData);
					text1.setText("180");
				}*/
			}
			{
				composite2 = new Composite(dialogShell, SWT.NONE);
				GridLayout composite2Layout = new GridLayout();
				composite2Layout.numColumns = 2;
				composite2Layout.makeColumnsEqualWidth = true;
				GridData composite2LData = new GridData();
				composite2LData.grabExcessHorizontalSpace = true;
				composite2LData.horizontalAlignment = GridData.CENTER;
				composite2LData.heightHint = 26;
				composite2LData.verticalSpan = 8;
				composite2.setLayoutData(composite2LData);
				composite2.setLayout(composite2Layout);
				{
					okButton = new Button(composite2, SWT.PUSH | SWT.CENTER);
					GridData okButtonLData = new GridData();
					okButtonLData.widthHint = 49;
					okButtonLData.heightHint = 20;
					okButtonLData.horizontalAlignment = GridData.CENTER;
					okButton.setLayoutData(okButtonLData);
					okButton.setText("OK");
					okButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							okButtonWidgetSelected(evt);
						}
					});
				}
				{
					cancelButton = new Button(composite2, SWT.PUSH | SWT.CENTER);
					GridData cancelButtonLData = new GridData();
					cancelButtonLData.widthHint = 44;
					cancelButtonLData.heightHint = 20;
					cancelButtonLData.horizontalAlignment = GridData.CENTER;
					cancelButton.setLayoutData(cancelButtonLData);
					cancelButton.setText("Cancel");
					cancelButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							cancelButtonWidgetSelected(evt);
						}
					});
				}
			}
			dialogShell.setLocation(getParent().toDisplay(200, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void okButtonWidgetSelected(SelectionEvent evt) {
		//String seconds = text1.getText();
		if(checkbox.getSelection()) {
			System.setProperty("UseIMCell", "true");
		}
		else {
			System.setProperty("UseIMCell", "false");
		}
		
		String icr = incrementtext.getText();
		System.setProperty("PMVIncrement", icr);
		dialogShell.close();
	}
	
	private void cancelButtonWidgetSelected(SelectionEvent evt) {
		dialogShell.close();
	}

}
