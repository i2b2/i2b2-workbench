/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v1.0 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 
 * 
 */

package edu.harvard.i2b2.eclipse.plugins.admin.utilities.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Font;

public class ResponseTab {
	
		private Text text;
		private static ResponseTab instance;
		
		/**
		 * The constructor
		 */
		private ResponseTab(Composite  tabFolder, Font textFont) {
			text = new Text(tabFolder, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL|SWT.WRAP);
			text.setFont(textFont);
		}
		/**
		 * Function to set the initial ResponseTab instance
		 * 
		 * @param tabFolder Composite to place tabFolder into
		 * @return  ResponseTab object
		 */
		public static void setInstance(Composite tabFolder, Font font) {
			instance = new ResponseTab(tabFolder, font);
		}

		/**
		 * Function to return the ResponseTab instance
		 * 
		 * @return  ResponseTab object
		 */
		public static ResponseTab getInstance() {
			return instance;
		}

		/**
		 * Function to return the Text widget
		 * 
		 * @return  Text object
		 */
		public Text getText(){
			return text;
		}
		
		/**
		 * Function to add text to the Text widget
		 * 
		 * @param input String to place in Text widget
		 */
		public void setText(String input){
			text.setText(input);
		}
		/**
		 * Function to clear the Text widget
		 * 
		 */
		public void clear() {
			text.setText("");
		}

}
