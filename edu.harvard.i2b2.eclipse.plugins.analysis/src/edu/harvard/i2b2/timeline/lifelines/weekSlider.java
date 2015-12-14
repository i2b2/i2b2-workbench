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

public class weekSlider extends slider{

    private int weekRange;

    public weekSlider(int width, int height, MyDate today){
        super(width, height,today);

        dateMin = loadRecord.getMinDate();
        dateMax = loadRecord.getMinDate();
        validDateMin = loadRecord.getMinDate();
        validDateMax = loadRecord.getMinDate();
        aScale = new scale(rangeWidth, validDateMin, validDateMax,today);

        validMin = 0; validMax = rangeWidth;
        diff = 7 * 24 * 60;

        level = 2;
        weekRange = (((int)Math.round((double)rangeWidth * 7 / 31)));
        status = "disabled";
        talk(INIT);
    }

    public void wakeup(String status, MyDate minDate){
        this.status = status;
        if(status.equals("enabled")){
            dateMin = minDate;
            dateMax = minDate.DateAfterDays(7);
            validDateMin = minDate;
            validDateMax = validDateMin.DateAfterDays(7);
            aScale.setScale(dateMin, dateMax,today);
        }
        else{
            minClipPos = 0; minClipWidth = 0; maxClipWidth = 0;
            validMin = 0; validMax = rangeWidth;
            rangeWidth = sliderWidth - 2*thumbWidth;
            rangePos = thumbWidth;
            minThumbPos = 0;
            maxThumbPos = sliderWidth - thumbWidth;
            minThumbRect.reshape(minThumbPos, 0, thumbWidth, thickness);
            maxThumbRect.reshape(maxThumbPos, 0, thumbWidth, thickness);
            rangeRect.reshape(minThumbPos + thumbWidth, 0, rangeWidth, thickness);
            minClipRect.reshape(0,0,0,0);
            maxClipRect.reshape(0,0,0,0);
            maxClipPos = sliderWidth;
            lowValue = 0; highValue = sliderWidth - 2*thumbWidth;
        }
        repaint();
    }

    @Override
	public boolean mouseDrag(Event e, int x, int y){
        if(status.equals("disabled")) return true;
        switch(currentPos){
            case MINTHUMB:
                 minOffset = x - oldMinValue;
                 if(rangeWidth < minOffset + weekRange) minOffset = rangeWidth - weekRange;
                 else if(-minOffset > minClipWidth) minOffset = -minClipWidth;
                 if(minOffset != 0){
                    rangeWidth -= minOffset;
                    minThumbPos += minOffset;
                    rangePos += minOffset;
                    minClipWidth += minOffset;
                    oldMinValue = x;
                    minThumbRect.reshape(minThumbPos, 0, thumbWidth, thickness);
                    minClipRect.reshape(minClipPos, 0, minClipWidth, thickness);
                    rangeRect.reshape(rangePos, 0, rangeWidth, thickness);
                    lowValue = minThumbPos;
                    adjust(MINTHUMB, lowValue);
                    repaint();
                 }
                 break;

            case MAXTHUMB:
                 maxOffset = oldMaxValue - x;
                 if(rangeWidth < maxOffset + weekRange) maxOffset = rangeWidth - weekRange;
                 else if(-maxOffset > maxClipWidth) maxOffset = -maxClipWidth;
                 if(maxOffset != 0){
                    rangeWidth -= maxOffset;
                    maxThumbPos -= maxOffset;
                    maxClipWidth += maxOffset;
                    maxClipPos -= maxOffset;
                    oldMaxValue = x;
                    maxThumbRect.reshape(maxThumbPos, 0, thumbWidth, thickness);
                    maxClipRect.reshape(maxClipPos, 0, maxClipWidth, thickness);
                    rangeRect.reshape(rangePos, 0, rangeWidth, thickness);
                    highValue = maxThumbPos - thumbWidth;
                       mainPanel.theMonthSlider.wakeup("enabled",validDateMin);
                    adjust(MAXTHUMB, highValue);
                    repaint();
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
                    minThumbRect.reshape(minThumbPos, 0, thumbWidth, thickness);
                    minClipRect.reshape(minClipPos, 0, minClipWidth, thickness);
                    rangeRect.reshape(rangePos, 0, rangeWidth, thickness);

                    maxThumbPos += rangeOffset;
                    maxClipWidth -= rangeOffset;
                    maxClipPos += rangeOffset;
                    oldMaxValue += rangeOffset;
                    maxThumbRect.reshape(maxThumbPos, 0, thumbWidth, thickness);
                    maxClipRect.reshape(maxClipPos, 0, maxClipWidth, thickness);
                    oldRangeValue = x;
                    rangeState = DRAG;

                    lowValue = minThumbPos;
                    adjust(MINTHUMB, lowValue);
                    highValue = maxThumbPos - thumbWidth;
                    adjust(MAXTHUMB, highValue);
                    repaint();
                 }
                 break;


            default:
                 break;
        }
        return true;
    }
}
