package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.DefaultDateStruct;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryDateConstraintProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DateRangeChangeListeneer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.ExpandContractListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.DaemonThreadFactory;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.ExpandBar;

public class EventExpandPanel extends ExpandBar implements UIManagerContentChangedListener, QueryDateConstraintProvider, UIConst, DataChangedListener, DateRangeChangeListeneer
{
	
	protected 	EventListManager 	myManager;
	protected 	GroupManager 		myGroupManager 	= null;
	protected	Event				myEvent;

	protected 	Composite				myGroupPanelsPanel;
	protected	DateConstraintDisplay 	myDateDisplay;

	protected	Label	myExpandLabel;
	protected 	Label 	myColorLabel;
	protected 	Label	myCloseLabel;

	protected	Button	myAddPanelButton;

	protected 	ArrayList<UIManagerContentChangedListener> 	myUIContentListener;
	protected	ArrayList<DataChangedListener> 				myDataChangedListeners;


	public EventExpandPanel(Composite parent, int style, String name, EventListManager evenListManager ) 
	{		
		super(parent, style );
		myManager = evenListManager;
		myEvent = new Event( name );
		isExpanded = true;	// default state for EventExpandPanel is expanded
		myUIContentListener = new ArrayList<UIManagerContentChangedListener>();
		setupUI();
		attachListeners();
	}

