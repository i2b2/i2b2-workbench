/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.views; 


import edu.harvard.i2b2.eclipse.plugins.workplace.util.StringUtil;
import edu.harvard.i2b2.wkplclient.datavo.wdo.FolderType;

public class TreeData extends FolderType
{
	private String tableCd;
	
	//private boolean protected_access = false; ////put it here for testing, remove when it is added in folder type.
  	//public boolean protected_access() {
  		//return protected_access;
  	//}
  	//public void protected_access(boolean b) {
  		//protected_access = b;
  	//}
  	
  	public TreeData(String index, String name, String visualAttributes)
  	{
  		this.index = index;
  		this.name = name;
  		this.visualAttributes = visualAttributes;
  	}
  	
  	
  	public TreeData(String tableCd, String index, String name, String visualAttributes, String tooltip)
  	{
  		this.tableCd = tableCd;
  		this.index = index;
//  		this.hierarchy = hierarchy;
  		this.name = name;
  		this.visualAttributes = visualAttributes;
  		this.tooltip = tooltip;
  	}
  	
  	public TreeData() {}
  	  	
 	public TreeData(FolderType folder)
  	{ 		
 	//	this.hierarchy = folder.getHierarchy();
 		this.index = StringUtil.getIndex(folder.getIndex());
 		this.tableCd = StringUtil.getTableCd(folder.getIndex());
 		this.name = folder.getName();
 		this.visualAttributes = folder.getVisualAttributes().trim();
 //		this.hlevel = folder.getHlevel();
 		this.tooltip = folder.getTooltip();		
 		this.shareId = folder.getShareId();
 		this.workXml = folder.getWorkXml();
 		this.workXmlI2B2Type = folder.getWorkXmlI2B2Type();
 		this.parentIndex = folder.getParentIndex();
 		this.groupId = folder.getGroupId();
 		this.userId = folder.getUserId();
 		this.protectedAccess = folder.getProtectedAccess();
  	}  	

	public String getTableCd() {
		return tableCd;
	}
	public void setTableCd(String tableCd) {
		this.tableCd = tableCd;
	}	

}