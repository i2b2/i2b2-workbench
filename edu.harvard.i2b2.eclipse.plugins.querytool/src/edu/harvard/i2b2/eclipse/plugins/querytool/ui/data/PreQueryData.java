package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import java.util.ArrayList;

public class PreQueryData 
{
	
	private ArrayList<Group> myGroups;
	
	private ArrayList<Event> 				myEvents;
	private ArrayList<TemporalRelationship> myTemporalRelationships;
	private boolean							myIncludeUnreferredEvents;
	
	public PreQueryData( ArrayList<Group> groupData )
	{
		myGroups = groupData;
	}
	
	public PreQueryData( ArrayList<Event> events, ArrayList<TemporalRelationship> relationships, boolean includeUnreferredEvents )
	{
		myEvents 					= events;
		myTemporalRelationships 	= relationships;
		myIncludeUnreferredEvents 	= includeUnreferredEvents;
	}
	
	public ArrayList<Group> getGroupData()
	{ return this.myGroups; }
	
	public ArrayList<Event> getEventData()
	{ return this.myEvents; }
	
	public ArrayList<TemporalRelationship> getTemporalRelationshipData()
	{ return this.myTemporalRelationships; }
	
	public boolean getIncludeUnreferredEvents()
	{ return this.myIncludeUnreferredEvents; }
	
	
}
