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

/**
 * genRecord defines a generic class for aggregate and event to inherite from
 */
public class genRecord {

    protected MyDate start_date;
    protected MyDate end_date;
    private String type;
    public static conflictResolver xOverlap1 = null, xOverlap2 = null;
    public boolean fit = false, beforefit = false, selected = true;
    public int labelX, labelY;
    public int saveLabelX, saveLabelY;
    public boolean label = true, timeline = true;

    public genRecord() {
    }

    public int getRectWidth() {
	return -1;
    }

    public Color getRectColor() {
	return (Color.black);
    }

    public String getInputLine() {
	return ("genRecord");
    }

    public String getCause() {
	return ("genRecord!");
    }

    public void redraw() {
    }

    public boolean getSelected() {
	return false;
    }

    public genRecord getSelected(int x, int y) {
	System.out.println("Should not be in genRecord: getSelected");
	return null;
    }

    public Hashtable getAttrList() {
	System.out.println("Should not be in genRecord: getAttrList");
	return null;
    }

    public void unSelect() {
	System.out.println("Should not be in genRecord: unSelect");
    }

    public conflictResolver getXOverlap2() {
	return xOverlap2;
    }

    public void setXOverlap(conflictResolver xOverlap1,
	    conflictResolver xOverlap2) {
	genRecord.xOverlap1 = xOverlap1;
	genRecord.xOverlap2 = xOverlap2;
    }

    public void setVar(boolean fit, boolean beforefit, boolean selected) {
	this.fit = fit;
	this.beforefit = beforefit;
	this.selected = selected;
    }

    public void setLabelXY(int labelX, int labelY) {
	this.labelX = labelX;
	this.labelY = labelY;
    }

    public void saveLabelXY() {
	this.saveLabelX = this.labelX;
	this.saveLabelY = this.labelY;
    }

    public void setSavedLabelXY() {
	this.labelX = this.saveLabelX;
	this.labelY = this.saveLabelY;
    }

    public Vector rubber_band(int rubber_startX, int rubber_startY,
	    int rubber_endX, int rubber_endY) {
	System.out.println("Shouln't be in genRecord: rubber_band()");
	return null;
    }

    public Vector rubber_band(int centerX, int centerY, int radius) {
	System.out.println("Shouln't be in genRecord: rubber_band()");
	return null;
    }

    public genRecord intersects(int rubber_startX, int rubber_startY,
	    int rubber_endX, int rubber_endY) {
	System.out.println("Shouln't be in genRecord: intersects()");
	return null;
    }

    public Hashtable getGenList() {
	System.out.println("Should not be in genRecord: getGenList()");
	return null;
    }

    public MyDate getStartdate() {
	return new MyDate(start_date.getMonth(), start_date.getDay(),
		start_date.getYear(), start_date.getHour(), start_date.getMin());
    }

    public MyDate getEnddate() {
	return new MyDate(end_date.getMonth(), end_date.getDay(), end_date
		.getYear(), end_date.getHour(), end_date.getMin());
    }

    public genRecord(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }

    public int storeRect(Rectangle rect, int rwinWidth, int rwinOffset,
	    MyDate validDateMin, MyDate validDateMax) {
	return 0;
    }

    public void load(StringTokenizer line_tokens) {

    }

    public boolean contains(int x, int y) {

	System.out.println("Shouldn't be here!");
	return false;
    }

    public void select() {

	System.out.println("Shouldn't be here");
    }

    public void select(int x, int y) {

	System.out.println("Shouldn't be here");
    }

    public int getHeight() {

	System.out.println("Shouldn't be here");
	return -1;
    }

    public void draw(int currentY, timeLinePanel displayArea,
	    boolean silhouette, boolean label, boolean timeline,
	    boolean summaryrecord, boolean stream) {

	System.out.println("Should not be in genRecord:draw");
    }

    public void drawData(int currentY, timeLinePanel displayArea,
	    boolean silhouette, boolean timeline, boolean summaryrecord) {
	System.out.println("Should not be in genRecord:drawData");
    }

    public void drawLabel(int currentY, timeLinePanel displayArea,
	    boolean label, boolean summaryrecord, boolean stream) {
	System.out.println("Should not be in genRecord:drawLabel");
    }

    public boolean fitlabel(int currentY, timeLinePanel displayArea,
	    boolean backtrack, int height) {
	System.out.println("Should not be in genRecord:fitlabel");
	return false;
    }

    public void resetlabel() {
	System.out.println("Should not be in genRecord:reset");
    }

    public void setConflicts(int y) {
	System.out.println("Should not be in genRecord: setConflicts");
    }

    public boolean getBelow() {
	System.out.println("Should not be in genRecord:getBelow");
	return false;
    }

    // added 1/7/98 for timeLinePanel and just for storyRecord for now

    public String getUrl() {

	System.out.println("Should not be here");
	// * changed to funny string
	return "null";
    }

    public void clearConflicts(int currentY) {
	System.out.println("Should not be in genRecord:clearConflicts");
    }

    public int getLabelX() {
	return labelX;
    }

    public int getLabelY() {
	return labelY;
    }

    public int getSaveLabelX() {
	return saveLabelX;
    }

    public void setSummaryFlag(boolean label, boolean timeline) {
	System.out.println("Shouln't be in genRecord: setSummaryFlag");
    }

    // added 1/28/98 for grep:
    public boolean contains(String searchString) {

	System.out.println("Shouldn't be here, contains");

	return false;

    }

    // added 1/28/98 for grep:

    public void select(String searchString) {

	System.out.println("Shouldn't be here, select");

    }

}
