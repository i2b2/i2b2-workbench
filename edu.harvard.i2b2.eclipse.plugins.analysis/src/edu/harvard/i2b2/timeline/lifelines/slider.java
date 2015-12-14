/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *		Shawn Murphy, MD, PH.D (MGH)
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.awt.*;

// snm - this is the lower slider
public class slider extends Panel{ // implements newApplet?

    protected int sliderWidth, sliderHeight, barWidth; // barWidth always remains the original width of the slider (+thumbs?)
    public int thumbWidth, minThumbPos, maxThumbPos, rangeWidth, rangePos, thickness; // rangeWidth is present width of
    // rangeBar
    private Image thumbImage1 = null, thumbImage2 = null;
    private Graphics thumbGraphics1 = null, thumbGraphics2 = null;
    private int arrowArraySize = 11;
    private int xArray[] = {0,0,0,0,0,0,0,0,0,0,0};
    private int yArray[] = {0,0,0,0,0,0,0,0,0,0,0};
    protected Rectangle minThumbRect, maxThumbRect, rangeRect, minClipRect, maxClipRect;
    protected int currentPos = -1, minThumbState = UP, maxThumbState = UP, rangeState = UP, minClipState = UP, maxClipState = UP;
    protected int oldMinValue, oldMaxValue, oldRangeValue, minOffset, maxOffset, rangeOffset;
    protected int minClipPos = 0, minClipWidth = 0, maxClipPos, maxClipWidth = 0;
    protected int lowValue, highValue;
    protected Color llgray = new Color(220,255,220); // was (220,220,220) then (220,220,0) color under the slider

    protected MyDate dateMax, dateMin, validDateMin, validDateMax;
    public scale aScale;

    protected int validMin, validMax;
    protected long diff;

    Font font = new Font("Courier", Font.BOLD, 9);
    FontMetrics fontMetrics = getFontMetrics(font);

    static final int INIT = 0;
    static final int MINTHUMB = 1;
    static final int MAXTHUMB = 2;
    static final int RANGEBAR = 3;
    static final int MINCLIP  = 4;
    static final int MAXCLIP  = 5;
    static final int DOWN = 0;
    static final int UP = 1;
    static final int DRAG = 2;

    static final int BUTTON = 10;

    //protected String status = "disabled";
    protected String status = "enabled";
    protected int level;

    protected MyDate today;

    //for example purposes:
    static newApplet theApplet;
    public static void setApplet(newApplet inApplet) {
        theApplet = inApplet;

    }
    protected int trackerVariable = 0;
    //

    public slider(int width, int height,MyDate today) {

        sliderWidth = width;
        sliderHeight = height;
        thumbWidth = 20;
        barWidth = width - 2*thumbWidth;
        rangeWidth = width - 2*thumbWidth;
        rangePos = thumbWidth;
        minThumbPos = 0;
        maxThumbPos = width - thumbWidth;
        thickness = height;
        minThumbRect = new Rectangle(minThumbPos, 0, thumbWidth, thickness);
        maxThumbRect = new Rectangle(maxThumbPos, 0, thumbWidth, thickness);
        rangeRect = new Rectangle(minThumbPos + thumbWidth, 0, rangeWidth, thickness);
        minClipRect = new Rectangle(0,0,0,0);
        maxClipRect = new Rectangle(0,0,0,0);
        maxClipPos = sliderWidth;
        lowValue = 0; highValue = sliderWidth - 2*thumbWidth;

        // new:
        dateMin = new MyDate(1,1,1990,0,0);
        //dateMax = new MyDate(1,1,1997);
        dateMax = new MyDate(1,1,2003,0,0); //snm0
        validDateMin = new MyDate(1,1,1990,0,0);
        //validDateMax = new MyDate(1,1,1997);
        validDateMax = new MyDate(1,1,2003,0,0); //snm0
        validMin = 0;
        validMax = sliderWidth;
        oldMinValue = 0;
        oldMaxValue = sliderWidth;

        aScale = new scale(280,dateMin,dateMax,today);

        /*if(status.equals("enabled"))
            System.out.println("enabled");*/

        this.today = new MyDate(today.getMonth(),today.getDay(),today.getYear(),today.getHour(),today.getMin());

    }

