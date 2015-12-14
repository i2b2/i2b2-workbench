/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.views.find;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.harvard.i2b2.eclipse.UserInfoBean;

public class FindDisplayOptionsDialog extends Dialog {
    
	private Text maximum = null;
	private Button showHiddens = null;
	private Button showSynonyms = null;
	private Button showPatientCount = null;
	private Button showShortTooltips = null;
	private Button showConceptCode = null;
	private Button disableModifiers = null;
	
    /**
     * Creates new form FindDisplayOptionsDialog
     */
    public FindDisplayOptionsDialog(Shell parentShell) {
    	super(parentShell);
    	
    }
   
    @Override
	protected Control createDialogArea(Composite parent){
    	Composite comp = (Composite)super.createDialogArea(parent);
    	comp.getShell().setText("Find Folders Options");
    	
       	GridLayout layout = (GridLayout)comp.getLayout();
    	layout.numColumns = 2;
    	
    	Label maxLabel = new Label(comp, SWT.RIGHT);
    	maxLabel.setText("Maximum number of children to display: ");
    	maximum = new Text(comp, SWT.SINGLE);
    	GridData data = new GridData(GridData.FILL_HORIZONTAL);
    	maximum.setLayoutData(data);
		maximum.setText(System.getProperty("OntFindMax"));
		
		Composite compOptions = new Composite(comp, SWT.NULL);
		GridLayout gridLayoutOptions = new GridLayout(1, false);
		compOptions.setLayout(gridLayoutOptions);
		
    	showHiddens = new Button(compOptions, SWT.CHECK);
    	showHiddens.setText("Show hiddens");
		showHiddens.setSelection(Boolean.parseBoolean(
				System.getProperty("OntFindHiddens")));
    	
    	//showSynonyms = new Button(compOptions, SWT.CHECK);
    	//showSynonyms.setText("Show synonyms");
    	//showSynonyms.setSelection(Boolean.parseBoolean(
				//System.getProperty("OntFindSynonyms")));

    	/*showPatientCount = new Button(compOptions, SWT.CHECK);
    	showPatientCount.setText("Show patient count");
    	showPatientCount.setSelection(Boolean.parseBoolean(
				System.getProperty("OntFindPatientCount")));
    	
    	showShortTooltips = new Button(compOptions, SWT.CHECK);
    	showShortTooltips.setText("Show short tooltips");
    	showShortTooltips.setSelection(Boolean.parseBoolean(
				System.getProperty("OntFindShortTooltips")));
    	
    	showConceptCode = new Button(compOptions, SWT.CHECK);
    	showConceptCode.setText("Show concept code in tooltip");
    	showConceptCode.setSelection(Boolean.parseBoolean(
				System.getProperty("OntFindConceptCode")));
    	
    	
    	disableModifiers = new Button(compOptions, SWT.CHECK);
    	disableModifiers.setText("Disable display of modifiers");
    	disableModifiers.setSelection(Boolean.parseBoolean(
				System.getProperty("OntDisableModifiers")));  */
    	
    	
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
    		
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindMax") != null)
    			System.setProperty("OntFindMax", UserInfoBean.getInstance().getCellDataParam("ont", "OntFindMax"));
    		else 
    			System.setProperty("OntFindMax","200");
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindHiddens") != null)	
    			System.setProperty("OntFindHiddens", UserInfoBean.getInstance().getCellDataParam("ont","OntFindHiddens"));
    		else
    			System.setProperty("OntFindHiddens","false");
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindSynonyms") != null)
    			System.setProperty("OntFindSynonyms",  UserInfoBean.getInstance().getCellDataParam("ont","OntFindSynonyms"));	
    		else
    			System.setProperty("OntFindSynonyms","true");		
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindPatientCount") != null)
    			System.setProperty("OntFindPatientCount",  UserInfoBean.getInstance().getCellDataParam("ont","OntFindPatientCount"));	
    		else
    			System.setProperty("OntFindPatientCount","false");
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindShortTooltips") != null)
    			System.setProperty("OntFindShortTooltips",  UserInfoBean.getInstance().getCellDataParam("ont","OntFindShortTooltips"));	
    		else
    			System.setProperty("OntFindShortTooltips","false");
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindConceptCode") != null)
    			System.setProperty("OntFindConceptCode",  UserInfoBean.getInstance().getCellDataParam("ont","OntFindConceptCode"));	
    		else
    			System.setProperty("OntFindConceptCode","false");    			
    		
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntDisableModifiers") != null)
    			System.setProperty("OntDisableModifiers",  UserInfoBean.getInstance().getCellDataParam("ont","OntDisableModifiers"));	
    		else
    			System.setProperty("OntDisableModifiers","false");
    		
    		maximum.setText(System.getProperty("OntFindMax"));
    		showHiddens.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntFindHiddens")));
    		showSynonyms.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntFindSynonyms")));
    		showPatientCount.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntFindPatientCount")));

    		showShortTooltips.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntFindShortTooltips")));
    		
    		showConceptCode.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntFindConceptCode")));
    		disableModifiers.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntDisableModifiers")));

    		
    	}	
    	// OK
    	else if(buttonId == 0){
    		String message = "";
    		try{
    			if(Integer.parseInt(maximum.getText())< 2)
    				message = "Maximum children size should be greater than 1 \n";
    		}catch(java.lang.NumberFormatException e){
    			message = message + "Maximum children size is invalid \n";
    		}
    		
    		if(!message.equals("")){
    			MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
						SWT.ICON_ERROR);
				mBox.setText("Please Note ...");
				mBox.setMessage(message);
				mBox.open();
    			return;
    		}
    		System.setProperty("OntFindMax", maximum.getText());
    		System.setProperty("OntFindHiddens", String.valueOf(showHiddens.getSelection()));
    		//System.setProperty("OntFindSynonyms", String.valueOf(showSynonyms.getSelection()));
      		//System.setProperty("OntFindPatientCount",  String.valueOf(showPatientCount.getSelection()));
      		//System.setProperty("OntFindShortTooltips",  String.valueOf(showShortTooltips.getSelection()));
      		//System.setProperty("OntFindConceptCode",  String.valueOf(showConceptCode.getSelection()));
      		//System.setProperty("OntDisableModifiers",  String.valueOf(disableModifiers.getSelection()));
      		close();
    	}
    	//Cancel
    	else if(buttonId ==1) {
    		close();
    	}
    }

 }


