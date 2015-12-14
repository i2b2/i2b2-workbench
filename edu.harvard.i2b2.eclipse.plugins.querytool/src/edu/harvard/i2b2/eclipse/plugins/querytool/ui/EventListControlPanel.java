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
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DropTarget;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IEventDataProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.EventSelectedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.QueryDroppedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.ExpandContractListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.QueryDropHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.MixedTextIconPanel;

public class EventListControlPanel extends QueryToolPanelComposite implements UIConst, UIManagerContentChangedListener, DataChangedListener, IEventDataProvider, ExpandContractListener
{
	private EventSelectedDelegator	myEventSelectedDelegator;
	
	private ScrolledComposite 	myScroller;
	private Composite			myEventComp;
	private Button				myAddEventButton;
		private Composite 		titleComp;
		private Label 			titleLabel;
		//private Label			myInstructions;			// instruction to add/edit Events
		private MixedTextIconPanel 	myDropInstructions; // instructions to drop query
		
	private EventListManager		myEventListManager;
	private QueryDroppedDelegator	myQueryDroppedDelegator; // deal with queryDropping
	
	private ArrayList<DataChangedListener> 				myDataChangedListeners;
	private ArrayList<UIManagerContentChangedListener> 	myUIContentListener;

	public EventListControlPanel(Composite parent, int style, EventSelectedDelegator delegator, QueryDroppedDelegator queryDroppedDelegator ) 
	{
		super(parent, style);
		myEventSelectedDelegator 	= delegator;
		myQueryDroppedDelegator		= queryDroppedDelegator;
		myDataChangedListeners 		= new ArrayList<DataChangedListener>();
		myUIContentListener	= new ArrayList<UIManagerContentChangedListener>();
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
		titleLabel.setText( EVENT_LIST );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 0, 0, (Integer)null, 0));
		
		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );
		
		myScroller = new ScrolledComposite( this, SWT.V_SCROLL | SWT.H_SCROLL);
		myScroller.setLayout( new FormLayout() );
		myScroller.setLayoutData( FormDataMaker.makeFormData( titleComp, 0, 100, 0, 0, 0, 100, 0) );		
		
			myEventComp = new Composite( myScroller, SWT.None );
			myEventComp.setLayout( new FormLayout() );
			myEventComp.setLayoutData( FormDataMaker.makeFullFormData() );
			myEventComp.setBackground( Colors.DARK_GRAY ); //bugbug coloring for debug
		
		myScroller.setExpandHorizontal( true );
		myScroller.setExpandVertical( true );
		myScroller.setContent( myEventComp );
		
		myEventListManager = new EventListManager( myEventComp, this, myEventSelectedDelegator );
		myEventListManager.addDataChangedListener( this );	// listen to an Event's change in content
		myEventListManager.addUIContentListener( this );	// listen to Event's data change (adding/removing Event's Groups' concepts)
				
		myAddEventButton = new Button( myEventComp, SWT.PUSH );		
		try
		{
			myAddEventButton.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ADD ));
			myAddEventButton.setText( ADD_EVENT );
		}
		catch ( IllegalStateException e)
		{
			myAddEventButton.setText( "+ " + ADD_EVENT );
		}
		/*
		myInstructions = new Label( myEventComp, SWT.WRAP );
		myInstructions.setLayoutData( FormDataMaker.makeFormData( myAddEventButton, GROUP_PANEL_MARGIN, (Integer)null, 2, 15, 4, 85, -4) );
		myInstructions.setBackground( myInstructions.getParent().getBackground() );
		myInstructions.setForeground( Colors.WHITE );
		myInstructions.setText( EVENT_LIST_INSTRUCTIONS );
		*/
		
		ArrayList<Object> items = new ArrayList<Object>();
		items.add( EVENT_LIST_INSTRUCTIONS ); 
		items.add( QUERY_DROP_INSTRUCTIONS_1 );
		items.add( Images.getImageByKey( Images.PREVIOUS_QUERY ));
		items.add( Images.getImageByKey( Images.PREVIOUS_TEMPORAL_QUERY ));
		items.add( QUERY_DROP_INSTRUCTIONS_2 );
		myDropInstructions = new MixedTextIconPanel( myEventComp, SWT.WRAP, items,  Colors.WHITE, myEventComp.getBackground() );
		myDropInstructions.setLayoutData( FormDataMaker.makeFormData(myAddEventButton, GROUP_PANEL_MARGIN, (Integer)null, -UIConst.GROUP_PANEL_MARGIN, 15, 0, 85, -4) );

		// add one Event by default
		myEventListManager.addPanels( 2 );
		
		updateAddPanelButtonLayout();

		myScroller.setMinHeight( myEventComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
	}

	private void attachListeners() 
	{
		myAddEventButton.addSelectionListener( new SelectionAdapter()
		{	@Override
			public void widgetSelected( SelectionEvent ev )
			{
				myEventListManager.addPanels( 1 );
			}
		});
		
		DropTarget target = new DropTarget( myEventComp, UIConst.DND_DROP_OPS );
		target.setTransfer( UIConst.DND_TRANSFER_TYPES );
		target.addDropListener( new QueryDropHandler( this.myQueryDroppedDelegator ) );
	}

	public void performPostQueryDropActions()
	{
		updateAddPanelButtonLayout();
	}
	
	private void updateAddPanelButtonLayout() 
	{
		EventExpandPanel lastLabel = myEventListManager.getLastPanel();
		Point buttonSize = myAddEventButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		if ( lastLabel != null )
			myAddEventButton.setLayoutData( FormDataMaker.makeFormData( lastLabel, GROUP_PANEL_MARGIN, (Integer)null, -GROUP_PANEL_MARGIN, 50, -buttonSize.x/2, (Integer)null, 0 ));
		else
			myAddEventButton.setLayoutData( FormDataMaker.makeFormData( 0, GROUP_PANEL_MARGIN, (Integer)null, -GROUP_PANEL_MARGIN, 50, -buttonSize.x/2, (Integer)null, 0 ));
		
		if ( myEventListManager.getNumberOfPanels() > Settings.getInstance().getItemCountHelpCutoff() )
		{
			//myInstructions.setVisible( false );
			myDropInstructions.setVisible( false );
		}
		else
		{
			//myInstructions.setVisible( true );
			myDropInstructions.setVisible( true );
		}
		
		((FormData)myAddEventButton.getLayoutData()).height =  myAddEventButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;		
	}

	
	// return a list of Events currently in myEventListManager. Each call creates a new Array (see myEventListManager.makeEventData())
	public ArrayList<Event> getEventData()
	{ return this.myEventListManager.makeEventData(); }


	public void setEventData( ArrayList<Event> events )
	{
		this.myEventListManager.removeAllPanels();
		this.myEventListManager.addPanels( events );
	}
	
	public HashMap<String, String> renameEvents()
	{ return this.myEventListManager.resetEventNames(); }
	
	public void relayout()
	{
		updateAddPanelButtonLayout();
		myEventComp.layout( true );
		myEventComp.redraw();
		myScroller.setMinHeight( myEventComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
	}
	
	public void reset()
	{
		this.myEventListManager.removeAllPanels();	// remove all event panels
		Event.resetCounter();						// reset event counters
		myEventListManager.addPanels( 2 );			// add back 2 events
		updateAddPanelButtonLayout();				// relayout
		myScroller.setMinHeight( myEventComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y ); // reset Scroller range
	}

	/* UIManagerContentChangedListener method. Content of EventList has changed (EventExpandPanel added/removed). Update UI, etc */
	@Override
	public void groupManagerContentChanged( Object source ) 
	{
		relayout();
		this.notifyUIContentListeners();
	}

	@Override
	protected void setActive( boolean flag )
	{
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
		
		for ( Control c: myEventComp.getChildren() ) // tell children to be enabled or not
			c.setEnabled( flag );
	}	


	public void addDataChangedListener( DataChangedListener list )
	{ this.myDataChangedListeners.add( list ); }
		
	public boolean removeDataChangedListener( DataChangedListener list )
	{ return this.myDataChangedListeners.remove( list ); }

	// notify TemporalQueryModePanel that Groups's contents are changed
	private void notifyDataChangedListeners( Object source ) // source is EventListeManager
	{
		for ( DataChangedListener list : myDataChangedListeners)
			list.dataChanged( source );
	}
	
	
	public void addUIContentListener( UIManagerContentChangedListener list )
	{ myUIContentListener.add( list ); }
	public boolean removeUIContentListener( UIManagerContentChangedListener list )
	{ return this.myUIContentListener.remove( list ); }
	
	// notify TemporalQueryModePanel that Events are added/removed
	private void notifyUIContentListeners()
	{
		for ( UIManagerContentChangedListener list : myUIContentListener)
			list.groupManagerContentChanged( this );
	}

	/*
	 * ExpandContractListener methods
	 * 	when EventExpandPanels are contracted/expanded, we readjust our scroll panel
	 */
	@Override
	public void controlExpanded(Control control) 
	{
		myScroller.setMinHeight( myEventComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
	}

	@Override
	public void controlContracted(Control control) 
	{
		myScroller.setMinHeight( myEventComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
	}
	
	
	@Override /* DataChangedListener method */
	public void dataChanged(Object source) 
	{
		notifyDataChangedListeners( source ); // propagate to TemporalQueryModePanel
	}

	@Override /* IEventDataProvider */
	public Event getEventByName(String name) 
	{ return this.myEventListManager.getEvent( name ); }

	@Override /* IEventDataProvider */
	public ArrayList<Event> getEvents() 
	{ return this.myEventListManager.getEvents(); } // just delegate
	
	
	public static void main( String [] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		EventListControlPanel gp = new EventListControlPanel( myShell, SWT.None, null, null );
		gp.setLayoutData( FormDataMaker.makeFullFormData() );
		
		myShell.setSize( 150, 500 );
		
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
