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

import java.util.ArrayList;

public abstract class AbstractTableModel
{

	protected ArrayList<String> myHeaders;
	
	public int getNumColumns() 
	{ return myHeaders.size(); }
	
	public ArrayList<String> getColumnHeaders() 
	{ return myHeaders; }
	
	public abstract int addData( Object obj );
	public abstract int getNumRowItems();
	public abstract Object getDataAt(int row, int col);
	
}