	// create an EventExpandPanel without an Event (to be set later)
	public EventExpandPanel(Composite parent, int style, EventListManager evenListManager ) 
	{		
		super(parent, style );
		myManager = evenListManager;
		isExpanded = true;	// default state for EventExpandPanel is expanded
		myUIContentListener = new ArrayList<UIManagerContentChangedListener>();
		setupUI();
		attachListeners();
	}

	
	@Override
	protected void setupUI()
	{
		//this.setBackground( Colors.GRAY );
		this.setBackground( this.getParent().getBackground() );
		this.setLayout( new FormLayout() );
		
		Composite labelComposite = new Composite( this, SWT.NONE );
		labelComposite.setLayout( new FormLayout() );
		labelComposite.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100));

		myExpandLabel	= new Label( labelComposite, SWT.NONE );
		myColorLabel	= new Label( labelComposite, SWT.NONE );
		myTextLabel 	= new Label( labelComposite, SWT.NONE );
		myCloseLabel 	= new Label( labelComposite, SWT.NONE );

		FormData expandLabelFD = FormDataMaker.makeFormData(0, 2, 100, -2, 0,2, (Integer)null, 0);
		myExpandLabel.setLayoutData( expandLabelFD );
		myExpandLabel.setBackground( labelComposite.getBackground() );
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			if ( this.isExpanded )
				this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_OPEN ));
			else 
				this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_CLOSED ));
		}
		else
		{
			myExpandLabel.setText(" > ");
			myExpandLabel.setForeground( Colors.DARK_RED );
		}
		
		FormData imageLabelFD = FormDataMaker.makeFormData(0, 2, 100, -2, myExpandLabel ,2, (Integer)null, 0);
		imageLabelFD.width = 16;
		myColorLabel.setLayoutData( imageLabelFD );
		myColorLabel.setBackground( labelComposite.getBackground() );
		
		// Add Date constraint display for the whole Event 
		Point singleCharSize = UIUtils.getDefaultCharSize(this);
		myDateDisplay = new SkinnyDateConstraintDisplay( labelComposite, new DefaultDateStruct(), SWT.None );
		FormData dateDisplayFD 		= FormDataMaker.makeFormData( 0, 0, 100, 0, (Integer)null, 0, myCloseLabel, 0);
			dateDisplayFD.width 	= NUM_CHARS_IN_DATE_DISPLAY * singleCharSize.x;
		myDateDisplay.setLayoutData( dateDisplayFD );
		myDateDisplay.setActive( true );
		myDateDisplay.addListener( this );
		
		if ( myEvent != null)
			myTextLabel.setText( myEvent.getName() );
		myTextLabel.setLayoutData( FormDataMaker.makeFormData( 50, -myTextLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, 100, 0, myColorLabel, 2, myDateDisplay, 0) );
				
		myCloseLabel.setLayoutData( FormDataMaker.makeFormData(0, 2, 100, -2, (Integer)null, 0, 100, 0) );
		
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			myCloseLabel.setImage( Images.getEclipseImagesByKey( ISharedImages.IMG_ETOOL_DELETE_DISABLED ) );
		}
		else
		{
			myCloseLabel.setText(" X ");
			myCloseLabel.setForeground( Colors.DARK_RED );
		}

		
		myGroupPanelsPanel = new Composite( this, SWT.NONE );
		myGroupPanelsPanel.setLayout( new FormLayout() );
		myGroupPanelsPanel.setLayoutData( FormDataMaker.makeFormData( labelComposite, 100, 0, 100 ));
		myGroupPanelsPanel.setBackground( myGroupPanelsPanel.getParent().getBackground() );
		
		myGroupManager = new EventExpandPanelGroupManager( myGroupPanelsPanel, this );
		//myGroupManager.addListener( groupBindingControlPanel );							// link GroupManager with BindingControlPanel
		myGroupManager.addPanels( Settings.getInstance().getTemporalQueryNumGroups() );  	// create x number of groups as prescribed by Settings
		
		// grab groups in the GroupManager and add them to this.myEvent
		if ( myEvent != null )
			synchWithGroupManagersGroups();
		
		myGroupManager.addUIContentListener( this );
		myGroupManager.addDataChangedListener( this );
		
		myAddPanelButton = new Button( myGroupPanelsPanel, SWT.PUSH );
		try
		{
			myAddPanelButton.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ADD ));
			myAddPanelButton.setText( ADD_GROUP );
		}
		catch ( IllegalStateException e )
		{
			myAddPanelButton.setText( "+ " + ADD_GROUP );
		}
		updateAddPanelButtonLayout();
		
		contractedHeight 	= labelComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
		expandedHeight 	= this.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
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
		this.myColorLabel.addMouseListener( expandContractClicker );
		this.myExpandLabel.addMouseListener( expandContractClicker );
		
		MouseTrackListener expandContractHoverer = new MouseTrackListener()
		{
			@Override
			public void mouseEnter(MouseEvent e) 
			{ setHot(); }
			
			@Override
			public void mouseExit(MouseEvent e) 
			{ 
				if ( !QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
					return;
				if ( isExpanded )
					myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_OPEN) );
				else
					myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_CLOSED ) );

			}
			
			@Override
			public void mouseHover(MouseEvent e) 
			{ setHot(); }
			
			private void setHot()
			{
				if ( !QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
					return;
				if ( isExpanded )
					myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_OPEN_ACTIVE) );
				else
					myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_CLOSED_ACTIVE) );
			}
		};
		
		// Allow Mouse-over indicator
		this.myExpandLabel.addMouseTrackListener( expandContractHoverer );
		this.myTextLabel.addMouseTrackListener( expandContractHoverer );
		this.myColorLabel.addMouseTrackListener( expandContractHoverer );
		this.myExpandLabel.addMouseTrackListener( expandContractHoverer );

		
		// Allow Event addition
		myAddPanelButton.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  {
				  myGroupManager.addPanels( 1, EventExpandPanel. this.isUsingGroupSpecificConstraints(), EventExpandPanel.this.getStartDate(), EventExpandPanel.this.getEndDate()); //bugbug: need to change the boolean to be whether the current setting is use_individual_panel_date or event_set_date
				  EventExpandPanel.this.layout( true );
			  }
		});

		// Allow Event removal
		myCloseLabel.addMouseListener( new MouseAdapter()
		{
			public void mouseUp(MouseEvent e) 
			{
				myManager.removePanel( EventExpandPanel.this );
			}
		});
		
		// mousing over on close label shows hot icon
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
	
	private void autoSetExpanderIcon(  )
	{
			if ( this.isExpanded )
				this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_OPEN_ACTIVE ));
			else 
				this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_CLOSED_ACTIVE ));
	}
	
	public void setEvent( Event event )
	{
		myEvent = event;
		myTextLabel.setText( myEvent.getName() );
		myGroupManager.removeAllPanels();
		myGroupManager.addPanels( myEvent );
	}
	
	public void autoSetPanelName( )
	{
		myTextLabel.setText( myEvent.getName() );
		myTextLabel.redraw();
	}
	
	public Event getEvent()					{ return this.myEvent; }

	// whether the event in the EvnetPanl has any terms in its groups
	public boolean hasContent()				{ return myEvent.hasContent(); }
	
	private void updateAddPanelButtonLayout() 
	{
		GroupPanel lastPanel = myGroupManager.getLastPanel();
		//Point panelSize = myAddPanelButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		if ( lastPanel != null )
			myAddPanelButton.setLayoutData( FormDataMaker.makeFormData( lastPanel, EventExpandPanelGroupManager.SMALL_PANEL_SPACING, (Integer)null, -GROUP_PANEL_MARGIN, (Integer)null, 0, 100, 0 ));
		else
			myAddPanelButton.setLayoutData( FormDataMaker.makeFormData( 0, EventExpandPanelGroupManager.SMALL_PANEL_SPACING, (Integer)null, -GROUP_PANEL_MARGIN, (Integer)null, 0, 100, 0 ));
		((FormData)myAddPanelButton.getLayoutData()).height =  myAddPanelButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;		
	}

	public void recomputeExpandedHeight()
	{
		expandedHeight = this.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
	}
	
	public void setEnabled( boolean flag )
	{
		super.setEnabled( flag );
		myAddPanelButton.setEnabled( flag );
		for ( Control c : this.myGroupPanelsPanel.getChildren() )
			c.setEnabled( flag );
	}
	
	
	public void addUIContentListener( UIManagerContentChangedListener list )
	{ this.myUIContentListener.add( list ); }
	public void notifyUIContentListeners()
	{
		for ( UIManagerContentChangedListener list : this.myUIContentListener )
			list.groupManagerContentChanged( this );
	}
	
	@Override /* overriding default swt dispose method */
	public void dispose()
	{
		super.dispose();
		myUIContentListener.clear();
		myUIContentListener = null;			// purge link to UIContentlistener
		myManager	= null;					// purge link to manager		
	}

	
	@Override /* UIManagerContentChangedListener: for when a GroupPanel is removed/added by the GroupManager */
	public void groupManagerContentChanged(Object object) 
	{
		updateAddPanelButtonLayout();
		recomputeExpandedHeight();
		((FormData)this.getLayoutData()).height = expandedHeight;
		this.myGroupPanelsPanel.layout( true );
		this.layout( true );
		this.getParent().layout( true );
				
		synchWithGroupManagersGroups();	// grab groups in the GroupManager and add them to this.myEvent
		autoSetEventColor();			// check to see if this Event still has content		
		notifyUIContentListeners(); // notify listeners that Groups are added/memoved
	}
	
	private void synchWithGroupManagersGroups()
	{
		ArrayList <Group> groups = myGroupManager.makeGroupData();
		this.myEvent.removeAllGroups();
		this.myEvent.addAllGroups( groups );
	}
	
	/*
	 * DateRangeChangeListeneer method
	 */
	@Override 
	public void dateRangeChanged(GregorianCalendar startDate, GregorianCalendar endDate) 
	{ constraintChanged( (startDate==null) && (endDate==null) ); }

	/*
	 * QueryDateConstraintDelegator methods
	 */
	@Override
	public GregorianCalendar getStartDate() 
	{ return myDateDisplay.getStartDate(); }
	@Override
	public GregorianCalendar getEndDate() 
	{ return myDateDisplay.getEndDate(); }
	@Override
	public boolean isUsingGroupSpecificConstraints() 
	{ return  ((getStartDate()==null) && (getEndDate()==null)); }

	@Override
	public void constraintChanged(boolean useGroupSpecificConstraints) 
	{
		//System.err.println("constraintChanged: " + useGroupSpecificConstraints );
		if ( useGroupSpecificConstraints )
			myGroupManager.useGroupSpecificDateConstraint();
		else
			myGroupManager.useCentralDateConstraint( getStartDate(), getEndDate() );
	}

	@Override /* DataChangedListener: listen when a Group's data is changed (add/remove term, binding change, etc)*/
	public void dataChanged(Object source) 
	{
		autoSetEventColor();				// update coloring of Events 
		myManager.dataChanged( source ); 	// propagate the change to Manager 
	}

	private void autoSetEventColor()
	{
		// if Event has content, show color!
		if ( this.myEvent.hasContent() )
			myColorLabel.setBackground( Colors.getEventColor( myEvent ));			
		else
			myColorLabel.setBackground( myColorLabel.getParent().getBackground() );
	}

	/* QueryToolPanelComposite method 
	@Override 
	protected void setActive(boolean flag) 
	{
		myGroupPanelsPanel.setEnabled( flag );
		for ( Control c : myGroupPanelsPanel.getChildren() ) // tell each GroupPanel to be enabled/disabled
			c.setEnabled( flag );
	}
	*/
}


