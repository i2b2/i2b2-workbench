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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


public interface UIConst 
{
	public static final String 	NAME_QUERY_TEXT 				= "Name your query:";
	public static final String 	SELECT_ANALYSIS_TYPES_TEXT 		= "Select desired analysis types:";
	
	public static final String	DROP_A_PREV_QUERY_HERE	= "Drop a Previous Query Here";

	public static final String	NON_TEMPORAL_QUERY_MODE = "Specify Non-Temporal Query";
	public static final String	TEMPORAL_QUERY_MODE 	= "Specify Temporal Query";
	public static final String	GET_EVERYONE			= "Get Everyone";
	public static final String	ALL_PATIENTS			= "All Patients";	// old query tool uses this instead of "Get Everyone"

	public static final String 	SUBMIT_QUERY			= "Submit Query";

	// QueryToolMainUI
	public static final String	PROCESSING				= "Processing";
	public static final String	CRC_DOWN_MESSAGE		= "The CRC Cell is unavailable. Queries cannot be submitted or processed.";
	public static final String  CLEARQUERY				= "Reset Query";
	public static final String  CLEAR_QUERY_TOOLTIP_TEXT= "Clear existing query and start over";
	// Basic Query Mode Panel
	public static final String 	ADD_GROUP				= "Add Group";
	public static final String	PATIENTS				= "Patients";
	public static final String	ENCOUNTERS				= "Encounters";
	public static final String	PATIENT					= "Patient";
	public static final String	ADD_GROUP_INSTRUCTIONS 	= "Add and Edit Groups to define your population.\nLeave all Groups empty to use all data in the database.";
	public static final String	QUERY_DROP_INSTRUCTIONS_1 	= "Drop a Previous Query (";
	public static final String	QUERY_DROP_INSTRUCTIONS_2 	= ") here to load it (overwrites your current query).";
	
	// Group Binding Control Panel
	public static final String 	GROUP_BOUNDED_BY		= "Groups Bounded by...";
	public static final String 	BOUND_BY_PATIENT		= "Patient";
	public static final String 	BOUND_BY_ENCOUNTER_OR_OBSERVATION	= "Encounter/Observation";

	public static final String 	BOUND_BY_PATIENT_EXPLANATION	= "Terms defined in Groups occurs in the same patient, but not necessarily bound to a financial encounter or an observation.";
	public static final String 	BOUND_BY_ENCOUNTER_OR_OBSERVATION_EXPLANATION	= "Terms in Groups selected below are bound to the same financial encounter or addtionally observation (if applicable). Terms in unselected Groups are still bound by Patient.";

	public static final String 	OBSERVATION 			= "Observation";
	public static final String 	ENCOUNTER 				= "Encounter";	

	public static final String	SAMEVISIT				= "SAMEVISIT"; // for QueryTiming and PanelTiming in QueryDefinitionType
	
	// Date Constraint Control Panel
	public static final String	INCLUSIVE				= "(inclusive)";
	public static final String 	DATE_CONSTRAINT			= "Date Constraints";
	public static final String	GROUP_SPECIFIC			= "Group-Specific";
	public static final String	QUERY_WIDE				= "Query-Wide";
	public static final String	EVENT_WIDE				= "Event-Wide";
	public static final String	DATE_FORMAT				= "mm/dd/yyyy";
	public static final String	TO						= "to";
	public static final String	FROM					= "From";
	public static final String	NO_CONSTRAINTS			= "No Date Constraints";
	public static final String	CLICK_TO_CHANGE			= "(Click to Change)";
	public static final String	NEW_LINE				= "\n";
	public static final String 	UP_TO_AND_INCLUDING		= "Anytime\nbefore the end of\n";
	public static final String 	STARTING_AND_AFTER		= "and\nanytime after";

	// Date Constraint Dialog
	public static final String 	START_DATE				= "Start Date " + INCLUSIVE + ":";
	public static final String	END_DATE				= "End Date " + INCLUSIVE + ":";
	public static final String 	OK						= "OK";
	public static final String 	CANCEL					= "Cancel";
	public static final String 	NONE					= "None";
	public static final String	NOT_A_LEGAL_DATE		= "Not a legal date";
	public static final String 	START_DATE_AFTER_END_DATE = "Start Date is after End Date";	
	public static final String 	SPECIFY_DATE_CONSTRAINTS= "Specify Date Constraints";
	public static final int		NUM_CHARS_IN_DATE_DISPLAY = 23;
	
