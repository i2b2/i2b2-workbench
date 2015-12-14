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
 * A RectLayout tries to layout a list of labels as two left-aligned columns of
 * labels.
 *<p>
 * The only visible method is <code>layout</code> that compute the optimal
 * positions of labels on the list.
 * <p>
 * The algorithm works that way:
 * <p>
 * <ul>
 * <li>The labels are initially placed on the circle at the position closest to
 * the item they point to.
 * <li>We move them closer the focus radius first.
 * <li>We order them from left to right in trigonometric order, i.e. we
 * associate a real value to each item where labels on the left have negative
 * values and the ones on the right have positive values. The values increase
 * when going downwards on the left and upwards on the right, being 0 at the
 * bottom.
 * <li>We sort the vector according to this order and place left labels on the
 * left and right labels on the right.
 * <li>Labels are left aligned, so for the left label, we have a bit more work
 * than for the right labels.
 * <li>For the left labels, we compute their max widths to have the left
 * position where all the labels are positioned.
 * <li>We then add two new lines to each <code>LiteDisplacedLabel</code>: one
 * that goes horizontally from the right of the label to the right of the
 * longest label and the second which connects this one to the endpoint of the
 * original line (on the focus circle).
 * <li>For the right labels, all the labels are left aligned and only only line
 * is added to each <code>LiteDisplaedLabel</code>.
 * <li>Each label is not only left aligned, but also stacked on top of each
 * others. Once this is done, we compute the best vertical origin by finding the
 * smallest distance of all the labels to their position on the circle radius.
 * 
 * @version 0.1, 07/30/98
 * @author Jean-Daniel Fekete
 * @since JDK1.1.5
 */
public class RectLayout extends LabelLayout {
	boolean stable_x;
	int column;
	int[] offsets;

	/**
	 * Construct a new <code>RectLayout</code> object.
	 */
	public RectLayout(int width, int height, int column) {
		super(width, height);
		this.column = column;
	}

	public RectLayout(int width, int height, int column, boolean sx) {
		super(width, height);
		stable_x = sx;
		this.column = column;
	}

	/**
	 * Compute the best layout for the list of labels.
	 * <p>
	 * 
	 * @param v
	 *            Vector of <code>LiteDisplacedLabel</code>.
	 * @param center
	 *            Center point of the circle.
	 * @param radius
	 *            Radius of the circle.
	 * @return <code>OVERLAP</code> in case too many labels are on the circle or
	 *         <code>DONT_OVERLAP</code> if the layout succeeded.
	 */
	@Override
	public int do_layout() {
		int nlabel = v.size();

		if (nlabel == 0)
			return DONT_OVERLAP;

		if (getStableX())
			record_offsets();
		move_labels_initially(radius / 2);
		double order[] = new double[nlabel + 1]; // first is copied in last
		compute_order(order);
		sort_labels_vector(order);
		if (column == 2)
			return compute_left_right(order);
		else
			return compute_right(order);
	}

	protected int compute_left_right(double order[]) {
		int nlabel = v.size();
		int left_right;
		int i;

		for (left_right = 0; left_right < nlabel && order[left_right] < 0; left_right++)
			/* do nothing */;

		int max_left_width = compute_max_width(0, left_right);
		int max_right_width = compute_max_width(left_right, nlabel);
		if ((center.x - radius - max_left_width) < 0) {
			// move everything to the right
			for (i = 0; i < left_right; i++)
				order[i] = -order[i];
			sort_labels_vector(order);
			align_right(order, 0);
		} else if ((center.x + radius + max_right_width) > width) {
			for (i = left_right; i < nlabel; i++)
				order[i] = -order[i];
			sort_labels_vector(order);
			align_left(order, nlabel, Math.max(max_left_width, max_right_width));
		} else {
			align_left(order, left_right, max_left_width);
			align_right(order, left_right);
		}
		return DONT_OVERLAP;
	}

	// Julia: only one column on the right hand size
	protected int compute_right(double order[]) {
		int nlabel = v.size();
		int max_right_width = compute_max_width(0, nlabel);
		align_right(order, 0);
		return DONT_OVERLAP;
	}

	/**
	 * Move each <code>LiteDisplacedLabel</code> to another circle radius.
	 * 
	 * @param v
	 *            List of <code>LiteDisplacedLabel</code>.
	 * @param center
	 *            Center of the circle.
	 * @param radius
	 *            Radius of the circle.
	 */
	public void move_labels_initially(int radius) {
		for (int i = 0; i < v.size(); i++) {
			LiteDisplacedLabel l = (LiteDisplacedLabel) v.elementAt(i);
			l.setPosition(nearest_point(l.getPosition(), center, radius));
		}
	}

