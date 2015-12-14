package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.harvard.i2b2.eclipse.plugins.querytool.QueryToolRuntime;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.data.Group;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.GroupPanelConceptDragHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.GroupPanelConceptDropHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.GroupPanelGroupDragHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.events.dnd.GroupPanelGroupDropHandler;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Images;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIUtils;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets.MixedTextIconPanel;
import edu.harvard.i2b2.query.data.DataConst.GroupBinding;

public class GroupPanelForTemporalEventAnchor extends GroupPanel 
{
	private static String 	ANCHOR_GROUP_NAME 		= "Anchored by these Observations";
	private static int		DEFAULT_TITLE_HEIGHT 	= 10;
	
	// This constructor should only be used for testing purpoese
	private GroupPanelForTemporalEventAnchor(Composite parent, int style )
	{
		super(parent, style | SWT.BORDER);
	}

	/*
	public GroupPanelForTemporalEventAnchor(Composite parent, int style, Group group, GroupManager manager, String name, boolean areDatesEnabled, GregorianCalendar startDate, GregorianCalendar endDate, int number, int op) 
	{
		super(parent, style, group, manager, name, areDatesEnabled, startDate, endDate, number, op);		
	}
	*/
	
	public GroupPanelForTemporalEventAnchor(Composite parent, int style, GroupManager manager, String name, boolean areDatesEnabled, GregorianCalendar startDate, GregorianCalendar endDate, int number, int op )
	{
		super(parent, style, manager, name, areDatesEnabled, startDate, endDate, number, op );
		this.myGroup.setBinding( GroupBinding.BY_OBSERVATION );
	}

