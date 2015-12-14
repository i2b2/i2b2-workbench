/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */

package edu.harvard.i2b2.patientMapping.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WaitPanel extends javax.swing.JPanel implements Runnable {

	int patientNum = 1;
	String estimatedTimeStr = "00 : 00";
	GIFComponent comp;
	GIFPanel gpanel;
	boolean stop = false;
	int elapsedTime = 0;
	int elapsedMinute = 0;
	javax.swing.JLabel topLabel;
	javax.swing.JLabel topLabel1;
	javax.swing.JLabel estimateTimeLabel;
	javax.swing.JLabel elapsedTimeLabel;
	javax.swing.JLabel secondLabel;
	javax.swing.JLabel minuteLabel;
	javax.swing.JLabel seperateLabel;
	private static final Log log = LogFactory.getLog(WaitPanel.class);

	/** Creates new form WaitPanel */
	public WaitPanel(int width, int height, int patients) {
		initComponents();
		patientNum = patients;

		if (patientNum >= 1000) {
			this.estimatedTimeStr = "00 : 20";
		} else if ((patientNum < 1000) && (patientNum >= 500)) {
			this.estimatedTimeStr = "00 : 10";
		} else if ((patientNum < 500) && (patientNum >= 200)) {
			this.estimatedTimeStr = "00 : 06";
		} else {
			this.estimatedTimeStr = "00 : 02";
		}

		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentMoved(java.awt.event.ComponentEvent evt) {
				// formComponentMoved(evt);
			}

			@Override
			public void componentResized(java.awt.event.ComponentEvent evt) {
				// formComponentResized(evt);
				// log.debug("waiting panel resizing ...");
				int width = (int) (getParent().getWidth() * 0.40);
				int height = (int) (getParent().getHeight() * 0.40);
				if (comp != null) {
					comp.setBounds(width, height, 110, 110);
					topLabel.setBounds(width - 100, height - 70, 250, 40);
					topLabel1.setBounds(width - 100, height - 50, 400, 40);
					elapsedTimeLabel.setBounds(width - 50, height + 120, 260,
							40);
					estimateTimeLabel.setBounds(width - 50, height + 140, 250,
							40);
				}
			}
		});
	}

	public void init(int width, int height) {
		if (width <= 0 || height <= 0) {
			width = 273;
			height = 144;
		}

		setBackground(java.awt.Color.WHITE);
		java.awt.Image img = this.getToolkit().getImage(
				WaitPanel.class.getResource("waiting.gif"));
		comp = new GIFComponent(img);
		add(comp);
		comp.setBounds(width, height, 110, 110);
		comp.go();

		Font currentFont = comp.getFont();
		Font thisFont = new Font(currentFont.getName(), Font.BOLD, 16);

		topLabel = new javax.swing.JLabel();
		add(topLabel);
		topLabel.setBounds(width - 100, height - 70, 250, 40);
		topLabel.setText("Loading patient set .......");
		topLabel.setFont(thisFont);

		topLabel1 = new javax.swing.JLabel();
		add(topLabel1);
		topLabel1.setBounds(width - 100, height - 50, 400, 40);
		topLabel1
				.setText("Click on the \"Cancel\" button below to abort");
		topLabel1.setFont(thisFont);

		elapsedTimeLabel = new javax.swing.JLabel();
		add(elapsedTimeLabel);
		elapsedTimeLabel.setBounds(width - 50, height + 120, 260, 40);
		elapsedTimeLabel.setText("Elapsed Time  =   00 : 00");
		elapsedTimeLabel.setFont(thisFont);

		/*
		 * minuteLabel = new javax.swing.JLabel(); add(minuteLabel);
		 * minuteLabel.setBounds(width+75, height+120, 25, 40);
		 * minuteLabel.setText("00"); minuteLabel.setFont(thisFont);
		 * 
		 * seperateLabel = new javax.swing.JLabel(); add(seperateLabel);
		 * seperateLabel.setText(":"); seperateLabel.setBounds(width+100,
		 * height+120, 10, 40); seperateLabel.setFont(thisFont);
		 * 
		 * secondLabel = new javax.swing.JLabel(); add(secondLabel);
		 * secondLabel.setBounds(width+112, height+120, 25, 40);
		 * secondLabel.setText("00"); secondLabel.setFont(thisFont);
		 */
		estimateTimeLabel = new javax.swing.JLabel();
		//add(estimateTimeLabel);
		estimateTimeLabel.setBounds(width - 50, height + 140, 250, 40);
		estimateTimeLabel.setText("Estimated Time = " + this.estimatedTimeStr);
		estimateTimeLabel.setFont(thisFont);

		new Thread(this).start();
	}

	public void run() {
		// Thread thisThread = Thread.currentThread();
		String min = "00";
		String sec = "00";

		while (true) {
			if (stop) {
				return;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			elapsedTime++;
			if (elapsedTime == 60) {
				elapsedTime = 0;
				elapsedMinute++;
				sec = "00";

				if (elapsedMinute < 10) {
					min = "0" + new Integer(elapsedMinute).toString();
					// minuteLabel.setText("0"+new
					// Integer(elapsedMinute).toString());
				} else {
					min = new Integer(elapsedMinute).toString();
					// minuteLabel.setText(new
					// Integer(elapsedMinute).toString());
				}
			} else if (elapsedTime < 10) {
				sec = "0" + new Integer(elapsedTime).toString();
				// secondLabel.setText("0"+new Integer(elapsedTime).toString());
			} else {
				sec = new Integer(elapsedTime).toString();
				// secondLabel.setText(new Integer(elapsedTime).toString());
			}
			elapsedTimeLabel.setText("Elapsed Time  =   " + min + " : " + sec);
			// log.debug("Elapsed time: "+elapsedTime);
		}
	}

	public void stop() {
		this.stop = true;
		this.comp.stop();
	}

	public void go() {
		this.stop = false;
		this.comp.go();
	}

	private void initComponents() {
		setLayout(null);
	}
}

