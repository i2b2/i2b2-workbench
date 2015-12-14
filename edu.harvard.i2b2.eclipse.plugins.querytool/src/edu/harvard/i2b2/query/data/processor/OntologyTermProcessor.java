package edu.harvard.i2b2.query.data.processor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.Filter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.query.data.DataConst;
import edu.harvard.i2b2.query.data.DataUtils;
import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeFactoryProduct;
import edu.harvard.i2b2.query.data.UnitsData;

public class OntologyTermProcessor implements DataConst
{
	
	/*
	 * DEAL WITH THE CONCEPTS INCLUDED IN THE DROP
	 * 
	 * 	mainXMLElement represents the <concepts> tag
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static QueryConceptTreeNodeFactoryProduct process( Document xmlDoc, Element mainXMLElement, String originalXml )
	{
		Element root = xmlDoc.getRootElement();
		Element newRoot = (Element)root.clone();	// make new Doc
		newRoot.removeContent();					// empty root
		List content = root.getContent( CONCEPTS_TAG_FILTER );
		// content's first element should be the <concepts> tag. Clone it.		
		Element conceptsTag = (Element)((Element)content.get(0)).clone();
		conceptsTag.removeContent(); 				// empty content
		newRoot.addContent( conceptsTag );			// add empty <concepts> to root.
		Document emptyDoc = new Document();			// make new Document that contains only the <concepts> tag
		emptyDoc.setRootElement( newRoot );
		
		ArrayList<QueryConceptTreeNodeData> newNodes = new ArrayList<QueryConceptTreeNodeData>();
		List conceptChildren = mainXMLElement.getChildren();
		for (Iterator itr = conceptChildren.iterator(); itr.hasNext();) 
		{
			Element conceptElement = (Element) itr.next();
			// check for badness
			if (isConceptElementAFolder( conceptElement )) 
				return new QueryConceptTreeNodeFactoryProduct(null, true, UIUtils.CANNOT_DROP_MSG, "It is only used for organizing lists");			
			if (isConceptElementRootNode( conceptElement ))
				return new QueryConceptTreeNodeFactoryProduct(null, true, UIUtils.CANNOT_DROP_MSG, "coceptText is 'null' -- Did you try to drop the root node? You cannot do that here.");
			QueryConceptTreeNodeData node = constructQueryConceptTreeNodeData( null, conceptElement );	// construct a QueryConceptTreeNodeData (may be ModifierData!)
			// check to see if xml is complete
			try
			{
				// create an empty document that has <dnd:plugin_drag_drop> followed by <concepts> followed by the <concept> in question
				Document 	singleConceptDoc	= (Document)emptyDoc.clone();
				Element 	newConceptElement	= (Element)conceptElement.clone();
				newConceptElement.detach();																								// detach from parent
				((Element)singleConceptDoc.getRootElement().getContent( CONCEPTS_TAG_FILTER ).get(0)).addContent( newConceptElement );	// add to the document
				
				// for printing uses
				//StringWriter writerz = new StringWriter();
				//DataUtils.prettyPrintXMLDoc( singleConceptDoc, writerz );
				//System.err.println("OntologyTermProcessor.process: singleConceptDocument: \n" + writerz.toString() );

				
				completeXMLIfNecessary( singleConceptDoc, conceptElement, node );
				// now the node is guaranteed to have the comlpete XML, we parse it to set the Value Property schema
				node.parseOriginalXMLAndSetValuePropertySchema();
			}
			catch ( IOException e )
			{ 
				e.printStackTrace(); 
			}
			catch ( JDOMException e )
			{ 
				e.printStackTrace(); 
			}
			newNodes.add(node);	// add node with full XML
		}
		return new QueryConceptTreeNodeFactoryProduct( newNodes );
	}

	/*
	 * Construct QueryConceptTreeNodeData from a <concept> node but not retrieving metadataXML information from ONT
	 */
	public static QueryConceptTreeNodeData constructQueryConceptTreeNodeData( QueryConceptTreeNodeData parent, Element conceptElement )
	{
		String toolTip = extractToolTip( conceptElement );				// set tooltip
		Element modifierTag = conceptElement.getChild( MODIFIER );		// determine whether concept is a modifier
		QueryConceptTreeNodeData node =  makeQueryConceptTreeNodeData( 	parent, 		// parent QueryConceptTreeNodeData
																		modifierTag, 
																		conceptElement.getChild( NAME ).getText().trim(),     			//name 
																		conceptElement.getChild( NAME ).getText().trim(), 				//title
																		conceptElement.getChild( VISUAL_ATTRIBUTES ).getText().trim(),
																		toolTip, 
																		conceptElement.getChild( LEVEL ).getText().trim(), 
																		conceptElement.getChild( KEY ).getText().trim(), 
																		null ); // create a concept node and give it a default (null) originalXML, preventing finalizing originalXML in the constructor
		autoSetVisualAttributeForConcepts( node );	// autoset the visual attributes
		return node;
	}
	
	/* Check a <Concept> XML node to see if it represents a Folder object from Workplace 
	 *  - using visual attributes to check is lame, but it's the best info we got */
	public static boolean isConceptElementAFolder( Element conceptElement )
	{ return conceptElement.getChild( VISUAL_ATTRIBUTES ).getText().trim().toUpperCase().startsWith("C"); }
	
	/* The RootNode of an ontology does not have concept text */
	public static boolean isConceptElementRootNode( Element conceptElement )
	{
		String conceptText = conceptElement.getText().trim();
		return conceptText.equals( NULL ); // this is root level node
	}

