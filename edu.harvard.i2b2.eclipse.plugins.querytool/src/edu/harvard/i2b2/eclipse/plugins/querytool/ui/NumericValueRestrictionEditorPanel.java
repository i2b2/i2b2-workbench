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
import java.util.HashSet;

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

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.query.data.DataConst;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;
import edu.harvard.i2b2.query.data.UnitsData;
import edu.harvard.i2b2.query.data.ValuePropertyUtils;

public class NumericValueRestrictionEditorPanel extends ValueRestrictionEditorPanel implements UIConst, DataConst
{
	protected Button	myNoValueButton;
	protected Button	myFlagValueButton;
	protected Button	myNumericValueButton;
	
	protected Label		myExplanation;
	
	protected Composite	myNoValuePanel;
	protected Composite	myFlagValuePanel;
		protected Combo myFlagCombo;
		
	protected Composite	myNumericValuePanel;
		protected Combo myOperatorCombo;
		protected Text	myText;
		protected Text	myText2;
		protected Label	myBetweenLabel;		
		protected Combo	myUnitCombo;
	
	protected ArrayList<UnitsData> myUnits; // array of units that parallels the unit names in myUnitCombo 
	
	public NumericValueRestrictionEditorPanel(Composite parent, int style, QueryConceptTreeNodeData data) 
	{
		super(parent, SWT.BORDER | style);
		myNodeData = data;
		myValueRestriction = myNodeData.valuePropertyData().makeCopy();
		myUnits = new ArrayList<UnitsData>();
		setupUI();
		attachListeners();
	}
	
	/* Constructor for testing only*/
	private NumericValueRestrictionEditorPanel(Composite parent, int style) 
	{
		super(parent, style | SWT.BORDER );
		myNodeData 			= null;
		myValueRestriction 	= null;
		myUnits = new ArrayList<UnitsData>();
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
		int constraintType = 0;
		if (myValueRestriction.okToUseValueFlag())
		{
			labelText = labelText + "a High/Low Flag";
			constraintType++;
		}
		if (myValueRestriction.okToUseValue())
		{
			String valueText = "a specific Numeric Value";
			if ( constraintType == 1 )
				valueText = " or " + valueText;
			labelText = labelText + valueText;
			constraintType++;
		}
		labelText = labelText + ". Default is 'No Value.'";
		
		if ( constraintType == 0 )
			labelText =  getNodeName() + " cannot be constrained any further.";
		
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
		myFlagValueButton.setText( NUMERIC_FLAG_VALUE );
		myNumericValueButton = new Button( buttonsComp, SWT.RADIO );
		myNumericValueButton.setText( NUMERIC_VALUE );
		
		// disable buttons when prescribed by the valueRestriction
		if (!myValueRestriction.okToUseValueFlag())
			myFlagValueButton.setEnabled(false);
		if (!myValueRestriction.okToUseValue())
			myNumericValueButton.setEnabled(false);
		
		myNoValueButton.setLayoutData( FormDataMaker.makeFormData( 10, 0, (Integer)null, 0, 10, 0, 100, -10 ));
		myFlagValueButton.setLayoutData( FormDataMaker.makeFormData( myNoValueButton, 0, (Integer)null, 0, 10, 0, 100, -10 ));
		myNumericValueButton.setLayoutData( FormDataMaker.makeFormData( myFlagValueButton, 0, (Integer)null, 0, 10, 0, 100, -10 ));
		
		myNoValuePanel 		= makeNoValuePanel( widgetComp, getNodeName() );		
		myFlagValuePanel 	= makeFlagValuePanel( widgetComp, getNodeName() );
		myNumericValuePanel = makeNumericValuePanel( widgetComp, getNodeName() );
		
		myExplanation = new Label( buttonsComp, SWT.WRAP );
		myExplanation.setBackground( Colors.WHITE );
		myExplanation.setLayoutData( FormDataMaker.makeFormData( myNumericValueButton, 30, 100, -10, 0, 10, 100, 0));
		
		// set defaults
		initUIWithData();		
		autoSetPanelVisibilityAndExplanation();
	}

	private Composite makeNoValuePanel( Composite parent, String conceptName )
	{
		Composite comp = new Composite( parent, SWT.NONE );
		comp.setLayout( new FormLayout() );
		comp.setLayoutData( FormDataMaker.makeFullFormData() );
		return comp;
	}
	
