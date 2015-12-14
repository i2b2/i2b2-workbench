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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.DataConst;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;

public class TextBlobValueRestrictionEditorPanel extends ValueRestrictionEditorPanel implements UIConst
{
	
	protected Composite	myNoValuePanel;
	protected Composite myFlagValuePanel;
	protected Composite myTextValuePanel;
	
	protected Button	myNoValueButton;
	protected Button	myFlagValueButton;
	protected Button	myTextValueButton;
	protected Combo		myTextOpsCombo;
	
	protected Label		myExplanation;
	
	protected Text		myTextInput;
	protected Button	myUseDBOpsButton;
	protected Label		myUseDBOpsLabel;
	
	
	public TextBlobValueRestrictionEditorPanel(Composite parent, int style, QueryConceptTreeNodeData data) 
	{
		super(parent,  SWT.BORDER | style);
		myNodeData = data;
		myValueRestriction = myNodeData.valuePropertyData().makeCopy();
		setupUI();
		attachListeners();
	}


	/* Constructor for testing only*/
	private TextBlobValueRestrictionEditorPanel(Composite parent, int style) 
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
		String labelText = "You can constrain " + getNodeName() + " by ";
		if ( myValueRestriction.isLongText() ) // long text means no flag value allowed
			labelText = labelText + "a specific Text Value";
		else
			labelText = labelText + "an Abnormal Flag or a specific Text Value";
		labelText = labelText + ". Default is 'No Value.'";
		
		myInstructions.setText( labelText );
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
		myTextValueButton = new Button( buttonsComp, SWT.RADIO );
		myTextValueButton.setText( ENUM_VALUE );
		
		myNoValueButton.setLayoutData( FormDataMaker.makeFormData( 10, 0, (Integer)null, 0, 10, 0, 100, -10 ));
		myFlagValueButton.setLayoutData( FormDataMaker.makeFormData( myNoValueButton, 0, (Integer)null, 0, 10, 0, 100, -10 ));
		myTextValueButton.setLayoutData( FormDataMaker.makeFormData( myFlagValueButton, 0, (Integer)null, 0, 10, 0, 100, -10 ));
		
		myNoValuePanel 		= makeNoValuePanel( widgetComp, getNodeName() );		
		myFlagValuePanel 	= makeFlagValuePanel( widgetComp, getNodeName() );
		myTextValuePanel 	= makeTextValuePanel( widgetComp, getNodeName() );
		
		myExplanation = new Label( buttonsComp, SWT.WRAP );
		myExplanation.setBackground( Colors.WHITE );
		myExplanation.setLayoutData( FormDataMaker.makeFormData( myTextValueButton, 30, 100, -10, 0, 10, 100, 0));
		
		
		if (myValueRestriction.isLongText())
		{
			myFlagValueButton.setEnabled(false);
		}
		else
		{
			myUseDBOpsButton.setVisible(false);
			myUseDBOpsLabel.setVisible(false);
		}
		// set defaults
		initUIWithData();		
		autoSetPanelVisibilityAndExplanation();
		if (!(UserInfoBean.getInstance().isRoleInProject("DATA_DEID")))
			myTextValueButton.setEnabled(false);
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

