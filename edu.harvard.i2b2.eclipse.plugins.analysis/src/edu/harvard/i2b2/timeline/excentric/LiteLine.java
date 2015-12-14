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
 * A <code>LiteLite</code> is a liteweight line with a color and a
 * thickness (0 or 1).
 */
public class LiteLine extends Lite {
  private Point start, end;
  private Color foreground;
  private int thickness;
  
  public LiteLine(Point start, Point end, int thickness, Color fg) {
    this.start = start;
    this.end = end;
    this.thickness = thickness;
    this.foreground = fg;
  }

  @Override
public void setPosition(Point p) { start = p; }
  @Override
public Point getPosition() { return start; }
  @Override
public Rectangle getBounds() {
    int margin = (thickness+1)/2;
    Rectangle r = new Rectangle(start);
    r.add(end);
    r.grow(margin, margin);
    return r;
  }
  public int getThickness() { return thickness; }
  public void setThickness(int t) { thickness = t; }
  public Color getColor() { return foreground; }
  public void setColor(Color c) { foreground = c; }
  
  @Override
public void paint(Graphics g) {
    if (thickness > 0) {
      g.setColor(foreground);
      g.drawLine(start.x, start.y, end.x, end.y);
    }
  }
}

