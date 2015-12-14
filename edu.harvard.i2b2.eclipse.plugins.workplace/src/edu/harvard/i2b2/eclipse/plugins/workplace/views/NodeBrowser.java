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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
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
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
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

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.workplace.util.XmlUtil;
import edu.harvard.i2b2.eclipse.plugins.workplace.ws.AddChildRequestMessage;


public class NodeBrowser extends ApplicationWindow
{
  private Log log = LogFactory.getLog(NodeBrowser.class.getName());	
  private TreeViewer viewer;
  private TreeData currentData;
  public TreeNode rootNode;       
  private ImageRegistry imageRegistry;
  private Menu menu;
  private Menu folderMenu;
  private Menu caseMenu;
  private IAction protectAction;
  
  public NodeBrowser(Composite parent, int inputFlag, StatusLineManager slm)
  {
    super(null);
    
    ArrayList<String> roles = (ArrayList<String>) UserInfoBean.getInstance().getProjectRoles();
    for(String param :roles) {
    	if(param.equalsIgnoreCase("manager")) {
    		System.setProperty("WPManager", String.valueOf(true));
    		break;
    	}
    }

    imageRegistry= new ImageRegistry();
    createImageRegistry();
    //dont allow multi-selection for now
    createTreeViewer(parent, SWT.BORDER, inputFlag);
//    createTreeViewer(parent, SWT.MULTI | SWT.BORDER, inputFlag);
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
       
   	this.viewer.addDragSupport(DND.DROP_COPY, types, new NodeDragListener(this.viewer));    
	this.viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT, types, new NodeDropListener(this.viewer));

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
    gridData.verticalSpan = 50;
    gridData.horizontalSpan = 2;
    gridData.widthHint = 150;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    tree.setLayoutData(gridData);
    
    MenuManager popupMenu = new MenuManager();
    IAction renameAction = new RenameAction();
    IAction annotateAction = new AnnotateAction();
    IAction deleteAction = new DeleteAction();
    IAction exportAction = new ExportAction();
    protectAction = new ProtectedAction();
    popupMenu.add(renameAction);
    popupMenu.add(annotateAction);
    popupMenu.add(deleteAction);
    popupMenu.add(exportAction);
    //if (UserInfoBean.getInstance().isRoleInProject("DATA_LDS"/*"DATA_PROT"*/)) {
    	//popupMenu.add(protectAction);
   // }
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
    //if (UserInfoBean.getInstance().isRoleInProject("DATA_LDS"/*"DATA_PROT"*/)) {
    	//folderPopupMenu.add(protectAction);
    //}
    folderMenu = folderPopupMenu.createContextMenu(tree);
    
//    tree.setMenu(menu);
    
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
        	
//        	 if element is Hidden; print label in red
        	else if (((TreeNode)element).getData().getVisualAttributes().substring(1,2).equals("H"))
        	{
        		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        		item.setForeground(color);
        	}
        	
//       	 if element is undefined; print label in red
        	else if (((TreeNode)element).getData().getVisualAttributes().equals("C-ERROR"))
        	{
        		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        		item.setForeground(color);
        	}
        	
        	//if((((TreeNode)element).getData().getProtectedAccess() != null) &&
        			//Boolean.parseBoolean(((TreeNode)element).getData().getProtectedAccess())) {
        	if((((TreeNode)element).getData().getProtectedAccess() != null) &&
        			(((TreeNode)element).getData().getProtectedAccess().equalsIgnoreCase("Y"))) {//protected_access()) {      		
        		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
        		item.setForeground(color);      		
        	}
        	else {
        		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
        		item.setForeground(color); 
        	}
        	
//       	 if element is synonym; print label in dark blue
//        	if (((TreeNode)element).getData().getSynonymCd() != null) {
//        		if (((TreeNode)element).getData().getSynonymCd().equals("Y"))
//        		{
//        			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
//        			item.setForeground(color);
//        		}
//        	}	
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
    String version = System.getProperty("wkplServerVersion");
	double vernum = Double.parseDouble(version);
    if ((vernum >= 1.6/*1.7*/) && UserInfoBean.getInstance().isRoleInProject("DATA_LDS"/*"DATA_PROT"*/)) {
    	popupMenu.add(protectAction);
    	folderPopupMenu.add(protectAction);
    }
    
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
				//if((firstChild.getData().getVisualAttributes().equals("LAO")) || (firstChild.getData().getVisualAttributes().equals("LHO")) )
				//{
					// child is a placeholder, so remove from list 
					//   update list with real children  
//					node.getXMLData(viewer, browser).start();		
					node.getXMLData(viewer).start();					
				//}


				/*else {
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
				}*/
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
				case SWT.MouseDown:
					if(event.button == 3) // right click
					{
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
						if (selection.size() != 1)
							return;

						TreeNode node =  (TreeNode) selection.getFirstElement();
						if(node.getData().getProtectedAccess() != null 
								&& node.getData().getProtectedAccess().equalsIgnoreCase("Y")) {
							protectAction.setText("Clear PHI Access");
						}
						else {
							protectAction.setText("Set PHI Access");
						}
						
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
	  TreeNode root = new TreeNode("home",
			  "home", "CA");	  
	  
	  //make call to getHomeFolders to get list of root nodes
	  root.getHomeFolders(this.viewer);

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
  
  private class ProtectedAction extends Action 
  {
	  public ProtectedAction()
	  {
		  super("Set PHI Access");
	  }
	  
	  
	  @Override
	  public void setText(String text) {
		// TODO Auto-generated method stub
		super.setText(text);
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
			  //if(node.getData().getVisualAttributes().startsWith("F")){
			//	  MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
			//			  SWT.ICON_INFORMATION | SWT.OK);
			//	  mBox.setText("Protect Node Dialog");
			//	  mBox.setMessage("Can't perform this action on a folder for now.");
			//	  result = mBox.open();
			 // }
			 // else{
				  MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
						  SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				  mBox.setText("Protect Node Dialog");
				  mBox.setMessage("Protect node \""+ node.getData().getName() + "\"?\n");
				  result = mBox.open();
			 // }
			  if(result == SWT.NO) {
				  return;
			  }
			  else {
				  node.protectNode(viewer).start();
			  }
		  }
		  else{
			  MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_INFORMATION | SWT.OK);
			  mBox.setText("Protect Node Message");
			  mBox.setMessage("You do not have permission to protect this node");
			  int result = mBox.open();
		  }

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
//			log.info(tdata.getIndex());
			
			TreeNode child = new TreeNode(tdata);
			currentTarget.addChild(child);

			child.addNode(viewer).start();

	//	    child.renameNode(viewer).start();
			
			viewer.refresh();
	  }
  }
  
} 







