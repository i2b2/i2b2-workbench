/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 *     Taowei David Wang
 */

package edu.harvard.i2b2.query.data;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

//import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetModifierInfoType;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetTermInfoType;
//import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetTermInfoType;
import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.querytool.views.MessageUtil;


public class ModifierData extends QueryConceptTreeNodeData 
{
	private static final Log log = LogFactory.getLog(ModifierData.class);
	
	protected String modifier_name;
	protected String modifier_key;
	protected String applied_path;
	
	public ModifierData() 
	{
		super();
		this.isModifier( true );
		valuePropertyData = new ValuePropertyData();
	}
		
	public ModifierData( QueryConceptTreeNodeData parent, String name, String title, String vAttribute, String tooltip, String hLevel, String fullName, String originalXml ) 
	{ 
		super( parent, name, title, vAttribute, tooltip, hLevel, fullName, originalXml );
		this.isModifier( true );
		valuePropertyData = new ValuePropertyData();
	}

	// constructor with 'null' parent
	public ModifierData( String name, String title, String vAttribute, String tooltip, String hLevel, String fullName, String originalXml )
	{ this( null, name, title, vAttribute, tooltip, hLevel, fullName, originalXml ); }

	
	//public boolean hasModifierValue() 						{ return hasModifierValue; }
	public void modifier_name(String str)					{ modifier_name = new String(str); }
	public String modifier_name() 							{ return modifier_name; }
	public void modifier_key(String str) 					{ modifier_key = new String(str); }
	public String modifier_key() 							{ return modifier_key; }	
	public void applied_path(String str) 					{ applied_path = new String(str); }
	public String applied_path() 							{ return applied_path; }
	
	
	//public ValuePropertyData getModifierValueRestriction() 	{ return valuePropertyData; }	
	//public boolean			 hasModifierValueRestriction()	{ return this.hasModifierValueRestriction; }
	
	
	
	
	/* incompleteDoc is an XML document that has the <dnd:plugin_drag_drop> <concepts> <concept> tags. 
	 * 		The <concpet> tag contains the incomplete XML for the concept.
	 * 		We need to make the ontology call to ONT, grab the metadataXML in the form of
	 * 			<message_body> <ont:modifiers> <ont:modifier>, and stick the <ont:modifier> tag and its
	 * 			content as a child of the <concept> tag in incompleteXMLDoc
	 */
	@Override
	public Document retrieveMetadataXMLfromONTAndSetOriginalXML( Document incompleteXMLDoc ) 
	{
		try 
		{			
			// call ONT service to get the full XML for the modifier			
			GetModifierInfoType vocab = new GetModifierInfoType();
			vocab.setHiddens(true);
			vocab.setSynonyms(false);
			vocab.setMax(200);
			vocab.setType("default");
			vocab.setBlob(true);
			vocab.setAppliedPath(applied_path);
			vocab.setSelf(modifier_key);
			String xmlContent = OntServiceDriver.getModifierInfo(vocab, "");
			
			// parse the XML and build a DOM
			java.io.StringReader xmlStringReader = new StringReader(xmlContent);
			SAXBuilder parser = new SAXBuilder();
			Document conceptDoc = parser.build(xmlStringReader);
			
			//bugbug: printouts
			System.err.println( "Modifier XML from ONT" );						// bugbug:
			System.err.println( MessageUtil.prettyFormat( xmlContent, 5) );		// bugbug:
			
			// work with DOM to extract the <ont:concepts> node
			Element elementMsgBody = conceptDoc.getRootElement().getChild( MESSAGE_BODY ); 
			Element modifiersTag = (Element)elementMsgBody.getChild( MODIFIERS , Namespace.getNamespace( ONT_NAMESPACE )); 				// get a copy of the <concepts> tag
			Element modifierTag  = (Element)((Element)modifiersTag.getContent( DataUtils.makeTagFilter( MODIFIER )).get(0)).clone(); 	// we expect only 1 modifier, so we grab the first one			
			modifierTag.detach();
			
			// add the modifierXML as a child to the <concpet> element in incompleteXMLDoc
			Element dndTag		= (Element)incompleteXMLDoc.getContent( DataUtils.DRAG_AND_DROP_TAG_FILTER ).get(0);
			Element conceptsTag = (Element)dndTag.getContent( DataUtils.CONCEPTS_TAG_FILTER ).get(0);
			Element conceptTag = (Element)conceptsTag.getContent( DataUtils.makeTagFilter( CONCEPT )).get(0) ;
			conceptTag.removeChild( MODIFIER );		// remove the incomplete modifier tag if it exists			
			conceptTag.addContent( modifierTag );	// add the complete version
			//conceptTag.removeChild( QueryConceptTreeNodeData.TAG_IS_XML_COMPLETE ); // remove <isXMLComplete> if it exists
			
			// find the <ont:concept> node and set its supplemental <isXMLComplete> tag to "true"
			Element isXMLCoompleteTag = new Element( QueryConceptTreeNodeData.TAG_IS_XML_COMPLETE );	// mark the concept as having a complete XML, add the supplemental tag
			isXMLCoompleteTag.setText( DataConst.TRUE );			
			conceptTag.addContent( isXMLCoompleteTag );
			
			// get a String representation of the XML and set it as the originalXML for this node.
			StringWriter strWriter = new StringWriter();			
			DataUtils.prettyPrintXMLDoc( incompleteXMLDoc, strWriter );	
			this.finalizeOriginalXML( strWriter.toString() );	// set and finalize originalXML
			
			System.err.println("Completed Modifier  XML: ");				// bugbug:
			System.err.println( strWriter.toString() );					// bugbug:			
			//System.err.println("Updated ModifierData ");
			
			strWriter.close();
			
			// return the DOM document for good measure.
			return incompleteXMLDoc;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	
	@Override
	public Document retrieveFullConceptXMLFromONTandSetOriginalXML() 
	{
		try 
		{
			// retrieve concept XML by calling helper method in super class
			Document incompleteXMLDoc = super.doRetrieveMetadataXMLfromONTAsDoc();  
			
			// bugbug: prints
			System.err.println("ModifierData: ConceptDoc:");
			DataUtils.prettyPrintXMLDoc( incompleteXMLDoc, System.err );
			
			// extract a clone of <ont:concepts> from conceptXMLDoc
			//Element conceptsTag = (Element)((Element)((Element)conceptXMLDoc.getContent( DataConst.DRAG_AND_DROP_TAG_FILTER ).get(0)).getContent().get(0)).clone();
			//Element elementMsgBody 	= conceptXMLDoc.getRootElement().getChild( DataConst.DRAG_AND_DROP ); 
			//Element conceptsTag 	= (Element)elementMsgBody.getChild( CONCEPTS , Namespace.getNamespace( ONT_NAMESPACE )).clone(); // get a copy of the <concepts> tag 
			//conceptsTag.detach();

			// create an empty XML Doc and attach the cloned <ont:conepts> to it
			//Document incompleteXMLDoc 	= DataUtils.makeEmptyDNDXMLDocument();			
			//Element dndTag				= (Element)incompleteXMLDoc.getContent( DataUtils.DRAG_AND_DROP_TAG_FILTER ).get(0);
			//dndTag.addContent( conceptsTag );
			
			// bugbug: prints
			//System.err.println("ModifierData: ConceptDoc with Metadata:");
			//DataUtils.prettyPrintXMLDoc( incompleteXMLDoc, System.err );
			
			retrieveMetadataXMLfromONTAndSetOriginalXML( incompleteXMLDoc );
			
			return incompleteXMLDoc;
			
			//Element elementMsgBody = conceptDoc.getRootElement().getChild("message_body");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void parseOriginalXMLAndSetValuePropertySchema() throws JDOMException, IOException
	{
		StringReader xmlStringReader = new StringReader( this.originalXML );
		SAXBuilder parser 	= new SAXBuilder();
		Document conceptDoc = parser.build(xmlStringReader);

		Element conceptsTag = conceptDoc.getRootElement().getChild( CONCEPTS, Namespace.getNamespace( ONT_NAMESPACE ));

		Element conceptTag = (Element)conceptsTag.getChildren().get(0); // get the only Concept
		Element modifierTag = (Element)conceptTag.getContent( DataUtils.makeTagFilter( DataUtils.MODIFIER )).get(0);
		titleName(modifierTag.getChild( NAME ).getText() );

		Element metadataAttribs 	= modifierTag.getChild( METADATA_XML );
		Element valuedataAttribs 	= null;
		if ( metadataAttribs != null )
			valuedataAttribs = metadataAttribs.getChild( VALUE_METADATA );
		
		if ( (metadataAttribs != null) && (valuedataAttribs != null )) 
		{
			this.hasValue( true );
			Element dataTypeElement = valuedataAttribs.getChild( DATA_TYPE );
			if (dataTypeElement != null && dataTypeElement.getTextTrim().equalsIgnoreCase(ENUM)) 
			{
				// add text values to node data
				valuePropertyData.hasEnumValue(true);
				Element enumElement = valuedataAttribs.getChild( ENUM_VALUES );
				for (int i = 0; i < enumElement.getChildren().size(); i++) 
				{
					Element valElement = (Element) enumElement.getChildren().get(i);
					String valString = new String(valElement.getTextTrim());
					valuePropertyData.enumValues.add(valString);
				}
			} 
			else if (dataTypeElement != null && (dataTypeElement.getTextTrim().equalsIgnoreCase( STRING ) || dataTypeElement.getTextTrim().equalsIgnoreCase( LARGE_STRING ))) 
			{
				// add text values to node data
				valuePropertyData.hasStringValue(true);
				Element maxLengthElement = valuedataAttribs.getChild( MAX_STRING_LENGTH );
				String valString = new String(maxLengthElement.getTextTrim());
				if(!valString.equalsIgnoreCase("")) 
					valuePropertyData.searchStrLength(Integer.parseInt(valString));
				if(dataTypeElement.getTextTrim().equalsIgnoreCase( LARGE_STRING )) 
					valuePropertyData.isLongText(true);
			}

			if (valuedataAttribs.getChild( OK_TO_USE_VALUES ) != null && valuedataAttribs.getChild( OK_TO_USE_VALUES ).getText().equalsIgnoreCase("Y")) 
				valuePropertyData.okToUseValue(true);

			if (valuedataAttribs.getChild( FLAGS_TO_USE ) == null || valuedataAttribs.getChild( FLAGS_TO_USE ).getText().equalsIgnoreCase("")) 
				valuePropertyData.okToUseValueFlag(false);

			Element unitElement = valuedataAttribs.getChild( UNIT_VALUES );
			if (unitElement != null) 
			{
				for (int i = 0; i < unitElement.getChildren().size(); i++) 
				{
					Element element = (Element) unitElement.getChildren().get(i);
					if (element.getName().equalsIgnoreCase( NORMAL_VALUES )|| element.getName().equalsIgnoreCase( EQUAL_UNITS )) 
					{
						String unitString = new String(element.getTextTrim());
						valuePropertyData.units.add(new UnitsData(unitString, 1, false));
					}
					else if (element.getName().equalsIgnoreCase( CONVERTING_UNITS )) 
					{
						Element cunitElement = element.getChild( UNITS );
						String unitString = new String(cunitElement.getTextTrim());
						Element mfElement = element.getChild( MULTIPLYING_FACTOR );
						if(mfElement !=null && !mfElement.getTextTrim().equalsIgnoreCase("")) 
						{								
							double mf = Double.parseDouble(mfElement.getTextTrim());
							valuePropertyData.units.add(new UnitsData(unitString, mf, true));
						}
					}
				}
			}
		}
		
	}

	
	
	
	@Override /* Copyable method */
	public ModifierData makeCopy() 
	{
		ModifierData md 		= new ModifierData( this.myParent, this.name, this.titleName, this.visualAttribute, this.tooltip, this.hlevel, this.fullname, this.originalXML );
		md.valueName 			= this.valueName;
		md.hasValue				= this.hasValue;
		md.valuePropertyData 	= this.valuePropertyData.makeCopy();
		
		md.modifier_name		= this.modifier_name;
		md.modifier_key			= this.modifier_key;
		md.applied_path			= this.applied_path;

		for ( QueryConceptTreeNodeData node : this.myChildren )
		{
			QueryConceptTreeNodeData newNode = node.makeCopy();
			newNode.myParent = md; 
			md.myChildren.add( newNode );
		}
		return md;
	}

	
	/*
	public ConstrainByModifier writeModifierConstraint() 
	{		
		ConstrainByModifier modifierConstraint = new ConstrainByModifier();
		modifierConstraint.setAppliedPath(applied_path);
		modifierConstraint.setModifierKey(modifier_key);
		modifierConstraint.setModifierName(modifier_name);		
		// handle value constraint
		if (!modifierValuePropertyData.noValue()) {
			ConstrainByModifier.ConstrainByValue valueConstrain = modifierValuePropertyData.writeModifierValueConstrain();
			modifierConstraint.getConstrainByValue().add(valueConstrain);
		}		
		return modifierConstraint;
	}
	*/
		
	/* bugbug: unused so commented out
	public void setModifierValueConstraint(List<ConstrainByModifier.ConstrainByValue> list) 
	{
		if (list != null && list.size() > 0) 
		{
			hasValue(true);
			ConstrainByModifier.ConstrainByValue cons = list.get(0);
			if (cons.getValueConstraint() == null) 
			{
				// hasValue(false);
				return;
			}
			if (cons.getValueType().equals(ConstrainValueType.NUMBER)) 
				this.modifierValuePropertyData().useNumericValue(true);
			else if (cons.getValueType().equals(ConstrainValueType.TEXT) || cons.getValueType().equals(ConstrainValueType.LARGETEXT)) 
				this.modifierValuePropertyData().useTextValue(true);
			else if (cons.getValueType().equals(ConstrainValueType.FLAG))
				this.modifierValuePropertyData().useValueFlag(true);
			
			updateModifierMetaDataXML();
			if(this.modifierValuePropertyData().hasStringValue() && this.modifierValuePropertyData().useTextValue()) 
			{
				this.modifierValuePropertyData().useStringValue(true);
				this.modifierValuePropertyData().useTextValue(false);
			}

			this.modifierValuePropertyData().noValue(false);
			this.modifierValuePropertyData().value(cons.getValueConstraint());
			
			for (int i = 0; i < modifierValuePropertyData.enumValues.size(); i++) 
			{
				String eval = modifierValuePropertyData.enumValues.get(i);
				String teval = "'"+eval+"'";
				if (modifierValuePropertyData().value().indexOf(teval)>=0)
					modifierValuePropertyData().selectedValues.add(eval);
			}
			
			if (cons.getValueUnitOfMeasure() != null)
				this.modifierValuePropertyData().unit(cons.getValueUnitOfMeasure());
			if (cons.getValueOperator() != null) 
			{
				this.modifierValuePropertyData().operator( cons.getValueOperator().value());
				// deal with between...
				if (this.modifierValuePropertyData().operator().equalsIgnoreCase("between")) {
					String[] result = cons.getValueConstraint().split(" and ");
					if (result != null && result.length == 2)
					{
						this.modifierValuePropertyData().lowValue(result[0]);
						this.modifierValuePropertyData().highValue(result[1]);
					}
				}

			}
			this.modifierValuePropertyData().okToUseValue(true);
		} 
		else 
		{
			hasValue(false);
		}
	}
	*/
	
	@Override
	public String toString() 
	{
		String valuePropertyString = this.valuePropertyData.toString();
		if ( valuePropertyString.isEmpty() )
			return name() + " ["+this.modifier_name() + "]";
		else 
			return name() + " ["+this.modifier_name() + " " + valuePropertyString + "]";
	}
}