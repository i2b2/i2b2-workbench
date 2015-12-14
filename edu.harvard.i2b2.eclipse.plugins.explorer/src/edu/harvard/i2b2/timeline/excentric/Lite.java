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
/*
 * 
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
 * A <code>Lite</code> is a very liteweight graphic object. It only has to
 * implement <code>setPosition</code>, <code>getPosition</code>,
 * <code>getBounds</code> and <code>paint</code>.
 * 
 * @version 0.1, 08/04/98
 * @author Jean-Daniel Fekete
 * @since JDK1.1.5
 */

public abstract class Lite implements Cloneable {
	/**
	 * Sets the position of the <code>Lite</code> object.
	 * 
	 * @param p
	 *            The position Point.
	 */
	public abstract void setPosition(Point p);

	/**
	 * Get the position of the <code>Lite</code> object.
	 * 
	 * @return The position Point.
	 */
	public abstract Point getPosition();

	public abstract Rectangle getBounds();

	/**
	 * Test whether a rectangle intersects this Lite.
	 * 
	 * @param r
	 *            The rectangle.
	 * @return true if the rectangle intersects the Lite.
	 */
	public boolean intersects(Rectangle r) {
		return r.intersects(getBounds());
	}

	/**
	 * Paint the <code>Lite</code> object on a <code>Graphics</code>.
	 * 
	 * @param g
	 *            The Graphics object.
	 */
	public abstract void paint(Graphics g);

	/**
	 * Copy myself
	 */
	public Lite copy() {
		return (Lite) clone();
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	public boolean isInside(Rectangle r) {
		Rectangle b = getBounds();
		return ((r.x <= b.x) && ((r.x + r.width) >= (b.x + b.width))
				&& (r.y <= b.y) && ((r.y + r.height) >= (b.x + b.height)));
	}
}
