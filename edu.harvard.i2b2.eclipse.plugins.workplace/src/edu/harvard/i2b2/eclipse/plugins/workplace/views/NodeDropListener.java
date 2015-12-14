/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.views;

import java.io.StringWriter;
import java.util.*;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.harvard.i2b2.common.datavo.pdo.PatientIdType;
import edu.harvard.i2b2.common.datavo.pdo.PatientSet;
import edu.harvard.i2b2.common.datavo.pdo.PatientType;
import edu.harvard.i2b2.common.datavo.pdo.PidSet;
import edu.harvard.i2b2.common.datavo.pdo.PidType;
import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.MessageUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.WorkplaceJAXBUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.XmlUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.AddChildRequestMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceRequestData;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceResponseMessage;
import edu.harvard.i2b2.wkplclient.datavo.dnd.DndType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FolderType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FoldersType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.XmlValueType;


final class NodeDropListener implements DropTargetListener
{
	private Log log = LogFactory.getLog(NodeDropListener.class.getName());	
	private final TreeViewer viewer;
	
	NodeDropListener(TreeViewer viewer)
	{
		this.viewer = viewer;
	}
	
	public void dragEnter(DropTargetEvent event)
	{
//		log.info("Enter op " + event.detail);
//		log.info("copy " + DND.DROP_COPY);
//		log.info("default " + DND.DROP_DEFAULT);
//		log.info("move " + DND.DROP_MOVE);
	
		TextTransfer textTransfer = TextTransfer.getInstance();
		if(textTransfer.isSupportedType(event.currentDataType)){
			if(event.detail == DND.DROP_DEFAULT)
				event.detail = DND.DROP_COPY;
		}

	}
	public void dragLeave(DropTargetEvent event)
	{
	}
	public void dragOperationChanged(DropTargetEvent event)
	{
		// old code
//		TreeNode currentTarget = (TreeNode)event.item.getData();
//		if(currentTarget.getData().getVisualAttributes().startsWith("F"))
//			event.detail = DND.DROP_MOVE;
	}
	public void dragOver(DropTargetEvent event)
	{
	}
	public void dropAccept(DropTargetEvent event)
	{

		TreeNode currentTarget = (TreeNode)event.item.getData();
		// old dnd copy v move code
//		if(currentTarget.getData().getShareId().equals("Y"))
//			event.detail = DND.DROP_COPY;
		if(currentTarget.getData().getVisualAttributes().startsWith("F"))
			return;
		else if(currentTarget.getData().getVisualAttributes().startsWith("C"))
			return;

		else
			event.detail = DND.DROP_NONE;
	}
	public void drop(DropTargetEvent event)
	{	
		String text = (String)event.data;
		if(text !=null) { 
			XmlValueType xml = XmlUtil.stringToXml(text);
			TreeNode currentTarget = (TreeNode)event.item.getData();
				
			FolderType workItem = null;
			String dragShareId = null;
			Boolean moveFlag = false;
			// check contents of display.getData
			if(event.display.getData() != null){
				// display.getData == M|O*V+E means we did not move previous item 
				//   within workplace tree.   So reset to null.
				if(event.display.getData().equals("M|O*V+E"))
					event.display.setData(null);
				// otherwise display.getData == the work object being dragged.
				// we need this to get name/annotation info in event of rename or new annotation
				else {
					workItem = (FolderType)event.display.getData();
					dragShareId = workItem.getShareId();
				}

			}
			//set up M|O*V+E flag so dragFinish can clean up appropriately
			// if source or destination is shared do a copy not a move
			//   if workItem is empty -- drag source is not from a workplace folder.
			if (!(currentTarget.getData().getShareId().equals("Y")) &&
					(workItem != null) && !(dragShareId.equals("Y")) ) {
				event.display.setData("M|O*V+E");
				moveFlag = true;
			}		
			
			// Check that xml dragged in is of a type we know about.
			// Set icon to correct type.
			
			String visualAttribute = "";
			String workXmlI2B2Type = "";
			String name = XmlUtil.getName(xml);
			
			if(XmlUtil.hasFolderTag(xml)) {

				JAXBUtil jaxbUtil = WorkplaceJAXBUtil.getJAXBUtil();
				FoldersType folders = null;
				FolderType folder = null;
				try {
					JAXBElement jaxbElement  = jaxbUtil.unMashallFromString(text);
					DndType dndType = (DndType)jaxbElement.getValue();     
					folders = (FoldersType) new JAXBUnWrapHelper().getObjectByClass(dndType.getAny(),
							FoldersType.class);
					folder = folders.getFolder().get(0);
				} catch (JAXBUtilException e) {
					log.error("Unwrap error: " + e.getMessage(), e);
					return;
				}  
				TreeData data = new TreeData(folder);
				data.setParentIndex(currentTarget.getData().getIndex());
				data.setTableCd(currentTarget.getData().getTableCd());
				data.setVisualAttributes("FA");
				data.setShareId(currentTarget.getData().getShareId());
				TreeNode node = new TreeNode(data);
				currentTarget.addChild(node);
				
				if(moveFlag == true)
					node.moveNode(viewer).start();

				else{ 
					String copiedTo = new AddChildRequestMessage().generateMessageId();
					
					// we are copying (from a shared folder)
					// Give the node its own index
					node.getData().setIndex(copiedTo);
					// This is a hack to pass the index and shareId back to dragListener
					//   so children of copied Folder can also be copied.
					event.display.setData(copiedTo+","+data.getShareId());
					
					// set up folder with placeholder child so display is correct
					if((node.getData().getVisualAttributes().equals("FA")) )  
	    			{
	    				TreeNode placeholder = new TreeNode("working...", "working...", "LAO");
	    				node.addChild(placeholder);
	    			
	    			}
	    			else if	((node.getData().getVisualAttributes().equals("FH")) )
	    			{
	    				TreeNode placeholder = new TreeNode("working...", "working...", "LHO");
	    				node.addChild(placeholder);
	    			
	    			}

					node.addNode(viewer).start();

				}
				viewer.refresh();
				return;
			}
		/*
		 * This code used to single out folders for different processing (move only)
		 * 
		 * 
		 * 	if(XmlUtil.hasFolderTag(xml)) {
				// Folders are processed differently
				// They are moved as opposed to added to the db
				JAXBUtil jaxbUtil = WorkplaceJAXBUtil.getJAXBUtil();
				FoldersType folders = null;
				FolderType folder = null;
				try {
					JAXBElement jaxbElement  = jaxbUtil.unMashallFromString(text);
					DndType dndType = (DndType)jaxbElement.getValue();     
					folders = (FoldersType) new JAXBUnWrapHelper().getObjectByClass(dndType.getAny(),
							FoldersType.class);
					folder = folders.getFolder().get(0);
				} catch (JAXBUtilException e) {
					log.error("Unwrap error: " + e.getMessage(), e);
					return;
				}  
				TreeData data = new TreeData(folder);
				data.setParentIndex(currentTarget.getData().getIndex());
				data.setTableCd(currentTarget.getData().getTableCd());
				data.setVisualAttributes("FA");
				data.setShareId(currentTarget.getData().getShareId());
				TreeNode node = new TreeNode(data);
				currentTarget.addChild(node);
				/// dont allow copying folders between private area and shared area for the moment
				if((!(data.getShareId().equals("Y")) && (currentTarget.getData().getShareId().equals("Y"))) ||
						(data.getShareId().equals("Y") && (!(currentTarget.getData().getShareId().equals("Y"))))){
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Copy Folder Message");
					mBox.setMessage("Copying folders between shared and local areas not allowed \n" +
							"Create a new folder and then copy its contents.");
					int result = mBox.open();		
				}
				// other wise move the folder
				else
					node.moveNode(viewer).start();

				viewer.refresh();
				return;
			}*/
			else if(XmlUtil.hasConceptTag(xml)) {

				visualAttribute = "ZA";     
				workXmlI2B2Type = "CONCEPT";
				
				try {
					JAXBUtil jaxbUtil = WorkplaceJAXBUtil.getJAXBUtil();
					ConceptType concept = null;
					JAXBElement jaxbElement  = jaxbUtil.unMashallFromString(text);
					DndType dndType = (DndType)jaxbElement.getValue();     
					ConceptsType conceptsType = (ConceptsType) new JAXBUnWrapHelper().getObjectByClass(dndType.getAny(),
							ConceptsType.class);

					List concepts = conceptsType.getConcept();
					if(concepts != null) {
						Iterator conceptsIterator = concepts.iterator();		
						while(conceptsIterator.hasNext())
						{
							concept = (ConceptType) conceptsIterator.next();
							XmlValueType conceptXml = createWorkXml(concept);
							if (concept.getVisualattributes().startsWith("FA"))
								visualAttribute = "ZAF";
							String cname = concept.getName();
							if(concept.getModifier() != null)	{
								cname += " [" + concept.getModifier().getName() + "]";
							}
								
				//			createWorkplaceNode(workItem, currentTarget, conceptXml, XmlUtil.getName(conceptXml), workXmlI2B2Type, visualAttribute, moveFlag);
							createWorkplaceNode(workItem, currentTarget, conceptXml, cname, workXmlI2B2Type, visualAttribute, moveFlag);
							
						}
					}
				}catch (JAXBUtilException e) {
					log.error("Unwrap error: " + e.getMessage(), e);
					return;
				}  
			}
			
			else if(XmlUtil.hasPatientSetTag(xml)) {
				visualAttribute = "ZA";		
				workXmlI2B2Type = "PATIENT_COLL";
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			}
			
			else if(XmlUtil.hasEncounterSetTag(xml)) {
				visualAttribute = "ZA";		
				workXmlI2B2Type = "ENCOUNTER_COLL";
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			}
			
			else if(XmlUtil.hasPatientCountTag(xml)) {
				visualAttribute = "ZA";		
				workXmlI2B2Type = "PATIENT_COUNT_XML";
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			}

			else if(XmlUtil.hasBreakdownTag(xml)) {
				visualAttribute = "ZA";		
				workXmlI2B2Type = XmlUtil.getXmlI2B2Type(xml);
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			}
			// this has to be after patient set and patient count tag checks 
			else if(XmlUtil.hasPrevQueryTag(xml)) {
				visualAttribute = "ZA";		
				workXmlI2B2Type = "PREV_QUERY";
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			}
			

			else if(XmlUtil.hasGroupTemplateTag(xml)) {
				visualAttribute = "ZA";		
				workXmlI2B2Type = "GROUP_TEMPLATE";
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			}
			
			else if(XmlUtil.hasQueryDefinitionTag(xml)) {
				visualAttribute = "ZA";		
				workXmlI2B2Type = "QUERY_DEFINITION";
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			}
			
			/// NEXT SET == ORDER IMPORTANT
			//  Have to rule out pdo before trying observations, patients, events, etc
			
			else if(XmlUtil.hasPatientDataTag(xml)) {
				visualAttribute = "ZA";		
				workXmlI2B2Type = "PDO";
				name = "PDO" +  MessageUtil.getInstance().getTimestamp();
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			}
			
			else if(XmlUtil.hasObservationTag(xml)) {
				visualAttribute = "ZA";		
				workXmlI2B2Type = "OBSERVATION";
				if(name.equals("PDO"))
					name = "OBS" +  MessageUtil.getInstance().getTimestamp();
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			}
			
			else if(XmlUtil.hasPatientTag(xml)) {
				/*visualAttribute = "ZA";		
				workXmlI2B2Type = "PATIENT";
				if(name.equals("PDO"))
					name = "PATIENT " + XmlUtil.getSiteId(xml)
					+":"+ XmlUtil.getPatientId(xml);
				createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			*/
				Element rootElement = xml.getAny().get(0);
				NodeList nameElements = rootElement.getElementsByTagName("patient_id");
				if (nameElements.getLength() != 0 && nameElements.item(0).getAttributes().getLength() != 0){				
				
				for(int i=0; i<nameElements.getLength();i++) {
					visualAttribute = "ZA";		
					workXmlI2B2Type = "PATIENT";
					String site = nameElements.item(i).getAttributes().item(0).getTextContent();
					String id = nameElements.item(i).getTextContent();
					if(name.equals("PDO"))
						name = "PATIENT " + site +":"+id;
					//XmlUtil.getSiteId(xml)+":"+ XmlUtil.getPatientId(xml);
					//////
					StringWriter strWriter = new StringWriter();
			        try {
			        
			        
			        JAXBUtil jaxbUtil = WorkplaceJAXBUtil.getJAXBUtil();
			       
			        PatientSet pdoPatientSet = new PatientSet();
			        //for (int i = 0, n = selection.length; i < n; i++) {
					
						

						// JAXBElement jaxbElement =
						// jaxbUtil.unMashallFromString(nodedata.xmlContent());
						// ResponseMessageType messageType =
						// (ResponseMessageType)jaxbElement.getValue();
						// BodyType bt = messageType.getMessageBody();
						// ResultResponseType resultResponseType =
						// (ResultResponseType) new
						// JAXBUnWrapHelper().getObjectByClass(bt.getAny(),
						// ResultResponseType.class);
						// QueryResultInstanceType queryResultInstanceType =
						// resultResponseType.getQueryResultInstance().get(0);
						// strWriter = new StringWriter();
						
						//log.info(jTable1.getSelectedColumn());
						
						//else {
							//site = "HIVE";
						//}
						//String hid = patientRowData.get(selection[i]-1).patientID;
						//String site = "HIVE";
						edu.harvard.i2b2.wkplclient.datavo.dnd.PatientType patientType = new edu.harvard.i2b2.wkplclient.datavo.dnd.PatientType();
						edu.harvard.i2b2.wkplclient.datavo.dnd.PatientSet patientSet = new edu.harvard.i2b2.wkplclient.datavo.dnd.PatientSet();
						patientType.setPatientId(id);//nodedata.patientID());
						// patientType.setUploadId(nodedata.patientSetID());
						//patientSet.setPatientSetId(nodedata.patientSetID());
						//patientSet.setPatientSetName(nodedata.queryName());
						patientSet.getPatient().add(patientType);
						
						PidType pidType = new PidType();
						PidType.PatientId pid = new PidType.PatientId();
						pid.setValue(id);//nodedata.patientID());
						pidType.setPatientId(pid);
						
						PidSet pset = new PidSet();
						pset.getPid().add(pidType);							
						
						PatientIdType pdoPidType = new PatientIdType();
						pdoPidType.setValue(id);
						pdoPidType.setSource(site);
						
						PatientType pdoPatientType = new PatientType();
						pdoPatientType.setPatientId(pdoPidType);
						pdoPatientSet.getPatient().add(pdoPatientType);
					//}

						DndType dnd = new DndType();
						// edu.harvard.i2b2.crcxmljaxb.datavo.pdo.ObjectFactory
						// pdoOf = new
						// edu.harvard.i2b2.crcxmljaxb.datavo.pdo.ObjectFactory();
						// dnd.getAny().add(patientType);
						
						//dnd.getAny().add(patientSet);
						//dnd.getAny().add(pset);
						dnd.getAny().add(pdoPatientSet);
						edu.harvard.i2b2.wkplclient.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.wkplclient.datavo.dnd.ObjectFactory();
						WorkplaceJAXBUtil.getJAXBUtil().marshaller(
								of.createPluginDragDrop(dnd), strWriter);
					} catch (JAXBUtilException e) {
						// log.error("Error marshalling Ont drag text");
						// throw e;
						e.printStackTrace();
					}
					//////
					createWorkplaceNode(workItem, currentTarget, XmlUtil.stringToXml(strWriter.toString())/*xml*/, name, workXmlI2B2Type, visualAttribute, moveFlag);
					name = "PDO";
				}
				}
			}
			
			if(visualAttribute.equals("")) {
				MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_INFORMATION | SWT.OK);
				mBox.setText("Unsupported Work Item Message");
				mBox.setMessage("Work item being dropped is not supported.");
				log.info("Work item being dropped is not supported.");
				int result = mBox.open();		
				return;
			}
			
			
/*			TreeData tdata = new TreeData();

			if(workItem != null){
				tdata.setTooltip(workItem.getTooltip());
				tdata.setName(workItem.getName());
				tdata.setIndex(workItem.getIndex());
			}
			else{
				tdata.setTooltip(workXmlI2B2Type + ":" + name);
				tdata.setName(name);
				tdata.setIndex(new AddChildRequestMessage().generateMessageId());		
			}

			tdata.setParentIndex(currentTarget.getData().getIndex());
			tdata.setVisualAttributes(visualAttribute);
			tdata.setWorkXml(xml);
			tdata.setWorkXmlI2B2Type(workXmlI2B2Type);
			tdata.setUserId(UserInfoBean.getInstance().getUserName());
			tdata.setGroupId(currentTarget.getData().getGroupId());
			tdata.setShareId(currentTarget.getData().getShareId());
			tdata.setWorkXmlSchema(null);
			tdata.setEntryDate(null);
			tdata.setChangeDate(null);
			tdata.setStatusCd(null);
			tdata.setTableCd(currentTarget.getData().getTableCd());
		
			TreeNode child = new TreeNode(tdata);
			currentTarget.addChild(child);
			// check to see if we are moving node or adding a new nod
			if(event.display.getData() != null) {
				if(event.display.getData().equals("M|O*V+E"))
					child.moveNode(viewer).start();
			}
			else
				child.addNode(viewer).start(); */
	//		createWorkplaceNode(workItem, currentTarget, xml, name, workXmlI2B2Type, visualAttribute, moveFlag);
			viewer.refresh();
		}

