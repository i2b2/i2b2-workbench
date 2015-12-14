/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *      Wensong Pan
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.views;

import javax.swing.JFrame;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import edu.harvard.i2b2.eclipse.plugins.workplace.util.MessageUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.views.WorkplaceDisplayXmlMessageDialog;

/**
 * @author wp066
 *
 */
public class ViewResponseMessageToolbarActionDelegate implements IViewActionDelegate {
	
	private WorkplaceView workView;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		workView = (WorkplaceView) view;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		
		String response = MessageUtil.getInstance().getResponse();
		JFrame frame = new WorkplaceDisplayXmlMessageDialog(response);
		frame.setTitle("Workplace Last XML Response Message");
		frame.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
