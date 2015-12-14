package edu.harvard.i2b2.query.data;

import org.jdom.filter.Filter;

import edu.harvard.i2b2.query.data.processor.ProcessorConst;

public interface DataConst extends ProcessorConst
{
	public static final String	TRUE			= "true";
	
	public enum GroupBinding { BY_PATIENT, BY_ENCOUNTER, BY_OBSERVATION }
	public static final String	ANY 			= "ANY";
	public static final String	SAME_VISIT		= "SAMEVISIT";
	public static final String	SAME_INSTANCE	= "SAMEINSTANCENUM";
	

	/*
	 * SUPPLEMENTAL XML constants used for drag/drop to 
	 * 	(1) preserve ValueProperty values.
	 * 	(2) prevent unnecessary ONT calls.
	 * 
	 */
	// supplemental tag added to QueryConceptTreeNodeData when they are being dragged into panels so we know if they need ONT call to update their XML
	public static final String	TAG_IS_XML_COMPLETE				= "isXMLComplete";
	public static final String	TAG_ORIGINAL_XML_IN_ITEM_TYPE 	= "originalXML";	// used in itemType's XML
	
	public static final String	DRAG_AND_DROP		= "plugin_drag_drop";
	public static final String	PANEL				= "panel";	// for identifying the Panel Element in a DND xml when dropping GroupPanels
	public static final String	ITEM				= "item";	// for identifying the Item Element in a DND xml when dropping GroupPanels
	public static final String	QUERY_MASTER_TYPE 	= "QueryMasterType"; // for identifying the QueryMasterType after dropping a previous query
	
	public static final Filter	DRAG_AND_DROP_TAG_FILTER	= DataUtils.makeTagFilter( DRAG_AND_DROP );
	public static final Filter	CONCEPTS_TAG_FILTER 		= DataUtils.makeTagFilter( CONCEPTS );
	public static final Filter	PANEL_TAG_FILTER 			= DataUtils.makeTagFilter( PANEL );
	public static final Filter	ITEM_TAG_FILTER				= DataUtils.makeTagFilter( ITEM );

	

	/* for displaying Value Constraint constants */
	// Numeric Value Constraint
	public static final String HIGH_FLAG_VALUE 		= "H";
	public static final String HIGH_FLAG_NAME 		= "= High";
		
	public static final String LOW_FLAG_VALUE 		= "L";
	public static final String LOW_FLAG_NAME 		= "= Low";
	
	// Enum Value Constraint
	public static final String ABNORNAL_FLAG_VALUE 	= "A";
	public static final String ABNORMAL_FLAG_NAME 	= "= Abnormal";
}
