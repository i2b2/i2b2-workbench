/*
 * Copyright (c) 2006-2015 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Settings 
{
	
	/*
	 * Static default values
	 */
	public static final String		TAB = "\t";
	public static final String		TEMPORAL_QUERYTOOL_SETTINGS = "[TemporalQueryToolSettings]";
	
	private static final String		SETTINGS_FILE_NAME = "." + Files.getPluginName() + ".settings";
	
	protected static final int		DEFAULT_NON_TEMPORAL_NUM_GROUPS = 3;
	protected static final int		DEFAULT_TEMPORAL_NUM_GROUPS 	= 1;
	protected static final int		DEFAULT_ITEM_COUNT_HELP_CUTOFF 	= 2; 

	public static final String 	QT_MAX_WAITING_TIME_KEY 	= "QueryToolMaxWaitingTime";
	public static final String	SHOW_WELCOME_SCREEN_KEY		= "QueryToolShowWelcomeScreen";
	
	public static final String 	DEFAULT_QT_WAITING_TIME 	= "180"; // 180 seconds
	public static final	boolean	DEFAULT_SHOW_WELCOME		= true;
	
	/*
	 * Factory Pattern methods and other static vars
	 */
	private static Settings myInstance;

	public static Settings getInstance()	// FIRST created in QueryToolView.createPartControl()
	{
		if (myInstance == null)
			myInstance = new Settings();
		return myInstance;
	}

	private Settings()
	{ init(); }

	// initialize defauilt values
	private void init()
	{
		myNonTemporalQueryNumGroups = DEFAULT_NON_TEMPORAL_NUM_GROUPS;
		myTemporalQueryNumGroups	= DEFAULT_TEMPORAL_NUM_GROUPS;
		myItemCountHelpCutoff		= DEFAULT_ITEM_COUNT_HELP_CUTOFF;

		myIsShowingWelcomeScreen	= DEFAULT_SHOW_WELCOME;		
		myQTMaxWaitingTime			= Integer.parseInt( DEFAULT_QT_WAITING_TIME );

		System.setProperty( QT_MAX_WAITING_TIME_KEY, DEFAULT_QT_WAITING_TIME );

		mySettingsFileName = Files.getDefaultFileDirectory() + System.getProperty("file.separator") + SETTINGS_FILE_NAME;
		loadSettings(); 
	}


	/*
	 * Instance methods and vars
	 */
	private String	mySettingsFileName	= null;
	
	private int 	myNonTemporalQueryNumGroups;
	private int 	myTemporalQueryNumGroups;
	private int 	myItemCountHelpCutoff;
	private int		myQTMaxWaitingTime;
	private boolean myIsShowingWelcomeScreen;
	
	public int 		getNonTemporalQueryNumGroups() 	{ return this.myNonTemporalQueryNumGroups; }
	public int 		getTemporalQueryNumGroups() 	{ return this.myTemporalQueryNumGroups; }
	public int 		getItemCountHelpCutoff() 		{ return this.myItemCountHelpCutoff; }
	public int		getMaxWaitingTime()				{ return this.myQTMaxWaitingTime; }
	
	public boolean getIsShowingWelcomeScreen()				{ return this.myIsShowingWelcomeScreen; }
	public void	   setIsShowingWelcomeScreen(boolean flag)	{ myIsShowingWelcomeScreen = flag; }
	
	public void loadSettings()
	{
		try		
		{
			File settingsFile = new File( mySettingsFileName );
			if ( !settingsFile.exists() )
				System.err.println("Settings file '" + mySettingsFileName + "' not found. Using default values");
			else
			{
				BufferedReader reader = new BufferedReader( new FileReader(this.mySettingsFileName) );
				String line = null;
				int lineNo = 1;
				while ( (line = reader.readLine()) != null )
				{
					if ( line.startsWith(QT_MAX_WAITING_TIME_KEY))
						loadMaxWaitingTime( line );
					else if ( line.startsWith(SHOW_WELCOME_SCREEN_KEY))
						loadShowWelcomeScreen( line );
					else
						System.err.println( TEMPORAL_QUERYTOOL_SETTINGS + ": " + mySettingsFileName + " line " + lineNo + " is ignored. Unrecognized settings.");
					lineNo++;
				}
				reader.close();
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	private void loadMaxWaitingTime(String line) 
	{
		if ( (line.indexOf(TAB) < 0 ) || (line.indexOf(TAB)+1>line.length()-1) )
			return;
		try
		{
			String numString = line.substring( line.indexOf(TAB) + 1 );
			int numMilliseconds = Integer.parseInt( numString );
			if ( numMilliseconds < 0 )
				return;
			myQTMaxWaitingTime = numMilliseconds;
			System.setProperty( QT_MAX_WAITING_TIME_KEY, numString );
		}
		catch ( NumberFormatException e )
		{
			System.err.println( TEMPORAL_QUERYTOOL_SETTINGS+": " + SHOW_WELCOME_SCREEN_KEY + " is not parseable: '" + line.substring( line.indexOf(TAB) + 1 ) + "'. Applying default value: " + DEFAULT_QT_WAITING_TIME +".");
		}

	}
	
	private void loadShowWelcomeScreen(String line) 
	{
		if ( (line.indexOf(TAB) < 0 ) || (line.indexOf(TAB)+1>line.length()-1) )
			return;
		myIsShowingWelcomeScreen = Boolean.parseBoolean( line.substring( line.indexOf(TAB) + 1 ) ); // true is true, otherwise it's false
	}

	public void saveSettings()
	{
		try
		{
			BufferedWriter writer = new BufferedWriter( new FileWriter(this.mySettingsFileName) );
			writer.write( QT_MAX_WAITING_TIME_KEY + TAB + myQTMaxWaitingTime );
			writer.newLine();
			writer.write( SHOW_WELCOME_SCREEN_KEY + TAB + myIsShowingWelcomeScreen );
			writer.newLine();
			
			writer.flush();
			writer.close();			
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

}
