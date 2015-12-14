package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Fonts;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;

public class QueryToolWelcomeScreen extends Composite 
{
	private static final String WELCOME_MSG = "Welcome to the Temporal Query Tool. Follow these 3 steps to construct a temporal query:";
	private static final int	COMP_HEIGHT	= 60;

	private Label 	myWelcomeLabel;
	private boolean isMouseOnTextAndLabel = false;
	
	private Label 	myTextLabel;
	private Label 	myImageLabel;
	
	public QueryToolWelcomeScreen(QueryToolMainUI parent) 
	{
		super(parent, SWT.NONE );
		setupUI();
	}
	
	// for testing purposes only
	private QueryToolWelcomeScreen(Composite parent, int style) 
	{
		super(parent, style);
		setupUI();
	}

	private void setupUI() 
	{
		this.setLayout( new FormLayout());
		this.setLayoutData( FormDataMaker.makeFullFormData() );
		this.setBackground( Colors.BLACK );
		
		myWelcomeLabel = new Label( this, SWT.WRAP );
		myWelcomeLabel.setText( WELCOME_MSG );		
		myWelcomeLabel.setLayoutData( FormDataMaker.makeFormData(0, 10, (Integer)null, 0, 0, 30, (Integer)null, 0) );
		myWelcomeLabel.setBackground( Colors.BLACK );
		myWelcomeLabel.setForeground( Colors.ORANGE );
				
		
		//int yStart 	= 10 + myWelcomeLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
		//int height 	= nextComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y - yStart;

		Composite comp1 = makeLabelComposite("1. Define a patient population" );
		comp1.setLayoutData( FormDataMaker.makeFormData( myWelcomeLabel, 20,  (Integer)null, 0, 0, 30, 100, -30 ) );
		((FormData)comp1.getLayoutData()).height = COMP_HEIGHT;
		
		Composite comp2 = makeLabelComposite("2. Add temporal relationships" );
		comp2.setLayoutData( FormDataMaker.makeFormData( comp1, 2,  (Integer)null, 0, 0, 30, 100, -30 ) );
		((FormData)comp2.getLayoutData()).height = COMP_HEIGHT;

		Composite comp3 = makeLabelComposite("3. Review and submit query" );
		comp3.setLayoutData( FormDataMaker.makeFormData( comp2, 2,  (Integer)null, 0, 0, 30, 100, -30 ) );
		((FormData)comp3.getLayoutData()).height = COMP_HEIGHT;

		Composite nextComp = makeActionComposite();
		nextComp.setLayoutData( FormDataMaker.makeFormData( comp3, 10, (Integer)null, -10, 0, 30, 100, -30 ) );
		((FormData)nextComp.getLayoutData()).height = COMP_HEIGHT;

	}

	private Composite makeLabelComposite( String labelText )
	{
		Composite comp = new Composite( this, SWT.NONE );
		comp.setLayout( new FormLayout() );
		comp.setBackground( Colors.BLACK );
		
		Label label = new Label( comp, SWT.NONE );
		label.setFont( Fonts.LARGE_TAHOMA );
		label.setBackground(Colors.BLACK);
		label.setForeground( Colors.GRAY );
		label.setText( labelText );
		
		label.setLayoutData( FormDataMaker.makeFormData( 50, -label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 30, (Integer)null, 0) );
		
		return comp;
	}

