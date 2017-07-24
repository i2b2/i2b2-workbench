/*
 * Copyright (c)  2006-2017 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *  	Heekyong Park (hpark25) (MGH)
 *	
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Vector;

import edu.harvard.i2b2.explorer.ui.MainPanel;
import edu.harvard.i2b2.explorer.ui.TimeLinePanel;

/**
 * facet class defines a segmentation of events, such as educational, financial,
 * and medical for personal data, or into further specifics such as physician
 * visits, hospitalizations, medications, and lab tests.
 */
public class Facet extends Panel {
	// private String token;
	private Hashtable facetList;
	private Vector facetLines;
	private Vector orig_aggregates;
	private Vector rt_aggregates;
	private Vector aggregates;
	private String title;
	private int maxNumNeighbrs = 9; // maximum number of neighbors to retrieve for pop up 
	// can retrieve up to maxNumNeighbrs neighbor records, 
	// thus showing 10 records in the pop up, including mouse hovered record
	//	private int cntNeighbrs;
	//private int curIndx;
	//private int leftMostIndx, rightMostIndx; // Index of left/right most end of extracted overlap data
		// needed to implement finding next/previous neighboring overlapping ticks 
	private Aggregate curntAggr;
	
	
	
	private int facetLnIndx, aggrIndx;
	
	public int getFacetLnIndx()
	{
		return facetLnIndx;
	}
	

	public int getAggrIndx()
	{
		return aggrIndx;
	}
	
	
	
	
	public String title() {
		return title;
	}
	
	public Aggregate getCurntAggr() {
		return curntAggr;
	}
	
	private Checkbox open;
	public Color backgroundColor; // added 1/12/98 dan: also added to
	// constructor and in draw method
	public boolean enabled; // = record.openFacets; // 1/6/98
	private int enabled_count;
	private int remove_count;
	private Rectangle currentRect;
	
	private String fullName;
	public String fullName() {
		return fullName;
	}
	public void fullName(String str) {
		fullName = new String(str);
	}

	public Rectangle currentRect() {
		return currentRect;
	}
	

	public int maxNumNeighbrs(){
		return maxNumNeighbrs;
	}


	public Facet(String token, Hashtable facetList, Color backgroundColor,
			boolean openFacet) { // last part
		// of argumemt list added 1/12/98 by dan, note Color is used not MyColor
		// (as in loadRecord)
		
		title = token;
		this.backgroundColor = backgroundColor;
		this.enabled = openFacet;
		this.enabled = true;
		this.enabled_count = (enabled) ? 1 : 0;
		this.remove_count = 0;

		this.facetList = facetList;

		orig_aggregates = null;
		rt_aggregates = null;
		aggregates = null;

		open = new Checkbox();
		open.setBounds(2, 2, 10, 10);
		add(open);

		currentRect = new Rectangle(0, 0, 0, 0);
	}

	public String getKey() {
		return title;
	}

	public Hashtable getList() {
		return facetList;
	}

	/**
	 * Store all the events to a vector so that events can be later laid out in
	 * different ways
	 */
	public void addEventObject(GenRecord addThis) {
		if (orig_aggregates == null)
			orig_aggregates = new Vector();
		orig_aggregates.addElement(addThis);
	}

