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

import edu.harvard.i2b2.explorer.ui.MainPanel;

public class YearSlider extends Slider {
	public int getBarWidth() {

		return barWidth;

	}

	private int yearRange; // minutes in year, appropriately scaled.

	public YearSlider(int width, int height, MyDate today) {
		super(width, height, today);

		dateMin = LoadRecord.getMinDate();
		dateMax = LoadRecord.getMaxDate();
		validDateMin = LoadRecord.getMinDate();
		validDateMax = LoadRecord.getMaxDate();
		aScale = new Scale(rangeWidth, validDateMin, validDateMax, today);

		validMin = 0;
		validMax = rangeWidth;
		diff = dateMin.MinDiff(dateMax);

		level = 0;
		yearRange = (((int) Math.round((double) rangeWidth * 365 * 24 * 60
				/ diff))); // minutes in year, appropriately
		// scaled
		status = "enabled";
		talk(INIT);
	}

	@Override
	public boolean mouseDrag(Event e, int x, int y) {
		if (status.equals("disabled"))
			return true;
		switch (currentPos) {
		case MINTHUMB:

			// for illustration purposes:
			/*
			 * trackerVariable = x;
			 * 
			 * theApplet.showStatus(Integer.toString(oldMinValue)+ "/" +
			 * Integer.toString(x));
			 * System.out.println(Integer.toString(oldMinValue)+ "/" +
			 * Integer.toString(x));
			 * System.out.println(Integer.toString(validDateMin.getYear()) + "/"
			 * +Integer.toString(validDateMin.getMonth() ) + "/" +
			 * Integer.toString(validDateMin.getDay()) );
			 */
			//

			minOffset = x - oldMinValue;

			// again for illustration purposes
			// if(minOffset != 1 && minOffset!= 0)
			// System.out.println("Not equal to one!");
			// System.out.println(Integer.toString(minOffset));
			// System.out.println("-----------------");
			//

			if (minThumbPos + minOffset > (maxThumbPos - thumbWidth))
				break; // 3/16/98 dan to fix overlap
			// problem on drag (see also below)

			// if(rangeWidth < minOffset + yearRange) minOffset = rangeWidth -
			// yearRange;
			/* else */
			if (-minOffset > minClipWidth)
				minOffset = -minClipWidth; // 3/16/98 dan
			// to try top allow full dragging
			if (minOffset != 0) {
				rangeWidth -= minOffset;
				minThumbPos += minOffset;
				rangePos += minOffset;
				minClipWidth += minOffset;
				oldMinValue = x;
				minThumbRect.reshape(minThumbPos, 0, thumbWidth, thickness);
				minClipRect.reshape(minClipPos, 0, minClipWidth, thickness);
				rangeRect.reshape(rangePos, 0, rangeWidth, thickness);
				// repaint(); // this had been removed...
				lowValue = minThumbPos;
				adjust(MINTHUMB, lowValue);
				if (rangeWidth == yearRange)
					MainPanel.theMonthSlider.wakeup("enabled", validDateMin);
				else if (rangeWidth > yearRange
						&& MainPanel.theMonthSlider.getStatus().equals(
								"enabled"))
					MainPanel.theMonthSlider.wakeup("disabled", null);
			}
			break;

		case MAXTHUMB:
			maxOffset = oldMaxValue - x;
			// if(rangeWidth < maxOffset + yearRange) maxOffset = rangeWidth -
			// yearRange;
			/* else */
			if (-maxOffset > maxClipWidth)
				maxOffset = -maxClipWidth; // 3/16/98 dan
			// to try to allow full dragging
			if (maxOffset != 0) {
				// if(rangeWidth-maxOffset < yearRange) break; 3/16/98 ditto
				if (maxThumbPos - maxOffset < minThumbPos + thumbWidth)
					break; // 3/16/98 dan to fix overlap bug
				rangeWidth -= maxOffset;
				maxThumbPos -= maxOffset;
				maxClipWidth += maxOffset;
				maxClipPos -= maxOffset;
				oldMaxValue = x;
				maxThumbRect.reshape(maxThumbPos, 0, thumbWidth, thickness);
				maxClipRect.reshape(maxClipPos, 0, maxClipWidth, thickness);
				rangeRect.reshape(rangePos, 0, rangeWidth, thickness);
				// repaint(); // this had been removed
				highValue = maxThumbPos - thumbWidth;
				adjust(MAXTHUMB, highValue);
				if (rangeWidth == yearRange)
					MainPanel.theMonthSlider.wakeup("enabled", validDateMin);
				else if (rangeWidth > yearRange
						&& MainPanel.theMonthSlider.getStatus().equals(
								"enabled"))
					MainPanel.theMonthSlider.wakeup("disabled", null);
			}
			break;

		case RANGEBAR:
			rangeOffset = x - oldRangeValue;
			if (rangeOffset > maxClipWidth)
				rangeOffset = maxClipWidth;
			else if (-rangeOffset > minClipWidth)
				rangeOffset = -minClipWidth;
			if (rangeOffset != 0) {
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
				if (rangeWidth == yearRange)
					MainPanel.theMonthSlider.wakeup("enabled", validDateMin);
				// repaint(); // this had been removed
			}
			break;

		default:
			break;
		}
		return true;
	}
}
