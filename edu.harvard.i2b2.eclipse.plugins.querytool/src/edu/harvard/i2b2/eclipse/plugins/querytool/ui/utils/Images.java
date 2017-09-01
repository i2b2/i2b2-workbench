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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.Activator;

/*
 * A convenience class for keeping track of constants used by images in the plugin.
 * All the heavy lifting is done in edu.harvard.i2b2.eclipse.plugins.querytool.Activator
 */
public class Images 
{	
	/*================================================================
	 * Keys for normal image files to be loaded as Images
	* ===============================================================*/
	public static final String LEAF 					= "leaf";
	public static final String OPEN_FOLDER 				= "openFolder";
	public static final String OPEN_CASE 				= "openCase";
	public static final String CLOSED_FOLDER			= "closedFolder";
	public static final String CLOSED_CASE 				= "closedCase";
	public static final String PREVIOUS_QUERY			= "prevQuery";
	public static final String PREVIOUS_TEMPORAL_QUERY	= "openFolderClock";
	public static final String PLAIN_PEOPLE 			= "plainpeople";
	public static final String PATIENT_SET				= "patient_coll";
	public static final String PATIENT_COUNT			= "patient_count_xml";
	public static final String ENCOUNTER_SET			= "morepeople";
	public static final String RETURN					= "return2";
	
	public static final String HELP_INACTIVE 	= "help_inactive";
	public static final String HELP_ACTIVE 		= "help_active";
	
	public static final String BOUND_BY_PATIENT		= "patient";
	public static final String BOUND_BY_ENCOUNTER	= "encounter";
	public static final String BOUND_BY_OBSERVATION	= "observation";
	public static final String TEMPORAL_ANCHOR		= "anchor";
	
	public static final String EXPANDER_CLOSED 			= "closed";
	public static final String EXPANDER_CLOSED_ACTIVE 	= "closed_active";
	public static final String EXPANDER_OPEN			= "open";	
	public static final String EXPANDER_OPEN_ACTIVE		= "open_active";

	public static final String EXPANDER_DOWN			= "expander_down";
	public static final String EXPANDER_DOWN_ACTIVE 	= "expander_down_active";
	public static final String EXPANDER_UP				= "expander_up";
	public static final String EXPANDER_UP_ACTIVE		= "expander_up_active";
	
	public static final String RIGHT_ARROW				= "right_arrow";
	public static final String RIGHT_ARROW_ACTIVATED	= "right_arrow_activated";
	public static final String RIGHT_ARROW_SMALL_WHITE	= "right_arrow_small_white";
	public static final String RIGHT_ARROW_SMALL_GRAY	= "right_arrow_small_gray";		
	public static final String RIGHT_ARROW_TRIMMED_BLACK= "right_arrow_trimmed_black";
	public static final String LEFT_ARROW_TRIMMED_BLACK	= "left_arrow_trimmed_black";

	public static final String LINUX_CB_AU				= "cb_active_unchecked";
	public static final String LINUX_CB_AC				= "cb_active_checked";
	public static final String LINUX_CB_IU				= "cb_inactive_unchecked";
	public static final String LINUX_CB_IC				= "cb_inactive_checked";
	
	/* ================================================================
	 * Keys for animated gif files to be loaded as ImageLoaders
	 * ===============================================================*/
	public static final String WORKING					= "working";  // also used as a key for normal Images

	/* ================================================================
	 * Paths
	 * ===============================================================*/
	private static final String ICON_PATH			= "icons/";
	private static final String CONCEPT_ICON_PATH	= ICON_PATH + "concepts/";
	private static final String HELP_PATH			= ICON_PATH + "help/";
	private static final String GROUP_BINDING_PATH	= ICON_PATH + "group_bindings/";
	private static final String MISC_PATH			= ICON_PATH + "misc/";

	public static final String LEAF_PATH 					= CONCEPT_ICON_PATH + LEAF + ".jpg";
	public static final String OPEN_FOLDER_PATH 			= CONCEPT_ICON_PATH + OPEN_FOLDER + ".jpg";
	public static final String OPEN_CASE_PATH 				= CONCEPT_ICON_PATH + OPEN_CASE + ".jpg";
	public static final String CLOSED_FOLDER_PATH			= CONCEPT_ICON_PATH + CLOSED_FOLDER + ".jpg";
	public static final String CLOSED_CASE_PATH 			= CONCEPT_ICON_PATH + CLOSED_CASE + ".jpg";
	public static final String PREVIOUS_QUERY_PATH			= CONCEPT_ICON_PATH + PREVIOUS_QUERY + ".gif";
	public static final String PREVIOUS_TEMPORAL_QUERY_PATH	= CONCEPT_ICON_PATH + PREVIOUS_TEMPORAL_QUERY + ".gif";
	public static final String PLAIN_PEOPLE_PATH			= CONCEPT_ICON_PATH + PLAIN_PEOPLE + ".jpg";
	public static final String PATIENT_SET_PATH				= CONCEPT_ICON_PATH + PATIENT_SET + ".jpg";
	public static final String PATIENT_COUNT_PATH			= CONCEPT_ICON_PATH + PATIENT_COUNT + ".jpg";
	public static final String ENCOUNTER_SET_PATH			= CONCEPT_ICON_PATH + ENCOUNTER_SET + ".jpg";
	 
