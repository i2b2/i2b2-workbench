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

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.ControlBorderDrawer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.KeyboardUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class DateConstraintDialog implements UIConst
{
	

	private Shell		myShell;
	private Composite	myMainComp;

	private Composite	titleComp;
	private Label		titleLabel;
	private Composite	innerComp;

	private Label		myStartDateLabel;
	private	Text		myStartDateText;
	private Label		myStartDateErrorLabel;
	private Composite	myStartDateComposite;
	private Button		myStartDateOKButton;
	private Button		myStartDateNoneButton;
	private	DateTime	myStartDateCalendar;
	private boolean 	isStartDateCalendarShown = false;

	private Label		myEndDateLabel;
	private	Text		myEndDateText;
	private Label		myEndDateErrorLabel;
	private Composite	myEndDateComposite;
	private Button		myEndDateOKButton;
	private Button		myEndDateNoneButton;
	private	DateTime	myEndDateCalendar;
	private boolean 	isEndDateCalendarShown = false;

	//private Composite 	myButtonsComposite;
	private Button		myOKButton;
	private Button		myCancelButton;

	private Point		myInitLocation; 
	// date data
	private GregorianCalendar	myStartDate	= null;
	private GregorianCalendar	myEndDate	= null;

	private boolean				isCanceled 	= false;
	
	public DateConstraintDialog( GregorianCalendar startDate, GregorianCalendar endDate, int styles  )
	{
		myStartDate 	= startDate;
		myEndDate		= endDate;
		setupUI( styles );
		attachListeners();
	}

	public DateConstraintDialog( GregorianCalendar startDate, GregorianCalendar endDate )
	{
		myStartDate 	= startDate;
		myEndDate		= endDate;
		setupUI( SWT.NONE );
		attachListeners();
	}

	private void setupUI( int shellStyles )
	{
		myShell = new Shell( Display.getCurrent(), SWT.APPLICATION_MODAL | shellStyles);
		myShell.setLayout( new FormLayout() );
		
		myMainComp = new Composite( myShell, SWT.NONE );
		myMainComp.setLayoutData( FormDataMaker.makeFullFormData() );
		myMainComp.setLayout( new FormLayout() );

		titleComp = new Composite( myMainComp, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );

		titleLabel = new Label( titleComp, SWT.NONE );
		titleLabel.setText( SPECIFY_DATE_CONSTRAINTS );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 50, -titleSize.x/2, (Integer)null, 0));

		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );

		Composite outerComp = new Composite( myMainComp, SWT.NONE );
		outerComp.setLayout( new FormLayout() );
		outerComp.setLayoutData( FormDataMaker.makeFormData( titleComp, (Integer)null, 0, 100) );

		innerComp = new Composite( outerComp, SWT.BORDER );
		innerComp.setLayout( new FormLayout() );
		innerComp.setLayoutData( FormDataMaker.makeFullFormData() );

		/* Start Date Widgets */
		myStartDateLabel = new Label( innerComp, SWT.NONE );
		myStartDateLabel.setText(START_DATE);
		myStartDateLabel.setLayoutData( FormDataMaker.makeFormData(0, DIALOG_BORDER_MARGIN, (Integer)null, 0, 0, DIALOG_BORDER_MARGIN, (Integer)null, 0) );

		myStartDateErrorLabel = new Label( innerComp, SWT.NONE );
		myStartDateErrorLabel.setLayoutData( FormDataMaker.makeFormData(0, DIALOG_BORDER_MARGIN, (Integer)null, 0, (Integer)null, 0, 100, -3* DIALOG_BORDER_MARGIN));		
		
		myStartDateText	= new Text( innerComp, SWT.SINGLE );
		myStartDateText.setLayoutData( FormDataMaker.makeFormData(0, DIALOG_BORDER_MARGIN, (Integer)null, 0, myStartDateLabel, DIALOG_BORDER_MARGIN, myStartDateErrorLabel, -DIALOG_BORDER_MARGIN) );
		
		myStartDateComposite = new Composite(innerComp, SWT.NONE);
		myStartDateComposite.setLayout( new FormLayout() );
		myStartDateComposite.setLayoutData( FormDataMaker.makeFormData(100, 0, (Integer)null, 0, myStartDateLabel, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN));

		myStartDateCalendar = new DateTime (myStartDateComposite, SWT.CALENDAR );
		myStartDateCalendar.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100) );
		setCalendarDates( myStartDateCalendar, myStartDate, myStartDateText );
		
		myStartDateOKButton = new Button( myStartDateComposite, SWT.PUSH );
		myStartDateOKButton.setText( OK );
		myStartDateOKButton.setLayoutData( FormDataMaker.makeFormData( myStartDateCalendar, 100, 0, 50) );
		myStartDateNoneButton = new Button( myStartDateComposite, SWT.PUSH );
		myStartDateNoneButton.setText( NONE );
		myStartDateNoneButton.setLayoutData( FormDataMaker.makeFormData( myStartDateCalendar, 100, 50, 100) );
		
		/* End Date Widgets */
		myEndDateLabel 	= new Label( innerComp, SWT.NONE );
		myEndDateLabel.setText(END_DATE);
		myEndDateLabel.setLayoutData( FormDataMaker.makeFormData( myStartDateLabel, DIALOG_BORDER_MARGIN, (Integer)null, 0, 0, DIALOG_BORDER_MARGIN, (Integer)null, 0) );

		myEndDateErrorLabel = new Label( innerComp, SWT.NONE );
		myEndDateErrorLabel.setLayoutData( FormDataMaker.makeFormData(myStartDateLabel, DIALOG_BORDER_MARGIN, (Integer)null, 0, (Integer)null, 0, 100, -3* DIALOG_BORDER_MARGIN));

		myEndDateText	= new Text( innerComp, SWT.SINGLE );
		myEndDateText.setLayoutData( FormDataMaker.makeFormData( myStartDateLabel, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN, myStartDateLabel, DIALOG_BORDER_MARGIN, myEndDateErrorLabel, -DIALOG_BORDER_MARGIN) );
		
		myEndDateComposite = new Composite(innerComp, SWT.NONE);
		myEndDateComposite.setLayout( new FormLayout() );
		myEndDateComposite.setLayoutData( FormDataMaker.makeFormData(100, 0, (Integer)null, 0, myStartDateLabel, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN));

		myEndDateCalendar = new DateTime (myEndDateComposite, SWT.CALENDAR );
		myEndDateCalendar.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100) );
		setCalendarDates( myEndDateCalendar, myEndDate, myEndDateText );

		myEndDateOKButton = new Button( myEndDateComposite, SWT.PUSH );
		myEndDateOKButton.setText( OK );
		myEndDateOKButton.setLayoutData( FormDataMaker.makeFormData( myEndDateCalendar, 100, 0, 50) );
		myEndDateNoneButton = new Button( myEndDateComposite, SWT.PUSH );
		myEndDateNoneButton.setText( NONE );
		myEndDateNoneButton.setLayoutData( FormDataMaker.makeFormData( myEndDateCalendar, 100, 50, 100) );
		
		Composite buttonsComposite = new Composite( myMainComp, SWT.NONE );
		buttonsComposite.setLayout( new FormLayout() );
		FormData fd = FormDataMaker.makeFormData( outerComp, 100, 0, 100);
		fd.height = DECISION_HEIGHT;
		buttonsComposite.setLayoutData( fd );
		buttonsComposite.setBackground( Colors.DARK_GRAY );
		
		myOKButton 		= new Button( buttonsComposite, SWT.PUSH );
		myOKButton.setText( OK );
		Point size = myOKButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		myOKButton.setLayoutData( FormDataMaker.makeFormData( 50, -size.y/2, (Integer)null, 0, (Integer)null, 0, 50, -size.x/2 - 5 ));
		
		myCancelButton 	= new Button( buttonsComposite, SWT.PUSH );
		size = myCancelButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		myCancelButton.setText( CANCEL );
		myCancelButton.setLayoutData( FormDataMaker.makeFormData( 50, -size.y/2, (Integer)null, 0, 50, size.x/2 + 5, (Integer)null, 0 ));

		try
		{
			myStartDateErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR) );
			myEndDateErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR) );
		}
		catch ( IllegalStateException e)
		{
			myStartDateErrorLabel.setText("X");
			myStartDateErrorLabel.setForeground( Colors.DARK_RED );
			myEndDateErrorLabel.setText("X");
			myEndDateErrorLabel.setForeground( Colors.DARK_RED );
		}
		myStartDateErrorLabel.setVisible( false );
		myEndDateErrorLabel.setVisible( false );
		
		myShell.setSize( myShell.computeSize( SWT.DEFAULT, SWT.DEFAULT ));
	}
	
	private void setCalendarDates( DateTime dateWidget, GregorianCalendar value, Text text )
	{
		if ( value != null )
		{
			dateWidget.setYear( value.get(Calendar.YEAR) );
			dateWidget.setMonth( value.get(Calendar.MONTH) );
			dateWidget.setDay( value.get(Calendar.DAY_OF_MONTH) );
			text.setText( DateParser.toFormat( value ) );
		}
		else
			text.setText( DATE_FORMAT );
	}
	
	private void attachListeners() 
	{		
		/* Custom Drawing Texts' borders */
		myStartDateText.addPaintListener( new ControlBorderDrawer( myStartDateText, Colors.ORANGE ) );
		myEndDateText.addPaintListener( new ControlBorderDrawer( myEndDateText, Colors.ORANGE ) );
		
		/* Hides Calendars and check input when Mouse clicks away in these components */
		MouseListener hideCalendarMouseListener = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent arg0) {}
			public void mouseDown(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0) 
			{	// hide both calendars when this is clicked
				if ( isStartDateCalendarShown )
				{
					checkDateInputs( myStartDateErrorLabel );
					setStartDateCalendarVisible( false );
				}
				if ( isEndDateCalendarShown )
				{
					checkDateInputs( myEndDateErrorLabel );
					setEndDateCalendarVisible( false );
				}
				myOKButton.setFocus();
				relayoutDialog();
			}
		};
		titleComp.addMouseListener( hideCalendarMouseListener );
		titleLabel.addMouseListener( hideCalendarMouseListener );
		innerComp.addMouseListener( hideCalendarMouseListener );

		/*  Mouse Click on date text fields */
		myStartDateText.addMouseListener( new MouseListener(){
			public void mouseDoubleClick(MouseEvent arg0) {}
			public void mouseDown(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0) 
			{
				if ( isEndDateCalendarShown )
				{
					setEndDateCalendarVisible( false );
					checkDateInputs( myEndDateErrorLabel );
				}				
				if ( isStartDateCalendarShown )return;
				setStartDateCalendarVisible( true );
				myStartDateText.selectAll();
				myOKButton.setEnabled( DateParser.isLegal( myStartDateText.getText() ) && DateParser.isLegal( myEndDateText.getText()) );			
				relayoutDialog();
			}});		
		myEndDateText.addMouseListener( new MouseListener(){
			public void mouseDoubleClick(MouseEvent arg0) {}
			public void mouseDown(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0) 
			{
				if ( isStartDateCalendarShown )
				{
					setStartDateCalendarVisible( false );
					checkDateInputs( myStartDateErrorLabel );
				}
				if ( isEndDateCalendarShown ) return;
				setEndDateCalendarVisible( true );
				myEndDateText.selectAll();
				myOKButton.setEnabled( DateParser.isLegal( myStartDateText.getText() ) && DateParser.isLegal( myEndDateText.getText()) );			
				relayoutDialog();
			}});
		
		/* Key listeners on date text fields*/
		myStartDateText.addKeyListener( new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent arg0)
			{
				if ( KeyboardUtils.isTextNavigationKey(arg0) ) return; // handle directional/home/end keys normally
				if ( !DateParser.isValidCharacterForDates( arg0.character ) )
					arg0.doit = false;	// if not legal character, disable drawing the character in text field
				
				if ( arg0.character == 'N' || arg0.character == 'n') //'None' or 'none'
					myStartDateText.setText( NONE );				
				else if ( arg0.character == 13 ) // carriage return (user accepts the typed-in text)
				{
					checkDateInputs( myStartDateErrorLabel );
					setStartDateCalendarVisible( false );
					myOKButton.setFocus();
					relayoutDialog();
				}
				else if ( arg0.character == 27 ) // ESC	(user cancels editing))
				{
					if ( myStartDate == null )
						myStartDateText.setText( NONE );
					else
						myStartDateText.setText( myStartDate.get( Calendar.MONTH )+1 + "/" + myStartDate.get( Calendar.DAY_OF_MONTH ) + "/" + myStartDate.get( Calendar.YEAR ) );
					setStartDateCalendarVisible( false );
					relayoutDialog();				
				}							
			}
			public void keyReleased(KeyEvent arg0)	
			{ 
				myOKButton.setEnabled( DateParser.isLegal( myStartDateText.getText() ) && DateParser.isLegal( myEndDateText.getText()) );
			}
		} );
		
		myEndDateText.addKeyListener( new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent arg0)
			{
				if ( KeyboardUtils.isTextNavigationKey(arg0) ) return; // handle directional/home/end keys normally
				if ( !DateParser.isValidCharacterForDates( arg0.character ) )
					arg0.doit = false;	// if not legal character, disable drawing the character in text field
				
				if ( arg0.character == 'N' || arg0.character == 'n') //'None' or 'none'
					myEndDateText.setText( NONE );				
				else if ( arg0.character == 13 ) // carriage return (user accepts the typed-in text)
				{
					checkDateInputs( myEndDateErrorLabel );
					setEndDateCalendarVisible( false );
					myOKButton.setFocus();
					relayoutDialog();
				}
				else if ( arg0.character == 27 ) // ESC	(user cancels editing))
				{
					if ( myEndDate == null )
						myEndDateText.setText( NONE );
					else
						myEndDateText.setText( myEndDate.get( Calendar.MONTH )+1 + "/" + myEndDate.get( Calendar.DAY_OF_MONTH ) + "/" + myEndDate.get( Calendar.YEAR ) );
					setEndDateCalendarVisible( false );
					relayoutDialog();				
				}							
			}
			public void keyReleased(KeyEvent arg0)	
			{
				myOKButton.setEnabled( DateParser.isLegal( myStartDateText.getText() ) && DateParser.isLegal( myEndDateText.getText()) );
			}
		} );

		
		/*  Calendar selection */
		this.myStartDateCalendar.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{
				int year	= myStartDateCalendar.getYear();
				int month	= myStartDateCalendar.getMonth();
				int day		= myStartDateCalendar.getDay();				
				myStartDateText.setText( (month+1) + "/" + day + "/" + year );				
				myOKButton.setEnabled( DateParser.isLegal( myStartDateText.getText() ) && DateParser.isLegal( myEndDateText.getText()) );
			}
		});		
		this.myEndDateCalendar.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{
				int year	= myEndDateCalendar.getYear();
				int month	= myEndDateCalendar.getMonth();
				int day		= myEndDateCalendar.getDay();				
				myEndDateText.setText( (month+1) + "/" + day + "/" + year );
				myOKButton.setEnabled( DateParser.isLegal( myStartDateText.getText() ) && DateParser.isLegal( myEndDateText.getText()) );
			}
		});

		/* Calendar Keyboard Enter */
		this.myStartDateCalendar.addKeyListener( new KeyListener()
		{
			public void keyPressed(KeyEvent arg0) 
			{
				if ( arg0.character == 13 ) // carriage return (user accepts the typed-in text)
				{
					checkDateInputs( myStartDateErrorLabel );
					setStartDateCalendarVisible( false );
					myOKButton.setFocus();
					relayoutDialog();
				}
				else if ( arg0.character == 27 ) // ESC	(user cancels editing))
				{
					if ( myStartDate == null )
						myStartDateText.setText( NONE );
					else
						myStartDateText.setText( myStartDate.get( Calendar.MONTH )+1 + "/" + myStartDate.get( Calendar.DAY_OF_MONTH ) + "/" + myStartDate.get( Calendar.YEAR ) );
					setStartDateCalendarVisible( false );
					myOKButton.setFocus();
					relayoutDialog();
				}
			}
			public void keyReleased(KeyEvent arg0){}
		});

		this.myEndDateCalendar.addKeyListener( new KeyListener()
		{
			public void keyPressed(KeyEvent arg0) 
			{
				if ( arg0.character == 13 ) // carriage return (user accepts the typed-in text)
				{
					checkDateInputs( myEndDateErrorLabel );
					setEndDateCalendarVisible( false );
					myOKButton.setFocus();
					relayoutDialog();
				}
				else if ( arg0.character == 27 ) // ESC	(user cancels editing))
				{
					if ( myEndDate == null )
						myEndDateText.setText( NONE );
					else
						myEndDateText.setText( myEndDate.get( Calendar.MONTH )+1 + "/" + myEndDate.get( Calendar.DAY_OF_MONTH ) + "/" + myEndDate.get( Calendar.YEAR ) );
					setEndDateCalendarVisible( false );
					myOKButton.setFocus();
					relayoutDialog();				
				}
			}
			public void keyReleased(KeyEvent arg0){}			
		});
		
		/* Calendar None Button */
		myStartDateNoneButton.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{ 
				myStartDateText.setText( NONE );
				checkDateInputs( myStartDateErrorLabel );
				setStartDateCalendarVisible( false );
				myOKButton.setFocus();
				relayoutDialog();			
			}
		});
		myEndDateNoneButton.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{ 
				myEndDateText.setText( NONE );
				checkDateInputs( myEndDateErrorLabel );
				setEndDateCalendarVisible( false );
				myOKButton.setFocus();
				relayoutDialog();			
			}
		});
		
		/* Calendar OK Button */
		myStartDateOKButton.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{ 		
				checkDateInputs( myStartDateErrorLabel );
				setStartDateCalendarVisible( false );
				myOKButton.setFocus();
				relayoutDialog();
			}
		});		
		myEndDateOKButton.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{
				checkDateInputs( myEndDateErrorLabel );
				setEndDateCalendarVisible( false );
				myOKButton.setFocus();
				relayoutDialog();
			}
		});
		
		// Dialog OK button
		myOKButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{
				myStartDate = newDateValue( myStartDateText );
				myEndDate = newDateValue( myEndDateText );
				myShell.setVisible( false );
				myShell.dispose();
			}
		});
		// Dialog Cancel button
		myCancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{
				isCanceled = true;
				myShell.setVisible( false );
				myShell.dispose();
			}
		});
		
		// TITLE for moving the dialog
		DialogMoveMouseListener moveListener = new DialogMoveMouseListener( myShell, titleComp );
		titleComp.addMouseListener( moveListener );
		titleComp.addMouseMoveListener( moveListener );
		titleLabel.addMouseListener( moveListener );
		titleLabel.addMouseMoveListener( moveListener );
	}

	private void setStartDateCalendarVisible( boolean flag )
	{
		if ( flag )
		{
			myStartDateComposite.setLayoutData( FormDataMaker.makeFormData(myStartDateLabel, 0, (Integer)null, 0, myStartDateLabel, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN) );
			myEndDateLabel.setLayoutData( FormDataMaker.makeFormData( myStartDateComposite, DIALOG_BORDER_MARGIN, (Integer)null, 0, 0, DIALOG_BORDER_MARGIN, (Integer)null, 0) );
			myEndDateText.setLayoutData( FormDataMaker.makeFormData( myStartDateComposite, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN, myStartDateLabel, DIALOG_BORDER_MARGIN, myEndDateErrorLabel, -DIALOG_BORDER_MARGIN) );
			myEndDateErrorLabel.setLayoutData( FormDataMaker.makeFormData( myStartDateComposite, DIALOG_BORDER_MARGIN, (Integer)null, 0, (Integer)null, 0, 100, -3*DIALOG_BORDER_MARGIN) );
		}
		else
		{
			myStartDateComposite.setLayoutData( FormDataMaker.makeFormData(100, 0, (Integer)null, 0, myStartDateLabel, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN) );
			myEndDateLabel.setLayoutData( FormDataMaker.makeFormData( myStartDateLabel, DIALOG_BORDER_MARGIN, (Integer)null, 0, 0, DIALOG_BORDER_MARGIN, (Integer)null, 0) );
			myEndDateText.setLayoutData( FormDataMaker.makeFormData( myStartDateLabel, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN, myStartDateLabel, DIALOG_BORDER_MARGIN, myEndDateErrorLabel, -DIALOG_BORDER_MARGIN) );
			myEndDateErrorLabel.setLayoutData( FormDataMaker.makeFormData( myStartDateLabel, DIALOG_BORDER_MARGIN, (Integer)null, 0, (Integer)null, 0, 100, -3*DIALOG_BORDER_MARGIN) );
		}
		isStartDateCalendarShown = flag;
	}

	private void setEndDateCalendarVisible( boolean flag )
	{
		if ( flag )
		{
			myEndDateText.setLayoutData( FormDataMaker.makeFormData( myStartDateLabel, DIALOG_BORDER_MARGIN, (Integer)null, 0, myStartDateLabel, DIALOG_BORDER_MARGIN, myEndDateErrorLabel, -DIALOG_BORDER_MARGIN) );
			myEndDateComposite.setLayoutData( FormDataMaker.makeFormData(myEndDateLabel, 0, 100, -DIALOG_BORDER_MARGIN, myStartDateLabel, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN) );			
		}
		else
		{
			myEndDateText.setLayoutData( FormDataMaker.makeFormData( myStartDateLabel, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN, myStartDateLabel, DIALOG_BORDER_MARGIN, myEndDateErrorLabel, -DIALOG_BORDER_MARGIN) );
			myEndDateComposite.setLayoutData( FormDataMaker.makeFormData(100, 0, (Integer)null, 0, myStartDateLabel, DIALOG_BORDER_MARGIN, 100, -DIALOG_BORDER_MARGIN) );			
		}
		isEndDateCalendarShown = flag;
	}
		
	private void checkDateInputs( Label lastChangedDateLabel )
	{
		boolean isStartDateLegal 	= DateParser.isLegal( myStartDateText.getText() );
		boolean isEndDateLegal 		= DateParser.isLegal( myEndDateText.getText() );
		if ( isStartDateLegal && !isEndDateLegal )
		{
			setErrorIconVisible( myStartDateErrorLabel, false );
			myStartDate = newDateValue( myStartDateText );
			setErrorIconVisible( myEndDateErrorLabel, true );
			myEndDateErrorLabel.setToolTipText( NOT_A_LEGAL_DATE );
			myOKButton.setEnabled( false );
		}
		else if ( !isStartDateLegal && isEndDateLegal )
		{
			setErrorIconVisible( myEndDateErrorLabel, false );
			myEndDate = newDateValue( myEndDateText );
			setErrorIconVisible( myStartDateErrorLabel, true );
			myStartDateErrorLabel.setToolTipText( NOT_A_LEGAL_DATE );
			myOKButton.setEnabled( false );
		}
		else if ( !isStartDateLegal && !isEndDateLegal )
		{
			setErrorIconVisible( myStartDateErrorLabel, true );
			myStartDateErrorLabel.setToolTipText( NOT_A_LEGAL_DATE );
			setErrorIconVisible( myEndDateErrorLabel, true );
			myEndDateErrorLabel.setToolTipText( NOT_A_LEGAL_DATE );
			myOKButton.setEnabled( false );
		}
		else // ( isStartDateLegal && isEndDateLegal )
		{
			myStartDate = newDateValue( myStartDateText );
			myEndDate 	= newDateValue( myEndDateText );
			if ( isStartDateAfterEndDate() )
			{
				lastChangedDateLabel.setToolTipText( START_DATE_AFTER_END_DATE );
				setErrorIconVisible( lastChangedDateLabel, true );
				myOKButton.setEnabled( false );
			}
			else // clear both flags
			{
				setErrorIconVisible( this.myStartDateErrorLabel, false );
				setErrorIconVisible( this.myEndDateErrorLabel, false );
				myOKButton.setEnabled( true );
			}
		}
	}

	private GregorianCalendar newDateValue( Text dateText )
	{
		String text = dateText.getText();
		if ( DateParser.isEmpty( text) )
		{
			dateText.setText( NONE );
			return null;
		}
		else
		{
			GregorianCalendar date = new GregorianCalendar();
			date.setTimeInMillis( DateParser.parseDate( text ).getTime() );
			return date;
		}
	}
	
	private void relayoutDialog()
	{
		DateConstraintDialog.this.myShell.layout(true, true);
		myShell.setSize( myShell.computeSize( SWT.DEFAULT, SWT.DEFAULT ));
	}
	
	// Check to see if start date is greater than end date. Equal dates are fine as we 
	// are dealing with inclusive dates: e.g. 12/14/2012-12/14/2012 is the one-day span
	private boolean isStartDateAfterEndDate()
	{
		if ( this.myStartDate == null || myEndDate == null ) 
			return false;
		if ( this.myStartDate.getTimeInMillis() > this.myEndDate.getTimeInMillis() )
			return true;
		return false;
	}
	
	private void setErrorIconVisible( Label label, boolean flag )
	{	label.setVisible( flag ); }
	
	
	// Public accessors
	public GregorianCalendar getStartDate()	{ return this.myStartDate; }
	public GregorianCalendar getEndDate()	{ return this.myEndDate; }
	public boolean	isCanceled()			{ return this.isCanceled; }
	
	
	public void setLocation( Point location )
	{
		myInitLocation = location;
	}
	
	public Point getPreferredSize()
	{ return myShell.computeSize( SWT.DEFAULT, SWT.DEFAULT ); }
	
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
		DateConstraintDialog dcd = new DateConstraintDialog( null, null, SWT.BORDER );
		dcd.open();
	}
	
}
