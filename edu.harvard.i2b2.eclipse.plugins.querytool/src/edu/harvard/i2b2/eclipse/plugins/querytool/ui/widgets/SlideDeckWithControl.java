package edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;

public class SlideDeckWithControl extends SlideDeck 
{
	public static final String NEXT 	= "  Next  ";
	public static final String PREVIOUS = "Previous";

	protected Composite myControlComposite;
	protected Button	myNextButton;
	protected Button	myPrevButton;
	
	public SlideDeckWithControl(Composite parent, int style) 
	{
		super(parent, style);
	}
	
	@Override
	protected void initialize()
	{
		super.initialize();
		
		myControlComposite = new Composite( this, SWT.NONE );
		myControlComposite.setLayout( new FormLayout() );
		myControlComposite.setBackground( Colors.BLACK );
		
		myNextButton = new Button( myControlComposite, SWT.FLAT | SWT.CENTER);
		myPrevButton = new Button( myControlComposite, SWT.FLAT | SWT.CENTER);
		myNextButton.setText( NEXT );
		myPrevButton.setText( PREVIOUS );
		
		if ( QueryToolRuntime.getInstance().isLaunchedFromWorkbench() )
		{
			myNextButton.setImage( Images.getImageByKey( Images.RIGHT_ARROW_TRIMMED_BLACK ) );
			myPrevButton.setImage( Images.getImageByKey( Images.LEFT_ARROW_TRIMMED_BLACK ) );
		}
		myControlComposite.setLayoutData( FormDataMaker.makeFormData((Integer)null, 100, 0, 100));
		((FormData)myControlComposite.getLayoutData()).height = myNextButton.computeSize(SWT.DEFAULT, SWT.DEFAULT ).y;
		
		myNextButton.setLayoutData( FormDataMaker.makeFormData((Integer)null, 0, 100, 0, 50, 10, (Integer)null, 0 ));
		myPrevButton.setLayoutData( FormDataMaker.makeFormData((Integer)null, 0, 100, 0, (Integer)null, 0, 50, -10 ));
		
		((FormData)myNextButton.getLayoutData()).width = myPrevButton.computeSize(SWT.DEFAULT, SWT.DEFAULT ).x;
		
		myNextButton.addSelectionListener( new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{ 
				slideNext();
			}
		});
		