	public static final String WORKING_PATH					= CONCEPT_ICON_PATH + WORKING + ".gif";
		
	public static final String HELP_INACTIVE_PATH	= HELP_PATH + HELP_INACTIVE + ".gif";
	public static final String HELP_ACTIVE_PATH		= HELP_PATH + HELP_ACTIVE + ".gif";	

	public static final String BOUND_BY_PATIENT_PATH 	= GROUP_BINDING_PATH + BOUND_BY_PATIENT + ".gif";
	public static final String BOUND_BY_ENCOUNTER_PATH 	= GROUP_BINDING_PATH + BOUND_BY_ENCOUNTER + ".gif";
	public static final String BOUND_BY_OBSERVATION_PATH= GROUP_BINDING_PATH + BOUND_BY_OBSERVATION + ".gif";
	public static final String TEMPORAL_ANCHOR_PATH		= GROUP_BINDING_PATH + TEMPORAL_ANCHOR + ".gif"; 

	public static final String EXPANDER_CLOSED_PATH 		= MISC_PATH + EXPANDER_CLOSED + ".gif";
	public static final String EXPANDER_OPEN_PATH			= MISC_PATH + EXPANDER_OPEN + ".gif";
	public static final String EXPANDER_CLOSED_ACTIVE_PATH 	= MISC_PATH + EXPANDER_CLOSED_ACTIVE + ".gif";
	public static final String EXPANDER_OPEN_ACTIVE_PATH	= MISC_PATH + EXPANDER_OPEN_ACTIVE + ".gif";
	
	public static final String EXPANDER_DOWN_PATH			= MISC_PATH + EXPANDER_DOWN + ".gif";
	public static final String EXPANDER_DOWN_ACTIVE_PATH 	= MISC_PATH + EXPANDER_DOWN_ACTIVE + ".gif";
	public static final String EXPANDER_UP_PATH				= MISC_PATH + EXPANDER_UP + ".gif";
	public static final String EXPANDER_UP_ACTIVE_PATH		= MISC_PATH + EXPANDER_UP_ACTIVE + ".gif";

	public static final String RIGHT_ARROW_PATH				= MISC_PATH + RIGHT_ARROW + ".png";
	public static final String RIGHT_ARROW_ACTIVATED_PATH   = MISC_PATH + RIGHT_ARROW_ACTIVATED + ".png";
	public static final String RIGHT_ARROW_SMALL_WHITE_PATH	= MISC_PATH + RIGHT_ARROW_SMALL_WHITE + ".png";
	public static final String RIGHT_ARROW_SMALL_GRAY_PATH	= MISC_PATH + RIGHT_ARROW_SMALL_GRAY + ".png";

	public static final String RIGHT_ARROW_TRIMMED_BLACK_PATH	= MISC_PATH + RIGHT_ARROW_TRIMMED_BLACK + ".gif";
	public static final String LEFT_ARROW_TRIMMED_BLACK_PATH	= MISC_PATH + LEFT_ARROW_TRIMMED_BLACK + ".gif";
	public static final String RETURN_PATH						= MISC_PATH + RETURN + ".gif";

	public static final String LINUX_CB_AU_PATH 	= MISC_PATH + LINUX_CB_AU + ".png";
	public static final String LINUX_CB_AC_PATH 	= MISC_PATH + LINUX_CB_AC + ".png";
	public static final String LINUX_CB_IU_PATH 	= MISC_PATH + LINUX_CB_IU + ".png";
	public static final String LINUX_CB_IC_PATH 	= MISC_PATH + LINUX_CB_IC + ".png";
	
	/*
	 * Use the plugin activator's ImageRegistry, which is initialized in the Activator's initializeImageRegistry method
	 * 
	 * Sde Activator.initializeImageRegistry(...)
	 */
	public static Image getImageByKey( String key )
	{
		return Activator.getDefault().getImageRegistry().get( key );
	}

	// to access each frame of an animated gif
	public static Image getImageByKeyAndFrame( String key, int frameNumber )
	{ return Activator.getDefault().getImageRegistry().get( key + frameNumber ); }

	
	/*
	 * img_id usually starts with ISharedImages.IMG...
	 */
	public static Image getEclipseImagesByKey( String img_id )
	{
		return PlatformUI.getWorkbench().getSharedImages().getImage( img_id );
	}

	public static ImageLoader getImageLoader( String key )
	{
		return Activator.getDefault().getImageLoader( key );
	}
	
}

