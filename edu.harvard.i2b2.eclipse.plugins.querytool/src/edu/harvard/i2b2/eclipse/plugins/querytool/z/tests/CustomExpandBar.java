package edu.harvard.i2b2.eclipse.plugins.querytool.z.tests;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.DaemonThreadFactory;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;

public 	class CustomExpandBar extends Composite
{
	private static final Random RANDOM = new Random();
	
	private ScheduledExecutorService 	myAnimationExecutor = Executors.newSingleThreadScheduledExecutor( new DaemonThreadFactory() ); 
	private ScheduledFuture	<?>			myFuture			= null;
	private boolean						isMoving			= false;
	
	private Label		myLabel;
	private boolean 	isExpanded = false;
	
	private int			contractedHeight 	= 0;
	private int			expandedHeight		= 0;
	
	public CustomExpandBar(Composite parent, int style, String name)
	{
		super(parent, style);
		setupUI( name );
		attachListeners();
	}

	private void setupUI( String name )
	{
		this.setBackground( Colors.DARK_GRAY );
		this.setLayout( new FormLayout() );
		
		Composite labelComposite = new Composite( this, SWT.NONE );
		labelComposite.setLayout( new FormLayout() );
		labelComposite.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100));
		
		myLabel = new Label( labelComposite, SWT.NONE );
		myLabel.setText( name );
		//myLabel.setBackground( Colors.DUCKLING_FEATHER );
		myLabel.setForeground( Colors.BLACK );
		myLabel.setLayoutData( FormDataMaker.makeFormData(0, 0, (Integer)null, 0, 0, 0, 100, 0) );

		contractedHeight = myLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
		
		int numComps = RANDOM.nextInt(4) + 1;
		Control previousControl = labelComposite;
		for ( int i = 0; i < numComps; i++ )
		{
			Composite comp = new Composite( this, SWT.BORDER );
			FormData compFD = FormDataMaker.makeFormData( previousControl, 2, (Integer)null, 0, 0, 30, 100, -4);
			compFD.height = 30;
			comp.setLayoutData( compFD );
			comp.setBackground( Colors.WHITE );
			previousControl = comp;
		}
		
		myLabel.setText( name + " (" + numComps + ")" );
		expandedHeight = this.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
	}
		
	private void attachListeners()
	{
		MouseAdapter clicker = new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e) 
			{
				if ( isMoving )
					return; // don't do anything if it's already expanding/contracting
				isMoving = true;
				if (isExpanded)
					contract();
				else
					expand();
			}
		};
		
		this.addMouseListener( clicker );
		myLabel.addMouseListener( clicker );
	}
	
	public int getPreferredContractedHeight()
	{ return this.contractedHeight; }

	
	public void expand()
	{
		isExpanded = true;
		final Runnable expandAction = new Runnable()
		{			
			int counter = 0;
			@Override
			public void run() 
			{
				Display.getDefault().asyncExec( new Runnable()
				{
					@Override
					public void run() 
					{	
						FormData fd = (FormData)CustomExpandBar.this.getLayoutData();
						if ( fd.height >= expandedHeight )
						{
							myFuture.cancel( true );
							isMoving = false;
							counter = 0;
							return;
						}
						int distance = (int)(expandedHeight/(Math.pow(2, counter+1)));
						fd.height = Math.min( (fd.height + distance), expandedHeight);
						CustomExpandBar.this.getParent().layout();
						if ( counter < 9)
							counter++;
						else
							counter = 0;
					}
				});		
			}
		};
		
		myFuture = myAnimationExecutor.scheduleAtFixedRate( expandAction, 0, 30, TimeUnit.MILLISECONDS );
		
	}
	
	public void contract()
	{
		isExpanded = false;
		final Runnable contractAction = new Runnable()
		{
			int contractionStep = (expandedHeight - contractedHeight)/4 + 1;
			
			@Override
			public void run() 
			{
				Display.getDefault().asyncExec( new Runnable()
				{
					@Override
					public void run() 
					{	
						FormData fd = (FormData)CustomExpandBar.this.getLayoutData();
						if ( fd.height <= contractedHeight )
						{
							myFuture.cancel( true );
							isMoving = false;
							return;
						}						
						fd.height = Math.max( fd.height - contractionStep, contractedHeight);
						CustomExpandBar.this.getParent().layout();
					}
				});				
			}
		};
		
		myFuture = myAnimationExecutor.scheduleAtFixedRate( contractAction, 0, 30, TimeUnit.MILLISECONDS );
		
	}
	
	
}
