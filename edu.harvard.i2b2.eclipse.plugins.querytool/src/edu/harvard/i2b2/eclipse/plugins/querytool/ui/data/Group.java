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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Copyable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.DataConst;
import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;

public class Group implements DataConst, IDateStruct, Copyable<Group>
{
	
	private String				myName		= null;
	private GregorianCalendar 	myStartDate	= null;
	private GregorianCalendar 	myEndDate	= null;
	private int					myOperator	= UIConst.GREATER_THAN;
	private int					myNumber	= 0;
	private GroupBinding		myBinding	= GroupBinding.BY_PATIENT;
	
	// GroupBinding policy that governs whether BindingBy Patient/Encounter/Instance is allowed 
	private IGroupBindingPolicyProvider myGroupBindingPolicyProvider = IGroupBindingPolicyProvider.DEFAULT_POLICY;
	
	private QueryConceptTreeNodeData	myTreeData = null;
	
	private ArrayList<DataChangedListener> myListeners;
	
	private boolean	isContainingModifier 	= false;
	//private boolean isTemporalQuery			= false;		// for UIs. This tells UI that this Group is for TemporalQuery, we set the binding to, by default, by-observation
	private int	myAccuracy	= 100; // legacy member. It's always set to 100 now.
	
	
	public Group( String name )
	{
		myName		= name;
		myTreeData 	= new QueryConceptTreeNodeData();		// root node for all dropped concepts
		myListeners	= new ArrayList<DataChangedListener>();
		
		//this.isTemporalQuery	= isTemporalQuery;
		//if ( this.isTemporalQuery ) 
		//	this.myBinding = GroupBinding.BY_OBSERVATION;	// set the binding to, by default, by-observation
	}


	/* Copy Constructor*/
	private Group( Group g )
	{
		this.myName 		= g.myName;
		this.myTreeData		= g.myTreeData.makeCopy();
		this.myStartDate 	= (GregorianCalendar)g.myStartDate.clone();
		this.myEndDate		= (GregorianCalendar)g.myEndDate.clone();
		this.myOperator		= g.myOperator;
		this.myNumber		= g.myNumber;
		this.isContainingModifier 	= g.isContainingModifier;
		this.myAccuracy				= g.myAccuracy;
		//this.isTemporalQuery		= g.isTemporalQuery;
		// do not copy Listeners
		myListeners	= new ArrayList<DataChangedListener>();
	}
	
	/* Group Binding (ie. 'timing') Restrictions */
	public GroupBinding getBinding()				{ return this.myBinding; }
	
/*	
  	public boolean		isTemporalQuery()			{ return this.isTemporalQuery; }
	public void			setIsTemporalQuery( boolean isTQ ) { this.isTemporalQuery = isTQ; }
*/
	
	// setting binding requires explicit notification to GroupPanel
	public void setBinding( GroupBinding binding) 	
	{ 
		boolean toNotify = (this.myBinding != binding); // if it's the same value, don't notify
		this.myBinding = binding;
		
		if ( toNotify )
			notifyDataChangedListeners();
	}

	// set binding without notification to GroupPanel (called by GroupPanel)
	public void silentlySetBinding( GroupBinding binding)
	{ this.myBinding = binding; }
	
	/* Date Restrictions, DateStuct methods */
	public GregorianCalendar getStartDate()			{ return this.myStartDate; }
	public void setStartDate(GregorianCalendar c)	{ this.myStartDate = c; }	
	public GregorianCalendar getEndDate()			{ return this.myEndDate; }
	public void setEndDate(GregorianCalendar c)		{ this.myEndDate = c; }
	
	/* Number Restrictions */
	public int	getOperator()			{ return this.myOperator; }
	public void setOperator( int op )	{ this.myOperator = op; }
	public int	getNumber()				{ return this.myNumber; }
	public void setNumber( int num )	{ this.myNumber = num; }

	/* Terms in the Group Panel */
	public ArrayList<QueryConceptTreeNodeData> getTerms()	
	{ return this.myTreeData.getChildren(); }

	
	
