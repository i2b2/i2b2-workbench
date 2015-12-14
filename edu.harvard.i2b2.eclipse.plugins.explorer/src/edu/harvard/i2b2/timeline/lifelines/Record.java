/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *		Shawn Murphy, MD, PH.D (MGH)
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;

import javax.swing.JPanel;

import edu.harvard.i2b2.explorer.ui.MainPanel;
import edu.harvard.i2b2.explorer.ui.TimelineSearchFrame;

// this is the top level class
@SuppressWarnings("serial")
public class Record extends JPanel implements NewApplet {// , Runnable{
// public class record extends Applet implements newApplet,Runnable{
	public static final int SILPIXEL = 2;
	static public boolean noRects = false;

	static boolean yearFirst = true; // just static so can reference in MyDate
	// and loadRecord. Could use access function

	// booleans for facet and facetLine read in as parameters in init.. added
	// 1/6/98

	static boolean[] option = new boolean[5];
	static boolean[] lbloption = new boolean[3];
	public static boolean excentric; // is not included in lbloption because it is
	// dynamic
	public static boolean[] column = new boolean[2];
	public static boolean angle_label, infotip;
	static boolean[] timeoption = new boolean[2];
	static boolean[] arrowoption = new boolean[2];
	static boolean[] lengthoption = new boolean[2];
	static boolean[] summaryoption = new boolean[2];
	static boolean searchoption, symbol, vague_association;
	static boolean[] searchoption_timeline = new boolean[2];
	static boolean[] searchoption_label = new boolean[2];
	static int lbllength = 16;
	// these booleans are read from the html file in init() (see that function
	// for
	// parameter names

	static boolean comments = false;
	// static boolean openFacets = true; 3/28/98

	// end booleans for facet and facetLine

	Thread theThread;
	static public boolean changed = true;
	static public boolean threadTest = false;

	public static String oldLabel = new String(" ");
	// newapplet allows status bar changes and opens new browser windows
	// needed so that non Applet classes can call Applet methods?
	public static LoadRecord theData;
	public static MainPanel theTabPanel;
	// tabpanel at present only holds a timelinepanel, eventually can hold
	// several tabbed sheets

	protected static Font font = new Font("TimesRoman", Font.BOLD, 12);
	protected FontMetrics fontMetrics = getFontMetrics(font);
	private Color bgColor, fgColor, hlColor, textColor;
	Panel topPanel; // the container panel
	InfoPanel theInfoPanel; // name etc, middle panel of top of toppanel
	AlertPanel theAlertPanel; // top right panel
	PicPanel thePicPanel; // top right left of toppanel, spot to put a picture
	CurrPanel theCurrPanel; // bottom left panel
	LabelPanel theLabelPanel;
	ScrollPane pane;/* Added 09-13-98 by Partha */

	// int resizeWidth; // width to be used when a resize is done... read from a
	// parameter
	// int resizeHeight; // same for height...

	public Record() {

	}

	// public void start() {
	// if(theThread == null)
	// theThread = new Thread(this);
	// theThread.start();
	// System.out.println("got to start");
	// }

	/*
	 * public void run() { System.out.println("got to run");
	 * Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	 * 
	 * while(Thread.currentThread() == theThread) { if(changed && threadTest) {
	 * theTabPanel.theTimeLinePanel.repaint(); changed = false; } try {
	 * Thread.sleep(1); } catch (InterruptedException e) {
	 * System.out.println("thread exception"); } } }
	 * 
	 * public void stop() { theThread = null; if (theCurrPanel.ctrlpanel !=
	 * null) theCurrPanel.ctrlpanel.setVisible(false); }
	 */

	public void update(Graphics g) {
		paint(g);
	}

	public boolean getParm(String parameter) {
		String tempString = getParameter(parameter);
		if (tempString.equals("true"))
			return true;
		else
			return false;
	}

	public void init() {
		init(null);
	}