	/**
	 * Define five event layout algorithms
	 */
	@Override
	public void layout() {
		Aggregate addThis = null;
		facetLines = null;

		if (Record.timeoption[1]) {
			if (rt_aggregates == null)
				rt_aggregates = orig_aggregates;
			if (Record.option[3] || Record.option[2])
				aggregates = timeOrder(rt_aggregates);
			else
				aggregates = rt_aggregates;
		} else {
			if (Record.option[3] || Record.option[2])
				aggregates = timeOrder(orig_aggregates);
			else
				aggregates = orig_aggregates;
		}

		if (aggregates != null) {
			for (int v = 0; v < aggregates.size(); v++) {
				addThis = (Aggregate) aggregates.elementAt(v);
				FacetLine temp = null;

				if ((Record.searchoption_timeline[1]) ? addThis.selected : true) {
					if (facetLines == null) {
						if (Record.comments) { // if statement: 1/6/98
							addThis.getStartdate().print();
							System.out.println("new timeline");
						}
						facetLines = new Vector();
						temp = new FacetLine(title, backgroundColor);
						facetLines.addElement(temp);
						if (Record.option[4])
							temp.setLabelString(addThis.getLabelString());
					} else {
						if (Record.option[1] || Record.option[2]) { // compact
							// the
							// timelines
							// to the
							// minimum
							// rows
							int size = facetLines.size(); // need to set this at
							// the beginning
							// because the value
							// can change inside
							// the for loop
							int i = 0;
							forloop: for (i = 0; i < size; i++) {
								temp = (FacetLine) (facetLines.elementAt(i));
								if (temp.fits(addThis)) { // if current event
									// fits the row, it
									// will be added
									if (Record.comments) { // if statement:
										// 1/6/98
										addThis.getStartdate().print();
										System.out.println("fit timeline " + i);
									}
									break forloop;
								}
							}
							if (i == size) { // start a new row
								temp = new FacetLine(title, backgroundColor);
								if (Record.comments) { // if statement added
									// 1/6/98
									addThis.getStartdate().print();
									System.out.println("new timeline");
								}
								facetLines.addElement(temp);
							}
						} else if (Record.option[3]) { // time_ordered
							temp = new FacetLine(title, backgroundColor);
							// if(record.comments) { // if statement added
							// 1/6/98
							if (title.equals("Tests")) {
								addThis.getStartdate().print();
								// System.out.println("new timeline");
							}
							facetLines.addElement(temp);
						} else if (Record.option[4]) { // agg_ordered
							int size = facetLines.size();
							int i = 0;
							forloop: for (i = 0; i < size; i++) {
								temp = (FacetLine) (facetLines.elementAt(i));
								if (!temp.getLabelString().equals("")
										&& temp.getLabelString().equals(
												addThis.getLabelString())) {
									if (temp.fits(addThis)) {
										if (Record.comments) {
											addThis.getStartdate().print();
											System.out.println("fit timeline "
													+ i);
										}
									} else {
										temp = new FacetLine(title,
												backgroundColor);
										if (Record.comments) {
											addThis.getStartdate().print();
											System.out.println("new timeline");
										}
										if (i != size - 1) // not at the last
											// row yet
											facetLines.insertElementAt(temp,
													i + 1);
										else
											facetLines.addElement(temp);
									}
									break forloop;
								}
							}
							if (i == size) { // start a new row
								temp = new FacetLine(title, backgroundColor);
								if (Record.comments) {
									addThis.getStartdate().print();
									System.out.println("new timeline");
								}
								facetLines.addElement(temp);
							}
						} else if (Record.option[0]) { // only see whether the
							// event fits the current
							// row or not
							if (Record.comments)
								System.out.println("normal");
							temp = (FacetLine) (facetLines.lastElement());
							if (!temp.fits(addThis)) {
								temp = new FacetLine(title, backgroundColor); // added
								// back
								// in
								// 12
								// /
								// 23
								// /
								// 97
								// removed
								// for
								// compact
								facetLines.addElement(temp); // start a new row
							}
						}
					}
					temp.addEventObject(addThis); // add the event to the
					// current row
					if (Record.option[4] && addThis.getLabelString() != null)
						temp.setLabelString(addThis.getLabelString());
				}
			} // for loop
		}
	}

	/**
	 * Order events by their starting dates
	 */
	public Vector timeOrder(Vector aggregates) {
		Vector newagg = new Vector();
		GenRecord aggOrdered;
		for (int i = 0; i < aggregates.size(); i++) {
			GenRecord aggThis = (GenRecord) aggregates.elementAt(i);
			boolean insert = false;
			int j = 0;
			while (j < i && !insert) {
				if (j == 0)
					aggOrdered = (GenRecord) aggregates.elementAt(j);
				else
					aggOrdered = (GenRecord) newagg.elementAt(j);
				if (aggThis.getStartdate().before(aggOrdered.getStartdate())) {
					newagg.insertElementAt(aggThis, j);
					insert = true;
				}
				j++;
			}
			if (!insert)
				newagg.addElement(aggThis);
		}
		return newagg;
	}