    private void drawThumbImage(boolean raised){
        if (thumbImage1 == null && raised){
            thumbImage1 = createImage(thumbWidth, thickness);
            thumbGraphics1 = thumbImage1.getGraphics();
            thumbGraphics1.setColor(Color.white); // was lightGray
            thumbGraphics1.fillRect(0,0,thumbWidth, thickness);
            drawBorderRect(thumbGraphics1, 0,0, thumbWidth, thickness, true);
            thumbGraphics1.setColor(Color.black);
            makeThumbArrays(); // should be called "arrowArrays, vertices of arrows in thumb")
            thumbGraphics1.fillPolygon(xArray, yArray, arrowArraySize);
            }
        else if(thumbImage2 == null && raised == false){
            thumbImage2 = createImage(thumbWidth, thickness);
            thumbGraphics2 = thumbImage2.getGraphics();
            thumbGraphics2.setColor(Color.lightGray);
            thumbGraphics2.fillRect(0,0,thumbWidth, thickness);
            drawBorderRect(thumbGraphics2, 0,0, thumbWidth, thickness, false);
            thumbGraphics2.setColor(Color.black);
            makeThumbArrays();
            thumbGraphics2.fillPolygon(xArray, yArray, arrowArraySize);
        }
    }

    private void drawMinThumb(Graphics g, boolean raised){
        if(raised) g.drawImage(thumbImage1, minThumbPos, 0, null);
        else g.drawImage(thumbImage2, minThumbPos, 0, null);
    }

    private void drawMaxThumb(Graphics g, boolean raised){
        if(raised) g.drawImage(thumbImage1, maxThumbPos, 0, null);
        else g.drawImage(thumbImage2, maxThumbPos, 0, null);
    }

    private void drawRange(Graphics g, boolean raised){

        drawBorderRect(g, rangePos, 0, rangeWidth, thickness, raised);
        g.setColor(new Color(255,255,236)); // was lightgray
        if(raised) g.fillRect(rangePos+2, 2, rangeWidth-2,thickness-4);
        else g.fillRect(rangePos+1, 1, rangeWidth-2,thickness-2);
    }

    private void drawMinClip(Graphics g){
        g.setColor(llgray);
        g.fillRect(minClipPos, 0, minClipWidth, thickness);
    }

    private void drawMaxClip(Graphics g){
        g.setColor(llgray);
        g.fillRect(maxClipPos, 0, maxClipWidth, thickness);
    }

    private void drawBorderRect(Graphics g, int xRect, int yRect, int wRect, int hRect, boolean raised){
        if(raised){
           g.setColor(Color.white);
           g.draw3DRect(xRect,yRect,wRect-1,hRect-1, raised);
           g.setColor(Color.lightGray);
           g.draw3DRect(xRect+1,yRect+1,wRect-3,hRect-3, raised);
        }
        else{
           g.setColor(Color.gray);
           g.drawRect(xRect,yRect,wRect-1,hRect-1);
        }
    }

    private void makeThumbArrays() {
        xArray[0] = 3;        yArray[0] = (thickness/2);
        xArray[1] = (thumbWidth/3+2);        yArray[1] = 4;
        xArray[2] = (thumbWidth/3+2);        yArray[2] = (thickness/3 + 2);
        xArray[3] = (thumbWidth*2/3-1);        yArray[3] = (thickness/3 + 2);
        xArray[4] = (thumbWidth*2/3-1);        yArray[4] = 4;
        xArray[5] = (thumbWidth-3);        yArray[5] = (thickness/2);
        xArray[6] = (thumbWidth*2/3-1);        yArray[6] = (thickness-4);
        xArray[7] = (thumbWidth*2/3-1);        yArray[7] = (thickness*2/3 - 1);
        xArray[8] = (thumbWidth/3+2);        yArray[8] = (thickness*2/3 - 1);
        xArray[9] = (thumbWidth/3+2);        yArray[9] = (thickness-4);
        xArray[10] = 3;        yArray[10] = (thickness/2);
    }

    private boolean inMinThumb(int x, int y){
        if(minThumbRect.contains(x, y)) return true;
        else return false;
    }

    private boolean inMaxThumb(int x, int y){
        if(maxThumbRect.contains(x, y)) return true;
        else return false;
    }

    private boolean inRange(int x, int y){
        if(rangeRect.contains(x, y)) return true;
        else return false;
    }

    private boolean inMinClip(int x, int y){
        if(minClipRect.contains(x, y)) return true;
        else return false;
    }

