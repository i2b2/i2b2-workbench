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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Event;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IGroupBindingPolicyProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.GroupBindingSelectionListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.VerticalLabel;

public class GroupBindingControlPanel extends QueryToolPanelComposite implements UIConst, UIManagerContentChangedListener
{
	private static final int MAX_SCROLLER_HEIGHT = 200; 
	
		private Composite 	titleComp;
		private Label 		titleLabel;
		
		private Label		myHelpLabel;
		
	private Composite 	myMainComp;
	
	private Composite 			myLabelCom;
	
	private ScrolledComposite 	myScroller;
		private Composite		mySelectionComp;
	
	// Row UI elements
	private ArrayList<GroupBindingControlRow> myRows;
	
	public GroupBindingControlPanel(Composite parent, int style ) 
	{
		super(parent, style);
		myRows = new ArrayList<GroupBindingControlRow>();
		
		setupUI();
		attachListeners( );
	}

	private void setupUI() 
	{
		this.setLayout( new FormLayout() );
		
		titleComp = new Composite( this, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		
		// add help label first so it shows up on top of title
		myHelpLabel = new Label( titleComp, SWT.None );
		myHelpLabel.setBackground( myHelpLabel.getParent().getBackground() );
		myHelpLabel.setImage( Images.getImageByKey( Images.HELP_INACTIVE ) );
		myHelpLabel.setLayoutData( FormDataMaker.makeFormData( 0, 0, (Integer)null, 0, (Integer)null, 0, 100, 0) );

		
		titleLabel = new Label( titleComp, SWT.None );
		titleLabel.setText( GROUP_BOUNDED_BY );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 50, -titleSize.x/2, (Integer)null, 0));
		
