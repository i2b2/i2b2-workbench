/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 	     Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.find;

import java.util.Iterator;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.StringUtil;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.GetModifiersResponseMessage;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetModifiersType;
import edu.harvard.i2b2.ontclient.datavo.vdo.MatchStrType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifierType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifiersType;
import edu.harvard.i2b2.ontclient.datavo.vdo.VocabRequestType;

public class ModifierComposite //extends Composite
{
	private Log log = LogFactory.getLog(ModifierComposite.class.getName());	
	private Tree tree;
	private TreeViewer viewer;
	private ImageRegistry imageRegistry;
	//	private List<String> list = new ArrayList();;
	private Label findLabel;

	private String findText = null;

	private String match;
	private Button findButton;

	private static ModifierComposite instance;
	private static ModifierComposite codeInstance;
	private TreeNode currentNode;
	//	private TreeData currentData;
	private TreeNode rootNode;
	private Group compositeFind = null;
	private Composite compositeLabel = null;
	private TreeNode conceptNode;
	private int result;

	private Composite parent;

	public static final String OS = System.getProperty("os.name").toLowerCase();

	public static void setInstance(Composite composite) {
		instance = new ModifierComposite(composite);
	}

	public static ModifierComposite getInstance() {
		return instance;
	}
	
	public static void setCodeInstance(Composite composite) {
		codeInstance = new ModifierComposite(composite);
	}

	public static ModifierComposite getCodeInstance() {
		return codeInstance;
	}

	public Composite getParent(){
		return parent;
	}
	
	public String getNodeKey(){
		return conceptNode.getData().getKey();
	}

	private ModifierComposite(Composite composite)
	{
		//	super(composite, SWT.NONE); 
		parent = composite;

	}

	public void disableComposite(){
		if ((compositeFind != null) && (!compositeFind.isDisposed())) {
			compositeFind.dispose();
			compositeLabel.dispose();
			parent.layout(true);
		}
	}
	
	
	public void enableComposite(TreeNode concept){
		if ((compositeLabel != null) && (!compositeLabel.isDisposed())) {
			conceptNode = concept;
			findLabel.setText("Find associated modifiers for: " + concept.getData().getName());
			//		findLabel.redraw();
			findLabel.pack(true);
			//	findLabel.setVisible(true);
			//	compositeFind.setEnabled(true);
			compositeLabel.redraw();
		}

		else{
			conceptNode = concept;
			compositeFind = new Group(parent, SWT.NONE);
			compositeFind.setText("Modifier Search");

			GridData fromTreeGridData = new GridData (GridData.FILL_BOTH);
			fromTreeGridData.grabExcessHorizontalSpace = true;
			fromTreeGridData.grabExcessVerticalSpace = true;
			fromTreeGridData.horizontalSpan = 2;
			compositeFind.setLayoutData(fromTreeGridData);
			GridLayout gridLayout = new GridLayout(2, false);
			compositeFind.setLayout(gridLayout);

			compositeLabel = new Composite(compositeFind, SWT.NONE);
			GridData labelData = new GridData (GridData.FILL_HORIZONTAL);
			labelData.grabExcessHorizontalSpace = true;
			//	labelData.grabExcessVerticalSpace = true;
			labelData.horizontalSpan = 2;

			compositeLabel.setLayoutData(labelData);
			//		GridLayout gridLayout2 = new GridLayout(1, false);
			//		compositeFind.setLayout(gridLayout2);
			findLabel = new Label (compositeLabel, SWT.NONE);
			findLabel.setText("Find associated modifiers for: " + concept.getData().getName());
			findLabel.pack(true);

			//		new Label (compositeFind, SWT.NONE);

			imageRegistry= new ImageRegistry();
			createImageRegistry();

			//	First Set up the match combo box
			final Combo matchCombo = new Combo(compositeFind,SWT.READ_ONLY);

			matchCombo.add("Starting with");
			matchCombo.add("Ending with");
			matchCombo.add("Containing");
			matchCombo.add("Exact");

			// set default category
			matchCombo.setText("Containing");
			match = "Containing";

			matchCombo.addSelectionListener(new SelectionListener(){
				public void widgetSelected(SelectionEvent e) {
					// Item in list has been selected
					match = matchCombo.getItem(matchCombo.getSelectionIndex());
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					// this is not an option (text cant be entered)
				}
			});

			// Then set up the Find text combo box    
			final Combo findCombo = new Combo(compositeFind, SWT.DROP_DOWN);
			GridData findComboData = new GridData (GridData.FILL_HORIZONTAL);
	//		findComboData.widthHint = 200;
			findComboData.horizontalSpan = 1;
			findCombo.setLayoutData(findComboData);
			findCombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {	    
					// Text Item has been entered
					// Does not require 'return' to be entered
					findText = findCombo.getText();
				}
			});

