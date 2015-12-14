/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 
 * 	Wensong Pan
 */


package edu.harvard.i2b2.eclipse.login;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;

import javax.xml.bind.JAXBElement;

//import org.apache.axis2.AxisFault;
//import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.logging.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.*;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.util.Messages;
import edu.harvard.i2b2.eclipse.util.ProjectManagementJAXBUtil;
import edu.harvard.i2b2.pm.datavo.pm.PasswordType;

//import edu.harvard.i2b2.common.pm.UserInfoBean;

public class ReLoginDialog extends Dialog {

	private static final Log log = LogFactory.getLog(ReLoginDialog.class);

	public static final String OS = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$

	private static ReLoginDialog instance = null;

	//private static final String LOGIN_FAILED_MSG = "";

	private String title; // dialog title

	private String input; // return from dialog

	private UserInfoBean userInfo ; //return from dialog

	private String userid; 

	private static int iDEFAULT_TIMEOUTINMILLISECONDS = 1800000;
	
	private String password; 

	private String loginStatus; // message on login status

	private String appName = null;

	//private String defaultProject = null;
	private Project currentPrj = null;

	public void setCurrentPrj(Project currentPrj) {
		this.currentPrj = currentPrj;
	}



	private ArrayList<Project> projects = new ArrayList<Project>();
	//private Project defaultProject; 
	//private Hashtable pmAddresses = null;

	//private String pmAddress = null;

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setUserInfoBean(UserInfoBean info){
		this.userInfo = info;
	}

	public UserInfoBean getUserInfoBean(){
		return userInfo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * constructor
	 * 
	 * @param parent
	 *            the parent
	 */
	public ReLoginDialog(Shell parent) {

		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.ON_TOP | SWT.PRIMARY_MODAL) ;
		if (instance == null)
			instance = this;	

		//pmAddresses = new Hashtable();
	}

	/**
	 * constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public ReLoginDialog(Shell parent, int style) {
		// Let caller override the default styles
		super(parent, style);
		if (instance == null) {
			instance = this;
		}

		setText(Messages.getString("LoginDialog.ReText")+ System.getProperty("applicationName")); //$NON-NLS-1$ //$NON-NLS-2$
		setTitle(Messages.getString("LoginDialog.Title")); //$NON-NLS-1$
		setUserid(System.getProperty("user.name")); //$NON-NLS-1$
		setPassword(""); //$NON-NLS-1$


	}

	public static ReLoginDialog getInstance() {
		return instance;
	}

	/**
	 * Opens dialog and returns the input in a UserInfo object
	 * 
	 * @return  UserInfo object
	 */
	public UserInfoBean open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		shell.setSize(new Point(295, 238));
		shell.setLocation(400, 200);	
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value or null
		return userInfo;
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(null);

		///Cancel the reaunthentication timer while dialog is showing
		  //             Timer t=UserInfoBean.getReauthenticationTimer();
		    //           if(t!=null)
		      //                 t.cancel(); 

		
		// row for login message
		final Label labelMsg = new Label(shell, SWT.CENTER | SWT.SHADOW_NONE | SWT.WRAP);
		labelMsg.setText(Messages.getString("HiveLoginDialog.ReEnterCredential")); //$NON-NLS-1$
		if (OS.startsWith("mac")) //$NON-NLS-1$
			labelMsg.setBounds(new Rectangle(18, 5, 267, 35));
		else
			labelMsg.setBounds(new Rectangle(18, 5, 260, 30));

		// row for project label/prompt
		/*
		Label projectLabel = new Label(shell, SWT.NULL);
		projectLabel.setText(Messages.getString("LoginDialog.8")); //$NON-NLS-1$
		projectLabel.setBounds(new Rectangle(18, 42, 85, 18));
		*/

