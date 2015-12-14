package edu.harvard.i2b2.eclipse.plugins.querytool.z.tests;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.VerticalLabel;

public class VerticalLabelTest 
{
	
	public static void main( String [] args )
	{
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setBackground( Colors.GRAY );
		shell.setSize( 400, 400);
		shell.setLayout( new FormLayout() );
		
		Composite topComp = new Composite( shell, SWT.BORDER );
		topComp.setLayout( new FormLayout() );
		topComp.setLayoutData( FormDataMaker.makeFormData( 0, (Integer)null, 0, 100) );
		
		Label topLabel = new Label( topComp, SWT.BORDER );
		topLabel.setText("topLabel");
		topLabel.setLayoutData( FormDataMaker.makeFormData(0, 100, 0, (Integer)null));
		
		Composite myMainComp = new Composite( shell, SWT.None );
		myMainComp.setLayout( new FormLayout() );
		myMainComp.setLayoutData( FormDataMaker.makeFormData( topComp, 100, 0, 100) );
		
		VerticalLabel vlabel1 = new VerticalLabel( myMainComp, SWT.None, "Observation" );
		vlabel1.setLayoutData( FormDataMaker.makeFormData(0, 0, 100, 0, 0, 0, 0, vlabel1.getPreferredSize().x) );
		
		VerticalLabel vlabel2 = new VerticalLabel( myMainComp, SWT.None, "Encounter" );
		vlabel2.setLayoutData( FormDataMaker.makeFormData( 0			, (vlabel1.getPreferredSize().y-vlabel2.getPreferredSize().y), 
														  (Integer)100 	, 0,
														  vlabel1		, 0,
														  (Integer)null	,0 ));		
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
