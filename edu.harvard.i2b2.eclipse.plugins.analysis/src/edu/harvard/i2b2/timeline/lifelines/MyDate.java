/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  	
 *  	Mike Mendis (MGH)
 *  	Wensong Pan (MGH)
 *		
 */

package edu.harvard.i2b2.timeline.lifelines;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyDate {
   private int month, day, year, hour, min;
   public static int mdays[] = {31,29,31,30,31,30,31,31,30,31,30,31};
   
   public MyDate copy() {
        MyDate temp = new MyDate(getMonth(),getDay(),getYear(),getHour(),getMin());
        return temp;
   }

   public boolean equals(MyDate checkIfEqual)   {
        if(checkIfEqual.getHour() == getHour() && checkIfEqual.getMin() == getMin() && checkIfEqual.getMonth() == getMonth() && checkIfEqual.getDay() == getDay() && checkIfEqual.getYear() == getYear() )
           return true;

        return false;
   }
    
   
   /* Disable creation of d/m/y
   public MyDate(int m, int d, int y) {
        this.month = m; this.day = d; this.year = y;
        this.hour = 0; this.min = 0;
   }
   */
    public MyDate(int month, int d, int y, int h, int min){
        this.month = month; this.day = d; this.year = y;
        this.hour = h; setMin(min);
    }

    public MyDate(String str) throws Exception{
       if(str.equals("today"))  {
        MyDate today = loadRecord.getToday();

        this.month = today.getMonth();
        this.day = today.getDay();
        this.year = today.getYear() /*+ 1900*/;
        this.hour = today.getHour();
        this.min = today.getMin();
       }

       else{
        boolean yearFirst = (record.yearFirst == true); // prevents reference misuse?
        //System.out.println("yearFirst in MyDate " + (new Boolean(yearFirst)).toString());

        if(yearFirst) {
        	//yyy-mm-dd hh:ss
			SimpleDateFormat iFormat =  new SimpleDateFormat("yyyy-MM-dd hh:mm");
			Calendar oDate = Calendar.getInstance();
			oDate.setTime(iFormat.parse(str));
			
			year = oDate.get(Calendar.YEAR); //.getYear() + 1900;
			month = oDate.get(Calendar.MONTH) + 1; //.getMonth();
			day = oDate.get(Calendar.DAY_OF_MONTH); //.getDay();
			hour = oDate.get(Calendar.HOUR_OF_DAY); //.getHours();
			min = oDate.get(Calendar.MINUTE); //.getMinutes();
						
			/* mm removed
            tokens = new StringTokenizer(str, "-");
            token = tokens.nextToken();
            year = (int)(new Integer(token.trim()).intValue());

            token = tokens.nextToken();
            month = (int)(new Integer(token.trim()).intValue());
            //System.out.println("month " + month);

            token = tokens.nextToken();
            day = (int)(new Integer(token.trim()).intValue());
            // System.out.println("day " + day);
            
            tokens = new StringTokenizer(token.trim(), ":");
            token = tokens.nextToken();
            hour = (int)(new Integer(token.trim()).intValue());

            token = tokens.nextToken();
            min = (int)(new Integer(token.trim()).intValue());
            */
       }
       else  {
    	   	//mm-dd-yyyy hh:ss
			SimpleDateFormat iFormat =  new SimpleDateFormat("MM-dd-yyyy hh:mm");
			Calendar oDate = Calendar.getInstance();
			oDate.setTime(iFormat.parse(str));
			
			year = oDate.get(Calendar.YEAR); //.getYear() + 1900;
			month = oDate.get(Calendar.MONTH) + 1; //.getMonth();
			day = oDate.get(Calendar.DAY_OF_MONTH); //.getDay();
			hour = oDate.get(Calendar.HOUR_OF_DAY); //.getHours();
			min = oDate.get(Calendar.MINUTE); //.getMinutes();
			
    	   /* mm removed 7/26/06
            tokens = new StringTokenizer(str, "-");
            token = tokens.nextToken();
            month = (int)(new Integer(token.trim()).intValue());
            token = tokens.nextToken();
            day = (int)(new Integer(token.trim()).intValue());
            token = tokens.nextToken();
            year = (int)(new Integer(token.trim()).intValue());
            
            tokens = new StringTokenizer(token.trim(), ":");
            token = tokens.nextToken();
            hour = (int)(new Integer(token.trim()).intValue());

            token = tokens.nextToken();
            min = (int)(new Integer(token.trim()).intValue());
           */
       }
      // hour = 0; min = 0;
      }
    }

    public MyDate(String str1, String str2, String str3){
       StringTokenizer tokens;
       String token;

       if(str1.equals("today")){
        Date today = new Date();
        this.month = today.getMonth();
        this.day = today.getDay();
        this.year = today.getYear() + 1900;
        this.hour = today.getHours();
        this.min = today.getMinutes();
       }
       else {
        tokens = new StringTokenizer(str1, "/");
        token = tokens.nextToken();
        month = (new Integer(token.trim()).intValue());

        token = tokens.nextToken();
        day = (new Integer(token.trim()).intValue());

        token = tokens.nextToken();
        year = (new Integer(token.trim()).intValue());

        tokens = new StringTokenizer(str2, ":");
        token = tokens.nextToken();
        hour = (new Integer(token.trim()).intValue());

        token = tokens.nextToken();
        min = (new Integer(token.trim()).intValue());

        if(hour == 12){
          if(str3.equals("AM")) hour = 0;
          else hour = 12;
        }
        else if(str3.equals("PM")) 
          hour = hour + 12;
       }
    }

    public MyDate(MyDate date){
       this.year = date.getYear();
       this.month = date.getMonth();
       this.day = date.getDay();
       this.hour = date.getHour();
       this.min = date.getMin();
    }

    public int getMonth(){
        return this.month;
    }

    public int getDay(){
        return this.day;
    }

    public int getYear(){
        return this.year;
    }

    public int getHour(){
        return this.hour;
    }

    public int getMin(){
        return this.min;
    }

    public int getTotalMin(){
        return(this.hour * 60 + this.min);
    }

    public void setYear(int year){
        this.year = year;
    }

    public void setMonth(int month){
        this.month = month;
    }

    public void setDay(int day){
        this.day = day;
    }

    public void setHour(int hour){
        this.hour = hour;
    }

    public void setMin(int min){
        this.min = min;
    }

    public boolean after(MyDate when){
       if(this.year > when.year) return true;
       else if(this.year < when.year) return false;
       else{
           if(this.month > when.month) return true;
           else if(this.month < when.month) return false;
           else{
               if(this.day > when.day) return true;
               else if(this.day < when.day) return false;
               else{
                   if(this.hour > when.hour) return true;
                   else if(this.hour < when.hour) return false;
                   else{
                       if(this.min > when.min) return true;
                       else return false;
                   }
               }
           }
       }
    }

    public boolean before(MyDate when){
       if(this.year < when.year) return true;
       else if(this.year > when.year) return false;
       else{
           if(this.month < when.month) return true;
           else if(this.month > when.month) return false;
           else{
               if(this.day < when.day) return true;
               else if(this.day > when.day) return false;
               else{
                    if(this.hour < when.hour) return true;
                    else if(this.hour > when.hour) return false;
                    else{
                        if(this.min < when.min) return true;
                        else return false;
                    }
               }
           }
       }
    }

    public MyDate DateAfterDays(long days){ // Date in current object + days number of days...
       return(DateFromStart(DaysFromStart(new MyDate(getMonth(), getDay(), getYear(),getHour(), getMin()))+days));
    }

    public MyDate DateFromStart(long days){
       int day,month,year;
       long y400,d400,y100,d100,y4,d4,y1,d1,d;

       y400=days/(365*400+97);
       d400=days%(365*400+97);
       y100=d400/(365*100+24);
       d100=d400%(365*100+24);
       if (y100==4) {y100=3; d100=365*100+24;}
       y4=d100/(365*4+1);
       d4=d100%(365*4+1);
       if (y4==25) {y4=24; d4=365*4+1; /* impossible */}
       y1=d4/365;
       d1=d4%365;
       if (y1==4) {y1=3; d1=365;}
       year=(int)(y400*400+y100*100+y4*4+y1+1);
       for (month=0,d=0;month<12;d+=mdays[month],month++)
        if (month==1 && (year%4==0 && year%100!=0 || year%400==0))
            if (d+mdays[month]>d1) break;
            else d++;
        else
            if (d+mdays[month]>d1) break;
       day=(int)(d1-d+1);
       return(new MyDate(month+1, day, year, hour, min));
    }

    public long DateDiff(MyDate dateMax){
        return(DaysFromStart(dateMax) - DaysFromStart(this)); // difference in days between two dates.
    }

    public long DaysFromStart(MyDate date){ // uses the current object any? I don't think so...
        // start is 1/1/1

        int day,month,year,i;
        long days;

        year = date.getYear(); month = date.getMonth() -1; day = date.getDay(); // month - 1 since don't want
        // to count current month below

        // first compute days based on year and day
        days=(year-1)*365+(year-1)/4-(year-1)/100+(year-1)/400+day-1; // year -1 since starting at 1, extra
        // day every 4 years (leap), every 100 years (?), every 400 years. add day to add in for the present month
        // subtract 1 (why?)

        for (i=0;i<month;i++) days+=mdays[i]; // mdays is number of days in each month (actually 29 for feb!)
        //System.out.println("month " + month); //print


        if ((year%4==0 && year%100!=0 || year%400==0) && (month>=2)) days++; // also adding for leaps here? not done
        // above?

        return(days);

    }

    public MyDate DateAfterHours(long hours){
        hours += hour;
        if(hours >= 24){
            MyDate date = DateAfterDays(hours/24);
            date.setHour((int)hours % 24);
            return date;
        }
        else setHour((int)hours);
        return this;
    }

    public MyDate DateAfterMins(long minutes){
        minutes += min;
        if(minutes >= 60){
            MyDate date = DateAfterHours(minutes/60);
            date.setMin((int)minutes%60);
            return date;
        }
        else setMin((int)minutes);
        return this;
    }
/*
    public int HourDiff(MyDate dateMax){
        return((dateMax.getHour() - this.hour +24)%24);
    }
*/
    public long MinDiff(MyDate dateMax){
        long i = DateDiff(dateMax)*24*60 + dateMax.getTotalMin() - getTotalMin();
        if(i<0)
             i=0;
        return(i);
    }

    public void addDays(int daysToAdd) {

        // important note: if add more than one month of days this is presently wrong!

        if(daysToAdd > 31) {
            System.out.println("Warning: MyDate class doesn't yet support adding more than one month of days!");
        }

        //

        if(getDay() + daysToAdd > mdays[getMonth() - 1]) {// adding past end of month? note:
        // assuming feb has 29 days! must fix
            if(getMonth() == 12) { // if at end of year (and end of month)
                setMonth(1);
                setYear(getYear() + 1);
            }
            else { // not at end of year (though adding past end of month
                setMonth(getMonth() + 1);

            }

            setDay(getDay() + daysToAdd - (mdays[getMonth() - 1])); // again watch
                // february. setting to number of days added after beginning of new month
        }
        else { // just add the days it won't go past the end of the month
            setDay(getDay() + daysToAdd);
        }

    }

    public void subtractDays(int daysToSubtract) {

        // important note: if subtract more than one month of days this is presently wrong!

        if(daysToSubtract > 31) {
            System.out.println("Warning: MyDate class doesn't yet support subtracting more than one month of days!");
        }

        //

        if(getDay() - daysToSubtract < 1) {// subtracting past end of month? note:
        // assuming feb has 29 days! must fix
            if(getMonth() == 1) { // if at end of year (and end of month)
                setMonth(12);
                setYear(getYear() - 1);
            }
            else { // not at end of year (though subtracting past end of month
                setMonth(getMonth() - 1);

            }

            setDay(getDay() - daysToSubtract + (
            getMonth() > 1 ?mdays[getMonth() - 2]:mdays[11])); // again watch
                // february. setting to number of days subtracted after beginning of new month
        }
        else { // just subtract the days it won't go past the end of the month
            setDay(getDay() - daysToSubtract );
        }

    }

    public void print() {
        System.out.println(printString());
    }
    
    public String printString() {
        String datestring = getMonth() + "/" + getDay() + "/" + getYear();
        return datestring;
    }
}