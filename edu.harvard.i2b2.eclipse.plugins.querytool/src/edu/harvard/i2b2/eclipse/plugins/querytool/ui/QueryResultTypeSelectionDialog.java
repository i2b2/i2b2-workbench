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

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.bind.JAXBElement;

import org.apache.axis2.AxisFault;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.common.util.jaxb.DTOFactory;
import edu.harvard.i2b2.common.util.jaxb.JAXBUnWrapHelper;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtil;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ApplicationType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.FacilityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageControlIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.MessageTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ProcessingIdType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ResponseMessageType;
import edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.SecurityType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmQryHeaderType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.PsmRequestTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryResultTypeType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ResultTypeResponseType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.UserType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.StatusType.Condition;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.query.utils.Messages;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.MessageErrorHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.table.EmulatedNativeCheckboxLabelProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.table.Selectable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.table.TableViewerMouseAdaptor;

import edu.harvard.i2b2.query.data.DataUtils;
import edu.harvard.i2b2.query.jaxb.utils.QueryJAXBUtil;
import edu.harvard.i2b2.query.serviceClient.QueryRequestClient;

/* This class is an adaption of edu.harvard.i2b2.query.ui.AnalysisPanel */
public class QueryResultTypeSelectionDialog implements UIConst
{
	/*
	 * Static methods and variables
	 */
	private static QueryResultTypeSelectionDialog myInstance;
	
	public static QueryResultTypeSelectionDialog getInstance()
	{
		if ( myInstance == null )
			myInstance = new QueryResultTypeSelectionDialog( SWT.BORDER );
		return myInstance;
	}

	// contact hive and prebuild the dialog UI so it pops up fast when 'submit query' is cilcked by user
	public static void preBuildUI()
	{
		if ( myInstance == null )
			myInstance = new QueryResultTypeSelectionDialog( SWT.BORDER );
	}

	public static final String	GRAPHICAL_ANALYSIS_SIGN = "CATNUM";
	public static final String	TIMELINE_DESCRIPTION	= "TimeLine";

	// known return types
	public static final String	PATIENT_SET 			= "PATIENTSET";
	public static final String	ENCOUNTER_SET			= "PATIENT_ENCOUNTER_SET";
	public static final String	NUM_OF_PATIENTS			= "PATIENT_COUNT_XML";
	public static final String	GENDER_BREAKDOWN		= "PATIENT_GENDER_COUNT_XML";
	public static final String	VITAL_STATUS_BREAKDOWN	= "PATIENT_VITALSTATUS_COUNT_XML";
	public static final String	RACE_BREAKDOWN			= "PATIENT_RACE_COUNT_XML";
	public static final String	AGE_BREAKDOWN			= "PATIENT_COUNT_XML";

	private ArrayList<SelectableQueryResultTypeItem> 	analyses;
	private boolean 									isCellDown = false;

	private boolean										isCanceled 				= false;	// whether user cancels the dialog
	private boolean 									isUsingGraphicAnalysis 	= false;
	private boolean										isUsingTimeline			= false;
	private ArrayList<QueryResultTypeType> 				missingTypes			= null;
	
	private Shell		myShell;
	private Composite	myMainComp;
	private Composite 	titleComp;
	private Label		titleLabel;

	private Composite 	myTableComposite;
	private Composite	myCRCDownComposite;
	private TableViewer	myTableViewer;
	private QueryResultTypeMouseListener		myTableMouseListener;	// for mouse clicks on the table

	private Button		myOKButton;
	private Button		myCancelButton;
	private Button		myUsePreferenceButton;
	private Label		myUserPreferenceLabel;	
	
	private QueryResultTypeSelectionDialog( int style )
	{
		initializeModel(  );
		setupUI( style );
		attachListeners( );
	}

