/*
 * Copyright (c) 2006-2016 Partners HealthCare 
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

import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.query.data.processor.FoldersProcessor;
import edu.harvard.i2b2.query.data.processor.OntologyTermProcessor;
import edu.harvard.i2b2.query.data.processor.PatientSetProcessor;
import edu.harvard.i2b2.query.data.processor.QueryMasterProcessor;
import edu.harvard.i2b2.query.data.processor.QueryResultProcessor;

public class QueryConceptTreeNodeDataFactory implements DataConst
{
	private static QueryConceptTreeNodeDataFactory myInstance = null;
	
	public static QueryConceptTreeNodeDataFactory getInstance()
	{
		if ( myInstance == null )
			myInstance = new QueryConceptTreeNodeDataFactory();
		return myInstance;
	}
		
	private QueryConceptTreeNodeDataFactory() {}
	

	/*
	 * if the ogiginalXML represents an ontology term, it is structured with an outer tag of <plugin_drag_drop>, 
	 * 		followed by either <concepts> or <modifiers> tags
	 *		if	<concepts>, it has a single child of <concept> 
	 * 		if	<modifiers>, it has  a single child of <modifier>
	 * 
	 * if the originalXML represents other things (patient sets, patients, etc), then it's different 
	 */
	public QueryConceptTreeNodeFactoryProduct makeTreeNodeData( String originalXML )
	{
		try 
		{
			System.err.println("QueryConceptTreeNodeFactory.makeTreeNodeData: dropped xml:" );
			DataUtils.prettyPrintXMLDoc( originalXML, System.err );
			
			SAXBuilder parser = new SAXBuilder();
			final Document xmlDoc = parser.build(new StringReader(originalXML));
			
			Element mainXMLElement = null;
			for (int i = 0; i < xmlDoc.getRootElement().getContent().size(); i++) 
			{
				if (xmlDoc.getRootElement().getContent().get(i).getClass().getSimpleName().equalsIgnoreCase("Element")) 
				{
					mainXMLElement = (Element)xmlDoc.getRootElement().getContent().get(i);
					break;
				}
			}

			/* FOLDERS from Workplace */
			if (mainXMLElement.getName().equalsIgnoreCase( FOLDERS )) 
				return FoldersProcessor.process( xmlDoc, originalXML );
			/* QUERY RESULT INSTANCE (Encounter Set) */
			else if (mainXMLElement.getName().equalsIgnoreCase( QUERY_RESULT_INSTANCE )) 	// Patient Set, Patient Count, Encounter Set
				return QueryResultProcessor.process( mainXMLElement, originalXML );
			/* PATIENT_SET from Workplace or Previous Query */
			else if (mainXMLElement.getName().equalsIgnoreCase( PATIENT_SET )) 				// a list of loose patients
				return PatientSetProcessor.process( mainXMLElement, originalXML);
			/* QUERY_MASTER from Previous Query or Workplace */
			else if (mainXMLElement.getName().equalsIgnoreCase( QUERY_MASTER )) 			// Previous Queries
				return QueryMasterProcessor.process( mainXMLElement, originalXML );
			/* QUERY_MASTER from Ontology or Previous Query or Workplace or other GroupPanels */
			else if (mainXMLElement.getName().equalsIgnoreCase( CONCEPTS ) )				// a list of Concepts
				return OntologyTermProcessor.process( xmlDoc, mainXMLElement, originalXML );

			/* Catch all other unknown drop types (pop up an error)*/
			return new QueryConceptTreeNodeFactoryProduct( true, "You cannot drop that here.", "A '" + mainXMLElement.getName() + "' cannot be dropped inside a Group Panel. (QueryConceptTreeNodeFactory.makeTreeNodeData()" );				
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new QueryConceptTreeNodeFactoryProduct( true, "An Error has Occurred", e.getMessage() );
		}		
	}

}
