/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *     Lori Phillips - initial API and implementation
 *     Wensong Pan
 *     Mike Mendis
 */

package edu.harvard.i2b2.eclipse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.swt.*;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;

import com.sun.management.OperatingSystemMXBean;

import edu.harvard.i2b2.eclipse.login.LoginDialog;
import edu.harvard.i2b2.eclipse.login.LoginHelper;
import edu.harvard.i2b2.eclipse.login.LoginThread;
import edu.harvard.i2b2.eclipse.login.PasswordDialog;
import edu.harvard.i2b2.eclipse.login.Project;
import edu.harvard.i2b2.eclipse.login.ProjectDialog;
import edu.harvard.i2b2.eclipse.login.ReLoginDialog;
import edu.harvard.i2b2.eclipse.util.Messages;
import edu.harvard.i2b2.pm.datavo.pm.PasswordType;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IPlatformRunnable {

	private static Log log = LogFactory.getLog(Application.class.getName());
	private static boolean inScreenSaver=false;
	public static Project project = null;
	private static String user = null;
	//private static String password = null;
	private static Display display = null;
	public static Date getLastUsed() {
		return lastUsed;
	}

	private static Date lastUsed = new Date();

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */
	public Object run(Object args) throws Exception {
		display = PlatformUI.createDisplay();

		log.debug("STARTING APP"); //$NON-NLS-1$

		try {
			if(!checkMemorySetting()) {
				return IPlatformRunnable.EXIT_OK;
			}

			if(loginAction(true) == false) {
				return IPlatformRunnable.EXIT_OK;
			}

			if(UserInfoBean.getInstance().getUserPassword().equalsIgnoreCase("***")) { //$NON-NLS-1$
				System.out.println("Password: ***"); //$NON-NLS-1$
				PasswordDialog passwordDialog = new PasswordDialog(new Shell());
				passwordDialog.open();
			}

			if(UserInfoBean.getInstance().getProjects().size() == 1) {
				UserInfoBean.selectedProject(UserInfoBean.getInstance().getProjects().get(0));
			}
			else{
				ProjectDialog projectDialog =  new ProjectDialog();
				projectDialog.open(display);
				if (projectDialog.getCancelSelected())
				{
					System.exit(0);
				}
			}


			
			Listener listener = new Listener () {

				public void handleEvent (Event e) {
					
					lastUsed = new Date();
				}



			};

			/* add the screen saver listener

			Listener listener = new Listener () {

				public void handleEvent (Event e) {
					checkSessionExpired();
					checkScreenSaver();
				}



			};
			display.addFilter(SWT.Activate, listener);
*/
//			display.addFilter(SWT.Paint,listener);
			/* */
			display.addFilter(SWT.MouseMove,listener);
			display.addFilter(SWT.KeyDown, listener);
			
			
			//display.addListener(SWT.Activate, listener);

			/**********************************************************************/
			//for mac fix
			//if ( System.getProperty("os.name").toLowerCase().startsWith("mac"))
				//SWT_AWT.embeddedFrameClass = "sun.lwawt.macosx.CViewEmbeddedFrame";

			int returnCode = PlatformUI.createAndRunWorkbench(display, 
					new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IPlatformRunnable.EXIT_RESTART;
			}

			return IPlatformRunnable.EXIT_OK;
		} finally {
			display.dispose();
		}
	}



	/**
	 * Method to check the maximum heap size and the physical memory size
	 * 
	 * @return  true/false
	 * 
	 * 
	 */
	private boolean checkMemorySetting() {
		boolean status = true;

		OperatingSystemMXBean mxbean =  (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		long memory = mxbean.getTotalPhysicalMemorySize()/1000000;

		//RuntimeMXBean mxbean1 = ManagementFactory.getRuntimeMXBean();

		long vmSize = Runtime.getRuntime().maxMemory() / 1000000;//getXmxFromi2b2Properties();

		if(memory < vmSize) {
			status = false;

			Shell activeShell = new Shell();

			//java.lang.management.OperatingSystemMXBean mxbean =  ManagementFactory.getOperatingSystemMXBean();
			//System.out.println("In Application total physical memory: "+(mxbean.getTotalPhysicalMemorySize()/1000000));

			MessageBox mBox = new MessageBox(activeShell, SWT.ICON_INFORMATION | SWT.OK);
			mBox.setText(Messages.getString("Application.MemoryPopup")); //$NON-NLS-1$
			mBox.setMessage(Messages.getString("Application.MemoryPopupText1")+memory+Messages.getString("Application.MemoryPopupText2") //$NON-NLS-1$ //$NON-NLS-2$
					+Messages.getString("Application.MemoryPopupText3")+vmSize+Messages.getString("Application.MemoryPopupText4") //$NON-NLS-1$ //$NON-NLS-2$
					+Messages.getString("Application.MemoryPopupText5") //$NON-NLS-1$
					+Messages.getString("Application.MemoryPopupText6")); //$NON-NLS-1$
			mBox.open();
		}

		return status;
	}



	/**
	 * Method to read in crcnavigator properties file
	 * 
	 * @return  webservicename
	 * 
	 */
	private String getCRCNavigatorProperties() {
		Properties properties = new Properties();
		String webServiceName=""; //$NON-NLS-1$
		//String communicationProtocol="";
		try {
			String filename=Messages.getString("Application.PropertiesFile"); //$NON-NLS-1$
			properties.load(new FileInputStream(filename));
			webServiceName=properties.getProperty("applicationName"); //$NON-NLS-1$
			System.setProperty("applicationName", properties.getProperty("applicationName")); //$NON-NLS-1$ //$NON-NLS-2$
			//webServiceMethod=properties.getProperty("webservicemethod");
			//System.setProperty("webServiceMethod", webServiceMethod);
			//communicationProtocol=properties.getProperty("communicationProtocol");
			//System.setProperty("communicationProtocol", communicationProtocol);
		} catch (IOException e) {
			log.error(e.getMessage());
			webServiceName=""; //$NON-NLS-1$
		}
		log.debug("webservicename="+webServiceName); //$NON-NLS-1$
		return webServiceName;
	}

	/**
	 * this method populates controls on login/logout using UserInfoBean
	 * 
	 * @param login-
	 *            true if action is login, false if action is logout
	 * @return Boolean true if user logged in
	 * 					false if user cancelled login session
	 */
	private boolean loginAction(boolean login) {
		UserInfoBean userInfoBean = null;
		// if login action true open dialog and wait for return
		Shell activeShell = new Shell();		

		if (login) {
			getCRCNavigatorProperties();

			LoginDialog loginDialog = new LoginDialog(activeShell);	
			userInfoBean = loginDialog.open();	
			if(userInfoBean != null) {
				user = userInfoBean.getUserName();
				//password = userInfoBean.getOrigPassword();
				project = loginDialog.getCurrentPrj();
				
				UserInfoBean.setLastActivityTime(Calendar.getInstance().getTime());
			}
		}	

		// userInfoBean null means user pressed cancel- logout and close pages
		if (userInfoBean == null) {
			log.debug( " Login cancel"); //$NON-NLS-1$
			return false;
		} else {
			// login successful
			log.debug("Login Successful"); //$NON-NLS-1$
			return true;
		}
	}
}