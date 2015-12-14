/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *     Wensong Pan, Lori Phillips - The LoginView class provides the header/banner for the
 *  i2b2 Eclipse framework
 *     Mike Mendis 
 */

package edu.harvard.i2b2.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import edu.harvard.i2b2.eclipse.login.*;
import edu.harvard.i2b2.eclipse.util.Messages;
import edu.harvard.i2b2.pm.datavo.pm.PasswordType;


public class LoginView extends ViewPart  {
	public static final String ID = "edu.harvard.i2b2.eclipse.loginView"; //$NON-NLS-1$
	public static String noteKey = null;

	public static final String PREFIX = "edu.harvard.i2b2.eclipse";  //$NON-NLS-1$
	public static final String LOGIN_VIEW_CONTEXT_ID = PREFIX + ".login_view_help_context"; //$NON-NLS-1$

	private static Log log = LogFactory.getLog(LoginView.class.getName());

	private Composite top;
	public String msTitle = ""; //i2b2 Workbench for Asthma Project"; //$NON-NLS-1$
	public String msUsername = ""; //$NON-NLS-1$
	public String msPassword = ""; //$NON-NLS-1$
	public static LoginView APP;
	private String APP_PROD = "PRODUCTION"; //Production"; //$NON-NLS-1$
	private String APP_TEST = "TEST"; //$NON-NLS-1$
	private String APP_CURRENT = APP_PROD;
	private String helpURL = ""; //http://www.i2b2.org";	 //$NON-NLS-1$
	private String logFileName = "i2b2log.html"; //$NON-NLS-1$
	private Color goColor;
	private Color backColor;
	private Composite banner;
	private CLabel titleLabel;
	private ToolBar titleToolBar;
	private Label authorizationLabel;
	private Label statusOvalLabel;
	private StatusLabelPaintListener statusLabelPaintListener;
	private Label statusLabel;
	private String OS = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
	private static int iDEFAULT_TIMEOUTINMILLISECONDS = 1800000;

	public static String BUTTON_TEXT_LOGIN =  Messages.getString("LoginView.ButtonLogIn"); //$NON-NLS-1$

	public static String BUTTON_TEXT_LOGOUT = Messages.getString("LoginView.ButtonLogout"); //$NON-NLS-1$

	public Button loginButton;

	public String userLoginMode = Messages.getString("LoginView.LoginMode"); //$NON-NLS-1$

	//host webservice for application stored in crcnavigator.properties file 
	public String webServiceName;

	// color and gui
	Color badColor;
	Color warningColor;
	Color grayColor;
	Color devColor;
	Color prodColor;
	Color testColor;


	// how much to offset the folder so tab text does not show
	// to do compute fontsize or use another way
	int tabFolderOffset = -20;

	public static UserInfoBean userInfoBean;

	// popup menu for toolbar
	public Menu menu;

	// tabfolder
	public TabFolder tabFolder;

	public TabItem tabLogin;

	public TabItem tabExp;

	public TabItem tabQuery;

	public TabItem tabOntology;

	public int tabFolderIndex;

