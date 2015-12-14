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

public class LowerBar extends Panel {

	static final int INIT = 0;
	static final int MINTHUMB = 1;
	static final int MAXTHUMB = 2;

	protected int width, height;
	protected MyDate dateMax, dateMin, validDateMin, validDateMax;
	public Scale aScale;

	private int validMin, validMax;
	private long diff;

	Font font = new Font("Courier", Font.BOLD, 12);
	FontMetrics fontMetrics = getFontMetrics(font);

	public LowerBar(int width, int height, MyDate today) {
		this.width = width;
		this.height = height;
		dateMin = LoadRecord.getMinDate();
		dateMax = LoadRecord.getMaxDate();
		validDateMin = LoadRecord.getMinDate();
		validDateMax = LoadRecord.getMaxDate();
		aScale = new Scale(width, validDateMin, validDateMax, today);

		validMin = 0;
		validMax = width;
		diff = dateMin.MinDiff(dateMax);
		talk(INIT);
	}

	public void listen(int type, int value) {
		if (type == MINTHUMB)
			validMin = value;
		else if (type == MAXTHUMB)
			validMax = value;
		talk(type);
		repaint();
	}

	public void talk(int type) {
		if (type == MINTHUMB || type == INIT) {
			if (validMin >= 5)
				validMin += 0;
			validDateMin = CoordToDate(validMin);
		} else if (type == MAXTHUMB || type == INIT) {
			validDateMax = CoordToDate(validMax);
		}
		MainPanel.upBar.listen(validDateMin, validDateMax);
		MainPanel.theTimeLinePanel.listen(validDateMin, validDateMax);
	}

	public MyDate CoordToDate(int start) {
		return (dateMin
				.DateAfterMins(Math.round((double) diff * start / width)));
	}

	@Override
	public void paint(Graphics g) {
		repaint();
	}

	@Override
	public void update(Graphics g) {
		int strWidth;
		int tickHeight = 1;

		// System.out.println("in update of lowerbar");

		g.setFont(font);
		for (int i = 0; i < aScale.n_ticks; i++) {
			if ((validMin <= aScale.theTicks[i])
					&& (aScale.theTicks[i] <= validMax))
				g.setColor(Color.black);
			else
				g.setColor(new Color(255, 255, 236)); // was gray
			g.drawLine(aScale.theTicks[i], 0, aScale.theTicks[i], tickHeight);
			if (i == 0)
				strWidth = 0;
			else if (i == aScale.n_ticks)
				strWidth = fontMetrics.stringWidth(aScale.theLabelString[i]);
			else
				strWidth = (fontMetrics.stringWidth(aScale.theLabelString[i]) / 2);
			g.drawString(aScale.theLabelString[i], aScale.theTicks[i]
					- strWidth, fontMetrics.getHeight() + 1);
		}
	}
}
