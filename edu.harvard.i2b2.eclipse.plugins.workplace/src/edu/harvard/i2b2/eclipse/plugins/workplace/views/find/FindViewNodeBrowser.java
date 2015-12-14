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

import java.util.*;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.resource.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.workplace.model.TermSelectionProvider;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.StringUtil;
//import edu.harvard.i2b2.eclipse.plugins.workplace.views.find.ModifierComposite;
//import edu.harvard.i2b2.eclipse.plugins.workplace.views.NodeDropListener;
import edu.harvard.i2b2.eclipse.plugins.workplace.views.find.TreeData;
import edu.harvard.i2b2.eclipse.plugins.workplace.views.find.TreeNode;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.AddChildRequestMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.WorkplaceServiceDriver;
//import edu.harvard.i2b2.eclipse.plugins.workplace.ws.OntServiceDriver;
//import edu.harvard.i2b2.eclipse.plugins.workplace.ws.OntologyResponseMessage;
import edu.harvard.i2b2.wkplclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FolderType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FoldersType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.GetChildrenType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.MatchStrType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ModifierType;
import edu.harvard.i2b2.wkplclient.datavo.vdo.ModifiersType;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FindByChildType;

public class FindViewNodeBrowser extends ApplicationWindow
{
	private Log log = LogFactory.getLog(FindViewNodeBrowser.class.getName());
	public boolean stopRunning = false;
	private Button findButton;
	private TreeViewer viewer;
	private int result;
	private TreeData currentData;
	public TreeNode rootNode;       
	private ImageRegistry imageRegistry;
	private StatusLineManager slm;
	//private NodeBrowser browser;
	//private Menu modifierMenu;
	
	private Menu menu;
	private Menu folderMenu;
	private Menu caseMenu;

	public FindViewNodeBrowser(Composite parent, int inputFlag, Button button, StatusLineManager slm)
	{
		super(null);
		this.slm = slm;
		//this.browser = this;
		findButton = button;
		imageRegistry= new ImageRegistry();
		createImageRegistry();

		createTreeViewer(parent, SWT.MULTI | SWT.BORDER, inputFlag);
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		this.viewer.addDragSupport(DND.DROP_COPY, types, new NodeDragListener(this.viewer));     
		//this.viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT, types, new NodeDropListener(this.viewer));

	  }

	// key mappings and icon file names match xml_i2b2_type.

