package edu.harvard.i2b2.eclipse.plugins.querytool.ui.events;

public interface IVisualActivationManageable 
{
	
	public void addVisualActivationListener( VisualActivationListener list );
	public boolean removeVisualActivationListener( VisualActivationListener list );
	
	public void setVisualActivationListeners();
	public void resetVisualActivationListeners();

}