	public void addTerms( ArrayList<QueryConceptTreeNodeData> terms )
	{
		for ( QueryConceptTreeNodeData term : terms )
		{
			this.myTreeData.addChild( term );
			if ( term instanceof ModifierData)
				isContainingModifier = true;
		}
		notifyDataChangedListeners();
	}
	
	public void	addTerm( QueryConceptTreeNodeData term )
	{ 
		this.myTreeData.addChild( term );
		if ( term instanceof ModifierData)
			isContainingModifier = true;
		notifyDataChangedListeners();
	}
	
	public boolean removeTerm( QueryConceptTreeNodeData term )
	{ 
		boolean success = this.myTreeData.removeChild( term );
		if ( success )
		{
			isContainingModifier = false;
			for ( QueryConceptTreeNodeData child : myTreeData.getChildren() )
				if ( child instanceof ModifierData)
				{
					isContainingModifier = true;
					break;
				}
			notifyDataChangedListeners();
		}
		return success;
	}

	public boolean removeTerms( List<QueryConceptTreeNodeData> terms )
	{ 
		boolean success = this.myTreeData.removeChildren( terms );
		if ( success )
		{
			isContainingModifier = false;
			for ( QueryConceptTreeNodeData child : myTreeData.getChildren() )
				if ( child instanceof ModifierData)
				{
					isContainingModifier = true;
					break;
				}
			notifyDataChangedListeners();
		}
		return success;
	}
	
	public QueryConceptTreeNodeData	removeTermAt( int index )
	{ 
		QueryConceptTreeNodeData removed = this.myTreeData.removeChildAt( index );
		if ( removed != null )
		{
			isContainingModifier = false;
			for ( QueryConceptTreeNodeData child : myTreeData.getChildren() )
				if ( child instanceof ModifierData)
				{
					isContainingModifier = true;
					break;
				}
			notifyDataChangedListeners();
		}
		return removed;
	}
	
	/* Bulk-add terms and notifyDataChangedListeners*/
	public void setTerms( ArrayList<QueryConceptTreeNodeData> terms )
	{
		this.myTreeData.clearChildren();
		isContainingModifier = false;
		for ( QueryConceptTreeNodeData term : terms )
		{
			this.myTreeData.addChild( term );
			if ( term instanceof ModifierData )
				isContainingModifier = true;
		}
		notifyDataChangedListeners();
	}
	
	public void setGroupBindingPolicyProvider ( IGroupBindingPolicyProvider provider )
	{ this.myGroupBindingPolicyProvider = provider; }
	
	public IGroupBindingPolicyProvider getGroupBindingPolicyProvider()
	{ return this.myGroupBindingPolicyProvider; }
	
	public QueryConceptTreeNodeData getTreeData()
	{ return myTreeData; }

	public String getName()
	{ return myName; }
	
	public void setName( String name )
	{ myName = name; }

	
	public void addDataChangedListener( DataChangedListener list )
	{ myListeners.add( list ); }
	
	public void removeDataChangedListener( DataChangedListener list )
	{ myListeners.remove( list ); }

	public void removeAllDataChangedListeners()
	{ myListeners.clear(); }

	private void notifyDataChangedListeners()
	{
		for ( DataChangedListener list : myListeners )
			list.dataChanged( this );
	}
	/*
	 * Whether the group panel is excluded (legacy terminology)
	 */
	public boolean isExcluded()
	{
		// 'less than' => inverted. 'Equal' is only associated with 0, and that also means less than 1, so => inverted.
		if  ( ( myOperator == UIConst.LESS_THAN ) || ( myOperator == UIConst.EQUAL ) ) 
			return true;
		return false;
	}
	
	/* Wether this Group contains a modifier concept */
	public boolean isContainingModifier()
	{ return isContainingModifier; }

	/* Wether this Group contains a term */	
	public boolean isContainingTerm()
	{ return (this.myTreeData.getChildren().size() != 0);}

	// accuracy seems to be 100 and never changed. We keep it here in case accuracy is user-changeable in the future
	public int getAccuracy()
	{ return this.myAccuracy; }
	
	@Override /* Copyable */
	public Group makeCopy() 
	{		
		return new Group( this );
	}	
}