class EventExpandPanelGroupManager extends GroupManager
{
	public  static final int SMALL_PANEL_SPACING = 0;
	private static final int PANEL_INDENT = 30;

	public EventExpandPanelGroupManager(Composite parent, QueryDateConstraintProvider provider) 
	{
		super(parent, provider );
	}

	@Override // all panel layout data are produced here
	protected FormData makeLayoutData( Control previousControl )
	{
		if ( previousControl == null )
			return FormDataMaker.makeFormData( 0, 0, (Integer)null, 0, 0, PANEL_INDENT , 100, 0 );
		else
			return FormDataMaker.makeFormData( previousControl, SMALL_PANEL_SPACING, (Integer)null, 0, 0, PANEL_INDENT, 100, 0 );
	}

	@Override // we want the 1st panel to be a GroupPanelForTemporalEvent instead of a plain old GroupPanel
	public void addPanels( int number, boolean isEnabled, GregorianCalendar startDate, GregorianCalendar endDate )
	{
		Control previousControl = null;
		if ( myGroupPanels.size() != 0)
			previousControl = myGroupPanels.get( myGroupPanels.size()-1 );
		int counter = myGroupPanels.size()+1;
		for ( int i = 0; i < number; i++)
		{
			GroupPanel panel = null;
			if ( counter != 1 ) // if not the first panel 
				panel = new GroupPanel( myParent, SWT.None, this, UIConst.GROUP + " " + counter, isEnabled, startDate, endDate, 0, UIConst.GREATER_THAN );
			else				// if it is the first panel
				panel = new GroupPanelForTemporalEventAnchor( myParent, SWT.None, this, UIConst.GROUP + " " + counter, isEnabled, startDate, endDate, 0, UIConst.GREATER_THAN );
			if ( !isEnabled )
				panel.useCentralDateConstraint(  startDate, endDate );
			FormData fd = makeLayoutData( previousControl);
			fd.height= getDefaultPanelHeight();
			panel.setLayoutData( fd );
			previousControl = panel;
			myGroupPanels.add( panel );
			counter++;
		}
		notifyUIContentChangeListeners();
	}
	