	/**
	 * Draw the facet on the display
	 */
	public void draw(int currentY, TimeLinePanel displayPanel,
			boolean relabeling, boolean slide, boolean stream, boolean data,
			boolean label) {

		if (enabled) {
			if (Record.timeoption[1] && slide) {
				rt_aggregates = new Vector();
				Aggregate thisagg;
				for (int i = 0; i < orig_aggregates.size(); i++) {
					thisagg = (Aggregate) orig_aggregates.elementAt(i);
					if (!displayPanel.offScale(thisagg)) { // Otherwise, don't
						// consider
						// thisagg.setStartdate(displayPanel.scaleMin((genRecord)
						// thisagg));
						// thisagg.setEnddate(displayPanel.scaleMax((genRecord)
						// thisagg));
						rt_aggregates.addElement(thisagg);
					}
				}

				this.layout();
			}

			if (Record.searchoption_timeline[1]
					&& MainPanel.theTimeLinePanel.search) {
				this.layout();
			}

			FacetLine temp;
			String fontNm="Tahoma"; 
			int gap=3, fontSz=11;
			Graphics g = displayPanel.getOfg();
			Graphics2D g2d = (Graphics2D) g;		    
		    GradientPaint gradient;
			int[] xCoordinates = { 1, 5, 10 };
			int[] yCoordinates = { currentY + 4 + gap, currentY + 12 + gap, currentY + 4 + gap };

			if (facetLines == null) {
				// 3/10/98 need to remove these codes after fitlabel function,
				// height is determined by fitlabel()
				currentRect = new Rectangle(0, currentY, displayPanel
						.getFullWidth(), getHeight() + 12);
				g.setColor(backgroundColor);
				g.fillRect(currentRect.x, currentRect.y, currentRect.width,
						currentRect.height);
				g.setColor(Color.black);
				g.setFont(new Font(fontNm, Font.PLAIN, fontSz));
				g.drawString(fullName, 15, currentY + 12 + gap); 
			} else {
				if (relabeling || enabled_count == 0) { // || (record.
					// searchoption_timeline
					// [1] &&
					// record.theTabPanel
					// .theTimeLinePanel
					// .search)) // 3/28/98
					for (int i = 0; i < facetLines.size(); i++) {
						temp = (FacetLine) (facetLines.elementAt(i));
						// don't consider cross facetlines
						temp.resetlabel();

						temp.fitlbl = temp.fitlabel(currentY, displayPanel,
								false);
					}
					enabled_count++;
				}

				// 3/10/98 need to remove these codes after fitlabel function,
				// height is determined by fitlabel()
				currentRect = new Rectangle(0, currentY, displayPanel
						.getFullWidth(), getHeight() + 5);
				
				Color idBgColor1, idBgColor2, dataBgColor;			
				idBgColor1=Color.decode("0x6a87a2");
				idBgColor2=Color.decode("0x0d2c48");
				dataBgColor=Color.decode("0xf3f3f3");
				
				if(title.startsWith("ID"))
				{
					gradient = new GradientPaint(currentRect.x, currentRect.y, idBgColor1,
					    			currentRect.x, currentRect.y + currentRect.height, idBgColor2);
					g2d.setPaint(gradient);
				    g2d.fill(new Rectangle(currentRect.x, currentRect.y, currentRect.width,currentRect.height));				
					g.setColor(Color.white);
					g.setFont(new Font(fontNm, Font.BOLD, fontSz));
					g.drawString(fullName, 5, currentY + 12 + gap);
				}
				else // concept name display
				{
					g.setColor(dataBgColor);
					g.fillRect(currentRect.x, currentRect.y, currentRect.width,
											currentRect.height);
				    // Show data color rectangle before concept name
					temp = (FacetLine) (facetLines.elementAt(0));
					Aggregate tempAggr = (Aggregate) temp.getAggregates().elementAt(0);
					StoryRecord tempStory = (StoryRecord) (tempAggr.getAllRecords().elementAt(0));
					Color conceptColr = tempStory.getRectColor();				
					g.setColor(conceptColr); 					
					g.fillRect(currentRect.x, currentRect.y + 4, 10, 15);
					
					g.setColor(Color.black);
					g.setFont(new Font(fontNm, Font.PLAIN, fontSz));
					g.drawString(fullName, 14, currentY + 12 + gap);
				}
				
				// show labels even when
				// open, loses some vertical space, maybe use abbreviation here
				// eventually

				for (int i = 0; i < facetLines.size(); i++) {

					temp = (FacetLine) (facetLines.elementAt(i));

					if ((Record.lbloption[1] || Record.lbloption[2])
							&& !temp.fitlbl) {
						// later, adjust timeline
						temp.setSavedLabelXY();
						if (data)
							temp.drawData(currentY + 5, displayPanel, false); 
						if (label)
							temp.drawLabel(currentY, displayPanel, stream);

						currentY += temp.getHeight() + ((temp.below) ? 15 : 7);
						System.out.println("sorry no solution yet");
					} else {
						if (data)
						{
							if(title.startsWith("ID"))
								continue;
							temp.drawData(currentY + 5, displayPanel, false); 
						}
						if (label)
							temp.drawLabel(currentY, displayPanel, stream);

						currentY += temp.getHeight() + ((temp.below) ? 15 : 7);
					}
				}
			}
		} else { // silhoutte
			Graphics g = displayPanel.getOfg();
			currentRect = new Rectangle(0, currentY, displayPanel
					.getFullWidth(), getHeight() + 5);
			g.drawString(title, 15, currentY + 12);

			FacetLine temp;
			if (facetLines != null) { // might be because of sliding
				for (int i = 0; i < facetLines.size(); i++) {
					temp = (FacetLine) (facetLines.elementAt(i));
					if (data)
						temp.drawData(currentY + 5, displayPanel, true); 
					currentY += Record.SILPIXEL;
				}
			}
			g.setColor(Color.black);
		}
	}
	
