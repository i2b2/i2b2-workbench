/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
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

import java.awt.Point;
import java.awt.Dimension;
import java.util.Vector;

/**
 * LabelLayout is an abstract class used to layout a list of
 * <code>LiteDisplacedLabel</code>s around a center and at a given
 * radius.
 * <p>
 * Only the method <code>layout</code> is abstract and used to layout
 * the list. 
 *<p>
 * The interpretation of the center and the radius is left to the
 * method itself.  Several strategies can be used, such as placing the
 * labels around a circle, on the edges of a rectangle, or even
 * changing the radius if required to fit all the labels.
 */
public
abstract class LabelLayout {
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

  public static final int LABEL_RADIUS = 5;
  public static final int LABEL_RADIUS2 = LABEL_RADIUS * LABEL_RADIUS;

  Vector v;
  Point center;
  int radius;
  int width;
  int height;

  public LabelLayout(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public int layout(Vector v, Point center, int radius) {
    this.v = v;
    this.center = center;
    this.radius = radius;
    return do_layout();
  }
  public abstract int do_layout();

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

  Dimension getSize() { return new Dimension(width, height); }

  int getRadius() { return radius; }
  Point getCenter() { return center; }
  Vector getVector() { return v; }

  /**
   * Sort the vector of labels according to the array of values (e.g. angles)
   * computed from the initial labels positions.
   * The vector and the angles are sorted in ascending order of angles
   * values.
   * @param	v	Vector of <code>LiteDisplacedLabel</code>.
   * @param	center	Center point of the circle.
   */
  public void sort_labels_vector(double[] order) {
    int nlabel = v.size();
    int i;

    // We use a displaced sort.  We put the indexes in one table, the
    // ordered values in another and sort the order while moving the indexes
    // at the same time.  At the end, we have a sorted table of values
    // and the table of permutations that we apply to the list of
    // labels.
    int []indexes = new int[nlabel];
    for (i = 0; i < nlabel; i++)
      indexes[i] = i;

    quicksort(indexes, order);
    Vector tmp = (Vector)v.clone();
    for (i = 0; i < nlabel; i++) {
      //System.out.println("order of "+i+"="+order[i]);      
      v.setElementAt(tmp.elementAt(indexes[i]), i);
    }
  }
  /**
   * Compute the angles of each label in the list and store it in a
   * pre-allocated array.
   * @param	v	The vector of <code>LiteDisplacedLabel</code>
   * @param	center	The center of the circle.
   * @param	angles	The array of angles filled by this method.
   */
  public void compute_angles(double angles[]) {
    int nlabel = v.size();

    for (int i = 0; i < nlabel; i++) {
      LiteDisplacedLabel l1 = (LiteDisplacedLabel)v.elementAt(i);
      Point p = l1.getPosition();
      if (p.x == center.x && p.y == center.y)
	    angles[i] = 0;
      else
	    angles[i] = Math.atan2(p.x - center.x, p.y - center.y);
    }
  }
  /**
   * convert fast approximation of arc tangent result to radians.
   */
  public static final double FAST_TO_RAD = 2*Math.PI / 8.0;

  /**
   * Compute an approximation of the angles of each label in the list
   * and store it in a pre-allocated array.  The approximation is much
   * faster to compute than the real angle.
   * @param	v	The vector of <code>LiteDisplacedLabel</code>
   * @param	center	The center of the circle.
   * @param	angles	The array of angles filled by this method.
   */
  public void compute_fast_angles(double angles[]) {
    int nlabel = v.size();

    for (int i = 0; i < nlabel; i++) {
      LiteDisplacedLabel l1 = (LiteDisplacedLabel)v.elementAt(i);
      Point p = l1.getPosition();
      if (p.x == center.x && p.y == center.y)
	angles[i] = 0;
      else
	angles[i] = fast_atan2(p.x - center.x, p.y - center.y) * FAST_TO_RAD;
    }
  }
  /**
   * Compute an approximation of atan2, in the range[0,8].
   * Based on Graphics Gems II, p. 390, Academic Press, 1991.
   * @param	x	The X axis in center coordinate .
   * @param	y	The Y axis in center coordinate.
   * @return	The fast approximation in the range [0,8].
   */
  public static double fast_atan2(double x, double y) {
    if (x >= 0) {
      if (y >= 0) {
	if (x > y)
	  return y/x;
	else
	  return 1 + (1-x/y);
      } else {
	if (x > (-y))
	  return -1 + (1+y/x);
	else
	  return -2 - x/y;
      }
    } else {
      if (y >= 0) {
	if ((-x) < y)
	  return 2 - x/y;
	else
	  return 3 + (1+y/x);
      } else {
	if ((-x) > (-y))
	return -4+y/x;
      else
	return -3+1-x/y;
      }
    }
  }
  /**
   * Compute the point on a circle nearest to a certain point.
   * @param	from	The given point.
   * @param	center	The center of the circle.
   * @param	radius	The radius of the circle.
   * @return	The closest point.
   */
  public static Point nearest_point(Point from, Point center, int radius) {
    int dx = from.x - center.x, dy = from.y - center.y;
    double hyp = Math.sqrt(dx*dx+dy*dy);
    if (hyp == 0) {
      dx = 1;
      dy = 0;
      hyp = 1;
    }
    return new Point(center.x+(int)(dx * radius / hyp),
		     center.y+(int)(dy * radius / hyp));
  }

  /* Support for quicksort */
  /**
   * Displaced quicksort.
   * @param	indexes	The vector of initial indices (0..n) that will
   * be sorted according to the order table.
   * @param	order	The vector of values that will be sorted in
   * ascending order.
   */
  public static void quicksort(int[] indexes, double[] order) {
    quicksort(indexes, order, 0, indexes.length-1);
//     for (int i = 0; i < (indexes.length-1); i++) {
//       if (order[i] > order[i+1]) {
// 	System.out.println("Error in sorting: order["+i+"]="+order[i]+
// 			   ", order["+(i+1)+"]="+order[i+1]);
//       }
//     }
  }

  private static void swap(int[] indexes, double[] order, int i, int j) {
    int ti = indexes[i];
    indexes[i] = indexes[j];
    indexes[j] = ti;
    double td = order[i];
    order[i] = order[j];
    order[j] = td;
  }
  
  private static void quicksort(int[] indexes, double[] order,
				int left, int right) {
				int i, last;

    if (left >= right) { /* do nothing if array contains fewer than two */
      return; 	     /* two elements */
    }
    swap(indexes, order, left, (left+right) / 2);
    last = left;
    for (i = left+1; i <= right; i++) {
      if (order[i] < order[left]) {
	swap(indexes, order, ++last, i);
      }
    }
    swap(indexes, order, left, last);
    quicksort(indexes, order, left, last-1);
    quicksort(indexes, order, last+1, right);
  }
  /* If you want to try it...
  public static void main(String argv[]) {
    double min_dist = 10000, max_dist = 0;
    int radius = 100;
    for (double a = -Math.PI; a < Math.PI; a += Math.PI / 100) {
      double x = radius * Math.cos(a);
      double y = radius * Math.sin(a);
      double fa = fast_atan2(x, y) * FAST_TO_RAD;
      double dist = Math.abs(fa - a);
      //System.out.println("for "+a+"("+x+","+y+") angle="+fa);
      min_dist = Math.min(dist, min_dist);
      max_dist = Math.max(dist, max_dist);
    }
    //System.out.println("Max error in computing fast_atan2 "+max_dist);
  }
  */
}
