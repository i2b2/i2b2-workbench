package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;

public class GroupBindingControlLabel extends Canvas 
{

	/*
	 * checkboxWidth is the the width of a checkbox widget on a system.
	 * labelHeight is the height of a label widget on a system. 
	 */
	public GroupBindingControlLabel(Composite parent, int style, int checkboxWidth, int labelHeight ) 
	{
		super(parent, style);
		addListeners( checkboxWidth, labelHeight );
	}
	
	private void addListeners( final int checkboxWidth, int labelHeight)
	{
		final int hcbw 	= checkboxWidth/2;	// half of CheckboxWidth
		final int hlh	= labelHeight/2-2;	// half of LabelHeight
		
		this.addPaintListener( new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent e) 
			{				
				int h = GroupBindingControlLabel.this.getBounds().height-1;
				e.gc.setForeground( Colors.DARK_GRAY );
				e.gc.drawLine( checkboxWidth, hlh, hcbw, hlh );
				e.gc.drawLine( hcbw, hlh, hcbw, h );
				e.gc.drawLine( hcbw, h, hcbw-2, h-3 );
				e.gc.drawLine( hcbw, h, hcbw+2, h-3 );
			}
		});
	}
	
}