	private void initializeModel()
	{		
		String response = null;
		try
		{
			response = QueryRequestClient.sendQueryRequestREST( makeResultTypeRequestXML() );
		}
		catch ( Exception e )
		{ 
			e.printStackTrace();
			if (  e instanceof AxisFault )
				UIUtils.popupError("Error", "Trouble with connection to the remote server.\nCcannot retrieve required startup information (available analysis types).\nThis is often a network error, please try again. Shutting down.", "Network Error" );
			else
				UIUtils.popupError("Error", "An error has occurred attempting to retrieve startup information  (available analysis types)\nfrom the remote server. Shutting down.", e.getMessage() );
			
			// system exit because we cannot get required information to setup the plugin
			System.exit(0);
		}
		
		analyses = new ArrayList<SelectableQueryResultTypeItem>();
		
		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		try 
		{
			JAXBElement jaxbElement = jaxbUtil.unMashallFromString(response);
			ResponseMessageType messageType = (ResponseMessageType) jaxbElement.getValue();
			String version = messageType.getMessageHeader().getSendingApplication().getApplicationVersion();
			System.setProperty("serverVersion", version);
			double vernum = Double.parseDouble(version);
			if (vernum < 1.4) 
			{
				QueryResultTypeType qtype = new QueryResultTypeType();
				qtype.setDescription( TIMELINE_DESCRIPTION );
				qtype.setName("PATIENTSET");
				qtype.setVisualAttributeType("LA");
				qtype.setDisplayType("LIST");
				analyses.add( new SelectableQueryResultTypeItem(qtype, false) );

				qtype = new QueryResultTypeType();
				qtype.setDescription("Number of patients");
				qtype.setName("PATIENT_COUNT_XML");
				qtype.setVisualAttributeType("LA");
				qtype.setDisplayType("CATNUM");
				analyses.add( new SelectableQueryResultTypeItem(qtype, true) );
			} 
			else 
			{
				BodyType bt = messageType.getMessageBody();
				ResultTypeResponseType resultTypes = (ResultTypeResponseType) new JAXBUnWrapHelper().getObjectByClass(bt.getAny(),ResultTypeResponseType.class);
				for (Condition status : resultTypes.getStatus().getCondition()) 
				{
					if (status.getType().equals("ERROR")) 
					{
						this.isCellDown = true;
						break;
					}
				}

				if (!this.isCellDown) 
				{
					for (int i = 0; i < resultTypes.getQueryResultType().size(); i++) 
					{
						QueryResultTypeType queryResultType = resultTypes.getQueryResultType().get(i);
						String desc = queryResultType.getDescription();
						boolean isSelected = false;
						if (desc.equalsIgnoreCase("Number of patients"))
							isSelected = true;
						else if(desc.equalsIgnoreCase( TIMELINE_DESCRIPTION ) && UserInfoBean.getInstance().isRoleInProject("DATA_LDS")) 
							isSelected = true;
						String va = queryResultType.getVisualAttributeType();
						if (va != null && !va.toLowerCase().endsWith("h")) 
						{
							analyses.add( new SelectableQueryResultTypeItem( queryResultType, isSelected ) );
							//System.err.println( queryResultType.getName() + " " + queryResultType.getDisplayType() + " " + queryResultType.getDescription() );
						}
					}
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			this.isCellDown = true;
		}
	}

	private void setupUI( int shellStyles )
	{
		myShell = new Shell( Display.getCurrent(), SWT.APPLICATION_MODAL | shellStyles );
		myShell.setLayout( new FormLayout() );
		
		myMainComp = new Composite( myShell, SWT.None );
		myMainComp.setLayoutData( FormDataMaker.makeFullFormData() );
		myMainComp.setLayout( new FormLayout() );

		titleComp = new Composite( myMainComp, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );

		titleLabel = new Label( titleComp, SWT.None );
		titleLabel.setText( SPECIFY_QUERY_RESULT_TYPE );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 50, -titleSize.x/2, (Integer)null, 0));

		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );

		
		Composite footer = new Composite( myMainComp, SWT.NONE );
		footer.setLayout( new FormLayout() );
		footer.setBackground( Colors.BLACK );
		FormData footerFD = FormDataMaker.makeFormData( (Integer)null, 100, 0, 100);
		footerFD.height = 80;
		footer.setLayoutData( footerFD );
		
