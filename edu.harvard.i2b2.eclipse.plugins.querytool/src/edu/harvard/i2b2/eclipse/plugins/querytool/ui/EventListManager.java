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
import org.eclipse.swt.widgets.Label;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IEventDataProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.EventSelectedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.ExpandContractListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class EventListManager implements DataChangedListener, IEventDataProvider, UIManagerContentChangedListener
{
	public static final int 	EVENT_SPACING 	= UIConst.EVENT_LABEL_MARGIN;
	private static final int	RIGHT_MARGIN	= 1;
	private static final int	LEFT_MARGIN		= EVENT_SPACING;
	
	private EventSelectedDelegator myDelegator;
	private Composite myParent;
	private ExpandContractListener myEcListener;
	
	private ArrayList<EventExpandPanel> myEventPanels;
	
	private ArrayList<UIManagerContentChangedListener> myUIContentListeners;
	
	protected ArrayList<DataChangedListener> myDataChangedListeners;
	
	
	public EventListManager( Composite parent, ExpandContractListener ecListener, EventSelectedDelegator delegator )
	{
		myEventPanels 				= new ArrayList<EventExpandPanel>();
		myUIContentListeners		= new ArrayList<UIManagerContentChangedListener>();
		myDataChangedListeners		= new ArrayList<DataChangedListener>();
		myDelegator = delegator;
		myParent = parent;
		myEcListener = ecListener;
	}
	
	public void addUIContentListener( UIManagerContentChangedListener listener )
	{ myUIContentListeners.add( listener ); }
	public boolean removeUICOntentListener( UIManagerContentChangedListener listener )
	{ return myUIContentListeners.remove( listener ); }
	public void removeUIContentAllListeners()
	{ myUIContentListeners = new ArrayList<UIManagerContentChangedListener>(); }
	
	public int getNumberOfPanels()
	{ return myEventPanels.size(); }
	
	public ArrayList<Event> makeEventData()
	{
		ArrayList<Event> events = new ArrayList<Event>( myEventPanels.size() );
		for ( EventExpandPanel panel : myEventPanels )
			events.add( panel.getEvent() );
		return events;
	}
	
	public Event getEvent( String eventName )
	{
		for ( EventExpandPanel panel : myEventPanels )
			if ( panel.getEvent().getName().equals( eventName ))
				return panel.getEvent();
		return null;
	}

	private void notifyContentChangeListeners() // notify EventListControlPanel
	{
		for ( int i = 0; i < myUIContentListeners.size(); i++ )
			myUIContentListeners.get(i).groupManagerContentChanged( this );
	}

	
	// all panel layout data are produced here
	protected FormData makeLayoutData( Control previousControl, EventExpandPanel targetPanel )
	{
		FormData fd = null;
		if ( previousControl == null )
			fd = FormDataMaker.makeFormData( 0, EVENT_SPACING, (Integer)null, 0, 0, LEFT_MARGIN, 100, -RIGHT_MARGIN );
		else
			fd = FormDataMaker.makeFormData( previousControl, EVENT_SPACING, (Integer)null, 0, 0, LEFT_MARGIN, 100, -RIGHT_MARGIN );
		
		if ( targetPanel.isExpanded() )
			fd.height = targetPanel.getPreferredExpandedHeight();
		else
			fd.height = targetPanel.getPreferredContractedHeight();
		return fd;
	}
	
	public void addPanels( int number )
	{
		Control previousControl = null;
		if ( myEventPanels.size() != 0)
			previousControl = myEventPanels.get( myEventPanels.size()-1 );
		int oldSize = myEventPanels.size();
		for ( int i = 0; i < number; i++)
		{
			EventExpandPanel panel = new EventExpandPanel( myParent, SWT.NONE, UIConst.EVENT + " " + (oldSize + 1 + i) , this );
			panel.addUIContentListener( this );
			panel.addExpandControlListener( this.myEcListener );
			panel.setLayoutData( makeLayoutData( previousControl, panel) );
			((FormData)panel.getLayoutData()).height = panel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y; // set the formdata height because EventExpandBar works off that
			previousControl = panel;
			myEventPanels.add( panel );
		}
		notifyContentChangeListeners();
	}

	public void addPanels( ArrayList<Event> events )
	{
		Control previousControl = null;
		if ( myEventPanels.size() != 0)
			previousControl = myEventPanels.get( myEventPanels.size()-1 );
		for ( int i = 0; i < events.size(); i++)
		{
			Event event = events.get(i);
			EventExpandPanel panel = new EventExpandPanel( myParent, SWT.NONE, this ); // create an EventPanel without Event
			panel.addUIContentListener( this );
			panel.addExpandControlListener( this.myEcListener );
			panel.setLayoutData( makeLayoutData( previousControl, panel) );
			((FormData)panel.getLayoutData()).height = panel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y; // set the formdata height because EventExpandBar works off that
			previousControl = panel;
			myEventPanels.add( panel );
			panel.setEvent( event ); // set event
		}
		notifyContentChangeListeners();
	}

	// remove specified panel. Rename remaining panels
	public void removePanel( EventExpandPanel l )
	{
		int index = -1;
		for ( int i = 0; i < myEventPanels.size(); i++)
		{
			if ( myEventPanels.get(i) == l )
			{
				index = i;
				break;
			}
		}
		if ( index == -1)
			return; // panel p not found, do nothing
		myEventPanels.remove( index );

		Control previousControl = null;
		if ( index > 0 )
			previousControl = myEventPanels.get( index-1 );
		// relayout all grouppanels after and including index
		for ( int i = index; i < myEventPanels.size(); i++)
		{
			EventExpandPanel panel = myEventPanels.get(i);
			panel.getEvent().setName( UIConst.EVENT + " " + (i + 1) ); // rename Events so they always start with Event 1 and are always consecutive.
			panel.setLayoutData( makeLayoutData( previousControl, panel ) );
			panel.autoSetPanelName(); // set new name and redraw text
			previousControl = panel;			
		}
		l.dispose();	// free OS resources
		notifyContentChangeListeners();		
	}

	// remove specified panel. Rename remaining panels
	public void removeAllPanels()
	{
		// dispose all and clear myEventPanels
		for ( int i = 0; i < myEventPanels.size(); i++)
			myEventPanels.get(i).dispose();
		myEventPanels.clear();
		notifyContentChangeListeners();		
	}
	
	public EventExpandPanel getLastPanel()
	{
		if ( myEventPanels.isEmpty() )
			return null;
		return myEventPanels.get( myEventPanels.size()-1 );
	}
	
	public void addDataChangedListener( DataChangedListener list )
	{ this.myDataChangedListeners.add( list ); }
		
	public boolean removeDataChangedListener( DataChangedListener list )
	{ return this.myDataChangedListeners.remove( list ); }


	/*
	 * Goes through all events and them name Event 1, Event 2, in a non-broken order
	 */
	public HashMap<String, String> resetEventNames()
	{
		String oldName = null;
		String newName = null;
		HashMap<String, String> nameMap = new HashMap<String, String>();
		
		int i = 1;		
		for ( EventExpandPanel ePanel : myEventPanels )
		{
			oldName 	= ePanel.getEvent().getName();
			newName	= UIConst.EVENT + " " + i;
			if ( !oldName.equals( newName ) )
				nameMap.put( oldName, newName );
			ePanel.getEvent().setName( newName );
			ePanel.autoSetPanelName();	// make name change show up in UI
			i++;
		}
		return nameMap;
	}
	
	// propagate to EventListeControlPanel
	private void notifyDataChangedListeners()
	{
		for ( DataChangedListener list : myDataChangedListeners)
			list.dataChanged( this );
	}
	
	@Override /* DataChangedListener method */
	public void dataChanged(Object source) 
	{ 
		notifyDataChangedListeners(); // propagate to EventListeControlPanel
	}

	
	@Override /* IEventDataProvider */
	public Event getEventByName( String name )
	{
		for ( int i = 0; i < myEventPanels.size(); i++)
			if ( myEventPanels.get(i).getEvent().getName().equals( name ) )
				return myEventPanels.get(i).getEvent();
		return null;
	}
	
	@Override /* IEventDataProvider */
	public ArrayList<Event> getEvents() 
	{ return makeEventData(); }

	
	@Override /* UIManagerContentChangedListener methods to listen for Group addition/removal for the Groups in the Events*/
	public void groupManagerContentChanged(Object object) 
	{ 
		notifyContentChangeListeners(); // all we do is to propagate it to our listeners 
	}

}
