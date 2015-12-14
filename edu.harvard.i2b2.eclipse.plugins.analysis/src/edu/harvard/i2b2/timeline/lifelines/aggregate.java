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

package edu.harvard.i2b2.timeline.lifelines;

import java.util.*;
import edu.harvard.i2b2.timeline.external.*;

/**
 * aggregate class defines a group of events that are closely related
 */
public class aggregate extends genRecord {

    private int mark;
    private boolean[] lbloption = new boolean[3];
    private boolean[] summaryoption = new boolean[2];
    public int numLabel;
    public boolean below = false;
    public boolean all_labelled = true;
    public boolean labelonce = false;

    private Hashtable genList; // hashtable allows dynamic sizing; 7/23/98 -
    // could contain aggregates as well
    private int height;
    private int counter; // since numberOfEvents is read in from the file...
    private storyRecord summary; // 3/10/98 and then 7/25/98 change to
    // storyRecord
    private Hashtable rectIndexList; // needed for now because storyRecord keeps
    // a current index called rectp
    // also this is allocated ("newed") only in draw since the values (but not
    // likely the number, though this
    // would be determined after all adds (not in constructor), so don't do for
    // now..)
    private String labelString;
    int cum = 0;

    public aggregate(String type) {

	super(type);

	genList = new Hashtable();
	height = 0;
	counter = 0;
	mark = 0;
	numLabel = 0;
	rectIndexList = new Hashtable();
	start_date = null;
	end_date = null;
    }

    /**
     * Get the starting date
     */
    public MyDate getDateMin() {
	return start_date;
    }

    /**
     * Get the ending date
     */
    public MyDate getDateMax() {

	MyDate dateMax = new MyDate(getStartdate().getMonth(), getStartdate()
		.getDay(), getStartdate().getYear(), getStartdate().getHour(),
		getStartdate().getMin());

	for (int i = 0; i < genList.size(); i++) {
	    genRecord aGenRecord = (genRecord) genList.get(new Integer(i));

	    if (aGenRecord.getEnddate().after(dateMax)) {
		dateMax.setMonth(aGenRecord.getEnddate().getMonth());
		dateMax.setDay(aGenRecord.getEnddate().getDay());
		dateMax.setYear(aGenRecord.getEnddate().getYear());
		dateMax.setHour(aGenRecord.getEnddate().getHour());
		dateMax.setMin(aGenRecord.getEnddate().getMin());

	    }
	}
	return end_date;
    }

    /**
     * Get the width of the aggregate by the number of pixels on the display
     */
    @Override
    public int getRectWidth() {

	int rectWidth = 0;

	for (int i = 0; i < genList.size(); i++) {
	    if (rectWidth < ((genRecord) (genList.get(new Integer(i))))
		    .getRectWidth())
		rectWidth = ((genRecord) (genList.get(new Integer(i))))
			.getRectWidth();
	}
	return rectWidth;
    }

    /**
     * Set the start date
     */
    public void setStartdate(MyDate start) {
	this.start_date = start.copy();
    }

    /**
     * Set the end date
     */
    public void setEnddate(MyDate end) {
	this.end_date = end.copy();
    }

    @Override
    public void redraw() {
	// storyRecord aGenRecord = null;
	genRecord aGenRecord = null;

	for (int k = 0; k < genList.size(); k++) {
	    aGenRecord = (storyRecord) (genList.get(new Integer(k)));
	    aGenRecord.redraw();
	    /*
	     * Rectangle r = aGenRecord.getBarArea();
	     * System.out.println("original rect: "+r.x+","+r.y); r.x = -1; r.y
	     * = -1; r.width = -1; r.height = -1;
	     * System.out.println("set rect to: "
	     * +aGenRecord.getBarArea().x+","+aGenRecord.getBarArea().y);
	     */
	}
    }

