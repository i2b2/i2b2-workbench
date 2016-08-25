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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.DataConst;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;

public class EnumValueRestrictionEditorPanel extends ValueRestrictionEditorPanel implements UIConst
{

	protected Composite	myNoValuePanel;
	protected Composite myFlagValuePanel;
	protected Composite myEnumValuePanel;
	
	protected Button	myNoValueButton;
	protected Button	myFlagValueButton;
	protected Button	myEnumValueButton;
	
	protected Label		myExplanation;
	
	protected TableViewer myTableViewer;
	
	
	public EnumValueRestrictionEditorPanel(Composite parent, int style, QueryConceptTreeNodeData data) 
	{
		super(parent, SWT.BORDER | style);
		myNodeData = data;
		myValueRestriction = myNodeData.valuePropertyData().makeCopy();
		setupUI();
		attachListeners();
	}
	
	/* Constructor for testing only*/
	private EnumValueRestrictionEditorPanel(Composite parent, int style) 
	{
		super(parent, style | SWT.BORDER );
		myNodeData 			= null;
		myValueRestriction 	= null;
		setupUI();
		attachListeners();
	}

	
	private void setupUI() 
	{
		this.setLayoutData( FormDataMaker.makeFullFormData() );
		
		this.setLayout(new FormLayout());
		Composite labelComp = new Composite( this, SWT.NONE );
		labelComp.setBackground( Colors.WHITE );
		labelComp.setLayout( new FormLayout() );
		labelComp.setLayoutData( FormDataMaker.makeFormData( 0, (Integer)null, 0, 100));
		Label myInstructions = new Label( labelComp, SWT.LEFT | SWT.WRAP | SWT.HORIZONTAL);
		myInstructions.setText("You can constrain " + getNodeName() + " by a High/Low Flag or a specific Text Value. Default is 'No Value.'");
		myInstructions.setBackground( Colors.WHITE);
		myInstructions.setLayoutData( FormDataMaker.makeFormData( 0, 2, 100, -2, 0, 2, 100, -2));
		
		Composite buttonsComp 	= new Composite( this, SWT.NONE );
		Composite widgetComp	= new Composite( this, SWT.NONE );
		buttonsComp.setLayout( new FormLayout() );
		widgetComp.setLayout( new FormLayout() );
		buttonsComp.setLayoutData( FormDataMaker.makeFormData( labelComp, 2, 100, 0, 0, 0, 45, 0) );
		widgetComp.setLayoutData( FormDataMaker.makeFormData( labelComp, 2, 100, 0, buttonsComp, 0, 100, 0) );
		
		myNoValueButton = new Button( buttonsComp, SWT.RADIO );
		myNoValueButton.setText( NO_VALUE );
		myFlagValueButton = new Button( buttonsComp, SWT.RADIO );
		myFlagValueButton.setText( ABNORMAL_FLAG_VALUE );
		myEnumValueButton = new Button( buttonsComp, SWT.RADIO );
		myEnumValueButton.setText( ENUM_VALUE );
		
		myNoValueButton.setLayoutData( FormDataMaker.makeFormData( 10, 0, (Integer)null, 0, 10, 0, 100, -10 ));
		myFlagValueButton.setLayoutData( FormDataMaker.makeFormData( myNoValueButton, 0, (Integer)null, 0, 10, 0, 100, -10 ));
		myEnumValueButton.setLayoutData( FormDataMaker.makeFormData( myFlagValueButton, 0, (Integer)null, 0, 10, 0, 100, -10 ));

		myNoValuePanel 		= makeNoValuePanel( widgetComp, getNodeName() );		
		myFlagValuePanel 	= makeFlagValuePanel( widgetComp, getNodeName() );
		myEnumValuePanel 	= makeEnumValuePanel( widgetComp, getNodeName() );
		
		myExplanation = new Label( buttonsComp, SWT.WRAP );
		myExplanation.setBackground( Colors.WHITE );
		myExplanation.setLayoutData( FormDataMaker.makeFormData( myEnumValueButton, 30, 100, -10, 0, 10, 100, 0));
		
		// set defaults
		initUIWithData();
		autoSetPanelVisibilityAndExplanation();
	}