	/* conceptElement.getChild( TOOL_TIP ) may be null, as in some custom ontology terms. If it's null, we use empty String ""; */
	public static String extractToolTip( Element conceptElement )
	{
		String toolTip = "";
		if ( conceptElement.getChild( TOOL_TIP ) != null )
			toolTip = conceptElement.getChild( TOOL_TIP ).getText();	// tooltip not null, use it!
		else if ( conceptElement.getChild( DIM_CODE ) != null )
			toolTip = conceptElement.getChild( DIM_CODE ).getText();	// no tooltip available, try using Dimcode
		else
			toolTip = conceptElement.getChild( NAME ).getText();		// defaults to using Name
		return toolTip;
	}

	/* check to see if the conceptElement is complete (should have the tag <isXMLComplete>true<isXMLComplete>)*/
	@SuppressWarnings("unchecked")
	public static boolean isConceptXMLComplete( Element conceptElement )
	{
		
		List <Element> xmlCompleteTags = (List<Element>)conceptElement.getContent( DataUtils.makeTagFilter( QueryConceptTreeNodeData.TAG_IS_XML_COMPLETE )  );
		return (xmlCompleteTags.size() > 0 && xmlCompleteTags.get(0).getValue().equalsIgnoreCase("true"));
	}

	/* automatically set visual attributes for nodes that are dropped from ONT or expanded in the tree */
	public static void autoSetVisualAttributeForConcepts( QueryConceptTreeNodeData node )
	{
		if ( !node.visualAttribute().startsWith("F") ) // if concept is not a folder, set it as a leaf (since the visual_attributes from conceptElement is not reliable, we do this check)
			node.visualAttribute( "M" );
	}

	public static void completeXMLIfNecessary( Document singleConceptDoc, Element conceptElement, QueryConceptTreeNodeData node ) throws IOException
	{
		// check to see if the <concpet> has complete XML
		boolean isXMLComplete = isConceptXMLComplete( conceptElement );		
		if ( !isXMLComplete ) // call ONT to get the metadataXML because we have incomplete xml
			node.retrieveMetadataXMLfromONTAndSetOriginalXML( singleConceptDoc );
		else // XML is complete. We make a copy of the existing conceptElement and add the copy to the doc and write the document out and save it as originalXML
		{				
			StringWriter writer = new StringWriter();
			DataUtils.prettyPrintXMLDoc( singleConceptDoc, writer );
			node.finalizeOriginalXML( writer.toString() );	// write the full XML into the node's originalXML
		}
	}
	
	/* check to see if the */
	/* PROBABLY UNUSED CODE - INSERT INTO <UNUSED CODE METAATTRIBS>
	Element metadataAttribs = conceptXml.getChild( METADATA_XML );
	Element valuedataAttribs = null;
	if (metadataAttribs != null)
	{
		valuedataAttribs = metadataAttribs.getChild( VALUE_METADATA );
	}
	if ((metadataAttribs != null) && (valuedataAttribs != null)) 
	{
		Element dataTypeElement = valuedataAttribs.getChild( DATA_TYPE );
		if (dataTypeElement != null && dataTypeElement.getTextTrim().equalsIgnoreCase( ENUM )) 
		{
			// add text values to node data
			node.valuePropertyData().hasEnumValue(true);
			Element enumElement = valuedataAttribs.getChild( ENUM_VALUES );
			for (int i = 0; i < enumElement.getChildren().size(); i++) 
			{
				Element valElement = (Element) enumElement.getChildren().get(i);
				String valString = new String(valElement.getTextTrim());
				node.valuePropertyData().enumValues.add(valString);
			}
		}

		if (valuedataAttribs.getChild(OK_TO_USE_VALUES) != null && valuedataAttribs.getChild(OK_TO_USE_VALUES).getText().equalsIgnoreCase("Y"))
			node.valuePropertyData().okToUseValue(true);

		if (valuedataAttribs.getChild(FLAGS_TO_USE) == null || valuedataAttribs.getChild(FLAGS_TO_USE).getText().equalsIgnoreCase(""))
			node.valuePropertyData().okToUseValueFlag(false);

		Element unitElement = valuedataAttribs.getChild(UNIT_VALUES);
		if (unitElement != null) 
		{
			for (int i = 0; i < unitElement.getChildren().size(); i++) 
			{
				Element element = (Element) unitElement.getChildren().get(i);
				if (element.getName().equalsIgnoreCase( NORMAL_VALUES )) 
				{
					String unitString = new String(element.getTextTrim());
					node.valuePropertyData().units.add(new UnitsData(unitString, 1,false));
				}
			}
		}
	}
	*/

	
	public static QueryConceptTreeNodeData makeQueryConceptTreeNodeData( QueryConceptTreeNodeData parent, Element modifierFlag, String name, String title, String vAttribute, String tooltip, String hLevel, String fullName, String originalXml)
	{
		if ( modifierFlag != null )
		{
			ModifierData mod = new ModifierData( parent, name, title, vAttribute, tooltip, hLevel, fullName, originalXml );
			mod.applied_path(modifierFlag.getChild( APPLIED_PATH ).getText());
			mod.modifier_key(modifierFlag.getChild( KEY ).getText());
			mod.modifier_name(modifierFlag.getChild( NAME ).getText());
			return  mod;
		}
		else
			return new QueryConceptTreeNodeData( parent, name, title, vAttribute, tooltip, hLevel, fullName, originalXml );
	}
}
