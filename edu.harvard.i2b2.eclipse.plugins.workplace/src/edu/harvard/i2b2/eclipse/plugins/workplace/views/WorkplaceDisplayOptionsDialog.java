/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


/**
 *
 * @author  lcp5
 */

// This was disabled from plugin.xml as there are no options to display.
public class WorkplaceDisplayOptionsDialog extends Dialog {
    
    /**
     * Creates new form WorkplaceOptionsDialog
     */
    public WorkplaceDisplayOptionsDialog(Shell parentShell) {
    	super(parentShell);
    	
    }
      	
    protected Control createDialogArea(Composite parent){
    	Composite comp = (Composite)super.createDialogArea(parent);
    	comp.getShell().setText("Workplace Options");
   
    	return parent;
    }
    
    protected void createButtonsForButtonBar(Composite parent){
    	super.createButtonsForButtonBar(parent);
    }
    
    protected void buttonPressed(int buttonId){
    	// reset
    }

 }
