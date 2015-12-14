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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import org.eclipse.swt.widgets.Composite;

import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.ValuePropertyData;

public abstract class ValueRestrictionEditorPanel extends Composite 
{

	protected QueryConceptTreeNodeData 	myNodeData 			= null;
	protected ValuePropertyData			myValueRestriction 	= null;
	
	
	public ValueRestrictionEditorPanel(Composite parent, int style) 
	{ super(parent, style); }
	
	public QueryConceptTreeNodeData getNodeData()
	{ return myNodeData; }
	
	public ValuePropertyData getValueRestriction()
	{ return myValueRestriction; }
	
	protected String getNodeName()
	{
		String conceptName = "the concept";
		if ( myNodeData != null )
			conceptName = myNodeData.name();
		return conceptName;
	}

	public void saveValueRestriction()
	{ myNodeData.valuePropertyData( myValueRestriction ); }
	
}
