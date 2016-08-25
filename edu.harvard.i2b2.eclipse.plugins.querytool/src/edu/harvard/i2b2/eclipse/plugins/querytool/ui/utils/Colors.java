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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;

public class Colors 
{
	// generic colors
	public static final Color DUCKLING_FEATHER 	= new Color(Display.getDefault(), 229, 210, 151 );
	public static final Color LIGHT_SEPIA		= new Color(Display.getDefault(), 176, 143,  70 );
	public static final Color SEPIA				= new Color(Display.getDefault(), 116,  93,  50 );
	public static final Color ORANGE 			= new Color(Display.getDefault(), 255, 201,  14 );
	public static final Color GOLDENROD			= new Color(Display.getDefault(), 252, 217, 117 );
	
	public static final Color VERY_DARK_GRAY	= new Color(Display.getDefault(),  25,  25,  25);
	public static final Color DARK_DARK_GRAY	= new Color(Display.getDefault(),  55,  50,  45);
	public static final Color LIGHT_LIGHT_GRAY	= new Color(Display.getDefault(), 215, 215, 215);
	
	public static final Color TURQUOIS			= new Color(Display.getDefault(),  80, 216, 179 );
	public static final Color LIGHT_TURQUOIS	= new Color(Display.getDefault(), 153, 217, 236 );
	public static final Color INDIGO			= new Color(Display.getDefault(),  11, 21,   39 );
	public static final Color DARK_BLUE			= new Color(Display.getDefault(),  5,  10,   25 );
	
	public static final Color BLACK				= Display.getDefault().getSystemColor( SWT.COLOR_BLACK );
	public static final Color WHITE				= Display.getDefault().getSystemColor( SWT.COLOR_WHITE );
	public static final Color DARK_GRAY			= Display.getDefault().getSystemColor( SWT.COLOR_DARK_GRAY );
	public static final Color GRAY				= Display.getDefault().getSystemColor( SWT.COLOR_GRAY );
	public static final Color DARK_RED			= Display.getDefault().getSystemColor( SWT.COLOR_DARK_RED );
	
	public static final Color OFF_WHITE 		= new Color(Display.getDefault(), 222, 223, 224 );
	
	public static final Color PALE_GREEN		= new Color(Display.getDefault(), 190, 236, 176 );
	public static final Color PALE_ORANGE		= new Color(Display.getDefault(), 252, 215, 129 );
	public static final Color PALE_RED			= new Color(Display.getDefault(), 252, 189, 173 );
	
	public static final Color PALE_YELLOW		= new Color(Display.getDefault(), 250, 247, 201 );
	
	// query tool-specific accents
	public static final Color CONTROL_TITLE_BG	= new Color(Display.getDefault(), 165, 210, 255 );
	public static final Color CONTROL_TITLE_FG	= BLACK;
	
	public static final Color CONTROL_TITLE_BG_DISABLED = GRAY;
	
	public static final Color FAKE_LINK_COLOR	= new Color(Display.getDefault(), 0, 179, 255 );
	
	// Event Colors
	public static final Color [] EVENT_COLORS 	= { new Color(Display.getDefault(), 181, 230, 29 ),
													new Color(Display.getDefault(), 240, 193, 93 ),													
													new Color(Display.getDefault(), 227, 205, 164 ),
													new Color(Display.getDefault(), 199, 121, 102 ),
													new Color(Display.getDefault(), 112, 146, 190 ),
													new Color(Display.getDefault(), 200, 191, 131 ),
													new Color(Display.getDefault(), 55, 84, 54 ),
													new Color(Display.getDefault(), 239, 157, 84 ),
													new Color(Display.getDefault(), 112, 48, 48 ),
													new Color(Display.getDefault(), 126, 130, 122 ),
													new Color(Display.getDefault(), 135, 166, 68 ),
													new Color(Display.getDefault(), 18, 62, 129 ),
													new Color(Display.getDefault(), 0, 162, 132 ),
													new Color(Display.getDefault(), 122, 54, 122 ) };
	
	public static Color getEventColor( Event event )
	{
		if ( event == null )
			return Colors.GRAY;
		else if ( event.hasContent() )
			return Colors.EVENT_COLORS[ event.getEventID() % Colors.EVENT_COLORS.length ];
		return Colors.GRAY;
	}
	
}
