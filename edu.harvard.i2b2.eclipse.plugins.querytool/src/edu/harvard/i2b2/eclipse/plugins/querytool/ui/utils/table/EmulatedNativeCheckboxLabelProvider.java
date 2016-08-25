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


package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.table;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;

/*
 * 
 * Adapted from http://tom-eclipse-dev.blogspot.com/2007/01/tableviewers-and-nativelooking.html
 * 		Takes screenshot (makes images) of native checkboxes in different states: {checked, unchecked} x {enabled, disabled}
 *  	and use these images to draw checkboxes in TableViewers/TreeViewers
 */

public abstract class EmulatedNativeCheckboxLabelProvider extends ColumnLabelProvider 
{
	
	protected static final String CHECKED_KEY = "CHECKED";
	protected static final String UNCHECK_KEY = "UNCHECKED";
	
	protected static final String DISABLED_CHECKED_KEY = "DCHECKED";
	protected static final String DISABLED_UNCHECK_KEY = "DUNCHECKED";
	
	
	public EmulatedNativeCheckboxLabelProvider(ColumnViewer viewer) 
	{
		if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null) 
		{
			JFaceResources.getImageRegistry().put(UNCHECK_KEY, makeShot(viewer.getControl(), false, false));
			JFaceResources.getImageRegistry().put(CHECKED_KEY, makeShot(viewer.getControl(), false, true));
			JFaceResources.getImageRegistry().put(DISABLED_UNCHECK_KEY, makeShot(viewer.getControl(), true, false));
			JFaceResources.getImageRegistry().put(DISABLED_CHECKED_KEY, makeShot(viewer.getControl(), true, true));
		}
	}

	private Image makeShot(Control control, boolean disabled, boolean type) 
	{		
		Shell shell = new Shell(control.getShell(), SWT.NO_TRIM | SWT.NO_BACKGROUND );
		shell.setBackground( Colors.OFF_WHITE ); // use off-white as the transparent background color
		
		Button button = new Button(shell, SWT.CHECK | SWT.NO_BACKGROUND  );
		button.setBackground( Colors.OFF_WHITE );
		button.setSelection( type );
		button.setEnabled( !disabled );
		
		button.setLocation(0, 0);
		Point bsize = button.computeSize( SWT.DEFAULT, SWT.DEFAULT );

		bsize.x = Math.max( bsize.x, bsize.y ) ;
		bsize.y = Math.max( bsize.x, bsize.y ) ;
		button.setSize(bsize);
		shell.setSize(bsize);

		shell.open();
		GC gc = new GC(shell);
		Image image = new Image(control.getDisplay(), bsize.x -3, bsize.y -2);
		gc.copyArea(image, 0, 0);
		gc.dispose();
		shell.close();

		ImageData imageData = image.getImageData();
		imageData.transparentPixel = imageData.palette.getPixel( Colors.OFF_WHITE.getRGB() );
		return new Image( control.getDisplay(), imageData );
	}

	public Image getImage( Object element ) 
	{
		if ( isEnabled(element) )
		{
			if (isChecked(element)) 
				return JFaceResources.getImageRegistry().get(CHECKED_KEY);
			else 
				return JFaceResources.getImageRegistry().get(UNCHECK_KEY);
		}
		else
		{
			if (isChecked(element))
				return JFaceResources.getImageRegistry().get(DISABLED_CHECKED_KEY);
			else 
				return JFaceResources.getImageRegistry().get(DISABLED_UNCHECK_KEY);
		}
	}
	
	
	protected abstract boolean isEnabled( Object element );
	protected abstract boolean isChecked( Object element );
	
}