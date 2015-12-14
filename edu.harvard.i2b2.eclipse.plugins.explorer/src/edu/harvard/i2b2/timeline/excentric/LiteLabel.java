/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
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
 * A <code>LiteLabel</code> is a liteweight label with a foreground, a
 * background color and a border (one pixel or 0 pixel).
 * 
 * @version 0.1, 08/04/98
 * @author Jean-Daniel Fekete
 * @since JDK1.1.5
 */

public class LiteLabel extends Lite {
	private String text;

	private Point position;

	private Font font;

	private Color foreground;

	private Color background;

	private int border;

	private int alignment = CENTER;

	private Rectangle r;

	/**
	 * Default font used when the font parameter is null.
	 */
	public static final Font DEFAULT_FONT = new Font("Helvetica", Font.PLAIN, 9);

	/**
	 * Indicates that the label should be left justified.
	 */
	public static final int LEFT = 0;

	/**
	 * Indicates that the label should be centered.
	 */
	public static final int CENTER = 1;

	/**
	 * Indicates that the label should be right justified.
	 */
	public static final int RIGHT = 2;

	/**
	 * Constructor of a <code>LiteLabel</code>.
	 * 
	 * @param s
	 *            Text of the label.
	 * @param p
	 *            Position of the label (the label is center around by default)
	 * @param border
	 *            0 if no border is required, 1 otherwise.
	 * @param fg
	 *            The color of the border. Note that the text is always painted
	 *            in black for the moment (this is arguable).
	 * @param bg
	 *            The color of the background, or <code>null</code> is no color
	 *            is required.
	 */
	public LiteLabel(String s, Point p, int border, Font f, Color fg, Color bg) {
		this.text = s;
		this.position = p;
		this.border = border;
		if (f == null) {
			f = DEFAULT_FONT;
		}
		this.font = f;
		this.foreground = fg;
		this.background = bg;
	}

	/**
	 * Simple constructor for black and white label.
	 */
	public LiteLabel(String s, Point p, int border) {
		this(s, p, border, null, Color.black, Color.white);
	}

	/**
	 * Change the position of the label.
	 * 
	 * @param p
	 *            The new position.
	 */
	@Override
	public void setPosition(Point p) {
		position = new Point(p);
		invalidate();
	}

	/**
	 * @return the position of the label.
	 */
	@Override
	public Point getPosition() {
		return new Point(position);
	}

	/**
	 * @return the bounds of the label. It is actually cached for faster
	 *         computation.
	 */
	@Override
	public Rectangle getBounds() {
		if (r == null) {
			FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
			int width = fm.stringWidth(text) + 2 * border + 2;
			int height = 2 * border + fm.getMaxDescent() + fm.getMaxAscent();
			int x;
			switch (alignment) {
			case LEFT:
			default: {
				x = position.x;
				break;
			}
			case CENTER: {
				x = position.x - width / 2;
				if (x < 0) {
					x = 1;
				}
				break;
			}
			case RIGHT: {
				x = position.x - width - 10;
				if (x < 0) {
					x = 1;
				}
				break;
			}
			}

			r = new Rectangle(x, position.y - height / 2, width, height);
		}
		return r;
	}

	/**
	 * Paint the label.
	 */
	@Override
	public void paint(Graphics g) {
		Rectangle r = getBounds();
		g.setFont(font);
		if (background != null) {
			g.setColor(background);
			g.fillRect(r.x, r.y, r.width, r.height);
		}
		// g.setColor(Color.black); Julia: change this to reflect the border
		// color
		g.setColor(foreground);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, r.x + border + 1, r.y + fm.getMaxAscent() + border
				+ 1);
		g.setColor(foreground);
		if (border > 0) {
			g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
		}
	}

	/**
	 * Invalide the cache of the label bounding box.
	 */
	public void invalidate() {
		r = null;
	}

	/**
	 * @return the text associated with this label.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Modifies the text associated with this label.
	 * 
	 * @param s
	 *            New text of the label.
	 */
	public void setText(String s) {
		invalidate();
		text = s;
	}

	/**
	 * @return the font of this label.
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @return the background color of this label.
	 */
	public Color getBackground() {
		return background;
	}

	/**
	 * @return the foreground color of this label.
	 */
	public Color getForeground() {
		return foreground;
	}

	/**
	 * @return the alignment of this label, which can be
	 *         <code>LiteLabel.LEFT</code>, <code>LiteLabel.CENTER</code> and
	 *         <code>LiteLabel.RIGHT</code>. public int getAlignment() { return
	 *         alignment; }
	 * 
	 *         /** Modifies the alignment of this label.
	 * 
	 * @param a
	 *            New alignment value, which can be <code>LiteLabel.LEFT</code>,
	 *            <code>LiteLabel.CENTER</code> and <code>LiteLabel.RIGHT</code>
	 *            .
	 */
	public void setAlignment(int a) {
		invalidate();
		alignment = a;
	}
}
