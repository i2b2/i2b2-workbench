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
package edu.harvard.i2b2.eclipse.plugins.patientMapping.views;

import javax.swing.JOptionPane;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author wp066
 * 
 */
public class SearchViewActionDelegate implements IViewActionDelegate {

	private PatientMappingView view_;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		view_ = (PatientMappingView) view;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		// System.out.println("Timeline View Search Action.");
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// new TimelineSearchFrame(view_.getRecord()).setVisible(true);
				/*if (view_.getRecord() == null) {
					JOptionPane
							.showMessageDialog(null,
									"The search dialog shows only when the timeline tab is active.");
					return;
				}
				view_.getRecord().showSearchFrame();*/
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
		// TODO Auto-generated method stub

	}

}
