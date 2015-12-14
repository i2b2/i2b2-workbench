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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBElement;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.MessageUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.StringUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.WorkplaceJAXBUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.XmlUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.AddChildRequestMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.AnnotateChildResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.DeleteChildResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.GetChildrenResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.MoveChildResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.ProtectResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.RenameChildResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.ExportChildResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceResponseData;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceServiceDriver;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.ExportChildType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.ProtectedType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.XmlValueType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.AnnotateChildType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.ChildType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.DeleteChildType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FolderType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FoldersType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.GetChildrenType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.GetReturnType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.RenameChildType;

public class TreeNode
{		
	private Log log = LogFactory.getLog(TreeNode.class.getName());
	private TreeData data;
	private List children = new ArrayList();
	private TreeNode parent;
	private int result;


	public TreeNode(String index, String name, String visualAttributes)
	{
		this.data = new TreeData(index, name, visualAttributes);
	}


	public TreeNode(TreeData data)
	{
		this.data = data;
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

	public List getChildren()
	{
		return children;
	}

	public TreeData getData()
	{
		return this.data;
	}


	public String toString()
	{
		return this.data.getName();
	}

	public String getIconKey()
	{
		String key = null;
		if (data.getVisualAttributes().substring(0,1).equals("F"))
		{
			if ((data.getVisualAttributes().substring(1).equals("A")) ||
					(data.getVisualAttributes().substring(1).equals("I"))  ||
					(data.getVisualAttributes().substring(1).equals("H")))
				key = "closedFolder";
			else if ((data.getVisualAttributes().substring(1).equals("AO")) ||
					(data.getVisualAttributes().substring(1).equals("IO")) ||
					(data.getVisualAttributes().substring(1).equals("HO")))	
				key = "openFolder";
		}
		else if (data.getVisualAttributes().substring(0,1).equals("C"))
		{
			if ((data.getVisualAttributes().substring(1).equals("A")) ||
					(data.getVisualAttributes().substring(1).equals("I"))  ||
					(data.getVisualAttributes().substring(1).equals("H")))
				key = "closedCase";
			else if ((data.getVisualAttributes().substring(1).equals("AO")) ||
					(data.getVisualAttributes().substring(1).equals("IO")) ||
					(data.getVisualAttributes().substring(1).equals("HO")))	
				key = "openCase";
		}
		else if (data.getVisualAttributes().substring(0,1).equals("L"))
		{
			key = "leaf";
		}
		else if (data.getVisualAttributes().substring(0,1).equals("Z"))
		{
			if (data.getVisualAttributes().equals("ZAF"))
				key = "conceptFA";
			else
				key = data.getWorkXmlI2B2Type().toLowerCase();

			//    		if (data.getWorkXmlI2B2Type().equals("PATIENT_COLL"))
			//    			key = "patient_coll";
			//    		else if (data.getWorkXmlI2B2Type().equals("CONCEPT"))
			//    			key = "leaf";
			//    		else if (data.getWorkXmlI2B2Type().equals("XML_RESULTS"))
			//    			key = "patientCount";
			//    		else if (data.getWorkXmlI2B2Type().equals("PREV_QUERY"))
			//    			key = "prevQuery";
			//    		else if (data.getWorkXmlI2B2Type().equals("GROUP_TEMPLATE"))
			//    			key = "template";
			//    		else if (data.getWorkXmlI2B2Type().equals("QUERY_DEFINITION"))
			//    			key = "query";
			//    		else if (data.getWorkXmlI2B2Type().equals("OBSERVATIONS"))
			//    			key = "observations";
		}
		else if (data.getVisualAttributes().substring(0,1).equals("M"))
		{
			key = "multi";
		}
		else if (data.getVisualAttributes().equals("C-ERROR"))
		{
			key = "error";
		}


		return key;
	}
	public Thread getXMLData(TreeViewer viewer) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
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
					}
				});
			}
		};
	}

	public void updateChildren(final Display theDisplay, final TreeViewer theViewer) 
	{
		try {
			GetChildrenType parentType = new GetChildrenType();
			parentType.setBlob(true);
			parentType.setParent("\\\\" + this.getData().getTableCd() + "\\" + this.getData().getIndex());	

			//		log.info(parentType.getParent());
			//		log.info(this.getData().getHierarchy());

			GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = WorkplaceServiceDriver.getChildren(parentType);

				procStatus = msg.processResult(response);
				if(procStatus.getValue().equals("MAX_EXCEEDED")) {
					theDisplay.syncExec(new Runnable() {
						public void run() {
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), 
									SWT.ICON_QUESTION | SWT.YES | SWT.NO);
							mBox.setText("Please Note ...");
							mBox.setMessage("The node has exceeded maximum number of children\n"
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
						response = WorkplaceServiceDriver.getChildren(parentType);
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
					return;
				}			
			}
			FoldersType allFolders = msg.doReadFolders();   	  
			if (allFolders != null){
				List folders = allFolders.getFolder();
				getChildren().clear();
				getNodesFromXMLString(folders);
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
		} catch (Exception e) {
			log.error(e.getMessage());
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
	private void getNodesFromXMLString(List folders){   	

		if(folders != null) {
			Iterator it = folders.iterator();

			while(it.hasNext()){
				TreeData child = new TreeData((FolderType) it.next()); 	
				TreeNode childNode = new TreeNode(child);
				// if the child is a folder/directory set it up with a leaf placeholder
				if((child.getVisualAttributes().equals("FA")) || (child.getVisualAttributes().equals("CA")))  
				{
					TreeNode placeholder = new TreeNode("working...", "working...", "LAO");
					childNode.addChild(placeholder);
				}
				else if	((child.getVisualAttributes().equals("FH")) || (child.getVisualAttributes().equals("CH")))
				{
					TreeNode placeholder = new TreeNode("working...", "working...", "LHO");
					childNode.addChild(placeholder);
				}
				this.addChild(childNode);

			} 	
		}
	}


	public void getHomeFolders(TreeViewer viewer) {
		final TreeNode theRoot = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();

		try {
			theRoot.updateFolders(theDisplay, theViewer);
			theViewer.expandToLevel(theRoot, 1);
			theViewer.refresh(theRoot);
		} catch (Exception e) {
			log.error(e.getMessage());					
		}

	}

	public void updateFolders(final Display theDisplay, final TreeViewer theViewer) 
	{
		try {
			GetReturnType request = new GetReturnType();
			request.setType("core");

			GetChildrenResponseMessage msg = new GetChildrenResponseMessage();
			StatusType procStatus = null;	
			String response = null;
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				//String response = null;
				if(Boolean.parseBoolean(System.getProperty("WPManager")))
					response = WorkplaceServiceDriver.getHomeFoldersByProject(request);
				else
					response = WorkplaceServiceDriver.getHomeFoldersByUserId(request);
				procStatus = msg.processResult(response);

				//				if  other error codes
				//				TABLE_ACCESS_DENIED and USER_INVALID
				if (procStatus.getType().equals("ERROR")){

					System.setProperty("errorMessage",  procStatus.getValue());				

					return;
				}	
				procStatus.setType("DONE");
			}
			
			JAXBElement jaxbElement = WorkplaceJAXBUtil.getJAXBUtil().unMashallFromString(response);
			ResponseMessageType respMessageType  = (ResponseMessageType) jaxbElement.getValue();
			String version = respMessageType.getMessageHeader().getSendingApplication().getApplicationVersion();
			System.setProperty("wkplServerVersion", version);
			double vernum = Double.parseDouble(version);
			log.info("Workplace server version: "+version);
	
			FoldersType allFolders = msg.doReadFolders();   	    
			List folders = allFolders.getFolder();
			getNodesFromXMLString(folders);	
		} catch (AxisFault e) {
			log.error(e.getMessage());
			System.setProperty("errorMessage",  "Workplace cell is unavailable");
		} catch (I2B2Exception e) {
			log.error(e.getMessage());
			System.setProperty("errorMessage",  e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			System.setProperty("errorMessage",  "Remote server is unavailable");

		}
	}
	
	public Thread protectNode(TreeViewer viewer) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			public void run(){
				try {
					theNode.protect(theDisplay, theViewer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.setProperty("statusMessage", e.getMessage());					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						//	  ((TreeNode)(theNode.getParent())).getChildren().remove(theNode);
						theViewer.refresh(theNode.getParent());
					}
				});
			}
		};
	}
	
	public void protect(final Display theDisplay, final TreeViewer theViewer) 
	{
		XmlValueType newWorkXml = null;
		//String theNewName = new String("test_protected_access");
		try {
			ProtectResponseMessage msg = new ProtectResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){

				ProtectedType childType = new ProtectedType();
				childType.setIndex("\\\\" + this.getData().getTableCd() +  "\\" + this.getData().getIndex());
				//childType.setName(theNewName);
				//newWorkXml = updateWorkXml(theNewName);
				//childType.setWorkXml(newWorkXml);
				if(this.getData().getProtectedAccess().equalsIgnoreCase("Y")) {
					childType.setProtectedAccess("false");
				}
				else {
					childType.setProtectedAccess("true");
				}
				String response = WorkplaceServiceDriver.protectChild(childType);

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
					return;
				}			
			}
			//this.getData().setName(theNewName);
			//this.getData().protected_access(true);
			if(this.getData().getProtectedAccess().equalsIgnoreCase("Y")) {
				this.getData().setProtectedAccess("N");
			}
			else {
				this.getData().setProtectedAccess("Y");
			}
			if (newWorkXml != null)
				this.getData().setWorkXml(newWorkXml);
			/*  old code before rename fix
			 * if((this.getData().getWorkXmlI2B2Type().equals("CONCEPT"))) {
				Element rootElement = this.getData().getWorkXml().getAny().get(0);
				NodeList nameElements = rootElement.getElementsByTagName("name");
				nameElements.item(0).setTextContent(theNewName);	   

				NodeList synonymElements = rootElement.getElementsByTagName("synonym_cd");
				if(synonymElements.item(0) != null)
					synonymElements.item(0).setTextContent("Y");
			}
			 */
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

		} catch (Exception e) {
			log.error(e.getMessage());
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

	public Thread deleteNode(TreeViewer viewer) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			public void run(){
				try {
					theNode.delete(theDisplay, theViewer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.setProperty("statusMessage", e.getMessage());					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						//	  ((TreeNode)(theNode.getParent())).getChildren().remove(theNode);
						theViewer.refresh(theNode.getParent());
					}
				});
			}
		};
	}

	public void delete(final Display theDisplay, final TreeViewer theViewer) 
	{
		try {
			DeleteChildType childType = new DeleteChildType();

			//			childType.setNode(this.getData().getHierarchy());	
			childType.setNode("\\\\" + this.getData().getTableCd() +  "\\" + this.getData().getIndex());

			DeleteChildResponseMessage msg = new DeleteChildResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = WorkplaceServiceDriver.deleteChild(childType);

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
					return;
				}			
			}
			((TreeNode)(this.getParent())).getChildren().remove(this);
			//		theViewer.refresh(this.getParent());	

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
		} catch (Exception e) {
			log.error(e.getMessage());
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

	public Thread moveNode(TreeViewer viewer) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			public void run(){
				try {
					theNode.move(theDisplay, theViewer);
					if(theNode.getData().getVisualAttributes().startsWith("F"))
						theNode.updateChildren(theDisplay, theViewer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.setProperty("statusMessage", e.getMessage());					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						theViewer.refresh(theNode.getParent());
						theViewer.refresh(theNode);
					}
				});
			}
		};
	}

	public void move(final Display theDisplay, final TreeViewer theViewer) 
	{
		try {
			ChildType childType = new ChildType();
			childType.setNode("\\\\" + this.getData().getTableCd() + "\\" + this.getData().getIndex());
			childType.setParent(this.getData().getParentIndex());

			MoveChildResponseMessage msg = new MoveChildResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = WorkplaceServiceDriver.moveChild(childType);

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
					return;
				}			
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
		} catch (Exception e) {
			log.error(e.getMessage());
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



	public Thread renameNode(TreeViewer viewer) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();

		String newName = null;
		InputDialog inputDialog = null;
		if(theNode.getData().getVisualAttributes().startsWith("F")){
			//		if(theNode.getData().getName().equals("New Folder")){
			inputDialog = new InputDialog(theDisplay.getActiveShell(), 
					"Rename Folder Dialog", "Rename this folder to: ",
					theNode.getData().getName(), null);
		}
		else {
			inputDialog = new InputDialog(theDisplay.getActiveShell(), 
					"Rename Work Item Dialog", "Rename this work item to: ",
					theNode.getData().getName(), null);
		}
		if(inputDialog.open() == Window.OK){
			newName = inputDialog.getValue();
		}

		//	log.info(newName);
		final String theNewName = newName;

		return new Thread() {
			public void run(){
				try {
					if(theNewName != null)   // do nothing on cancel
						theNode.rename(theDisplay, theViewer, theNewName);
				} catch (Exception e) {
					log.error("Rename node error");					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						theViewer.refresh(theNode);
					}
				});
			}
		};
	}

	public void rename(final Display theDisplay, final TreeViewer theViewer, final String theNewName)
	{
		XmlValueType newWorkXml = null;
		try {
			RenameChildResponseMessage msg = new RenameChildResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){

				RenameChildType childType = new RenameChildType();
				childType.setNode("\\\\" + this.getData().getTableCd() +  "\\" + this.getData().getIndex());
				childType.setName(theNewName);
				newWorkXml = updateWorkXml(theNewName);
				childType.setWorkXml(newWorkXml);
				String response = WorkplaceServiceDriver.renameChild(childType);

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
					return;
				}			
			}
			this.getData().setName(theNewName);
			if (newWorkXml != null)
				this.getData().setWorkXml(newWorkXml);
			/*  old code before rename fix
			 * if((this.getData().getWorkXmlI2B2Type().equals("CONCEPT"))) {
				Element rootElement = this.getData().getWorkXml().getAny().get(0);
				NodeList nameElements = rootElement.getElementsByTagName("name");
				nameElements.item(0).setTextContent(theNewName);	   

				NodeList synonymElements = rootElement.getElementsByTagName("synonym_cd");
				if(synonymElements.item(0) != null)
					synonymElements.item(0).setTextContent("Y");
			}
			 */
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

		} catch (Exception e) {
			log.error(e.getMessage());
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

	private XmlValueType updateWorkXml(String newName)
	{
		if(this.getData().getWorkXml() == null)
			return null;
		if((this.getData().getWorkXmlI2B2Type().equals("CONCEPT"))) {
			Element rootElement = this.getData().getWorkXml().getAny().get(0);
			NodeList nameElements = rootElement.getElementsByTagName("name");
			nameElements.item(0).setTextContent(newName);	   

			NodeList synonymElements = rootElement.getElementsByTagName("synonym_cd");
			if(synonymElements.item(0) != null)
				synonymElements.item(0).setTextContent("Y");
		}
		else if((this.getData().getWorkXmlI2B2Type().equals("PATIENT_COLL"))) {
			Element rootElement = this.getData().getWorkXml().getAny().get(0);
			NodeList descriptionElements = rootElement.getElementsByTagName("description");
			descriptionElements.item(0).setTextContent(newName);	
		}
		else if((this.getData().getWorkXmlI2B2Type().equals("ENCOUNTER_COLL"))) {
			Element rootElement = this.getData().getWorkXml().getAny().get(0);
			NodeList descriptionElements = rootElement.getElementsByTagName("description");
			descriptionElements.item(0).setTextContent(newName);	
		}
		else {
			Element rootElement = this.getData().getWorkXml().getAny().get(0);
			NodeList nameElements = rootElement.getElementsByTagName("name");
			// Group templates dont have tag 'name'
			if (nameElements.getLength() == 0){
				nameElements = rootElement.getElementsByTagNameNS("*", "panel");
				if (nameElements.getLength() == 0){
					nameElements = rootElement.getElementsByTagName("query_name");
					if (nameElements.getLength() == 0){
						// if we get to here and no name has been found then its a PDO.
						// return generically -- change to obs or event etc one level up.
						return this.getData().getWorkXml();
					} 
					// query_name
					else {
						nameElements.item(0).setTextContent(newName);
					}
				}
				//panel / template name
				else {
					nameElements.item(0).getAttributes().getNamedItem("name").setNodeValue(newName);
				}
			}
			// prev query name
			else
				nameElements.item(0).setTextContent(newName);	
		}

		return this.getData().getWorkXml();
	}


	public Thread exportNode(TreeViewer viewer) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();

		String newTooltip = null;


		// 3) The user agrees to overwrite existing file
		boolean done = false;
		String xmlFilename  = null;
		FileDialog fd = new FileDialog(theDisplay.getActiveShell(), SWT.SAVE);
		fd.setText("Export");
		String[] filterExt = { "*.xml", "*.*" };
		fd.setFilterExtensions(filterExt);

		while (!done) {
			// Open the File Dialog
			xmlFilename = fd.open();
			if (xmlFilename == null) {
				// User has cancelled, so quit and return
				done = true;
			} else {
				// User has selected a file; see if it already exists
				File file = new File(xmlFilename);
				if (file.exists()) {
					// The file already exists; asks for confirmation
					MessageBox mb = new MessageBox(fd.getParent(), SWT.ICON_WARNING
							| SWT.YES | SWT.NO);

					// We really should read this string from a
					// resource bundle
					mb.setMessage(xmlFilename + " already exists. Do you want to replace it?");

					// If they click Yes, we're done and we drop out. If
					// they click No, we redisplay the File Dialog
					done = mb.open() == SWT.YES;
				} else {
					// File does not exist, so drop out
					done = true;
				}
			}
		}
		final String thexmlFilename = xmlFilename;
		/*
        FileDialog fd = new FileDialog(theDisplay.getActiveShell(), SWT.SAVE);
        fd.setText("Export");
        String[] filterExt = { "*.xml", "*.*" };
        fd.setFilterExtensions(filterExt);
        final String xmlFilename = fd.open();
		 */
		return new Thread() {
			public void run(){
				theNode.export(theDisplay, theViewer, thexmlFilename, theNode);
			}
		};


		/*
		InputDialog inputDialog = new InputDialog(theDisplay.getActiveShell(), 
				"Annotate Work Item Dialog", "Annotate this work item: ",
				theNode.getData().getTooltip(), null);

		if(inputDialog.open() == Window.OK){
			newTooltip = inputDialog.getValue();
		}

	//	log.info(newName);
		final String theNewTooltip = newTooltip;

		return new Thread() {
			public void run(){
				try {
					if(theNewTooltip != null)   // do nothing on cancel
						theNode.annotate(theDisplay, theViewer, theNewTooltip);
				} catch (Exception e) {
					log.error("Annotate node error");					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						  theViewer.refresh(theNode);
					}
				});
			}
		};
		 */
	}

	public void export (final Display theDisplay, final TreeViewer theViewer, final String thexmlFilename, TreeNode theNode)
	{
		try {


			org.w3c.dom.Element dndElement = theNode.getData().getWorkXml().getAny().get(0);
			org.jdom.input.DOMBuilder builder = new DOMBuilder();
			org.jdom.Element jdomElement = builder.build(dndElement);
			String myXmldata = (new XMLOutputter()).outputString(jdomElement);



			if (myXmldata.contains("<ns4:query_master"))
			{
				String qmid = myXmldata.substring(
						myXmldata.indexOf("<query_master_id>")+17,
						myXmldata.indexOf("</query_master_id>"));
				ExportChildResponseMessage msg = new ExportChildResponseMessage();
				StatusType procStatus = null;	
				while(procStatus == null || !procStatus.getType().equals("DONE")){

					ExportChildType childType = new ExportChildType();
					childType.setNode(qmid);
					childType.setType("QM");
					//childType.setNode("\\\\" + this.getData().getTableCd() +  "\\" + this.getData().getIndex());
					String response = WorkplaceServiceDriver.exportChild(childType);

					procStatus = msg.processResult(response);

					myXmldata = msg.processBody(response);
					//myXmldata = response;
					//myXmldata = msg.processBody(response);
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
						return;
					}			
				}												
				//myXmldata = XmlUtil.getInstance().writeQueryXML(myXmldata);

			} else if (myXmldata.contains("<ns4:query_result_instance"))
			{
				String qmid = myXmldata.substring(
						myXmldata.indexOf("<result_instance_id>")+20,
						myXmldata.indexOf("</result_instance_id>"));
				ExportChildResponseMessage msg = new ExportChildResponseMessage();
				StatusType procStatus = null;	
				while(procStatus == null || !procStatus.getType().equals("DONE")){

					ExportChildType childType = new ExportChildType();
					childType.setNode(qmid);
					childType.setType("QR");
					//childType.setNode("\\\\" + this.getData().getTableCd() +  "\\" + this.getData().getIndex());
					String response = WorkplaceServiceDriver.exportChild(childType);

					procStatus = msg.processResult(response);


					myXmldata = msg.processBody(response);
					//myXmldata = msg.processBody(response);
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
						return;
					}			
				}												
				//myXmldata = XmlUtil.getInstance().writeQueryXML(myXmldata);

			}
			//this.getData().setTooltip(theNewTooltip);

			FileWriter outFile = new FileWriter(thexmlFilename);
			PrintWriter out = new PrintWriter(outFile);
			out.println(myXmldata);
			out.close();

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

		} catch (Exception e) {
			log.error(e.getMessage());
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

	public Thread annotateNode(TreeViewer viewer) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();

		String newTooltip = null;
		InputDialog inputDialog = new InputDialog(theDisplay.getActiveShell(), 
				"Annotate Work Item Dialog", "Annotate this work item: ",
				theNode.getData().getTooltip(), null);

		if(inputDialog.open() == Window.OK){
			newTooltip = inputDialog.getValue();
		}

		//	log.info(newName);
		final String theNewTooltip = newTooltip;

		return new Thread() {
			public void run(){
				try {
					if(theNewTooltip != null)   // do nothing on cancel
						theNode.annotate(theDisplay, theViewer, theNewTooltip);
				} catch (Exception e) {
					log.error("Annotate node error");					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						theViewer.refresh(theNode);
					}
				});
			}
		};
	}

	public void annotate(final Display theDisplay, final TreeViewer theViewer, final String theNewTooltip)
	{
		try {
			AnnotateChildResponseMessage msg = new AnnotateChildResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){

				AnnotateChildType childType = new AnnotateChildType();
				//				childType.setNode(this.getData().getHierarchy());
				childType.setNode("\\\\" + this.getData().getTableCd() +  "\\" + this.getData().getIndex());
				childType.setTooltip(theNewTooltip);
				String response = WorkplaceServiceDriver.annotateChild(childType);

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
					return;
				}			
			}												
			this.getData().setTooltip(theNewTooltip);

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

		} catch (Exception e) {
			log.error(e.getMessage());
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

	public Thread addNode(TreeViewer viewer) {
		final TreeNode theNode = this;
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();

		//	log.info(newName);

		if((theNode.getData().getName().equals("New Folder")) &&
				(theNode.getData().getVisualAttributes().equals("FA"))){
			InputDialog inputDialog = new InputDialog(theDisplay.getActiveShell(), 
					"New Folder Dialog", "Name this folder: ",
					theNode.getData().getName(), null);


			if(inputDialog.open() == Window.OK){
				String newName = inputDialog.getValue();
				this.getData().setName(newName);
				this.getData().setTooltip("FOLDER: " + newName);

			}
		}

		return new Thread() {
			public void run(){
				try {
					theNode.add(theDisplay, theViewer);
				} catch (Exception e) {
					log.error("Add node error");					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						theViewer.refresh(theNode);
					}
				});
			}
		};
	}

	public void add(final Display theDisplay, final TreeViewer theViewer)
	{
		try {
			WorkplaceResponseMessage msg = new WorkplaceResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){

				FolderType childType = new FolderType();
				//			childType.setHierarchy(this.getData().getHierarchy());
				childType.setName(this.getData().getName());
				childType.setGroupId(this.getData().getGroupId());
				//			childType.setHlevel(this.getData().getHlevel());
				childType.setIndex(this.getData().getIndex());
				childType.setParentIndex("\\\\" + this.getData().getTableCd() + "\\" + this.getData().getParentIndex());
				//	childType.setParentIndex(this.getData().getParentIndex());
				childType.setTooltip(this.getData().getTooltip());
				childType.setUserId(this.getData().getUserId());
				childType.setVisualAttributes(this.getData().getVisualAttributes());
				childType.setWorkXml(this.getData().getWorkXml());
				childType.setWorkXmlI2B2Type(this.getData().getWorkXmlI2B2Type());
				childType.setShareId(this.getData().getShareId());
				childType.setWorkXmlSchema(this.getData().getWorkXmlSchema());
				childType.setEntryDate(null);
				childType.setChangeDate(null);
				childType.setStatusCd(null);

				String response = WorkplaceServiceDriver.addChild(childType);

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
					return;
				}			
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

		} catch (Exception e) {
			log.error(e.getMessage());
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
	public void copyChildren(String indexShare) {
		// indexShare contains "index,shareId" of folder we are copying to
		String[] nodeInfo = indexShare.split(",");

		// Get the children of the node we are copying from 	
		List<TreeNode> children = getChildren();
		Iterator i = children.iterator();
		while(i.hasNext()){
			TreeNode childNode = (TreeNode)(i.next());
			childNode.getData().setIndex(new AddChildRequestMessage().generateMessageId());
			childNode.getData().setParentIndex(nodeInfo[0]);
			childNode.getData().setShareId(nodeInfo[1]);
			childNode.addChild().start();
			if(childNode.getData().getVisualAttributes().startsWith("F"))
				// set up folder with placeholder child so display is correct
				if((childNode.getData().getVisualAttributes().equals("FA")) )  
				{
					TreeNode placeholder = new TreeNode("working...", "working...", "LAO");
					childNode.addChild(placeholder);

				}
				else if	((childNode.getData().getVisualAttributes().equals("FH")) )
				{
					TreeNode placeholder = new TreeNode("working...", "working...", "LHO");
					childNode.addChild(placeholder);

				}
			childNode.copyChildren(childNode.getData().getIndex());
		}

	}

	public Thread addChild(){
		final TreeNode theNode = this;
		return new Thread() {
			public void run(){
				try {
					theNode.add();
				} catch (Exception e) {
					log.error("Copy children error");					
				}
			}
		};
	}
	public void add()
	{
		try {
			WorkplaceResponseMessage msg = new WorkplaceResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				FolderType childType = new FolderType();
				childType.setName(this.getData().getName());
				childType.setGroupId(this.getData().getGroupId());
				childType.setIndex(this.getData().getIndex());
				childType.setParentIndex("\\\\" + this.getData().getTableCd() + "\\" + this.getData().getParentIndex());
				childType.setTooltip(this.getData().getTooltip());
				childType.setUserId(this.getData().getUserId());
				childType.setVisualAttributes(this.getData().getVisualAttributes());
				childType.setWorkXml(this.getData().getWorkXml());
				childType.setWorkXmlI2B2Type(this.getData().getWorkXmlI2B2Type());
				childType.setShareId(this.getData().getShareId());
				childType.setWorkXmlSchema(this.getData().getWorkXmlSchema());
				childType.setEntryDate(null);
				childType.setChangeDate(null);
				childType.setStatusCd(null);

				String response = WorkplaceServiceDriver.addChild(childType);

				procStatus = msg.processResult(response);
				//				else if  other error codes
				//				TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
				if (procStatus.getType().equals("ERROR")){		
					log.error(procStatus.getValue());				
					return;
				}			
			}
		} catch (AxisFault e) {
			log.error("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");

		} catch (Exception e) {
			log.error("Error message delivered from the remote server\n" +  
					"You may wish to retry your last action");		
		}
	}

}