		String filename=Messages.getString("Application.PropertiesFile"); //$NON-NLS-1$
		Properties properties = new Properties();
		//Boolean demoFlag = false;
		//String[] projectNames = null;
		//ArrayList<Project> projectName = new ArrayList<Project>();
		try {
			properties.load(new FileInputStream(filename));
			appName = properties.getProperty("applicationName"); //$NON-NLS-1$

			if(appName == null || appName.equals("")  ) //$NON-NLS-1$
			{
				MessageBox messageBox =
					new MessageBox(shell,
							SWT.OK|
							SWT.ICON_ERROR);
				messageBox.setMessage(Messages.getString("LoginDialog.12")); //$NON-NLS-1$
				messageBox.open();
				log.info(Messages.getString("LoginDialog.13")); //$NON-NLS-1$
				System.exit(0);
			}

			System.setProperty("applicationName", appName); //$NON-NLS-1$

			String demoUserFlag = properties.getProperty("demoUser"); //$NON-NLS-1$
			if (demoUserFlag == null)				
				System.setProperty("demoUser", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			else
				System.setProperty("demoUser", demoUserFlag); //$NON-NLS-1$

			//String projects = properties.propertyNames(); //.getProperty(appName + ".1"); //"projects");
			/*
			Enumeration propertyNames = properties.propertyNames();

			while (propertyNames.hasMoreElements()) {

				String propertyName = (String)propertyNames.nextElement();

				if (propertyName.toUpperCase().startsWith(appName.toUpperCase()))
				{
					try {
						String[] propertyValue = properties.getProperty(propertyName).split(","); //$NON-NLS-1$
						if (propertyValue.length > 2){
							Project prj = new Project();
							prj.setId(propertyName);
							prj.setName(propertyValue[0]);
							prj.setMethod(propertyValue[1]);
							prj.setUrl(propertyValue[2]);
							projects.add(prj);
							if (currentPrj == null)
								currentPrj = projects.get(0);

						}
						else {
							MessageBox messageBox =
								new MessageBox(shell,
										SWT.OK|
										SWT.ICON_ERROR);
							messageBox.setMessage(Messages.getString("LoginDialog.PMLocation1") + propertyName + Messages.getString("LoginDialog.PMLocation2")); //$NON-NLS-1$ //$NON-NLS-2$
							messageBox.open();
							log.info("PM Target location " + propertyName + " not specified properly"); //$NON-NLS-1$ //$NON-NLS-2$
							System.exit(0);
						}
					} catch (Exception ee)
					{
						ee.printStackTrace();

					}
				}

			}	        
			*/
			if(currentPrj == null) {
				MessageBox messageBox =
					new MessageBox(shell,
							SWT.OK|
							SWT.ICON_ERROR);
				messageBox.setMessage(Messages.getString("LoginDialog.NOPMProvided") + appName); //$NON-NLS-1$
				messageBox.open();
				log.info("No PM target locations were provided that have prefix of " + appName); //$NON-NLS-1$
				System.exit(0);
			}
		} 
		catch (IOException e) {
			MessageBox messageBox =
				new MessageBox(shell,
						SWT.OK|
						SWT.ICON_ERROR);
			messageBox.setMessage(e.getMessage());
			messageBox.open();
			log.error(e.getMessage());
			System.exit(0);
		}
		// row for user label/prompt
		Label labelUser = new Label(shell, SWT.NULL);
		labelUser.setText(Messages.getString("LoginDialog.UserName")); //$NON-NLS-1$
		labelUser.setBounds(new Rectangle(18, 68, 62, 13));

		textUser = new Text(shell, SWT.SINGLE | SWT.BORDER);
		textUser.setText(userid);
	//	if(projectCombo.getItem(0).equalsIgnoreCase("rpdr")) { //$NON-NLS-1$
	//		textUser.setText("partners\\"+userid); //$NON-NLS-1$
	//	}
		if (OS.startsWith("mac")) //$NON-NLS-1$
			textUser.setBounds(new Rectangle(104, 65, 170, 27));
		else
			textUser.setBounds(new Rectangle(104, 65, 170, 21));

		textUser.setEditable(false);
		// row for password label/prompt
		Label labelPassword = new Label(shell, SWT.NULL);
		labelPassword.setText(Messages.getString("LoginDialog.Password")); //$NON-NLS-1$
		labelPassword.setBounds(new Rectangle(18, 92, 59, 16));

		textPassword = new Text(shell, SWT.SINGLE | SWT.BORDER);
		if (OS.startsWith("mac"))		 //$NON-NLS-1$
			textPassword.setBounds(new Rectangle(104, 93, 170, 25));
		else
			textPassword.setBounds(new Rectangle(104, 91, 170, 21));

		textPassword.setText(password);
		textPassword.setEchoChar('*');
		textPassword.setFocus();

		// create ok button and add handler
		// pressing it will set userid and start login query
		final Button ok = new Button(shell, SWT.PUSH);
		ok.setText(Messages.getString("LoginDialog.ButtonLogin")); //$NON-NLS-1$
		if (OS.startsWith("mac"))	 //$NON-NLS-1$
			ok.setBounds(new Rectangle(87, 144, 94, 30));
		else
			ok.setBounds(new Rectangle(147, 144, 54, 23));

		// add selection handler
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				labelMsg.setText(Messages.getString("LoginDialog.LogginIn")); //$NON-NLS-1$
				//currentPrj = getProject(projectCombo.getSelectionIndex());
				//pmAddress = getProject(projectCombo.getText()).getUrl();
				System.setProperty("webServiceMethod", currentPrj.getMethod()); //$NON-NLS-1$
				//(String) pmAddresses.get(projectCombo.getText());

				PasswordType ptype = new PasswordType();
				ptype.setValue(textPassword.getText());
				ptype.setIsToken(false);
				ptype.setTokenMsTimeout(getWorkbenchTimeoutInMiliseconds());
				
					LoginThread loginThread = new LoginThread(textUser.getText()
							.trim(), ptype,
							currentPrj.getUrl(),
							//(String) pmAddresses.get(projectCombo.getText()), 
							currentPrj.getName(), false);
					// shows busy caret, spawns thread and blocks until return
					BusyIndicator.showWhile(ok.getDisplay(), loginThread);
					String userName = loginThread.getUserBean().getUserName();
					log.debug("Login name for userId="+textUser.getText() //$NON-NLS-1$
							.trim() + ", userName=" +userName );				  //$NON-NLS-1$
					// if login fails, set message text and return to dialog
					if (userName == null) {
						log.debug("Login Fail for userid="+textUser.getText().trim()); //$NON-NLS-1$
						//log.info("Login Fail for userid="+textUser.getText().trim());
						labelMsg.setForeground(labelMsg.getParent().getDisplay().getSystemColor(SWT.COLOR_DARK_RED));

						if (loginThread.getMsg() != null)
							labelMsg.setText(loginThread.getMsg());

						//labelMsg.setText("Unable to connect to server.");
						//LOGIN_FAILED_MSG + " " +System.getProperty("statusMessage"));
						textUser.setText(textUser.getText().trim());

					} else {
						//login succeeded, return userInfoBean and close dialog
						labelMsg.setText(Messages.getString("LoginDialog.LoginOK") + userName); //$NON-NLS-1$
						textUser.setText(textUser.getText().trim());
						//return UserInfo object
						userInfo=loginThread.getUserBean();
						
						

				
												
						shell.close();					
					}
				//}
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null and close window
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Exit"); //$NON-NLS-1$
		if (OS.startsWith("mac"))	 //$NON-NLS-1$
			cancel.setBounds(new Rectangle(187, 144, 90, 30));
		else
			cancel.setBounds(new Rectangle(217, 144, 50, 23));
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// do something here- return null
				userInfo = null;
				shell.close();
				System.exit(0);
			}
		});
