/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.workplace.model;

import edu.harvard.i2b2.eclipse.plugins.workplace.views.TreeNode;

public class RefreshNode {
	private TreeNode refreshNode = null;
	private int level = 0;
	
	private static RefreshNode thisInstance;
	static {
		thisInstance = new RefreshNode();
	}

	public static RefreshNode getInstance() {
		return thisInstance;
	}
	
	public TreeNode getRefreshNode(){		
		return refreshNode;
	}
	public void setRefreshNode(TreeNode node){
		refreshNode = node;
	}
	
	public int getLevel(){		
		return level;
	}
	public void setLevel(int newLevel){
		level = newLevel;
	}
}
