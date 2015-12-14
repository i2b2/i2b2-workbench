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
 * A <code>LiteDisplacedLabel</code> is a <code>LiteGroup</code>
 * containing (initially) a <code>LiteLabel</code> and a
 * <code>LiteLine</code>.  
 * <p>
 * It is meant to represent a displaced label with a line pointing at
 * the origin.
 * <p>
 * Note that other <code>Lite</code> objects can be added to the group
 * without hurting the interface, as long as the label and the line
 * are not removed.
 * 
 * @version 	0.1, 08/04/98
 * @author 	Jean-Daniel Fekete
 * @since       JDK1.1.5
 */


public class LiteDisplacedLabel extends LiteGroup {
    int item = 1;
  /**
   * Only constructor.
   * <p>
   * @param	s	Label string.
   * @param	p1	Position of the label and of a line endpoint.
   * @param	p2	Position of the other line's endpoint.
   * @param	border	0 for no border and any other value for a border.
   * @param	f	Font of the label, or <code>null</code> for
   the default.
   * @param	fg	Foreground color, used for borders and lines.
   * @param	bg	background color for the label.
   */
  public LiteDisplacedLabel(String s, Point p1, Point p2,
		     int border, Font f, Color fg, Color bg) {
    super(2);
    item = 2;
    addElement(new LiteLine(p1, p2, border, fg));
    addElement(new LiteLabel(s, p1, border, f, fg, bg));
  }

  public LiteDisplacedLabel(String s, Point p1,
		     int border, Font f, Color fg, Color bg) {
    super(1);
    addElement(new LiteLabel(s, p1, border, f, fg, bg));
  }

  /**
   * @return the <code>LiteLine</code>.
   */
  public LiteLine getLine() { return (LiteLine)elementAt(0); }
  /**
   * @return the <code>LiteLabel</code>.
   */
  public LiteLabel getLabel() { return (LiteLabel)elementAt(item-1); }
  
  public int getItem() { return item; }
};