	private Composite makeNoValuePanel(Composite parent, String nodeName) 
	{
		Composite comp = new Composite( parent, SWT.NONE );
		comp.setLayout( new FormLayout() );
		comp.setLayoutData( FormDataMaker.makeFullFormData() );
		return comp;
	}
	
	private Composite makeFlagValuePanel(Composite parent, String nodeName) 
	{
		Composite comp = new Composite( parent, SWT.NONE );
		comp.setLayout( new FormLayout() );
		comp.setLayoutData( FormDataMaker.makeFullFormData() );
		return comp;
	}
	
	private Composite makeEnumValuePanel(Composite parent, String nodeName) 
	{
		Composite comp = new Composite( parent, SWT.NONE );
		comp.setLayout( new FormLayout() );
		comp.setLayoutData( FormDataMaker.makeFullFormData() );
		
		Label instruction = new Label( comp, SWT.WRAP );
		instruction.setText("Select Desired Values:");
		instruction.setLayoutData( FormDataMaker.makeFormData( 10, -instruction.computeSize( SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 4, 100, -4) );
		
		myTableViewer = new TableViewer( comp, SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION );
		myTableViewer.getTable().setLayoutData( FormDataMaker.makeFormData( instruction, 2, 100, -10, 0, 4, 100, -4) );

		if ( myValueRestriction != null)
			for ( String enumName : myValueRestriction.enumValues )
			{
				TableItem item = new TableItem( myTableViewer.getTable(), SWT.NONE);
				item.setText( enumName ); // text IS data
			}
		else // bugbug: for testing only
		    for (int i = 0; i < 12; i++) 
		    {
		    	TableItem item = new TableItem( myTableViewer.getTable(), SWT.NONE);
		    	item.setText("Item " + i);
		    }
		return comp;
	}

	private void initUIWithData() 
	{
		if (myValueRestriction.hasEnumValue()) 
		{
			if (myValueRestriction.useValueFlag()) 
				myFlagValueButton.setSelection( true );
			else if (myValueRestriction.useTextValue()) 
			{
				myEnumValueButton.setSelection(true);
				// set selected values to be 'checked'
				for ( TableItem item : myTableViewer.getTable().getItems() )
					for (String selectedValue : myValueRestriction.selectedValues) 
						if (selectedValue.equals( item.getText() ))
							item.setChecked( true );
			}
			else
				myNoValueButton.setSelection( true );
		}
	}

	
	private void autoSetPanelVisibilityAndExplanation() 
	{
		if ( myNoValueButton.getSelection() )
		{
			myNoValuePanel.setVisible( true );
			myFlagValuePanel.setVisible( false );
			myEnumValuePanel.setVisible( false);
			
			myExplanation.setText( "All '" + getNodeName() + "' will be included in the search results." );
		}
		else if ( myFlagValueButton.getSelection() )
		{
			myNoValuePanel.setVisible( false );
			myFlagValuePanel.setVisible( true );
			myEnumValuePanel.setVisible( false );
			myExplanation.setText( "Only '" + getNodeName() + "' that has an abnormal flag will be included in the search results." );
		}
		else if ( myEnumValueButton.getSelection() )
		{
			myNoValuePanel.setVisible( false );
			myFlagValuePanel.setVisible( false );
			myEnumValuePanel.setVisible( true );
			myExplanation.setText( "Only '" + getNodeName() + "' that has one of the selected values will be included in the search results." );
		}
		else
			assert false: "EnumValueRestrictionEditorPanel.autoSetPanelVisibilityAndExplanation(): Button state: {NoValue=false, FlagValue=false, EnumValue=false} is not recognized.";
	}


	private void attachListeners() 
	{
		/* Change UI by button selected */
		SelectionListener buttonSelectedListener = new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent event )
			{  
				if (((Button)event.getSource()).getSelection()) // only apply on button that's been set to true (the one that sets false does not call the autoSet method)
					autoSetPanelVisibilityAndExplanation(); 
			}
		};		
		myNoValueButton.addSelectionListener( buttonSelectedListener );
		myFlagValueButton.addSelectionListener( buttonSelectedListener );
		myEnumValueButton.addSelectionListener( buttonSelectedListener );
		