    /**
     * Add the event to the aggregate
     */
    public void addGen(genRecord gen) {
	if (cum == 0)
	    cum = gen.getRectWidth();

	genList.put(new Integer(counter), gen);
	counter++;

	if (genList.size() > 1
		&& gen.getStartdate().equals(
			((genRecord) genList.get(new Integer(counter - 2)))
				.getStartdate()))
	    cum += ((genRecord) genList.get(new Integer(counter - 2)))
		    .getRectWidth() + 1;
	else {
	    if (height < cum)
		height = cum;
	    cum = 0;
	}

	if (counter == genList.size() && height < cum)
	    height = cum;

	if (start_date == null)
	    start_date = (gen.getStartdate()).copy();
	else if (gen.getStartdate().before(start_date))
	    start_date = new MyDate(gen.getStartdate().getMonth(), gen
		    .getStartdate().getDay(), gen.getStartdate().getYear(), gen
		    .getStartdate().getHour(), gen.getStartdate().getMin());

	if (end_date == null)
	    end_date = (gen.getEnddate()).copy();
	else if (gen.getEnddate().after(end_date))
	    end_date = new MyDate(gen.getEnddate().getMonth(), gen.getEnddate()
		    .getDay(), gen.getEnddate().getYear(), gen.getEnddate()
		    .getHour(), gen.getEnddate().getMin());

    }

    /**
     * Draw the labels on the display
     */
    @Override
    public void drawLabel(int currentY, timeLinePanel displayArea,
	    boolean label, boolean summaryrecord, boolean stream) {
	genRecord aGenRecord;

	if (!all_labelled && (summary != null)
		&& (summary.getLabelX() >= 0 || summary.getSaveLabelX() >= 0))
	    summary.drawLabel(currentY, displayArea, summaryoption[0], true,
		    stream);

	for (int k = 0; k < genList.size(); k++) {
	    aGenRecord = (genRecord) (genList.get(new Integer(k)));

	    if (!all_labelled
		    && (summary != null)
		    && (summary.getLabelX() >= 0 || summary.getSaveLabelX() >= 0))
		aGenRecord.drawLabel(currentY, displayArea, !summaryoption[0],
			false, stream);
	    else
		aGenRecord
			.drawLabel(currentY, displayArea, true, false, stream);
	}
    }

    /**
     * Draw the aggregate on the display
     */
    @Override
    public void drawData(int currentY, timeLinePanel displayArea,
    		boolean silhouette, boolean timeline, boolean summaryrecord) {
    	genRecord aGenRecord = null;

    	if (!all_labelled && (summary != null))
    		summary.drawData(currentY, displayArea, silhouette,
    				summaryoption[1], true);
		int backupY=0;  // backupY is the Y position of the first tick that has dup entries on same date
    	for (int k = 0; k < genList.size(); k++) {

    		aGenRecord = (genRecord) (genList.get(new Integer(k)));

    		if (!all_labelled && (summary != null))
    			aGenRecord.drawData(currentY, displayArea, silhouette,
    					!summaryoption[1], false);
    		else {
    			aGenRecord.drawData(currentY, displayArea, silhouette, true,
    					false);

    			// 10/26/98 Julia, handle parallel aggregates
    			genRecord nextGenRecord = aGenRecord; // just a dummy
    			// initialization

    			if (k < (genList.size() - 1))
    				nextGenRecord = (genRecord) (genList
    						.get(new Integer(k + 1)));

    			if ((nextGenRecord.getStartdate()).equals(aGenRecord
    					.getStartdate())) {
    				// !aGenRecord.getCause().equals("nolabel") &&
    				// !nextGenRecord.getCause().equals
    				// ("nolabel"))

    				// if we have entries on same date, stack them
    				// but same Y position for next event on different date
    				if(backupY == 0)
    					backupY = currentY;
    				currentY = currentY + aGenRecord.getRectWidth() + 1;
    			}
    			else {
    				// If we have an entry on a different date and a 
    				// backup Y position has been noted, reset currentY to
    				// backupY and reset backupY back to zero.
    				if(backupY != 0)
    				{
    					currentY = backupY;
    					backupY = 0;

    				}
    			}
    		}

    	}
    }
    /**
     * Check whether the aggregate contains the point (x,y)
     */
    @Override
    public boolean contains(int x, int y) {
	for (int i = 0; i < genList.size(); i++) {
	    if (((genRecord) (genList.get(new Integer(i)))).contains(x, y))
		return true;
	}

	return false;

    }

    /**
     * Check whether the aggregate contains the search string
     */
    @Override
    public boolean contains(String searchString) {

	for (int i = 0; i < genList.size(); i++) {
	    if (((genRecord) (genList.get(new Integer(i)))).getCause().equals(
		    searchString))
		return true;
	}
	return false;
    }

