package edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.SlideDeckListener;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;

public class SlideDeck extends Composite
{
	private static final int DEFAULT_FRAME_DISPLAY_TIME = 40; // number of miliseconds a frame is displayed during sliding animation
	
	public enum SlideEventType { PRE_SLIDE, POST_SLIDE};
	
	public enum Direction {RIGHT, LEFT};
	
	protected ScheduledExecutorService 	myAnimationExecutor = Executors.newSingleThreadScheduledExecutor( new SlideDeckThreadFactory() ); 
	protected ScheduledFuture	<?>			myFuture			= null;
	protected boolean						isMoving			= false;

	protected ArrayList<AbstractSlideWithTransitionControls>	mySlides;
	protected int						myCurrentSlideIndex;
	protected int						myFarthestSlideIndex;
	
	protected ArrayList<SlideDeckListener> 	myListeners;
	
	protected SlideDeckTransitionPolicy 	myTransitionPolicy = SlideDeckTransitionPolicy.ALL_OK_POLICY; // default policy is all-ok
	
	public SlideDeck(Composite parent, int style) 
	{
		super(parent, style);
		initialize();
	}
	
	protected void initialize()
	{
		this.setLayout( new FormLayout() );
		mySlides = new ArrayList<AbstractSlideWithTransitionControls>();
		myListeners = new ArrayList<SlideDeckListener>();
		myCurrentSlideIndex 	= -1;
		myFarthestSlideIndex 	= -1;
	}
	
	public void addListener( SlideDeckListener listener )
	{ myListeners.add( listener ); }
	
	public void removeListener( SlideDeckListener listener )
	{ myListeners.remove( listener ); }
	
	protected void notifyListeners( SlideEventType EventType, int fromIndex, int toIndex )
	{
		for ( SlideDeckListener listener: this.myListeners )
			listener.slideOccurred( this, EventType, fromIndex, toIndex );
	}
	
	// allow users to set the transition policy
	public void setTransitionPolicy( SlideDeckTransitionPolicy policy )
	{ this.myTransitionPolicy = policy; }
	
	public boolean canTransitionTo( int toSldeIndex )
	{ return this.myTransitionPolicy.canTransitionTo( this.myCurrentSlideIndex, toSldeIndex); }
	
	public void addNewSlide( AbstractSlideWithTransitionControls slide )
	{
		if ( slide.getParent() != this ) // not a slide for this deck
			return;
		if ( mySlides.contains( slide )) // already contains this slide 
			return;
		slide.setLayoutData( makeFullFormData() );
		mySlides.add( slide );		
		if ( mySlides.size() == 1 ) // first slide is created, set the starting slide, and set others invisible
			myCurrentSlideIndex = 0;			
		else
			slide.setVisible( false );
		myFarthestSlideIndex = Math.max(myFarthestSlideIndex, myCurrentSlideIndex);
	}
	
	// create a new slide and return it for users to customize
	public Composite makeNewSlide()
	{
		DefaultSlideWithTransitionControls slide = new DefaultSlideWithTransitionControls( this, SWT.NONE );
		slide.setLayoutData( makeFullFormData() );
		
		mySlides.add( slide );
		
		if ( mySlides.size() == 1 ) // first slide is created, set the starting slide, and set others invisible
			myCurrentSlideIndex = 0;
		else
			slide.setVisible( false );
		myFarthestSlideIndex = Math.max(myFarthestSlideIndex, myCurrentSlideIndex);
		return slide;
	}
	
	/* Override the following 3 methods for customized layout */
	protected FormData makeFullFormData()								{ return FormDataMaker.makeFullFormData(); }
	protected FormData makeSlideLeftFormData( Composite currentSlide )	{ return FormDataMaker.makeFormData(0, 0, 100, 0, currentSlide, 0, (Integer)null , 0); }
	protected FormData makeSlideRightFormData( Composite currentSlide )	{ return FormDataMaker.makeFormData(0, 0, 100, 0, (Integer)null, 0, currentSlide, 0 ); }


	public Composite getSlide( int k )		{ return this.mySlides.get( k ); }	
	public int getCurrentSlideIndex() 		{ return this.myCurrentSlideIndex; }
	public int getNumSlides()				{ return this.mySlides.size(); }
	public Composite getCurrentSlide() 		{ return this.getSlide(myCurrentSlideIndex); }
	public int getFarthestSlideIndex()		{ return this.myFarthestSlideIndex; }
	public void	resetFarthestSlideIndex()	{ this.myFarthestSlideIndex = 0; }
	
