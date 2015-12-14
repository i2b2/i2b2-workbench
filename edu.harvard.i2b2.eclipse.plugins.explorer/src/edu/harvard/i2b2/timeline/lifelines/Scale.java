/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *		Mike Mendis (MGH)
 */

package edu.harvard.i2b2.timeline.lifelines;

public class Scale {
	private int width;
	private String mode;
	private MyDate dateMax, dateMin;
	public int n_ticks, theTicks[];
	public String theLabelString[];
	public static int TICK_RANGE = 45, MAX_TICKS;

	private int todayPosition;

	public Scale(int width, MyDate dateMin, MyDate dateMax, MyDate today) {
		this.width = width;
		this.dateMin = dateMin;
		this.dateMax = dateMax;
		MAX_TICKS = 100;
		theTicks = new int[MAX_TICKS + 1];
		theLabelString = new String[MAX_TICKS + 1];
		setScale(dateMin, dateMax, today);

		todayPosition = (int) Math.round((double) dateMin.MinDiff(today)
				* width / dateMin.MinDiff(dateMax)); // some D's were
		// capitalized when I
		// copied?
		// dateMin and dateMax are current dateMin and dateMax
	}

	public void setMode(MyDate dateMin, MyDate dateMax) {
		long d;

		this.dateMin = dateMin;
		this.dateMax = dateMax;
		mode = "decade";

		d = dateMin.MinDiff(dateMax);
		mode = "decade";
		if (d <= 60 * 24 * 365 * 10)
			mode = "year";
		if (d <= 60 * 24 * 365)
			mode = "month";
		if (d <= 60 * 24 * 14)
			mode = "day";
		if (d <= 60 * 12)
			mode = "hour";
		// if(d <= 20) mode = "min";
		if (d <= 60 * 12)
			mode = "disabled";
	}