	/**
	 * Check weather any event contains the point (x,y)
	 */
	@Override
	public boolean contains(int x, int y) {
		if (facetLines == null)
			return false;

		for (int i = 0; i < facetLines.size(); i++) {
			if (((FacetLine) (facetLines.elementAt(i))).contains(x, y))
				return true;
		}
		return false;
	}

	/**
	 * Check whether any event contains the search string
	 */
	public boolean contains(String searchString) {
		FacetLine temp;

		for (int i = 0; i < facetLines.size(); i++) {
			temp = (FacetLine) (facetLines.elementAt(i));

			if (temp.contains(searchString))
				return true;
		}
		return false;
	}

	/**
	 * Returns the event if the point (x,y) falls inside the region of the event
	 * or its corrsponding label Otherwise, return null.
	 */
	public GenRecord inRegion(int x, int y, boolean data, boolean label,
			int distance) {
		double minDis = Integer.MAX_VALUE;
		StoryRecord selectedRecord = null;
		if (facetLines == null) {
			return null;
		}

		for (int i = 0; i < facetLines.size(); i++) {
			FacetLine tempFacetLine = (FacetLine) (facetLines.elementAt(i));
			String title = tempFacetLine.getTitle();
			for (int j = 0; j < tempFacetLine.getAggregates().size(); j++) {
				Aggregate tempAgg = (Aggregate) (tempFacetLine.getAggregates()
						.elementAt(j));
				for (int k = 0; k < tempAgg.getAllRecords().size(); k++) {
					StoryRecord tempStory = (StoryRecord) (tempAgg
							.getAllRecords().elementAt(k));
					Rectangle dataRect = tempStory.getBarArea();
					Rectangle labelRect = tempStory.getLabelArea();
					if ((data && dataRect.contains(x, y))
							|| (label && labelRect.contains(x, y)))
						return tempStory;
					else {
						int selectedX, selectedY;
						if (Math.abs(dataRect.x - x) <= Math.abs(dataRect.x
								+ dataRect.width - x))
							selectedX = dataRect.x;
						else
							selectedX = dataRect.x + dataRect.width;
						if (Math.abs(dataRect.y - y) <= Math.abs(dataRect.y
								+ dataRect.height - y))
							selectedY = dataRect.y;
						else
							selectedY = dataRect.y + dataRect.height;
						double dis = Math.sqrt((selectedX - x)
								* (selectedX - x) + (selectedY - y)
								* (selectedY - y));
						if (dis < minDis) {
							minDis = dis;
							selectedRecord = tempStory;
						}
					}
				}
			}
		}
		if (minDis <= distance)
			return selectedRecord;
		else
			return null;
	}

