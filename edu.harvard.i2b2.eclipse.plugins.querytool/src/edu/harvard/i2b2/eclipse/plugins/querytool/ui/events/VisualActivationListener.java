package edu.harvard.i2b2.eclipse.plugins.querytool.ui.events;

import org.eclipse.swt.widgets.Control;

/*
 * This interface allows the system to know which UI component the user clicked on to launch a modal dialog.
 * 	   When the model dialog is launched, all UI components are grayed out except for the one the user clicked on.
 */
public interface VisualActivationListener 
{

	public void setActivatedControl( Control control, Object supplementalData );

	public Control getActivatedControl();

	public void resetActivatedControl();

}
