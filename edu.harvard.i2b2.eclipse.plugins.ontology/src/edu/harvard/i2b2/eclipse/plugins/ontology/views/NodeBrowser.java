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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.graphics.*;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.ontology.model.RefreshNode;
import edu.harvard.i2b2.eclipse.plugins.ontology.model.TermSelectionProvider;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.StringUtil;
import edu.harvard.i2b2.eclipse.plugins.ontology.views.TreeNode;

public class NodeBrowser extends ApplicationWindow
{
  private Log log = LogFactory.getLog(NodeBrowser.class.getName());	
  private NodeBrowser browser;
  private TreeViewer viewer;
  private TreeData currentData;
  public TreeNode rootNode;       //unfortunately I dont have a way
	                               // to get the rootNode of a tree.....
  private ImageRegistry imageRegistry;
  private StatusLineManager slm;
  private Menu menu;
  
  public NodeBrowser(Composite parent, int inputFlag, StatusLineManager slm)
  {
    super(null);
    this.slm = slm;
    imageRegistry= new ImageRegistry();
    createImageRegistry();
    
    createTreeViewer(parent, SWT.MULTI | SWT.BORDER, inputFlag);
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
       
   	this.viewer.addDragSupport(DND.DROP_COPY, types, new NodeDragListener(this.viewer));      
  }

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
	  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifier_small.png");
	  this.imageRegistry.put("modLeaf", imageDescriptor);
	  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierFolder.png");
	  this.imageRegistry.put("modFolder", imageDescriptor);
	  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierContainer.png");
	  this.imageRegistry.put("modCase", imageDescriptor);
//	  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/xyz.jpg");
//	  this.imageRegistry.put("error", imageDescriptor);
  }
  
  private void createTreeViewer(Composite parent, int style, int inputFlag)
  {
	this.browser = this;
	Tree tree = new Tree(parent, style);  
	
	GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.verticalSpan = 50;
    gridData.horizontalSpan = 2;
    gridData.widthHint = 150;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    tree.setLayoutData(gridData);
  
    MenuManager popupMenu = new MenuManager();

    IAction countAction = new CountAction();
    countAction.setChecked(false);
 //   System.setProperty("getPatientCount", "false");
    //System.setProperty("getPatientCount", "false");
    
    boolean answer = Boolean.valueOf(System.getProperty("patientCountVisible"));
    countAction.setEnabled(answer);
    
    IAction shortTooltipAction = new ShortTooltipAction();
    shortTooltipAction.setChecked(false);
    System.setProperty("shortToolTip","false");
    IAction conceptCodeAction = new ConceptCodeAction();
    conceptCodeAction.setChecked(false);
    System.setProperty("showConceptCodes", "false");
    IAction refreshAction = new RefreshAction();
    IAction refreshAllAction = new RefreshAllAction();
//    popupMenu.add(countAction);
 //   popupMenu.add(shortTooltipAction);
  //  popupMenu.add(conceptCodeAction);
  //  popupMenu.add(new Separator());
    popupMenu.add(refreshAction);   
    popupMenu.add(refreshAllAction);   
    
    menu = popupMenu.createContextMenu(tree);
   
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
           	   		
        	if(System.getProperty("shortToolTip").equals("true"))
        		tooltip = ((TreeNode)element).getData().getName();
        	
        	else if(    ((TreeNode)element).getData().getModifier() != null ){
        		tooltip = ((TreeNode)element).getData().getModifier().getTooltip();
            	if(System.getProperty("shortToolTip").equals("true"))
            		tooltip = ((TreeNode)element).getData().getModifier().getName();
        	}
        	
        	else if ((tooltip == null) || (tooltip.equals("")))
        	{
        		tooltip = ((TreeNode)element).toString();		
        	}
	

        	if(!(tooltip.contains("\n")))
        	{
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
        	
        	if(System.getProperty("showConceptCodes").equals("true")){
        		if(    ((TreeNode)element).getData().getModifier() != null ){
        			if ((((TreeNode)element).getData().getModifier().getBasecode() != null) && (!((TreeNode)element).getData().getModifier().getBasecode().equals("null")) ) {
        				tooltip = tooltip + "(" + ((TreeNode)element).getData().getBasecode() + ")";
        			}
        		}
        		else if ((((TreeNode)element).getData().getBasecode() != null) && (!((TreeNode)element).getData().getBasecode().equals("null")) ) {
    				tooltip = tooltip + "(" + ((TreeNode)element).getData().getBasecode() + ")";
    			}
        	}
        	item.setData("TOOLTIP", tooltip);        
   
        	// if element is Inactive; print label in gray
        	if(    ((TreeNode)element).getData().getModifier() != null ){
        		if (((TreeNode)element).getData().getModifier().getVisualattributes().substring(1,2).equals("I")){
        			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
        			item.setForeground(color);
        		}
        	//	 if element is Hidden; print label in red
             	else if (((TreeNode)element).getData().getModifier().getVisualattributes().substring(1,2).equals("H"))
             	{
             		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
             		item.setForeground(color);
             	}
             	
//            	 if element is undefined; print label in red
             	else if (((TreeNode)element).getData().getModifier().getVisualattributes().equals("C-ERROR"))
             	{
             		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
             		item.setForeground(color);
             	}
//           	 if element is synonym; print label in blue
            	else if (((TreeNode)element).getData().getModifier().getSynonymCd() != null) {
            		if (((TreeNode)element).getData().getModifier().getSynonymCd().equals("Y"))
            		{
            			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
            			item.setForeground(color);
            		}
            	}
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
             	
//            	 if element is undefined; print label in red
             	else if (((TreeNode)element).getData().getVisualattributes().equals("C-ERROR"))
             	{
             		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
             		item.setForeground(color);
             	}

            	//       	 if element is synonym; print label in dark blue
             	else if (((TreeNode)element).getData().getSynonymCd() != null) {
            		if (((TreeNode)element).getData().getSynonymCd().equals("Y"))
            		{
            			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
            			item.setForeground(color);
            		}
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
    	System.setProperty("errorMessage", "");
    }
   
	this.viewer.addTreeListener(new ITreeViewerListener() {
		public void treeExpanded(TreeExpansionEvent event) {
			final TreeNode node = (TreeNode) event.getElement();
			node.setOpen(true);
			
			// check to see if child is a placeholder ('working...')
			//   if so, make Web Service call to update children of node
			//   leaves that are placeholders have open==true
			if (node.getChildren().size() == 1)
			{	
				TreeNode child = (TreeNode)(node.getChildren().get(0));
				if( (child.getData().getVisualattributes().startsWith("L"))							
						&& child.isOpen())	
				{
					// child is a placeholder, so remove from list 
					//   update list with real children  
					node.getXMLData(viewer, browser).start();
				}
				if( (child.getData().getVisualattributes().startsWith("R"))			
						&& child.isOpen())	
				{
					// child is a placeholder, so remove from list 
					//   update list with real children  
					node.getModXMLData(viewer, browser).start();
				}
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
 	           
 	 //          TableComposite.getInstance().addModifiers(node);
 	           setCurrentNode(node);
 	       }
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
			if((visualAttribute.startsWith("F")) || (visualAttribute.startsWith("C")) || (visualAttribute.startsWith("D")) || (visualAttribute.startsWith("O")))
				if(node.isOpen()){
					// collapsing node
					node.setOpen(false);
					viewer.collapseToLevel(node, 1);
					viewer.refresh();
				}
				else  // expanding node
				{
					viewer.expandToLevel(node, 1);
					viewer.refresh(node);
					node.setOpen(true);
					// check to see if this node's child is a placeholder ('working...')
					//   if so, make Web Service call to update children of node

					if (node.getChildren().size() == 1)
					{	
						TreeNode child = (TreeNode)(node.getChildren().get(0));
						if( (child.getData().getVisualattributes().startsWith("L"))							
								&& child.isOpen())	
						{
							// child is a placeholder, so remove from list 
							//   update list with real children  
							node.getXMLData(viewer, browser).start();
						}
						if( (child.getData().getVisualattributes().startsWith("R"))			
								&& child.isOpen())	
						{
							// child is a placeholder, so remove from list 
							//   update list with real children  
							node.getModXMLData(viewer, browser).start();
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

			//			TreeNode node =  (TreeNode) selection.getFirstElement();
			//			if( node.getData().getModifier() == null)
	 						menu.setVisible(true);
//						else if(node.getData().getVisualAttributes().startsWith("ZA"))
//							menu.setVisible(true);
					//	else if(node.getData().getVisualAttributes().startsWith("F"))
					//		folderMenu.setVisible(true);
					//	else if(node.getData().getVisualAttributes().startsWith("C"))
					//		caseMenu.setVisible(true);
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
		    
		    ISelectionChangedListener findListener = new ISelectionChangedListener() {
		    	public void selectionChanged(SelectionChangedEvent event) {
		    		if(event.getSelection() instanceof IStructuredSelection) {
		    			IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		    			edu.harvard.i2b2.eclipse.plugins.ontology.views.find.TreeNode node = 
		    				(edu.harvard.i2b2.eclipse.plugins.ontology.views.find.TreeNode) selection.getFirstElement();

		    			TreeNode node1 = new TreeNode(node);
		    			String foundKey = node1.getData().getKey();

		    			String parent = "\\\\"+StringUtil.getTableCd(foundKey)+"\\";
		    			String fullName = StringUtil.getPath(foundKey);

		    			String[] parts = fullName.split("\\\\");

		    			if(rootNode.getChildren().isEmpty())
		    				log.debug("rootNode is empty");
		    			else{
		    				viewer.getTree().setEnabled(false);
		    				rootNode.expandFindTree(viewer, parts, parent).start();	
		    			}
		    		}
		    	}
		    };

		    TermSelectionProvider.getInstance().addSelectionChangedListener(findListener);
			
					
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

// used in old select service version (pass XMLContents)
//  public void addNodes(String data)
//  {
//	  TreeData td = new TreeData(data);
//	  TreeNode child = new TreeNode(td);
//	  this.viewer.setInput(child);
//  }
  
  public TreeData getSelectedNode()
  {
	  return this.currentData;
  }
  
  public void refresh()
  {
	  this.viewer.refresh(this.rootNode);
	  this.slm.setMessage(System.getProperty("statusMessage"));
	  this.slm.update(true);
  }
  
  public void flush()
  {
	  this.rootNode.getChildren().clear();
  }
  
  public TreeNode populateRootNode()
  {	  	  
	  TreeNode root = new TreeNode(0,"Standard Query Items",
			  "Standard Query Items", "CA");
  
	  //make call to getCategories to get list of root nodes
		  root.getCategories(this.viewer, this.browser );

	  this.rootNode = root;
	  return root;
  
  }
	
	public void expandTreeView(TreeNode node){
		
		String foundKey = node.getData().getKey();

		String parent = "\\\\"+StringUtil.getTableCd(foundKey)+"\\";
		String fullName = StringUtil.getPath(foundKey);

		String[] parts = fullName.split("\\\\");

		if(rootNode.getChildren().isEmpty())
				log.debug("rootNode is empty");
		else{
			rootNode.expandFindTree(viewer, parts, parent).start();	
		}
		
	}
	
	public TreeViewer getViewer(){
		return this.viewer;
	}
  
//  IAction countAction = new CountAction();
//  IAction shortTooltipAction = new ShortTooltipAction();
 // IAction conceptCodeAction = new ConceptCodeAction();
//  IAction refreshAction = new RefreshAction();
  
  private class CountAction extends Action 
  {
	  public CountAction()
	  {
		  super("Enable Patient Counts");
	  }
	  @Override
	public void run()
	  {
		  //System.setProperty("getPatientCount", Boolean.toString(this.isChecked()));
		  IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		  if (selection.size() != 1)
			  return;
	  }
  }
  
  
  private class ShortTooltipAction extends Action 
  {
	  public ShortTooltipAction()
	  {
		  super("Use Short Tooltips");
	  }
	  @Override
	public void run()
	  {
		  System.setProperty("shortToolTip", Boolean.toString(this.isChecked()));
		  IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		  if (selection.size() != 1)
			  return;
	  }
  }

  private class ConceptCodeAction extends Action 
  {
	  public ConceptCodeAction()
	  {
		  super("Show Concept Codes in Tooltips");
	  }
	  @Override
	public void run()
	  {
		  System.setProperty("showConceptCodes", Boolean.toString(this.isChecked()));
		  IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		  if (selection.size() != 1)
			  return;


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
		    
	 	    if(node.getData().getModifier() == null)
	 	    	node.getXMLData(viewer, browser).start();
	 	    else
	 	    	node.getModXMLData(viewer, browser).start();
		    viewer.refresh();

	  }
  }
  
  private class RefreshAllAction extends Action 
  {
	  public RefreshAllAction()
	  {
		  super("Refresh All");
		 
	  }
	  @Override
	public void run()
	  {
		  viewer.setInput(populateRootNode());

	  }
  }
  
  
  
// Old select service version
//  private TreeNode getRootNode(int inputFlag)
//  {	  	  
//	  TreeNode root = new TreeNode(0,"Standard Query Items",
//			  "Standard Query Items", "CA", "UNDEF", "UNDEF", "UNDEF");
//
//	// Read in configuration data from HTTP request of xml file  
//	Document config = getConfig();  
//	if (config == null)
//	{
//		this.rootNode = root;	
//		this.refresh();
//		return root;
//	}
//	  
//	if (inputFlag == 1)
//	{
//		List tables = config.getRootElement().getChildren("table");
//		Iterator tableIterator = tables.iterator();
//		
//		while(tableIterator.hasNext())
//		{
//			Element table = (org.jdom.Element) tableIterator.next();
//			String name = table.getChild("name").getText();
//			String tableName = table.getChild("tableName").getText();
//			String status = table.getChild("status").getText();
//			String description = table.getChild("description").getText();
//			String lookupDB = table.getChild("lookupDB").getText();
//			String webserviceName = table.getChild("webserviceName").getText();
//			
//			if(System.getProperty("selectservice") == null && webserviceName != null) {
//				System.setProperty("selectservice", webserviceName);
//			}
//			
//			root.addChild(new TreeNode(0, name, name, "CA", tableName, lookupDB, webserviceName)
//				 .addChild(new TreeNode(1, "working...", "working...", "LAO", tableName, lookupDB, webserviceName)));			
//		}		
//	}
	
	  // original hardcoded list of tree elements
/**	  if (inputFlag == 1)
	  {
		  root.addChild(new TreeNode(0,"Demographics", "Demographics", "CA", "DEMOGRAPHICS")
				  .addChild(new TreeNode(1, "working....",
						  "working....", "LAO", "DEMOGRAPHICS")));   
			  	
		  root.addChild(new TreeNode(0,"Diagnoses", "Diagnoses", "CA", "DIAGNOSES")
				  .addChild(new TreeNode(1, "working....",
	            								"working....", "LAO", "DIAGNOSES")));    
			  
		  root.addChild(new TreeNode(0,"Encounters", "Encounters", "CA", "ENCOUNTERS")
				  .addChild(new TreeNode(1, "working....",
						  "working....", "LAO", "ENCOUNTERS")));    
		  
		  
		  root.addChild(new TreeNode(0,"Laboratory Tests", "Laboratory Tests", "CA", "LABTESTS")
				  .addChild(new TreeNode(1, "working....",
						  "working....", "LAO","LABTESTS")));   
			  
		  root.addChild(new TreeNode(0,"Medications", "Medications", "CA", "MEDICATIONS")
				  .addChild(new TreeNode(1, "working....",
						  "working....", "LAO", "MEDICATIONS")));    
		  
		  root.addChild(new TreeNode(0,"Microbiology", "Microbiology", "CA", "MICROBIOLOGY")
				  .addChild(new TreeNode(1, "working....",
						  "working....", "LAO", "MICROBIOLOGY")));   
			  
		  root.addChild(new TreeNode(0,"Procedures", "Procedures", "CA", "PROCEDURES")
				  .addChild(new TreeNode(1, "working....",
						  "working....", "LAO", "PROCEDURES")));    
			  		  
		  root.addChild(new TreeNode(0,"Providers", "Providers", "CA", "PROVIDERS")
				  .addChild(new TreeNode(1, "working....",
						  "working....", "LAO", "PROVIDERS")));
			  
		  root.addChild(new TreeNode(0,"Transfusion Services", "Transfusion Services", "CA", "TRANSFUSIONS")
				  .addChild(new TreeNode(1, "working....",
						  "working....", "LAO", "TRANSFUSIONS")));   
			  
		  root.addChild(new TreeNode(0,"i2b2", "i2b2", "CA", "i2b2")
				  .addChild(new TreeNode(1, "working....",
						  "working....", "LAO", "i2b2"))); 
		  
	  }

	  this.rootNode = root;
      return root;
  }
  **/
  
  // lcp Old select service version
  
  // snm - Acquires configuration data via properties variable
  // Returns as JDOM document
//  private Document getConfig(){
//		String responseBody = "";
//		
//		try{
//			responseBody = System.getProperty("ExplorerConfigurationXML");
//		}catch(Exception e){
//	//		e.printStackTrace();
//	//		System.out.println(e.getMessage());
//			System.setProperty("statusMessage", e.getMessage());
//		}finally{
//
//		}
//	//	System.out.println(responseBody);
//		Document responseDoc = null;
//		try {
//			SAXBuilder parser = new SAXBuilder();
//			responseDoc = parser.build(new java.io.StringReader(responseBody));
//		} catch (JDOMException e) {
//		//	System.out.println(e.getMessage());
//			System.setProperty("statusMessage", e.getMessage());
//			//e.printStackTrace();
//		} catch (IOException e) {
//		//	System.out.println(e.getMessage());
//			System.setProperty("statusMessage", e.getMessage());
//			//e.printStackTrace();
//		}		
//		return(responseDoc);
//	}
  // snm - old routine Acquires configuration data via Http call
  // Returns as JDOM document
/* private Document getConfigEx(){
		String responseBody = "";
		HttpClient client = new HttpClient();
//		client.getHttpConnectionManager().getParams().setConnectionTimeout 
//(30000);
		//GetMethod get = new GetMethod("http://localhost/queryToolConfig/contents.xml");
		GetMethod get = new GetMethod("http://localhost/queryToolConfig/contents.xml");
//		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new  
//									DefaultHttpMethodRetryHandler(3, false));
		
		try{
			int resultCode = client.executeMethod(get);
			responseBody = get.getResponseBodyAsString();
		}catch(Exception e){
	//		e.printStackTrace();
	//		System.out.println(e.getMessage());
			System.setProperty("statusMessage", e.getMessage());
		}finally{
			get.releaseConnection();
		}
	//	System.out.println(responseBody);
		Document responseDoc = null;
		if(responseBody.contains("Not Found"))
		{
			System.setProperty("statusMessage", "Query tool config file contents.xml cannot be found");
			return responseDoc;
		}
		try {
			SAXBuilder parser = new SAXBuilder();
			responseDoc = parser.build(new java.io.StringReader(responseBody));
		} catch (JDOMException e) {
		//	System.out.println(e.getMessage());
			System.setProperty("statusMessage", e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
		//	System.out.println(e.getMessage());
			System.setProperty("statusMessage", e.getMessage());
			//e.printStackTrace();
		}		
		return(responseDoc);
	}*/
} 







