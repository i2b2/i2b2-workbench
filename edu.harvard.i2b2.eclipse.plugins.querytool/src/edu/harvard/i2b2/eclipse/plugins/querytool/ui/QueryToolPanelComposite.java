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

import org.eclipse.swt.widgets.Composite;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.IVisualActivationManageable;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.VisualActivationListener;


/*
 * Special Composite used in QueryTool to override and handle setEnabled(...) for its
 *  children.
 *  
 *  See UIUtils.recursiveSetEnabledAndRememberUnchangedControls(...) and related static methods in the same class.
 */
public abstract class QueryToolPanelComposite extends Composite implements IVisualActivationManageable
{

	protected ArrayList<VisualActivationListener> myActivationListeners;
	
	public QueryToolPanelComposite(Composite parent, int style) 
	{
		super(parent, style);
		myActivationListeners = new ArrayList<VisualActivationListener>();
	}
	

	@Override
	public void setEnabled( boolean flag )
	{
		super.setEnabled( flag );
		setActive( flag );
	}


	protected abstract void setActive( boolean flag );

	
	/*
	 * IVisualActivationManageable methods
	 */
	public void addVisualActivationListener( VisualActivationListener list )
	{ myActivationListeners.add( list ); }
	
	public boolean removeVisualActivationListener( VisualActivationListener list )
	{ return myActivationListeners.remove( list ); };

	public void setVisualActivationListeners()
	{
		for ( VisualActivationListener listener : myActivationListeners )
			listener.setActivatedControl( this, null );
	}

	public void resetVisualActivationListeners()
	{
		for ( VisualActivationListener listener : myActivationListeners )
			listener.resetActivatedControl();
	}
}