	public void init(String recordData) {
		System.out.println("got to init");
		option[0] = getParm("default");
		option[1] = getParm("quick_compact");
		option[2] = getParm("slow_compact");
		option[3] = getParm("time_ordered");
		option[4] = getParm("agg_ordered");

		comments = getParm("comments");

		lbloption[0] = getParm("top_right");
		lbloption[1] = getParm("top_right_left");
		lbloption[2] = getParm("4corners");

		excentric = getParm("excentric");

		column[0] = getParm("one_column");
		column[1] = getParm("two_columns");

		timeoption[0] = getParm("delay_time");
		timeoption[1] = getParm("real_time");

		arrowoption[0] = getParm("no_arrow");
		arrowoption[1] = getParm("arrow");

		lengthoption[0] = getParm("fix_length");
		lengthoption[1] = getParm("variable_length");

		summaryoption[0] = getParm("summarylabel");
		summaryoption[1] = getParm("summarytimeline");

		searchoption_timeline[0] = getParm("timeline_gray");
		searchoption_timeline[1] = getParm("timeline_remove");

		searchoption_label[0] = getParm("label_gray");
		searchoption_label[1] = getParm("label_remove");

		symbol = getParm("symbol");
		vague_association = getParm("vague_association");
		angle_label = getParm("angle_label");
		infotip = getParm("infotip");

		// resizeWidth = Integer.parseInt(getParameter("width"));
		// resizeHeight = Integer.parseInt(getParameter("height"));

		String dataString = getParameter("thedata");

		String colorString = getParameter("bgcolor");

		if (colorString != null) {
			Long theInt = Long.valueOf(colorString, 16);
			bgColor = new Color(theInt.intValue());
		} else
			bgColor = Color.lightGray;
		setBackground(bgColor);

		if ((colorString = getParameter("fgcolor")) != null) {
			Long theInt = Long.valueOf(colorString, 16);
			fgColor = new Color(theInt.intValue());
		} else
			fgColor = Color.black;
		setForeground(fgColor);

		// where is "highlight?" color used?
		if ((colorString = getParameter("hlcolor")) != null) {
			Long theInt = Long.valueOf(colorString, 16);
			hlColor = new Color(theInt.intValue());
		} else
			hlColor = Color.blue;

		if ((colorString = getParameter("textcolor")) != null) {
			Long theInt = Long.valueOf(colorString, 16);
			textColor = new Color(theInt.intValue());
		} else
			textColor = Color.black;

		if (recordData == null) {
			String datafile = getParameter("datafile");
			if (datafile == null)
				// datafile =
				// "http://www.cs.umd.edu/projects/hcil/Research throw /1997/dandemo/width.old";
				System.out.println("got a null pointer for datafile");
			else
				try {
					String appDirectory = System.getProperty("user.dir")
							.toString()
							+ "/temp/";
					datafile = "file:///" + appDirectory + File.separator
							+ datafile;
					System.out.println(datafile);
				} catch (Exception e) {
				}
			// datafile = getCodeBase() + datafile; // why did this stop working
			// for a url? or did it?

			theData = new LoadRecord(datafile, dataString);
		} else {
			try {
				BufferedReader br = new BufferedReader(new StringReader(
						recordData));
				theData = new LoadRecord(br, dataString);
				br.close();
				br = null;
			} catch (Exception e) {
				System.out.println("an exception " + e);
			}
		}
		// does this one make a difference? All the others panels are in
		// toppanel

		setLayout(null);

		Dimension pd = getParent().getSize();
		// pd.setSize(pd.getWidth(), pd.getHeight());
		setSize(pd);

		// no layout is set, so reshapes are not needed to get anything to
		// display
		// if a layout is set... use preferredsize or some other solution (like
		// a good layout manager?)
		// presently there is no layout set (two lines above)

		// panel for picture (upper left)
		thePicPanel = new PicPanel((int) (getSize().width * 0.075),
				(int) (getSize().height * 0.1), this, theData.getPictureFile());
		// add(thePicPanel);
		thePicPanel.setBounds(0, 0, (int) (getSize().width * 0.075),
				(int) (getSize().height * 0.1));

		// panel with personal info on patient middle top of applet
		theInfoPanel = new InfoPanel((int) (getSize().width * 0.30),
				(int) (getSize().height * 0.1), theData.getName(), theData
						.getGender(), theData.getAge(), theData.getMoreInfo()); // 3/28/98
		theInfoPanel.setLayout(null);
		// add(theInfoPanel);
		theInfoPanel.setBounds((int) (getSize().width * 0.075), 0,
				(int) (getSize().width * 0.30), (int) (getSize().height * 0.1));

		// just below infopanel... presently empty
		theAlertPanel = new AlertPanel((int) (getSize().width * 0.30),
				(int) (getSize().height * 0.05));
		theAlertPanel.setLayout(null);
		// add(theAlertPanel);
		theAlertPanel.setBounds((int) (getSize().width * 0.075),
				(int) (getSize().height * .05), (int) (getSize().width * 0.30),
				(int) (getSize().height * 0.05));

		// top right
		pane = new ScrollPane();
		pane.setSize((int) (getSize().width * 0.6),
				(int) (getSize().height * 0.1));
		theLabelPanel = new LabelPanel((int) (getSize().width * 0.6),
				(int) (getSize().height * 0.1));
		theLabelPanel = new LabelPanel(800, 400);
		theLabelPanel.setLayout(null);
		// add(theLabelPanel); // snm
		theLabelPanel.setBounds((int) (getSize().width * 0.375), 0,
				(int) (getSize().width * 0.6), (int) (getSize().height * 0.1));
		theLabelPanel.setBounds(500, 0, 800, 400);
		pane.add(theLabelPanel);
		// add(pane); //snm
		pane.setBounds((int) (getSize().width * 0.375), 0,
				(int) (getSize().width * 0.6), (int) (getSize().height * 0.1));

		// left bottom of toppanel
		theCurrPanel = new CurrPanel((int) (getSize().width), 40, this);
		theCurrPanel.setLayout(null);
		// add(theCurrPanel); // snm comment out
		theCurrPanel.setBounds(0, 0, (int) getSize().width, 40);

		// eventually will have multiple tabs (now just lifeline) use card
		// layout perhaps? (and
		// a little bit of "tab" graphics)
		theTabPanel = new MainPanel((int) getSize().width,
				(int) (getSize().height), this, LoadRecord.getToday());
		theTabPanel.setLayout(null);
		add(theTabPanel); // snm comment out
		theTabPanel.setBounds(0, 0, (int) getSize().width,
				(int) (getSize().height));/* modified 12/02 - Partha */
		// tabpanel actually goes to www for data within timelinepanel, actually
		// this
		// method sets the applet for the timelinepanel (so can use showstatus,
		// showdocument,
		// newapplet methods... actually applet methods, but are using inside a
		// non applet
		// derived class
		theTabPanel.setApplet(this);
		// theTabPanel.init(); // needed?
	}

