/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *  
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.awt.*;

public class enterField extends TextField {

    private record theApplet;

    public void setApplet(record theApplet) {

	this.theApplet = theApplet;

    }

    @Override
    public void update(Graphics g) {

	paint(g);

    }

    @Override
    public boolean keyUp(Event evt, int key) {

	if (key == 10) {

	    record.theData = new loadRecord(
		    theApplet.getCodeBase() + getText(), "none");

	    theApplet.resetTabPanel();

	    theApplet.resetPicPanel();

	    theApplet.resetInfoPanel();

	    theApplet.setWidthHeight(400, 350); /*
						 * else if(key > 32 && key <
						 * 126) { setText(char(key) +
						 * getText()); }
						 */

	    // 3/28/98
	    if (theApplet.theCurrPanel.ctrlpanel != null) {
		theApplet.theCurrPanel.ctrlpanel.hide();
		theApplet.theCurrPanel.ctrlpanel = null;
	    }
	}
	return true;

    }

}