    private boolean inMaxClip(int x, int y){
        if(maxClipRect.contains(x, y)) return true;
        else return false;
    }

    @Override
	public boolean mouseDown(Event e, int x, int y){

        /*MyDate temp = new MyDate(CoordToDate(x).getMonth(),CoordToDate(x).getDay(),CoordToDate(x).getYear());

        System.out.println("the date:");
        temp.print();*/

        if(inMinThumb(x, y)){
            currentPos = MINTHUMB;
            if(minThumbState == UP){
                minThumbState = DOWN;
                oldMinValue = x;
                // temporary for illustration purposes
                /*theApplet.showStatus(Integer.toString(validDateMin.getMonth()) + "/" +Integer.toString(validDateMin.
                getDay()
                ) + "/" + Integer.toString(validDateMin.getYear()) + " | " + Integer.toString(validDateMax.getMonth())
                + "/" + Integer.toString(validDateMax.getDay()) + "/" + Integer.toString(validDateMax.getYear()) );



                //theApplet.showStatus(Integer.toString(minClipPos));
                //theApplet.showStatus(Integer.toString(trackerVariable)); */
                repaint();
            }
        }
        if(inMaxThumb(x, y)){
            currentPos = MAXTHUMB;
            if(maxThumbState == UP){
                maxThumbState = DOWN;
                oldMaxValue = x;

                // temporary for illustration purposes
                /*theApplet.showStatus(Integer.toString(validDateMin.getMonth()) + "/" +Integer.toString(validDateMin.
                getDay()
                ) + "/" + Integer.toString(validDateMin.getYear()) + " | " + Integer.toString(validDateMax.getMonth())
                + "/" + Integer.toString(validDateMax.getDay()) + "/" + Integer.toString(validDateMax.getYear()) );*/

                //theApplet.showStatus(Integer.toString(timeLinePanel.selectedIndex));



                //theApplet.showStatus(Integer.toString(minClipPos));
                //theApplet.showStatus(Integer.toString(trackerVariable));


                repaint();
            }
        }
        if(inRange(x, y)){
            currentPos = RANGEBAR;
            if(rangeState == UP){
                rangeState = DOWN;
                oldRangeValue = x;
                repaint();
            }
        }

        else if(inMinClip(x,y)){
            minClipState = DOWN;
            currentPos = MINCLIP;
        }

        else if(inMaxClip(x,y)){
            maxClipState = DOWN;
            currentPos = MAXCLIP;
        }

        return true;
    }

    @Override
	public boolean mouseUp(Event e, int x, int y){
        if(minThumbState == DOWN){
           minThumbState = UP;
           repaint();
        }
        if(maxThumbState == DOWN){
           maxThumbState = UP;
           repaint();
        }

        if(rangeState == DOWN){
           rangeState = UP;
           repaint();
           currentPos = MINTHUMB;
           mouseDrag(e, 0, y);
           currentPos = MAXTHUMB;
           mouseDrag(e, sliderWidth,y);
        }

        if(rangeState == DRAG){
            rangeState = UP;
            repaint();
        }

        if(minClipState == DOWN){
            minClipState = UP;
            currentPos = MINTHUMB;
            mouseDrag(e, x, y);
        }

        if(maxClipState == DOWN){
            maxClipState = UP;
            currentPos = MAXTHUMB;
            mouseDrag(e, x, y);
        }

        return true;
    }

    public void setRangePos(int pos){
        rangePos = pos;
    }