/*
 * class GIFComponent extends Component { java.awt.Image image; boolean stop =
 * false; long curtime = System.currentTimeMillis();
 * 
 * public GIFComponent(java.awt.Image image) { super(); this.image = image;
 * MediaTracker mt = new MediaTracker(this); mt.addImage(image, 0); try {
 * mt.waitForID(0); } catch (Exception e){e.printStackTrace();} }
 * 
 * 
 * public void paint(Graphics g) { g.drawImage(image, 0, 0, this); }
 * 
 * public void update(Graphics g) { paint(g); }
 * 
 * public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y,
 * int width, int height) {
 * 
 * if (stop) { return false; } if ( (infoflags & FRAMEBITS) != 0) { try {
 * Thread.sleep(150); } catch(Exception e) {}
 * 
 * repaint(x, y, width, height); } return true; /////// /* if ((flags &
 * (FRAMEBITS|ALLBITS)) != 0) repaint (); else if ((flags & SOMEBITS) != 0) {
 * //if (incrementalDraw) { if (redrawRate != null) { long tm =
 * redrawRate.longValue(); if (tm < 0) tm = 0; repaint (tm); } else repaint
 * (100); } } return (flags & (ALLBITS|ABORT|ERROR)) == 0;
 */

/*
 * }
 * 
 * 
 * public void stop() { this.stop = true; }
 * 
 * 
 * public void go() { this.stop = false; repaint(); }
 * 
 * 
 * public boolean stopped() { return this.stop; }
 * 
 * 
 * public Dimension getMinimumSize() { return new
 * Dimension(image.getWidth(this), image.getHeight(this)); }
 * 
 * public Dimension getPreferredSize() { return getMinimumSize(); } }
 */

class TimeCounter implements Runnable {
	// private long start_;
	private int e_time = 0;

	public TimeCounter() {
		new Thread(this).start();
	}

	public void run() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		e_time = 1;
	}

	public int getElapsedTime() {
		return e_time;
	}
}

class GIFPanel extends javax.swing.JPanel implements Runnable {
	java.awt.Image img;
	private static final Log log = LogFactory.getLog(GIFPanel.class);

	float current = 0F;
	Thread runner;
	int xPosition = 100;
	int xMove = 1;
	int yPosition = 100;
	int imgHeight = 185;
	int imgWidth = 190;
	int height;
	TimeCounter timer;
	int elapsedTime = 0;
	int drawtime = 0;
	boolean candraw = false;

	public GIFPanel(java.awt.Image img) {
		super();
		Toolkit kit = Toolkit.getDefaultToolkit();

		this.img = kit.getImage("waiting.gif");
		runner = new Thread(this);
		// timer = new TimeCounter();
		runner.start();
	}

	@Override
	public void paintComponent(Graphics comp) {
		Graphics2D comp2D = (Graphics2D) comp;
		// height = getSize().height - imgHeight;
		// if (yPosition == -1)
		// yPosition = height - 20;
		// int cur = timer.getElapsedTime();
		log.debug("In paint drawtime: " + drawtime);
		log.debug("In paint elapsed time: " + elapsedTime);
		if (drawtime == elapsedTime) {
			candraw = true;
		}

		if (img != null && candraw) {
			// elapsedTime = cur;
			comp2D.drawImage(img, xPosition, yPosition, this);
			drawtime++;
			candraw = false;
		}
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		while (true) {
			// (runner == thisThread) {
			// current += (float) 0.1;
			// if (current > 3)
			// current = (float) 0;
			// xPosition += xMove;
			// if (xPosition > (getSize().width - imgWidth))
			// xMove *= -1;
			// if (xPosition < 1)
			// xMove *= -1;
			// double bounce = Math.sin(current) * height;
			// yPosition = (int) (height - bounce);
			// if(timer.getElapsedTime() == 1) {
			repaint();
			// }
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			elapsedTime++;
			log.debug("Elapsed time: " + elapsedTime);
		}
	}
}