	public void addPanels( Event event )
	{
		List<Group> groups = event.getGroups();
		Control previousControl = null;
		if ( myGroupPanels.size() != 0)
			previousControl = myGroupPanels.get( myGroupPanels.size()-1 );
		int counter = myGroupPanels.size()+1;
		for ( int i = 0; i < groups.size(); i++)
		{
			Group group = groups.get(i);
			group.setGroupBindingPolicyProvider( myGroupBindingPolicyProvider ); // set GroupBindingPolicyProvider for each new Group according to this GroupManager
			myGroupBindingPolicyProvider.forceConformity( group );
			GroupPanel panel = null;
			if ( counter != 1 ) // if not the first panel 
				panel = new GroupPanel( myParent, SWT.None, group, this, UIConst.GROUP + " " + counter, event.isUsingGroupSpecificDates(), group.getStartDate(), group.getEndDate(), group.getNumber(), group.getOperator() );
			else				// if it is the first panel
				panel = new GroupPanelForTemporalEventAnchor( myParent, SWT.None, group, this, UIConst.GROUP + " " + counter, event.isUsingGroupSpecificDates(), group.getStartDate(), group.getEndDate(), group.getNumber(), group.getOperator() );
			//GroupPanel panel = new GroupPanel( myParent, SWT.None, group, this, UIConst.GROUP + " " + (counter+i), event.isUsingGroupSpecificDates(), group.getStartDate(), group.getEndDate(), group.getNumber(), group.getOperator() );
			if ( !event.isUsingGroupSpecificDates() )
				panel.useCentralDateConstraint(  event.getStartDate(), event.getEndDate() );
			FormData fd = makeLayoutData( previousControl);
			fd.height= getDefaultPanelHeight();
			panel.setLayoutData( fd );
			previousControl = panel;			
			myGroupPanels.add( panel );
			counter++;
		}
		notifyUIContentChangeListeners();
	}

	/* tell this Groupmanager that Grouppanel's layout/size have changed. This GroupManager may need to do some extra work */
	public void panelLayoutChanged()
	{ 
		notifyUIContentChangeListeners();
	}
	
	/*
	 * 	Methods subclasses can override to adjust layout parameters 
	 */
	@Override
	protected int getDefaultPanelHeight()
	{
		return UIConst.SHORT_GROUPPANEL_HEIGHT;
	}

	@Override /* DataChangedListener */
	public void dataChanged(Object source) 
	{
		for ( DataChangedListener list : myDataChangedListeners)
			list.dataChanged( source );
	}

}
