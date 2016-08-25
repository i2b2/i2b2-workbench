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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;

public class Event
{
	public static final int		ANONYMOUS_ID		= -1;
	public static int			EVENT_COUNTER 		= 0;
	
	protected boolean areDatesGroupSpecific = true;
	
	protected String			myName		= null;
	protected GregorianCalendar myStartDate	= null;
	protected GregorianCalendar myEndDate	= null;
	protected Integer			myEventID	= null; 	// every Event has an ID number, which is used to create a color for it.
	
	protected ArrayList<Group> 	myGroups;
	
	protected ArrayList<DataChangedListener> myListeners;
	
	/* STATIC methods */
	public static Event makeAnonymouseEvent()	{ return new Event(); }
	public static void resetCounter()			{ EVENT_COUNTER = 0; }

	
	
	protected Event()
	{
		myName		= "";
		myGroups 	= new ArrayList<Group>();
		myListeners	= new ArrayList<DataChangedListener>();
		myEventID 	= ANONYMOUS_ID;
	}
	
	public Event( String name )
	{
		myName		= name;
		myGroups 	= new ArrayList<Group>();
		myListeners	= new ArrayList<DataChangedListener>();
		myEventID 	= EVENT_COUNTER;
		EVENT_COUNTER++;
	}
	
	public String	getName()					{ return this.myName; }
	public void		setName( String name )		{ this.myName = name; }
	
	public ArrayList<Group> getGroups()						{ return this.myGroups; }	
	public void setGroups( ArrayList<Group> groups )		{ this.myGroups = groups; }

	public void addGroup( Group g )							{ this.myGroups.add(g); }	
	public void removeGroupAt( int index )					{ myGroups.remove( index ); }
	public void removeAllGroups()							{ myGroups.clear(); }
	public void addAllGroups( Collection<Group> groups)		{ myGroups.addAll( groups ); }
	
	public boolean 	isUsingGroupSpecificDates()					{ return areDatesGroupSpecific; }
	public void 	setIsUsingGroupSpecificDates( boolean flag ){ areDatesGroupSpecific = flag; }

	public GregorianCalendar 	getStartDate() 				{ return this.myStartDate; }
	public void		setStartDate( GregorianCalendar cal )	{ this.myStartDate = cal; }
	
	public GregorianCalendar 	getEndDate()				{ return this.myEndDate; }
	public void		setEndDate( GregorianCalendar cal )		{ this.myEndDate = cal; }
	
	public int getEventID()									{ return this.myEventID; }
	
	public void addListener( DataChangedListener listener )
	{ myListeners.add( listener ); }
	
	public void notifyListeners()
	{
		for ( DataChangedListener listener : myListeners )
			listener.dataChanged( this );
	}
	
	public boolean hasContent()
	{
		for ( Group group : myGroups )
			if ( group.isContainingTerm() )
				return true;
		return false;
	}
	
}
