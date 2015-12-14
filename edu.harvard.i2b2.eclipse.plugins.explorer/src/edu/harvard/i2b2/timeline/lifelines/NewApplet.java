/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *		
 */
package edu.harvard.i2b2.timeline.lifelines;

public interface NewApplet {

	// show a string on the status line
	public void showStatus(String aStatus);

	// show a www document in a new window
	public void showDocument(String theURL);

	// set the description
	public void showLabel(String theLabel);

	public void setWidthHeight(int width, int height);

}
