/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	
 *		
 */

package edu.harvard.i2b2.timeline.lifelines;

public class hourstoryRecord extends genRecord {

    // individual record stuff
    private String dose = "";
    private MyDate start_date;
    private MyDate end_date;

    public hourstoryRecord(String type, MyDate start_date, MyDate end_date,
	    String dose) {
	super(type);
	this.start_date = start_date;
	this.end_date = end_date;
	this.dose = dose;
    }

    public String getDose() {
	return dose;
    }

    @Override
    public MyDate getStartdate() {
	return start_date;
    }

    @Override
    public MyDate getEnddate() {
	return end_date;
    }
}
