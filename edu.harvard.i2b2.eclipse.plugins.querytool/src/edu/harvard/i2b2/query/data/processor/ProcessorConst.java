package edu.harvard.i2b2.query.data.processor;

public interface ProcessorConst 
{
	// OntologyTermProcessor and QueryConceptTreeNode
	public static final String	ONT_NAMESPACE 			= "http://www.i2b2.org/xsd/cell/ont/1.1/";		// refers to the ontology namespace
	public static final String	DND_NAMESPACE			= "http://www.i2b2.org/xsd/hive/plugin/";		// refers to the plugin dnd namespace
	public static final String	CRC_PMS_1_1_NAMESPACE 	= "http://www.i2b2.org/xsd/cell/crc/psm/1.1/";	// refers to CRC PMS namespace
	
	public static final String 	ONT_PREFIX			= "ont";
	public static final String 	DND_PREFIX			= "dnd";
	public static final String	CRC_PMS_PREFIX		= "crc_pms";

	public static final String	MESSAGE_BODY		= "message_body";
	
	/* XML tag that defines the type a QueryConceptTreeNodeData can be 
	 *		see QueryConceptTreeNodeDataFactory.java 
	 */
	public static final String	FOLDERS					= "folders";	
	public static final String	QUERY_RESULT_INSTANCE 	= "query_result_instance";
	public static final String	PATIENT_SET				= "patient_set";
	public static final String	QUERY_MASTER			= "query_master";
	public static final String	CONCEPTS 				= "concepts";
	public static final String	MODIFIERS				= "modifiers";
	
	public static final String	PLUGIN_DRAG_DROP		= "plugin_drag_drop";
	public static final String	CONCEPT 				= "concept";
	public static final String	NULL					= "null";
	public static final String	MODIFIER				= "modifier";
	public static final String	APPLIED_PATH			= "applied_path";
	public static final String	KEY						= "key";
	public static final String	NAME					= "name";
	public static final String	VISUAL_ATTRIBUTES		= "visualattributes";
	public static final String  VISUAL_ATTRIBUTE_TYPE 	= "visual_attribute_type";
	public static final String	TOOL_TIP				= "tooltip";
	public static final String	LEVEL					= "level";
	public static final String 	DIM_CODE				= "dimcode";
	
	public static final String	METADATA_XML		= "metadataxml";
	public static final String	VALUE_METADATA		= "ValueMetadata";
	public static final String	DATA_TYPE			= "DataType";
	public static final String	ENUM				= "Enum";
	public static final String	ENUM_VALUES			= "EnumValues";
	public static final String	UNIT_VALUES			= "UnitValues";
	public static final String	NORMAL_VALUES		= "NormalUnits";
		
	public static final String	OK_TO_USE_VALUES	= "Oktousevalues";
	public static final String	FLAGS_TO_USE		= "Flagstouse";

	public static final String	EQUAL_UNITS			= "EqualUnits";
	public static final String	CONVERTING_UNITS	= "ConvertingUnits";
	public static final String	UNITS				= "Units";
	public static final String	MULTIPLYING_FACTOR	= "MultiplyingFactor";
	public static final String	STRING				= "String";
	public static final String 	LARGE_STRING		= "Large" + STRING;
	public static final String	MAX_STRING_LENGTH	= "MaxStringLength";
	
	// QueryMasterProcesser
	public static final String QUERY_MASTER_ID 		= "query_master_id";
	public static final String MASTER_ID_PREFIX 	= "masterid:"; 
	public static final String PREV_QUERY_PREFIX	= "(PrevQuery)";
	public static final String PQ					= "PQ";
	
	// PatientSetProcessor
	public static final String 	PATIENTS 	= 	"patient";
	public static final String 	PATIENT_ID	= 	"patient_id";
	public static final String 	SOURCE		= 	"source";
	public static final String	HIVE		= 	"HIVE";
	public static final String	PATIENT		= 	"PATIENT";
	public static final String	PT			=	"PT";

	//QueryResultProcessor
	/* see i2b2 CRC server code: edu.harvard.i2b2.crc.util.ItemKeyUtil for more details*/
	public static final String	PATIENT_SET_COLL_ID_PREFIX	=	"patient_set_coll_id:";	// a node is a patient set	
	public static final String	PATIENT_SET_ENC_ID_PREFIX	=	"patient_set_enc_id:";	// a node is an encounter set
	public static final String 	PATIENT_PREFIX				= 	"patient:";				// a node is a patient
	public static final String 	ENCOUNTER_PREFIX			= 	"encounter:";			// a node is an encounter
	public static final String 	CELL_URL_PREFIX				= 	"cellurl:";				// a node is a cell URL
		
	public static final String	RESULT_INSTANCE_ID 			= 	"result_instance_id";
	public static final String	DESCRIPTION					=	"description";
	public static final String	QUERY_RESULT_TYPE			= 	"query_result_type";
	public static final String	RESULT_TYPE_ID				=	"result_type_id";
	public static final String	PATIENT_COUNT				= 	"PATIENT_COUNT_XML";
	public static final String	ENCOUNTER_SET				= 	"Encounter Set";
	public static final String	PATIENT_ENCOUNTER_SET		=	"PATIENT_ENCOUNTER_SET";
	public static final String	DISPLAY_TYPE				=	"display_type";
	public static final String	LIST						=	"LIST";
	public static final String	PATIENTSET					=	"PATIENTSET";
	public static final String	DESCRIPTION_PATIENT_SET		=	"Patient set";
	
	public static final String	L	= 	"L";
	public static final String	LA	=	"LA";
	public static final String	ICON_ENCOUNTER_SET	=	"ENCS";
	public static final String	ICON_PATIENT_SET	=	"PATS";
	public static final String	ICON_WORKING		=	"WORKING";
	public static final String 	ICON_WARNING 		= 	"WARNING";
	
	//FoldersProcessor
	public static final String DONE			= 	"DONE";
	public static final String MAX_EXCEEDED = 	"MAX_EXCEEDED";
	public static final String ERROR		=	"ERROR";
	public static final String ERROR_MESSAGE=	"errorMessage";	

	// DragAndDrop2XML 
	public static final String  ITEM_NAME_PATIENT_SET_PREFIX		=	"patient set";			// if an ItemType.getItemName starts with "Number of Patients" it's a Patietn Set
	public static final String  ITEM_NAME_PATIENT_COUNT_PREFIX		=	"number of patients";	// if an ItemType.getItemName starts with "Number of Patients" it's a Number of Patients node

}