		// set colors of title		
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );
				
		myMainComp = new Composite( this, SWT.NONE );
		myMainComp.setLayout( new FormLayout() );
		myMainComp.setLayoutData( FormDataMaker.makeFormData( titleComp, 100, 0, 100) );
		
		myLabelCom = new Composite( myMainComp, SWT.BORDER );
		myLabelCom.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100));
		myLabelCom.setLayout( new FormLayout() );
		
		// compute how big a checkbox is
		Shell shell = new Shell(this.getShell(), SWT.NO_TRIM | SWT.NO_BACKGROUND );
		shell.setBackground( Colors.OFF_WHITE ); // use off-white as the transparent background color
		
		Button button = new Button(shell, SWT.CHECK | SWT.NO_BACKGROUND  );
		button.setBackground( Colors.OFF_WHITE );
		button.setLocation(0, 0);
		
		Point checkboxSize = button.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		
		button.dispose();	// release OS resources
		shell.dispose();	// release OS resources

		Label label1 = new Label( myLabelCom, SWT.NONE	);
		label1.setText( OBSERVATION );
		
		Point labelSize		= label1.computeSize(SWT.DEFAULT, SWT.DEFAULT );
		
		GroupBindingControlLabel canvas1 = new GroupBindingControlLabel( myLabelCom, SWT.NONE, checkboxSize.x, labelSize.y );		
		GroupBindingControlLabel canvas2 = new GroupBindingControlLabel( myLabelCom, SWT.NONE, checkboxSize.x, labelSize.y );
		GroupBindingControlLabel canvas3 = new GroupBindingControlLabel( myLabelCom, SWT.NONE, checkboxSize.x, labelSize.y );
		
		Label label2 = new Label( myLabelCom, SWT.NONE );
		label2.setText( ENCOUNTER );

		Label label3 = new Label( myLabelCom, SWT.NONE );
		label3.setText( BOUND_BY_PATIENT );
		
		FormData canvas1FD	= FormDataMaker.makeFormData(0, 0, (Integer)null, 0, 0, 0, 0, checkboxSize.x);
		canvas1FD.height	= labelSize.y * 3;
		FormData canvas2FD	= FormDataMaker.makeFormData(label1, 0, (Integer)null, 0, canvas1, 0, 0, checkboxSize.x*2);
		canvas2FD.height	= labelSize.y * 2;
		FormData canvas3FD	= FormDataMaker.makeFormData(label2, 0,(Integer)null, 0 , canvas2, 0, 0, checkboxSize.x*3);
		canvas3FD.height	= labelSize.y ;

		canvas1.setLayoutData( canvas1FD );
		canvas2.setLayoutData( canvas2FD );
		canvas3.setLayoutData( canvas3FD );
		
		label1.setLayoutData( FormDataMaker.makeFormData(0, 0, (Integer)null, 0, canvas1, 0, (Integer)null, 0) );		
		label2.setLayoutData( FormDataMaker.makeFormData( label1,		 0, 
														  (Integer)null, 0,
														  canvas2,		 0,
														  (Integer)null, 0));
		
		label3.setLayoutData( FormDataMaker.makeFormData( label2,		 0, 
														  (Integer)null, 0,
														  canvas3,		 0,
														  (Integer)null, 0));

		
		myScroller = new ScrolledComposite( myMainComp, SWT.V_SCROLL | SWT.H_SCROLL);
		myScroller.setLayout( new FormLayout() );
		FormData data = FormDataMaker.makeFormData( myLabelCom, 0, (Integer)null, 0, 0, 0, 100, 0);
		data.height = MAX_SCROLLER_HEIGHT;
		myScroller.setLayoutData( data );
		
		mySelectionComp = new Composite( myScroller, SWT.None );
		mySelectionComp.setLayoutData( FormDataMaker.makeFullFormData() );
		mySelectionComp.setLayout( new FormLayout() );
		//mySelectionComp.setBackground( Colors.DARK_RED ); //bugbug coloring for debug
		
		myScroller.setContent( mySelectionComp );
		myScroller.setExpandHorizontal( true );
		myScroller.setExpandVertical( true );
	}

	private void attachListeners( )
	{
		// show help when clicked
		myHelpLabel.addMouseListener( new MouseAdapter()
		{			
			public void mouseDown(MouseEvent e) 
			{
				GroupBindingInfoDialog gbid = new GroupBindingInfoDialog( SWT.NONE );
				Point preferred = gbid.getPreferredSize();
				Point startingPoint = GroupBindingControlPanel.this.toDisplay( 0, 0 );
				startingPoint.x = Math.max(5, startingPoint.x);
				startingPoint.y = Math.max(5, startingPoint.y);
				startingPoint.x = Math.min( Display.getCurrent().getBounds().width - preferred.x - 5, startingPoint.x);
				startingPoint.y = Math.min( Display.getCurrent().getBounds().height - preferred.y - 5, startingPoint.y);
				gbid.setLocation( startingPoint );
				gbid.open();
			}
		});
		
		// change icon on mousing-in/out
		myHelpLabel.addMouseTrackListener( new MouseTrackListener()
		{
			@Override
			public void mouseEnter(MouseEvent e) 
			{ myHelpLabel.setImage( Images.getImageByKey( Images.HELP_ACTIVE ) ); }
			@Override
			public void mouseExit(MouseEvent e) 
			{ myHelpLabel.setImage( Images.getImageByKey( Images.HELP_INACTIVE ) ); }
			@Override
			public void mouseHover(MouseEvent e) {}
		});
	}
		
	public void initializeWithEvent( Event event )
	{
		synchUIWithGroupData( event.getGroups() );
	}

	public void autoSetCheckBoxes()
	{
		for ( GroupBindingControlRow row : myRows )
			row.autoEnableCheckboxes();		
	}
	
	public void synchUIWithGroupData( final ArrayList<Group> groups )
	{
		/* Remove all existing GroupBindingControlRows */
		for ( GroupBindingControlRow row : myRows )
			row.dispose();
		myRows.clear();
		
		Control previousControl  = null;
		for ( int i = 0; i < groups.size(); i++ )
		{
			GroupBindingControlRow row = new GroupBindingControlRow( mySelectionComp, SWT.BORDER , groups.get(i) );
			if ( previousControl == null )
				row.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100));
			else
				row.setLayoutData( FormDataMaker.makeFormData(previousControl, (Integer)null, 0, 100));
			myRows.add( row );
			previousControl = row;
		}
		int height = 0;
		if ( previousControl != null )
			height = previousControl.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
		height =  Math.min( MAX_SCROLLER_HEIGHT, height * groups.size() );

		mySelectionComp.layout();	// force mySelectionComp to relayout so when number of Rows grow from 0 to 1, the panel would layout correctly
		// make sure scroller's size (height) and scroller's minHeight are properly adjusted 
		((FormData)myScroller.getLayoutData()).height = height;
		myScroller.setMinHeight(mySelectionComp.computeSize( SWT.DEFAULT, SWT.DEFAULT).y ); 	
		this.getParent().layout();	// relayout this component's parent so this component and its siblings are properly laid out

		//System.err.println("GroupBindingControlpanel.synchUIWithGroupData: Synching. height = " + height + " minHeight = " + mySelectionComp.computeSize( SWT.DEFAULT, SWT.DEFAULT).y );		
	}
	
	
	@Override /* QueryToolPanelComposite method */
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
		myHelpLabel.setBackground( myHelpLabel.getParent().getBackground() ); // make sure the help label has the same color as its parent
	}
	
	@Override /* UIManagerContentChangedListener method */
	public void groupManagerContentChanged( Object source ) 
	{
		GroupManager gm = (GroupManager)source;
		final ArrayList<Group> groups = gm.makeGroupData();
		synchUIWithGroupData( groups );
	}
		
}
