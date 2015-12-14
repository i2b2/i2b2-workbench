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

public class AlertPanel extends Panel {

	private int width, height;

	protected static Font font = new Font("TimesRoman", Font.BOLD, 12);
	protected FontMetrics fontMetrics = getFontMetrics(font);

	public AlertPanel(int width, int height) {
		this.width = width;
		this.height = height;

	}

	@Override
	public void paint(Graphics g) {

		// actual alerts

		g.setColor(Color.lightGray);
		g.draw3DRect(0, 0, width - 1, height - 1, false);
		g.draw3DRect(1, 1, width - 3, height - 3, false);

		g.setColor(Color.red);
		g.setFont(font);

		// int currH = 20;
		// g.drawString("Mild Hypoglycemia", 10, currH);
		// //tempg.drawString("penicillin allergy", 10, currH);
		// g.drawString("Diabetic retinopathy", 10, currH +
		// fontMetrics.getHeight());
		// //temp g.drawString("Smoker", 10, currH + fontMetrics.getHeight());
		// g.drawString("Hypertension", 10, currH + 2*fontMetrics.getHeight());
		// g.drawString("Former smoker", 10, currH + 3*fontMetrics.getHeight());

		// end of actual alerts

	}
}