	private Composite makeTextValuePanel(Composite parent, String nodeName) 
	{
		Composite comp = new Composite( parent, SWT.NONE );
		comp.setLayout( new FormLayout() );
		comp.setLayoutData( FormDataMaker.makeFullFormData() );

		Label instruction = new Label( comp, SWT.WRAP );
		instruction.setText( ENTER_TEXT_SEARCH_TERM );
		instruction.setLayoutData( FormDataMaker.makeFormData( 10, -instruction.computeSize( SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 4, 100, -4) );
		
		myTextOpsCombo = new Combo( comp, SWT.READ_ONLY | SWT.DROP_DOWN );
		int selectedIndex = 0;
		boolean hasLongText = false;
		if ( myValueRestriction.isLongText() )
		{
			hasLongText = true;					// if it's long text, we hide the textOpsCombo
			myTextOpsCombo.add( CONTAINING );
		}
		else
			for ( String op : TEXT_OPERATORS )
				myTextOpsCombo.add( op );
		
		myTextInput = new Text( comp, SWT.SINGLE );
		myTextInput.setText("");
		
		if ( hasLongText ) // if it's long text, the only option is "CONTAINING," and so we do not display the combo box
		{
			myTextOpsCombo.setVisible( false );
			myTextInput.setLayoutData( FormDataMaker.makeFormData( instruction, 2, (Integer)null, 0, 0, 4, 100, -4) );
		}
		else
		{
			myTextOpsCombo.setLayoutData( FormDataMaker.makeFormData( instruction, 2, (Integer)null, 0, 0, 4, 100, -4) );
			myTextInput.setLayoutData( FormDataMaker.makeFormData(myTextOpsCombo, 2, (Integer)null, 0, 0, 4, 100, -4) );
		}		
		
		myUseDBOpsButton = new Button( comp, SWT.CHECK );
		myUseDBOpsButton.setText( "" );
		myUseDBOpsButton.setLayoutData( FormDataMaker.makeFormData( myTextInput, 2, (Integer)null, 0, 0, 4, (Integer)null,  0 ));
		myUseDBOpsLabel = new Label( comp, SWT.WRAP );
		myUseDBOpsLabel.setText( USE_DB_OPS );
		myUseDBOpsLabel.setLayoutData( FormDataMaker.makeFormData( myTextInput, 2, (Integer)null, 0, myUseDBOpsButton, 2, 100, -4 ) );
		
		myTextOpsCombo.select( selectedIndex );
		return comp;
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
		myTextValueButton.addSelectionListener( buttonSelectedListener );
		
		/* Change Data by UI changes*/
		SelectionListener dataChangedListener = new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent event )
			{  
				Object source = event.getSource();
				if ( source != myNoValueButton && source != myFlagValueButton && source != myTextValueButton )
					autoSetValueRestriction(); // change myNodeData
				else if ( ((Button)event.getSource()).getSelection() )
					autoSetValueRestriction(); // change myNodeData					
			}
		};		
		myNoValueButton.addSelectionListener( dataChangedListener );
		myFlagValueButton.addSelectionListener( dataChangedListener );
		myTextValueButton.addSelectionListener( dataChangedListener );
		myTextOpsCombo.addSelectionListener( dataChangedListener );
		myUseDBOpsButton.addSelectionListener( dataChangedListener );		
		
