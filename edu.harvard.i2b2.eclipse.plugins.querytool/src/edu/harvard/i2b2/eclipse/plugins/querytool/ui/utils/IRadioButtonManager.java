package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

public interface IRadioButtonManager 
{
	
	public void addButton( Button radioButton );
	
	public void resetAllButtons();
	public void buttonSelected( Control buttonOwner, Button targetButton );
	
	public void selectButtonbyIndex( int index);
	
}
