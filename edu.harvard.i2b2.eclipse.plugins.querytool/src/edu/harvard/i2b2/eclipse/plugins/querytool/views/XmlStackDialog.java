/*
 * Copyright (c) 2006-2016 Partners HealthCare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors:
 *      Wensong Pan
 * 		Lori Phillips
 * 		Taowei David Wang
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.dialogs.Dialog;


public class XmlStackDialog extends Dialog 
{
	
	private java.util.List<StackData> msgs;
	
	public XmlStackDialog(Shell parentShell, java.util.List<StackData> msgs)
	{
		super(parentShell);
		this.msgs = msgs;
	}
	
	/** Create new form for list of response messages **/
	protected Control createDialogArea(Composite parent)
	{
		this.getShell().setText("XML Messages sent/received by this cell");
		Composite comp = (Composite) super.createDialogArea(parent);
		comp.setLayout( new FormLayout() );
    	
    	final List list = new List(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    	
    		FormData listFD = new FormData();
    		listFD.top 			= new FormAttachment(0,0);
    		listFD.bottom 		= new FormAttachment(100,-20);
    		listFD.left 		= new FormAttachment(0,0);    		
    		listFD.right 		= new FormAttachment(0,120);
    	list.setLayoutData(listFD);
    	
    	for (int loopIndex = 0; loopIndex < msgs.size() ; loopIndex++) 
    	{
    		list.add(msgs.get(loopIndex).getName());
    	}

    	final Text text = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL| SWT.H_SCROLL | SWT.READ_ONLY);
    	
			FormData textFD = new FormData();
			textFD.top 			= new FormAttachment(0,0);
			textFD.bottom 		= new FormAttachment(100,-20);
			textFD.left 		= new FormAttachment( list );    		
			textFD.right 		= new FormAttachment(100, 0);
         text.setLayoutData(textFD);
          
        	final Button showPassword = new Button(comp, SWT.CHECK);
        	showPassword.setText("Show password");
    		showPassword.setSelection(false);
    		
			FormData buttonFD = new FormData();
			buttonFD.top 			= new FormAttachment( list );
			buttonFD.bottom 		= new FormAttachment(100,0);
			buttonFD.left 		= new FormAttachment( 0, 0 );
		showPassword.setLayoutData(buttonFD);
			
    	list.addSelectionListener(new SelectionListener() 
    	{
    		public void widgetSelected(SelectionEvent event) 
    		{
    			if (( list.getSelectionIndex() > list.getItemCount() - 1) ||( list.getSelectionIndex() < 0))
    				return;
    			String outString = msgs.get(list.getSelectionIndex()).getMessage();
    			if(!showPassword.getSelection()){
    				Pattern p = Pattern.compile(">.+</password>");
    				Matcher m = p.matcher(outString);
    				outString = m.replaceAll(">*********</password>");
    			}
    			text.setText(outString);
    		}

    		public void widgetDefaultSelected(SelectionEvent event) 
    		{
    			if (( list.getSelectionIndex() > list.getItemCount() - 1) ||( list.getSelectionIndex() < 0))
    				return;
    			String outString = msgs.get(list.getSelectionIndex()).getMessage();
    			if(!showPassword.getSelection()){
    				Pattern p = Pattern.compile(">.+</password>");
    				Matcher m = p.matcher(outString);
    				outString = m.replaceAll(">*********</password>");
    			}
    			text.setText(outString);
    		}
    	});
    	
    	
    	return comp;
    }
    	
	// overwrite super. open with a size
	public int open()
	{		
		int val = super.open();
		int width 	= 700;
		int height 	= 500;
		this.getShell().setSize( width, height );
		this.getShell().setLocation((Display.getCurrent().getClientArea().width-width)/2, (Display.getCurrent().getClientArea().height-height)/2);
		return val;
	}
	
	
	// overwrite super. make this dialog resizable
	protected boolean isResizable() 
	{
		return true;
	}
	
}
