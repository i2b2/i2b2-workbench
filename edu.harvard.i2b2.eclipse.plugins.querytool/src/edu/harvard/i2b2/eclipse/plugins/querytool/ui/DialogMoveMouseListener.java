/*
 * Copyright (c) 2006-2016 Partners HealthCare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DialogMoveMouseListener implements MouseListener, MouseMoveListener 
{
	
	private Shell 		myDialogShell;
	private Control		myAttachedControl;
	
	private boolean		isMouseDown;
	private	Point		myMouseDownPoint	= null;
		
	public DialogMoveMouseListener( Shell shell, Control control )
	{
		myDialogShell 		= shell;
		myAttachedControl 	= control;
		myAttachedControl.setCursor( new Cursor( shell.getDisplay(), SWT.CURSOR_SIZEALL) );
		isMouseDown 		= false;
	}

	public void mouseMove(MouseEvent e) 
	{
		if ( isMouseDown )
		{
			int deltaX = e.x - myMouseDownPoint.x;
			int deltaY = e.y - myMouseDownPoint.y;
			
			myDialogShell.setLocation( myDialogShell.getLocation().x + deltaX,
									   myDialogShell.getLocation().y + deltaY );
		}
	}


	public void mouseDown(MouseEvent arg0) 
	{
		isMouseDown 		= true;
		myMouseDownPoint 	= new Point( arg0.x, arg0.y );
	}

	public void mouseUp(MouseEvent arg0) 
	{
		isMouseDown 		= false;
		myMouseDownPoint 	= null;
	}

	public void mouseDoubleClick(MouseEvent arg0) 
	{ /*do nothing*/ }

}
