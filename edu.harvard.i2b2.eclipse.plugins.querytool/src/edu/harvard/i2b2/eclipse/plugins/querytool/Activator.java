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

package edu.harvard.i2b2.eclipse.plugins.querytool;

import java.util.HashMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.harvard.i2b2.eclipse.plugins.querytool"; //$NON-NLS-1$
	
	// The shared instance
	private static Activator plugin;
	
	// animated gifs to be loaded frame-by-frame and their corresponding keys
	private String [] GIF_FILE_PATHS 	= new String [] { Images.WORKING_PATH };
	private String [] GIF_IMAGE_KEYS	= new String [] { Images.WORKING };
	private HashMap<String, ImageLoader> myImageLoaders;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() 
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) 
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
		
	/*
	 * Overwriting the default initializeImageRegistry method to load all necessary images.
	 *  This Activator will automatically deal with disposing of the images
	 */
	@Override
    protected void initializeImageRegistry(ImageRegistry registry) 
    {
        super.initializeImageRegistry(registry);
        Bundle bundle = Platform.getBundle( PLUGIN_ID );

        registry.put(Images.LEAF, 			ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.LEAF_PATH), null) ) );
        registry.put(Images.OPEN_FOLDER, 	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.OPEN_FOLDER_PATH), null) ) );
        registry.put(Images.OPEN_CASE, 		ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.OPEN_CASE_PATH), null) ) );
        registry.put(Images.CLOSED_FOLDER, 	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.CLOSED_FOLDER_PATH), null) ) );
        registry.put(Images.CLOSED_CASE, 	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.CLOSED_CASE_PATH), null) ) );
        registry.put(Images.PREVIOUS_QUERY, ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.PREVIOUS_QUERY_PATH), null) ) );
        registry.put(Images.PREVIOUS_TEMPORAL_QUERY, ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.PREVIOUS_TEMPORAL_QUERY_PATH), null) ) );
        registry.put(Images.PLAIN_PEOPLE, 	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.PLAIN_PEOPLE_PATH), null) ) );
        registry.put(Images.PATIENT_SET, 	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.PATIENT_SET_PATH), null) ) );
        registry.put(Images.PATIENT_COUNT, 	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.PATIENT_COUNT_PATH), null) ) );
        registry.put(Images.ENCOUNTER_SET, 	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.ENCOUNTER_SET_PATH), null) ) );
        registry.put(Images.WORKING, 		ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.WORKING_PATH), null) ) );

        registry.put(Images.HELP_INACTIVE,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.HELP_INACTIVE_PATH), null) ) );
        registry.put(Images.HELP_ACTIVE,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.HELP_ACTIVE_PATH), null) ) );

        registry.put(Images.BOUND_BY_PATIENT,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.BOUND_BY_PATIENT_PATH), null) ) );
        registry.put(Images.BOUND_BY_ENCOUNTER,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.BOUND_BY_ENCOUNTER_PATH), null) ) );
        registry.put(Images.BOUND_BY_OBSERVATION,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.BOUND_BY_OBSERVATION_PATH), null) ) );
        registry.put(Images.TEMPORAL_ANCHOR, ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.TEMPORAL_ANCHOR_PATH), null ) ) );

        registry.put(Images.EXPANDER_CLOSED,ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.EXPANDER_CLOSED_PATH), null) ) );
        registry.put(Images.EXPANDER_CLOSED_ACTIVE,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.EXPANDER_CLOSED_ACTIVE_PATH), null) ) );
        registry.put(Images.EXPANDER_OPEN,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.EXPANDER_OPEN_PATH), null) ) );        
        registry.put(Images.EXPANDER_OPEN_ACTIVE,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.EXPANDER_OPEN_ACTIVE_PATH), null) ) );

        registry.put(Images.EXPANDER_DOWN,ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.EXPANDER_DOWN_PATH), null) ) );
        registry.put(Images.EXPANDER_DOWN_ACTIVE,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.EXPANDER_DOWN_ACTIVE_PATH), null) ) );
        registry.put(Images.EXPANDER_UP,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.EXPANDER_UP_PATH), null) ) );
        registry.put(Images.EXPANDER_UP_ACTIVE,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.EXPANDER_UP_ACTIVE_PATH), null) ) );

        registry.put(Images.RIGHT_ARROW,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.RIGHT_ARROW_PATH), null) ) );
        registry.put(Images.RIGHT_ARROW_ACTIVATED,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.RIGHT_ARROW_ACTIVATED_PATH), null) ) );
        registry.put(Images.RIGHT_ARROW_SMALL_WHITE,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.RIGHT_ARROW_SMALL_WHITE_PATH), null) ) );
        registry.put(Images.RIGHT_ARROW_SMALL_GRAY,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.RIGHT_ARROW_SMALL_GRAY_PATH), null) ) );
        registry.put(Images.RIGHT_ARROW_TRIMMED_BLACK,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.RIGHT_ARROW_TRIMMED_BLACK_PATH), null) ));
        registry.put(Images.LEFT_ARROW_TRIMMED_BLACK,	ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.LEFT_ARROW_TRIMMED_BLACK_PATH), null) ));
        registry.put(Images.RETURN, ImageDescriptor.createFromURL( FileLocator.find(bundle, new Path(Images.RETURN_PATH), null) ) );

        // load known animated gifs frame-by-frame
        initializeAnimatedGifs( registry );
    }

	// craete an image for each frame of gif images for animation purposes.
	private void initializeAnimatedGifs( ImageRegistry registry )
	{		
		myImageLoaders = new HashMap<String, ImageLoader>();
		Bundle bundle = Platform.getBundle( PLUGIN_ID );
		for ( int i = 0; i < GIF_FILE_PATHS.length; i++ )
		{						
			try
			{
				ImageLoader imageLoader = new ImageLoader();
				imageLoader.load( FileLocator.openStream(bundle, new Path(GIF_FILE_PATHS[i]), false));
				for ( int j = 0; j < imageLoader.data.length; j++)
				{
					Image frameImage = new Image( Display.getDefault(), imageLoader.data[j]);
					registry.put( GIF_IMAGE_KEYS[i]+j, frameImage );
				}
				myImageLoaders.put( GIF_IMAGE_KEYS[i], imageLoader );	
			}
			catch ( Exception e )
			{ e.printStackTrace(); }			
		}
	}

	public ImageLoader getImageLoader( String key )
	{ return myImageLoaders.get( key ); }
	
}
