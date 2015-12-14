package edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.Element;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.query.data.DataUtils;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.processor.ProcessorConst;

public class XMLCreator implements ProcessorConst
{
	public static final String	XML_HEADER 			= "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	
	public static String makePreviousQueryXML( QueryConceptTreeNodeData node ) throws IOException
	{
		Document doc = new Document( new Element( PLUGIN_DRAG_DROP, DND_PREFIX, DND_NAMESPACE ) );
		Element qmasterElement 	= new Element( QUERY_MASTER, CRC_PMS_PREFIX, CRC_PMS_1_1_NAMESPACE );

		Element queryMasterID	= new Element( QUERY_MASTER_ID );
		queryMasterID.setText( node.fullname().replaceFirst( MASTER_ID_PREFIX, "") );

		Element name			= new Element( NAME );
		name.setText( node.name() );

		Element user_id			= new Element( "user_id" );
		user_id.setText( UserInfoBean.getInstance().getUserName() );

		Element group_id		= new Element( "group_id" );
		group_id.setText( UserInfoBean.getInstance().getProjectId() );

		qmasterElement.addContent( queryMasterID );
		qmasterElement.addContent( name );
		qmasterElement.addContent( user_id );
		qmasterElement.addContent( group_id );

		doc.getRootElement().addContent( qmasterElement );

		StringWriter writer = new StringWriter();
		DataUtils.prettyPrintXMLDoc( doc, writer );

		return writer.toString();
	}
	
	public static String makeEncounterSetXML( QueryConceptTreeNodeData node ) throws IOException
	{
		Document doc = new Document( new Element( PLUGIN_DRAG_DROP, DND_PREFIX, DND_NAMESPACE ) );
		Element queryResultInstanceElement 	= new Element( QUERY_RESULT_INSTANCE, CRC_PMS_PREFIX, CRC_PMS_1_1_NAMESPACE );

		Element resultInstanceID	= new Element( RESULT_INSTANCE_ID );	// probably the most important tag
		resultInstanceID.setText( node.fullname() );

		Element resultDescription	= new Element( DESCRIPTION );
		resultDescription.setText( node.name() );

		Element queryResultType	= new Element( QUERY_RESULT_TYPE );

		Element resultTypeID	= new Element( RESULT_TYPE_ID );
		resultTypeID.setText("2"); // result type id for encounter set is 2

		Element name	= new Element( NAME );
		name.setText( PATIENT_ENCOUNTER_SET );

		Element displayType 	= new Element( DISPLAY_TYPE );
		displayType.setText( LIST );

		Element visualAttributeType = new Element( VISUAL_ATTRIBUTE_TYPE );
		visualAttributeType.setText( ICON_ENCOUNTER_SET );

		Element description = new Element( DESCRIPTION );
		description.setText( ENCOUNTER_SET );

		queryResultType.addContent( resultTypeID );
		queryResultType.addContent( name );
		queryResultType.addContent( displayType );
		queryResultType.addContent( visualAttributeType );
		queryResultType.addContent( description );

		queryResultInstanceElement.addContent( resultInstanceID );
		queryResultInstanceElement.addContent( resultDescription );
		queryResultInstanceElement.addContent( queryResultType );

		doc.getRootElement().addContent( queryResultInstanceElement );

		StringWriter writer = new StringWriter();
		DataUtils.prettyPrintXMLDoc( doc, writer );

		return writer.toString();
	}

	public static String makePatientSetXML( QueryConceptTreeNodeData node ) throws IOException
	{
		Document doc = new Document( new Element( PLUGIN_DRAG_DROP, DND_PREFIX, DND_NAMESPACE ) );
		Element queryResultInstanceElement 	= new Element( QUERY_RESULT_INSTANCE, CRC_PMS_PREFIX, CRC_PMS_1_1_NAMESPACE );

		Element resultInstanceID	= new Element( RESULT_INSTANCE_ID );	// probably the most important tag
		resultInstanceID.setText( node.fullname() );

		Element resultDescription	= new Element( DESCRIPTION );
		resultDescription.setText( node.name() );

		Element queryResultType	= new Element( QUERY_RESULT_TYPE );

		Element resultTypeID	= new Element( RESULT_TYPE_ID );
		resultTypeID.setText("3"); // result type id for patient set is 3

		Element name	= new Element( NAME );
		name.setText( PATIENTSET );

		Element displayType 	= new Element( DISPLAY_TYPE );
		displayType.setText( LIST );

		Element visualAttributeType = new Element( VISUAL_ATTRIBUTE_TYPE );
		visualAttributeType.setText( L );

		Element description = new Element( DESCRIPTION );
		description.setText( DESCRIPTION_PATIENT_SET );

		queryResultType.addContent( resultTypeID );
		queryResultType.addContent( name );
		queryResultType.addContent( displayType );
		queryResultType.addContent( visualAttributeType );
		queryResultType.addContent( description );

		queryResultInstanceElement.addContent( resultInstanceID );
		queryResultInstanceElement.addContent( resultDescription );
		queryResultInstanceElement.addContent( queryResultType );

		doc.getRootElement().addContent( queryResultInstanceElement );

		StringWriter writer = new StringWriter();
		DataUtils.prettyPrintXMLDoc( doc, writer );

		return writer.toString();
	}

	public static String makePatientXML( QueryConceptTreeNodeData node ) throws IOException
	{
		Document doc = new Document( new Element( PLUGIN_DRAG_DROP, DND_PREFIX, DND_NAMESPACE ) );
		Element patientsetNode 	= new Element( PATIENT_SET, DND_PREFIX, DND_NAMESPACE );

		Element patientNode		= new Element( PATIENT.toLowerCase() );
		Element patientIDNode 	= new Element( PATIENT_ID );
		int lastIndexOfColon	= node.fullname().lastIndexOf(":");
		patientIDNode.setText( node.fullname().substring( lastIndexOfColon + 1) );

		patientsetNode.addContent( patientNode );
		patientNode.addContent( patientIDNode );

		doc.getRootElement().addContent( patientsetNode );

		StringWriter writer = new StringWriter();
		DataUtils.prettyPrintXMLDoc( doc, writer );

		return writer.toString();
	}

}
