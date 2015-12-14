package edu.harvard.i2b2.timeline.external;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

//	01/29/97	TWB	Integrated changes from Windows 

/**
 * DirectionButton component.
 * 
 * @version 1.0, Nov 26, 1996
 * @author Symantec
 */

public class DirectionButton extends ButtonBase {
	/**
	 * The point LEFT constant.
	 */
	public static final int LEFT = 0;

	/**
	 * The point RIGHT constant.
	 */
	public static final int RIGHT = 1;

	/**
	 * The point UP constant.
	 */
	public static final int UP = 2;

	/**
	 * The point DOWN constant.
	 */
	public static final int DOWN = 3;

	private int direction;
	private int left;
	private int right;
	private int top;
	private int bottom;
	private int indent;
	private Polygon poly;

	/**
	 * Constructs a Default Direction Button: LEFT
	 */
	public DirectionButton() {
		this(LEFT);
	}

	/**
	 * Constructs a Direction Button
	 * 
	 * @param direction
	 *            constant indicating direction to point button
	 */
	public DirectionButton(int d) {
		direction = d;
		left = 0;
		right = 0;
		bottom = 0;
		indent = 0;
		poly = null;
	}

	/**
	 * set the direction of the arrow after construction
	 * 
	 * @param direction
	 *            constant indicating direction to point button
	 */
	public void setDirection(int d) {
		direction = d;
	}

	public int getDirection() {
		return direction;
	}

	public void setArrowIndent(int ai) {
		indent = ai;
		invalidate();
	}

	public int getArrowIndent() {
		return indent;
	}

	/**
	 * set extra amount in pixels to shrink triangle
	 * 
	 * @param left
	 *            pixels to shrink from left side
	 * @param right
	 *            pixels to shrink from right side
	 * @param top
	 *            pixels to shrink from top
	 * @param bottom
	 *            pixels to shrink from bottom
	 */
	public void shrinkTriangle(int l, int r, int t, int b) {
		left = l;
		right = r;
		top = t;
		bottom = b;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		updatePolygon();

		if (isEnabled()) {
			g.setColor(Color.black);
		} else {
			g.setColor(Color.gray);
		}

		g.fillPolygon(poly);
	}

	@Override
	public Dimension preferredSize() {
		Dimension s;

		s = size();

		return new Dimension(Math.max(s.width, minimumSize().width), Math.max(
				s.height, minimumSize().height));
	}

	void updatePolygon() {
		Dimension s;
		int centerHorizontal;
		int centerVertical;
		int topSide;
		int bottomSide;
		int leftSide;
		int rightSide;

		s = size();
		poly = new Polygon();

		centerHorizontal = (s.width / 2) + pressedAdjustment;
		centerVertical = (s.height / 2) + pressedAdjustment;
		topSide = (top + bevel * 2) + pressedAdjustment + indent;
		bottomSide = (s.height - bottom - bevel * 2) + pressedAdjustment
				- indent;
		leftSide = (left + bevel * 2) + pressedAdjustment + indent;
		rightSide = (s.width - right - bevel * 2) + pressedAdjustment - indent;

		switch (direction) {
		// -1, -2 etc... added 12/11/96 - Andy
		case UP: {
			// if (symantec.itools.lang.OS.isMacintosh())
			// {
			// poly.addPoint(centerHorizontal-1, topSide-1);
			// poly.addPoint(leftSide-1, bottomSide-2);
			// poly.addPoint(rightSide-2, bottomSide-2);
			// }
			// else
			// {
			poly.addPoint(centerHorizontal, topSide);
			poly.addPoint(leftSide, bottomSide);
			poly.addPoint(rightSide, bottomSide);
			// }
			break;
		}

		case DOWN: {
			// if (symantec.itools.lang.OS.isMacintosh())
			// {
			// poly.addPoint(centerHorizontal-1, bottomSide);
			// poly.addPoint(leftSide-1, topSide);
			// poly.addPoint(rightSide-1, topSide);
			// }
			// else
			// {
			poly.addPoint(centerHorizontal, bottomSide);
			poly.addPoint(leftSide, topSide);
			poly.addPoint(rightSide, topSide);
			// }
			break;
		}

		case LEFT: {
			// if (symantec.itools.lang.OS.isMacintosh())
			// {
			// poly.addPoint(leftSide-2, centerVertical-1);
			// poly.addPoint(rightSide-2, topSide-1);
			// poly.addPoint(rightSide-2, bottomSide-1);
			// }
			// else
			// {
			poly.addPoint(leftSide, centerVertical);
			poly.addPoint(rightSide, topSide);
			poly.addPoint(rightSide, bottomSide);
			// }
			break;
		}

		case RIGHT: {
			// if (symantec.itools.lang.OS.isMacintosh())
			// {
			// poly.addPoint(rightSide-1, centerVertical-1);
			// poly.addPoint(leftSide, topSide-1);
			// poly.addPoint(leftSide, bottomSide-2);
			// }
			// else
			// {
			poly.addPoint(rightSide, centerVertical);
			poly.addPoint(leftSide, topSide);
			poly.addPoint(leftSide, bottomSide);
			// }
			break;
		}
		}
	}
}
