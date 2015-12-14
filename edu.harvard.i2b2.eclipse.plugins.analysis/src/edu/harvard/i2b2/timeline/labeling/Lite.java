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

/**
 * A <code>Lite</code> is a very liteweight graphic object. It only has to
 * implement <code>setPosition</code>, <code>getPosition</code>,
 * <code>getBounds</code> and <code>paint</code>.
 * 
 * @version 0.1, 08/04/98
 * @author Jean-Daniel Fekete
 * @since JDK1.1.5
 */

public interface Lite {
	/**
	 * Sets the position of the <code>Lite</code> object.
	 * 
	 * @param p
	 *            The position Point.
	 */
	public void setPosition(Point p);

	/**
	 * Get the position of the <code>Lite</code> object.
	 * 
	 * @return The position Point.
	 */
	public Point getPosition();

	/**
	 * Paint the <code>Lite</code> object on a <code>Graphics</code>.
	 * 
	 * @param g
	 *            The Graphics object.
	 */
	public void paint(Graphics g);

	public abstract Rectangle getBounds();

}
