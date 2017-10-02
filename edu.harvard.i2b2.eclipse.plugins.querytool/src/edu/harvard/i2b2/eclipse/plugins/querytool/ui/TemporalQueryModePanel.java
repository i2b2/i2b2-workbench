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
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.BasicQueryModePanel.PopulationSetType;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IGroupBindingPolicyProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IPreQueryDataProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PopulationAutoSynchronizer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PopulationLoader;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PopulationProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PreQueryData;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryDateConstraintSynable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.TemporalRelationship;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.EventSelectedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.PopulationTypeChangedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.QueryDroppedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.DefaultSlideWithTransitionControls;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.ExpandBar;
import edu.harvard.i2b2.query.data.DragAndDrop2XML;

public class TemporalQueryModePanel extends DefaultSlideWithTransitionControls implements UIConst, EventSelectedDelegator, UIManagerContentChangedListener, DataChangedListener, IPreQueryDataProvider, SplitterManager, PopulationProvider, PopulationLoader, QueryDroppedDelegator
{
	//public static final int		EDIT_EVENT_MODE = 0;
	//public static final int		EDIT_RELATIONSHIP_MODE = 1;
		
	
	class InstructionHeader extends ExpandBar
	{		
		protected	Label	myExpandLabel;
		protected	Label	myInstruction1;

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
			
			myInstruction1			= new Label( detailComposite, SWT.NONE );
						
			myTextLabel.setText("Define Temporal Relationships on right side, using Events defined on the left side.");
			myInstruction1.setText("You must define at least one Temporal Relationship before you can submit your query. ");
						
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

