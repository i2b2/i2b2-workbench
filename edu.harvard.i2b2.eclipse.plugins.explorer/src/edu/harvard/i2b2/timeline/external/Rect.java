package edu.harvard.i2b2.timeline.external;

import java.awt.Graphics;
import java.awt.Color;

/**
 * This class forms the Rectangle shape component.
 * 
 * @see symantec.itools.awt.shape.HorizontalLine
 * @see symantec.itools.awt.shape.Square
 * @see symantec.itools.awt.shape.VerticalLine
 * @version 1.0, Nov 26, 1996
 * @author Symantec
 */

public class Rect extends Shape {
	/**
	 * Constructs a default Rectangle.
	 */
	public Rect() {
	}

	/**
	 * Paints the rectangle.
	 */
	@Override
	public void paint(Graphics g) {
		g.clipRect(0, 0, width, height);

		int w = width - 1, h = height - 1;

		switch (style) {

		case BEVEL_LINE:
		default:
			if (fill) {
				g.setColor(fillColor);
				g.fillRect(0, 0, w, h);
			} else
				g.drawRect(0, 0, w, h);
			break;

		case BEVEL_LOWERED:
			g.setColor(Color.gray);
			g.drawLine(0, h, 0, 0);
			g.drawLine(0, 0, w, 0);

			g.setColor(Color.white);
			g.drawLine(w, 0, w, h);
			g.drawLine(w, h, 0, h);

			if (fill) {
				g.setColor(fillColor);
				g.fillRect(1, 1, w - 1, h - 1);
			}
			break;

		case BEVEL_RAISED:
			g.setColor(Color.white);
			g.drawLine(0, h, 0, 0);
			g.drawLine(0, 0, w, 0);

			g.setColor(Color.gray);
			g.drawLine(w, 0, w, h);
			g.drawLine(w, h, 0, h);

			if (fill) {
				g.setColor(fillColor);
				g.fillRect(1, 1, w - 1, h - 1);
			}
			break;
		}
	}
}
