/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */
/**
 * 
 */
package edu.harvard.i2b2.eclipse.plugins.explorer.views;

import javax.swing.JFrame;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import edu.harvard.i2b2.explorer.ui.DisplayXmlMessageDialog;

/**
 * @author wp066
 * 
 */
public class RequestMessageViewActionDelegate implements IViewActionDelegate {
	private ExplorerView view_;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		view_ = (ExplorerView) view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new DisplayXmlMessageDialog(view_.explorer()
						.lastRequestMessage());
				frame.setTitle("Timeline View Last Request XML Message");
				frame.setVisible(true);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
