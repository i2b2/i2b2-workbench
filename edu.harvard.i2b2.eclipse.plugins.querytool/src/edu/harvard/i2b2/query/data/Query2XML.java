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

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import edu.harvard.i2b2.common.util.jaxb.DTOFactory;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ApplicationType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.FacilityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageControlIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ProcessingIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.SecurityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainDateType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.MetadataxmlValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PanelType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmQryHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmRequestTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryAggregateOperatorType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryConstraintType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionRequestType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryJoinColumnType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryJoinType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryModeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryOperatorType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QuerySpanConstraintType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultOutputOptionListType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultOutputOptionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.UserType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByDate;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByModifier;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.query.utils.Messages;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.OrderedDuration;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.TemporalRelationship;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.jaxb.utils.QueryJAXBUtil;

public class Query2XML implements DataConst
{
	// Time units in the format the server accepts
	public static final String [] TIME_UNITS = {"SECOND", "MINUTE", "HOUR", "DAY", "MONTH", "YEAR"};
	
	public static final String QUERY_TYPE_EVENET = "EVENT";
	
	/*======================================================================================================================================================================================
	 * Generate message for Non-Temporal query
	 */
	public static String writeQueryXML( String queryName, ArrayList<String> resultOptions, ArrayList<Group> groups )
	{
		
		ResultOutputOptionListType resultOutputOptionListType = new ResultOutputOptionListType();
		// loop thru the options
		for (int i = 0; i < resultOptions.size(); i++) 
		{
			ResultOutputOptionType resultOutputOptionType = new ResultOutputOptionType();
			resultOutputOptionType.setName(resultOptions.get(i));
			resultOutputOptionType.setPriorityIndex(new Integer(i + 1));
			resultOutputOptionListType.getResultOutput().add(resultOutputOptionType);
		}

		QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();
		
		QueryDefinitionType queryDefinitionType = makeQueryDefinitionType( groups );
		String queryTiming = makeQueryTimingFromPanelTimings( queryDefinitionType.getPanel() );
		
		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();

		UserType userType = new UserType();
		userType.setLogin(UserInfoBean.getInstance().getUserName());
		userType.setGroup(System.getProperty("projectName"));
		userType.setValue(UserInfoBean.getInstance().getUserName());

		headerType.setUser(userType);
		headerType.setRequestType(PsmRequestTypeType.CRC_QRY_RUN_QUERY_INSTANCE_FROM_QUERY_DEFINITION);

		headerType.setQueryMode(QueryModeType.OPTIMIZE_WITHOUT_TEMP_TABLE);

		queryDefinitionType.setQueryName(queryName);
		queryDefinitionType.setQueryTiming( queryTiming );
		queryDefinitionRequestType.setQueryDefinition(queryDefinitionType);
		queryDefinitionRequestType.setResultOutputList(resultOutputOptionListType);

		RequestHeaderType requestHeader = new RequestHeaderType();

		if (System.getProperty("QueryToolMaxWaitingTime") != null) 
			requestHeader.setResultWaittimeMs((Integer.parseInt(System.getProperty("QueryToolMaxWaitingTime"))) * 1000);
		else 
			requestHeader.setResultWaittimeMs(180000);

		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(queryDefinitionRequestType));

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try 
		{
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil.marshallerWithCDATA(of.createRequest(requestMessageType), strWriter, new String[] {"value_constraint"});
		} 
		catch (Exception e) 
		{ e.printStackTrace(); }
		
