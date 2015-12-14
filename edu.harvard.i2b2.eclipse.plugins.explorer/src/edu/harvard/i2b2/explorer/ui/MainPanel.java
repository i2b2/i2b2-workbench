/*
 * Copyright (c)  2006-2012 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *		
 */

package edu.harvard.i2b2.explorer.ui;

import java.awt.*;

import edu.harvard.i2b2.timeline.lifelines.LoadRecord;
import edu.harvard.i2b2.timeline.lifelines.LowerBar;
import edu.harvard.i2b2.timeline.lifelines.MonthSlider;
import edu.harvard.i2b2.timeline.lifelines.MyDate;
import edu.harvard.i2b2.timeline.lifelines.NewApplet;
import edu.harvard.i2b2.timeline.lifelines.PicPanel;
import edu.harvard.i2b2.timeline.lifelines.Record;
import edu.harvard.i2b2.timeline.lifelines.Slider;
import edu.harvard.i2b2.timeline.lifelines.UpperBar;
import edu.harvard.i2b2.timeline.lifelines.WeekSlider;
import edu.harvard.i2b2.timeline.lifelines.YearSlider;

public class MainPanel extends Panel {

	public static UpperBar upBar;
	public static LowerBar lowBar;
	public static TimeLinePanel theTimeLinePanel;
	public static YearSlider theYearSlider;
	public static MonthSlider theMonthSlider;
	public static WeekSlider theWeekSlider;
	private int width, height, sliderWidth, sliderOffset;
	private Button preferButton;
	private MyDate dateMin, dateMax;
	private MyDate today;
	private PicPanel logoPicPanel;/*
								 * Added by Partha - 10/15 this is the lifelines
								 * logo
								 */
	private PicPanel clogPicPanel;/*
								 * Added by Partha - 11/12 this is the copyright
								 * label
								 */
	private PicPanel hlogPicPanel;/*
								 * Added by Partha - 11/12 this is the hcil logo
								 */

	private Record theApplet;
	public Scrollbar tlpScroll; // dan 3/26/98 timeLinePanel scrollBar

	public static TimeLinePanel anotherTimeLinePanel;

	public void init() {
		theTimeLinePanel.init();
		// theTimeLinePanel.paint(theTimeLinePanel.getOfg());
		// System.out.println("theTimeLinePanel.height = " +
		// theTimeLinePanel.height);
		// System.out.println("theTimeLinePanel.dataheight = " +
		// theTimeLinePanel.getTimeLinePanelDataheight());
		// tlpScroll = new Scrollbar(Scrollbar.VERTICAL,0,height-24,0,13209);
		// tlpScroll = new
		// Scrollbar(Scrollbar.VERTICAL,0,height-24,0,theTimeLinePanel.getDrawnHeight());
		// tlpScroll.reshape(width-12-thumbWidth,43,20,height - 67);
		// tlpScroll.reshape(width-12-20,43,20,height - 67);
		// add(tlpScroll);
	}

