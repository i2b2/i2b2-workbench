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

package edu.harvard.i2b2.query.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;

public class OptionShell {

	private Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,10"
	private Text maxchildrentext = null;
	private Text maxwaitingtext = null;
	private Label label = null;
	private Label label1 = null;
	private Button okbutton = null;
	private Button closebutton = null;

	/**
	 * 
	 */
	public OptionShell() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * Before this is run, be sure to set up the launch configuration
		 * (Arguments->VM Arguments) for the correct SWT library path in order
		 * to run with the SWT dlls. The dlls are located in the SWT plugin jar.
		 * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
		 * installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
		 */
		Display display = Display.getDefault();
		OptionShell thisClass = new OptionShell();
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
		sShell = new Shell();
		sShell.setText("Query Tool Options Dialog");
		sShell.setImage(new Image(Display.getCurrent(), getClass()
				.getResourceAsStream("/edu/harvard/i2b2/query/core-cell.gif")));
		sShell.setSize(new Point(371, 178));
		sShell.setLayout(null);
		maxchildrentext = new Text(sShell, SWT.BORDER);
		maxchildrentext.setBounds(new Rectangle(276, 23, 69, 22));
		maxchildrentext.setText("200");
		maxwaitingtext = new Text(sShell, SWT.BORDER);
		maxwaitingtext.setBounds(new Rectangle(275, 60, 72, 20));
		maxwaitingtext.setText("180");
		label = new Label(sShell, SWT.NONE);
		label.setBounds(new Rectangle(11, 26, 246, 21));
		label.setText("Maximum number of children to be displayed:");
		label1 = new Label(sShell, SWT.NONE);
		label1.setBounds(new Rectangle(14, 59, 252, 18));
		label1.setText("Maximum waiting time (seconds) fro XML response:");
		okbutton = new Button(sShell, SWT.NONE);
		okbutton.setBounds(new Rectangle(75, 107, 48, 23));
		okbutton.setText("OK");
		okbutton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("widgetSelected()"); // TODO
						// Auto-generated
						// Event stub
						// widgetSelected
						// ()
						sShell.close();
					}
				});
		closebutton = new Button(sShell, SWT.NONE);
		closebutton.setBounds(new Rectangle(198, 106, 63, 23));
		closebutton.setText("Close");
		closebutton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("widgetSelected()"); // TODO
						// Auto-generated
						// Event stub
						// widgetSelected
						// ()
						sShell.close();
					}
				});
	}

}
