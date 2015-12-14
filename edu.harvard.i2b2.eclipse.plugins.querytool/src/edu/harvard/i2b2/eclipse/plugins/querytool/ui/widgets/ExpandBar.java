package edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
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

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.DataChangedListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.ExpandContractListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.DaemonThreadFactory;


/*
 * Note 1: ExpandBar will only work if its parent uses Formlayout. The expanding/contraction
 * 			mechanism works off FormData.height
 * Note 2: Any ExpandBar (and its subclass) requires itself to set its FormData.height before
 * 			the expand/contract bahaviors take place. Otherwise it may not work properly. 
 */
public abstract class ExpandBar extends Composite
{
	protected ScheduledExecutorService 	myAnimationExecutor = Executors.newSingleThreadScheduledExecutor( new ExpandContractThreadFactory() ); 
	protected ScheduledFuture	<?>			myFuture			= null;
	protected boolean						isMoving			= false;
	
	protected Label		myTextLabel;
	protected boolean 	isExpanded = false;
	
	protected int		contractedHeight 	= 0;
	protected int		expandedHeight		= 0;

	protected ArrayList<ExpandContractListener> myECListeners;
	
	//protected ArrayList
	public ExpandBar(Composite parent, int style) 
	{
		super(parent, style);
		myECListeners = new ArrayList<ExpandContractListener>();
	}

	public boolean isExpanded()
	{ return this.isExpanded; }
	
	protected abstract void setupUI();
	/*
	{		
		this.setBackground( Colors.DARK_GRAY );
		this.setLayout( new FormLayout() );
		
		Composite labelComposite = new Composite( this, SWT.NONE );
		labelComposite.setLayout( new FormLayout() );
		labelComposite.setLayoutData( FormDataMaker.makeFormData(0, (Integer)null, 0, 100));
		
		myTextLabel = new Label( labelComposite, SWT.NONE );
		myTextLabel.setText( name );

		myTextLabel.setForeground( Colors.BLACK );
		myTextLabel.setLayoutData( FormDataMaker.makeFormData(0, 0, (Integer)null, 0, 0, 0, 100, 0) );

		contractedHeight = myTextLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
		
		int numComps = 3;
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
		
		myTextLabel.setText( name + " (" + numComps + ")" );
		expandedHeight = this.computeSize( SWT.DEFAULT, SWT.DEFAULT).y;
	}
	*/
	
	// default setupUI for demo, subclass should override it
	protected abstract void attachListeners();
	/*
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
		myTextLabel.addMouseListener( clicker );
	}
	 */
	
	// Notify ExpandContractListeners
	protected void notifyExpandContractListeners( boolean isExpansion )
	{
		for ( ExpandContractListener ecl : myECListeners )
			if ( isExpansion)
				ecl.controlExpanded( this );
			else
				ecl.controlContracted( this );
	}
	
	public void addExpandControlListener( ExpandContractListener list )
	{ this.myECListeners.add( list ); }
	
	public void removeAllExpandControlListeners()
	{ this.myECListeners.clear(); }


	public int getPreferredContractedHeight()
	{ return this.contractedHeight; }
	
	public int getPreferredExpandedHeight()
	{ return this.expandedHeight; }


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
						FormData fd = (FormData)ExpandBar.this.getLayoutData();
						if ( fd.height >= expandedHeight )
						{
							myFuture.cancel( true );
							isMoving = false;
							counter = 0;
							notifyExpandContractListeners( true );
							return;
						}
						int distance = (int)(expandedHeight/(Math.pow(2, counter+1)));
						fd.height = Math.min( (fd.height + distance), expandedHeight);
						ExpandBar.this.getParent().layout();
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
						FormData fd = (FormData)ExpandBar.this.getLayoutData();
						if ( fd.height <= contractedHeight )
						{
							myFuture.cancel( true );
							isMoving = false;
							notifyExpandContractListeners( false );
							return;
						}						
						fd.height = Math.max( fd.height - contractionStep, contractedHeight);
						ExpandBar.this.getParent().layout();
					}
				});	
			}
		};
		myFuture = myAnimationExecutor.scheduleAtFixedRate( contractAction, 0, 30, TimeUnit.MILLISECONDS );
	}

	@Override
	public void dispose()
	{
		super.dispose();
		if ( this.myFuture != null ) 
			this.myFuture.cancel( true );
		this.myAnimationExecutor.shutdownNow();
		this.removeAllExpandControlListeners();	// unlink to listeners;
	}
}

class ExpandContractThreadFactory implements ThreadFactory
{
	public static int numThreads = 0;

	public Thread newThread(Runnable runnable) 
	{
		Thread aThread = new Thread( runnable, "ExpandContractThread - " + numThreads );		
		aThread.setDaemon( true );
		aThread.setPriority( Thread.MIN_PRIORITY );
		numThreads++;
		return aThread;
	}
	
}
