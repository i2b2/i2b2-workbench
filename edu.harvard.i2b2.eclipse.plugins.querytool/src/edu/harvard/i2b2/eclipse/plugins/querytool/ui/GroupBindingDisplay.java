package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DateRangeChangeListeneer;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class GroupBindingDisplay extends QueryToolPanelComposite implements UIConst, DataChangedListener
{

	protected Composite 		myLabelComposite;
	//protected Label			myBindingImageLabel;
	protected Label			myBindingTextLabel;	
	
	protected Group 			myGroup = null;
	
	public GroupBindingDisplay(Composite parent, int style, Group group ) 
	{
		super(parent, style);
		myGroup = group;
		
		setupUI();
		attachListeners();
	}
	
	/*
	public GroupBindingDisplay(Composite parent, int style) 
	{
		super(parent, style);
		setupUI();
		attachListeners();
	}
	 */
	
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
		
		updateDisplay();	
	}

	protected void attachListeners() 
	{
		myGroup.addDataChangedListener( this );
		MouseListener showGroupBindingDialogOnClick =  new MouseAdapter()
		{
			public void mouseDown(MouseEvent arg0) 
			{				
				GroupBindingDialog gbd = new GroupBindingDialog( myGroup, SWT.NONE );
				Point preferred = gbd.getPreferredSize();
				Point startingPoint = GroupBindingDisplay.this.toDisplay( 0, 0 );
				startingPoint.x = Math.max(5, startingPoint.x);
				startingPoint.y = Math.max(5, startingPoint.y);
				startingPoint.x = Math.min( Display.getCurrent().getBounds().width - preferred.x - 5, startingPoint.x);
				startingPoint.y = Math.min( Display.getCurrent().getBounds().height - preferred.y - 5, startingPoint.y);
				gbd.setLocation( startingPoint );
				
				// try disable workbench
				GroupBindingDisplay.this.setVisualActivationListeners();	// tell listener to NOT set this disabled
				HashSet<Control> alreadyDisabledControls = new HashSet<Control>();
				try
				{ UIUtils.recursiveSetEnabledAndRememberUnchangedControls( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), false, alreadyDisabledControls ); }
				catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to disable Workbench window when it does not exist (DateConstraintDisplay.attachListener): " + e.toString() ); }

				gbd.open();
				
				// try enable workbench
				try
				{ UIUtils.recursiveSetEnabled( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true, alreadyDisabledControls ); }
				catch ( IllegalStateException e ){ System.err.println("Non-Fatal Warning: Attempting to enable Workbench window when it does not exist (DateConstraintDisplay.attachListener): " + e.toString() ); }
				GroupBindingDisplay.this.resetVisualActivationListeners();	// tell listener to NOT set this disabled
				
				if ( !gbd.isCanceled() )
				{
					myGroup.setBinding( gbd.getSelectedValue() );
				}

			}
		};
		
		myBindingTextLabel.addMouseListener( showGroupBindingDialogOnClick );
		//myBindingImageLabel.addMouseListener( showGroupBindingDialogOnClick );
	}

	protected void updateDisplay()
	{
		myBindingTextLabel.setText( GroupBindingInfoRow.getLabelText( myGroup.getBinding() ) );
		//if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		//	myBindingImageLabel.setImage( GroupBindingInfoRow.getLabelImage( myGroup.getBinding() ) );		
		myBindingTextLabel.setForeground( Colors.BLACK );
		//myBindingImageLabel.setLayoutData( FormDataMaker.makeFormData(50, -myBindingImageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 0, (Integer)null, 0) );
	}

	
	@Override
	protected void setActive(boolean flag) 
	{
		if ( flag )
		{
			this.setBackground( Colors.ORANGE );
			myBindingTextLabel.setEnabled( true );
			myBindingTextLabel.setBackground( Colors.WHITE );
			//myBindingImageLabel.setEnabled( true );
			//myBindingImageLabel.setBackground( Colors.WHITE );
			myLabelComposite.setBackground( Colors.WHITE );
		}
		else
		{
			this.setBackground( Colors.GRAY );
			myBindingTextLabel.setEnabled( false );
			myBindingTextLabel.setBackground( Colors.LIGHT_LIGHT_GRAY );
			//myBindingImageLabel.setEnabled( false );
			//myBindingImageLabel.setBackground( Colors.LIGHT_LIGHT_GRAY );
			myLabelComposite.setBackground( Colors.LIGHT_LIGHT_GRAY );
		}
	}


	@Override /* DataChangedListener method -- listen for binding change and update display */
	public void dataChanged(Object source) 
	{
		updateDisplay();
	}
	
}
