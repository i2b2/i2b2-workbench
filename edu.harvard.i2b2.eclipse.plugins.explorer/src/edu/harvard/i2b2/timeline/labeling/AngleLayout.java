/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *
 */

package edu.harvard.i2b2.timeline.labeling;

import java.awt.*;
import java.util.Vector;

/**
 * AngleLayout is used to layout a list of labels with angles
 */

public class AngleLayout {
	/**
	 * Return value of <code>layout</code> when labels still overlap.
	 */
	public static final int OVERLAP = 1;
	/**
	 * Return value of <code>layout</code> when labels don't overlap.
	 */
	public static final int DONT_OVERLAP = 0;
	/**
	 * Return value of <code>layout</code> when labels may overlap.
	 */
	public static final int DONT_KNOW = -1;
	/**
	 * Polygon point number
	 */
	public static final int NPOINT = 4;

	Vector v;
	int degree;
	int width;
	int height;
	int fh;

	public AngleLayout(int fh) {
		this.fh = fh;
	}

	public int layout(Vector v, int degree) {
		this.v = v;
		this.degree = degree;
		return do_layout();
	}

	public int do_layout() {
		// this time, do not check whether it is overlapping or not
		Point lastP, thisP;
		double lastAngle, thisAngle;
		int lastX;

		lastP = new Point(0, 0);
		lastAngle = 0;
		lastX = 0;
		for (int i = 0; i < v.size(); i++) {
			LiteLabel l = (LiteLabel) v.elementAt(i);
			if (degree != 0) {
				thisP = rotate(l.getPosition(), degree);
				thisAngle = angle(lastP, thisP);
				if (lastAngle <= thisAngle) {
					// not overlap
					l.showlabel(true);
					lastP = thisP;
					lastAngle = thisAngle;
				} else
					l.showlabel(false);
			} else {
				if (l.getPosition().x >= lastX) {
					l.showlabel(true);
					lastX = l.getPosition().x + l.getWidth();
				} else
					l.showlabel(false);
			}
		}

		return DONT_OVERLAP;
	}

	/**
	 * Calculate the point that is rotated by a degree
	 */
	public Point rotate(Point point, int degree) {
		Point newPoint;
		int newX, newY, x, y;
		double sine, cosine, radians;

		radians = Math.PI * degree / 180.0;
		sine = Math.sin(radians);
		cosine = Math.cos(radians);
		x = point.x;
		y = point.y;
		newX = (int) Math.round(x - fh * sine);
		newY = (int) Math.round(y - fh + fh * cosine);
		newPoint = new Point(newX, newY);
		return newPoint;
	}

	public double angle(Point p1, Point p2) {
		int distanceX, distanceY;
		double angle;

		distanceX = p2.x - p1.x;
		distanceY = p2.y - p1.y;
		if (distanceY == 0)
			angle = 0;
		else
			angle = ((double) distanceY) / ((double) distanceX);
		return angle;
	}

	/**
	 * Set the width and height of the Component for layout.
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setSize(Dimension d) {
		this.width = d.width;
		this.height = d.height;
	}

	Dimension getSize() {
		return new Dimension(width, height);
	}

	Vector getVector() {
		return v;
	}

}