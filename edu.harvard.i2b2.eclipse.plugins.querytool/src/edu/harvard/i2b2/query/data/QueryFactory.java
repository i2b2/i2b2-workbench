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

package edu.harvard.i2b2.query.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IPreQueryDataProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IQueryTimingProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PreQueryData;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Query;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.QueryResultObtainedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class QueryFactory 
{
	public static final int DEFAULT_QUERY_NAME_LENGTH = 15;
	
	
	
	private static QueryFactory myInstance;
	
	public static QueryFactory getInstance()
	{
		if ( myInstance == null )
			myInstance = new QueryFactory();
		return myInstance;
	}
	
	
	private QueryFactory()
	{}
	
	
	public Query makeQuery( String preferredQueryName, String queryMode, ArrayList<String> analysisTypeNames, boolean isUsingGraphicalAnalysis, boolean isUsingTimeline, IPreQueryDataProvider basicPreQueryDataProvider, IPreQueryDataProvider temporalPreQueryDataProvider, IQueryTimingProvider queryTimingProvider, QueryResultObtainedDelegator delegator )
	{
		String queryName 	= preferredQueryName;
		String queryXML 	= null;		
		if ( queryMode.equals( UIConst.NON_TEMPORAL_QUERY_MODE ) ) // normal query
		{
			if ( queryName == null )
				queryName 	= makeNormalQueryName( basicPreQueryDataProvider.getPreQueryData().getGroupData() );
			queryXML 	= Query2XML.writeQueryXML( queryName, analysisTypeNames, basicPreQueryDataProvider.getPreQueryData().getGroupData() );
		}
		else if ( queryMode.equals( UIConst.TEMPORAL_QUERY_MODE ) ) // temporal query
		{
			PreQueryData temporalPQD = temporalPreQueryDataProvider.getPreQueryData();
			if ( queryName == null )
				queryName 	= makeTemporalQueryName( temporalPQD.getEventData() );
			queryXML 	= Query2XML.writeQueryXML( queryName, analysisTypeNames, basicPreQueryDataProvider.getPreQueryData().getGroupData(), temporalPQD.getEventData(), temporalPQD.getTemporalRelationshipData(), temporalPQD.getIncludeUnreferredEvents(), queryTimingProvider.getQueryTimingID() );
		}
		else if ( queryMode.equals( UIConst.GET_EVERYONE ) )
		{
			queryName 	= "Get Everyone @" + getTimeString();
			queryXML 	= Query2XML.makeGetEveryoneQueryXML( queryName, DataConst.ANY, analysisTypeNames );
		}
		else
			assert false : "QueryFactory.makeQuery(...): '" + queryMode + "' is not a valid query mode.";

		return new Query( queryName, queryMode, queryXML, isUsingGraphicalAnalysis, isUsingTimeline, delegator );
	}


	public static String getTimeString()
	{
		GregorianCalendar now = new GregorianCalendar();
		return padZeros((now.get( Calendar.HOUR_OF_DAY ))) + ":" + 
			   padZeros((now.get( Calendar.MINUTE ))) + ":" + 
			   padZeros((now.get( Calendar.SECOND )));
	}

	public static String getDayString()
	{
		GregorianCalendar now = new GregorianCalendar();
		return padZeros(now.get(Calendar.MONTH) + 1) + "-" +
			   padZeros(now.get(Calendar.DAY_OF_MONTH)) + "-" +
			   padZeros(now.get(Calendar.YEAR));
	}

	public static String makeTemporalQueryName( ArrayList<Event> events )
	{
		ArrayList<Event> eventsWithContent = new ArrayList<Event>();
		for ( Event ev : events )
			eventsWithContent.add( ev );		
		Event event = eventsWithContent.get(0);		
		StringBuffer name = getNameFromGroups( event.getGroups() );		
		return name.toString();
	}
	
	// decorate a temporal query name with markers
	public static String decorateTemporalQueryName( String queryName, IPreQueryDataProvider temporalPreQueryDataProvider )
	{
		StringBuffer nameBuffer = new StringBuffer( queryName );
		if ( QueryFactory.hasTemporalQueryConstraint( temporalPreQueryDataProvider.getPreQueryData().getEventData() ) )
			nameBuffer.insert( 0 , "(t+) " );
		else
			nameBuffer.insert( 0 , "(t) " );
		return nameBuffer.toString();
	}

	public static String makeTemporalQueryNameWithMarkers( ArrayList<Event> events )
	{
		ArrayList<Event> eventsWithContent = new ArrayList<Event>();
		for ( Event ev : events )
			eventsWithContent.add( ev );		
		Event event = eventsWithContent.get(0);		
		StringBuffer name = getNameFromGroups( event.getGroups() );
		if ( hasTemporalQueryConstraint( eventsWithContent ) )
			name.insert( 0 , "(t+) " );
		else
			name.insert( 0 , "(t) " );
		return name.toString();
	}
	
	public static String makeNormalQueryName( ArrayList<Group> groups )
	{
		StringBuffer name = getNameFromGroups( groups );
		
		if ( hasNormalQueryConstraint(groups) )
			name.insert( 0 , "(+) " );
		
		return name.toString();
	}

	
	public static StringBuffer getNameFromGroups( ArrayList<Group> groups )
	{
		StringBuffer name = new StringBuffer();
		int numberOfChar = DEFAULT_QUERY_NAME_LENGTH / groups.size();
		
		for (int i = 0; i < groups.size(); i++) 
		{	
			if (i < 3) 
			{
				Group group = groups.get(i);
				if ( !group.isContainingTerm() ) // don't include empty groups
					break;
				ArrayList<QueryConceptTreeNodeData> concepts = group.getTreeData().getChildren();
				if (!name.toString().trim().isEmpty()) 
					name.append("-");
				
				if ( concepts.get(0).name().length() >= numberOfChar) 
					name.append( concepts.get(0).name().substring(0, numberOfChar) );
				else 
					name.append( concepts.get(0).name() );
			}
		}
		name.append( "@" ).append( getTimeString() );
		return name;
	}
	
	/* Look into each Group in each Event to see if any Constraints have been set */
	public static boolean hasTemporalQueryConstraint( ArrayList<Event> events )
	{
		for ( Event event : events ) 
			if ( hasNormalQueryConstraint( event.getGroups() ) )
				return true;
		return false;
	}
	
	/* Look into Groups to see if any of them has a constraint */
	public static boolean hasNormalQueryConstraint( ArrayList<Group> groups )
	{
		for (int i = 0; i < groups.size(); i++) 
		{
			Group group = groups.get(i);
			if ( group.getStartDate() != null || group.getEndDate() != null || group.getNumber() != 0 || group.getOperator() != UIConst.GREATER_THAN ) 
				return true;
		}
		return false;
	}
	
	
	
	public static String padZeros(int number) 
	{
		String result = new Integer(number).toString();
		if (number < 10 && number >= 0)
			result = "0" + result;
		return result;
	}

}
