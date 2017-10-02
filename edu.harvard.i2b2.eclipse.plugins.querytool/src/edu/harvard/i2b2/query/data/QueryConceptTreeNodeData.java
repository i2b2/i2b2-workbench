/*
 * Copyright (c) 2006-2017 Massachusetts General Hospital 
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
import java.util.ArrayList;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.GetTermInfoRequestMessage;
import edu.harvard.i2b2.eclipse.plugins.query.ontologyMessaging.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Copyable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.query.data.processor.ProcessorConst;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ConstrainValueType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ItemType.ConstrainByValue;
import edu.harvard.i2b2.crcxmljaxb.datavo.vdo.GetTermInfoType;

public class QueryConceptTreeNodeData implements Copyable <QueryConceptTreeNodeData>, DataConst, ProcessorConst
{
	
	// ONLY written to, never read. Candidates for removal
	protected String hlevel;
	protected String titleName = "";
	protected String visualAttribute;
	protected String tooltip;
	
	protected String 	fullname;
	protected boolean 	isModifier = false;
	protected String 	name = "";
	protected String 	valueName = "";
	
	protected String 			originalXML;
	protected boolean 			hasValue = false;	
	protected ValuePropertyData valuePropertyData;	// Value Restrictions for Concepts (Text, Enum, or Numeric (labs))
	
	protected QueryConceptTreeNodeData 				myParent;
	protected ArrayList<QueryConceptTreeNodeData> 	myChildren;
	
	protected boolean			isXMLComplete = false;
	
	/*
	 * Constructors
	 */
	// default constructor with 'null' parent
	public QueryConceptTreeNodeData()
	{ this( null ); }	

	public QueryConceptTreeNodeData( QueryConceptTreeNodeData parent )
	{ 
		myParent 			= parent;
		valuePropertyData 	= new ValuePropertyData();
		myChildren 			= new ArrayList<QueryConceptTreeNodeData> ();
	}
	
	public QueryConceptTreeNodeData( QueryConceptTreeNodeData parent, String name, String title, String vAttribute, String tooltip, String hLevel, String fullName, String originalXML )
	{
		myParent 			= parent;
		valuePropertyData 	= new ValuePropertyData();
		myChildren 			= new ArrayList<QueryConceptTreeNodeData> ();

		this.name( name );
		this.titleName( title );
		this.visualAttribute( vAttribute );
		this.tooltip( tooltip );
		this.hlevel( hLevel );
		this.fullname( fullName );
		this.finalizeOriginalXML( originalXML ); // finalizes and sets isXMLComplete = true, unless originalXML is null
	}
	
	// constructor with 'null' parent
	public QueryConceptTreeNodeData( String name, String title, String vAttribute, String tooltip, String hLevel, String fullName, String originalXml )
	{ this( null, name, title, vAttribute, tooltip, hLevel, fullName, originalXml ); }

	/*
	 * bugbug: Using visualAttribute to check if the node is a concept is lame. Should probably examine xml if available.
	 */
	public boolean isConcept()
	{
		if ( this.isModifier )	return true;
		if ( this.visualAttribute().substring(0, 1).equals("F") ) 	return true;
		if ( this.visualAttribute().substring(0, 1).equals("M")) 	return true;
		if ( this.visualAttribute().substring(0, 1).equals("L")) 	return true;
		return false;
	}
	
	/*
	 * Tree node structure 
	 */
	public QueryConceptTreeNodeData getParent() 									{ return myParent; }
	public void						setParent( QueryConceptTreeNodeData parent ) 	{ myParent = parent; }
	
	public boolean	hasParent() 									
	{ return this.myParent != null; }
	
	public void addChild( QueryConceptTreeNodeData child )
	{ myChildren.add(child ); }
	
	public boolean removeChild( QueryConceptTreeNodeData child )
	{ return myChildren.remove( child ); }
	
	public QueryConceptTreeNodeData removeChildAt( int index )
	{ return myChildren.remove( index ); }
	
	public boolean removeChildren( List<QueryConceptTreeNodeData> children )
	{ return myChildren.removeAll( children ); }
	
	public void clearChildren()
	{ myChildren.clear(); }
	
	public int getNumChildren()
	{ return myChildren.size(); }
	
	public ArrayList<QueryConceptTreeNodeData> getChildren()
	{ return myChildren; }
		
	
	/*
	 * Data
	 */
	public void 	hlevel(String str) 		{ hlevel = str; }
	public String	hlevel() 				{ return hlevel; }
	public void 	fullname(String str)	{ fullname = str; }
	public String 	fullname() 				{ return fullname; }
	public void 	isModifier(boolean b) 	{ isModifier = b; }
	public boolean 	isModifier() 			{ return isModifier; }
	public void 	name(String str) 		{ name = str; }
	public String 	name() 					{ return name; }
	
	public String 	valueName() 			{ return this.valuePropertyData.toString() ; }

	public void 	titleName(String str) 				{ titleName = str; } 			//bugbug: this.titleName seems unused.
	
	public void 	visualAttribute(String str) 		{ visualAttribute = str; }
	public String	visualAttribute()					{ return visualAttribute; }
	
	public String	getOriginalXML()					{ return this.originalXML; }
	
	/* Allows setting of originalXML only once or until it's not null */
	public void 	finalizeOriginalXML( String completeXML )
	{
		if ( this.isXMLComplete )
		{
			assert false : "QueryConceptTreeNode.finalizeOriginalXML: attempting to re-finalize originalXML. Not permitted.";
			return;
		}
		this.originalXML 	= 	completeXML;
		if ( this.originalXML != null )
			this.isXMLComplete 	=	true;
	}
	
	public boolean	isXMLComplete()
	{ return this.isXMLComplete; }
	
	
	public void 	hasValue(boolean b) 				{ hasValue = b; }
	public boolean 	hasValue() 							{ return hasValue; }
	
	public ValuePropertyData 	valuePropertyData() 						{ return valuePropertyData;}
	public void				 	valuePropertyData(ValuePropertyData val) 	{ valuePropertyData = val;}
	
	public void 	tooltip(String str)
	{
		if (str != null)
			tooltip = new String(str);
	}
	public String	tooltip()
	{ return tooltip; }
	
	
	
	/* incompleteDoc is an XML document that has the <dnd:plugin_drag_drop> <concepts> <concept> tags. 
	 * 		The <concpet> tag contains the incomplete XML for the concept.
	 * 		Note: the parameter incompleteXMLDoc is not used here, but it is used in subclasses where this method is overridden
	 */
	public Document retrieveMetadataXMLfromONTAndSetOriginalXML( Document incompleteXMLDoc ) 
	{ 
		try 
		{
			Document newDoc = doRetrieveMetadataXMLfromONTAsDoc(); // hit ONT cell

			// find the <ont:concept> node and set its supplemental <isXMLComplete> tag to "true"			
			Element isXMLCoompleteElement = new Element( QueryConceptTreeNodeData.TAG_IS_XML_COMPLETE );	// mark the concept as having a complete XML, add the supplemental tag
			isXMLCoompleteElement.setText( DataConst.TRUE );
			Element conceptsTag = (Element)((Element)((Element)newDoc.getContent( DataConst.DRAG_AND_DROP_TAG_FILTER ).get(0)).getContent().get(0));
			Element conceptElement = conceptsTag.getChild( CONCEPT );
			
			// force the xml to take up this's visualAttributes because xml may not be correct.
			Element visualAttributes = conceptElement.getChild( DataConst.VISUAL_ATTRIBUTES );
			visualAttributes.setText( this.visualAttribute );
			
			conceptElement.addContent( isXMLCoompleteElement );

			// get a String representation of the XML and set it as the originalXML for this node.
			StringWriter strWriter = new StringWriter();
			DataUtils.prettyPrintXMLDoc( newDoc, strWriter );
			this.finalizeOriginalXML( strWriter.toString() );	// set and finalize originalXML
			//System.err.println( "QueryConceptTreeNodeData.retrieveMetadataXMLfromONTAndSetOriginalXML: \n" + strWriter.toString() );
			strWriter.close();
			
			// return the DOM document for good measure.
			return newDoc;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	/*
	 *	Does the dirty work of hitting ONT cell and get the metadataXML for
	 *		this QueryConceptTreeNodeData 
	 */
	protected Document doRetrieveMetadataXMLfromONTAsDoc() throws Exception
	{
		// call ONT service to get the full XML
		GetTermInfoType vocab = new GetTermInfoType();
		vocab.setHiddens(true);
		vocab.setSynonyms(false);
		vocab.setMax(200);
		vocab.setType("default");
		vocab.setBlob(true);
		vocab.setSelf(this.fullname());
		String xmlContent = null; //OntServiceDriver.getTermInfo(vocab, "");

		//String response = null;
		String getTermInfoRequestString = null;
		try 
		{
			GetTermInfoRequestMessage reqMsg = new GetTermInfoRequestMessage();
			getTermInfoRequestString = reqMsg.doBuildXML(vocab);
			xmlContent = OntServiceDriver.sendREST(OntServiceDriver.termInfoEPR, getTermInfoRequestString, "");
		} 
		catch (AxisFault e) 
		{
			System.err.println("QueryConceptTreeNodeData.doRetrieveMetadataXMLfromONTAsDoc: Axis Fault");
			//log.error(e.getMessage());
			//log.error("Unable to make connection to remote server");
		}
		catch (Exception e) 
		{
			System.err.println("QueryConceptTreeNodeData.doRetrieveMetadataXMLfromONTAsDoc: Exception");
			//log.error(e.getMessage());
			//log.error("Error returned from remote server");
		}

		//System.err.println( xmlContent );
		// parse the XML and build a DOM
		java.io.StringReader xmlStringReader = new StringReader(xmlContent);
		SAXBuilder parser = new SAXBuilder();
		Document conceptDoc = parser.build(xmlStringReader);

		try
		{
			// work with DOM to clone the <ont:concepts> node
			Element elementMsgBody = conceptDoc.getRootElement().getChild( MESSAGE_BODY ); 
			Element conceptsXML = (Element)elementMsgBody.getChild( CONCEPTS , Namespace.getNamespace( ONT_NAMESPACE )).clone(); // get a copy of the <concepts> tag 
			conceptsXML.detach();
			// make a new blank document and add the <ont:concepts> node
			Document newDoc = DataUtils.makeEmptyDNDXMLDocument();
			Element dndElement = (Element)newDoc.getContent().get(0);
			dndElement.addContent( conceptsXML );
			return newDoc;
		}
		catch ( NullPointerException e )
		{
			System.err.println("QueryConceptTreeNodeData.doRetrieveMetadataXMLfromONTAsDoc(): handling exceptions for GetTermInfo");
			System.err.println( e );
			System.err.println("Error has occurred: " + e.getMessage() );
			
			System.err.println("Offending request XML: " + getTermInfoRequestString );
		}

		return null;
	}
	
	/* Contact the Ontology and get the XML for the QueryConceptTreeNode.
	 *      The result document includes the metadataXML and modifierflag:
	 *		<concepts>
	 *			<concept>
	 *				<metadata>	
	 *				<modifier>
	 *	However, the modifier flag does not contain metadataXML for the modifier
	 * */
	public Document retrieveFullConceptXMLFromONTandSetOriginalXML() 
	{
		try 
		{
			return this.retrieveMetadataXMLfromONTAndSetOriginalXML( null );
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	/* Called when original XML has been set. Only called for Ontology Terms. */
	public void parseOriginalXMLAndSetValuePropertySchema( ) throws JDOMException, IOException
	{		
		StringReader xmlStringReader = new StringReader( this.originalXML );
		SAXBuilder parser 	= new SAXBuilder();
		Document conceptDoc = parser.build(xmlStringReader);

		//Element elementMsgBody = concpetDoc.getRootElement().getChild("message_body"); 
		Element tableXml = conceptDoc.getRootElement().getChild( CONCEPTS , Namespace.getNamespace(ONT_NAMESPACE));

		Element conceptXml = (Element)tableXml.getChildren().get(0);	// Get the only Concept child.
		titleName( conceptXml.getChild( NAME ).getText() );				// set the titleName for this

		Element metadataAttribs = conceptXml.getChild( METADATA_XML );
		Element valuedataAttribs = null;
		if(metadataAttribs != null) 
			valuedataAttribs = metadataAttribs.getChild( VALUE_METADATA );

		if ((metadataAttribs != null) && (valuedataAttribs != null)) 
		{
			this.hasValue( true );
			Element dataTypeElement = valuedataAttribs.getChild( DATA_TYPE );
			if (dataTypeElement != null && dataTypeElement.getTextTrim().equalsIgnoreCase( ENUM )) 
			{
				// add text values to node data
				valuePropertyData.hasEnumValue(true);
				Element enumElement = valuedataAttribs.getChild( ENUM_VALUES );
				for (int i = 0; i < enumElement.getChildren().size(); i++) 
				{
					Element valElement = (Element) enumElement.getChildren().get(i);
					String valString = new String(valElement.getTextTrim());
					this.valuePropertyData().enumValues.add(valString);
				}
			} 
			else if (dataTypeElement != null && (dataTypeElement.getTextTrim().equalsIgnoreCase( STRING )) || (dataTypeElement.getTextTrim().equalsIgnoreCase( LARGE_STRING ))) 
			{
				// add text values to node data
				valuePropertyData.hasStringValue(true);
				Element maxLengthElement = valuedataAttribs.getChild( MAX_STRING_LENGTH );
				String valString = new String(maxLengthElement.getTextTrim());
				if(!valString.equalsIgnoreCase("")) 
					valuePropertyData.searchStrLength(Integer.parseInt(valString));
				if((dataTypeElement.getTextTrim().equalsIgnoreCase( LARGE_STRING ))) 
					valuePropertyData.isLongText(true);
			}

			if (valuedataAttribs.getChild( OK_TO_USE_VALUES ) != null && valuedataAttribs.getChild( OK_TO_USE_VALUES ).getText().equalsIgnoreCase("Y")) 
				this.valuePropertyData().okToUseValue(true);

			if (valuedataAttribs.getChild( FLAGS_TO_USE ) == null || valuedataAttribs.getChild( FLAGS_TO_USE ).getText().equalsIgnoreCase("")) 
				this.valuePropertyData().okToUseValueFlag(false);

			Element unitElement = valuedataAttribs.getChild( UNIT_VALUES );
			if (unitElement != null) 
			{
				for (int i = 0; i < unitElement.getChildren().size(); i++) 
				{
					Element element = (Element) unitElement.getChildren().get(i);
					if (element.getName().equalsIgnoreCase( NORMAL_VALUES ) || element.getName().equalsIgnoreCase( EQUAL_UNITS )) 
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
	
	
	
	
	/* Given a value constraints, this method sets the values in this QueryConceptTreeNodeData. null and empty list allowed */
	// bugbug: this method makes a call to ontology cell by calling updateNodeMetaDataXML, should try to NOT call it unless necessary
	public void setValueConstraint( ConstrainByValue cons ) throws IOException, JDOMException
	{
		if ( cons != null)
		{
			hasValue(true);
			//ConstrainByValue cons = list.get(0); // only deal with the first constraint because only 1 is really allowed for now.
			if (cons.getValueConstraint() == null) 
			{
				// hasValue(false);
				return;
			}
			if (cons.getValueType().equals(ConstrainValueType.NUMBER))
				this.valuePropertyData().useNumericValue(true);
			else if (cons.getValueType().equals(ConstrainValueType.TEXT) || cons.getValueType().equals(ConstrainValueType.LARGETEXT)) 
				this.valuePropertyData().useTextValue(true);
			else if (cons.getValueType().equals(ConstrainValueType.FLAG)) 
				this.valuePropertyData().useValueFlag(true);

			// if XML is not complete, we call out to ONT and parse the XML
			if ( !isXMLComplete )
			{				
				retrieveFullConceptXMLFromONTandSetOriginalXML();
				parseOriginalXMLAndSetValuePropertySchema(); // bugbug: this is not quite right...
			}
				
			if(this.valuePropertyData().hasStringValue() && this.valuePropertyData().useTextValue()) 
			{
				this.valuePropertyData().useStringValue(true);
				this.valuePropertyData().useTextValue(false);
			}

			this.valuePropertyData().noValue(false);
			this.valuePropertyData().value(cons.getValueConstraint());
			
			for (int i = 0; i < valuePropertyData.enumValues.size(); i++) 
			{
				String eval = valuePropertyData.enumValues.get(i);
				String teval = "'"+eval+"'";
				if (valuePropertyData().value().indexOf(teval)>=0)
					valuePropertyData().selectedValues.add(eval);
			}
			
			/*
			if (this.valuePropertyData().selectedValues.size() > 0)
				this.valueName(" Is " + valuePropertyData().selectedValues.get(0));
			
			if (this.valuePropertyData().selectedValues.size() > 1) 
				for (int j = 1; j < this.valuePropertyData().selectedValues.size(); j++) 
					this.valueName(this.valueName() + "," + this.valuePropertyData().selectedValues.get(j));
			*/
			if (cons.getValueUnitOfMeasure() != null)
				this.valuePropertyData.unit(cons.getValueUnitOfMeasure());
			if (cons.getValueOperator() != null) 
			{
				this.valuePropertyData().operator(cons.getValueOperator().value());
				// deal with between...
				if (this.valuePropertyData().operator().equalsIgnoreCase("between")) 
				{
					String[] result = cons.getValueConstraint().split(" and ");
					if (result != null && result.length == 2) 
					{
						this.valuePropertyData().lowValue(result[0]);
						this.valuePropertyData().highValue(result[1]);
					}
				}
			}			
			this.valuePropertyData().okToUseValue(true);
			/*
			if (this.valuePropertyData().selectedValues.size() <= 0)
				this.valueName(" ["+getOperator(this.valuePropertyData().operator()) + " \""+ this.valuePropertyData().value()+"\"]");
			*/
		}
		else
			hasValue(false);
	}
	
	
	protected String getOperator(String opStr) 
	{
		String result = "";
		if (opStr == null) 
		{
			return result;
		}

		if (opStr.equalsIgnoreCase("LT"))
			result = "<";
		else if (opStr.equalsIgnoreCase("LE"))
			result = "<=";
		else if (opStr.equalsIgnoreCase("="))
			result = "=";
		else if (opStr.equalsIgnoreCase("GT"))
			result = ">";
		else if (opStr.equalsIgnoreCase("GE"))
			result = ">=";
		else if (opStr.equalsIgnoreCase("LIKE[contains]"))
			result = "Contains";
		else if (opStr.equalsIgnoreCase("LIKE[begin]"))
			result = "Begin with";
		else if (opStr.equalsIgnoreCase("LIKE[end]"))
			result = "End with";
		else if (opStr.equalsIgnoreCase("LIKE[exact]"))
			result = "Exact";
		else if (opStr.equalsIgnoreCase("CONTAINS"))
			result = "Contains";
		else if (opStr.equalsIgnoreCase("Contains[database]"))
			result = "Contains[database]";
		return result;
	}
	
	/*=============================================================================================
	 * Returns what image should be used to represent this QueryConceptTreeNodeData in a GroupPanel
	 * Can be overwritten by subclasses
	 ==============================================================================================*/
	public Image getImage()
	{
		String key = Images.LEAF;
		String visualAttributes = this.visualAttribute(); 
		if ( visualAttributes.equals("F") || visualAttributes.equals("FA") || visualAttributes.equals("FI") || visualAttributes.equals("FAE"))
			key = Images.CLOSED_FOLDER;
		else if ( visualAttributes.equals("FAO") || visualAttributes.equals("FIO"))
				key = Images.OPEN_FOLDER;
		else if ( visualAttributes.equals("CA") || visualAttributes.equals("CI")) 
				key = Images.CLOSED_CASE;
		else if ( visualAttributes.equals("CAO") || visualAttributes.equals("CIO"))
				key = Images.OPEN_CASE; 
		else if ( visualAttributes.equals( ProcessorConst.L ) ) 
			key = Images.LEAF;
		else if ( visualAttributes.equals( ProcessorConst.LA ) )
			key = Images.LEAF;
		else if ( visualAttributes.substring(0, 1).equals("M")) 
			key = Images.LEAF;
		else if ( visualAttributes.equals("PQ")) 
			key = Images.PREVIOUS_QUERY;
		else if ( visualAttributes.equals("PT")) 
			key = Images.PLAIN_PEOPLE;
		else if ( visualAttributes.equals( ProcessorConst.ICON_ENCOUNTER_SET ))
			key = Images.ENCOUNTER_SET;
		else if ( visualAttributes.equals( ProcessorConst.ICON_PATIENT_SET ))
			key = Images.PATIENT_SET;		
		else if ( visualAttributes.equals( ProcessorConst.ICON_WORKING ))
			key = Images.WORKING;
		else if ( visualAttributes.equals( ProcessorConst.ICON_WARNING ))
			return Images.getEclipseImagesByKey( ISharedImages.IMG_OBJS_WARN_TSK );
		return Images.getImageByKey( key );
	}
	

	@Override
	public String toString() 
	{
		String valuePropertyString = this.valuePropertyData.toString();
		if ( valuePropertyString == null || valuePropertyString.isEmpty() )
			return name();
		else 
			return name() + " " + valuePropertyString;
	}

	@Override /* Copyable method */
	public QueryConceptTreeNodeData makeCopy() 
	{
		QueryConceptTreeNodeData concept = new QueryConceptTreeNodeData( this.myParent, this.name, this.titleName, this.visualAttribute, this.tooltip, this.hlevel, this.fullname, this.originalXML );
		concept.valueName 			= this.valueName;
		concept.hasValue			= this.hasValue;
		concept.valuePropertyData 	= this.valuePropertyData.makeCopy();
		for ( QueryConceptTreeNodeData node : this.myChildren )
		{
			QueryConceptTreeNodeData newNode = node.makeCopy();
			newNode.myParent = concept;
			concept.myChildren.add( newNode );
		}
		return concept;
	}
	
}
