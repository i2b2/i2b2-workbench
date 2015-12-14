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
import java.awt.image.*;
import java.util.*;
import edu.harvard.i2b2.timeline.lifelines.record;

public abstract class ImgFilt implements ImageConsumer {
	protected int new_width;
	protected int new_height;
	protected int newpixels[];
	protected Color trans_color;
	protected int trans_color_value;
	protected boolean transparent = false;
	protected Color bg = Color.white;
	protected Color fg = Color.black;
	protected Font font;
	protected String tx;
	protected record applet;
	protected boolean underline;

	public abstract int[] filter(int[] p1, int w, int h);

	public int getWidth() {
		return new_width;
	}

	public int getHeight() {
		return new_height;
	}

	public void setparameter(String s, int i) {
		// Do nothing
	}

	public void setparameter(String s) {
		// Do nothing
	}

	public void setTransparentColor(Color color) {
		transparent = true;
		trans_color = color;
		trans_color_value = (0xFF << 24) | (color.getRed() << 16)
				| (color.getGreen() << 8) | (color.getBlue());
	}

	public void setTransparentColor(boolean t, Color color) {
		transparent = t;
		trans_color = color;
		trans_color_value = (0xFF << 24) | (color.getRed() << 16)
				| (color.getGreen() << 8) | (color.getBlue());
	}

	public void setTransparent(boolean t) {
		transparent = t;
	}

	public void setBackground(Color bg) {
		this.bg = bg;
	}

