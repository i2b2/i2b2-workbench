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

import java.util.ArrayList;
import java.util.Iterator;

public interface ITableModel
{
	
	public int getNumRowItems();
	public int getNumColumns();
	
	public Object getDataAt( int row, int col);
	
	public ArrayList<String> getColumnHeaders();
	
	// to allow traversal of all items in the table
	public Iterator<Object> getIterator();
}