	public GenRecord[] inOverlapRegion(int x, int y, boolean data, boolean label,
			int distance) {
		double minDis = Integer.MAX_VALUE;
		int gap=1; // maximum gap between two data bars to determine in the overlapped range 
		int cntLtNeighbrs=0; // total number of neighbor records retrieved
		//cntNeighbrs = 0;
		
		boolean findNxtLt = true, findNxtRt = true;
		StoryRecord selectedRecord = null, rtNeighbrStory = null, ltNeighbrStory = null;
		GenRecord[] overlapRecords = new GenRecord[maxNumNeighbrs+1];
		StoryRecord[] ltStoryRecords = new StoryRecord[maxNumNeighbrs];
		StoryRecord[] rtStoryRecords = new StoryRecord[maxNumNeighbrs];
		
		
		if (facetLines == null) {
			return null;
		}

		for (int i = 0; i < facetLines.size(); i++) {
			FacetLine tempFacetLine = (FacetLine) (facetLines.elementAt(i));
			facetLnIndx = i;
			String title = tempFacetLine.getTitle();
			for (int j = 0; j < tempFacetLine.getAggregates().size(); j++) {
				Aggregate tempAgg = (Aggregate) (tempFacetLine.getAggregates().elementAt(j));
				aggrIndx = j;
				for (int k = 0; k < tempAgg.getAllRecords().size(); k++) {
					
					StoryRecord tempStory = (StoryRecord) (tempAgg.getAllRecords().elementAt(k));
					Rectangle dataRect = tempStory.getBarArea();
					Rectangle labelRect = tempStory.getLabelArea();
					if ((data && dataRect.contains(x, y))
							|| (label && labelRect.contains(x, y)))
					{
						curntAggr = tempAgg;
						overlapRecords = curntAggr.findOverlap(k, true, true);
						return overlapRecords;			
					}
					else {
						int selectedX, selectedY;
						if (Math.abs(dataRect.x - x) <= Math.abs(dataRect.x
								+ dataRect.width - x))
							selectedX = dataRect.x;
						else
							selectedX = dataRect.x + dataRect.width;
						if (Math.abs(dataRect.y - y) <= Math.abs(dataRect.y
								+ dataRect.height - y))
							selectedY = dataRect.y;
						else
							selectedY = dataRect.y + dataRect.height;
						double dis = Math.sqrt((selectedX - x)
								* (selectedX - x) + (selectedY - y)
								* (selectedY - y));
						if (dis < minDis) {
							minDis = dis;
							selectedRecord = tempStory;
						}
					}
				}
			}
		}
		if (minDis <= distance)
			//return selectedRecord;
			return null;
		else
			return null;
	}

	
	
	public GenRecord getSelected(int x, int y) {

		FacetLine temp;

		for (int i = 0; i < facetLines.size(); i++) {
			temp = (FacetLine) (facetLines.elementAt(i));
			if (temp.contains(x, y))
				return temp.getSelected(x, y);
		}
		return null; // no selection
	}

	/**
	 * Mark the event that contains the point (x,y)
	 */
	public void select(int x, int y) {
		FacetLine temp;

		for (int i = 0; i < facetLines.size(); i++) {
			temp = (FacetLine) (facetLines.elementAt(i));
			if (temp.contains(x, y))
				temp.select(x, y);
		}
	}

