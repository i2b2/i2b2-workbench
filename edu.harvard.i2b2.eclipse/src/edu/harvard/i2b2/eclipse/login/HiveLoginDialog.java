/*
* Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *    wp066 
 */

package edu.harvard.i2b2.eclipse.login;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.harvard.i2b2.eclipse.util.Messages;


public class HiveLoginDialog {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Label label = null;
	private Label label1 = null;
	private Combo combo = null;
	private Label label2 = null;
	private Text text = null;
	private Label label3 = null;
	private Text text1 = null;
	private Button checkBox = null;
	private Button button = null;
	private Button button1 = null;
	private Button button2 = null;
	private Label label4 = null;

	/**
	 * This method initializes combo	
	 *
	 */
	private void createCombo() {
		combo = new Combo(sShell, SWT.NONE);
		combo.setBounds(new Rectangle(88, 33, 185, 21));
		combo.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
		 * for the correct SWT library path in order to run with the SWT dlls. 
		 * The dlls are located in the SWT plugin jar.  
		 * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
		 *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
		 */
		Display display = Display.getDefault();
		HiveLoginDialog thisClass = new HiveLoginDialog();
		thisClass.createSShell();
		thisClass.sShell.open();

		while (!thisClass.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell(SWT.DIALOG_TRIM | SWT.MODELESS);
		sShell.setText(Messages.getString("HiveLoginDialog.LoginText")); //$NON-NLS-1$
		sShell.setSize(new Point(295, 233));
		sShell.setLayout(null);
		label = new Label(sShell, SWT.NONE);
		label.setBounds(new Rectangle(78, 10, 143, 18));
		label.setText(Messages.getString("HiveLoginDialog.EnterCredential")); //$NON-NLS-1$
		label1 = new Label(sShell, SWT.NONE);
		label1.setBounds(new Rectangle(17, 34, 49, 18));
		label1.setText(Messages.getString("HiveLoginDialog.Project")); //$NON-NLS-1$
		createCombo();
		label2 = new Label(sShell, SWT.NONE);
		label2.setBounds(new Rectangle(18, 62, 62, 13));
		label2.setText(Messages.getString("HiveLoginDialog.UserName")); //$NON-NLS-1$
		text = new Text(sShell, SWT.BORDER);
		text.setBounds(new Rectangle(87, 59, 185, 19));
		label3 = new Label(sShell, SWT.NONE);
		label3.setBounds(new Rectangle(18, 87, 59, 16));
		label3.setText(Messages.getString("HiveLoginDialog.Password")); //$NON-NLS-1$
		text1 = new Text(sShell, SWT.BORDER);
		text1.setBounds(new Rectangle(87, 86, 185, 19));
		checkBox = new Button(sShell, SWT.CHECK);
		checkBox.setBounds(new Rectangle(18, 113, 156, 19));
		checkBox.setText(Messages.getString("HiveLoginDialog.InDemo")); //$NON-NLS-1$
		checkBox.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
		button = new Button(sShell, SWT.NONE);
		button.setBounds(new Rectangle(180, 112, 21, 18));
		button.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD)); //$NON-NLS-1$
		button.setText("?"); //$NON-NLS-1$
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
		button1 = new Button(sShell, SWT.NONE);
		button1.setBounds(new Rectangle(147, 139, 54, 23));
		button1.setText(Messages.getString("HiveLoginDialog.ButtonLogin")); //$NON-NLS-1$
		button2 = new Button(sShell, SWT.NONE);
		button2.setBounds(new Rectangle(217, 139, 50, 23));
		button2.setText(Messages.getString("HiveLoginDialog.ButtonCancel")); //$NON-NLS-1$
		label4 = new Label(sShell, SWT.SHADOW_NONE | SWT.WRAP | SWT.BORDER);
		label4.setBounds(new Rectangle(4, 177, 281, 27));
		label4.setText("http://phsi2b2appdev.mgh.harvard.edu:8080/i2b2/services/select"); //$NON-NLS-1$
	}
}