	public void setScale(MyDate DateMin, MyDate DateMax, MyDate today) { // really
		// changescale
		int i;
		MyDate currDate = new MyDate(DateMin);

		todayPosition = (int) Math.round((double) DateMin.MinDiff(today)
				* width / DateMin.MinDiff(DateMax)); // D's capitalized again

		setMode(DateMin, DateMax);

		if (mode.equals("decade")) {
			for (i = 0; i <= MAX_TICKS; i++) {
				if (i == 0)
					theTicks[i] = 0;

				else if (i == 1) {
					currDate.setYear(DateMin.getYear() - DateMin.getYear() % 10
							+ 10);
					currDate.setMonth(1);
					currDate.setDay(1);
					currDate.setHour(0);
					currDate.setMin(0);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax)); // positions
					// for
					// vertical
					// grid bars
					// (background)
				} else {
					currDate.setYear(currDate.getYear() + 10);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
				}
				if (theTicks[i] >= width) {
					n_ticks = i;
					break;
				}
				if (currDate.getMonth() != 1)
					theLabelString[i] = ""
							+ currDate.getMonth()
							+ "/"
							+ ((currDate.getYear() - 1900 >= 100) ? (currDate
									.getYear() - 2000)
									: (currDate.getYear() - 1900));
				else
					theLabelString[i] = ""
							+ ((currDate.getYear() - 1900 >= 100) ? (currDate
									.getYear() - 2000)
									: (currDate.getYear() - 1900));
			}
		}

		if (mode.equals("year")) {
			if (DateMin.getYear() == 1996/*
										 * && DateMin.getMonth()==1 &&
										 * DateMin.getDay()==1
										 */)
				i = 0;
			for (i = 0; i <= MAX_TICKS; i++) {
				if (i == 0)
					theTicks[i] = 0;
				else if (i == 1) {
					currDate.setYear(DateMin.getYear() + 1);
					currDate.setMonth(1);
					currDate.setDay(1);
					currDate.setHour(0);
					currDate.setMin(0);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
				} else {
					currDate.setYear(currDate.getYear() + 1);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
				}
				if (theTicks[i] >= width) {
					n_ticks = i;
					break;
				}
				if (currDate.getMonth() != 1)
					theLabelString[i] = ""
							+ currDate.getMonth()
							+ "/"
							+ ((currDate.getYear() - 1900 >= 100) ? (currDate
									.getYear() - 2000)
									: (currDate.getYear() - 1900));
				else
					theLabelString[i] = ""
							+ ((currDate.getYear() - 1900 >= 100) ? (currDate
									.getYear() - 2000)
									: (currDate.getYear() - 1900));
			}
		}

		else if (mode.equals("month")) {
			for (i = 0; i <= MAX_TICKS; i++) {
				if (i == 0)
					theTicks[i] = 0;
				else if (i == 1) {
					currDate.setYear(currDate.getYear() + currDate.getMonth()
							/ 12);
					currDate.setMonth(currDate.getMonth() % 12 + 1);
					currDate.setDay(1);
					currDate.setHour(0);
					currDate.setMin(0);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
					if (theTicks[i] < 0)
						i = i;
				} else {
					currDate.setYear(currDate.getYear() + currDate.getMonth()
							/ 12);
					currDate.setMonth(currDate.getMonth() % 12 + 1);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
					if (theTicks[i] < 0)
						i = i;
				}
				if (theTicks[i] >= width) {
					n_ticks = i;
					break;
				}

				theLabelString[i] = ""
						+ currDate.getMonth()
						+ "/"
						+ ((currDate.getYear() - 1900 >= 100) ? (currDate
								.getYear() - 2000)
								: (currDate.getYear() - 1900));
			}

		}

		else if (mode.equals("day")) {
			for (i = 0; i <= MAX_TICKS; i++) {
				if (i == 0)
					theTicks[i] = 0;
				else if (i == 1) {
					currDate = currDate.DateAfterDays(1);
					currDate.setHour(0);
					currDate.setMin(0);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
					if (theTicks[i] < 0)
						i = i;
				} else {
					currDate = currDate.DateAfterDays(1);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
					if (theTicks[i] < 0)
						i = i;
				}
				if (theTicks[i] >= width) {
					n_ticks = i;
					break;
				}
				theLabelString[i] = "" + currDate.getMonth() + "/"
						+ currDate.getDay();
			}
		}

		else if (mode.equals("hour")) {
			for (i = 0; i <= MAX_TICKS; i++) {
				if (i == 0)
					theTicks[i] = 0;
				else if (i == 1) {
					currDate = currDate.DateAfterHours(1);
					currDate.setMin(0);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
				} else {
					currDate = currDate.DateAfterHours(1);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
				}
				if (theTicks[i] >= width) {
					n_ticks = i;
					break;
				}

				if (currDate.getHour() == 0)
					theLabelString[i] = "" + "12am";
				else if (currDate.getHour() == 12)
					theLabelString[i] = "" + "12pm";
				else if (currDate.getHour() < 12)
					theLabelString[i] = "" + currDate.getHour() + "am";
				else if (currDate.getHour() > 12)
					theLabelString[i] = "" + (currDate.getHour() - 12) + "pm";
			}
		} else if (mode.equals("min")) {
			for (i = 0; i <= MAX_TICKS; i++) {
				if (i == 0)
					theTicks[i] = 0;
				else {
					currDate = currDate.DateAfterMins(1);
					theTicks[i] = (int) Math.round((double) DateMin
							.MinDiff(currDate)
							* width / DateMin.MinDiff(DateMax));
				}
				if (theTicks[i] >= width) {
					n_ticks = i;
					break;
				}

				if (currDate.getHour() == 0)
					theLabelString[i] = "" + "12am";
				else if (currDate.getHour() == 12)
					theLabelString[i] = "" + "12pm";
				else if (currDate.getHour() < 12)
					theLabelString[i] = "" + currDate.getHour() + "am";
				else if (currDate.getHour() > 12)
					theLabelString[i] = "" + (currDate.getHour() - 12) + "pm";

				theLabelString[i] = "" + currDate.getHour() + ":"
						+ currDate.getMin();
			}
		}
	}

	/*
	 * public void setLowerScale(MyDate DateMin, MyDate DateMax){ int
	 * start_decade, start_year, start_month, start_day; int i;
	 * 
	 * setMode(DateMin, DateMax); theTicks = new int[n_ticks + 1];
	 * theLabelString = new String[n_ticks + 1];
	 * 
	 * for(i=0; i <= n_ticks; i++) theTicks[i] = (int)Math.round((double)(i *
	 * (width-1))/ n_ticks);
	 * 
	 * if(mode.equals("decade")){ start_decade = DateMin.getYear() -
	 * DateMin.getYear()%10; for(i=0; i <= n_ticks; i++) theLabelString[i] = ""
	 * + (start_decade + i*10); }
	 * 
	 * if(mode.equals("year")){ start_year = DateMin.getYear(); for(i=0; i <=
	 * n_ticks; i++) theLabelString[i] = "" + (start_year +i - 1900); }
	 * 
	 * else if(mode.equals("month")){ start_year = DateMin.getYear();
	 * start_month = DateMin.getMonth(); for(i=0; i <= n_ticks; i++)
	 * theLabelString[i] = "" + ((start_month +i-1)%12+1) + "/" + (start_year -
	 * 1900 +(start_month+i-1)/12); }
	 * 
	 * else if(mode.equals("day")){ start_year = DateMin.getYear(); start_month
	 * = DateMin.getMonth(); start_day = DateMin.getDay(); for(i=0; i <=
	 * n_ticks; i++) theLabelString[i] = "" + (start_month + (start_day +i
	 * -1)/31) + "/" + ((start_day + i - 1)%31 + 1); } }
	 */
	public String getMode() {
		return this.mode;
	}

	public boolean offScale(MyDate Date1, MyDate Date2) {

		if (Date1.after(dateMax) || Date2.before(dateMin))
			return true;
		else
			return false;

	}

	public MyDate getDateMax() {

		return dateMax;

	}

	public MyDate getDateMin() {

		return dateMin;

	}

	public MyDate scaleMin(MyDate lowDate) {

		if (lowDate.before(dateMin))
			return dateMin;
		else
			return lowDate;
	}

	public MyDate scaleMax(MyDate highDate) {

		if (highDate.after(dateMax))
			return dateMax;
		else
			return highDate;
	}

	public int getTodayPosition() {

		return todayPosition;

	}

}