	public void setForeground(Color fg) {
		this.fg = fg;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setApplet(record a) {
		applet = a;
	}

	public void setText(String t) {
		tx = t;
	}

	public void setUnderline(boolean b) {
		underline = b;
	}

	public Color String2Color(String s) {
		String retcolor;

		if (s == null)
			return null;
		try {
			if (s.charAt(0) == '#') {
				char chars[];
				// Get rid of leading #
				chars = new char[s.length()];
				s.getChars(0, s.length(), chars, 0);
				return (new Color(Integer.parseInt(new String(chars, 1, s
						.length() - 1), 16)));
			} else {
				return (new Color(Integer.parseInt(s, 16)));
			}
		} catch (NumberFormatException e) {
			retcolor = getColor(s);
			if (retcolor != null)
				return (new Color(Integer.parseInt(retcolor, 16)));
			else
				System.out
						.println("Bad color specification: " + e.getMessage());

			return null;
		}
	}

	// Special code to figure what the bg color REALLY is!
	// This is necessary because there is a bug when using 16 or 24 bit
	// color on Win95. You fill the rect specifying one color, and you get
	// another color
	int getRealBackgroundColor(Color bg) {
		Image tmp_image;
		Graphics tmp_g;
		int tmp_pixels[];
		PixelGrabber tmp_pg;
		boolean retval;
		String os_name;

		os_name = System.getProperty("os.name");
		if (os_name.equals("Solaris") || os_name.equals("SunOS"))
			return bg.getRGB();
		tmp_image = applet.createImage(1, 1);
		tmp_g = tmp_image.getGraphics();
		tmp_g.setColor(bg);
		tmp_g.fillRect(0, 0, 1, 1);
		tmp_pixels = new int[1];
		// PixGrabInit(tmp_image, 0, 0, 1, 1, tmp_pixels, 0, 1);
		tmp_pg = new PixelGrabber(tmp_image, 0, 0, 1, 1, tmp_pixels, 0, 1);
		try {
			// retval = grabPixels(0);
			retval = tmp_pg.grabPixels(0);
			if (retval == false)
				System.out.println("getRealBackgroundColor: grabPixels failed");
		} catch (InterruptedException e) {
			System.err.println("Interrupted waiting for pixels");
		}
		return tmp_pixels[0];
	}

	//
	// The drawUnderline method draws an underline
	// The underline is actually a filled rectangle
	// whose size is based on the width of a pipe
	// character from the same font. It works!
	//
	protected void drawUnderline(Graphics g, FontMetrics fm, int x, int y,
			int w, Color c) {
		if (underline) {
			int pipewidth = fm.charWidth('|');
			g.setColor(c);
			g.fillRect(x, y, w, Math.max(1, pipewidth / 4));
		}
	}

	// Begin - Included from ColrLook.include

	private Hashtable colors;

	private String getColor(String name) {
		if (colors == null)
			createHashTable();
		return (String) colors.get(name);
	}

	private void createHashTable() {

		if (colors != null)
			return;

		colors = new Hashtable(650);
		colors.put("aliceblue", "f0f8ff");
		colors.put("antiquewhite", "faebd7");
		colors.put("aquamarine", "7fffd4");
		colors.put("azure", "f0ffff");
		colors.put("beige", "f5f5dc");
		colors.put("bisque", "ffe4c4");
		colors.put("black", "000000");
		colors.put("blanchedalmond", "ffebcd");
		colors.put("blue", "0000ff");
		colors.put("blueviolet", "8a2be2");
		colors.put("brown", "a52a2a");
		colors.put("burlywood", "deb887");
		colors.put("cadetblue", "5f9ea0");
		colors.put("chartreuse", "7fff00");
		colors.put("chocolate", "d2691e");
		colors.put("coral", "ff7f50");
		colors.put("cornflowerblue", "6495ed");
		colors.put("cornsilk", "fff8dc");
		colors.put("cyan", "00ffff");
		colors.put("darkgoldenrod", "b8860b");
		colors.put("darkgreen", "006400");
		colors.put("darkkhaki", "bdb76b");
		colors.put("darkolivegreen", "556b2f");
		colors.put("darkorange", "ff8c00");
		colors.put("darkorchid", "9932cc");
		colors.put("darksalmon", "e9967a");
		colors.put("darkseagreen", "8fbc8f");
		colors.put("darkslateblue", "483d8b");
		colors.put("darkslategray", "2f4f4f");
		colors.put("darkslategrey", "2f4f4f");
		colors.put("darkturquoise", "00ced1");
		colors.put("darkviolet", "9400d3");
		colors.put("deeppink", "ff1493");
		colors.put("deepskyblue", "00bfff");
		colors.put("dimgray", "696969");
		colors.put("dimgrey", "696969");
		colors.put("dodgerblue", "1e90ff");
		colors.put("firebrick", "b22222");
		colors.put("floralwhite", "fffaf0");
		colors.put("forestgreen", "228b22");
		colors.put("green", "00ff00");
		colors.put("gainsboro", "dcdcdc");
		colors.put("ghostwhite", "f8f8ff");
		colors.put("gold", "ffd700");
		colors.put("goldenrod", "daa520");
		colors.put("gray", "bebebe");
		colors.put("honeydew", "f0fff0");
		colors.put("hotpink", "ff69b4");
		colors.put("indianred", "cd5c5c");
		colors.put("ivory", "fffff0");
		colors.put("khaki", "f0e68c");
		colors.put("lavender", "e6e6fa");
		colors.put("lavenderblush", "fff0f5");
		colors.put("lawngreen", "7cfc00");
		colors.put("lemonchiffon", "fffacd");
		colors.put("lightblue", "add8e6");
		colors.put("lightcoral", "f08080");
		colors.put("lightcyan", "e0ffff");
		colors.put("lightgoldenrod", "eedd82");
		colors.put("lightgoldenrodyellow", "fafad2");
		colors.put("lightgray", "d3d3d3");
		colors.put("lightgrey", "d3d3d3");
		colors.put("lightpink", "ffb6c1");
		colors.put("lightsalmon", "ffa07a");
		colors.put("lightseagreen", "20b2aa");
		colors.put("lightskyblue", "87cefa");
		colors.put("lightslateblue", "8470ff");
		colors.put("lightslategray", "778899");
		colors.put("lightslategrey", "778899");
		colors.put("lightsteelblue", "b0c4de");
		colors.put("lightyellow", "ffffe0");
		colors.put("limegreen", "32cd32");
		colors.put("linen", "faf0e6");
		colors.put("magenta", "ff00ff");
		colors.put("maroon", "b03060");
		colors.put("mediumaquamarine", "66cdaa");
		colors.put("mediumblue", "0000cd");
		colors.put("mediumorchid", "ba55d3");
		colors.put("mediumpurple", "9370db");
		colors.put("mediumseagreen", "3cb371");
		colors.put("mediumslateblue", "7b68ee");
		colors.put("mediumspringgreen", "00fa9a");
		colors.put("mediumturquoise", "48d1cc");
		colors.put("mediumvioletred", "c71585");
		colors.put("midnightblue", "191970");
		colors.put("mintcream", "f5fffa");
		colors.put("mistyrose", "ffe4e1");
		colors.put("moccasin", "ffe4b5");
		colors.put("navajowhite", "ffdead");
		colors.put("navy", "000080");
		colors.put("navyblue", "000080");
		colors.put("oldlace", "fdf5e6");
		colors.put("olivedrab", "6b8e23");
		colors.put("orange", "ffa500");
		colors.put("orangered", "ff4500");
		colors.put("orchid", "da70d6");
		colors.put("palegoldenrod", "eee8aa");
		colors.put("palegreen", "98fb98");
		colors.put("paleturquoise", "afeeee");
		colors.put("palevioletred", "db7093");
		colors.put("papayawhip", "ffefd5");
		colors.put("peachpuff", "ffdab9");
		colors.put("peru", "cd853f");
		colors.put("pink", "ffc0cb");
		colors.put("plum", "dda0dd");
		colors.put("powderblue", "b0e0e6");
		colors.put("purple", "a020f0");
		colors.put("red", "ff0000");
		colors.put("rosybrown", "bc8f8f");
		colors.put("royalblue", "4169e1");
		colors.put("saddlebrown", "8b4513");
		colors.put("salmon", "fa8072");
		colors.put("sandybrown", "f4a460");
		colors.put("seagreen", "2e8b57");
		colors.put("seashell", "fff5ee");
		colors.put("sienna", "a0522d");
		colors.put("skyblue", "87ceeb");
		colors.put("slateblue", "6a5acd");
		colors.put("slategray", "708090");
		colors.put("slategrey", "708090");
		colors.put("snow", "fffafa");
		colors.put("springgreen", "00ff7f");
		colors.put("steelblue", "4682b4");
		colors.put("tan", "d2b48c");
		colors.put("thistle", "d8bfd8");
		colors.put("tomato", "ff6347");
		colors.put("turquoise", "40e0d0");
		colors.put("violet", "ee82ee");
		colors.put("violetred", "d02090");
		colors.put("wheat", "f5deb3");
		colors.put("white", "ffffff");
		colors.put("whitesmoke", "f5f5f5");
		colors.put("yellow", "ffff00");
		colors.put("yellowgreen", "9acd32");
	}

	// End - Included from ColrLook.include

	// Begin - Included from GetPixels.include

	public synchronized void loadImageAndWait(Image image, record applet) {
		int checkImageFlags;
		boolean ImagePrepared;

		ImagePrepared = applet.prepareImage(image, applet);
		if (ImagePrepared == false) {
			while (((checkImageFlags = applet.checkImage(image, applet)) & ImageObserver.ALLBITS) == 0) {
				try {
					wait(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public synchronized int getWidth(Image image, ImageObserver observer) {
		int width;
		while ((width = image.getWidth(observer)) == -1) {
			try {
				wait(100);
			} catch (InterruptedException e) {
			}
		}
		return width;
	}

	public synchronized int getHeight(Image image, ImageObserver observer) {
		int height;
		while ((height = image.getHeight(observer)) == -1) {
			try {
				wait(100);
			} catch (InterruptedException e) {
			}
		}
		return height;
	}

	public int[] getPixels(Image image, record applet) {
		int pixels[];
		int w, h;
		boolean retval;

		w = getWidth(image, applet);
		h = getHeight(image, applet);
		loadImageAndWait(image, applet);
		pixels = new int[w * h];
		PixGrabInit(image, 0, 0, w, h, pixels, 0, w);
		try {
			retval = grabPixels(0);
		} catch (InterruptedException e) {
			System.err.println("Interrupted waiting for pixels");
		}
		return pixels;
	}

	// End - Included from GetPixels.include

	ImageProducer producer;

	int dstX;
	int dstY;
	int dstW;
	int dstH;

	int[] pixelbuf;
	int dstOff;
	int dstScan;

	private boolean grabbing;
	private int flags;

	private final int GRABBEDBITS = (ImageObserver.FRAMEBITS | ImageObserver.ALLBITS);
	private final int DONEBITS = (GRABBEDBITS | ImageObserver.ERROR);

	/**
	 * Create a PixelGrabber object to grab the (x, y, w, h) rectangular section
	 * of pixels from the specified image into the given array. The pixels are
	 * stored into the array in the default RGB ColorModel. The RGB data for
	 * pixel (i, j) where (i, j) is inside the rectangle (x, y, w, h) is stored
	 * in the array at <tt>pix[(j - y) * scansize + (i - x) + off]</tt>.
	 * 
	 * @see ColorModel#getRGBdefault
	 * @param img
	 *            the image to retrieve pixels from
	 * @param x
	 *            the x coordinate of the upper left corner of the rectangle of
	 *            pixels to retrieve from the image, relative to the default
	 *            (unscaled) size of the image
	 * @param y
	 *            the y coordinate of the upper left corner of the rectangle of
	 *            pixels to retrieve from the image
	 * @param w
	 *            the width of the rectangle of pixels to retrieve
	 * @param h
	 *            the height of the rectangle of pixels to retrieve
	 * @param pix
	 *            the array of integers which are to be used to hold the RGB
	 *            pixels retrieved from the image
	 * @param off
	 *            the offset into the array of where to store the first pixel
	 * @param scansize
	 *            the distance from one row of pixels to the next in the array
	 */
	public void PixGrabInit(Image img, int x, int y, int w, int h, int[] pix,
			int off, int scansize) {
		PixGrabInit(img.getSource(), x, y, w, h, pix, off, scansize);
	}

	/**
	 * Create a PixelGrabber object to grab the (x, y, w, h) rectangular section
	 * of pixels from the image produced by the specified ImageProducer into the
	 * given array. The pixels are stored into the array in the default RGB
	 * ColorModel. The RGB data for pixel (i, j) where (i, j) is inside the
	 * rectangle (x, y, w, h) is stored in the array at
	 * <tt>pix[(j - y) * scansize + (i - x) + off]</tt>.
	 * 
	 * @see ColorModel#getRGBdefault
	 * @param img
	 *            the image to retrieve pixels from
	 * @param x
	 *            the x coordinate of the upper left corner of the rectangle of
	 *            pixels to retrieve from the image, relative to the default
	 *            (unscaled) size of the image
	 * @param y
	 *            the y coordinate of the upper left corner of the rectangle of
	 *            pixels to retrieve from the image
	 * @param w
	 *            the width of the rectangle of pixels to retrieve
	 * @param h
	 *            the height of the rectangle of pixels to retrieve
	 * @param pix
	 *            the array of integers which are to be used to hold the RGB
	 *            pixels retrieved from the image
	 * @param off
	 *            the offset into the array of where to store the first pixel
	 * @param scansize
	 *            the distance from one row of pixels to the next in the array
	 */
	public void PixGrabInit(ImageProducer ip, int x, int y, int w, int h,
			int[] pix, int off, int scansize) {
		producer = ip;
		dstX = x;
		dstY = y;
		dstW = w;
		dstH = h;
		dstOff = off;
		dstScan = scansize;
		pixelbuf = pix;
	}

	/**
	 * Request the Image or ImageProducer to start delivering pixels and wait
	 * for all of the pixels in the rectangle of interest to be delivered.
	 * 
	 * @return true if the pixels were successfully grabbed, false on abort,
	 *         error or timeout
	 * @exception InterruptedException
	 *                Another thread has interrupted this thread.
	 */
	public boolean grabPixels() throws InterruptedException {
		return grabPixels(0);
	}

	/**
	 * Request the Image or ImageProducer to start delivering pixels and wait
	 * for all of the pixels in the rectangle of interest to be delivered or
	 * until the specified timeout has elapsed.
	 * 
	 * @param ms
	 *            the number of milliseconds to wait for the image pixels to
	 *            arrive before timing out
	 * @return true if the pixels were successfully grabbed, false on abort,
	 *         error or timeout
	 * @exception InterruptedException
	 *                Another thread has interrupted this thread.
	 */
	public synchronized boolean grabPixels(long ms) throws InterruptedException {
		if ((flags & DONEBITS) != 0) {
			return (flags & GRABBEDBITS) != 0;
		}
		long end = ms + System.currentTimeMillis();
		if (!grabbing) {
			grabbing = true;
			flags &= ~(ImageObserver.ABORT);
			producer.startProduction(this);
		}
		while (grabbing) {
			long timeout;
			if (ms == 0) {
				timeout = 0;
			} else {
				timeout = end - System.currentTimeMillis();
				if (timeout <= 0) {
					break;
				}
			}
			wait(timeout);
		}
		return (flags & GRABBEDBITS) != 0;
	}

	/**
	 * Return the status of the pixels. The ImageObserver flags representing the
	 * available pixel information are returned.
	 * 
	 * @see ImageObserver
	 * @return the bitwise OR of all relevant ImageObserver flags
	 */
	public synchronized int status() {
		return flags;
	}

	/**
	 * The setDimensions method is part of the ImageConsumer API which this
	 * class must implement to retrieve the pixels.
	 */
	public void setDimensions(int width, int height) {
		return;
	}

	/**
	 * The setHints method is part of the ImageConsumer API which this class
	 * must implement to retrieve the pixels.
	 */
	public void setHints(int hints) {
		return;
	}

	/**
	 * The setProperties method is part of the ImageConsumer API which this
	 * class must implement to retrieve the pixels.
	 */
	public void setProperties(Hashtable props) {
		return;
	}

	/**
	 * The setColorModel method is part of the ImageConsumer API which this
	 * class must implement to retrieve the pixels.
	 */
	public void setColorModel(ColorModel model) {
		return;
	}

	/**
	 * The setPixels method is part of the ImageConsumer API which this class
	 * must implement to retrieve the pixels.
	 */
	public void setPixels(int srcX, int srcY, int srcW, int srcH,
			ColorModel model, byte pixels[], int srcOff, int srcScan) {
		if (srcY < dstY) {
			int diff = dstY - srcY;
			if (diff >= srcH) {
				return;
			}
			srcOff += srcScan * diff;
			srcY += diff;
			srcH -= diff;
		}
		if (srcY + srcH > dstY + dstH) {
			srcH = (dstY + dstH) - srcY;
			if (srcH <= 0) {
				return;
			}
		}
		if (srcX < dstX) {
			int diff = dstX - srcX;
			if (diff >= srcW) {
				return;
			}
			srcOff += diff;
			srcX += diff;
			srcW -= diff;
		}
		if (srcX + srcW > dstX + dstW) {
			srcW = (dstX + dstW) - srcX;
			if (srcW <= 0) {
				return;
			}
		}
		int dstPtr = dstOff + (srcY - dstY) * dstScan + (srcX - dstX);
		int dstRem = dstScan - dstW;
		int srcRem = srcScan - srcW;
		for (int h = srcH; h > 0; h--) {
			for (int w = srcW; w > 0; w--) {
				pixelbuf[dstPtr++] = model.getRGB(pixels[srcOff++] & 0xff);
			}
			srcOff += srcRem;
			dstPtr += dstRem;
		}
		flags |= ImageObserver.SOMEBITS;
	}

	/**
	 * The setPixels method is part of the ImageConsumer API which this class
	 * must implement to retrieve the pixels.
	 */
	public void setPixels(int srcX, int srcY, int srcW, int srcH,
			ColorModel model, int pixels[], int srcOff, int srcScan) {
		if (srcY < dstY) {
			int diff = dstY - srcY;
			if (diff >= srcH) {
				return;
			}
			srcOff += srcScan * diff;
			srcY += diff;
			srcH -= diff;
		}
		if (srcY + srcH > dstY + dstH) {
			srcH = (dstY + dstH) - srcY;
			if (srcH <= 0) {
				return;
			}
		}
		if (srcX < dstX) {
			int diff = dstX - srcX;
			if (diff >= srcW) {
				return;
			}
			srcOff += diff;
			srcX += diff;
			srcW -= diff;
		}
		if (srcX + srcW > dstX + dstW) {
			srcW = (dstX + dstW) - srcX;
			if (srcW <= 0) {
				return;
			}
		}
		int dstPtr = dstOff + (srcY - dstY) * dstScan + (srcX - dstX);
		int dstRem = dstScan - dstW;
		int srcRem = srcScan - srcW;
		for (int h = srcH; h > 0; h--) {
			for (int w = srcW; w > 0; w--) {
				pixelbuf[dstPtr++] = model.getRGB(pixels[srcOff++]);
			}
			srcOff += srcRem;
			dstPtr += dstRem;
		}
		flags |= ImageObserver.SOMEBITS;
	}

	/**
	 * The imageComplete method is part of the ImageConsumer API which this
	 * class must implement to retrieve the pixels.
	 */
	public synchronized void imageComplete(int status) {
		grabbing = false;
		switch (status) {
		default:
		case IMAGEERROR:
			flags |= ImageObserver.ERROR | ImageObserver.ABORT;
			break;
		case IMAGEABORTED:
			flags |= ImageObserver.ABORT;
			break;
		case STATICIMAGEDONE:
			flags |= ImageObserver.ALLBITS;
			break;
		case SINGLEFRAMEDONE:
			flags |= ImageObserver.FRAMEBITS;
			break;
		}
		producer.removeConsumer(this);
		notifyAll();
	}
}