    /**
     * Return the events that are inside the rubberband rectangle
     */
    @Override
    public Vector rubber_band(int rubber_startX, int rubber_startY,
	    int rubber_endX, int rubber_endY) {
	Vector rubberlist = new Vector(), subrubberlist;
	genRecord temp, storytemp;

	for (int i = 0; i < genList.size(); i++) {
	    temp = (genRecord) (genList.get(new Integer(i)));
	    if (temp.getType().equals("aggregate")) {
		subrubberlist = temp.rubber_band(rubber_startX, rubber_startY,
			rubber_endX, rubber_endY);
		for (int j = 0; j < subrubberlist.size(); j++)
		    rubberlist.addElement(subrubberlist.elementAt(j));
	    } else {
		storytemp = temp.intersects(rubber_startX, rubber_startY,
			rubber_endX, rubber_endY);
		if (storytemp != null)
		    rubberlist.addElement(storytemp);
	    }
	}

	return rubberlist;
    }

    /**
     * Return the events that are inside the rubberband circle
     */
    @Override
    public Vector rubber_band(int centerX, int centerY, int radius) {
	Vector rubberlist = new Vector(), subrubberlist;
	genRecord temp, storytemp;

	for (int i = 0; i < genList.size(); i++) {
	    temp = (genRecord) (genList.get(new Integer(i)));
	    if (temp.getType().equals("aggregate")) {
		subrubberlist = temp.rubber_band(centerX, centerY, radius);
		for (int j = 0; j < subrubberlist.size(); j++)
		    rubberlist.addElement(subrubberlist.elementAt(j));
	    } else {
		storytemp = ((storyRecord) temp).intersects(centerX, centerY,
			radius);
		if (storytemp != null)
		    rubberlist.addElement(storytemp);
	    }
	}
	return rubberlist;
    }

    @Override
    public void select() {
    }

    @Override
    public void unSelect() {
    }

    public void setInitFlags(MyRectangle rects[]) {

	for (int i = 0; i < rectIndexList.size(); i++) {
	    rects[((Integer) (rectIndexList.get(new Integer(i)))).intValue()]
		    .setInitFlag();
	}

    }

    public void unSetInitFlags(MyRectangle rects[]) {

	for (int i = 0; i < rectIndexList.size(); i++) {
	    rects[((Integer) (rectIndexList.get(new Integer(i)))).intValue()]
		    .unSetInitFlag();
	}
    }

    /**
     * Mark the event that contains the point (x,y)
     */
    @Override
    public void select(int x, int y) {
	genRecord temp;

	for (int i = 0; i < genList.size(); i++) {
	    temp = (genRecord) (genList.get(new Integer(i)));
	    if (temp.contains(x, y))
		temp.select();
	}
    }

    /**
     * Set the conflict resolvers
     */
    @Override
    public void setXOverlap(conflictResolver xOverlap1_,
	    conflictResolver xOverlap2_) {
	xOverlap1 = xOverlap1_;
	xOverlap2 = xOverlap2_;

	genRecord temp;
	for (int i = 0; i < genList.size(); i++) {
	    temp = (genRecord) (genList.get(new Integer(i)));
	    temp.setXOverlap(xOverlap1, xOverlap2);
	}
    }

    /**
     * Set a number of variables
     */
    @Override
    public void setVar(boolean fit, boolean beforefit, boolean selected) {
	this.fit = fit;
	this.beforefit = beforefit;
	this.selected = selected;

	genRecord temp;
	for (int i = 0; i < genList.size(); i++) {
	    temp = (genRecord) (genList.get(new Integer(i)));
	    temp.setVar(fit, beforefit, selected);
	}
    }

    /**
     * Set label positions
     */
    @Override
    public void setLabelXY(int labelX_, int labelY_) {
	labelX = labelX_;
	labelY = labelY_;

	genRecord temp;
	for (int i = 0; i < genList.size(); i++) {
	    temp = (genRecord) (genList.get(new Integer(i)));
	    temp.setLabelXY(labelX, labelY);
	}
    }

    /**
     * Save label positions
     */
    @Override
    public void saveLabelXY() {
	saveLabelX = labelX;
	saveLabelY = labelY;

	genRecord temp;
	for (int i = 0; i < genList.size(); i++) {
	    temp = (genRecord) (genList.get(new Integer(i)));
	    temp.saveLabelXY();
	}
    }

