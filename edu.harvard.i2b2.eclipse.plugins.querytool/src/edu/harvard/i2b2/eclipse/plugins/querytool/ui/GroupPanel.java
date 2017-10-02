/*
 * Copyright (c) 2006-2017 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IGroupBindingPolicyProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.ConceptDroppedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.GroupDroppedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.VisualActivationListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.GroupPanelConceptDragHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.GroupPanelConceptDropHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.GroupPanelGroupDragHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.GroupPanelGroupDropHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.task.TermFetchRunnable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.MixedTextIconPanel;
import edu.harvard.i2b2.eclipse.plugins.querytool.utils.DefaultDaemonThreadFactory;
import edu.harvard.i2b2.eclipse.plugins.querytool.views.QueryToolViewAccessor;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeFactoryProduct;
import edu.harvard.i2b2.query.data.WorkingTreeNodeData;
import edu.harvard.i2b2.query.data.processor.ProcessorConst;

public class GroupPanel extends QueryToolPanelComposite implements UIConst, DataChangedListener, ConceptDroppedDelegator, GroupDroppedDelegator, VisualActivationListener
{	
	private ScheduledExecutorService 	myTermFetchScheduler 	= Executors.newScheduledThreadPool( 3, new DefaultDaemonThreadFactory("TermFetchDaemonRunner") );	// display indicator so users don't think it's stuck
	
	protected Composite 				myMainComposite;
	protected Composite				myViewerComposite;
	protected Composite				myBindingComposite;
		protected Label					myBindingImageLabel;
		protected Label					myBindingTextLabel;	

	protected Composite				titleComp;
	protected Label 					titleLabel;

	protected Label						myPreTreeLabel;
	protected TreeViewer 				myTreeViewer;
	protected MenuManager				myMenuManager;

	protected Label 					myCloseLabel;

	protected GroupBindingDisplay		myBindingDisplay;
	protected DateConstraintDisplay 	myDateDisplay;
	protected NumberConstraintDisplay myNumberDisplay;

	protected GroupManager			myGroupManager;

	protected Group					myGroup;
	protected int					myRowCount = 0; // keep the number of Rows of Items the GroupPanel currently has
	
	protected QueryToolPanelComposite	myCurrentlySelectedDisplay = null;	// keep track of which of {myBindingDisplay, myDateDisplay, myNumberDisplay} is selected by user

	protected HashSet<TermFetchRunnable> myTermFetchRunnables;

	// This constructor should only be used for testing purpoese
	protected GroupPanel(Composite parent, int style )
	{
		super(parent, style | SWT.BORDER);
		myGroup = new Group( GROUP );
		myGroup.addDataChangedListener( this );
		myGroup.setGroupBindingPolicyProvider( IGroupBindingPolicyProvider.ONLY_INSTANCE ); // try a different policy
		myMenuManager 		= new MenuManager();
		myTermFetchRunnables= new HashSet<TermFetchRunnable>();
		setupUI();
		attachListeners();
	}

	// create a new group with this GroupPanel
	public GroupPanel(Composite parent, int style, GroupManager manager, String name, boolean areDatesEnabled, GregorianCalendar startDate, GregorianCalendar endDate, int number, int op )
	{
		super(parent, style | SWT.BORDER);
		myGroupManager = manager;		
		myGroup = new Group( name );
		myGroup.addDataChangedListener( this );
		myMenuManager 		= new MenuManager();
		myTermFetchRunnables= new HashSet<TermFetchRunnable>();
		setupUI();
		this.myDateDisplay.setStartDate( startDate );
		this.myDateDisplay.setEndDate( endDate );
		this.myDateDisplay.setActive( areDatesEnabled );
		this.myNumberDisplay.setNumber( number );
		this.myNumberDisplay.setOperator( op );
		attachListeners();
	}

	// use an existing Group with this GrouPpanel
	public GroupPanel(Composite parent, int style, Group group, GroupManager manager, String name, boolean areDatesEnabled, GregorianCalendar startDate, GregorianCalendar endDate, int number, int op )
	{
		super(parent, style | SWT.BORDER);
		myGroupManager = manager;
		myGroup = group;
		myGroup.setName( name );
		myRowCount =  myGroup.getTerms().size();
		myGroup.addDataChangedListener( this );	
		myMenuManager 		= new MenuManager();
		myTermFetchRunnables = new HashSet<TermFetchRunnable>();
		setupUI();
		this.myDateDisplay.setStartDate( startDate );
		this.myDateDisplay.setEndDate( endDate );
		this.myDateDisplay.setActive( areDatesEnabled );
		this.myNumberDisplay.setNumber( number );
		this.myNumberDisplay.setOperator( op );
		this.autoSetGroupTree();
		attachListeners();
	}

	protected void setupUI() 
	{
		this.setLayout( new FormLayout() );
		
		Point singleCharSize = UIUtils.getDefaultCharSize(this);
		
		// set up title
		titleComp = new Composite( this, SWT.NONE );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleComp.setLayoutData( titleCompFD );

		titleLabel = new Label( titleComp, SWT.None );
		titleLabel.setText( myGroup.getName() );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 0, 0, myNumberDisplay, 0));

		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );

		// set up close button
		myCloseLabel = new Label( titleComp, SWT.CENTER );
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			myCloseLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE_DISABLED) );
		}
		else		
		{
			myCloseLabel.setText("X ");
			myCloseLabel.setForeground( Colors.DARK_RED );
		}
		
		myCloseLabel.setLayoutData( FormDataMaker.makeFormData( 50, -myCloseLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, (Integer)null, 0, 100, 0 ) );
		// adjust title height to at most button height
		// titleCompFD.height = Math.max( MINOR_TITLE_HEIGHT, myCloseLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		if ( !QueryToolRuntime.getInstance().isTitleHeightSet() )
			QueryToolRuntime.getInstance().setGroupTitleHeight( Math.max( MINOR_TITLE_HEIGHT, myCloseLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y) );
		titleCompFD.height = QueryToolRuntime.getInstance().getGroupTitleHeight();
			
		//set up date constraint Display and Number of Occurrence display
		myNumberDisplay = new NumberConstraintDisplay( titleComp, myGroup, SWT.None );
		FormData numberDisplayFD 	= FormDataMaker.makeFormData( 0, 0, 100, 0, (Integer)null, 0, myCloseLabel, 0);
			numberDisplayFD.width	= NUM_CHARS_IN_NUMBER_DISPLAY * singleCharSize.x;
		myNumberDisplay.setLayoutData( numberDisplayFD );
				
		myDateDisplay = new SkinnyDateConstraintDisplay( titleComp, myGroup, SWT.None );
		FormData dateDisplayFD 		= FormDataMaker.makeFormData( 0, 0, 100, 0, (Integer)null, 0, myNumberDisplay, 1);
			dateDisplayFD.width 	= NUM_CHARS_IN_DATE_DISPLAY * singleCharSize.x;
		myDateDisplay.setLayoutData( dateDisplayFD );
		myDateDisplay.setActive( true );
		
		myBindingDisplay = new GroupBindingDisplay( titleComp, SWT.NONE, this.myGroup );
			FormData bindingDisplayFD 		= FormDataMaker.makeFormData( 0, 0, 100, 0, (Integer)null, 0, myDateDisplay, 1);
		myBindingDisplay.setLayoutData( bindingDisplayFD );
		myBindingDisplay.setActive( true );
				
		// add this as Displays' VisualActivationListener so that when a Display is clicked, it stays 'activated'
		myNumberDisplay.addVisualActivationListener( this );
		myDateDisplay.addVisualActivationListener( this );
		myBindingDisplay.addVisualActivationListener( this );		
		
		myMainComposite = new Composite( this, SWT.NONE );
		myMainComposite.setLayout( new FormLayout() );
		myMainComposite.setLayoutData( FormDataMaker.makeFormData( titleComp, 100, 0, 100));
		myMainComposite.setBackground( Colors.WHITE );
		
		// label and viewerComposite occupy the same space.
		ArrayList<Object> contents = new ArrayList<Object>();
		contents.add( DRAG_ONLOTOGY_TERMS_HERE1 );

		//myPreTreeLabel = new MixedTextIconPanel( myMainComposite, SWT.CENTER | SWT.WRAP, contents, Colors.GRAY, Colors.WHITE  );
		myPreTreeLabel = new Label(myMainComposite, SWT.CENTER | SWT.WRAP );
		myPreTreeLabel.setText( DRAG_ONLOTOGY_TERMS_HERE1 );
		myPreTreeLabel.setForeground(  Colors.GRAY );
		myPreTreeLabel.setBackground( myPreTreeLabel.getParent().getBackground() );
		
		Point labelSize = myPreTreeLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		//myPreTreeLabel.setLayoutData( FormDataMaker.makeFormData( (Integer)null, 0, 100, -4, 20, 0, 80, 0));
		myPreTreeLabel.setLayoutData( FormDataMaker.makeFormData( 50, -labelSize.y/2, (Integer)null, 0, 50, -labelSize.x/2, (Integer)null, 0));
		
		myViewerComposite = new Composite( myMainComposite, SWT.NONE );
		myViewerComposite.setLayout( new FormLayout() );
		myViewerComposite.setLayoutData( FormDataMaker.makeFullFormData() );
		
		myTreeViewer = new TreeViewer( myViewerComposite, SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL );
		myTreeViewer.getTree().setLayoutData( FormDataMaker.makeFullFormData() );
		
		// initialize visibility of treeviewer and label
		myPreTreeLabel.setVisible( true );
		myViewerComposite.setVisible( false );
		
		myTreeViewer.setContentProvider( new GroupPanelConceptContentProvider() );
		myTreeViewer.setLabelProvider(  new GroupPanelConceptLabelProvider() );
		myTreeViewer.setInput( myGroup.getTreeData() );
		
		ColumnViewerToolTipSupport.enableFor(myTreeViewer);
	}

	// Listener additions are broken into several methods so subclasses can choose which ones to use, omit, or override
	protected void attachListeners()
	{
		addCloseButtonListener();		 // add listener to listen for actions and mouse hovers to the CloseLabel
		addConceptDragDropHandlers();	 // Add listener for Dragging/Dropping concepts
		addGroupPanelGroupDragHandler(); // Add Dragging actions to drag a Group out
        addGroupPanelGroupDropHandler(); // Add Dropping actions to drop a Group to titleComp and titleLabel
        addContextualMenus();			 // Add Contextual popup menu
        addDoubleClickListenerToViewer();// Add detection of double-clicking on an item to fetch children from ONT	
        addTreeListenerToViewer();		 // Add listener for tree expansion/collapse
	}

	protected void addCloseButtonListener()
	{
		// close button removes the panel
		myCloseLabel.addMouseListener( new MouseAdapter()
		{
			public void mouseUp(MouseEvent e) 
			{				
				myGroupManager.removePanel( GroupPanel.this );
			}
		});
		
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			myCloseLabel.addMouseTrackListener( new MouseTrackListener()
			{
				@Override
				public void mouseEnter(MouseEvent e) 
				{ myCloseLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE ) ); }
				@Override
				public void mouseExit(MouseEvent e) 
				{ myCloseLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE_DISABLED ) ); }
				@Override
				public void mouseHover(MouseEvent e) {}
			});
		}
	}

	protected void addConceptDragDropHandlers()
	{
		// Add Dropping actions to accept Concept dropping
		DropTarget target = new DropTarget( myMainComposite, UIConst.DND_DROP_OPS );
		target.setTransfer( UIConst.DND_TRANSFER_TYPES );
		target.addDropListener( new GroupPanelConceptDropHandler( this ) );

		// Add Dragging actions to drag Concepts out
		DragSource source = new DragSource( myTreeViewer.getTree(), UIConst.DND_DRAG_OPS );
		source.setTransfer( UIConst.DND_TRANSFER_TYPES );
		source.addDragListener(new GroupPanelConceptDragHandler( this.myTreeViewer ));
	}

	protected void addGroupPanelGroupDragHandler()
	{
		GroupPanelGroupDragHandler groupDragHandler = new GroupPanelGroupDragHandler( this.myGroup );
		DragSource titleCompSource = new DragSource( titleComp, UIConst.DND_DRAG_OPS );
		titleCompSource.setTransfer( UIConst.DND_TRANSFER_TYPES );
		titleCompSource.addDragListener( groupDragHandler );
		
		DragSource titleLabelSource = new DragSource( titleLabel, UIConst.DND_DRAG_OPS );
		titleLabelSource.setTransfer( UIConst.DND_TRANSFER_TYPES );
		titleLabelSource.addDragListener( groupDragHandler );
	}

	protected void addGroupPanelGroupDropHandler()
	{
		GroupPanelGroupDropHandler groupDropHandler = new GroupPanelGroupDropHandler( this.myGroup, this );
		DropTarget titleCompTarget = new DropTarget( titleComp, UIConst.DND_DROP_OPS );
		titleCompTarget.setTransfer( UIConst.DND_TRANSFER_TYPES );
		titleCompTarget.addDropListener( groupDropHandler );
		
		DropTarget titleLabelTarget = new DropTarget( titleLabel, UIConst.DND_DROP_OPS );
		titleLabelTarget.setTransfer( UIConst.DND_TRANSFER_TYPES );
		titleLabelTarget.addDropListener( groupDropHandler );
	}

	protected void addContextualMenus()
	{
        Menu menu = myMenuManager.createContextMenu(myTreeViewer.getControl());
        myMenuManager.addMenuListener(new IMenuListener() 
        {
            @Override
            public void menuAboutToShow(IMenuManager manager) 
            {
                if (myTreeViewer.getSelection().isEmpty())
                    return;
                buildContextMenu();
            }
        });
        myMenuManager.setRemoveAllWhenShown(true);
        myTreeViewer.getControl().setMenu(menu);
	}

	protected void addDoubleClickListenerToViewer()
	{
		myTreeViewer.addDoubleClickListener(new IDoubleClickListener() 
		{
			@Override
			public void doubleClick(DoubleClickEvent event) 
			{
				TreeViewer viewer = (TreeViewer) event.getViewer();
				IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
				QueryConceptTreeNodeData parentNode = (QueryConceptTreeNodeData)thisSelection.getFirstElement();
				
				// if selected node is a working node
				if ( parentNode instanceof WorkingTreeNodeData )
				{
					WorkingTreeNodeData workingNode = (WorkingTreeNodeData)parentNode;
					// if failed or canceled, restart it!
					if ( workingNode.getState() == WorkingTreeNodeData.WorkingTreeNodeState.CANCELED || workingNode.getState() == WorkingTreeNodeData.WorkingTreeNodeState.FAILED )
					{
						parentNode = workingNode.getParent();						
						workingNode.name( WorkingTreeNodeData.DEFAULT_WORKING_LABEL );
						workingNode.visualAttribute( ProcessorConst.ICON_WORKING );
						((WorkingTreeNodeData)workingNode).setState( WorkingTreeNodeData.WorkingTreeNodeState.RUNNING );
						TermFetchRunnable runner = new TermFetchRunnable( GroupPanel.this, viewer, parentNode, workingNode );
						workingNode.setTerMFetchRunnable( runner );
						myTermFetchRunnables.add( runner );
						// try fetching again
						myTermFetchScheduler.schedule( runner, 10, TimeUnit.MILLISECONDS );
						return;
					}
					else if ( workingNode.getState() == WorkingTreeNodeData.WorkingTreeNodeState.RUNNING ) // if it's RUNNING, cancel it!
					{
						workingNode.cancelRunner(); // send cancel signal to the task via workingNode
					}
				}
				if ( !parentNode.isConcept() ) // nothing to expand if concept is not a node
					return;
				// if already has a child, don't fetch children, just expand/contract
				if ( parentNode.getChildren().size() > 0 )
				{
					viewer.setExpandedState(parentNode,!viewer.getExpandedState( parentNode ));	// expand the selected node
					viewer.update(parentNode, null );											// make sure the tree redraws
					autoSetMaxAllowablePanelHeight( parentNode,  viewer.getExpandedState( parentNode ));
					return;
				}

				WorkingTreeNodeData child = null;
				if ( !viewer.getExpandedState(parentNode)) 
				{
					child = new WorkingTreeNodeData(parentNode);
					parentNode.addChild(child);
				}
				if ( child != null )
				{
					viewer.setExpandedState(parentNode,!viewer.getExpandedState(parentNode));	// expand the selected node
					viewer.update(parentNode, null );											// make sure the tree redraws
					autoSetMaxAllowablePanelHeight( parentNode, true );							// expand tree immediate to accommodate the Working Node
					TermFetchRunnable runner = new TermFetchRunnable( GroupPanel.this, viewer, parentNode, child );
					child.setTerMFetchRunnable( runner );
					myTermFetchRunnables.add( runner );
					myTermFetchScheduler.schedule( runner, 10, TimeUnit.MILLISECONDS );
				}
			}
		});
	}

	protected void addTreeListenerToViewer()
	{
		this.myTreeViewer.addTreeListener( new ITreeViewerListener()
		{
			@Override
			public void treeCollapsed(TreeExpansionEvent event) 
			{
				QueryConceptTreeNodeData node = (QueryConceptTreeNodeData)event.getElement();
				autoSetMaxAllowablePanelHeight( node, false );
			}

			@Override
			public void treeExpanded(TreeExpansionEvent event) 
			{
				QueryConceptTreeNodeData node = (QueryConceptTreeNodeData)event.getElement();
				autoSetMaxAllowablePanelHeight( node, true );
			}
		});
	}
	
	protected int getExpandedTreeItemDescendantCount( TreeItem [] children )
	{
		int count = children.length; // add up all top-level nodes
		for ( TreeItem child : children )
			if ( child.getExpanded() )
				count = count + getExpandedTreeItemDescendantCount( child.getItems() );
		return count;
	}

	protected void buildContextMenu()
	{
        if (myTreeViewer.getSelection() instanceof IStructuredSelection) 
        {
            IStructuredSelection selection = (IStructuredSelection) myTreeViewer.getSelection();            
            QueryConceptTreeNodeData node = (QueryConceptTreeNodeData)selection.getFirstElement();
            
            myMenuManager.add( new EditValueAction( node ) );
            myMenuManager.add( new Separator() );
            myMenuManager.add( new DeleteAction( node ) );            	
        }
	}

	protected void removeNodes(List<QueryConceptTreeNodeData> terms )
	{
		cancelAllDescendantWorkingNodes( terms );
		myGroup.removeTerms( terms );
		autoSetGroupTree();
		myTreeViewer.refresh();
	}
	
	// RECURSIVELY cancel working nodes in the descendants
	protected void cancelAllDescendantWorkingNodes( List<QueryConceptTreeNodeData> nodes )
	{
		for ( QueryConceptTreeNodeData node : nodes)
		{
			cancelAllDescendantWorkingNodes( node.getChildren() );
			if ( node instanceof WorkingTreeNodeData )
			{
				((WorkingTreeNodeData) node).cancelRunner(); // if it's still running, the node will be detached from its parent. If it's not running, then it is already detached.
				TermFetchRunnable runnable = ((WorkingTreeNodeData) node).getTermFetchRunnable();
				this.myTermFetchRunnables.remove( runnable );
			}
		}
	}

	public void cancelWorkingNodesAndShutDownExecutor()
	{
		this.cancelAllWorkingNodes();
		this.myTermFetchScheduler.shutdownNow();
	}
	
	// private to package so GroupManager can access it.
	private void cancelAllWorkingNodes()
	{
		for ( TermFetchRunnable runnable : this.myTermFetchRunnables )
			runnable.cancel();	// if it's still running, the node will be detached from its parent. If it's not running, then it is already detached.
		this.myTermFetchRunnables.clear();
	}

	public void autoSetGroupTree()
	{
		if ( myGroup.isContainingTerm() )
		{
			myViewerComposite.setVisible( true );
			myPreTreeLabel.setVisible( false );
		}
		else
		{
			myViewerComposite.setVisible( false );
			myPreTreeLabel.setVisible( true );
		}
	}

	public void setName( String groupName )
	{
		myGroup.setName( groupName );
		Display.getCurrent().asyncExec( new Runnable()
		{

			@Override
			public void run() 
			{				
				titleLabel.setText( myGroup.getName() );
				titleLabel.getParent().layout();
				titleLabel.getParent().redraw();
			}
		});
	}
	
	// data for Date Constraints
	public GregorianCalendar getStartDate()
	{ return this.myDateDisplay.getStartDate(); }	
	public GregorianCalendar getEndDate()
	{ return this.myDateDisplay.getEndDate(); }
	public void useCentralDateConstraint( GregorianCalendar startDate, GregorianCalendar endDate )
	{
		this.myDateDisplay.setStartDate( startDate );
		this.myDateDisplay.setEndDate( endDate );
		this.myDateDisplay.setEnabled( false );
		this.myDateDisplay.updateLabels();
		this.myDateDisplay.redraw();
	}
	public void useGroupSpecificDateConstraint()
	{
		this.myDateDisplay.setEnabled( true );
		this.myDateDisplay.updateLabels();
		this.myDateDisplay.redraw();
	}	

	// data for Number Constraints
	public Integer getOperator()
	{ return this.myNumberDisplay.getOperator(); }
	public Integer getNumber()
	{ return this.myNumberDisplay.getNumber(); }
	
	public String getName()
	{ return myGroup.getName(); }
	
	public int getTermSize()
	{ return myGroup.getTreeData().getNumChildren(); }
	
	public Group getGroupData()
	{ return myGroup; }

	/*  Modification to the underlying Group */	
	// silently setBinding (without notification). Called by GroupManager
	//public void silentlySetBinding( GroupBinding binding  )
	//{ this.myGroup.silentlySetBinding( binding ); }

	public void setBinding( GroupBinding binding  )
	{ this.myGroup.setBinding( binding ); }

	public int	getRowCounts()
	{ return this.myRowCount; }
	
	public void addToRowCounts( int diff ) 
	{ this.myRowCount = this.myRowCount + diff; }

	/*
	public void reComputeRowCounts() // recompute rowCounts because tree has been expanded/collapsed
	{ GroupPanel.this.myRowCount = getExpandedDescendantCount( myGroup.getTerms() ); }
	*/
	
	/*
	 * ConceptDroppedDelegator methods
	 * Handle a concept text dropping onto the myTreeeViewer or the myPreTreeLabel
	 */
	@Override
	public void conceptDropped( QueryConceptTreeNodeFactoryProduct product ) 
	{
		//long tic = System.currentTimeMillis();
		if ( product.hasError() )
		{
			//bugbug: handle errors here
		}
		else
		{
			ArrayList<QueryConceptTreeNodeData> concepts = product.getData();
			myGroup.addTerms( concepts );
			updateTreeVisibility();

			List<QueryConceptTreeNodeData> conceptsWithValueRestrictions = ValueRestrictionEditorFactory.getInstance().getValueRestrictedNodes( concepts );
			if ( !conceptsWithValueRestrictions.isEmpty() )
			{
				// make Dialog
				AbstractValueRestrictionEditorDialog dialog = null;
				
				if ( conceptsWithValueRestrictions.size() == 1 )
					dialog = new SingleValueRestrictionEditorDialog( conceptsWithValueRestrictions.get(0), SWT.RESIZE | SWT.CLOSE );					
				else
					dialog = new ConsolidatedValueRestrictionEditorDialog( conceptsWithValueRestrictions, SWT.RESIZE | SWT.CLOSE );
				
				if ( !dialog.hasContent() ) // don't open dialog if there is nothing to edit
					return;
				
				// try disable workbench
				HashSet<Control> alreadyDisabledControls = new HashSet<Control>();
				try
				{ UIUtils.recursiveSetEnabledAndRememberUnchangedControls( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), false, alreadyDisabledControls ); }
				catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to disable Workbench window when it does not exist (DateConstraintDisplay.attachListener): " + e.toString() ); }
				
				// show dialog
				dialog.open();
				
				// try enable workbench
				try
				{ UIUtils.recursiveSetEnabled( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true, alreadyDisabledControls ); }
				catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to enable Workbench window when it does not exist (DateConstraintDisplay.attachListener): " + e.toString() ); }
			}
			this.myRowCount = this.myRowCount + concepts.size();
			myTreeViewer.refresh( true );
			autoAdjustPanelHeight();
		}
		//System.err.println( "PROFILING: GroupPanel.conceptDropped took " + (System.currentTimeMillis() - tic)/1000f + " sec" );
	}
	
	protected void updateTreeVisibility()
	{
		if (!myViewerComposite.getVisible())
			setTreeVisisble( true );
    	//myTreeViewer.refresh(true);
	}
	
	protected void setTreeVisisble( boolean flag )
	{
		myPreTreeLabel.setVisible( !flag );
		myViewerComposite.setVisible( flag );
	}

	protected void autoAdjustPanelHeight()
	{				
		((FormData)this.getLayoutData()).height = getAdjustedPanelHeight();
		this.getParent().layout( true, true );
		this.myGroupManager.panelLayoutChanged();
	}
	
	protected void autoSetMaxAllowablePanelHeight( QueryConceptTreeNodeData nodeInAction, boolean isExpanded ) // true for expanded, false for collapsed
	{
		autoSetMaxAllowablePanelHeightWithOffset( nodeInAction, isExpanded, 0 );	
	}

	protected void autoSetMaxAllowablePanelHeightWithOffset( QueryConceptTreeNodeData nodeInAction, boolean isExpanded, int offset ) // true for expanded, false for collapsed
	{
		TreeItem item = (TreeItem)myTreeViewer.testFindItem( nodeInAction );
		int sign 	= isExpanded?1:-1;
		int delta 	= sign * getExpandedTreeItemDescendantCount( item.getItems() ) + offset;	
		GroupPanel.this.myRowCount = GroupPanel.this.myRowCount + delta; 
		GroupPanel.this.setMaxAllowablePanelHeight();
	}

	protected int getAdjustedPanelHeight()
	{		
		// at initialization (when GroupPanel is created with an Event, titleComp.getBounds().height is 0. We use max( itemHeight, titleComp.getBounds().height) as an estimate -- mileage varies depending on OS
		return Math.min( myGroupManager.getMaxPanelHeight(), Math.max( myGroupManager.getDefaultPanelHeight(), Math.max( myTreeViewer.getTree().getItemHeight(), titleComp.getBounds().height) + myTreeViewer.getTree().getItemHeight() * getExpandedTreeItemDescendantCount( myTreeViewer.getTree().getItems() ) )) ;
	}

	public void setMaxAllowablePanelHeight() // public so TermFetchRunnable can access it
	{				
		((FormData)this.getLayoutData()).height = getMaxAllowablePanelHeight();
		this.getParent().layout( true, true );
		this.myGroupManager.panelLayoutChanged();
	}

	protected int getMaxAllowablePanelHeight()
	{		
		// at initialization (when GroupPanel is created with an Event, titleComp.getBounds().height is 0. We use max( itemHeight, titleComp.getBounds().height) as an estimate -- mileage varies depending on OS
		return Math.min( (QueryToolViewAccessor.getInstance().getQueryToolView().getMainUI().getClientArea().height-100), Math.max( myGroupManager.getDefaultPanelHeight(), Math.max( myTreeViewer.getTree().getItemHeight(), titleComp.getBounds().height) + myTreeViewer.getTree().getItemHeight() * this.myRowCount ));
	}
	
	@Override /* DataChangedListener method (listen to myGroup when it changes )*/
	public void dataChanged( Object source ) 
	{ 
		if ( !QueryToolRuntime.getInstance().isLaunchedFromWorkbench() ) // if not launching from workbench, do nothing
			return;
		myGroupManager.dataChanged( source ); // simply propagate to the GroupManager
	}


	@Override /* QueryToolPanelComposite */
	protected void setActive( boolean flag )
	{
		//System.err.println("titleComp is enabled? " + titleComp.isEnabled() );
		if ( flag )
		{
			titleComp.setBackground( Colors.CONTROL_TITLE_BG );
			titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		}
		else
		{
			titleComp.setBackground( Colors.CONTROL_TITLE_BG_DISABLED );
			titleLabel.setBackground( Colors.CONTROL_TITLE_BG_DISABLED );
		}
		
		myBindingDisplay.setEnabled( flag );
		myDateDisplay.setEnabled( this.myGroupManager.isUsingGroupSpecificDateConstraint() && flag );
		myNumberDisplay.setEnabled( flag );
		
		if ( this.myCurrentlySelectedDisplay != null )
			this.myCurrentlySelectedDisplay.setActive( true );
	}
	
	
	/*
	 * Classes for Contextual Menu Actions
	 */
	protected class EditValueAction extends Action
	{
		private QueryConceptTreeNodeData myNode = null;
		
		public EditValueAction( QueryConceptTreeNodeData node )
		{ 
			super( EDIT_VALUE );
			myNode = node;
			this.setEnabled( node.hasValue() && !myNode.hasParent() ); // only enable if node has values to set and is a top-level node
		}
		
		@Override
		public void run()
		{
			// make Dialog
			SingleValueRestrictionEditorDialog dialog = new SingleValueRestrictionEditorDialog( myNode, SWT.RESIZE | SWT.CLOSE );
			if ( !dialog.hasContent() ) // don't open dialog if there is nothing to edit
				return;
			
			// try disable workbench
			HashSet<Control> alreadyDisabledControls = new HashSet<Control>();
			try
			{ UIUtils.recursiveSetEnabledAndRememberUnchangedControls( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), false, alreadyDisabledControls ); }
			catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to disable Workbench window when it does not exist (DateConstraintDisplay.attachListener): " + e.toString() ); }
			
			// show dialog
			dialog.open();
			
			// try enable workbench
			try
			{ UIUtils.recursiveSetEnabled( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true, alreadyDisabledControls ); }
			catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to enable Workbench window when it does not exist (DateConstraintDisplay.attachListener): " + e.toString() ); }
			myTreeViewer.refresh( true );
		}
	}

	
	protected class DeleteAction extends Action
	{
		private QueryConceptTreeNodeData myNode = null;
		
		public DeleteAction( QueryConceptTreeNodeData node )
		{ 
			super( DELETE );
			myNode = node;
			this.setEnabled( !myNode.hasParent() );
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void run()
		{
			boolean hadByObservationBinding = myGroup.getBinding() == GroupBinding.BY_OBSERVATION;
			
            IStructuredSelection selection = (IStructuredSelection) myTreeViewer.getSelection();
            List<QueryConceptTreeNodeData> removeList = new ArrayList<QueryConceptTreeNodeData>();

            int delta = GroupPanel.this.getExpandedTreeItemDescendantCount( ((TreeItem)myTreeViewer.testFindItem(myNode)).getItems() ) + 1; // self = 1
            GroupPanel.this.addToRowCounts( -delta );
            
            for ( Iterator<QueryConceptTreeNodeData> it = selection.iterator(); it.hasNext(); )
            	removeList.add( it.next() );
			removeNodes(removeList);
			
			autoAdjustPanelHeight();
			if ( hadByObservationBinding && !myGroup.isContainingModifier() ) // if group had modifier, but not any more, we change binding to by Encounter
				myGroup.setBinding( GroupBinding.BY_ENCOUNTER );
		}
	}
	
	@Override /* override default swt dispose method */
	public void dispose()
	{
		super.dispose();
		myGroupManager 	= null;
		myGroup			= null;
	}

	@Override /* GroupDroppedDelegator: handles UI changes after a Group has been dropped */
	public void groupDropped() 
	{
		this.myGroupManager.forceGroupBindingConformity( this.myGroup );
		Display.getDefault().asyncExec( new Runnable()
		{
			@Override
			public void run() 
			{	
				autoSetGroupTree();
				myTreeViewer.refresh();										// refresh the terms list
				GroupPanel.this.myRowCount = myGroup.getTerms().size();	// initialize rowCoutns
				autoAdjustPanelHeight();
				myDateDisplay.updateLabels();
				myNumberDisplay.updateDisplay();				
			}
		});
	}
	
	/*
	 * VisualActivationListener methods
	 */
	@Override
	public void setActivatedControl(Control control, Object supplementalData ) 
	{ myCurrentlySelectedDisplay = (QueryToolPanelComposite)control; }

	@Override
	public Control getActivatedControl() 
	{ return myCurrentlySelectedDisplay; }

	@Override
	public void resetActivatedControl() 
	{ myCurrentlySelectedDisplay = null; }
	
	
	/*
	 * main method for testing only
	 */
	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		GroupPanel gp = new GroupPanel( myShell, SWT.None );
		gp.setLayoutData( FormDataMaker.makeFullFormData() );
		
		myShell.setSize( 550, 200 );
		
		myShell.open();
		while (!myShell.isDisposed()) 
		{
			if (!Display.getCurrent().readAndDispatch())
				Display.getCurrent().sleep();
		}
		if (!myShell.isDisposed())
		{
			myShell.close();
			myShell.dispose();
		}
	}

}

