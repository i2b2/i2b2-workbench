package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryResultTypeType;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.QueryResultTypeSelectionDialog.QueryResultTypeMouseListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.QueryResultTypeSelectionDialog.SelectableQueryResultTypeItem;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IPreQueryDataProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Query;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryDataIntegrityChecker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryTaskResult;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IQueryTimingProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.TaskResult;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryTaskResult.StatusCode;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.QueryResultObtainedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.DaemonThreadFactory;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.AbstractSlideWithTransitionControls;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.DefaultSlideWithTransitionControls;
import edu.harvard.i2b2.query.data.QueryFactory;

public class QuerySubmissionPanel extends AbstractSlideWithTransitionControls implements UIConst, QueryResultObtainedDelegator
{
	private static final String CANCEL_MSG = "Your last query was cancelled."; //\nIts results may still display in Previous Queries\nas the server already began to process it.";
	
	private QueryToolMainUI		myQTUI;

	private Button 				myQuerySubmissionButtion;
	private Label				myCancelQueryLabel;
	private Label				myQueryStatusLabel;
	private Label				myQueryNameLabel;
	private Text				myQueryNameText;
	
	// For analysis selection Table
	private Label								myAnalysisLabel;
	private TableViewer							myTableViewer;
	private TableViewerColumn 					myColumn;
	private QueryResultTypeMouseListener		myTableMouseListener;	// for mouse clicks on the table
	
	private Composite							myTransitionControlComposite; 
	
	// executor service that submits queries
	private ScheduledExecutorService 	myQueryExecutor = Executors.newSingleThreadScheduledExecutor( new DaemonThreadFactory() );
	private Future<QueryTaskResult>		myQueryHandle	= null;
	
