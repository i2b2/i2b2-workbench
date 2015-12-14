/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 */

package edu.harvard.i2b2.eclipse.login;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.pm.datavo.pm.PasswordType;

//import edu.harvard.i2b2.common.pm.UserInfoBean;

/**
 *  Moved from the LoginDialog class.
 *
 */
public class LoginThread extends Thread {

	private String userID;
	private String project;
	private PasswordType password;
	private String projectID;
	private String userName;
	public String getUserName() {return userName;}
	public String getProject() {return project;}
	private String msg = "";
	private UserInfoBean userInfoBean;
	public UserInfoBean getUserBean() {return userInfoBean;}	
	
	private boolean isDemo;

	/**
	 * Constructor
	 * 
	 * @param userID
	 * @param password
	 */
	public LoginThread(String userID, PasswordType password, String project, String projectID, boolean isDemo) {
		this.userID = userID;
		this.password = password;
		this.isDemo = isDemo;
		this.projectID = projectID;
		this.project = project;
	}

	@Override
	public void run() {
		// call LoginDhelper here returns userInfoBean
		
		LoginHelper loginHelper = new LoginHelper();

		try {
		if(isDemo) {
			userInfoBean = loginHelper.getUserInfo(userID, password, project, projectID, true);
		}
		else {
			userInfoBean = loginHelper.getUserInfo(userID, password, project, projectID, false);
		}
		} catch (Exception e)
		{
			//e.printStackTrace();
		}
		setMsg(loginHelper.getMsg());
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}

