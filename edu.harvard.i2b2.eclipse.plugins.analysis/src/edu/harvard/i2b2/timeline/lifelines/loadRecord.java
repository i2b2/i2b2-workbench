/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *  	Mike Mendis (MGH)
 *		
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;

/*
This class reads in a textfile and parses it into
records and into filter definitions.  Each record in the
textfile has lists of attributes and attribute values.  All
of these define filters which are used to filter the records.
The records produced here maintain a list of their attributes,
and the filters are made from each attribute and each attribute
value.
*/

public class loadRecord{

    //public Date today;
    private Hashtable recordTable;
    private Hashtable facetList;
    String URLString;
   //private MyDate max_date = new MyDate(1,1,0); // was there a year zero?
    static private MyDate max_date = new MyDate(1,1,1,0,0);

    static private MyDate min_date = new MyDate(12,31,9999,0,0);
    private Hashtable storyList; // old idea, not used?
    private static MyDate today;
    private String name;
    private String gender;
    private int age;
    private String moreinfo;   
    private String pictureFile;
    private facet currentFacet;
    private boolean useCommas = true;
    private BufferedReader d;   
    private StringTokenizer line_tokens;
    private String token, cause;
    private MyDate start_date, end_date;
    private int penWidth, tupleID;

    // the simple contructor, called from the applet
    // init(), simply passes the complete URL of the
    // source text data file
    public loadRecord(String theURLString,String dataString) 
    {
        URLString = theURLString;
        Reader file= null;
        boolean fromParam = false;

		try {
		    // try to get an input stream

		    boolean onWeb = true;

		    if(onWeb && !fromParam) {

		    URL theURL = new URL(URLString);
		    file = new InputStreamReader(theURL.openStream());

		    }

            if(!onWeb && !fromParam) {
		    file = new FileReader(URLString);
		    } // also need file of type "width.db" in record.java

		    if(fromParam)
		        file = new StringReader(dataString);



            //if(!fromParam) {
		    d = new BufferedReader(file);
		    
		    initLoadRecord(d, dataString);
		    d.close();
		    d = null;
		}
		catch (Exception e) {
		    System.out.println("an exception "+e);
		}

    }
    public loadRecord(BufferedReader d, String dataString){
    	initLoadRecord(d, dataString);
    }
	private void initLoadRecord(BufferedReader inputReader, String dataString){

        record.yearFirst = true; // default, need to be reset each time load
        // see below for older files

        d = inputReader;
        //today = new MyDate(9,17,1997); // just in case not read in. 
        today = new MyDate(9,17,2003,0,0); // just in case not read in. // snm0
        max_date = new MyDate(1,1,1,0,0);
        min_date = new MyDate(12,31,9999,0,0);

        // the table to return
        recordTable = new Hashtable();

        int recID = 0;
        tupleID = 0;

        try {

		    String line;

		    String delimeter = new String("\t\n\r"); // didn't help too much (why? does it really go back to default
		    // unlike what api says?

            //first read the date, actually do this in the loop, hope that is read first

            /*line = d.readLine();

            line_tokens = new StringTokenizer(line);

            // read the "today"

            token = line_tokens.nextToken();

            if(!(token.equals("%today")))
                System.out.println("today is not in expected place in file");

            today = new MyDate(line_tokens.nextToken());

            today.print(); */

            //

            // set default value for today in case not read in correctly in file:

            //today = new MyDate(1,1,1998);
            today = new MyDate(1,1,2003,0,0); // snm0


            // read one line
		    while ((line = d.readLine()) != null) { // wait! this just reads a line at time....

		        // is it a key/value pair???
		        // inputs are either key/value pairs or a single term

             if(!line.equals("")) { // don't want to read blank lines, fix indendation sometime

			    if(useCommas)
			        line_tokens = new StringTokenizer(line," ,",true); // also loads in tokens, note the space
			        // that is there before the comma in " ,"
			    else
			        line_tokens = new StringTokenizer(line);

			    token = readBlanks(line_tokens);

			    if (token.equals("%facet")) {
    			    if(currentFacet != null) // 1/7/98
				        currentFacet.layout(); // doing layout for previous facet...

			        if(useCommas)
				        token = line_tokens.nextToken(); // adding extra for comma

			        String name = readBlanks(line_tokens);

			        if(useCommas)
				        token = line_tokens.nextToken(); // adding extra for comma

			        token = line_tokens.nextToken(); // reading facet color. Not presently used.
                                myColor backgroundColor = new myColor(token);

                    if(useCommas)
                        token = line_tokens.nextToken();

                    String openString = new String("yes");
                    if(line_tokens.hasMoreTokens()) {
                    openString = readBlanks(line_tokens);
                    }
                    boolean open;

                    if (openString.equals("yes"))
                        open = true;
                    else
                        open = false;

			        facetList = new Hashtable();
			        storyList = new Hashtable();
                    currentFacet = new facet(name,facetList,backgroundColor.getColor(),open);

			        recordTable.put(new Integer(recID++),currentFacet);
		            tupleID = 0;

			    }
			    else if (token.equals("%c")) {
			        //while(!(token.equals("endcomment")))
			            token = line_tokens.nextToken("\n");

			    }
			    else if (token.equals("%agg")) {
			        load_aggregate(true);
			    }
			    else if (token.equals("%today")) {
        	        if(useCommas)
				        token = line_tokens.nextToken(); // adding extra for comma

			        today = new MyDate(line_tokens.nextToken(","));
			        if(max_date.before(today)) max_date = new MyDate(today.getMonth(),today.getDay(),today.getYear(),today.getHour(),today.getMin());
			    }
			    else if (token.equals("%person")) {
			        if(useCommas)
			            token = line_tokens.nextToken(" ,"); // adding extra for comma

                    name = new String(readBlanks(line_tokens,",")); // no blank to read names with spaces

                    if(useCommas)
			            token = line_tokens.nextToken(); // adding extra for comma

			        gender = new String(readBlanks(line_tokens," ,")); // added blank back in for readBlanks (age?)

			        if(useCommas)
			            token = line_tokens.nextToken(" ,"); // adding extra for comma

			        age = (new Integer(readBlanks(line_tokens))).intValue();

			        if(useCommas)
			            token = line_tokens.nextToken(); // adding extra for comma

                    // 3/28/98
			        moreinfo = new String(readBlanks(line_tokens,","));
			        if(useCommas)
			            token = line_tokens.nextToken(" ,");

			        pictureFile = new String(readBlanks(line_tokens));

			    }
                else if (token.equals("%end")) {
                	if (currentFacet!=null)
                		currentFacet.layout(); 
                     try {d.close();} catch (Exception e) {}
                     break;
                }
                else if (token.equals("%beforeSeptember1997"))
                    record.yearFirst = false; // for different date form in input file
			}

			} // end "if" for blank lines
		}

		catch (Exception e) {
		    System.out.println("an exception "+e);
		}
	}

    
    /*public loadRecord(String theURLString,String dataString) {

        record.yearFirst = true; // default, need to be reset each time load
        // see below for older files

        //today = new MyDate(9,17,1997); // just in case not read in. 
        today = new MyDate(9,17,2003); // just in case not read in. // snm0
        max_date = new MyDate(1,1,1);
        min_date = new MyDate(12,31,9999);
        URLString = theURLString;

        // the table to return
        recordTable = new Hashtable();
        InputStream file= null;

        boolean fromParam = false;

        int recID = 0;
        tupleID = 0;

        int storyID = 0;

	    // tokenizer used to split key/value pairs
        StringTokenizer period;
        String dose;

		try {
		    // try to get an input stream

		    boolean onWeb = true;

		    if(onWeb && !fromParam) {

		    URL theURL = new URL(URLString);
		    file = theURL.openStream();

		    }

            if(!onWeb && !fromParam) {
		    file = new FileInputStream(URLString);
		    } // also need file of type "width.db" in record.java

		    if(fromParam)
		        file = new StringBufferInputStream(dataString);



            //if(!fromParam) {
		    d = new DataInputStream(file);
		    //}
           /* else {
//		    d = new StringBufferInputStream(dataString);
		    }*/

