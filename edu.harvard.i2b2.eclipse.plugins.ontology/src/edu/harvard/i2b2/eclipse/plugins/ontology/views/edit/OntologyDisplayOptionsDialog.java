/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import edu.harvard.i2b2.eclipse.UserInfoBean;


/**
 *
 * @author  lcp5
 */
public class OntologyDisplayOptionsDialog extends Dialog {
    

	private Button showConceptCode = null;
    /**
     * Creates new form OntologyOptionsDialog
     */
    public OntologyDisplayOptionsDialog(Shell parentShell) {
    	super(parentShell);
    	
    }
      	
    @Override
	protected Control createDialogArea(Composite parent){
    	Composite comp = (Composite)super.createDialogArea(parent);
    	comp.getShell().setText("Edit Terms Options");
    	
       	GridLayout layout = (GridLayout)comp.getLayout();
    	layout.numColumns = 2;
    	
		Composite compOptions = new Composite(comp, SWT.NULL);
		GridLayout gridLayoutOptions = new GridLayout(1, false);
		compOptions.setLayout(gridLayoutOptions);
    	
    	showConceptCode = new Button(compOptions, SWT.CHECK);
    	showConceptCode.setText("Show concept code in tooltip");
    	showConceptCode.setSelection(Boolean.parseBoolean(
				System.getProperty("OntEditConceptCode")));   
    	
    	return parent;
    }
    
    @Override
	protected void createButtonsForButtonBar(Composite parent){
    	super.createButtonsForButtonBar(parent);
    	createButton(parent, 2, "Reset to Defaults", false);
    }
    
    @Override
	protected void buttonPressed(int buttonId){
    	// reset
    	if(buttonId == 2){
    	
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntEditConceptCode") != null)
    			System.setProperty("OntEditConceptCode",  UserInfoBean.getInstance().getCellDataParam("ont","OntConceptCode"));	
    		else
    			System.setProperty("OntEditConceptCode","false");
    		
       		showConceptCode.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntEditConceptCode")));
    		
    	}	
    	// OK
    	else if(buttonId == 0){
    		String message = "";
      		System.setProperty("OntEditConceptCode",  String.valueOf(showConceptCode.getSelection()));

        	close();
    	}
    	//Cancel
    	else if(buttonId ==1) {
    		close();
    	}
    }

 }