		myTableComposite = new Composite( myMainComp, SWT.NONE );
		myTableComposite.setLayout( new FormLayout() );
		myTableComposite.setLayoutData( FormDataMaker.makeFormData( titleComp, footer, 0, 100));
		
	    myTableViewer = new TableViewer( myTableComposite, SWT.FULL_SELECTION  | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL );	
	    myTableViewer.getTable().setLayoutData( FormDataMaker.makeFormData( 0, 100, 0, 100 ) );
	    myTableViewer.getTable().setLinesVisible( true );
	    myTableViewer.getTable().setHeaderVisible( false );
	    myTableViewer.setUseHashlookup( true );
		if ( !isCellDown )
		{
			// set the itemCount for the tree so vertical scrolls would work properly
			myTableViewer.getTable().setItemCount( analyses.size() );
		}

		TableViewerColumn col = new TableViewerColumn( myTableViewer, SWT.None );
		col.setLabelProvider( new ResultTypeLabelProvider( myTableViewer ) );
		col.getColumn().setWidth( 300 );

		myTableViewer.setContentProvider( new ResultTypeContentProvider() );
		myTableViewer.setInput( analyses );
		initializeTableMouseListener();

		myCRCDownComposite = new Composite(  myMainComp, SWT.NONE  );
		myCRCDownComposite.setLayoutData( FormDataMaker.makeFormData( titleComp, footer, 0, 100));
		myCRCDownComposite.setBackground( Colors.DARK_RED );

