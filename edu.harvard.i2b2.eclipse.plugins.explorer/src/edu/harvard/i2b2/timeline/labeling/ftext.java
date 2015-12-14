/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	
 *
 */

package edu.harvard.i2b2.timeline.labeling;

import java.awt.*;

public class ftext extends ImgFilt {

	@Override
	public int[] filter(int[] p1, int w, int h) {
		Image image;
		Graphics g;
		int ascent, descent;
		FontMetrics fontmetrics;
		int pixels[];
		boolean retval;
		int i;

		// Ignore p1, w, and h

		fontmetrics = applet.getFontMetrics(font);
		ascent = fontmetrics.getAscent();
		descent = fontmetrics.getDescent();
		new_width = fontmetrics.stringWidth(tx);
		new_height = fontmetrics.getHeight();

		image = applet.createImage(new_width, new_height);
		g = image.getGraphics();
		g.setFont(font);
		g.setColor(bg);
		g.fillRect(0, 0, new_width, new_height);

		// Now draw the main foreground text
		g.setColor(fg);
		g.drawString(tx, 0, ascent);
		drawUnderline(g, fontmetrics, 0, ascent + Math.max(1, (descent / 4)),
				new_width, fg);

		// Grab the pixels
		pixels = getPixels(image, applet);
		try {
			retval = grabPixels(0);
		} catch (InterruptedException e) {
			System.err.println("Interrupted waiting for pixels");
		}
		i = 0;
		if (transparent) {
			int newsize = new_width * new_height;

			trans_color_value = getRealBackgroundColor(bg);
			for (i = 0; i < newsize; i++) {
				if (pixels[i] == trans_color_value) {
					pixels[i] &= 0x00ffffff;
				}
			}
		}
		return pixels;
	}
}
