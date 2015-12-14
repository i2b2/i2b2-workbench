/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.views.find;

import java.io.StringWriter;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.dnd.*;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;

import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.common.util.xml.XMLUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.WorkplaceJAXBUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.XmlUtil;
import edu.harvard.i2b2.wkplclient.datavo.dnd.DndType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FolderType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FoldersType;


final class NodeDragListener implements DragSourceListener
{
	private Log log = LogFactory.getLog(NodeDragListener.class.getName());	
	private final TreeViewer viewer;
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
			FolderType data = (FolderType) node.getData();
		    if (data.getVisualAttributes().substring(0,1).equals("C")) {
				event.doit = false;		
				event.detail = DND.DROP_NONE;
				return;
		    }
		}
		
		
		
		selectionOnDrag = (IStructuredSelection)this.viewer.getSelection();
		event.doit = true;		
	}
	
	
	// this is the new jaxb based dnd XML
	public void dragSetData(DragSourceEvent event) 
	{
		Iterator iterator = selectionOnDrag.iterator();
		
		TreeNode node = (TreeNode) iterator.next();
		TreeData ndata = (TreeData)node.getData();
		String oindex = ndata.getIndex();
		ndata.setIndex("\\\\" + ndata.getTableCd() + "\\" +oindex);
		
		FolderType workData = (FolderType) ndata;//node.getData();
		
		if(workData.getVisualAttributes().startsWith("F")){
			StringWriter strWriter = null;
			FoldersType folders = new FoldersType();
			folders.getFolder().add(workData);
			//dnd xml for foldersType
			try {
				strWriter = new StringWriter();
				DndType dnd = new DndType();
				edu.harvard.i2b2.wkplclient.datavo.wdo.ObjectFactory wdoOf = new edu.harvard.i2b2.wkplclient.datavo.wdo.ObjectFactory();
				dnd.getAny().add( wdoOf.createFolders(folders));
				edu.harvard.i2b2.wkplclient.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.wkplclient.datavo.dnd.ObjectFactory();
				WorkplaceJAXBUtil.getJAXBUtil().marshaller(of.createPluginDragDrop(dnd), strWriter);				
			} catch (JAXBUtilException e) {
				log.error("Error marshalling Workplace drag text");
			} 
			event.data = strWriter.toString();
			
		}
		else{
			org.w3c.dom.Element dndElement = workData.getWorkXml().getAny().get(0);
			try {
				org.jdom.input.DOMBuilder builder = new DOMBuilder();
				org.jdom.Element jdomElement = builder.build(dndElement);
				event.data = (new XMLOutputter()).outputString(jdomElement);
			} catch (Exception e) {
				log.error("Error marshalling Workplace drag text");
			}
		}
		// back door way to pass drag folderType data to drop target
		//   used to pass any new rename or re-annotation data 
		//   drag xml does not contain folderType data.
		workData.setIndex(oindex);
		event.display.setData(workData);
	}


	public void dragFinished(DragSourceEvent event) {

		if ((event.display.getData() != null ) && (event.display.getData().getClass().equals(String.class))){
			Iterator iterator = selectionOnDrag.iterator();
			TreeNode node = (TreeNode) iterator.next();		
			if(event.display.getData().equals("M|O*V+E")){
				// clean up drag source on move
					((TreeNode)(node.getParent())).getChildren().remove(node);
	//			}
			}
			else {
				// if we have copied a folder
				//  set up with placeholder so display is correct
				//   then copy children also
				
				node.copyChildren((String)event.display.getData());

			}
		}
		event.display.setData(null);
		selectionOnDrag = null;
		viewer.refresh();
	}
}