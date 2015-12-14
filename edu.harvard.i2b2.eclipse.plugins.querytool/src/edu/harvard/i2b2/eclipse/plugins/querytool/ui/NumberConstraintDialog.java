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

package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.IVisualActivationManageable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.VisualActivationListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.ControlBorderDrawer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.DefaultSpinnerValidator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class NumberConstraintDialog implements UIConst
{
	public static 	String [] COMBO_OPERATORS = { NumberConstraintDisplay.OPERATORS[1] + " (Greater than)", NumberConstraintDisplay.OPERATORS[2]+" (Less than)", " = 0 (No Occurrences)" };
	
	private Shell		myShell;
	private Composite	myMainComp;
	private Composite	titleComp;
	private Label		titleLabel;
	private Composite 	innerComp;
	
	private Combo		myOperatorCombo;
	private Spinner		myNumberSpinner;
	
	private Button		myOKButton;
	private Button		myCancelButton;
	
	private Integer 	myOperator 	= null;
	private Integer 	myNumber	= null;
	
	private boolean 	isCanceled = false;
	private Point		myInitLocation; 

	
	public NumberConstraintDialog( Integer operator, Integer number, int style) 
	{
		myOperator 	= operator;
		myNumber	= number;
		setupUI( style );
		attachListeners();
	}

	public NumberConstraintDialog( Integer operator, Integer number ) 
	{
		myOperator 	= operator;
		myNumber	= number;
		setupUI( SWT.None );
		attachListeners();
	}
	
	private void setupUI( int shellStyles ) 
	{
		myShell = new Shell( Display.getCurrent(), SWT.APPLICATION_MODAL | shellStyles);
		myShell.setLayout( new FormLayout() );
		
		myMainComp = new Composite( myShell, SWT.None );
		myMainComp.setLayoutData( FormDataMaker.makeFullFormData() );
		myMainComp.setLayout( new FormLayout() );

		titleComp = new Composite( myMainComp, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );

		titleLabel = new Label( titleComp, SWT.None );
		titleLabel.setText( SPECIFY_OCCURRENCE_CONSTRAINTS );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 50, -titleSize.x/2, (Integer)null, 0));

		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );

		Composite outerComp = new Composite( myMainComp, SWT.NONE );
		outerComp.setLayout( new FormLayout() );
		outerComp.setLayoutData( FormDataMaker.makeFormData( titleComp, (Integer)null, 0, 100) );
		//outerComp.setBackground( Colors.ORANGE );

		innerComp = new Composite( outerComp, SWT.BORDER );
		innerComp.setLayout( new FormLayout() );
		innerComp.setLayoutData( FormDataMaker.makeFullFormData() );

		/* Start Number Widgets */
		Label startLabel = new Label( innerComp, SWT.None );
		startLabel.setText( TERMS_MUST_OCCUR );
		Point labelSize = startLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		startLabel.setLayoutData( FormDataMaker.makeFormData( 50, -labelSize.y/2, 100, -DIALOG_BORDER_MARGIN, 0, DIALOG_BORDER_MARGIN, (Integer)null, 0 ));
		
		myOperatorCombo = new Combo( innerComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		myOperatorCombo.setItems( COMBO_OPERATORS );
		Point comboSize = myOperatorCombo.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		myOperatorCombo.setLayoutData( FormDataMaker.makeFormData(50, -comboSize.y/2, 100, -DIALOG_BORDER_MARGIN, startLabel, DIALOG_BORDER_MARGIN, (Integer)null, 0));
		
		myNumberSpinner = new Spinner( innerComp, SWT.BORDER );
		myNumberSpinner.setBackground( Colors.WHITE );
		myNumberSpinner.setMinimum( 0 );
		myNumberSpinner.setMaximum( 9999 );
		Point spinnerSize = myNumberSpinner.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		myNumberSpinner.setLayoutData( FormDataMaker.makeFormData( 50, -spinnerSize.y/2, 100, -DIALOG_BORDER_MARGIN, myOperatorCombo, DIALOG_BORDER_MARGIN, (Integer)null, 0));
		myNumberSpinner.addModifyListener( new DefaultSpinnerValidator(myNumberSpinner) );
		
		Label endLabel = new Label( innerComp, SWT.None );
		endLabel.setText( TIMES );
		Point endLabelSize = startLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		endLabel.setLayoutData( FormDataMaker.makeFormData( 50, -endLabelSize.y/2, 100, -DIALOG_BORDER_MARGIN, myNumberSpinner, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN ));
		
		/* Start OK/Cancel buttons */
		Composite buttonsComposite = new Composite( myMainComp, SWT.None );
		buttonsComposite.setLayout( new FormLayout() );
		FormData fd = FormDataMaker.makeFormData( outerComp, 100, 0, 100);
		fd.height = DECISION_HEIGHT;
		buttonsComposite.setLayoutData( fd );
		buttonsComposite.setBackground( Colors.DARK_GRAY );
				
		myOKButton = new Button( buttonsComposite, SWT.PUSH );
		myOKButton.setText( OK );
		Point size = myOKButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		myOKButton.setLayoutData( FormDataMaker.makeFormData( 50, -size.y/2, (Integer)null, 0, (Integer)null, 0, 50, -size.x/2 - 5 ));
				
		myCancelButton 	= new Button( buttonsComposite, SWT.PUSH );
		size = myCancelButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		myCancelButton.setText( CANCEL );
		myCancelButton.setLayoutData( FormDataMaker.makeFormData( 50, -size.y/2, (Integer)null, 0, 50, size.x/2 + 5, (Integer)null, 0 ));
		
		myShell.setSize( myShell.computeSize( SWT.DEFAULT, SWT.DEFAULT ));
		updateWidgets(); // set default values
	}

	private void attachListeners() 
	{
		
		// TITLE for moving the dialog
		DialogMoveMouseListener moveListener = new DialogMoveMouseListener( myShell, titleComp );
		titleComp.addMouseListener( moveListener );
		titleComp.addMouseMoveListener( moveListener );
		titleLabel.addMouseListener( moveListener );
		titleLabel.addMouseMoveListener( moveListener );
		
		myOperatorCombo.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  { 				  
				  if ( myOperatorCombo.getText().equals( COMBO_OPERATORS[2] )) // EQUAL is selected, set number to 0
				  {
					  myNumberSpinner.setMinimum( 0 );
					  myNumberSpinner.setSelection(0);
					  myNumberSpinner.setEnabled( false );
				  }
				  else if ( myOperatorCombo.getText().equals( COMBO_OPERATORS[1] ) ) // LESS THAN is selected, make sure minimum is 2 
				  {
					  myNumberSpinner.setEnabled( true );
					  myNumberSpinner.setMinimum( 2 );
					  if ( Integer.parseInt( myNumberSpinner.getText() ) < 2 )
						  myNumberSpinner.setSelection( 2 );
				  }
				  else	// GREATER THAN is selected
				  {
					  myNumberSpinner.setMinimum( 0 );
					  myNumberSpinner.setEnabled( true );
				  }
					  
			  }
		});
		
		// Dialog OK button
		myOKButton.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  { 
				  myNumber = Integer.parseInt(myNumberSpinner.getText());
				  if ( myOperatorCombo.getItem( myOperatorCombo.getSelectionIndex() ).equals(COMBO_OPERATORS[0]) )
					  myOperator = UIConst.GREATER_THAN;
				  else if ( myOperatorCombo.getItem( myOperatorCombo.getSelectionIndex() ).equals(COMBO_OPERATORS[1]) )
					  myOperator = UIConst.LESS_THAN;
				  else
					  myOperator = UIConst.EQUAL;				  
				  //System.err.println( "OK: " + NumberConstraintDisplay.OPERATORS[myOperator] + " " + myNumber );
				  myShell.setVisible( false );
				  myShell.dispose();
			  }
		});
		// Dialog Cancel button
		myCancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{
				//System.err.println( "Cancel: " + NumberConstraintDisplay.OPERATORS[myOperator] + " " + myNumber );
				isCanceled = true;
				myShell.setVisible( false );
				myShell.dispose();
			}
		});
		
		// for drawing borders of widgets... bugbug: too tacky?
		//myOperatorCombo.addPaintListener( new ControlBorderDrawer( myOperatorCombo, Colors.ORANGE) );
		//myNumberSpinner.addPaintListener( new ControlBorderDrawer( myNumberSpinner, Colors.ORANGE) );
	}
	
	
	public Point getPreferredSize()
	{ return myShell.computeSize( SWT.DEFAULT, SWT.DEFAULT ); }
	
	public int getNumber()
	{ return this.myNumber; }
	
	public int getOperator()
	{ return this.myOperator;}
	
	public boolean isCanceled()
	{ return this.isCanceled; }
	
	public void updateWidgets()
	{
		if ( myOperator != null )
		{
			if ( myOperator == UIConst.GREATER_THAN )
				myOperatorCombo.select( 0 );
			else if ( myOperator == UIConst.LESS_THAN )
			{
				myOperatorCombo.select( 1 );
				myNumberSpinner.setMinimum( 2 );
			}
			else
				myOperatorCombo.select( 2 );
		}
		else
			myOperatorCombo.select( 0 );	// > (Greater Than) is default
		if ( myNumber != null )
			myNumberSpinner.setSelection( myNumber );
		else
			myNumberSpinner.setSelection( 0 );
	}
	
	public void setLocation( Point location )
	{
		myInitLocation = location;
	}

	
	// open the dialog
	public void open()
	{
		if ( this.myInitLocation != null )
			myShell.setLocation( this.myInitLocation );
		
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
	
	public static void main( String [] args )
	{
		NumberConstraintDialog ncd = new NumberConstraintDialog( null, null, SWT.BORDER | SWT.CLOSE );
		ncd.open();
	}

}