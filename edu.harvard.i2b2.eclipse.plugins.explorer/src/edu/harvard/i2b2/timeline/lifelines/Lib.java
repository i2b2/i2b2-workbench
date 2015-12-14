/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 * 		Shawn Murphy, MD, Ph.D
 *     
 */
/**
 /* Big bunch of Static funtions
 /* (c) 1998 Shawn Murphy
 /* Last Modified 9148
 /*
 /* String getJavaVersion()
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

package edu.harvard.i2b2.timeline.lifelines;

import java.applet.Applet;
import java.awt.Frame;
import java.awt.Component;
import java.awt.Container;
import java.awt.Panel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Date;

public class Lib {
	public static String getJavaVersion() {
		return "1.02";
	}

	/**
	 * This class shouldn't be instantiated.
	 */
	private Lib() {
	}

	// test to see if string is a number
	public static boolean isNumber(String s) {
		if ((s == null) | (s.length() == 0))
			return false; // blanks are not numbers
		char c;
		for (int i = 0; i < s.length() - 1; i++) {
			c = s.charAt(i);
			if ((c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5')
					& (c != '6') & (c != '7') & (c != '8') & (c != '9')
					& (c != '0') & (c != '.'))
				return false;
		}
		return true;
	}

	// test to see if string is a unit number (can have a '-', but not a '.')
	public static boolean isUnitNumber(String s) {
		if ((s == null) | (s.length() == 0))
			return false; // blanks are not unit numbers
		char c;
		for (int i = 0; i < s.length() - 1; i++) {
			c = s.charAt(i);
			if ((c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5')
					& (c != '6') & (c != '7') & (c != '8') & (c != '9')
					& (c != '0') & (c != '-'))
				return false;
		}
		return true;
	}

	// test to see if string is my date format (a128 kind)
	public static boolean isMydate(String s) {
		if ((s == null) | (s.length() == 0))
			return false; // blanks are not dates
		if (s.length() != 4)
			return false;
		char c;
		c = s.charAt(0);
		if ((c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5')
				& (c != '6') & (c != '7') & (c != '8') & (c != '9')
				& (c != 'a') & (c != 'A') & (c != 'b') & (c != 'B')
				& (c != 'c') & (c != 'C'))
			return false;
		for (int i = 1; i < 4; i++) {
			c = s.charAt(i);
			if ((c != '1') & (c != '2') & (c != '3') & (c != '4') & (c != '5')
					& (c != '6') & (c != '7') & (c != '8') & (c != '9')
					& (c != '0'))
				return false;
		}
		return true;
	}

	public static String removeDashes(String s) {
		if ((s == null) | (s.length() == 0))
			return "";
		return StrFindAndReplace("-", "", s);
	}

	public static String makeEuropeanDateFromMydate(String s) {
		if ((s == null) | (s.length() == 0))
			return "";
		if (!isMydate(s))
			return "";
		String dateSeparator = "-";
		StringBuffer europeanDate = new StringBuffer(10);
		char c = s.charAt(3);
		int theYear = Integer.parseInt(s.substring(3, 4));
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
	 * /* ReturnSubFromSuper - returns a substring between two delimiters from
	 * /* SuperString, where SubNumber is the #th occurence of the delimiter. /
	 */
	public static String ReturnSubFromSuper(String SuperString, int SubNumber,
			char Delimiter) {
		int StartOfSegment = 0;
		int EndOfSegment = 0;

		if (SuperString == null)
			return "";
		if (SuperString.length() == 0)
			return "";
		if (Delimiter == '\0')
			return "";
		if (SubNumber < 0)
			SubNumber = 0;

		StartOfSegment = indexOfNum(SuperString, SubNumber, Delimiter);
		if (SubNumber == 0)
			StartOfSegment = -1;
		else if (StartOfSegment == -1)
			return "";

		EndOfSegment = indexOfNum(SuperString, SubNumber + 1, Delimiter);
		if (EndOfSegment == -1) {
			EndOfSegment = SuperString.length();
		}
		if ((EndOfSegment - StartOfSegment) <= 1)
			return "";
		return SuperString.substring(StartOfSegment + 1, EndOfSegment);
	}

	/**
	 * /* indexOfNum - returns the index where theChar occurs /* on the theNum
	 * occurence. If theChar does not occur, /* or theNum is 0, then -1 is
	 * returned. /
	 */
	public static int indexOfNum(String theString, int theNum, char theChar) {
		int count;
		int tempIndex;

		if (theNum <= 0)
			return -1;
		if (theString == null)
			return -1;
		if (theChar == '\0')
			return -1;
		if (theString.length() == 0)
			return -1;

		tempIndex = -1;
		for (count = 1; count < theNum + 1; count++) {
			tempIndex = theString.indexOf(theChar, tempIndex + 1);
			if (tempIndex == -1)
				return -1;
		}
		return tempIndex;
	}

	/**
	 * /* remove - removes a character at postition CharNum /* (zero based) from
	 * theString. /
	 */
	public static void remove(int CharNum, StringBuffer theString) {
		int i = 0;

		if ((theString == null) | (theString.length() == 0))
			return;
		if ((CharNum < 0) | (theString.length() < CharNum))
			return;

		for (i = CharNum; i < theString.length() - 1; i++) {
			theString.setCharAt(i, theString.charAt(i + 1));
		}
		theString.setLength(theString.length() - 1);
		return;
	}

	/**
	 * /* PullOut - finds stuff between left and right in StringBuffer /*
	 * theSBuffer, and returns it in pulledOut, /* starts at fromIndex, returns
	 * index of left find. /
	 */
	public static int PullOut(String left, String right,
			StringBuffer theSBuffer, StringBuffer pulledOut, int fromIndex) {

		String interString;
		int leftIndex, farLeftIndex, rightIndex;

		if (right == null)
			return -1;
		if (left == null)
			return -1;
		if (theSBuffer == null)
			return -1;
		if (theSBuffer.length() < (right.length() + left.length()))
			return -1;
		if ((fromIndex < 0) || (fromIndex > theSBuffer.length()))
			return -1;
		if (pulledOut == null)
			pulledOut = new StringBuffer("");
		if (pulledOut.length() != 0)
			pulledOut.setLength(0);

		interString = theSBuffer.toString();
		if (left.length() == 0)
			leftIndex = 0;
		else {
			leftIndex = interString.indexOf(left, fromIndex);
			if (leftIndex == -1)
				return -1;
		}
		farLeftIndex = leftIndex + left.length();
		if (right.length() == 0)
			rightIndex = theSBuffer.length();
		else {
			rightIndex = interString.indexOf(right, farLeftIndex);
			if (rightIndex == -1)
				return -1;
		}
		pulledOut.append(interString.substring(farLeftIndex, rightIndex));
		return leftIndex;
	}

	/*
	 * this can easily be made into cut-out with length and returned index for
	 * (i=theIndex;i<theSBuffer.length()-find.length();i++) {
	 * theSBuffer.setCharAt(i,theSBuffer.charAt(i+find.length())); }
	 * theSBuffer.setLength(theSBuffer.length() - find.length());
	 * theSBuffer.insert(theIndex,replace); return theIndex; }
	 */

	/**
	 * /* FindAndReplace - finds and replaces in StringBuffer theSBuffer, /*
	 * starts at fromIndex, returns index of find. /
	 */
	public static int FindAndReplace(String find, String replace,
			StringBuffer theSBuffer, int fromIndex) {

		String interString;
		int theIndex, i;

		if (find == null)
			return -1;
		if (replace == null)
			return -1;
		if (theSBuffer == null)
			return -1;
		if (theSBuffer.length() < find.length())
			return -1;
		if ((fromIndex < 0) || (fromIndex > theSBuffer.length()))
			return -1;

		interString = theSBuffer.toString();
		theIndex = interString.indexOf(find, fromIndex);
		if (theIndex == -1)
			return -1;
		for (i = theIndex; i < theSBuffer.length() - find.length(); i++) {
			theSBuffer.setCharAt(i, theSBuffer.charAt(i + find.length()));
		}
		theSBuffer.setLength(theSBuffer.length() - find.length());
		theSBuffer.insert(theIndex, replace);
		return theIndex;
	}

	/**
	 * /* StrFindAndReplace - finds and replaces all in String theString. /
	 */
	public static String StrFindAndReplace(String find, String replace,
			String theString) {
		if (theString.length() == 0)
			return "";
		if (find.length() == 0)
			return theString;

		StringBuffer theBuf = new StringBuffer(theString);
		int found = 0;
		while (found != -1) {
			found = FindAndReplace(find, replace, theBuf, 0);
		}
		return theBuf.toString();
	}

	public static byte[] IntTo6Bytes(int theInt) throws NumberFormatException {
		if (theInt < 0) {
			NumberFormatException e = new NumberFormatException(
					"y coordinate out of bounds in ZTX");
			throw e;
		}
		byte[] theBytes = new byte[6];
		theBytes[0] = IntToByte(0);
		theBytes[1] = IntToByte(0);
		theBytes[2] = IntToByte(theInt / 16777216);
		theBytes[3] = IntToByte((theInt % 16777216) / 65536);
		theBytes[4] = IntToByte((theInt % 65536) / 256);
		theBytes[5] = IntToByte(theInt % 256);
		return theBytes;
	}

	/*
	 * public static byte[] IntTo2Bytes(int theInt) throws NumberFormatException
	 * { if ((theInt < 0) || (theInt > 65536)) { NumberFormatException e = new
	 * NumberFormatException ("y coordinate out of bounds in ZTX"); throw e; }
	 * byte[] theBytes = new byte[2]; theBytes[0] = IntToByte(theInt/256);
	 * theBytes[1] = IntToByte(theInt%256); return theBytes; }
	 */
	public static byte[] IntTo2Bytes(int theInt) throws NumberFormatException {
		if ((theInt < 0) || (theInt > 65536)) {
			NumberFormatException e = new NumberFormatException(
					"y coordinate out of bounds in ZTX");
			throw e;
		}
		byte[] theBytes = new byte[2];
		theBytes[0] = (byte) ((theInt >> 8) & 0xFF);
		theBytes[1] = (byte) (theInt & 0xFF);
		return theBytes;
	}

	/*
	 * public static byte IntToByte(int theInt) throws NumberFormatException {
	 * byte theByte; byte lastBitSet = -128; if ((theInt < 0) || (theInt > 256))
	 * { NumberFormatException e = new NumberFormatException
	 * ("Error in IntToBytes"); throw e; } if (theInt <= 127) { theByte =
	 * (byte)(theInt); } else { theByte = (byte)(theInt - 128); theByte =
	 * (byte)(theByte | lastBitSet); } return theByte; }
	 */
	public static byte IntToByte(int theInt) throws NumberFormatException {
		if ((theInt < 0) || (theInt > 256)) {
			NumberFormatException e = new NumberFormatException(
					"Error in IntToBytes");
			throw e;
		}
		return (byte) (theInt & 0xFF);
	}

	public static String getPostValue(String name, String post) {
		if (post.length() == 0)
			return "";
		if (name.length() == 0)
			return "";

		try {
			int namePlace = -1;
			int equalsPlace = -1;
			while (true) {
				while (true) {
					namePlace = post.indexOf(name, namePlace + 1);
					if (namePlace == 0)
						break;
					if (namePlace > 0) {
						if (post.charAt(namePlace - 1) == '&')
							break;
					}
					if (namePlace == -1)
						return "";
				}
				equalsPlace = post.indexOf('=', namePlace + 1);
				if (equalsPlace == -1)
					return "";
				if (name.equalsIgnoreCase(post
						.substring(namePlace, equalsPlace).trim()))
					break;
			}
			int valueEnd = post.indexOf('&', equalsPlace);
			return post.substring(equalsPlace + 1, valueEnd);
		} catch (Exception e) {
			System.out.println("getPostValue Error: " + e.getMessage());
			return "";
		}
	}

	public static String toTwoPlace(int theInteger) {
		if (theInteger > 9) {
			return Integer.toString(theInteger);
		} else {
			return "0" + Integer.toString(theInteger);
		}
	}

	/**
	 * /* Wait - waits for a number of seconds. /
	 */
	public static void Wait(int seconds) {
		long milliseconds = seconds * 1000;
		Date dateThen = new Date();
		long then = dateThen.getTime();
		while (true) {
			Date dateNow = new Date();
			long now = dateNow.getTime();
			if ((now - then) >= milliseconds)
				break;
			// Thread.currentThread().yield();
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}

		}
	}

	/**
	 * /* WaitWithStatus - waits for a number of seconds. /
	 */
	public static void Wait(Component object, int seconds) {
		long milliseconds = seconds * 1000;
		Date dateThen = new Date();
		long then = dateThen.getTime();
		while (true) {
			Date dateNow = new Date();
			long now = dateNow.getTime();
			if ((now - then) >= milliseconds)
				break;
			// Thread.currentThread().yield();
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}

	public static String statusPercent(double seconds, double projectedSeconds) {
		if (seconds < 0)
			seconds = 0;
		if (projectedSeconds < 0)
			seconds = 100;

		double percentIn = seconds / projectedSeconds;
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
		for (int i = 1; i <= igpercentOut; i++) {
			theGraph += "X";
		}
		for (int i = igpercentOut + 1; i <= 10; i++) {
			theGraph += "_";
		}
		theGraph += "]";
		return theGraph + theSentence;
	}

	public static void WaitForChar() {
		try {
			System.in.read();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static Frame getFrame(Component component) {
		Component c = component;

		if (c instanceof Frame)
			return (Frame) c;

		while ((c = c.getParent()) != null) {
			if (c instanceof Frame)
				return (Frame) c;
		}
		return null;
	}

	public static Applet getApplet(Component component) {
		Component c = component;

		if (c instanceof Applet)
			return (Applet) c;

		while ((c = c.getParent()) != null) {
			if (c instanceof Applet)
				return (Applet) c;
		}
		return null;
	}

	public static Panel getPanel(Component component) {
		Component c = component;

		if (c instanceof Panel)
			return (Panel) c;

		while ((c = c.getParent()) != null) {
			if (c instanceof Panel)
				return (Panel) c;
		}
		return null;
	}

	public static void sort(String a[], boolean caseSensitive) throws Exception {
		boolean stopRequested = false;
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
					if (a[j].compareTo(a[j + 1]) > 0) {
						bigger = true;
					} else {
						bigger = false;
					}
				else if (a[j].toLowerCase().compareTo(a[j + 1].toLowerCase()) > 0) {
					bigger = true;
				} else {
					bigger = false;
				}
				if (bigger == true) {
					String T = a[j];
					a[j] = a[j + 1];
					a[j + 1] = T;
					swapped = true;
				}
				// pause(st, limit);
			}
			if (!swapped) {
				return;
			} else
				swapped = false;
			for (j = limit; --j >= st;) {
				if (stopRequested) {
					return;
				}
				if (caseSensitive)
					if (a[j].compareTo(a[j + 1]) > 0) {
						bigger = true;
					} else {
						bigger = false;
					}
				else if (a[j].toLowerCase().compareTo(a[j + 1].toLowerCase()) > 0) {
					bigger = true;
				} else {
					bigger = false;
				}
				if (bigger == true) {
					String T = a[j];
					a[j] = a[j + 1];
					a[j + 1] = T;
					swapped = true;
				}
				// pause(st, limit);
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
			SMonth = '0' + String.valueOf(month);
		} else {
			SMonth = String.valueOf(month);
		}
		if (day < 10) {
			SDay = '0' + String.valueOf(day);
		} else {
			SDay = String.valueOf(day);
		}
		year = year + 1900;
		SYear = String.valueOf(year);
		return SYear + SMonth + SDay;
	}

	// Generates a new random string
	static public String newRandomString(int theLength) {
		if (theLength <= 0)
			return "";
		byte[] bytes = new byte[theLength];
		for (int i = 0; i < theLength; i++) {
			bytes[i] = (byte) (Math.random() * 128.0);
		}
		return new String(bytes, 0);
	}

	// Copies a byte array
	static public byte[] copyByteArray(byte[] byteArray) {
		if (byteArray == null) {
			System.out.println("copyByteArray got passed a null");
		}
		if (byteArray.length == 0)
			return new byte[0];
		byte[] newBytes = new byte[byteArray.length];
		for (int i = 0; i < byteArray.length; i++) {
			newBytes[i] = byteArray[i];
		}
		return newBytes;
	}

	/**
	 * Copy files and/or directories.
	 * 
	 * @param src
	 *            source file or directory
	 * @param dest
	 *            destination file or directory
	 * @exception IOException
	 *                if operation fails
	 */
	private static int BUFSIZE = 4096;

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
			} else if (dest.isDirectory()) {
				dest = new File(dest + File.separator + src);
			}
		} else if (src.isDirectory()) {
			if (dest.isFile())
				throw new IOException("cannot copy directory " + src
						+ " to file " + dest);

			if (!dest.exists())
				dest.mkdir();
		}

		// The following line requires that the file already
		// exists!! Thanks to Scott Downey (downey@telestream.com)
		// for pointing this out. Someday, maybe I'll find out
		// why java.io.File.canWrite() behaves like this. Is it
		// intentional for some odd reason?
		// if (!dest.canWrite())
		// throw new IOException("destination is unwriteable: " + dest);

		// If we've gotten this far everything is OK and we can copy.
		if (src.isFile()) {
			try {
				source = new FileInputStream(src);
				destination = new FileOutputStream(dest);
				buffer = new byte[1024];
				while (true) {
					bytes_read = source.read(buffer);
					if (bytes_read == -1)
						break;
					destination.write(buffer, 0, bytes_read);
				}
			} finally {
				if (source != null)
					try {
						source.close();
					} catch (IOException e) {
						;
					}
				if (destination != null)
					try {
						destination.close();
					} catch (IOException e) {
						;
					}
			}
		} else if (src.isDirectory()) {
			String targetfile, target, targetdest;
			String[] files = src.list();

			for (int i = 0; i < files.length; i++) {
				targetfile = files[i];
				target = src + File.separator + targetfile;
				targetdest = dest + File.separator + targetfile;

				if ((new File(target)).isDirectory()) {
					copy(new File(target), new File(targetdest));
				} else {

					try {
						source = new FileInputStream(target);
						destination = new FileOutputStream(targetdest);
						buffer = new byte[1024];

						while (true) {
							bytes_read = source.read(buffer);
							if (bytes_read == -1)
								break;
							destination.write(buffer, 0, bytes_read);
						}
					} finally {
						if (source != null)
							try {
								source.close();
							} catch (IOException e) {
								;
							}
						if (destination != null)
							try {
								destination.close();
							} catch (IOException e) {
								;
							}
					}
				}
			}
		}
	}

	/**
	 * File.getParent() can return null when the file is specified without a
	 * directory or is in the root directory. This method handles those cases.
	 * 
	 * @param f
	 *            the target File to analyze
	 * @return the parent directory as a File
	 */
	private static File parent(File f) {
		String dirname = f.getParent();
		if (dirname == null) {
			if (f.isAbsolute())
				return new File(File.separator);
			else
				return new File(System.getProperty("user.dir"));
		}
		return new File(dirname);
	}

	/**
	 * Read file into a string.
	 * 
	 * @param src
	 *            source file or directory
	 * @exception IOException
	 *                if operation fails
	 */
	public static String oldRead(File src) throws IOException {

		FileInputStream source = null;
		byte[] buffer;
		int bytes_read = 0, total_bytes_read = 0;
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
				while (true) {
					bytes_read = source.read(buffer);
					if (bytes_read == -1)
						break;
					innerString = new String(buffer, 0);
					outerString = outerString.append(innerString);
					total_bytes_read = total_bytes_read + bytes_read;
				}
			} finally {
				if (source != null)
					try {
						source.close();
					} catch (IOException e) {
						;
					}
			}
			outerString.setLength(total_bytes_read);
			return outerString.toString();
		} else
			throw new IOException("ambiguous file error: " + src);
	}

	/**
	 * Write file as a string.
	 * 
	 * @param src
	 *            source file or directory
	 * @exception IOException
	 *                if operation fails
	 */
	public static void write(File dest, String outString) throws IOException {

		FileOutputStream outFileStream = null;
		byte[] buffer;
		// Make sure the specified source exists and is readable.
		// if (dest.exists())
		// throw new IOException("destination already exists: " + dest);
		if (dest.isDirectory())
			throw new IOException("destination is a directory: " + dest);

		// If we've gotten this far everything is OK and we can write.
		// if (dest.isFile()) {
		try {
			System.out.println(outString.length());
			outFileStream = new FileOutputStream(dest);
			buffer = new byte[outString.length()];
			outString.getBytes(0, outString.length(), buffer, 0);
			outFileStream.write(buffer, 0, outString.length());
		} catch (IOException e) {
			throw new IOException("trouble writing file: " + dest);
		}
		return;
		// }
		// else
		// throw new IOException("ambiguous file write error: " + dest);
	}

	public static String read(File theFile) throws IOException {

		byte buf[];
		String data = new String("");
		int max = 0, count = 0, total = 0;

		// Make sure the specified source exists and is readable.
		if (!theFile.exists())
			throw new IOException("source not found: " + theFile);
		if (!theFile.canRead())
			throw new IOException("source is unreadable: " + theFile);
		if (theFile.isDirectory())
			throw new IOException("source is a directory: " + theFile);

		// If we've gotten this far everything is OK and we can read.
		if (theFile.isFile()) {
			buf = new byte[BUFSIZE];
			try {
				FileInputStream input = new FileInputStream(theFile);
				max = input.available();
				total = 0;

				while (total < max) {
					count = BUFSIZE;
					if (max - total < count)
						count = max - total;
					count = input.read(buf, 0, count);
					data += new String(buf, 0, 0, count);
					total += count;
				}
				input.close();
			} catch (IOException e) {
				throw new IOException("openFile: read error: " + e.getMessage());
			}
			return data;
		}
		return null;
	}

	public static String openFile(Component theComponent) throws IOException {
		byte buf[];
		String data = new String("");

		int max, count, total;

		buf = new byte[BUFSIZE];

		File theFile = getFile(theComponent, "", FileDialog.LOAD);

		if (theFile != null) {
			try {
				FileInputStream input = new FileInputStream(theFile);
				max = input.available();
				total = 0;

				while (total < max) {
					count = BUFSIZE;
					if (max - total < count)
						count = max - total;
					count = input.read(buf, 0, count);
					data += new String(buf, 0, 0, count);
					total += count;
				}
				input.close();
			} catch (IOException e) {
				throw new IOException("openFile: read error: " + e.getMessage());
			}
			return data;
		}
		return null;
	}

	private static File getFile(Component theComponent, String fileName,
			int mode) {
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
			filename = filename.substring(0, filename.length() - 4);

		pathname = d.getDirectory() + filename;

		if (filename != null) {
			file = new File(pathname);
			return file;
		} else
			return null;
	}

	/*********************************************************************
	 * autoGetFile - static method to get a 'File' object. The absolute path is
	 * the User's current working directory (where the application resides)
	 * unless if the absolutePath variable is null or empty.
	 *********************************************************************/
	public static File autoGetFile(String fileName, String absolutePath,
			String prompt, boolean quiet, Component theComponent) {
		boolean fileError = false;
		File absoluteFilePath;
		File returnedFile;

		if (absolutePath == null) {
			absoluteFilePath = new File(System.getProperty("user.dir"));
		} else if (absolutePath.length() == 0) {
			absoluteFilePath = new File(System.getProperty("user.dir"));
		} else {
			absoluteFilePath = new File(absolutePath);
			if (!absoluteFilePath.isDirectory()) {
				absoluteFilePath = new File(absolutePath.substring(0,
						absolutePath.length() - 1));
				if (!absoluteFilePath.isDirectory()) {
					absoluteFilePath = new File(System.getProperty("user.dir"));
				}
			}
		}
		returnedFile = new File(absoluteFilePath.toString() + File.separator
				+ fileName);
		if (fileName.length() == 0) {
			prompt = "Specify file name ...";
			fileError = true;
		}
		// Make sure the specified source exists and is readable.
		else if (!returnedFile.exists()) {
			prompt = "Specify file location ...";
			fileError = true;
		} else if (!returnedFile.canRead()) {
			prompt = "Specify readable file ...";
			fileError = true;
		} else if (returnedFile.isDirectory()) {
			prompt = "Specify a non-directory ...";
			fileError = true;
		}
		if (fileError && !quiet && (theComponent != null)) {
			FileDialog d = new FileDialog(getFrame(theComponent), prompt,
					FileDialog.LOAD);
			d.setFile("Output.txt");
			// d.setFile("*");
			d.setDirectory(".");
			d.show();
			String interFilename = d.getFile();
			if (interFilename.endsWith(".*.*")) // must be a java-on-windows bug
				interFilename = interFilename.substring(0, interFilename
						.length() - 4);
			returnedFile = new File(d.getDirectory() + interFilename);
		} else if (fileError) {
			System.out.println(prompt);
			return null;
		}
		return returnedFile;
	}

	/*********************************************************************
	 * autoGetRAFile - static method to get a 'RandomAccessFile' object. Same
	 * input parameters as above.
	 *********************************************************************/
	public static RandomAccessFile autoGetRAFile(String fileName,
			String absolutePath, String prompt, boolean quiet,
			Component theComponent) throws Exception {
		File interFile = autoGetFile(fileName, absolutePath, prompt, quiet,
				theComponent);
		return new RandomAccessFile(interFile, "rw");
	}

	public static String returnBaseFileNameWithoutSuffix(String fileName) {
		return returnBaseFileName(fileName, false);
	}

	public static String returnBaseFileNameWithSuffix(String fileName) {
		return returnBaseFileName(fileName, true);
	}

	public static String returnBaseFileName(String fileName, boolean wantsSuffix) {
		if ((fileName == null) | (fileName.length() == 0))
			return "";
		int lastSeparatorIndex = fileName.lastIndexOf(File.separator);
		if (lastSeparatorIndex >= 0) {
			fileName = fileName
					.substring(lastSeparatorIndex, fileName.length());
		}
		if (wantsSuffix)
			return fileName;
		lastSeparatorIndex = fileName.lastIndexOf('.');
		if (lastSeparatorIndex >= 0) {
			fileName = fileName.substring(0, lastSeparatorIndex);
		}
		return fileName;
	}

	public static void addVertCmp(Container toContainer,
			GridBagLayout usingGridBagLayout,
			GridBagConstraints usingGridBagConstraints, Component theComponent,
			int cellsWide, double widthWeight) {
		usingGridBagConstraints.weighty = 1.0;
		usingGridBagConstraints.gridwidth = cellsWide;
		usingGridBagConstraints.weightx = widthWeight;
		usingGridBagLayout
				.setConstraints(theComponent, usingGridBagConstraints);
		toContainer.add(theComponent);
	}

	public static void addHorizCmp(Container toContainer,
			GridBagLayout usingGridBagLayout,
			GridBagConstraints usingGridBagConstraints, Component theComponent,
			int cellsTall, double heightWeight) {
		usingGridBagConstraints.weightx = 1.0;
		usingGridBagConstraints.gridheight = cellsTall;
		usingGridBagConstraints.weighty = heightWeight;
		usingGridBagLayout
				.setConstraints(theComponent, usingGridBagConstraints);
		toContainer.add(theComponent);
	}

	public static void addPlainVertComp(Container toContainer,
			Component theComponent, GridBagLayout theGridBagLayout,
			double widthWeight) {
		if (theGridBagLayout == null) {
			System.out.println("addPlainVertComp: null GridBagLayout");
			return;
		}
		if ((widthWeight < 0) || (widthWeight > 1)) {
			System.out.println("Illegal weight in Gridbag call");
		}
		toContainer.setLayout(theGridBagLayout);
		GridBagConstraints usingGridBagConstraints = new GridBagConstraints();
		usingGridBagConstraints.weighty = 1.0;
		usingGridBagConstraints.weightx = widthWeight;
		usingGridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		int cellsWide = (int) Math.round((float) widthWeight * 100.0);
		usingGridBagConstraints.gridwidth = cellsWide;
		// nothing should be added to determine minimum size of component
		usingGridBagConstraints.ipady = 0;
		usingGridBagConstraints.ipadx = 0;
		// no border around the component
		usingGridBagConstraints.insets = new Insets(0, 0, 0, 0);
		// stretch the component to fill the region
		usingGridBagConstraints.fill = GridBagConstraints.BOTH;
		theGridBagLayout.setConstraints(theComponent, usingGridBagConstraints);
		toContainer.add(theComponent);
	}

	public static void addPlainHorizComp(Container toContainer,
			Component theComponent, GridBagLayout theGridBagLayout,
			double heightWeight) {
		if (theGridBagLayout == null) {
			System.out.println("addPlainHorizComp: null GridBagLayout");
			return;
		}
		if ((heightWeight < 0) || (heightWeight > 1)) {
			System.out.println("Illegal weight in Gridbag call");
		}
		toContainer.setLayout(theGridBagLayout);
		GridBagConstraints usingGridBagConstraints = new GridBagConstraints();
		usingGridBagConstraints.weightx = 1.0;
		usingGridBagConstraints.weighty = heightWeight;
		usingGridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		int cellsTall = (int) Math.round((float) heightWeight * 100.0);
		usingGridBagConstraints.gridheight = cellsTall;
		// nothing should be added to determine minimum size of component
		usingGridBagConstraints.ipady = 0;
		usingGridBagConstraints.ipadx = 0;
		// no border around the component
		usingGridBagConstraints.insets = new Insets(0, 0, 0, 0);
		// stretch the component to fill the region
		usingGridBagConstraints.fill = GridBagConstraints.BOTH;
		theGridBagLayout.setConstraints(theComponent, usingGridBagConstraints);
		toContainer.add(theComponent);
	}

	/**
	 * Error functions
	 */

	public static boolean m_MessageToConsole = true;
	public static int m_dbgLevel = 2;
	public static String logFile = "SMlog.txt";
	public static String errFile = "SMerr.txt";

	public static void Error(String s) {
		if (m_dbgLevel < 1)
			return;
		if (m_MessageToConsole)
			System.out.println(s);
		try {
			RandomAccessFile f = new RandomAccessFile(errFile, "rw");
			f.seek(f.length());
			f.writeBytes(s);
			f.writeByte('\r');
			f.writeByte('\n');
			f.close();
		} catch (Exception e) {
			if (m_MessageToConsole)
				System.out.println("File " + errFile + " error: " + e);
		}
	}

	public static void TError(String s) {
		if (m_dbgLevel < 1)
			return;
		if (m_MessageToConsole)
			System.out.println(s);
		try {
			RandomAccessFile f = new RandomAccessFile(errFile, "rw");
			f.seek(f.length());
			Date today = new Date();
			f.writeBytes('[' + today.toLocaleString() + "] ");
			f.writeBytes(s);
			f.writeByte('\r');
			f.writeByte('\n');
			f.close();
		} catch (Exception e) {
			if (m_MessageToConsole)
				System.out.println("File " + errFile + " error: " + e);
		}
	}

	public static void Message(String s) {
		if (m_dbgLevel < 2)
			return;
		if (m_MessageToConsole)
			System.out.println(s);
		try {
			RandomAccessFile f = new RandomAccessFile(logFile, "rw");
			f.seek(f.length());
			f.writeBytes(s);
			f.writeByte('\r');
			f.writeByte('\n');
			f.close();
		} catch (Exception e) {
			if (m_MessageToConsole)
				System.out.println("File " + logFile + " error: " + e);
		}
	}

	public static void TMessage(String s) {
		if (m_dbgLevel < 2)
			return;
		if (m_MessageToConsole)
			System.out.println(s);
		try {
			RandomAccessFile f = new RandomAccessFile(logFile, "rw");
			f.seek(f.length());
			Date today = new Date();
			f.writeBytes('[' + today.toLocaleString() + "] ");
			f.writeBytes(s);
			f.writeByte('\r');
			f.writeByte('\n');
			f.close();
		} catch (Exception e) {
			if (m_MessageToConsole)
				System.out.println("File " + logFile + " error: " + e);
		}
	}

}