			myTextLabel.setLayoutData( FormDataMaker.makeFormData(0, 2, (Integer)null, 0, myExpandLabel, 2, (Integer)null , 0) );
			myInstruction1.setLayoutData( FormDataMaker.makeFormData(0, 2, (Integer)null, 0, 0, 2, 100, 0 ) );

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
						myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP) );
					else
						myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN ) );
				}
				
				@Override
				public void mouseHover(MouseEvent e) 
				{ setHot(); }
				
				private void setHot()
				{
					if ( !QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
						return;
					if ( isExpanded )
						myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP_ACTIVE) );
					else
						myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN_ACTIVE) );
				}
			};
			
			// Allow Mouse-over indicator
			this.myExpandLabel.addMouseTrackListener( expandContractHoverer );
			this.myTextLabel.addMouseTrackListener( expandContractHoverer );
			this.myExpandLabel.addMouseTrackListener( expandContractHoverer );

		}
		
		private void autoSetExpanderIcon(  )
		{
				if ( this.isExpanded )
					this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_UP_ACTIVE ));
				else 
					this.myExpandLabel.setImage( Images.getImageByKey( Images.EXPANDER_DOWN_ACTIVE ));
		}
	}

	
	
	private Composite						leftContainer;
		private SashForm 					myTopBotSplitter;
		private EventListControlPanel		myEventListControlPanel;
		private PopulationControlPanel 		myPopulationControlPanel;
		
		//private EventInclusionControlPanel 	myEventInclusionControlPanel;
	
	private Composite			titleComp;
	private Label				titleLabel;
	
	private Composite									myMainComposite;
		private DefineTemporalRelationshipPanel			myDefineRelationshipComp;
	
	private ArrayList<DataChangedListener> 				myDataChangedListeners;	// listen for Event modifications
	private ArrayList<UIManagerContentChangedListener> 	myContentListeners; 	// listen for adding/removing temporal relationships
	
	private PopulationAutoSynchronizer					myPopulationSynchronizer;
	
	
	// IGroupBindingPolicyProvider, QueryDateConstraintDelegator are for constructing PopulationControlPanel
	public TemporalQueryModePanel(Composite parent, int style, IGroupBindingPolicyProvider policyProvider, QueryDateConstraintSynable dateSyncable, PopulationTypeChangedDelegator populationTypeChangedDelegator, PopulationAutoSynchronizer synchronizer ) 
	{
		super(parent, style);
		myDataChangedListeners 		= new ArrayList<DataChangedListener>();
		myContentListeners			= new ArrayList<UIManagerContentChangedListener>();
		myPopulationSynchronizer 	= synchronizer;
		setupUI( policyProvider, dateSyncable, populationTypeChangedDelegator);
		attachListeners();
	}

	private void setupUI( IGroupBindingPolicyProvider groupBindingPolicyProvider, QueryDateConstraintSynable dateSyncable, PopulationTypeChangedDelegator populationTypeChangedDelegator ) 
	{
		this.setLayout( new FormLayout() );
		
		InstructionHeader header = new InstructionHeader( this, SWT.NONE );
		header.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100) );
		((FormData)header.getLayoutData()).height = header.getPreferredContractedHeight() ; // set the formdata height because EventExpandBar works off that

		SashForm leftRightSplitter = new SashForm( this, SWT.HORIZONTAL | SWT.SMOOTH );	
		leftRightSplitter.setLayout( new FormLayout() );
		leftRightSplitter.setLayoutData(  FormDataMaker.makeFormData(header, 100, 0, 100)  );
		
		/* initialize LEFT side */
		leftContainer = new Composite( leftRightSplitter, SWT.None );
		leftContainer.setLayout( new FormLayout() );
		leftContainer.setLayoutData( FormDataMaker.makeFullFormData() );
		leftContainer.setBackground( Colors.DARK_GRAY );	//BUGBUG color for debugging
		
		myTopBotSplitter = new SashForm( leftContainer, SWT.VERTICAL | SWT.SMOOTH );	
		myTopBotSplitter.setLayout( new FormLayout() );
		myTopBotSplitter.setLayoutData(  FormDataMaker.makeFormData(0, 100, 0, 100)  );
		
		myEventListControlPanel	= new EventListControlPanel( myTopBotSplitter, SWT.NONE, this, this );
		myEventListControlPanel.addDataChangedListener( this );	// listens for Events' Groups' adding/removing Terms so we can color TemporalRelationshipPanel's color labels
		myEventListControlPanel.addUIContentListener( this );	// listens for Events in EventList change so we can update TemporalRelationshipPanel combo widgets

		myPopulationControlPanel = new PopulationControlPanel( myTopBotSplitter, SWT.NONE, groupBindingPolicyProvider, this, populationTypeChangedDelegator );
		myPopulationControlPanel.setBackground( Colors.DARK_BLUE );

		myEventListControlPanel.setLayoutData( FormDataMaker.makeFormData(0 , 100, 0, 100 ) );
		myPopulationControlPanel.setLayoutData( FormDataMaker.makeFormData(0, 100, 0, 100 )  );

		/* Initializing the RIGHT SIDE */
		Composite rightComp = new Composite( leftRightSplitter, SWT.NONE );
		rightComp.setLayout( new FormLayout() );
		rightComp.setLayoutData( FormDataMaker.makeFullFormData() );
		
		titleComp = new Composite( rightComp, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );
		
		titleLabel = new Label( titleComp, SWT.LEFT );
		titleLabel.setText( DEFINE_RELATIONSHIPS );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 0, 0, (Integer)null, 0));
		
		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );

		myMainComposite = new Composite( rightComp, SWT.NONE );
		myMainComposite.setLayout( new FormLayout() );
		myMainComposite.setLayoutData( FormDataMaker.makeFormData( titleComp, 100, 0, 100 ));
		myMainComposite.setBackground( Colors.DARK_GRAY );
		
		myDefineRelationshipComp 	= new DefineTemporalRelationshipPanel( myMainComposite, SWT.NONE, myEventListControlPanel );
		myDefineRelationshipComp.setLayoutData( FormDataMaker.makeFullFormData() ); 
		leftRightSplitter.setWeights(new int[] {50, 50});
		
		// minimize PopulationControPanel
		int height = myTopBotSplitter.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y + 5;
		int percentage = Math.round( (float)(height-this.myPopulationControlPanel.getTitleHeight()-this.myTopBotSplitter.getSashWidth())/(float)height * 100 ) ;
		myTopBotSplitter.setWeights( new int[]{ percentage, (100-percentage) });
	}

	private void attachListeners() 
	{
		myDefineRelationshipComp.addUIContentListener( this ); 		// to listen for temporal relationship addition/removal
		myDefineRelationshipComp.addDataChangedListener( this ); 	// listen for temporal relationship edits
		
		myTopBotSplitter.addControlListener( new ControlListener()
		{
			@Override
			public void controlMoved(ControlEvent e) 
			{/* do nothing */}

			@Override
			public void controlResized(ControlEvent e) 
			{ 
				if ( myPopulationControlPanel.isCollapsed() )
					TemporalQueryModePanel.this.maintainCollapsedHeight( myPopulationControlPanel.getTitleHeight() ); 
			}
		});
	}

	/* Deal with UIContentListeners */
	public void addUIContentListener( UIManagerContentChangedListener listener )
	{ myContentListeners.add( listener ); }
	public boolean removeListener( UIManagerContentChangedListener listener )
	{ return myContentListeners.remove( listener ); }
	public void removeAllListeners()
	{ myContentListeners = new ArrayList<UIManagerContentChangedListener>(); }
	private void notifyContentChangeListeners( Object source ) // listeners include: TemporalQueryModePanel
	{
		// if source == EventListControLPanel, then an Event has been added/removed
		// if source == DefineTemporalRelationshipPanel, then a Temporal Relationship has been added/removed
		for ( int i = 0; i < myContentListeners.size(); i++ )
			myContentListeners.get(i).groupManagerContentChanged( this );
	}
	
	/* Deal with DataChangedListeners */
	public void addDataChangedListener( DataChangedListener list )
	{ this.myDataChangedListeners.add( list ); }		
	public boolean removeDataChangedListener( DataChangedListener list )
	{ return this.myDataChangedListeners.remove( list ); }
	// notify TemporalQueryModePanel that Groups's content has changed or that a Temporal Relationship's content has changed
	private void notifyDataChangedListeners( Object source ) // source could be EventListeManager or DefineTemporalRelationshipPanel
	{
		for ( DataChangedListener list : myDataChangedListeners)
			list.dataChanged( source );
	}

	/* Consolidate GroupPanels in myPopulationControlPanel */
	public void consolidatePopulationPanels() 
	{ this.myPopulationControlPanel.consolidatePanels(); }

	public void updatePopulationType( PopulationProvider popProvider ) 
	{ this.myPopulationControlPanel.updatePopulationType( popProvider ); }
	
	// Returns the UI element myPopulationControlPanel, which is also a DateConstraintSyncable
	public PopulationControlPanel getPopulationControlPanel()
	{ return this.myPopulationControlPanel; }

	public void setTemporalQueryDefinition( QueryDefinitionType queryDefinitionType ) // query is dropped in BasicQueryModePanel
	{
		try
		{
			Event.resetCounter();
			ArrayList<Event> events = DragAndDrop2XML.makeEventsWithQueryDefinition( queryDefinitionType );
			myEventListControlPanel.setEventData( events );
			this.myDefineRelationshipComp.removeAllTemporalRelationships();
			ArrayList<TemporalRelationship> temporalRelationships = DragAndDrop2XML.makeTemporalRelationshipsWithQueryDefinition( queryDefinitionType,  events );
			this.myDefineRelationshipComp.addTemporalRelationships( temporalRelationships );
			this.myEventListControlPanel.performPostQueryDropActions(); // update additional UI, etc
			//System.err.println("TemporalQueryModePanel.setTemporalQueryDefinition() -- place holder");
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	public void setNonTemporalQueryDefinition( QueryDefinitionType queryDefinitionType ) 
	{
		try
		{	// update PopulationControl with event
			this.myPopulationControlPanel.setDataWithQueryDefinition( queryDefinitionType );
		}
		catch ( Exception e )
		{
			UIUtils.popupError("Error Occurred During Query Drop",  "Cannot parse the dropped query.", "An IOException of a JDOMException has occurred. Either the query XML is not accessible or improperly formatted.\n(QueryToolMainUI.setNonTemporalQueryDefinition())" );
			e.printStackTrace();
		}
	}

	/*
	 * Clear Population. Reset UI.
	 */
	public void reset()
	{
		this.myEventListControlPanel.reset();
		this.myDefineRelationshipComp.reset();
		this.myPopulationControlPanel.reset();
		this.resetPopulation();
	}

	
	@Override /* EventSelectedDelegator - When an EventPanel on left is selected, we show Event Editor Mode*/
	public void eventSelected(Event event) {}

	/* Events in EventListControlPanl changed. Update the combos in RelationshipPanels. 
	   Or a Temporal Relationship is added/removed. 
	   In either case, Enable/disable SlideDeck's transition buttons by notify parent: QueryToolMainUI */
	@Override  
	public void groupManagerContentChanged( Object source ) 
	{
		if ( source == this.myEventListControlPanel )
		{
			ArrayList<Event> currentEvents = myEventListControlPanel.getEventData();
			myDefineRelationshipComp.groupManagerContentChanged(  new EventBundle( currentEvents ) );
			//this.myDefineRelationshipComp.dataChanged( new EventBundle( currentEvents ) );
		}
		else if ( source == this.myDefineRelationshipComp )
		{ /* Do nothing */ }
		this.notifyContentChangeListeners( source); // Tell QueryToolUI that an Event has been added/removed or that a temporal relationship has been added/removed
	}

	@Override /* Events in EventListControlPanl changed. Update the combos in RelationshipPanels */
	public void dataChanged(Object source) // source is EventListeManager
	{
		//BUGBUG: sometiems adding/removing a term in a group triggers this method twice. Should find out why.
		this.myDefineRelationshipComp.dataChanged( source);
		this.notifyDataChangedListeners( this ); // Tell QueryToolUI that an Event has been edited
	}

	
	@Override /* IPreQueryDataProvder */
	public PreQueryData getPreQueryData() 
	{
		return new PreQueryData( myEventListControlPanel.getEventData(), myDefineRelationshipComp.getTemporalRelationshipData(), false );
		//return new PreQueryData( myEventListControlPanel.getEventData(), myDefineRelationshipComp.getTemporalRelationshipData(), myEventInclusionControlPanel.areAllEventsIncluded() );
	}

	@Override /* SplitterManager methods */
	public boolean toggleAndReturnCollapseState( int targetMinHeight ) 
	{
		int height = myTopBotSplitter.getClientArea().height;
		if ( height <= 100 )
		{
			myTopBotSplitter.setWeights( new int[]{60,40});
			return false;
		}
		
		if ( this.myPopulationControlPanel.isCollapsed() ) // it's already collapsed at bottom
		{
			myTopBotSplitter.setWeights( new int[]{60,40});
			return false; 	// it's NOT collapsed
		}
		else
		{
			int percentage = Math.round( (float)(height-targetMinHeight-this.myTopBotSplitter.getSashWidth())/(float)height * 100 ) ;
			myTopBotSplitter.setWeights( new int[]{ percentage, (100-percentage) });
			return true;	// it is now collapsed
		}
	}
	
	@Override
	public void maintainCollapsedHeight( int targetMinHeight )
	{
		int height = myTopBotSplitter.getClientArea().height;
		int percentage = Math.round( (float)(height-targetMinHeight-this.myTopBotSplitter.getSashWidth())/(float)height * 100 ) ;
		myTopBotSplitter.setWeights( new int[]{ percentage, (100-percentage) });
	}

	@Override /* PopulationLoader methods */
	public void loadPopulation(Event event) 
	{ this.myPopulationControlPanel.loadPopulation( event ); }
	@Override
	public void resetPopulation() 
	{ this.myPopulationControlPanel.resetPopulation( ); }

	@Override /* PopulationProvider methods */
	public Event getPopulation() 
	{ return this.myPopulationControlPanel.getPopulation(); }

	@Override /* PopulationProvider methods */
	public Long getPopulationTimestamp() 
	{ return this.myPopulationControlPanel.getPopulationTimestamp(); }

	@Override /* PopulationProvider methods */
	public PopulationSetType getPopulationType() 
	{ return this.myPopulationControlPanel.getPopulationType(); }
	
	@Override /* DefaultSlideWithTransitionControls */
	public void performPreSlideActions( int toSlideIndex ) 
	{
		HashMap<String, String> nameMap = this.myEventListControlPanel.renameEvents();
		/*
		System.err.println("TemporalQueryModePanel.performPreSlideActions(): nameMap is:");
		for ( String key : nameMap.keySet() )
		{
			System.err.println( key + " -> " + nameMap.get(key));
		}
		*/
		this.myDefineRelationshipComp.autoResetEventNames( nameMap );
	}


	@Override /* DefaultSlideWithTransitionControls */ 
	public void performPostSlideActions(int fromSlideIndex) 
	{
		if ( fromSlideIndex == QueryToolMainUI.POPULATION_SLIDE_INDEX ) // if swtiching from BasicQueryModePanel
			this.myPopulationSynchronizer.autoSyncPopulations();
	}

	@Override /* QueryDroppedDelegator */
	public void queryDropped(QueryDefinitionType queryDefinitionType) // Query is dropped in EventListControlPane
	{
		if ( queryDefinitionType == null )
		{
			UIUtils.popupError("Error Occurred During Drop",  "You can not drop that here.", "Only previous queries may be dropped here." );
			return;
		}

		if ( queryDefinitionType.getQueryName().startsWith( GET_EVERYONE ) ||  queryDefinitionType.getQueryName().startsWith( ALL_PATIENTS )) // get everyone query
			setGetEveryoneQueryDefinition();
		else
		{
			setNonTemporalQueryDefinition( queryDefinitionType ); 	// setup non-temporal portion of the query (population)
			setTemporalQueryDefinition( queryDefinitionType );		// setup temporal portion of the query (Events and Temporal Relationships) 
		}
		this.myEventListControlPanel.performPostQueryDropActions();
	}


	private void setGetEveryoneQueryDefinition() 
	{
		this.myPopulationControlPanel.resetPopulation(); // clear query in population control panel
	}

	
	/*===========================================
	 * Main Method
	 *===========================================*/
	public static void main( String [] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );

		QueryDateConstraintSynable dateSynable = new QueryDateConstraintSynable()
		{
			@Override
			public GregorianCalendar getStartDate()  
			{ return null; }

			@Override
			public GregorianCalendar getEndDate() 
			{ return null; }

			@Override
			public boolean isUsingGroupSpecificConstraints() 
			{ return false; }

			@Override
			public void constraintChanged(boolean useGroupSpecificConstraints) 
			{ System.err.println("TemopralQueryModePanel.main() TEST: QueryDateConstraintDelegator.constraintchanged.");}

			@Override
			public void syncTo(QueryDateConstraintSynable syncSource) 
			{ /* do nothing */ }
		};
		
		PopulationProvider populationProvider = new PopulationProvider()
		{
			@Override
			public Event getPopulation() 
			{ return null; }

			@Override
			public PopulationSetType getPopulationType() 
			{ return null; }

			@Override
			public Long getPopulationTimestamp() 
			{ return null; }
		};
		
		PopulationTypeChangedDelegator ptcDelegator = new PopulationTypeChangedDelegator()
		{
			@Override
			public void populationTypeChanged(PopulationSetType newType) 
			{ /* do nothing */ }
		};
		
		TemporalQueryModePanel gp = new TemporalQueryModePanel( myShell, SWT.None, IGroupBindingPolicyProvider.DEFAULT_POLICY, dateSynable, ptcDelegator, null );
		gp.setLayoutData( FormDataMaker.makeFullFormData() );

		myShell.setSize( 800, 700 );

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

/* Utility wrapper class  for ArrayList<Event> */
class EventBundle
{
	private ArrayList<Event> myEvents = null;
	
	public EventBundle( ArrayList<Event> events )
	{ myEvents = events; }
	
	public ArrayList<Event> getEvents()
	{ return myEvents; }
}


interface SplitterManager
{
	// returns true if collapsed. false if not.
	public boolean toggleAndReturnCollapseState( int targetMinHeight );
	public void maintainCollapsedHeight( int targetMinHeight );
}
