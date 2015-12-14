package edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class ControlBorderDrawer implements PaintListener
{
	
	private Control myControl;
	private Color	myColor;
	
	public ControlBorderDrawer( Control control, Color borderColor )
	{
		myControl 	= control;
		myColor 	= borderColor;
	}

	@Override /* PaintListener method */
	public void paintControl(PaintEvent e) 
	{
		 e.gc.setForeground( this.myColor );
		 Rectangle rText = this.myControl.getBounds();
		 Rectangle rect1 = new Rectangle(0, 0, rText.width-1, rText.height-1);
		 e.gc.drawRectangle(rect1);
	}
	
}
