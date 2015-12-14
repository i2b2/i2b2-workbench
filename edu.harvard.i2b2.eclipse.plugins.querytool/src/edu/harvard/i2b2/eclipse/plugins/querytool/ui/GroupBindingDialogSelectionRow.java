package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.LayoutRequiredListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.IRadioButtonManager;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.ExpandBar;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class GroupBindingDialogSelectionRow extends ExpandBar implements SelectionListener
{
	
	protected Button	myButton;
	//protected Label 	myImageLabel;
	protected Label 	myHelpLabel;
	
	protected Composite myHelpComp;
	protected Label		myHelpText;
	
	protected IRadioButtonManager 	myButtonManager = null;
	protected GroupBinding 			myValue = null;
	
	protected boolean				firstClick = true;
	
	protected ArrayList<LayoutRequiredListener> myLayoutRequiredListeners;
	
	public GroupBindingDialogSelectionRow(Composite parent, int style, GroupBinding val, IRadioButtonManager buttonManager )
	{
		super(parent, style);
		myLayoutRequiredListeners = new ArrayList<LayoutRequiredListener>();
		myValue = val;
		myButtonManager = buttonManager;
		setupUI();
		attachListeners();
	}

	@Override
	protected void setupUI() 
	{
		this.setLayout( new FormLayout() );

		Composite labelComposite = new Composite( this, SWT.NONE );
		labelComposite.setLayout( new FormLayout() );
		labelComposite.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100));

		myButton = new Button( labelComposite, SWT.RADIO );
		myButton.setSelection( false );

		/*
		myImageLabel = new Label( labelComposite, SWT.NONE );
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			PlatformUI.getWorkbench(); // try to cause an IllegalStateException
			myImageLabel.setImage( GroupBindingInfoRow.getLabelImage( myValue ) );
		}
		else		
		{
			myImageLabel.setText("<->");
			myImageLabel.setForeground( Colors.DARK_RED );
		}
		*/
		
		myTextLabel = new Label( labelComposite, SWT.NONE );
		myTextLabel.setText( GroupBindingInfoRow.getLabelText( myValue) );
		myTextLabel.setForeground( Colors.BLACK );
		
		myHelpLabel = new Label( labelComposite, SWT.NONE );
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
			myHelpLabel.setImage( Images.getImageByKey( Images.HELP_INACTIVE ));
		else
		{
			myHelpLabel.setForeground( Colors.LIGHT_TURQUOIS );
			myHelpLabel.setText("?");
		}

		myButton.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, (Integer)null ) );
		//myImageLabel.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, myButton, (Integer)null ) );
		myTextLabel.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, myButton, (Integer)null ) );
		myHelpLabel.setLayoutData( FormDataMaker.makeFormData(0, 0, (Integer)null, 0, myTextLabel, 3, (Integer)null, 0 ) );

		contractedHeight = labelComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
		
		myHelpComp = new Composite( this, SWT.NONE );
		myHelpComp.setLayout( new FormLayout() );
		FormData helpFD = FormDataMaker.makeFormData( labelComposite, 0, 100, 0, 0, 20, 95, 0);
		myHelpComp.setLayoutData( helpFD );
		
		myHelpText = new Label( myHelpComp, SWT.WRAP );
		myHelpText.setText( GroupBindingInfoRow.getHelpText( myValue ) );
		myHelpText.setForeground( Colors.DARK_GRAY );
		myHelpText.setLayoutData( FormDataMaker.makeFullFormData() );
		
		expandedHeight = this.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
		myButtonManager.addButton( this.myButton );
	}

	@Override
	protected void attachListeners() 
	{
		this.addControlListener( new ControlAdapter()
		{
			@Override
			public void controlResized(ControlEvent e) // when resizing, recompute expanded height and relayout at the parent level
			{
				if ( isExpanded )
				{
					recomputeExpandedHeight();
					((FormData)GroupBindingDialogSelectionRow.this.getLayoutData()).height = expandedHeight;
					//GroupBindingDialogSelectionRow.this.getParent().layout( );
					notifyRelayoutRequiredListeners();
				}
			}
		});
		
		// change icon on mousing-in/out if we are launching from the workbench
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
			myHelpLabel.addMouseTrackListener( new MouseTrackListener()
			{
				@Override
				public void mouseEnter(MouseEvent e) 
				{ myHelpLabel.setImage( Images.getImageByKey( Images.HELP_ACTIVE ) ); }
				@Override
				public void mouseExit(MouseEvent e) 
				{ myHelpLabel.setImage( Images.getImageByKey( Images.HELP_INACTIVE ) ); }
				@Override
				public void mouseHover(MouseEvent e) {}
			});
		
		myHelpLabel.addMouseListener( new MouseListener()
		{
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
			@Override
			public void mouseDown(MouseEvent e) 
			{
				if ( isMoving )
					return; // don't do anything if it's already expanding/contracting
				isMoving = true;
				if (isExpanded)
					contract();
				else
				{	
					recomputeExpandedHeight();
					expand();
				}
			}
			@Override
			public void mouseUp(MouseEvent e) {}
		});
		
		/* Make image and text labels also mouse targets for selecting the radio button */
		MouseListener labelClickedListener = new MouseAdapter()
		{
			@Override // notify buttonManager that this button is clicked.
			public void mouseDown(MouseEvent e) 
			{
				if ( !myButton.getEnabled() ) // no not respond to mosue click if button is disabled
					return;
				myButtonManager.buttonSelected( GroupBindingDialogSelectionRow.this, GroupBindingDialogSelectionRow.this.myButton ); 
			}
		};		
		//this.myImageLabel.addMouseListener( labelClickedListener );
		this.myTextLabel.addMouseListener( labelClickedListener );
		
		this.myButton.addSelectionListener( this );		
	}

	public void setButtonSelected( boolean flag )
	{
		this.myButton.setSelection( flag );
	}

	@Override
	public void setEnabled( boolean flag )
	{
		this.myButton.setEnabled( flag );
		//this.myImageLabel.setEnabled( flag );
		if ( flag )
			this.myTextLabel.setForeground( Colors.BLACK );
		else
			this.myTextLabel.setForeground( Colors.GRAY );
	}
	
	public GroupBinding getValue()
	{ return this.myValue; }
	
	protected void recomputeExpandedHeight()
	{
		int helpTextHeight = myHelpText.computeSize(myHelpText.getBounds().width, SWT.DEFAULT).y;
		((FormData)myHelpComp.getLayoutData()).height = helpTextHeight;
		expandedHeight =	GroupBindingDialogSelectionRow.this.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
	}	
	
	public void addRelayoutRequiredListener( LayoutRequiredListener list )
	{ myLayoutRequiredListeners.add( list ); }
	
	public void removeRelayoutRequiredListener( LayoutRequiredListener list )
	{ myLayoutRequiredListeners.remove( list ); }
	
	public void notifyRelayoutRequiredListeners()
	{
		for ( LayoutRequiredListener listener : myLayoutRequiredListeners )
			listener.layoutRequired( this );
	}
	
	public static Image getLabelAnchorImage()
	{
		return Images.getImageByKey( Images.TEMPORAL_ANCHOR );
	}
	
	public static String getLabelAnchorText()
	{
		return "Anchor";
	}

	/*
	 * SelectionListener Methods
	 */
	@Override
	public void widgetSelected(SelectionEvent e) 
	{
		this.myButtonManager.buttonSelected( this, (Button)e.getSource() );
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}
	
	
	public static void main( String [] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		IRadioButtonManager mnger = new IRadioButtonManager()
		{
			ArrayList<Button> myButtons = new ArrayList<Button>(); 
			@Override
			public void addButton(Button radioButton) 
			{
				myButtons.add( radioButton );
			}

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
			}

			@Override
			public void selectButtonbyIndex(int index) 
			{
				resetAllButtons();
				myButtons.get( index ).setSelection( true );			
			}};
			
		GroupBindingDialogSelectionRow gp1 = new GroupBindingDialogSelectionRow( myShell, SWT.NONE, GroupBinding.BY_PATIENT, mnger );
		FormData gpFD1 = FormDataMaker.makeFormData(0, (Integer)null, 0, 100 ); 
			gpFD1.height =  gp1.contractedHeight;
		gp1.setLayoutData( gpFD1 );
		
		GroupBindingDialogSelectionRow gp2 = new GroupBindingDialogSelectionRow( myShell, SWT.NONE, GroupBinding.BY_ENCOUNTER, mnger );
		FormData gpFD2 = FormDataMaker.makeFormData(gp1, (Integer)null, 0, 100 ); 
			gpFD2.height =  gp2.contractedHeight;
		gp2.setLayoutData( gpFD2 );
		GroupBindingDialogSelectionRow gp3 = new GroupBindingDialogSelectionRow( myShell, SWT.NONE, GroupBinding.BY_OBSERVATION, mnger );
		FormData gpFD3 = FormDataMaker.makeFormData(gp2, (Integer)null, 0, 100 ); 
			gpFD3.height =  gp3.contractedHeight;
		gp3.setLayoutData( gpFD3 );
		
		Composite comp = new Composite( myShell, SWT.NONE );
		comp.setBackground( Colors.DARK_RED );
		comp.setLayoutData( FormDataMaker.makeFormData( gp3, 100, 0, 100));
		myShell.setSize( 450, 200 );
		
		myShell.open();
		//mnger.selectButtonbyIndex(1 );
		
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
