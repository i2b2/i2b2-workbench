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
import java.util.*;

/**
 * A <code>LiteGroup</code> is a composite <code>Lite</code> object.
 *<p>
 * <code>Lite</code> objects are stored into a <code>Vector</code>. Most of the
 * vector methods are provided directly on a <code>LiteGroup</code>:
 * <code>addElement</code>, <code>removeElement</code>,
 * <code>removeAllElements</code>, <code>size</code> and <code>isEmpty</code>.
 * 
 * @version 0.1, 08/04/98
 * @author Jean-Daniel Fekete
 * @since JDK1.1.5
 */

public class LiteGroup implements Lite {
	Vector lites;

	/**
	 * Default constructor with default-sized vector
	 */
	public LiteGroup() {
		lites = new Vector();
	}

	/**
	 * Constructor when the number of items to store is known.
	 * 
	 * @param capacity
	 *            Initial capacity of the vector.
	 */
	public LiteGroup(int capacity) {
		lites = new Vector(capacity);
	}

	/**
	 * Add a Lite into the vector.
	 * 
	 * @param l
	 *            Lite to add.
	 */
	public void addElement(Lite l) {
		lites.addElement(l);
	}

	/**
	 * Remove a lite from the vector.
	 * 
	 * @param l
	 *            Lite to remove.
	 */
	public void removeElement(Lite l) {
		lites.removeElement(l);
	}

	/**
	 * Remove all the elements from the vector.
	 */
	public void removeAllElements() {
		lites.removeAllElements();
	}

	/**
	 * Return the lite element at the given index in the vector.
	 * 
	 * @param i
	 *            Index of the lite to get.
	 * @return The <code>Lite</code> object at the given index.
	 */
	public Lite elementAt(int i) {
		return (Lite) lites.elementAt(i);
	}

	public Enumeration elements() {
		return lites.elements();
	}

	public int size() {
		return lites.size();
	}

	public boolean isEmpty() {
		return lites.isEmpty();
	}

	/**
	 * Access the vector or lites directly.
	 * 
	 * @return The vector or <code>Lite</code> objets.
	 */
	public Vector getVector() {
		return lites;
	}

	/**
	 * Compute the bounds of the <code>Lite</code> objets.
	 * 
	 * @return The bounding rectangle.
	 */
	public Rectangle getBounds() {
		if (lites.isEmpty())
			return new Rectangle();

		Enumeration e = elements();
		Lite l = (Lite) e.nextElement();
		Rectangle r = l.getBounds();
		while (e.hasMoreElements()) {
			l = (Lite) e.nextElement();
			r.add(l.getBounds());
		}
		return r;
	}

	/**
	 * Sets the position of each of the <code>Lite</code> objects to the
	 * specified Point.
	 * 
	 * @param p
	 *            The new position point.
	 */
	public void setPosition(Point p) {
		for (Enumeration e = lites.elements(); e.hasMoreElements();) {
			Lite l = (Lite) e.nextElement();
			l.setPosition(p);
		}
	}

	/**
	 * @return The position of the first <code>Lite</code> object.
	 */
	public Point getPosition() {
		if (!lites.isEmpty())
			return ((Lite) lites.firstElement()).getPosition();
		else
			return new Point();
	}

	/**
	 * Paint each <code>Lite</code> object in turn after testing if it is inside
	 * the clip of the Graphics. This is the right way to do it if the
	 * repainting time of <code>Lite</code> objects is much greater than the
	 * time required to compute its bounds.
	 * 
	 * @see excentric.LiteGroup#paintAll(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		Rectangle clip = g.getClipBounds();
		for (Enumeration e = lites.elements(); e.hasMoreElements();) {
			Lite l = (Lite) e.nextElement();
			// if (l.getBounds().intersects(clip))
			l.paint(g);
		}
	}

	/**
	 * Paint each <code>Lite</code> object .
	 * 
	 * @see excentric.LiteGroup#paint(java.awt.Graphics)
	 */
	public void paintAll(Graphics g) {
		for (Enumeration e = lites.elements(); e.hasMoreElements();) {
			Lite l = (Lite) e.nextElement();
			l.paint(g);
		}
	}

	/**
	 * Retrieve the <code>Lite</code> object visible under a point. This object
	 * should actually be searched in reverse order in the <code>Lite</code>
	 * object list.
	 */
	public Lite itemUnder(int x, int y) {
		for (int i = lites.size() - 1; i >= 0; i--) {
			Lite l = elementAt(i);
			if (l.getBounds().contains(x, y))
				return l;
		}
		return null;
	}
}
