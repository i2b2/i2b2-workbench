package edu.harvard.i2b2.eclipse.plugins.querytool.z.tests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;

public class GlobalMouseListenerTest 
{
	
	public static void main( String [] args )
	{
		final Display display = new Display();
		final Shell shell = new Shell(display, SWT.CLOSE );
		shell.setBackground( Colors.GRAY );
		shell.setSize( 400, 400);
		shell.setLayout( new FormLayout() );
		
		Composite topComp = new Composite( shell, SWT.BORDER );
		topComp.setLayout( new FormLayout() );
		topComp.setLayoutData( FormDataMaker.makeFormData( 0, (Integer)null, 0, 100) );
		
		shell.addListener( SWT.MouseDown, new Listener()
		{
			@Override
			public void handleEvent(Event event) 
			{
				System.err.println( "Mouse downed" ); // responds when a mouse click occurs anywhere in the shell
			}
		});		

		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		if (!shell.isDisposed())
			shell.close();
	}

}
