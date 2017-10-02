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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;

/**
 * Supports editing a Value Restriction of a single term
 */
public class SingleValueRestrictionEditorDialog extends AbstractValueRestrictionEditorDialog
{
	/*
	private Shell		myShell;
	private Composite	myMainComp;
	private Point		myInitLocation; 
	
	private Composite	titleComp;
	private Label		titleLabel;
	private Composite	innerComp;
	
	private Composite	myPanelComp;
	*/
	
	private Button		myOKButton;
	private Button		myCancelButton;

	private ValueRestrictionEditorPanel 			myEditor 	= null;
	private QueryConceptTreeNodeData				myNode		= null;

	public SingleValueRestrictionEditorDialog( QueryConceptTreeNodeData node, int styles  )
	{	
		myNode = node;
		setupUI( node, styles );
		attachListeners();
	}

	private void setupUI(QueryConceptTreeNodeData node, int styles) 
	{
		myShell = new Shell( Display.getCurrent(), SWT.APPLICATION_MODAL | styles);
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
		titleLabel.setText( SPECIFY_VALUE_CONSTRAINTS );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 50, -titleSize.x/2, (Integer)null, 0));

		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );
		
		Composite outerComp = new Composite( myMainComp, SWT.BORDER );
		outerComp.setLayout( new FormLayout() );
		outerComp.setLayoutData( FormDataMaker.makeFormData( titleComp,100, 0, 100) );
		outerComp.setBackground( Colors.ORANGE );

		innerComp = new Composite( outerComp, SWT.BORDER );
		innerComp.setLayout( new FormLayout() );
		innerComp.setLayoutData( FormDataMaker.makeBorderingFormData() );

		Composite buttonsComp = new Composite( innerComp, SWT.NONE );
			FormData buttonsCompFD = FormDataMaker.makeFormData( (Integer)null, 100, 0, 100);
			buttonsCompFD.height = UIConst.DECISION_HEIGHT;
		buttonsComp.setBackground( Colors.BLACK );
		buttonsComp.setLayoutData( buttonsCompFD );
		buttonsComp.setLayout( new FormLayout() );
		
		myPanelComp = new Composite( innerComp, SWT.NONE );
		myPanelComp.setLayout( new FormLayout() );
		myPanelComp.setLayoutData( FormDataMaker.makeFormData(0, buttonsComp, 0, 100) );
		
		myCancelButton = new Button( buttonsComp, SWT.PUSH );
		myCancelButton.setText( CANCEL );
		myCancelButton.setLayoutData( FormDataMaker.makeFormData(50, -myCancelButton.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 50, 4, (Integer)null, 0 ) );

		myOKButton = new Button( buttonsComp, SWT.PUSH );
		myOKButton.setText( OK );
		myOKButton.setLayoutData( FormDataMaker.makeFormData(50, -myOKButton.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 50, -myCancelButton.computeSize(SWT.DEFAULT, SWT.DEFAULT).x-4, 50, -4 ) );

		
		myEditor =  ValueRestrictionEditorFactory.getInstance().makeEditor(myPanelComp, myNode );	
	}

	private void attachListeners() 
	{
		myOKButton.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent ev )
			{
				myEditor.saveValueRestriction();
				myShell.close();
				myShell.dispose(); // terminate this dialog
			}
		});
		
		myCancelButton.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent ev )
			{
				myShell.close();
				myShell.dispose(); // terminate this dialog
			}
		});

	}

	public boolean hasContent()
	{ return myEditor != null; }

	
	public void setLocation( Point location )
	{ myInitLocation = location; }

	// open the dialog
	public void open()
	{
		if ( this.myInitLocation != null )
			myShell.setLocation( this.myInitLocation );
		myShell.setSize( new Point(400, 400) );
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
		SingleValueRestrictionEditorDialog dcd = new SingleValueRestrictionEditorDialog( null, SWT.CLOSE | SWT.RESIZE);
		dcd.open();
	}

}