		return strWriter.toString();
	}

	/* Iterate over all panels, return the strictest PanelTiming as QueryTiming. The order of strictness is SAME_INSTANCE > SAME_VISIT > ANY */
	private static String makeQueryTimingFromPanelTimings( List<PanelType> panels )
	{
		String queryTiming = ANY;		
		for ( PanelType panel : panels )
		{
			String timing = panel.getPanelTiming();
			if ( timing.equals(SAME_VISIT) && queryTiming.equals(ANY))
				queryTiming = SAME_VISIT;
			if ( timing.equals(SAME_INSTANCE))
			{
				queryTiming = SAME_INSTANCE;
				break;
			}
		}
		return queryTiming;
	}
	
	/* Create a QueryDefinitionTypefrom a list of Groups */
	private static QueryDefinitionType makeQueryDefinitionType( ArrayList<Group> groups )
	{
		QueryDefinitionType queryDefinitionType = new QueryDefinitionType();
		for (int i = 0; i < groups.size(); i++) // process each Group
		{
			Group group = groups.get(i);
			ArrayList<QueryConceptTreeNodeData> nodelist = group.getTerms();
			if ((nodelist != null) && (nodelist.size() > 0)) 
			{
				PanelType panelType = new PanelType();
				panelType.setInvert( group.isExcluded() ? 1 : 0);				
				panelType.setTotalItemOccurrences( makeTotalItemOccurrences( group ) );
				panelType.setPanelAccuracyScale( group.getAccuracy() );
				panelType.setPanelNumber(i + 1);
				panelType.setPanelTiming( mapToGroupTiming(group.getBinding()) );

				// Add date constraints to Panel even though it's replicated for each Term.
				//   The date constraints here are currently not used by the query processor (2013.02.13) 
				if ( group.getStartDate() != null )
				{
					ConstrainDateType panelStartDate = new ConstrainDateType();
					panelStartDate.setValue( makeXMLGregorianCalendar( group.getStartDate(), false ) );
					panelType.setPanelDateFrom( panelStartDate );
				}
				if ( group.getEndDate() != null )
				{
					ConstrainDateType panelStartDate = new ConstrainDateType();
					panelStartDate.setValue( makeXMLGregorianCalendar( group.getEndDate(), true ) );
					panelType.setPanelDateTo( panelStartDate );
				}

				
				for (int j = 0; j < nodelist.size(); j++) // process each QueryConceptTreeNodeData in a Group
					panelType.getItem().add( makeItemTypeWithoutMetadata(group, nodelist.get(j)) );
				queryDefinitionType.getPanel().add(panelType);
			}
		}
		return queryDefinitionType;
	}

	/* Make a PanelType.TotalItemOccurrences from a Group */
	public static PanelType.TotalItemOccurrences makeTotalItemOccurrences( Group group )
	{
		PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();		
		totalOccurrences.setValue( group.getNumber()+1 );  	// > 0 is >=1
		if ( group.isExcluded() )
			totalOccurrences.setValue( group.getNumber() );	// < 2 is <= 1
		if ( group.getOperator() == UIConst.EQUAL ) // '=' means less than 1
			totalOccurrences.setValue( 1 );
		return totalOccurrences;
	}
	
	/* Map DataConst.GroupBindinig to a String value to be used in XML */
	public static String mapToGroupTiming( DataConst.GroupBinding binding )
	{
		if ( binding == GroupBinding.BY_PATIENT)
			return ANY;
		else if ( binding == GroupBinding.BY_ENCOUNTER)
			return SAME_VISIT;
		else if ( binding == GroupBinding.BY_OBSERVATION)
			return SAME_INSTANCE;
		assert false : "Query2XML.mapToGroupTiming(): binding '" + binding +"' is not recognized.";
		return null;
	}
	
	/* Make an ItemType from a list of terms from a Group*/
	public static ItemType makeItemType( Group group, QueryConceptTreeNodeData node )
	{
		ItemType itemType = new ItemType();
		// checking for special items, patient, encounter set or prevQuery
		if (   node.name().indexOf("Patient Set") >= 0
			|| node.name().indexOf("Encounter Set") >= 0
			|| node.name().indexOf("PrevQuery") >= 0
			|| node.name().indexOf("PATIENT") >= 0) 
		{
			itemType.setItemKey(node.fullname());
			itemType.setItemName(node.name());
			itemType.setTooltip(node.tooltip());
		} 
		else 
		{
			itemType.setItemKey(node.fullname());
			itemType.setItemName(node.name());
			itemType.setTooltip(node.tooltip());
			itemType.setHlevel(Integer.parseInt(node.hlevel()));
			itemType.setClazz("ENC");
			itemType.setItemIcon(node.visualAttribute());
		}
		// handle time constraints
		if ( group.getStartDate() != null || group.getEndDate() != null ) 
		{
			ConstrainByDate timeConstrain = makeTimeConstrain( group.getStartDate(), group.getEndDate() );
			itemType.getConstrainByDate().add(timeConstrain);
		}
		// handle modifier
		if (node.isModifier()) 
		{
			ConstrainByModifier modifierConstraint = makeModifierConstraint( ((ModifierData)node) );
			itemType.setConstrainByModifier(modifierConstraint);
		}
		else if (!node.valuePropertyData().noValue()) // handle value restriction that is NOT on a modifier 
		{
			ConstrainByValue valueConstrain = node.valuePropertyData().makeValueConstraint();
			itemType.getConstrainByValue().add(valueConstrain);
		}
		
		// (mis)using metadataXML, add originalXML. Should have its own entry
		MetadataxmlValueType metadata = new MetadataxmlValueType();
		metadata.getContent().add( node.getOriginalXML() );		
		itemType.setMetadataxml( metadata );
		
		//metaDataXM
		return itemType;
	}

	/* Similar to makeItemType(), but this one is used when generating query XML and does not include metadataxml so query text is cleaner*/
	public static ItemType makeItemTypeWithoutMetadata( Group group, QueryConceptTreeNodeData node )
	{
		ItemType itemType = new ItemType();
		// checking for special items, patient, encounter set or prevQuery
		if (   node.name().indexOf("Patient Set") >= 0
			|| node.name().indexOf("Encounter Set") >= 0
			|| node.name().indexOf("PrevQuery") >= 0
			|| node.name().indexOf("PATIENT") >= 0) 
		{
			itemType.setItemKey(node.fullname());
			itemType.setItemName(node.name());
			itemType.setTooltip(node.tooltip());
		} 
		else 
		{
			itemType.setItemKey(node.fullname());
			itemType.setItemName(node.name());
			itemType.setTooltip(node.tooltip());
			itemType.setHlevel(Integer.parseInt(node.hlevel()));
			itemType.setClazz("ENC");
			itemType.setItemIcon(node.visualAttribute());
		}
		// handle time constraints
		if ( group.getStartDate() != null || group.getEndDate() != null ) 
		{
			ConstrainByDate timeConstrain = makeTimeConstrain( group.getStartDate(), group.getEndDate() );
			itemType.getConstrainByDate().add(timeConstrain);
		}
		// handle modifier
		if (node.isModifier()) 
		{
			ConstrainByModifier modifierConstraint = makeModifierConstraint( ((ModifierData)node) );
			itemType.setConstrainByModifier(modifierConstraint);
		}
		else if (!node.valuePropertyData().noValue()) // handle value restriction that is NOT on a modifier 
		{
			ConstrainByValue valueConstrain = node.valuePropertyData().makeValueConstraint();
			itemType.getConstrainByValue().add(valueConstrain);
		}
		
		// (mis)using metadataXML, add originalXML. Should have its own entry
		//MetadataxmlValueType metadata = new MetadataxmlValueType();
		//metadata.getContent().add( node.getOriginalXML() );		
		//itemType.setMetadataxml( metadata );
		
		//metaDataXM
		return itemType;
	}

	

	/*======================================================================================================================================================================================
	 * Generate message for Temporal query
	 * 
	 */
	public static String writeQueryXML( String queryName, ArrayList<String> resultOptions, ArrayList<Group> groups, ArrayList<Event> events, ArrayList<TemporalRelationship> relationships, boolean includeAllEvents, String queryTimingString )
	{
		ResultOutputOptionListType resultOutputOptionListType = new ResultOutputOptionListType();
		
		// loop thru the options
		for (int i = 0; i < resultOptions.size(); i++) 
		{
			ResultOutputOptionType resultOutputOptionType = new ResultOutputOptionType();
			resultOutputOptionType.setName(resultOptions.get(i));
			resultOutputOptionType.setPriorityIndex(new Integer(i + 1));
			resultOutputOptionListType.getResultOutput().add(resultOutputOptionType);
		}

		QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();
		QueryDefinitionType qDef = null;

		// deal with query is for Patient set or Encounter set (Query Timing)
		//TODO:
		
		// deal with Population definition
		if ( groups.isEmpty() ) // no Population data, apply GetEveryone setting
		{
			qDef = new QueryDefinitionType();
			qDef.getPanel().add( makeGetEveryonePanelType() );
		}
		else // create a queryDefinition with panels as the Population defines 
			qDef = makeQueryDefinitionType( groups );
		
		// collect Temporal Relationships and Events.
		List<QueryConstraintType> qConstraints = qDef.getSubqueryConstraint();
		HashSet<Event> eventsInTemporalRelationships = new HashSet<Event>();
		for ( TemporalRelationship tr : relationships )
		{
			QueryConstraintType qConstraint = new QueryConstraintType();
			
			QueryJoinType query1 = new QueryJoinType();
			query1.setQueryId( tr.getTopEvent().getName() );
			query1.setJoinColumn( mapToQueryJoinColumnType( tr.getTopReferencePoint()) );						// startOf, endOf
			query1.setAggregateOperator( mapToQueryAggregateOperatorType( tr.getTopOccurrenceRestriction() )); 	// firstEver, LastEver, Any
			
			QueryJoinType query2 = new QueryJoinType();
			query2.setQueryId( tr.getBotEvent().getName() );
			query2.setJoinColumn( mapToQueryJoinColumnType( tr.getBotReferencePoint()) );						// startOf, endOf
			query2.setAggregateOperator( mapToQueryAggregateOperatorType( tr.getBotOccurrenceRestriction() )); 	// firstEver, LastEver, Any
			
			qConstraint.setFirstQuery( query1 );
			qConstraint.setSecondQuery( query2 );
			qConstraint.setOperator( mapToQueryOperatorType( tr.getOperator() ) );
			
			// deal with Spans
			QuerySpanConstraintType qSpan1 = makeQuerySpanConstraintType( tr.getDuration1() );
			QuerySpanConstraintType qSpan2 = makeQuerySpanConstraintType( tr.getDuration2() );
			if ( qSpan1 != null )
			{
				qConstraint.getSpan().add( qSpan1 );
				if ( qSpan2 != null )
					qConstraint.getSpan().add( qSpan2 );
			}
			qConstraints.add( qConstraint );
			eventsInTemporalRelationships.add( tr.getTopEvent() );
			eventsInTemporalRelationships.add( tr.getBotEvent() );
		}
		
		// deal with Events (subqueries)	
		ArrayList<Event> eventList = events;	// if we are including all Events, whether they are in a TemporalRelationship or not, we use all events		
		if ( !includeAllEvents ) 				// if not, we use only those mentioned in TemporalRelationships
			eventList = new ArrayList<Event>( eventsInTemporalRelationships );
		
		List<QueryDefinitionType> subQueries  = qDef.getSubquery();
		for ( Event event : eventList )
		{
			QueryDefinitionType subQueryDef = makeQueryDefinitionType( event.getGroups() );	// build PanelType for each Group
			String queryTiming = makeQueryTimingFromPanelTimings( subQueryDef.getPanel() );	// set queryTiming for subquery with known panelTiming

			subQueryDef.setQueryId( event.getName() );
			subQueryDef.setQueryName( event.getName() );
			subQueryDef.setQueryTiming( queryTiming );
			subQueryDef.setQueryType( QUERY_TYPE_EVENET );
			subQueryDef.setSpecificityScale(0); 			// Unused. Defaults to 0;

			subQueries.add( subQueryDef );					// add subQuery to subQueries of qDef
		}

		// we now use the timingString in accordance to user's selection of Patient/Encounter in the BasicQueryModePanel
		qDef.setQueryTiming( queryTimingString );
		qDef.setQueryName( queryName );
		queryDefinitionRequestType.setQueryDefinition(qDef);
		queryDefinitionRequestType.setResultOutputList(resultOutputOptionListType);
		
		// Request Header
		RequestHeaderType requestHeader = new RequestHeaderType();
		if (System.getProperty("QueryToolMaxWaitingTime") != null) 
			requestHeader.setResultWaittimeMs((Integer.parseInt(System.getProperty("QueryToolMaxWaitingTime"))) * 1000);
		else 
			requestHeader.setResultWaittimeMs(180000);

		// Query header
		PsmQryHeaderType headerType = new PsmQryHeaderType();
			UserType userType = new UserType();
			userType.setLogin(UserInfoBean.getInstance().getUserName());
			userType.setGroup(System.getProperty("projectName"));
			userType.setValue(UserInfoBean.getInstance().getUserName());
		headerType.setUser(userType);
		headerType.setRequestType(PsmRequestTypeType.CRC_QRY_RUN_QUERY_INSTANCE_FROM_QUERY_DEFINITION);
		//headerType.setQueryMode(QueryModeType.OPTIMIZE_WITHOUT_TEMP_TABLE); // Temporal Queries are not using this for now

		// Body: includes PsmQryHeader and QueryDefinitionRequest
		BodyType bodyType = new BodyType();
			edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
			bodyType.getAny().add(psmOf.createPsmheader(headerType));
			bodyType.getAny().add(psmOf.createRequest(queryDefinitionRequestType));
			
		// Message Header
		MessageHeaderType messageHeader = getMessageHeader();
		
		// Message Type: includes Body, Message Header, and Request Header
		RequestMessageType requestMessageType = new RequestMessageType();
			requestMessageType.setMessageBody(bodyType);
			requestMessageType.setMessageHeader(messageHeader);
			requestMessageType.setRequestHeader(requestHeader);
		
		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try 
		{
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil.marshallerWithCDATA(of.createRequest(requestMessageType), strWriter, new String[] {"value_constraint"});
		} 
		catch (Exception e) 
		{ e.printStackTrace(); }
		
		return strWriter.toString();
	}
	
	private static QuerySpanConstraintType makeQuerySpanConstraintType( OrderedDuration rd)
	{
		if ( rd == null )
			return null;
		QuerySpanConstraintType qSpan = new QuerySpanConstraintType();
		qSpan.setOperator( mapToQueryOperatorType( rd.getOperator()) );
		qSpan.setSpanValue( rd.getNumber() );
		qSpan.setUnits( mapToTimeUnits( rd.getUnit() ));
		return qSpan;
	}
	
	
	private static QueryJoinColumnType mapToQueryJoinColumnType( TemporalRelationship.EventMarker marker )
	{
		if ( marker == TemporalRelationship.EventMarker.START_OF )
			return QueryJoinColumnType.STARTDATE;
		else if ( marker == TemporalRelationship.EventMarker.END_OF )
			return QueryJoinColumnType.ENDDATE;
		assert false: "Query2XML.mapToQueryJoinColumnType(): EventMarker = '" + marker + "' is not recognized.";
		return null;
	}
		
	private static QueryAggregateOperatorType mapToQueryAggregateOperatorType( TemporalRelationship.OccurrenceRestriction occurrenceRestriction )
	{
		if ( occurrenceRestriction == TemporalRelationship.OccurrenceRestriction.FIRST_EVER )
			return QueryAggregateOperatorType.FIRST;
		else if ( occurrenceRestriction == TemporalRelationship.OccurrenceRestriction.LAST_EVER )
			return QueryAggregateOperatorType.LAST;
		else if ( occurrenceRestriction == TemporalRelationship.OccurrenceRestriction.ANY )
			return QueryAggregateOperatorType.ANY;
		assert false: "Query2XML.mapToQueryAggregateOperatorType(): occurrenceRestriction = '" + occurrenceRestriction + "' is not recognized.";
		return null;
	}

	private static QueryOperatorType mapToQueryOperatorType( TemporalRelationship.Operator operator )
	{
		if ( operator == TemporalRelationship.Operator.BEFORE )
			return QueryOperatorType.LESS;
		else if ( operator == TemporalRelationship.Operator.ON_OR_BEFORE )
			return QueryOperatorType.LESSEQUAL;
		else if ( operator == TemporalRelationship.Operator.EQUALS )
			return QueryOperatorType.EQUAL;
		else if ( operator == TemporalRelationship.Operator.ON_OR_AFTER )
			return QueryOperatorType.GREATEREQUAL;
		else if ( operator == TemporalRelationship.Operator.AFTER )
			return QueryOperatorType.GREATER;
		assert false: "Query2XML.mapToQueryOperatorType(TemporalRelationship.Operator): operator = '" + operator + "' is not recognized.";
		return null;
	}
	
	private static QueryOperatorType mapToQueryOperatorType( OrderedDuration.Operator operator )
	{
		if ( operator == OrderedDuration.Operator.LT )
			return QueryOperatorType.LESS;
		else if ( operator == OrderedDuration.Operator.LTE)
			return QueryOperatorType.LESSEQUAL;
		else if ( operator == OrderedDuration.Operator.E )
			return QueryOperatorType.EQUAL;
		else if ( operator == OrderedDuration.Operator.GTE )
			return QueryOperatorType.GREATEREQUAL;
		else if ( operator == OrderedDuration.Operator.GT )
			return QueryOperatorType.GREATER;
		assert false: "Query2XML.mapToQueryOperatorType(OrderedDuration.Operator): operator = '" + operator + "' is not recognized.";
		return null;
	}
	
	private static String mapToTimeUnits( TemporalRelationship.TimeUnit unit )
	{
		if ( unit == TemporalRelationship.TimeUnit.SECONDS )
			return TIME_UNITS[0];
		else if ( unit == TemporalRelationship.TimeUnit.MINUTES )
			return TIME_UNITS[1];
		else if ( unit == TemporalRelationship.TimeUnit.HOURS )
			return TIME_UNITS[2];
		else if ( unit == TemporalRelationship.TimeUnit.DAYS )
			return TIME_UNITS[3];
		else if ( unit == TemporalRelationship.TimeUnit.MONTHS )
			return TIME_UNITS[4];
		else if ( unit == TemporalRelationship.TimeUnit.YEARS )
			return TIME_UNITS[5];
		assert false: "Query2XML.mapToTimeUnits(...): unit = '" + unit + "' is not recognized.";
		return null;
	}

	
	/*======================================================================================================================================================================================
	 * Generate message for Get Everyone Queries 
	 */
	public static String makeGetEveryoneQueryXML( String queryName, String queryTiming, ArrayList<String> queryResultTypeNames )
	{		
		ResultOutputOptionListType resultOutputOptionListType = new ResultOutputOptionListType();
		// /loop thru the options
		for (int i = 0; i < queryResultTypeNames.size(); i++) 
		{
			ResultOutputOptionType resultOutputOptionType = new ResultOutputOptionType();
			resultOutputOptionType.setName( queryResultTypeNames.get(i) );
			resultOutputOptionType.setPriorityIndex(new Integer(i + 1));
			resultOutputOptionListType.getResultOutput().add(resultOutputOptionType);
		}

		QueryDefinitionRequestType queryDefinitionRequestType = new QueryDefinitionRequestType();
		QueryDefinitionType qDef = new QueryDefinitionType();
		qDef.getPanel().add( makeGetEveryonePanelType() );

		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();

		UserType userType = new UserType();
		userType.setLogin(UserInfoBean.getInstance().getUserName());
		userType.setGroup(System.getProperty("projectName"));
		userType.setValue(UserInfoBean.getInstance().getUserName());

		headerType.setUser(userType);
		headerType.setRequestType(PsmRequestTypeType.CRC_QRY_RUN_QUERY_INSTANCE_FROM_QUERY_DEFINITION);
		
		/*
		if (queryName == null) 
		{
			queryName = getTreePanel(0).data().getItems().get(0).name() + "_" + generateMessageId().substring(0, 4);
		}
		*/
		
		headerType.setQueryMode(QueryModeType.OPTIMIZE_WITHOUT_TEMP_TABLE);
		
		qDef.setQueryName(queryName);
		qDef.setQueryTiming(queryTiming);
		queryDefinitionRequestType.setQueryDefinition(qDef);
		queryDefinitionRequestType
				.setResultOutputList(resultOutputOptionListType);

		RequestHeaderType requestHeader = new RequestHeaderType();

		if (System.getProperty("QueryToolMaxWaitingTime") != null) {
			requestHeader.setResultWaittimeMs((Integer.parseInt(System
					.getProperty("QueryToolMaxWaitingTime"))) * 1000);
		} else {
			requestHeader.setResultWaittimeMs(180000);
		}

		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));
		bodyType.getAny().add(psmOf.createRequest(queryDefinitionRequestType));

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try 
		{
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil.marshaller(of.createRequest(requestMessageType), strWriter);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return strWriter.toString();
	}
	
	private static PanelType makeGetEveryonePanelType()
	{
		PanelType panelType = new PanelType();
		panelType.setInvert(1);
		PanelType.TotalItemOccurrences totalOccurrences = new PanelType.TotalItemOccurrences();
		totalOccurrences.setValue(1);
		panelType.setTotalItemOccurrences(totalOccurrences);
		panelType.setPanelNumber(1);
		return panelType;
	}
	
	protected static MessageHeaderType getMessageHeader() 
	{
		MessageHeaderType messageHeader = new MessageHeaderType();
		messageHeader.setI2B2VersionCompatible(new BigDecimal(Messages.getString("QueryData.i2b2VersionCompatible"))); //$NON-NLS-1$

		ApplicationType appType = new ApplicationType();
		appType.setApplicationName(Messages.getString("QueryData.SendingApplicationName")); //$NON-NLS-1$
		appType.setApplicationVersion(Messages.getString("QueryData.SendingApplicationVersion")); //$NON-NLS-1$
		messageHeader.setSendingApplication(appType);

		messageHeader.setAcceptAcknowledgementType(new String("messageId"));

		MessageTypeType messageTypeType = new MessageTypeType();
		messageTypeType.setEventType(Messages.getString("QueryData.EventType"));
		messageTypeType.setMessageCode(Messages.getString("QueryData.MessageCode"));
		messageHeader.setMessageType(messageTypeType);

		FacilityType facility = new FacilityType();
		facility.setFacilityName(Messages.getString("QueryData.SendingFacilityName")); //$NON-NLS-1$
		messageHeader.setSendingFacility(facility);

		ApplicationType appType2 = new ApplicationType();
		appType2.setApplicationVersion(Messages.getString("QueryData.ReceivingApplicationVersion")); //$NON-NLS-1$
		appType2.setApplicationName(Messages.getString("QueryData.ReceivingApplicationName")); //$NON-NLS-1$
		messageHeader.setReceivingApplication(appType2);

		FacilityType facility2 = new FacilityType();
		facility2.setFacilityName(Messages.getString("QueryData.ReceivingFacilityName")); //$NON-NLS-1$
		messageHeader.setReceivingFacility(facility2);

		Date currentDate = new Date();
		DTOFactory factory = new DTOFactory();
		messageHeader.setDatetimeOfMessage(factory.getXMLGregorianCalendar(currentDate.getTime()));

		SecurityType secType = new SecurityType();
		secType.setDomain(UserInfoBean.getInstance().getUserDomain());
		secType.setUsername(UserInfoBean.getInstance().getUserName());

		PasswordType ptype = new PasswordType();
		ptype.setIsToken(UserInfoBean.getInstance().getUserPasswordIsToken());
		ptype.setTokenMsTimeout(UserInfoBean.getInstance().getUserPasswordTimeout());
		ptype.setValue(UserInfoBean.getInstance().getUserPassword());

		secType.setPassword(ptype);
		messageHeader.setSecurity(secType);

		MessageControlIdType mcIdType = new MessageControlIdType();
		mcIdType.setInstanceNum(0);
		mcIdType.setMessageNum( DataUtils.generateMessageId());
		messageHeader.setMessageControlId(mcIdType);

		ProcessingIdType proc = new ProcessingIdType();
		proc.setProcessingId(Messages.getString("QueryData.ProcessingId")); //$NON-NLS-1$
		proc.setProcessingMode(Messages.getString("QueryData.ProcessingMode")); //$NON-NLS-1$
		messageHeader.setProcessingId(proc);

		messageHeader.setAcceptAcknowledgementType(Messages.getString("QueryData.AcceptAcknowledgementType")); //$NON-NLS-1$
		messageHeader.setApplicationAcknowledgementType(Messages.getString("QueryData.ApplicationAcknowledgementType")); //$NON-NLS-1$
		messageHeader.setCountryCode(Messages.getString("QueryData.CountryCode")); //$NON-NLS-1$
		messageHeader.setProjectId(UserInfoBean.getInstance().getProjectId());
		return messageHeader;
	}

	
	
	/*
	 * Make ConstrainByDate
	 */
	public static ConstrainByDate makeTimeConstrain( GregorianCalendar startDate, GregorianCalendar endDate ) 
	{
		ConstrainByDate timeConstrain = new ConstrainByDate();
		DTOFactory dtoFactory = new DTOFactory();
		if ( startDate != null ) 
		{
			ConstrainDateType constraindateType = new ConstrainDateType();
			XMLGregorianCalendar xmlC = makeXMLGregorianCalendar( startDate, false ); 
			/*
			dtoFactory.getXMLGregorianCalendarDate( startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH) + 1, startDate.get( Calendar.DAY_OF_MONTH) );
			int minuteOffSet = startDate.getTimeZone().getOffset( startDate.getTimeInMillis() )/60000; // get the time difference in minutes between the local timezone (in startDate) and UTC
			xmlC.setTimezone( minuteOffSet );
			xmlC.setHour(0);
			xmlC.setMinute(0);
			xmlC.setSecond(0);
			*/
			constraindateType.setValue(xmlC);
			timeConstrain.setDateFrom(constraindateType);
		}

		if ( endDate != null) 
		{
			ConstrainDateType constraindateType = new ConstrainDateType();
			XMLGregorianCalendar xmlC = makeXMLGregorianCalendar( endDate, true ); 
			/*		
			dtoFactory.getXMLGregorianCalendarDate( endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH) + 1, endDate.get( Calendar.DAY_OF_MONTH) );
			int minuteOffSet = endDate.getTimeZone().getOffset( endDate.getTimeInMillis() )/60000; // get the time difference in minutes between the local timezone (in endDate) and UTC
			xmlC.setTimezone(minuteOffSet);
			xmlC.setHour(23); // make sure that all of endDate is covered by extending to 23:59:59.999
			xmlC.setMinute(59);
			xmlC.setSecond(59);
			xmlC.setMillisecond(999);
			*/
			constraindateType.setValue(xmlC);
			timeConstrain.setDateTo(constraindateType);
		}
		return timeConstrain;
	}

	/*
	 * Make XMLGregorianCalendar.
	 *  if it isEndDate, we subtract one millisencond from it so we cover the whole length of the end date but no more
	 */
	public static XMLGregorianCalendar makeXMLGregorianCalendar( GregorianCalendar cal, boolean isEndDate )
	{
		DTOFactory dtoFactory = new DTOFactory();
		XMLGregorianCalendar xmlC = dtoFactory.getXMLGregorianCalendarDate( cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get( Calendar.DAY_OF_MONTH) );
		int minuteOffSet = cal.getTimeZone().getOffset( cal.getTimeInMillis() )/60000; // get the time difference in minutes between the local timezone (in endDate) and UTC
		xmlC.setTimezone(minuteOffSet);
		if ( isEndDate ) // make sure that all of endDate is covered by extending to 23:59:59.999
		{
			xmlC.setHour(23); 
			xmlC.setMinute(59);
			xmlC.setSecond(59);
			xmlC.setMillisecond(999);
		}
		else
		{
			xmlC.setHour(0); 
			xmlC.setMinute(0);
			xmlC.setSecond(0);
			xmlC.setMillisecond(0);
		}
		return xmlC;
	}


	/*
	 * Make ConstrainByModifier
	 */
	public static ConstrainByModifier makeModifierConstraint( ModifierData mod ) 
	{		
		ConstrainByModifier modifierConstraint = new ConstrainByModifier();
		modifierConstraint.setAppliedPath( mod.applied_path() );
		modifierConstraint.setModifierKey( mod.modifier_key() );
		modifierConstraint.setModifierName( mod.modifier_name() );		
		// handle value constraint
		if (!mod.valuePropertyData().noValue()) 
		{
			ConstrainByModifier.ConstrainByValue valueConstrain = mod.valuePropertyData().makeModifierValueConstraint();
			modifierConstraint.getConstrainByValue().add(valueConstrain);
		}		
		return modifierConstraint;
	}

	

}
