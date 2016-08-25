/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.find;

import java.util.*;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.StringUtil;
import edu.harvard.i2b2.eclipse.plugins.ontology.views.find.NodeBrowser;
import edu.harvard.i2b2.eclipse.plugins.ontology.views.find.TreeData;

import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetModifierChildrenResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetModifiersResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.i2b2result.DataType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetCategoriesType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetChildrenType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetModifierChildrenType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetModifiersType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetReturnType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifierType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifiersType;

public class TreeNode
{
	private Log log = LogFactory.getLog(TreeNode.class.getName());
	private TreeData data;
	private List children = new ArrayList();
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

	public Object getParent()
	{
		return parent;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public TreeNode addChild(TreeNode child)
	{
		children.add(child);
		child.parent = this;
		return this;
	}

	public List getChildren()
	{
		return children;
	}

	public TreeData getData()
	{
		return this.data;
	}


	@Override
	public String toString()
	{
		return this.data.getName();
	}

	public String getIconKey()
	{
		String key = null;
		if(data.getModifier() != null){
			if (data.getModifier().getVisualattributes().substring(0,1).equals("R"))
			{
				key = "modifier";
			}
			else if (data.getModifier().
					getVisualattributes().substring(0,1).equals("D"))
			{
				key = "modifierFolder";
			}
			else if (data.getModifier().
					getVisualattributes().substring(0,1).equals("O"))
			{
				key = "modifierContainer";
			}
			return key;
		}
		if (data.getVisualattributes().substring(0,1).equals("F"))
		{
			if (isOpen())
				key = "openFolder";
			else 
				key = "closedFolder";

		}
		else if (data.getVisualattributes().equals("C-UNDEF"))
		{
			key = "undefined";
		}
		else if (data.getVisualattributes().substring(0,1).equals("C"))
		{
			if (isOpen())
				key = "openCase";
			else 
				key = "closedCase";
		}
		else if (data.getVisualattributes().substring(0,1).equals("L"))
		{
			key = "leaf";
		}

		else if (data.getVisualattributes().substring(0,1).equals("M"))
		{
			key = "multi";
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
					System.setProperty("statusMessage", e.getMessage());					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						//	theViewer.expandToLevel(theNode, 1);
						theViewer.refresh(theNode);
						//	theBrowser.refresh();
					}
				});
			}
		};
	}
	public void updateChildren(final Display theDisplay, final TreeViewer theViewer) 
	{
		try {
			GetChildrenType parentType = new GetChildrenType();
			parentType.setMax(Integer.parseInt(System.getProperty("OntFindMax")));
			parentType.setHiddens(Boolean.parseBoolean(System.getProperty("OntFindHiddens")));
			parentType.setSynonyms(Boolean.parseBoolean(System.getProperty("OntFindSynonyms")));
			parentType.setType("core");

			parentType.setParent(this.getData().getKey());

			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = OntServiceDriver.getChildren(parentType, "FIND");
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
						response = OntServiceDriver.getChildren(parentType, "FIND");
						procStatus = msg.processResult(response);
					}
				}
				//	/				else  -- other error codes
				// TABLE_ACCESS_DENIED and USER_INVALID, DATABASE ERROR
				else if (procStatus.getType().equals("ERROR")){
					System.setProperty("statusMessage",  procStatus.getValue());				
					theDisplay.syncExec(new Runnable() {
						public void run() {
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Server reports: " +  System.getProperty("statusMessage"));
							int result = mBox.open();
						}
					});
					return;
				}
			}
			ConceptsType allConcepts = msg.doReadConcepts();   	    
			List concepts = allConcepts.getConcept();
			getChildren().clear();
			getNodesFromXMLString(concepts);
		} catch (AxisFault e) {
			theDisplay.syncExec(new Runnable() {
				public void run() {
					MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});
		}catch (Exception e) {
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
		}


	}
	private void getNodesFromXMLString(List concepts){	    	
		Iterator it = concepts.iterator();

		while(it.hasNext()){
			TreeData child = new TreeData((ConceptType) it.next()); 		 

			TreeNode childNode = new TreeNode(child);
			// if the child is a folder/directory set it up with a leaf placeholder
			if((child.getVisualattributes().equals("FA")) || (child.getVisualattributes().equals("CA")))
			{
				TreeNode placeholder = new TreeNode(child.getLevel() + 1, "working...", "working...", "LA");
				placeholder.setOpen(true);
				childNode.addChild(placeholder);
			}
			else if	((child.getVisualattributes().equals("FH")) || (child.getVisualattributes().equals("CH")))
			{
				TreeNode placeholder = new TreeNode(child.getLevel() + 1, "working...", "working...", "LH");
				placeholder.setOpen(true);
				childNode.addChild(placeholder);
			}
			this.addChild(childNode);

		}	    	
	}

	public void updateCategories(final Display theDisplay, final TreeViewer theViewer) 
	{
		try {
//			GetReturnType request = new GetReturnType();
//			request.setType("limited");

			GetCategoriesType request = new GetCategoriesType();
			request.setType("core");
			request.setHiddens(false);
			request.setSynonyms(false);
			
			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = OntServiceDriver.getCategories(request, "FIND");
				procStatus = msg.processResult(response);

				//					if  other error codes
				//					TABLE_ACCESS_DENIED and USER_INVALID
				if (procStatus.getType().equals("ERROR")){
					System.setProperty("statusMessage",  procStatus.getValue());				
					theDisplay.syncExec(new Runnable() {
						public void run() {
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Server reports: " +  System.getProperty("statusMessage"));
							int result = mBox.open();
						}
					});
					return;
				}	

				procStatus.setType("DONE");
			}
			ConceptsType allConcepts = msg.doReadConcepts();   	    
			List concepts = allConcepts.getConcept();
			getNodesFromXMLString(concepts);	
		} catch (AxisFault e) {
			theDisplay.syncExec(new Runnable() {
				public void run() {
					MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});
		} catch (Exception e) {
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
					//				System.setProperty("statusMessage", e.getMessage());					
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

		GetModifierChildrenType parentType = new GetModifierChildrenType();

		parentType.setHiddens(Boolean.parseBoolean(System.getProperty("OntHiddens")));
		parentType.setSynonyms(Boolean.parseBoolean(System.getProperty("OntSynonyms")));

		parentType.setBlob(false);
		parentType.setType("core");

		parentType.setParent(this.getData().getModifier().getKey());		
		parentType.setAppliedPath(this.getData().getModifier().getAppliedPath());
		parentType.setAppliedConcept(this.getData().getFullName());

		GetModifierChildrenResponseMessage msg = new GetModifierChildrenResponseMessage();
		StatusType procStatus = null;	
		while(procStatus == null || !procStatus.getType().equals("DONE")){
			String response = OntServiceDriver.getModifierChildren(parentType, "FIND");

			procStatus = msg.processResult(response);

			//					else if  other error codes
			//					TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
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

	}
	private void getModifiersFromXMLString(List concepts, List<DataType> counts){   	


		if(concepts != null) {
			Iterator it = concepts.iterator();

			while(it.hasNext()){
				ConceptType concept = (ConceptType) it.next(); 
				TreeData child = new TreeData(concept); 
				child.setModifier(concept.getModifier());
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
} 