package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.LayoutRequiredListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.IRadioButtonManager;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class GroupBindingDialog extends GroupBindingInfoDialog implements IRadioButtonManager, LayoutRequiredListener
{
	
	protected Composite	contentComp;
	protected Button	myCancelButton;
	protected Group		myGroup;
	
	protected ArrayList<Button> myButtons 	= new ArrayList<Button>(); // to support IRadioButtonManager
	protected boolean isCanceled 			= false;
	
	protected GroupBinding mySelectedValue 	= null;
	
	protected GroupBindingDialogSelectionRow 	myPatientRow 		= null;
	protected GroupBindingDialogSelectionRow 	myEncounterRow		= null;
	protected GroupBindingDialogSelectionRow 	myObservationRow 	= null;


	public GroupBindingDialog( Group group, int style ) 
	{
		myGroup = group;
		setupUI( style );
		attachListeners();
	}

	private void setupUI( int shellStyles ) 
	{
		myShell = new Shell( Display.getCurrent(), SWT.APPLICATION_MODAL | shellStyles);
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
		titleLabel.setText( SPECIFY_GROUPBINDING );
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
		myOKButton.setText( OK );
		Point size = myOKButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		myOKButton.setLayoutData( FormDataMaker.makeFormData( 50, -size.y/2, (Integer)null, 0, (Integer)null, 0, 50, -size.x/2 - 5 ));

		myCancelButton 	= new Button( buttonsComposite, SWT.PUSH );
		size = myCancelButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		myCancelButton.setText( CANCEL );
		myCancelButton.setLayoutData( FormDataMaker.makeFormData( 50, -size.y/2, (Integer)null, 0, 50, size.x/2 + 5, (Integer)null, 0 ));

		
		// selection buttons and explanations
		contentComp = new Composite( myMainComp, SWT.NONE );
		contentComp.setLayout( new FormLayout() );
		contentComp.setLayoutData( FormDataMaker.makeFormData( titleComp, buttonsComposite, 0, 100 ));
		
		myPatientRow = new GroupBindingDialogSelectionRow( contentComp, SWT.NONE, GroupBinding.BY_PATIENT, this );
		FormData gpFD1 = FormDataMaker.makeFormData(0, (Integer)null, 5, 100 ); 
			gpFD1.height =  myPatientRow.getPreferredContractedHeight();
		myPatientRow.setLayoutData( gpFD1 );
		
		myEncounterRow = new GroupBindingDialogSelectionRow( contentComp, SWT.NONE, GroupBinding.BY_ENCOUNTER, this );
		FormData gpFD2 = FormDataMaker.makeFormData(myPatientRow, (Integer)null, 5, 100 ); 
			gpFD2.height =  myEncounterRow.getPreferredContractedHeight();
		myEncounterRow.setLayoutData( gpFD2 );
		
		myObservationRow = new GroupBindingDialogSelectionRow( contentComp, SWT.NONE, GroupBinding.BY_OBSERVATION, this );		
		FormData gpFD3 = FormDataMaker.makeFormData(myEncounterRow, (Integer)null, 5, 100 ); 
			gpFD3.height =  myObservationRow.getPreferredContractedHeight();
		myObservationRow.setLayoutData( gpFD3 );

		// add this to the Row's RelayoutRequiredListener list
		myPatientRow.addRelayoutRequiredListener( this );
		myEncounterRow.addRelayoutRequiredListener( this );
		this.myObservationRow.addRelayoutRequiredListener( this );
		
		myShell.setSize( myShell.computeSize( SWT.DEFAULT, SWT.DEFAULT ));
		updateWidgets(); // set default values
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
		// Dialog Cancel button
		myCancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected( SelectionEvent e )
			{
				isCanceled = true;
				myShell.setVisible( false );
				myShell.dispose();
			}
		});
		
	}
	
	private void updateWidgets()
	{
		// allow GroupBindingPolicyProvider to set what is enabled.		
		myPatientRow.setEnabled( this.myGroup.getGroupBindingPolicyProvider().isByPatientEnabled( myGroup ) );
		myEncounterRow.setEnabled( this.myGroup.getGroupBindingPolicyProvider().isByEncounterEnabled( myGroup ) );
		myObservationRow.setEnabled( this.myGroup.getGroupBindingPolicyProvider().isByObservationEnabled( myGroup ) );
			
		// set the widgets to the state of the data
		if ( this.myGroup.getBinding() == GroupBinding.BY_PATIENT )
			this.selectButtonbyIndex( 0 );
		else if ( this.myGroup.getBinding() == GroupBinding.BY_ENCOUNTER )
			this.selectButtonbyIndex( 1 );
		else if ( this.myGroup.getBinding() == GroupBinding.BY_OBSERVATION )
			this.selectButtonbyIndex( 2 );
		assert false : "GroupBindingDialog.updateWidgets: unrecognized GroupBinding = '" + myGroup.getBinding() + "'";
	}

	
	public boolean	isCanceled()			{ return this.isCanceled; }
	
	public GroupBinding getSelectedValue()	
	{
		if ( this.mySelectedValue == null )
			this.mySelectedValue = myGroup.getBinding();
		return this.mySelectedValue; 
	}
		
	// open the dialog
	public void open()
	{
		if ( this.myInitLocation != null )
			myShell.setLocation( this.myInitLocation );
		
		myShell.setSize( myShell.computeSize( DIALOG_DEFAULT_WIDTH, DIALOG_DEFAULT_HEIGHT ) );
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


	/*
	 * IRadioButtonManager methods
	 */
	@Override
	public void addButton(Button radioButton) 
	{ myButtons.add( radioButton ); }

	@Override
	public void resetAllButtons() 
	{
		for ( Button b: myButtons )
			b.setSelection( false );
	}

	@Override
	public void buttonSelected(Control buttonOwner, Button targetButton) 
	{
		resetAllButtons();
		targetButton.setSelection( true );
		
		GroupBindingDialogSelectionRow row  = (GroupBindingDialogSelectionRow)buttonOwner;
		mySelectedValue = row.getValue();
	}

	@Override
	public void selectButtonbyIndex(int index) 
	{
		resetAllButtons();
		myButtons.get( index ).setSelection( true );			
	}

	/* LayoutRequiredListener method */
	@Override
	public void layoutRequired(Control control) 
	{
		this.contentComp.layout();
		int newHeight = myShell.computeSize( DIALOG_DEFAULT_WIDTH, SWT.DEFAULT ).y;
		if ( myShell.getBounds().height < newHeight )
			myShell.setSize( myShell.computeSize( DIALOG_DEFAULT_WIDTH, SWT.DEFAULT ) );
	}
	
	public static void main( String [] args )
	{
		Group testGroup = new Group("Test Group" );
		testGroup.setBinding( GroupBinding.BY_ENCOUNTER );
		GroupBindingDialog ncd = new GroupBindingDialog( testGroup, SWT.BORDER | SWT.CLOSE | SWT.RESIZE );
		ncd.open();
		
		System.err.println( "Selected value is '" + ncd.getSelectedValue() + "'");
	}



	
}
