/*
* Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.login;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import edu.harvard.i2b2.eclipse.util.Messages;


/**
 *
 * @author  lcp5
 */
public class DisplayVersionMismatchDialog extends Dialog {
    
	
    /**
     * Creates new form VersionMismatchDialog
     */
    public DisplayVersionMismatchDialog(Shell parentShell, String appName, String workbenchversion, String OS) {
    	super(parentShell); 	
    }
      	
    protected Control createDialogArea(Composite parent, String appName, String workbenchversion, String OS){    	
    	Composite comp = (Composite)super.createDialogArea(parent);
    	comp.getShell().setText(Messages.getString("DisplayVersionMismatchDialog.VersionConflictTitle")); //$NON-NLS-1$
    	
       	GridLayout layout = (GridLayout)comp.getLayout();
    	layout.numColumns = 2;
    	
    	Label maxLabel = new Label(comp, SWT.RIGHT);
    	maxLabel.setText(Messages.getString("DisplayVersionMismatchDialog.VersionConflictText1") + appName + Messages.getString("DisplayVersionMismatchDialog.VersionConflictText2") + workbenchversion //$NON-NLS-1$ //$NON-NLS-2$
				+Messages.getString("DisplayVersionMismatchDialog.VersionConflictText3") + appName + Messages.getString("DisplayVersionMismatchDialog.VersionConflictText4") //$NON-NLS-1$ //$NON-NLS-2$
				+ Messages.getString("DisplayVersionMismatchDialog.VersionConflictText5") //$NON-NLS-1$
				+ Messages.getString("DisplayVersionMismatchDialog.VersionConflictText6") //$NON-NLS-1$
				+ Messages.getString("DisplayVersionMismatchDialog.VersionConflictText7")); //$NON-NLS-1$

		Button override = new Button(comp, SWT.PUSH);
		override.setText(Messages.getString("DisplayVersionMismatchDialog.OverrideOption")); //$NON-NLS-1$
		if (OS.startsWith("mac"))	 //$NON-NLS-1$
			override.setBounds(new Rectangle(187, 144, 90, 33));
		else 
			override.setBounds(new Rectangle(217, 144, 50, 23));
		override.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				System.setProperty("override", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			};
		});		
		
   
		Button exit = new Button(comp, SWT.PUSH);
		exit.setText(Messages.getString("DisplayVersionMismatchDialog.ExitApplication")); //$NON-NLS-1$
		if (OS.startsWith("mac"))	 //$NON-NLS-1$
			exit.setBounds(new Rectangle(187, 144, 90, 33));
		else 
			exit.setBounds(new Rectangle(217, 144, 50, 23));
		
		exit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				System.setProperty("override", "false"); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			};
		});	
		

    	return parent;
    }


 }
