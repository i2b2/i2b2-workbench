/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *     Wensong Pan
 *     
 */
package edu.harvard.i2b2.analysis.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MediaTracker;

public class GIFComponent extends Component {
	private java.awt.Image image;
	private boolean stop = false;

	// private long curtime = System.currentTimeMillis();

	public GIFComponent(java.awt.Image image) {
		super();
		this.image = image;
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(image, 0);
		try {
			mt.waitForID(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y,
			int width, int height) {

		if (stop) {
			return false;
		}
		if ((infoflags & FRAMEBITS) != 0) {
			try {
				Thread.sleep(150);
			} catch (Exception e) {
			}

			repaint(x, y, width, height);
		}
		return true;
		// /////
		/*
		 * if ((flags & (FRAMEBITS|ALLBITS)) != 0) repaint (); else if ((flags &
		 * SOMEBITS) != 0) { //if (incrementalDraw) { if (redrawRate != null) {
		 * long tm = redrawRate.longValue(); if (tm < 0) tm = 0; repaint (tm); }
		 * else repaint (100); } } return (flags & (ALLBITS|ABORT|ERROR)) == 0;
		 */

	}

	public void stop() {
		this.stop = true;
	}

	public void go() {
		this.stop = false;
		repaint();
	}

	public boolean stopped() {
		return this.stop;
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(image.getWidth(this), image.getHeight(this));
	}

	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
}
