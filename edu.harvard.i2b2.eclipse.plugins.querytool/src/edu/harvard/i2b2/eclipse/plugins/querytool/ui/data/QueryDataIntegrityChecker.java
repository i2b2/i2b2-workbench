package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import java.util.ArrayList;
import java.util.HashSet;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class QueryDataIntegrityChecker 
{
	public static final String CANNOT_SUBMIT_QUERY = "Cannot submit query." ;
	
	/*
	 * Singleton methods
	 */
	private static QueryDataIntegrityChecker myInstance = null;
	
	public static QueryDataIntegrityChecker getInstance()
	{
		if ( myInstance == null )
			myInstance = new QueryDataIntegrityChecker();
		return myInstance;
	}
	
	private QueryDataIntegrityChecker()
	{}
	
	
	public TaskResult checkQueryData( final String queryMode, IPreQueryDataProvider basicPreQueryDataProvider, IPreQueryDataProvider temporalPreQueryDataProvider )
	{
		if ( queryMode.equals( UIConst.NON_TEMPORAL_QUERY_MODE ) ) // normal query
		{
			ArrayList<Group> groups = basicPreQueryDataProvider.getPreQueryData().getGroupData();
			boolean hasContent = hasGroupsContent(groups);
			// failuer
			if (!hasContent)
				return new TaskResult( null, CANNOT_SUBMIT_QUERY, "All Groups are empty (no content in them)");
			// success
			return new TaskResult();
		}
		else if ( queryMode.equals( UIConst.TEMPORAL_QUERY_MODE ) ) // temporal query
			return isTemporalQueryWellFormed( temporalPreQueryDataProvider );
		else if ( queryMode.equals( UIConst.GET_EVERYONE ) ) // GET_EVERYONE queries always fulfills integrity check
			return new TaskResult();
		else
		{
			String msg = "QueryDataIntegrityChecker.checkQueryData(...): '" + queryMode + "' is not a valid query mode.";
			System.err.println( msg );
			assert false : msg;
		}
		return new TaskResult( null, CANNOT_SUBMIT_QUERY, "Query mode '" + queryMode + "' is not a valid query mode. (query mode unrecognized).");
	}
	
	public TaskResult isTemporalQueryWellFormed( IPreQueryDataProvider temporalPreQueryDataProvider )
	{
		PreQueryData temporalPQD = temporalPreQueryDataProvider.getPreQueryData();
		ArrayList<Event> events = temporalPQD.getEventData();
		ArrayList<TemporalRelationship> relationships = temporalPQD.getTemporalRelationshipData();
		if ( temporalPQD.getIncludeUnreferredEvents() )
		{
			boolean hasContent = false;
			for ( Event event : events )
			{
				if ( hasGroupsContent(event.getGroups()) )
				{
					hasContent = true;
					break;
				}
			}
			if (!hasContent)
				return new TaskResult( null, CANNOT_SUBMIT_QUERY, "All Events are empty (you need to drag terms into them)");
			// success
			return new TaskResult();
		}
		else
		{
			if ( relationships.isEmpty() )
				return new TaskResult( null, CANNOT_SUBMIT_QUERY, "A temporal query requires at least one Temporal Relationship (or you forgot to deselect the '" + UIConst.ONLY_EVENTS_USED_IN_RELATIONSHIPS + "' box.");		
			HashSet<Event> includedEvents = new HashSet<Event>();
			for ( TemporalRelationship tr: relationships )
			{
				includedEvents.add( tr.getTopEvent() );
				includedEvents.add( tr.getBotEvent() );
			}
			
			for ( Event event : includedEvents )
			{
				if ( event == null)
					return new TaskResult( null, CANNOT_SUBMIT_QUERY, "A Temporal Relationships is missing at least one Event.");
				if ( !hasGroupsContent(event.getGroups()) )
					return new TaskResult( null, CANNOT_SUBMIT_QUERY, "The Event '" + event.getName() + "' required by a Temporal Relationships has no content.");
			}
			// success
			return new TaskResult();
		}
	}
	
	
	
	public static boolean hasGroupsContent( ArrayList<Group> groups )
	{
		boolean hasContent = false;
		for ( Group g : groups )
			if ( g.isContainingTerm() )
			{
				hasContent = true;
				break;
			}
		return hasContent;
	}
	
}