	public GroupPanelForTemporalEventAnchor(Composite parent, int style,  Group group, GroupManager manager, String name, boolean areDatesEnabled, GregorianCalendar startDate, GregorianCalendar endDate, int number, int op )
	{
		super(parent, style, group, manager, name, areDatesEnabled, startDate, endDate, number, op );
		this.myGroup.setBinding( GroupBinding.BY_OBSERVATION );
	}

	
	/* very similar to the setupUI of the parent class GroupPanel, with only a few exceptions:
	 * 	1. No close button. (Users cannot remove this time-anchoring group)
	 *  2. Timing is restricted to "time-anchoring observations"
	 *  3. No Timing widget
	 *  4. Title is not Group 1
	 */
	protected void setupUI() 
	{
		this.setLayout( new FormLayout() );
		
		Point singleCharSize = UIUtils.getDefaultCharSize(this);
		
		// set up title
		titleComp = new Composite( this, SWT.NONE );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleComp.setLayoutData( titleCompFD );

		titleLabel = new Label( titleComp, SWT.None );
		titleLabel.setText( ANCHOR_GROUP_NAME );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 0, 0, myNumberDisplay, 0));

		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );

		// adjust title height to the button height		
		if ( !QueryToolRuntime.getInstance().isTitleHeightSet() )
		{
			if (QueryToolRuntime.getInstance().isLaunchedFromWorkbench())
				QueryToolRuntime.getInstance().setGroupTitleHeight( Math.max( MINOR_TITLE_HEIGHT, this.myCloseLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y) );
			else
				titleCompFD.height = Math.max( MINOR_TITLE_HEIGHT, DEFAULT_TITLE_HEIGHT);
		}
		else
			titleCompFD.height = UIUtils.computeButtonSizeOffScreen( this, null, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE )).y;
		
		//set up date constraint Display and Number of Occurrence display
		myNumberDisplay = new NumberConstraintDisplay( titleComp, myGroup, SWT.None );
		FormData numberDisplayFD 	= FormDataMaker.makeFormData( 0, 0, 100, 0, (Integer)null, 0, 100, 0);
			numberDisplayFD.width	= NUM_CHARS_IN_NUMBER_DISPLAY * singleCharSize.x;
		myNumberDisplay.setLayoutData( numberDisplayFD );
				
		myDateDisplay = new SkinnyDateConstraintDisplay( titleComp, myGroup, SWT.None );
		FormData dateDisplayFD 		= FormDataMaker.makeFormData( 0, 0, 100, 0, (Integer)null, 0, myNumberDisplay, 1);
			dateDisplayFD.width 	= NUM_CHARS_IN_DATE_DISPLAY * singleCharSize.x;
		myDateDisplay.setLayoutData( dateDisplayFD );
		myDateDisplay.setActive( true );
		
		/*
		myBindingDisplay = new GroupBindingDisplayForTemporalEventAnchor( titleComp, SWT.NONE, this.myGroup );
			FormData bindingDisplayFD 		= FormDataMaker.makeFormData( 0, 0, 100, 0, (Integer)null, 0, myDateDisplay, 1);
		myBindingDisplay.setLayoutData( bindingDisplayFD );
		myBindingDisplay.setActive( true );
		*/
		
		// add this as Displays' VisualActivationListener so that when a Display is clicked, it stays 'activated'
		myNumberDisplay.addVisualActivationListener( this );
		myDateDisplay.addVisualActivationListener( this );
		//myBindingDisplay.addVisualActivationListener( this );		
		
		myMainComposite = new Composite( this, SWT.NONE );
		myMainComposite.setLayout( new FormLayout() );
		myMainComposite.setLayoutData( FormDataMaker.makeFormData( titleComp, 100, 0, 100));
		myMainComposite.setBackground( Colors.WHITE );
		
		// label and viewerComposite occupy the same space.
		//myPreTreeLabel = new Label( myMainComposite, SWT.CENTER | SWT.WRAP );
		//myPreTreeLabel.setText( DRAG_ONLOTOGY_TERMS_HERE );
		
		ArrayList<Object> contents = new ArrayList<Object>();
		contents.add( DRAG_ONLOTOGY_TERMS_HERE1 );

		myPreTreeLabel = new MixedTextIconPanel( myMainComposite, SWT.CENTER | SWT.WRAP, contents, Colors.GRAY, Colors.WHITE  );
		Point labelSize = myPreTreeLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		//myPreTreeLabel.setLayoutData( FormDataMaker.makeFormData( (Integer)null, 0, 100, -4, 20, 0, 80, 0));
		myPreTreeLabel.setLayoutData( FormDataMaker.makeFormData( 50, -labelSize.y/2, (Integer)null, 0, 50, -labelSize.x/2, (Integer)null, 0));
		
		myViewerComposite = new Composite( myMainComposite, SWT.NONE );
		myViewerComposite.setLayout( new FormLayout() );
		myViewerComposite.setLayoutData( FormDataMaker.makeFullFormData() );
		
		myTreeViewer = new TreeViewer( myViewerComposite, SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL );
		myTreeViewer.getTree().setLayoutData( FormDataMaker.makeFullFormData() );
		
		// initialize visibility of treeviewer and label
		myPreTreeLabel.setVisible( true );
		myViewerComposite.setVisible( false );
		
		myTreeViewer.setContentProvider( new GroupPanelConceptContentProvider() );
		myTreeViewer.setLabelProvider(  new GroupPanelConceptLabelProvider() );
		myTreeViewer.setInput( myGroup.getTreeData() );
		
		ColumnViewerToolTipSupport.enableFor(myTreeViewer);
	}

	protected void attachListeners()
	{
		addConceptDragDropHandlers();
		addGroupPanelGroupDragHandler();
		addGroupPanelGroupDropHandler();
		addContextualMenus();
		addDoubleClickListenerToViewer();
		addTreeListenerToViewer();
	}
	
	
	@Override /* QueryToolPanelComposite */
	protected void setActive( boolean flag )
	{
		//System.err.println("titleComp is enabled? " + titleComp.isEnabled() );
		if ( flag )
		{
			titleComp.setBackground( Colors.CONTROL_TITLE_BG );
			titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		}
		else
		{
			titleComp.setBackground( Colors.CONTROL_TITLE_BG_DISABLED );
			titleLabel.setBackground( Colors.CONTROL_TITLE_BG_DISABLED );
		}
		
		myDateDisplay.setEnabled( this.myGroupManager.isUsingGroupSpecificDateConstraint() && flag );
		myNumberDisplay.setEnabled( flag );
		
		if ( this.myCurrentlySelectedDisplay != null )
			this.myCurrentlySelectedDisplay.setActive( true );
	}
	
	@Override /* GroupDroppedDelegator: handles UI changes after a Group has been dropped */
	public void groupDropped() 
	{
		Display.getDefault().asyncExec( new Runnable()
		{
			@Override
			public void run() 
			{	
				autoSetGroupTree();
				myTreeViewer.refresh();					// refresh the terms list
				GroupPanelForTemporalEventAnchor.this.myRowCount = myGroup.getTerms().size();	// initialize rowCoutns
				autoAdjustPanelHeight();
				myDateDisplay.updateLabels();
				myNumberDisplay.updateDisplay();
				// if this is the first GroupPanel, make sure myGroup ALWAYS has BY_OBSERVATION
				if ( myGroupManager.getIndexOf( GroupPanelForTemporalEventAnchor.this ) == 0 )
					myGroup.setBinding( GroupBinding.BY_OBSERVATION );
			}
		});
	}
	
	/*
	 * main method for testing only
	 */
	public static void main( String[] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		GroupPanel gp = new GroupPanelForTemporalEventAnchor( myShell, SWT.None );
		gp.setLayoutData( FormDataMaker.makeFullFormData() );
		
		myShell.setSize( 550, 200 );
		
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
