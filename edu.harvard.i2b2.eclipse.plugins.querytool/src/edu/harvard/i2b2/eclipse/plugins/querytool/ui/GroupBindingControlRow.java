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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.IGroupBindingPolicyProvider;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.UIManagerContentChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class GroupBindingControlRow extends Composite implements DataChangedListener, UIConst
{
	public static final String	CHECKBOX_TOOLIP_PREFIX 	= "A check mark here means the Group is bound by ";
	
	
	private String 	myName;
	private Button	myPatCheckBox;	// checkbox for patient
	private Button	myObsCheckBox;	// checkbox for observations
	private Button	myEncCheckBox;	// checkbox for encounters
	private Label	myLabel;
	
	private	Group	myGroup;
	
	public GroupBindingControlRow( Composite parent, int style, Group g ) 
	{
		super(parent, style );
		myGroup = g;
		g.addDataChangedListener( this ); // so we can receive changes to group's data
		this.setName( myGroup.getName() );
		setupUI();
		attachListeners();
	}

	private void setupUI() 
	{
		this.setLayout( new FormLayout() );
		
		myObsCheckBox 	= new Button( this, SWT.CHECK );		
		myEncCheckBox 	= new Button( this, SWT.CHECK );
		myPatCheckBox	= new Button( this, SWT.CHECK );
		myLabel			= new Label( this, SWT.RIGHT );
		myLabel.setText( myName );
		
		myObsCheckBox.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, (Integer)null));
		myEncCheckBox.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, myObsCheckBox, (Integer)null));
		myPatCheckBox.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, myEncCheckBox, (Integer)null));
		
		myLabel.setLayoutData( FormDataMaker.makeFormData(0, 0, (Integer)null, 0, myPatCheckBox, 5, 100, 0));
		
		if ( myGroup.getBinding() == GroupBinding.BY_ENCOUNTER )
		{
			myEncCheckBox.setSelection( true );
		}
		else if ( myGroup.getBinding() == GroupBinding.BY_OBSERVATION )
		{			
			myObsCheckBox.setSelection( true );
			myEncCheckBox.setSelection( true );			
			myEncCheckBox.setEnabled( false ); 
		}
		myPatCheckBox.setSelection( true );	// always selected
		myPatCheckBox.setEnabled( false );	// never enabled (By_Patient is always checked)
		
		myObsCheckBox.setToolTipText( CHECKBOX_TOOLIP_PREFIX + OBSERVATION );
		myEncCheckBox.setToolTipText( CHECKBOX_TOOLIP_PREFIX + ENCOUNTER );
		myPatCheckBox.setToolTipText( CHECKBOX_TOOLIP_PREFIX + BOUND_BY_PATIENT );
		
		myObsCheckBox.setEnabled( myGroup.isContainingModifier() );
		
		autoEnableCheckboxes();
	}
	
	/* Allows GroupBindingControlPanel to reset the group binding */
	public void resetBinding()
	{ 
		this.myGroup.setBinding( GroupBinding.BY_PATIENT );	// set data, update the GroupPanel UI
		myObsCheckBox.setSelection( false ); 				// update this component's UI
		myEncCheckBox.setSelection( false );
		myObsCheckBox.setEnabled( myGroup.isContainingModifier() );
		myEncCheckBox.setEnabled( true ); 
	}
	
	private void attachListeners() 
	{
		myObsCheckBox.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  {
				  myEncCheckBox.setSelection( myObsCheckBox.getSelection() );
				  if ( myObsCheckBox.getSelection() )
					  myEncCheckBox.setEnabled( false );
				  else
					  myEncCheckBox.setEnabled( true );
				  autoSetSelectionInData();
			  }
		});
		myEncCheckBox.addSelectionListener( new SelectionAdapter()
		{
			  public void widgetSelected( SelectionEvent e )
			  {
				  autoSetSelectionInData();
			  }
		});
	}
	
	private void autoSetSelectionInData()
	{
		if ( myObsCheckBox.getSelection() )
			myGroup.setBinding( GroupBinding.BY_OBSERVATION );
		else if ( myEncCheckBox.getSelection() )
			myGroup.setBinding( GroupBinding.BY_ENCOUNTER );
		else
			myGroup.setBinding( GroupBinding.BY_PATIENT );
	}

	
	public void setName( String name )
	{
		myName = name;
	}

	public boolean getIsObsChecked()	{ return this.myObsCheckBox.getSelection(); }
	public boolean getIsEncChecked()	{ return this.myEncCheckBox.getSelection(); }


	public void autoEnableCheckboxes()
	{
		// do not change myPatCheckBox because it should always be disabled for this widget (It's always By_Patient by default)
		// enable EncCheckBox and ObsCheckBox appropriately by checking with the group's policy
		this.myEncCheckBox.setEnabled( myGroup.getGroupBindingPolicyProvider().isByEncounterEnabled( myGroup ) );
		this.myObsCheckBox.setEnabled( myGroup.getGroupBindingPolicyProvider().isByObservationEnabled( myGroup ) );
	}

	@Override /* DataChangedListener method */
	public void dataChanged(Object source) 
	{
		// enable EncCheckBox and ObsCheckBox appropriately by checking with the group's policy
		this.myEncCheckBox.setEnabled( myGroup.getGroupBindingPolicyProvider().isByEncounterEnabled( myGroup ) );
		this.myObsCheckBox.setEnabled( myGroup.getGroupBindingPolicyProvider().isByObservationEnabled( myGroup ) );

		// set button selection to be in accordance to data
		this.myEncCheckBox.setSelection( (myGroup.getBinding() == GroupBinding.BY_ENCOUNTER) || (myGroup.getBinding() == GroupBinding.BY_OBSERVATION) );
		this.myObsCheckBox.setSelection( (myGroup.getBinding() == GroupBinding.BY_OBSERVATION) );
		
		if ( myObsCheckBox.getEnabled() )		
			this.myEncCheckBox.setEnabled( !myObsCheckBox.getSelection() ); // if myObsCheckBox is selected, disable myEncCheckBox and vise versa
	}
	
	@Override /* Widget method */
	public void dispose()
	{		
		myGroup.removeDataChangedListener( this ); // remove self as myData's listener
		super.dispose();
	}


}
