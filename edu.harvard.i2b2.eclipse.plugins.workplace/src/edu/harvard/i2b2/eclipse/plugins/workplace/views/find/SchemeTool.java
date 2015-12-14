/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 * 		Mike Mendis
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.views.find;

import java.util.Iterator;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

import edu.harvard.i2b2.common.exception.I2B2Exception;
//import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
//import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.GetReturnType;

public class SchemeTool extends ApplicationWindow 
{
	private Log log = LogFactory.getLog(SchemeTool.class.getName());
	private FindViewNodeBrowser browser;
	private String findText = null;
	private String schemesKey;
	private String match;
	private Button findButton;
	private List schemes;
	private StatusLineManager slm;
	public static final String OS = System.getProperty("os.name").toLowerCase();

	public SchemeTool(StatusLineManager slm)
	{
		super(null);
		this.slm = slm;
	}
	
	
	public Control getFindTabControl(TabFolder tabFolder)
	{		
		// Find Composite
		Composite compositeFind = new Composite(tabFolder, SWT.NULL);
		GridLayout gridLayout = new GridLayout(2, false);
		compositeFind.setLayout(gridLayout);		

		//	First Set up the match combo box
	    final Combo matchCombo = new Combo(compositeFind,SWT.READ_ONLY);

	    matchCombo.add("Starting with");
	    matchCombo.add("Ending with");
	    matchCombo.add("Containing");
	    matchCombo.add("Exact");
	    
	    // set default category
	    matchCombo.setText("Containing");
	    match = "Containing";
	    
	    matchCombo.addSelectionListener(new SelectionListener(){
	    	public void widgetSelected(SelectionEvent e) {
	    		// Item in list has been selected
	    		match = matchCombo.getItem(matchCombo.getSelectionIndex());
	    	}
	    	public void widgetDefaultSelected(SelectionEvent e) {
	    		// this is not an option (text cant be entered)
	    	}
	    });

	    // Then set up the Find text combo box    
	    final Combo findCombo = new Combo(compositeFind, SWT.DROP_DOWN);
		GridData findComboData = new GridData (GridData.FILL_HORIZONTAL);
		findComboData.widthHint = 200;
		findComboData.horizontalSpan = 1;
		findCombo.setLayoutData(findComboData);
	    findCombo.addModifyListener(new ModifyListener() {
	    	public void modifyText(ModifyEvent e) {	    
	    		// Text Item has been entered
	    		// Does not require 'return' to be entered
	    		findText = findCombo.getText();
	    	}
	    });
	    
	    findCombo.addSelectionListener(new SelectionListener(){
	    	public void widgetSelected(SelectionEvent e) {
	    	}
	    	public void widgetDefaultSelected(SelectionEvent e) {
	    		findText = findCombo.getText();
	    		if(findCombo.indexOf(findText) < 0) {
	    			findCombo.add(findText);
	    		}
	    		if(findButton.getText().equals("Find"))
	    		{
	    			slm.setMessage("Performing search");
	    			slm.update(true);
	    			browser.flush();
	    			System.setProperty("statusMessage", "Calling WebService");
;
		 			TreeNode placeholder = new TreeNode("placeholder", "working...", "C-UNDEF");
		 			browser.rootNode.addChild(placeholder);
					browser.refresh();
					//ModifierComposite.getCodeInstance().disableComposite();
					browser.getSchemeData(schemesKey, schemes, findText, match).start();
	    			findButton.setText("Cancel");
	    		}
	    		else
	    		{
	    			System.setProperty("statusMessage", "Canceling WebService call");
	    			browser.refresh();
	    			browser.stopRunning = true;
	    			findButton.setText("Find");
	    		}
	    	}
	    });
	     
	    // Next include 'Find' Button
	    findButton = new Button(compositeFind, SWT.PUSH);
	    findButton.setText("Find");
		GridData findButtonData = new GridData ();
		if (OS.startsWith("mac"))	
			findButtonData.widthHint = 80;
		else
			findButtonData.widthHint = 60;
	    findButton.setLayoutData(findButtonData);
	    findButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
	    		// Add item to findCombo drop down list if not already there
	    		if(findText == null)
	    		{
	    			return;
	    		}
	    		if(findCombo.indexOf(findText) < 0) {
	    			findCombo.add(findText);
	    		}
	    		if(findButton.getText().equals("Find"))
	    		{	    			
	    			browser.flush();
	    			System.setProperty("statusMessage", "Calling WebService");
		 			TreeNode placeholder = new TreeNode("placeholder", "working...", "C-UNDEF");
					browser.rootNode.addChild(placeholder);
					browser.refresh();
					//ModifierComposite.getCodeInstance().disableComposite();
	    			browser.getSchemeData(schemesKey, schemes, findText, match).start();
	    			findButton.setText("Cancel");
	    		}
	    		else
	    		{
	    			System.setProperty("statusMessage", "Canceling WebService call");
	    			browser.refresh();
	    			browser.stopRunning = true;
	    			findButton.setText("Find");
	    		}
	    	}
	    });	
	    
	    // Next set up the schemes combo box
	    final Combo schemesCombo = new Combo(compositeFind,SWT.READ_ONLY);
	    setSchemes(schemesCombo);    
	    
	    schemesCombo.addSelectionListener(new SelectionListener(){
	    	public void widgetSelected(SelectionEvent e) {
	    		// Item in list has been selected
	    		if (schemesCombo.getSelectionIndex() == 0)
	    			schemesKey = "any";
	    		else{
	    			ConceptType concept = (ConceptType)schemes.get(schemesCombo.getSelectionIndex()-1);
	    			schemesKey = concept.getKey();
	    		}
	    	}
	    	public void widgetDefaultSelected(SelectionEvent e) {
	    		// this is not an option (text cant be entered)
	    	}
	    });
	    
		
	 
	    //ModifierComposite.setCodeInstance(compositeFind);
	    browser = new FindViewNodeBrowser(compositeFind, 1, findButton, slm);	    

	    
	    
	    return compositeFind;
	}

	
	private void setSchemes(Combo schemesCombo)
	{
		// set default category for combo box
	
		schemesCombo.add("Any Coding System");
	    schemesCombo.setText("Any Coding System");
	    schemesKey = "any";	
		schemes = getSchemes();

		if(schemes != null) {
			Iterator schemesIterator = schemes.iterator();		
			while(schemesIterator.hasNext())
			{
				ConceptType scheme = (ConceptType) schemesIterator.next();
				String name = scheme.getName();
				schemesCombo.add(name);
			}
		}
	
		return;
	}
	
	public List getSchemes() 
	{
		/*try {
			GetReturnType request = new GetReturnType();
			request.setType("default");

			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = OntServiceDriver.getSchemes(request, "FIND");
				procStatus = msg.processResult(response);
//				if  error 
//				TABLE_ACCESS_DENIED and USER_INVALID, DATABASE ERROR
				if (procStatus.getType().equals("ERROR")){					
			 		System.setProperty("errorMessage", procStatus.getValue());
					return null;
				}
				procStatus.setType("DONE");
			}
			ConceptsType allConcepts = msg.doReadConcepts();   	    
			schemes = allConcepts.getConcept();

		} catch (AxisFault e) {
    		log.error(e.getMessage());
    		System.setProperty("errorMessage", "Ontology cell unavailable");	
    	} catch (I2B2Exception e) {
    		log.error(e.getMessage());
    		System.setProperty("errorMessage", e.getMessage());
		} catch (Exception e) {
    		log.error(e.getMessage());
    		System.setProperty("errorMessage", "Remote service unavailable");
		}*/
		return null;//schemes;
	}

}
