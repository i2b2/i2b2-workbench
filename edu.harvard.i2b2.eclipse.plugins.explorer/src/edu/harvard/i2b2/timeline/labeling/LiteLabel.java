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

import edu.harvard.i2b2.timeline.lifelines.Record;

/**
 * A <code>LiteLabel</code> is a liteweight label with a foreground, a
 * background color and a border (one pixel or 0 pixel).
 * 
 * @version 0.1, 08/04/98
 * @author Jean-Daniel Fekete
 * @since JDK1.1.5
 * @modify Julia Li
 */
public class LiteLabel implements Lite {
	private String text;
	private Color datacolor; // extended by Julia
	private Point position;
	private Font font;
	private Color foreground;
	private Color background;
	private int border;
	private int alignment = LEFT;
	private Rectangle r;
	private int degree;
	private Rotate rotate;
	private boolean showlabel;
	private Record applet;

	/**
	 * Default font used when the font parameter is null.
	 */
	public static final Font DEFAULT_FONT = new Font("TimesRoman", Font.PLAIN,
			11);

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

	public LiteLabel(String s, Point sp, Point lp, int border, Font f,
			Color fg, Color bg, int degree, Record applet) {
		this.text = s;
		this.position = lp;
		this.border = border;
		if (f == null)
			f = DEFAULT_FONT;
		this.font = f;
		this.foreground = fg;
		this.background = bg;
		this.degree = degree;
		this.applet = applet;
	}

	public LiteLabel(String s, Point p, int border, Font f, Color fg, Color bg,
			int degree, Record applet) {
		this(s, new Point(0, 0), p, border, f, fg, bg, degree, applet);
	}

	public LiteLabel(String s, Point p, int border, Font f, Color fg, Color bg,
			int degree) {
		this(s, p, border, null, Color.black, Color.white, degree, null);
	}

	public LiteLabel(String s, Point p, int border, Font f, Color fg, Color bg) {
		this(s, p, border, f, fg, bg, 0, null);
	}

	public LiteLabel(String s, Point sp, Point lp, int border, Font f,
			Color fg, Color bg) {
		this(s, sp, lp, 0, f, fg, bg, 0, null);
	}

	/**
	 * Simple constructor for black and white label.
	 */
	public LiteLabel(String s, Point p, int border, int degree) {
		this(s, p, border, null, Color.black, Color.white, degree, null);
	}

	/**
	 * Change the position of the label.
	 * 
	 * @param p
	 *            The new position.
	 */
	public void setPosition(Point p) {
		position = new Point(p);
		invalidate();
	}

	public void showlabel(boolean show) {
		this.showlabel = show;
	}

	/**
	 * @return the position of the label.
	 */
	public Point getPosition() {
		return new Point(position);
	}

	/**
	 * @return the color of the event that the label is associated with.
	 */
	public Color getDatacolor() {
		return datacolor;
	}

	/**
	 * @return the bounds of the label. It is actually cached for faster
	 *         computation.
	 */
	public Rectangle getBounds() {
		if (r == null) {
			FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
			int width = fm.stringWidth(text) + 2 * border + 2;
			int height = 2 * border + fm.getMaxDescent() + fm.getMaxAscent();
			int x;
			switch (alignment) {
			case LEFT:
			default:
				x = position.x;
				break;
			case CENTER:
				x = position.x - width / 2;
				break;
			case RIGHT:
				x = position.x - width;
			}
			// r = new Rectangle(x, position.y - height/2, width, height);
			r = new Rectangle(x, position.y, width, height);

		}
		return new Rectangle(r);
	}

	public int getWidth() {
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
		int width = fm.stringWidth(text) + 2 * border;
		return width;
	}

	/**
	 * Paint the label.
	 */
	public void paint(Graphics g) {
		if (showlabel) {
			if (degree == 0) {
				g.setFont(font);
				FontMetrics fm = g.getFontMetrics(font);
				g.setColor(Color.black);
				g.drawString(text, position.x, position.y - fm.getMaxDescent());
				g.setColor(foreground);
			} else {
				/**
				 * convert the string to image and then rotate to the degree
				 */
				rotate = new Rotate(text, position, font, background, degree,
						applet);
				rotate.paint(g);
			}
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
