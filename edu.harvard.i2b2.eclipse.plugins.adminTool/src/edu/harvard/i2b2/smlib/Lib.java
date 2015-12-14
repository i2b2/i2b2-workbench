/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 *     Shawn Murphy
 */
/**
/* Big bunch of Static funtions
/* (c) 1998 Shawn Murphy
/* Last Modified 9148
/*
/* --- Misc functions
/* String getJavaVersion()
/* --- JScript functions
/* boolean isUnitNumber(String s)
/* isMydate(String s)
/* removeDashes(String s)
/* makeEuropeanDateFromMydate(String s)
/* --- Parser funtions
/* String ReturnSubFromSuper(String SuperString, int SubNumber, char Delimiter)
/* int indexOfNum(String theString, int theNum, char theChar)
/* void remove(int CharNum, StringBuffer theString) 
/* int PullOut(String left, String right, StringBuffer theSBuffer, StringBuffer pulledOut, int fromIndex)
/* int FindAndReplace(String find, String replace, StringBuffer theSBuffer, int fromIndex)
/* String StrFindAndReplace(String find, String replace, String theString) 
/* byte[] IntTo6Bytes(int theInt) throws NumberFormatException 
/* byte[] IntTo2Bytes(int theInt) throws NumberFormatException 
/* byte IntToByte(int theInt) throws NumberFormatException 
/* String getPostValue(String name, String post) 
/* String toTwoPlace(int theInteger)
/* --- Assorted funtions 
/* void Wait(int seconds) 
/* void Wait(Component object, int seconds) 
/* String statusPercent(double seconds, double projectedSeconds) 
/* void WaitForChar() 
/* Frame getFrame(Component component) 
/* Applet getApplet(Component component) 
/* Panel getPanel(Component component) 
/* void sort(String a[]) throws Exception 
/* String getDate()
/* String newRandomString(int theLength)
/* byte[] copyByteArray(byte[] byteArray) {
/* --- File funtions
/* int BUFSIZE=4096;
/* void copy(File src, File dest) throws IOException {
/* String oldRead(File src) throws IOException {
/* void write(File dest, String outString) throws IOException {
/* String read(File theFile) throws IOException {
/* String openFile(Component theComponent) throws IOException 
/* File autoGetFile(String fileName, String absolutePath, String prompt, boolean quiet, Component theComponent) {
/* RandomAccessFile autoGetRAFile(String fileName, String absolutePath, String prompt, boolean quiet, Component theComponent) throws Exception {
/* String returnBaseFileNameWithoutSuffix(String fileName) {
/* String returnBaseFileNameWithSuffix(String fileName) {
/* String returnBaseFileName(String fileName, boolean wantsSuffix) {
/* --- Gridbag funtions
/* void addVertCmp(
		Container toContainer, 
		GridBagLayout usingGridBagLayout, 
        GridBagConstraints usingGridBagConstraints, 
        Component theComponent, 
        int cellsWide, 
        double widthWeight)
/* void addHorizCmp(
		Container toContainer, 
		GridBagLayout usingGridBagLayout, 
        GridBagConstraints usingGridBagConstraints, 
        Component theComponent, 
        int cellsTall, 
        double heightWeight)
/* void addPlainVertComp(Container toContainer, 
		Container toContainer, 
		Component theComponent,
		GridBagLayout theGridBagLayout,
		double widthWeight) 
/* void addPlainHorizComp(Container toContainer, 
		Container toContainer, 
		Component theComponent,
		GridBagLayout theGridBagLayout,
		double heightWeight)
/* --- Error functions
	public static void Message(String s) {
	public static void TMessage(String s) {
	public static void Error(String s) {
	public static void TError(String s) {

/*/

package edu.harvard.i2b2.smlib;

