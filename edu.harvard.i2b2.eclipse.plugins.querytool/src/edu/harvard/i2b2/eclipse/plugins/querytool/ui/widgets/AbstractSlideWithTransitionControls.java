package edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractSlideWithTransitionControls extends Composite implements TransitionControlProvider
{

	public AbstractSlideWithTransitionControls(Composite parent, int style) 
	{
		super(parent, style);
	}

	public abstract void performPreSlideActions( int toSlideIndex );
	public abstract void performPostSlideActions( int toSlideIndex );
}