	// Number Constraint Display/Dialog	
	public static final int		EQUAL					= 0;
	public static final int		GREATER_THAN			= 1;
	public static final int		LESS_THAN				= 2;
	public static final String	SPECIFY_OCCURRENCE_CONSTRAINTS = "Specify Occurrence Constraints";
	public static final String 	TERMS_MUST_OCCUR		= "Terms must occur ";
	public static final String 	TIMES					= "Times";
	public static final int		NUM_CHARS_IN_NUMBER_DISPLAY = 11;
	
	// Group Binding Selection Dialog
	public static final String	SPECIFY_GROUPBINDING	= "Specify Group Binding";
	// Group Binding Information Dialog
	public static final String	GROUPBINDING_EXPLANATION= "Explanation of Group Binding choices";
	public static final String	CLOSE					= "Close";

	// Group Panel
	public static final String 	GROUP						= "Group";
	public static final String 	DRAG_ONLOTOGY_TERMS_HERE1	= "Drop Ontology Terms, Previous Queries, or Query Results here";
	
	public static final int 	DEFAULT_GROUPPANEL_HEIGHT 		= 90;
	public static final int		DEFAULT_GROUPPANEL_MAX_HEIGHT	= 224; // about 11 items in the GroupPanel
	public static final int 	SHORT_GROUPPANEL_HEIGHT 		= 70;	
	public static final String  SAME_OBSERVATION			= "Same " + OBSERVATION;
	public static final String  SAME_ENCOUNTER				= "Same " + ENCOUNTER;

	// Group Panel Concept Right-Click Actions
	public static final String	DELETE				= "Delete";			// DELETE the QueryConceptNodeData
	public static final String	EDIT_VALUE			= "Edit Value...";	// EDIT the value of the QueryConceptNodeData

	// Event List Control Panel
	public static final String	EVENT				= "Event";
	public static final String	EVENT_LIST			= "Events in Query";
	public static final String	ADD_EVENT			= "Add Event";
	public static final String	EVENT_LIST_INSTRUCTIONS		= "Add and Edit Events here. Use the interface to right to describe temporal relationshps among the Events.";
	
	// Event Inclusion
	public static final String 	EVENT_INCLUSION		= "Event Inclusion";
	public static final String 	ONLY_EVENTS_USED_IN_RELATIONSHIPS = "Only query for Events used in Temporal Relationships";

	// TEMPORAL QUERY MODE PANEL
	public static final String DEFINE_RELATIONSHIPS 				= "Define Temporal Relationships Among Events";
	public static final String DEFINE_EVENT							= "Define Event";
	//public static final String POPULATION_COTROL_PANEL				= "Edit Population";
	public static final String EDIT_TEMPORAL_RELATIONSHIPS 			= "Edit Temporal Relationships";
	public static final String EDIT_TEMPORAL_RELATIONSHIPS_TOOLTIP 	= "Go to the Temporal Relationship Editor.\nYour current Event will be automatically saved.";
	
	// DEFINE TEMPORAL RELATIONSHP PANEL 
	public static final String DEFINE_TEMPORAL_RELATIONSHIP_INSTRUCTIONS = "Add Temporal Relationships among Events defined in the panel to the left. ";

	// TEMPORAL RELATIONSHIP PANEL
	public static final String	START_0F			= "Start of";
	public static final String	END_OF				= "End of";
	public static final String	FIRST_EVER			= "the First Ever";
	public static final String	LAST_EVER			= "the Last Ever";
	public static final String	ANY					= "Any";
	public static final String	BEFORE				= "Occurs Before";
	public static final String	ON_OR_BEFORE		= "on or Before";
	public static final String	EQUALS				= "Equals";
	public static final String	ON_OR_AFTER			= "Occurs on or After";
	public static final String	AFTER				= "Occurs After";
	public static final String	GREATER_THAN_SYM			= ">";
	public static final String	GREATER_THAN_EQUALS_TO_SYM	= "\u2265";
	public static final String	EQUAL_SYM					= "=";
	public static final String	LESS_THAN_EQUALS_TO_SYM		= "\u2264";
	public static final String	LESS_THAN_SYM				= "<";
	public static final String 	ELEMENT_OF					= "\u2208";

	public static final String	SECONDS				= "second(s)";
	public static final String	MINUTES				= "minute(s)";
	public static final String 	HOURS				= "hour(s)";
	public static final String	DAYS				= "day(s)";
	public static final String	MONTHS				= "month(s)";
	public static final String	YEARS				= "year(s)";