import java.applet.Applet;
import java.awt.Frame;
import java.awt.Component;
import java.awt.Container;
import java.awt.Panel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FileDialog;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.EOFException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.lang.Runtime;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Lib {
  // error handling
  private static String sModule = "snm.library.Lib";
  /**
	* This class shouldn't be instantiated.
	*/
	private Lib() {}
  /**
  * getJavaVersion - static method to return the current Java complient
  * version of the library.
  * @return a String that is "1.02" or "1.1"
  */  
  public static String getJavaVersion() {
    return "1.1";
  }
  /** 
  * getFirstWord - returns the fisrt word in a string.
  * Anything other that a letter or a number is a delimiter.
	*/
  public static String getFirstWord(String theString) throws Exception {
    if ((theString==null)||(theString.length()==0)) return "";
    StringBuffer returnString = new StringBuffer(512);
    boolean gotToAWord = false;
    for (int i=0; i<theString.length(); i++) {
      if (Character.isLetterOrDigit(theString.charAt(i))
          || (theString.charAt(i)=='_')
          || (theString.charAt(i)=='-') 
          || (theString.charAt(i)=='\\')
          || (theString.charAt(i)=='/') 
          ) {
        returnString.append(theString.charAt(i));
        gotToAWord = true;
      }
      else if (gotToAWord == true) {
        break;
      }
      else {
        continue;
      }
    }
    return returnString.toString();
  }
	/** test to see if string is a number (by what digits are in string)
	* @param s the String which is tested to see if it is a number
	* @return boolean that signifies if s is a number
	*/
	public static boolean isNumber(String s) {
  	if ((s == null) || (s.length() == 0)) return false; // blanks are not numbers
  	char c; 
  	for (int i=0; i<s.length()-1; i++) {
  	  c = s.charAt(i);
  		if ((c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5') 
  		     & (c != '6') & (c != '7') & (c != '8') & (c != '9') & (c != '0') 
  		     & (c != '.')) 
  		  return false;
  	}
	  return true;
	} 
	/** test to see if string is a unit number (can have a '-', but not a '.')
	* @param s the String which is tested to see if it is a unit number
	* @return boolean that signifies if s is a unit number
	*/
	public static boolean isUnitNumber(String s) {
  	if ((s == null) || (s.length() == 0)) return false; // blanks are not unit numbers
  	char c; 
  	for (int i=0; i<s.length()-1; i++) {
  	  c = s.charAt(i);
  		if ((c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5') 
  		     & (c != '6') & (c != '7') & (c != '8') & (c != '9') & (c != '0') 
  		     & (c != '-')) 
  		  return false;
  	}
	  return true;
	}
	/** test to see if string is my date format ("a128" kind)
	* @param s the String which is tested to see if it is in my date format
	* @return boolean that signifies if s is in my date format
	*/
	public static boolean isMydate(String s) {
  	if ((s == null) || (s.length() == 0)) return false; // blanks are not dates
  	if (s.length() != 4) return false;
  	char c; 
	  c = s.charAt(0);
		if ((c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5') 
		     & (c != '6') & (c != '7') & (c != '8') & (c != '9')  
		     & (c != 'a') & (c != 'A') & (c != 'b') & (c != 'B') & (c != 'c')
		     & (c != 'C')) 
		  return false;
  	for (int i=1; i<4; i++) {
  	  c = s.charAt(i);
  		if ((c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5') 
  		     & (c != '6') & (c != '7') & (c != '8') & (c != '9') & (c != '0')) 
  		  return false;
  	}
	  return true;
	}
	/** remove dashes from a String (mostly for unit number entries)
	* @param s the String which is to have dashes removed
	* @return String that has dashes removed
	*/
  public static String removeDashes(String s) {
  	if ((s == null) || (s.length() == 0)) return "";
  	return StrFindAndReplace("-","",s);
	}
	/** makes a date in the form 1999-12-25 from my date in form "c259"
	* good for alphabetic sorting.
	* @param s the String in my date format
	* @return String the european date format
	*/
  public static String makeEuropeanDateFromMydate(String s) {
  	if ((s == null) || (s.length() == 0)) return "";
  	if (!isMydate(s)) return "";
  	String dateSeparator = "-";
  	StringBuffer europeanDate = new StringBuffer(10);
  	char c = s.charAt(3);
  	int theYear = Integer.parseInt(s.substring(3,4));
  	if (theYear >= 6) 
  	  europeanDate.append("199");
  	else
  	  europeanDate.append("200");
  	europeanDate.append(c);
  	europeanDate.append(dateSeparator);
  	c = s.charAt(0);
  	if ((c == 'a') | (c == 'A'))
  	  europeanDate.append("10");
  	else if ((c == 'b') | (c == 'B'))
  	  europeanDate.append("11");
  	else if ((c == 'c') | (c == 'C'))
  	  europeanDate.append("12");
  	else {
  	  europeanDate.append("0");
  	  europeanDate.append(c);
  	}
  	europeanDate.append(dateSeparator);
  	europeanDate.append(s.charAt(1));
  	europeanDate.append(s.charAt(2)); 	
  	return europeanDate.toString();
	}
	/**
	/* ReturnSubFromSuper - returns a substring between two delimiters from
	/*   SuperString, where SubNumber is the #th occurence of the delimiter.
	/*/
	public static String ReturnSubFromSuper(String SuperString, int SubNumber, char Delimiter) {
		int StartOfSegment=0;
		int EndOfSegment=0;

		if (SuperString == null) return "";
		if (SuperString.length() == 0) return "";
		if (Delimiter == '\0') return "";
		if (SubNumber < 0) SubNumber=0;

		StartOfSegment = indexOfNum(SuperString, SubNumber, Delimiter);
		if (SubNumber == 0) StartOfSegment = -1;
		else if (StartOfSegment == -1) return "";

		EndOfSegment = indexOfNum(SuperString, SubNumber+1, Delimiter);
		if (EndOfSegment == -1) {
			EndOfSegment = SuperString.length();
		}
		if ((EndOfSegment - StartOfSegment) <= 1) return "";
		return 	SuperString.substring(StartOfSegment+1,EndOfSegment);
	}

  public static String ReturnSubFromSuperWithQuotes(String SuperString, int SubNumber, char Delimiter) {
    String beforeQuotesRemoved = ReturnSubFromSuper(SuperString, SubNumber, Delimiter);
    if ((beforeQuotesRemoved==null)||(beforeQuotesRemoved.length()==0)) return "";
    if (beforeQuotesRemoved.charAt(0)=='\"') {
      return beforeQuotesRemoved.substring(1,beforeQuotesRemoved.length()-1);
    }
    return beforeQuotesRemoved;
  }
  
	/**
	/* indexOfNum - returns the index where theChar occurs
	/*   on the theNum occurence. If theChar does not occur,
	/*   or theNum is 0, then -1 is returned.
	/*/
	public static int indexOfNum(String theString, int theNum, char theChar) {
		int count;
		int tempIndex;

		if (theNum <= 0) return -1;
		if (theString == null) return -1;
		if (theChar == '\0') return -1;
		if (theString.length() == 0) return -1;

		tempIndex=-1;
		for (count=1; count<theNum+1; count++) {
			tempIndex = theString.indexOf(theChar, tempIndex+1);
			if (tempIndex==-1) return -1;
		}
		return tempIndex;
	}

  /**
   * countChar(String) - Counts the number of times a certain character occurs
   * in a string.
	 */
	public static int countChar(String theString, char theChar) {
		int count;
		int tempIndex;

		if (theString == null) return -1;
		if (theChar == '\0') return -1;
		if (theString.length() == 0) return -1;

		int numChars = 0;
		for (count=0; count<theString.length(); count++) {
			if (theString.charAt(count) == theChar) numChars++;
		}
		return numChars;
	}

	/**
	/* remove - removes a character at postition CharNum
	/*   (zero based) from theString.
	/*/
	public static void remove(int CharNum, StringBuffer theString) {
		int i=0;

		if ((theString == null) | (theString.length() == 0)) return;
		if ((CharNum < 0) | (theString.length() < CharNum)) return;

		for (i=CharNum;i<theString.length()-1;i++) {
			theString.setCharAt(i,theString.charAt(i+1));
		}
		theString.setLength(theString.length() - 1);
		return;
	}
  /**
	/* PullOut - returns String between left and right in String 
	/*   theString, and returns it, starts looking at fromIndex.
	/*/
	public static String PullOut(String left, String right,
		String theString, int fromIndex) {
    
    if ((theString==null)||(theString.length()==0)) return "";
		if ((fromIndex < 0)||(fromIndex > theString.length())) return "";
		StringBuffer theBuf = new StringBuffer(theString);
    StringBuffer pulledOut = new StringBuffer();
    PullOut(left, right, theBuf, pulledOut, fromIndex);
    if ((pulledOut==null)||(pulledOut.length()==0)) return "";
    return pulledOut.toString();
	}
	/**
	/* PullOut - finds stuff between left and right in StringBuffer 
	/*   theSBuffer, and returns it in pulledOut,
	/*   starts at fromIndex, returns index of left find.
	/*/
	public static int PullOut(String left, String right,
		StringBuffer theSBuffer, StringBuffer pulledOut, int fromIndex) {

		String interString;
		int leftIndex, farLeftIndex, rightIndex;

		if (right == null) return -1;
		if (left == null) return -1;
		if (theSBuffer == null) return -1;
    if (pulledOut == null) return -1;
		if (theSBuffer.length() < (right.length()+left.length())) return -1;
		if ((fromIndex < 0)||(fromIndex > theSBuffer.length())) return -1;
		if (pulledOut.length() != 0) pulledOut.setLength(0);

		interString = theSBuffer.toString();
		if (left.length() == 0) leftIndex = 0;
		else {
			leftIndex = interString.indexOf(left,fromIndex);
			if (leftIndex == -1) return -1;
		}
		farLeftIndex = leftIndex + left.length();
		if (right.length() == 0) rightIndex = theSBuffer.length();
		else {
			rightIndex = interString.indexOf(right,farLeftIndex);
			if (rightIndex == -1) return -1;
		}
		pulledOut.append(interString.substring(farLeftIndex,rightIndex));
		return leftIndex;
	}

	/**
	/* StrPullAndCutOut - finds stuff between sLeft and sRight in String
	/*   sMainString, and returns it, starts at fromIndex.
	/*
	/*  @param bIncludeEdges boolean, true if edges should be in the cut string.
	/*  @param sLeft String, the string to find on the left
	/*  @param sRight String, the string to find on the right
	/*  @param sMainString String, the string to pull stuff out of
	/*  @param fromIndex int, the integer that the search should start from
	/*  @return String, the string with the pulled out stuff
	/*  @exception the original sMainString is returned
	/*/
	public static String StrPullAndCutOut(boolean bIncludeEdges, String sLeft, String sRight,
		String sMainString, int fromIndex) {
		if ((sMainString == null) || (sMainString.length() == 0)) return sMainString;
		StringBuffer theSBuffer = new StringBuffer(sMainString);
		StringBuffer pulledOut = new StringBuffer(32);
    int iReturn = PullAndCutOut(bIncludeEdges, sLeft, sRight, theSBuffer, pulledOut, fromIndex);
    if (iReturn == -1) return sMainString;
    return theSBuffer.toString();
	}

	/**
	/* PullAndCutOut - finds stuff between left and right in StringBuffer 
	/*   theSBuffer, and returns it in pulledOut, starts at
	/*   fromIndex, returns index of left find.
	/*
	/*  @param bIncludeEdges boolean, true if edges should be in the cut string.
	/*  @param left String, the string to find on the left
	/*  @param right String, the string to find on the right
	/*  @param theSBuffer StringBuffer, the string to pull stuff out of
	/*  @param pulledOut StringBuffer, the string with the pulled out stuff
	/*  @param fromIndex int, the integer that the search should start from
	/*  @return int, the (left) place that the string was found
	/*/
	public static int PullAndCutOut(boolean bIncludeEdges, String left, String right,
		StringBuffer theSBuffer, StringBuffer pulledOut, int fromIndex) {
		String interString;
		int leftIndex, farLeftIndex, rightIndex, farRightIndex;
    // weed out bad input values
		if (theSBuffer == null) return -1;
    if (pulledOut == null) return -1;
		if (theSBuffer.length() < (right.length()+left.length())) return -1;
		if ((fromIndex < 0)||(fromIndex > theSBuffer.length())) return -1;
		if (pulledOut.length() != 0) pulledOut.setLength(0);
    //  find the string to pull out
		interString = theSBuffer.toString();
		/// find the left edge
		if (left == null) leftIndex = 0;
		if (left.length() == 0) leftIndex = 0;
		else {
			leftIndex = interString.indexOf(left,fromIndex);
			if (leftIndex == -1) return -1;
		}
		if (bIncludeEdges) {  // keep the left string in the cutout
		  farLeftIndex = leftIndex;
		}
		else {
		  farLeftIndex = leftIndex + left.length();
		}
		/// find the right edge
		if (right == null) rightIndex = theSBuffer.length();
		if (right.length() == 0) rightIndex = theSBuffer.length();
		else {
			rightIndex = interString.indexOf(right,farLeftIndex);
			if (rightIndex == -1) return -1;
		}
		if (bIncludeEdges) {  // keep the right string in the cutout
		  farRightIndex = rightIndex + right.length();
		}
		else {
		  farRightIndex = rightIndex;
		}
		// make the pulled out StringBuffer to return
		pulledOut.append(interString.substring(farLeftIndex,farRightIndex));
	  // cut out the string from the StringBuffer
		int iAmountCutOut = farRightIndex - farLeftIndex;
		for (int i=farLeftIndex;i<theSBuffer.length()-iAmountCutOut;i++) {
			theSBuffer.setCharAt(i,theSBuffer.charAt(i+iAmountCutOut));
		}
		int newLength = theSBuffer.length() - iAmountCutOut;
	  theSBuffer.setCharAt(newLength,(char)(0));
		theSBuffer.setLength(newLength);
		return farLeftIndex;
	}
  /** 
  * Cuts out a string with index 'from' until one character
  * before index 'to' and returns the modified string.  On an 
  * error, the return is a blank. 
	* @param from int index to start the cut
	* @param to int one minus the index to end the cut
	* @param sliceString String from which to cut
	* @return String after the cut is made
	*/
	public static String CutOut(int from, int to, String sliceString) {
	  if ((sliceString == null)||(sliceString.length()==0)) return "";
	  if (to <= from) return "";
	  if (from < 0) return "";
	  if (to > sliceString.length()) return "";
    // cut out the string
    StringBuffer theCutOut = new StringBuffer(sliceString);
    int length = to - from;
		for (int i = from; i < sliceString.length()-length; i++) {
			theCutOut.setCharAt(i,sliceString.charAt(i+length));
		}
		theCutOut.setLength(sliceString.length() - length);
		return theCutOut.toString();
	}

	/**
	/* FindAndReplace - finds and replaces in StringBuffer theSBuffer,
	/*   starts at fromIndex, returns index of find.
	/*/
	public static int FindAndReplace(String find, String replace,
		StringBuffer theSBuffer, int fromIndex) {

		String interString;
		int theIndex, i, j;

		if (find == null) return -1;
		if (replace == null) return -1;
		if (theSBuffer == null) return -1;
		int theSBufferLength = theSBuffer.length();
		int findLength = find.length();		
		if (theSBufferLength == 0) return -1;
		if (findLength == 0) return -1;
		if (theSBufferLength < findLength) return -1;
		if ((fromIndex < 0)||(fromIndex > theSBufferLength)) return -1;

		interString = theSBuffer.toString();
		theIndex = interString.indexOf(find,fromIndex);
		if (theIndex == -1) return -1;
		
		//// on 9210 the following code ...
		for (i=theIndex;i<theSBufferLength-findLength;i++) {
			theSBuffer.setCharAt(i,theSBuffer.charAt(i+findLength));
		}
		for (j=theSBufferLength-1; j >= (theSBufferLength-findLength); j--) {
			theSBuffer.setCharAt(j,(char)(0));
		}
		int newLength = theSBufferLength-findLength;
		theSBuffer.setLength(newLength);
		theSBuffer.insert(theIndex,replace);
		return theIndex;
		//// replaced this code ...
		// for (i=theIndex;i<theSBufferLength-findLength;i++) {
		//	theSBuffer.setCharAt(i,theSBuffer.charAt(i+findLength));
		// }
		// int newLength = theSBufferLength-findLength;
		// theSBuffer.setCharAt(newLength,(char)(0));
		// theSBuffer.setLength(newLength);
		// theSBuffer.insert(theIndex,replace);
		// return theIndex;
	}
	/**
	/* StrFindAndReplace - finds and replaces all in String theString.
	/*/
	public static String StrFindAndReplace(String find, String replace,	String theString) {
		if (theString.length() == 0) return "";
		if (find.length() == 0) return theString;
		if (find.equals(replace)) return theString;

		StringBuffer theBuf = new StringBuffer(theString);
		int found=0;
		int iSearchFrom = 0;
 		while (found != -1) {
			found = FindAndReplace(find,replace,theBuf,iSearchFrom);
			iSearchFrom = found+replace.length();
		}
		return theBuf.toString();
	}

	public static byte[] IntTo6Bytes(int theInt) throws NumberFormatException {
		if (theInt < 0) {
			NumberFormatException e = new NumberFormatException ("y coordinate out of bounds in ZTX");
			throw e;
		}
		byte[] theBytes = new byte[6];
		theBytes[0] = IntToByte(0);
		theBytes[1] = IntToByte(0);
		theBytes[2] = IntToByte(theInt/16777216);
		theBytes[3] = IntToByte((theInt%16777216)/65536);
		theBytes[4] = IntToByte((theInt%65536)/256);
		theBytes[5] = IntToByte(theInt%256);
		return theBytes;
	}
/*	public static byte[] IntTo2Bytes(int theInt) throws NumberFormatException {
		if ((theInt < 0) || (theInt > 65536)) {
			NumberFormatException e = new NumberFormatException ("y coordinate out of bounds in ZTX");
			throw e;
		}
		byte[] theBytes = new byte[2];
		theBytes[0] = IntToByte(theInt/256);
		theBytes[1] = IntToByte(theInt%256);
		return theBytes;
	} */
	public static byte[] IntTo2Bytes(int theInt) throws NumberFormatException {
		if ((theInt < 0) || (theInt > 65536)) {
			NumberFormatException e = new NumberFormatException ("y coordinate out of bounds in ZTX");
			throw e;
		}
		byte[] theBytes = new byte[2];
		theBytes[0] = (byte) ((theInt >> 8) & 0xFF);
		theBytes[1] = (byte) (theInt & 0xFF);
		return theBytes;
	}
/*	public static byte IntToByte(int theInt) throws NumberFormatException {
		byte theByte;
		byte lastBitSet = -128;
		if ((theInt < 0) || (theInt > 256)) {
			NumberFormatException e = new NumberFormatException ("Error in IntToBytes");
			throw e;
		}
		if (theInt <= 127) {
			theByte = (byte)(theInt);
		}
		else {
			theByte = (byte)(theInt - 128);
			theByte = (byte)(theByte | lastBitSet);
		}
		return theByte;
	}  */
	public static byte IntToByte(int theInt) throws NumberFormatException {
		if ((theInt < 0) || (theInt > 256)) {
			NumberFormatException e = new NumberFormatException ("Error in IntToBytes");
			throw e;
		}
		return (byte)(theInt & 0xFF);
	}

  /** returns a value associated with a name in a post string, a 
  * limitation is the name can not appear **anywhere** else in the 
  * post string.
	* @param name the name String of the name-value pair
	* @param post the post String
	* @return the value String of the name-value pair
	*/
	public static String getPostValue(String name, String post) {
		if (post.length() == 0) return "";
		if (name.length() == 0) return "";
		try {
			int namePlace=-1;
			int equalsPlace=-1;
			while (true) {
				while (true) {
					namePlace = post.indexOf(name,namePlace+1);
					if (namePlace == 0)	break;
					if (namePlace > 0) {
						if (post.charAt(namePlace-1) == '&') break;
					}
					if (namePlace == -1) return "";
				}
				equalsPlace	= post.indexOf('=',namePlace+1);
				if (equalsPlace == -1) return "";
				if (name.equalsIgnoreCase(post.substring(namePlace,equalsPlace).trim())) break;
			}
			int valueEnd = post.indexOf('&',equalsPlace);
			return post.substring(equalsPlace+1,valueEnd);
		}
		catch (Exception e) {
			System.out.println("getPostValue Error: "+e.getMessage());
			return "";
		}
	}
	
	public static String toTwoPlace(int theInteger) {
	  if (theInteger > 9) {
	    return Integer.toString(theInteger);
	  }
	  else {
	    return "0"+Integer.toString(theInteger);
	  }
	}

  public static String removeCharacters(String originalString, String[] replaceStrings) {
    int found = 0;
    if (replaceStrings.length == 0) return originalString;
    if ((originalString == null) || (originalString.length() == 0)) return "";
		StringBuffer sb = new StringBuffer(originalString);
		for (int i = 0; i< replaceStrings.length; i++) {
			found = 0;
			while (found != -1) {
				found = Lib.FindAndReplace(replaceStrings[i],"",sb,0);
			}
		}
		return sb.toString();
	}
	
  public static String fixQueryString(String sOriginalString) {
    String[] queryTaboo = {"'","\""};
    return removeCharacters(sOriginalString, queryTaboo);
  }
	
/***********************************************
/*
/*  ASSORTED FUNCTIONS
/*
/**********************************************/

	/**
	/*	Wait - waits for a number of seconds.
	/*/
	public static void Wait(int seconds) {
		long milliseconds = seconds*1000;
		Date dateThen = new Date();
		long then = dateThen.getTime();
		while (true) {
			Date dateNow = new Date();
			long now = dateNow.getTime();
			if ((now-then) >= milliseconds) break;
			//Thread.currentThread().yield();
			try {Thread.currentThread().sleep(100);} catch (Exception e) {}

		}
	}

	/**
	/*	WaitWithStatus - waits for a number of seconds.
	/*/
	public static void Wait(Component object, int seconds) {
		long milliseconds = seconds*1000;
		Date dateThen = new Date();
		long then = dateThen.getTime();
		while (true) {
			Date dateNow = new Date();
			long now = dateNow.getTime();
			if ((now-then) >= milliseconds) break;
			//Thread.currentThread().yield();
			try {Thread.currentThread().sleep(100);} catch (Exception e) {}
		}
	}

	public static String statusPercent(double seconds, double projectedSeconds) {
		if (seconds < 0) seconds = 0;
		if (projectedSeconds < 0) seconds = 100;

		double percentIn = seconds/projectedSeconds;
		double percentOut = 0;
		if (percentIn < 0.5) {
			percentOut = percentIn;
		}
		if ((percentIn >= 0.5) && (percentIn < 1.0)) {
			percentOut = 0.5 + (percentIn - 0.5) * 0.5;
		}
		if ((percentIn >= 1.0) && (percentIn < 2.0)) {
			percentOut = 0.75 + (percentIn - 1.0) * 0.1;
		}
		if ((percentIn >= 2.0) && (percentIn < 7.0)) {
			percentOut = 0.85 + (percentIn - 2.0) * 0.02;
		}
		if ((percentIn >= 7.0) && (percentIn < 9.0)) { 
			percentOut = 0.96;
		}
		if ((percentIn >= 9.0) && (percentIn < 12.0)) { 
			percentOut = 0.97;
		}
		if ((percentIn >= 12.0) && (percentIn < 16.0)) { 
			percentOut = 0.98;
		}
		if (percentIn >= 16.0) { 
			percentOut = 0.99;
		}
		int ipercentOut = (int) (percentOut * 100.0);
		String theSentence = " " + String.valueOf(ipercentOut) + "% complete";
		int igpercentOut = (int) (percentOut * 10.0);
		String theGraph = "[";
		for (int i=1;i<=igpercentOut;i++) {
			theGraph += "X";
		}
		for (int i=igpercentOut+1;i<=10;i++) {
			theGraph += "_";
		}
		theGraph += "]";		
		return theGraph + theSentence;
	}

	public static void WaitForChar() {
		try {
			System.in.read();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static Frame getFrame(Component component) {
        Component c = component;

        if(c instanceof Frame)
            return (Frame)c;

        while((c = c.getParent()) != null) {
            if(c instanceof Frame)
                return (Frame)c;
        }
        return null;
    }

    public static Applet getApplet(Component component) {
        Component c = component;

        if(c instanceof Applet)
            return (Applet)c;

        while((c = c.getParent()) != null) {
            if(c instanceof Applet)
                return (Applet)c;
        }
        return null;
    }

	public static Panel getPanel(Component component) {
        Component c = component;

        if(c instanceof Panel)
            return (Panel)c;

        while((c = c.getParent()) != null) {
            if(c instanceof Panel)
                return (Panel)c;
        }
        return null;
    }
    
  public static void sort(String a[], boolean caseSensitive)
      throws Exception {
    boolean stopRequested=false;
		int j;
		int limit = a.length;
		int st = -1;
    boolean bigger = false;
		while (st < limit) {
			st++;
			limit--;
			boolean swapped = false;
			for (j = st; j < limit; j++) {
				if (stopRequested) {
					return;
				}
        if (caseSensitive)
          if (a[j].compareTo(a[j + 1]) > 0) {bigger = true;} else {bigger=false;}
        else
				  if (a[j].toLowerCase().compareTo(a[j + 1].toLowerCase()) > 0) {bigger = true;} else {bigger=false;}
        if (bigger==true) {
					String T = a[j];
					a[j] = a[j + 1];
					a[j + 1] = T;
					swapped = true;
				}
				//pause(st, limit);
			}
			if (!swapped) {
			return;
			}
			else
			swapped = false;
			for (j = limit; --j >= st;) {
				if (stopRequested) {
					return;
				}
        if (caseSensitive)
          if (a[j].compareTo(a[j + 1]) > 0) {bigger = true;} else {bigger=false;}
        else
				  if (a[j].toLowerCase().compareTo(a[j + 1].toLowerCase()) > 0) {bigger = true;} else {bigger=false;}
        if (bigger==true) {
					String T = a[j];
					a[j] = a[j + 1];
					a[j + 1] = T;
					swapped = true;
				}
				//pause(st, limit);
			}
			if (!swapped) {
				return;
			}
		}
  }
    
	public static String getDate() {
		String SMonth;
		String SDay;
		String SYear;

		Date today = new Date();
		int month = today.getMonth();
		int day = today.getDate();	
		int year = today.getYear();
		month = month + 1;
		if (month < 10) {
			SMonth = '0'+ String.valueOf(month);
		}
		else {
			SMonth = String.valueOf(month);
		}
		if (day < 10) {
			SDay = '0'+ String.valueOf(day);
		}
		else {
			SDay = String.valueOf(day);
		}
	  // This codes changed on 10/5/00 from
	  //
	  // year = year + 1900;
	  //
	  // to...		
	  year = year + 2000;
		SYear =  String.valueOf(year);
		return SYear+SMonth+SDay;
	}

  // Generates a new random string
	static public String newRandomString(int theLength) {
		if (theLength <= 0) return "";
		byte[] bytes = new byte[theLength];
		for (int i=0; i<theLength; i++) {
			bytes[i] = (byte)(Math.random()*128.0);
		}
		return new String(bytes,0);
	}

	// Copies a byte array
	static public byte[] copyByteArray(byte[] byteArray) {
		if (byteArray == null) {
			System.out.println("copyByteArray got passed a null");
		}
		if (byteArray.length == 0) return new byte[0];
		byte[] newBytes = new byte[byteArray.length];
		for (int i=0; i<byteArray.length; i++) {
			newBytes[i] = byteArray[i];
		}
		return newBytes;
	}
  
  static public Object[][] copyStringHashtableToArray(Hashtable theHashtable) 
    throws Exception {
    int i = 0;
    if ((theHashtable == null) || (theHashtable.isEmpty())) {
      return new Object[0][0];
    }
		int hashtableCount = theHashtable.size();
    Object[][] theArray = new Object[hashtableCount][2];
    Enumeration keys = theHashtable.keys();
    while(keys.hasMoreElements()) {
      theArray[i][0] = (String)(keys.nextElement());
      theArray[i][1] = (Object)(theHashtable.get(theArray[i][0]));
      i++;
    }
    return theArray;
  }

   /**
	* Copy files and/or directories.
	*
	* @param src source file or directory
	* @param dest destination file or directory
   	* @exception IOException if operation fails
	*/
	private static int BUFSIZE=4096;
	public static void copy(File src, File dest) throws IOException {

		FileInputStream source = null;
		FileOutputStream destination = null;
		byte[] buffer;
		int bytes_read;

		// Make sure the specified source exists and is readable.
		if (!src.exists())
			throw new IOException("source not found: " + src);
		if (!src.canRead())
			throw new IOException("source is unreadable: " + src);

		if (src.isFile()) {
			if (!dest.exists()) {
                File parentdir = parent(dest);
                if (!parentdir.exists())
					parentdir.mkdir();
			}
			else if (dest.isDirectory()) {
				dest = new File(dest + File.separator + src);
			}
		}
		else if (src.isDirectory()) {
			if (dest.isFile())
				throw new IOException("cannot copy directory " + src + " to file " + dest);

			if (!dest.exists())
				dest.mkdir();
		}
		
		// The following line requires that the file already
		// exists!!  Thanks to Scott Downey (downey@telestream.com)
		// for pointing this out.  Someday, maybe I'll find out
		// why java.io.File.canWrite() behaves like this.  Is it
		// intentional for some odd reason?
		//if (!dest.canWrite())
			//throw new IOException("destination is unwriteable: " + dest);

		// If we've gotten this far everything is OK and we can copy.
		if (src.isFile()) {
			try {
	            source = new FileInputStream(src);
		        destination = new FileOutputStream(dest);
			    buffer = new byte[1024];
				while(true) {
	                bytes_read = source.read(buffer);
		            if (bytes_read == -1) break;
			        destination.write(buffer, 0, bytes_read);
				}
			}
	        finally {
		        if (source != null) 
			        try { source.close(); } catch (IOException e) { ; }
				if (destination != null) 
	                try { destination.close(); } catch (IOException e) { ; }
		    }
		}
		else if (src.isDirectory()) {
			String targetfile, target, targetdest;
			String[] files = src.list();

			for (int i = 0; i < files.length; i++) {
				targetfile = files[i];
				target = src + File.separator + targetfile;
				targetdest = dest + File.separator + targetfile;


				if ((new File(target)).isDirectory()) {
		 			copy(new File(target), new File(targetdest));
				}
				else {

					try {
						source = new FileInputStream(target);
						destination = new FileOutputStream(targetdest);
						buffer = new byte[1024];
					 
						while(true) {
							bytes_read = source.read(buffer);
							if (bytes_read == -1) break;
							destination.write(buffer, 0, bytes_read);
						}
					}
					finally {
						if (source != null) 
							try { source.close(); } catch (IOException e) { ; }
						if (destination != null) 
							try { destination.close(); } catch (IOException e) { ; }
					}
				}
			}
		}
	}


   /**
	* File.getParent() can return null when the file is specified without
	* a directory or is in the root directory. This method handles those cases.
	*
	* @param f the target File to analyze
	* @return the parent directory as a File
	*/
	private static File parent(File f) {
		String dirname = f.getParent();
		if (dirname == null) {
			if (f.isAbsolute()) return new File(File.separator);
			else return new File(System.getProperty("user.dir"));
		}
		return new File(dirname);
	}
  
  /**
	* Write file as a string.
	*
	* @param src source file or directory
  * @exception IOException if operation fails
	*/
	public static void write(File dest, String outString) throws IOException {

		FileOutputStream outFileStream = null;
		byte[] buffer;
		int bytes_write;

		// Make sure the specified source exists and is readable.
//		if (dest.exists()) 
//			throw new IOException("destination already exists: " + dest);
		if (dest.isDirectory()) 
			throw new IOException("destination is a directory: " + dest);
		
		// If we've gotten this far everything is OK and we can write.
	//	if (dest.isFile()) {
			try {
				//System.out.println(outString.length());
		        outFileStream = new FileOutputStream(dest);
			    buffer = new byte[outString.length()];
				outString.getBytes(0,outString.length(),buffer,0);
			    outFileStream.write(buffer, 0, outString.length());
			    //outFileStream.flush();
			    //outFileStream.close();
			}
	        catch (IOException e) {
				throw new IOException("trouble writing file: " + dest);
			}
			return;
	//	}
	//	else 
	//		throw new IOException("ambiguous file write error: " + dest);
	}
	
	public static void append(RandomAccessFile f, String outString) throws IOException {
		try {
			f.seek(f.length());
			f.writeBytes(outString);
		}
        catch (IOException e) {
			throw new IOException("trouble writing to random access file.");
		}
		return;
	}
  /**
  * funtion read for Java 1.02, the difference is in the 
  * use of FileInputStream instead of the 1.16 BufferedReader.
  * The BufferedReader is preferable because it can read the 
  * unicode files.
  */
  /*
	public static String read(File theFile) throws IOException {

		byte buf[];
		String data = new String("");
		int max=0,count=0,total=0;

		// Make sure the specified source exists and is readable.
		if (!theFile.exists()) 
			throw new IOException("source not found: " + theFile);
		if (!theFile.canRead())	
			throw new IOException("source is unreadable: " + theFile);
		if (theFile.isDirectory()) 
			throw new IOException("source is a directory: " + theFile);
		
		// If we've gotten this far everything is OK and we can read.
		if (theFile.isFile()) 
		{
			buf = new byte[BUFSIZE];
			try
			{
				FileInputStream input = new FileInputStream(theFile);
				max = input.available();
				total = 0;

				while (total < max)
				{
					count = BUFSIZE;
					if (max-total < count)
						count = max-total;
					count = input.read(buf,0,count);
					data += new String(buf,0,0,count);
					total += count;
				}
				input.close();
			}
			catch (IOException e) {
				throw new IOException("openFile: read error: " + e.getMessage());
			}
			return data;
		}
		return null;
	}
  */
  
  /**
  * funtion read for Java 1.16.
  */
	public static String read(File theFile) throws IOException {
    boolean eof = false;
		// Make sure the specified source exists and is readable.
		if (!theFile.exists()) 
			throw new IOException("source not found: " + theFile);
		if (!theFile.canRead())	
			throw new IOException("source is unreadable: " + theFile);
		if (theFile.isDirectory()) 
			throw new IOException("source is a directory: " + theFile);
		// If we've gotten this far everything is OK and we can read.
		if (theFile.isFile()) {
		  StringBuffer tempBuffer = new StringBuffer(BUFSIZE);
			try {
        BufferedReader input = new BufferedReader(new FileReader(theFile));
        while (!eof) {
          try {
            String tempLine = null;
            tempLine = input.readLine();
            if (tempLine==null) {
              eof = true;
              continue;
            }
            else if (tempLine.length() < 1) {
              tempBuffer.append("\r\n");
              continue;
            }
            else {
              tempBuffer.append(tempLine);
              tempBuffer.append("\r\n");
              continue;
            }
          }
          catch (EOFException eofe) {
            eof = true;
          }
        }
				input.close();
			}
			catch (IOException e) {
				throw new IOException("openFile: read error: " + e.getMessage());
			}
			return tempBuffer.toString();
		}
		return null;
	}
	
	public static String readbybyte(File theFile) throws IOException {
    int BUFFSIZE = 2048;
    boolean eof = false;
		// Make sure the specified source exists and is readable.
		if (!theFile.exists()) 
			throw new IOException("source not found: " + theFile);
		if (!theFile.canRead())	
			throw new IOException("source is unreadable: " + theFile);
		if (theFile.isDirectory()) 
			throw new IOException("source is a directory: " + theFile);
		// If we've gotten this far everything is OK and we can read.
		if (theFile.isFile()) {
		  StringBuffer tempBuffer = new StringBuffer(BUFSIZE);
			try {
        BufferedReader input = new BufferedReader(new FileReader(theFile));
        while (!eof) {
          try {
            int tempInt = input.read();
            if (tempInt==0) {
              continue;
            }
            if (tempInt==-1) {
              eof = true;
              continue;
            }
            else {
              tempBuffer.append((char)(tempInt));
              continue;
            }
          }
          catch (EOFException eofe) {
            eof = true;
          }
        }
				input.close();
			}
			catch (IOException e) {
				throw new IOException("openFile: read error: " + e.getMessage());
			}
			return tempBuffer.toString();
		}
		return null;
	}

  /**
	* Read file into a string.
	*
	* @param src source file or directory
  * @exception IOException if operation fails
	*/
	public static String oldRead(File src) throws IOException {

		FileInputStream source = null;
		byte[] buffer;
		int bytes_read=0, total_bytes_read=0;
		String innerString;
		StringBuffer outerString = new StringBuffer(2048);

		// Make sure the specified source exists and is readable.
		if (!src.exists()) 
			throw new IOException("source not found: " + src);
		if (!src.canRead())	
			throw new IOException("source is unreadable: " + src);
		if (src.isDirectory()) 
			throw new IOException("source is a directory: " + src);
		
		// If we've gotten this far everything is OK and we can read.
		if (src.isFile()) {
			try {
	            source = new FileInputStream(src);
			    buffer = new byte[1024];
				while(true) {
	                bytes_read = source.read(buffer);
		            if (bytes_read == -1) break;
			        innerString = new String(buffer,0);
					outerString = outerString.append(innerString);
					total_bytes_read=total_bytes_read+bytes_read;
				}
			}
	        finally {
		        if (source != null) 
			        try { source.close(); } catch (IOException e) { ; }
		    }
			outerString.setLength(total_bytes_read);
			return outerString.toString();
		}
		else 
			throw new IOException("ambiguous file error: " + src);
	}
  // Returns the contents of a file that is defined by it's full path name.
  public static String readQuietlyWithFullpath(String theFileName) throws IOException {
		File theFile = autoGetFile(theFileName,"",false,null);
    return(read(theFile));
  }

  // Returns the contents of a file that is defined by it's full path name.
  public static String readBybyteQuietlyWithFullpath(String theFileName) throws IOException {
		File theFile = autoGetFile(theFileName,"",false,null);
    return(readbybyte(theFile));
  }

	public static String openFile(Component theComponent) throws IOException 
	{
		byte buf[];
		String data = new String("");

		int max,count,total;

		buf = new byte[BUFSIZE];

		File theFile = getFile(theComponent,"",FileDialog.LOAD);

		if (theFile != null)
		{
			try
			{
				FileInputStream input = new FileInputStream(theFile);
				max = input.available();
				total = 0;

				while (total < max)
				{
					count = BUFSIZE;
					if (max-total < count)
						count = max-total;
					count = input.read(buf,0,count);
					data += new String(buf,0,0,count);
					total += count;
				}
				input.close();
			}
			catch (IOException e) {
				throw new IOException("openFile: read error: " + e.getMessage());
			}
			return data;
		}
		return null;
	}

	private static File getFile(Component theComponent, String fileName, int mode)
	{
		File file;
		String prompt;
		String filename;
		String pathname;

		if (mode == FileDialog.LOAD)
			prompt = "Open File ...";
		else
			prompt = "Save File ...";

		FileDialog d = new FileDialog(getFrame(theComponent), prompt, mode);

		if (mode == FileDialog.LOAD)
			d.setFile("*");
		else
			d.setFile(fileName);

		d.setDirectory(".");
		d.show();

		filename = d.getFile(); 

		if (filename.endsWith(".*.*")) // must be a java-on-windows bug
			filename = filename.substring(0,filename.length() - 4);

		pathname = d.getDirectory() + filename; 

		if (filename != null)
		{
			file = new File(pathname);
			return file;
		}
		else
			return null;
	}
	
  /**
  * autoGetFile - static method to get a 'File' object.
  * <p>The URI that you supply will become the base URI for
  * resolving relative links, but &AElig;lfred will actually read
  * the document from the supplied input stream.
  * <p>You may parse a document more than once, but only one thread
  * may call this method for an object at one time.
  * @param fileName The filename without any path information.
  * @param absolutePath The path of the file, where the application resides
  *                     if thevariable is null or empty.  Do NOT use
  *                     this function if all the path info is in 
  *                     the filename, use the other version below.
  * @param prompt A string that displays in the choose file box.
  * @param quiet A boolean set to true if no chooser box should appear
  *              if there is an error, automatically set if theComponent
  *              is null.
  * @param theComponent The parent component (that is needed to show a 
  *        file chooser box).
  * @return the File object specified in the paramaters
  */

  public static File autoGetFile(String fileName, String absolutePath, String prompt, boolean quiet, Component theComponent) {
    boolean fileError = false;
    File absoluteFilePath=null;
    File returnedFile=null;
    if (theComponent == null) { quiet = true; }
    // get a path for the file, default to directory of the application.
    // if we can't get user.dir, assume that nothing is going to work (likely correct).
    try {
      if ( absolutePath==null ) {
        absoluteFilePath =  new File(System.getProperty("user.dir"));
      }
      else if ( absolutePath.length()==0 ) {
        absoluteFilePath =  new File(System.getProperty("user.dir"));
      }
      else {
        absoluteFilePath = new File(absolutePath);
        if (!absoluteFilePath.isDirectory()) {
          absoluteFilePath = new File(absolutePath.substring(0,absolutePath.length()-1));
          if (!absoluteFilePath.isDirectory()) {
            absoluteFilePath =  new File(System.getProperty("user.dir"));
          }
        }
		  }
    }
    catch (SecurityException e) {
      TError("Security exception getting to 'user.dir'"); 
      absoluteFilePath=null;
    }
    catch (Exception e) {
      TError("Can't get a default path, probably no context for 'user.dir'"); 
      absoluteFilePath=null;
    }
    if (absoluteFilePath==null) { 
      fileError = true;
      TError("Error in absolute path."); 
    }   
    else if (fileName == null) {
      TError("Error, filename is null."); 
      fileError = true;
    }
    else if (fileName.length() == 0) {
      TError("Error, filename is zero length."); 
      fileError = true;
    }
    // if this is a batch/quiet mode, we are dead if couldn't find absolute path or filename.
    if (fileError) {
      if (quiet) return null;
    }
		// Make sure the specified source exists and is readable.
    else {
      returnedFile = new File(absoluteFilePath.toString() + File.separator + fileName);
      if (!returnedFile.exists()) {
        TError("Error, file does not exist."); 
        fileError = true;
        if (quiet) return null;
      }
      else if (!returnedFile.canRead()) {
        TError("Error, file is not readable."); 
        fileError = true;
        if (quiet) return null;
        }
      else if (returnedFile.isDirectory()) {
        TError("Error, file is a directory."); 
        fileError = true;
        if (quiet) return null;
      }
    }
    if (fileError) {
      FileDialog d = new FileDialog(getFrame(theComponent), prompt, FileDialog.LOAD);
      if (fileName != null) d.setFile(fileName);
    	d.setDirectory(".");
    	d.show();
    	fileName = d.getFile(); 
      if (fileName.endsWith(".*.*")) { // must be a java-on-windows bug
        fileName = fileName.substring(0,fileName.length() - 4);
      }
      returnedFile = new File(d.getDirectory() + fileName);
    }
    return returnedFile;
  }
  
  public static File autoGetFile(String completeFileName, String prompt, boolean quiet, Component theComponent) {
    if ((completeFileName==null) || (completeFileName.length()==0)) return null;
    int lastSeparatorIndex = completeFileName.lastIndexOf(File.separator);
    if (lastSeparatorIndex < 0) {
      return autoGetFile(completeFileName, null, prompt, quiet, theComponent);
    }
    else {
      String path = completeFileName.substring(0, lastSeparatorIndex);
      String filename = completeFileName.substring(lastSeparatorIndex+1,completeFileName.length());
      return autoGetFile(filename, path, prompt, quiet, theComponent);
    }
  }
    
	/*********************************************************************
	 autoGetRAFile - static method to get a 'RandomAccessFile' object.  Same 
	    input parameters as above.
	*********************************************************************/
	public static RandomAccessFile autoGetRAFile(String fileName, String absolutePath, String prompt, boolean quiet, Component theComponent) throws Exception {
        File interFile = autoGetFile(fileName, absolutePath, prompt, quiet, theComponent);
        return new RandomAccessFile(interFile,"rw");
  }
    
  public static String returnBaseFileNameWithoutSuffix(String fileName) {
    return returnBaseFileName(fileName, false);
  }
   
  public static String returnBaseFileNameWithSuffix(String fileName) {
    return returnBaseFileName(fileName, true);
  }
	
  public static String returnBaseFileName(String fileName, boolean wantsSuffix) {
    if ((fileName == null) || (fileName.length() == 0)) return "";
    int lastSeparatorIndex = fileName.lastIndexOf(File.separator);
    if (lastSeparatorIndex >= 0) {
      // on 9250 then following line ...
      //  fileName = fileName.substring(lastSeparatorIndex,  fileName.length());
      // was changed to ...
      fileName = fileName.substring(lastSeparatorIndex+1,  fileName.length());
    }
    if (wantsSuffix) return fileName;
    lastSeparatorIndex = fileName.lastIndexOf('.');
    if (lastSeparatorIndex >= 0) {
      fileName = fileName.substring(0, lastSeparatorIndex);
    }
    return fileName;
  }
      
  public static String returnFilenameWithoutSuffix(String fileName) {
    if ((fileName == null) || (fileName.length() == 0)) return "";
    int lastSeparatorIndex = fileName.lastIndexOf('.');
    if (lastSeparatorIndex >= 0) {
      return fileName.substring(0,lastSeparatorIndex);
    }
    return fileName;
  }
  
  public static String returnFilenameSuffix(String fileName) {
    if ((fileName == null) || (fileName.length() == 0)) return "";
    int lastSeparatorIndex = fileName.lastIndexOf('.');
    if (lastSeparatorIndex >= 0) {
      return fileName.substring(lastSeparatorIndex+1, fileName.length());
    }
    return "";
  }

  public static void addVertCmp(
		Container toContainer, 
		GridBagLayout usingGridBagLayout, 
        GridBagConstraints usingGridBagConstraints, 
        Component theComponent, 
        int cellsWide, 
        double widthWeight)
    {
		usingGridBagConstraints.weighty = 1.0 ; 
        usingGridBagConstraints.gridwidth = cellsWide; 
        usingGridBagConstraints.weightx = widthWeight; 
        usingGridBagLayout.setConstraints(theComponent, usingGridBagConstraints); 
        toContainer.add(theComponent); 
    }

	public static void addHorizCmp(
		Container toContainer, 
		GridBagLayout usingGridBagLayout, 
        GridBagConstraints usingGridBagConstraints, 
        Component theComponent, 
        int cellsTall, 
        double heightWeight)
    {
		usingGridBagConstraints.weightx = 1.0 ; 
		usingGridBagConstraints.gridheight = cellsTall; 
        usingGridBagConstraints.weighty = heightWeight; 
        usingGridBagLayout.setConstraints(theComponent, usingGridBagConstraints); 
        toContainer.add(theComponent); 
    }

	public static void addPlainVertComp(
		Container toContainer, 
		Component theComponent,
		GridBagLayout theGridBagLayout,
		double widthWeight) 
	{
		if (theGridBagLayout == null) {
			System.out.println("addPlainVertComp: null GridBagLayout");
			return;
		}
		if ((widthWeight < 0) || (widthWeight > 1)) {
			System.out.println("Illegal weight in Gridbag call");
		}
		toContainer.setLayout(theGridBagLayout);
		GridBagConstraints usingGridBagConstraints= new GridBagConstraints(); 
		usingGridBagConstraints.weighty = 1.0 ; 
        usingGridBagConstraints.weightx = widthWeight; 
        usingGridBagConstraints.gridheight= GridBagConstraints.REMAINDER;
		int cellsWide = (int) Math.round((float) widthWeight * 100.0);
        usingGridBagConstraints.gridwidth = cellsWide; 
		// nothing should be added to determine minimum size of component
        usingGridBagConstraints.ipady= 0; 
        usingGridBagConstraints.ipadx= 0;
		// no border around the component
		usingGridBagConstraints.insets= new Insets(0, 0, 0, 0);
		// stretch the component to fill the region
        usingGridBagConstraints.fill= GridBagConstraints.BOTH;
        theGridBagLayout.setConstraints(theComponent, usingGridBagConstraints); 
        toContainer.add(theComponent); 
    }

	public static void addPlainHorizComp(
		Container toContainer, 
		Component theComponent,
		GridBagLayout theGridBagLayout,
		double heightWeight) 
	{
		if (theGridBagLayout == null) {
			System.out.println("addPlainHorizComp: null GridBagLayout");
			return;
		}
		if ((heightWeight < 0) || (heightWeight > 1)) {
			System.out.println("Illegal weight in Gridbag call");
		}
		toContainer.setLayout(theGridBagLayout);
		GridBagConstraints usingGridBagConstraints= new GridBagConstraints(); 
		usingGridBagConstraints.weightx = 1.0 ; 
        usingGridBagConstraints.weighty = heightWeight; 
        usingGridBagConstraints.gridwidth= GridBagConstraints.REMAINDER; 
		int cellsTall = (int) Math.round((float) heightWeight * 100.0);
        usingGridBagConstraints.gridheight = cellsTall; 
		// nothing should be added to determine minimum size of component
        usingGridBagConstraints.ipady= 0; 
        usingGridBagConstraints.ipadx= 0;
		// no border around the component
		usingGridBagConstraints.insets= new Insets(0, 0, 0, 0);
		// stretch the component to fill the region
        usingGridBagConstraints.fill= GridBagConstraints.BOTH;
        theGridBagLayout.setConstraints(theComponent, usingGridBagConstraints); 
        toContainer.add(theComponent); 
    }

    /**
      Error functions
      modified by SNM on 4/3/2001 to change '/' to '\\' between paths
      added set... functions
    */

 		public static boolean m_MessageToConsole=true;
		public static int m_dbgLevel=2;
    public static String logFile = "SMlog.txt";
    public static String errFile = "SMerr.txt";
    
    public static String setLogFileName(String sFileName) {
      if ((sFileName == null) || (sFileName.length() == 0)) return "";
		  if (returnBaseFileNameWithSuffix(sFileName).equalsIgnoreCase(sFileName)) {
		    logFile = System.getProperty("user.dir")+'\\'+sFileName;
		  }
		  else {
		    logFile = sFileName;
		  }
		  return logFile;
    }

    public static String setErrFileName(String sFileName) {
      if ((sFileName == null) || (sFileName.length() == 0)) return "";
		  if (returnBaseFileNameWithSuffix(sFileName).equalsIgnoreCase(sFileName)) {
		    errFile = System.getProperty("user.dir")+'\\'+sFileName;
		  }
		  else {
		    errFile = sFileName;
		  }
		  return errFile;
    }

    public static void Error(String s) {
  		if (m_dbgLevel < 1) return;
  		if (m_MessageToConsole) System.out.println(s);
  		try {
		    if (returnBaseFileNameWithSuffix(errFile).equalsIgnoreCase(errFile)) {
		      errFile = System.getProperty("user.dir")+'\\'+errFile;
		    }
		  	RandomAccessFile f = new RandomAccessFile(errFile, "rw");
		  	f.seek(f.length());
		  	f.writeBytes(s);
		  	f.writeByte('\r');
		  	f.writeByte('\n');
		  	f.close();
	  	}
	  	catch (Exception e) {
		  	if (m_MessageToConsole) System.out.println("File "+errFile+" error: "+e);
	  	}
	  }

	 public static void TError(String s) {
		if (m_dbgLevel < 1) return;
		if (m_MessageToConsole) System.out.println(s);
		try {
		  if (returnBaseFileNameWithSuffix(errFile).equalsIgnoreCase(errFile)) {
		    errFile = System.getProperty("user.dir")+'\\'+errFile;
		  }
			RandomAccessFile f = new RandomAccessFile(errFile, "rw");
			f.seek(f.length());
			Date today = new Date();
			f.writeBytes('['+today.toLocaleString()+"] ");
			f.writeBytes(s);
			f.writeByte('\r');
			f.writeByte('\n');
			f.close();
		} 
		catch (Exception e) {
			if (m_MessageToConsole) System.out.println("File "+errFile+" error: "+e);
		}
	}

	public static void Message(String s) {
		if (m_dbgLevel < 2) return;
		if (m_MessageToConsole) System.out.println(s);
		try {
		  if (returnBaseFileNameWithSuffix(logFile).equalsIgnoreCase(logFile)) {
		    logFile = System.getProperty("user.dir")+'/'+logFile;
		  }
			RandomAccessFile f = new RandomAccessFile(logFile, "rw");
			f.seek(f.length());
			f.writeBytes(s);
			f.writeByte('\r');
			f.writeByte('\n');
			f.close();
		}
    catch (Exception e) {
			if (m_MessageToConsole) System.out.println("File "+logFile+" error: "+e);
		}
	}
	
  //
  // The Time Stamp Log routines
  //
  // Works like this, in the interest of not consuming time writing a file 
  // that would interfere too much with the timing we are trying to measure.
  //
  // First, you run ResetTimeStampLog
  // Then, at events you wish to time you run TimeStampLog
  // Finally, when you want to print out the file, you run OutputTimeStampLog,
  // which resets things back also.
  //
  
  private static long lMillisecThen = -1;
  private static String sTimeStampLog = "";
  
  public static boolean TResetTimeStampLog() {
    lMillisecThen = -1;
    sTimeStampLog = "";
    return true;
  }
  
  public static boolean TTimeStampLog(String sMessage) {
			long lMillisecNow;
			long lMillisecDifference;
			try {
			  Date today = new Date();
			  lMillisecNow = today.getTime();
			  if (lMillisecThen != -1) {
			    lMillisecDifference = lMillisecNow - lMillisecThen;
			    lMillisecThen = lMillisecNow;
			  }
			  else {
			    lMillisecDifference = 0;
			    lMillisecThen = lMillisecNow;
			  }
			  sTimeStampLog = sTimeStampLog + "\t" + Long.toString(lMillisecDifference) + "\r\n" + Long.toString(lMillisecThen) + "\t" + sMessage;
			  return true;
			}
			catch (Exception e) {
			  TMessage("Error in sTimeStampLog: " + e.getMessage());
			  return false;
			} 
  }
  
  public static boolean TOutputTimeStampLog(String sMessage) {
    try {
      TMessage(sMessage + "\r\n" + sTimeStampLog);
      Lib.TResetTimeStampLog();
      return true;
    }
		catch (Exception e) {
		  TMessage("Error in sTimeStampLog: " + e.getMessage());
		  return false;
		} 
  }

  //
  // Message routines
  //
  
	public static void TMessage(String s) {
		if (m_dbgLevel < 2) return;
		if (m_MessageToConsole) System.out.println(s);
		try {
		  if (returnBaseFileNameWithSuffix(logFile).equalsIgnoreCase(logFile)) {
		    logFile = System.getProperty("user.dir")+'/'+logFile;
		  }
			RandomAccessFile f = new RandomAccessFile(logFile, "rw");
			f.seek(f.length());
			Date today = new Date();
			f.writeBytes('['+today.toLocaleString()+"] ");
			f.writeBytes(s);
			f.writeByte('\r');
			f.writeByte('\n');
			f.close();
		}
    catch (Exception e) {
			  if (m_MessageToConsole) System.out.println("File "+logFile+" error: "+e);
		}
	}

  
  public static Hashtable readPropertiesFile(String fileName, String propertiesTag, Component theComponent) {
    try {
      boolean quiet = false;
      String prompt = "Please fing the User Preferences file ...";
      File thePropertiesFile = autoGetFile(fileName, prompt, quiet, theComponent);
      String thePropertiesString = prefRead(thePropertiesFile);
      String leftTag = null;
      String rightTag = null;
      String xmlString = null;
      if ((propertiesTag==null)||(propertiesTag.length()==0)) {
        leftTag = "";
        rightTag = "";
        xmlString = thePropertiesString;
      }
      else {
        leftTag = "<"+propertiesTag+">";
        rightTag = "</"+propertiesTag+">";
        xmlString = PullOut(leftTag, rightTag, thePropertiesString, 0);
      }
      Hashtable htOfProperties = XMLLib.GetXMLValues(leftTag+xmlString+rightTag);
      return htOfProperties;
    }
    catch (Exception e) {
      TError(e.getMessage());
      return null;
    }
  } 
  /** Read preferences file into a string, does not return lines 
  * preceeded by a comment character.
	* @param src source file or directory
	* @param commentChar character used to annotate a line that is a comment
	* @return the file as a String
  * @exception IOException if operation fails
  * @see not 1.02 compatible.
	*/
	public static String prefRead(File src, char commentChar)
    throws IOException {
    BufferedReader input = null;
    StringBuffer tempBuffer = null;
    boolean eof=false;
		// Make sure the specified source exists and is readable.
		if (!src.exists()) 
			throw new IOException("source not found: " + src);
		if (!src.canRead())	
			throw new IOException("source is unreadable: " + src);
		if (src.isDirectory()) 
			throw new IOException("source is a directory: " + src);
		// If we've gotten this far everything is OK and we can read.
		if (src.isFile()) {
      tempBuffer = new StringBuffer(BUFSIZE);
			try {
        input = new BufferedReader(new FileReader(src));
        while (!eof) {
          try {
            String tempLine = null;
            tempLine = input.readLine();
            if (tempLine==null) {
              eof = true;
              continue;
            }
            else if (tempLine.length() < 1) {
              tempBuffer.append("\r\n");
              continue;
            }
            else if (tempLine.charAt(0) == commentChar) {
              continue;
            }
            else {
              tempBuffer.append(tempLine);
              tempBuffer.append("\r\n");
              continue;
            }
          }
          catch (EOFException eofe) {
            eof = true;
          }
        }
      }
      catch (FileNotFoundException fnfe) {
        Lib.TError("Error, file "+src.toString()+" not found.");
      }
      catch (IOException ioe) {
        Lib.TError("Error, "+ioe.getMessage());
      }
      catch (Exception e) {
        Lib.TError("Error, "+e.getMessage());
      }
	    finally {
		    if (input != null) input.close();
		  }
      return tempBuffer.toString();
    }
    else {
			throw new IOException("ambiguous file error: " + src);
	  }
  }
  
  public static String prefRead(File src) throws IOException {
    	return prefRead(src, ';');
  }
  
  //* 5030
  public static String sNormalIcdToDbType(String sNormalIcd, boolean bIsProcedure) {
    if ((sNormalIcd == null) || (sNormalIcd.trim().length() == 0) ) return "";
    String sTempNormalIcd = sNormalIcd.trim();
    int iPeriodLocation = sTempNormalIcd.lastIndexOf('.');
    // handle V codes
    if (sTempNormalIcd.startsWith("V")||sTempNormalIcd.startsWith("v")) {
      if (iPeriodLocation < 0) return sTempNormalIcd;
      else {
        return Lib.StrFindAndReplace(".","",sTempNormalIcd);
      }
    }
    // handle E codes
    if (sTempNormalIcd.startsWith("E")||sTempNormalIcd.startsWith("e")) {
      if (iPeriodLocation < 0) return sTempNormalIcd;
      else {
        return Lib.StrFindAndReplace(".","",sTempNormalIcd);
      }
    }
    // handle Procedure codes
    if (bIsProcedure) {
      if (iPeriodLocation == 2) {
        return "P" + Lib.StrFindAndReplace(".","",sTempNormalIcd);
      }
      else if (iPeriodLocation == 1) {
        return "P0" + Lib.StrFindAndReplace(".","",sTempNormalIcd);
      }
      else if (iPeriodLocation == 0) {
        return "ERROR";
      }
      else {
        if (sTempNormalIcd.length() == 2) {
          return "P" + sTempNormalIcd;
        }
        else if (sTempNormalIcd.length() == 1) {
          return "P0" + sTempNormalIcd;
        }
        else {
          return "ERROR";
        }
      }
    }
    // handle the rest
    if (iPeriodLocation == 3) {
      return Lib.StrFindAndReplace(".","",sTempNormalIcd);
    }
    else if (iPeriodLocation == 2) {
      return "0" + Lib.StrFindAndReplace(".","",sTempNormalIcd);
    }
    else if (iPeriodLocation == 1) {
      return "00" + Lib.StrFindAndReplace(".","",sTempNormalIcd);
    }
    else if (iPeriodLocation == 0) {
      return "ERROR";
    }
    else {
      if (sTempNormalIcd.length() == 3) {
        return sTempNormalIcd;
      }
      else if (sTempNormalIcd.length() == 2) {
        return "0" + sTempNormalIcd;
      }
      else if (sTempNormalIcd.length() == 1) {
        return "00" + sTempNormalIcd;
      }
      else {
        return "ERROR";
      }
    }
  }
  
  //* 5150
  public static String makeDbQuotes(String sNormal) {
    if ((sNormal==null)||(sNormal.length()==0)) return "";
    sNormal = Lib.StrFindAndReplace("''","~~^~~",sNormal);
    sNormal = Lib.StrFindAndReplace("'","''",sNormal);
    sNormal = Lib.StrFindAndReplace("~~^~~","''",sNormal);
    return sNormal;
  }
  
  //*a110
  // When input is DOB in european format (19490430), this function 
  // returns a sting 8 characters long (padded with spaces).
  public static String getAge(String sDobEuropean) throws Exception {
		String sDobYear, sDobMonth, sDobDay;
		int iDobYear, iDobMonth, iDobDay;
		int iYear, iMonth, iDay;
		double dAgeDays;
		int iAge;
		String sAge;
		Exception e = new Exception("Lib.getAge Exception");
		if ((sDobEuropean==null)||(sDobEuropean.length()==0)) throw e;
		Date today = new Date();
		try {
		  sDobYear = sDobEuropean.substring(0,4);
		  iDobYear = Integer.parseInt(sDobYear);
		  if (iDobYear < 1850) throw e;
		  if (iDobYear > today.getYear()+1900) throw e;
		  int iTest = today.getYear();
		  iYear = (today.getYear() + 1900) - iDobYear;
		  sDobMonth = sDobEuropean.substring(4,6);
		  iDobMonth = Integer.parseInt(sDobMonth);
		  if (iDobMonth < 1) throw e;
		  if (iDobMonth > 12) throw e;
		  iMonth = (today.getMonth() + 1) - iDobMonth;
		  sDobDay = sDobEuropean.substring(6,8);
		  iDobDay = Integer.parseInt(sDobDay);
		  if (iDobDay < 1) throw e;
		  if (iDobDay > 31) throw e;
		  iDay = today.getDate() - iDobDay;
		  dAgeDays = iYear*365.25 + iMonth*30.5 + iDay;
		  iAge = (int)(dAgeDays/365.25);
		  if (iAge < 0) {
		    sAge = "      -1";
		    throw e;
		  }
		  else if (iAge < 10) {
		    sAge = "       " + Integer.toString(iAge);
		  }
		  else if (iAge < 100) {
		    sAge = "      " + Integer.toString(iAge);
		  }
		  else if (iAge > 150) {
		    sAge = "      -1";
		    throw e;
		  }
		  else {
		    sAge = "     " + Integer.toString(iAge);
		  }
		}
		catch (Exception ex) {
		  Lib.TError("Lib.getAge Exception: " + ex.getMessage());
		  throw e;
		}
		return sAge;
	}
	
	// A310
	// A model function for error handling
	// private static String sModule = "Module";
  private void ModelFunction () throws Exception {
    String sFunction = " ModelFunction";
    Exception e = new Exception(sModule + "." + sFunction + " failed.");
    try {
    }
    catch (Exception err) {
      Lib.TError("In " + sModule + "." + sFunction + ": " + err.getMessage());
      throw e;
    }
  }
  //* Used to set NTFS permissions on files
  public static void SetFileAccess(String Filename, String Action) throws Exception {
   String sFunction = " SetFileAccess";
	 String osName = System.getProperty("os.name");
	 if (!osName.equalsIgnoreCase("Windows 95")){
    Exception e = new Exception(sModule + "." + sFunction + " failed.");
    try {
			Runtime loRun = Runtime.getRuntime();
			Process p = loRun.exec("C:\\winnt\\system32\\Cacls.exe " + Filename + " " + Action );
			p.waitFor();
    }
    catch (Exception err) {
      Lib.TError("In " + sModule + "." + sFunction + ": " + err.getMessage());
      throw e;
    }
	 }
		/*	Displays or modifies access control lists (ACLs) of files

					CACLS filename [/T] [/E] [/C] [/G user:perm] [/R user [...]]
					               [/P user:perm [...]] [/D user [...]]
					   filename      Displays ACLs.
					   /T            Changes ACLs of specified files in
					                 the current directory and all subdirectories.
					   /E            Edit ACL instead of replacing it.
					   /C            Continue on access denied errors.
					   /G user:perm  Grant specified user access rights.
					                 Perm can be: R  Read
					                              C  Change (write)
					                              F  Full control
					   /R user	 Revoke specified user's access rights (only valid with /E).
					   /P user:perm  Replace specified user's access rights.
					                 Perm can be: N  None
					                              R  Read
					                              C  Change (write)
					                              F  Full control
					   /D user       Deny specified user access.
					Wildcards can be used to specify more that one file in a command.
					You can specify more than one user in a command.	*/
  }
//  public static void SetFileAccess(String Filename, String Action, String User, String Access) throws Exception {
//   String sFunction = " SetFileAccess";
//    Exception e = new Exception(sModule + "." + sFunction + " failed.");
//    try {
//			Runtime loRun = Runtime.getRuntime();
//			loRun.exec("\\\\diagon\\d\\AdminTools\\RpdrFile.exe " + Filename + " " + Action + " " + User + " " + Access);
//// the exec line is repeated temporarily since it can't run on diagon with the network syntax  KSmith 12/22/00
//			loRun.exec("D:\\AdminTools\\RpdrFile.exe " + Filename + " " + Action + " " + User + " " + Access);
//   }
//    catch (Exception err) {
//      Lib.TError("In " + sModule + "." + sFunction + ": " + err.getMessage());
//      throw e;
//    }
//  }

  /**
   * This method obtains a page from the Web.
   * @param url The URL of the page to obtain
   * @return The page obtained
   */
  public static String getPage(String sUrl) throws Exception {
    String sFunction = "getPage";
    Exception e = new Exception(sModule + "." + sFunction + " failed.");
    // A buffer for the incoming page
    StringBuffer oPage = new StringBuffer();
    String sNextLine; // The next line in the input stream
    
    try { 
      URL oUrl = new URL(sUrl);
      DataInputStream oInput = new DataInputStream(oUrl.openStream());
      while ((sNextLine = oInput.readLine()) != null) {
        oPage.append(sNextLine+"\r\n");
      }
    }
    catch (MalformedURLException oExcpt) {
      Lib.TError("In " + sModule + "." + sFunction + ": " + oExcpt.getMessage());
    }
    catch (IOException oExcpt) {
      Lib.TError("In " + sModule + "." + sFunction + ": " + oExcpt.getMessage());
    }
    // convert the buffer to a string and return
    return oPage.toString();
  }
  
  /*
  public static String getSecurePage(String sUrl) throws Exception {
    String sFunction = "getSecurePage";
    Exception e = new Exception(sModule + "." + sFunction + " failed.");
    if (!mbURLSet){
      try {
        if (Class.forName("com.ms.net.wininet.WininetStreamHandlerFactory" )!=null){
          URL.setURLStreamHandlerFactory(new com.ms.net.wininet.WininetStreamHandlerFactory());
          mbURLSet = true;
        }
      }
      catch (ClassNotFoundException oExcpt) {
        Lib.TError("In " + sModule + "." + sFunction + ": " + oExcpt.getMessage());
      }
    }
    return getPage(sUrl);
  }
  */
 
  public static int translateEbcdicToAscii(int ebcdicchar){  
    int e2a[] = {
          0,  1,  2,  3,156,  9,134,127,151,141,142, 11, 12, 13, 14, 15,
         16, 17, 18, 19,157,133,  8,135, 24, 25,146,143, 28, 29, 30, 31,
        128,129,130,131,132, 10, 23, 27,136,137,138,139,140,  5,  6,  7,
        144,145, 22,147,148,149,150,  4,152,153,154,155, 20, 21,158, 26,
         32,160,161,162,163,164,165,166,167,168, 91, 46, 60, 40, 43, 33,
         38,169,170,171,172,173,174,175,176,177, 93, 36, 42, 41, 59, 94,
         45, 47,178,179,180,181,182,183,184,185,124, 44, 37, 95, 62, 63,
        186,187,188,189,190,191,192,193,194, 96, 58, 35, 64, 39, 61, 34,
        195, 97, 98, 99,100,101,102,103,104,105,196,197,198,199,200,201,
        202,106,107,108,109,110,111,112,113,114,203,204,205,206,207,208,
        209,126,115,116,117,118,119,120,121,122,210,211,212,213,214,215,
        216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,
        123, 65, 66, 67, 68, 69, 70, 71, 72, 73,232,233,234,235,236,237,
        125, 74, 75, 76, 77, 78, 79, 80, 81, 82,238,239,240,241,242,243,
         92,159, 83, 84, 85, 86, 87, 88, 89, 90,244,245,246,247,248,249,
         48, 49, 50, 51, 52, 53, 54, 55, 56, 57,250,251,252,253,254,255
    };

    if ((ebcdicchar>255)||(ebcdicchar<0))
      return ebcdicchar;
    else
      return e2a[ebcdicchar];
  }
  
  public static int translateAsciiToEbcdic(int asciichar){
    int a2e[]  = {
          0,  1,  2,  3, 55, 45, 46, 47, 22,  5, 37, 11, 12, 13, 14, 15,
         16, 17, 18, 19, 60, 61, 50, 38, 24, 25, 63, 39, 28, 29, 30, 31,
         64, 79,127,123, 91,108, 80,125, 77, 93, 92, 78,107, 96, 75, 97,
        240,241,242,243,244,245,246,247,248,249,122, 94, 76,126,110,111,
        124,193,194,195,196,197,198,199,200,201,209,210,211,212,213,214,
        215,216,217,226,227,228,229,230,231,232,233, 74,224, 90, 95,109,
        121,129,130,131,132,133,134,135,136,137,145,146,147,148,149,150,
        151,152,153,162,163,164,165,166,167,168,169,192,106,208,161,  7,
         32, 33, 34, 35, 36, 21,  6, 23, 40, 41, 42, 43, 44,  9, 10, 27,
         48, 49, 26, 51, 52, 53, 54,  8, 56, 57, 58, 59,  4, 20, 62,225,
         65, 66, 67, 68, 69, 70, 71, 72, 73, 81, 82, 83, 84, 85, 86, 87,
         88, 89, 98, 99,100,101,102,103,104,105,112,113,114,115,116,117,
        118,119,120,128,138,139,140,141,142,143,144,154,155,156,157,158,
        159,160,170,171,172,173,174,175,176,177,178,179,180,181,182,183,
        184,185,186,187,188,189,190,191,202,203,204,205,206,207,218,219,
        220,221,222,223,234,235,236,237,238,239,250,251,252,253,254,255
    };

    if ((asciichar>255)||(asciichar<0))
      return asciichar;
    else
      return a2e[asciichar];
  }
  
  public static String translateEbcdicStringtoAscii(String ebsidicString){
    String returnstring="";
    char chr;
    
    for (int i =0; i<ebsidicString.length(); i++){
      chr = ebsidicString.charAt(i);
      chr = (char) translateEbcdicToAscii(chr);
      returnstring = returnstring + chr;
    }
    
    return returnstring;
  }
  
  public static boolean isFloppyInDrive()
  {
    //checks to see if there is a floppy in drive A
    
    String[] command = {"cmd.exe", "/y", "/c", "dir a:"};
    
    try {
      Process p = Runtime.getRuntime().exec(command);
      InputStream is = p.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      line = br.readLine();
      //line will come back with dir information if disk is in drive A
      //otherwise line comes back with null
      if ((line==null)||(line.length()==0))
          return false;
      else
          return true;
    }
    catch (IOException e){
      Lib.TError("Error reading file:" + e.getMessage());
      return false;
    }
  }
  
  public static Hashtable loadFilesIntoHashtable(Vector filenames) throws Exception{
    try {
      
      Hashtable h = new Hashtable();
      if ((filenames==null)||(filenames.size()==0))
        return null;
    
      String filename=null;
      for(int i = 0; i<filenames.size(); i++){
        filename = (String) filenames.elementAt(i);
        if ((filename!=null)&&(filename.length()>0)){
          h.put(filename, Lib.read(autoGetFile(filename,"",false,null)));
        }    
      }
      return h;
    }
    catch (Exception e){
      Lib.TError("Error loading files into Hashtable:" + e.getMessage());
      throw new Exception("Could not load files into Hashtable");
    }
  }
  
  public static Hashtable loadFilesIntoHashtable(String filename0, 
                                  String filename1, 
                                  String filename2, 
                                  String filename3, 
                                  String filename4, 
                                  String filename5, 
                                  String filename6, 
                                  String filename7, 
                                  String filename8, 
                                  String filename9)throws Exception {
    
    //load vector up with filenames
    
    Vector v = new Vector();
    if (filename0!=null)
      v.addElement(filename0);
    if (filename1!=null)
      v.addElement(filename1);
    if (filename2!=null)
      v.addElement(filename2);
    if (filename3!=null)
      v.addElement(filename3);
    if (filename4!=null)
      v.addElement(filename4);
    if (filename5!=null)
      v.addElement(filename5);
    if (filename6!=null)
      v.addElement(filename6);
    if (filename7!=null)
      v.addElement(filename7);
    if (filename8!=null)
      v.addElement(filename8);
    if (filename9!=null)
      v.addElement(filename9);
    
    //pass vector to vector version of this function
    return loadFilesIntoHashtable(v);
  }
	/** test to see if string is an ICD-9CM code, which means it must be in
	 * this format:
	 *  N[NN.NN] (or permutations)
	 *  VN[N.NN]
	 *  E8NN[.NN]
	 * @param s the String which is tested to see if it is a number
	 * @return boolean that signifies if s is a number
	 */
	public static boolean isICD9CM(String s) {
  	if ((s == null) || (s.length() == 0)) return true; // blanks return true for backward compatibility
    // test for first charcter
  	char c1 = s.charAt(0);
  	if ( (c1 != '1') & (c1 != '2') & (c1 != '3') & (c1 != '4') & (c1 != '5') 
  	   & (c1 != '6') & (c1 != '7') & (c1 != '8') & (c1 != '9') & (c1 != '0') 
  	   & (c1 != 'V') & (c1 != 'v') & (c1 != 'E') & (c1 != 'e') ) 
  	  return false;
    else if ( (c1 != 'V') & (c1 != 'v') & (c1 != 'E') & (c1 != 'e') &&
              (s.length() <= 2) )
  	  return false;
    if (s.length() == 1) return true;
    // test for second character
  	char c2 = s.charAt(1);
    if ((c1 == 'E') | (c1 == 'e')) {
      if ( ((c2 != '8') && (c2 != '9')) || (s.length() <= 3) || (s.length() >= 8) ) return false;
    }
  	else if ( (c2 != '1') & (c2 != '2') & (c2 != '3') & (c2 != '4') & (c2 != '5') 
  	   & (c2 != '6') & (c2 != '7') & (c2 != '8') & (c2 != '9') & (c2 != '0') 
  	   & (c2 != '.') || (s.length() >= 7) ) 
  	  return false;
    //test for remaining characters
  	for (int i=2; i<s.length()-1; i++) {
  	  char c = s.charAt(i);
  		if ( (c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5') 
  		   & (c != '6') & (c != '7') & (c != '8') & (c != '9') & (c != '0') 
  		   & (c != '.') ) 
  		  return false;
  	}
	  return true;
	}
  	/** test to see if string is a TSI ICD-9CM code, which means it must be in
	 * this format:
	 *  NNN[NN] (or permutations)
	 *  VNN[NN]
	 *  E8NN[NN]
	 * @param s the String which is tested to see if it is a number
	 * @return boolean that signifies if s is a number
	 */
	public static boolean isTSI_ICD9(String s) {
  	if ((s == null) || (s.length() == 0)) return true; // blanks return true for backward compatibility
    // test for first charcter
  	char c1 = s.charAt(0);
  	if ( (c1 != '1') & (c1 != '2') & (c1 != '3') & (c1 != '4') & (c1 != '5') 
  	   & (c1 != '6') & (c1 != '7') & (c1 != '8') & (c1 != '9') & (c1 != '0') 
  	   & (c1 != 'V') & (c1 != 'v') & (c1 != 'E') & (c1 != 'e') ) 
  	  return false;
    else if ( (c1 != 'V') & (c1 != 'v') & (c1 != 'E') & (c1 != 'e') &&
              (s.length() <= 2) )
  	  return false;
    else if (s.length() <= 2) return false;
    // test for second character
  	char c2 = s.charAt(1);
    if ((c1 == 'E') | (c1 == 'e')) {
      if ( ((c2 != '8') && (c2 != '9')) || (s.length() <= 3) || (s.length() >= 7) ) 
        return false;
    }
  	else if ( (c2 != '1') & (c2 != '2') & (c2 != '3') & (c2 != '4') & (c2 != '5') 
  	   & (c2 != '6') & (c2 != '7') & (c2 != '8') & (c2 != '9') & (c2 != '0') 
  	   & (c2 != '.') || (s.length() >= 6) ) 
  	  return false;
    //test for remaining characters
  	for (int i=2; i<s.length()-1; i++) {
  	  char c = s.charAt(i);
  		if ((c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5') 
  		     & (c != '6') & (c != '7') & (c != '8') & (c != '9') & (c != '0') 
  		     & (c != '.')) 
  		  return false;
  	}
	  return true;
  }
}