		else log.debug("No transfer happened -- data was null");
	}
	
	private XmlValueType createWorkXml(ConceptType concept) 
	{
		StringWriter strWriter = null;
		ConceptsType concepts = new ConceptsType();
		concepts.getConcept().add(concept);	
		try {
			strWriter = new StringWriter();
			DndType dnd = new DndType();
			edu.harvard.i2b2.wkplclient.datavo.vdo.ObjectFactory vdoOf = new edu.harvard.i2b2.wkplclient.datavo.vdo.ObjectFactory();
			dnd.getAny().add( vdoOf.createConcepts(concepts));

			edu.harvard.i2b2.wkplclient.datavo.dnd.ObjectFactory of = new edu.harvard.i2b2.wkplclient.datavo.dnd.ObjectFactory();
			WorkplaceJAXBUtil.getJAXBUtil().marshaller(of.createPluginDragDrop(dnd), strWriter);
			
			
		} catch (JAXBUtilException e) {
			log.error("Error marshalling Ont drag text");
		} 

		return XmlUtil.stringToXml(strWriter.toString());
	}
	
	private void createWorkplaceNode(FolderType workItem, TreeNode currentTarget, XmlValueType workXml,
									String name, String workXmlI2B2Type, String visualAttribute, Boolean moveFlag){
		TreeData tdata = new TreeData();

		if(workItem != null){
			tdata.setTooltip(workItem.getTooltip());
			tdata.setName(workItem.getName());
			if(moveFlag == true)
				tdata.setIndex(workItem.getIndex());
			else
				tdata.setIndex(new AddChildRequestMessage().generateMessageId());		
		}
		else{
			tdata.setTooltip(workXmlI2B2Type + ":" + name);
			tdata.setName(name);
			tdata.setIndex(new AddChildRequestMessage().generateMessageId());		
		}

		tdata.setParentIndex(currentTarget.getData().getIndex());
		tdata.setVisualAttributes(visualAttribute);
		tdata.setWorkXml(workXml);
		tdata.setWorkXmlI2B2Type(workXmlI2B2Type);
		tdata.setUserId(UserInfoBean.getInstance().getUserName());
		tdata.setGroupId(currentTarget.getData().getGroupId());
		tdata.setShareId(currentTarget.getData().getShareId());
		tdata.setWorkXmlSchema(null);
		tdata.setEntryDate(null);
		tdata.setChangeDate(null);
		tdata.setStatusCd(null);
		tdata.setTableCd(currentTarget.getData().getTableCd());
	
		TreeNode child = new TreeNode(tdata);
		currentTarget.addChild(child);
		// check to see if we are moving node or adding a new nod
		if(moveFlag == true){
				child.moveNode(viewer).start();
		}
		else {
			child.addNode(viewer).start();
		}
	}
}