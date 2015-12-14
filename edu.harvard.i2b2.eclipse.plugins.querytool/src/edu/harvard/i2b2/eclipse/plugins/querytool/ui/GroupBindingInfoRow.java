package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.ExpandBar;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class GroupBindingInfoRow extends Composite
{
	public static final String BOUND_BY_PATIENT_EXPLANATION 	= "Terms in Groups that are 'bound by Patient' must be from the same patient (default).";
	public static final String BOUND_BY_ENCOUNTER_EXPLANATION 	= "Terms in Groups that are 'bound by Encounter' must be from the same financial encounter (e.g. same visit to the hospital) in addition to being from the same patient.";
	public static final String BOUND_BY_OBSERVATION_EXPLANATION = "Terms in Groups that are 'bound by Observation' must be from the same instance of an observation (e.g. same instance of a lab test) in addition to being in the same financial encounter (e.g. same visit to the hospital) and by the same patient. This option is only available only if an applicable term such as a medication route (or more generally, a Modifier).";

	protected GroupBinding 			myValue = null;
	
	protected Label		myTextLabel;
	protected Label 	myImageLabel;
	
	protected Composite myHelpComp;
	protected Label		myHelpText;

	
	public GroupBindingInfoRow(Composite parent, int style, GroupBinding val )
	{
		super(parent, style);
		myValue = val;
		setupUI();
		attachListeners();
	}

	protected void setupUI() 
	{
		this.setLayout( new FormLayout() );

		Composite labelComposite = new Composite( this, SWT.NONE );
		labelComposite.setLayout( new FormLayout() );
		labelComposite.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100));

		myImageLabel = new Label( labelComposite, SWT.NONE );
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			PlatformUI.getWorkbench(); // try to cause an IllegalStateException
			myImageLabel.setImage( getLabelImage( myValue ) );
		}
		else		
		{
			myImageLabel.setText("<->");
			myImageLabel.setForeground( Colors.DARK_RED );
		}

		myTextLabel = new Label( labelComposite, SWT.NONE );
		myTextLabel.setText( getLabelText( myValue) );
		myTextLabel.setForeground( Colors.BLACK );
		
		myImageLabel.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, (Integer)null ) );
		myTextLabel.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, myImageLabel, (Integer)null ) );

		myHelpComp = new Composite( this, SWT.NONE );
		myHelpComp.setLayout( new FormLayout() );
		FormData helpFD = FormDataMaker.makeFormData( labelComposite, 0, 100, 0, 0, 20, 95, 0);
		myHelpComp.setLayoutData( helpFD );
		
		myHelpText = new Label( myHelpComp, SWT.WRAP );
		myHelpText.setText( getHelpText( myValue ) );
		myHelpText.setForeground( Colors.DARK_GRAY );
		myHelpText.setLayoutData( FormDataMaker.makeFullFormData() );
		
	}

	protected void attachListeners() 
	{ }

	public static String getHelpText( GroupBinding value )
	{
		if ( value == GroupBinding.BY_PATIENT )
			return BOUND_BY_PATIENT_EXPLANATION;
		else if ( value == GroupBinding.BY_ENCOUNTER )
			return BOUND_BY_ENCOUNTER_EXPLANATION;
		else if ( value == GroupBinding.BY_OBSERVATION )
			return BOUND_BY_OBSERVATION_EXPLANATION;
			
		assert false : "GroupBindingDialogSelectionRow.getHelpText(): Unrecognized GroupBinding '" + value + "'";
		return null;
	}
	
	public static Image getLabelImage( GroupBinding value ) 
	{		
		if ( value == GroupBinding.BY_PATIENT )
			return Images.getImageByKey( Images.BOUND_BY_PATIENT );
		else if ( value == GroupBinding.BY_ENCOUNTER )
			return Images.getImageByKey( Images.BOUND_BY_ENCOUNTER );
		else if ( value == GroupBinding.BY_OBSERVATION )
			return Images.getImageByKey( Images.BOUND_BY_OBSERVATION );
		assert false : "GroupBindingDialogSelectionRow.getLabelImage(): Unrecognized GroupBinding '" + value + "'";
		return null;
	}

	public static String getLabelText( GroupBinding value )
	{
		if ( value == GroupBinding.BY_PATIENT )
			return UIConst.BOUND_BY_PATIENT;
		else if ( value == GroupBinding.BY_ENCOUNTER )
			return UIConst.ENCOUNTER;
		else if ( value == GroupBinding.BY_OBSERVATION )
			return UIConst.OBSERVATION;
		
		assert false : "GroupBindingDialogSelectionRow.getLabelText(): Unrecognized GroupBinding '" + value + "'";
		return null;
	}

}