	public void redraw() {

		currentRect = new Rectangle(-1, -1, -1, -1);

		FacetLine temp;
		if (facetLines == null) {
			return;
		}

		for (int i = 0; i < facetLines.size(); i++) {
			temp = (FacetLine) (facetLines.elementAt(i));
			temp.redraw();
		}
	}

	/**
	 * Return the events that are inside the rubberband rectangle
	 */
	public Vector rubber_band(int rubber_startX, int rubber_startY,
			int rubber_endX, int rubber_endY) {
		FacetLine temp;
		Vector streamlist = new Vector(), substreamlist;

		for (int i = 0; i < facetLines.size(); i++) {
			temp = (FacetLine) (facetLines.elementAt(i));
			substreamlist = temp.rubber_band(rubber_startX, rubber_startY,
					rubber_endX, rubber_endY);
			if (!substreamlist.isEmpty()) {
				for (int j = 0; j < substreamlist.size(); j++)
					streamlist.addElement(substreamlist.elementAt(j));
			}
		}
		return streamlist;
	}

	/**
	 * Return the events that are inside the rubberband circle
	 */
	public Vector rubber_band(int centerX, int centerY, int radius) {
		FacetLine temp;
		Vector streamlist = new Vector(), substreamlist;

		for (int i = 0; i < facetLines.size(); i++) {
			temp = (FacetLine) (facetLines.elementAt(i));
			substreamlist = temp.rubber_band(centerX, centerY, radius);
			if (!substreamlist.isEmpty()) {
				for (int j = 0; j < substreamlist.size(); j++)
					streamlist.addElement(substreamlist.elementAt(j));
			}
		}
		return streamlist;
	}

	/**
	 * Get the height of the facet by the number of pixels on the display
	 */
	@Override
	public int getHeight() {

		int height = 0;
		FacetLine temp;

		if (facetLines != null) {
			for (int i = 0; i < facetLines.size(); i++) {
				temp = (FacetLine) (facetLines.elementAt(i));
				if (enabled)
					height += temp.getHeight() + ((temp.below) ? 15 : 7);
				else
					height += Record.SILPIXEL;
			}
		} else
			height += 12;
		if (enabled)
			return height;
		else
			return ((height < 12) ? 12 : height);
	}

	/**
	 * Check if any checkbox contains the point (x,y)
	 */
	public boolean checkBoxContains(int x, int y) {

		Rectangle currentBox;

		currentBox = new Rectangle(currentRect.x, currentRect.y, 10, 10);

		if (currentBox.contains(x, y))
			return true;
		else
			return false;

	}

	/**
	 * Toggle the siloutte on/off option
	 */
	public void checkBoxClick() {
		if (enabled)
			enabled = false;
		else
			enabled = true;
	}

	/**
	 * Check if title contains the point (x,y)
	 */
	public boolean checkTitleContains(int x, int y) {

		if (currentRect.x < 0 || currentRect.y < 0) {
			return false;
		}

		Rectangle currentBox;

		currentBox = new Rectangle(currentRect.x + 62, currentRect.y, 40, 10);

		if (currentBox.contains(x, y))
			return true;
		else
			return false;

	}
	
	/**
	 * Check if title contains the point (x,y)
	 */
	public boolean checkTitleContainsFull(int x, int y) {

		if (currentRect.x < 0 || currentRect.y < 0) {
			return false;
		}

		Rectangle currentBox;

		currentBox = new Rectangle(currentRect.x+15, currentRect.y, 115, 10);

		if (currentBox.contains(x, y))
			return true;
		else
			return false;

	}

	/**
	 * Mark all the events which contain the search string
	 */
	public void select(String searchString) {
		if (Record.searchoption_timeline[1]) {
			if (remove_count > 0) {
				for (int v = 0; v < aggregates.size(); v++) {
					Aggregate addThis = (Aggregate) aggregates.elementAt(v);
					addThis.selected = true;
				}
				this.layout();
			}
			remove_count++;
		}

		FacetLine temp;
		for (int i = 0; i < facetLines.size(); i++) {
			temp = (FacetLine) (facetLines.elementAt(i));
			temp.select(searchString);
		}
	}

}
