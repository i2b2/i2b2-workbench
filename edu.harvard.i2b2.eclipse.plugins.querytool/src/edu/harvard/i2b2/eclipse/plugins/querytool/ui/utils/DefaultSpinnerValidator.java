package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Spinner;

public class DefaultSpinnerValidator implements ModifyListener
{
	
	protected Spinner mySpinner;
	protected Integer myPreviousValidValue = 1;
	
	public DefaultSpinnerValidator( Spinner spinner )
	{ 
		mySpinner = spinner; 
	}
	
	public void modifyText(ModifyEvent e) 
	{
		String string = mySpinner.getText();
		try 
		{
			int value = Integer.parseInt(string);
			int maximum = mySpinner.getMaximum();
			int minimum = mySpinner.getMinimum();
			if ((value > maximum) || (value < minimum))
				mySpinner.setSelection( myPreviousValidValue.intValue() );
			else
				myPreviousValidValue = value;
		} 
		catch (Exception ex) 
		{
			mySpinner.setSelection( myPreviousValidValue.intValue() );
		}
	}

}
