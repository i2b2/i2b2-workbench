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

import java.awt.*;
import java.util.*;
import edu.harvard.i2b2.timeline.labeling.*;

/**
 * facetLine class defines a group of events that are aligned horizontally
 */
public class facetLine {
    private Vector aggregates;
    // private MyDate minDate;
    private MyDate maxDate;
    private int height = 0;
    private String labelString;
    private int mark;
    private boolean[] lbloption = new boolean[3];
    public boolean below = false;
    public boolean fitlbl = true;
    private int numLabel = 0;
    private Color backgroundColor;
    private LiteGroup labels = new LiteGroup();
    private int degree = 0;
    private AngleLayout layout;
    private String title = null;

    public String getTitle() {
	return title;
    }

    public facetLine(String title, Color backgroundColor) {
	this.backgroundColor = backgroundColor;
	aggregates = new Vector();
	// minDate = null;
	maxDate = null; // to show other functions that the line is empty
	mark = 0;
	this.title = new String(title);
    }

    /**
     * Store all the events to a vector
     */
    public void addEventObject(genRecord addThis) {
	aggregates.addElement(addThis);
	if (maxDate == null || addThis.getEnddate().after(maxDate)) {
	    maxDate = (addThis.getEnddate()).copy();
	}

	int tmp = addThis.getHeight();
	if (((genRecord) (addThis.getGenList().get(new Integer(0))))
		.getInputLine().indexOf("Value") >= 0) {
	    tmp = 22;
	}

	if (tmp > height) {
	    height = tmp;
	}
    }

    /**
     * Check whether the event can be fitted horizontally
     */
    public boolean fits(genRecord thisEvent) {
	if (maxDate == null)
	    return true;
	else {
	    if (thisEvent.getStartdate().before(maxDate)) {
		return false;
	    } else
		return true;
	}
    }

    /**
     * Check weather any event contains the point (x,y)
     */
    public boolean contains(int x, int y) {
	for (int i = 0; i < (aggregates.size()); i++) {
	    if (((genRecord) (aggregates.elementAt(i))).contains(x, y))
		return true;
	}
	return false;
    }

    public genRecord getSelected(int x, int y) {

	genRecord temp;

	for (int i = 0; i < (aggregates.size()); i++) {
	    temp = (genRecord) (aggregates.elementAt(i));
	    if (temp.contains(x, y))
		return temp.getSelected(x, y);
	}
	return null; // nothing selected
    }

    /**
     * Mark the event that contains the point (x,y)
     */
    public void select(int x, int y) {
	genRecord temp;

	for (int i = 0; i < (aggregates.size()); i++) {
	    temp = (genRecord) (aggregates.elementAt(i));
	    if (temp.contains(x, y))
		temp.select(x, y);
	}
    }

    /**
     * Get the height of the facetLine by the number of pixels on the display
     */
    public int getHeight() {
	return height + 10; // this is an approximation of fontTextHeight which
			    // depends on the displayPanel...
    }

    /**
     * Label all the events in the facetLine
     */
    public boolean fitlabel(int currentY, timeLinePanel displayArea,
	    boolean backtrack) {

	aggregate temp;
	boolean fit = false;
	genRecord aGenRecord, aRecord;

	for (int i = 0; i < 3; i++)
	    lbloption[i] = record.lbloption[i];

	if (backtrack)
	    mark--;
	int num = 0;
	while (mark < aggregates.size() && mark >= 0) {
	    temp = (aggregate) (aggregates.elementAt(mark));
	    // 3/28/98 Don't label the ones that are not selected for search
	    if ((temp.selected && record.searchoption_label[1])
		    || record.searchoption_label[0]) {
		fit = temp.fitlabel(currentY, displayArea, backtrack,
			getHeight());

		if ((lbloption[1] || lbloption[2]) && !fit) { // the labels of
							      // the aggregate
							      // cannot fit and
							      // need to
							      // backtrack the
							      // previous
							      // aggregate
		    mark--;
		    num--;
		    if (mark < 0) // cannot fit the whole line
			fit = false;
		    else {
			backtrack = true;
			for (int i = mark + 1; i < aggregates.size(); i++)
			    ((genRecord) (aggregates.elementAt(i)))
				    .resetlabel();
		    }
		} else {
		    // temp.draw(currentY, displayArea); // probably save for
		    // future
		    mark++;
		    backtrack = false;
		    num++;
		}
	    } else
		mark++;
	    if (num > numLabel) {
		numLabel = num;
		for (int i = 0; i < mark; i++) {
		    aGenRecord = (genRecord) (aggregates.elementAt(i));
		    for (int j = 0; j < aGenRecord.getGenList().size(); j++) {
			aRecord = (genRecord) (aGenRecord.getGenList()
				.get(new Integer(j)));
			aRecord.saveLabelXY();
		    }
		}
	    }
	}
	// 3/28/98 label the aggregates that have never been labeled because the
	// previous ones cannot fit
	for (int k = 0; k < aggregates.size(); k++) {
	    temp = (aggregate) (aggregates.elementAt(k));
	    if (!temp.labelonce)
		temp.fitlabel(currentY, displayArea, false, getHeight());
	}
	genRecord lastAgg = (genRecord) aggregates.lastElement();
	below = lastAgg.getBelow();
	return fit;
    }

