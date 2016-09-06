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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;

public class VerticalLabel extends Composite implements PaintListener
{
	
	private String 	myText;
	private Canvas 	myCanvas;
	
	private Point	myPreferredSize = null;
	
	public VerticalLabel(Composite parent, int style, String text) 
	{
		super(parent, SWT.NO_BACKGROUND);
		//this.setBackground( Colors.DARK_RED );
		setText(text);
		setupUI();
		addListeners();
	}
	
	private void setupUI()
	{
		this.setLayout( new FormLayout() );
		myCanvas = new Canvas( this, SWT.NO_REDRAW_RESIZE  );
		myCanvas.setBackground( this.getBackground() );
		myCanvas.setLayoutData( FormDataMaker.makeFullFormData() );		
	}
	
	private void addListeners() 
	{
		myCanvas.addPaintListener( this );
	}

	public void setText(String text)
	{ 
		myText = text;
		GC gc = new GC(this);
		Point size = gc.textExtent(myText);
		myPreferredSize = new Point( size.y, size.x);
		gc.dispose ();
	}

	@Override
	public void paintControl(PaintEvent arg0) 
	{
		GC gc = arg0.gc;
		Point stringExt = gc.stringExtent(myText);
		
		Transform k = new Transform( this.getDisplay() );
		k.translate(0, stringExt.x+1);
		k.rotate(-90);
		gc.setTransform( k );
		
		//gc.drawRectangle(-2, 1, stringExt.x+2, stringExt.y);
		gc.drawText( myText, 0, 0, true );
		this.setSize( stringExt.y+2, stringExt.x+2 );

		k.dispose();
	}
	
	public void setBackground( Color c )
	{
		super.setBackground( c );
		myCanvas.setBackground( this.getBackground() );
	}
	
	public Point getPreferredSize()
	{ return this.myPreferredSize; }
}
