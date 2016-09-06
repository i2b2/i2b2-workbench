/*
 * Copyright (c) 2006-2016 Partners HealthCare 
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
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryDefinitionType;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.PopulationAutoSynchronizer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryDataIntegrityChecker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.QueryDroppedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.SlideDeckListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.SlideDeck;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.SlideDeckWithControl;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.SlideDeckTransitionPolicy;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.SlideDeck.Direction;
import edu.harvard.i2b2.eclipse.plugins.querytool.views.QueryToolView;
import edu.harvard.i2b2.query.data.DragAndDrop2XML;

public class QueryToolMainUI extends Composite implements UIConst, QueryDroppedDelegator, SlideDeckListener, UIManagerContentChangedListener, DataChangedListener, PopulationAutoSynchronizer
{
	public static final int POPULATION_SLIDE_INDEX 			= 0;
	public static final int TEMPORAL_DEFINITION_SLIDE_INDEX = 1;
	public static final int SUBMISSION_SLIDE_INDEX 			= 2;
	
	private int	MAIN_UI_TITLE_HEIGHT		= 30;
	private int	QUERY_SUBMIT_PANEL_HEIGHT 	= 26;
	
	//private ExecutorService 	myQueryExecutor = Executors.newSingleThreadExecutor( new DaemonThreadFactory() );	
	// updates the query indicator
	//private ScheduledExecutorService 	myQueryIndicatorExecutor 	= Executors.newSingleThreadScheduledExecutor( new DaemonThreadFactory() ); 
	//private ScheduledFuture<?>			myQueryIndicatorHandle		= null;
	/*
	private Runnable myQueryIndicatorRunner = new Runnable()
	{
		int counter = 0;
		@Override
		public void run() 
		{
			StringBuffer buff = new StringBuffer();
			for ( int i = 0; i < counter; i++ )
				buff.append('.');
			final String dots = buff.toString();
			counter = (counter+1)%4;
			Display.getDefault().asyncExec( new Runnable()
			{
				public void run() 
				{
					myQueryResultLabel.setText( PROCESSING + dots);
				}
			});			
		}};
	*/

	private QueryToolView		myViewPart;

	private Composite 	myInterfaceContainer;
	private Composite 	myHeader;
	private Composite 	myFooter;
	private Composite 	myBody;

	// header components
	private Composite 	myQueryNameComposite;
	private Label		myQueryLabel;
	private ArrayList<Label> myStepLabels; 		
	//private Combo		myQueryModeCombo;

	// body components
	private SlideDeckWithControl 	myInnerDeck;		// all main UIs are contained in a slide deck
	private BasicQueryModePanel 	myBasicModePanel;
	private TemporalQueryModePanel	myTemporalModePanel;
	private QuerySubmissionPanel	myQuerySubmissionPanel;
	private Label					myGetAllPatientsLabel;

	private Hyperlink				myClearQueryLink;
	private Label					myClearQueryLabel;
	
	// footer components
	/*
	private Button		mySubmitQueryButton;
	private Label		myQueryResultLabel;
	private Label		myToSpecifyResultTypesLabel;
	private Button		myToSpecifyResultTypesButton;	
	private boolean		myUseAnalysisTypesPreference = false;
	*/

	public QueryToolMainUI( QueryToolView view, Composite parent, int style ) 
	{
		super(parent, style);
		// check if CRC cell is up and running, if not, setup error message and go no further
		if ( QueryResultTypeSelectionDialog.getInstance().isCRCDown() )
			setupCRCDownUI();
		else
		{
			myViewPart = view;
			setupUI();
			attachListeners();
		}
	}

	private void setupCRCDownUI()
	{
		this.setLayout( new FormLayout() );
		if ( QueryResultTypeSelectionDialog.getInstance().isCRCDown() )
		{
			Composite comp = new Composite( this, SWT.NONE);
			comp.setLayout( new FormLayout() );
			comp.setLayoutData( FormDataMaker.makeFullFormData() );
			comp.setBackground( Colors.DARK_RED );
			
			Label label = new Label( comp, SWT.NONE );
			label.setBackground( label.getParent().getBackground() );
			label.setForeground( Colors.WHITE );
			label.setText( CRC_DOWN_MESSAGE );
			label.setLayoutData( FormDataMaker.makeFormData( 50, -label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 50, -label.computeSize(SWT.DEFAULT, SWT.DEFAULT).x/2, (Integer)null, 0 ));
		}		
	} 

	private void setupUI() 
	{
		//QueryToolWelcomeScreen welcomeScreen = new QueryToolWelcomeScreen( this );
		//this.addNewSlide( welcomeScreen );
		
		this.setLayout( new FormLayout() );
		myInterfaceContainer = new Composite( this, SWT.NONE );
		myInterfaceContainer.setLayoutData( FormDataMaker.makeFullFormData());
		
		setupInterfaceContainer();
	}

	
	private void setupInterfaceContainer() 
	{
		myInterfaceContainer.setLayout( new FormLayout() );
		
		// setup Header
		Composite header = new Composite( myInterfaceContainer, SWT.NONE );
		header.setLayout( new FormLayout() );
		header.setBackground( Colors.BLACK );
		
		myClearQueryLink = new Hyperlink( header, SWT.NONE );
		myClearQueryLink.setBackground( header.getBackground() );
		myClearQueryLink.setForeground( Colors.WHITE );
		myClearQueryLink.setText( UIConst.CLEARQUERY );
		myClearQueryLink.setUnderlined( true );
		myClearQueryLink.setToolTipText( UIConst.CLEAR_QUERY_TOOLTIP_TEXT );
		myClearQueryLabel = new Label( header, SWT.NONE );
		myClearQueryLabel.setImage( Images.getImageByKey( Images.RETURN ));
		myClearQueryLabel.setBackground( header.getBackground() );
		myClearQueryLabel.setToolTipText( UIConst.CLEAR_QUERY_TOOLTIP_TEXT );

		Label separator = new Label( header, SWT.NONE );
		separator.setText("| ");
		separator.setBackground( header.getBackground() );
		separator.setForeground( Colors.GRAY );

		Label labelStep1 = new Label( header, SWT.NONE );
		labelStep1.setBackground( labelStep1.getParent().getBackground() );
		labelStep1.setForeground( Colors.ORANGE );								// initially, 1st slide is orange (current)
		labelStep1.setText("1. Define Population");

		Label labelStep2 = new Label( header, SWT.NONE );
		labelStep2.setBackground( labelStep2.getParent().getBackground() );
		labelStep2.setForeground( Colors.GRAY );								// others are gray (not traversed and not current)
		labelStep2.setText("2. Define Temporal Relationships");

		Label labelStep3 = new Label( header, SWT.NONE );
		labelStep3.setBackground( labelStep3.getParent().getBackground() );
		labelStep3.setForeground( Colors.GRAY );
		labelStep3.setText("3. Review and Submit Query");

		Label arrowTransition1 = new Label( header, SWT.NONE );
		arrowTransition1.setImage( Images.getImageByKey( Images.RIGHT_ARROW_SMALL_GRAY ));

		Label arrowTransition2 = new Label( header, SWT.NONE );
		arrowTransition2.setImage( Images.getImageByKey( Images.RIGHT_ARROW_SMALL_GRAY ));

		labelStep1.setLayoutData( FormDataMaker.makeFormData( 0, 0, (Integer)null, 0, 0, 2, (Integer)null, 0) );
		arrowTransition1.setLayoutData( FormDataMaker.makeFormData( 0, 0, (Integer)null, 0, labelStep1, 4, (Integer)null, 0) );
		labelStep2.setLayoutData( FormDataMaker.makeFormData( 0, 0, (Integer)null, 0, arrowTransition1, 6, (Integer)null, 0) );
		arrowTransition2.setLayoutData( FormDataMaker.makeFormData( 0, 0, (Integer)null, 0, labelStep2, 4, (Integer)null, 0) );
		labelStep3.setLayoutData( FormDataMaker.makeFormData( 0, 0, (Integer)null, 0, arrowTransition2, 6, (Integer)null, 0) );

		myClearQueryLink.setLayoutData( FormDataMaker.makeFormData(0, 0, (Integer)null, 0, (Integer)null, 0, 100, -2) );
		myClearQueryLabel.setLayoutData( FormDataMaker.makeFormData(0, 0, (Integer)null, 0, (Integer)null, 0, myClearQueryLink, 0) );
		separator.setLayoutData( FormDataMaker.makeFormData(0, 0, (Integer)null, 0, (Integer)null, 0, myClearQueryLabel, 0) );

		header.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100));

		// collect labels
		myStepLabels = new ArrayList<Label>(3);
		myStepLabels.add( labelStep1 );
		myStepLabels.add( labelStep2 );
		myStepLabels.add( labelStep3 );
		
		myInnerDeck = new SlideDeckWithControl( myInterfaceContainer, SWT.NONE );
		myInnerDeck.addListener( this );
		myInnerDeck.setLayoutData( FormDataMaker.makeFormData(header, 100, 0, 100) );
		myInnerDeck.setTransitionPolicy( new QueryConstructionPolicy() );
		
		myBasicModePanel = new BasicQueryModePanel( myInnerDeck, SWT.None, this, this );
		myInnerDeck.addNewSlide( myBasicModePanel );
		
		myTemporalModePanel = new TemporalQueryModePanel( myInnerDeck, SWT.NONE, this.myBasicModePanel.getGroupBindingPolicyProvider(), this.myBasicModePanel, this.myBasicModePanel.getPopulationTypeChangedDelegator(), this );
		myInnerDeck.addNewSlide( myTemporalModePanel );
		
		myQuerySubmissionPanel = new QuerySubmissionPanel( myInnerDeck, this, SWT.NONE );
		myInnerDeck.addNewSlide( myQuerySubmissionPanel );
		
		//Composite reviewComp = myInnerDeck.makeNewSlide();
		//reviewComp.setBackground( Colors.DARK_BLUE );		
	}
	
	private void attachListeners() 
	{
		myTemporalModePanel.addUIContentListener( this );
		myTemporalModePanel.addDataChangedListener( this );

		// add link to reset query
		myClearQueryLink.addHyperlinkListener( new HyperlinkAdapter()
		{
			@Override
			public void linkActivated(HyperlinkEvent event) 
			{ handleResetQuery(); }
		});
		
		ClearQueryLabelMouseListener labelMousListener = new ClearQueryLabelMouseListener( myClearQueryLabel );
		myClearQueryLabel.addMouseListener( labelMousListener );
		myClearQueryLabel.addMouseTrackListener( labelMousListener );
	}

	private void handleResetQuery()
	{
		QueryToolMainUI.this.myBasicModePanel.clear();			// clear all Groups/Panels
		QueryToolMainUI.this.myTemporalModePanel.reset();				
		myInnerDeck.resetFarthestSlideIndex();					// so the colors of the Steps will be gray after sliding
		myInnerDeck.slideTo( 0, Direction.LEFT );				
		QueryToolMainUI.this.myBasicModePanel.initialize();		// add back default 3 Groups
	}

	/*
	private void setupFooter()
	{
		myFooter.setLayout( new FormLayout() );
		mySubmitQueryButton = new Button( myFooter, SWT.PUSH );
		mySubmitQueryButton.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_TOOL_FORWARD ) );
				
		mySubmitQueryButton.setText( SUBMIT_QUERY );
		Point buttonSize	= mySubmitQueryButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );

		mySubmitQueryButton.setLayoutData( FormDataMaker.makeFormData( 50, -buttonSize.y/2, (Integer)null, 0, 50, -buttonSize.x/2, (Integer)null, 0) );
		
		myQueryResultLabel = new Label( myFooter, SWT.NONE );
		myQueryResultLabel.setBackground( myFooter.getBackground() );
		myQueryResultLabel.setForeground( Colors.GRAY );
		myQueryResultLabel.setLayoutData( FormDataMaker.makeFormData( 50, -myQueryResultLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y/2, (Integer)null, 0, mySubmitQueryButton, 8, 100, -4 ) );
		
		myToSpecifyResultTypesLabel = new Label( myFooter, SWT.NONE );
		myToSpecifyResultTypesLabel.setBackground( myFooter.getBackground() );
		myToSpecifyResultTypesLabel.setForeground( Colors.GRAY );
		myToSpecifyResultTypesLabel.setLayoutData( FormDataMaker.makeFormData( 50, -myQueryResultLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y/2, (Integer)null, 0, (Integer)null, 0, mySubmitQueryButton, -8 ) );
		myToSpecifyResultTypesLabel.setText("Let me select result types.");
		
		myToSpecifyResultTypesButton = new Button( myFooter, SWT.CHECK );
		myToSpecifyResultTypesButton.setBackground( Colors.BLACK );
		myToSpecifyResultTypesButton.setLayoutData( FormDataMaker.makeFormData( 50, -myToSpecifyResultTypesButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y/2, (Integer)null, 0, (Integer)null, 0, myToSpecifyResultTypesLabel, -2 ) );
		myToSpecifyResultTypesButton.setVisible( myUseAnalysisTypesPreference );
		myToSpecifyResultTypesLabel.setVisible( myUseAnalysisTypesPreference );
	}
	 */
	
	/*
	private void setupUIOld() 
	{
		this.setLayout( new FormLayout() );
		
		myHeader 	= new Composite( this, SWT.BORDER );		
		myBody		= new Composite( this, SWT.None );
		myFooter	= new Composite( this, SWT.None );
					
		myBody.setBackground( Colors.DARK_GRAY );
		myFooter.setBackground( Colors.BLACK );
		
		setupHeader();
		setupFooter();
		setupBody();
		
		FormData headerFD	= FormDataMaker.makeFormData(0, (Integer)null, 0, 100 );
			headerFD.height	= MAIN_UI_TITLE_HEIGHT;
	
		FormData footerFD	= FormDataMaker.makeFormData( (Integer)null, 100, 0, 100 );
			footerFD.height	= mySubmitQueryButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y + 4;
	
		FormData bodyFD	= FormDataMaker.makeFormData( myHeader, myFooter, 0, 100 );
		
		myHeader.setLayoutData( headerFD );
		myBody.setLayoutData( bodyFD );
		myFooter.setLayoutData( footerFD );
		
		autoSetUIPanelByQueryMode(); // automatically select the default query mode
	}
	*/
	/*
	private void setupHeader()
	{		
		myHeader.setLayout(new FormLayout() );
		
		myQueryNameComposite = new Composite( myHeader, SWT.None );
		myQueryNameComposite.setLayout( new FormLayout() );
		
		myQueryModeCombo	= new Combo( myHeader, SWT.READ_ONLY | SWT.FLAT | SWT.DROP_DOWN );
		myQueryModeCombo.add( NON_TEMPORAL_QUERY_MODE );
		myQueryModeCombo.add( TEMPORAL_QUERY_MODE );
		myQueryModeCombo.add( GET_EVERYONE );
		myQueryModeCombo.select( 0 );
		Point comboSize = myQueryModeCombo.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		
		// place layout data for QueryNameComposite and QueryModeCombo
		myQueryNameComposite.setLayoutData( FormDataMaker.makeFormData(0, 0, 100, 0, 0, 0, myQueryModeCombo, -5) );
		myQueryModeCombo.setLayoutData( FormDataMaker.makeFormData( 50, -comboSize.y/2, (Integer)null, 0, (Integer)null, 0, 100, -5) );

		myQueryLabel 		= new Label( myQueryNameComposite, SWT.None);
		myQueryLabel.setText( NAME_QUERY_TEXT );
		Point labelSize = myQueryLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );

		myQueryLabel.setLayoutData( FormDataMaker.makeFormData( 50, -labelSize.y/2, 100, 0, 0, 0, 100, 0) );
	}
	 */
	/*
	private void setupBody()
	{
		this.myBody.setLayout( new FormLayout() );
		myBasicModePanel = new BasicQueryModePanel( myBody, SWT.None, true );
		myBasicModePanel.setLayoutData( FormDataMaker.makeFullFormData() );		
		myBasicModePanel.setVisible( true );
		
		myTemporalModePanel = new TemporalQueryModePanel( myBody, SWT.None );
		myTemporalModePanel.setLayoutData( FormDataMaker.makeFullFormData() );
		myTemporalModePanel.setVisible( false );
		
		
		myGetAllPatientsLabel = new Label( myBody, SWT.WRAP | SWT.CENTER );
		myGetAllPatientsLabel.setText( "Click on the '" + SUBMIT_QUERY + "' button to \nretrieve all patients in the project." );
		myGetAllPatientsLabel.setBackground( myGetAllPatientsLabel.getParent().getBackground() );
		myGetAllPatientsLabel.setForeground( Colors.WHITE );
		myGetAllPatientsLabel.setLayoutData( FormDataMaker.makeFormData(50, -myGetAllPatientsLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 50, -myGetAllPatientsLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT).x/2,(Integer)null, 0));
		myGetAllPatientsLabel.setVisible( false );
		
		//myUIEnabledManager = new UIEnabledManager( myBody );		
	}
	*/
	/*
	private void attachListeners() 
	{
		this.myQueryModeCombo.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  { autoSetUIPanelByQueryMode(); }
		});

		this.mySubmitQueryButton.addSelectionListener( new SelectionAdapter()
		{
			@Override 
			public void widgetSelected( SelectionEvent e )
			{
				// obtain queryMode and consolidate panels if in non-temporal query mode
				String queryMode = myQueryModeCombo.getItem( myQueryModeCombo.getSelectionIndex() );				
				if ( queryMode.equals( UIConst.NON_TEMPORAL_QUERY_MODE ))
					myBasicModePanel.consolidatePanels();

				// perform integrity check on the query data
				TaskResult ts = QueryDataIntegrityChecker.getInstance().checkQueryData(queryMode, QueryToolMainUI.this.myBasicModePanel,  QueryToolMainUI.this.myTemporalModePanel );				
				if ( !ts.isSuccess() )
				{
					UIUtils.popupError("Error", ts.getMessage(), ts.getReason() );
					return;
				}
				
				// grab a handle of the singleton QueryResultTypeSelectionDialog
				QueryResultTypeSelectionDialog dialog = QueryResultTypeSelectionDialog.getInstance();
				
				// prompt for analysis type selection if we are using user's default types.
				if ( !myUseAnalysisTypesPreference )
				{
					// disable UI
					HashSet<Control> alreadyDisabledControls = new HashSet<Control>();
					UIUtils.recursiveSetEnabledAndRememberUnchangedControls(  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), false, alreadyDisabledControls );

					// show QueryResultTypeSelection Dialog				
					dialog.open( null );

					// re-enable UI
					UIUtils.recursiveSetEnabled( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true, alreadyDisabledControls );
					if ( dialog.isCanceled() ) // user canceled, do nothing
						return;

					myUseAnalysisTypesPreference = dialog.isUsingAnalysisTypesPreference();					
					myToSpecifyResultTypesButton.setSelection( false );
				 }

				// Deal with the case that there are missing plugins that cannot handle certain requested query result types.
				// Consult plugins.query.ui.MainPanel.jRunQueryButtonActionPerformed(...) for the original code 
				ArrayList<QueryResultTypeType> unhandledAnalyses = dialog.getUnhandledQueryResultType();
				StringBuffer analyses = new StringBuffer();
				if ( unhandledAnalyses != null && unhandledAnalyses.size() != 0 )
				{
					for ( int i = 0; i < unhandledAnalyses.size(); i++ )
					{
						analyses = analyses.append( unhandledAnalyses.get(i).getDescription() );
						if ( i < unhandledAnalyses.size()-1 )
							analyses = analyses.append(", ");
					}
					UIUtils.popupError("Error", "Cannot submit query", "The knowledge on how to run the query is missing for the following Analysis type(s): \n" + analyses.toString() );
					return;
				}
				
				handleQuerySubmission( queryMode, dialog.getSelectedQueryResultTypeNames(), dialog.isUsingGraphicalAnalysis(), dialog.isUsingTimeline(), QueryToolMainUI.this.myBasicModePanel,  QueryToolMainUI.this.myTemporalModePanel);
			}
		});
		
		myToSpecifyResultTypesButton.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{ myUseAnalysisTypesPreference = !myToSpecifyResultTypesButton.getSelection(); }
		});
		
		myToSpecifyResultTypesLabel.addMouseListener( new MouseAdapter()
		{
			public void mouseDown(MouseEvent e)
			{ myUseAnalysisTypesPreference = !myToSpecifyResultTypesButton.getSelection(); }
		});
		
		myQueryLabel 		= new Label( myQueryNameComposite, SWT.None);
		// make top query area as a drop area
		// Add Dropping actions to accept Query dropping on QueryLabel
		DropTarget labelTarget = new DropTarget( myQueryLabel, UIConst.DND_DROP_OPS );
		labelTarget.setTransfer( UIConst.DND_TRANSFER_TYPES );
		labelTarget.addDropListener( new QueryDropHandler( this ) );

		// Add Dropping actions to accept Query dropping on QueryNameComposite
		DropTarget compTarget = new DropTarget( myQueryNameComposite, UIConst.DND_DROP_OPS );
		compTarget.setTransfer( UIConst.DND_TRANSFER_TYPES );
		compTarget.addDropListener( new QueryDropHandler( this ) );
	}
	*/

	/*
	private void autoSetUIPanelByQueryMode()
	{
		  String selectedItem = myQueryModeCombo.getItem( myQueryModeCombo.getSelectionIndex() );
		  if ( selectedItem.equals(NON_TEMPORAL_QUERY_MODE) )
		  {					  
			  myBasicModePanel.setVisible( true );
			  myTemporalModePanel.setVisible( false );
			  myGetAllPatientsLabel.setVisible( false );
		  }
		  else if ( selectedItem.equals(TEMPORAL_QUERY_MODE) )
		  {					  
			  myBasicModePanel.setVisible( false );
			  myTemporalModePanel.setVisible( true );
			  myGetAllPatientsLabel.setVisible( false );			  
		  }
		  else // GET_EVERYONE
		  {
			  myBasicModePanel.setVisible( false );
			  myTemporalModePanel.setVisible( false );
			  myGetAllPatientsLabel.setVisible( true );
		  }
		  setFooterVisible( true );
	}
	*/
	
	public BasicQueryModePanel getBasicModePanel()
	{ return this.myBasicModePanel; }
	
	public TemporalQueryModePanel getTemporalModePanel()
	{ return this.myTemporalModePanel; }
	
	public QuerySubmissionPanel getQuerySubmissionPanel()
	{ return this.myQuerySubmissionPanel; }
	
	/*
	 * 1. Build a Query object
	 * 2. Disable query submission button
	 * 3. Submit Query and parse response on background thread
	 */
	/*
	private void handleQuerySubmission( final String queryMode, final ArrayList<String> resultTypeNames, boolean isUsingGraphicalAnalysis, boolean isUsingTimeline, IPreQueryDataProvider basicPreQueryDataProvider, IPreQueryDataProvider temporalPreQueryDataProvider )
	{
		// make the query given querymode, analysis result types, and content of the group panels
		Query query = QueryFactory.getInstance().makeQuery( queryMode, resultTypeNames, isUsingGraphicalAnalysis, isUsingTimeline, basicPreQueryDataProvider, temporalPreQueryDataProvider, this );
		//System.err.println( "QueryToolMainUI.mySubmitQueryButton.addSelectionListener: Request:\n" + MessageUtil.prettyFormat( query.getXML(), 5) );
		
		this.mySubmitQueryButton.setEnabled( false );
		myQueryResultLabel.setForeground( Colors.GOLDENROD );
		myQueryResultLabel.setText("Processing");
		
		myQueryIndicatorHandle = myQueryIndicatorExecutor.scheduleAtFixedRate(myQueryIndicatorRunner, 0, 250, TimeUnit.MILLISECONDS ); 
		// submit and process query on the background. See Query's call() method
		myQueryExecutor.submit( query );		
	}
	*/
	
	public void setFooterVisible( boolean flag )
	{
		if ( flag )
			((FormData)myFooter.getLayoutData()).height = QUERY_SUBMIT_PANEL_HEIGHT;
		else
			((FormData)myFooter.getLayoutData()).height = 0;
		this.layout();
	}
	
	public QueryToolView getView()
	{ 
		return this.myViewPart;
	}
	
	public String getNowString()
	{
		GregorianCalendar now = new GregorianCalendar();
		return (now.get( Calendar.MONTH )+1) + "/" + (now.get( Calendar.DAY_OF_MONTH)) + "/" + (now.get( Calendar.YEAR)) + " " + 
			   (now.get( Calendar.HOUR_OF_DAY )) + ":" + (now.get( Calendar.MINUTE )) + ":" + (now.get( Calendar.SECOND ))  + "." 
			   + (now.get( Calendar.MILLISECOND ));
	}

	/* QueryResultObtainedDelegator method */
	/*
	@Override 
	public void queryResultObtained(final QueryTaskResult result) 
	{
		//myQueryIndicatorHandle.cancel( true );
		//myQueryIndicatorHandle = null;
		if ( result.isSuccess() )
		{
			Display.getDefault().asyncExec( new Runnable()
			{
				@Override
				public void run() 
				{
					myQueryResultLabel.setForeground( Colors.GRAY );
					myQueryResultLabel.setText("Patient(s) Returned: " + result.getFormattedResultCount());
					mySubmitQueryButton.setEnabled( true );
					
					myToSpecifyResultTypesButton.setVisible( myUseAnalysisTypesPreference );
					myToSpecifyResultTypesLabel.setVisible( myUseAnalysisTypesPreference );					
				}
			});
		}
		else
		{
			Display.getDefault().asyncExec( new Runnable()
			{
				@Override
				public void run() 
				{
					if ( result.getStatusCode() == StatusCode.ERROR )
					{
						myQueryResultLabel.setForeground( Colors.DARK_RED );
						myQueryResultLabel.setText("Your last query resulted in an error." );					
						UIUtils.popupError(UIUtils.AN_ERROR_HAS_OCCURRED, result.getMessage(), result.getReason() );
						mySubmitQueryButton.setEnabled( true );
						
						myToSpecifyResultTypesButton.setVisible( myUseAnalysisTypesPreference );
						myToSpecifyResultTypesLabel.setVisible( myUseAnalysisTypesPreference );
					}
					else if ( result.getStatusCode() == StatusCode.TIMEOUT )
					{
						myQueryResultLabel.setForeground( Colors.GRAY );
						myQueryResultLabel.setText("Your last query's results will be in Previous Queries when finished." );					
						UIUtils.popupMessage(UIUtils.QUERY_TIMED_OUT, result.getMessage() + "\n\n" + "Reason: "+result.getReason() );
						mySubmitQueryButton.setEnabled( true );
						
						myToSpecifyResultTypesButton.setVisible( myUseAnalysisTypesPreference );
						myToSpecifyResultTypesLabel.setVisible( myUseAnalysisTypesPreference );
						
						UIUtils.refreshPreviousQuery();
					}
				}
			});			
		}
	}
	*/
	
	@Override
	/* QueryDroppedDelegaotr method: Allows a delegator to do some UI work after a previous query item has been dropped */
	public void queryDropped( QueryDefinitionType queryDefinitionType ) 
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
		/*
		if ( queryDefinitionType.getQueryName().startsWith( GET_EVERYONE ) || 
			 queryDefinitionType.getQueryName().startsWith( ALL_PATIENTS )) // get everyone query
			setGetEveryoneQueryDefinition();
		else if ( !queryDefinitionType.getPanel().isEmpty() ) // non-temporal query
			setNonTemporalQueryDefinition( queryDefinitionType );
		else  // temporal query 
			setTemporalQueryDefinition( queryDefinitionType );
		*/
	}

	// setting non-temporal query def
	private void setNonTemporalQueryDefinition( QueryDefinitionType queryDefinitionType) 
	{
		try
		{
			// programmatically set Population to be Patient-based or Encounter-based according to query timing 
			String queryTiming = queryDefinitionType.getQueryTiming().toLowerCase();
			if ( queryTiming.equals( ANY.toLowerCase()) )
				this.myBasicModePanel.setPopulationTypeChanged( BasicQueryModePanel.PopulationSetType.PATIENT );
			else if ( queryTiming.equals( SAMEVISIT.toLowerCase() ))
				this.myBasicModePanel.setPopulationTypeChanged( BasicQueryModePanel.PopulationSetType.ENCOUNTER );
			else
			{
				UIUtils.popupError("Error Occurred During Query Drop",  "Cannot parse the dropped query.", "Query Timing value '" + queryDefinitionType.getQueryTiming() + "' is not recognized." );
				return;
			}
			Event event = Event.makeAnonymouseEvent();
			DragAndDrop2XML.setEventWithQueryDefinition( event, queryDefinitionType );
			myBasicModePanel.initalizeWithEvent(event);
		}
		catch ( Exception e )
		{
			UIUtils.popupError("Error Occurred During Query Drop",  "Cannot parse the dropped query.", "An IOException of a JDOMException has occurred. Either the query XML is not accessible or improperly formatted.\n(QueryToolMainUI.setNonTemporalQueryDefinition())" );
			e.printStackTrace();
		}
	}
	
	private void setTemporalQueryDefinition(QueryDefinitionType queryDefinitionType) 
	{
		this.myTemporalModePanel.setTemporalQueryDefinition( queryDefinitionType );
	}
	
	private void setGetEveryoneQueryDefinition() 
	{
		this.myBasicModePanel.resetPopulation(); // clear query
	}

	@Override /* PopulationAutoSynchronizer -- Make sure BasicQueryModePanel has the latest Population */
	public void autoSyncPopulations()
	{	
		// make sure TemporalModePanel's PopulationControlPanel has the latest Population information
		if (this.myBasicModePanel.getPopulationTimestamp() > this.myTemporalModePanel.getPopulationTimestamp())
		{
			this.myTemporalModePanel.getPopulationControlPanel().syncTo( this.myBasicModePanel );
			this.myTemporalModePanel.getPopulationControlPanel().loadPopulation( this.myBasicModePanel.getPopulation() );
		}
		else // make sure BasicModePanel has the latest Population information 
		{
			this.myBasicModePanel.syncTo( this.myTemporalModePanel.getPopulationControlPanel() );
			this.myBasicModePanel.loadPopulation( myTemporalModePanel.getPopulation() );
		}
	}
	
	@Override /* SlideDeckLIstener method(s) */
	public void slideOccurred( SlideDeck source, SlideDeck.SlideEventType eventType, int fromIndex, int toIndex ) 
	{
		if ( source == this.myInnerDeck )
		{
			int currentIndex = this.myInnerDeck.getCurrentSlideIndex();
			if ( eventType == SlideDeck.SlideEventType.POST_SLIDE )
			{				
				for ( int i = 0; i < this.myInnerDeck.getNumSlides(); i++)
				{
					if ( i <= this.myInnerDeck.getFarthestSlideIndex() )
						this.myStepLabels.get(i).setForeground( Colors.WHITE );
					else
						this.myStepLabels.get(i).setForeground( Colors.GRAY );
				}			
				myStepLabels.get( currentIndex ).setForeground( Colors.ORANGE );
				
				if ( currentIndex == POPULATION_SLIDE_INDEX )
				{
					//this.myBasicModePanel.syncTo( this.myTemporalModePanel.getPopulationControlPanel() );
					//this.myBasicModePanel.loadPopulation( myTemporalModePanel.getPopulation() );
				}
				else if ( currentIndex == TEMPORAL_DEFINITION_SLIDE_INDEX )
				{
					//this.myTemporalModePanel.getPopulationControlPanel().syncTo( this.myBasicModePanel );
					//this.myTemporalModePanel.loadPopulation( myBasicModePanel.getPopulation() );
					//this.myTemporalModePanel.updatePopulationType( myBasicModePanel );
				}
				else if ( currentIndex == SUBMISSION_SLIDE_INDEX ) 									// if we just transitioned into submission slide
					this.myQuerySubmissionPanel.autoSetQueryName( this.myTemporalModePanel );		// set the default query name
			}
			else if ( eventType == SlideDeck.SlideEventType.PRE_SLIDE )
			{
				if ( currentIndex == POPULATION_SLIDE_INDEX ) 	// if currently at population slide before transition
					this.myBasicModePanel.consolidatePanels();	// consolidate Groups
				else if ( currentIndex == TEMPORAL_DEFINITION_SLIDE_INDEX)
					this.myTemporalModePanel.consolidatePopulationPanels();	// consolidate Groups
			}
		}
	}

	@Override
	public void groupManagerContentChanged(Object source) 
	{
		if ( source == this.myTemporalModePanel ) 		// a Temporal Relationship or an Event has been added/removed
			this.myInnerDeck.autoSetButtonsEnabled();	// make sure next buttons are properly set 
	}

	@Override	// Listen for an Event's edit or a Temporal Relationship's edit
	public void dataChanged(Object source) 
	{
		//System.err.println( "QTMainUI.datachanged: source = " + source );
		if ( source == this.myTemporalModePanel ) 		// an Event or a Temporal Relationship has been edited
			this.myInnerDeck.autoSetButtonsEnabled();	// make sure next SlideDeck transition buttons are properly set 
	}

	/* Transition Policy for InnerDeck */
	class QueryConstructionPolicy implements SlideDeckTransitionPolicy 
	{
		public static final int POPULATION_SLIDE 		= 0;
		public static final int TEMPORAL_QUERY_SLIDE 	= 1;
		public static final int SUBMISSION_SLIDE 		= 2;

		@Override
		public boolean canTransitionTo(int fromSlide, int toSlide) 
		{
			if ( fromSlide == POPULATION_SLIDE )
				return true;
			else if ( fromSlide == SUBMISSION_SLIDE )
				return true;
			else if ( fromSlide == TEMPORAL_QUERY_SLIDE )
			{
				if ( toSlide == POPULATION_SLIDE )
					return true;
				else if ( toSlide == SUBMISSION_SLIDE )
					return QueryDataIntegrityChecker.getInstance().isTemporalQueryWellFormed( QueryToolMainUI.this.myTemporalModePanel ).isSuccess();
			}
			return false; 	// forbid all other transitions
		}
	}

	class ClearQueryLabelMouseListener implements MouseListener, MouseTrackListener
	{
		private Label	myLabel = null;
		private boolean isArmed = false;
		
		public ClearQueryLabelMouseListener( Label label )
		{
			myLabel = label;
		}
		
		@Override
		public void mouseDoubleClick(MouseEvent e) {}

		@Override
		public void mouseDown(MouseEvent e) 
		{ isArmed = true; }

		@Override
		public void mouseUp(MouseEvent e) 
		{
			if ( isArmed )
			{
				handleResetQuery();
				isArmed = false;
			}
		}

		@Override
		public void mouseEnter(MouseEvent e) 
		{
			myLabel.setCursor( myLabel.getDisplay().getSystemCursor( SWT.CURSOR_HAND ));
		}
		@Override
		public void mouseExit(MouseEvent e)  
		{ 
			myLabel.setCursor( null );
			isArmed = false; 
		}
		@Override
		public void mouseHover(MouseEvent e) {}
		
	}
}
