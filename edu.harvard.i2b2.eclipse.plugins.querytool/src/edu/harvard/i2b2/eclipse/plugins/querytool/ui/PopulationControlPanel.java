package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.io.IOException;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.jdom.JDOMException;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.BasicQueryModePanel.PopulationSetType;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.DefaultDateStruct;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IGroupBindingPolicyProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PopulationLoader;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PopulationProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryDateConstraintProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryDateConstraintSynable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.PopulationTypeChangedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DateRangeChangeListeneer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.query.data.DragAndDrop2XML;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class PopulationControlPanel extends Composite implements UIConst, ControlListener, UIManagerContentChangedListener, PopulationProvider, PopulationLoader, QueryDateConstraintSynable, DateRangeChangeListeneer
{	
	public static final long STEP1_POPULATION_TIMESTAMP_INIT_VALUE = -1L;
	public static final long STEP2_POPULATION_TIMESTAMP_INIT_VALUE = -2L;

	private static final String	SHOW_POPULATION 	= "Show Population";	
	private static final String	EDIT_POPULATION 	= "Edit Population";
	private static final String POPULATION_TOGGLE 	= "Toggle Population Display";

	private static final int	HEIGHT_THRESHOLD	=  40;

	private Composite			titleComp;
	private Label				titleLabel;
	private Label				expandLabel;
	private DateConstraintDisplay 	myDateDisplay;
	private Combo					myPopulationTypeCombo;

	private ScrolledComposite	myScroller;
	private Composite			myContainer;
	private Button				myAddGroupButton;

	private GroupManager					myGroupManager 					= null;
	private IGroupBindingPolicyProvider 	myGroupBindingProvider			= null;
	//private QueryDateConstraintSynable 		myDateConstraintSyncableSource 	= null;	// A delegator from Step 1.
	private SplitterManager					mySplitterManager				= null;

	private PopulationTypeChangedDelegator	myPopulationTypeChangedDelgator = null;

	private boolean							amICollapsed				= true;
	private Long							myLastPopulationUpdate		= STEP2_POPULATION_TIMESTAMP_INIT_VALUE; 	//-2


	public PopulationControlPanel(Composite parent, int style, IGroupBindingPolicyProvider policyProvider, SplitterManager manager, PopulationTypeChangedDelegator populationTypeChangedDelegator ) 
	{
		super(parent, style);
		myGroupBindingProvider			= policyProvider;
		//myDateConstraintSyncableSource	= delegator;
		mySplitterManager				= manager;
		myPopulationTypeChangedDelgator = populationTypeChangedDelegator;
		setupUI();
		attachListeners();
	}

	private void setupUI() 
	{
		this.setLayout( new FormLayout() );
		
		titleComp = new Composite( this, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
			titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );
		
		titleLabel = new Label( titleComp, SWT.LEFT );
		titleLabel.setText( SHOW_POPULATION );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		
		expandLabel = new Label( titleComp, SWT.NONE );
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			expandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP ));
			expandLabel.setBackground( Colors.CONTROL_TITLE_BG );
		}
		else
			expandLabel.setText("^");
		expandLabel.setToolTipText( POPULATION_TOGGLE );

		Point singleCharSize = UIUtils.getDefaultCharSize(this);
		myDateDisplay = new SkinnyDateConstraintDisplay( titleComp, new DefaultDateStruct(), SWT.None );
		myDateDisplay.setActive( true );
		myDateDisplay.addListener( this );

		myPopulationTypeCombo 	= new Combo( titleComp, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.CENTER );
		// add items to combo and set selection
		myPopulationTypeCombo.add( BasicQueryModePanel.POPULATION_TYPES[ PopulationSetType.PATIENT.getIndex() ].getName() );
		myPopulationTypeCombo.add( BasicQueryModePanel.POPULATION_TYPES[ PopulationSetType.ENCOUNTER.getIndex() ].getName() );
		myPopulationTypeCombo.select( BasicQueryModePanel.DEFAULT_POPULATION_TYPE_SELECTION );
		
		FormData dateDisplayFD 		= FormDataMaker.makeFormData( 0, 0, 100, 0, (Integer)null, 0, expandLabel, -2);
			dateDisplayFD.width 	= NUM_CHARS_IN_DATE_DISPLAY * singleCharSize.x;
		
		expandLabel.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, (Integer)null, 100));
		myDateDisplay.setLayoutData( dateDisplayFD );
		myPopulationTypeCombo.setLayoutData( FormDataMaker.makeFormData(0, 0, 100, 0, (Integer)null, 0 , myDateDisplay, -2) );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 0, 0, myPopulationTypeCombo, 0));
		
		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );

		// Re-adjust height of tht title so it can be as tall as the combo
		titleCompFD.height = Math.max( titleCompFD.height, myPopulationTypeCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y );
		
		// set up scroller
		myScroller = new ScrolledComposite( this, SWT.V_SCROLL);
		myScroller.setLayout( new FormLayout() );
		myScroller.setLayoutData( FormDataMaker.makeFormData( titleComp, 100, 0, 100 ) );
		
			myContainer = new Composite( myScroller, SWT.None );
			myContainer.setLayout( new FormLayout() );
			myContainer.setLayoutData( FormDataMaker.makeFullFormData() );
			myContainer.setBackground( Colors.DARK_GRAY );
		
		myScroller.setContent( myContainer );
		myScroller.setExpandHorizontal( true );
		myScroller.setExpandVertical( true );
		
		// Create a GroupManager, using the GroupbindingPolicyProvider and QueryDateConstraintDelegator provided by BasicQueryModePanel
		myGroupManager = new MiniHeightGroupManager( myContainer, myGroupBindingProvider, this );
		myGroupManager.addPanels( Settings.getInstance().getNonTemporalQueryNumGroups() );  // create x number of groups as prescribed by Settings
		myGroupManager.addUIContentListener( this );

		myAddGroupButton = new Button( myContainer, SWT.PUSH );
		try
		{
			myAddGroupButton.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ADD ));
			myAddGroupButton.setText( ADD_GROUP );
		}
		catch ( IllegalStateException e )
		{
			myAddGroupButton.setText( "+ " + ADD_GROUP );
		}
		updateAddPanelButtonLayout();
		
		myScroller.setMinHeight( myContainer.computeSize( SWT.DEFAULT, SWT.DEFAULT).y );
	}
	
	private void updateAddPanelButtonLayout() 
	{
		GroupPanel lastPanel = myGroupManager.getLastPanel();
		Point panelSize = myAddGroupButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		if ( lastPanel != null )
			myAddGroupButton.setLayoutData( FormDataMaker.makeFormData( lastPanel, GROUP_PANEL_MARGIN, (Integer)null, -GROUP_PANEL_MARGIN, 50, -panelSize.x/2, (Integer)null, 0 ));
		else
			myAddGroupButton.setLayoutData( FormDataMaker.makeFormData( 0, GROUP_PANEL_MARGIN, (Integer)null, -GROUP_PANEL_MARGIN, 50, -panelSize.x/2, (Integer)null, 0 ));
		((FormData)myAddGroupButton.getLayoutData()).height =  myAddGroupButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;		
	}

	
	private void attachListeners() 
	{
		this.addControlListener( this ); // Listen for resizing

		myAddGroupButton.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent ev )
			{
				myGroupManager.addPanels(1, PopulationControlPanel.this.isUsingGroupSpecificConstraints(), PopulationControlPanel.this.getStartDate(), PopulationControlPanel.this.getEndDate());
			}
		});

		MouseAdapter expander = new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e) 
			{
				amICollapsed = mySplitterManager.toggleAndReturnCollapseState( getTitleHeight() );
				autoSetExpansionWidgets();
			}
		};

		MouseTrackListener highlighter = new MouseTrackListener()
		{
			@Override
			public void mouseEnter(MouseEvent e) 
			{ setHot(); }
			
			@Override
			public void mouseExit(MouseEvent e) 
			{
				if ( amICollapsed )
					expandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP) );
				else
					expandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN ) );
			}

			@Override
			public void mouseHover(MouseEvent e) 
			{ setHot(); }
			
			private void setHot()
			{
				if ( amICollapsed )
					expandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP_ACTIVE) );
				else
					expandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN_ACTIVE) );
			}
		};
		
		expandLabel.addMouseListener( expander );
		titleComp.addMouseListener( expander );
		titleLabel.addMouseListener( expander );
		
		expandLabel.addMouseTrackListener( highlighter ) ;
		titleComp.addMouseTrackListener( highlighter );
		titleLabel.addMouseTrackListener( highlighter );
		
		myPopulationTypeCombo.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent ev )
			{
				myPopulationTypeChangedDelgator.populationTypeChanged( getPopulationType() );
				if ( myPopulationTypeCombo.getSelectionIndex() == PopulationSetType.PATIENT.getIndex() ) // must RESTRICT the allowable binding type and FORCE current settings to ANY
					myGroupManager.batchSetBinding( GroupBinding.BY_PATIENT );	// force current setting to BY_PATIENT
				else if ( myPopulationTypeCombo.getSelectionIndex() == PopulationSetType.ENCOUNTER.getIndex() )
					myGroupManager.batchSetBindingAtLeast( GroupBinding.BY_ENCOUNTER );					// make sure Group binding is at least BY_ENCOUNTER
			}
		});
	}

	
	public int getTitleHeight()
	{ return ((FormData)titleComp.getLayoutData()).height; }


	private void autoSetExpansionWidgets()
	{		
		if ( amICollapsed )
		{
			if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
				expandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP ));
			else
				expandLabel.setText( "^" );
			titleLabel.setText( SHOW_POPULATION );
		}
		else
		{
			if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
				expandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN ));
			else
				expandLabel.setText( "V");
			titleLabel.setText( EDIT_POPULATION );
		}
	}
	
	public boolean isCollapsed()
	{ return this.amICollapsed; }

	public void consolidatePanels()
	{ this.myGroupManager.consolidatePanels(); }

	public void setDataWithQueryDefinition( QueryDefinitionType queryDefinitionType ) throws JDOMException, IOException
	{
		// programmatically set Population to be Patient-based or Encounter-based according to query timing 
		String queryTiming = queryDefinitionType.getQueryTiming().toLowerCase();
		if ( queryTiming.equals( ANY.toLowerCase()) )
			myPopulationTypeCombo.select( BasicQueryModePanel.PopulationSetType.PATIENT.getIndex() );
		else if ( queryTiming.equals( SAMEVISIT.toLowerCase() ))
			myPopulationTypeCombo.select( BasicQueryModePanel.PopulationSetType.ENCOUNTER.getIndex() );
		else
		{
			UIUtils.popupError("Error Occurred During Query Drop",  "Cannot parse the dropped query.", "Query Timing value '" + queryDefinitionType.getQueryTiming() + "' is not recognized." );
			return;
		}
		Event event = Event.makeAnonymouseEvent();
		DragAndDrop2XML.setEventWithQueryDefinition( event, queryDefinitionType );
		this.initializeWithEvent(event);
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}

	private void initializeWithEvent( Event event )
	{
		// clear and reset all data
		myGroupManager.removeAllPanels(); // remove all GroupPanel UI
		if ( event.getGroups().size() != 0 )
			this.myGroupManager.addPanels( event );				// create GroupPanels and Groups with the given event
		else
			this.myGroupManager.addPanels( 3 );					// if no data, we create 3 empty GroupsPanels for users to work with
	}

	public void updatePopulationType(PopulationProvider popProvider) 
	{ 
		this.myPopulationTypeCombo.select( popProvider.getPopulationType().getIndex() );
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}
	
	// reset population and update UI
	public void reset()
	{
		this.resetPopulation();
	}

	@Override /* UIManagerContentChangedListener methods: listen to Group addition/removal */
	public void groupManagerContentChanged(Object object) 
	{
		updateAddPanelButtonLayout();
		myContainer.layout();
		myContainer.redraw();
		myScroller.setMinHeight( myContainer.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}

	/* ControlListener methods */
	@Override
	public void controlMoved(ControlEvent e) 
	{ /* do nothing */ }

	@Override // Listen for resizing due to Sashform (parent) changing this control's size. 
	public void controlResized(ControlEvent e) 
	{
		this.amICollapsed = !(this.getBounds().height > HEIGHT_THRESHOLD);
		autoSetExpansionWidgets();
	}

	@Override /* PopulationLoader methods */
	public void loadPopulation(Event event) 
	{
		myGroupManager.removeAllPanels(); 							// remove all GroupPanel UI
		//myGroupBindingControlPanel.initializeWithEvent( event );	// update GroupBindingControlPanel
		//myQueryDateControlPanel.initializeWithEvent( event);		// update DateControLPanel
		if ( !event.isUsingGroupSpecificDates() )
		{
			myDateDisplay.setStartDate( event.getStartDate() );
			myDateDisplay.setEndDate( event.getEndDate() );
		}
		else
		{
			myDateDisplay.setStartDate( null );
			myDateDisplay.setEndDate( null );
		}
		myDateDisplay.updateLabels();
		
		myGroupManager.addPanels( event );
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}
	@Override
	public void resetPopulation() 
	{
		this.myGroupManager.removeAllPanelsWithNotification();
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}


	@Override /* PopulationProvider methods */
	public Event getPopulation() 
	{
		Event event = Event.makeAnonymouseEvent();
		myGroupManager.cancelAllRunningTasks();
		event.setGroups( myGroupManager.makeGroupData() );
		if ( myGroupManager.isUsingGroupSpecificDateConstraint() )
			event.setIsUsingGroupSpecificDates( true );
		else
		{
			event.setIsUsingGroupSpecificDates( false );
			event.setStartDate( this.getStartDate() );
			event.setEndDate( this.getEndDate() );
		}
		return event;
	}

	@Override /* PopulationProvider methods */
	public Long getPopulationTimestamp() 
	{
		return this.myLastPopulationUpdate;
	}

	@Override /* DateRangeChangeListeneer method */
	public void dateRangeChanged(GregorianCalendar startDate, GregorianCalendar endDate) 
	{ 
		constraintChanged( (startDate==null) && (endDate==null) );
	}
	
	@Override /* QueryDateConstraintSyncable methods */
	public GregorianCalendar getStartDate() 
	{ return this.myDateDisplay.getStartDate(); }

	@Override
	public GregorianCalendar getEndDate() 
	{ return this.myDateDisplay.getEndDate(); }

	@Override
	public boolean isUsingGroupSpecificConstraints() 
	{ return ((getStartDate()==null) && (getEndDate()==null)); }

	@Override
	public void constraintChanged(boolean useGroupSpecificConstraints) 
	{
		if ( useGroupSpecificConstraints )
			myGroupManager.useGroupSpecificDateConstraint();
		else
			myGroupManager.useCentralDateConstraint( getStartDate(), getEndDate() );
		this.myLastPopulationUpdate = System.currentTimeMillis();
	}

	@Override
	public void syncTo(QueryDateConstraintSynable syncSource) 
	{
		this.myDateDisplay.setStartDate( syncSource.getStartDate() );
		this.myDateDisplay.setEndDate( syncSource.getEndDate() );
		constraintChanged( syncSource.isUsingGroupSpecificConstraints() );
		this.myDateDisplay.updateLabels(); // update DateDisplay
	}

	@Override
	public PopulationSetType getPopulationType() 
	{ return BasicQueryModePanel.POPULATION_TYPES[ myPopulationTypeCombo.getSelectionIndex() ]; }
}


class MiniHeightGroupManager extends GroupManager
{
	public  static final int SMALL_PANEL_SPACING = 4;
	private static final int PANEL_INDENT = 1;

	public MiniHeightGroupManager(Composite parent, IGroupBindingPolicyProvider gbpProvider, QueryDateConstraintProvider provider) 
	{
		super(parent, gbpProvider, provider );
	}

	@Override // all panel layout data are produced here
	protected FormData makeLayoutData( Control previousControl )
	{
		if ( previousControl == null )
			return FormDataMaker.makeFormData( 0, SMALL_PANEL_SPACING, (Integer)null, 0, 0, PANEL_INDENT , 100, 0 );
		else
			return FormDataMaker.makeFormData( previousControl, SMALL_PANEL_SPACING, (Integer)null, 0, 0, PANEL_INDENT, 100, 0 );
	}

	/*
	 * 	Methods subclasses can override to adjust layout parameters 
	 */
	@Override
	protected int getDefaultPanelHeight()
	{
		return UIConst.SHORT_GROUPPANEL_HEIGHT;
	}

}
