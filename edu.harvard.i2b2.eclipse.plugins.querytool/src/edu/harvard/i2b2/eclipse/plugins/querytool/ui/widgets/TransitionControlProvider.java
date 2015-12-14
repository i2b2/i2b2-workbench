package edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface TransitionControlProvider 
{

	public Composite getNextTransitionControl( Composite parent, Control leftControl );
	public Composite getPrevTransitionControl( Composite parent, Control rightControl );
	
	public void setNextTransitionControlVisible( boolean flag );
	public void setPrevTransitionControlVisible( boolean flag );
}
