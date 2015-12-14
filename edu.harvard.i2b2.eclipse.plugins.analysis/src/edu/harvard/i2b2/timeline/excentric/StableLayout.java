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

/**
 * A StableLayout tries to layout a list of labels as two left-aligned
 * columns of labels where the vertical order of labels is maintained.
 *
 * @version 	0.1, 08/05/98
 * @author 	Jean-Daniel Fekete
 * @since       JDK1.1.5
 */

public
class StableLayout extends RectLayout {
  public StableLayout(int width, int height, int column) {
    super(width, height, column);
  }
  public StableLayout(int width, int height, int column, boolean sx) {
    super(width, height, column, sx);
  }

  @Override
public int do_layout() {
    int nlabel = v.size();

    if (nlabel == 0) return DONT_OVERLAP;

    if (getStableX())
      record_offsets();
    double order[] = new double[nlabel+1]; // first is copied in last
    
    if (getColumn() == 2)
        compute_order(order);
    else
        compute_order_x(order);
    sort_labels_vector(order);
    
    if (getColumn() == 2)   // Julia
        return compute_left_right(order);   
    else 
        return compute_right(order);
  }

}