    /**
     * Reset the labeling positions of all the events
     */
    public void resetlabel() {
	mark = 0;
	numLabel = 0;
	for (int i = 0; i < aggregates.size(); i++) {
	    aggregate temp;
	    temp = ((aggregate) (aggregates.elementAt(i)));
	    temp.numLabel = 0;
	    temp.below = false;
	    temp.all_labelled = true;
	    temp.selected = true;
	    temp.labelonce = false;
	    temp.resetlabel();
	    for (int j = 0; j < temp.getGenList().size(); j++) {
		genRecord gen;
		gen = ((genRecord) (temp.getGenList().get(new Integer(j))));
		gen.setXOverlap(null, null);
		gen.setVar(false, false, true);
		gen.setLabelXY(-1, -10000);
		gen.saveLabelXY();
	    }
	}
    }

    public void redraw() {
	aggregate temp;
	for (int i = 0; i < (aggregates.size()); i++) {
	    temp = (aggregate) (aggregates.elementAt(i));
	    temp.redraw();
	}
    }

    /**
     * Set the saved labeling positions for all the events
     */
    public void setSavedLabelXY() {
	for (int i = 0; i < aggregates.size(); i++) {
	    genRecord temp;
	    temp = ((genRecord) (aggregates.elementAt(i)));
	    temp.setSavedLabelXY();
	}
    }

    /**
     * Draw all labels on the display
     */
    public void drawLabel(int currentY, timeLinePanel displayArea,
	    boolean stream) {
	FontMetrics fm = mainPanel.theTimeLinePanel.fontMetrics1;
	aggregate temp;
	Graphics offScreenGraphics = displayArea.getOfg();
	int fh = fm.getHeight() + fm.getMaxAscent() + fm.getMaxDescent();

	if (record.angle_label) {
	    show_labels();
	    if (labels.isEmpty())
		return;
	    layout = new AngleLayout(fh);
	    layout.layout(labels.getVector(), degree);
	    labels.paint(offScreenGraphics);
	} else { // normal
	    for (int i = 0; i < (aggregates.size()); i++) {
		temp = (aggregate) (aggregates.elementAt(i));
		temp.drawLabel(currentY, displayArea, true, true, stream);
	    }
	}
    }

    /**
     * Returns the aggregates
     */
    public Vector getAggregates() {
	return aggregates;
    }

    /**
     * Draw all the events of the facetLine on the display
     */
    public void drawData(int currentY, timeLinePanel displayArea,
	    boolean silhouette) {
	aggregate temp;

	for (int i = 0; i < (aggregates.size()); i++) {
	    temp = (aggregate) (aggregates.elementAt(i));
	    temp.drawData(currentY, displayArea, silhouette, true, true);
	}
    }

    /**
     * Return the events that are inside the rubberband rectangle
     */
    public Vector rubber_band(int rubber_startX, int rubber_startY,
	    int rubber_endX, int rubber_endY) {
	genRecord temp;
	Vector streamlist = new Vector(), substreamlist;

	for (int i = 0; i < aggregates.size(); i++) {
	    temp = (genRecord) (aggregates.elementAt(i));
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
	genRecord temp;
	Vector streamlist = new Vector(), substreamlist;

	for (int i = 0; i < aggregates.size(); i++) {
	    temp = (genRecord) (aggregates.elementAt(i));
	    substreamlist = temp.rubber_band(centerX, centerY, radius);
	    if (!substreamlist.isEmpty()) {
		for (int j = 0; j < substreamlist.size(); j++)
		    streamlist.addElement(substreamlist.elementAt(j));
	    }
	}
	return streamlist;
    }

    /**
     * Set the event name for the facetLine. This is used in the
     * "group by event name" layout.
     */
    public void setLabelString(String value) {
	labelString = new String(value);
    }

    /**
     * Get the event name of the facetLine
     */
    public String getLabelString() {
	return labelString;
    }

    /**
     * Mark all the events which contain the search string
     */
    public void select(String searchString) {

	for (int i = 0; i < (aggregates.size()); i++) {
	    ((genRecord) (aggregates.elementAt(i))).select(searchString);
	}
    }

    /**
     * Check whether any event contains the search string
     */
    public boolean contains(String searchString) {

	for (int i = 0; i < (aggregates.size()); i++) {
	    if (((genRecord) (aggregates.elementAt(i))).contains(searchString))
		return true;
	}
	return false;
    }

    /**
     * Get all the labels in this facetline.
     */
    public void show_labels() {
	aggregate aAgg;
	storyRecord aStory;
	for (int i = 0; i < aggregates.size(); i++) {
	    aAgg = ((aggregate) (aggregates.elementAt(i)));
	    for (int j = 0; j < aAgg.getGenList().size(); j++) {
		aStory = ((storyRecord) (aAgg.getGenList().get(new Integer(j))));
		if (!aStory.getCause().equals(" "))
		    labels.addElement(new LiteLabel(aStory.getCause(),
			    new Point(aStory.startX, aStory.startY), 0,
			    mainPanel.theTimeLinePanel.font1, Color.black,
			    backgroundColor, degree,
			    mainPanel.theTimeLinePanel.thisApplet));
	    }
	}
    }
}
