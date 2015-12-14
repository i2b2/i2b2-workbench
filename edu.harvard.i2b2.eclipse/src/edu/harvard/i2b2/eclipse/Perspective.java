/*
* Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *     Lori Phillips - initial API and implementation
 *     Mike Mendis 
 */

package edu.harvard.i2b2.eclipse;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	IPageLayout storedLayout;
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);

		layout.addStandaloneView(LoginView.ID,  false, IPageLayout.TOP, 0.05f, editorArea);
	}

	public IPageLayout getStoredLayout() {
		return storedLayout;
	}
	
	
}
