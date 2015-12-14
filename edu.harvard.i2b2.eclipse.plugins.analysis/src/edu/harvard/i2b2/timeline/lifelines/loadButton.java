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

import java.awt.*;

public class loadButton extends Panel {

  record theApplet;
  String loadFile;

  loadButton(record theApplet) {

        this.theApplet = theApplet;

        setBackground(Color.gray);

  }

  @Override
public void update(Graphics g) {

        paint(g);

  }


  @Override
public void paint(Graphics g) {

        g.drawString("Load",3,10);

  }

  public void setLoadFile(String loadFile) {


       this.loadFile = loadFile;

  }


  public boolean mouseDown(Event e,Object arg) {

        record.theData = new loadRecord(theApplet.getCodeBase() + loadFile,"none");

        theApplet.resetTabPanel();

        theApplet.resetPicPanel();

        theApplet.resetInfoPanel();

        theApplet.setWidthHeight(400,350);


        return true;


  }



         
}
