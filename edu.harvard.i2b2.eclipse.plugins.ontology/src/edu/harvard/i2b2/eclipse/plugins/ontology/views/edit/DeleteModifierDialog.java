/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.StringUtil;



/**
 *
 * @author  lcp5
 */
public class DeleteModifierDialog extends Dialog {

	private Button children= null;
	private TreeNode node = null;
	private TreeNode parentNode = null;
	
	private Log log = LogFactory.getLog(DeleteModifierDialog.class.getName());	
	/**
	 * Creates new form OntologyOptionsDialog
	 */
	public DeleteModifierDialog(Shell parentShell, TreeNode modifierNode, TreeNode currentNode) {
		super(parentShell);	
		node = modifierNode;
		parentNode = currentNode;
	}

	@Override
	protected Control createDialogArea(Composite parent){
		Composite comp = (Composite)super.createDialogArea(parent);
		comp.getShell().setText("Delete Modifier Confirmation");

		GridLayout layout = (GridLayout)comp.getLayout();
		layout.numColumns = 1;

	
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 15;
		gridLayout.horizontalSpacing = 15;
		gridLayout.marginTop = 5;
		gridLayout.marginRight = 15;
		gridLayout.marginLeft = 15;
		gridLayout.marginBottom = 5;
		gridLayout.numColumns = 1;

		comp.setLayout(gridLayout);
		new Label(comp,SWT.NONE);
		Label message = new Label(comp,SWT.NONE);


		if(node.getData().getModifier().getAppliedPath().length()+ 1 < StringUtil.getPath(parentNode.getData().getFullName()).length()){
			
			message.setText("This modifier applies to concept: " + node.getData().getModifier().getAppliedPath() 
				+ "\n Deleting it will cause it to be excluded from selected concept "+ parentNode.getData().getName()+ " and its children \n\n" +
				"Are you sure you want to exclude this modifier?");

			children = new Button(comp,SWT.CHECK);
			children.setText("Apply to children of this modifier also");
			children.setSelection(true);

		}
		else{
			message.setText("Are you sure you want to delete this modifier?");
			new Label(comp,SWT.NONE);
			children = new Button(comp,SWT.CHECK);
			children.setText("Include children of this modifier");
			children.setSelection(true);
		}
		


		GridData data = new GridData ();
		data.widthHint = 300;
		data.heightHint = 20;
		data.horizontalAlignment = GridData.BEGINNING;


		
		return parent;
	}
//	@Override
//	 protected Button createButton(Composite arg0, int arg1, String arg2, boolean arg3)
//	 {
	 //Retrun null so that no default buttons like 'OK' and 'Cancel' will be created
//		return null;
//	 }
	
/*	@Override
	protected void createButtonsForButtonBar(Composite parent){
		super.createButtonsForButtonBar(parent);
//		createButton(parent, 0, "Update", true);
//		createButton(parent, 1, "Cancel/Exit", false);
		createButton(parent, 2, "Run in Background", false);
	}
*/
	@Override
	protected void buttonPressed(int buttonId){
		// Run in background
	
		// OK
		if(buttonId == 0){
			// run synchronize within processStatus command
			System.setProperty("IncludeChildren" , String.valueOf(children.getSelection()));
			System.setProperty("cancel", "false");
		}


		//Cancel
		else if(buttonId ==1) {
			System.setProperty("cancel", "true");
		}
		close();
	}
	
}