    @Override
	public boolean mouseDrag(Event e, int x, int y){
        switch(currentPos){
            case MINTHUMB:
                 // for illustration purposes:
                 trackerVariable = x;
                 theApplet.showStatus(Integer.toString(oldMinValue)+ "/"
                 + Integer.toString(x));
                 // nothing prints here now... using derived classes mousedrag....
                 //

                 minOffset = x - oldMinValue;
                 if(rangeWidth < minOffset) minOffset = rangeWidth;
                 else if(-minOffset > minClipWidth) minOffset = -minClipWidth;
                 if(minOffset != 0){
                    rangeWidth -= minOffset;
                    minThumbPos += minOffset;
                    rangePos += minOffset;
                    minClipWidth += minOffset;
                    oldMinValue = x;
                    minThumbRect.setBounds(minThumbPos, 0, thumbWidth, thickness);
                    minClipRect.setBounds(minClipPos, 0, minClipWidth, thickness);
                    rangeRect.setBounds(rangePos, 0, rangeWidth, thickness);
                    repaint();
                    lowValue = minThumbPos;
                    adjust(MINTHUMB, lowValue);
                 }
                 break;

            case MAXTHUMB:
                 maxOffset = oldMaxValue - x;
                 if(rangeWidth < maxOffset) maxOffset = rangeWidth;
                 else if(-maxOffset > maxClipWidth) maxOffset = -maxClipWidth;
                 if(maxOffset != 0){
                    rangeWidth -= maxOffset;
                    maxThumbPos -= maxOffset;
                    maxClipWidth += maxOffset;
                    maxClipPos -= maxOffset;
                    oldMaxValue = x;
                    maxThumbRect.setBounds(maxThumbPos, 0, thumbWidth, thickness);
                    maxClipRect.setBounds(maxClipPos, 0, maxClipWidth, thickness);
                    rangeRect.setBounds(rangePos, 0, rangeWidth, thickness);
                    repaint();
                    highValue = maxThumbPos - thumbWidth;
                    adjust(MAXTHUMB, highValue);
                 }
                 break;

            case RANGEBAR:
                 rangeOffset = x - oldRangeValue;
                 if(rangeOffset > maxClipWidth) rangeOffset = maxClipWidth;
                 else if(-rangeOffset > minClipWidth) rangeOffset = -minClipWidth;
                 if(rangeOffset != 0){
                    rangePos += rangeOffset;
                    minThumbPos += rangeOffset;
                    minClipWidth += rangeOffset;
                    oldMinValue += rangeOffset;
                    minThumbRect.setBounds(minThumbPos, 0, thumbWidth, thickness);
                    minClipRect.setBounds(minClipPos, 0, minClipWidth, thickness);
                    rangeRect.setBounds(rangePos, 0, rangeWidth, thickness);

                    maxThumbPos += rangeOffset;
                    maxClipWidth -= rangeOffset;
                    maxClipPos += rangeOffset;
                    oldMaxValue += rangeOffset;
                    maxThumbRect.setBounds(maxThumbPos, 0, thumbWidth, thickness);
                    maxClipRect.setBounds(maxClipPos, 0, maxClipWidth, thickness);
                    oldRangeValue = x;
                    rangeState = DRAG;
                    repaint();

                    lowValue = minThumbPos;
                    adjust(MINTHUMB, lowValue);
                    highValue = maxThumbPos - thumbWidth;
                    adjust(MAXTHUMB, highValue);
                 }
                 break;


            default:
                 break;
        }
        return true;
    }


    public void adjust(int type, int value){
        if(type == MINTHUMB) validMin = value;
        else if(type == MAXTHUMB) validMax = value;
        talk(type);
        repaint();
    }