		    /*String line;

		    String delimeter = new String("\t\n\r"); // didn't help too much (why? does it really go back to default
		    // unlike what api says?

            //first read the date, actually do this in the loop, hope that is read first

            /*line = d.readLine();

            line_tokens = new StringTokenizer(line);

            // read the "today"

            token = line_tokens.nextToken();

            if(!(token.equals("%today")))
                System.out.println("today is not in expected place in file");

            today = new MyDate(line_tokens.nextToken());

            today.print(); */

            //

            // set default value for today in case not read in correctly in file:

            //today = new MyDate(1,1,1998);
            /*today = new MyDate(1,1,2003); // snm0


            // read one line
		    while ((line = d.readLine()) != null) { // wait! this just reads a line at time....

		        // is it a key/value pair???
		        // inputs are either key/value pairs or a single term

             if(!line.equals("")) { // don't want to read blank lines, fix indendation sometime

			    if(useCommas)
			        line_tokens = new StringTokenizer(line," ,",true); // also loads in tokens, note the space
			        // that is there before the comma in " ,"
			    else
			        line_tokens = new StringTokenizer(line);

			    token = readBlanks(line_tokens);

			    if (token.equals("%facet")) {
    			    if(currentFacet != null) // 1/7/98
				        currentFacet.layout(); // doing layout for previous facet...

			        if(useCommas)
				        token = line_tokens.nextToken(); // adding extra for comma

			        String name = readBlanks(line_tokens);

			        if(useCommas)
				        token = line_tokens.nextToken(); // adding extra for comma

			        token = line_tokens.nextToken(); // reading facet color. Not presently used.
                                myColor backgroundColor = new myColor(token);

                    if(useCommas)
                        token = line_tokens.nextToken();

                    String openString = new String("yes");
                    if(line_tokens.hasMoreTokens()) {
                    openString = readBlanks(line_tokens);
                    }
                    boolean open;

                    if (openString.equals("yes"))
                        open = true;
                    else
                        open = false;

			        facetList = new Hashtable();
			        storyList = new Hashtable();
                    currentFacet = new facet(name,facetList,backgroundColor.getColor(),open);

			        recordTable.put(new Integer(recID++),currentFacet);
		            tupleID = 0;
		            storyID = 0;

			    }
			    else if (token.equals("%c")) {
			        //while(!(token.equals("endcomment")))
			            token = line_tokens.nextToken("\n");

			    }
			    else if (token.equals("%agg")) {
			        load_aggregate(true);
			    }
			    else if (token.equals("%today")) {
        	        if(useCommas)
				        token = line_tokens.nextToken(); // adding extra for comma

			        today = new MyDate(readBlanks(line_tokens));
			        if(max_date.before(today)) max_date = new MyDate(today.getMonth(),today.getDay(),today.getYear());
			    }
			    else if (token.equals("%person")) {
			        if(useCommas)
			            token = line_tokens.nextToken(" ,"); // adding extra for comma

                    name = new String(readBlanks(line_tokens,",")); // no blank to read names with spaces

                    if(useCommas)
			            token = line_tokens.nextToken(); // adding extra for comma

			        gender = new String(readBlanks(line_tokens," ,")); // added blank back in for readBlanks (age?)

			        if(useCommas)
			            token = line_tokens.nextToken(" ,"); // adding extra for comma

			        age = (new Integer(readBlanks(line_tokens))).intValue();

			        if(useCommas)
			            token = line_tokens.nextToken(); // adding extra for comma

                    // 3/28/98
			        moreinfo = new String(readBlanks(line_tokens,","));
			        if(useCommas)
			            token = line_tokens.nextToken(" ,");

			        pictureFile = new String(readBlanks(line_tokens));

			    }
                else if (token.equals("%end")) {
                	if (currentFacet!=null)
                		currentFacet.layout(); 
                     try {d.close();} catch (Exception e) {}
                     break;
                }
                else if (token.equals("%beforeSeptember1997"))
                    record.yearFirst = false; // for different date form in input file
			}

			} // end "if" for blank lines
		}

		catch (Exception e) {
		    System.out.println("an exception "+e);
		}
    }*/