	// * inherited from newApplet interface
	public void showStatus(String aStatus) {
		// * getAppletContext().showStatus(aStatus);
	}

	// * inherited from newApplet interface
	public void showDocument(String theURL) {
		try {
			// URL anURL = new URL(theURL);
			// getAppletContext().showDocument(anURL,"_blank");
			// * getAppletContext().showDocument(anURL,"View");
		} catch (Exception e) {
			System.out.println("Exception while opening URL " + theURL + " : "
					+ e);
		}
	}

	public void resetTabPanel() {
		remove(theTabPanel);
		theTabPanel = new MainPanel((int) (getSize().width * 0.98),
				(int) (getSize().height * 0.84), this, LoadRecord.getToday());
		theTabPanel.setLayout(null);
		add(theTabPanel);
		theTabPanel
				.setBounds(0, (int) (getSize().height * 0.1),
						(int) (getSize().width * 0.98),
						(int) (getSize().height * 0.84));
		// tabpanel actually goes to www for data within timelinepanel, actually
		// this
		// method sets the applet for the timelinepanel (so can use showstatus,
		// showdocument,
		// newapplet methods... actually applet methods, but are using inside a
		// non applet
		// derived class
		theTabPanel.setApplet(this);
		theTabPanel.resetYearSlider();
	}

	public void resetPicPanel() {
		remove(thePicPanel);
		thePicPanel = new PicPanel((int) (getSize().width * 0.075),
				(int) (getSize().height * 0.1), this, theData.getPictureFile());
		// panel that will hold a picture of the patient, resides in upper left
		// corner of applet
		add(thePicPanel);
		thePicPanel.setBounds(0, 0, (int) (getSize().width * 0.075),
				(int) (getSize().height * 0.1));
		thePicPanel.repaint();
		thePicPanel.validate();
		this.validate();
	}

