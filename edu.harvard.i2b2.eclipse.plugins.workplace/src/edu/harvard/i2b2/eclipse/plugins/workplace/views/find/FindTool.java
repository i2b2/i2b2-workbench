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
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.*;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.*;

import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.StringUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.views.find.TreeNode;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.GetChildrenResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceResponseMessage;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.GetCategoriesType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FolderType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.GetReturnType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FoldersType;


public class FindTool extends ApplicationWindow 
{
	private Log log = LogFactory.getLog(FindTool.class.getName());
	private FindViewNodeBrowser browser;
	private String findText = null;
	private String categoryKey;
	private String match;
	private Button findButton;
	private List categories;
	private StatusLineManager slm;
	public static final String OS = System.getProperty("os.name").toLowerCase();

	
	public FindTool(StatusLineManager slm)
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
/*		GridData fromTreeGridData = new GridData (GridData.FILL_BOTH);
		fromTreeGridData.grabExcessHorizontalSpace = true;
		fromTreeGridData.grabExcessVerticalSpace = true;
	//	fromTreeGridData.widthHint = 300;
		compositeFind.setLayoutData(fromTreeGridData);
*/
		
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
	    			//ModifierComposite.getInstance().disableComposite();
	    			System.setProperty("statusMessage", "Calling WebService");
	    			
		 			TreeNode placeholder = new TreeNode("placeholder", "working...", "C-UNDEF");
		 			browser.rootNode.addChild(placeholder);
					browser.refresh();
					
					browser.getFindData(categoryKey,categories, findText, match).start();
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
		if (OS.startsWith("mac")) {
			findButtonData.widthHint = 80;
		}
		else {
			findButtonData.widthHint = 60;
		}
		//findButtonData.heightHint = 25;
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
	    			//ModifierComposite.getInstance().disableComposite();
	    			browser.flush();
	    			System.setProperty("statusMessage", "Calling WebService");
		 			TreeNode placeholder = new TreeNode("placeholder", "working...", "C-UNDEF");
					browser.rootNode.addChild(placeholder);
					browser.refresh();
					
	    			browser.getFindData(categoryKey,categories,findText, match).start();
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
	    // Next set up the category combo box
	    final Combo categoryCombo = new Combo(compositeFind,SWT.READ_ONLY);
	    setCategories(categoryCombo);    
	    categoryCombo.select(0);
	    
	    categoryCombo.addSelectionListener(new SelectionListener(){
	    	public void widgetSelected(SelectionEvent e) {
	    		// Item in list has been selected
	    		if (categoryCombo.getSelectionIndex() == 0) {
	    			categoryKey = "@";
	    		}
	    		else {
	    			FolderType folder = (FolderType)categories.get(categoryCombo.getSelectionIndex()-1);
	    			categoryKey = folder.getName();//StringUtil.getTableCd(concept.getKey());
	    		}
	    	}
	    	public void widgetDefaultSelected(SelectionEvent e) {
	    		// this is not an option (text cant be entered)
	    	}
	    });
	    //ModifierComposite.setInstance(compositeFind);
	    browser = new FindViewNodeBrowser(compositeFind, 1, findButton, slm);
	    

	    
	    return compositeFind;
	}
	  

	private void setCategories(Combo categoryCombo)
	{
		// set default category for combo box
		//categoryCombo.add("Any Category");
	    //categoryCombo.setText("Any Category");
	    //categoryKey = "any";
		
		categories = getCategories();
		categoryCombo.add("In all folders");
		categoryKey = "@";//((FolderType)categories.get(0)).getName();
		
		if(categories != null) {
			Iterator categoriesIterator = categories.iterator();		
			while(categoriesIterator.hasNext())
			{
				FolderType category = (FolderType) categoriesIterator.next();
				String name = category.getName();
				categoryCombo.add("In folder "+name);
				//categoryCombo.getSelectionIndex();
			}
		}
		return;
	}
	
    public List getCategories() 
    {
    	List nodes = null;
    	try {
			GetReturnType request = new GetReturnType();
			request.setType("core");

			GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = null;
				if(Boolean.parseBoolean(System.getProperty("WPManager")))
					response = WorkplaceServiceDriver.getHomeFoldersByProject(request);
				else
					response = WorkplaceServiceDriver.getHomeFoldersByUserId(request);
				procStatus = msg.processResult(response);

				//				if  other error codes
				//				TABLE_ACCESS_DENIED and USER_INVALID
				if (procStatus.getType().equals("ERROR")){

					System.setProperty("errorMessage",  procStatus.getValue());				

					return null;
				}	
				procStatus.setType("DONE");
			}
			FoldersType allFolders = msg.doReadFolders();   	    
			nodes = allFolders.getFolder();
			//getNodesFromXMLString(folders);	
		} catch (AxisFault e) {
			log.error(e.getMessage());
			System.setProperty("errorMessage",  "Workplace cell is unavailable");
		} catch (I2B2Exception e) {
			log.error(e.getMessage());
			System.setProperty("errorMessage",  e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			System.setProperty("errorMessage",  "Remote server is unavailable");

		}

		return nodes;
    }	   
	
}