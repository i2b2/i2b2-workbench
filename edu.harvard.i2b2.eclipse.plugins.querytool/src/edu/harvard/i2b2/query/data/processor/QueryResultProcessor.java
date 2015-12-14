package edu.harvard.i2b2.query.data.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeFactoryProduct;

public class QueryResultProcessor implements ProcessorConst 
{
	private static final String PATIENT_COUNT_TYPE = "patient_count_xml (Paitent Count)";
	/*
	 * THIS IS AN EXAMPLE OF A PATIENT COUNT DROP!
	<ns4:query_result_instance>
    <result_instance_id>3958</result_instance_id>
    <query_instance_id>3958</query_instance_id>
    <description>Number of patients for "(PrevQuery)All @02:33:00"</description>
    <query_result_type>
        <result_type_id>4</result_type_id>
        <name>PATIENT_COUNT_XML</name>
        <display_type>CATNUM</display_type>
        <visual_attribute_type>LA</visual_attribute_type>
        <description>Number of patients</description>
    </query_result_type>
    <set_size>134</set_size>
	 
	 *
	 * THIS IS AN EXAMPLE OF A PATIENT SET DROP!
	 * 
	<ns4:query_result_instance>
    <result_instance_id>3956</result_instance_id>
    <query_instance_id>3956</query_instance_id>
    <description>Patient Set for "(PrevQuery)All @02:33:00"</description>
    <query_result_type>
        <result_type_id>1</result_type_id>
        <name>PATIENTSET</name>
        <display_type>LIST</display_type>
        <visual_attribute_type>LA</visual_attribute_type>
        <description>Patient set</description>
    </query_result_type>
    <set_size>134</set_size>

	 * THIS IS AN EXAMPLE OF AN ENCOUNTER SET DROP!
	 *    
	<ns4:query_result_instance>
    <result_instance_id>3957</result_instance_id>
    <query_instance_id>3957</query_instance_id>
    <description>Encounter Set for "(PrevQuery)All @02:33:00"</description>
    <query_result_type>
        <result_type_id>2</result_type_id>
        <name>PATIENT_ENCOUNTER_SET</name>
        <display_type>LIST</display_type>
        <visual_attribute_type>LA</visual_attribute_type>
        <description>Encounter set</description>
    </query_result_type>
    <set_size>134</set_size>
	 
	 * 
	 */

	@SuppressWarnings("rawtypes")
	public static QueryConceptTreeNodeFactoryProduct process( Element mainXMLElement, String originalXml )
	{
		ArrayList<QueryConceptTreeNodeData> newNodes = new ArrayList<QueryConceptTreeNodeData>();		
		QueryConceptTreeNodeData node = new QueryConceptTreeNodeData();		
		String id 			= mainXMLElement.getChildTextTrim( RESULT_INSTANCE_ID );		
		String description  = mainXMLElement.getChildTextTrim( DESCRIPTION );

		String queryResultTypeName = null;
		node.name(description);		
		node.tooltip(description);

		if (description == null)
		{
			String resultTypeDescription 	= mainXMLElement.getChild( QUERY_RESULT_TYPE ).getChildTextTrim( DESCRIPTION );;
			node.name(resultTypeDescription);
		}
		
		//System.err.println("QueryResultProcessor.process: " + originalXml );
		
		// determine if we have a Patient Set, Encounter Set, or Patient Count and set name/visual attributes appropriately
		queryResultTypeName =  mainXMLElement.getChild( QUERY_RESULT_TYPE ).getChildTextTrim( NAME ).toLowerCase();
		if ( queryResultTypeName.startsWith("patientset") )					// this is patient set
		{
			id = id.replaceFirst( PATIENT_SET_COLL_ID_PREFIX, "" );
			node.fullname( PATIENT_SET_COLL_ID_PREFIX + id );	// set fullname
			node.visualAttribute( ICON_PATIENT_SET ); 			// set visual attribute type
		}
		else if ( queryResultTypeName.startsWith("patient_encounter_set") )	// this is an encounter set
		{
			id = id.replaceFirst( PATIENT_SET_ENC_ID_PREFIX, "" );
			node.fullname( PATIENT_SET_ENC_ID_PREFIX + id );	// set fullname
			node.visualAttribute( ICON_ENCOUNTER_SET ); 		// use the code "ENC" to indicate encounter set
		}
		else if ( queryResultTypeName.startsWith("patient_count_xml") )		// this is a patient count
			return new QueryConceptTreeNodeFactoryProduct( true, "You cannot drop that here.", "A '" + PATIENT_COUNT_TYPE + "' cannot be dropped inside a Group Panel. (QueryResultProcessor.process())" );
		else
			return new QueryConceptTreeNodeFactoryProduct( true, "You cannot drop that here.", "A '" + queryResultTypeName + "' cannot be dropped inside a Group Panel. (QueryResultProcessor.process())" );

		node.finalizeOriginalXML( originalXml );
		newNodes.add( node );
		return new QueryConceptTreeNodeFactoryProduct(newNodes);
	}
}
