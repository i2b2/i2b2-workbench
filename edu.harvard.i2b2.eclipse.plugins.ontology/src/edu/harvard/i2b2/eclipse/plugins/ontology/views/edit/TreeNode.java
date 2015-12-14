/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import java.util.*;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import edu.harvard.i2b2.common.exception.I2B2Exception;

import edu.harvard.i2b2.eclipse.plugins.ontology.ws.CRCServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetChildrenResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetModifierChildrenResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetPsmResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;

import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.i2b2result.DataType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.QueryMasterType;
import edu.harvard.i2b2.ontclient.datavo.psm.query.QueryResultInstanceType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.ontclient.datavo.vdo.DeleteChildType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetCategoriesType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetChildrenType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetModifierChildrenType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetReturnType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifierType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifiersType;

public class TreeNode
{		
	private Log log = LogFactory.getLog(TreeNode.class.getName());
	private TreeData data;
	private List<TreeNode> children = new ArrayList();
	private TreeNode parent;
	private int result;


	private boolean open;

	public TreeNode(int level, String fullName, String name, String visualAttributes)
	{
		this.data = new TreeData(level, fullName, name, visualAttributes);
		open = false;
	}

	public TreeNode(TreeData data)
	{
		this.data = data;
		open = false;
	}

	public TreeNode(ConceptType concept)
	{
		this.data = new TreeData(concept);
		open = false;
	}

	public Object getParent()
	{
		return parent;
	}


	public TreeNode addChild(TreeNode child)
	{
		children.add(child);
		child.parent = this;
		return this;
	}

	public List<TreeNode> getChildren()
	{
		return children;
	}

