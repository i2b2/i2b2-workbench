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

import java.util.Iterator;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.resource.*;
import org.eclipse.jface.window.*;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.graphics.*;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.OntologyJAXBUtil;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.ontclient.datavo.vdo.DeleteChildType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetTermInfoType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType;
import edu.harvard.i2b2.ontclient.datavo.vdo.XmlValueType;

public class NodeBrowser extends ApplicationWindow
{
	private Log log = LogFactory.getLog(NodeBrowser.class.getName());	
	private NodeBrowser browser;
	private TreeViewer viewer;
	private TreeData currentData;
	private TreeNode currentNode;
	public TreeNode rootNode;       //unfortunately I dont have a way
	// to get the rootNode of a tree.....
	private ImageRegistry imageRegistry;
	private StatusLineManager slm;
	private Menu allMenu, itemMenu, rootMenu, lockedMenu, modItemMenu, viewMenu;


	public NodeBrowser(Composite parent, int inputFlag, StatusLineManager slm)
	{
		super(null);
		this.slm = slm;
		imageRegistry= new ImageRegistry();
		createImageRegistry();

		createTreeViewer(parent, SWT.MULTI | SWT.BORDER, inputFlag);
		//   Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		//  	this.viewer.addDragSupport(DND.DROP_COPY, types, new NodeDragListener(this.viewer));      
	}