	public static final LoginView getApp() {
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
	 * The constructor.
	 */
	public LoginView() {

	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		log.info(Messages.getString("LoginView.PluginVersion")); //$NON-NLS-1$

		parent.getShell();
		userInfoBean = UserInfoBean.getInstance();
		if(userInfoBean == null){
			log.debug("user info bean is null"); //$NON-NLS-1$
			return;
		}


		// local variable to get system fonts and colors
		final Display display = parent.getDisplay();


		/* TODO disabled screensaver */

		user = userInfoBean.getUserName();
		//password = userInfoBean.getOrigPassword();
		project= Application.project;
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (Throwable th) {
					}
					if (display.isDisposed())
						return;
					display.asyncExec(new Runnable() {
						public void run() {
							checkSessionExpired();
							try{
								checkScreenSaver(parent.getShell(), Application.getLastUsed()); //display.getActiveShell());
							}
							catch(Exception e) {
								return;
							}	  				
						}
					});
				}
			}
		}.start();



		final Font headerFont = new Font(display, "Tahoma", 12, SWT.BOLD); //$NON-NLS-1$
		final Font normalFont = new Font(display, "Tahoma", 12, SWT.NORMAL); //$NON-NLS-1$
		final Font buttonFont = new Font(display, "Tahoma", 9, SWT.NORMAL); //$NON-NLS-1$

		String environment = userInfoBean.getEnvironment();
		if (UserInfoBean.selectedProject().getWiki() !=null && ! UserInfoBean.selectedProject().getWiki().equals("")) //$NON-NLS-1$
			helpURL=UserInfoBean.selectedProject().getWiki();
		//if (userInfoBean.getHelpURL()!=null && !userInfoBean.getHelpURL().equals(""))
		//	helpURL=userInfoBean.getHelpURL();
		// set banner color
		// if environment not specified defaults to development (dark gray)
		APP_CURRENT = environment.toUpperCase();
		if (APP_CURRENT.equals(APP_PROD)) {
			backColor = display.getSystemColor(
					SWT.COLOR_WHITE);
		} else if (APP_CURRENT.equals(APP_TEST)) {
			backColor = display.getSystemColor(
					SWT.COLOR_GRAY);
		} else {
			// default to development
			backColor = display.getSystemColor(
					SWT.COLOR_DARK_GRAY);
		}

		log.info("Currently running in: " + APP_CURRENT); //$NON-NLS-1$
		final Color foreColor = display.getSystemColor(SWT.COLOR_BLACK);
		warningColor = display.getSystemColor(SWT.COLOR_YELLOW);

		goColor = display.getSystemColor(SWT.COLOR_GREEN);
		badColor = display.getSystemColor(SWT.COLOR_RED);

		// create top composite
		top = new Composite(parent, SWT.NONE);

		FormLayout topCompositeLayout = new FormLayout();
		top.setLayout(topCompositeLayout);

		// BannerC composite
		banner = new Composite(top, SWT.NONE);

		FormData bannerData = new FormData();
		bannerData.left = new FormAttachment(0);
		bannerData.right = new FormAttachment(100);
		banner.setLayoutData(bannerData);

		// The Banner itself is configured and layout is set
		FormLayout bannerLayout = new FormLayout();
		bannerLayout.marginWidth = 2;
		if (OS.startsWith("windows")) //$NON-NLS-1$
			bannerLayout.marginHeight = 8;
		else
			bannerLayout.marginHeight = 18;
		bannerLayout.spacing = 5;
		banner.setLayout(bannerLayout);

		banner.setBackground(backColor);
		banner.setForeground(foreColor);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(banner, LOGIN_VIEW_CONTEXT_ID);

		// add banner components and then configure layout
		// the label on the left is added
		titleLabel = new CLabel(banner, SWT.NO_FOCUS);
		titleLabel.setBackground(backColor);
		msTitle = System.getProperty("applicationName") + Messages.getString("LoginView.StatusTitle")+ UserInfoBean.selectedProject().getName(); //$NON-NLS-1$ //$NON-NLS-2$
		titleLabel.setText(msTitle);
		titleLabel.setFont(headerFont);
		titleLabel.setForeground(foreColor);
		titleLabel.setImage(new Image(display, LoginView.class.getResourceAsStream("big-hive.gif"))); //$NON-NLS-1$

		// the general application area toolbar is added
		titleToolBar = new ToolBar(banner, SWT.FLAT);
		titleToolBar.setBackground(backColor);
		titleToolBar.setFont(headerFont);

		menu = new Menu(banner.getShell(), SWT.POP_UP);

		// Authorization label is made
		authorizationLabel = new Label(banner, SWT.NO_FOCUS);
		authorizationLabel.setBackground(backColor);
		authorizationLabel.setText(userInfoBean.getUserFullName());
		authorizationLabel.setAlignment(SWT.RIGHT);
		authorizationLabel.setFont(normalFont);
		authorizationLabel.setForeground(foreColor);
		ArrayList<String> roles = (ArrayList<String>) UserInfoBean.selectedProject().getRole();

		String rolesStr = ""; //$NON-NLS-1$
		if (roles != null)
		{
			for (String param :roles)
				rolesStr += param + "\n"; //$NON-NLS-1$
					if (rolesStr.length() > 1)
						rolesStr = rolesStr.substring(0, rolesStr.length()-1);
		}
		authorizationLabel.setToolTipText(rolesStr);

		// the staus indicator is shown
		statusLabel = new Label(banner, SWT.NO_FOCUS);
		statusLabel.setBackground(backColor);
		statusLabel.setText(Messages.getString("LoginView.StatusStatus")); //$NON-NLS-1$
		statusLabel.setAlignment(SWT.RIGHT);
		statusLabel.setFont(normalFont);
		statusLabel.setForeground(foreColor);

		statusOvalLabel = new Label(banner, SWT.NO_FOCUS);
		statusOvalLabel.setBackground(backColor);

		statusOvalLabel.setSize(20,20);
		statusOvalLabel.setForeground(foreColor);
		statusOvalLabel.redraw();

		statusOvalLabel.addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event arg0) {
				statusOvalLabel.setSize(20,20);
				statusOvalLabel.redraw();
			}
		});

		// add selection listener so that clicking on status oval label shows error log
		// dialog
		statusOvalLabel.addListener(SWT.MouseDown, new Listener() {

			public void handleEvent(Event arg0) {
				//log.info(getNow() + "Status Listener Clicked");
				Display display = statusOvalLabel.getDisplay();
				final Shell shell = statusOvalLabel.getShell();
				// run asyncExec so that other pending ui events finished first
				display.asyncExec(new Runnable() {

					public void run() {
						File file = new File(logFileName);
						URL url = null;
						// Convert the file object to a URL with an absolute path
						try {
							url = file.toURL();
						} catch (MalformedURLException e) {
							log.error(e.getMessage());
						}
						final URL myurl = url;
						new HelpBrowser().run(myurl.toString(),shell);		
					}
				});

			}
		});

		// add status label paint listener so that it changes color
		statusLabelPaintListener = new StatusLabelPaintListener();
		statusOvalLabel.addPaintListener(statusLabelPaintListener);
		statusLabelPaintListener.setOvalColor(goColor);

		getCellStatus(statusLabelPaintListener, statusOvalLabel);
		//if (cellStatus == null)
		//	{
		//	statusLabelPaintListener.setOvalColor(goColor);
		//}
		//else
		//	{
		//	statusOvalLabel.setToolTipText(Messages.getString("LoginView.TooltipCellUnavailable") + cellStatus); //$NON-NLS-1$
		//	statusLabelPaintListener.setOvalColor(warningColor);
		//	}

		statusOvalLabel.setSize(20,20);
		statusOvalLabel.redraw();

		// Help button is made
		final Button rightButton = new Button(banner, SWT.PUSH | SWT.LEFT);
		rightButton.setFont(buttonFont);
		rightButton.setText(Messages.getString("LoginView.StatusWiki")); //$NON-NLS-1$
		if(helpURL.equals("")) { //$NON-NLS-1$
			rightButton.setEnabled(false);
		}
		rightButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				final Button myButton = (Button) event.widget;
				Display display=myButton.getDisplay();
				final Shell myShell=myButton.getShell();
				display.asyncExec(new Runnable() {

					public void run() {
						new HelpBrowser().run(helpURL,myShell);
					}
				});	
			}
		});
		
				
		final Button passwordButton = new Button(banner, SWT.PUSH | SWT.LEFT);
		passwordButton.setFont(buttonFont);
		passwordButton.setText("Password"); 
		passwordButton.setToolTipText("Display Set Password Dialog");
		passwordButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final Button myButton = (Button) event.widget;
				Display display=myButton.getDisplay();
				//final Shell myShell=myButton.getShell();
				display.asyncExec(new Runnable() {
					public void run() {
						java.awt.EventQueue.invokeLater(new Runnable() {
				            public void run() {
				                SetPasswordJDialog dialog = new SetPasswordJDialog(new javax.swing.JFrame(), true);
				                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
				                    @Override
				                    public void windowClosing(java.awt.event.WindowEvent e) {
				                        //System.exit(0);
				                    }
				                });
				                dialog.setSize(304, 237);
				                dialog.setLocation(400, 200);
				                dialog.setVisible(true);
				            }
				        });
					}
				});	
			}
		});			

		// attach titlelabel to left and align vertically with tool bar
		FormData titleLabelFormData = new FormData();

		titleLabelFormData.top = new FormAttachment(titleToolBar, 0, SWT.CENTER);
		titleLabelFormData.left = new FormAttachment(0, 10);
		titleLabel.setLayoutData(titleLabelFormData);

		// attach left of tool bar to title label, attach top to banner
		// attach right to authorization label so that it will resize and remain
		// visible when tool bar text changes
		FormData titleToolBarFormData = new FormData();
		titleToolBarFormData.left = new FormAttachment(titleLabel);
		titleToolBarFormData.top = new FormAttachment(0);
		titleToolBarFormData.right = new FormAttachment(authorizationLabel, 0, 0);

		titleToolBar.setLayoutData(titleToolBarFormData);

		// attach authorization label on right to status label and center
		// vertically

		FormData authorizationLabelFormData = new FormData();
		authorizationLabelFormData.right = new FormAttachment(passwordButton, -10);
		authorizationLabelFormData.top = new FormAttachment(passwordButton, 0, SWT.CENTER);
		authorizationLabel.setLayoutData(authorizationLabelFormData);
		
		FormData passwordButtonFormData = new FormData();
		passwordButtonFormData.right = new FormAttachment(statusLabel, -10);
		passwordButtonFormData.top = new FormAttachment(statusLabel, 0, SWT.CENTER);
		passwordButton.setLayoutData(passwordButtonFormData);

		FormData statusLabelFormData = new FormData();
		// statusLabelFormData.right = new FormAttachment(rightButton,0);
		statusLabelFormData.right = new FormAttachment(statusOvalLabel, 0);
		statusLabelFormData.top = new FormAttachment(statusOvalLabel, 0, SWT.CENTER);
		statusLabel.setLayoutData(statusLabelFormData);

		// attach status label on right to loginbutton and center vertically

		FormData statusOvalLabelFormData = new FormData();
		//add offset 
		statusOvalLabelFormData.right = new FormAttachment(rightButton, -25);
		statusOvalLabelFormData.top = new FormAttachment(rightButton, 0, SWT.CENTER);
		statusOvalLabel.setLayoutData(statusOvalLabelFormData);

		// attach right button to right of banner and center vertically on
		// toolbar
		FormData rightButtonFormData = new FormData();
		rightButtonFormData.right = new FormAttachment(100, -10);
		rightButtonFormData.top = new FormAttachment(titleToolBar, 0, SWT.CENTER);
		rightButton.setLayoutData(rightButtonFormData);

		//property action
		IAction propertyAction = new Action("Property") { //$NON-NLS-1$
			@Override
			public void run() {
				log.info("[Login view] PM response: "+UserInfoBean.pmResponse()); //$NON-NLS-1$
				JFrame frame = new DisplayXmlMessageDialog(UserInfoBean.pmResponse());
				frame.setTitle(Messages.getString("LoginView.PMXMLResponse")); //$NON-NLS-1$
				frame.setVisible(true);  
			}
		};

		getViewSite().getActionBars().setGlobalActionHandler("properties", propertyAction); //$NON-NLS-1$
	}

	private  boolean inScreenSaver=false;
	private  boolean inSessionExpired=false;
	private static Project project = null;
	private static String user = null;
	//private static String password = null;

	private void checkSessionExpired()
	{

		//		make a date to compare with

		if(UserInfoBean.getLastActivityTime()==null || this.inSessionExpired || this.inScreenSaver) return;
		this.inSessionExpired=true;
		try {
			Calendar c=Calendar.getInstance();
			if (UserInfoBean.getInstance().getUserPasswordTimeout() < 60000)
			{
				c.add(Calendar.MILLISECOND,-UserInfoBean.getInstance().getUserPasswordTimeout()); //-20);//TODO changed from -20 to -1
			} else {
				c.add(Calendar.MILLISECOND, -UserInfoBean.getInstance().getUserPasswordTimeout()+60000);//-20); //subtract 20 minutes;
			}
			//log.info("timeout: " + UserInfoBean.getInstance().getUserPasswordTimeout());
			//log.info("last activity: " + UserInfoBean.getLastActivityTime()
					//+ " c_time: "+ c.getTime() + " current: "+ Calendar.getInstance().getTime());
			if(!UserInfoBean.getLastActivityTime().after(c.getTime()))
			{
				LoginHelper loginHelper = new LoginHelper();

				/*
				PasswordType ptype = new PasswordType();
				ptype.setValue(password);
				ptype.setIsToken(false);
				 */

				PasswordType ptype = UserInfoBean.getInstance().getUserPasswordType();
				ptype.setIsToken(false);
				ptype.setTokenMsTimeout(getWorkbenchTimeoutInMiliseconds());
				
				UserInfoBean ubean = loginHelper.getUserInfo(
						user, ptype, project.getUrl(), project.getName(), false);


				UserInfoBean.getInstance().setUserPassword(ubean.getUserPassword());
				UserInfoBean.setLastActivityTime(Calendar.getInstance().getTime());

				//System.out.println("New Seesion is: " + UserInfoBean.getInstance().getUserPassword());
				log.info("Start new session: " + UserInfoBean.getInstance().getUserPassword()
						+ " at "+ Calendar.getInstance().getTime());
				log.info("Set time out to: " + UserInfoBean.getInstance().getUserPasswordTimeout());
			}
		} catch (Exception e)
		{
			log.error(e.getMessage());
		} finally {
			this.inSessionExpired=false;
		}
		return;
	}


	private void checkScreenSaver(Shell shell, Date lastUsed)
	{

		//		make a date to compare with

		if (shell == null)
		{
			this.inScreenSaver=false;
			return;

		}
		if(UserInfoBean.getScreenSaverTimer()==null || this.inScreenSaver) return;
		this.inScreenSaver=true;
		try {
			Calendar c=Calendar.getInstance();
			UserInfoBean.setScreenSaverTimer(lastUsed);

			if (UserInfoBean.getInstance().getUserPasswordTimeout() < 60000)
			{
				c.add(Calendar.MILLISECOND,-UserInfoBean.getInstance().getUserPasswordTimeout());//-20); //TODO changed from -20 to -1

			} else {
				//c.add(Calendar.MINUTE,-20); 
				c.add(Calendar.MILLISECOND, -UserInfoBean.getInstance().getUserPasswordTimeout() + 60000);//-20); //subtract 20 minutes;
			}
			
			//log.info("timeout: " + UserInfoBean.getInstance().getUserPasswordTimeout());
			//log.info("screen saver: " + UserInfoBean.getScreenSaverTimer()
					//+ " c_time: "+ c.getTime() + " current: "+ Calendar.getInstance().getTime());
			if(!UserInfoBean.getScreenSaverTimer().after(c.getTime()))

			{

				UserInfoBean userInfoBean = null;
				do {
					//Shell activeShell = new Shell(); //!!!!!!!!cant do this because its the wrong thread
					ReLoginDialog loginDialog = new ReLoginDialog(shell);	
					loginDialog.setUserid(user);
					loginDialog.setCurrentPrj(project);

					userInfoBean = loginDialog.open();		

					//activeShell.close();

				}
				while (userInfoBean == null);
				UserInfoBean.setScreenSaverTimer(Calendar.getInstance().getTime());
				UserInfoBean.getInstance().setUserPassword(userInfoBean.getUserPasswordType());
				log.info("Start new session (screen saver): " + UserInfoBean.getInstance().getUserPassword()
						+ " at "+ Calendar.getInstance().getTime());
				log.info("Set time out to: " + UserInfoBean.getInstance().getUserPasswordTimeout());
			}
		} catch (Exception e)
		{
			log.error(e.getMessage());
		} finally {
			this.inScreenSaver=false;
		}
		return;
	}

	private void getCellStatus(StatusLabelPaintListener statusLabelPaintListener2, Label statusLabel)
	{
		StringBuffer result = new StringBuffer();
		boolean coreDown = false;
		if (userInfoBean.getCellList() == null)
		{
			statusLabelPaintListener.setOvalColor(badColor);
			return;
		}
		for (String cellID: userInfoBean.getCellList())
		{
			try {
				URL url = new URL(userInfoBean.getCellDataUrl(cellID));
				URLConnection connection = url.openConnection();
				connection.connect();
			} catch (MalformedURLException e) {     // new URL() failed
				log.debug(e.getMessage());
				if (userInfoBean.isCoreCell(cellID))
					coreDown = true;
				result.append(userInfoBean.getCellName(cellID));
				result.append("\n"); //$NON-NLS-1$
			} catch (IOException e) {               // openConnection() failed
				log.debug(e.getMessage());
				if (userInfoBean.isCoreCell(cellID))
					coreDown = true;
				result.append(userInfoBean.getCellName(cellID));
				result.append("\n"); //$NON-NLS-1$
			}
		}

		if (result.length() > 0)
		{
			statusOvalLabel.setToolTipText(Messages.getString("LoginView.TooltipCellUnavailable") + result.toString()); //$NON-NLS-1$

			if (coreDown)
				statusLabelPaintListener.setOvalColor(badColor);
			else
				statusLabelPaintListener.setOvalColor(warningColor);
		}
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		banner.setFocus();
	}

	/**
	 * opens login dialog
	 * 
	 * @param parentShell
	 */
	public void showLoginD(Shell parentShell) {
		LoginDialog loginDNew = new LoginDialog(parentShell);
		loginDNew.open();
	}


	/**
	 * opens help broswer in another display and separate thread
	 * 
	 * @param button-
	 *            the control that calls this method
	 * @returns a thread that creates a SWT browser control
	 */
	public Thread showHelpBrowser(Button button) {
		final Shell shell=button.getShell();

		return new Thread() {
			@Override
			public void run() {
				new HelpBrowser().run(helpURL,shell);
			}
		};
	}

	/**
	 * opens logger browser in another display and separate thread
	 * 
	 * @param shell-
	 *            the control that calls this method
	 * 
	 * @return new thread to show browser with logger html file 
	 */
	public Thread showLoggerBrowser(Shell shell) {
		final Shell myShell=shell;
		File file = new File(logFileName);
		URL url = null;
		// Convert the file object to a URL with an absolute path
		try {
			url = file.toURL();
		} catch (MalformedURLException e) {
			log.debug(e.getMessage());
		}

		final URL myurl = url;
		return new Thread() {
			@Override
			public void run() {
				new HelpBrowser().run(myurl.toString(),myShell);
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

	//	 inner class for statusLabel paint listener to enable it to be redrawn
	private class StatusLabelPaintListener implements PaintListener {
		private Color ovalColor=null;
		public Color getOvalColor() {
			return ovalColor;
		}

		public void setOvalColor(Color ovalColor) {
			this.ovalColor = ovalColor;
		}

		public StatusLabelPaintListener() {
		}

		public void paintControl(PaintEvent e) {
			if(ovalColor != null) {
				e.gc.setBackground(ovalColor);
			}
			e.gc.fillOval(0, 0, 16, 16);
		}
	}
	
	/**
	 * Method to get the timeout in milliseconds of the workbench token from
	 * the workbench properties file.
	 * 
	 * @return  int TimeoutInMilliseconds
	 * 
	 */
	private int getWorkbenchTimeoutInMiliseconds() {
		Properties properties = new Properties();
		String sTimeout=""; //$NON-NLS-1$
		int iTimeoutInMilliseconds = iDEFAULT_TIMEOUTINMILLISECONDS;
		String filename=Messages.getString("Application.PropertiesFile"); //$NON-NLS-1$
		try {
			properties.load(new FileInputStream(filename));
			sTimeout=properties.getProperty("TimeoutInMilliseconds"); //$NON-NLS-1$
			iTimeoutInMilliseconds = Integer.parseInt(sTimeout);
		} catch (Exception e) {
			log.info("Could not find TimeoutInMilliseconds in " + filename); 
			iTimeoutInMilliseconds = iDEFAULT_TIMEOUTINMILLISECONDS;
		}
		log.info("workbench timeout in milliseconds set to: " + iTimeoutInMilliseconds); //$NON-NLS-1$
		return iTimeoutInMilliseconds;
	}

	/**
	 * get current date as string used for logi
	 * 
	 * @return
	 */
	public static String getNow() {
		return DateFormat.getDateTimeInstance().format(new Date());
	}

	public static UserInfoBean getUserInfoBean() {
		return userInfoBean;
	}

}