		/* Modification to content of Text triggers changing myNodeData as well */
	    ModifyListener textModifyListener = new ModifyListener()
	    {
	         public void modifyText(ModifyEvent arg0) 
	         {
	        	 autoSetValueRestriction(); // change myNodeData       
	         }
	    };
	    myTextInput.addModifyListener(textModifyListener);
	}
	
	private void autoSetPanelVisibilityAndExplanation() 
	{
		if ( myNoValueButton.getSelection() )
		{
			myNoValuePanel.setVisible( true );
			myFlagValuePanel.setVisible( false );
			myTextValuePanel.setVisible( false);
			
			myExplanation.setText( "All '" + getNodeName() + "' will be included in the search results." );
		}
		else if ( myFlagValueButton.getSelection() )
		{
			myNoValuePanel.setVisible( false );
			myFlagValuePanel.setVisible( true );
			myTextValuePanel.setVisible( false );
			myExplanation.setText( "Only '" + getNodeName() + "' that has an abnormal flag will be included in the search results." );
		}
		else if ( myTextValueButton.getSelection() )
		{
			myNoValuePanel.setVisible( false );
			myFlagValuePanel.setVisible( false );
			myTextValuePanel.setVisible( true );
			myExplanation.setText( "Only '" + getNodeName() + "' that contains the specified text value will be included in the search results." );
		}
		else
			assert false: "TextBlobValueRestrictionEditorPanel.autoSetPanelVisibilityAndExplanation(): Button state: {NoValue=false, FlagValue=false, TextValue=false} is not recognized.";
	}

	private void initUIWithData()
	{
		if (myValueRestriction.hasStringValue()) 
		{
			if (myValueRestriction.useValueFlag()) 
				myFlagValueButton.setSelection(true);
			else if (myValueRestriction.useStringValue()) 
			{
				myTextValueButton.setSelection(true);
				myTextInput.setText(myValueRestriction.value());
				if(myValueRestriction.isLongText()) 
				{
					if(myValueRestriction.operator().equalsIgnoreCase( CONTAINS )) 
						myUseDBOpsButton.setSelection(false);
					else if(myValueRestriction.operator().equalsIgnoreCase( CONTAINS_DB )) 
						myUseDBOpsButton.setSelection(true);
					myTextOpsCombo.select(0);
				} 
				else 
				{
					if(myValueRestriction.operator().equalsIgnoreCase( LIKE_CONTAINS )) 
						myTextOpsCombo.select(0); 	// "Containing"
					else if(myValueRestriction.operator().equalsIgnoreCase( LIKE_EXACT )) 
						myTextOpsCombo.select(1);	// "Exact"
					else if(myValueRestriction.operator().equalsIgnoreCase( LIKE_BEGINS )) 
						myTextOpsCombo.select(2);	// "Starting with"
					else if(myValueRestriction.operator().equalsIgnoreCase( LIKE_ENDS ))
						myTextOpsCombo.select(3);	// "Ending with"
				}
			}
			else
				myNoValueButton.setSelection( true );
		}
	}
	
	private void autoSetValueRestriction()	
	{
		myValueRestriction.selectedValues.clear();
		if ( this.myNoValueButton.getSelection() ) 
		{
			myValueRestriction.noValue(true);
			myValueRestriction.useValueFlag(false);
			myValueRestriction.useNumericValue(false);
			myValueRestriction.useTextValue(false);
			myValueRestriction.useStringValue(false);
			//myNodeData.valueName("");
		} 
		else if ( this.myFlagValueButton.getSelection() ) 
		{
			myValueRestriction.noValue(false);
			myValueRestriction.useValueFlag(true);
			myValueRestriction.useNumericValue(false);
			myValueRestriction.useTextValue(false);
			myValueRestriction.useStringValue(false);
			myValueRestriction.value( DataConst.ABNORNAL_FLAG_VALUE );
			//myNodeData.valueName( myValueRestriction.toString() );
		} 
		else if ( this.myTextValueButton.getSelection() ) 
		{
			myValueRestriction.noValue(false);
			myValueRestriction.useValueFlag(false);
			myValueRestriction.useNumericValue(false);
			myValueRestriction.useTextValue(false);
			myValueRestriction.useStringValue(true);
			myValueRestriction.selectedValues.clear();
			myValueRestriction.value( myTextInput.getText().trim());
			
			if(myValueRestriction.isLongText()) 
			{
				if( this.myUseDBOpsButton.getSelection() )
					myValueRestriction.operator( CONTAINS_DB );
				else 
					myValueRestriction.operator( CONTAINS );
			} 
			else 
			{
				if( myTextOpsCombo.getSelectionIndex() == 0)
					myValueRestriction.operator( LIKE_CONTAINS );
				else if( myTextOpsCombo.getSelectionIndex() == 1) 
					myValueRestriction.operator( LIKE_EXACT );
				else if( myTextOpsCombo.getSelectionIndex() == 2)
					myValueRestriction.operator( LIKE_BEGINS );
				else if(myTextOpsCombo.getSelectionIndex() == 3)
					myValueRestriction.operator( LIKE_ENDS );
			}
			//myNodeData.valueName( myValueRestriction.toString() );
		}
		else
			assert false: "TextBlobValueRestrictionEditorPanel.autoSetValueRestriction(): Button state: {NoValue=false, FlagValue=false, TextValue=false} is not recognized.";
		
		//System.err.println("TextBlobValueRestrictionEditorPanel.autoSetValueRestriction: data changed: " + myNodeData.toString() );
	}
	
	/*
	 * main method for testing only
	 */
	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		TextBlobValueRestrictionEditorPanel gp = new TextBlobValueRestrictionEditorPanel( myShell, SWT.None );
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