    /**
     * Set the saved label positions
     */
    @Override
    public void setSavedLabelXY() {
	labelX = this.saveLabelX;
	labelY = this.saveLabelY;

	genRecord temp;
	for (int i = 0; i < genList.size(); i++) {
	    temp = (genRecord) (genList.get(new Integer(i)));
	    temp.setSavedLabelXY();
	}
    }

    /**
     * Set the conflict resolver
     */
    @Override
    public void setConflicts(int currentY) {
	genRecord temp;

	for (int i = 0; i < genList.size(); i++) {
	    temp = (genRecord) (genList.get(new Integer(i)));
	    temp.setConflicts(currentY);
	}
    }

    /**
     * Clear the conflict resolver
     */
    @Override
    public void clearConflicts(int currentY) {
	genRecord temp;

	if (!all_labelled && summary != null) {
	    summary.clearConflicts(currentY);
	    summary.saveLabelXY();
	    summary.setLabelXY(-1, -10000);
	} else {
	    for (int i = 0; i < genList.size(); i++) {
		temp = (genRecord) (genList.get(new Integer(i)));
		temp.clearConflicts(currentY);
	    }
	}
    }

    /**
     * Set the aggregate name
     */
    public void setLabelString(String value) {

	labelString = new String(value);
    }

    /**
     * Get the aggregate name
     */
    public String getLabelString() {

	return labelString;
    }

    /**
     * Get the height of the aggregate in the number of pixels on the display
     */
    @Override
    public int getHeight() {
	return height;
    }

    /**
     * Get all the events for this aggregate
     */
    public Vector getAllRecords() {
	Vector allRecord = new Vector(), temprecord = new Vector();
	for (int i = 0; i < genList.size(); i++) {
	    if (!((genRecord) genList.get(new Integer(i))).getType().equals(
		    "aggregate"))
		allRecord.addElement(genList.get(new Integer(i)));
	    else {
		Vector tempRecord = ((aggregate) genList.get(new Integer(i)))
			.getAllRecords();
		for (int j = 0; j < tempRecord.size(); j++)
		    allRecord.addElement(tempRecord.elementAt(j));
	    }
	}
	return allRecord;
    }

    /**
     * Return the variable below
     */
    @Override
    public boolean getBelow() {
	return below;
    }

    /**
     * Return the variable selected
     */
    @Override
    public boolean getSelected() {
	return selected;
    }

    /**
     * Label all the events in the aggregate
     */
    @Override
    public boolean fitlabel(int currentY, timeLinePanel displayArea,
	    boolean backtrack, int height) {
	genRecord aGenRecord;
	boolean fitlabel = true;
	labelonce = true;

	// 9/8/98, do not need to truncate
	// if (all_labelled && record.lengthoption[1])
	// return true;
	// else
	all_labelled = true;

	for (int i = 0; i < 3; i++)
	    lbloption[i] = record.lbloption[i];

	for (int i = 0; i < 2; i++)
	    summaryoption[i] = record.summaryoption[i];

	if (backtrack)
	    mark--;

	// 10/30/98 Julia
	if (mark < 0)
	    fitlabel = false;

	int num = 0;
	while (mark < genList.size() && mark >= 0) {
	    aGenRecord = (genRecord) (genList.get(new Integer(mark)));
	    // do a cast to a labelless class of genRecord to get paralell
	    // aggregates
	    // 3/28/98 Don't label the ones that are not selected for search
	    if ((aGenRecord.getSelected() && record.searchoption_label[1])
		    || record.searchoption_label[0]) {
		fitlabel = aGenRecord.fitlabel(currentY, displayArea,
			backtrack, height);

		if ((lbloption[1] || lbloption[2]) && !fitlabel) { // add if the
		    // before is
		    // aggregate,
		    // ignore it
		    mark--;
		    num--;
		    if (mark < 0) // cannot fit the aggregate
			fitlabel = false;
		    else {
			backtrack = true;
			for (int i = mark + 1; i < genList.size(); i++)
			    ((genRecord) (genList.get(new Integer(i))))
				    .resetlabel();
		    }
		} else {
		    mark++;
		    backtrack = false;
		    num++;
		    if (lbloption[0] && !fitlabel)
			all_labelled = false;
		}
	    } else
		mark++;
	    // save the label configuration to maxmize the number of events get
	    // labelled
	    if (num > numLabel) {
		numLabel = num;
		for (int i = 0; i < mark; i++) {
		    aGenRecord = (genRecord) (genList.get(new Integer(i)));
		    aGenRecord.saveLabelXY();
		}
	    }
	}
	genRecord lastRecord = (genRecord) genList.get(new Integer(genList
		.size() - 1));
	if (lbloption[2] && lastRecord.getXOverlap2() != null) // if lastRecord
	    // is an
	    // aggregate,
	    // getXOverlap2
	    // will get the
	    // xOverlap2 of
	    // the last event
	    // in the
	    // aggregate
	    below = (!lastRecord.getXOverlap2().isEmpty());

	if (lbloption[1] || lbloption[2])
	    all_labelled = fitlabel; // all the previous items must have to be
	// labelled, so we only need to check the
	// last one
	if (!all_labelled && summaryoption[0] && (summary != null)) { // display
	    // summary
	    // label
	    // for the
	    // first
	    // event
	    // record
	    // clear all the conflict value set by labelled events
	    for (int i = 0; i < genList.size(); i++) {
		genRecord tempRecord = (genRecord) (genList.get(new Integer(i)));
		tempRecord.clearConflicts(currentY);
	    }
	    fitlabel = summary.fitlabel(currentY, displayArea, backtrack,
		    height);
	    // if fit is false, set back all the conflicts
	    if (!fitlabel) {
		summary.clearConflicts(currentY);
		for (int i = 0; i < genList.size(); i++) {
		    genRecord tempRecord = (genRecord) (genList
			    .get(new Integer(i)));
		    tempRecord.setSavedLabelXY();
		    tempRecord.setConflicts(currentY);
		}
	    }
	}
	// System.out.println("aggregate: finish relabeling");
	return fitlabel;
    }