	public MainPanel(int width, int height, Record theApplet, MyDate today) {
		this.width = width;
		this.height = height;

		this.today = today;
		this.theApplet = theApplet;

		logoPicPanel = new PicPanel(90, 19, theApplet, "images/lifelogo1.JPG");
		// *add(logoPicPanel);
		logoPicPanel.setBounds(3, 0, 90, 19);
		hlogPicPanel = new PicPanel(26, 19, theApplet, "images/hclogo11a.GIF");
		// *add(hlogPicPanel);
		hlogPicPanel.setBounds(width - 290, 0, 26, 19);
		SymMouse aSymMouse = new SymMouse();
		logoPicPanel.addMouseListener(aSymMouse);
		hlogPicPanel.addMouseListener(aSymMouse);
		clogPicPanel = new PicPanel(256, 19, theApplet, "images/copylogo1.JPG");
		// *add(clogPicPanel);
		clogPicPanel.setBounds(width - 260, 0, 256, 19);/*
														 * changed 7 to 0 to
														 * save space
														 */
		clogPicPanel.addMouseListener(aSymMouse);

		dateMin = LoadRecord.getMinDate();
		dateMax = LoadRecord.getMaxDate();

		int thumbWidth = 20;
		boolean doublePanels = false;

		if (!doublePanels) {
			theTimeLinePanel = new TimeLinePanel(width - 22 - thumbWidth,
					height - 62, theApplet, today);
			// theTimeLinePanel = new timeLinePanel(width-22 - thumbWidth+200,
			// height - 62,theApplet,today);
			add(theTimeLinePanel);
			theTimeLinePanel.setBounds(10, 36, width - 22 - thumbWidth,
					height - 62);
			// theTimeLinePanel.setBounds(10, 36, width-22- thumbWidth+200,
			// height - 62);
		}

		// snm - i think the problem is here
		// the problem is that the paint method theTimeLinePanel.paint() has not
		// run yet
		// but to run it needs a graphics handle

		// System.out.println("theTimeLinePanel.dataheight = " +
		// theTimeLinePanel.getTimeLinePanelDataheight());
		tlpScroll = new Scrollbar(Scrollbar.VERTICAL, 0, height - 74, 0, 23209);
		// tlpScroll = new
		// Scrollbar(Scrollbar.VERTICAL,0,height-24,0,(height-24)*20);
		// tlpScroll = new
		// Scrollbar(Scrollbar.VERTICAL,0,height-24,0,theTimeLinePanel.getDrawnHeight());
		tlpScroll.setBounds(width - 12 - thumbWidth, 43, 20, height - 67);
		// tlpScroll.setBounds(width-12-thumbWidth+200,43,20,height - 67);
		tlpScroll.setUnitIncrement(20);
		add(tlpScroll);
		tlpScroll.setVisible(false);

		if (doublePanels) {
			theTimeLinePanel = new TimeLinePanel(width / 2 - 22 - thumbWidth,
					height - 103, theApplet, today);
			add(theTimeLinePanel);
			theTimeLinePanel.setBounds(10, 43, width / 2 - 22 - thumbWidth,
					height - 103);

			anotherTimeLinePanel = new TimeLinePanel(width / 2 - 22
					- thumbWidth, height - 103, theApplet, today);
			add(anotherTimeLinePanel);
			anotherTimeLinePanel.setBounds(width / 2 - 22 - thumbWidth + 10
					+ 30, 43, width / 2 - 22 - thumbWidth, height - 103);
		}

		sliderWidth = theTimeLinePanel.getWidth() + 2 * thumbWidth;
		sliderOffset = theTimeLinePanel.getOffset();

		upBar = new UpperBar(theTimeLinePanel.getWidth(), 22, today);
		add(upBar);
		upBar.setBounds(sliderOffset + 10, 19, theTimeLinePanel.getWidth(), 20);

		theWeekSlider = new WeekSlider(sliderWidth, 18, today);
		theWeekSlider.setBounds(sliderOffset + 10 - thumbWidth, height - 59,
				sliderWidth, 18);

		theMonthSlider = new MonthSlider(sliderWidth, 18, today);
		theMonthSlider.setBounds(sliderOffset + 10 - thumbWidth, height - 41,
				sliderWidth, 18);

		theYearSlider = new YearSlider(sliderWidth, 18, today);
		add(theYearSlider);
		theYearSlider.setBounds(sliderOffset + 10 - thumbWidth, height - 23,
				sliderWidth, 18);
		preferButton = new Button("Zoom"); // for zoom button
	}

	public static void setApplet(NewApplet inApplet) {
		TimeLinePanel.setApplet(inApplet);
		Slider.setApplet(inApplet);
		Slider.setApplet(inApplet);
		Slider.setApplet(inApplet);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.white); // was lightGray, no effect
		g.draw3DRect(0, 0, width - 1, height - 1, true);
		g.draw3DRect(1, 1, width - 3, height - 3, true);
	}

	int iMaxDataheight = 0;

	@Override
	public boolean handleEvent(Event event) {
		// snm - this sets the proper max after it exists
		if (event.target.equals(tlpScroll)) {
			theTimeLinePanel.repaint();
			// System.out.println("theTimeLinePanel.dataheight = " +
			// theTimeLinePanel.getTimeLinePanelDataheight());
			int iDataheight = theTimeLinePanel.getTimeLinePanelDataheight() - 50;
			if (iMaxDataheight < iDataheight) {
				iMaxDataheight = iDataheight;
				tlpScroll.setMaximum(iMaxDataheight);
			}
		}

		return super.handleEvent(event);
	}

	class SymMouse extends java.awt.event.MouseAdapter {
		@Override
		public void mouseClicked(java.awt.event.MouseEvent event) {
			Object object = event.getSource();
			if (object == hlogPicPanel)
				panel2_MouseClick(event);
			if (object == clogPicPanel)
				panel1_MouseClick(event);
			if (object == logoPicPanel)
				panel3_MouseClick(event);
		}
	}

	void panel2_MouseClick(java.awt.event.MouseEvent event) {
		this.theApplet.showDocument("http://www.cs.umd.edu/hcil");
	}

	void panel1_MouseClick(java.awt.event.MouseEvent event) {
		this.theApplet.showDocument("http://www.umd.edu");
	}

	void panel3_MouseClick(java.awt.event.MouseEvent event) {
		this.theApplet
				.showDocument("http://www.cs.umd.edu/hcil/Research/1997/patientrecord.html");
	}

	@Override
	public boolean action(Event e, Object arg) {

		int rWinOffset = theTimeLinePanel.getRwinOffset();
		return true;
	}

	public void resetYearSlider() {

		int thumbWidth = 20;

		remove(theYearSlider);
		theYearSlider = new YearSlider(sliderWidth, 18, today);
		add(theYearSlider);
		theYearSlider.setBounds(sliderOffset + 10 - thumbWidth, height - 23,
				sliderWidth, 18);
	}
}
