package edu.harvard.i2b2.eclipse.plugins.querytool.z.tests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.DefaultSlideWithTransitionControls;

public class OverlayDraggablePanelTest 
{
	
	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );

		Composite mainComp = new Composite( myShell, SWT.NONE );
		mainComp.setLayout( new FormLayout() );
		mainComp.setLayoutData( FormDataMaker.makeFullFormData() );		
		mainComp.setBackground( Colors.DARK_BLUE );

		OverlayDraggablePanel panel = new OverlayDraggablePanel( mainComp, SWT.NONE );
		panel.setLayout( new FormLayout() );
		panel.setLayoutData( FormDataMaker.makeFormData( 40, 60, 40, 60 ) );

		myShell.setSize( 300 , 300 );
		myShell.open();

		while (!myShell.isDisposed()) 
		{
			if (!Display.getCurrent().readAndDispatch())
				Display.getCurrent().sleep();
		}
		if (!myShell.isDisposed())
		{
			myShell.close();
			myShell.dispose();
		}
	}
}

class OverlayDraggablePanel extends Composite
{

	public OverlayDraggablePanel(Composite parent, int style) 
	{
		super(parent, style);
		this.setBackground( Colors.WHITE );		
	}
	
}