	private Composite makeFlagValuePanel( Composite parent, String conceptName )
	{
		Composite comp = new Composite( parent, SWT.NONE );
		comp.setLayout( new FormLayout() );
		comp.setLayoutData( FormDataMaker.makeFullFormData() );

		Label instruction = new Label( comp, SWT.NONE );
		instruction.setText(SELECT_A_FLAG_VALUE);
		instruction.setLayoutData( FormDataMaker.makeFormData( 10, -instruction.computeSize( SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 4, 100, -4) );
		
		myFlagCombo = new Combo( comp, SWT.DROP_DOWN | SWT.READ_ONLY );
		myFlagCombo.setLayoutData( FormDataMaker.makeFormData( instruction, 4, (Integer)null, 0, 0, 10, (Integer)null, 0) );
		for ( String item : UIConst.HIGH_LOW_FLAGS )
			myFlagCombo.add( item );
		
		// set default
		myFlagCombo.select( 0 );
		return comp;
	}

	
	private Composite makeNumericValuePanel( Composite parent, String conceptName )
	{
		Composite comp = new Composite( parent, SWT.NONE );
		comp.setLayout( new FormLayout() );
		comp.setLayoutData( FormDataMaker.makeFullFormData() );

		Label instruction = new Label( comp, SWT.NONE );
		instruction.setText( SELECT_A_NUMERIC_VALUE );
		instruction.setLayoutData( FormDataMaker.makeFormData( 10, -instruction.computeSize( SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 4, 100, -4) );
		
		myOperatorCombo = new Combo( comp, SWT.DROP_DOWN | SWT.READ_ONLY );
		myOperatorCombo.setLayoutData( FormDataMaker.makeFormData( instruction, 4, (Integer)null, 0, 0, 10, 100, -10) );
		for ( String item : UIConst.VALUE_OPERATORS )
			myOperatorCombo.add( item );
		
		myText = new Text( comp, SWT.SINGLE | SWT.RIGHT );
		myText.setLayoutData( FormDataMaker.makeFormData( myOperatorCombo, 2, (Integer)null, 0, 0, 10, 50, -10 ) );
		myBetweenLabel = new Label( comp, SWT.NONE );
		myBetweenLabel.setLayoutData( FormDataMaker.makeFormData( myOperatorCombo, 2, (Integer)null, 0, myText, 2, (Integer)null, 0 ) );
		myBetweenLabel.setText(" - ");
		myText2 = new Text( comp, SWT.SINGLE | SWT.RIGHT );
		myText2.setLayoutData( FormDataMaker.makeFormData( myOperatorCombo, 2, (Integer)null, 0, myBetweenLabel, 2, 100, -10 ) );

		myUnitCombo = new Combo( comp, SWT.DROP_DOWN | SWT.READ_ONLY );
		
		Label unitLabel = new Label( comp, SWT.NONE );
		unitLabel.setText( SELECT_A_UNIT );
		unitLabel.setLayoutData( FormDataMaker.makeFormData( myText, 2+myUnitCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2-unitLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 10, (Integer)null, 0) );
		
		myUnitCombo.setLayoutData( FormDataMaker.makeFormData( myText, 2, (Integer)null, 0, unitLabel, 2, (Integer)null, 0));
		HashSet<String> unitSet = new HashSet<String>(); // do not add units that have already been added
		if ( myValueRestriction!= null )
			for (int i = 0; i < myValueRestriction.units.size(); i++)
			{
				UnitsData uData 	= myValueRestriction.units.get(i);
				String unitName = uData.name().trim();
				if ( unitName.isEmpty() ) // don't add units that is empty
					continue;
				if ( !unitSet.contains( uData.name() ))
				{
					myUnitCombo.add( uData.name() );
					myUnits.add( uData );
					unitSet.add( uData.name() );
				}
			}
		
		// set default
		myOperatorCombo.select( 0 );
		myUnitCombo.select( 0 );
		myText.setText( ZERO_STRING );
		myText2.setText( ZERO_STRING );
		autoSetNumericValueTextInputs();
		
		return comp;
	}

	private void initUIWithData() 
	{
		if (myValueRestriction.useNumericValue()) 
		{
			myOperatorCombo.select( getOperatorIndex(myValueRestriction.operator()) );
			myNumericValueButton.setSelection( true );
			if (myValueRestriction.operator().equals( BETWEEN )) 
			{
				myText.setText( myValueRestriction.lowValue() );
				myText2.setText( myValueRestriction.highValue() );
			} 
			else
				myText.setText( myValueRestriction.value() );
		} 
		else if (myValueRestriction.useValueFlag()) 
		{
			if (myValueRestriction.value().equals("H")) 
			{
				myFlagCombo.select(0);
				myFlagValueButton.setSelection(true);
			} 
			else if (myValueRestriction.value().equals("L")) 
			{
				myFlagCombo.select(1);
				myFlagValueButton.setSelection(true);
			}
		}
		else
			myNoValueButton.setSelection( true );

		if ( myNodeData.hasValue() && myValueRestriction.unit() != null) 
		{
			for ( int i = 0; i < this.myUnitCombo.getItemCount(); i++ )
				if ( myUnitCombo.getItem(i).equals( myValueRestriction.unit() ))
				{
					myUnitCombo.select( i );
					break;
				}
		}
	}

	
	private void autoSetNumericValueTextInputs()
	{
		if ( myOperatorCombo.getItem( myOperatorCombo.getSelectionIndex()).equals( BETWEEN ) )
		{
			myBetweenLabel.setVisible( true );
			myText2.setVisible( true );
		}
		else
		{
			myBetweenLabel.setVisible( false );
			myText2.setVisible( false );
		}
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
		myNumericValueButton.addSelectionListener( buttonSelectedListener );
		
		/* Change UI by Operator selected */
		myOperatorCombo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent event )
			{
					autoSetNumericValueTextInputs(); 
			}
		});
		