/*
		statusMsg = new Label(shell, SWT.BORDER | SWT.LEFT | SWT.WRAP);
		statusMsg.setText(currentPrj.getUrl());
		//(String) pmAddresses.get(defaultProject.getName()));
		Font font = statusMsg.getFont();
		//font.size = 10; //only for mac
		statusMsg.setFont(font);
		statusMsg.setBounds(new Rectangle(4, 182, 281, 27));
*/
		// Set the OK button as the default
		shell.setDefaultButton(ok);	
	}

	/**
	 * Method to get the message version of the workbench.
	 * 
	 * @return  version
	 * 
	 */
	private String getWorkbenchMessageVersion() {
		Properties properties = new Properties();
		String version=""; //$NON-NLS-1$
		String filename=Messages.getString("Application.PropertiesFile"); //$NON-NLS-1$
		try {
			properties.load(new FileInputStream(filename));
			version=properties.getProperty("messageversion"); //$NON-NLS-1$
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		log.info("workbench message version="+version); //$NON-NLS-1$
		return version;
	}

	/*
	private Project getProject(String id)
	{
		for (Project project: projects)
		{
			if (id.equalsIgnoreCase(project.getId()))
				return project;
		}
		return null;
	}
	 */

	private Project getProject(int id)
	{
		if (projects != null)
			return projects.get(id);
		return null;
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
	 * @param userID
	 * @param password
	 *            
	 * @return
	 *
	public String getUserLogins(String userID, String password, String project, String projectID) {
		LoginThread loginThread = new LoginThread(userID, password, project, projectID,  false);
		return "Login Thread Complete";
	}
	 */

	private Label statusMsg;
	private Button demoOnly;
	private Text textPassword;
	private Text textUser;
}