    public void adjust(int type,MyDate GivenDate1,MyDate GivenDate2,int rWinOffset){
        validMin = 0;
        validMax = 0;

           MyDate Date1 = new MyDate(GivenDate1.getMonth(),GivenDate1.getDay(),GivenDate1.getYear(),GivenDate1.getHour(),GivenDate1.getMin());
           MyDate Date2 = new MyDate(GivenDate2.getMonth(),GivenDate2.getDay(),GivenDate2.getYear(),GivenDate2.getHour(),GivenDate2.getMin());

          // Date1.print();

           Date1.subtractDays(30);

          // Date1.print();

          // Date2.print();

           Date2.addDays(30);

          // Date2.print();

           Event e = new Event(this,5,"hello");

           MyDate tempDate = new MyDate(1,17,1989,0,0);

           // the source of inspiration:
           //theTicks[i] = (int)Math.round((double)DateMin.MinDiff(currDate)*width/
           //                                    DateMin.MinDiff(DateMax));

           //aScale.setScale(Date1,Date2);

           //double barWidth = (double) ( (double)barWidth/ (double) (aScale.getDateMin()).MinDiff( aScale.getDateMax() ));
          // System.out.println("barWidth: " + barWidth);

           int start = (int)Math.round((double)dateMin.MinDiff(Date1)*barWidth/
                                               dateMin.MinDiff(dateMax));  // scale.java where calculating the ticks...

           int end = (int)Math.round((double)dateMin.MinDiff(Date2)*barWidth/
                                               dateMin.MinDiff(dateMax));  // scale.java where calculating the ticks...

           end += 2*thumbWidth; // notice that in mouseup method for a click in rangewidth (to reset) the end
           //goes to sliderWidth which is width of full range + 2*thumbWidth


           int oldMinValue = (int)Math.round((double)dateMin.MinDiff(validDateMin)*barWidth/
                                               dateMin.MinDiff(dateMax));  // scale.java where calculating the ticks...

           int oldMaxValue = (int)Math.round((double)dateMin.MinDiff(validDateMax)*barWidth/
                                               dateMin.MinDiff(dateMax));  // scale.java where calculating the ticks...

           oldMaxValue += 2*thumbWidth;

           //start += rWinOffset;
           //end += rWinOffset;

           //start = start - thumbWidth; // still needed now that using rangeWidth above?

          // end = end - thumbWidth;


           //rangeState = UP;

           //oldMinValue = validDateMin; // positions. not dates.
           //oldMaxValue = validDateMax;

           //oldMinValue = rWinOffset;

           repaint();

           // from mousedrag function

           /* int x = start;

           minOffset = x - oldMinValue;

           rangeWidth -= minOffset;
           minThumbPos += minOffset;
           rangePos += minOffset;
           minClipWidth += minOffset;
           oldMinValue = x;
           minThumbRect.setBounds(minThumbPos, 0, thumbWidth, thickness);
           minClipRect.setBounds(minClipPos, 0, minClipWidth, thickness);
           rangeRect.setBounds(rangePos, 0, rangeWidth, thickness);
           repaint();
           lowValue = minThumbPos; */

           // from mousedrag function
           currentPos = MINTHUMB;
           mouseDrag(e, start, 400); // 2nd number was sliderWidth....
           currentPos = MAXTHUMB;
           //mouseDrag(e, minThumbPos + 300,400); // don't use
           mouseDrag(e,end,400);

        validMin = start;
        validMax = end;

        validDateMin = new MyDate(Date1.getMonth(),Date1.getDay(),Date1.getYear(),Date1.getHour(),Date1.getMin()); // set validMin to start instead?
        validDateMax = new MyDate(Date2.getMonth(),Date2.getDay(),Date2.getYear(),Date2.getHour(),Date2.getMin());

        /*currentPos = RANGEBAR;
            if(rangeState == UP){
                rangeState = DOWN;
                oldRangeValue = 200;
        }*/

        talk(type);
        repaint();
    }

    public void talk(int type){
        if(type == MINTHUMB || type == INIT){
            validDateMin = CoordToDate(validMin);
        }
        else if(type == MAXTHUMB || type == INIT){
            validDateMax = CoordToDate(validMax);
        }
        else if(type == BUTTON) {
            //validDateMin = CoordToDate(validMin);
            //validDateMax = CoordToDate(validMax);
        }

        mainPanel.upBar.listen(validDateMin, validDateMax);
        mainPanel.theTimeLinePanel.listen(validDateMin, validDateMax);
        if (type == INIT)
            mainPanel.theTimeLinePanel.slide= false;
    }

    public MyDate CoordToDate(int start){
        return(dateMin.DateAfterMins(Math.round((double)diff * start / barWidth)));
    }

    public String getStatus(){
        return status;
    }

    @Override
	public void paint(Graphics g){
        repaint();
    }

    @Override
	public void update(Graphics g){
        int strWidth;
        drawThumbImage(true);
        drawThumbImage(false);
        drawRange(g, true);
        if(rangeState == UP) drawRange(g, true);
        else drawRange(g, false);

        if(minThumbState == UP) drawMinThumb(g, true);
        else drawMinThumb(g, false);
        if(maxThumbState == UP) drawMaxThumb(g, true);
        else drawMaxThumb(g, false);
        drawMinClip(g);
        drawMaxClip(g);

        if(status.equals("enabled")){
        g.setFont(font);
        for(int i=0; i < aScale.n_ticks; i++){
           if((validMin < aScale.theTicks[i+1]) && (aScale.theTicks[i] < validMax))
               g.setColor(Color.black);
           else
               g.setColor(Color.gray);
           strWidth = (fontMetrics.stringWidth(aScale.theLabelString[i]));
           g.drawString(aScale.theLabelString[i],
                (aScale.theTicks[i]+aScale.theTicks[i+1])/2 + thumbWidth - strWidth/2,
                 fontMetrics.getHeight());
        }
        }
    }
}
