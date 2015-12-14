/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *
 */
/*
 *
 * Copyright (c) 1998 Jean-Daniel Fekete
 *
 * Permission to use, copy, modify, distribute, and sell this software and 
 * its documentation for any purpose is hereby granted without fee, provided
 * that (i) the above copyright notices and this permission notice appear in
 * all copies of the software and related documentation, and (ii) the name of
 * Jean-Daniel Fekete may not be used in any advertising or publicity
 * relating to the software without his specific, prior written
 * permission.
 * 
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY 
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, INCIDENTAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY KIND, OR ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER OR NOT ADVISED OF
 * THE POSSIBILITY OF DAMAGE, AND ON ANY THEORY OF LIABILITY, ARISING OUT
 * OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package edu.harvard.i2b2.timeline.excentric;

import java.awt.*;

/**
 * A <code>LiteRect</code> is a liteweight rectangle with a foreground, a
 * background color and a border (one pixel or 0 pixel).
 * 
 * @version 0.1, 08/04/98
 * @author Jean-Daniel Fekete
 * @since JDK1.1.5
 */

public class LiteRect extends Lite {
	protected Rectangle rect;
	protected Color foreground;
	protected Color background;
	protected int border;

	public LiteRect(Rectangle r, int border, Color fg, Color bg) {
		this.rect = new Rectangle(r);
		this.border = border;
		this.foreground = fg;
		this.background = bg;
	}

	/**
	 * setPosition modifies the origin of the rectangle.
	 */
	@Override
	public void setPosition(Point p) {
		rect.setLocation(p);
	}

	@Override
	public Point getPosition() {
		return rect.getLocation();
	}

	@Override
	public Rectangle getBounds() {
		int margin = (border) / 2;
		return new Rectangle(rect.x - margin, rect.y - margin, rect.width
				+ border, rect.height + border);
	}

	@Override
	public void paint(Graphics g) {
		if (background != null) {
			g.setColor(background);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
		if (border > 0) {
			g.setColor(foreground);
			g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
		}
	}

	public Color getForeground() {
		return foreground;
	}

	public void setForeground(Color fg) {
		foreground = fg;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color bg) {
		background = bg;
	}

	public int getBorder() {
		return border;
	}

	public void setBorder(int b) {
		border = b;
	}
}
