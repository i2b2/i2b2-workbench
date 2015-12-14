/*
 * Copyright (c) 2006-2015 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.table;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableColumn;

public abstract class TableViewerMouseListener implements IStructuredViewerListener 
{
	protected TableViewer 	myTV;
	protected ITableModel	myModel;
	
	public TableViewerMouseListener( TableViewer tv, ITableModel model )
	{
		myTV = tv;
		myModel = model;
	}
	
	public ITableModel getModel()
	{ return myModel; }
	public ITableModel setModel( ITableModel model )
	{
		ITableModel oldModel = myModel;
		myModel = model;
		return oldModel; 
	}
	
	
	public void handleEvent(Event event) 
	{
		int eventType = event.type;
		if (eventType == SWT.MouseUp) 
			mouseUpped( event ); 
		else if (eventType == SWT.MouseDown) 
			mouseDowned( event );
		else if (eventType == SWT.MouseMove) 
			mouseMoved( event );
		else if ( eventType == SWT.MouseEnter )
			mouseEntered( event );
		else if ( eventType == SWT.MouseExit )
			mouseExited( event );
		else if ( eventType == SWT.MouseWheel )
			mouseWheeled( event );
		else if ( eventType == SWT.MouseHover )
			mouseHovered( event );
	}	
	
	public int mapPointToColumnIndex( int x, int y)
	{
		// assume the columns are created the order they are shown in table
		TableColumn [] columns = myTV.getTable().getColumns();
		int i = 0;
		int columnStart = 0;
		for ( i = 0; i < columns.length; i++ )
		{
			int columnEnd = columnStart + columns[i].getWidth()-1;
			if ( (x >= columnStart) && (x < columnEnd ) )
				return i;
			columnStart = columnEnd;
		}
		return -1;
	}

}
