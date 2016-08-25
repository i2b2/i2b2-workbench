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
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


/**
 *
 * @author  lcp5
 */
public class EnumeratedDialog extends Dialog {
    
	private Text nameBox;
    /**
     * Creates Keyword Dialog
     */
    public EnumeratedDialog(Shell parentShell) {
    	super(parentShell);
    	
    }
      	
    @Override
	protected Control createDialogArea(Composite parent){
    	Composite comp = (Composite)super.createDialogArea(parent);
    	comp.getShell().setText("Enter associated enumerated text value");
    	
       	GridLayout layout = (GridLayout)comp.getLayout();
    	layout.numColumns = 2;
    	
    	Label synLabel = new Label(comp, SWT.RIGHT);
    	synLabel.setText("Enumerated text: ");
    	nameBox = new Text(comp, SWT.SINGLE);
    	
    	GridData textData = new GridData ();	
		textData.widthHint = 250;
		textData.grabExcessHorizontalSpace = true;
		textData.horizontalAlignment = SWT.FILL;
		textData.heightHint = 15;
    	
    
    	nameBox.setLayoutData(textData);
    	nameBox.addVerifyListener(new VerifyListener() {			
			public void verifyText(VerifyEvent e){
				if((e.character == '\b') || (e.character == '\u007F')){
					e.doit = true;
					return;
				}
				// dont allow certain characters.
		//		if(invalid(e.character))
		//			e.doit = false;
				
				if(nameBox.getText().length() > 1999)
					e.doit = false;
			}
		});
    	
    	return parent;
    }
    
    @Override
	protected void buttonPressed(int buttonId){
    	
    	// OK
    	if(buttonId == 0){
    		ValueMetadata.getInstance().setVal(nameBox.getText());

        	close();
    	}
    	//Cancel
    	else if(buttonId ==1) {
    		ValueMetadata.getInstance().setVal(null);
    		close();
    	}
    }
	private boolean invalid(char c){
		if( (c == '*') || (c == '|') || (c == '/') || 
				 (c == '\\') || (c == ':') || (c == '"') || 
				 (c == '<') || (c == '>') || (c == '%') || (c == '?')) {
		
			MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
			mBox.setText("Please Note ...");
			mBox.setMessage("The following characters are not allowed for this field \n" + 
			" *   |   \\   /   :   \"   <   >   ?  %");
			int result = mBox.open();
			
			return true;
		}
		else
			return false;
	}

 }