		/* Change Data by UI changes*/
		SelectionListener dataChangedListener = new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent event )
			{  
				if ( ((Button)event.getSource()).getSelection() ) // call autoSet only if on set-to-true events.
					autoSetValueRestriction(); // change myNodeData					
			}
		};
		myNoValueButton.addSelectionListener( dataChangedListener );
		myFlagValueButton.addSelectionListener( dataChangedListener );
		myEnumValueButton.addSelectionListener( dataChangedListener );

		/* listen to checking/unchecking items */
		myTableViewer.getTable().addListener(SWT.Selection, new Listener()
    	{
			@Override
			public void handleEvent(Event event) 
			{
				TableItem item = (TableItem)event.item;
				if ( event.detail == SWT.NONE )				// if not clicking on the checkbox, manually set the check state
					item.setChecked( !item.getChecked() );
				// if clicking on the checkbox, event.detail would be SWT.CHECK (32), system will automatically set the check state, so we don't.
				autoSetValueRestriction();
			}
		});
	}

	
	private void autoSetValueRestriction()
	{
		if ( myNoValueButton.getSelection() )
		{
			myValueRestriction.noValue(true);
			myValueRestriction.useValueFlag(false);
			myValueRestriction.useNumericValue(false);
			myValueRestriction.useTextValue(false);
			//myNodeData.valueName("");
		}
		else if ( myFlagValueButton.getSelection() )
		{
			myValueRestriction.noValue(false);
			myValueRestriction.useValueFlag(true);
			myValueRestriction.useNumericValue(false);
			myValueRestriction.useTextValue(false);
			myValueRestriction.value( DataConst.ABNORNAL_FLAG_VALUE );
			//myNodeData.valueName( DataConst.ABNORMAL_FLAG_NAME );
		}
		else if ( myEnumValueButton.getSelection() )
		{
			myValueRestriction.noValue(false);
			myValueRestriction.useValueFlag(false);
			myValueRestriction.useNumericValue(false);
			myValueRestriction.useTextValue(true);
			myValueRestriction.selectedValues.clear();
			
			int itemCount = 0; 
			for ( TableItem item : myTableViewer.getTable().getItems())
			{
				if ( item.getChecked() )
				{
					myValueRestriction.selectedValues.add( item.getText() );
					itemCount++;
				}
			}
			if ( itemCount == 0 ) // if no value is selected, treat it as if user selected the No Value Button
			{
				myValueRestriction.noValue(true);
				myValueRestriction.useValueFlag(false);
				myValueRestriction.useNumericValue(false);
				myValueRestriction.useTextValue(false);
				//myNodeData.valueName("");
				return;
			}
			else				
			{
				/*
				myNodeData.valueName(" Is " + myValueRestriction.selectedValues.get(0) );
				if (myNodeData.valuePropertyData().selectedValues.size() > 1) 
				{
					for (int j = 1; j < myNodeData.valuePropertyData().selectedValues.size(); j++) 
						myNodeData.valueName(myNodeData.valueName() + "," + myValueRestriction.selectedValues.get(j) );
				}
				*/
			}			
		}
		else
			assert false: "EnumValueRestrictionEditorPanel.autoSetValueRestriction(): Button state: {NoValue=false, FlagValue=false, EnumValue=false} is not recognized.";
		
		//System.err.println("EnumValueRestrictionEditorPanel.autoSetValueRestriction: data changed: " + myNodeData.toString() );
	}

	/*
	 * main method for testing only
	 */
	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		EnumValueRestrictionEditorPanel gp = new EnumValueRestrictionEditorPanel( myShell, SWT.None );
		gp.setLayoutData( FormDataMaker.makeFullFormData() );
		
		myShell.setSize( 400, 300 );
		
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
