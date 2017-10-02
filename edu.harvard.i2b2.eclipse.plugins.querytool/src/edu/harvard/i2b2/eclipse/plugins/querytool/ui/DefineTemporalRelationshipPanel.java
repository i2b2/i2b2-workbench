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

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IEventDataProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.TemporalRelationship;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.delegator.TemporalRelationshipAddedRemovedDelegator;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class DefineTemporalRelationshipPanel extends Composite implements UIConst, UIManagerContentChangedListener, DataChangedListener
{

	private Composite	myMainComposite;
	private Button		myAddPanelButton;
	private ScrolledComposite myScroller;
	
	private Label		myInstructions;
	
	private TemporalRelationshipManager					myTemporalRelationshipManager;
	
	private ArrayList<DataChangedListener> 				myDataChangedListeners;	// listen for temporal relationship modifications
	private ArrayList<UIManagerContentChangedListener> 	myContentListeners; 	// listen for adding/removing temporal relationships
	
	public DefineTemporalRelationshipPanel( Composite parent, int style, IEventDataProvider eventDataProvider ) 
	{
		super(parent, style);
		myDataChangedListeners 	= new ArrayList<DataChangedListener>();
		myContentListeners		= new ArrayList<UIManagerContentChangedListener>();
		setupUI( eventDataProvider );
		attachListeners();
	}

	private void setupUI( IEventDataProvider eventDataProvider ) 
	{
		this.setLayout( new FormLayout() );		
		
		myScroller = new ScrolledComposite( this, SWT.V_SCROLL | SWT.H_SCROLL);
		myScroller.setLayout( new FormLayout() );
		myScroller.setLayoutData( FormDataMaker.makeFormData( 0, 0, 100, 0, 0, 0, 100, 0) );		
		
			myMainComposite = new Composite( myScroller, SWT.NONE );
			myMainComposite.setLayout( new FormLayout() );
			myMainComposite.setLayoutData( FormDataMaker.makeFullFormData() );
			myMainComposite.setBackground( Colors.DARK_GRAY ); // bugbug color for debugging	
		
		myScroller.setExpandHorizontal( true );
		myScroller.setExpandVertical( true );
		myScroller.setContent( myMainComposite );

		myTemporalRelationshipManager = new TemporalRelationshipManager( myMainComposite, eventDataProvider );
		myTemporalRelationshipManager.addUIContentListener( this );		// listen for adding/removing of temporal relationships
		myTemporalRelationshipManager.addDataChangedListener( this );	// listen for temporal relationship's content editing
		
		
		myAddPanelButton = new Button( myMainComposite, SWT.PUSH );
		try
		{
			myAddPanelButton.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ADD ));
			myAddPanelButton.setText( ADD_TEMPORAL_RELATIONSHIP );
		}
		catch ( IllegalStateException e )
		{
			myAddPanelButton.setText( "+ " + ADD_TEMPORAL_RELATIONSHIP );
		}

		myInstructions = new Label( myMainComposite, SWT.WRAP );
		myInstructions.setLayoutData( FormDataMaker.makeFormData(myAddPanelButton, UIConst.GROUP_PANEL_MARGIN, (Integer)null, -UIConst.GROUP_PANEL_MARGIN, 30, 0, 70, 0) );
		myInstructions.setText( DEFINE_TEMPORAL_RELATIONSHIP_INSTRUCTIONS );
		myInstructions.setForeground( Colors.WHITE );
		myInstructions.setBackground( myInstructions.getParent().getBackground() );
		
		// add one relationship by default
		myTemporalRelationshipManager.addPanels(1);
		//this.myTemporalRelationshipManager.updateEventListInPanels( eventDataProvider.getEvents() );
		updateAddPanelButtonLayout();

		Composite bottomPlaceHolder = new Composite( myMainComposite, SWT.NONE ); 
		bottomPlaceHolder.setLayoutData( FormDataMaker.makeFormData(myInstructions, UIConst.GROUP_PANEL_MARGIN, 100, 0, 0, 0, 100, 0));
		bottomPlaceHolder.setBackground( myMainComposite.getBackground() );

		myScroller.setMinHeight( myMainComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
	}

	private void attachListeners() 
	{
		myAddPanelButton.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent ev )
			{
				myTemporalRelationshipManager.addPanels(1);
			}
		});

	}
	
	private void updateAddPanelButtonLayout() 
	{
		TemporalRelationshipPanel lastPanel = myTemporalRelationshipManager.getLastPanel();
		Point panelSize = myAddPanelButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		if ( lastPanel != null )
			myAddPanelButton.setLayoutData( FormDataMaker.makeFormData( lastPanel, GROUP_PANEL_MARGIN, (Integer)null, -GROUP_PANEL_MARGIN, 50, -panelSize.x/2, (Integer)null, 0 ));
		else
			myAddPanelButton.setLayoutData( FormDataMaker.makeFormData( 0, GROUP_PANEL_MARGIN, (Integer)null, -GROUP_PANEL_MARGIN, 50, -panelSize.x/2, (Integer)null, 0 ));
		if ( myTemporalRelationshipManager.getNumberOfPanels() > Settings.getInstance().getItemCountHelpCutoff() )
			myInstructions.setVisible( false );
		else
			myInstructions.setVisible( true );
		((FormData)myAddPanelButton.getLayoutData()).height =  myAddPanelButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;		
	}

	public ArrayList<TemporalRelationship> getTemporalRelationshipData()
	{ return this.myTemporalRelationshipManager.getTemporalRelationshipData(); }


	public void addTemporalRelationships( ArrayList<TemporalRelationship> temporalRelationships ) 
	{
		this.myTemporalRelationshipManager.addPanels(temporalRelationships);
	}

	// remove all temporal relationship panels
	public void removeAllTemporalRelationships()
	{ this.myTemporalRelationshipManager.removeAllPanels(); }


	/* Dael with UIContentListener */
	public void addUIContentListener( UIManagerContentChangedListener listener )
	{ myContentListeners.add( listener ); }
	public boolean removeListener( UIManagerContentChangedListener listener )
	{ return myContentListeners.remove( listener ); }
	public void removeAllListeners()
	{ myContentListeners = new ArrayList<UIManagerContentChangedListener>(); }
	private void notifyContentChangeListeners() // listeners include: TemporalQueryModePanel
	{
		for ( int i = 0; i < myContentListeners.size(); i++ )
			myContentListeners.get(i).groupManagerContentChanged( this );
	}

	/* Deal with DataChangedListeners */
	public void addDataChangedListener( DataChangedListener list )
	{ this.myDataChangedListeners.add( list ); }		
	public boolean removeDataChangedListener( DataChangedListener list )
	{ return this.myDataChangedListeners.remove( list ); }
	// notify TemporalQueryModePanel that a Temporal Relationship's content has changed
	private void notifyDataChangedListeners( Object source ) 
	{
		for ( DataChangedListener list : myDataChangedListeners)
			list.dataChanged( this );
	}

	public void autoResetEventNames( HashMap<String, String> nameMap )
	{ this.myTemporalRelationshipManager.renameEvents( nameMap ); }


	// reset temporal relationships and update UI
	public void reset()
	{
		this.myTemporalRelationshipManager.removeAllPanels();		
		myTemporalRelationshipManager.addPanels(1);			// add one relationship by default
		updateAddPanelButtonLayout();
		myScroller.setMinHeight( myMainComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
	}

	
	// Update the event combos in TemporalRelationshipPanels when an event is saved 
	// Or if a temopral relationship's conten is changed, notify QueryToolMainUI
	@Override 
	public void dataChanged(Object source) 
	{
		if ( source instanceof EventListManager )
			this.myTemporalRelationshipManager.updateTemporalRelationshipPanels();
		else if ( source == this.myTemporalRelationshipManager ) // A temporal relationship's content has changed
			notifyDataChangedListeners( this );
	}


	/* Either (1) a New TemporalRelationshipPanel is added/removed (if source == this.myTemporalRelationshipManager) 
	 *     or (2) an Event is added/removed:  Need to update the event combos in TemporalRelationshipPanels when an event is saved 
	 * */
	@Override 
	public void groupManagerContentChanged( Object source ) 
	{
		if ( source == this.myTemporalRelationshipManager )
		{
			updateAddPanelButtonLayout();
			myMainComposite.layout();
			myScroller.setMinHeight( myMainComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
			myScroller.redraw();
			notifyContentChangeListeners();
		}
		else if ( source instanceof EventBundle ) // Groups in EventListManager have changed their concepts, update the TemporalRelationshipPanels so they have the right colors
		{
			ArrayList<Event> events = ((EventBundle)source).getEvents();			
			this.myTemporalRelationshipManager.updateEventListInPanels( events );
		}
	}
	
}
