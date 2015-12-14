package edu.harvard.i2b2.eclipse.plugins.querytool.z.tests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;

public class CustomExpandBarTest 
{
	
	public static void main( String [] args )
	{
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setBackground( Colors.GRAY );
		shell.setSize( 400, 400);
		shell.setLayout( new FormLayout() );
		
		Composite aComp = new Composite( shell, SWT.NONE );
		aComp.setLayout( new FormLayout() );
		aComp.setLayoutData( FormDataMaker.makeFullFormData() );
	
		ScrolledComposite myScroller = new ScrolledComposite( aComp, SWT.V_SCROLL | SWT.H_SCROLL);
		myScroller.setLayout( new FormLayout() );
		myScroller.setLayoutData( FormDataMaker.makeFullFormData() );
		
			Composite myEventComp = new Composite( myScroller, SWT.None );
			myEventComp.setLayout( new FormLayout() );
			myEventComp.setLayoutData( FormDataMaker.makeFullFormData() );
			myEventComp.setBackground( Colors.DARK_GRAY );
		
		myScroller.setExpandHorizontal( true );
		myScroller.setExpandVertical( true );
		myScroller.setContent( myEventComp );

		Control previousControl = null;
		for ( int i = 0; i < 3; i++ )
		{
			CustomExpandBar test = new CustomExpandBar( myEventComp, SWT.NONE, "Test " + (i+1));
			FormData testFD = null;
			if ( previousControl == null)
				testFD = FormDataMaker.makeFormData( 0, 4, (Integer)null, 0, 0, 4, 100, -4);
			else 
				testFD = FormDataMaker.makeFormData( previousControl, 4, (Integer)null, 0, 0, 4, 100, -4);
			testFD.height = test.getPreferredContractedHeight();		
			test.setLayoutData(  testFD );
			previousControl = test;
		}
		
		/*
		CustomExpandBar test1 = new CustomExpandBar( myEventComp, SWT.NONE, "Test 1");
		CustomExpandBar test2 = new CustomExpandBar( myEventComp, SWT.NONE, "Test 2");		
		FormData test1FD = FormDataMaker.makeFormData( 0, 4, (Integer)null, 0, 0, 4, 100, -4);
		test1FD.height = test1.getPreferredContractedHeight();		
		test1.setLayoutData(  test1FD );		
		FormData test2FD = FormDataMaker.makeFormData( test1, 4, (Integer)null, 0, 0, 4, 100, -4);
		test2FD.height = test1.getPreferredContractedHeight();
		test2.setLayoutData( test2FD );
		*/
		
		myScroller.setMinHeight( myEventComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
		
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
