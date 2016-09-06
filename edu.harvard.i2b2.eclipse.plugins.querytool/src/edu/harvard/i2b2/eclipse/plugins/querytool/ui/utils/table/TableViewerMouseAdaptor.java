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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.table;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;

public class TableViewerMouseAdaptor extends TableViewerMouseListener
{
	public TableViewerMouseAdaptor(TableViewer tv, ITableModel model) 
	{
		super(tv, model);
	}

	public void mouseUpped(Event event) 
	{}

	public void mouseDowned(Event event) 
	{}

	public void mouseMoved(Event event)
	{}

	public void mouseEntered(Event event) 
	{}

	public void mouseExited(Event event) 
	{}

	public void mouseWheeled(Event event) 
	{}

	public void mouseHovered(Event event) 
	{}

}