	public void resetInfoPanel() {
		remove(theInfoPanel);

		theInfoPanel = new InfoPanel((int) (getSize().width * 0.30),
				(int) (getSize().height * 0.1), theData.getName(), theData
						.getGender(), theData.getAge(), theData.getMoreInfo()); // 3/28/98
		theInfoPanel.setLayout(null);
		add(theInfoPanel);
		theInfoPanel.setBounds((int) (getSize().width * .075), 0,
				(int) (getSize().width * 0.30), (int) (getSize().height * 0.1));
	}

	public void showLabel(String label) {
		theLabelPanel.setLabel(label);
		if (!(oldLabel.equals(label))) {
			theLabelPanel.repaint();
		}
		oldLabel = new String(label);
	}

	public void setWidthHeight(int width, int height) {
		setWH(width, height);
	}

	public void showSearchFrame() {
		final Record r = this;
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TimelineSearchFrame(r).setVisible(true);
			}
		});
	}

	public void setWH(int width, int height) {

		// I needed this function because compiler complained that I couldn't
		// call
		// resize from a static method (which it declared the one in newApplet
		// to be)
		// setSize(resizeWidth, resizeHeight);
	}

	private String getParameter(String requestedString) {
		if (requestedString.equalsIgnoreCase("bgcolor"))
			return "ffffff";
		if (requestedString.equalsIgnoreCase("bgcolor"))
			return "ffffff";
		if (requestedString.equalsIgnoreCase("bgcolor"))
			return "ffffff";
		if (requestedString.equalsIgnoreCase("fgcolor"))
			return "000000";
		if (requestedString.equalsIgnoreCase("hlcolor"))
			return "99ccff";
		if (requestedString.equalsIgnoreCase("textcolor"))
			return "000000";
		if (requestedString.equalsIgnoreCase("datafile"))
			return "i2b2xml.lld";
		if (requestedString.equalsIgnoreCase("quick_compact"))
			return "false"; // ?
		if (requestedString.equalsIgnoreCase("slow_compact"))
			return "false"; // vertically optimizes layout for compactness
		if (requestedString.equalsIgnoreCase("time_ordered"))
			return "false"; // eveything has its own line and arranged cascading
		// down
		if (requestedString.equalsIgnoreCase("agg_ordered"))
			return "true"; // every different concept has it's own line
		if (requestedString.equalsIgnoreCase("comments"))
			return "false";
		if (requestedString.equalsIgnoreCase("top_right"))
			return "true";
		if (requestedString.equalsIgnoreCase("top_right_left"))
			return "false"; // labels start on left?
		if (requestedString.equalsIgnoreCase("4corners"))
			return "false";
		if (requestedString.equalsIgnoreCase("no_arrow"))
			return "true";
		if (requestedString.equalsIgnoreCase("arrow"))
			return "false"; // line connects label to bar
		if (requestedString.equalsIgnoreCase("delay_time"))
			return "true";
		if (requestedString.equalsIgnoreCase("real_time"))
			return "false";
		if (requestedString.equalsIgnoreCase("variable_length"))
			return "true";
		if (requestedString.equalsIgnoreCase("fix_length"))
			return "false";
		if (requestedString.equalsIgnoreCase("summarylabel"))
			return "true"; // bold labels
		if (requestedString.equalsIgnoreCase("summarytimeline"))
			return "true";
		if (requestedString.equalsIgnoreCase("label_only_result"))
			return "false";
		if (requestedString.equalsIgnoreCase("timeline_gray"))
			return "true";
		if (requestedString.equalsIgnoreCase("timeline_remove"))
			return "false";
		if (requestedString.equalsIgnoreCase("label_gray"))
			return "true";
		if (requestedString.equalsIgnoreCase("label_remove"))
			return "false";
		if (requestedString.equalsIgnoreCase("symbol"))
			return "false";
		if (requestedString.equalsIgnoreCase("vague_association"))
			return "false";
		if (requestedString.equalsIgnoreCase("angle_label"))
			return "false"; // makes blank?
		if (requestedString.equalsIgnoreCase("infotip"))
			return "true"; // Tooltip hovering over bar
		if (requestedString.equalsIgnoreCase("excentric"))
			return "false"; // very cool label attached under box
		if (requestedString.equalsIgnoreCase("width"))
			return "700";
		if (requestedString.equalsIgnoreCase("height"))
			return "685";

		System.out.println("got here");
		return "false";
	}

	public String getCodeBase() {
		return "";
	}
}