	public TreeData getData()
	{
		return this.data;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public String toString()
	{
		if(this.data.getNumPatients() == null)
			return this.data.getName();

		else
			return this.data.getName() + " - " + this.data.getNumPatients();
	}

	public String getIconKey()
	{
		String key = null;
		if(data.getModifier() != null){
			if (data.getModifier().getVisualattributes().substring(0,1).equals("R"))
			{
				key = "modifier";
				if (!(data.getModifier().getVisualattributes().endsWith("E"))){
					key = "modifierLocked";

				}
				return key;
			}
			else if (data.getModifier().
					getVisualattributes().substring(0,1).equals("D"))
			{
				key = "modifierFolder";
				if (!(data.getModifier().getVisualattributes().endsWith("E"))){
					key = "modifierFolderLocked";

				}
				return key;
			}
			else if (data.getModifier().
					getVisualattributes().substring(0,1).equals("O"))
			{
				key = "modifierContainer";
				if (!(data.getModifier().getVisualattributes().endsWith("E"))){
					key = "modifierContainerLocked";

				}
				return key;
			}
			return key;
		}


		if (data.getVisualattributes().substring(0,1).equals("F"))
		{
			if (!(data.getVisualattributes().endsWith("E"))){
				key = "lockedFolder";
				return key;
			}
			if (isOpen())
				key = "openFolder";
			else 
				key = "closedFolder";
			return key;
		}
		else if (data.getVisualattributes().substring(0,1).equals("C"))
		{
			if (!(data.getVisualattributes().endsWith("E"))){
				key = "lockedCase";
				return key;
			}
			if (isOpen())
				key = "openCase";
			else 
				key = "closedCase";
			return key;
		}
		else if (data.getVisualattributes().substring(0,1).equals("L"))
		{
			if (!(data.getVisualattributes().endsWith("E"))){
				key = "leafLocked";
			}
			else 
				key = "leafPencil";
			return key;
		}
		else if (data.getVisualattributes().substring(0,1).equals("M"))
		{
			key = "multi";
		}
		else if (data.getVisualattributes().equals("C-ERROR"))
		{
			key = "error";
		}

		return key;
	}

	public Thread getXMLData(TreeViewer viewer, NodeBrowser browser) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			@Override
			public void run(){
				try {
					theNode.updateChildren(theDisplay, theViewer);
				} catch (Exception e) {
					// TODO Auto-generated catch block			
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						//	theViewer.expandToLevel(theNode, 1);
						theViewer.refresh(theNode);
					}
				});
			}
		};
	}

	public void updateChildren(final Display theDisplay, final TreeViewer theViewer) 
	{
		try {
			GetChildrenType parentType = new GetChildrenType();

		//	parentType.setMax(Integer.parseInt(System.getProperty("OntMax")));
			parentType.setMax(null);
			parentType.setHiddens(false);
			parentType.setSynonyms(false);

			parentType.setBlob(true);
			//			parentType.setType("all");

			parentType.setParent(this.getData().getKey());		

			GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = OntServiceDriver.getChildren(parentType, "EDIT");
				response = response.replace("<ValueMetadata>","<ns6:ValueMetadata xmlns:ns6=\"http://www.i2b2.org/xsd/cell/ont/1.1/\">");
				response = response.replace("</ValueMetadata>","</ns6:ValueMetadata>");

				procStatus = msg.processResult(response);
				if(procStatus.getValue().equals("MAX_EXCEEDED")) {
					theDisplay.syncExec(new Runnable() {
						public void run() {
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), 
									SWT.ICON_QUESTION | SWT.YES | SWT.NO);
							mBox.setText("Please Note ...");
							mBox.setMessage("Max number of terms exceeded please try with a more specific query.\n"
									+ "Populating the node will be slow\n"
									+"Do you want to continue?");
							result = mBox.open();
						}
					});
					if(result == SWT.NO) {
						TreeNode node = (TreeNode) this.getChildren().get(0);
						node.getData().setName("Over maximum number of child nodes");
						procStatus.setType("DONE");
					}
					else {
						parentType.setMax(null);
						response = OntServiceDriver.getChildren(parentType, "EDIT");
						response = response.replace("<ValueMetadata>","<ns6:ValueMetadata xmlns:ns6=\"http://www.i2b2.org/xsd/cell/ont/1.1/\">");
						response = response.replace("</ValueMetadata>","</ns6:ValueMetadata>");

						procStatus = msg.processResult(response);
					}
				}
				//				else if  other error codes
				//				TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
				else if (procStatus.getType().equals("ERROR")){		
					System.setProperty("errorMessage",  procStatus.getValue());				
					theDisplay.syncExec(new Runnable() {
						public void run() {
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Server reports: " +  System.getProperty("errorMessage"));
							int result = mBox.open();
						}
					});
					getChildren().clear();
					return;
				}			
			}
			ConceptsType allConcepts = msg.doReadConcepts();   	  
			if (allConcepts != null){
				List concepts = allConcepts.getConcept();
				getChildren().clear();
				getNodesFromXMLString(concepts, null);
			}	

		} catch (AxisFault e) {
			log.error(e.getMessage());
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});
			getChildren().clear();
		} catch (I2B2Exception e) {
			log.error(e.getMessage());
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Your system does not have enough memory \n  to display the contents of this folder.");
					int result = mBox.open();
				}
			});		
			getChildren().clear();
		} catch (Exception e) {
			log.error(e.getMessage());
			//			disableCountButton();
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Error message delivered from the remote server\n" +  
					"You may wish to retry your last action");
					int result = mBox.open();
				}
			});		
			getChildren().clear();
		}

	}
	public void getNodesFromXMLString(List concepts, List<DataType> counts){   	

		if(concepts != null) {
			Iterator it = concepts.iterator();

			while(it.hasNext()){
				TreeData child = new TreeData((ConceptType) it.next()); 
				// TODO check button to display counts.
				child.setNumPatients(counts);

				TreeNode childNode = new TreeNode(child);   		
				// if the child is a folder/directory set it up with a leaf placeholder
				if((child.getVisualattributes().startsWith("FA")) || (child.getVisualattributes().startsWith("CA")))  
				{
					TreeNode placeholder = new TreeNode(child.getLevel() + 1, "working...", "working...", "LA");
					ModifierType modifier = new ModifierType();
					modifier.setName("working...");
					modifier.setVisualattributes("RA");
					placeholder.getData().setModifier(modifier);
					placeholder.setOpen(true);
					childNode.addChild(placeholder);
				}
				else if	((child.getVisualattributes().startsWith("FH")) || (child.getVisualattributes().startsWith("CH")))
				{
					TreeNode placeholder = new TreeNode(child.getLevel() + 1, "working...", "working...", "LH");
					ModifierType modifier = new ModifierType();
					modifier.setName("working...");
					modifier.setVisualattributes("RA");
					placeholder.getData().setModifier(modifier);
					placeholder.setOpen(true);
					childNode.addChild(placeholder);
				}
				
    			if(System.getProperty("OntEditConceptCode").equals("true"))
    			{
    				if ((childNode.data.getBasecode() != null) && (childNode.data.getBasecode().length() != 0))
    					childNode.data.setTooltip(childNode.data.getTooltip() + " - " + childNode.data.getBasecode());
    			}
				this.addChild(childNode);

			} 	
		}
	}


	public void getCategories(TreeViewer viewer, NodeBrowser browser) {
		final TreeNode theRoot = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();

		try {
			theRoot.updateCategories(theDisplay, theViewer);
			theViewer.expandToLevel(theRoot, 1);
			theViewer.refresh(theRoot);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());					
		}

	}




	public void updateCategories(final Display theDisplay, final TreeViewer theViewer) 
	{
		try {
//			GetReturnType request = new GetReturnType();
//			request.setType("core");

			GetCategoriesType request = new GetCategoriesType();
			request.setType("core");
			request.setHiddens(false);
			request.setSynonyms(false);
			
			
			
			GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = OntServiceDriver.getCategories(request, "EDIT");

				response = response.replace("<ValueMetadata>","<ns6:ValueMetadata xmlns:ns6=\"http://www.i2b2.org/xsd/cell/ont/1.1/\">");
				response = response.replace("</ValueMetadata>","</ns6:ValueMetadata>");
				procStatus = msg.processResult(response);

				//				if  other error codes
				//				TABLE_ACCESS_DENIED and USER_INVALID
				if (procStatus.getType().equals("ERROR")){

					System.setProperty("errorMessage",  procStatus.getValue());				
					return;
				}	
				procStatus.setType("DONE");
			}
			ConceptsType allConcepts = msg.doReadConcepts();   	    
			List concepts = allConcepts.getConcept();
			getNodesFromXMLString(concepts, null);	
		} catch (AxisFault e) {
			log.error(e.getMessage());
			System.setProperty("errorMessage",  "Ontology cell is unavailable");
		} catch (I2B2Exception e) {
			log.error(e.getMessage());
			System.setProperty("errorMessage", e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			System.setProperty("errorMessage",  "Remote server is unavailable");
		}
	}


	public Thread getModXMLData(TreeViewer viewer, NodeBrowser browser) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			@Override
			public void run(){
				try {
					theNode.updateModifierChildren(theDisplay, theViewer);
				} catch (Exception e) {
					// TODO Auto-generated catch block			
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						//	theViewer.expandToLevel(theNode, 1);
						theViewer.refresh(theNode);
					}
				});
			}
		};
	}

	public void updateModifierChildren(final Display theDisplay, final TreeViewer theViewer) 
	{
		//		try {
		GetModifierChildrenType parentType = new GetModifierChildrenType();

		parentType.setHiddens(Boolean.parseBoolean(System.getProperty("OntHiddens")));
		parentType.setSynonyms(Boolean.parseBoolean(System.getProperty("OntSynonyms")));

		//		log.info("sent : " + parentType.getMax() + System.getProperty("OntMax") + System.getProperty("OntHiddens")
		//				+ System.getProperty("OntSynonyms") );

		//		parentType.setMax(150);
		parentType.setBlob(true);
		parentType.setType("core");

		parentType.setParent(this.getData().getModifier().getKey());		
		parentType.setAppliedPath(this.getData().getModifier().getAppliedPath());
		parentType.setAppliedConcept(ModifierComposite.getInstance().getConceptNode().getData().getFullName());
		
		GetModifierChildrenResponseMessage msg = new GetModifierChildrenResponseMessage();
		StatusType procStatus = null;	
		while(procStatus == null || !procStatus.getType().equals("DONE")){
			String response = OntServiceDriver.getModifierChildren(parentType, "ONT");
			response = response.replace("<ValueMetadata>","<ns6:ValueMetadata xmlns:ns6=\"http://www.i2b2.org/xsd/cell/ont/1.1/\">");
			response = response.replace("</ValueMetadata>","</ns6:ValueMetadata>");

			procStatus = msg.processResult(response);

			//				else if  other error codes
			//				TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
			if (procStatus.getType().equals("ERROR")){		
				System.setProperty("errorMessage",  procStatus.getValue());				
				theDisplay.syncExec(new Runnable() {
					public void run() {
						MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Server reports: " +  System.getProperty("errorMessage"));
						int result = mBox.open();
					}
				});
				getChildren().clear();
				return;
			}
		}
		getChildren().clear();
		ModifiersType allModifiers = msg.doReadModifiers();   
		ConceptsType concepts = new ConceptsType();
		if (allModifiers != null){
			// convert list of modifiers to list of concepts
			List<ModifierType> modifiers = allModifiers.getModifier();
			if(!modifiers.isEmpty()){	
				Iterator<ModifierType> it = modifiers.iterator();
				while(it.hasNext()){

					ConceptType concept = this.getData();
					TreeData data = new TreeData(concept);

					data.setModifier(	(ModifierType)it.next());
					concepts.getConcept().add(data);
				}
			}


			List<DataType> counts = null;
			getModifiersFromXMLString(concepts.getConcept(), counts);
		}	


		/*		} catch (AxisFault e) {
			log.error(e.getMessage());
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});
			getChildren().clear();
		} catch (I2B2Exception e) {
			log.error(e.getMessage());
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Your system does not have enough memory \n  to display the contents of this folder.");
					int result = mBox.open();
				}
			});		
			getChildren().clear();
		} catch (Exception e) {
			log.error(e.getMessage());
//			disableCountButton();
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Error message delivered from the remote server\n" +  
					"You may wish to retry your last action");
					int result = mBox.open();
				}
			});		
			getChildren().clear();
		}*/

	}
	private void getModifiersFromXMLString(List concepts, List<DataType> counts){   	


		if(concepts != null) {
			Iterator it = concepts.iterator();

			while(it.hasNext()){
				ConceptType concept = (ConceptType) it.next(); 
				TreeData child = new TreeData(concept); 

				TreeNode childNode = new TreeNode(child);
				// TODO check button to display counts.
				if((child.getModifier().getVisualattributes().startsWith("DA")) || (child.getModifier().getVisualattributes().startsWith("OA")))  
				{

					TreeNode placeholder = new TreeNode(child.getLevel() + 1, "working...", "working...", "RA");
					ModifierType modifier = new ModifierType();
					modifier.setName("working...");
					modifier.setVisualattributes("RA");
					placeholder.getData().setModifier(modifier);			
					placeholder.setOpen(true);
					childNode.addChild(placeholder);
					this.addChild(childNode);

				}
				else if	((child.getModifier().getVisualattributes().startsWith("DH")) || (child.getModifier().getVisualattributes().startsWith("OH")))
				{

					TreeNode placeholder = new TreeNode(child.getLevel() + 1, "working...", "working...", "RH");
					ModifierType modifier = new ModifierType();
					modifier.setName("working...");
					modifier.setVisualattributes("RA");
					placeholder.getData().setModifier(modifier);
					placeholder.setOpen(true);
					childNode.addChild(placeholder);
					this.addChild(childNode);

				}
				else if((child.getModifier().getVisualattributes().startsWith("R"))){

					this.addChild(childNode);

				}

			}


		}
	}


	private List<DataType> getCounts(GetChildrenType parentType){
		QueryMasterType queryMaster = null;
		try {    		
			String response = null;
			GetPsmResponseMessage r_msg = new GetPsmResponseMessage();
			StatusType procStatus = null;	

			// send request to start the count process
			response = CRCServiceDriver.getChildrenCount(parentType.getParent());
			procStatus = r_msg.processResult(response);		

			if (procStatus.getType().equals("ERROR")){
				System.setProperty("errorMessage",  procStatus.getValue());				
				return null;
			}	

			queryMaster = r_msg.extractQueryMaster(response);
			if(queryMaster != null){

				QueryResultInstanceType resultInstanceId = r_msg.extractResultInstance(response);
				procStatus = null;

				// sent request to get count results
				response = CRCServiceDriver.getChildrenCount(resultInstanceId);
				procStatus = r_msg.processResult(response);		

				if (procStatus.getType().equals("ERROR")){
					System.setProperty("errorMessage",  procStatus.getValue());			
					String delResponse = CRCServiceDriver.deleteQueryMaster(queryMaster);
					return null;
				}	

				String delResponse = CRCServiceDriver.deleteQueryMaster(queryMaster);

				return r_msg.extractXMLResult(response);
			}else
				return null;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//    		disableCountButton();
			log.info("Problem processing number of patients with concept");    		
			return null;
		}
	}

	public DeleteChildType getDeleteChildType(){
		DeleteChildType delChild = new DeleteChildType();

		if(this.getData().getModifier() != null){
			delChild.setKey(this.getData().getModifier().getKey());
			delChild.setBasecode(this.getData().getModifier().getBasecode());
			delChild.setLevel(this.getData().getModifier().getLevel());
			delChild.setName(this.getData().getModifier().getName());
			delChild.setSynonymCd(this.getData().getModifier().getSynonymCd());
			delChild.setVisualattribute(this.getData().getModifier().getVisualattributes());
		}
		else {
			delChild.setKey(this.getData().getKey());
			delChild.setBasecode(this.getData().getBasecode());
			delChild.setLevel(this.getData().getLevel());
			delChild.setName(this.getData().getName());
			delChild.setSynonymCd(this.getData().getSynonymCd());
			delChild.setVisualattribute(this.getData().getVisualattributes());
		}

		return delChild;
	}
}