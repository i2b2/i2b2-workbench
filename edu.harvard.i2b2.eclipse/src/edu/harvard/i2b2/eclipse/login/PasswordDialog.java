/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 
 * 		Wensong Pan 
 */

package edu.harvard.i2b2.eclipse.login;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
//import com.swtdesigner.SWTResourceManager;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.util.Messages;

public class PasswordDialog extends Dialog {

	private Text passwordText;
	protected Object result;

	protected Shell shell;

	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public PasswordDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public PasswordDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		shell.setLocation(400, 200);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	/**
	 * Create contents of the dialog
	 */
	protected void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setImage(new Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream("/edu/harvard/i2b2/eclipse/hive.gif"))); //$NON-NLS-1$
				//SWTResourceManager.getImage(PasswordDialog.class, "/edu/harvard/i2b2/eclipse/hive.gif"));
		shell.setSize(300, 131);
		shell.setText(Messages.getString("PasswordDialog.Title")); //$NON-NLS-1$

		final Label pleaseTypeYourLabel = new Label(shell, SWT.NONE);
		pleaseTypeYourLabel.setBounds(10, 20, 142, 20);
		pleaseTypeYourLabel.setText(Messages.getString("PasswordDialog.Text")); //$NON-NLS-1$

		passwordText = new Text(shell, SWT.BORDER);
		passwordText.setBounds(155, 15, 124, 23);
		passwordText.setEchoChar('*');

		final Button okButton = new Button(shell, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				UserInfoBean.getInstance().setUserPassword(passwordText.getText());
				System.setProperty("pass", passwordText.getText()); //$NON-NLS-1$
				
				shell.close();
			}
		});
		okButton.setText(Messages.getString("PasswordDialog.ButtonOK")); //$NON-NLS-1$
		okButton.setBounds(110, 65, 64, 23);
		//
	}
	
	public static void main(String[] args) {
		
		PasswordDialog thisClass = new PasswordDialog(new Shell());
		thisClass.open();
	}
}