	public void slideNext()
	{ slideTo( this.myCurrentSlideIndex+1, Direction.LEFT); }
	
	public void slidePrevious()
	{ slideTo( this.myCurrentSlideIndex-1, Direction.RIGHT ); }
	
	public void slideTo( int index, Direction direction )
	{	
		index = constrainIndex(index);
		// make sure (-1 < k < mySlides.size())
		if (index == this.myCurrentSlideIndex )
		{
			System.err.println("SlideDeck.slideTo(): same slide, no transitioning");
			return;
		}
		if ( direction != null )
		{
			if ( direction == Direction.LEFT )
				slideLeftTo( index );
			else if ( direction == Direction.RIGHT )
				slideRightTo( index );
			else
				System.err.println("SlideDeck.slideTo(...): Direction [" + direction + "] is not recognized.");
		}
		else // automatically decide which direction to slide
		{
			if ( myCurrentSlideIndex + 1 == index )	// slide to show next (slide left to reveal next from right)
				slideLeftTo( index );
			else if ( myCurrentSlideIndex - 1 == index ) // slide to preivous (slide right reveal previous from left)
				slideRightTo( index );
			else
			{}
		}
	}

	protected int constrainIndex( int index )
	{
		while ( index >= (this.mySlides.size()) )
			index = index - this.mySlides.size();
		while ( index < 0)
			index = index + this.mySlides.size();
		return index;
	}

	protected void slideLeftTo( final int index )
	{	
		if ( isMoving == true )
			return;
		notifyListeners( SlideEventType.PRE_SLIDE, myCurrentSlideIndex, index );
		isMoving = true;
		performPreSlideActions( index );
		final int oldIndex = myCurrentSlideIndex; // remember what current slide is
		final Composite currentSlide 	= getCurrentSlide();
		final Composite rightComp		= getSlide(index);
		//rightComp.setLayoutData( FormDataMaker.makeFormData(0, 0, 100, 0, currentSlide, 0, 100, SlideDeck.this.getBounds().x ) );
		rightComp.setLayoutData( makeSlideLeftFormData( currentSlide ) );
		((FormData)rightComp.getLayoutData()).width = this.getBounds().width;
		rightComp.setVisible( true );
		final int width = currentSlide.getBounds().width;
		Runnable slideRightAction = new Runnable()
		{
			private int numFrames = 0;
			@Override
			public void run() 
			{					
				Display.getDefault().syncExec( new Runnable()
				{
					@Override
					public void run() 
					{
						FormData fd = (FormData)currentSlide.getLayoutData();
						fd.right.offset = fd.right.offset - width/5;
						fd.left.offset  = fd.left.offset - width/5;
						if ( numFrames == 4 )
						{
							myFuture.cancel( true );
							fd.right.offset = 0;
							fd.left.offset  = -currentSlide.getBounds().width;							
							rightComp.setLayoutData( makeFullFormData() );
							currentSlide.setVisible( false );
							SlideDeck.this.layout();
							myCurrentSlideIndex = index;							
							isMoving = false;
							myFarthestSlideIndex = Math.max(myFarthestSlideIndex, myCurrentSlideIndex);
							performPostSlideActions( oldIndex );
							notifyListeners( SlideEventType.POST_SLIDE, myCurrentSlideIndex, index );
							return;
						}
						else
							SlideDeck.this.layout();
						numFrames++;
					}
				});
			}
		};		
		myFuture = myAnimationExecutor.scheduleAtFixedRate( slideRightAction, 0, DEFAULT_FRAME_DISPLAY_TIME, TimeUnit.MILLISECONDS );
	}
	
