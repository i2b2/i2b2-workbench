/*
 * Copyright (c) 2006-2016 Partners HealthCare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class DateParser implements UIConst
{
	public static final int [] MAX_DAYS_OF_MONTHS = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	public static final GregorianCalendar CURRENT_TIME = new GregorianCalendar();
	
	public static boolean isLegal( String dateString )
	{
		if ( isEmpty(dateString)) 
			return true;
		else
		{
			String [] parts = dateString.split("/");
			if ( parts.length != 3) return false;
			try
			{
				int month = Integer.parseInt( parts[0] );
				int day = Integer.parseInt( parts[1] );
				int year = Integer.parseInt( parts[2] );
				if ( month > MAX_DAYS_OF_MONTHS.length || month < 1)
					return false;
				if ( day > MAX_DAYS_OF_MONTHS[month-1] || day < 1)
					return false;
				if ( month == 2) //if it is February
					if ( !CURRENT_TIME.isLeapYear(year) && (day > 28) )
						return false;
				return true;
			}
			catch ( NumberFormatException e )
			{ return false; }			
		}
	}
	
	public static boolean isEmpty( String dateString )
	{
		if ( dateString.toLowerCase().equals( NONE.toLowerCase() ))
			return true;
		else if  ( dateString.toLowerCase().equals( DATE_FORMAT.toLowerCase() ))
			return true;
		else if  ( dateString.length()== 0 )
			return true;
		return false;
	}
	
	public static Date parseDate( String dateString )
	{	
		try
		{	
			Date date = DEFAULT_FORMAT.parse( dateString );
			return date;
		}
		catch( ParseException e )
		{	
			return null;	
		}
	}
	
	public static boolean isValidCharacterForDates( char c )
	{
		// ASCII 47='/', 48='0', 58='9' 
		if ( (c>46) && (c<59) ) return true;
		if ( c == 8 )			// allow BACKSPACE
			return true;
		return false;
	}
	
	public static String toFormat( GregorianCalendar cal )
	{
		return (cal.get(Calendar.MONTH)+1) + "/" + cal.get( Calendar.DAY_OF_MONTH ) + "/" + cal.get( Calendar.YEAR );
	}
}
