/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *		
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.awt.*;

public class MyRectangle extends Rectangle {

	private String Cause;
	private GenRecord theRecord;
	private Color rectColor;
	private boolean initFlag;
	private MyDate startDate;
	private MyDate endDate;

	private String theUrl;
	private String inputLine;

	MyRectangle(int a, int b, int c, int d) {

		super(a, b, c, d);

	}

	public String getUrl() {

		return theUrl;

	}

	public void setUrl(String theUrl) {

		this.theUrl = theUrl;

	}

	public void setInitFlag() {

		initFlag = true;

	}

	public void unSetInitFlag() {

		initFlag = false;

	}

	public boolean getInitFlag() {

		return initFlag;

	}

	public void setColor(Color rectColor) {

		this.rectColor = rectColor;

	}

	public Color getColor() {

		return rectColor;

	}

	public void setStartDate(MyDate startDate) {

		this.startDate = startDate;

	}

	public MyDate getStartDate() {

		return startDate;

	}

	public void setEndDate(MyDate endDate) {

		this.endDate = endDate;

	}

	public MyDate getEndDate() {

		return endDate;

	}

	public String getCause() {

		return new String(Cause);

	}

	public void setCause(String cause) {

		Cause = new String(cause);

	}

	public void setRecord(GenRecord theRecord) {

		this.theRecord = theRecord;

	}

	public void brighterColor() {
		rectColor.brighter();
	}

	public GenRecord getRecord() {

		return theRecord;

	}

	public String getInputLine() {

		return inputLine;

	}

	public void setInputLine(String inputLine) {

		this.inputLine = inputLine;

	}

}
