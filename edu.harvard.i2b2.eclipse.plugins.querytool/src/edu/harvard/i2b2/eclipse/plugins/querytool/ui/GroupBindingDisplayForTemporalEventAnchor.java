package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;

public class GroupBindingDisplayForTemporalEventAnchor extends GroupBindingDisplay 
{

	private static final String TOOL_TIP = "This binding anchors the Event, dictating its\n temporal ordering in relation to other Events.\n This binding is not changeable.";
	
	public GroupBindingDisplayForTemporalEventAnchor(Composite parent, int style, Group group) 
	{
		super(parent, style, group);
	}
	
	protected void setupUI() 
	{
		this.setLayout( new FormLayout() );

		myLabelComposite = new Composite( this, SWT.NONE );
		myLabelComposite.setLayout( new FormLayout() );
		myLabelComposite.setLayoutData( FormDataMaker.makeBorderingFormData() );
		
		/*
		myBindingImageLabel = new Label( myLabelComposite, SWT.LEFT  );	
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			PlatformUI.getWorkbench(); // try to see if we are launching from Workbench
			myBindingImageLabel.setImage( GroupBindingInfoRow.getLabelImage( myGroup.getBinding() ));
		}
		else
		{
			myBindingImageLabel.setText("<->");
			myBindingImageLabel.setForeground( Colors.DARK_RED );
		}
		myBindingImageLabel.setLayoutData( FormDataMaker.makeFormData(50, -myBindingImageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 0, (Integer)null, 0) );
		*/
		
		myBindingTextLabel = new Label( myLabelComposite, SWT.CENTER );
		myBindingTextLabel.setText( UIConst.OBSERVATION ); // set label to be the longest one before setting layout data
		FormData textFD = FormDataMaker.makeFormData( 50, -myBindingTextLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 0, (Integer)null, 0);
			textFD.width = myBindingTextLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;  
		myBindingTextLabel.setLayoutData( textFD );
		
		myBindingTextLabel.setToolTipText( TOOL_TIP );
		//myBindingImageLabel.setToolTipText( TOOL_TIP );
		updateDisplay();	
	}

	protected void attachListeners() 
	{
		// do nothing
	}

	@Override //this widget is always look inactive/disabled, but actually not
	protected void setActive(boolean flag) 
	{
		this.setBackground( Colors.GRAY );
		myBindingTextLabel.setBackground( Colors.LIGHT_LIGHT_GRAY );		
		//myBindingImageLabel.setBackground( Colors.LIGHT_LIGHT_GRAY );
		myLabelComposite.setBackground( Colors.LIGHT_LIGHT_GRAY );
		myBindingTextLabel.setForeground( Colors.DARK_GRAY );
		
		if ( flag )
		{
			myBindingTextLabel.setEnabled( true );
			//myBindingImageLabel.setEnabled( true );
		}
		else
		{
			myBindingTextLabel.setEnabled( false );
			//myBindingImageLabel.setEnabled( false );
		}
	}

	@Override 
	protected void updateDisplay()
	{
		myBindingTextLabel.setText( GroupBindingDialogSelectionRow.getLabelAnchorText() );
		//if (QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		//	myBindingImageLabel.setImage( GroupBindingDialogSelectionRow.getLabelAnchorImage() );
		//myBindingImageLabel.setLayoutData( FormDataMaker.makeFormData(50, -myBindingImageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 0, (Integer)null, 0) );
	}
	
	@Override /* DataChangedListener method -- listen for binding change and update display */
	public void dataChanged(Object source) 
	{
		updateDisplay();
	}
}