		myPrevButton.addSelectionListener( new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{ 
				slidePrevious();
			}
		});
		
		autoLayoutAndSetButtons();
	}
	
	@Override
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
		autoLayoutAndSetTransitionControls();
		myFarthestSlideIndex = Math.max(myFarthestSlideIndex, myCurrentSlideIndex);
	}
	
	// create a new slide and return it for users to customize
	@Override
	public Composite makeNewSlide()
	{
		DefaultSlideWithTransitionControls slide = new DefaultSlideWithTransitionControls( this, SWT.NONE );
		slide.setLayoutData( makeFullFormData() );
		
		mySlides.add( slide );
		
		if ( mySlides.size() == 1 ) // first slide is created, set the starting slide, and set others invisible
			myCurrentSlideIndex = 0;			
		else
			slide.setVisible( false );
		autoLayoutAndSetTransitionControls();
		myFarthestSlideIndex = Math.max(myFarthestSlideIndex, myCurrentSlideIndex);
		return slide;
	}

	
	@Override
	protected void performPreSlideActions( int toSlideIndex )
	{
		// before transitioning, hide the transitionControls of the current slide (if they exist)
		AbstractSlideWithTransitionControls currentSlide = mySlides.get( myCurrentSlideIndex );
		currentSlide.setPrevTransitionControlVisible( false );
		currentSlide.setNextTransitionControlVisible( false );
		
		// perform slide-specific tasks
		currentSlide.performPreSlideActions( toSlideIndex );
	}

	// after sliding is performed myCurrentIndex is changed, so we set the buttons appropriately
	@Override
	protected void performPostSlideActions( int fromSlideIndex )
	{
		autoLayoutAndSetTransitionControls();
		
		// perform slide-specific tasks
		AbstractSlideWithTransitionControls currentSlide = mySlides.get( myCurrentSlideIndex );
		currentSlide.performPostSlideActions( fromSlideIndex );
	}

	// Automatically set the visibility of buttons by current slide position.
	// Sets appropriate buttons' availability according to the policy attached to this SlideDeck
	public void autoLayoutAndSetTransitionControls()
	{
		AbstractSlideWithTransitionControls currentSlide = mySlides.get( myCurrentSlideIndex );
		Composite prevTransitionControl = currentSlide.getPrevTransitionControl( this.myControlComposite, myNextButton );
		Composite nextTransitionControl = currentSlide.getNextTransitionControl( this.myControlComposite, myPrevButton );
		
		if ( prevTransitionControl == null && nextTransitionControl == null )
			autoLayoutAndSetButtons();
		else if ( prevTransitionControl == null && nextTransitionControl != null )
		{
			// hide nextButton
			myNextButton.setVisible( false );
			// show nextTransitionControl
			nextTransitionControl.setVisible( true );
			
			//enable prev button if necessary
			this.myPrevButton.setEnabled( myTransitionPolicy.canTransitionTo( this.myCurrentSlideIndex,  this.myCurrentSlideIndex-1 ) );
			nextTransitionControl.setEnabled( myTransitionPolicy.canTransitionTo( this.myCurrentSlideIndex,  this.myCurrentSlideIndex+1 ) );
		}
		else // the case that ( prevTransitionControl != null && nextTransitionControl == null )
		{
			// hide prevButton
			myPrevButton.setVisible( false );
			// show prevTransitionControl
			prevTransitionControl.setVisible( true );
			
			// enable next button if necessary
			this.myNextButton.setEnabled( myTransitionPolicy.canTransitionTo( this.myCurrentSlideIndex,  this.myCurrentSlideIndex+1 ) );
			prevTransitionControl.setEnabled( myTransitionPolicy.canTransitionTo( this.myCurrentSlideIndex,  this.myCurrentSlideIndex-1 ) );
		}
		this.myControlComposite.layout();
	}
	
	public void autoLayoutAndSetButtons()
	{
		if ( myCurrentSlideIndex == -1 )
		{
			myNextButton.setVisible( false );
			myPrevButton.setVisible( false );
		}
		else if ( myCurrentSlideIndex == 0 && this.mySlides.size() == 1 )
		{
			myNextButton.setVisible( false );
			myPrevButton.setVisible( false );

		}
		else if ( myCurrentSlideIndex == 0)
		{
			myControlComposite.setVisible( true );
			myNextButton.setVisible( true );
			myPrevButton.setVisible( false );
		}
		else if ( myCurrentSlideIndex == this.mySlides.size()-1 )
		{
			myControlComposite.setVisible( true );
			myNextButton.setVisible( false );
			myPrevButton.setVisible( true );
		}
		else
		{
			myControlComposite.setVisible( true );
			myNextButton.setVisible( true );
			myPrevButton.setVisible( true );
		}
		autoSetButtonsEnabled();
	}
	
	public void autoSetButtonsEnabled()
	{
		this.myNextButton.setEnabled( myTransitionPolicy.canTransitionTo( this.myCurrentSlideIndex,  this.myCurrentSlideIndex+1 ) );
		this.myPrevButton.setEnabled( myTransitionPolicy.canTransitionTo( this.myCurrentSlideIndex,  this.myCurrentSlideIndex-1 ) );
	}

	/* Override the following 3 methods for customized layout */
	@Override
	protected FormData makeFullFormData()								{ return FormDataMaker.makeFormData(0, this.myControlComposite, 0, 100); }
	@Override
	protected FormData makeSlideLeftFormData( Composite currentSlide )	{ return FormDataMaker.makeFormData(0, 0, this.myControlComposite, 0, currentSlide, 0, (Integer)null , 0); }
	@Override
	protected FormData makeSlideRightFormData( Composite currentSlide )	{ return FormDataMaker.makeFormData(0, 0, this.myControlComposite, 0, (Integer)null, 0, currentSlide, 0 ); }


	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		final SlideDeckWithControl sd = new SlideDeckWithControl( myShell, SWT.NONE );
		sd.setLayoutData( FormDataMaker.makeFullFormData() );
		
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
		
		Composite comp3 = sd.makeNewSlide();
		comp3.setBackground( Colors.INDIGO );

		
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