    @Override
    public void resetlabel() {
	mark = 0;
	if (summary != null)
	    summary.resetlabel();
	for (int i = 0; i < genList.size(); i++)
	    ((genRecord) (genList.get(new Integer(i)))).resetlabel();
    }

    /**
     * Set the flag for summary record
     */
    @Override
    public void setSummaryFlag(boolean label, boolean timeline) {
	genRecord aGenRecord;

	if (summary != null)
	    summary.setSummaryFlag(!label, !timeline);
	for (int k = 0; k < genList.size(); k++) {
	    aGenRecord = (genRecord) (genList.get(new Integer(k)));
	    aGenRecord.setSummaryFlag(label, timeline);
	}
    }

    @Override
    public genRecord getSelected(int x, int y) {
	genRecord tempGen;
	if (summary != null && summary.contains(x, y, true))
	    return summary.getSelected(x, y);
	else {
	    for (int i = 0; i < genList.size(); i++) {
		tempGen = (genRecord) (genList.get(new Integer(i)));
		if (tempGen.contains(x, y))
		    return tempGen.getSelected(x, y);
	    }
	}
	return null;
    }

    /**
     * Return the variable genList
     */
    @Override
    public Hashtable getGenList() {
	return genList;
    }

    /**
     * Return the variable xOverlap2
     */
    @Override
    public conflictResolver getXOverlap2() {
	genRecord lastRecord = (genRecord) genList.get(new Integer(genList
		.size() - 1));
	return lastRecord.getXOverlap2();
    }

    /**
     * Mark all the events which contain the search string
     */
    @Override
    public void select(String searchString) {
	genRecord temp;

	selected = false;
	if (summary != null)
	    summary.selected = false;
	for (int i = 0; i < genList.size(); i++) {
	    try {

		RegExp reg = new RegExp(searchString); // gnu public licence
		// regexp package
		// (presently
		// in same directory as here

		temp = (genRecord) (genList.get(new Integer(i)));
		if (reg.match(temp.getInputLine()) != null) {
		    temp.select();
		    selected = true;
		    if (summary != null)
			summary.selected = true;
		} else
		    temp.unSelect();
	    } catch (Exception e) {
		System.out.println("Exception: " + e.toString());
	    }
	}
    }

    /**
     * Set the summary record
     */
    public void addSummary(storyRecord summary) {
	this.summary = summary;
    }

    /**
     * Return the variable summary
     */
    public storyRecord getSummary() {
	return summary;
    }
}
