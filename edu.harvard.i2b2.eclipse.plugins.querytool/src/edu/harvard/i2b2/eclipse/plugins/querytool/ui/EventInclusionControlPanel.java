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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class EventInclusionControlPanel extends QueryToolPanelComposite implements UIConst
{

	private Button 		myEventInclusionButton;
		private Composite 	titleComp;
		private Label		titleLabel;
	
	public EventInclusionControlPanel(Composite parent, int style) 
	{
		super(parent, style);
		setupUI();
		attachListeners();
	}

	private void setupUI() 
	{
		this.setLayout( new FormLayout() );
		
		titleComp = new Composite( this, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );

		titleLabel = new Label( titleComp, SWT.None );
		titleLabel.setText( EVENT_INCLUSION );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 50, -titleSize.x/2, (Integer)null, 0));

		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );
		
		myEventInclusionButton = new Button( this, SWT.CHECK );
		myEventInclusionButton.setLayoutData( FormDataMaker.makeFormData( titleComp, (Integer)null, 0, (Integer)null ));
		myEventInclusionButton.setText( "" );
		myEventInclusionButton.setSelection( true );
		
		Label label = new Label( this, SWT.WRAP );
		label.setText( ONLY_EVENTS_USED_IN_RELATIONSHIPS );
		label.setLayoutData( FormDataMaker.makeFormData( titleComp, 2, 100, -4, myEventInclusionButton, 4, 100, -4));
	}

	private void attachListeners() 
	{
		
	}

	public boolean areAllEventsIncluded()
	{ return !myEventInclusionButton.getSelection(); }


	@Override
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
	}
	
}