class GroupPanelConceptContentProvider implements ITreeContentProvider 
{

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
	{}

	@Override
	public Object[] getElements(Object inputElement) 
	{
		return ((QueryConceptTreeNodeData)inputElement).getChildren().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) 
	{
		return ((QueryConceptTreeNodeData)parentElement).getChildren().toArray();
	}

	@Override
	public Object getParent(Object element) 
	{
		return ((QueryConceptTreeNodeData)element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) 
	{
		return ((QueryConceptTreeNodeData)element).getNumChildren() > 0;
	}	
}

class GroupPanelConceptLabelProvider extends ColumnLabelProvider
{
	//public Color 	getBackground(Object element) 	{}
	//public Font 	getFont(Object element)			{}
	//public Color 	getForeground(Object element)	{}
	
	public Image 	getImage(Object element)
	{
		return GroupPanelIcons.getImageIcon( (QueryConceptTreeNodeData)element );
	}
	
	public String 	getText(Object element)
	{
		return ((QueryConceptTreeNodeData)element).toString();
	}
	
	@Override
	public String 	getToolTipText(Object element) 
	{
		return ((QueryConceptTreeNodeData)element).tooltip();
	}
}

class GroupPanelIcons
{	
	public static Image getImageIcon(QueryConceptTreeNodeData data) 
	{
		return data.getImage();
	}
}

class ConceptRedrawThreadFactory implements ThreadFactory
{
	public static int numThreads = 0;

	public String myFactoryName = null;	
	public ConceptRedrawThreadFactory() {}
	
	public ConceptRedrawThreadFactory( String factoryName )
	{ myFactoryName = factoryName; }
	
	public Thread newThread(Runnable runnable) 
	{
		Thread aThread = new Thread( runnable );
		if ( myFactoryName != null )
			aThread.setName("ConceptRedrawThread - [" + myFactoryName + "]" + numThreads );
		else
			aThread.setName("ConceptRedrawThread - "  + numThreads );
		aThread.setDaemon( true );
		numThreads++;
		return aThread;
	}	
}


