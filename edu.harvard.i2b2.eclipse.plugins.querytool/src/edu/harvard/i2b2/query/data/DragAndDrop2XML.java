package edu.harvard.i2b2.query.data;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.harvard.i2b2.crcxmljaxb.datavo.dnd.DndType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainDateType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PanelType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryConstraintType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByModifier;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryOperatorType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QuerySpanConstraintType;
import edu.harvard.i2b2.eclipse.plugins.querytool.error.ErrorConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.TemporalRelationship;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.XMLCreator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.query.jaxb.utils.QueryJAXBUtil;

public class DragAndDrop2XML implements DataConst, UIConst
{
	
	/* When dragging a group, XML is created for that group just PRIOR to drop */
	public static String makeDNDXMLFromGroup( Group myGroup )
	{
		// set Panel-level data
		PanelType panelType = new PanelType();
		panelType.setInvert( myGroup.isExcluded() ? 1 : 0);										// set Excludedness
		panelType.setTotalItemOccurrences( Query2XML.makeTotalItemOccurrences( myGroup ) );		// set occurrences
		panelType.setPanelAccuracyScale( myGroup.getAccuracy() );								// set accuracy
		//panelType.setPanelNumber( 1 );														// set panelNumber (unnecessary?)
		panelType.setPanelTiming( Query2XML.mapToGroupTiming(myGroup.getBinding()) );			// set panelTiming
		panelType.setName(  myGroup.getName() );
		
		// set date constraints (if the Group has them)
		if ( myGroup.getStartDate() != null )
		{
			ConstrainDateType dateConstraint = new ConstrainDateType();
			dateConstraint.setValue( Query2XML.makeXMLGregorianCalendar(myGroup.getStartDate(), false ));
			panelType.setPanelDateFrom( dateConstraint );
		}
		if ( myGroup.getEndDate() != null )
		{
			ConstrainDateType dateConstraint = new ConstrainDateType();
			dateConstraint.setValue( Query2XML.makeXMLGregorianCalendar(myGroup.getEndDate(), true ));
			panelType.setPanelDateTo( dateConstraint );
		}
		
		// add each ConceptTreeNodeData
		ArrayList<QueryConceptTreeNodeData> nodes = myGroup.getTerms();
		for (int j = 0; j < nodes.size(); j++) 
		{
			QueryConceptTreeNodeData temp = nodes.get(j);
			ItemType itemType = Query2XML.makeItemType( myGroup, nodes.get(j) );
			panelType.getItem().add(itemType);
		}
		QueryDefinitionType queryDefinitionType = new QueryDefinitionType();
		queryDefinitionType.getPanel().add(panelType);

		StringWriter strWriter = new StringWriter();
		try 
		{			
			edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
			DndType dnd = new DndType();
			dnd.getAny().add(psmOf.createPanel(panelType));
			edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory();
			QueryJAXBUtil.getJAXBUtil().marshaller(of.createPluginDragDrop(dnd), strWriter);			
		} 
		catch (Exception e)  
		{
			// bugbug: need to handle the case where Exception occurs 
			e.printStackTrace();
		}
				
    	return strWriter.toString();
	}

	public static ArrayList<Event> makeEventsWithQueryDefinition( QueryDefinitionType qdt ) throws IOException, JDOMException
	{	
		ArrayList<Event> events = new ArrayList<Event>();

		List<QueryDefinitionType> subQueries = qdt.getSubquery();

		// sort by subQuery name so we process Event 1 before Event 2
		Collections.sort( subQueries, new Comparator<QueryDefinitionType>()
		{
			@Override
			public int compare(QueryDefinitionType q1, QueryDefinitionType q2) 
			{ return q1.getQueryName().compareTo( q2.getQueryName() ); }
		});

		for ( QueryDefinitionType subQuery : subQueries )
		{
			Event event = new Event( subQuery.getQueryName() );
			setEventWithQueryDefinition( event, subQuery );
			events.add( event );
		}
		
		return events;
	}

