/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 * 		Mike Mendis
 */

package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import java.util.ArrayList;

import edu.harvard.i2b2.eclipse.UserInfoBean;

public class Roles {
	private static Roles thisInstance;

	   static {
           thisInstance = new Roles();
   }
   
   public static Roles getInstance() {
       return thisInstance;
   }

   public boolean isRoleValid(){
 
	   ArrayList<String> roles = (ArrayList<String>) UserInfoBean.getInstance().getProjectRoles();
	   for(String param :roles) {
		   // Bug 728; enable feature for role = editor only
		   // superceded by bug 944 enable feature for editor or manager
		   // 8/4/14 back to editor only...
			//   if(param.equalsIgnoreCase("manager")) 
			//	   return true;
			 //  if(param.equalsIgnoreCase("admin")) 
			//	   return true;
			   if(param.equalsIgnoreCase("editor")) 
				   return true;
	    }
	   return false;
   }
	
}
