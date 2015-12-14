/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     
 */
/**

 */
package edu.harvard.i2b2.navigator;

/**
 * This is the control that is used in the visual interface to perform some
 * housekeeping functions and general selections, specifically: 1) presents the
 * TITLE and if one is in a know development, test, or production environment by
 * painting the background dark gray, gray, or white. 2) Manage the logon
 * process, for Linux this means presenting the logon dialog and retrieving the
 * correct configuration file. 3) present the options from the configuation file
 * to the user and allow them to pick a general application area. The default
 * area comes up automatically. 4) The proper connections for that application
 * area are tested to see if they are all "up", and a canvas light is shown to
 * indicate the results. 5) There is a number of buttons (up to three) that come
 * up for each application area, generally pointing to a URL. 6) The persons
 * name is retrieved from the user database and presented in the control.
 * 
 * properties file crcnavigator.properties in root directory contains webServiceName
 * for web service host called by CRCNavigator on initial startup and LoginDHelper on login 
 */

/**
 *		   Main Application class entry point for i2b2 application start process-
 *         allows dialog to appear with main window in background 1 create main
 *         window APP- bannerC and tabfolder 2 do not block on true allows main
 *         window to open 3 APP.open() opens main window 4
 *         APP.loginAction(true)- opens LoginDNew dialog 5 LoginDNew ok button
 *         selection listener 6 creates new loginDThread with LoginDHelper in
 *         run(), 7 LoginDNew Busyindicator.showWhile(), 8 -spawns thread which
 *         calls loginDhelper, -displays caret, -blocks until thread returns 9
 *         loginDNew open() returns and populates bannerC,menu, and creates
 *         tabFolder pages from xml configuration file 10 APP.loginAction()
 *         returns 11 enter event loop. All web service work is done in
 *         LoginDHelper which can be run locally as a java app for testing User
 *         initialization info is stored in UserInfoBean instance, xml query
 *         information returned as strings and set into system properties
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

//import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.window.*;

//import edu.harvard.i2b2.select.soap.Select;
//import edu.harvard.i2b2.select.soap.SelectService;
//mport edu.harvard.i2b2.select.soap.SelectServiceLocator;

import edu.harvard.i2b2.explorer.ui.MainComposite;

public class CRCNavigator extends ApplicationWindow {

    // static instance to the running application
    public static CRCNavigator APP;

    // application environment constants eg Development, Production or Test
    // private String APP_DEV = "Development";

    private String APP_PROD = "Production";

    private String APP_TEST = "Test";

    // set current application environment
    // default to development prior to login
    // resets after login from user config file
    private String APP_CURRENT = APP_PROD;

    // application constants here
    public String msTitle = "";

    public String msUsername = "";

    public String msPassword = "";

    // button text on bannerC menu
    public static String BUTTON_TEXT_LOGIN = "  Log in  ";

    public static String BUTTON_TEXT_LOGOUT = " Log out ";

    public Button loginButton;

    public String helpURL = "";

    // filename for logfile to show to users
    public String logFileName = "i2b2log.html";

    public String userLoginMode = "Login Mode";

    // host webservice for application stored in crcnavigator.properties file
    public String webServiceName;

    // color and gui
    Color badColor;

    Color warningColor;

    Color goColor;

    Color grayColor;

    Color devColor;

    Color prodColor;

    Color testColor;

    Color backColor;

    // how much to offset the folder so tab text does not show
    // to do compute fontsize or use another way
    int tabFolderOffset = -20;

    // external classes
    // public BannerC bannerC = null;

    // public ExplorerC explorerC = null;

    public LoginContent loginContentFake = null;

    // public UserInfoBean userInfoBean = null;

    // popup menu for toolbar
    public Menu menu;

    // tabfolder
    public TabFolder tabFolder;

    public TabItem tabLogin;

    public TabItem tabExp;

    public TabItem tabQuery;

    public TabItem tabOntology;

    public int tabFolderIndex;

    // from BannerC control

    private Composite banner;

    private Label titleLabel;

    private ToolBar titleToolBar;

    public ToolItem titleToolItem;

    public Label authorizationLabel;

    public Label statusOvalLabel;

    public StatusLabelPaintListener statusLabelPaintListener;

    public Label statusLabel;

    public MainComposite explorer;

    // public ArrayList<QueryMasterData> previousQueries = null;
    // public QueryPreviousRunsPanel runTreePanel = null;

    // add logging
    private static final Log log = LogFactory.getLog(CRCNavigator.class);

    // testing xml strings to set System Property ExplorerCongigurationXML web
    // service calls
    // System.setProperty("ExplorerConfigurationXML",
    // ssFakeApplicationConfigurationXML);
    public String ssFakeApplicationConfigurationXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
	    + "<contents>\r\n"
	    + "    <table>\r\n"
	    + "        <name>Demographics</name>\r\n"
	    + "        <tableName>Demographics</tableName>\r\n"
	    + "        <status/>\r\n"
	    + "        <description/>\r\n"
	    + "        <lookupDB>metadata</lookupDB>\r\n"
	    + "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
	    + "    </table>\r\n"
	    + "    <table>\r\n"
	    + "        <name>Diagnoses</name>\r\n"
	    + "        <tableName>Diagnoses</tableName>\r\n"
	    + "        <status/>\r\n"
	    + "        <description/>\r\n"
	    + "        <lookupDB>metadata</lookupDB>\r\n"
	    + "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
	    + "    </table>\r\n"
	    + "    <table>\r\n"
	    + "        <name>Medications</name>\r\n"
	    + "        <tableName>Medications</tableName>\r\n"
	    + "        <status/>\r\n"
	    + "        <description/>\r\n"
	    + "        <lookupDB>metadata</lookupDB>\r\n"
	    + "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
	    + "    </table>\r\n"
	    + "    <table>\r\n"
	    + "        <name>I2B2</name>\r\n"
	    + "        <tableName>i2b2</tableName>\r\n"
	    + "        <status/>\r\n"
	    + "        <description/>\r\n"
	    + "        <lookupDB>metadata</lookupDB>\r\n"
	    + "        <webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName>\r\n"
	    + "    </table>\r\n" + "</contents>";

    public String defaultExplorerXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<contents>"
	    + "<table><name>Demographics</name><tableName>Demographics</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>+"
	    + "<table><name>Diagnoses</name><tableName>Diagnoses</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Encounters</name><tableName>Encounters</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Laboratory Tests</name><tableName>LabTests</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Medications</name><tableName>Medications</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Microbiology</name><tableName>Microbiology</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Procedures</name><tableName>Procedures</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Providers</name><tableName>Providers</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Transfusion Services</name><tableName>Transfusions</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>I2B2</name><tableName>i2b2</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "</contents>";

    public String defaultExplorerProdXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<contents>"
	    + "<table><name>Demographics</name><tableName>Demographics</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>+"
	    + "<table><name>Diagnoses</name><tableName>Diagnoses</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Encounters</name><tableName>Encounters</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Laboratory Tests</name><tableName>LabTests</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Medications</name><tableName>Medications</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Microbiology</name><tableName>Microbiology</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Procedures</name><tableName>Procedures</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Providers</name><tableName>Providers</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>Transfusion Services</name><tableName>Transfusions</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "<table><name>I2B2</name><tableName>i2b2</tableName><status /><description /><lookupDB>metadata_dev</lookupDB><webserviceName>http://phsi2b2appprod1.mgh.harvard.edu:8080/i2b2/services/Select</webserviceName></table>"
	    + "</contents>";

    /**
     * gets the running application
     * 
     * @return
     */
    public static final CRCNavigator getApp() {
	return APP;
    }

    public String getWebServiceName() {
	return webServiceName;
    }

    public void setWebServiceName(String webServiceName) {
	this.webServiceName = webServiceName;
    }

    public int getTabFolderIndex() {
	return tabFolder.getSelectionIndex();
    }

    public void setTabFolderIndex(int tabFolderIndex) {
	this.tabFolder.setSelection(tabFolderIndex);
    }

    /**
     * Constructor
     */
    public CRCNavigator() {
	super(null);

	setCRCNavigatorNameProperties();

	APP = this;

    }

    /**
     * Runs the application
     */
    public void run() {

    }

    protected String generateMessageId() {
	StringWriter strWriter = new StringWriter();
	for (int i = 0; i < 20; i++) {
	    int num = getValidAcsiiValue();
	    // System.out.println("Generated number: " + num +
	    // " char: "+(char)num);
	    strWriter.append((char) num);
	}
	return strWriter.toString();
    }

    private int getValidAcsiiValue() {
	int number = 48;
	while (true) {
	    number = 48 + (int) Math.round(Math.random() * 74);
	    if ((number > 47 && number < 58) || (number > 64 && number < 91)
		    || (number > 96 && number < 123)) {
		break;
	    }
	}
	return number;

    }

    private String writeContentQueryXML() {
	/*
	 * DTOFactory dtoFactory = new DTOFactory();
	 * 
	 * // create header HeaderType headerType = new HeaderType();
	 * 
	 * UserType userType = new UserType(); String userId =
	 * System.getProperty("user"); userType.setLogin(userId);
	 * userType.setValue(userId);
	 * 
	 * headerType.setUser(userType);
	 * headerType.setRequestType(RequestTypeType
	 * .CRC_QRY_GET_QUERY_MASTER_LIST_FROM_USER_ID);
	 * 
	 * UserRequestType userRequestType = new UserRequestType();
	 * userRequestType.setUserId(userId);
	 * 
	 * RequestHeader requestHeader = new RequestHeader();
	 * requestHeader.setResultWaittimeMs(180000); BodyType bodyType = new
	 * QueryToolDTOFactory().buildBodyType(headerType, userRequestType,
	 * null); MessageHeader messageHeader = getMessageHeader();
	 * RequestMessageType requestMessageType =
	 * dtoFactory.getRequestMessageType(messageHeader, requestHeader,
	 * bodyType);
	 * 
	 * JAXBUtil jaxbUtil = new JAXBUtil(); StringWriter strWriter = new
	 * StringWriter(); try { jaxbUtil.requestMarshaller(requestMessageType,
	 * strWriter); } catch(Exception e) { e.printStackTrace(); }
	 * 
	 * System.out.println("Generated content XML request: " +
	 * strWriter.toString()); return strWriter.toString();
	 */
	return "";
    }

    private void loadPreviousQueries() {
	System.out.println(" Logged in, loading previous queries for: "
		+ System.getProperty("user"));
	String xmlStr = writeContentQueryXML();
	// System.out.println(xmlStr);

	/*
	 * String responseStr = QueryListNamesClient.sendQueryRequest(xmlStr);
	 * try { JAXBUtil jaxbUtil = new JAXBUtil(); ResponseMessageType
	 * messageType =
	 * jaxbUtil.unMashallResponseMessageTypeFromString(responseStr);
	 * 
	 * BodyType bt = messageType.getMessageBody(); MasterResponseType
	 * masterResponseType = (MasterResponseType) new
	 * QueryProcessorJAXBUnWrapHelper().getResponseType(bt); previousQueries
	 * = new ArrayList<QueryMasterData>(); for(QueryMasterType
	 * queryMasterType:masterResponseType.getQueryMaster()) {
	 * QueryMasterData tmpData; tmpData = new QueryMasterData();
	 * tmpData.name(queryMasterType.getName());
	 * tmpData.tooltip("A query run by "+System.getProperty("user"));
	 * tmpData.visualAttribute("CA"); tmpData.xmlContent(null);
	 * tmpData.id(new
	 * Integer(queryMasterType.getQueryMasterId()).toString());
	 * tmpData.userId(System.getProperty("user"));
	 * previousQueries.add(tmpData); } } catch(Exception e) {
	 * e.printStackTrace(); }
	 */
    }

    protected void configureShell(Shell shell) {

	super.configureShell(shell);
	msTitle = System.getProperty("applicationName") + " CRC Navigator in";

	// set title bar and size
	shell.setText(System.getProperty("applicationName")
		+ " CRC Navigator Version 2.0");
	shell.setSize(1024, 728);
    }

    /*
     * creates main window contents
     * 
     * @see
     * org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets
     * .Composite)
     */
    protected Control createContents(Composite parent) {

	// local variable to get system fonts and colors
	Display display = parent.getDisplay();
	// todo dispose of fonts when page is closed?
	final Font headerFont = new Font(display, "Tahoma", 12, SWT.BOLD);
	final Font normalFont = new Font(display, "Tahoma", 12, SWT.NORMAL);
	final Font buttonFont = new Font(display, "Tahoma", 9, SWT.NORMAL);

	// set background color
	if (APP_CURRENT.equals(APP_PROD)) {
	    backColor = display.getSystemColor(SWT.COLOR_WHITE);
	} else if (APP_CURRENT.equals(APP_TEST)) {
	    backColor = display.getSystemColor(SWT.COLOR_GRAY);
	    // default to dev
	} else {
	    backColor = display.getSystemColor(SWT.COLOR_DARK_GRAY);

	}

	final Color foreColor = display.getSystemColor(SWT.COLOR_BLACK);
	warningColor = display.getSystemColor(SWT.COLOR_YELLOW);
	// final Color textColor = display.getSystemColor(SWT.COLOR_BLACK);
	goColor = display.getSystemColor(SWT.COLOR_GREEN);
	badColor = display.getSystemColor(SWT.COLOR_RED);

	// create top composite
	Composite top = new Composite(parent, SWT.NONE);
	FormLayout topCompositeLayout = new FormLayout();
	// FormData topData=new FormData();
	top.setLayout(topCompositeLayout);

	// GridLayout topGridLayout = new GridLayout(1, false);
	// topGridLayout.numColumns = 1;
	// topGridLayout.marginWidth = 2;
	// topGridLayout.marginHeight = 2;
	// top.setLayout(topGridLayout);

	// BannerC composite
	banner = new Composite(top, SWT.NONE);

	FormData bannerData = new FormData();
	bannerData.left = new FormAttachment(0);
	bannerData.right = new FormAttachment(100);
	banner.setLayoutData(bannerData);

	// The Banner itself is configured and layout is set
	FormLayout bannerLayout = new FormLayout();
	bannerLayout.marginWidth = 2;
	bannerLayout.marginHeight = 2;
	bannerLayout.spacing = 5;
	banner.setLayout(bannerLayout);

	// banner.setBackground(grayColor);
	banner.setBackground(backColor);
	banner.setForeground(foreColor);

	// add banner components and then configure layout

	// the label on the left is added
	titleLabel = new Label(banner, SWT.NO_FOCUS);
	titleLabel.setBackground(backColor);
	titleLabel.setText(msTitle);
	titleLabel.setFont(headerFont);
	titleLabel.setForeground(foreColor);

	// the general application area toolbar is added
	titleToolBar = new ToolBar(banner, SWT.FLAT);
	titleToolBar.setBackground(backColor);
	titleToolBar.setFont(headerFont);

	// add query mode dropdown tool item
	// set initial text to userLoginMode variable
	titleToolItem = new ToolItem(titleToolBar, SWT.DROP_DOWN);
	titleToolItem.setText(userLoginMode);

	// create menu for dropdown, create menu items, and add listeners for
	// dropdown tool item
	// hard code replace with user detail bean from webservice login values
	// Changed to member variable userModes [] from bean
	// String [] modes={"Exploration Mode","Query Mode", "Ontology Mode"};
	menu = new Menu(banner.getShell(), SWT.POP_UP);

	// wait until after login to create menu items
	// addMenuItems(menu, userModes);

	/*
	 * for (int i=0;i<userModes.length;i++){ MenuItem menuItem=new
	 * MenuItem(menu,SWT.PUSH); menuItem.setText(userModes[i]);
	 * menuItem.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override public void widgetSelected(SelectionEvent event) {
	 * Auto-generated method stub //note tabFolderIndex [0] is always on
	 * login class MenuItem selected=(MenuItem)event.widget;
	 * //System.out.println("titleToolItem="+selected.getText());
	 * titleToolItem.setText(selected.getText());
	 * //setTabFolderIndex(menu.indexOf(selected));
	 * System.out.println("selected.getText="+selected.getText());
	 * System.out.println("menu.indexOf(selected)="+menu.indexOf(selected));
	 * setTabFolderIndex(menu.indexOf(selected));
	 * 
	 * //if (userModes[1].equals(selected.getText())){ //
	 * setTabFolderIndex(1); //} //if
	 * (userModes[2].equals(selected.getText())){ // setTabFolderIndex(2);
	 * //} //if (userModes[0].equals(selected.getText())){ //
	 * setTabFolderIndex(0); //} }
	 * 
	 * }); }
	 */

	// add listener for toolbaritem
	titleToolItem.addListener(SWT.Selection, new DropDownListener(
		titleToolBar, menu));
	titleToolItem.setEnabled(false);

	// Authorization label is made
	authorizationLabel = new Label(banner, SWT.NO_FOCUS);
	authorizationLabel.setBackground(backColor);
	authorizationLabel.setText("Awaiting Authorization...");
	authorizationLabel.setAlignment(SWT.RIGHT);
	authorizationLabel.setFont(normalFont);
	authorizationLabel.setForeground(foreColor);

	// the staus indicator is shown
	statusLabel = new Label(banner, SWT.NO_FOCUS);
	statusLabel.setBackground(backColor);
	statusLabel.setText("Status:");
	statusLabel.setAlignment(SWT.RIGHT);
	statusLabel.setFont(normalFont);
	statusLabel.setForeground(foreColor);

	statusOvalLabel = new Label(banner, SWT.NO_FOCUS);
	statusOvalLabel.setBackground(backColor);
	statusOvalLabel.setToolTipText("Click to show error log");
	// statusOvalLabel.setAlignment(SWT.LEFT);
	// statusOvalLabel.setSize(16,16);
	// statusOvalLabel.setFont(normalFont);
	statusOvalLabel.setSize(20, 20);
	statusOvalLabel.setForeground(foreColor);
	statusOvalLabel.redraw();

	statusOvalLabel.addListener(SWT.Resize, new Listener() {

	    public void handleEvent(Event arg0) {
		statusOvalLabel.setSize(20, 20);
		statusOvalLabel.redraw();
	    }
	});

	// add selection listener so that clicking on status oval label shows
	// error log
	// dialog
	statusOvalLabel.addListener(SWT.MouseDown, new Listener() {

	    public void handleEvent(Event arg0) {
		// log.info(getNow() + "Status Listener Clicked");
		Display display = statusOvalLabel.getDisplay();
		final Shell shell = statusOvalLabel.getShell();
		// run asyncExec so that other pending ui events finished first
		display.asyncExec(new Runnable() {

		    public void run() {
			// LoggerD loggerD = new LoggerD(shell);
			// loggerD.open();
			// final Shell myShell=shell;
			File file = new File(logFileName);
			URL url = null;
			// Convert the file object to a URL with an absolute
			// path
			try {
			    url = file.toURL();
			} catch (MalformedURLException e) {
			    log.info(e.getMessage());
			}
			final URL myurl = url;
			new HelpBrowser().run(myurl.toString(), shell);
		    }
		});
		// shows browser with logger in separate
		// showLoggerBrowser(shell).start();
	    }
	});

	// add status label paint listener so that it changes color
	statusLabelPaintListener = new StatusLabelPaintListener();
	// statusLabelPaintListener.setOvalColor(warningColor);
	statusOvalLabel.addPaintListener(statusLabelPaintListener);

	statusLabelPaintListener.setOvalColor(display
		.getSystemColor(SWT.COLOR_YELLOW));
	statusOvalLabel.setSize(20, 20);
	statusOvalLabel.redraw();

	// Login button is made
	loginButton = new Button(banner, SWT.PUSH | SWT.LEFT);
	loginButton.setFont(buttonFont);
	loginButton.setText(BUTTON_TEXT_LOGIN);

	// add selection listener for login Button for login/logout from banner
	loginButton.addSelectionListener(new SelectionAdapter() {

	    @Override
	    // loginAction(true) logs in, loginAction(false) logs out
	    public void widgetSelected(SelectionEvent event) {
		if (loginButton.getText().equals(BUTTON_TEXT_LOGIN)) {
		    loginAction(true);
		} else {
		    loginAction(false);
		}
	    }
	});

	// right button is made
	final Button rightButton = new Button(banner, SWT.PUSH | SWT.LEFT);
	rightButton.setFont(buttonFont);
	rightButton.setText(" Help ");
	// These don't work on Windows
	// rightButton.setBackground(backColor);
	// rightButton.setForeground(foreColor);
	// add selection listener to show help browser in new window- separate
	// thread
	rightButton.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent event) {
		// super.widgetSelected(arg0);
		final Button myButton = (Button) event.widget;
		// showHelpBrowser(myButton).start();
		Display display = myButton.getDisplay();
		final Shell myShell = myButton.getShell();
		display.asyncExec(new Runnable() {

		    public void run() {
			// LoggerD loggerD = new LoggerD(shell);
			// loggerD.open();
			// final Shell myShell=shell;
			new HelpBrowser().run(helpURL, myShell);
		    }
		});
	    }
	});

	// top.pack();
	// int titleLabelHeight = titleLabel.getBounds().height;
	// System.out.println(titleLabelHeight);
	// layout and configure banner components

	// attach titlelabel to left and align vertically with tool bar
	FormData titleLabelFormData = new FormData();
	// titleLabelFormData.top = new FormAttachment(50, -(titleLabelHeight /
	// 2));
	// titleLabelFormData.bottom = new FormAttachment(100);
	titleLabelFormData.top = new FormAttachment(titleToolBar, 0, SWT.CENTER);
	titleLabelFormData.left = new FormAttachment(0, 10);
	titleLabel.setLayoutData(titleLabelFormData);

	// attach left of tool bar to title label, attach top to banner
	// attach right to authorization label so that it will resize and remain
	// visible when tool bar text changes
	FormData titleToolBarFormData = new FormData();
	titleToolBarFormData.left = new FormAttachment(titleLabel);
	titleToolBarFormData.top = new FormAttachment(0);
	titleToolBarFormData.right = new FormAttachment(authorizationLabel, 0,
		0);

	// titleToolBarFormData.top = new FormAttachment(titleLabel,
	// -titleLabelHeight - 10);
	titleToolBar.setLayoutData(titleToolBarFormData);

	// attach authorization label on right to status label and center
	// vertically

	FormData authorizationLabelFormData = new FormData();
	authorizationLabelFormData.right = new FormAttachment(statusLabel, -10);
	// authorizationLabelFormData.top = new
	// FormAttachment(topCanvas,-titleLabelHeight-10);
	authorizationLabelFormData.top = new FormAttachment(statusLabel, 0,
		SWT.CENTER);
	authorizationLabel.setLayoutData(authorizationLabelFormData);

	FormData statusLabelFormData = new FormData();
	// statusLabelFormData.right = new FormAttachment(rightButton,0);
	statusLabelFormData.right = new FormAttachment(statusOvalLabel, 0);
	statusLabelFormData.top = new FormAttachment(statusOvalLabel, 0,
		SWT.CENTER);
	statusLabel.setLayoutData(statusLabelFormData);

	// attach status label on right to loginbutton and center vertically

	FormData statusOvalLabelFormData = new FormData();
	// statusLabelFormData.right = new FormAttachment(rightButton,0);
	// add offset
	statusOvalLabelFormData.right = new FormAttachment(loginButton, -25);
	statusOvalLabelFormData.top = new FormAttachment(loginButton, 0,
		SWT.CENTER);
	statusOvalLabel.setLayoutData(statusOvalLabelFormData);

	// attach login button on right to right button and center vertically
	FormData loginButtonFormData = new FormData();
	// loginButtonFormData.right = new FormAttachment(100,-10);
	loginButtonFormData.right = new FormAttachment(rightButton);
	loginButtonFormData.top = new FormAttachment(rightButton, 0, SWT.CENTER);
	// loginButtonFormData.top = new FormAttachment(50,
	// -(titleLabelHeight / 2) - 2);
	loginButton.setLayoutData(loginButtonFormData);

	// attach right button to right of banner and center vertically on
	// toolbar
	FormData rightButtonFormData = new FormData();
	rightButtonFormData.right = new FormAttachment(100, -10);
	rightButtonFormData.top = new FormAttachment(titleToolBar, 0,
		SWT.CENTER);
	// rightButtonFormData.top = new FormAttachment(50,
	// -(titleLabelHeight / 2) - 2);
	rightButton.setLayoutData(rightButtonFormData);

	// banner.pack();

	// create tab folder underneath but hide tabs
	// don't contruct tab items until after login
	tabFolder = new TabFolder(top, SWT.NONE);

	FormData tabFolderData = new FormData();
	tabFolderData.top = new FormAttachment(banner, tabFolderOffset);
	tabFolderData.left = new FormAttachment(0);
	tabFolderData.right = new FormAttachment(100);
	tabFolderData.bottom = new FormAttachment(100);
	tabFolder.setLayoutData(tabFolderData);

	return top;
    }

    /**
     * adds menu items to popup menu
     * 
     * @param menu
     *            parent menu for items
     * @param array
     *            of menu items (userModes) (eg Exploration Mode, Query Mode,
     *            etc
     */
    public void addMenuItems(final Menu menu, String[] userModes,
	    String[] userModeValues) {
	for (int i = 0; i < userModes.length; i++) {
	    MenuItem menuItem = new MenuItem(menu, SWT.PUSH);

	    menuItem.setText(userModeValues[i] + " Mode");
	    menuItem.addSelectionListener(new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent event) {
		    // note tabFolderIndex [0] is always on login class
		    MenuItem selected = (MenuItem) event.widget;
		    // System.out.println("titleToolItem="+selected.getText());
		    titleToolItem.setText(selected.getText());
		    // setTabFolderIndex(menu.indexOf(selected));
		    // System.out
		    // .println("selected.getText=" + selected.getText());
		    // System.out.println("menu.indexOf(selected)="
		    // + menu.indexOf(selected));
		    setTabFolderIndex(menu.indexOf(selected));
		    // if(menu.indexOf(selected) == 1) {
		    Control[] items = tabFolder.getChildren();
		    // Composite queryTab = (Composite) items[2];
		    Composite selectedTab = (Composite) items[menu
			    .indexOf(selected)];
		    if ((selectedTab.getChildren()[0]).getClass()
			    .getSimpleName().equalsIgnoreCase("ExplorerC")) {
			// ExplorerC qc =
			// (ExplorerC)(selectedTab.getChildren()[0]);
			// qc.getAWTContainer().requestFocus();
			// qc.runTreePanel().reset();
		    } else if ((selectedTab.getChildren()[0]).getClass()
			    .getSimpleName().indexOf("Query") >= 0) {
			// QueryC qc = (QueryC)(selectedTab.getChildren()[0]);
			// qc.getAWTContainer().requestFocus();
			// qc.runTreePanel().reset();
		    }
		    // }
		}
	    });
	}
    }// end of addMenuItems()

    /**
     * opens login dialog
     * 
     * @param parentShell
     */
    public void showLoginD(Shell parentShell) {
	// LoginDialog loginDNew = new LoginDialog(parentShell);
	// loginDNew.open();
    }

    private Control getTabQueryControl(TabFolder tabFolder) {
	Composite composite = new Composite(tabFolder, SWT.NONE);
	composite.setLayout(new FillLayout());
	// new QueryC(composite, 1);
	return composite;
    }

    private Control getTabInvestigatorControl(TabFolder tabFolder) {
	Composite composite = new Composite(tabFolder, SWT.NONE);
	composite.setLayout(new FillLayout());
	// new QueryC(composite, 0);
	return composite;
    }

    private Control getTabExpControl(TabFolder tabFolder) {
	Composite composite = new Composite(tabFolder, SWT.NONE);
	composite.setLayout(new FillLayout());
	explorer = new MainComposite(composite);
	return composite;
    }

    private Control getTabOntologyControl(TabFolder tabFolder) {
	Composite composite = new Composite(tabFolder, SWT.NONE);
	composite.setLayout(new FillLayout());
	// new OntologyFake(composite);
	return composite;
    }

    /**
     * gets fake control for dev, test or trouble shooting
     * 
     * @param tabFolder
     *            - parent TabFolder
     * @param buttonName
     *            text to appear on button in center of control
     * @return
     */
    private Control getTabLoginControl(TabFolder tabFolder, String buttonName) {
	Composite composite = new Composite(tabFolder, SWT.NONE);
	composite.setLayout(new FillLayout());
	LoginContent loginContentFake = new LoginContent(composite);
	loginContentFake.setMyButtonText(buttonName);
	return composite;
    }

    public void setCRCNavigatorNameProperties() {
	Properties properties = new Properties();
	String webServiceName = "";
	String filename = "crcnavigator.properties";
	try {
	    properties.load(new FileInputStream(filename));
	    webServiceName = properties.getProperty("applicationName");
	} catch (IOException e) {
	    log.error(e.getMessage());
	    webServiceName = "";
	}
	// log.info("webservicename="+webServiceName);
	System.setProperty("applicationName", webServiceName);
    }

    public String getCRCNavigatorProperties() {
	Properties properties = new Properties();
	String webServiceName = "";
	String filename = "crcnavigator.properties";
	try {
	    properties.load(new FileInputStream(filename));
	    webServiceName = properties.getProperty("webservicename");
	    // System.out.println("Properties webServiceName="+webServiceName);
	} catch (IOException e) {
	    log.error(e.getMessage());
	    webServiceName = "";
	}
	// log.info("webservicename="+webServiceName);
	return webServiceName;
    }

    /**
     * this method populates controls on login/logout using UserInfoBean
     * 
     * @param login
     *            - true if action is login, false if action is logout
     * 
     */
    public void loginAction(boolean login) {
	// UserInfoBean userInfoBean = null;
	// if login action true open dialog and wait for return

	if (login) {
	    // try to connect to tomcat server if not available show message and
	    // exit
	    String webservice = getCRCNavigatorProperties();
	    String msg = "";
	    // webservice=
	    // "http://phsi2b2appdev.mgh.harvard.edu:8080/i2b2/services/Select";
	    /*
	     * // try to connect to tomcat server if not available show message
	     * and logout try {
	     * 
	     * // SelectService service = new SelectServiceLocator(); //
	     * java.net.URL endpoint = new java.net.URL(webservice); //
	     * service.getSelect(endpoint); log.info(getNow() + " Start login");
	     * //LoginDialog loginDNew = new LoginDialog(getApp().getShell());
	     * //userInfoBean = //loginDNew.open(); // } catch
	     * (MalformedURLException e) { //e.printStackTrace();
	     * log.error(e.getMessage()); //MessageBox messageBox=new
	     * MessageBox(getApp().getShell(),SWT.OK);
	     * msg="The server is unavailable. Please try later.";
	     * //messageBox.setMessage(msg); //int reply=messageBox.open(); // }
	     * catch (ServiceException e) { //e.printStackTrace();
	     * msg="The web service is unavailable. Please try later";
	     * log.error(e.getMessage()); } if (!msg.equals("")){ MessageBox
	     * messageBox=new MessageBox(getApp().getShell(),SWT.OK);
	     * //msg="The i2b2 server is temporarily unavailable. Please try later."
	     * ; messageBox.setMessage(msg); messageBox.open(); }
	     */
	}

	// userInfoBean null means user pressed cancel- logout and close pages
	/*
	 * if (userInfoBean == null) { log.info(getNow() +
	 * " Login fail or cancel"); // System.out.println(getNow()+
	 * " Login fail or cancel");
	 * 
	 * // remove all tab items int numtabs = tabFolder.getItemCount(); if
	 * (numtabs > 0) { TabItem[] tabItemList = tabFolder.getItems(); for
	 * (int i = 0; i < tabItemList.length; i++) { tabItemList[i].dispose();
	 * } } // remove menu items int numitems = menu.getItemCount(); if
	 * (numitems > 0) { MenuItem[] menuItemList = menu.getItems(); for (int
	 * i = 0; i < menuItemList.length; i++) { menuItemList[i].dispose(); } }
	 * // set ui to login mode loginButton.setText(BUTTON_TEXT_LOGIN);
	 * titleToolItem.setText(userLoginMode);
	 * authorizationLabel.setText("Login Cancelled ..."); //
	 * mainShell.close(); statusLabelPaintListener.setOvalColor(badColor);
	 * statusOvalLabel.setSize(20,20);
	 * 
	 * statusOvalLabel.redraw(); titleToolItem.setEnabled(false);
	 * 
	 * } else {
	 */
	// login successful
	log.info(getNow() + " Login Successful");
	// System.out.println(System.getProperty("ExplorerConfigurationXML"));
	// todo this will be userInfo bean from login
	// String [] userModes={"Exploration Mode","Query Mode","Ontology
	// Mode"};
	/*
	 * OLD????? String[] userModes = userInfoBean.getUserModes(); String[]
	 * userModeValues = userInfoBean.getUserModeValues(); String
	 * userDefaultMode = userInfoBean.getDefaultUserMode(); String
	 * environment = userInfoBean.getEnvironment(); if
	 * (!userInfoBean.getHelpURL().equals(""))
	 * helpURL=userInfoBean.getHelpURL();
	 * 
	 * // add menu items addMenuItems(menu, userModes, userModeValues);
	 * 
	 * // load previous queries loadPreviousQueries();
	 * 
	 * // load Explorer control and other pages in tab folder here
	 * addTabFolders(userModes, userDefaultMode);
	 * 
	 * // set bannerC background color from config file here // if
	 * environment not specified defaults to development (dark gray)
	 * APP_CURRENT = environment; if (APP_CURRENT.equals(APP_PROD)) {
	 * backColor = getApp().getShell().getDisplay().getSystemColor(
	 * SWT.COLOR_WHITE); } else if (APP_CURRENT.equals(APP_TEST)) {
	 * backColor = getApp().getShell().getDisplay().getSystemColor(
	 * SWT.COLOR_GRAY); // default to dev } else { backColor =
	 * getApp().getShell().getDisplay().getSystemColor(
	 * SWT.COLOR_DARK_GRAY);
	 * 
	 * } // set the banner background setBannerBackColor(backColor);
	 * 
	 * // set initial text for toolbar- use first if no default specified
	 * String defaultMode = userInfoBean.getDefaultUserModeValue(); //
	 * System.out.println(" defaultMode="+defaultMode); //
	 * System.out.println("userMode[0]=" +userModeValues[0]); if
	 * (defaultMode.equals("")) { defaultMode = userModeValues[0]; }
	 * defaultMode = defaultMode + " Mode";
	 * titleToolItem.setText(defaultMode);
	 * 
	 * // set button text to logout loginButton.setText(BUTTON_TEXT_LOGOUT);
	 * // enable toolbar and set rest of banner controls
	 * titleToolItem.setEnabled(true);
	 * authorizationLabel.setText(UserInfoBean.getInstance().getUserName());
	 * statusLabelPaintListener.setOvalColor(goColor);
	 * statusOvalLabel.setSize(20,20); statusOvalLabel.redraw();
	 */
	// }
    }

    /**
     * @param tabList
     *            list of tab items to add creates item and loads control for it
     */
    public void addTabFolders(String[] tabList, String userDefaultMode) {
	for (int i = 0; i < tabList.length; i++) {
	    // InvestigatorC page
	    if (tabList[i].equalsIgnoreCase("investigator")) {

		tabExp = new TabItem(tabFolder, SWT.NULL);
		tabExp.setText(tabList[i]);
		Control exp = getTabInvestigatorControl(tabFolder);
		tabExp.setControl(exp);
		// explorer = (ExplorerC) exp;
		if (tabList[i].equalsIgnoreCase(userDefaultMode)) {
		    setTabFolderIndex(i);
		}

	    }
	    // ExplorerC page
	    else if (tabList[i].equalsIgnoreCase("exploration")) {

		tabExp = new TabItem(tabFolder, SWT.NULL);
		tabExp.setText(tabList[i]);
		Control exp = getTabExpControl(tabFolder);
		tabExp.setControl(exp);
		// explorer = (ExplorerC) exp;
		if (tabList[i].equalsIgnoreCase(userDefaultMode)) {
		    setTabFolderIndex(i);
		}

	    } else if (tabList[i].equalsIgnoreCase("query")) {
		// Query Page
		tabQuery = new TabItem(tabFolder, SWT.NULL);
		tabQuery.setText(tabList[i]);
		tabQuery.setControl(getTabQueryControl(tabFolder));
		if (tabList[i].equalsIgnoreCase(userDefaultMode)) {
		    setTabFolderIndex(i);
		}
	    } else if (tabList[i].equalsIgnoreCase("ontology")) {
		// Ontology Page
		tabOntology = new TabItem(tabFolder, SWT.NULL);
		tabOntology.setText(tabList[i]);
		tabOntology.setControl(getTabOntologyControl(tabFolder));
		if (tabList[i].equalsIgnoreCase(userDefaultMode)) {
		    setTabFolderIndex(i);
		}

	    } else {
		// new fake page use tabList[i] name as default label
		tabLogin = new TabItem(tabFolder, SWT.NULL);
		tabLogin.setText(tabList[i]);
		tabLogin.setControl(getTabLoginControl(tabFolder, tabList[i]
			+ " Fake "));
		if (tabList[i].equalsIgnoreCase(userDefaultMode)) {
		    setTabFolderIndex(i);
		}
	    }
	}
    }

    /**
     * opens help broswer in another display and separate thread
     * 
     * @param button
     *            - the control that calls this method
     * @returns a thread that creates a SWT browser control
     */
    public Thread showHelpBrowser(Button button) {
	// final Button theButton = button;
	final Shell shell = button.getShell();

	return new Thread() {
	    public void run() {
		new HelpBrowser().run(helpURL, shell);

		// Display display2 = new Display();
		// Shell shell2 = new Shell(display2);
		// shell2.setSize(800, 600);
		// shell2.setText("I2B2 Help");

		// shell2.setLayout(new FillLayout());
		// Browser browser = new Browser(shell2, SWT.NONE);
		// browser.setUrl(helpURL);

		// shell2.open();

		// update the first display
		// theButton.getParent().getDisplay();
		// display.asyncExec(new Runnable() {
		// public void run() {
		// theButton.setText(HELP_DISPLAYED);
		// }
		// });
		// while (!shell2.isDisposed()) {
		// if (!display2.readAndDispatch())
		// display2.sleep();
		// }
		// display2.dispose();
	    }
	};
    }

    /**
     * @return new thread to show broswer with logger html file
     */
    public Thread showLoggerBrowser(Shell shell) {
	// final Button theButton = button;
	final Shell myShell = shell;
	File file = new File(logFileName);
	URL url = null;
	// Convert the file object to a URL with an absolute path
	try {
	    url = file.toURL();
	} catch (MalformedURLException e) {
	    log.info(e.getMessage());
	}

	final URL myurl = url;
	return new Thread() {
	    public void run() {
		new HelpBrowser().run(myurl.toString(), myShell);
	    }
	};
    }

    /**
     * sets background color of banner composite
     * 
     * @param bc
     *            backcolor
     */
    public void setBannerBackColor(Color bc) {
	banner.setBackground(bc);
	titleLabel.setBackground(bc);
	titleToolBar.setBackground(bc);
	statusLabel.setBackground(bc);
	authorizationLabel.setBackground(bc);
	statusOvalLabel.setBackground(bc);
    }

    /**
     * class entry point
     * 
     * @param args
     */
    public static void main(String[] args) {
	// create application instance and set name to APP
	new CRCNavigator();
	// remove block on open so that dialog can show on initial startup
	// APP.setBlockOnOpen(true);
	APP.open();
	log.debug(getNow() + " AppStart debug  ");

	// get webservicename from crcnavigator.properties file
	String tempWeb = APP.getCRCNavigatorProperties();
	APP.setWebServiceName(tempWeb);

	// open login dialog
	APP.loginAction(true);
	log.debug(getNow() + " Returned from login dialog");

	Shell mainShell = APP.getShell();
	Display display = mainShell.getDisplay();
	while (mainShell != null && !mainShell.isDisposed()) {
	    try {
		if (!display.readAndDispatch())
		    display.sleep();
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	}
    }

    // inner class for statusLabel paint listener to enable it to be redrawn
    private class StatusLabelPaintListener implements PaintListener {

	private Color ovalColor = null;

	public Color getOvalColor() {
	    return ovalColor;
	}

	public void setOvalColor(Color ovalColor) {
	    this.ovalColor = ovalColor;
	}

	public StatusLabelPaintListener() {

	}

	public void paintControl(PaintEvent e) {
	    if (ovalColor != null) {
		e.gc.setBackground(ovalColor);
	    }
	    e.gc.fillOval(0, 0, 16, 16);
	}
    }

    // inner class for dropdown toolbar item listener
    private class DropDownListener implements Listener {
	private final ToolBar bar;

	private final Menu menu;

	private DropDownListener(ToolBar bar, Menu menu) {
	    super();
	    this.bar = bar;
	    this.menu = menu;
	}

	public void handleEvent(Event event) {
	    if (event.detail == SWT.ARROW) {
		Point point = new Point(event.x, event.y);
		point = bar.getParent().getDisplay().map(bar, null, point);
		menu.setLocation(point);
		menu.setVisible(true);
	    }
	}
    }

    /**
     * get current date as string used for logi
     * 
     * @return
     */
    public static String getNow() {
	return DateFormat.getDateTimeInstance().format(new Date());
    }
}
