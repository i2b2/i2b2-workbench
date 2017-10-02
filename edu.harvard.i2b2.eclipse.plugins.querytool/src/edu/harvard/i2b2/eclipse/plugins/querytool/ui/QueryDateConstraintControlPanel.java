/*
 * Copyright (c) 2006-2017 Partners Healthcare 
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

import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.DefaultDateStruct;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.QueryDateConstraintProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DateRangeChangeListeneer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;


public class QueryDateConstraintControlPanel extends QueryToolPanelComposite implements UIConst, DateRangeChangeListeneer
{
	private Button	myUseGroupSpecificDatesButton;
	private Button	myUseSharedDatesButton;
	
	private DateConstraintDisplay myDisplay;
	private QueryDateConstraintProvider myDelegator;
	
		private Composite 	titleComp;
		private Label 		titleLabel;
		
	public QueryDateConstraintControlPanel(Composite parent, QueryDateConstraintProvider delegator, int style, boolean isBasicMode ) 
	{
		super(parent, style);
		setupUI( isBasicMode );
		myDelegator = delegator;
		attachListeners();
	}

	private void setupUI( boolean isBasicMode ) 
	{
		this.setLayout( new FormLayout() );

		titleComp = new Composite( this, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );

		titleLabel = new Label( titleComp, SWT.None );
		titleLabel.setText( DATE_CONSTRAINT );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 50, -titleSize.x/2, (Integer)null, 0));

		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );
		
		Composite buttonComposite = new Composite( this, SWT.None );
		buttonComposite.setLayout( new FormLayout() );
		buttonComposite.setLayoutData( FormDataMaker.makeFormData( titleComp, (Integer)null, 0, 100 ));

		myUseGroupSpecificDatesButton 	= new Button( buttonComposite, SWT.RADIO );
		myUseGroupSpecificDatesButton.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 4, 100));
		myUseGroupSpecificDatesButton.setText( UIConst.GROUP_SPECIFIC );

		myUseSharedDatesButton		= new Button( buttonComposite, SWT.RADIO );
		myUseSharedDatesButton.setLayoutData( FormDataMaker.makeFormData(myUseGroupSpecificDatesButton, (Integer)null, 4, 100));
		String dateScopeStr = UIConst.EVENT_WIDE;
		if ( isBasicMode )
			dateScopeStr = UIConst.QUERY_WIDE;
		myUseSharedDatesButton.setText( dateScopeStr );

		myDisplay = new DateConstraintDisplay( this, new DefaultDateStruct(), SWT.None );
		myDisplay.setLayoutData( FormDataMaker.makeFormData( buttonComposite, 0, 100, -2, 0, 12, 100, -2) );
		myDisplay.addListener( this );
		
		// set defaults
		myUseGroupSpecificDatesButton.setSelection( true );
		handleButtonSelected();
	}

	@Override
	protected void setActive( boolean flag )
	{
		if ( flag )
		{
			titleComp.setBackground( Colors.CONTROL_TITLE_BG );
			titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		}
		else
		{
			titleComp.setBackground( Colors.CONTROL_TITLE_BG_DISABLED );
			titleLabel.setBackground( Colors.CONTROL_TITLE_BG_DISABLED );
		}
	}
	
	
	private void attachListeners() 
	{
		myUseGroupSpecificDatesButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{  handleButtonSelected(); }
		});

	}

	private void handleButtonSelected()
	{
		//System.err.println("QueryDateConstraintControlPanel.handleButtonSelected: myUseGroupSpecificDatesButton selected." );
		Display.getCurrent().asyncExec( new Runnable() 
		{
			@Override
			public void run() 
			{
				myDisplay.setActive( !myUseGroupSpecificDatesButton.getSelection() );
				myDelegator.constraintChanged( myUseGroupSpecificDatesButton.getSelection() );
			}
		});		
	}
	
	public boolean isUsingGroupSpecificDates()
	{ return myUseGroupSpecificDatesButton.getSelection(); }

	public void setIsUsingGroupSpecificDates( boolean isSelected )
	{ 
		if ( isSelected )
		{
			myUseGroupSpecificDatesButton.setSelection( true );
			myUseSharedDatesButton.setSelection( false );
		}
		else
		{	
			myUseSharedDatesButton.setSelection( true );
			myUseGroupSpecificDatesButton.setSelection( false );
		}
		
		Display.getCurrent().asyncExec( new Runnable() 
		{
			@Override
			public void run() 
			{
				myDisplay.updateLabels();
				myDisplay.setActive( !myUseGroupSpecificDatesButton.getSelection() ); 
			}
		});
	}

	
	public GregorianCalendar getStartDate()
	{ return this.myDisplay.getStartDate(); }
	
	public GregorianCalendar getEndDate()
	{ return this.myDisplay.getEndDate(); }

	public void setStartDate( GregorianCalendar start)
	{ this.myDisplay.setStartDate( start ); }
	
	public void setEndDate( GregorianCalendar end )
	{ this.myDisplay.setEndDate( end ); }

	
	// reset dates, no UI update
	public void resetDates()
	{
		this.myDisplay.setStartDate( null );
		this.myDisplay.setEndDate( null );
	}
	
	// reset all data and automatically adjust UI
	public void initializeWithEvent( Event event )
	{
		this.myDisplay.setStartDate( event.getStartDate() );
		this.myDisplay.setEndDate( event.getEndDate() );
		if (  event.isUsingGroupSpecificDates() )
		{
			myUseGroupSpecificDatesButton.setSelection( true );
			myUseSharedDatesButton.setSelection( false );
		}
		else
		{	
			myUseGroupSpecificDatesButton.setSelection( false );
			myUseSharedDatesButton.setSelection( true );
		}
		myDisplay.updateLabels();
		myDisplay.setActive( !myUseGroupSpecificDatesButton.getSelection() );
	}

	public void updateLabels()
	{ myDisplay.updateLabels(); }
	
	/*
	 * DateRangeChangeListeneer method
	 */
	@Override 
	public void dateRangeChanged(GregorianCalendar startDate, GregorianCalendar endDate) 
	{
		myDelegator.constraintChanged( myUseGroupSpecificDatesButton.getSelection() );
	}
	
}