		myOKButton 				= new Button( footer, SWT.PUSH );
		myOKButton.setText( SUBMIT_QUERY );
		myOKButton.setLayoutData( FormDataMaker.makeFormData( (Integer)null, 0, 50, -2, 0, 20, (Integer)null, 0) );
		myOKButton.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_TOOL_FORWARD ) );

		myUsePreferenceButton 	= new Button( footer, SWT.CHECK );		
		myUsePreferenceButton.setLayoutData( FormDataMaker.makeFormData( (Integer)null, 0, 50, -2, myOKButton, 4, (Integer)null, 0) );
		myUsePreferenceButton.setBackground( myUsePreferenceButton.getParent().getBackground() );
		myUsePreferenceButton.setText( "" );

		myUserPreferenceLabel	= new Label( footer, SWT.None );
		myUserPreferenceLabel.setText( REMEMBER_MY_RESULT_TYPES );
		myUserPreferenceLabel.setForeground( Colors.GRAY );
		myUserPreferenceLabel.setBackground( myUserPreferenceLabel.getParent().getBackground() );
		myUserPreferenceLabel.setLayoutData( FormDataMaker.makeFormData( (Integer)null, 0, 50, -2, myUsePreferenceButton, 4, (Integer)null, 0) );
		
		myCancelButton 			= new Button( footer, SWT.PUSH );
		myCancelButton.setText( DO_NOT_SUBMIT_QUERY );
		myCancelButton.setLayoutData( FormDataMaker.makeFormData( 50, 6, (Integer)null, 0, 0, 20, (Integer)null, 0));		
		myCancelButton.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_ETOOL_DELETE ) );

		autoSetUI();
	}

	private void attachListeners() 
	{
		// TITLE for moving the dialog
		DialogMoveMouseListener moveListener = new DialogMoveMouseListener( myShell, titleComp );
		titleComp.addMouseListener( moveListener );
		titleComp.addMouseMoveListener( moveListener );
		titleLabel.addMouseListener( moveListener );
		titleLabel.addMouseMoveListener( moveListener );

		myCancelButton.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  { 
				  myShell.setVisible( false );
				  isCanceled = true;
			  }
		});

		myOKButton.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  {  
				  myShell.setVisible( false );
				  isCanceled = false;
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
		
		myTableMouseListener  = new QueryResultTypeMouseListener( myTableViewer );
		myTableViewer.getTable().addListener(SWT.MouseDown,	myTableMouseListener);	// to handle col selection
		myTableViewer.getTable().addListener(SWT.MouseUp,	myTableMouseListener);	// to handle col selection
	}
	
	private void autoSetUI()
	{
		if ( !this.isCellDown )
		{
			myCRCDownComposite.setVisible( false );
			myTableComposite.setVisible( true );
		}
		else
		{
			myCRCDownComposite.setVisible( true );
			myTableComposite.setVisible( false );
		}
	}
	
	public boolean isCRCDown()
	{ return this.isCellDown; }
	
	
	public ArrayList<SelectableQueryResultTypeItem> getSelectableQueryResultTypes()
	{ return this.analyses; }
	
	// open the dialog with a specific location
	public void open( Point initLocation )
	{
		// reset data related to each query submission
		isCanceled 				= false;
		isUsingGraphicAnalysis 	= false;
		isUsingTimeline			= false;
		
		myShell.setSize( myShell.computeSize( SWT.DEFAULT, SWT.DEFAULT ));
		if ( initLocation == null )
		{
			initLocation = new Point(Display.getCurrent().getBounds().width/2 - myShell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x/2,
									 Display.getCurrent().getBounds().height/2 - myShell.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2 );
		}

		myShell.setLocation( initLocation );
		myShell.open();
		while (myShell.isVisible()) 
		{
			if (!Display.getCurrent().readAndDispatch())
				Display.getCurrent().sleep();
		}
	}

	
	public boolean isCanceled()
	{ return isCanceled; }
	
	public boolean isToSkipResultTypeDialog()
	{ return !this.myUsePreferenceButton.getSelection(); }
	public void setIsToSkipResultTypeDialog( boolean flag )
	{  this.myUsePreferenceButton.setSelection( flag ); }

	
	/* Adapted from edu.harvard.i2b2.query.ui.AnalysisPanel */
	public ArrayList<String> getSelectedQueryResultTypeNames()
	{
		missingTypes 			= new ArrayList<QueryResultTypeType>();
		isUsingGraphicAnalysis 	= false;
		isUsingTimeline			= false;
		
		ArrayList<String> desiredResultTypeNames = new ArrayList<String>();
		for ( SelectableQueryResultTypeItem analysisType : analyses )
		{
			String resultTypeName = analysisType.getQueryType().getName();

			if (analysisType.isSelected() && !desiredResultTypeNames.contains( resultTypeName ) )
			{
				if (analysisType.getQueryType().getDisplayType() == null )
				{
					missingTypes.add( analysisType.getQueryType() );
					continue;
				}
				if ( analysisType.getQueryType().getDisplayType().equalsIgnoreCase( GRAPHICAL_ANALYSIS_SIGN )) 
					this.isUsingGraphicAnalysis = true;
				if ( analysisType.getQueryType().getDescription().equalsIgnoreCase( TIMELINE_DESCRIPTION ))
					this.isUsingTimeline = true; 
				desiredResultTypeNames.add( resultTypeName );
			}
		}
		return desiredResultTypeNames;
	}

	/* Adapted from edu.harvard.i2b2.query.ui.AnalysisPanel */
	public ArrayList<QueryResultTypeType> getSelectedQueryResultTypes()
	{
		missingTypes 			= new ArrayList<QueryResultTypeType>();
		isUsingGraphicAnalysis 	= false;
		isUsingTimeline			= false;
		
		ArrayList<QueryResultTypeType> desiredResultTypes = new ArrayList<QueryResultTypeType>();
		for ( SelectableQueryResultTypeItem analysisType : analyses )
		{
			if (analysisType.isSelected )
			{
				if (analysisType.getQueryType().getDisplayType() == null )
				{
					missingTypes.add( analysisType.getQueryType() );
					continue;
				}
				if ( analysisType.getQueryType().getDisplayType().equalsIgnoreCase( GRAPHICAL_ANALYSIS_SIGN )) 
					isUsingGraphicAnalysis = true;
				if ( analysisType.getQueryType().getDescription().equalsIgnoreCase( TIMELINE_DESCRIPTION ))
					this.isUsingTimeline = true; 
				desiredResultTypes.add( analysisType.getQueryType() );
			}
		}
		return desiredResultTypes;
	}

	
	
	public ArrayList<QueryResultTypeType> getUnhandledQueryResultType()
	{ return this.missingTypes; }
	public boolean hasUnhandledQueryResultType()
	{ return this.missingTypes.size() > 0; }
	
	public boolean isUsingAnalysisTypesPreference()
	{ return this.myUsePreferenceButton.getSelection(); }
	
	public boolean isUsingGraphicalAnalysis()
	{ return this.isUsingGraphicAnalysis; }
	
	public boolean isUsingTimeline() 
	{ return this.isUsingTimeline; }

	private String makeResultTypeRequestXML() 
	{
		// create header
		PsmQryHeaderType headerType = new PsmQryHeaderType();
		UserType userType = new UserType();
		userType.setLogin(UserInfoBean.getInstance().getUserName());
		userType.setValue(UserInfoBean.getInstance().getUserName());
		headerType.setUser(userType);
		headerType.setRequestType(PsmRequestTypeType.CRC_QRY_GET_RESULT_TYPE);

		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setResultWaittimeMs(180000);

		BodyType bodyType = new BodyType();
		edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory psmOf = new edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.ObjectFactory();
		bodyType.getAny().add(psmOf.createPsmheader(headerType));

		MessageHeaderType messageHeader = getMessageHeader();
		RequestMessageType requestMessageType = new RequestMessageType();
		requestMessageType.setMessageBody(bodyType);
		requestMessageType.setMessageHeader(messageHeader);
		requestMessageType.setRequestHeader(requestHeader);

		JAXBUtil jaxbUtil = QueryJAXBUtil.getJAXBUtil();
		StringWriter strWriter = new StringWriter();
		try 
		{
			edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crcxmljaxb.datavo.i2b2message.ObjectFactory();
			jaxbUtil.marshaller(of.createRequest(requestMessageType), strWriter);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return strWriter.toString();
	}

	protected MessageHeaderType getMessageHeader() 
	{
		MessageHeaderType messageHeader = new MessageHeaderType();
		messageHeader.setI2B2VersionCompatible(new BigDecimal(Messages.getString("QueryData.i2b2VersionCompatible"))); //$NON-NLS-1$
		
		ApplicationType appType = new ApplicationType();
		appType.setApplicationName(Messages.getString("QueryData.SendingApplicationName")); //$NON-NLS-1$
		appType.setApplicationVersion(Messages.getString("QueryData.SendingApplicationVersion")); //$NON-NLS-1$
		messageHeader.setSendingApplication(appType);

		messageHeader.setAcceptAcknowledgementType(new String("messageId"));

		MessageTypeType messageTypeType = new MessageTypeType();
		messageTypeType.setEventType(Messages.getString("QueryData.EventType"));
		messageTypeType.setMessageCode(Messages.getString("QueryData.MessageCode"));
		messageHeader.setMessageType(messageTypeType);

		FacilityType facility = new FacilityType();
		facility.setFacilityName(Messages.getString("QueryData.SendingFacilityName")); //$NON-NLS-1$
		messageHeader.setSendingFacility(facility);

		ApplicationType appType2 = new ApplicationType();
		appType2.setApplicationVersion(Messages.getString("QueryData.ReceivingApplicationVersion")); //$NON-NLS-1$
		appType2.setApplicationName(Messages.getString("QueryData.ReceivingApplicationName")); //$NON-NLS-1$
		messageHeader.setReceivingApplication(appType2);

		FacilityType facility2 = new FacilityType();
		facility2.setFacilityName(Messages.getString("QueryData.ReceivingFacilityName")); //$NON-NLS-1$
		messageHeader.setReceivingFacility(facility2);

		Date currentDate = new Date();
		DTOFactory factory = new DTOFactory();
		messageHeader.setDatetimeOfMessage(factory.getXMLGregorianCalendar(currentDate.getTime()));

		SecurityType secType = new SecurityType();
		secType.setDomain(UserInfoBean.getInstance().getUserDomain());
		secType.setUsername(UserInfoBean.getInstance().getUserName());
		PasswordType ptype = new PasswordType();
		ptype.setIsToken(UserInfoBean.getInstance().getUserPasswordIsToken());
		ptype.setTokenMsTimeout(UserInfoBean.getInstance()
				.getUserPasswordTimeout());
		ptype.setValue(UserInfoBean.getInstance().getUserPassword());

		secType.setPassword(ptype);
		messageHeader.setSecurity(secType);

		MessageControlIdType mcIdType = new MessageControlIdType();
		mcIdType.setInstanceNum(0);
		mcIdType.setMessageNum( DataUtils.generateMessageId() );
		messageHeader.setMessageControlId(mcIdType);

		ProcessingIdType proc = new ProcessingIdType();
		proc.setProcessingId(Messages.getString("QueryData.ProcessingId")); //$NON-NLS-1$
		proc.setProcessingMode(Messages.getString("QueryData.ProcessingMode")); //$NON-NLS-1$
		messageHeader.setProcessingId(proc);

		messageHeader.setAcceptAcknowledgementType(Messages.getString("QueryData.AcceptAcknowledgementType")); //$NON-NLS-1$
		messageHeader.setApplicationAcknowledgementType(Messages.getString("QueryData.ApplicationAcknowledgementType")); //$NON-NLS-1$
		messageHeader.setCountryCode(Messages.getString("QueryData.CountryCode")); //$NON-NLS-1$
		messageHeader.setProjectId(UserInfoBean.getInstance().getProjectId());
		return messageHeader;
	}
	
	// mapping the display string to the request option string
	/*
	private void populateStringMap() 
	{
		requestMap.put("Timeline", "PATIENTSET");
		requestMap.put("Patient count", "PATIENT_COUNT_XML");
		requestMap.put("Gender", "PATIENT_GENDER_COUNT_XML");
		requestMap.put("Age", "PATIENT_AGE_COUNT_XML");
		requestMap.put("Vital", "PATIENT_VITALSTATUS_COUNT_XML");
		requestMap.put("Race", "PATIENT_RACE_COUNT_XML");
	}
	*/
	
	/*
	 * classes that suppor the table
	 */
	class ResultTypeContentProvider implements IStructuredContentProvider 
	{
		public Object[] getElements(Object inputElement) 
		{
			return analyses.toArray();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		
		public void dispose() {}

	}
	
	class ResultTypeLabelProvider extends EmulatedNativeCheckboxLabelProvider 
	{

		public ResultTypeLabelProvider(ColumnViewer viewer) 
		{ super(viewer); }

		public String getText( Object element )
		{  return ((SelectableQueryResultTypeItem)element).getQueryType().getDescription(); }
		
		protected boolean isEnabled(Object element) 
		{ return true; }

		protected boolean isChecked(Object element) 
		{ return ((SelectableQueryResultTypeItem)element).isSelected(); }
	}


	class SelectableQueryResultTypeItem implements Selectable
	{
		private boolean				isSelected;
		private QueryResultTypeType myQueryResultType;
		
		public SelectableQueryResultTypeItem( QueryResultTypeType type, boolean flag  )
		{
			isSelected = flag;
			myQueryResultType = type;
		}
		
		public boolean isSelected()
		{ return isSelected; }
		
		public QueryResultTypeType getQueryType()
		{ return myQueryResultType;  }

		@Override
		public void setSelection(boolean flag) 
		{ isSelected = flag; }
		
	}
 
	class QueryResultTypeMouseListener extends TableViewerMouseAdaptor
	{
		private	TableViewer					myTV;
		
		public QueryResultTypeMouseListener( TableViewer tv ) 
		{ 
			super( tv, null );
			myTV = tv;
		}

		@SuppressWarnings("unchecked")
		public void mouseUpped(Event event) 
		{
	        Point point = new Point(event.x, event.y);        
	        TableItem item = myTV.getTable().getItem(point);        
		  	int colIndex = mapPointToColumnIndex( event.x, event.y );
			if (colIndex == 0) 
			{
				// use the current input of the TableViewer
				ArrayList<SelectableQueryResultTypeItem> model 		= (ArrayList<SelectableQueryResultTypeItem>)myTV.getInput();
				SelectableQueryResultTypeItem rtim 	= model.get( item.getParent().indexOf( item ) );
				rtim.setSelection( !rtim.isSelected() );
				myTV.update( rtim, null );
			}
		}
	}


}