			findCombo.addSelectionListener(new SelectionListener(){
				public void widgetSelected(SelectionEvent e) {
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					findText = findCombo.getText();
					if(findCombo.indexOf(findText) < 0) {
						findCombo.add(findText);
					}
					rootNode.getChildren().clear();
					//					System.setProperty("statusMessage", "Calling WebService");
					TreeNode placeholder = new TreeNode(1, "placeholder", "working...", "C-UNDEF");
					ModifierType  placeholderModifier = new ModifierType();
					placeholderModifier.setName("working");
					placeholderModifier.setVisualattributes("C-UNDEF");
					placeholder.getData().setModifier(placeholderModifier);
					rootNode.addChild(placeholder);
					viewer.refresh();

					getFindModifierData("name", findText, match).start();
					
					// can only run query via the find buttons now.
				}
			});

			final Composite buttonComp = new Composite(compositeFind, SWT.NONE);

			GridLayout buttonLayout =  new GridLayout();
			buttonLayout.horizontalSpacing = 10;
		//	buttonLayout.makeColumnsEqualWidth = true;
			buttonLayout.numColumns = 3;

			buttonComp.setLayout(buttonLayout);		
			GridData buttonData = new GridData (GridData.FILL_HORIZONTAL);
			buttonData.horizontalSpan = 2;
	//		buttonData.grabExcessHorizontalSpace = true;
			buttonComp.setLayoutData(buttonData);

