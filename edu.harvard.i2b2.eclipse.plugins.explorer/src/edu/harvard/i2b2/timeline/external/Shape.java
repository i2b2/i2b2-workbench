package edu.harvard.i2b2.timeline.external;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Color;

/**
 * This is the parent Shape class for the various shape components.
 * 
 * @see symantec.itools.awt.shape.Ellipse
 * @see symantec.itools.awt.shape.Rectangle
 * @version 1.0, Nov 26, 1996
 * @author Symantec
 */

public abstract class Shape extends Canvas implements BevelStyle {
	protected int width;
	protected int height;
	protected int style;
	protected boolean fill;
	protected Color fillColor;

	protected Shape() {
		style = BEVEL_LINE;
	}

	/**
	 * Sets the border style of the shape.
	 * 
	 * @see #getStyle
	 */
	public void setBevelStyle(int s) {
		style = s;
		repaint();
	}

	/**
	 * Returns the current style of the shape.
	 * 
	 * @see #setStyle
	 */
	public int getBevelStyle() {
		return style;
	}

	/**
	 * Sets the fill mode of the shape.
	 * 
	 * @see #getFillMode
	 */
	public void setFillMode(boolean f) {
		fill = f;
		repaint();
	}

	/**
	 * Returns the current fill mode of the shape.
	 * 
	 * @see #setFillMode
	 */
	public boolean getFillMode() {
		return fill;
	}

	/**
	 * Sets the fill color of the shape.
	 * 
	 * @see #getFillColor
	 */
	public void setFillColor(Color color) {
		fillColor = color;
		repaint();
	}

	/**
	 * Returns the current fill color of the shape.
	 * 
	 * @see #setFillColor
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Reshapes the shape.
	 */
	@Override
	public void reshape(int x, int y, int width, int height) {
		this.width = width;
		this.height = height;

		super.reshape(x, y, width, height);
	}

	/**
	 * Returns the current "preferred size" of the shape.
	 */
	@Override
	public Dimension preferredSize() {
		return new Dimension(50, 50);
	}
}
