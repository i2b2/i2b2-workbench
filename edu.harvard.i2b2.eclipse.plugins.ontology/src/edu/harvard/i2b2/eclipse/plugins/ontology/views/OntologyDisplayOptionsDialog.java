/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views;

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
    
	private Text maximum = null;
	private Button showHiddens = null;
	private Button showSynonyms = null;
	private Button showPatientCount = null;
	private Button showShortTooltips = null;
	private Button showConceptCode = null;
	private Button disableModifiers = null;
    /**
     * Creates new form OntologyOptionsDialog
     */
    public OntologyDisplayOptionsDialog(Shell parentShell) {
    	super(parentShell);
    	
    }
      	
    @Override
	protected Control createDialogArea(Composite parent){
    	Composite comp = (Composite)super.createDialogArea(parent);
    	comp.getShell().setText("Navigate Terms Options");
    	
       	GridLayout layout = (GridLayout)comp.getLayout();
    	layout.numColumns = 2;
    	
    	Label maxLabel = new Label(comp, SWT.RIGHT);
    	maxLabel.setText("Maximum number of children to display: ");
    	maximum = new Text(comp, SWT.SINGLE);
    	GridData data = new GridData(GridData.FILL_HORIZONTAL);
    	maximum.setLayoutData(data);
		maximum.setText(System.getProperty("OntMax"));
    	
		Composite compOptions = new Composite(comp, SWT.NULL);
		GridLayout gridLayoutOptions = new GridLayout(1, false);
		compOptions.setLayout(gridLayoutOptions);
    	
    	showHiddens = new Button(compOptions, SWT.CHECK);
    	showHiddens.setText("Show hiddens");
		showHiddens.setSelection(Boolean.parseBoolean(
				System.getProperty("OntHiddens")));
    	
    	showSynonyms = new Button(compOptions, SWT.CHECK);
    	showSynonyms.setText("Show synonyms");
    	showSynonyms.setSelection(Boolean.parseBoolean(
				System.getProperty("OntSynonyms")));

    	showPatientCount = new Button(compOptions, SWT.CHECK);
    	showPatientCount.setText("Show patient count");
    	showPatientCount.setSelection(Boolean.parseBoolean(
				System.getProperty("OntPatientCount")));
    	
    	showShortTooltips = new Button(compOptions, SWT.CHECK);
    	showShortTooltips.setText("Show short tooltips");
    	showShortTooltips.setSelection(Boolean.parseBoolean(
				System.getProperty("OntShortTooltips")));
    	
    	showConceptCode = new Button(compOptions, SWT.CHECK);
    	showConceptCode.setText("Show concept code in tooltip");
    	showConceptCode.setSelection(Boolean.parseBoolean(
				System.getProperty("OntConceptCode")));   	
    	
    	disableModifiers = new Button(compOptions, SWT.CHECK);
    	disableModifiers.setText("Disable display of modifiers");
    	disableModifiers.setSelection(Boolean.parseBoolean(
				System.getProperty("OntDisableModifiers")));  
    	
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
    	
    		
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntMax") != null)
    			System.setProperty("OntMax", UserInfoBean.getInstance().getCellDataParam("ont", "OntMax"));
    		else 
    			System.setProperty("OntMax","200");
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntHiddens") != null)	
    			System.setProperty("OntHiddens", UserInfoBean.getInstance().getCellDataParam("ont","OntHiddens"));
    		else
    			System.setProperty("OntHiddens","false");
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntSynonyms") != null)
    			System.setProperty("OntSynonyms",  UserInfoBean.getInstance().getCellDataParam("ont","OntSynonyms"));	
    		else
    			System.setProperty("OntSynonyms","false");
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntPatientCount") != null)
    			System.setProperty("OntPatientCount",  UserInfoBean.getInstance().getCellDataParam("ont","OntPatientCount"));	
    		else
    			System.setProperty("OntPatientCount","false");
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntShortTooltips") != null)
    			System.setProperty("OntShortTooltips",  UserInfoBean.getInstance().getCellDataParam("ont","OntShortTooltips"));	
    		else
    			System.setProperty("OntShortTooltips","false");
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntConceptCode") != null)
    			System.setProperty("OntConceptCode",  UserInfoBean.getInstance().getCellDataParam("ont","OntConceptCode"));	
    		else
    			System.setProperty("OntConceptCode","false");
    		
    		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntDisableModifiers") != null)
    			System.setProperty("OntDisableModifiers",  UserInfoBean.getInstance().getCellDataParam("ont","OntDisableModifiers"));	
    		else
    			System.setProperty("OntDisableModifiers","false");
    		
    		maximum.setText(System.getProperty("OntMax"));
    		showHiddens.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntHiddens")));
        	
    		showSynonyms.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntSynonyms")));

    		showPatientCount.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntPatientCount")));

    		showShortTooltips.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntShortTooltips")));
    		
    		showConceptCode.setSelection(Boolean.parseBoolean(
    				System.getProperty("OntConceptCode")));
    		
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
    		
    		System.setProperty("OntMax", maximum.getText());
    		System.setProperty("OntHiddens", String.valueOf(showHiddens.getSelection()));
      		System.setProperty("OntSynonyms", String.valueOf(showSynonyms.getSelection()));
      		System.setProperty("OntPatientCount",  String.valueOf(showPatientCount.getSelection()));
      		System.setProperty("OntShortTooltips",  String.valueOf(showShortTooltips.getSelection()));
      		System.setProperty("OntConceptCode",  String.valueOf(showConceptCode.getSelection()));
      		System.setProperty("OntDisableModifiers",  String.valueOf(disableModifiers.getSelection()));
      		      		

        	close();
    	}
    	//Cancel
    	else if(buttonId ==1) {
    		close();
    	}
    }

 }
