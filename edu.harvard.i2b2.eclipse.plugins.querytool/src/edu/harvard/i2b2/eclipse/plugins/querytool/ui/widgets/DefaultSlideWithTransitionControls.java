package edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DefaultSlideWithTransitionControls extends AbstractSlideWithTransitionControls
{

	public DefaultSlideWithTransitionControls(Composite parent, int style) 
	{
		super(parent, style);
	}

	@Override
	public Composite getNextTransitionControl(Composite parent, Control leftControl) 
	{ return null; }

	@Override
	public Composite getPrevTransitionControl(Composite parent, Control rightControl) 
	{ return null; }
	
	@Override
	public void setNextTransitionControlVisible( boolean flag )
	{/*do nothing*/}
	
	@Override
	public void setPrevTransitionControlVisible( boolean flag )
	{/*do nothing*/}

	@Override
	public void performPreSlideActions(int toSlideIndex) 
	{/*by default do nothing. Let subclass override.*/}

	@Override
	public void performPostSlideActions(int fromSlideIndex) 
	{/*by default do nothing. Let subclass override.*/}

}
