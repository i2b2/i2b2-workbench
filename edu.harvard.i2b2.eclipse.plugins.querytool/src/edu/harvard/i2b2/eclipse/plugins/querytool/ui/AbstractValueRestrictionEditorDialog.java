package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public abstract class AbstractValueRestrictionEditorDialog implements UIConst 
{
	
	protected Shell		myShell;
	protected Composite	myMainComp;
	protected Point		myInitLocation; 
	
	protected Composite	titleComp;
	protected Label		titleLabel;
	protected Composite	innerComp;

	protected Composite	myPanelComp;
	
	public abstract boolean hasContent();

	public abstract void setLocation( Point location );

	// open the dialog
	public abstract void open();

}