			// Next include 'Find Name' Button
			findButton = new Button(buttonComp, SWT.PUSH);
			findButton.setText("Find Name");
			GridData findButtonData = new GridData (GridData.FILL_HORIZONTAL);
			if (OS.startsWith("mac"))	
				findButtonData.widthHint = 120;
			else
				findButtonData.widthHint = 80;
			findButton.setLayoutData(findButtonData);
			findButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					// Add item to findCombo drop down list if not already there
					if(findText == null)
					{
						return;
					}
					if(findCombo.indexOf(findText) < 0) {
						findCombo.add(findText);
					}
					if(findButton.getText().equals("Find Name"))
					{	    			
						rootNode.getChildren().clear();
						//					System.setProperty("statusMessage", "Calling WebService");
						TreeNode placeholder = new TreeNode(1, "placeholder", "working...", "C-UNDEF");
						ModifierType  placeholderModifier = new ModifierType();
						placeholderModifier.setName("working");
						placeholderModifier.setVisualattributes("C-UNDEF");
						placeholder.getData().setModifier(placeholderModifier);
						rootNode.addChild(placeholder);
						viewer.refresh();

						getFindModifierData("name", findText, match).start();
						//				findButton.setText("Cancel");
					}
					else
					{
						//				System.setProperty("statusMessage", "Canceling WebService call");
						viewer.refresh();
						//			browser.stopRunning = true;
						findButton.setText("Find Name");
					}
				}
			});	  

			// Next include 'Find Code' Button
			final Button findButton2 = new Button(buttonComp, SWT.PUSH);
			findButton2.setText("Find Code");

			findButton2.setLayoutData(findButtonData);
			findButton2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					// Add item to findCombo drop down list if not already there
					if(findText == null)
					{
						return;
					}
					if(findCombo.indexOf(findText) < 0) {
						findCombo.add(findText);
					}
					if(findButton2.getText().equals("Find Code"))
					{	    			
						//	    			browser.flush();
						rootNode.getChildren().clear();
						TreeNode placeholder = new TreeNode(1, "placeholder", "working...", "C-UNDEF");
						ModifierType  placeholderModifier = new ModifierType();
						placeholderModifier.setName("working");
						placeholderModifier.setVisualattributes("C-UNDEF");
						placeholder.getData().setModifier(placeholderModifier);
						rootNode.addChild(placeholder);
						viewer.refresh();

						getFindModifierData("code", findText, match).start();
						//				findButton2.setText("Cancel");
					}
					else
					{
						//	System.setProperty("statusMessage", "Canceling WebService call");
						viewer.refresh();
						//		browser.stopRunning = true;
						findButton2.setText("Find Code");
					}
				}
			});	    

			final Button findButton3 = new Button(buttonComp, SWT.PUSH);
			findButton3.setText("Get All");

			findButton3.setLayoutData(findButtonData);
			findButton3.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if(findButton3.getText().equals("Get All"))
					{	    			
						findCombo.setText("");
						findCombo.clearSelection();
						findText = null;
						rootNode.getChildren().clear();
						TreeNode placeholder = new TreeNode(1, "placeholder", "working...", "C-UNDEF");
						ModifierType  placeholderModifier = new ModifierType();
						placeholderModifier.setName("working");
						placeholderModifier.setVisualattributes("C-UNDEF");
						placeholder.getData().setModifier(placeholderModifier);
						rootNode.addChild(placeholder);
						viewer.refresh();
						getAllModXMLData(viewer).start();

						//				findButton.setText("Cancel");
					}
					else
					{
						//	System.setProperty("statusMessage", "Canceling WebService call");
						viewer.refresh();
						//		browser.stopRunning = true;
						findButton3.setText("Get All");
					}
				}
			});	    

			tree = new Tree(compositeFind, SWT.MULTI | SWT.BORDER);
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			gridData.heightHint = 100;
			gridData.grabExcessHorizontalSpace = true;
			//	gridData.grabExcessVerticalSpace = true;
			tree.setLayoutData(gridData);

			createTreeViewer();
			Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

			viewer.addDragSupport(DND.DROP_COPY, types, new NodeDragListener(this.viewer));      
			parent.layout(true);
		//	parent.pack(true);
		//	parent.redraw();
			//  browser = new NodeBrowser(compositeFind, 1, findButton, slm);
		}
	}

	private void createImageRegistry()
	{
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifier_small.png");
		this.imageRegistry.put("modifier", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierFolder.png");
		this.imageRegistry.put("modifierFolder", imageDescriptor);
		imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/modifierContainer.png");
		this.imageRegistry.put("modifierContainer", imageDescriptor);

		//	  imageDescriptor = ImageDescriptor.createFromFile(getClass(), "icons/xyz.jpg");
		//	  this.imageRegistry.put("error", imageDescriptor);
	}

	private void createTreeViewer()
	{	

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

				}
				return ((TreeNode)element).getData().getModifier().getName();
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
				String visualAttribute = node.getData().getModifier().getVisualattributes();
				if((visualAttribute.startsWith("D")) || (visualAttribute.startsWith("O")))
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
					/*				if(event.button == 3) // right click
					{
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
						if (selection.size() != 1)
							return;

						TreeNode node =  (TreeNode) selection.getFirstElement();
						setCurrentNode(node);
						if(node.getData().getModifier().getVisualattributes().substring(2).equals("E")){							

							if	((node.getData().getModifier().getVisualattributes().startsWith("O"))
									|| (node.getData().getModifier().getVisualattributes().startsWith("D"))){
								lockedMenu.setVisible(false);
								modAllMenu.setVisible(true);
								modItemMenu.setVisible(false);

							}
							else if	((node.getData().getModifier().getVisualattributes().startsWith("R"))){
								modAllMenu.setVisible(false);
								modItemMenu.setVisible(true);
								lockedMenu.setVisible(false);
							}

							else{
								lockedMenu.setVisible(false);
								modAllMenu.setVisible(false);
								modItemMenu.setVisible(false);
							}

						}
						else{
							modAllMenu.setVisible(false);
							modItemMenu.setVisible(false);
							lockedMenu.setVisible(true);
						}
					} */


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

		//		MetadataRecord.getInstance().registerModifierViewer(viewer);
	}
	public void setCurrentNode(TreeNode node)
	{
		this.currentNode = node;
		//		this.currentData = node.getData();
	}



	public void refresh()
	{
		this.viewer.refresh();
	}

	public void refreshNode()
	{
		//		currentNode.getData().getModifier().setName(MetadataRecord.getInstance().getMetadata().getModifier().getName());
		this.viewer.refresh(currentNode);
	}

	public Thread getFindModifierData(final String type, String phrase, String match) {
		final Display theDisplay = Display.getCurrent();
		final TreeViewer theViewer = this.viewer;
		final String lookupPhrase = phrase;
		final String lookupOperator = match;
		final String lookupType = type;
		return new Thread() {
			public void run() {						
				rootNode.getChildren().clear();

				findModifierNodes(lookupType, lookupPhrase, lookupOperator, theDisplay, theViewer);

				theDisplay.syncExec(new Runnable() {
					public void run() {
						if (rootNode.getChildren().size() == 0)
						{	
							ModifierType modifier = new ModifierType();
							modifier.setName("There were no matches");
							modifier.setVisualattributes("C-UNDEF");							
							TreeNode placeholder = new TreeNode(1, "placeholder", "There were no matches", "C-UNDEF");
							placeholder.getData().setModifier(modifier);
							rootNode.addChild(placeholder);
						}	
						theViewer.refresh();
					}
				});
			}
		};
	}

	private void findModifierNodes(String type, String phrase, String operator, Display display, TreeViewer viewer) {
		VocabRequestType vocabData = new VocabRequestType();

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
		vocabData.setSynonyms(Boolean.parseBoolean(System.getProperty("OntFindSynonyms")));
	//	vocabData.setSelf(ModifierComposite.getInstance().getNodeKey());
		vocabData.setSelf(getNodeKey());
		List modifiers = getModifierNodes(type, vocabData, display, viewer);
		getModifierNodesFromXMLString(modifiers);

	}

	private List getModifierNodes(String type, VocabRequestType vocabData, Display theDisplay, final TreeViewer theViewer) {

		OntologyResponseMessage msg = new OntologyResponseMessage();
		StatusType procStatus = null;	
		try {
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				String response = null;
				if(type.equals("name"))
					response = OntServiceDriver.getModifierNameInfo(vocabData, "FIND");
				else
					response = OntServiceDriver.getModifierCodeInfo(vocabData, "FIND");

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
					//		this.stopRunning = true;
					return null;
				}		
				else
					procStatus.setType("DONE");
			}


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
			//		this.stopRunning = true;
			return null;
		}
		ModifiersType allModifiers = msg.doReadModifiers();   	 
		if(allModifiers == null)
			return null;
		List modifiers = allModifiers.getModifier();
		return modifiers;
	}

	public void getModifierNodesFromXMLString(List<ModifierType> modifiers){
		if(!modifiers.isEmpty()){
			Iterator<ModifierType> it = modifiers.iterator();

			while(it.hasNext()){
				ConceptType child = conceptNode.getData();
				TreeData data = new TreeData(child);
				data.setModifier(	(ModifierType)it.next());
				TreeNode childNode = new TreeNode(data);

				if((data.getModifier().getVisualattributes().startsWith("DA")) || (data.getModifier().getVisualattributes().startsWith("OA")))  
				{
					TreeNode placeholder = new TreeNode(data.getModifier().getLevel() + 1, "working...", "working...", "RA");
					placeholder.setOpen(true);
					ModifierType modifier = new ModifierType();
					modifier.setName("working...");
					modifier.setVisualattributes("RA");
					placeholder.getData().setModifier(modifier);
					childNode.addChild(placeholder);
					//			if(childNode.getData().getModifier().getLevel() == 1)
					rootNode.addChild(childNode);
					//			else
					//				currentNode.addChild(childNode);
				}
				else if	((data.getModifier().getVisualattributes().startsWith("DH")) || (data.getModifier().getVisualattributes().startsWith("OH")))
				{
					TreeNode placeholder = new TreeNode(data.getModifier().getLevel() + 1, "working...", "working...", "RH");
					placeholder.setOpen(true);
					ModifierType modifier = new ModifierType();
					modifier.setName("working...");
					modifier.setVisualattributes("RA");
					placeholder.getData().setModifier(modifier);
					childNode.addChild(placeholder);
					//			if(childNode.getData().getModifier().getLevel() == 1)
					rootNode.addChild(childNode);
					//			else
					//				currentNode.addChild(childNode);
				}
				else if((data.getModifier().getVisualattributes().startsWith("R"))){
					//		if(childNode.getData().getModifier().getLevel() == 1)
					rootNode.addChild(childNode);
					//		else
					//			currentNode.addChild(childNode);
				}	
			}
		}


	}


	public Thread getAllModXMLData(TreeViewer viewer) {
		final TreeViewer theViewer = viewer;
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			@Override
			public void run(){
				try {
					updateModifiers(theDisplay, theViewer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//				System.setProperty("statusMessage", e.getMessage());					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						//	theViewer.expandToLevel(theNode, 1);
						theViewer.refresh(rootNode);
					}
				});
			}
		};
	}

	public void updateModifiers(final Display theDisplay, final TreeViewer theViewer) 
	{
		String path = StringUtil.getPath(conceptNode.getData().getKey());
		String tableCd = StringUtil.getTableCd(conceptNode.getData().getKey());
		//	getChildren().clear();
		//		while (path.length()>3){
		GetModifiersType nodeType = new GetModifiersType();

		nodeType.setBlob(false);
		nodeType.setSelf("\\\\"+tableCd +  path);		
		nodeType.setType("core");


		GetModifiersResponseMessage msg = new GetModifiersResponseMessage();
		StatusType procStatus = null;	
		while(procStatus == null || !procStatus.getType().equals("DONE")){
			String response = OntServiceDriver.getModifiers(nodeType, "FIND");
			// case where server is at pre-1.6 version
			// ignore the response and continue
			if(response == null){
				rootNode.getChildren().clear();
				return;
			}
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
				rootNode.getChildren().clear();
				return;
			}			
		}
		rootNode.getChildren().clear();
		ModifiersType allModifiers = msg.doReadModifiers();   
		ConceptsType concepts = new ConceptsType();
		if (allModifiers != null){
			// convert list of modifiers to list of concepts
			List<ModifierType> modifiers = allModifiers.getModifier();
			getModifierNodesFromXMLString(modifiers);
		}	

	}
}