	/**
	 * Align left of the <code>LiteDisplacedLabel</code> objects from index 0 to
	 * index <code>last</code>.
	 */
	public void align_left(double order[], int last, int max_width) {
		int first = 0;
		if (first == last)
			return;
		int left = center.x - radius - max_width;
		int i;
		int height = ((LiteDisplacedLabel) v.firstElement()).getLabel()
				.getBounds().height;

		optimize_origin(order, height, first, last);

		int offset = compute_offset(order[first] + center.y + radius - height
				/ 2, order[last - 1] + center.y + radius + height / 2);

		for (i = first; i < last; i++) {
			LiteDisplacedLabel l = (LiteDisplacedLabel) v.elementAt(i);
			LiteLabel lab = l.getLabel();
			lab.setAlignment(LiteLabel.LEFT);
			Point p = lab.getPosition();
			p.x = left;
			if (stable_x)
				p.x += offsets[i];
			p.y = (int) order[i] + center.y + radius + offset;

			lab.setPosition(p);
			Rectangle r = lab.getBounds();
			Point p2 = new Point(p.x + max_width, p.y);
			p.x += r.width;

			if (l.getItem() == 2) {
				LiteLine ll = l.getLine();
				l.addElement(new LiteLine(p, p2, ll.getThickness(), ll
						.getColor()));
				l.addElement(new LiteLine(p2, ll.getPosition(), ll
						.getThickness(), ll.getColor()));
				// l.getLine().setPosition(p);
			}
		}
	}

	public void align_right(double order[], int first) {
		int last = v.size();
		if (first == last)
			return;
		int left = center.x + radius;
		int i;
		int height = ((LiteDisplacedLabel) v.firstElement()).getLabel()
				.getBounds().height;

		// Julia, when one column, move the labels closer to the square
		if (column == 1)
			left = center.x + radius * 3 / 4;
		optimize_origin(order, -height, first, last);
		int offset = compute_offset(order[first] + center.y + radius + height
				/ 2, order[last - 1] + center.y + radius - height / 2);

		for (i = first; i < last; i++) {
			LiteDisplacedLabel l = (LiteDisplacedLabel) v.elementAt(i);
			LiteLabel lab = l.getLabel();
			lab.setAlignment(LiteLabel.LEFT);
			Point p = lab.getPosition();
			p.x = left;
			if (stable_x)
				p.x += offsets[i];
			if (column == 1)
				p.y = (int) order[i] + center.y;
			else
				p.y = (int) order[i] + center.y + radius + offset;
			lab.setPosition(p);
			// System.out.println("right aligning "+i+" angle("+order[i]+
			// ") at ("+p.x+","+p.y+")");
			Rectangle r = lab.getBounds();
			if (l.getItem() == 2) {
				LiteLine ll = l.getLine();
				l.addElement(new LiteLine(p, ll.getPosition(), ll
						.getThickness(), ll.getColor()));
				// l.getLine().setPosition(p);
			}
		}
	}

	public void record_offsets() {
		int nlabel = v.size();

		offsets = new int[nlabel];
		for (int i = 0; i < nlabel; i++) {
			Point p = ((Lite) v.elementAt(i)).getPosition();
			offsets[i] = p.x - center.x;
		}
	}

	public void setStableX(boolean sx) {
		stable_x = sx;
	}

	public boolean getStableX() {
		return stable_x;
	}

	public int getColumn() {
		return column;
	}

	protected int compute_max_width(int start, int end) {
		int max_width = 0;
		for (int i = start; i < end; i++) {
			LiteDisplacedLabel l = (LiteDisplacedLabel) v.elementAt(i);
			Rectangle r = l.getLabel().getBounds();
			if (stable_x)
				max_width = Math.max(max_width, r.width + Math.abs(offsets[i]));
			else
				max_width = Math.max(max_width, r.width);
		}
		return max_width;
	}

	protected void compute_order(double order[]) {
		int nlabel = v.size();

		for (int i = 0; i < nlabel; i++) {
			LiteDisplacedLabel l1 = (LiteDisplacedLabel) v.elementAt(i);
			Point p = l1.getPosition();
			if (p.x == center.x && p.y == center.y)
				order[i] = 0;
			else {
				order[i] = rect_atan2(p.x - center.x, p.y - center.y - radius);
			}
		}
	}

	protected void compute_order_x(double order[]) {
		int nlabel = v.size();

		for (int i = 0; i < nlabel; i++) {
			LiteDisplacedLabel l1 = (LiteDisplacedLabel) v.elementAt(i);
			order[i] = nlabel / 2 - i;
		}
	}

	protected int compute_offset(double dy0, double dy1) {
		int y0 = (int) (dy0 + 0.5);
		int y1 = (int) (dy1 + 0.5);
		if (y0 > y1) {
			int tmp = y0;
			y0 = y1;
			y1 = tmp;
		}
		if (y0 < 0) {
			// System.out.println("Offset is "+(-y0));
			return -y0;
		}
		if (y1 > height) {
			// System.out.println("Offset is "+(height - y1));
			return height - y1;
		}
		return 0;
	}

	static double rect_atan2(double dx, double dy) {
		return (dx < 0) ? dy : -dy;
	}

	public void optimize_origin(double order[], int space, int from, int to) {
		int origin = 0;
		int i;

		for (i = from; i < to; i++) {
			int o = Math.abs((int) order[i]);
			origin += space * (i - from) + o;
		}
		origin /= from - to;
		for (i = from; i < to; i++)
			order[i] = (i - from) * space + origin;
	}
}