	private Composite makeActionComposite()
	{
		Composite comp = new Composite( this, SWT.NONE );
		comp.setLayout( new FormLayout() );
		comp.setBackground( Colors.BLACK );

		myTextLabel	 = new Label(comp, SWT.NONE );
		myTextLabel.setFont( Fonts.MEDIUM_TAHOMA );
		myTextLabel.setBackground( Colors.BLACK );
		myTextLabel.setForeground( Colors.WHITE );

		myImageLabel = new Label(comp, SWT.NONE );			
		myImageLabel.setFont( Fonts.MEDIUM_TAHOMA );
		myImageLabel.setBackground( Colors.BLACK );
		myImageLabel.setForeground( Colors.WHITE );

		myTextLabel.setText("Start here");
		myImageLabel.setText("->");

		//int width = textLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + imageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		myImageLabel.setLayoutData( FormDataMaker.makeFormData( 0, 10, (Integer)null, 0, (Integer)null, 0, 100, -10  ));
		myTextLabel.setLayoutData( FormDataMaker.makeFormData( 0, 10, (Integer)null, 0, (Integer)null, 0, myImageLabel, 0) );
		if (QueryToolRuntime.getInstance().isLaunchedFromWorkbench())
		{
			myImageLabel.setText("");
			myImageLabel.setImage( Images.getImageByKey( Images.RIGHT_ARROW) );
			//width = textLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + imageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			myImageLabel.setLayoutData( FormDataMaker.makeFormData( 0, 10, (Integer)null, 0, (Integer)null, 0, 100, -10  ));
			myTextLabel.setLayoutData( FormDataMaker.makeFormData( 0, 10 + myImageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2-myTextLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, (Integer)null, 0, myImageLabel, 0) );
		}
		
		
				

		
		final Button button = new Button( comp, SWT.CHECK );
		button.setBackground( Colors.BLACK );
		button.setSelection( true );
		button.setLayoutData( FormDataMaker.makeFormData(0, 10 + myImageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y - button.computeSize(SWT.DEFAULT, SWT.DEFAULT).y, (Integer)null, 0, 0, 0, (Integer)null, 0 ) );
		Label label = new Label( comp, SWT.NONE );
		label.setText("Don't show me this screen next time.");
		label.setBackground( Colors.BLACK );
		label.setForeground( Colors.GRAY );
		label.setLayoutData( FormDataMaker.makeFormData(0, 10 + myImageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y - label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y, (Integer)null, 0, button, 0, (Integer)null, 0) );
		
		
		
		/*
		 * Now we add listeners
		 */
		
		// selecting label is like selecting the button
		label.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e) 
			{ button.setSelection( !button.getSelection() ); }
		});
				
		// track mouse movements so to highlight image and text labels.
		MouseTrackListener tracker = new MouseTrackListener()
		{
			@Override
			public void mouseEnter(MouseEvent e)	{ setTextAndImage( true );}
			@Override
			public void mouseExit(MouseEvent e) 	{ setTextAndImage( false ); }
			@Override
			public void mouseHover(MouseEvent e) 	{}
		};

		// when image and text labels are clicked, transition to the next screen
		MouseAdapter mouseClicker = new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e) 
			{ 
				/*
				if (QueryToolRuntime.getInstance().isLaunchedFromWorkbench())
				{
					((QueryToolMainUI)getParent()).slideNext();
					Settings.getInstance().setIsShowingWelcomeScreen( button.getSelection() ); // remember whether to show Welcome Screen next time 
				}
				*/
			}
		};
		
		myImageLabel.addMouseTrackListener( tracker );
		myTextLabel.addMouseTrackListener( tracker );
		myImageLabel.addMouseListener( mouseClicker );
		myTextLabel.addMouseListener( mouseClicker );
		
		return comp;
	}

	public void setTextAndImage( boolean isMouseOn )
	{
		if ( isMouseOn == isMouseOnTextAndLabel )
			return;
		isMouseOnTextAndLabel = isMouseOn;
		if (QueryToolRuntime.getInstance().isLaunchedFromWorkbench())
		{
			if (isMouseOnTextAndLabel)
			{
				myTextLabel.setForeground( Colors.ORANGE );
				myImageLabel.setImage( Images.getImageByKey( Images.RIGHT_ARROW_ACTIVATED)  );
			}
			else
			{
				myTextLabel.setForeground( Colors.WHITE );
				myImageLabel.setImage( Images.getImageByKey( Images.RIGHT_ARROW )  );
			}
		}
		else
		{
			if (isMouseOnTextAndLabel)
			{
				myImageLabel.setForeground( Colors.ORANGE );
				myTextLabel.setForeground( Colors.ORANGE );
			}
			else
			{
				myImageLabel.setForeground( Colors.WHITE );
				myTextLabel.setForeground( Colors.WHITE );
			}
		}
	}
	
	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		QueryToolWelcomeScreen screen = new QueryToolWelcomeScreen( myShell, SWT.NONE );
		
		myShell.setSize( 600 , 600 );
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
