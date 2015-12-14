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
import java.net.*;

public class picPanel extends Panel{
    private int width, height;
    private Image image1;
    public picPanel(int width, int height,record theApplet,String pictureFile) {
        this.width = width;
        this.height = height;
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        try {
            if( pictureFile.substring(0,4).equals("http") )
            image1 = toolkit.getImage(new URL(pictureFile));
            else
            image1 = toolkit.getImage(new URL(theApplet.getCodeBase() + pictureFile));

        }
        catch (Exception e) {
		    System.out.println("an exception "+e);
		}
    }

    @Override
	public void paint(Graphics g){
        g.setColor(Color.black);
        g.drawRect(0,0,width-1,height-1);
        g.drawImage(image1, 0, 0, width-1,height-1, this);
    }
}
