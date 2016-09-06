/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 	     Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.OntologyJAXBUtil;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.StringUtil;

import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetModifiersResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.DeleteChildType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetModifierInfoType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetModifiersType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifierType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifiersType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType;
import edu.harvard.i2b2.ontclient.datavo.vdo.XmlValueType;


import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ModifierComposite //extends Composite
{
	private Log log = LogFactory.getLog(ModifierComposite.class.getName());	
	private Tree tree;
	private TreeViewer viewer;
	private ImageRegistry imageRegistry;
//	private List<String> list = new ArrayList();;
	private Label findLabel;
	private Menu modAllMenu, lockedMenu, modItemMenu, modOnlyMenu;
	
	private static ModifierComposite instance;
	private TreeNode currentNode;
	private TreeNode conceptNode;
//	private TreeData currentData;
	private TreeNode rootNode;
	private Composite modifierComposite;
	private Composite parent;
	
	public static void setInstance(Composite composite) {
		instance = new ModifierComposite(composite);
	}

	/**
	 * Function to return the TableComposite instance
	 * 
	 * @return  TableComposite object
	 */
	public static ModifierComposite getInstance() {
		return instance;
	}
	
	private ModifierComposite(Composite composite)
	{
	//	super(composite, SWT.NONE); 
		parent = composite;
	}
	/*	modifierComposite = new Composite(composite, SWT.NONE);
		
		findLabel = new Label (modifierComposite, SWT.NONE);
		findLabel.setText("Associated modifiers");
		findLabel.setVisible(false);

		tree = new Tree(modifierComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalSpan = 100;
		gridData.horizontalSpan = 2;
		gridData.widthHint = 150;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tree.setLayoutData(gridData);
		
		imageRegistry= new ImageRegistry();
		createImageRegistry();
		createTreeViewer();
	}*/

	private void createImageRegistry()
	{
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierPencil.png");
		this.imageRegistry.put("modifier", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierFolderPencil.png");
		this.imageRegistry.put("modifierFolder", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierContainerPencil.png");
		this.imageRegistry.put("modifierContainer", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierLocked.png");
		this.imageRegistry.put("modifierLocked", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierFolderLocked.png");
		this.imageRegistry.put("modifierFolderLocked", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierContainerLocked.png");
		this.imageRegistry.put("modifierContainerLocked", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/leaf.jpg");
		this.imageRegistry.put("leaf", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/leafLocked.gif");
		this.imageRegistry.put("leafLocked", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/leafPencil.gif");
		this.imageRegistry.put("leafPencil", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/multi.bmp");
		this.imageRegistry.put("multi", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/openFolder.gif");
		this.imageRegistry.put("openFolder", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/openCase.gif");
		this.imageRegistry.put("openCase", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/closedFolderPencil.gif");
		this.imageRegistry.put("closedFolder", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/closedCasePencil.gif");
		this.imageRegistry.put("closedCase", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/closedCaseLocked.gif");
		this.imageRegistry.put("lockedCase", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/closedFolderLocked.gif");
		this.imageRegistry.put("lockedFolder", imageDescriptor);

		//	  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/xyz.jpg");
		//	  this.imageRegistry.put("error", imageDescriptor);
	}

	private void createTreeViewer()
	{	
		MenuManager modItemPopupMenu = createModItemPopupMenu();
		modItemMenu = modItemPopupMenu.createContextMenu(tree);
		modItemMenu.setVisible(false);
		
		MenuManager allPopupMenu = createModAllPopupMenu();
		modAllMenu = allPopupMenu.createContextMenu(tree);
		modAllMenu.setVisible(false);
		
		MenuManager modOnlyPopupMenu = createModOnlyPopupMenu();
		modOnlyMenu = modOnlyPopupMenu.createContextMenu(tree);
		modOnlyMenu.setVisible(false);
		
		IAction lockedAction = new LockedAction();
		MenuManager lockedPopupMenu = new MenuManager();
		lockedPopupMenu.add(lockedAction);
		lockedMenu = lockedPopupMenu.createContextMenu(tree);
		lockedMenu.setVisible(false);
		
		//	  table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		this.viewer = new TreeViewer(tree);  

		this.viewer.setLabelProvider(new LabelProvider() {
			@Override

			public String getText(Object element) 
			{
				// Set the tooltip data
				//  (cant be done in the lookup thread)
				//   maps TreeViewer node to Tree item and sets item.data
				TreeItem item =  (TreeItem) (viewer.testFindItem(element));
				Color defaultColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
				item.setForeground(defaultColor);
				String tooltip = ((TreeNode)element).getData().getTooltip();

				if(    ((TreeNode)element).getData().getModifier() != null ){
					tooltip = ((TreeNode)element).getData().getModifier().getTooltip();
					//	            	if(System.getProperty("shortToolTip").equals("true"))
					//	            		tooltip = ((TreeNode)element).getData().getModifier().getName();
				}
				if ((tooltip == null) || (tooltip.equals("")))
				{
					tooltip = ((TreeNode)element).toString();		
				}
				
				
				if(!(tooltip.contains("\n"))){
					String formattedTooltip = "";
					String[] tooltips=tooltip.split(" ");
					int length = tooltips.length;

					for(int i = 0; i< length; i++){
						formattedTooltip += " " +  tooltips[i]; 
						if(i > 1 && i%10 == 0)
							formattedTooltip += "\n";
					}
					tooltip = formattedTooltip;


				}
	        	
				
				tooltip = " " + tooltip + " ";

				/*	        	if(System.getProperty("showConceptCodes").equals("true")){
	        		if(    ((TreeNode)element).getData().getModifier() != null ){
	        			if ((((TreeNode)element).getData().getModifier().getBasecode() != null) && (!((TreeNode)element).getData().getModifier().getBasecode().equals("null")) ) {
	        				tooltip = tooltip + "(" + ((TreeNode)element).getData().getBasecode() + ")";
	        			}
	        		}
	        		else if ((((TreeNode)element).getData().getBasecode() != null) && (!((TreeNode)element).getData().getBasecode().equals("null")) ) {
	    				tooltip = tooltip + "(" + ((TreeNode)element).getData().getBasecode() + ")";
	    			}
	        	}
				 */			
				item.setData("TOOLTIP", tooltip);        

				// if element is Inactive; print label in gray
				if(    ((TreeNode)element).getData().getModifier() != null ){

					if (((TreeNode)element).getData().getModifier().getVisualattributes().substring(1,2).equals("I"))
					{
						Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
						item.setForeground(color);
					}

					//        	 if element is Hidden; print label in red
					else if (((TreeNode)element).getData().getModifier().getVisualattributes().substring(1,2).equals("H"))
					{
						Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
						item.setForeground(color);
					}

					//       	 if element is undefined; print label in red
					else if (((TreeNode)element).getData().getModifier().getVisualattributes().equals("C-ERROR"))
					{
						Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
						item.setForeground(color);
					}

					//       	 if element is synonym; print label in dark blue
					if (((TreeNode)element).getData().getModifier().getSynonymCd() != null) {
						if (((TreeNode)element).getData().getModifier().getSynonymCd().equals("Y"))
						{
							Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
							item.setForeground(color);
						}
					}	
					return ((TreeNode)element).getData().getModifier().getName();
				}
				
				

	        	else {
	        		if (((TreeNode)element).getData().getVisualattributes().substring(1,2).equals("I")){
	        			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	        			item.setForeground(color);
	        		}
	        	//	 if element is Hidden; print label in red
	             	else if (((TreeNode)element).getData().getVisualattributes().substring(1,2).equals("H"))
	             	{
	             		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	             		item.setForeground(color);
	             	}
	             	
//	            	 if element is undefined; print label in red
	             	else if (((TreeNode)element).getData().getVisualattributes().equals("C-ERROR"))
	             	{
	             		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	             		item.setForeground(color);
	             	}
	        	}
	        	

	        	//       	 if element is synonym; print label in dark blue
	        	if (((TreeNode)element).getData().getSynonymCd() != null) {
	        		if (((TreeNode)element).getData().getSynonymCd().equals("Y"))
	        		{
	        			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
	        			item.setForeground(color);
	        		}
	        	}	
	        	return ((TreeNode)element).toString();
				
				
				
//				return ((TreeNode)element).toString();
			}
			@Override
			public Image getImage(Object element)
			{
				return imageRegistry.get(((TreeNode)element).getIconKey());
			}
		});

		
		this.viewer.setContentProvider(new ITreeContentProvider() {
			public Object[] getChildren(Object parentElement) {
				return ((TreeNode)parentElement).getChildren().toArray();
			}

			public Object getParent(Object element) {
				return ((TreeNode)element).getParent();
			}

			public boolean hasChildren(Object element) {
				return ((TreeNode)element).getChildren().size() > 0;
			}

			public Object[] getElements(Object inputElement) {
				return ((TreeNode)inputElement).getChildren().toArray();
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, 
					Object oldInput, 
					Object newInput) {}
		});
		
		rootNode = new TreeNode(0,"Standard Modifier Items",
				"Standard Modifier Items", "CA");
		this.viewer.setInput(rootNode);


		this.viewer.addTreeListener(new ITreeViewerListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				final TreeNode node = (TreeNode) event.getElement();
				node.setOpen(true);

				// check to see if child is a placeholder ('working...')
				//   if so, make Web Service call to update children of node
				//   leaves that are placeholders have open==true
				if (node.getChildren().size() == 1) {	
					TreeNode child = (TreeNode)(node.getChildren().get(0));
					if((child.getData().getModifier().getVisualattributes().startsWith("R")) && child.isOpen())			{
						// child is a placeholder, so remove from list 
						//   update list with real children  
						node.getModXMLData(viewer, null).start();				
					}
					//				}
				}


				viewer.refresh();
				viewer.expandToLevel(node, 1);
			}
			public void treeCollapsed(TreeExpansionEvent event) {
				final TreeNode node = (TreeNode) event.getElement();
				node.setOpen(false);
				viewer.collapseToLevel(node, 1);
				viewer.refresh();
			}
		});


		this.viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event)
			{	
				TreeNode node = null;
				// if the selection is empty clear the label
				if(event.getSelection().isEmpty()) {
					setCurrentNode(null);
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					node = (TreeNode) selection.getFirstElement();
					setCurrentNode(node);
				}

				// Case where we are expanding the node
				boolean expand = false;
				String visualAttribute = null;
				if(node.getData().getModifier() == null)
					visualAttribute = node.getData().getVisualattributes();
				else
					visualAttribute = node.getData().getModifier().getVisualattributes();
				if((visualAttribute.startsWith("F")) || (visualAttribute.startsWith("D")) || (visualAttribute.startsWith("O")))
					if(node.isOpen()){
						// collapsing node
						node.setOpen(false);
						viewer.collapseToLevel(node, 1);
						viewer.refresh();
					}
					else  // expanding node
					{
						node.setOpen(true);
						viewer.expandToLevel(node, 1);
						viewer.refresh(node);

						// check to see if this node's child is a placeholder ('working...')
						//   if so, make Web Service call to update children of node

						if (node.getChildren().size() == 1)
						{	
							TreeNode child = (TreeNode)(node.getChildren().get(0));
							if((child.getData().getModifier().getVisualattributes().startsWith("R")) && (child.isOpen()))
							{
								// child is a placeholder, so remove from list 
								//   update list with real children  
								node.getModXMLData(viewer, null).start();
							}
						}
						viewer.refresh();
					}
			}

		});

		//	 Implement a "fake" tooltip
		final Listener labelListener = new Listener () {
			public void handleEvent (Event event) {
				Label label = (Label)event.widget;
				Shell shell = label.getShell();
				switch (event.type) {
				case SWT.MouseDown:
					Event e = new Event ();
					e.item = (TreeItem) label.getData ("_TREEITEM");
					//cdh@20060314 have to fix this for multi select in treeview

					// Assuming table is single select, set the selection as if
					// the mouse down event went through to the table
					(viewer.getTree()).setSelection(new TreeItem[] {(TreeItem) e.item}); 
					(viewer.getTree()).notifyListeners(SWT.Selection, e);
					//table.setSelection (new TableItem [] {(TableItem) e.item});
					//table.notifyListeners (SWT.Selection, e);
					// fall through
				case SWT.MouseExit:
					shell.dispose ();
					break;
				}
			}
		};

		Listener viewerListener = new Listener() {
			Shell tip = null;
			Label label = null;

			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Dispose:
				case SWT.KeyDown:
				case SWT.MouseMove: 
				case SWT.MouseExit: {
					if (tip == null)
						break;
					tip.dispose();
					tip = null;
					label = null;
					break;
				}
				case SWT.MouseDown:
					if(event.button == 3) // right click
					{
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
						if (selection.size() != 1)
							return;

						TreeNode node =  (TreeNode) selection.getFirstElement();
						setCurrentNode(node);
						if(node.getData().getModifier() == null){
							if(node.getData().getVisualattributes().substring(2).equals("E")){
								lockedMenu.setVisible(false);
								modAllMenu.setVisible(false);
								modItemMenu.setVisible(false);
								modOnlyMenu.setVisible(true);
							}
						}
						else if(node.getData().getModifier().getVisualattributes().substring(2).equals("E")){							

							if	((node.getData().getModifier().getVisualattributes().startsWith("O"))
									|| (node.getData().getModifier().getVisualattributes().startsWith("D"))){
								lockedMenu.setVisible(false);
								modAllMenu.setVisible(true);
								modItemMenu.setVisible(false);
								modOnlyMenu.setVisible(false);
							}
							else if	((node.getData().getModifier().getVisualattributes().startsWith("R"))){
								modAllMenu.setVisible(false);
								modItemMenu.setVisible(true);
								lockedMenu.setVisible(false);
								modOnlyMenu.setVisible(false);
							}

							else{
								lockedMenu.setVisible(false);
								modAllMenu.setVisible(false);
								modItemMenu.setVisible(false);
								modOnlyMenu.setVisible(false);
							}

						}
						else{
							modAllMenu.setVisible(false);
							modItemMenu.setVisible(false);
							lockedMenu.setVisible(true);
							modOnlyMenu.setVisible(false);
						}
					}


				case SWT.MouseHover: {
					TreeItem item = (viewer.getTree()).getItem(new Point(event.x, event.y));
					if (item != null) {
						if (tip != null && !tip.isDisposed())
							tip.dispose();
						tip = new Shell(Display.getCurrent().getActiveShell(), SWT.ON_TOP | SWT.TOOL);            
						tip.setLayout(new FillLayout());
						label = new Label(tip, SWT.NONE);
						label.setForeground(Display.getCurrent()
								.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
						label.setBackground(Display.getCurrent()
								.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
						label.setData("_TREEITEM", item);
						label.setText((String)item.getData("TOOLTIP"));
						//		            label.setText("Tooltip test");
						label.addListener(SWT.MouseExit, labelListener);
						label.addListener(SWT.MouseDown, labelListener);
						Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
						Rectangle rect = item.getBounds(0);
						Point pt = viewer.getTree().toDisplay(event.x, event.y);
						tip.setBounds(pt.x + 10, pt.y + 20, size.x, size.y);
						tip.setVisible(true);
					}
				}
				}
			}
		};
		viewer.getTree().addListener(SWT.MouseDown, viewerListener);
		viewer.getTree().addListener(SWT.Dispose, viewerListener);
		viewer.getTree().addListener(SWT.KeyDown, viewerListener);
		viewer.getTree().addListener(SWT.MouseMove, viewerListener);
		viewer.getTree().addListener(SWT.MouseHover, viewerListener);	
		viewer.getTree().addListener(SWT.MouseExit, viewerListener);	

		MetadataRecord.getInstance().registerModifierViewer(viewer);
	}
	public void setCurrentNode(TreeNode node)
	{
		this.currentNode = node;
//		this.currentData = node.getData();
	}
	
	public void clearTree(){
		rootNode.getChildren().clear();
		viewer.refresh(rootNode);
	}
	
	public void addModifiers(TreeData node) 
	{
		final TreeViewer theViewer = this.viewer;
		final Display theDisplay = Display.getCurrent();

		String path = StringUtil.getPath(node.getKey());
		String tableCd = StringUtil.getTableCd(node.getKey());
		//rootNode.getChildren().clear();
		clearTree();
		
		TreeNode conceptTerm = new TreeNode(node);
		conceptTerm.setOpen(true);
		rootNode.addChild(conceptTerm);
		conceptNode= conceptTerm;

//		while (path.length()>2){		
			try {
				GetModifiersType modifiersType = new GetModifiersType();

				modifiersType.setSynonyms(false);
				modifiersType.setHiddens(false);
				modifiersType.setMax(null);
				modifiersType.setBlob(true);

				modifiersType.setSelf("\\\\"+tableCd +  path);		
				findLabel.setVisible(true);

				GetModifiersResponseMessage msg = new GetModifiersResponseMessage();
				StatusType procStatus = null;	
				while(procStatus == null || !procStatus.getType().equals("DONE")){
					String response = OntServiceDriver.getModifiers(modifiersType, "ONT");
					response = response.replace("<ValueMetadata>","<ns6:ValueMetadata xmlns:ns6=\"http://www.i2b2.org/xsd/cell/ont/1.1/\">");
					response = response.replace("</ValueMetadata>","</ns6:ValueMetadata>");

					
					procStatus = msg.processResult(response);

					//			TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
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

				ModifiersType allModifiers = msg.doReadModifiers();   	  
				if (allModifiers != null){
					List<ModifierType> modifiers = allModifiers.getModifier();
					//		rootNode.getNodesFromXMLString(modifiers, null);
					if(!modifiers.isEmpty()){	
						Iterator<ModifierType> it = modifiers.iterator();
						while(it.hasNext()){
							ConceptType child = rootNode.getData();
							TreeData data = new TreeData(child);
							ModifierType mod = (ModifierType)it.next();
							 if(System.getProperty("OntEditConceptCode").equals("true"))
							 {
								 if ((mod.getBasecode() != null) && (mod.getBasecode().length() != 0))
									 mod.setTooltip(mod.getTooltip() + " - " + mod.getBasecode());
							 }
							data.setModifier(mod);
							TreeNode childNode = new TreeNode(data);
							//		child.setModifier(data.getModifier());

							if((data.getModifier().getVisualattributes().startsWith("DA")) || (data.getModifier().getVisualattributes().startsWith("OA")))  
							{
								TreeNode placeholder = new TreeNode(child.getLevel() + 1, "working...", "working...", "RA");
								placeholder.setOpen(true);
								ModifierType modifier = new ModifierType();
								modifier.setName("working...");
								modifier.setVisualattributes("RA");
								placeholder.getData().setModifier(modifier);
								childNode.addChild(placeholder);
					//			rootNode.addChild(childNode);
								conceptTerm.addChild(childNode);
							}
							else if	((data.getModifier().getVisualattributes().startsWith("DH")) || (data.getModifier().getVisualattributes().startsWith("OH")))
							{
								TreeNode placeholder = new TreeNode(child.getLevel() + 1, "working...", "working...", "RH");
								placeholder.setOpen(true);
								ModifierType modifier = new ModifierType();
								modifier.setName("working...");
								modifier.setVisualattributes("RA");
								placeholder.getData().setModifier(modifier);
								childNode.addChild(placeholder);
								//rootNode.addChild(childNode);
								conceptTerm.addChild(childNode);
							}
							else if((data.getModifier().getVisualattributes().startsWith("R"))){
							//	rootNode.addChild(childNode);	
								conceptTerm.addChild(childNode);
							}
						}
					}

				}		
				theViewer.expandToLevel(rootNode, 2);
				theViewer.refresh(rootNode);
			

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
	/*		if(path.endsWith("%")){
				path = path.substring(0, path.length()-2);
	//			log.debug("INTERMED modifier path is " + path);
				path = path.substring(0, path.lastIndexOf("\\") + 1) + "%";
	//			log.debug("NEW modifier path is " + path);
			}
			else
				path = path + "%";
		}
		*/
	}
	
	public void addChildModifier(ModifierType modifier){
//		TreeNode child = new TreeNode(modifier.getLevel(), StringUtil.getPath(modifier.getKey()),modifier.getName(),modifier.getVisualattributes());
//		child.getData().setModifier(modifier);
//		currentNode.addChild(child);
		currentNode.updateModifierChildren(Display.getCurrent(), viewer);
	    viewer.refresh();
	    this.viewer.expandToLevel(currentNode,1);

	}
	 

	public void refresh()
	{
		this.viewer.refresh();
	}

	public void refreshNode()
	{
		currentNode.getData().getModifier().setName(MetadataRecord.getInstance().getMetadata().getModifier().getName());
		this.viewer.refresh(currentNode);
	}
	
/*	public List getList(){
		return list;
	}
	*/
	private MenuManager createModItemPopupMenu() {
		MenuManager modItemPopupMenu = new MenuManager();
		modItemPopupMenu.add(new DeleteAction());
		modItemPopupMenu.add(new RefreshAction());
		modItemPopupMenu.add(new EditAction());
		return modItemPopupMenu;

	}
	private MenuManager createModAllPopupMenu(){

		MenuManager modifierPopupMenu2= new MenuManager("Modifier");
		modifierPopupMenu2.add(new ModifierFolderAction());
		modifierPopupMenu2.add(new ModifierItemAction());
//		modifierPopupMenu2.add(new ModifierContainerAction());

		MenuManager newMenu2 = new MenuManager("New");
		newMenu2.add(modifierPopupMenu2);

		MenuManager allPopupMenu = new MenuManager();
		allPopupMenu.add(newMenu2);
		allPopupMenu.add(new DeleteAction());
		allPopupMenu.add(new RefreshAction());
		allPopupMenu.add(new EditAction());

		return allPopupMenu;

	}
	private MenuManager createModOnlyPopupMenu(){

		MenuManager modifierPopupMenu2= new MenuManager("Modifier");
		modifierPopupMenu2.add(new ModifierFolderAction());
		modifierPopupMenu2.add(new ModifierItemAction());
		modifierPopupMenu2.add(new ModifierContainerAction());

		MenuManager newMenu2 = new MenuManager("New");
		newMenu2.add(modifierPopupMenu2);

		MenuManager modOnlyPopupMenu = new MenuManager();
		modOnlyPopupMenu.add(newMenu2);
		modOnlyPopupMenu.add(new RefreshAction());

		return modOnlyPopupMenu;

	}
	
	
	private class ModifierFolderAction extends Action 
	{
		public ModifierFolderAction()
		{
			super("ModifierFolder");
		}
		@Override
		public void run()
		{
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			TreeNode node = (TreeNode) selection.getFirstElement();
//			setCurrentNode(node);
			MetadataRecord.getInstance().clear();
			MetadataRecord.getInstance().setType("ModifierFolder");
	//		MetadataRecord.getInstance().setMetadata(node);
			if(node.getData().getModifier() == null){
				MetadataRecord.getInstance().setMetadata(node);
				MetadataRecord.getInstance().setParentData(node.getData());
			//	node.getData().setFacttablecolumn("modifier_cd");
			//	node.getData().setColumnname("modifier_path");
			//	node.getData().setTablename("modifier_dimension");
				MetadataRecord.getInstance().getParentData().setFacttablecolumn("modifier_cd");
				MetadataRecord.getInstance().getParentData().setColumnname("modifier_path");
				MetadataRecord.getInstance().getParentData().setTablename("modifier_dimension");

			}
			else{
				MetadataRecord.getInstance().setParentData(node.getData().getModifier());
				MetadataRecord.getInstance().setMetadata(node.getData().getModifier());
			}
			ValueMetadata.getInstance().clear();

			ModifierFolderWizard wizard = new ModifierFolderWizard();

			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.setPageSize(350, 350);
			dialog.setHelpAvailable(true);
			dialog.create();
			dialog.open();

			wizard.dispose();		
		}
	}

	private class ModifierItemAction extends Action 
	{
		public ModifierItemAction()
		{
			super("ModifierItem");
		}
		@Override
		public void run()
		{
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			TreeNode node = (TreeNode) selection.getFirstElement();
	//		setCurrentNode(node);
			MetadataRecord.getInstance().clear();
			MetadataRecord.getInstance().setType("ModifierItem");
			if(node.getData().getModifier() == null){
				MetadataRecord.getInstance().setMetadata(node);
				MetadataRecord.getInstance().setParentData(node.getData());
			//	node.getData().setFacttablecolumn("modifier_cd");
			//	node.getData().setColumnname("modifier_path");
			//	node.getData().setTablename("modifier_dimension");
				MetadataRecord.getInstance().getParentData().setFacttablecolumn("modifier_cd");
				MetadataRecord.getInstance().getParentData().setColumnname("modifier_path");
				MetadataRecord.getInstance().getParentData().setTablename("modifier_dimension");

			}
			else{
				MetadataRecord.getInstance().setParentData(node.getData().getModifier());
				MetadataRecord.getInstance().setMetadata(node.getData().getModifier());
			}
			ValueMetadata.getInstance().clear();

			ModifierItemWizard wizard = new ModifierItemWizard();

			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.setPageSize(350, 350);
			dialog.setHelpAvailable(true);
			dialog.create();
			dialog.open();


			wizard.dispose();	           
		}
	}
	private class	ModifierContainerAction extends Action 
	{
		public ModifierContainerAction ()
		{
			super("ModifierContainer");
		}
		@Override
		public void run()
		{
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			TreeNode node = (TreeNode) selection.getFirstElement();
			setCurrentNode(node);
			MetadataRecord.getInstance().clear();
			MetadataRecord.getInstance().setType("ModifierContainer");

			if(node.getData().getModifier() == null){
				MetadataRecord.getInstance().setMetadata(node);
				MetadataRecord.getInstance().setParentData(node.getData());
			//	node.getData().setFacttablecolumn("modifier_cd");
			//	node.getData().setColumnname("modifier_path");
			//	node.getData().setTablename("modifier_dimension");
				MetadataRecord.getInstance().getParentData().setFacttablecolumn("modifier_cd");
				MetadataRecord.getInstance().getParentData().setColumnname("modifier_path");
				MetadataRecord.getInstance().getParentData().setTablename("modifier_dimension");

			}
			else{
				MetadataRecord.getInstance().setParentData(node.getData().getModifier());
				MetadataRecord.getInstance().setMetadata(node.getData().getModifier());
			}
			ValueMetadata.getInstance().clear();
			
			ModifierContainerWizard wizard = new ModifierContainerWizard();

			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.setPageSize(350, 350);
			dialog.setHelpAvailable(true);
			dialog.create();
			dialog.open();

			wizard.dispose();

		}
	}
	private class LockedAction extends Action 
	{
		public LockedAction()
		{
			super("This ontology is locked.");

		}
	}
	
	
	private class DeleteAction extends Action 
	{
		public DeleteAction()
		{
			super("Delete");
			System.setProperty("IncludeChildren" , "false");

		}
		@Override
		public void run()
		{  
			if (!(Roles.getInstance().isRoleValid())){
				MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING|SWT.OK);
				mBox.setText("Delete Modifier Warning");
				mBox.setMessage(Messages.getString("EditView.MinRoleNeeded2"));

				int result = mBox.open();

				return;
			}

			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			TreeNode node = (TreeNode) selection.getFirstElement();
			String visAttribute = node.getData().getModifier().getVisualattributes();
			if(visAttribute.startsWith("R")){
				MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_QUESTION|SWT.YES|SWT.NO);
				mBox.setText("Delete Modifier Confirmation");
				
				if(node.getData().getModifier().getAppliedPath().length()+ 1 < StringUtil.getPath(conceptNode.getData().getFullName()).length()){
				
					mBox.setMessage("This modifier applies to concept: " + node.getData().getModifier().getAppliedPath() 
						+ "\n Deleting it will cause it to be excluded from this concept and its children \n\n" +
						"Are you sure you want to exclude this modifier?");
				}
				else{
					mBox.setMessage("Are you sure you want to delete this modifier?");
				}
				int result = mBox.open();
				if(result == SWT.NO)
					return;
			}
			// ask if we are deleting children also
			else{
				DeleteModifierDialog dlg = new DeleteModifierDialog(Display.getCurrent().getActiveShell(), node, conceptNode);
				dlg.open();
				if (Boolean.valueOf(System.getProperty("cancel")) == true)
					return;
			}

			if(node.getData().getModifier().getAppliedPath().length()+ 1 < StringUtil.getPath(conceptNode.getData().getFullName()).length()){
				if((node.getData().getModifier().getSourcesystemCd() == null) || (node.getData().getModifier().getSourcesystemCd().length()==0))
					node.getData().getModifier().setSourcesystemCd(UserInfoBean.getInstance().getUserName() + "_manualentry");
				if(node.getData().getModifier().getComment() == null)
					node.getData().getModifier().setComment("");
						
				excludeChild(node).start();
			}
	//		setCurrentNode(node);

			// Nodes that are "deleted" have visAttrib == Hidden.
			// not true anymore jun 1, 2010
			//		String hiddenAttribute = visAttribute.charAt(0)+ "H" + visAttribute.charAt(2);
			//		node.getData().setVisualattributes(hiddenAttribute);
			//			System.out.println(hiddenAttribute);
			else {
				deleteChild(node).start();
			}

		}

		public Thread deleteChild(TreeNode node){
			final Display theDisplay = Display.getCurrent();
			final boolean includeChildren = Boolean.valueOf(System.getProperty("IncludeChildren"));
			final TreeNode theNode = node;
			return new Thread() {
				@Override
				public void run(){
					try {
						delete(theDisplay, theNode, includeChildren);
					} catch (Exception e) {
						log.error("Delete term error");					
					}
					theDisplay.syncExec(new Runnable() {
						public void run() {
					//		((TreeNode)theNode.getParent()).getChildren().clear();
							((TreeNode)theNode.getParent()).getChildren().remove(theNode);
					//		((TreeNode)theNode.getParent()).getXMLData(viewer, null).start();
							viewer.refresh();
							viewer.expandToLevel(theNode.getParent(), 1);
							MetadataRecord.getInstance().getSyncAction()
							.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/red_database_refresh.png"));
						}
					});
				}
			};
		}
		public void delete(final Display theDisplay, final TreeNode theNode, final boolean includeChildren)
		{
			try {
				OntologyResponseMessage msg = new OntologyResponseMessage();
				StatusType procStatus = null;	
				while(procStatus == null || !procStatus.getType().equals("DONE")){

				DeleteChildType delChild = theNode.getDeleteChildType();
					// automatically include children on deletes from folders, containers.
					//					delChild.setIncludeChildren(!(delChild.getVisualattribute().startsWith("L")));
					delChild.setIncludeChildren(includeChildren);
					String response = OntServiceDriver.deleteChild(delChild);

					procStatus = msg.processResult(response);
					//					else if  other error codes
					//					TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
					if (procStatus.getType().equals("ERROR")){		
						theDisplay.syncExec(new Runnable() {
							public void run() {
								// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
								MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("Unable to make a connection to the remote server\n" +  
								"This is often a network error, please try again");
								int result = mBox.open();
							}
						});
						log.error(procStatus.getValue());				
						return;
					}			
				}
			} catch (AxisFault e) {
				log.error("Unable to make a connection to the remote server\n" +  
				"This is often a network error, please try again");
				theDisplay.syncExec(new Runnable() {
					public void run() {
						// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
						MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Unable to make a connection to the remote server\n" +  
						"This is often a network error, please try again");
						int result = mBox.open();
					}
				});
			} catch (Exception e) {
				log.error("Error message delivered from the remote server\n" +  
				"You may wish to retry your last action");		
				theDisplay.syncExec(new Runnable() {
					public void run() {
						// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
						MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Unable to make a connection to the remote server\n" +  
						"This is often a network error, please try again");
						int result = mBox.open();
					}
				});
			}
		}
		public Thread excludeChild(TreeNode node){
			final Display theDisplay = Display.getCurrent();
//			final boolean includeChildren = 
			if(Boolean.valueOf(System.getProperty("IncludeChildren")))
				node.getData().getModifier().setAppliedPath(StringUtil.getPath(conceptNode.getData().getFullName())+ "%");

			else
				node.getData().getModifier().setAppliedPath(StringUtil.getPath(conceptNode.getData().getFullName()));

				
			final TreeNode theNode = node;
			return new Thread() {
				@Override
				public void run(){
					try {
						exclude(theDisplay, theNode);
					} catch (Exception e) {
						log.error("Delete term error");					
					}
					theDisplay.syncExec(new Runnable() {
						public void run() {
					//		((TreeNode)theNode.getParent()).getChildren().clear();
							((TreeNode)theNode.getParent()).getChildren().remove(theNode);
					//		((TreeNode)theNode.getParent()).getXMLData(viewer, null).start();
							viewer.refresh();
							viewer.expandToLevel(theNode.getParent(), 1);
					//		MetadataRecord.getInstance().getSyncAction()
					//		.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/red_database_refresh.png"));
						}
					});
				}
			};
		}
		public void exclude(final Display theDisplay, final TreeNode theNode)
		{
			try {
				OntologyResponseMessage msg = new OntologyResponseMessage();
				StatusType procStatus = null;	
				while(procStatus == null || !procStatus.getType().equals("DONE")){

		//		ModifierType dchild = theNode.getData().getModifier();
					// automatically include children on deletes from folders, containers.
					//					delChild.setIncludeChildren(!(delChild.getVisualattribute().startsWith("L")));
		//			delChild.setIncludeChildren(includeChildren);
					String response = OntServiceDriver.excludeModifier(theNode.getData().getModifier());

					procStatus = msg.processResult(response);
					//					else if  other error codes
					//					TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
					if (procStatus.getType().equals("ERROR")){		
						theDisplay.syncExec(new Runnable() {
							public void run() {
								// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
								MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("Unable to make a connection to the remote server\n" +  
								"This is often a network error, please try again");
								int result = mBox.open();
							}
						});
						log.error(procStatus.getValue());				
						return;
					}			
				}
				
			} catch (AxisFault e) {
				log.error("Unable to make a connection to the remote server\n" +  
				"This is often a network error, please try again");
				theDisplay.syncExec(new Runnable() {
					public void run() {
						// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
						MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Unable to make a connection to the remote server\n" +  
						"This is often a network error, please try again");
						int result = mBox.open();
					}
				});
			} catch (Exception e) {
				log.error("Error message delivered from the remote server\n" +  
				"You may wish to retry your last action");		
				theDisplay.syncExec(new Runnable() {
					public void run() {
						// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
						MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
						mBox.setText("Please Note ...");
						mBox.setMessage("Unable to make a connection to the remote server\n" +  
						"This is often a network error, please try again");
						int result = mBox.open();
					}
				});
			}
		}
	
		
	}


	private class RefreshAction extends Action 
	{
		public RefreshAction()
		{
			super("Refresh");

		}
		@Override
		public void run()
		{
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			TreeNode node = (TreeNode) selection.getFirstElement();
//			setCurrentNode(node);
			if(node.getData().getModifier() == null)
				node.getModXMLData(viewer, null).start();
			else if(!(node.getData().getModifier().getVisualattributes().startsWith("R")))
				node.getModXMLData(viewer, null).start();
			viewer.refresh(node);
		}
	}

	private class EditAction extends Action 
	{
		public EditAction()
		{
			super("Edit");
		}
		@Override
		public void run()
		{
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			TreeNode node = (TreeNode) selection.getFirstElement();
			setCurrentNode(node);

			MetadataRecord.getInstance().clear();

			MetadataRecord.getInstance().registerModifierViewer(viewer);

			MetadataRecord.getInstance().setParentData(node.getData().getModifier());
			MetadataRecord.getInstance().setMetadata(node.getData().getModifier());

			// populate synonyms list
			//		MetadataRecord.getInstance().getSynonyms().clear();  // start fresh with empty synonym list
			//		MetadataRecord.getInstance().setSynonymEditFlag(false);
			synonyms(Display.getCurrent());

			if(node.getData().getModifier().getVisualattributes().startsWith("O")){
				MetadataRecord.getInstance().setType("ModifierContainer");
			}
			else if(node.getData().getModifier().getVisualattributes().startsWith("D")){
				MetadataRecord.getInstance().setType("ModifierFolder");
			}
			else if(node.getData().getModifier().getVisualattributes().startsWith("R")){
				MetadataRecord.getInstance().setType("Modifier");
			}

			ValueMetadata.getInstance().clear();
			ValueMetadataType vmType = null;
			XmlValueType xml = node.getData().getModifier().getMetadataxml();
			if (xml != null){
				try {
					org.w3c.dom.Element xmlElement =  xml.getAny().get(0);  

					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					org.w3c.dom.Document doc1 = builder.newDocument();
					doc1.appendChild( doc1.importNode(xmlElement,true)); 

					JAXBElement jaxbElement = OntologyJAXBUtil.getJAXBUtil().unMashallFromDocument(doc1);
					vmType = (ValueMetadataType) jaxbElement.getValue();
				} catch (JAXBUtilException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ValueMetadata.getInstance().setValueMetadataType(vmType);

			//			ValueMetadata.getInstance().updateFlags();

			EditModifierWizard wizard = new EditModifierWizard();

			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.setPageSize(350, 350);
			dialog.setHelpAvailable(true);
			dialog.create();
			dialog.open();


			wizard.dispose();	           
		}

	}

	public Thread getSynonyms(){
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			@Override
			public void run(){
				try {
					synonyms(theDisplay);
				} catch (Exception e) {
					log.error("Get synonyms error");					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						;
					}
				});
			}
		};
	}
	public void synonyms(final Display theDisplay)
	{
		try {
			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){

				GetModifierInfoType modInfo = new GetModifierInfoType();
				modInfo.setSelf(MetadataRecord.getInstance().getMetadata().getModifier().getKey());
				modInfo.setAppliedPath(MetadataRecord.getInstance().getMetadata().getModifier().getAppliedPath());
				modInfo.setBlob(false);
				modInfo.setSynonyms(true);
				modInfo.setHiddens(false);
				modInfo.setType("default");
				String response = OntServiceDriver.getModifierInfo(modInfo, "EDIT");

				procStatus = msg.processResult(response);
				//			else if  other error codes
				//			TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
				if (procStatus.getType().equals("ERROR")){		
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
					log.error(procStatus.getValue());				
					//					return false;
				}			
				//  check response
				ModifiersType modifiers = msg.doReadModifiers();
				Iterator<ModifierType> it = modifiers.getModifier().iterator();
				while(it.hasNext()){
					ModifierType modifier = it.next();
					if(modifier.getSynonymCd().equals("Y")){
						MetadataRecord.getInstance().getSynonyms().add(modifier.getName());
					}
				}


			}		
		} catch (Exception e) {
			log.error("Error message delivered from the remote server\n" +  
			"You may wish to retry your last action");		
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});
		}
	}
	public void disableComposite(){
		if ((modifierComposite != null) && (!modifierComposite.isDisposed())) {
			modifierComposite.dispose();
			parent.layout(true);
		}
	}
	
	public void enableComposite(TreeNode concept){
		if ((modifierComposite != null) && (!modifierComposite.isDisposed())) {
			modifierComposite.redraw();
		}
		else{
			modifierComposite = new Composite(parent, SWT.NONE);
			
			modifierComposite.setLayoutData(new GridData (GridData.FILL_BOTH));
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 1;
			gridLayout.horizontalSpacing = 1;
			gridLayout.verticalSpacing = 1;
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 0;
			modifierComposite.setLayout(gridLayout);
			
			findLabel = new Label (modifierComposite, SWT.NONE);
			findLabel.setText("Associated modifiers");
			findLabel.setVisible(false);

			tree = new Tree(modifierComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.verticalSpan = 100;
			gridData.horizontalSpan = 2;
			gridData.widthHint = 150;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			tree.setLayoutData(gridData);
			
			imageRegistry= new ImageRegistry();
			createImageRegistry();
			createTreeViewer();
			parent.layout(true);
		}
	}
	
	public TreeNode getConceptNode(){
		return conceptNode;
	}
}






