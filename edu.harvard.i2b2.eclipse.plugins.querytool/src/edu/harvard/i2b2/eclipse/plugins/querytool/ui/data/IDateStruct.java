/*
 * Copyright (c) 2006-2017 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import java.util.GregorianCalendar;

public interface IDateStruct 
{
	public GregorianCalendar getStartDate();
	public void setStartDate(GregorianCalendar c);
	public GregorianCalendar getEndDate();
	public void setEndDate(GregorianCalendar c);
}
