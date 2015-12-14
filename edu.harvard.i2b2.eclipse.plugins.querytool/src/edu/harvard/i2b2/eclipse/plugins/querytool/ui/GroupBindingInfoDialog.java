package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class GroupBindingInfoDialog implements UIConst
{
	public static final int	DIALOG_DEFAULT_HEIGHT 	= 300;
	public static final int	DIALOG_DEFAULT_WIDTH 	= 300;

	protected Shell		myShell;
	protected Composite	myMainComp;
	protected Composite	titleComp;
	protected Label		titleLabel;
	protected Composite 	innerComp;

	protected Button		myOKButton;

	protected Point		myInitLocation; 
	
	// default constructor only for subclasses
	protected GroupBindingInfoDialog()
	{}
	
	
	public GroupBindingInfoDialog( int style ) 
	{
		setupUI( style );
		attachListeners();
	}

	private void setupUI( int shellStyles ) 
	{
		myShell = new Shell( Display.getCurrent(), SWT.APPLICATION_MODAL | shellStyles );
		myShell.setLayout( new FormLayout() );

		myMainComp = new Composite( myShell, SWT.NONE );
		myMainComp.setLayoutData( FormDataMaker.makeFullFormData() );
		myMainComp.setLayout( new FormLayout() );

		titleComp = new Composite( myMainComp, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );

		titleLabel = new Label( titleComp, SWT.NONE );
		titleLabel.setText( GROUPBINDING_EXPLANATION );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 50, -titleSize.x/2, (Integer)null, 0));

		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );

		/* Start OK/Cancel buttons */
		Composite buttonsComposite = new Composite( myMainComp, SWT.NONE );
		buttonsComposite.setLayout( new FormLayout() );
		FormData fd = FormDataMaker.makeFormData( (Integer)null, 100, 0, 100);
		
		fd.height = DECISION_HEIGHT;
		buttonsComposite.setLayoutData( fd );
		buttonsComposite.setBackground( Colors.DARK_GRAY );

		myOKButton = new Button( buttonsComposite, SWT.PUSH );
		myOKButton.setText( CLOSE );
		Point size = myOKButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		myOKButton.setLayoutData( FormDataMaker.makeFormData( 50, -size.y/2, (Integer)null, 0,  50, -size.x/2, (Integer)null, 0 ));

		// selection buttons and explanations
		Composite contentComp = new Composite( myMainComp, SWT.NONE );
		contentComp.setLayout( new FormLayout() );
		contentComp.setLayoutData( FormDataMaker.makeFormData( titleComp, buttonsComposite, 0, 100 ));
		
		GroupBindingInfoRow gp1 = new GroupBindingInfoRow( contentComp, SWT.NONE, GroupBinding.BY_PATIENT );
		FormData gpFD1 = FormDataMaker.makeFormData(0, (Integer)null, 5, 100 ); 
		gp1.setLayoutData( gpFD1 );
		
		GroupBindingInfoRow gp2 = new GroupBindingInfoRow( contentComp, SWT.NONE, GroupBinding.BY_ENCOUNTER );
		FormData gpFD2 = FormDataMaker.makeFormData(gp1, (Integer)null, 5, 100 ); 
		gp2.setLayoutData( gpFD2 );
		
		GroupBindingInfoRow gp3 = new GroupBindingInfoRow( contentComp, SWT.NONE, GroupBinding.BY_OBSERVATION );		
		FormData gpFD3 = FormDataMaker.makeFormData(gp2, (Integer)null, 5, 100 ); 
		gp3.setLayoutData( gpFD3 );
		
		myShell.setSize( myShell.computeSize( SWT.DEFAULT, SWT.DEFAULT ));
		
	}

	private void attachListeners() 
	{
		// TITLE for moving the dialog
		DialogMoveMouseListener moveListener = new DialogMoveMouseListener( myShell, titleComp );
		titleComp.addMouseListener( moveListener );
		titleComp.addMouseMoveListener( moveListener );
		titleLabel.addMouseListener( moveListener );
		titleLabel.addMouseMoveListener( moveListener );

		// Dialog OK button
		myOKButton.addSelectionListener(new SelectionAdapter()
		{	// close dialog. Do nothing. Rely on caller to call 'getSelectedValue' upon dialog closing
			public void widgetSelected( SelectionEvent e )
			{
				myShell.setVisible( false );
				myShell.dispose();
			}
		});
	}
	
	public void setLocation( Point location )
	{
		myInitLocation = location;
	}

	public Point getPreferredSize()	
	{ 
		return myShell.computeSize( DIALOG_DEFAULT_WIDTH, DIALOG_DEFAULT_HEIGHT ); 
	}

	// open the dialog
	public void open()
	{
		if ( this.myInitLocation != null )
			myShell.setLocation( this.myInitLocation );
		
		myShell.setSize( myShell.computeSize( DIALOG_DEFAULT_WIDTH, SWT.DEFAULT ) );
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

	
	public static void main( String [] args )
	{
		GroupBindingInfoDialog gbid = new GroupBindingInfoDialog( SWT.BORDER | SWT.CLOSE | SWT.RESIZE );
		gbid.open();
	}

}
