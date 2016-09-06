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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.ValuePropertyData;

public class ValueRestrictionEditorFactory 
{
	private static ValueRestrictionEditorFactory myInstance = null;
	
	public static ValueRestrictionEditorFactory getInstance()
	{
		if ( myInstance == null )
			myInstance = new ValueRestrictionEditorFactory();
		return myInstance;
	}
	
	
	private ValueRestrictionEditorFactory()
	{}
	
	public List<QueryConceptTreeNodeData> getValueRestrictedNodes( List<QueryConceptTreeNodeData> nodeData )
	{
		ArrayList<QueryConceptTreeNodeData> nodes = new ArrayList<QueryConceptTreeNodeData>(); 
		for ( QueryConceptTreeNodeData node : nodeData )
		{
			if ( node.hasValue() )
				nodes.add( node );
			/*
			else if ( node instanceof ModifierData )
			{
				nodes.add( node );
			}*/
		}
		return nodes;
	}
	
	public List<ValueRestrictionEditorPanel> makeEditors( Composite parent, List<QueryConceptTreeNodeData> nodeData )
	{
		ArrayList<ValueRestrictionEditorPanel> panels = new ArrayList<ValueRestrictionEditorPanel>(); 
		for ( QueryConceptTreeNodeData node : nodeData )
		{
			ValueRestrictionEditorPanel panel = makeEditor( parent, node );
			if ( panel != null )
				panels.add( panel );
		}
		return panels;
	}
	
	
	public ValueRestrictionEditorPanel makeEditor( Composite parent, QueryConceptTreeNodeData node )
	{
		ValueRestrictionEditorPanel panel = null;
		if ( node == null )		// for testing only, normally node is never null
			return new ValueRestrictionEditorPanel( parent, SWT.NONE ){};

		if ( node.hasValue() )	// whether it's a Concept node or a Modifier node, either may have a ValueRestriction
		{
			if (node.valuePropertyData().hasEnumValue()) 			// enum value
				return new EnumValueRestrictionEditorPanel( parent, SWT.NONE, node );
			else if (node.valuePropertyData().hasStringValue()) 	// text blob value
				return new TextBlobValueRestrictionEditorPanel( parent, SWT.NONE, node );
			else 													// must have numeric Value
				return new NumericValueRestrictionEditorPanel( parent, SWT.NONE, node );
		}			
		return panel;
	}

	
}
