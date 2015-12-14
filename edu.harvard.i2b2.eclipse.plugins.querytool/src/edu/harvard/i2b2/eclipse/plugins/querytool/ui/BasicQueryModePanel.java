/*
 * Copyright (c) 2006-2015 Partners Healthcare 
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.GroupBindingPolicyUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IGroupBindingPolicyProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IPreQueryDataProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PopulationAutoSynchronizer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PopulationLoader;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PopulationProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PreQueryData;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IQueryTimingProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryDateConstraintSynable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.PopulationTypeChangedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.QueryDroppedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.GroupBindingSelectionListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.QueryDropHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.DefaultSlideWithTransitionControls;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.ExpandBar;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.MixedTextIconPanel;
import edu.harvard.i2b2.query.data.DataConst;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class BasicQueryModePanel extends DefaultSlideWithTransitionControls implements UIConst, GroupBindingSelectionListener, UIManagerContentChangedListener, QueryDateConstraintSynable, IPreQueryDataProvider, IQueryTimingProvider, PopulationProvider, PopulationLoader
{
	public enum PopulationSetType 
	{ 
		PATIENT( UIConst.PATIENT, DataConst.ANY, 0 ), 
		ENCOUNTER( UIConst.ENCOUNTER, DataConst.SAME_VISIT, 1 );
		
		private String 	myName;		// what to display in combo
		private	String	myTiming;	// what the actual timing text is
		private int 	myIndex;
		
		private PopulationSetType(String name, String timing, int index) 
		{ 
			this.myName 	= name;
			this.myIndex 	= index;
			this.myTiming	= timing;
		}

		public String 	getName()		{ return this.myName; 	}
		public int		getIndex()		{ return this.myIndex; 	}
		public String	getTimingID()	{ return this.myTiming; }
	};

	public static final PopulationSetType [] POPULATION_TYPES = new PopulationSetType [] { PopulationSetType.PATIENT, PopulationSetType.ENCOUNTER };
	public static final int DEFAULT_POPULATION_TYPE_SELECTION = 0; // DEFAULT is PATIENT
	
	class InstructionHeader extends ExpandBar implements IQueryTimingProvider, IGroupBindingPolicyProvider, PopulationTypeChangedDelegator
	{
		protected	Combo 	myPopulationTypeCombo;
		protected 	Button	myControlToggle;
		protected	Label	myExpandLabel;
		protected	Label	myHeaderText;
		protected	Label	myInstruction1;
		protected	Label	myInstruction2;

		public InstructionHeader(Composite parent, int style) 
		{
			super(parent, style);
			isExpanded = false;	// default state for EventExpandPanel is collapsed
			setupUI();
			attachListeners();
		}

		@Override
		protected void setupUI() 
		{
			this.setBackground( Colors.GRAY );
			this.setLayout( new FormLayout() );
			
			Composite labelComposite = new Composite( this, SWT.NONE );
			labelComposite.setLayout( new FormLayout() );
			labelComposite.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100));

			Composite detailComposite = new Composite( this, SWT.NONE );
			detailComposite.setLayout( new FormLayout() );
			detailComposite.setLayoutData( FormDataMaker.makeFormData(labelComposite, (Integer)null, 0, 100));
			
			myExpandLabel			= new Label( labelComposite, SWT.NONE );
			myTextLabel 			= new Label( labelComposite, SWT.NONE );
			myHeaderText			= new Label( labelComposite, SWT.NONE );
			myPopulationTypeCombo 	= new Combo( labelComposite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.CENTER );
			myControlToggle			= new Button(labelComposite, SWT.PUSH);
			
			myInstruction1			= new Label( detailComposite, SWT.NONE );
			myInstruction2			= new Label( detailComposite, SWT.NONE );
			
			// add items to combo
			myPopulationTypeCombo.add( POPULATION_TYPES[ PopulationSetType.PATIENT.getIndex() ].getName() );
			myPopulationTypeCombo.add( POPULATION_TYPES[ PopulationSetType.ENCOUNTER.getIndex() ].getName() );

			myPopulationTypeCombo.select( DEFAULT_POPULATION_TYPE_SELECTION );
			
			myTextLabel.setText("Define a ");
			myHeaderText.setText(" population from which temporal relationships will be found.");
			myInstruction1.setText("Construct the set by dragging ontology terms into the groups below and then press the [Next] button at the bottom. ");
			myInstruction2.setText("Alternatively, leave all the groups empty to use ALL data in the database.");
			myControlToggle.setText("Toggle Controls");
			
			FormData expandLabelFD = FormDataMaker.makeFormData(0, 2, 100, -2, 0,2, (Integer)null, 0);
			myExpandLabel.setLayoutData( expandLabelFD );
			myExpandLabel.setBackground( labelComposite.getBackground() );
			if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
			{
				if ( this.isExpanded )
					this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP ));
				else 
					this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN ));
			}
			else		
			{
				myExpandLabel.setText(" > ");
				myExpandLabel.setForeground( Colors.DARK_RED );
			}

			myTextLabel.setLayoutData( FormDataMaker.makeFormData(0, (myPopulationTypeCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y-myTextLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y)/2, (Integer)null, 0, myExpandLabel, 2, (Integer)null , 0) );
			myPopulationTypeCombo.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, myTextLabel, (Integer)null ) );
			myHeaderText.setLayoutData( FormDataMaker.makeFormData(0, (myPopulationTypeCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y-myHeaderText.computeSize(SWT.DEFAULT, SWT.DEFAULT).y)/2, (Integer)null, 0, myPopulationTypeCombo, 0, myControlToggle, 0 ) );
			myControlToggle.setLayoutData( FormDataMaker.makeFormData( 0, 0, (Integer)null, 0, (Integer)null, 0, 100, 0 )   );
			
			myInstruction1.setLayoutData( FormDataMaker.makeFormData(0, 2, (Integer)null, 0, 0, 2, 100, 0 ) );
			myInstruction2.setLayoutData( FormDataMaker.makeFormData(myInstruction1, 2, (Integer)null, 0, 0, 2, 100, 0 ) );

			contractedHeight 	= labelComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
			expandedHeight 	= this.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
			
			myPopulationTypeCombo.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		}

		@Override
		protected void attachListeners() 
		{
			// allow expand/contract the entire EVentExpandPanel
			MouseAdapter expandContractClicker = new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e) 
				{
					if ( isMoving )
						return; // don't do anything if it's already expanding/contracting
					isMoving = true;
					if ( isExpanded )
						contract();
					else
						expand();
					autoSetExpanderIcon( );
				}
			};
			this.addMouseListener( expandContractClicker );
			this.myTextLabel.addMouseListener( expandContractClicker );
			this.myHeaderText.addMouseListener( expandContractClicker );
			this.myExpandLabel.addMouseListener( expandContractClicker );
			
			MouseTrackListener expandContractHoverer = new MouseTrackListener()
			{
				@Override
				public void mouseEnter(MouseEvent e) 
				{ setHot(); }
				
				@Override
				public void mouseExit(MouseEvent e) 
				{ 
					if ( isExpanded )
						myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP) );
					else
						myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN) );
				}
				
				@Override
				public void mouseHover(MouseEvent e) 
				{ setHot(); }
				
				private void setHot()
				{
					if ( isExpanded )
						myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP_ACTIVE) );
					else
						myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN_ACTIVE) );
				}
			};
			
			// Allow Mouse-over indicator
			this.myExpandLabel.addMouseTrackListener( expandContractHoverer );
			this.myTextLabel.addMouseTrackListener( expandContractHoverer );
			this.myHeaderText.addMouseTrackListener( expandContractHoverer );
			this.myExpandLabel.addMouseTrackListener( expandContractHoverer );

			myControlToggle.addSelectionListener( new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) 
				{
					if ( mySplitter.getWeights()[1] != 0 )
						mySplitter.setWeights( new int [] {100, 0} );
					else
					{
						int percent = Math.round( 200f/((float)BasicQueryModePanel.this.getClientArea().width)*100);
						mySplitter.setWeights( new int [] {100-percent, percent} );
					}
				}
			});
			
			myPopulationTypeCombo.addSelectionListener( new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) 
				{
					if ( myPopulationTypeCombo.getSelectionIndex() == PopulationSetType.PATIENT.getIndex() ) // must RESTRICT the allowable binding type and FORCE current settings to ANY
						myGroupManager.batchSetBinding( GroupBinding.BY_PATIENT );	// force current setting to BY_PATIENT
					else if ( myPopulationTypeCombo.getSelectionIndex() == PopulationSetType.ENCOUNTER.getIndex() )
						myGroupManager.batchSetBindingAtLeast( GroupBinding.BY_ENCOUNTER );					// make sure Group binding is at least BY_ENCOUNTER
					BasicQueryModePanel.this.myGroupBindingControlPanel.autoSetCheckBoxes(); // automatically set check boxes in GroupBindingControlPanel 
				}
			});			
		}

		private void autoSetExpanderIcon(  )
		{
				if ( this.isExpanded )
					this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP_ACTIVE ));
				else 
					this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN_ACTIVE ));
		}

		public PopulationSetType getPopulationSetType()
		{	return POPULATION_TYPES[ myPopulationTypeCombo.getSelectionIndex() ];	}

		@Override
		public String getQueryTimingID() 
		{	return POPULATION_TYPES[ myPopulationTypeCombo.getSelectionIndex() ].getTimingID(); }

		/* IGroupBindingPolicyProvider methods */
		@Override
		public boolean isByPatientEnabled(Group group) 		{ return true; }// always enabled 
		@Override
		public boolean isByEncounterEnabled(Group group)					// enabled only if user selects Encounter set and not Patient set
		{
			if ( getPopulationSetType() == PopulationSetType.ENCOUNTER )
				return true;
			return false;
		}
		
		@Override
		public boolean isByObservationEnabled(Group group) 	
		{
			return (( getPopulationSetType() == PopulationSetType.ENCOUNTER ) && group.isContainingModifier());
		}
		
		@Override
		public GroupBinding getDefaultBinding( )
		{ 
			if ( this.getPopulationSetType() == PopulationSetType.PATIENT )
				return GroupBinding.BY_PATIENT;
			else if ( this.getPopulationSetType() == PopulationSetType.ENCOUNTER )
				return GroupBinding.BY_ENCOUNTER;
			return null;
		}
		
		@Override
		public void forceConformity(Group group) 
		{ GroupBindingPolicyUtils.forceDefaultConformity(group, this); }

		@Override /* PopulationTypeChangedDelegator methods */
		public void populationTypeChanged(PopulationSetType newType) 
		{
			this.myPopulationTypeCombo.select( newType.myIndex );
		}
	}

	private InstructionHeader 	myInstructionHeader;// instructions to use this BasicQueryModePanel
	private Label				myInstructions;		// instructions to add Groups
	private MixedTextIconPanel 	myDropInstructions;	// instruction so drop a previous query
	
	private SashForm 			mySplitter;
	private ScrolledComposite	myRightScroller;
	private Composite			rightContainer;

	private ScrolledComposite	myLeftScroller;
	private Composite			leftContainer;
	private Button				myAddPanelButton;

	private GroupBindingControlPanel 		myGroupBindingControlPanel;
	private QueryDateConstraintControlPanel myQueryDateControlPanel;
	
	private QueryDroppedDelegator			myQueryDroppedDelegator;
	private GroupManager					myGroupManager 	= null;
	private PopulationAutoSynchronizer		myPopulationSynchronizer;
	
	private Event							myCurrentEvent 	= null; // the Event that this BasicQueryModePanel is loaded with.
	private Long							myLastPopulationUpdate = PopulationControlPanel.STEP1_POPULATION_TIMESTAMP_INIT_VALUE; //-1
	
	// isBasicMode designates whether the BasicQueryModePanel is for BasicMode (true) or TemporalQueryMode (false)
	public BasicQueryModePanel(Composite parent, int style, QueryDroppedDelegator queryDropDelegator,  PopulationAutoSynchronizer synchronizer ) 
	{
		super(parent, style);
		myQueryDroppedDelegator	= queryDropDelegator;
		myPopulationSynchronizer= synchronizer;
		setupUI();
		attachListeners();
	}

	private void setupUI() 
	{
		this.setLayout( new FormLayout() );
		
		myInstructionHeader = new InstructionHeader( this, SWT.NONE );
		myInstructionHeader.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100) );
		((FormData)myInstructionHeader.getLayoutData()).height = myInstructionHeader.getPreferredContractedHeight();
		//myInstructionHeader.computeSize(SWT.DEFAULT, SWT.DEFAULT).y; // set the formdata height because EventExpandBar works off that
		
		mySplitter = new SashForm( this, SWT.HORIZONTAL | SWT.SMOOTH );	
		mySplitter.setLayout( new FormLayout() );
		mySplitter.setLayoutData( FormDataMaker.makeFormData(myInstructionHeader, 100, 0, 100) );
		mySplitter.setSashWidth( 5 );
		
		/* initialize LEFT SCROLLER */
		myLeftScroller = new ScrolledComposite( mySplitter, SWT.V_SCROLL);
		myLeftScroller.setLayout( new FormLayout() );
		myLeftScroller.setLayoutData( FormDataMaker.makeFullFormData() );
		
			leftContainer = new Composite( myLeftScroller, SWT.None );
			leftContainer.setLayout( new FormLayout() );
			leftContainer.setLayoutData( FormDataMaker.makeFullFormData() );
			leftContainer.setBackground( Colors.DARK_GRAY );
			
		myLeftScroller.setContent( leftContainer );
		myLeftScroller.setExpandHorizontal( true );
		myLeftScroller.setExpandVertical( true );

		/* initialize RIGHT SCROLLER */
		myRightScroller = new ScrolledComposite( mySplitter, SWT.V_SCROLL);
		myRightScroller.setLayout( new FormLayout() );
		myRightScroller.setLayoutData( FormDataMaker.makeFullFormData() );
		
			rightContainer = new Composite( myRightScroller, SWT.None );
			rightContainer.setLayout( new FormLayout() );
			rightContainer.setLayoutData( FormDataMaker.makeFullFormData() );
			rightContainer.setBackground( Colors.DARK_GRAY );
			
		myRightScroller.setContent( rightContainer );
		myRightScroller.setExpandHorizontal( true );
		myRightScroller.setExpandVertical( true );
		
		// add Group Binding Control Panel
		myGroupBindingControlPanel = new GroupBindingControlPanel( rightContainer, SWT.None );
		myGroupBindingControlPanel.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100) );
		
		// add Date Constraints Control Panel
		myQueryDateControlPanel = new QueryDateConstraintControlPanel( rightContainer, this, SWT.None, true );
		myQueryDateControlPanel.setLayoutData( FormDataMaker.makeFormData(myGroupBindingControlPanel, 0, (Integer)null, 0, 0, 0, 100, 0) );
		
		myGroupManager = new GroupManager( leftContainer, this.myInstructionHeader, this );
		myGroupManager.addUIContentListener( myGroupBindingControlPanel );					// link GroupManager with BindingControlPanel
		myGroupManager.addPanels( Settings.getInstance().getNonTemporalQueryNumGroups() );  // create x number of groups as prescribed by Settings
		myGroupManager.addUIContentListener( this );

		myAddPanelButton = new Button( leftContainer, SWT.PUSH );
		try
		{
			myAddPanelButton.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ADD ));
			myAddPanelButton.setText( ADD_GROUP );
		}
		catch ( IllegalStateException e )
		{
			myAddPanelButton.setText( "+ " + ADD_GROUP );
		}

		myInstructions = new Label( leftContainer, SWT.WRAP | SWT.CENTER );
		myInstructions.setText( ADD_GROUP_INSTRUCTIONS );
		myInstructions.setLayoutData( FormDataMaker.makeFormData(myAddPanelButton, UIConst.GROUP_PANEL_MARGIN, (Integer)null, -UIConst.GROUP_PANEL_MARGIN, 50, -myInstructions.computeSize(SWT.DEFAULT, SWT.DEFAULT).x/2, (Integer)null, 0) );		
		myInstructions.setForeground( Colors.WHITE );
		myInstructions.setBackground( myInstructions.getParent().getBackground() );

		ArrayList<Object> items = new ArrayList<Object>();
		items.add( QUERY_DROP_INSTRUCTIONS_1 );
		items.add( Images.getImageByKey( Images.PREVIOUS_QUERY ));
		items.add( Images.getImageByKey( Images.PREVIOUS_TEMPORAL_QUERY ));
		items.add( QUERY_DROP_INSTRUCTIONS_2 );
		myDropInstructions = new MixedTextIconPanel( leftContainer, SWT.WRAP, items, myInstructions.getForeground(), myInstructions.getParent().getBackground() );
		myDropInstructions.setLayoutData( FormDataMaker.makeFormData(myInstructions, 0, (Integer)null, -UIConst.GROUP_PANEL_MARGIN, 50, -myDropInstructions.computeSize(SWT.DEFAULT, SWT.DEFAULT).x/2, (Integer)null, 0) );
		
		// has no function but to take up space to create a bottom margin
		Composite bottomPlaceHolder = new Composite( leftContainer, SWT.NONE ); 
		bottomPlaceHolder.setLayoutData( FormDataMaker.makeFormData(myInstructions, GroupManager.PANEL_SPACING, 100, 0, 0, 0, 100, 0));
		bottomPlaceHolder.setBackground( leftContainer.getBackground() );

		updateAddPanelButtonLayout();

		mySplitter.setWeights(new int[] {100,0});

		// set initial minHight for leftScroller 
		myRightScroller.setMinHeight( rightContainer.computeSize( SWT.DEFAULT, SWT.DEFAULT).y );
		myLeftScroller.setMinHeight( leftContainer.computeSize( SWT.DEFAULT, SWT.DEFAULT).y );
	}

	private void updateAddPanelButtonLayout() 
	{
		GroupPanel lastPanel = myGroupManager.getLastPanel();
		Point panelSize = myAddPanelButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		if ( lastPanel != null )
			myAddPanelButton.setLayoutData( FormDataMaker.makeFormData( lastPanel, GROUP_PANEL_MARGIN, (Integer)null, -GROUP_PANEL_MARGIN, 50, -panelSize.x/2, (Integer)null, 0 ));
		else
			myAddPanelButton.setLayoutData( FormDataMaker.makeFormData( 0, GROUP_PANEL_MARGIN, (Integer)null, -GROUP_PANEL_MARGIN, 50, -panelSize.x/2, (Integer)null, 0 ));
		((FormData)myAddPanelButton.getLayoutData()).height =  myAddPanelButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;		
	}

	private void attachListeners() 
	{
		// whenever leftContainer changes size, we set the min height to an appropriate value
		rightContainer.addControlListener( new ControlListener()
		{
			public void controlMoved(ControlEvent arg0) {}
			public void controlResized(ControlEvent arg0) 
			{
				myRightScroller.setMinHeight( myQueryDateControlPanel.getLocation().y + myQueryDateControlPanel.getBounds().height + 20);
			}
		});
		
		myAddPanelButton.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  {
				  myGroupManager.addPanels( 1, myQueryDateControlPanel.isUsingGroupSpecificDates(), myQueryDateControlPanel.getStartDate(), myQueryDateControlPanel.getEndDate() );
				  leftContainer.layout( true );
			  }
		});
		
		DropTarget target = new DropTarget( leftContainer, UIConst.DND_DROP_OPS );
		target.setTransfer( UIConst.DND_TRANSFER_TYPES );
		target.addDropListener( new QueryDropHandler( myQueryDroppedDelegator ) );
	}

	public void initalizeWithEvent( Event event )
	{
		// do nothing if the current event is the same as the parameter
		if ( myCurrentEvent == event ) 
			return;
		// save currentEvent, if it exists, before changing it
		saveCurrentEvent();
		// clear and reset all data
		myGroupManager.removeAllPanels(); // remove all GroupPanel UI
		myGroupBindingControlPanel.initializeWithEvent( event );
		myQueryDateControlPanel.initializeWithEvent( event);
		if ( event.getGroups().size() != 0 )
			this.myGroupManager.addPanels( event );				// create GroupPanels and Groups with the given event
		else
			this.myGroupManager.addPanels( 3 );					// if no data, we create 3 empty GroupsPanels for users to work with
		
		myCurrentEvent = event; 	// update myCurrentEvent
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}
	
	public void saveCurrentEvent()
	{
		if ( myCurrentEvent != null )
		{
			updateEvent();
			myCurrentEvent.notifyListeners();
			myCurrentEvent = null; // clear myCurrentEvent
		}
	}
	
	public void consolidatePanels()
	{
		myGroupManager.consolidatePanels();
		leftContainer.layout( true ); 
	}
	
	public PopulationTypeChangedDelegator getPopulationTypeChangedDelegator()
	{ return this.myInstructionHeader; }
	
	public ArrayList<Group> getGroupData()
	{ return myGroupManager.makeGroupData(); }
	
	
	public void setPopulationTypeChanged(PopulationSetType newType) 
	{
		this.myInstructionHeader.populationTypeChanged( newType );
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}

	
	/*
	 * Write values from UI to currentEvent 
	 */
	public void updateEvent()
	{
		myCurrentEvent.setGroups( this.getGroupData() );
		myCurrentEvent.setIsUsingGroupSpecificDates( this.myQueryDateControlPanel.isUsingGroupSpecificDates() );
		myCurrentEvent.setStartDate( this.myQueryDateControlPanel.getStartDate() );
		myCurrentEvent.setEndDate( this.myQueryDateControlPanel.getEndDate() );
	}

	/* Retun the GroupBindingPolicyProvider built in myInstructionHeader */
	public IGroupBindingPolicyProvider getGroupBindingPolicyProvider()
	{ return this.myInstructionHeader;}

	
	/*
	 * Clear Population. Reset UI.
	 */
	public void clear()
	{
		this.resetPopulation();
	}

	public void initialize()
	{
		myGroupManager.addPanels( Settings.getInstance().getNonTemporalQueryNumGroups() );  // create x number of groups as prescribed by Settings
		updateAddPanelButtonLayout();
		mySplitter.setWeights(new int[] {100,0});
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}

	/*
	 * Create an Event object from the UIs.	 
	public Event makeEvent()
	{
		Event event = new Event( this.getGroupData(), this.queryDateControlPanel.isUsingGroupSpecificDates(), this.queryDateControlPanel.getStartDate(), this.queryDateControlPanel.getEndDate() );
		return event;
	}
	*/
	
	// whenever group binding changes (from By_Patient to By_Encounter or By_Observation), we set the min height to an appropriate value
	@Override
	public void groupBindingSelectionChanged() 
	{
		myRightScroller.setMinHeight( myQueryDateControlPanel.getLocation().y + myQueryDateControlPanel.getBounds().height + 20);
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}

	// whenever group panels are added/removed 
	@Override
	public void groupManagerContentChanged( Object source ) 
	{
		updateAddPanelButtonLayout();
		leftContainer.layout();
		leftContainer.redraw();
		myLeftScroller.setMinHeight( leftContainer.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}

	/*
	 * QueryDateConstraintSyncable methods
	 */
	@Override
	public GregorianCalendar getStartDate() 
	{ return myQueryDateControlPanel.getStartDate(); }

	@Override
	public GregorianCalendar getEndDate() 
	{ return myQueryDateControlPanel.getEndDate(); }
	
	@Override
	public boolean isUsingGroupSpecificConstraints()
	{
		return myQueryDateControlPanel.isUsingGroupSpecificDates();
	}
	
	@Override
	public void constraintChanged( boolean useGroupSpecificConstraints ) 
	{
		if ( useGroupSpecificConstraints )
			myGroupManager.useGroupSpecificDateConstraint();
		else
			myGroupManager.useCentralDateConstraint( myQueryDateControlPanel.getStartDate(), myQueryDateControlPanel.getEndDate() );
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}

	@Override
	public void syncTo(QueryDateConstraintSynable syncSource) 
	{
		this.myQueryDateControlPanel.setStartDate( syncSource.getStartDate() );
		this.myQueryDateControlPanel.setEndDate( syncSource.getEndDate() );
		constraintChanged( syncSource.isUsingGroupSpecificConstraints() );
		this.myQueryDateControlPanel.updateLabels();
	}
	
	
	@Override /* IPreQueryDataProvider */
	public PreQueryData getPreQueryData() 
	{ return new PreQueryData(getGroupData()); }

	@Override /* QueryTimingProvider */
	public String getQueryTimingID() 
	{	return myInstructionHeader.getQueryTimingID(); }

	
	@Override /* PopulationLoader method */
	public void loadPopulation(Event event) 
	{
		myGroupManager.removeAllPanels(); 							// remove all GroupPanel UI
		myGroupBindingControlPanel.initializeWithEvent( event );	// update GroupBindingControlPanel
		myQueryDateControlPanel.initializeWithEvent( event);		// update DateControLPanel
		myGroupManager.addPanels( event );
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}
	
	@Override /* PopulationProvider methods */
	public Event getPopulation() 
	{
		Event event = Event.makeAnonymouseEvent();
		myGroupManager.cancelAllRunningTasks();
		event.setGroups( myGroupManager.makeGroupData() );
		event.setIsUsingGroupSpecificDates( myGroupManager.isUsingGroupSpecificDateConstraint() );
		if ( !myGroupManager.isUsingGroupSpecificDateConstraint() )
		{
			event.setStartDate( myQueryDateControlPanel.getStartDate() );
			event.setEndDate( myQueryDateControlPanel.getEndDate() );
		}
		return event;
	}
	
	@Override
	public void resetPopulation() 
	{
		this.myGroupManager.removeAllPanelsWithNotification();
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}

	@Override /* DefaultSlideWithTransitionControls */ 
	public void performPostSlideActions(int fromSlideIndex) 
	{
		if ( fromSlideIndex == QueryToolMainUI.TEMPORAL_DEFINITION_SLIDE_INDEX ) // if  swtiching from TemporalQueryModePanel
			this.myPopulationSynchronizer.autoSyncPopulations();
	}
	
	@Override /* PopulationProvider method */
	public Long getPopulationTimestamp() 
	{ return this.myLastPopulationUpdate; }

	@Override /* PopulationProvider method */
	public PopulationSetType getPopulationType() 
	{ return this.myInstructionHeader.getPopulationSetType(); }

	
}