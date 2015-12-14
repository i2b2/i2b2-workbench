/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *     Mike Mendis - initial API and implementation
 */

package edu.harvard.i2b2.eclipse;

//import java.util.Hashtable;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.eclipse.util.Messages;
import edu.harvard.i2b2.pm.datavo.pm.PasswordType;
import edu.harvard.i2b2.pm.datavo.pm.CellDataType;
import edu.harvard.i2b2.pm.datavo.pm.CellDatasType;
import edu.harvard.i2b2.pm.datavo.pm.ParamType;
import edu.harvard.i2b2.pm.datavo.pm.ProjectType;

/**
 * class to store user details from web service
 * @author Michael Mendis
 *
 */
public class UserInfoBean {
	private static Log log = LogFactory.getLog(UserInfoBean.class.getName());


	private static UserInfoBean instance = null;

	private static String userName;
	private static PasswordType userPassword;
	
	private static int iDEFAULT_TIMEOUTINMILLISECONDS = 1800000;

	private static String userFullName;
	private static String userDomain;

	private static String environment;

	private static String helpURL;
	private static String key = null;

	private static Date lastActivityTime;

	private static String selectedProjectUrl;
	private static String userKey;



	//private static String origPassword;
	private static Date screenSaverTimer;

	private static Timer reauthenticationTimer;

	private static TimerTask reauthenticateTask;
	
	private static String pmResponse;
	public static void pmResponse(String str) {pmResponse = new String(str);}
	public static String pmResponse() {return pmResponse;}

	private static ProjectType selectedProject;
	public static void selectedProject(ProjectType p) {selectedProject=p;}
	public static ProjectType selectedProject() {return selectedProject;}

	public static String selectedProjectID() {return selectedProject.getId();}

	private static List<String> projectList;
	private static List<String> cellList;
	private static List<ProjectType> projects;

	private static CellDatasType cellDatas;

	private static List<ParamType> globals;
	
	private static boolean isAdmin;
	public boolean isAdmin() {return isAdmin;}
	public void isAdmin(boolean b) {UserInfoBean.isAdmin = b;} 

	public static UserInfoBean getInstance() {
		if (instance == null)
			instance = new UserInfoBean();
		return instance;
	}

	public String getProjectId() {
		return selectedProject.getId();
	}
	
	public String getProjectName(){
		return selectedProject.getName();
	}

	public String getUserName() {
		return userName;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		UserInfoBean.userKey = userKey;
	}

	public static String getSelectedProjectUrl() {
		return selectedProjectUrl;
	}

	public static void setSelectedProjectUrl(String selectedProjectUrl) {
		UserInfoBean.selectedProjectUrl = selectedProjectUrl;
	}
	public void setUserName(String userName) {
		UserInfoBean.userName = userName;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		UserInfoBean.key = key;
	}