	public static ArrayList<TemporalRelationship> makeTemporalRelationshipsWithQueryDefinition( QueryDefinitionType qdt, ArrayList<Event> events ) throws IOException, JDOMException
	{
		ArrayList<TemporalRelationship> temporalRelationships = new ArrayList<TemporalRelationship>();
		HashMap<String, Event> eventMap = new HashMap<String, Event>();
		for ( Event event : events )
			eventMap.put( event.getName(), event );
		List<QueryConstraintType> subQueryConstraints = qdt.getSubqueryConstraint();
		for ( QueryConstraintType qct: subQueryConstraints )
		{
			TemporalRelationship tr = new TemporalRelationship();
			Event firstEvent	= eventMap.get( qct.getFirstQuery().getQueryId() );
			Event secondEvent	= eventMap.get( qct.getSecondQuery().getQueryId() );

			// set events
			tr.setTopEvent( firstEvent );
			tr.setBotEvent( secondEvent );
			
			// Occurrence Restriction to "First of", "Last of", or "Any"
			tr.setTopOccurrenceRestriction( qct.getFirstQuery().getAggregateOperator().name() );
			tr.setBotOccurrenceRestriction( qct.getSecondQuery().getAggregateOperator().name() );

			// Set Event Marker to be "Start of" or "End of"
			tr.setTopEventMarker( qct.getFirstQuery().getJoinColumn().name() );
			tr.setBotEventMarker( qct.getSecondQuery().getJoinColumn().name() );

			// set Operator BEFORE/ AFTER between the two Events
			tr.setOperator( qct.getOperator().name() );

			// set durations (if they exist)
			List <QuerySpanConstraintType> spans = qct.getSpan();
			if ( spans.size() > 0) // set the first duration (the "By" part)
				setDuration( tr, spans.get(0), true );
			if ( spans.size() > 1) // set the second duration (the "And" part)
				setDuration( tr, spans.get(1), false );
			
			// add tr to our array
			temporalRelationships.add(tr);
		}
		return temporalRelationships;
	}

	public static void setDuration( TemporalRelationship tr, QuerySpanConstraintType span, boolean isFirst )
	{
		if ( isFirst )
			tr.setDuration1( span.getOperator().name(), span.getSpanValue(), span.getUnits() );
		else
			tr.setDuration2( span.getOperator().name(), span.getSpanValue(), span.getUnits() );
	}

	public static void setEventWithQueryDefinition( Event event, QueryDefinitionType qdt ) throws IOException, JDOMException
	{
		// Setting Groups 
		List<PanelType> panels 		= qdt.getPanel();
		ArrayList <Group> groups 	= new ArrayList<Group>();
		int number = 1;
		for ( PanelType panel: panels )
		{
			Group g = makeGroupFromPanelType( panel, GROUP + " " + number );
			groups.add( g );
		}
		event.setGroups( groups );
	}