	protected void slideRightTo( final int index ) 
	{
		if ( isMoving == true )
			return;
		isMoving = true;
		performPreSlideActions( index );
		notifyListeners( SlideEventType.PRE_SLIDE, myCurrentSlideIndex, index );		
		final Composite currentSlide 	= getCurrentSlide();
		final Composite leftComp		= getSlide(index);
		final int oldIndex 				= myCurrentSlideIndex;
		//leftComp.setLayoutData( FormDataMaker.makeFormData(0, 0, 100, 0, 0, -SlideDeck.this.getBounds().width, currentSlide, 0 ) );
		leftComp.setLayoutData( makeSlideRightFormData(currentSlide) );
		((FormData)leftComp.getLayoutData()).width = this.getBounds().width;
		
		leftComp.setVisible( true );
		final int width = currentSlide.getBounds().width;

		Runnable slideRightAction = new Runnable()
		{
			private int numFrames = 0;
			@Override
			public void run() 
			{					
				Display.getDefault().syncExec( new Runnable()
				{
					@Override
					public void run() 
					{
						FormData fd = (FormData)currentSlide.getLayoutData();
						fd.right.offset = fd.right.offset + width/5;
						fd.left.offset  = fd.left.offset + width/5;						
						if ( numFrames == 4 )
						{
							myFuture.cancel( true );
							fd.right.offset = currentSlide.getBounds().width+currentSlide.getBounds().width;
							fd.left.offset  = currentSlide.getBounds().width;							
							leftComp.setLayoutData( makeFullFormData() );
							currentSlide.setVisible( false );
							SlideDeck.this.layout();
							myCurrentSlideIndex = index;
							isMoving = false;
							performPostSlideActions( oldIndex );
							notifyListeners( SlideEventType.POST_SLIDE, myCurrentSlideIndex, index );
							return;
						}
						else
							SlideDeck.this.layout();
						numFrames++;
					}
				});
			}
		};
		myFuture = myAnimationExecutor.scheduleAtFixedRate( slideRightAction, 0, DEFAULT_FRAME_DISPLAY_TIME, TimeUnit.MILLISECONDS );
	}

	// perform actions after a slide is finished (not used here, but should be overridden by subclasses)
	protected void performPreSlideActions( int toSlideIndex ){}
	protected void performPostSlideActions( int fromSlideIndex ){}
	
	public void dispose()
	{
		super.dispose();
		if ( this.myFuture != null )
			this.myFuture.cancel( true );
		this.myAnimationExecutor.shutdownNow();
		this.myListeners.clear(); // remove all listeners
	}
	
	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		final SlideDeck sd = new SlideDeck( myShell, SWT.NONE );
		
		DefaultSlideWithTransitionControls comp1 = new DefaultSlideWithTransitionControls( sd, SWT.NONE );
		comp1.setLayout( new FormLayout() );
		sd.addNewSlide( comp1 );
		comp1.setBackground( Colors.GOLDENROD );
		Button button1 = new Button( comp1, SWT.PUSH );
		button1.setText( "Button 1" );
		button1.setLayoutData( FormDataMaker.makeFormData(0, 130, (Integer)null, 0, 0, 130, (Integer)null, 0 ) );
		
		DefaultSlideWithTransitionControls comp2 = new DefaultSlideWithTransitionControls( sd, SWT.NONE );
		comp2.setLayout( new FormLayout() );
		sd.addNewSlide( comp2 );
		comp2.setBackground( Colors.DARK_RED );
		Button button2 = new Button( comp2, SWT.PUSH );
		button2.setText( "Button 2" );
		button2.setLayoutData( FormDataMaker.makeFormData(0, 80, (Integer)null, 0, 0, 80, (Integer)null, 0 ) );
		
		Composite buttonComp = new Composite( myShell, SWT.NONE );
		buttonComp.setLayout( new FormLayout() );
		buttonComp.setBackground( Colors.BLACK );
		Button nextButton = new Button( buttonComp, SWT.PUSH );
		nextButton.setText("Next");
		nextButton.setLayoutData( FormDataMaker.makeFormData((Integer)null, 0, 100, 0, 50, 10, (Integer)null, 0) );
		Button prevButton = new Button( buttonComp, SWT.PUSH );
		prevButton.setText("Prev");
		prevButton.setLayoutData( FormDataMaker.makeFormData((Integer)null, 0, 100, 0, (Integer)null, 0, 50, -10 ) );
		
		buttonComp.setLayoutData( FormDataMaker.makeFormData((Integer)null, 100, 0, 100) );
		sd.setLayoutData( FormDataMaker.makeFormData(0, buttonComp, 0, 100) );
		
		Composite comp3 = sd.makeNewSlide();
		comp3.setBackground( Colors.INDIGO );
		
		nextButton.addSelectionListener( new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				sd.slideNext();
			}
		});
		
		prevButton.addSelectionListener( new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				sd.slidePrevious();
			}
		});

		
		myShell.setSize( 300 , 300 );
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
}

class SlideDeckThreadFactory implements ThreadFactory
{
	public static int numThreads = 0;

	public Thread newThread(Runnable runnable) 
	{
		Thread aThread = new Thread( runnable, "SlideDeckThread - " + numThreads );		
		aThread.setDaemon( true );
		aThread.setPriority( Thread.MIN_PRIORITY );
		numThreads++;
		return aThread;
	}
	
}


