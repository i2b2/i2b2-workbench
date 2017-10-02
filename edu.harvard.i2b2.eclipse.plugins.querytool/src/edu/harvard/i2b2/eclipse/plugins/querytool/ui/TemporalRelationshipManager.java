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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IEventDataProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.TemporalRelationship;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class TemporalRelationshipManager implements IEventDataProvider
{
	public static final int PANEL_SPACING = UIConst.EVENT_LABEL_MARGIN;
	
	private Composite myParent;
	private ArrayList<TemporalRelationshipPanel> myPanels;
	private IEventDataProvider myEventDataProvider;
	
	private ArrayList<DataChangedListener> 				myDataChangedListeners;	// listen for temporal relationship modifications
	private ArrayList<UIManagerContentChangedListener> 	myUIContentListeners;	// listen for adding/removing temporal relationships
		
	private ArrayList<Event> myCachedEvents; // a cache of current Events. Updated whenever an Event is created/edited and then saved.

	
	public TemporalRelationshipManager( Composite parent, IEventDataProvider eventDataProvider )
	{
		myPanels 				= new ArrayList<TemporalRelationshipPanel>();
		myDataChangedListeners	= new ArrayList<DataChangedListener>();
		myUIContentListeners	= new ArrayList<UIManagerContentChangedListener>();
		myEventDataProvider	= eventDataProvider; // allows obtaining Events by name
		myCachedEvents		= myEventDataProvider.getEvents();	// get the freshest copy of Events at startup as cache
		myParent = parent;
	}

	/* Deal with UIContentChangedListeners */
	public void addUIContentListener( UIManagerContentChangedListener listener )
	{ myUIContentListeners.add( listener ); }
	public boolean removeListener( UIManagerContentChangedListener listener )
	{ return myUIContentListeners.remove( listener ); }
	public void removeAllListeners()
	{ myUIContentListeners = new ArrayList<UIManagerContentChangedListener>(); }	
	private void notifyContentChangeListeners() // listeners include: DefineTemporalRelationshipPanel
	{
		for ( int i = 0; i < myUIContentListeners.size(); i++ )
			myUIContentListeners.get(i).groupManagerContentChanged( this );
	}

	/* Deal with DataChangedListeners */
	public void addDataChangedListener( DataChangedListener list )
	{ this.myDataChangedListeners.add( list ); }		
	public boolean removeDataChangedListener( DataChangedListener list )
	{ return this.myDataChangedListeners.remove( list ); }
	// notify DefineTemporalRelationshipPanel that a Temporal Relationship's content has changed
	private void notifyDataChangedListeners( TemporalRelationshipPanel source )
	{
		for ( DataChangedListener list : myDataChangedListeners)
			list.dataChanged( this );
	}
	public void temporalRelationshipEdited( TemporalRelationshipPanel source )
	{ notifyDataChangedListeners(source); }
	
	
	// given a list of events, this method updates all the event combo widgets in each TemporalRelationshipPanel
	public void updateEventListInPanels( ArrayList<Event> events )
	{
		for ( TemporalRelationshipPanel panel : myPanels )
		{
			panel.updateEventList( events );
			panel.autoUpdateColorLabels();
		}
		myCachedEvents = events;		// save a copy for later (so when we create new TemporalRelasionshipPanels, we don't need to fetch for current events)
	}

	// given a list of events, this method updates all the event combo widgets in each TemporalRelationshipPanel
	public void renameEvents( HashMap<String, String> nameMap )
	{
		for ( TemporalRelationshipPanel panel : myPanels )
			panel.renameEvents( nameMap );
	}

	
	// all panel layout data are produced here
	protected FormData makeLayoutData( Control previousControl )
	{
		if ( previousControl == null )
			return FormDataMaker.makeFormData( 0, PANEL_SPACING, (Integer)null, 0, 0, PANEL_SPACING, 100, -PANEL_SPACING );
		else
			return FormDataMaker.makeFormData( previousControl, PANEL_SPACING, (Integer)null, 0, 0, PANEL_SPACING, 100, -PANEL_SPACING );
	}
	
	public void addPanels( int number )
	{
		Control previousControl = null;
		if ( myPanels.size() != 0)
			previousControl = myPanels.get( myPanels.size()-1 );
		for ( int i = 0; i < number; i++)
		{
			TemporalRelationshipPanel panel = new TemporalRelationshipPanel( myParent, SWT.BORDER, this );
			panel.setLayoutData( makeLayoutData( previousControl) );
			previousControl = panel;
			myPanels.add( panel );
		}		
		notifyContentChangeListeners();
	}
	
	public void addPanels( ArrayList<TemporalRelationship> temporalRelationships )
	{
		Control previousControl = null;
		if ( myPanels.size() != 0)
			previousControl = myPanels.get( myPanels.size()-1 );
		for ( TemporalRelationship tr : temporalRelationships )
		{
			TemporalRelationshipPanel panel = new TemporalRelationshipPanel( myParent, SWT.BORDER, this, tr );
			panel.setLayoutData( makeLayoutData( previousControl) );
			previousControl = panel;
			myPanels.add( panel );
		}		
		notifyContentChangeListeners();
	}

	
	public ArrayList<Event> getCachedEvents()
	{ return this.myCachedEvents; }

	
	// remove specified panel. Rename remaining panels
	public void removePanel( TemporalRelationshipPanel target )
	{
		int index = -1;
		for ( int i = 0; i < myPanels.size(); i++)
		{
			if ( myPanels.get(i) == target )
			{
				index = i;
				break;
			}
		}
		if ( index == -1)
			return; // panel p not found, do nothing		
		myPanels.remove( index );

		Control previousControl = null;
		if ( index > 0 )
			previousControl = myPanels.get( index-1 );
		// relayout all grouppanels after and including index
		for ( int i = index; i < myPanels.size(); i++)
		{
			TemporalRelationshipPanel panel = myPanels.get(i);
			panel.setLayoutData( makeLayoutData( previousControl) );
			previousControl = panel;
		}
		notifyContentChangeListeners();		
		target.dispose();	// free OS resources
	}
	
	// remove all panels
	public void removeAllPanels()
	{
		ArrayList<TemporalRelationshipPanel> toRemove = new ArrayList<TemporalRelationshipPanel>();
		for ( int i = 0; i < myPanels.size(); i++)
			toRemove.add( myPanels.get(i) );
		if ( toRemove.size() == 0 )
			return;
		myPanels.removeAll( toRemove );
		notifyContentChangeListeners();
		for ( int i = 0; i < toRemove.size(); i++ ) // free OS resources
			toRemove.get(i).dispose();
	}

	// remove all panels that do not contain terms. Rename remaining panels
	public void consolidatePanels()
	{		
		ArrayList<TemporalRelationshipPanel> toRemove = new ArrayList<TemporalRelationshipPanel>();
		for ( int i = 0; i < myPanels.size(); i++)
			if ( myPanels.get(i).isEmpty() )
				toRemove.add( myPanels.get(i) );
		if ( toRemove.size() == 0 )
			return;
		myPanels.removeAll( toRemove );
		Control previousControl = null;
		for ( int i = 0; i < myPanels.size(); i++)
		{
			TemporalRelationshipPanel panel = myPanels.get(i);
			panel.setLayoutData( makeLayoutData( previousControl) );
			previousControl = panel;
			myPanels.add( panel );
		}
		notifyContentChangeListeners();
		for ( int i = 0; i < toRemove.size(); i++ ) // free OS resources
			toRemove.get(i).dispose();
	}

	public int getNumberOfPanels()
	{ return myPanels.size(); }
	
	public TemporalRelationshipPanel getLastPanel()
	{
		if ( myPanels.isEmpty() )
			return null;
		return myPanels.get( myPanels.size()-1 );
	}
	
	public void updateTemporalRelationshipPanels()
	{
		for ( TemporalRelationshipPanel panel : this.myPanels )
			panel.setDataValuesFromUI();
	}
	
	public ArrayList<TemporalRelationship> getTemporalRelationshipData()
	{
		ArrayList<TemporalRelationship> trs = new ArrayList<TemporalRelationship>();
		for ( TemporalRelationshipPanel panel : this.myPanels )
			trs.add( panel.getTemporalRelationship() );
		return trs;
	}
	
	@Override /* IEventDataProvider method */
	public Event getEventByName(String name) 
	{ return this.myEventDataProvider.getEventByName( name ); }

	@Override /* IEventDataProvider method */
	public ArrayList<Event> getEvents() 
	{ return this.myEventDataProvider.getEvents(); }


}