	public static final String [] EVENT_MARKERS 	= {START_0F, END_OF};
	public static final String [] EVENT_OCCURRENCES = {FIRST_EVER, LAST_EVER, ANY};
	public static final String [] EVENT_RELATIONSHIP= {BEFORE, ON_OR_BEFORE, EQUALS, ON_OR_AFTER, AFTER};
	public static final String [] TEMPORAL_OPERATORS= {GREATER_THAN_SYM, GREATER_THAN_EQUALS_TO_SYM, EQUAL_SYM, LESS_THAN_EQUALS_TO_SYM, LESS_THAN_SYM};
	public static final String [] TEMPORAL_UNITS	= {SECONDS, MINUTES, HOURS, DAYS, MONTHS, YEARS};
	public static final String BY_BUTTON			= "By";
	public static final String AND_BUTTON			= "And";
	public static final String ADD_TEMPORAL_RELATIONSHIP = "Add Temporal Relationship";

	// QUERY RESULT TYPE DIALOG
	public static final String SPECIFY_QUERY_RESULT_TYPE 	= 	"Select Desired Analysis Types for Your Query";
	public static final String DO_NOT_SUBMIT_QUERY 			= 	"Cancel";
	public static final String REMEMBER_MY_RESULT_TYPES 	= 	"Remember and don't ask again ";
	
	// CONSOLIDATED VALUE CONSTRAINT DIALOG
	public static final String		SPECIFY_VALUE_CONSTRAINTS = "Specify Value Constraints to Concepts and Modifiers";

	// NUMERIC VALUE CONSTRAINT PANEL
	public static final String		BETWEEN					= "Between";
	public static final String [] 	HIGH_LOW_FLAGS			= {"High", "Low"};
	public static final String [] 	VALUE_OPERATORS			= {"Less than (<)", "Less than or equal to (\u2264)", "Equal to (=)", BETWEEN, "Greater than or equal to (\u2265)", "Greater than (>)" };

	public static final String 		NO_VALUE				= "No Value";
	public static final String		NUMERIC_FLAG_VALUE		= "By High/Low Flag";
	public static final String		NUMERIC_VALUE			= "By Numeric Value";
	public static final String		SELECT_A_FLAG_VALUE		= "Select a Flag Value:"; 
	public static final String		SELECT_A_NUMERIC_VALUE	= "Select an operator and value(s):";
	public static final String		SELECT_A_UNIT			= "Select a unit:";
	public static final String		ZERO_STRING				= "0";
	public static final String		NEXT					= "Next";
	public static final String		PREVIOUS				= "Previous";
	public static final String		FINISH					= "Finish";

	// ENUM VALUE CONSTRAINT PANEL
	public static final String		ABNORMAL_FLAG_VALUE		= "By Abnormal Flag";
	public static final String		ENUM_VALUE				= "By Text Value";

	// TEXT BLOB VALUE CONSTRAINT PANEL
	public static final String		ENTER_TEXT_SEARCH_TERM	= "Enter Text Value to Search:";
	public static final String		USE_DB_OPS				= "Use Database Operators (Advanced Search)";
	public static final String		CONTAINING				= "Containing";
	public static final String []	TEXT_OPERATORS			= { CONTAINING, "Exact", "Starting with", "Ending with" };

	public static final String		LIKE_CONTAINS			= "LIKE[contains]";
	public static final String		LIKE_EXACT				= "LIKE[exact]";
	public static final String		LIKE_BEGINS				= "LIKE[begin]";
	public static final String		LIKE_ENDS				= "LIKE[end]";
	public static final String		CONTAINS_DB				= "CONTAINS[database]";
	public static final String		CONTAINS				= "Contains";

	public static final String		LABEL_LIKE_CONTAINS		= CONTAINS;
	public static final String		LABEL_LIKE_EXACT		= "Exact";
	public static final String		LABEL_LIKE_BEGINS		= "Begin with";
	public static final String		LABEL_LIKE_ENDS			= "End with";
	public static final String		LABEL_CONTAINS_DB		= "Contains[DB]";
	public static final String		LABEL_CONTAINS			= CONTAINS;	

	// GENERAL
	public static final int	DIALOG_BORDER_MARGIN 	= 8;
	public static final int	GROUP_PANEL_MARGIN		= 4;
	public static final int	EVENT_LABEL_MARGIN		= 4;
	public static final int RELATIONSHIP_COMBO_MARGIN = 12;
	public static final int RELATIONSHIP_COMBO_VERTICAL_SPACING = 6;
	public static final int RELATIONSHIP_COMBO_SPACING = 4;
	public static final int	TITLE_HEIGHT			= 18;
	public static final int	DECISION_HEIGHT			= 24;

	public static final int	MINOR_TITLE_HEIGHT		= 18;

	// Drag and Drop
	public static final int DND_DRAG_OPS				= DND.DROP_COPY;
	public static final int DND_DROP_OPS				= DND.DROP_COPY;
	public static final Transfer	DND_TEXT_TRANSFER	= TextTransfer.getInstance();
	public static final Transfer[]	DND_TRANSFER_TYPES	= new Transfer[] { DND_TEXT_TRANSFER };

}