    public aggregate load_aggregate(boolean toplevel)    {  // toplevel distinguishes the aggregate from sub-aggregates
        String token;
        String line;
        
	    aggregate agg = new aggregate("aggregate"); // parameter is genrecord type
		boolean summary = false;   

        try {
	        if(useCommas)
		        token = line_tokens.nextToken(","); // adding extra for comma, remove spaces (for this line) as a delimeter
            token = line_tokens.nextToken(); // reading in type. not presently used

    	    if(useCommas)
	            token = line_tokens.nextToken(" ,"); // adding extra for comma, remove spaces (for this line) as a delimeter, put in back in for leading spaces!
            else
                token = line_tokens.nextToken();
                
            token = readBlanks(line_tokens);
	        int number = Integer.parseInt(token);

	        if(useCommas)
		        token = line_tokens.nextToken(",");

    		token = line_tokens.nextToken(); // reading in whether has a summary. not used yet
        	if (token.equals("yes"))
	            summary = true;

    		for(int k=0;k<((summary)? number+1:number);k++) {  
                line = d.readLine();
            
                if(useCommas)
                    line_tokens = new StringTokenizer(line," ,",true);
                else
                    line_tokens = new StringTokenizer(line);
                    
                token = readBlanks(line_tokens);

                while(token.equals("%c")) {    // allow %c to occur inside %agg section
                    line = d.readLine();
                    token = readBlanks(line_tokens);
                }

                if (token.equals("%-"))     {
                    if(useCommas)
				        token = line_tokens.nextToken(" ,");
			        start_date = new MyDate(line_tokens.nextToken(","));

			        if(useCommas)
				        token = line_tokens.nextToken(); // adding extra for comma

			        String couldBeComma = readBlanks(line_tokens);
                        
			        if(couldBeComma.equals(","))
    			        end_date = new MyDate(start_date.getMonth(),start_date.getDay(),start_date.getYear(),start_date.getHour(),start_date.getMin());
			        else
		    	        end_date = new MyDate(couldBeComma);

			        if(useCommas)
				        token = line_tokens.nextToken(); // adding extra for comma

			        myColor tempColor = new myColor(readBlanks(line_tokens));
			        Color temp = tempColor.getColor();

			        if(useCommas)
				        token = line_tokens.nextToken(); // adding extra for comma

				    if(useCommas) {
                        token = readBlanks(line_tokens,"p, ");  // delimter is ' or , or space
				        penWidth = Integer.parseInt(line_tokens.nextToken()); // reading p before width*/
				    }
			        else
			            penWidth = Integer.parseInt(line_tokens.nextToken());

			        if(useCommas)
				        token = line_tokens.nextToken(","); // adding extra for comma

			        cause = line_tokens.nextToken();

		            if(useCommas)
    		            token = line_tokens.nextToken(" ,");

	                couldBeComma = readBlanks(line_tokens);

	                if(couldBeComma.equals(","))
	                    token = "";
	                else
	                    token = couldBeComma;

	                String theUrl = new String(token);

                    if(useCommas)
		                token = line_tokens.nextToken();    // comma
                    
	                token = readBlanks(line_tokens,"\"");  
	                
	                token = readBlanks(line_tokens,"\"=");
	                Hashtable attrList = new Hashtable(); 
	                if (!token.equals("\""))   {
				        // parse the key to the hashtable
				        String hashkey = new String(token);
				        token=line_tokens.nextToken();     // =
				        token=readBlanks(line_tokens,"\",");
				        // parse the value to the hashtable
				        attrList.put(hashkey, token);
				        token = line_tokens.nextToken(); 
				        while (!token.equals("\"")) {
        				    token = readBlanks(line_tokens,"\"=");  // allow the space in the key
        				    hashkey = new String(token);
				            token=line_tokens.nextToken();     // =
				            token=readBlanks(line_tokens,"\",");  // allow the space in the value
				            attrList.put(hashkey, token);
				            token = line_tokens.nextToken(); 
				        }
	                }

    			    storyRecord aRecord = new storyRecord("story", cause, start_date, end_date,temp,penWidth,theUrl,attrList,line);

	    			if (summary && k==0)    {  // summary record
		    		    agg.addSummary(aRecord);
			    	    agg.setLabelString(aRecord.getCause());
				    }
					else {
					    agg.addGen(aRecord);
					    if (!summary) agg.setLabelString(cause); // 3/10/98 Julia, don't seem need it. Orig comment: o.k. since right now using last label
					}

		            if(min_date.after(start_date)) min_date = start_date;
			        if(max_date.before(end_date)) max_date = end_date;
			   }  // end if %-
			   else if (token.equals("%agg"))   {
			        aggregate recursive_agg = load_aggregate(false);
			        agg.addGen(recursive_agg);
			   }   // end if %agg
            }   // for loop
            if (toplevel)   {
                currentFacet.addEventObject(agg);
        	    facetList.put(new Integer(tupleID++), agg);
        	}
        }
        catch (Exception e) {
		    System.out.println("an exception "+e);
		}
    	
    	return agg;
    }

    public Hashtable getRecordTable(){
        return recordTable;
    }

    static public MyDate getMinDate(){
        return min_date;
	}

	static public MyDate getMaxDate(){
	    return max_date;
	}

	static public MyDate getToday() {

	    return today;

	} // see below, protecting the data here...

	public String getName() {

	    return name;

	}

	public String getGender() {

	    return gender;

	}

	public int getAge() {

	    return age;

	}

	public String getMoreInfo()  { 
	    return moreinfo;
	}

	public String getPictureFile() {

	    return pictureFile;

	}

	public String readBlanks(StringTokenizer line_tokens) {

	    String token = new String(" ");

        while(token.equals(" ")) {
            token = line_tokens.nextToken(); // read blank spaces at beginning of line plus first token?
	    }
	    return token;
	}

    public String readBlanks(StringTokenizer line_tokens,String delimeter) {

	    String token = new String(" ");

        while(token.equals(" ")) {
		    token = line_tokens.nextToken(delimeter); // read blank spaces at beginning of line plus first token?
		}
	    return token;
	}

}