	public CellDataType getCellData(String id) {
		if (cellDatas == null)
			return null;

		for (CellDataType cellData :cellDatas.getCellData())
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals(selectedProject.getPath()))
				return cellData;
		}

		for (CellDataType cellData :cellDatas.getCellData() )
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals("/"))
				return cellData;
		}
		return null;
	}

	public List<String> getCellList()
	{
		//if (cellList == null)
		//	cellList = new ArrayList<String>();
		return cellList; 
	}

	public String getCellDataSpecial(String id) {
		if (cellDatas == null)
			return null;

		for (CellDataType cellData :cellDatas.getCellData())
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals(selectedProject.getPath()))
				return cellData.getSpecial();
		}

		for (CellDataType cellData :cellDatas.getCellData())
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals("/"))
				return cellData.getSpecial();
		}
		return null;
	}

	public String getSelectedProjectParam(String name){
		List<ParamType> params = selectedProject.getParam();
		for(int i=0; i<params.size(); i++) {
			ParamType param = params.get(i);
			if (param.getName().toLowerCase().equals(name.toLowerCase()))
				return param.getValue();
		}
		return null;
	}
	public String getCellDataParam(String id, String name) {
		if (cellDatas == null)
			return null;
		for (CellDataType cellData :cellDatas.getCellData())
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals(selectedProject.getPath()))
			{
				for (ParamType param :cellData.getParam())
				{
					if (param.getName().toLowerCase().equals(name.toLowerCase()))
						return param.getValue();
				}
			}

		for (CellDataType cellData :cellDatas.getCellData())
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals("/"))
			{
				for (ParamType param :cellData.getParam())
				{
					if (param.getName().toLowerCase().equals(name.toLowerCase()))
						return param.getValue();
				}
			}

		
		return null;
	}

	public String getCellName(String id) {
		if (cellDatas == null)
			return null;
		for (CellDataType cellData :cellDatas.getCellData())
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals(selectedProject.getPath()))
				return cellData.getName();
		}

		
		for (CellDataType cellData :cellDatas.getCellData())
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals("/"))
				return cellData.getName();
		}

		return null;
	}

	public boolean isCoreCell(String id)
	{
		if ((id.equalsIgnoreCase("ONT")) ||
				(id.equalsIgnoreCase("FR")) ||
				(id.equalsIgnoreCase("CRC")) ||
				(id.equalsIgnoreCase("WORK")) )
			return true;
		else
			return false;
	}
	public String getCellDataUrl(String id) {
		if (cellDatas == null)
			return null;
		for (CellDataType cellData :cellDatas.getCellData())
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals(selectedProject.getPath()))
				return cellData.getUrl();
		}
	
		for (CellDataType cellData :cellDatas.getCellData())
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals("/"))
				return cellData.getUrl();
		}

		
		return null;

	}

	public String getCellDataMethod(String id) {
		if (cellDatas == null)
			return null;
		for (CellDataType cellData :cellDatas.getCellData())
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals(selectedProject.getPath()))
				return cellData.getMethod();
		}

		for (CellDataType cellData :cellDatas.getCellData())
		{
			if (cellData.getId().toLowerCase().equals(id.toLowerCase()) && cellData.getProjectPath().equals("/"))
				return cellData.getMethod();
		}

		
		return null;
	}

	public void setCellDatas(CellDatasType cellDatas) {
		cellList = new ArrayList<String>();
		for (CellDataType cellData :cellDatas.getCellData())
		{
			cellList.add(cellData.getId());
		}

		UserInfoBean.cellDatas = cellDatas;
	}

	public List<String> getProjectRoles(String project) {
		for (ProjectType param :projects)
			if (param.getId().toLowerCase().equals(project.toLowerCase()))
				return param.getRole();
		return null;

	}

	public boolean isRoleInProject(String project)
	{
		if (
				selectedProject().getRole().contains(project.toUpperCase())
		)
			return true;
		else
			return false;
	}
	public List<String> getProjectRoles() {
		return selectedProject().getRole();
	}

	public List<String> getProjectList() {
		if (projectList == null) {
			projectList = new ArrayList<String>();
		}
		return projectList;
	}


	// these get and set routines work to manage the password, token, and timeout of both
	
	public PasswordType getUserPasswordType() {
		return userPassword;
	}

	public String getUserPassword() {
		return userPassword.getValue();
	}
	
	public int getUserPasswordTimeout() {
		if (userPassword.getTokenMsTimeout() == null) {
			userPassword.setTokenMsTimeout(getWorkbenchTimeoutInMiliseconds());//1800000);
		}
		
		return userPassword.getTokenMsTimeout();
	}
	
	public void setUserPasswordTimeout(int iTimeoutInMilliseconds) {
		userPassword.setTokenMsTimeout(iTimeoutInMilliseconds);
	}
	
	public boolean getUserPasswordIsToken() {
		return userPassword.isIsToken();
	}

	public void setUserPassword(String userPassword) {
		if (UserInfoBean.userPassword == null)
		{
			PasswordType ptype = new PasswordType();
			ptype.setIsToken(false);
			ptype.setValue(userPassword);
			UserInfoBean.userPassword = ptype;	
		}
		else {
			UserInfoBean.userPassword.setValue(userPassword);
		}
	}
	
	public void setUserPassword(PasswordType userPassword) {
		UserInfoBean.userPassword = userPassword;
	}
	
	/*
	public  String getOrigPassword() {
		return origPassword;
	}
	public  void setOrigPassword(String origPassword) {
		UserInfoBean.origPassword = origPassword;
	}
	*/

	public String getHelpURL() {
		return helpURL;
	}

	public void setHelpURL(String helpURL) {
		UserInfoBean.helpURL = helpURL;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		UserInfoBean.environment = environment;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		UserInfoBean.userFullName = userFullName;
	}


	/**
	 * constructor
	 */
	//public UserInfoBean() {

	//}

	public List<ProjectType> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectType> pType) {
		projectList = new ArrayList<String>();
		for (ProjectType project :pType)
			projectList.add(project.getId());

		UserInfoBean.projects = pType;
	}

	public String getGlobals(String name) {
		for (ParamType param :globals)
		{
			if (param.getName().toLowerCase().equals(name.toLowerCase()))
				return param.getValue();
		}
		return null;
	}

	public void setGlobals(String name, String value) {
		if (globals == null)
			globals = new ArrayList<ParamType>();

		ParamType pt = new ParamType();
		pt.setName(name);
		pt.setValue(value);

		globals.add(pt);
	}

	public String getUserDomain() {
		return userDomain;
	}

	public void setUserDomain(String userDomain) {
		UserInfoBean.userDomain = userDomain;
	}
	public static Timer getReauthenticationTimer() {

		return reauthenticationTimer;

	}

	public static void setReauthenticationTimer(Timer reauthenticationTimer) {

		UserInfoBean.reauthenticationTimer = reauthenticationTimer;

	}

	public static Date getLastActivityTime() {

		return lastActivityTime;

	}

	public static void setLastActivityTime(Date lastActivityTime) {

		UserInfoBean.lastActivityTime = lastActivityTime;

	}

	public static Date getScreenSaverTimer() {

		return screenSaverTimer;

	}

	public static void setScreenSaverTimer(Date screenSaverTimer) {

		UserInfoBean.screenSaverTimer = screenSaverTimer;

	}

	public static TimerTask getReauthenticateTask() {

		return reauthenticateTask;

	}
	
	public static boolean validateKey(String key)
	{
		if(key.length() != 16){
			log.error("Key should be 16 characters long");
			return false;
		}
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(key.getBytes());
				String a = selectedProject().getKey();
				String b = toHex(md5.digest()).substring(0, 3);
				if (a == null || a.length() == 0 || a.equals(b))
					return true;
				else
					return false;
					
			} catch (NoSuchAlgorithmException e) {
				log.error("NoSuchAlgorithm MD5!", e);    
			}
			return false;
	}
	
	private static  String toHex(byte[] digest) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			buf.append(Integer.toHexString((int) digest[i] & 0x00FF));
		}
		return buf.toString();
	}

	public static void setReauthenticateTask(TimerTask reauthenticateTask) {

		UserInfoBean.reauthenticateTask = reauthenticateTask;

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

}
