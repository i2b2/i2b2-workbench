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

public class Frotate extends ImgFilt {
	int degree = 0;
	double radians;

	@Override
	public void setparameter(String str, int i) {
		switch (i) {
		case 0:
			try {
				degree = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				System.err.println("rotate: Invalid degree " + str);
				degree = 0;
			}
			break;
		}
	}

	@Override
	public int[] filter(int[] p1, int w, int h) {
		int x, y;
		int newy;
		int newx;
		double sine, cosine;
		int imagesize = w * h;
		int vert_shift, horiz_shift;
		int new_index;
		int biggestx, biggesty, smallesty;
		int old_index, oldx, oldy;
		int tmp, index;
		int tmppixels[];

		if (p1.length != (imagesize)) {
			System.out.println("rotate filter: got array of wrong size");
			return null;
		}
		if (degree > 360)
			degree %= 360;
		if (degree < 0) {
			if (degree < -360)
				degree %= 360;
			degree = 360 + degree;
		}
		if (degree >= 270) {
			tmppixels = new int[w * h];
			new_index = 0;
			for (x = 0; x < w; x++) {
				for (y = h - 1; y >= 0; y--) {
					index = (y * w) + x;
					if (transparent == false) {
						tmppixels[new_index++] = p1[index];
					} else {
						if (p1[index] == trans_color_value) // Found trans_color
							tmppixels[new_index++] = 0x00ffffff; // Make Transp
						else
							tmppixels[new_index++] = p1[index];
					}
				}
			}
			tmp = h;
			h = w;
			w = tmp;
			if (degree == 270) {
				new_width = w;
				new_height = h;
				return tmppixels;
			}
			p1 = tmppixels;
			degree -= 270;
		} else if (degree >= 180) {
			tmppixels = new int[w * h];
			new_index = 0;
			for (y = h - 1; y >= 0; y--) {
				for (x = w - 1; x >= 0; x--) {
					index = (y * w) + x;
					if (transparent == false) {
						tmppixels[new_index++] = p1[index];
					} else {
						if (p1[index] == trans_color_value) // Found trans_color
							tmppixels[new_index++] = 0x00ffffff; // Make Transp
						else
							tmppixels[new_index++] = p1[index];
					}
				}
			}
			if (degree == 180) {
				new_width = w;
				new_height = h;
				return tmppixels;
			}
			p1 = tmppixels;
			degree -= 180;
		} else if (degree >= 90) {
			tmppixels = new int[w * h];
			new_index = 0;
			for (x = w - 1; x >= 0; x--) {
				for (y = 0; y < h; y++) {
					index = (y * w) + x;
					if (transparent == false) {
						tmppixels[new_index++] = p1[index];
					} else {
						if (p1[index] == trans_color_value) // Found trans_color
							tmppixels[new_index++] = 0x00ffffff; // Make Transp
						else
							tmppixels[new_index++] = p1[index];
					}
				}
			}
			tmp = h;
			h = w;
			w = tmp;
			if (degree == 90) {
				new_width = w;
				new_height = h;
				return tmppixels;
			}
			p1 = tmppixels;
			degree -= 90;
		}
		degree = -degree;

		radians = Math.PI * degree / 180.0;
		sine = Math.sin(radians);
		cosine = Math.cos(radians);

		newx = ((int) Math.round(((w) * cosine) - ((h) * sine)));
		newy = ((int) Math.round(((w) * sine) + ((h) * cosine)));
		biggestx = newx;
		biggesty = newy;
		smallesty = newy;

		newx = ((int) Math.round(((0) * cosine) - ((h) * sine)));
		newy = ((int) Math.round(((0) * sine) + ((h) * cosine)));
		if (newx > biggestx)
			biggestx = newx;
		if (newy > biggesty)
			biggesty = newy;
		if (newy < smallesty)
			smallesty = newy;

		newx = ((int) Math.round(((w) * cosine) - ((0) * sine)));
		newy = ((int) Math.round(((w) * sine) + ((0) * cosine)));

		if (newx > biggestx)
			biggestx = newx;
		if (newy > biggesty)
			biggesty = newy;
		if (newy < smallesty)
			smallesty = newy;

		degree = -degree;

		radians = Math.PI * degree / 180.0;
		sine = Math.sin(radians);
		cosine = Math.cos(radians);

		vert_shift = (int) Math.abs(Math.round(Math.tan(radians) * w));
		horiz_shift = (int) Math.abs(Math.round(Math.sin(radians) * h));

		new_width = biggestx;
		new_height = biggesty - smallesty;
		horiz_shift = biggestx - w;
		vert_shift = biggesty;
		newpixels = new int[(new_width) * (new_height)];
		for (y = 0; y < new_height; y++) {
			for (x = 0; x < new_width; x++) {
				oldx = (int) Math
						.round((x * cosine) - ((y + smallesty) * sine));
				oldy = (int) Math
						.round((x * sine) + ((y + smallesty) * cosine));
				old_index = idx(oldx, oldy, w);
				if (oldx > 0 && oldx < w && oldy > 0 && oldy < h
						&& old_index > 0 && old_index < imagesize)
					newpixels[idx(x, y, new_width)] = p1[old_index];
			}
		}
		return newpixels;
	}

	public int idx(int x, int y, int w) {
		return (y * w) + x;
	}
}
