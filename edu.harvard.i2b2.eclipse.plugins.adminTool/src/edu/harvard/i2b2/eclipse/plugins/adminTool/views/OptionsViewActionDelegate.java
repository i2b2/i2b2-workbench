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
package edu.harvard.i2b2.eclipse.plugins.adminTool.views;

import javax.swing.JOptionPane;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

//import edu.harvard.i2b2.timeline.lifelines.ControlPanel;

/**
 * @author wp066
 * 
 */
public class OptionsViewActionDelegate implements IViewActionDelegate {

	private AdminToolView view_;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		view_ = (AdminToolView) view;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				/*if (view_.getRecord() == null) {
					JOptionPane
							.showMessageDialog(null,
									"The option dialog shows only when the timeline tab is active.");
					return;
				}

				ControlPanel ctrlpanel = new ControlPanel(
						"Timeline Options Dialog", 400, 500);
				ctrlpanel.setBounds(700, 300, 400, 500);
				ctrlpanel.setVisible(true);*/
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
