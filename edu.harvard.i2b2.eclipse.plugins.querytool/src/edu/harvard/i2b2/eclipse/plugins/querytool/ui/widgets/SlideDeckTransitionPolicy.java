package edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets;

public interface SlideDeckTransitionPolicy 
{
	// always returns true
	public SlideDeckTransitionPolicy ALL_OK_POLICY = new SlideDeckTransitionPolicy()
	{
		public boolean canTransitionTo( int fromSlide, int toSlide )
		{ return true; }
	};
	
	public boolean canTransitionTo( int fromSlide, int toSlide );
}
