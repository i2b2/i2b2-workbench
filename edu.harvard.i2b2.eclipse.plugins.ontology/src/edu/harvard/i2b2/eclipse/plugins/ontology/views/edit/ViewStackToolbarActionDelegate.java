/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *      Wensong Pan
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;



import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.swt.widgets.*;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.MessageUtil;


/**
 * @author Lori Phillips
 *
 */
public class ViewStackToolbarActionDelegate implements IViewActionDelegate {
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {

		EditDisplayXmlStackList xmlList = new EditDisplayXmlStackList(Display.getCurrent().getActiveShell(), MessageUtil.getInstance().getXmlStack());
		xmlList.setBlockOnOpen(false);
		xmlList.open();		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