	/* A Group XML has been dropped, we need to parse it and update the group with the data in the Group XML */
	public static void setGroupWithDNDXMLAfterDrop( Group group, String groupXML )
	{
		try 
		{
			JAXBContext jc1 = JAXBContext.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.dnd.ObjectFactory.class);
			Unmarshaller unMarshaller = jc1.createUnmarshaller();
			@SuppressWarnings("rawtypes")
			JAXBElement jaxbElement = (JAXBElement) unMarshaller.unmarshal(new StringReader(groupXML));
			DndType dnd = (DndType) jaxbElement.getValue();
			
			//System.err.println( "DragAndDrop2XML.setGroupWithDNDXMLAfterDrop: dropped Group: " + groupXML);
			if (dnd.getAny().size() > 0) 
			{
				org.w3c.dom.Element rootElement = (org.w3c.dom.Element) dnd.getAny().get(0);
				String name = nodeToString(rootElement);
				jc1 = JAXBContext.newInstance(edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory.class);
				unMarshaller = jc1.createUnmarshaller();
				jaxbElement = (JAXBElement) unMarshaller.unmarshal(new StringReader(name));
				PanelType panelType = (PanelType) jaxbElement.getValue();
				setGroupWithPanelType( group, panelType );
			}			
		} 
		catch ( javax.xml.bind.UnmarshalException e )
		{
			e.printStackTrace();
			UIUtils.popupError( ErrorConst.GENERIC_DROP_TITLE,  ErrorConst.GENERIC_DROP_CANNOT_THAT_HERE, ErrorConst.NONGROUP_DROPPED_ON_GROUP_TTILE );
			return;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			UIUtils.popupError( ErrorConst.GENERIC_DROP_TITLE,  ErrorConst.GENERIC_DROP_CANNOT_THAT_HERE, ErrorConst.NONGROUP_DROPPED_ON_GROUP_TTILE );
			return;
		}
	}

	/* Set values of the Group with the given PanelType */
	public static void setGroupWithPanelType( Group group, PanelType panelType ) throws IOException, JDOMException
	{
		if ( panelType.getPanelDateFrom() != null )
			group.setStartDate( XMLGregorianCalendar2GregorianCalendar( panelType.getPanelDateFrom().getValue(), true ) );
		if ( panelType.getPanelDateTo() != null )
			group.setEndDate( XMLGregorianCalendar2GregorianCalendar( panelType.getPanelDateTo().getValue(), false ) );
		// Mark dates as set if panel has the data, otherwise, we check each term's date constraints (see in the loop)
		boolean areDateConstraintsSet = (( panelType.getPanelDateFrom() != null ) || ( panelType.getPanelDateTo() != null ));
		
		boolean hasError 			= false;
		StringBuffer 	errorBuffer = new StringBuffer("The following terms are omitted because they are not supported: ");
		HashSet<String> errorTerms  = new HashSet<String>();
		
		ArrayList<QueryConceptTreeNodeData> terms = new ArrayList<QueryConceptTreeNodeData>();				
		for (int j = 0; j < panelType.getItem().size(); j++) 
		{
			ItemType itemType = panelType.getItem().get(j);
			
			// critical bugbug: needs to account for other types of QueryDefinitionType like the ones that contain master_id (previous query)!!!
			QueryConceptTreeNodeData node = null;
			
			String itemKey = itemType.getItemKey().trim().toLowerCase();
			if ( itemKey.startsWith( MASTER_ID_PREFIX )) 				// a PREVIOUS QUERY
				node = makePreviousQueryNode( itemType );
			else if ( itemKey.startsWith( PATIENT_SET_COLL_ID_PREFIX ) ) // a PATIENT SET or NUMBER OF PATIENTS
			{
				if ( itemType.getItemName().toLowerCase().startsWith( ITEM_NAME_PATIENT_SET_PREFIX ) )			// a PATIENT SET 
					node = makePatientSetNode( itemType );
				else if ( itemType.getItemName().toLowerCase().startsWith( ITEM_NAME_PATIENT_COUNT_PREFIX ) )	// a PATIENT COUNT
				{
					hasError = true;
					errorTerms.add( itemType.getItemName() + "(Patient Count)" );
					continue;
				}
				else
				{
					hasError = true;
					errorTerms.add( itemType.getItemName() + "(Unrecognized Type)" );
					continue;
				}
			}
			else if ( itemKey.startsWith( PATIENT_SET_ENC_ID_PREFIX ) ) // an ENCOUNTER SET
				node = makeEncounterSetNode( itemType );
			else if ( itemKey.startsWith( PATIENT_PREFIX ))				// a PATIENT
				node = makePatientNode( itemType );
			else if ( itemKey.startsWith( ENCOUNTER_PREFIX ) )			// an Encounter
			{
				hasError = true;
				errorTerms.add( itemType.getItemName() + "(Encounter)" );
				continue;

			}
			else if ( itemKey.startsWith( CELL_URL_PREFIX ) )			// cell URL to call out to retrieve a list of PDO of patients
			{
				hasError = true;
				errorTerms.add( itemType.getItemName() + "(Cell URL)" );
				continue;
			}
			else														// an ordinary Concept
				node = makeConceptNode( itemType );
			
			terms.add( node );	// add to the collection
			
			/*  The group's Start/End Date constraints are not set, set it now using ItemType's constraints. 
			     this is necessary because older i2m2 query interface does not populate date constraints on the Group level.
			     This code assumes that all Term-level date constraints are identical */
			if (!areDateConstraintsSet) 
			{
				ConstrainDateType fromDate = null;
				ConstrainDateType toDate = null;
				
				if ( itemType.getConstrainByDate().size() > 0 )
				{
					fromDate 	= itemType.getConstrainByDate().get(0).getDateFrom();
					toDate 		= itemType.getConstrainByDate().get(0).getDateTo();
				}
				
				if ( fromDate != null )
					group.setStartDate( XMLGregorianCalendar2GregorianCalendar(fromDate.getValue(), true ) );
				if ( toDate != null )
					group.setEndDate( XMLGregorianCalendar2GregorianCalendar(toDate.getValue(), false) );
				areDateConstraintsSet = true;
			}
		}
		group.setTerms( terms );											// set terms
		group.setBinding( map2GroupBinding(panelType.getPanelTiming()) );	// set group binding (default is BY_PATIENT)
		if ( !group.isContainingModifier() &&  (group.getBinding() == GroupBinding.BY_OBSERVATION) )
			group.setBinding( GroupBinding.BY_ENCOUNTER ); 					// if group has no modifier do not allow Bound_By_Observation
		setNumberAndOperatorToGroup( group, panelType );					// set number and operator
		
		if ( hasError )
		{
			for ( Iterator<String> it = errorTerms.iterator();  it.hasNext(); )
			{
				errorBuffer.append( it.next() );
				if ( it.hasNext() )
					errorBuffer.append( ", " );
			}
			UIUtils.popupError("Error",  errorBuffer.toString(), "Their types (in parantheses) are not supported." );
		}
	}


	private static QueryConceptTreeNodeData makePatientNode(ItemType itemType) throws IOException, JDOMException
	{
		QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
		node.name( itemType.getItemName() );
		node.titleName( itemType.getItemName() );
		node.fullname( itemType.getItemKey() );
		node.tooltip( itemType.getItemName() );
		node.hlevel( itemType.getHlevel()+"" );
		node.visualAttribute( PT );
		if ( itemType.getMetadataxml() != null )
			node.finalizeOriginalXML( (String)itemType.getMetadataxml().getContent().get(0) );
		else
		{
			String xml = XMLCreator.makePatientXML(node);
			//System.err.println( "DragAndDrop2XML created XML: " + xml );
			node.finalizeOriginalXML( xml );
		}
		return node;
	}


	/*
	 *  EXAMPLE XML of dropping an Patient  Set from a Previous Query
	 *  
		<ns5:plugin_drag_drop>
			<ns4:query_result_instance>
				<result_instance_id>3986</result_instance_id>
				<query_instance_id>3986</query_instance_id>
				<description>Patient Set for "(t) Diagnoses@13:34:01"</description>
				<query_result_type>
					<result_type_id>1</result_type_id>
					<name>PATIENTSET</name>
					<display_type>LIST</display_type>
					<visual_attribute_type>LA</visual_attribute_type>
					<description>Patient set</description>
				</query_result_type>
				<set_size>133</set_size>
				<obfuscate_method/>
				<start_date>2013-09-17T13:34:07.273-04:00</start_date>
				<end_date>2013-09-17T13:34:12.747-04:00</end_date>
				<message/>
				<query_status_type>
					<status_type_id>3</status_type_id>
					<name>FINISHED</name>
					<description>FINISHED</description>
				</query_status_type>
			</ns4:query_result_instance>
		</ns5:plugin_drag_drop>
	 * 
	 * 	EXAMPLE XML of dropping a Group containing Patient Set that has no original XML
	 * 
	 *  <ns5:plugin_drag_drop><ns9:panel name="Group 1 - has Patient Set">
  			<panel_number>1</panel_number>
  			<panel_accuracy_scale>0</panel_accuracy_scale>
  			<invert>0</invert>
  			<total_item_occurrences>1</total_item_occurrences>
  				<item>
  					<hlevel>0</hlevel>
  					<item_name>Patient Set for "Patient Set for@10:31:12"</item_name>
  					<item_key>patient_set_coll_id:3945</item_key>
  					<item_icon>LA</item_icon>
  					<tooltip>Patient Set for "Patient Set for@10:31:12"</tooltip>
  					<class>ENC</class>
  					<item_is_synonym>false</item_is_synonym>
  				</item>
  			</ns9:panel>
  		</ns5:plugin_drag_drop> 
	 */
	private static QueryConceptTreeNodeData makePatientSetNode(ItemType itemType) throws IOException, JDOMException
	{
		QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
		node.name( itemType.getItemName() );
		node.titleName( itemType.getItemName() );
		node.fullname( itemType.getItemKey() );
		node.tooltip( itemType.getTooltip() );
		node.hlevel( itemType.getHlevel()+"" );
		node.visualAttribute( ICON_PATIENT_SET );
		if ( itemType.getMetadataxml() != null )
			node.finalizeOriginalXML( (String)itemType.getMetadataxml().getContent().get(0) );
		else
		{
			String xml = XMLCreator.makePatientSetXML(node);
			//System.err.println( "DragAndDrop2XML created XML: " + xml );
			node.finalizeOriginalXML( xml );
		}
		return node;
	}

	
	/*
	 * EXAMPLE XML of dropping an Encounter Set from a Previous Query
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<ns5:plugin_drag_drop xmlns:ns2="http://www.i2b2.org/xsd/hive/pdo/1.1/" xmlns:ns4="http://www.i2b2.org/xsd/cell/crc/psm/1.1/" xmlns:ns3="http://www.i2b2.org/xsd/cell/crc/pdo/1.1/" xmlns:ns5="http://www.i2b2.org/xsd/hive/plugin/" xmlns:ns6="http://www.i2b2.org/xsd/hive/msg/1.1/" xmlns:ns7="http://www.i2b2.org/xsd/cell/crc/psm/querydefinition/1.1/" xmlns:ns8="http://www.i2b2.org/xsd/cell/pm/1.1/">
	    <ns4:query_result_instance>
	        <result_instance_id>3948</result_instance_id>
	        <query_instance_id>3948</query_instance_id>
	        <description>Encounter Set for "All Patients"</description>
	        <query_result_type>
	            <result_type_id>2</result_type_id>
	            <name>PATIENT_ENCOUNTER_SET</name>
	            <display_type>LIST</display_type>
	            <visual_attribute_type>LA</visual_attribute_type>
	            <description>Encounter set</description>
	        </query_result_type>
	        <set_size>134</set_size>
	        <obfuscate_method></obfuscate_method>
	        <start_date>2013-09-11T14:07:58.517-04:00</start_date>
	        <end_date>2013-09-11T14:07:59.280-04:00</end_date>
	        <message></message>
	        <query_status_type>
	            <status_type_id>3</status_type_id>
	            <name>FINISHED</name>
	            <description>FINISHED</description>
	        </query_status_type>
	    </ns4:query_result_instance>
	</ns5:plugin_drag_drop>
	*
	* EXAMPLE XML of dropping a Group containing Encounter Set that has no original XML 
	  <ns5:plugin_drag_drop>
	  	<ns9:panel name="Group 1 - has Encounter Set">
			<panel_number>1</panel_number>
			<panel_accuracy_scale>0</panel_accuracy_scale>
			<invert>0</invert>
			<total_item_occurrences>1</total_item_occurrences>
			<item>
				<hlevel>0</hlevel>
				<item_name>Encounter Set for "(t) Circulatory sys@14:08:51"</item_name>
				<item_key>patient_set_enc_id:3951</item_key>
				<item_icon>LA</item_icon>
				<tooltip>Encounter Set for "(t) Circulatory sys@14:08:51"</tooltip>
				<class>ENC</class>
				<item_is_synonym>false</item_is_synonym>
			</item>
		</ns9:panel>
	  </ns5:plugin_drag_drop>
	*/
	private static QueryConceptTreeNodeData makeEncounterSetNode(ItemType itemType) throws IOException, JDOMException
	{
		QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
		node.name( itemType.getItemName() );
		node.titleName( itemType.getItemName() );
		node.fullname( itemType.getItemKey() );
		node.tooltip( itemType.getTooltip() );
		node.hlevel( itemType.getHlevel()+"" );
		node.visualAttribute( ICON_ENCOUNTER_SET );
		if ( itemType.getMetadataxml() != null )
			node.finalizeOriginalXML( (String)itemType.getMetadataxml().getContent().get(0) );
		else
		{
			String xml = XMLCreator.makeEncounterSetXML(node);
			//System.err.println( "DragAndDrop2XML created XML: " + xml );
			node.finalizeOriginalXML( xml );
		}
		return node;
	}

	private static QueryConceptTreeNodeData makePreviousQueryNode( ItemType itemType ) throws IOException, JDOMException
	{
		QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();
		node.name(itemType.getItemName());
		node.titleName(itemType.getItemName());		
		node.visualAttribute( PQ );
		node.tooltip(itemType.getTooltip());
		node.fullname(itemType.getItemKey());
		node.hlevel(new Integer(itemType.getHlevel()).toString());
		
		if ( itemType.getMetadataxml() != null )
			node.finalizeOriginalXML( (String)itemType.getMetadataxml().getContent().get(0) );
		else
		{
			String xml = XMLCreator.makePreviousQueryXML(node);
			//System.err.println( "DragAndDrop2XML created XML: " + xml );
			node.finalizeOriginalXML( xml );
		}
		return node;
	}

	private static QueryConceptTreeNodeData makeConceptNode( ItemType itemType ) throws IOException, JDOMException
	{
		QueryConceptTreeNodeData node = null;
		ConstrainByModifier md = itemType.getConstrainByModifier();
		if(md != null) 
		{
			node = new ModifierData();
			node.isModifier(true);
			((ModifierData)node).modifier_key(md.getModifierKey());
			((ModifierData)node).applied_path(md.getAppliedPath());
			((ModifierData)node).modifier_name(md.getModifierName());
		}
		else 
			node = new QueryConceptTreeNodeData();
		
		node.name(itemType.getItemName());
		node.titleName(itemType.getItemName());
		node.fullname(itemType.getItemKey());
		node.tooltip(itemType.getTooltip());					
		node.hlevel(Integer.toString(itemType.getHlevel()));
		
		if (itemType.getItemIcon() != null)
			node.visualAttribute(itemType.getItemIcon());
		else 
			node.visualAttribute("LA");
		
		/* Check to see if originalXML is stowed in getMetadatXML. Set it if available (dragged within the Query UI and not from Workplace)
		 * 	set and finalize originalXML before setting constraints so setting constraints will not fire a call to ONT cell.
		 */
		if ( (itemType.getMetadataxml() != null) &&  (!itemType.getMetadataxml().getContent().isEmpty()) && (!((String)itemType.getMetadataxml().getContent().get(0)).isEmpty() ) )
		{
			String escapedXML = (String)itemType.getMetadataxml().getContent().get(0); // first we parse the XML
			SAXBuilder builder = new SAXBuilder();
			StringReader xmlStringReader = new StringReader(escapedXML);
			Document originalXMLDoc = builder.build( xmlStringReader );

			StringWriter strWriter = new StringWriter();
			DataUtils.prettyPrintXMLDoc( originalXMLDoc, strWriter );
			String xmlString = strWriter.toString();
			node.finalizeOriginalXML( xmlString );				// set original XML
			node.parseOriginalXMLAndSetValuePropertySchema();	// parse to get the correct value properties
		}
		else // this is for backward compatibility to support Groups saved by the original Query plugin (have no originalXML in metadataXML)
		{
			node.retrieveFullConceptXMLFromONTandSetOriginalXML();
		}
		
		ItemType.ConstrainByValue cbv = null;
		if ( itemType.getConstrainByValue() != null && !itemType.getConstrainByValue().isEmpty() )
			cbv = itemType.getConstrainByValue().get(0);	// fetch the first constrainByvalue
		if ( node instanceof ModifierData && itemType.getConstrainByModifier() != null && !itemType.getConstrainByModifier().getConstrainByValue().isEmpty() )	// is a modifier
			cbv = DataUtils.toConstrainByValue( itemType.getConstrainByModifier().getConstrainByValue().get(0) );	// fetch the first constrainByValue
		if ( cbv != null )
			node.setValueConstraint( cbv );						// now set the constraint to the node
		
		return node;
	}
	
	/* make a new Group and set its values with the given PanelType */
	public static Group makeGroupFromPanelType( PanelType panelType, String groupName ) throws IOException, JDOMException
	{
		Group group = new Group( groupName );
		setGroupWithPanelType( group, panelType );
		return group;
	}

	
	/* set GroupBinding for a group, given a PanelType */
	public static GroupBinding map2GroupBinding( String timingString )
	{
		if ( timingString == null )					// Groups from the Workplace has no timingString
			return GroupBinding.BY_PATIENT;			// default value is BY_PATIENT
		
		else if ( timingString.equals( DataConst.ANY ) )
			return GroupBinding.BY_PATIENT;
		else if ( timingString.equals( DataConst.SAME_VISIT ) )
			return GroupBinding.BY_ENCOUNTER;
		else if ( timingString.equals( DataConst.SAME_INSTANCE ) )
			return GroupBinding.BY_OBSERVATION;
		assert false : "DragAndDrop2XML.mapToGroupTiming(): binding string '" + timingString +"' is not recognized.";
		return null;
	}
	
	/* set the Number and Operator for a group, given a PanelType */
	public static void setNumberAndOperatorToGroup( Group group, PanelType panelType )
	{
		int number = panelType.getTotalItemOccurrences().getValue();
		if ( panelType.getInvert() == 1 )
		{
			group.setNumber( number );
			group.setOperator( UIConst.LESS_THAN );
			if ( number == 0 )
				group.setOperator( UIConst.EQUAL );
		}
		else
		{
			group.setNumber( number-1 );
			group.setOperator( UIConst.GREATER_THAN );
		}
	}
	
	public static GregorianCalendar XMLGregorianCalendar2GregorianCalendar( XMLGregorianCalendar xmlC, boolean isConvertingToStartDate )
	{
		if ( xmlC == null )
			return null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		if ( isConvertingToStartDate ) // converting to a start date, we set the hour/min/sec/millisec to 0
		{
			c.set( xmlC.getYear(), xmlC.getMonth()-1, xmlC.getDay(), 0, 0, 0 ); // XMLGregorianCalendar's month is 1-oriented, GregorianCalendar is 0-oriented		
			c.set( Calendar.MILLISECOND, 0 );
		}
		else
		{
			c.set( xmlC.getYear(), xmlC.getMonth()-1, xmlC.getDay(), 23, 59, 59 ); // XMLGregorianCalendar's month is 1-oriented, GregorianCalendar is 0-oriented		
			c.set( Calendar.MILLISECOND, 999 );
		}
		c.setTimeZone( xmlC.getTimeZone( xmlC.getTimezone() ));
		return c;
	}
	
	
	public static String nodeToString(Node node) 
	{
		DOMImplementation impl 		= node.getOwnerDocument().getImplementation();
		DOMImplementationLS factory = (DOMImplementationLS) impl.getFeature("LS", "3.0");
		LSSerializer serializer 	= factory.createLSSerializer();
		return serializer.writeToString(node);
	}

}
