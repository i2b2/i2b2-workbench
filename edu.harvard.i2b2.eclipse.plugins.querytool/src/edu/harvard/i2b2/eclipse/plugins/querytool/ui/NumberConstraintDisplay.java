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

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Fonts;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;

public class NumberConstraintDisplay extends QueryToolPanelComposite implements UIConst
{
	public static 	String [] OPERATORS = {"=", ">", "<"};
	
	private Canvas 	myNumberDisplay;
	private Group	myData		= null;
	
		private boolean isActive = true;
		
	public NumberConstraintDisplay(Composite parent, Group data, int style) 
	{
		super(parent, style);
		myData	= data;
		setup();
		attachListeners();
	}

	private void setup() 
	{
		this.setLayout( new FormLayout() );
		this.setBackground( Colors.GOLDENROD );
		myNumberDisplay = new Canvas( this, SWT.DOUBLE_BUFFERED );
		myNumberDisplay.setLayoutData( FormDataMaker.makeBorderingFormData() );
		myNumberDisplay.setBackground( Colors.WHITE );
		
		this.updateDisplay();
	}

	private void attachListeners() 
	{
		myNumberDisplay.addPaintListener( new PaintListener()
		{
			public void paintControl(PaintEvent arg0) 
			{
				GC gc = arg0.gc;
				String operatorText  = OPERATORS[ myData.getOperator() ];
				String numberText	 = Integer.toString( myData.getNumber() );
				Point operatorSize	= gc.stringExtent( operatorText );
				Point numberSize	= gc.stringExtent( numberText );
				Point totalSize		= gc.stringExtent( operatorText + " " + numberText );
				if ( isActive )
				{
					/*
					if ( ( myData.getOperator() == 1) && ( myData.getNumber() == 0 ) )
						gc.setForeground( Colors.GRAY );
					else
					*/
					gc.setForeground( Colors.BLACK );
				}
				else
				{
					gc.setForeground( Colors.DARK_GRAY );
				}
					

				if ( operatorSize.x + numberSize.x < myNumberDisplay.getClientArea().width )
				{
					gc.drawText( operatorText+numberText, (myNumberDisplay.getClientArea().width-totalSize.x)/2, (myNumberDisplay.getClientArea().height-totalSize.y)/2, true );
				}
				else
				{
					gc.drawText( operatorText, (myNumberDisplay.getClientArea().width-operatorSize.x)/2, myNumberDisplay.getClientArea().height/2-operatorSize.y+2, true );
					String numberString = Integer.toString( myData.getNumber() );
					gc.drawText( numberString, myNumberDisplay.getClientArea().width - numberSize.x, myNumberDisplay.getClientArea().height - numberSize.y - 2, true);
				}
			}
		});
		
		myNumberDisplay.addMouseListener( new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0){}
			public void mouseDown(MouseEvent arg0) 
			{
				NumberConstraintDialog ncd = new NumberConstraintDialog( myData.getOperator(), myData.getNumber(), SWT.BORDER );
				// compute for an init location that is not out-of-view
				Point preferred = ncd.getPreferredSize();
				Point startingPoint = myNumberDisplay.toDisplay( myNumberDisplay.getBounds().x + 2, myNumberDisplay.getBounds().y );
				startingPoint.x = Math.max(5, startingPoint.x);
				startingPoint.y = Math.max(5, startingPoint.y);
				startingPoint.x = Math.min( Display.getCurrent().getBounds().width - preferred.x - 5, startingPoint.x);
				startingPoint.y = Math.min( Display.getCurrent().getBounds().height - preferred.y - 5, startingPoint.y);
				// set init location
				ncd.setLocation( startingPoint );
				
				// try disable workbench
				NumberConstraintDisplay.this.setVisualActivationListeners();	// tell listener to NOT set this disabled
				HashSet<Control> alreadyDisabledControls = new HashSet<Control>();
				try
				{ UIUtils.recursiveSetEnabledAndRememberUnchangedControls( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), false, alreadyDisabledControls ); }
				catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to disable Workbench window when it does not exist (NumberConstraintDisplay.attachListener): " + e.toString() ); }				
				ncd.open();
				
				// try enable workbench
				try
				{ UIUtils.recursiveSetEnabled( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true, alreadyDisabledControls ); }
				catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to enable Workbench window when it does not exist (NumberConstraintDisplay.attachListener): " + e.toString() ); }
				NumberConstraintDisplay.this.resetVisualActivationListeners();	// tell listener to NOT set this disabled
				
				if ( !ncd.isCanceled() )
				{
					myData.setOperator( ncd.getOperator() );
					myData.setNumber(  ncd.getNumber() );
					updateDisplay();
				}				
			}
		});
	}

	public void updateDisplay()
	{
		if ( isActive )
		{
			if ( ( myData.getOperator() == 1) && ( myData.getNumber() == 0 ) )
				myNumberDisplay.setBackground( Colors.WHITE );
			else if (( myData.getOperator() == 2 ) || ( myData.getOperator() == 0))
				myNumberDisplay.setBackground( Colors.PALE_RED );
			else
				myNumberDisplay.setBackground( Colors.PALE_GREEN );
			this.setBackground( Colors.ORANGE );			
		}
		else
		{
			myNumberDisplay.setBackground( Colors.LIGHT_LIGHT_GRAY );
			this.setBackground( Colors.LIGHT_LIGHT_GRAY );
		}
		myNumberDisplay.setEnabled( isActive );
		myNumberDisplay.setToolTipText("Terms must occur " + NumberConstraintDisplay.OPERATORS[myData.getOperator()] + " " + myData.getNumber() + " times.\n"+UIConst.CLICK_TO_CHANGE);
		myNumberDisplay.redraw();
	}
	
	public void setOperator( int op )
	{
		assert( op >= 0 && op < 3);
		myData.setOperator( op );
	}
	
	public void setNumber( int number )
	{
		assert( number > 0 );
		myData.setNumber( number );
	}
	
	// accessors
	public int getOperator() { return myData.getOperator(); }
	public int getNumber() { return myData.getNumber(); }	
	public String getOperatorString() { return OPERATORS[myData.getOperator()]; }

	
	
	@Override
	protected void setActive(boolean flag) 
	{ 		
		isActive = flag;
		updateDisplay();
	}

		
	public static void main( String [] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		NumberConstraintDisplay ncd = new NumberConstraintDisplay( myShell, new Group( "Test Group" ), SWT.None );
		ncd.setLayoutData( FormDataMaker.makeFullFormData() );
		
		myShell.setSize( 120, 100 );
		
		myShell.open();
		while (!myShell.isDisposed()) 
		{
			if (!Display.getCurrent().readAndDispatch())
				Display.getCurrent().sleep();
		}
		if (!myShell.isDisposed())
		{
			myShell.close();
			myShell.dispose();
		}

	}

}