	// executor service that refreshes query status
	private ScheduledExecutorService 	myQueryIndicatorExecutor 	= Executors.newSingleThreadScheduledExecutor( new DaemonThreadFactory() ); 
	private ScheduledFuture<?>			myQueryIndicatorHandle		= null;
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
					myQueryStatusLabel.setText( PROCESSING + dots);
				}
			});			
		}};

	
	public QuerySubmissionPanel(Composite parent, QueryToolMainUI ui, int style) 
	{		
		super(parent, style);
		myQTUI = ui;
		setupUI();
		attachListeners();
	}

	private void setupUI() 
	{
		this.setLayout( new FormLayout() );
		
		ScrolledComposite scroller = new ScrolledComposite( this, SWT.V_SCROLL);
		scroller.setLayout( new FormLayout() );
		scroller.setLayoutData( FormDataMaker.makeFullFormData() );
		
			Composite scrollerContent = new Composite( scroller, SWT.None );
			scrollerContent.setLayout( new FormLayout() );
			scrollerContent.setLayoutData( FormDataMaker.makeFullFormData() );
		
		scroller.setContent( scrollerContent );
		scroller.setExpandHorizontal( true );
		scroller.setExpandVertical( true );


		myQueryNameLabel= new Label( scrollerContent, SWT.NONE );
		myQueryNameText = new Text( scrollerContent, SWT.BORDER );

		myQueryNameLabel.setText( NAME_QUERY_TEXT );
		myQueryNameLabel.setLayoutData( FormDataMaker.makeFormData( 0, 20, (Integer)null, 0, 0, 20, (Integer)null, 0 ) );
		myQueryNameText.setLayoutData( FormDataMaker.makeFormData( 0, 20-(myQueryNameText.computeSize(SWT.DEFAULT, SWT.DEFAULT).y-myQueryNameLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y)/2, (Integer)null, 0, myQueryNameLabel, 2, 100, -20 ) );

		myAnalysisLabel = new Label( scrollerContent, SWT.NONE );
		myAnalysisLabel.setText( SELECT_ANALYSIS_TYPES_TEXT );
		myAnalysisLabel.setLayoutData( FormDataMaker.makeFormData( myQueryNameText, 10, (Integer)null, 0, 0, 20, (Integer)null, 0 ) );
		
		ArrayList<SelectableQueryResultTypeItem> analyses = QueryResultTypeSelectionDialog.getInstance().getSelectableQueryResultTypes();
		
	    myTableViewer = new TableViewer( scrollerContent, SWT.FULL_SELECTION  | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL );	
	    myTableViewer.getTable().setLayoutData( FormDataMaker.makeFormData( myQueryNameText, 10, (Integer)null, 0, myAnalysisLabel, 2, 100, -20 ) );
	    myTableViewer.getTable().setLinesVisible( true );
	    myTableViewer.getTable().setHeaderVisible( false );
	    myTableViewer.setUseHashlookup( true );

		myColumn = new TableViewerColumn( myTableViewer, SWT.None );
		myColumn.setLabelProvider( QueryResultTypeSelectionDialog.getInstance().new ResultTypeLabelProvider( myTableViewer ) );
    		
		myTableViewer.setContentProvider( QueryResultTypeSelectionDialog.getInstance().new ResultTypeContentProvider() );
		myTableViewer.setInput( analyses );
		initializeTableMouseListener(); 
		
		scroller.setMinHeight( scrollerContent.computeSize( SWT.DEFAULT, SWT.DEFAULT).y );
	}
	
	private void attachListeners()
	{
		// ensure the width of the column is the width of the table
		myTableViewer.getTable().addControlListener( new ControlAdapter()
		{
			@Override
			public void controlResized(ControlEvent e) 
			{ myColumn.getColumn().setWidth( myTableViewer.getTable().getClientArea().width ); }
		});
	}
	
	
	private void attachSubmitQueryListeners()
	{
		myQuerySubmissionButtion.addSelectionListener( new SelectionAdapter()
		{
			private ArrayList<String> 				selectedAnalysisNames 			= null;
			private ArrayList<QueryResultTypeType> 	unhandledAnalyses 				= null;
			private Boolean 						isUsingGraphicalAnalysis		= null;		
			private Boolean 						isUsingTimeLine					= null;
			
			@Override 
			public void widgetSelected( SelectionEvent e )
			{
				String queryMode = UIConst.TEMPORAL_QUERY_MODE;
				TaskResult ts = QueryDataIntegrityChecker.getInstance().isTemporalQueryWellFormed(myQTUI.getTemporalModePanel());				
				if ( !ts.isSuccess() )
				{
					UIUtils.popupError("Error", ts.getMessage() + "\n\nWill send a Get Everone query instead.", ts.getReason() );
					queryMode = UIConst.GET_EVERYONE;
				}
				
				// set selectedAnalysisNames, unhandledAnalyses, isUsingGraphicalAnalysis, isUsingTimeLine
				setQueryResultPreference();
				// show error if preference includes analysis types that we don't know how to handle
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
				// sync Population on BasicQueryModelPanel if necessary
				myQTUI.autoSyncPopulations();
				// submit query
				handleQuerySubmission( queryMode, selectedAnalysisNames, isUsingGraphicalAnalysis, isUsingTimeLine, myQTUI.getBasicModePanel(), myQTUI.getTemporalModePanel(), myQTUI.getBasicModePanel() );
			}
			
			private void setQueryResultPreference() 
			{
				selectedAnalysisNames 		= new ArrayList<String>();
				unhandledAnalyses 			= new ArrayList<QueryResultTypeType>();	
				isUsingGraphicalAnalysis	= Boolean.FALSE;		
				isUsingTimeLine				= Boolean.FALSE;
				
				@SuppressWarnings("unchecked")
				ArrayList<SelectableQueryResultTypeItem> analyses = (ArrayList<SelectableQueryResultTypeItem>)myTableViewer.getInput();
				
				//ArrayList<SelectableQueryResultTypeItem> analyses = QueryResultTypeSelectionDialog.getInstance().getSelectableQueryResultTypes();
				for ( SelectableQueryResultTypeItem analysisType : analyses )
				{
					String resultTypeName = analysisType.getQueryType().getName();
					if (analysisType.isSelected() && !selectedAnalysisNames.contains( resultTypeName ) )
					{
						if (analysisType.getQueryType().getDisplayType() == null )
						{
							unhandledAnalyses.add( analysisType.getQueryType() );
							continue;
						}
						if ( analysisType.getQueryType().getDisplayType().equalsIgnoreCase( QueryResultTypeSelectionDialog.GRAPHICAL_ANALYSIS_SIGN )) 
							isUsingGraphicalAnalysis = true;
						if ( analysisType.getQueryType().getDescription().equalsIgnoreCase( QueryResultTypeSelectionDialog.TIMELINE_DESCRIPTION ))
							isUsingTimeLine = true; 
						selectedAnalysisNames.add( resultTypeName );
					}
				}

			}
		});
		
		// canceling of a query is initiated
		myCancelQueryLabel.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e) 
			{
				if ( myQueryHandle != null )
					myQueryHandle.cancel( true );
				// cancel indicator updates
				myQueryIndicatorHandle.cancel( true ); 
				myQueryIndicatorHandle = null;
				
				// update status
				myQueryStatusLabel.setForeground( Colors.LIGHT_LIGHT_GRAY );
				myQueryStatusLabel.setText(CANCEL_MSG);
				
				myQuerySubmissionButtion.setEnabled( true );	// re-enable submission button
				myCancelQueryLabel.setVisible( false );			// re-hide the cancel button
				UIUtils.refreshPreviousQuery();					// display the submitted query
			}
		});
	}
	
	private void initializeTableMouseListener()
	{
		// remove old listeners
		if ( myTableMouseListener != null)
		{
			myTableViewer.getTable().removeListener( SWT.MouseDown, myTableMouseListener );
			myTableViewer.getTable().removeListener( SWT.MouseUp, myTableMouseListener );
		}
		
		myTableMouseListener  = QueryResultTypeSelectionDialog.getInstance().new QueryResultTypeMouseListener( myTableViewer );
		myTableViewer.getTable().addListener(SWT.MouseDown,	myTableMouseListener);	// to handle col selection
		myTableViewer.getTable().addListener(SWT.MouseUp,	myTableMouseListener);	// to handle col selection
	}

	
	private void handleQuerySubmission( final String queryMode, final ArrayList<String> resultTypeNames, boolean isUsingGraphicalAnalysis, boolean isUsingTimeline, IPreQueryDataProvider basicPreQueryDataProvider, IPreQueryDataProvider temporalPreQueryDataProvider, IQueryTimingProvider queryTimingProvider )
	{
		// make the query given querymode, analysis result types, and content of the group panels
		Query query = QueryFactory.getInstance().makeQuery( this.myQueryNameText.getText(), queryMode, resultTypeNames, isUsingGraphicalAnalysis, isUsingTimeline, basicPreQueryDataProvider, temporalPreQueryDataProvider, queryTimingProvider, this );

		this.myQuerySubmissionButtion.setEnabled( false ); 	// disable submit button
		this.myCancelQueryLabel.setVisible( true ); 		// show cancel button
		myQueryStatusLabel.setForeground( Colors.LIGHT_LIGHT_GRAY );
		myQueryStatusLabel.setText("Processing");
		
		myQueryIndicatorHandle = myQueryIndicatorExecutor.scheduleAtFixedRate(myQueryIndicatorRunner, 0, 250, TimeUnit.MILLISECONDS ); 
		// submit and process query on the background. See Query's call() method
		myQueryHandle = myQueryExecutor.submit( query );
	}

	
	public void autoSetQueryName( IPreQueryDataProvider temporalPreQueryDataProvider )
	{
		this.myQueryNameText.setText( QueryFactory.makeTemporalQueryNameWithMarkers( temporalPreQueryDataProvider.getPreQueryData().getEventData()) );
	}

	/* Deal with normal completion of a query submission (not canceled) */
	@Override /* QueryResultObtainedDelegator method */
	public void queryResultObtained(final QueryTaskResult result) 
	{
		myQueryIndicatorHandle.cancel( true );
		myQueryIndicatorHandle = null;
		if ( result.isSuccess() )
		{
			Display.getDefault().asyncExec( new Runnable()
			{
				@Override
				public void run() 
				{
					myQueryStatusLabel.setForeground( Colors.LIGHT_LIGHT_GRAY );
					myQueryStatusLabel.setText("Patient(s) Returned: " + result.getFormattedResultCount());
					myQuerySubmissionButtion.setEnabled( true );	// re-enable submission button
					myCancelQueryLabel.setVisible( false );			// re-hide the cancel button
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
						myQueryStatusLabel.setForeground( Colors.DARK_RED );
						myQueryStatusLabel.setText("Your last query resulted in an error." );					
						UIUtils.popupError(UIUtils.AN_ERROR_HAS_OCCURRED, result.getMessage(), result.getReason() );
						myQuerySubmissionButtion.setEnabled( true );	// re-enable submission button
						myCancelQueryLabel.setVisible( false );			// re-hide the cancel button
					}
					else if ( result.getStatusCode() == StatusCode.TIMEOUT )
					{
						myQueryStatusLabel.setForeground( Colors.LIGHT_LIGHT_GRAY );
						myQueryStatusLabel.setText("Your last query's results will be in Previous Queries when finished." );					
						UIUtils.popupMessage(UIUtils.QUERY_TIMED_OUT, result.getMessage() + "\n\n" + "Reason: "+result.getReason() );
						myQuerySubmissionButtion.setEnabled( true );	// re-enable submission button
						myCancelQueryLabel.setVisible( false );			// re-hide the cancel button
						UIUtils.refreshPreviousQuery();					// display the submitted query
					}
				}
			});			
		}
	}

	@Override
	public Composite getNextTransitionControl(Composite parent, Control leftControl) 
	{
		if ( this.myTransitionControlComposite == null )
		{
			myTransitionControlComposite = new Composite( parent, SWT.NONE );
			myTransitionControlComposite.setLayout(new FormLayout());
			myTransitionControlComposite.setLayoutData( FormDataMaker.makeFormData( 0, 0, 100, 0, leftControl, 20, 100, 0 ));
			myTransitionControlComposite.setBackground( myTransitionControlComposite.getParent().getBackground() );
			
			myQuerySubmissionButtion = new Button( myTransitionControlComposite, SWT.PUSH );
			myQuerySubmissionButtion.setText( SUBMIT_QUERY );
			myQuerySubmissionButtion.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_TOOL_FORWARD ) );
			
			myCancelQueryLabel		= new Label( myTransitionControlComposite, SWT.NONE );
			myCancelQueryLabel.setText("");
			myCancelQueryLabel.setToolTipText("Cancel the running query.");
			myCancelQueryLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_ELCL_STOP ) ); // use Eclipse's stop icon
			myCancelQueryLabel.setVisible( false );
			myCancelQueryLabel.setBackground( myCancelQueryLabel.getParent().getBackground()  );
			
			myQueryStatusLabel		= new Label( myTransitionControlComposite, SWT.NONE );
			myQueryStatusLabel.setBackground( myQueryStatusLabel.getParent().getBackground() );
			
			myQuerySubmissionButtion.setLayoutData( FormDataMaker.makeFormData(50, -myQuerySubmissionButtion.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0 , 0, 0, (Integer)null, 0));
			myCancelQueryLabel.setLayoutData( FormDataMaker.makeFormData(50, -(myQuerySubmissionButtion.computeSize(SWT.DEFAULT, SWT.DEFAULT).y-myCancelQueryLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y)/2, (Integer)null, 0 , myQuerySubmissionButtion, 6, (Integer)null, 0) );
			myQueryStatusLabel.setLayoutData( FormDataMaker.makeFormData(50, -(myQuerySubmissionButtion.computeSize(SWT.DEFAULT, SWT.DEFAULT).y-myCancelQueryLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y)/2, (Integer)null, 0 , myQuerySubmissionButtion, myCancelQueryLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 6 + 2, 100, 0) );
			
			// attach listeners for the submit query button and its helper widgets
			attachSubmitQueryListeners();
		}
		return myTransitionControlComposite;
	}

	@Override
	public Composite getPrevTransitionControl(Composite parent, Control rightControl) 
	{ return null; }

	@Override
	public void setNextTransitionControlVisible(boolean flag) 
	{ myTransitionControlComposite.setVisible( flag ); }

	@Override
	public void setPrevTransitionControlVisible(boolean flag) {/*do nothing*/}

	@Override /* AbstractSlideWithTransitionControls */
	public void performPreSlideActions(int toSlideIndex) {/*do nothing*/}

	@Override /* AbstractSlideWithTransitionControls */
	public void performPostSlideActions(int fromSlideIndex) {/*do nothing*/}
}
