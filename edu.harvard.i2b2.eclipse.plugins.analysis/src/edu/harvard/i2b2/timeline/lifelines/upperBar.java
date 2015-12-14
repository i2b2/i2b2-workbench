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

public class upperBar extends Panel{
    protected int width, height;
    protected MyDate dateMax, dateMin, validDateMin, validDateMax;
    public scale aScale;

    private MyDate today;

    public upperBar(int width, int height,MyDate today) {
        this.width = width;
        this.height = height;

        dateMin = loadRecord.getMinDate();
        dateMax = loadRecord.getMaxDate();

        validDateMin = loadRecord.getMinDate();
        validDateMax = loadRecord.getMaxDate();

        aScale = new scale(width, validDateMin, validDateMax,today);

        this.today = today;
    }

    //called from talk method in slider class (called from adjust)
    public void listen(MyDate validDateMin, MyDate validDateMax){
        aScale.setScale(validDateMin, validDateMax,today);
        repaint();
    }

    @Override
	public void paint(Graphics g){
        repaint();
    }

    @Override
	public void update(Graphics g){
        Font font = new Font("Courier", Font.BOLD, 9);
        FontMetrics fontMetrics = getFontMetrics(font);

        int i = 0, strWidth;
        int tickHeight = 1;
        //int tickHeight = 3;

        g.setColor(new Color(255,255,236)); // was lightGray
        g.fillRect(0,0,width-1,height - 1);

        g.setFont(font);
        g.setColor(Color.black);
        for(i=0; i < aScale.n_ticks; i++){
           g.drawLine(aScale.theTicks[i], fontMetrics.getHeight() + 1,
                aScale.theTicks[i], tickHeight + fontMetrics.getHeight() + 1);
           if(i == 0) strWidth = 0;
           else if(i == aScale.n_ticks)
               strWidth = fontMetrics.stringWidth(aScale.theLabelString[i]);
           else strWidth = (fontMetrics.stringWidth(aScale.theLabelString[i])/2);

           g.drawString(aScale.theLabelString[i], aScale.theTicks[i] - strWidth,
               fontMetrics.getHeight());

        }
    }

}