	private void createImageRegistry()
	{
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/leaf.jpg");
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

	private void createTreeViewer(Composite parent, int style, int inputFlag)
	{
		this.browser = this;
		Tree tree = new Tree(parent, style);  

		GridData gridData = new GridData(GridData.FILL_BOTH);
//		gridData.verticalSpan = 50;
		gridData.horizontalSpan = 2;
		gridData.widthHint = 150;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tree.setLayoutData(gridData);

		// create popup menus
		

		MenuManager modItemPopupMenu = createModItemPopupMenu();
		modItemMenu = modItemPopupMenu.createContextMenu(tree);
		modItemMenu.setVisible(false);

		MenuManager itemPopupMenu = createItemPopupMenu();
		itemMenu = itemPopupMenu.createContextMenu(tree);
		itemMenu.setVisible(false);


		MenuManager rootPopupMenu = createRootPopupMenu();
		rootMenu = rootPopupMenu.createContextMenu(tree);
		rootMenu.setVisible(false);

		MenuManager allPopupMenu = createAllPopupMenu();
		allMenu = allPopupMenu.createContextMenu(tree);
		allMenu.setVisible(false);

		IAction lockedAction = new LockedAction();
		MenuManager lockedPopupMenu = new MenuManager();
		lockedPopupMenu.add(lockedAction);
		lockedMenu = lockedPopupMenu.createContextMenu(tree);
		lockedMenu.setVisible(false);

		MenuManager viewPopupMenu = createViewPopupMenu();
		viewMenu = viewPopupMenu.createContextMenu(tree);
		viewMenu.setVisible(false);
		

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


				if ((tooltip == null) || (tooltip.equals("")))
				{
					tooltip = ((TreeNode)element).toString();		
				}
				
				else {
	           		if(tooltip.contains("\n"))
	        			;
	        		else{
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
	        	}
	        	
				tooltip = " " + tooltip + " ";

				item.setData("TOOLTIP", tooltip);        

				// if element is Inactive; print label in gray
				if (((TreeNode)element).getData().getVisualattributes().substring(1,2).equals("I"))
				{
					Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
					item.setForeground(color);
				}

				//        	 if element is Hidden; print label in red
				else if (((TreeNode)element).getData().getVisualattributes().substring(1,2).equals("H"))
				{
					Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
					item.setForeground(color);
				}

				//       	 if element is undefined; print label in red
				else if (((TreeNode)element).getData().getVisualattributes().equals("C-ERROR"))
				{
					Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
					item.setForeground(color);
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

		this.viewer.setInput(populateRootNode());

		String status = System.getProperty("errorMessage");
		if (status != null){
			TreeNode placeholder = new TreeNode(1, "placeholder",status, "C-ERROR");
			browser.rootNode.addChild(placeholder);
			browser.refresh();

		}

		this.viewer.addTreeListener(new ITreeViewerListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				final TreeNode node = (TreeNode) event.getElement();
				node.setOpen(true);

				// check to see if child is a placeholder ('working...')
				//   if so, make Web Service call to update children of node
				//   leaves that are placeholders have open==true
				if (node.getChildren().size() == 1) {	
					TreeNode child = (TreeNode)(node.getChildren().get(0));
					if((child.getData().getVisualattributes().startsWith("L")) && child.isOpen())			{
						// child is a placeholder, so remove from list 
						//   update list with real children  
						node.getXMLData(viewer, browser).start();				
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
				String visualAttribute = node.getData().getVisualattributes();
				if((visualAttribute.startsWith("F")) || (visualAttribute.startsWith("C")))
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
							if((child.getData().getVisualattributes().startsWith("L")) && (child.isOpen()))
							{
								// child is a placeholder, so remove from list 
								//   update list with real children  
								node.getXMLData(viewer, browser).start();
							}
						}
						viewer.refresh();
					}
			}

		});
		this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				
				TreeNode node = null;
		   	    // if the selection is empty clear the label
	 	       if(event.getSelection().isEmpty()) {
	 	 //          setCurrentNode(null);
	 	           return;
	 	       }
	 	       if(event.getSelection() instanceof IStructuredSelection) {
	 	           IStructuredSelection selection = (IStructuredSelection)event.getSelection();
	 	           node = (TreeNode) selection.getFirstElement();
	 	           if(!Boolean.parseBoolean(System.getProperty("OntDisableModifiers"))){
	 	        	  ModifierComposite.getInstance().enableComposite(node);
	 	        	   if(node.getData().getVisualattributes().endsWith("E"))
	 	        		   ModifierComposite.getInstance().addModifiers(node.getData());
	 	        	   else
	 	        		   ModifierComposite.getInstance().clearTree();
	 	           }else{
	 	        	   ModifierComposite.getInstance().disableComposite();
	 	           }
	 	           setCurrentNode(node);
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
						if(node.getData().getVisualattributes().substring(2).equals("E")){							
							if(node.getData().getLevel() == 0){
								lockedMenu.setVisible(false);
								itemMenu.setVisible(false);
								allMenu.setVisible(false);
								rootMenu.setVisible(true);
								modItemMenu.setVisible(false);
								viewMenu.setVisible(false);

							}
							else if( (node.getData().getVisualattributes().startsWith("F"))|| (node.getData().getVisualattributes().startsWith("C"))){
								lockedMenu.setVisible(false);
								rootMenu.setVisible(false);
								itemMenu.setVisible(false);
								allMenu.setVisible(true);
								modItemMenu.setVisible(false);
								viewMenu.setVisible(false);

							}
							else if	((node.getData().getVisualattributes().startsWith("L"))|| (node.getData().getVisualattributes().startsWith("O"))
									|| (node.getData().getVisualattributes().startsWith("D"))){
								lockedMenu.setVisible(false);
								rootMenu.setVisible(false);
								allMenu.setVisible(false);
								itemMenu.setVisible(true);
								modItemMenu.setVisible(false);
								viewMenu.setVisible(false);

							}
							else if	((node.getData().getVisualattributes().startsWith("R"))){
								rootMenu.setVisible(false);
								allMenu.setVisible(false);
								itemMenu.setVisible(false);
								modItemMenu.setVisible(true);
								lockedMenu.setVisible(false);
								viewMenu.setVisible(false);
							}
						
							else{
								lockedMenu.setVisible(false);
								rootMenu.setVisible(false);
								allMenu.setVisible(false);
								itemMenu.setVisible(false);
								modItemMenu.setVisible(false);
								viewMenu.setVisible(false);
							}

						}
						else if((System.getProperty("OntEdit_ViewOnly") != null) && (System.getProperty("OntEdit_ViewOnly").equals("true"))){

							//			else if(UserInfoBean.getInstance().getCellDataParam("ont", "OntEditView") != null){
							//				if(UserInfoBean.getInstance().getCellDataParam("ont", "OntEditView").equals("true"));
							viewMenu.setVisible(true);
							lockedMenu.setVisible(false);
							rootMenu.setVisible(false);
							allMenu.setVisible(false);
							itemMenu.setVisible(false);
							modItemMenu.setVisible(false);
						}

						else{
							rootMenu.setVisible(false);
							allMenu.setVisible(false);
							itemMenu.setVisible(false);
							modItemMenu.setVisible(false);
							viewMenu.setVisible(false);
							lockedMenu.setVisible(true);
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
	}

	public void setCurrentNode(TreeNode node)
	{
		this.currentNode = node;
		this.currentData = node.getData();
	}

	public TreeNode getCurrentNode()
	{
		return this.currentNode;
	}

	public void addNodes(TreeData data)
	{
		this.currentData = data;
		TreeNode child = new TreeNode(data);
		this.viewer.setInput(child);
	}


	public TreeData getSelectedNode()
	{
		return this.currentData;
	}

	public void refresh()
	{
		this.viewer.refresh(this.rootNode);
	}

	public void flush()
	{
		this.rootNode.getChildren().clear();
	}

	private TreeNode populateRootNode()
	{	  	  
		TreeNode root = new TreeNode(0,"Standard Query Items",
				"Standard Query Items", "CA");

		//make call to getCategories to get list of root nodes
		root.getCategories(this.viewer, this.browser );

		this.rootNode = root;
		return root;
	}

	public void update()
	{
		getCurrentNode().getXMLData(viewer, browser).start();
		viewer.refresh();
		viewer.expandToLevel(getCurrentNode(), 1);
	}

	/*
	 * Goes through all 1st level children, find the one with the matching name, set it as current node, and finally refreshes it.
	 *  name is to be in the form of "\\Custom Metadata\\Some Node Name\\" 
	 */
	public void updateNode( String nodePath )
	{
		String [] pathParts = nodePath.split("\\\\");
		TreeNode parent = this.rootNode;
		boolean nodeFound = false;
		for ( int i = 0; i < pathParts.length; i++ )
		{
			String part = pathParts[i];
			if ( part.isEmpty() )
				continue; // skip empty parts
			for ( TreeNode node : parent.getChildren() )
			{
				if ( node.getData().getName().equals( part ))
				{
					this.currentNode = node;
					nodeFound = true;
					break;
				}
			}
			if (nodeFound)
				break;
		}
		if ( nodeFound )
			update();
	}

	public void parentUpdate()
	{
		((TreeNode)getCurrentNode().getParent()).getXMLData(viewer, browser).start();
		viewer.refresh();
		viewer.expandToLevel((TreeNode)getCurrentNode().getParent(), 1);
	}
	
	
	

	private class NewFolderAction extends Action 
	{
		public NewFolderAction()
		{
			super("Folder");
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
			MetadataRecord.getInstance().setType("Folder");
			MetadataRecord.getInstance().setMetadata(node);
			MetadataRecord.getInstance().registerBrowser(browser);
			ValueMetadata.getInstance().clear();

			FolderWizard wizard = new FolderWizard();

			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.setPageSize(350, 350);
			dialog.setHelpAvailable(true);
			dialog.create();
			dialog.open();

			wizard.dispose();		
		}
	}

	private class NewItemAction extends Action 
	{
		public NewItemAction()
		{
			super("Item");
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
			MetadataRecord.getInstance().setType("Item");
			MetadataRecord.getInstance().setMetadata(node);
			MetadataRecord.getInstance().registerBrowser(browser);
			ValueMetadata.getInstance().clear();

			ItemWizard wizard = new ItemWizard();

			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.setPageSize(350, 350);
			dialog.setHelpAvailable(true);
			dialog.create();
			dialog.open();


			wizard.dispose();	           
		}

	}


	private class NewContainerAction extends Action 
	{
		public NewContainerAction()
		{
			super("Container");
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
			MetadataRecord.getInstance().setType("Container");
			MetadataRecord.getInstance().setMetadata(node);
			MetadataRecord.getInstance().registerBrowser(browser);
			ValueMetadata.getInstance().clear();

			ContainerWizard wizard = new ContainerWizard();

			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.setPageSize(350, 350);
			dialog.setHelpAvailable(true);
			dialog.create();
			dialog.open();

			wizard.dispose();

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
				mBox.setText("Delete Term Warning");
				mBox.setMessage(Messages.getString("EditView.MinRoleNeeded2"));

				int result = mBox.open();

				return;
			}

			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			TreeNode node = (TreeNode) selection.getFirstElement();
			String visAttribute = node.getData().getVisualattributes();
			if(visAttribute.startsWith("L")){
				MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_QUESTION|SWT.YES|SWT.NO);
				mBox.setText("Delete Term Confirmation");
				mBox.setMessage("Are you sure you want to delete this term?");

				int result = mBox.open();
				if(result == SWT.NO)
					return;
			}
			// ask if we are deleting children also
			else{
				DeleteChildrenDialog dlg = new DeleteChildrenDialog(Display.getCurrent().getActiveShell());
				dlg.open();
				if (Boolean.valueOf(System.getProperty("cancel")) == true)
					return;
			}
			MetadataRecord.getInstance().registerBrowser(browser);
			setCurrentNode(node);

			// Nodes that are "deleted" have visAttrib == Hidden.
			// not true anymore jun 1, 2010
			//		String hiddenAttribute = visAttribute.charAt(0)+ "H" + visAttribute.charAt(2);
			//		node.getData().setVisualattributes(hiddenAttribute);
			//			System.out.println(hiddenAttribute);
			deleteChild(node).start();

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
							MetadataRecord.getInstance().getBrowser().parentUpdate();
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
	}


	private class LockedAction extends Action 
	{
		public LockedAction()
		{
			super("This ontology is locked.");

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
			setCurrentNode(node);

			node.getXMLData(viewer, browser).start();
			viewer.refresh();

		}
	}

	/*	private class SynchronizeAction extends Action 
	{
		public SynchronizeAction()
		{
			super("Synchronize concepts");

		}
		@Override
		public void run()
		{  

			UpdateOntologyDialog dialog = new UpdateOntologyDialog(Display.getCurrent().getActiveShell());
			dialog.open(); 	

		}

	}
	 */
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
			MetadataRecord.getInstance().setMetadata(node);
			MetadataRecord.getInstance().registerBrowser(browser);



			// populate synonyms list
			//		MetadataRecord.getInstance().getSynonyms().clear();  // start fresh with empty synonym list
			//		MetadataRecord.getInstance().setSynonymEditFlag(false);
			synonyms(Display.getCurrent());

			if(node.getData().getVisualattributes().startsWith("C")){
				MetadataRecord.getInstance().setType("Container");
			}
			else if(node.getData().getVisualattributes().startsWith("F")){
				MetadataRecord.getInstance().setType("Folder");
			}
			else if(node.getData().getVisualattributes().startsWith("I")){
				MetadataRecord.getInstance().setType("Item");
			}

			ValueMetadata.getInstance().clear();
			ValueMetadataType vmType = null;
			XmlValueType xml = node.getData().getMetadataxml();
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

			EditWizard wizard = new EditWizard();

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

				GetTermInfoType termInfo = new GetTermInfoType();
				termInfo.setSelf(MetadataRecord.getInstance().getMetadata().getKey());
				termInfo.setBlob(false);
				termInfo.setSynonyms(true);
				termInfo.setHiddens(false);
				termInfo.setType("default");
				String response = OntServiceDriver.getTermInfo(termInfo, "EDIT");

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
				ConceptsType concepts = msg.doReadConcepts();
				Iterator<ConceptType> it = concepts.getConcept().iterator();
				while(it.hasNext()){
					ConceptType concept = it.next();
					if(concept.getSynonymCd().equals("Y")){
						MetadataRecord.getInstance().getSynonyms().add(concept.getName());
					}
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
			setCurrentNode(node);
			MetadataRecord.getInstance().clear();
			MetadataRecord.getInstance().setType("ModifierFolder");
			MetadataRecord.getInstance().setMetadata(node);
			MetadataRecord.getInstance().registerBrowser(browser);
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
			setCurrentNode(node);
			MetadataRecord.getInstance().clear();
			MetadataRecord.getInstance().setType("ModifierItem");
			MetadataRecord.getInstance().setMetadata(node);
			MetadataRecord.getInstance().registerBrowser(browser);
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
			MetadataRecord.getInstance().setMetadata(node);
			MetadataRecord.getInstance().registerBrowser(browser);
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
	
	private class ViewAction extends Action 
	{
		public ViewAction()
		{
			super("View");
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
			MetadataRecord.getInstance().setMetadata(node);
			MetadataRecord.getInstance().registerBrowser(browser);



			// populate synonyms list
			//		MetadataRecord.getInstance().getSynonyms().clear();  // start fresh with empty synonym list
			//		MetadataRecord.getInstance().setSynonymEditFlag(false);
			synonyms(Display.getCurrent());

			if(node.getData().getVisualattributes().startsWith("C")){
				MetadataRecord.getInstance().setType("Container");
			}
			else if(node.getData().getVisualattributes().startsWith("F")){
				MetadataRecord.getInstance().setType("Folder");
			}
			else if(node.getData().getVisualattributes().startsWith("I")){
				MetadataRecord.getInstance().setType("Item");
			}

			ValueMetadata.getInstance().clear();
			ValueMetadataType vmType = null;
			XmlValueType xml = node.getData().getMetadataxml();
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

			ViewWizard wizard = new ViewWizard();

		//	EditWizard wizard = new EditWizard();
			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.setPageSize(350, 350);
			dialog.setHelpAvailable(true);
			dialog.create();
			dialog.open();


			wizard.dispose();	           
		}

	}
	private MenuManager createModItemPopupMenu() {
		MenuManager modItemPopupMenu = new MenuManager();
		modItemPopupMenu.add(new DeleteAction());
		modItemPopupMenu.add(new RefreshAction());
		modItemPopupMenu.add(new EditAction());
		return modItemPopupMenu;

	}

	private MenuManager createRootPopupMenu(){

	//	MenuManager modifierPopupMenu = new MenuManager("Modifier");
	//	modifierPopupMenu.add(new ModifierFolderAction());
	//	modifierPopupMenu.add(new ModifierItemAction());
	//	modifierPopupMenu.add(new ModifierContainerAction());

		MenuManager newMenu = new MenuManager("New");
		newMenu.add(new	NewFolderAction());
		newMenu.add(new	NewItemAction());
		newMenu.add(new	NewContainerAction());
	//	newMenu.add(modifierPopupMenu);

		MenuManager rootPopupMenu = new MenuManager();
		rootPopupMenu.add(newMenu);
		rootPopupMenu.add(new RefreshAction());   

		return rootPopupMenu;
	}

	private MenuManager createItemPopupMenu(){

	//	MenuManager modifierPopupMenu = new MenuManager("Modifier");
	//	modifierPopupMenu.add(new ModifierFolderAction());
	//	modifierPopupMenu.add(new ModifierItemAction());
	//	modifierPopupMenu.add(new ModifierContainerAction());

	//	MenuManager newMenu = new MenuManager("New");
	//	newMenu.add(modifierPopupMenu);

		MenuManager itemPopupMenu = new MenuManager();
	//	itemPopupMenu.add(newMenu);
		itemPopupMenu.add(new DeleteAction());
		itemPopupMenu.add(new RefreshAction());
		itemPopupMenu.add(new EditAction());

		return itemPopupMenu;
	}


	private MenuManager createAllPopupMenu(){

	//	MenuManager modifierPopupMenu2= new MenuManager("Modifier");
	//	modifierPopupMenu2.add(new ModifierFolderAction());
	//	modifierPopupMenu2.add(new ModifierItemAction());
	//	modifierPopupMenu2.add(new ModifierContainerAction());

		MenuManager newMenu2 = new MenuManager("New");
		newMenu2.add(new NewFolderAction());
		newMenu2.add(new NewItemAction());
		newMenu2.add(new NewContainerAction());
//		newMenu2.add(modifierPopupMenu2);

		MenuManager allPopupMenu = new MenuManager();
		allPopupMenu.add(newMenu2);
		allPopupMenu.add(new DeleteAction());
		allPopupMenu.add(new RefreshAction());
		allPopupMenu.add(new EditAction());

		return allPopupMenu;

	}
	
	private MenuManager createViewPopupMenu(){


			MenuManager viewPopupMenu = new MenuManager();
			viewPopupMenu.add(new ViewAction());
			viewPopupMenu.add(new RefreshAction());   

			return viewPopupMenu;
		}
	

	
}



