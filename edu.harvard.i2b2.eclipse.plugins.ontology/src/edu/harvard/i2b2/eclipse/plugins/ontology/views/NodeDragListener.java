/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views;

import java.io.StringWriter;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.dnd.*;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.OntologyJAXBUtil;
import edu.harvard.i2b2.ontclient.datavo.dnd.DndType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptsType;


final class NodeDragListener implements DragSourceListener
{
	private Log log = LogFactory.getLog(NodeDragListener.class.getName());	
	private TreeViewer viewer;
	private IStructuredSelection selectionOnDrag = null;
	
	NodeDragListener(TreeViewer viewer)
	{
		this.viewer = viewer;
	}
	
	public void dragStart(DragSourceEvent event) 
	{ 
		
		Iterator it = ((IStructuredSelection)this.viewer.getSelection()).iterator();
		while(it.hasNext())
		{
			TreeNode node = (TreeNode) it.next();
			ConceptType data = node.getData();
			if (((data.getVisualattributes().substring(0,1).equals("C"))) || 
					((data.getVisualattributes().substring(1,2).equals("I")))){
				event.doit = false;		
				event.detail = DND.DROP_NONE;
				return;
			}
			else {
				if(data.getModifier() != null){
					if (((data.getModifier().getVisualattributes().substring(0,1).equals("O"))) || 
							((data.getModifier().getVisualattributes().substring(1,2).equals("I")))){
						event.doit = false;		
						event.detail = DND.DROP_NONE;
						return;
					}
				}
			}
		}

		event.doit = true;		
		event.detail = DND.DROP_COPY;
		
		selectionOnDrag = (IStructuredSelection)this.viewer.getSelection();
	}
	
	// snm - this was the old drag that produced a query
//	public void dragSetDataEx(DragSourceEvent event) 
//	{		
//		IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
//		
//		String statement  = "select * from observation_fact where ";
//		Iterator iterator = selection.iterator();
//		while(iterator.hasNext())
//		{
//			TreeNode node = (TreeNode) iterator.next();
//			TreeData data = node.getData();
//			
//			// dont allow cases and inactive elements to be dragged and dropped			
//		    if ((!(data.getVisualattributes().substring(0,1).equals("C"))) &&
//		    		(!(data.getVisualattributes().substring(1,2).equals("I"))))
//		    {
// 	    	   String dimCode = data.getDimcode();
// 	    	   if(data.getOperator().equals("LIKE"))
// 	    	   {
// 	    		   dimCode = "'" + dimCode + "\\%'";
// 	    	   }
// 	    	   else if(data.getOperator().equals("IN"))
// 	    	   {
// 	    		   dimCode = "(" + dimCode + ")";
// 	    	   }
// 	    	   else if(data.getOperator().equals("="))
// 	    	   {
// 	    		   if(data.getColumndatatype().equals("T"))
// 	    		   {
// 	    			   dimCode = "'" + dimCode + "'";
// 	    		   }
// 	    	   }
//
// 	    	   if (statement.equals("select * from observation_fact where "))
// 	    	   {
// 	    		   statement = statement + node.getData().getFacttablecolumn() + " in (select " 
//								+ node.getData().getFacttablecolumn() + " from " + node.getData().getTablename() +			
//							" where " + node.getData().getColumnname() + " " + node.getData().getOperator()
//							+ " " + dimCode + ") ";
// 	    	   }
// 	    	   else
// 	    	   {
// 	    		   statement = statement + " or " + node.getData().getFacttablecolumn() + " in (select " 
//								+ node.getData().getFacttablecolumn() + " from " + node.getData().getTablename() +								
//							" where " + node.getData().getColumnname() + " " + node.getData().getOperator()
//							+ " " + dimCode + ") ";
// 	    	   }			
//		    }
//		}
//		event.data = statement;
//
//        if(event.data == null)
//			log.debug("drag set data is null");		
//	}
	
	// this is the new jaxb based dnd XML
	public void dragSetData(DragSourceEvent event) 
	{
		StringWriter strWriter = null;
		ConceptsType concepts = new ConceptsType();
		Iterator iterator = selectionOnDrag.iterator();
		while(iterator.hasNext())
		{
			TreeNode node = (TreeNode) iterator.next();
//			TableComposite.getInstance().addModifiers(node);
			ConceptType data = node.getData();
		    if ((!(data.getVisualattributes().substring(0,1).equals("C"))) &&
    		(!(data.getVisualattributes().substring(1,2).equals("I"))))
				concepts.getConcept().add(data);
		}	

		try {
			strWriter = new StringWriter();
			DndType dnd = new DndType();
			edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory vdoOf = new edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory();
			dnd.getAny().add( vdoOf.createConcepts(concepts));

			edu.harvard.i2b2.ontclient.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.dnd.ObjectFactory();
			OntologyJAXBUtil.getJAXBUtil().marshaller(of.createPluginDragDrop(dnd), strWriter);


		} catch (JAXBUtilException e) {
			log.error("Error marshalling Ont drag text");
		} 

		//	log.info("Ont Client dragged "+ strWriter.toString());
		event.data = strWriter.toString();

	}
	
/*	//snm - this is the new drag that produces XML  -- JDOM based
	public void dragSetData(DragSourceEvent event) 
	{
		String sRootConceptTag = "concepts";
//		String sIndividualConceptTag = "Concept";
		String sNodesXMLRepresentation = "";
		Iterator iterator = selectionOnDrag.iterator();
		sNodesXMLRepresentation = "<" + sRootConceptTag + ">\r\n";
		while(iterator.hasNext())
		{
			TreeNode node = (TreeNode) iterator.next();
			TreeData data = node.getData();
	//		sNodesXMLRepresentation += "\t<" + sIndividualConceptTag + ">\r\n";
			sNodesXMLRepresentation += data.getXMLContents();
//			sNodesXMLRepresentation += "\t<lookupdb>" + data.getLookupDB() + "</lookupdb>\r\n";
//			sNodesXMLRepresentation += "\t<lookuptable>" + data.getLookupTable() + "</lookuptable>\r\n";
//			sNodesXMLRepresentation += "\t<selectservice>" + data.getWebserviceName() + "</selectservice>\r\n";
	//		sNodesXMLRepresentation += "\r\n\t</" + sIndividualConceptTag + ">\r\n";
		}
		sNodesXMLRepresentation += "</" + sRootConceptTag + ">\r\n";
		
		//formatting of data to make it look nice
		try
		{
		 SAXBuilder parser = new SAXBuilder();
		 String xmlContent = sNodesXMLRepresentation;
	     java.io.StringReader xmlStringReader = new java.io.StringReader(xmlContent);
	     org.jdom.Document tableDoc = parser.build(xmlStringReader);
	     XMLOutputter fmt = new XMLOutputter(Format.getPrettyFormat());
	     sNodesXMLRepresentation = fmt.outputString(tableDoc.getRootElement());      
	     parser = null;
	     xmlContent = null;
	     tableDoc = null;
	     fmt = null;
		}
		catch (Exception e){}
			
		event.data = sNodesXMLRepresentation;

        if(event.data == null)
			System.out.println("drag set data is null");		
	}*/

	public void dragFinished(DragSourceEvent event) {
		selectionOnDrag = null;
	}
}