		/* Verify inputs to Texts make valid doubles */
		myText.addVerifyListener( UIUtils.getDecimalInputVerifyListener() );
		myText2.addVerifyListener( UIUtils.getDecimalInputVerifyListener() );

		
		/* Change Data by UI changes*/
		SelectionListener dataChangedListener = new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent event )
			{  
				if ( !(event.getSource() instanceof Button) )
					autoSetValueRestriction(); // change myNodeData
				else if ( ((Button)event.getSource()).getSelection() )
					autoSetValueRestriction(); // change myNodeData					
			}
		};
		myNoValueButton.addSelectionListener( dataChangedListener );
		myFlagValueButton.addSelectionListener( dataChangedListener );
		myNumericValueButton.addSelectionListener( dataChangedListener );
		myOperatorCombo.addSelectionListener( dataChangedListener );
		myFlagCombo.addSelectionListener( dataChangedListener );
		myUnitCombo.addSelectionListener( dataChangedListener );

		/* Modification to content of Text triggers changing myNodeData as well */
	    ModifyListener textModifyListener = new ModifyListener()
	    {
	         public void modifyText(ModifyEvent arg0) 
	         {
	        	 autoSetValueRestriction(); // change myNodeData       
	         }
	    };
		myText.addModifyListener(textModifyListener);
		myText2.addModifyListener(textModifyListener);			
	}

	private void autoSetPanelVisibilityAndExplanation()
	{
		if ( myNoValueButton.getSelection() )
		{
			myNoValuePanel.setVisible( true );
			myFlagValuePanel.setVisible( false );
			myNumericValuePanel.setVisible( false);
			
			myExplanation.setText( "All '" + getNodeName() + "' will be included in the search results." );
		}
		else if ( myFlagValueButton.getSelection() )
		{
			myNoValuePanel.setVisible( false );
			myFlagValuePanel.setVisible( true );
			myNumericValuePanel.setVisible( false );
			myExplanation.setText( "Only '" + getNodeName() + "' that has the specified flag value will be included in the search results." );
		}
		else if ( myNumericValueButton.getSelection() )
		{
			myNoValuePanel.setVisible( false );
			myFlagValuePanel.setVisible( false );
			myNumericValuePanel.setVisible( true );
			autoSetNumericValueTextInputs();
			myExplanation.setText( "Only '" + getNodeName() + "' that meets specified numeric range will be included in the search results." );
		}
		else
			assert false: "NumericValueRestrictionEditorPanel.autoSetPanelVisibiilty(): Button state: {NoValue=false, FlagValue=false, NumericValue=false} is not recognized.";
	}

	private void autoSetValueRestriction()
	{
		if ( myNoValueButton.getSelection() )
		{
			myValueRestriction.noValue(true);
			myValueRestriction.useValueFlag(false);
			myValueRestriction.useNumericValue(false);
			//myNodeData.valueName("");
		}
		else if ( myFlagValueButton.getSelection() )
		{
			myValueRestriction.noValue(false);
			myValueRestriction.useValueFlag(true);
			myValueRestriction.useNumericValue(false);
			if ( myFlagCombo.getItem( myFlagCombo.getSelectionIndex() ).equals(HIGH_LOW_FLAGS[0]) ) // 0 is HIGH
			{
				//myNodeData.valueName( DataConst.HIGH_FLAG_NAME );
				myValueRestriction.value( DataConst.HIGH_FLAG_VALUE );
			} 
			else if ( myFlagCombo.getItem( myFlagCombo.getSelectionIndex() ).equals(HIGH_LOW_FLAGS[1]) ) // 1 is LOW
			{
				//myNodeData.valueName( DataConst.LOW_FLAG_NAME );
				myValueRestriction.value( DataConst.LOW_FLAG_VALUE );
			}
		}
		else if ( myNumericValueButton.getSelection() )
		{
			myValueRestriction.noValue(false);
			myValueRestriction.useValueFlag(false);
			myValueRestriction.useNumericValue(true);
			UnitsData ud = null;
			if (myUnitCombo.getItem( myUnitCombo.getSelectionIndex() ) != null) 
			{
				ud = myUnits.get( myUnitCombo.getSelectionIndex() );
				myValueRestriction.unit( myUnitCombo.getItem( myUnitCombo.getSelectionIndex() ) );
			}
			double value1 = Double.parseDouble( this.myText.getText() );
			double value2 = Double.parseDouble( this.myText2.getText() );
			
			String operator = myOperatorCombo.getItem( myOperatorCombo.getSelectionIndex() );
			myValueRestriction.operator(operator);
			
			if ( operator.equalsIgnoreCase( BETWEEN ) )
			{
				double highValue = Math.max( value1, value2 );
				double lowValue = Math.min( value1, value2 );
				if (ud != null && ud.needConversion()) // convert if necessary
				{
					highValue = highValue * ud.mFactor();
					lowValue  = lowValue  * ud.mFactor();
				}
				myValueRestriction.lowValue("" + lowValue );
				myValueRestriction.highValue("" + highValue );
				//myNodeData.valueName("(" + myValueRestriction.lowValue() + " - " + myValueRestriction.highValue() + ")");
			}
			else
			{
				if(ud != null && ud.needConversion()) 
					myValueRestriction.value(""+ value1 * ud.mFactor());
				else 
					myValueRestriction.value( value1 + "" );
				//myNodeData.valueName(ValuePropertyUtils.getOperatorDisplayString(myValueRestriction.operator()) + myValueRestriction.value());
			}
		}
		else
			assert false: "NumericValueRestrictionEditorPanel.autoSetValueRestriction(): Button state: {NoValue=false, FlagValue=false, NumericValue=false} is not recognized.";
		
		//System.err.println("NumericValueRestrictionEditorPanel.autoSetValueRestriction: data changed: " + myNodeData.toString() );
	}

	private int getOperatorIndex( String opStr )
	{
		if (opStr.equalsIgnoreCase( VALUE_OPERATORS[0] )) // 0 is Less than 
			return 0;
		else if (opStr.equalsIgnoreCase( VALUE_OPERATORS[1] )) // 1 is less than equal to 
			return 1;
		else if (opStr.equalsIgnoreCase( VALUE_OPERATORS[2] )) // 2 is equal to (and 3 is between)
			return 2;
		else if (opStr.equalsIgnoreCase( VALUE_OPERATORS[3] )) // 4 is greater than equal to 
			return 3;
		else if (opStr.equalsIgnoreCase( VALUE_OPERATORS[4] )) // 4 is greater than equal to 
			return 4;
		else if (opStr.equalsIgnoreCase( VALUE_OPERATORS[5] )) // 5 is greater than 
			return 5;
		else
			return -1;
	}

	/*
	 * main method for testing only
	 */
	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		NumericValueRestrictionEditorPanel gp = new NumericValueRestrictionEditorPanel( myShell, SWT.None );
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
