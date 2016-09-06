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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IDateStruct;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DateRangeChangeListeneer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;

public class DateConstraintDisplay extends QueryToolPanelComposite implements UIConst 
{
	
	protected Label 	myDateDisplay;
	protected Composite myBackgroundComp;
	protected boolean 			isDateDisplayTouched = false;
	
	protected ArrayList<DateRangeChangeListeneer> myDateChangeListeners;
	protected IDateStruct	myData		= null;

	public DateConstraintDisplay(Composite parent, IDateStruct data, int style) 
	{
		super(parent, style);
		myDateChangeListeners = new ArrayList<DateRangeChangeListeneer>();
		myData	= data;
		setupUI();
		attachListener();
	}

	protected void setupUI() 
	{
		this.setLayout( new FormLayout() );
		myBackgroundComp = new Composite(  this, SWT.NONE);
		myBackgroundComp.setLayout( new FormLayout() );
		myBackgroundComp.setLayoutData( FormDataMaker.makeBorderingFormData() );
		
		myDateDisplay = new Label( myBackgroundComp, SWT.WRAP | SWT.CENTER );
		//myDateDisplay.setLayoutData( FormDataMaker.makeFormData( 50, -myDateDisplay.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 0, 100, 0));
		updateDisplay();
	}

	protected void attachListener() 
	{
		myDateDisplay.addMouseListener( new MouseListener(){
			public void mouseDoubleClick(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0) {}
			
			public void mouseDown(MouseEvent arg0) 
			{
				if ( !isDateDisplayTouched ) 
					isDateDisplayTouched = true;

				DateConstraintDialog dcd = new DateConstraintDialog( myData.getStartDate(), myData.getEndDate(), SWT.BORDER );
				// compute for an init location that is not out-of-view
				Point preferred = dcd.getPreferredSize();
				Point startingPoint = myDateDisplay.toDisplay( myDateDisplay.getBounds().x + 2, myDateDisplay.getBounds().y );
				startingPoint.x = Math.max(5, startingPoint.x);
				startingPoint.y = Math.max(5, startingPoint.y);
				startingPoint.x = Math.min( Display.getCurrent().getBounds().width - preferred.x - 5, startingPoint.x);
				startingPoint.y = Math.min( Display.getCurrent().getBounds().height - preferred.y - 5, startingPoint.y);
				// set init location
				dcd.setLocation( startingPoint );
				
				// try disable workbench
				DateConstraintDisplay.this.setVisualActivationListeners();	// tell listener to NOT set this disabled
				HashSet<Control> alreadyDisabledControls = new HashSet<Control>();
				try
				{ UIUtils.recursiveSetEnabledAndRememberUnchangedControls( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), false, alreadyDisabledControls ); }
				catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to disable Workbench window when it does not exist (DateConstraintDisplay.attachListener): " + e.toString() ); }

				dcd.open();
				
				// try enable workbench
				try
				{ UIUtils.recursiveSetEnabled( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true, alreadyDisabledControls ); }
				catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to enable Workbench window when it does not exist (DateConstraintDisplay.attachListener): " + e.toString() ); }
				DateConstraintDisplay.this.resetVisualActivationListeners();
				
				if ( !dcd.isCanceled() )
				{
					myData.setStartDate( dcd.getStartDate() );
					myData.setEndDate( dcd.getEndDate() );
					updateDisplay();
					for ( DateRangeChangeListeneer listener : myDateChangeListeners )
						listener.dateRangeChanged( myData.getStartDate(), myData.getEndDate() );
				}
			}
		});
	}

	protected void updateDisplay()
	{
		if ( myData.getStartDate() == null && myData.getEndDate() == null )
		{
			if (!isDateDisplayTouched)
			{
				myDateDisplay.setText( NEW_LINE + NO_CONSTRAINTS + NEW_LINE + CLICK_TO_CHANGE);
				myDateDisplay.setToolTipText( CLICK_TO_CHANGE );
			}
			else
			{
				myDateDisplay.setText( NEW_LINE + NO_CONSTRAINTS + NEW_LINE );
				myDateDisplay.setToolTipText( CLICK_TO_CHANGE );
			}
		}
		else if (  myData.getStartDate() != null  &&  myData.getEndDate() == null)
		{
			String startString =  DateParser.toFormat( myData.getStartDate() );
			myDateDisplay.setText( startString + NEW_LINE + " " + STARTING_AND_AFTER );
			myDateDisplay.setToolTipText( startString + " " + INCLUSIVE + STARTING_AND_AFTER + "." );			
		}
		else if (  myData.getStartDate() == null &&  myData.getEndDate() != null)
		{
			String endString =  DateParser.toFormat( myData.getEndDate() );
			myDateDisplay.setText( UP_TO_AND_INCLUDING + endString );
			myDateDisplay.setToolTipText( endString + " " + INCLUSIVE + NEW_LINE + " and anytime before." );
		}
		else // ( myStartDate != null && myEndDate != null )
		{
			String startString =  DateParser.toFormat( myData.getStartDate() );
			String endString =  DateParser.toFormat( myData.getEndDate() );
			myDateDisplay.setText( startString + NEW_LINE + TO + NEW_LINE + endString );
			myDateDisplay.setToolTipText( startString + " " + INCLUSIVE + NEW_LINE + TO + NEW_LINE + endString +  " " + INCLUSIVE);
		}
		// relayout to be centered
		myDateDisplay.setLayoutData( FormDataMaker.makeFormData( 50, -myDateDisplay.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 0, 100, 0));
	}

	public void addListener( DateRangeChangeListeneer newListener )
	{ myDateChangeListeners.add( newListener ); }
	
	public void clearListeners( )
	{ myDateChangeListeners.clear(); }

	
	public void setDisplayTouched()
	{ isDateDisplayTouched = true; }

	public void setStartDate( GregorianCalendar start )
	{ myData.setStartDate( start ); }

	public void setEndDate( GregorianCalendar end )
	{ myData.setEndDate( end ); }

	public void updateLabels()
	{ updateDisplay(); }

	@Override
	protected void setActive( boolean flag )
	{
		if ( flag )
		{
			this.setBackground( Colors.ORANGE );
			myDateDisplay.setEnabled( true );
			myDateDisplay.setBackground( Colors.WHITE );
			this.myBackgroundComp.setBackground( Colors.WHITE );
		}
		else
		{
			this.setBackground( Colors.GRAY );
			myDateDisplay.setEnabled( false );
			myDateDisplay.setBackground( Colors.LIGHT_LIGHT_GRAY );
			this.myBackgroundComp.setBackground( Colors.LIGHT_LIGHT_GRAY );
		}
	}

	public boolean isDisplayTouched()
	{ return isDateDisplayTouched; }

	public GregorianCalendar getStartDate()
	{ return myData.getStartDate(); }

	public GregorianCalendar getEndDate()
	{ return myData.getEndDate(); }
		
}
