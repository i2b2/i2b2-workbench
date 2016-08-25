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
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.OrderedDuration;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.TemporalRelationship;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.TemporalRelationship.Operator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.TemporalRelationship.TimeUnit;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.DefaultSpinnerValidator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.KeyboardUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class TemporalRelationshipPanel extends QueryToolPanelComposite implements UIConst 
{

	public static boolean isValidCharacterForSpan( char c )
	{ return Character.isDigit(c); }
	

	private Composite mainComp;
	private Composite leftComp;
	
	private Label		myTopColorLabel;
	private Label		myBotColorLabel;
	
	private Label		myCloseLabel;
	
	private Combo		topEventMarkerCombo;
	private Combo		topEventOccurrenceCombo;
	private Combo		topEventCombo;
	
	private Combo		eventRelationshipCombo;
	
	private Combo		botEventMarkerCombo;
	private Combo		botEventOccurrenceCombo;
	private Combo		botEventCombo;
	
	private Button		byButton;
	private	Button		andButton;
	
	private Combo		topOperatorCombo;
	private Spinner		topSpinner;
	private Combo		topTimeUnitCombo;
	
	private Combo		botOperatorCombo;
	private Spinner		botSpinner;
	private Combo		botTimeUnitCombo;
	
	private TemporalRelationshipManager myManager;
	
	private TemporalRelationship		myData;
	
	
	
	// for testing only
	private TemporalRelationshipPanel(Composite parent, int style ) 
	{
		super(parent, style | SWT.BORDER );
		myData = new TemporalRelationship();
		setupUI();
		attachListeners();
	}
	
	public TemporalRelationshipPanel(Composite parent, int style, TemporalRelationshipManager manager) 
	{
		super(parent, style | SWT.BORDER );
		myManager = manager;
		myData = new TemporalRelationship(); 
		setupUI();
		if ( this.myManager !=  null ) // when doing UI testing, myManager == null
		{
			updateEventList( this.myManager.getCachedEvents() );
			setDataValuesFromUI(); // update data because updating lists automatically makes default selections
		}
		setUIWithData();		 // set UI with myData
		updateIntervalWidgets(); // set default selections
		attachListeners();
	}

	public TemporalRelationshipPanel(Composite parent, int style, TemporalRelationshipManager manager, TemporalRelationship relationship) 
	{
		super(parent, style | SWT.BORDER );
		myManager = manager;
		myData = relationship; 
		setupUI();
		updateEventList( this.myManager.getCachedEvents() );
		setUIWithData();		 // set UI with myData
		updateIntervalWidgets(); // set default selections
		attachListeners();
	}

	
	private void setupUI() 
	{
		this.setLayout( new FormLayout() );
		
		leftComp = new Composite( this, SWT.BORDER );
		FormData fd = FormDataMaker.makeFormData( 0, 100, 0, (Integer)null);
		fd.width = 20;
		leftComp.setLayoutData( fd );
		leftComp.setLayout( new FormLayout() );
		leftComp.setBackground( Colors.GRAY );
		
		myTopColorLabel = new Label( leftComp, SWT.NONE );
			FormData topLabelFD = FormDataMaker.makeFormData(0, 2, (Integer)null, 0, 0, 2, (Integer)null, 0);
			topLabelFD.width 	= 16;
			topLabelFD.height 	= 16;
		myTopColorLabel.setLayoutData( topLabelFD );
		myTopColorLabel.setBackground( Colors.GRAY );
		
		myBotColorLabel = new Label( leftComp, SWT.NONE );
			FormData botLabelFD = FormDataMaker.makeFormData(myTopColorLabel, 2, (Integer)null, 0, 0, 2, (Integer)null, 0);
			botLabelFD.width 	= 16;
			botLabelFD.height 	= 16;
		myBotColorLabel.setLayoutData( botLabelFD );
		myBotColorLabel.setBackground( Colors.GRAY );
		
		mainComp = new Composite( this, SWT.BORDER );
		mainComp.setLayout( new FormLayout() );
		mainComp.setLayoutData( FormDataMaker.makeFormData( 0, 100, leftComp, 100 ));
		
		myCloseLabel = new Label( mainComp, SWT.NONE );
		
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			myCloseLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_ETOOL_DELETE_DISABLED ) );
		}
		else
		{
			myCloseLabel.setText(" X ");
			myCloseLabel.setForeground( Colors.DARK_RED );
		}
		myCloseLabel.setLayoutData( FormDataMaker.makeFormData( 0, 2, (Integer)null, 0, (Integer)null, 0, 100, -2) );
		
		//top set of combos
		topEventMarkerCombo = new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		topEventMarkerCombo.setLayoutData(FormDataMaker.makeFormData(0, RELATIONSHIP_COMBO_MARGIN, (Integer)null, 0, 0, RELATIONSHIP_COMBO_MARGIN, (Integer)null, 0) );
		
		topEventOccurrenceCombo = new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		topEventOccurrenceCombo.setLayoutData( FormDataMaker.makeFormData( 0, RELATIONSHIP_COMBO_MARGIN, (Integer)null, 0,  topEventMarkerCombo, RELATIONSHIP_COMBO_SPACING, (Integer)null, 0) );
		
		topEventCombo = new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		topEventCombo.setLayoutData( FormDataMaker.makeFormData( 0, RELATIONSHIP_COMBO_MARGIN, (Integer)null, 0, topEventOccurrenceCombo, RELATIONSHIP_COMBO_SPACING, (Integer)null, 0 ));

		//relationship combo
		eventRelationshipCombo = new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		eventRelationshipCombo.setLayoutData( FormDataMaker.makeFormData( topEventMarkerCombo, RELATIONSHIP_COMBO_VERTICAL_SPACING, (Integer)null, 0, 0, RELATIONSHIP_COMBO_MARGIN*4, (Integer)null, 0) );

		//bottom set of combos
		botEventMarkerCombo = new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		botEventMarkerCombo.setLayoutData(FormDataMaker.makeFormData(eventRelationshipCombo, RELATIONSHIP_COMBO_VERTICAL_SPACING, (Integer)null, 0, 0, RELATIONSHIP_COMBO_MARGIN, (Integer)null, 0) );

		botEventOccurrenceCombo = new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		botEventOccurrenceCombo.setLayoutData( FormDataMaker.makeFormData( eventRelationshipCombo, RELATIONSHIP_COMBO_VERTICAL_SPACING, (Integer)null, 0,  botEventMarkerCombo, RELATIONSHIP_COMBO_SPACING, (Integer)null, 0) );

		botEventCombo = new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		botEventCombo.setLayoutData( FormDataMaker.makeFormData( eventRelationshipCombo, RELATIONSHIP_COMBO_VERTICAL_SPACING, (Integer)null, 0, botEventOccurrenceCombo, RELATIONSHIP_COMBO_SPACING, (Integer)null, 0 ));
		
		int comboHeight = botEventCombo.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y; // compute for generic combo height for layingout widgets
		
		// top interval combos
		topTimeUnitCombo	= new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		topTimeUnitCombo.setLayoutData( FormDataMaker.makeFormData( botEventCombo, RELATIONSHIP_COMBO_MARGIN, (Integer)null, 0, (Integer)null, 0, 100, -RELATIONSHIP_COMBO_SPACING ) );

		topSpinner			= new Spinner( mainComp, SWT.BORDER);
		topSpinner.setMaximum( 9999 );
		topSpinner.setMinimum( 1 );
		topSpinner.setBackground( Colors.WHITE );
		topSpinner.addModifyListener( new DefaultSpinnerValidator( topSpinner ) );
		
		int spinnerHeight = topSpinner.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
		topSpinner.setLayoutData( FormDataMaker.makeFormData( botEventCombo, RELATIONSHIP_COMBO_MARGIN + (comboHeight-spinnerHeight)/2, (Integer)null, 0, (Integer)null, 0, topTimeUnitCombo, -RELATIONSHIP_COMBO_SPACING ) );

		topOperatorCombo	= new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );		
		topOperatorCombo.setLayoutData( FormDataMaker.makeFormData( botEventCombo, RELATIONSHIP_COMBO_MARGIN, (Integer)null, 0, (Integer)null, 0, topSpinner, -RELATIONSHIP_COMBO_SPACING ));
		
		byButton		= new Button( mainComp, SWT.CHECK );
		byButton.setText( BY_BUTTON );
		int buttonHeight = byButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
		FormData byButtonFD = FormDataMaker.makeFormData( botEventCombo, RELATIONSHIP_COMBO_MARGIN + (comboHeight-buttonHeight)/2, (Integer)null, 0, (Integer)null, 0, topOperatorCombo, -RELATIONSHIP_COMBO_SPACING);
		byButton.setLayoutData( byButtonFD );

		// bottom interval combos
		botTimeUnitCombo	= new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );
		botTimeUnitCombo.setLayoutData( FormDataMaker.makeFormData( topOperatorCombo, RELATIONSHIP_COMBO_VERTICAL_SPACING + (comboHeight-spinnerHeight)/2, (Integer)null, 0, (Integer)null, 0, 100, -RELATIONSHIP_COMBO_SPACING ) );

		botSpinner			= new Spinner( mainComp, SWT.BORDER );
		botSpinner.setLayoutData( FormDataMaker.makeFormData( topOperatorCombo, RELATIONSHIP_COMBO_VERTICAL_SPACING, (Integer)null, 0, (Integer)null, 0, botTimeUnitCombo, -RELATIONSHIP_COMBO_SPACING ) );
		botSpinner.setMaximum( 9999 );
		botSpinner.setMinimum( 1 );
		botSpinner.setBackground( Colors.WHITE );
		botSpinner.addModifyListener( new DefaultSpinnerValidator( botSpinner ) );

		botOperatorCombo	= new Combo( mainComp, SWT.READ_ONLY | SWT.DROP_DOWN );		
		botOperatorCombo.setLayoutData( FormDataMaker.makeFormData( topOperatorCombo, RELATIONSHIP_COMBO_VERTICAL_SPACING, (Integer)null, 0, (Integer)null, 0, botSpinner, -RELATIONSHIP_COMBO_SPACING ));

		andButton		= new Button( mainComp, SWT.CHECK );
		andButton.setText( AND_BUTTON );
		FormData andButtonFD = FormDataMaker.makeFormData( topOperatorCombo, RELATIONSHIP_COMBO_VERTICAL_SPACING + (comboHeight-buttonHeight)/2, (Integer)null, 0, (Integer)null, 0, botOperatorCombo, -RELATIONSHIP_COMBO_SPACING);
		andButton.setLayoutData( andButtonFD );
		
		// make sure by and and button have the same width
		int preferredWidth =  Math.max( byButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x, andButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x );
		((FormData)byButton.getLayoutData()).width = preferredWidth;
		((FormData)andButton.getLayoutData()).width = preferredWidth;

		// set items for combos
		int i = 0;
		for ( i = 0; i < UIConst.EVENT_RELATIONSHIP.length; i++)
			eventRelationshipCombo.add( UIConst.EVENT_RELATIONSHIP[i]);
		eventRelationshipCombo.select(0);

		for ( i = 0; i < UIConst.EVENT_MARKERS.length; i++)
		{
			topEventMarkerCombo.add( UIConst.EVENT_MARKERS[i]);
			botEventMarkerCombo.add( UIConst.EVENT_MARKERS[i]);
		}
		topEventMarkerCombo.select(0);
		botEventMarkerCombo.select(0);

		for ( i = 0; i < UIConst.EVENT_OCCURRENCES.length; i++ )
		{
			topEventOccurrenceCombo.add( UIConst.EVENT_OCCURRENCES[i]);
			botEventOccurrenceCombo.add( UIConst.EVENT_OCCURRENCES[i]);
		}
		topEventOccurrenceCombo.select(0);
		botEventOccurrenceCombo.select(0);

		for ( i = 0; i < UIConst.TEMPORAL_OPERATORS.length; i++ )
		{
			topOperatorCombo.add( UIConst.TEMPORAL_OPERATORS[i] );
			botOperatorCombo.add( UIConst.TEMPORAL_OPERATORS[i] );
		}
		topOperatorCombo.select(1);	// default is >=
		botOperatorCombo.select(3);	// default is <=

		for ( i = 0; i < UIConst.TEMPORAL_UNITS.length; i++ )
		{
			topTimeUnitCombo.add( UIConst.TEMPORAL_UNITS[i] );
			botTimeUnitCombo.add( UIConst.TEMPORAL_UNITS[i] );
		}
		topTimeUnitCombo.select(3);	// 3 for day(s)
		botTimeUnitCombo.select(3);		
	}

	// set the widgets using  myData
	private void setUIWithData() 
	{
		// do nothing if myData is not complete
		if ( this.myData == null || this.myData.getTopEvent() == null )
			return; 
		myTopColorLabel.setBackground( Colors.getEventColor( this.myData.getTopEvent() ) );
		myBotColorLabel.setBackground( Colors.getEventColor( this.myData.getBotEvent() ) );

		// select Start of/ End of 
		topEventMarkerCombo.select( getComboIndexByString(topEventMarkerCombo, convertEventMarkerToString(myData.getTopReferencePoint()) ));
		botEventMarkerCombo.select( getComboIndexByString(botEventMarkerCombo, convertEventMarkerToString(myData.getBotReferencePoint()) ));

		// select First Ever/ Last Ever/ Any
		topEventOccurrenceCombo.select( getComboIndexByString(topEventOccurrenceCombo, convertEventOccurrenceToString( myData.getTopOccurrenceRestriction() ) ) );
		botEventOccurrenceCombo.select( getComboIndexByString(botEventOccurrenceCombo, convertEventOccurrenceToString( myData.getBotOccurrenceRestriction() ) ) );

		// select Event name
		topEventCombo.select( getComboIndexByString( topEventCombo, myData.getTopEvent().getName() ));
		botEventCombo.select( getComboIndexByString( botEventCombo, myData.getBotEvent().getName() ));

		// select event ordering
		eventRelationshipCombo.select( getComboIndexByString(eventRelationshipCombo, convertRelationshipToString( myData.getOperator() )) );

		// set durations
		if ( myData.getDuration1() != null )
		{
			byButton.setSelection( true );
			topOperatorCombo.select( getComboIndexByString( topOperatorCombo, convertOperatorToString( myData.getDuration1().getOperator()) ) );
			topTimeUnitCombo.select( getComboIndexByString( topTimeUnitCombo, convertTimeUnitToString( myData.getDuration1().getUnit() ) ));
			topSpinner.setSelection( myData.getDuration1().getNumber() );			
		}
		if ( myData.getDuration2() != null )
		{
			andButton.setSelection( true );
			botOperatorCombo.select( getComboIndexByString( botOperatorCombo, convertOperatorToString( myData.getDuration2().getOperator()) ) );
			botTimeUnitCombo.select( getComboIndexByString( botTimeUnitCombo, convertTimeUnitToString( myData.getDuration2().getUnit() ) ));
			botSpinner.setSelection( myData.getDuration2().getNumber() );			
		}
	}

	public static final String convertTimeUnitToString(TimeUnit unit) 
	{
		if ( unit == TimeUnit.SECONDS )
			return UIConst.TEMPORAL_UNITS[0];
		else if ( unit == TimeUnit.MINUTES )
			return UIConst.TEMPORAL_UNITS[1];
		else if ( unit == TimeUnit.HOURS )
			return UIConst.TEMPORAL_UNITS[2];
		else if ( unit == TimeUnit.DAYS )
			return UIConst.TEMPORAL_UNITS[3];
		else if ( unit == TimeUnit.MONTHS )
			return UIConst.TEMPORAL_UNITS[4];
		else if ( unit == TimeUnit.YEARS )
			return UIConst.TEMPORAL_UNITS[5];
		else
		{
			assert false : "TemporalRelationshipPanel.convertTimeUnitToString(): Unit '" + unit + "' is not recognized.";
			return null;
		}
	}

	public static final String convertRelationshipToString(Operator operator) 
	{
		if ( operator == TemporalRelationship.Operator.BEFORE )
			return UIConst.EVENT_RELATIONSHIP[0];
		else if ( operator == TemporalRelationship.Operator.ON_OR_BEFORE )
			return UIConst.EVENT_RELATIONSHIP[1];
		else if ( operator == TemporalRelationship.Operator.EQUALS )
			return UIConst.EVENT_RELATIONSHIP[2];
		else if ( operator == TemporalRelationship.Operator.ON_OR_AFTER )
			return UIConst.EVENT_RELATIONSHIP[3];
		else if ( operator == TemporalRelationship.Operator.AFTER )
			return UIConst.EVENT_RELATIONSHIP[4];
		else
		{
			assert false : "TemporalRelationshipPanel.convertRelationshipToString(): Operator '" + operator + "' is not recognized.";
			return null;
		}
	}

	public static final String convertEventMarkerToString( TemporalRelationship.EventMarker marker )
	{
		if ( marker == TemporalRelationship.EventMarker.START_OF )
			return UIConst.EVENT_MARKERS[0];
		else if ( marker == TemporalRelationship.EventMarker.END_OF )
			return UIConst.EVENT_MARKERS[1];
		else
		{
			assert false : "TemporalRelationshipPanel.convertEventMarkerToString(): EventMarker '" + marker + "' is not recognized.";
			return null;
		}
	}

	public static final String convertEventOccurrenceToString( TemporalRelationship.OccurrenceRestriction occ )
	{
		if ( occ == TemporalRelationship.OccurrenceRestriction.FIRST_EVER )
			return UIConst.EVENT_OCCURRENCES[0];
		else if ( occ == TemporalRelationship.OccurrenceRestriction.LAST_EVER )
			return UIConst.EVENT_OCCURRENCES[1];
		else if ( occ == TemporalRelationship.OccurrenceRestriction.ANY )
			return UIConst.EVENT_OCCURRENCES[2];
		else
		{
			assert false : "TemporalRelationshipPanel.convertEventOccurrenceToString(): OccurrenceRestriction '" + occ + "' is not recognized.";
			return null;
		}
	}

	public static final String convertOperatorToString( OrderedDuration.Operator operator )
	{
		if ( operator == OrderedDuration.Operator.LT )
			return UIConst.TEMPORAL_OPERATORS[4];
		else if ( operator == OrderedDuration.Operator.LTE)
			return UIConst.TEMPORAL_OPERATORS[3];
		else if ( operator == OrderedDuration.Operator.E)
			return UIConst.TEMPORAL_OPERATORS[2];
		else if ( operator == OrderedDuration.Operator.GTE )
			return UIConst.TEMPORAL_OPERATORS[1];
		else if ( operator == OrderedDuration.Operator.GT )
			return UIConst.TEMPORAL_OPERATORS[0];
		else
		{
			assert false : "TemporalRelationshipPanel.convertOperatorToString(): Operator '" + operator + "' is not recognized.";
			return null;
		}
	}

	private int getComboIndexByString( Combo combo, String itemName )
	{
		int index = -1;
		for ( String item : combo.getItems() )
		{
			index++;
			if ( item.equals( itemName ))
				return index;			
		}
		return -1;
	}
	
	private void attachListeners() 
	{
		myCloseLabel.addMouseListener( new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {}
			public void mouseUp(MouseEvent e) 
			{
				myManager.removePanel( TemporalRelationshipPanel.this );
			}
		});

		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			myCloseLabel.addMouseTrackListener( new MouseTrackListener()
			{
				@Override
				public void mouseEnter(MouseEvent e) 
				{ myCloseLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE ) ); }
				@Override
				public void mouseExit(MouseEvent e) 
				{ myCloseLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE_DISABLED ) ); }
				@Override
				public void mouseHover(MouseEvent e) {}
			});
		}

		byButton.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  {	
				  updateIntervalWidgets();
				  setDataValuesFromUI();
				  myManager.temporalRelationshipEdited( TemporalRelationshipPanel.this ); // tell manager that our content has been edited
			  }
		});
		
		andButton.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  {	
				  updateIntervalWidgets();
				  setDataValuesFromUI();
				  myManager.temporalRelationshipEdited( TemporalRelationshipPanel.this ); // tell manager that our content has been edited
			  }
		});
		
		SelectionAdapter dataUpdater = new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{ 
				setDataValuesFromUI();
				myManager.temporalRelationshipEdited( TemporalRelationshipPanel.this ); // tell manager that our content has been edited
			}
		};
		
		topEventMarkerCombo.addSelectionListener( dataUpdater );
		topEventOccurrenceCombo.addSelectionListener( dataUpdater );
		topEventCombo.addSelectionListener( dataUpdater );
		eventRelationshipCombo.addSelectionListener( dataUpdater );
		botEventMarkerCombo.addSelectionListener( dataUpdater );
		botEventOccurrenceCombo.addSelectionListener( dataUpdater );
		botEventCombo.addSelectionListener( dataUpdater );
				
		topOperatorCombo.addSelectionListener( dataUpdater );
		topTimeUnitCombo.addSelectionListener( dataUpdater );
		topSpinner.addSelectionListener( dataUpdater );
		
		botOperatorCombo.addSelectionListener( dataUpdater );		
		botTimeUnitCombo.addSelectionListener( dataUpdater );
		botSpinner.addSelectionListener( dataUpdater );
	}
		
	private void updateIntervalWidgets()
	{
		if ( byButton.getSelection() )
		{
			topOperatorCombo.setEnabled( true );
			topSpinner.setEnabled( true );
			topTimeUnitCombo.setEnabled( true );
			andButton.setEnabled( true );
			if ( andButton.getSelection() )
			{
				botOperatorCombo.setEnabled( true );
				botSpinner.setEnabled( true );
				botTimeUnitCombo.setEnabled( true );
			}
			else
			{
				botOperatorCombo.setEnabled( false );
				botSpinner.setEnabled( false );
				botTimeUnitCombo.setEnabled( false );
			}
		}
		else
		{
			andButton.setEnabled( false );
			andButton.setSelection( false );
			topOperatorCombo.setEnabled( false );
			topSpinner.setEnabled( false );
			topTimeUnitCombo.setEnabled( false );			
			botOperatorCombo.setEnabled( false );
			botSpinner.setEnabled( false );
			botTimeUnitCombo.setEnabled( false );
		}
	}
	
	// update myData using values currently in the UI widgets
	public void setDataValuesFromUI()
	{		
		this.myData.setTopEvent( myManager.getEventByName( this.topEventCombo.getText() ));
		this.myData.setTopOccurrenceRestriction( this.topEventOccurrenceCombo.getText() );
		this.myData.setTopEventMarker( this.topEventMarkerCombo.getText() );
		
		this.myData.setOperator( this.eventRelationshipCombo.getText() );
		
		this.myData.setBotEvent( myManager.getEventByName( this.botEventCombo.getText() ));
		this.myData.setBotOccurrenceRestriction( this.botEventOccurrenceCombo.getText() );
		this.myData.setBotEventMarker( this.botEventMarkerCombo.getText() );
		
		if ( byButton.getSelection() ) // byButton is checked
		{
			this.myData.setDuration1( topOperatorCombo.getText(), topSpinner.getSelection(), topTimeUnitCombo.getText() );
			if ( andButton.getSelection() ) // andButton is checked
				this.myData.setDuration2( botOperatorCombo.getText(), botSpinner.getSelection(), botTimeUnitCombo.getText() );
			else
				this.myData.resetDuration2();
		}
		else
		{
			this.myData.resetDuration1();
			this.myData.resetDuration2();
		}
		autoUpdateColorLabels();		
	}
	
	public void autoUpdateColorLabels()
	{
		// set color labels for selected Events
		Event topEvent = this.myData.getTopEvent();
		if ( topEvent != null )
			this.myTopColorLabel.setBackground( Colors.getEventColor( topEvent ));
		else
			this.myTopColorLabel.setBackground( Colors.GRAY );

		Event botEvent = this.myData.getBotEvent();
		if ( botEvent != null )
			this.myBotColorLabel.setBackground( Colors.getEventColor( botEvent ));
		else
			this.myBotColorLabel.setBackground( Colors.GRAY );
	}
	
	/*
	 * Update the event combos with a list of Events
	 */
	public void updateEventList( ArrayList<Event> events )
	{
		String topSelection = null;
		String botSelection = null;
		
		if ( topEventCombo.getSelectionIndex() > -1 )
			topSelection = topEventCombo.getText();
		if ( botEventCombo.getSelectionIndex() > -1 )
			botSelection = botEventCombo.getText();
		
		topEventCombo.removeAll();
		botEventCombo.removeAll();
		int topIndex = -1;
		int botIndex = -1;
		for ( int i = 0; i < events.size(); i++ )
		{
			Event event = events.get(i);
			topEventCombo.add( event.getName() );
			botEventCombo.add( event.getName() );
			if ( topSelection != null && event.getName().equals( topSelection ) )
				topIndex = i;
			if ( botSelection != null && event.getName().equals( botSelection ) )
				botIndex = i;
		}
		
		// select previously-selected Events. If not found, select the first event 
		if ( topIndex > -1 )
			topEventCombo.select( topIndex );
		else
			topEventCombo.select( 0 );
		
		if ( botIndex > -1 )
			botEventCombo.select( botIndex );
		else
		{
			if ( events.size() == 1 )
				botEventCombo.select( 0 );
			else if ( topEventCombo.getSelectionIndex()+1 < events.size() )
				botEventCombo.select( topEventCombo.getSelectionIndex()+1 );
			else
				botEventCombo.select( topEventCombo.getSelectionIndex()-1 );
		}
		// set the events so colors can be appropriated changged
		this.myData.setTopEvent( myManager.getEventByName( this.topEventCombo.getText() ));
		this.myData.setBotEvent( myManager.getEventByName( this.botEventCombo.getText() ));
	}
	
	public void renameEvents( HashMap<String, String> nameMap )
	{
		int oldTopComboIndex = topEventCombo.getSelectionIndex();
		int oldBotComboIndex = botEventCombo.getSelectionIndex();
		
		for ( int i = 0; i < topEventCombo.getItemCount(); i++ )
		{
			String oldName 	= topEventCombo.getItem(i);
			String newName	= nameMap.get( oldName ); 
			if ( newName != null ) // if there is a name change
			{
				topEventCombo.remove(i);
				topEventCombo.add(newName, i);
			}
		}
		for ( int i = 0; i < botEventCombo.getItemCount(); i++ )
		{
			String oldName 	= botEventCombo.getItem(i);
			String newName	= nameMap.get( oldName ); 
			if ( newName != null ) // if there is a name change
			{
				botEventCombo.remove(i);
				botEventCombo.add(newName, i);				
			}
		}
		topEventCombo.select( oldTopComboIndex );
		botEventCombo.select( oldBotComboIndex );
	}
	
	public TemporalRelationship getTemporalRelationship()
	{ return this.myData; }
	
	public boolean isEmpty()
	{
		return (this.topEventCombo.getText().equals("") && this.botEventCombo.getText().equals("")); 
	}
	
	
	@Override
	protected void setActive(boolean flag) 
	{
		leftComp.setBackground( Colors.GRAY );
		
		topEventMarkerCombo.setEnabled( flag );
		topEventOccurrenceCombo.setEnabled( flag );
		topEventCombo.setEnabled( flag );
		eventRelationshipCombo.setEnabled( flag );
		botEventMarkerCombo.setEnabled( flag );
		botEventOccurrenceCombo.setEnabled( flag );
		botEventCombo.setEnabled( flag );
		
		byButton.setEnabled( flag );
		andButton.setEnabled( flag && byButton.getSelection() );
		
		topOperatorCombo.setEnabled( flag && byButton.getSelection() );
		topSpinner.setEnabled( flag && byButton.getSelection() );
		topTimeUnitCombo.setEnabled( flag && byButton.getSelection() );
		
		botOperatorCombo.setEnabled( flag && andButton.getSelection() );
		botSpinner.setEnabled( flag && andButton.getSelection() );
		botTimeUnitCombo.setEnabled( flag && andButton.getSelection() );

	}

	@Override /* override default swt dispose method */
	public void dispose()
	{
		super.dispose();
		myManager 	= null;
		myData		= null;
	}

	
	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		TemporalRelationshipPanel gp = new TemporalRelationshipPanel( myShell, SWT.None );
		gp.setLayoutData( FormDataMaker.makeFullFormData() );
		
		myShell.setSize( 450, 220 );
		
		myShell.open();
		System.err.println( "TemporalRelationshipPanel isEmpty " + gp.isEmpty() );
		
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