	  private void createImageRegistry()
	  {
		  ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/leaf.jpg");
		  this.imageRegistry.put("leaf", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/multi.bmp");
		  this.imageRegistry.put("multi", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/openFolder.jpg");
		  this.imageRegistry.put("openFolder", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/openCase.jpg");
		  this.imageRegistry.put("openCase", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/closedFolder.jpg");
		  this.imageRegistry.put("closedFolder", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/closedCase.jpg");
		  this.imageRegistry.put("closedCase", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/concept.jpg");
		  this.imageRegistry.put("concept", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/conceptFA.jpg");
		  this.imageRegistry.put("conceptFA", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/patient_coll.jpg");
		  this.imageRegistry.put("patient_coll", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/xml_results.jpg");
		  this.imageRegistry.put("xml_results", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/patient_count_xml.jpg");
		  this.imageRegistry.put("patient_count_xml", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/prev_query.jpg");
		  this.imageRegistry.put("prev_query", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/query_definition.jpg");
		  this.imageRegistry.put("query_definition", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/group_template.jpg");
		  this.imageRegistry.put("group_template", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/observation.png");
		  this.imageRegistry.put("observation", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/patient.jpg");
		  this.imageRegistry.put("patient", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/patient.jpg");
		  this.imageRegistry.put("pdo", imageDescriptor);
		  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/morepeople.jpg");
		  this.imageRegistry.put("encounter_coll", imageDescriptor);
	  }

	  private void createTreeViewer(Composite parent, int style, int inputFlag)
	  {
		Tree tree = new Tree(parent, style);  
	 
		GridData gridData = new GridData(GridData.FILL_BOTH);
	    //gridData.verticalSpan = 50;
	    gridData.horizontalSpan = 2;
	    //gridData.widthHint = 150;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.heightHint = 100;
	    //gridData.grabExcessVerticalSpace = true;
	    tree.setLayoutData(gridData);
	    
	    MenuManager popupMenu = new MenuManager();
	    IAction renameAction = new RenameAction();
	    IAction annotateAction = new AnnotateAction();
	    IAction deleteAction = new DeleteAction();
	    IAction exportAction = new ExportAction();
	    popupMenu.add(renameAction);
	    popupMenu.add(annotateAction);
	    popupMenu.add(deleteAction);
	    popupMenu.add(exportAction);
	    menu = popupMenu.createContextMenu(tree);
	    
	    MenuManager casePopupMenu = new MenuManager();
	    IAction makeFolderAction = new NewFolderAction();
	    casePopupMenu.add(makeFolderAction);
	    caseMenu = casePopupMenu.createContextMenu(tree);
	    
	    MenuManager folderPopupMenu = new MenuManager();
	    folderPopupMenu.add(renameAction);
	    folderPopupMenu.add(annotateAction);
	    folderPopupMenu.add(deleteAction);
	    folderPopupMenu.add(makeFolderAction);
	    folderMenu = folderPopupMenu.createContextMenu(tree);
	    
//	    tree.setMenu(menu);
	    
	    this.viewer = new TreeViewer(tree);  
	    this.viewer.setLabelProvider(new LabelProvider() {
	        public String getText(Object element) 
	        {
	        	// Set the tooltip data
	        	//  (cant be done in the lookup thread)
	        	//   maps TreeViewer node to Tree item and sets item.data
	        	TreeItem item =  (TreeItem) (viewer.testFindItem((TreeNode) element));
	        	String tooltip = ((TreeNode)element).getData().getTooltip();
	        	if ((tooltip == null) || (tooltip.equals("")))
	        	{
	        		tooltip = ((TreeNode)element).toString();		
	        	}
	        	tooltip = " " + tooltip + " ";
	        	item.setData("TOOLTIP", tooltip);        
	   
	        	// if element is Inactive; print label in gray
	        	if (((TreeNode)element).getData().getVisualAttributes().substring(1,2).equals("I"))
	        	{
	        		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	        		item.setForeground(color);
	        	}
	        	
//	        	 if element is Hidden; print label in red
	        	else if (((TreeNode)element).getData().getVisualAttributes().substring(1,2).equals("H"))
	        	{
	        		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	        		item.setForeground(color);
	        	}
	        	
//	       	 if element is undefined; print label in red
	        	else if (((TreeNode)element).getData().getVisualAttributes().equals("C-ERROR"))
	        	{
	        		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	        		item.setForeground(color);
	        	}
	        	
//	       	 if element is synonym; print label in dark blue
//	        	if (((TreeNode)element).getData().getSynonymCd() != null) {
//	        		if (((TreeNode)element).getData().getSynonymCd().equals("Y"))
//	        		{
//	        			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
//	        			item.setForeground(color);
//	        		}
//	        	}	
	        	return ((TreeNode)element).toString();
	        }
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
	    	TreeNode placeholder = new TreeNode("placeholder",status, "C-ERROR");
	    	this.rootNode.addChild(placeholder);
	    	this.refresh();
	    	System.setProperty("errorMessage", "");
	    }
	   
		this.viewer.addTreeListener(new ITreeViewerListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				final TreeNode node = (TreeNode) event.getElement();
				if (node.getData().getVisualAttributes().equals("FA"))
					node.getData().setVisualAttributes("FAO");
				else if (node.getData().getVisualAttributes().equals("CA"))
					node.getData().setVisualAttributes("CAO");
				else if (node.getData().getVisualAttributes().equals("FH"))
					node.getData().setVisualAttributes("FHO");
				else if (node.getData().getVisualAttributes().equals("CH"))
					node.getData().setVisualAttributes("CHO");
				//viewer.refresh();

				// check to see if child is a placeholder ('working...')
				//   if so, make Web Service call to update children of node

				if (node.getChildren().size() > 0) {	
					TreeNode firstChild = (TreeNode)(node.getChildren().get(0));
					if((firstChild.getData().getVisualAttributes().equals("LAO")) || (firstChild.getData().getVisualAttributes().equals("LHO")) )
					{
						// child is a placeholder, so remove from list 
						//   update list with real children  
//						node.getXMLData(viewer, browser).start();		
						node.getXMLData(viewer).start();					
					}


					else {
						for(int i=0; i<node.getChildren().size(); i++) {
							TreeNode child = (TreeNode)(node.getChildren().get(i));
							if(child.getData().getVisualAttributes().equals("FAO"))
							{
								child.getData().setVisualAttributes("FA");
							}
							else if (child.getData().getVisualAttributes().equals("CAO")) {
								child.getData().setVisualAttributes("CA");	
							}
							else if(child.getData().getVisualAttributes().equals("FHO"))
							{
								child.getData().setVisualAttributes("FH");
							}
							else if (child.getData().getVisualAttributes().equals("CHO")) {
								child.getData().setVisualAttributes("CH");	
							}
						}
					}
				}
				viewer.refresh();
				viewer.expandToLevel(node, 1);
			}
			public void treeCollapsed(TreeExpansionEvent event) {
				final TreeNode node = (TreeNode) event.getElement();
				if (node.getData().getVisualAttributes().equals("FAO"))
					node.getData().setVisualAttributes("FA");
				else if (node.getData().getVisualAttributes().equals("CAO"))
					node.getData().setVisualAttributes("CA");
				else if (node.getData().getVisualAttributes().equals("FHO"))
					node.getData().setVisualAttributes("FH");
				else if (node.getData().getVisualAttributes().equals("CHO"))
					node.getData().setVisualAttributes("CH");
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
	 	       if((node.getData().getVisualAttributes()).equals("FA"))
	 	       {
	 	    	  node.getData().setVisualAttributes("FAO");
	 	    	  expand = true;
	 	       }
	 	       else if ((node.getData().getVisualAttributes()).equals("CA"))
	 	       {
	  	    	  node.getData().setVisualAttributes("CAO");
	  	    	  expand = true;
	  	       }
	 	       
				else if(node.getData().getVisualAttributes().equals("FH"))
				{
					node.getData().setVisualAttributes("FHO");
					expand = true;
				}
				else if (node.getData().getVisualAttributes().equals("CH")) {
					node.getData().setVisualAttributes("CHO");	
					expand = true;
				}
	 	       
	 	       if(expand == true)
	 	       {
	 			  viewer.expandToLevel(node, 1);
	 			  viewer.refresh(node);
	 			  
	 				// check to see if this node's child is a placeholder ('working...')
	 				//   if so, make Web Service call to update children of node

	 				if (node.getChildren().size() > 0)
	 				{	
	 					TreeNode firstChild = (TreeNode)(node.getChildren().get(0));
	 					if((firstChild.getData().getVisualAttributes().equals("LAO"))  || (firstChild.getData().getVisualAttributes().equals("LHO")))
	 					{
	 						// child is a placeholder, so remove from list 
	 						//   update list with real children  
	 		////				node.getXMLData(viewer, browser).start();
	 						node.getXMLData(viewer).start();				
	 					}
	 				
	 					else {
	 						for(int i=0; i<node.getChildren().size(); i++) {
	 							TreeNode child = (TreeNode)(node.getChildren().get(i));
	 							if(child.getData().getVisualAttributes().equals("FAO"))
	 							{
	 								child.getData().setVisualAttributes("FA");
	 							}
	 							else if (child.getData().getVisualAttributes().equals("CAO")) {
	 								child.getData().setVisualAttributes("CA");	
	 							}
	 							else if(child.getData().getVisualAttributes().equals("FHO"))
	 							{
	 								child.getData().setVisualAttributes("FH");
	 							}
	 							else if (child.getData().getVisualAttributes().equals("CHO")) {
	 								child.getData().setVisualAttributes("CH");	
	 							}
	 						}
	 						viewer.refresh();
	 					}
	 				}
	 				
	 	       }
	 	       
	 	       // Case where we are collapsing the node
	 	       else if (node.getData().getVisualAttributes().equals("FAO"))
	 	       {
	 	    	  node.getData().setVisualAttributes("FA");
	 	    	  viewer.collapseToLevel(node, 1);
	 	    	  viewer.refresh(node);
	 	       }
	 	       else if (node.getData().getVisualAttributes().equals("CAO"))
	 	       {
	 	    	  node.getData().setVisualAttributes("CA");
	 	    	  viewer.collapseToLevel(node, 1);
	 	    	  viewer.refresh(node);
	 	       }
	 	       else if (node.getData().getVisualAttributes().equals("FHO"))
	 	       {
	 	    	  node.getData().setVisualAttributes("FH");
	 	    	  viewer.collapseToLevel(node, 1);
	 	    	  viewer.refresh(node);
	 	       }
	 	       else if (node.getData().getVisualAttributes().equals("CHO"))
	 	       {
	 	    	  node.getData().setVisualAttributes("CH");
	 	    	  viewer.collapseToLevel(node, 1);
	 	    	  viewer.refresh(node);
	 	       }
			}
		});
		
//		 Implement a "fake" tooltip
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
					case SWT.MouseDown:
						if(event.button == 3) // right click
						{
							IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
							if (selection.size() != 1)
								return;

							TreeNode node =  (TreeNode) selection.getFirstElement();
							if(node.getData().getVisualAttributes().equals("LA"))
								menu.setVisible(true);
							else if(node.getData().getVisualAttributes().startsWith("ZA"))
								menu.setVisible(true);
							else if(node.getData().getVisualAttributes().startsWith("F"))
								folderMenu.setVisible(true);
							else if(node.getData().getVisualAttributes().startsWith("C"))
								caseMenu.setVisible(true);
						}

					case SWT.MouseMove: 
					case SWT.MouseExit: {
						if (tip == null)
							break;
						tip.dispose();
						tip = null;
						label = null;
						break;
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
//			            label.setText("Tooltip test");
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
		this.currentData = node.getData();
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
	
	private TreeNode getRootNode()
	{	  
		TreeNode root = new TreeNode("Find Items",
				"Find Items", "CA");
		this.rootNode = root;
		return root;
	}

	public void refresh()
	{		  
		findButton.setText("Find");	  
		if(this.stopRunning == false)
		{
			this.slm.setMessage(System.getProperty("statusMessage"));
			this.slm.update(true);
			this.viewer.refresh(this.rootNode);
		}
		else
		{  
			this.stopRunning = false;
			this.flush();
			this.viewer.refresh(this.rootNode);

		}
	}

	public void flush()
	{
		this.rootNode.getChildren().clear();
	}

	  private TreeNode populateRootNode()
	  {	  	  
		  TreeNode root = new TreeNode("home",
				  "home", "CA");	  
		  
		  //make call to getHomeFolders to get list of root nodes
		  //root.getHomeFolders(this.viewer);

		  this.rootNode = root;
		  return root;
	  
	  }
	  private class RenameAction extends Action 
	  {
		  public RenameAction()
		  {
			  super("Rename");
		  }
		  public void run()
		  {
			  IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			    if (selection.size() != 1)
			      return;

			  TreeNode node =  (TreeNode) selection.getFirstElement();
			  
			  if(Boolean.parseBoolean(System.getProperty("WPManager")) || 
					  (node.getData().getUserId().equals(UserInfoBean.getInstance().getUserName()))){
				  node.renameNode(viewer).start();
				  viewer.refresh();
			  }
			  else{
				  MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_INFORMATION | SWT.OK);
				  mBox.setText("Rename Node Message");
				  mBox.setMessage("You do not have permission to rename this node");
				  int result = mBox.open();
			  }
		  }
	  }
	  

	  private class ExportAction extends Action 
	  {
		  public ExportAction()
		  {
			  super("Export");
		  }
		  public void run()
		  {
			  IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			    if (selection.size() != 1)
			      return;

			  TreeNode node =  (TreeNode) selection.getFirstElement();
			  node.exportNode(viewer).start();

		  }
	  }

	  
	  private class AnnotateAction extends Action 
	  {
		  public AnnotateAction()
		  {
			  super("Annotate");
		  }
		  public void run()
		  {
			  IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			    if (selection.size() != 1)
			      return;

			  TreeNode node =  (TreeNode) selection.getFirstElement();
			  
			  if(Boolean.parseBoolean(System.getProperty("WPManager")) || 
					  (node.getData().getUserId().equals(UserInfoBean.getInstance().getUserName()))){
				  node.annotateNode(viewer).start();
				  viewer.refresh();
			  }
			  else{
				  MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_INFORMATION | SWT.OK);
				  mBox.setText("Annotate Node Message");
				  mBox.setMessage("You do not have permission to annotate this node");
				  int result = mBox.open();
			  }
		  }
	  }
	  
	  private class DeleteAction extends Action 
	  {
		  public DeleteAction()
		  {
			  super("Delete");
		  }
		  public void run()
		  {
			  IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			  if (selection.size() != 1)
				  return;
			  TreeNode node =  (TreeNode) selection.getFirstElement();

			  if(Boolean.parseBoolean(System.getProperty("WPManager")) || 
					  (node.getData().getUserId().equals(UserInfoBean.getInstance().getUserName()))){
				  int result = SWT.NO;
				  if(node.getData().getVisualAttributes().startsWith("F")){
					  MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
							  SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					  mBox.setText("Delete Node Dialog");
					  mBox.setMessage("Deleting a folder will delete its contents also\n" +
							  "Delete folder \""+ node.getData().getName() + "\"?\n");
					  result = mBox.open();
				  }
				  else{
					  MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
							  SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					  mBox.setText("Delete Node Dialog");
					  mBox.setMessage("Delete node \""+ node.getData().getName() + "\"?\n");
					  result = mBox.open();
				  }
				  if(result == SWT.NO) {
					  return;
				  }
				  else {
					  node.deleteNode(viewer).start();
				  }
			  }
			  else{
				  MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_INFORMATION | SWT.OK);
				  mBox.setText("Delete Node Message");
				  mBox.setMessage("You do not have permission to delete this node");
				  int result = mBox.open();
			  }
		  }
	  }
	  
	  private class NewFolderAction extends Action 
	  {
		  public NewFolderAction()
		  {
			  super("New Folder");
		  }
		  public void run()
		  {
			  IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			    if (selection.size() != 1)
			      return;

			  TreeNode currentTarget =  (TreeNode) selection.getFirstElement();
				TreeData tdata = new TreeData();
				tdata.setName("New Folder");
				tdata.setTooltip("FOLDER:New Folder");
				tdata.setIndex(new AddChildRequestMessage().generateMessageId());		
				tdata.setParentIndex(currentTarget.getData().getIndex());
				tdata.setVisualAttributes("FA");
				tdata.setWorkXml(null);
				tdata.setWorkXmlI2B2Type("FOLDER");
				tdata.setUserId(UserInfoBean.getInstance().getUserName());
				tdata.setGroupId(currentTarget.getData().getGroupId());
				tdata.setShareId(currentTarget.getData().getShareId());
				tdata.setWorkXmlSchema(null);
				tdata.setEntryDate(null);
				tdata.setChangeDate(null);
				tdata.setStatusCd(null);
				tdata.setTableCd(currentTarget.getData().getTableCd());
//				log.info(tdata.getIndex());
				
				TreeNode child = new TreeNode(tdata);
				currentTarget.addChild(child);

				child.addNode(viewer).start();

		//	    child.renameNode(viewer).start();
				
				viewer.refresh();
		  }

	  }


	public Thread getFindData(final String categoryKey, final List categories, String phrase, String match) {
		final Display theDisplay = Display.getCurrent();
		final FindViewNodeBrowser theBrowser = this;
		final TreeViewer theViewer = this.viewer;
		final String lookupPhrase = phrase;
		final String lookupOperator = match;
		final List lookupCategories = categories;
		return new Thread() {
			public void run() {						
				theBrowser.rootNode.getChildren().clear();
				theBrowser.findNodes(categoryKey, lookupCategories, lookupPhrase, lookupOperator, theDisplay, theViewer);

				theDisplay.syncExec(new Runnable() {
					public void run() {
						if (theBrowser.rootNode.getChildren().size() == 0)
						{	
							TreeNode placeholder = new TreeNode("placeholder", "There were no matches", "C-UNDEF");
							theBrowser.rootNode.addChild(placeholder);
						}	
						theBrowser.refresh();
					}
				});
			}
		};
	}
	private void findNodes(String categoryKey, List categories, String phrase, String operator, Display display, TreeViewer viewer) {
		FindByChildType vocabData = new FindByChildType();

		MatchStrType match = new MatchStrType();
		match.setValue(phrase);
		if(operator.equals("Containing"))
			operator = "contains";
		else if(operator.equals("Starting with"))
			operator = "left";
		else if(operator.equals("Ending with"))
			operator = "right";	
		else if(operator.equals("Exact"))
			operator = "exact";	
		match.setStrategy(operator);
		vocabData.setMatchStr(match);
		vocabData.setType("core");
		vocabData.setBlob(true);
		vocabData.setMax(Integer.parseInt(System.getProperty("OntFindMax")));
		vocabData.setHiddens(Boolean.parseBoolean(System.getProperty("OntFindHiddens")));
		//vocabData.setSynonyms(Boolean.parseBoolean(System.getProperty("OntFindSynonyms")));

		if(categoryKey.equals("any"))
		{
			if(categories != null ) {
				Iterator categoriesIterator = categories.iterator();		
				while(categoriesIterator.hasNext()&& this.stopRunning == false)
				{
					vocabData.setCategory(StringUtil.getTableCd(((ConceptType)categoriesIterator.next()).getKey()));
					List concepts = getNodes(vocabData, display, viewer);
					getNodesFromXMLString(concepts);
				}
				//				System.setProperty("statusMessage", count + " records returned");
			}
		}

		else
		{
			vocabData.setCategory(categoryKey);
			List folders = getNodes(vocabData, display, viewer);
			getNodesFromXMLString(folders);
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
				this.rootNode.addChild(childNode);
			} 	
		}
	}

	/*private void getNodesFromXMLString(List concepts){
		if(concepts != null){
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
				this.rootNode.addChild(childNode);
			}
		}
	}*/

	private List getNodes(FindByChildType vocabData, Display theDisplay, final TreeViewer theViewer) {

		WorkplaceResponseMessage msg = new WorkplaceResponseMessage();
		StatusType procStatus = null;	
		try {
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = WorkplaceServiceDriver.getNameInfo(vocabData);
				
				//////////testing -- using get children of "wp1"///////
				//GetChildrenType parentType = new GetChildrenType();
				//parentType.setBlob(true);
				//parentType.setParent("\\\\demo"+"\\Kmx3tsrijobZ9DnYN4Od");
				//String response = WorkplaceServiceDriver.getChildren(parentType);
				/////////////////
				
				procStatus = msg.processResult(response);;
				if(procStatus.getValue().equals("MAX_EXCEEDED")) {
					log.info("MAX_EXCEEDED");
					theDisplay.syncExec(new Runnable() {
						public void run() {
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), 
									SWT.ICON_QUESTION | SWT.YES | SWT.NO);
							mBox.setText("Please Note ...");
							mBox.setMessage("Max number of terms exceeded please try with a more specific query.\n"
									+ "Populating the query results will be slow\n"
									+"Do you want to continue?");
							result = mBox.open();
						}
					});
					if(result == SWT.NO) {
						procStatus.setType("DONE");
					}
					else {
						vocabData.setMax(null);
						response = WorkplaceServiceDriver.getNameInfo(vocabData);
						
						//////////testing -- using get children of "wp1"///////
						//parentType = new GetChildrenType();
						//parentType.setBlob(true);
						//parentType.setParent("\\\\demo"+"\\Kmx3tsrijobZ9DnYN4Od");
						//response = WorkplaceServiceDriver.getChildren(parentType);
						/////////////////
						
						procStatus = msg.processResult(response);
					}
				}
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
					this.stopRunning = true;
					return null;
				}		
				else
					procStatus.setType("DONE");
			}

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
			this.stopRunning = true;
			return null;
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
			this.stopRunning = true;
			return null;
		}
		FoldersType folders = msg.doReadFolders();   	 
		if(folders == null)
			return null;
		List nodes = folders.getFolder();
		return nodes;////concepts;
	}

	public Thread getSchemeData(final String schemeKey, List schemes, String phrase, String match) {
		final Display theDisplay = Display.getCurrent();
		final FindViewNodeBrowser theBrowser = this;
		final TreeViewer theViewer = this.viewer;
		final String lookupPhrase = phrase;
		final String lookupKey = schemeKey;
		final String lookupStrategy = match;
		final List lookupSchemes = schemes;

		return new Thread() {
			public void run() {						
				theBrowser.rootNode.getChildren().clear();			
				theBrowser.findSchemeNodes(lookupKey, lookupSchemes, lookupPhrase, lookupStrategy, theDisplay, theViewer);							
				theDisplay.syncExec(new Runnable() {
					public void run() {

						if (theBrowser.rootNode.getChildren().size() == 0)
						{	
							TreeNode placeholder = new TreeNode("placeholder", "There were no matches", "C-UNDEF");
							theBrowser.rootNode.addChild(placeholder);							
						}	
						theBrowser.refresh();
					}
				});
			}
		};
	}
	private void findSchemeNodes(String key, List schemes, String phrase, String operator, Display display, TreeViewer viewer) {
		FindByChildType vocabData = new FindByChildType();

		MatchStrType match = new MatchStrType();
		match.setValue(phrase);

		if(operator.equals("Containing"))
			operator = "contains";
		else if(operator.equals("Starting with"))
			operator = "left";
		else if(operator.equals("Ending with"))
			operator = "right";	
		else if(operator.equals("Exact"))
			operator = "exact";	
		match.setStrategy(operator);

		vocabData.setType("core");
		//	vocabData.setBlob(false);
		vocabData.setBlob(true);
		vocabData.setMax(Integer.parseInt(System.getProperty("OntFindMax")));
		vocabData.setHiddens(Boolean.parseBoolean(System.getProperty("OntFindHiddens")));
		//vocabData.setSynonyms(Boolean.parseBoolean(System.getProperty("OntFindSynonyms")));

		if(key.equals("any"))
		{
			if(schemes != null ) {
				Iterator schemesIterator = schemes.iterator();		
				while(schemesIterator.hasNext()&& this.stopRunning == false)
				{
					vocabData.setCategory(null);
					match.setValue(((ConceptType)schemesIterator.next()).getKey()+phrase);
					vocabData.setMatchStr(match);
					List concepts = getSchemeNodes(vocabData, display, viewer);
					if((concepts != null) && (concepts.isEmpty()==false)) {
						//getNodesFromXMLString(concepts);
						//		break;   // cycle through all schemes; dont stop at first hit
					}
				}
			}
			//			System.setProperty("statusMessage", count + " records returned");
		}

		else
		{
			match.setValue(key+phrase);
			vocabData.setMatchStr(match);
			vocabData.setCategory(null);
			List concepts = getSchemeNodes(vocabData, display, viewer);
			if(concepts != null) {
				//getNodesFromXMLString(concepts);
			}
		}		
	}

	private List getSchemeNodes(FindByChildType vocabData, Display theDisplay, final TreeViewer theViewer) {

		/*OntologyResponseMessage msg = new OntologyResponseMessage();
		StatusType procStatus = null;	
		try {
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = OntServiceDriver.getCodeInfo(vocabData, "FIND");			
				procStatus = msg.processResult(response);
				if(procStatus.getValue().equals("MAX_EXCEEDED")) {
					log.debug("MAX_EXCEEDED");
					theDisplay.syncExec(new Runnable() {
						public void run() {
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), 
									SWT.ICON_QUESTION | SWT.YES | SWT.NO);
							mBox.setText("Please Note ...");
							mBox.setMessage("Max number of terms exceeded please try with a more specific query.\n"
									+ "Populating the query results will be slow\n"
									+"Do you want to continue?");
							result = mBox.open();
						}
					});
					if(result == SWT.NO) {
						procStatus.setType("DONE");
					}
					else {
						vocabData.setMax(null);
						response = OntServiceDriver.getCodeInfo(vocabData, "FIND");
						procStatus = msg.processResult(response);
					}
				}
				// other error cases  USER INVALID, TABLE ACCESS DENIED, DATABASE ERROR
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
					this.stopRunning = true;
					return null;
				}		
				else
					procStatus.setType("DONE");
			}

		} catch (AxisFault e) {
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
			this.stopRunning = true;
			return null;
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
			this.stopRunning = true;
			return null;
		}
		ConceptsType allConcepts = msg.doReadConcepts();   	    
		List concepts = allConcepts.getConcept();*/
		return null;////concepts;
	}

	public Thread getFindModifierData(final String type, String phrase, String match) {
		final Display theDisplay = Display.getCurrent();
		final FindViewNodeBrowser theBrowser = this;
		final TreeViewer theViewer = this.viewer;
		final String lookupPhrase = phrase;
		final String lookupOperator = match;
		final String lookupType = type;
		return new Thread() {
			public void run() {						
				theBrowser.rootNode.getChildren().clear();

				theBrowser.findModifierNodes(lookupType, lookupPhrase, lookupOperator, theDisplay, theViewer);

				theDisplay.syncExec(new Runnable() {
					public void run() {
						if (theBrowser.rootNode.getChildren().size() == 0)
						{	
							TreeNode placeholder = new TreeNode("placeholder", "There were no matches", "C-UNDEF");
							theBrowser.rootNode.addChild(placeholder);
						}	
						theBrowser.refresh();
					}
				});
			}
		};
	}

	private void findModifierNodes(String type, String phrase, String operator, Display display, TreeViewer viewer) {
		FindByChildType vocabData = new FindByChildType();

		MatchStrType match = new MatchStrType();
		match.setValue(phrase);
		if(operator.equals("Containing"))
			operator = "contains";
		else if(operator.equals("Starting with"))
			operator = "left";
		else if(operator.equals("Ending with"))
			operator = "right";	
		else if(operator.equals("Exact"))
			operator = "exact";	
		match.setStrategy(operator);
		vocabData.setMatchStr(match);
		vocabData.setType("core");
		vocabData.setBlob(true);
		vocabData.setMax(Integer.parseInt(System.getProperty("OntFindMax")));
		vocabData.setHiddens(Boolean.parseBoolean(System.getProperty("OntFindHiddens")));
		//vocabData.setSynonyms(Boolean.parseBoolean(System.getProperty("OntFindSynonyms")));
		//vocabData.setSelf(ModifierComposite.getInstance().getNodeKey());
		List modifiers = getModifierNodes(type, vocabData, display, viewer);
		//getModifierNodesFromXMLString(modifiers);

	}

	/*private void getModifierNodesFromXMLString(List<ModifierType> modifiers){
		if(!modifiers.isEmpty()){
			Iterator<ModifierType> it = modifiers.iterator();

			while(it.hasNext()){
				ConceptType child = rootNode.getData();
				TreeData data = new TreeData(child);
				data.setModifier(	(ModifierType)it.next());
				TreeNode childNode = new TreeNode(data);
				if((data.getModifier().getVisualattributes().startsWith("DA")) || (data.getModifier().getVisualattributes().startsWith("OA")))  
				{
					TreeNode placeholder = new TreeNode(child.getLevel() + 1, "working...", "working...", "RA");
					placeholder.setOpen(true);
					ModifierType modifier = new ModifierType();
					modifier.setName("working...");
					modifier.setVisualattributes("RA");
					placeholder.getData().setModifier(modifier);
					childNode.addChild(placeholder);
					rootNode.addChild(childNode);
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
					rootNode.addChild(childNode);
				}
				else if((data.getModifier().getVisualattributes().startsWith("R"))){
					rootNode.addChild(childNode);
				}	
			}
		}
	}*/

	private List getModifierNodes(String type, FindByChildType vocabData, Display theDisplay, final TreeViewer theViewer) {

		/*OntologyResponseMessage msg = new OntologyResponseMessage();
		StatusType procStatus = null;	
		try {
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = OntServiceDriver.getNameInfo(vocabData, "FIND");			
				procStatus = msg.processResult(response);;
				if(procStatus.getValue().equals("MAX_EXCEEDED")) {
					log.info("MAX_EXCEEDED");
					theDisplay.syncExec(new Runnable() {
						public void run() {
							MessageBox mBox = new MessageBox(theViewer.getTree().getShell(), 
									SWT.ICON_QUESTION | SWT.YES | SWT.NO);
							mBox.setText("Please Note ...");
							mBox.setMessage("Max number of terms exceeded please try with a more specific query.\n"
									+ "Populating the query results will be slow\n"
									+"Do you want to continue?");
							result = mBox.open();
						}
					});
					if(result == SWT.NO) {
						procStatus.setType("DONE");
					}
					else {
						vocabData.setMax(null);
						if(type.equals("name"))
							response = OntServiceDriver.getModifierNameInfo(vocabData, "FIND");
						else
							response = OntServiceDriver.getModifierCodeInfo(vocabData, "FIND");
						procStatus = msg.processResult(response);
					}
				}
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
					this.stopRunning = true;
					return null;
				}		
				else
					procStatus.setType("DONE");
			}

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
			this.stopRunning = true;
			return null;
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
			this.stopRunning = true;
			return null;
		}
		ModifiersType allModifiers = msg.doReadModifiers();   	 
		if(allModifiers == null)
			return null;
		List modifiers = allModifiers.getModifier();*/
		return null;//modifiers;
	}

	private MenuManager createModifierPopupMenu(Composite parent){
		MenuManager allPopupMenu = null;
		/*if(parent == ModifierComposite.getInstance().getParent()){
			allPopupMenu = new MenuManager();
			allPopupMenu.add(new FindModifierAction());
			allPopupMenu.add(new JumpToTermAction());
			//		allPopupMenu.add(new RefreshAction());
		}
		else if(parent == ModifierComposite.getCodeInstance().getParent()){
			allPopupMenu = new MenuManager();
			allPopupMenu.add(new FindCodeModifierAction());
			allPopupMenu.add(new JumpToTermAction());


		}*/
		return allPopupMenu;

	}


	private class FindModifierAction extends Action 
	{
		public FindModifierAction()
		{
			super("Find Modifier");
		}
		@Override
		public void run()
		{
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			TreeNode node = (TreeNode) selection.getFirstElement();
			//ModifierComposite.getInstance().enableComposite(node);
		}
	}
	private class FindCodeModifierAction extends Action 
	{
		public FindCodeModifierAction()
		{
			super("Find Modifier");
		}
		@Override
		public void run()
		{
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			TreeNode node = (TreeNode) selection.getFirstElement();
			//ModifierComposite.getCodeInstance().enableComposite(node);
		}
	}
	private class JumpToTermAction extends Action 
	{
		public JumpToTermAction()
		{
			super("Jump To Term in Tree");
		}
		@Override
		public void run()
		{
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			IWorkbenchPage[] pages = windows[0].getPages();
		
			final ViewPart navTermsView = (ViewPart) pages[0].findView("edu.harvard.i2b2.eclipse.plugins.ontology.views.ontologyView");			
			pages[0].activate(navTermsView);
			
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;
			TermSelectionProvider.getInstance().fireSelectionChanged(selection);
			


			
		}
	}
	
}
// Old select service base methods
/*	public Thread getFindData(final String lookup, final Document categories, String phrase, String match) {
		final Display theDisplay = Display.getCurrent();
		final NodeBrowser theBrowser = this;
		final String lookupPhrase = phrase;
		final String lookupOperator = match;
		final Document lookupCategories = categories;
		return new Thread() {
			public void run() {		
				String lookupTable = lookup;
				List tables = categories.getRootElement().getChildren("table");
				Iterator tableIterator = tables.iterator();		
				while(tableIterator.hasNext())
				{
					Element table = (org.jdom.Element) tableIterator.next();
					String name = table.getChild("name").getText();
					if (name.equals(lookup))
					{
						lookupTable = table.getChild("tableName").getText();
						break;
					}
//					String tableName = table.getChild("tableName").getText();
//					String status = table.getChild("status").getText();
//					String description = table.getChild("description").getText();
//					String lookupDB = table.getChild("lookupDB").getText();
//					String webserviceName = table.getChild("webserviceName").getText();					
				}

				theBrowser.rootNode.getChildren().clear();
				theBrowser.parseFind(lookupTable, lookupCategories, lookupPhrase, lookupOperator);

	/**			if(lookupTable.equals("Any Category"))
				{
					theBrowser.getFind("Demographics", lookupName, lookupOperator);			    		
					theBrowser.getFind("Diagnoses", lookupName, lookupOperator);
					theBrowser.getFind("Encounters",lookupName, lookupOperator);		    		
					theBrowser.getFind("LabTests", lookupName, lookupOperator);
					theBrowser.getFind("Medications", lookupName, lookupOperator);			    		
					theBrowser.getFind("Microbiology", lookupName, lookupOperator);
					theBrowser.getFind("Procedures", lookupName, lookupOperator);		    		
					theBrowser.getFind("Providers", lookupName, lookupOperator);
					theBrowser.getFind("Transfusions", lookupName, lookupOperator);		    		
					theBrowser.getFind("I2B2", lookupName, lookupOperator);
				}
				else
				{
					theBrowser.getFind(lookupTable, lookupName, lookupOperator);
				}
 */			
/*				theDisplay.syncExec(new Runnable() {
					public void run() {
				 		if (theBrowser.rootNode.getChildren().size() == 0)
						{	
				 			TreeNode placeholder = new TreeNode(1, "placeholder", "There were no matches", "C-UNDEF", "UNDEF","UNDEF", "UNDEF");
							theBrowser.rootNode.addChild(placeholder);
					  	 }	
				 		theBrowser.refresh();
					}
				});
			}
		};
	}

	private void parseFind(String lookup, Document categories, String phrase, String operator)
	{
		int count = 0;
		if(categories == null)
		{
			slm.setMessage(System.getProperty("statusMessage"));
			slm.update(true);
			return;
		}
		if(lookup.equals("Any Category"))
		{
			List tables = categories.getRootElement().getChildren("table");
			Iterator tableIterator = tables.iterator();		
			while(tableIterator.hasNext())
			{
				Element table = (org.jdom.Element) tableIterator.next();
				String tableName = table.getChild("tableName").getText();
//				String name = table.getChild("name").getText();
//				String status = table.getChild("status").getText();
//				String description = table.getChild("description").getText();
				String lookupDB = table.getChild("lookupDB").getText();
				String webserviceName = table.getChild("webserviceName").getText();
				count = count + getFind(tableName, lookupDB, webserviceName, phrase, operator);
			}
			System.setProperty("statusMessage", count + " records returned");
		}

		else
		{
			List tables = categories.getRootElement().getChildren("table");
			Iterator tableIterator = tables.iterator();		
			while(tableIterator.hasNext())
			{
				Element table = (org.jdom.Element) tableIterator.next();
				String tableName = table.getChild("tableName").getText();
//				String name = table.getChild("name").getText();
//				String status = table.getChild("status").getText();
//				String description = table.getChild("description").getText();
				String lookupDB = table.getChild("lookupDB").getText();
				String webserviceName = table.getChild("webserviceName").getText();
				if(tableName.toUpperCase().equals(lookup.toUpperCase()))
				{
					count = getFind(tableName, lookupDB, webserviceName, phrase, operator);
					System.setProperty("statusMessage", count + " records returned");
				}
			}
		}		
	}

  private int getFind(String lookup, String lookupDB, String webserviceName, String phrase, String operator)
  {  
  	// method to send web services message to obtain 
	//  find query result data
      try {
  	    // Make a service
  	    SelectService service = new SelectServiceLocator();
  	    // Use service to get stub that implement SDI
        java.net.URL endpoint = new java.net.URL(webserviceName);
	    Select port = service.getSelect(endpoint);

  	    // Form the query
  	    org.w3c.dom.Document queryDoc = formFindQuery(lookup, lookupDB, phrase, operator);

  	    //System.out.println("Calling Web service");
  	    // Make the call
  	    org.w3c.dom.Document queryResultDoc  = port.getDataMartRecords(queryDoc);

  	    if (queryResultDoc == null)
  	    {
    		System.setProperty("statusMessage", "WebService query is faulty");
  	    	return 0;
  	    }
  	    return getNodesFromXML(queryResultDoc, lookup, lookupDB, webserviceName);
  	}catch(Exception e)
  	{    		
  		if(e.getMessage().contains("Not+Found"))
  		{
  			System.setProperty("statusMessage", "WebService " + webserviceName + " not found");
  		}
  		else
  		{
  			System.setProperty("statusMessage",e.getMessage());
  		}
  		return 0;
  	}
  }
  private org.w3c.dom.Document formFindQuery(String lookup, String lookupDB, String name, String operator){
  	org.w3c.dom.Document domDoc = null;
  	try {
  	    org.jdom.Element selectElement = new org.jdom.Element("selectParameters");
  	    org.jdom.Document jqueryDoc = new org.jdom.Document(selectElement);
  	    org.jdom.Element dbElement = new org.jdom.Element("i2b2Mart");
  	    dbElement.setText(lookupDB);	    
  	    selectElement.addContent(dbElement);

  	    org.jdom.Element table = new org.jdom.Element("table");
  	    table.setText(lookup);
  	    table.setAttribute("abbr", "l");
  	    table.setAttribute("numCols", "16");
  	    table.setAttribute("withBlob", "false");
  	    selectElement.addContent(table);
  	    org.jdom.Element where = new org.jdom.Element("where");
  	    String whereClause = "UPPER(l.c_name) ";
  	    if (operator.equals("Containing"))
  	    	whereClause = whereClause + "LIKE '%" + name.toUpperCase() + "%'";
  	    else if (operator.equals("Starting with"))
  	    	whereClause = whereClause + "LIKE '" + name.toUpperCase() + "%'";
  	    else if (operator.equals("Ending with"))
  	    	whereClause = whereClause + "LIKE '%" + name.toUpperCase() + "'";
  	    else if (operator.equals("Exact"))
  	    	whereClause = whereClause + "= '" + name.toUpperCase() + "'";

  	    where.setText(whereClause);
  	    selectElement.addContent(where);

  	    org.jdom.Element orderBy = new org.jdom.Element("orderBy");
  	    orderBy.setText("l.c_name");
  	    selectElement.addContent(orderBy);
 // 	System.out.println((new XMLOutputter()).outputString(jqueryDoc));
  	    org.jdom.output.DOMOutputter convertor = new org.jdom.output.DOMOutputter();
  	    domDoc = convertor.output(jqueryDoc);
  	}catch (Exception e){
  		System.setProperty("statusMessage", e.getMessage());
  	}
  	return domDoc;	
  }

  private int getNodesFromXML(org.w3c.dom.Document resultDoc, String lookupTable,
  								String lookupDB, String webserviceName)
  {
  	try {
  	    org.jdom.input.DOMBuilder builder = new org.jdom.input.DOMBuilder();
  	    org.jdom.Document jresultDoc = builder.build(resultDoc);
  	    org.jdom.Namespace ns = jresultDoc.getRootElement().getNamespace();
//System.out.println((new XMLOutputter()).outputString(jresultDoc));   	
  	    Iterator iterator = jresultDoc.getRootElement().getChildren("patientData", ns).iterator();
  	    while (iterator.hasNext())
  	    {
  	    	org.jdom.Element patientData = (org.jdom.Element) iterator.next();
     	    	org.jdom.Element lookup = (org.jdom.Element) patientData.getChild(lookupTable.toLowerCase(), ns).clone();

//     	    	modification of c_metadataxml tag to make it part of the xml document
       	    	org.jdom.Element metaDataXml = (org.jdom.Element) lookup.getChild("c_metadataxml");
       	    	String c_xml = metaDataXml.getText();
       	    	if ((c_xml!=null)&&(c_xml.trim().length()>0)&&(!c_xml.equals("(null)")))
       	    	{
       	    	 SAXBuilder parser = new SAXBuilder();
       			 String xmlContent = c_xml;
       		     java.io.StringReader xmlStringReader = new java.io.StringReader(xmlContent);
       		     org.jdom.Document tableDoc = parser.build(xmlStringReader);
       		     org.jdom.Element rootElement = (org.jdom.Element) tableDoc.getRootElement().clone();
       		     metaDataXml.setText("");
       		     metaDataXml.getChildren().add(rootElement);
       	    	}
       	    	//

     	    	XMLOutputter fmt = new XMLOutputter();
  	    	String XMLContents = fmt.outputString(lookup);
  	    		//this.setXMLContents(XMLContents);      	    
  	    	TreeData childData = new TreeData(XMLContents, lookupTable, lookupDB, webserviceName);
  	    	if(!(childData.getVisualattributes().substring(1,2).equals("H")))
  	    	{	
  	    		TreeNode child = new TreeNode(childData);
  	    	// if the child is a folder/directory set it up with a leaf placeholder
  	    		if((childData.getVisualattributes().equals("FA")) || (childData.getVisualattributes().equals("CA")))
  	    		{
  	    			TreeNode placeholder = new TreeNode(childData.getLevel() + 1, "working...", "working...", "LAO", childData.getLookupTable(), "UNDEF", "UNDEF");
  	    			child.addChild(placeholder);
  	    		}
  	    		this.rootNode.addChild(child);
  	    	}
  	    }
	    org.jdom.Element result = (org.jdom.Element) jresultDoc.getRootElement().getChild("result");
		String resultString = result.getChildTextTrim("resultString", ns);
		System.setProperty("statusMessage", resultString);
		int index = resultString.indexOf("records");
		return Integer.parseInt(resultString.substring(0,index-1).trim());
  	}catch (Exception e) {
  		System.setProperty("statusMessage", e.getMessage());
  		return 0;
  	}
  }
 */







