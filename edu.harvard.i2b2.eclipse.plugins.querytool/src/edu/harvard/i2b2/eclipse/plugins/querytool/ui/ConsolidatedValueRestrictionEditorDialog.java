/*
 * Copyright (c) 2006-2015 Partners Healthcare 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * This source code was developed as part of i2b2 for the 
 * Medical Imaging Informatics Bench to Beside project (mi2b2).
 * 
 * Contributors: Taowei David Wang 
 */

package edu.harvard.i2b2.eclipse.plugins.querytool.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.ModifierData;
import edu.harvard.i2b2.query.data.QueryConceptTreeNodeData;

public class ConsolidatedValueRestrictionEditorDialog extends AbstractValueRestrictionEditorDialog
{
	/*
	private Shell		myShell;
	private Composite	myMainComp;
	private Point		myInitLocation; 
	
	private Composite	titleComp;
	private Label		titleLabel;
	private Composite	innerComp;
	
	private Composite	myPanelComp;
	*/
	
	private TableViewer myTableViewer;
	
	private Button		myNextButton;
	private Button		myPrevButton;
	
	private List<ValueRestrictionEditorPanel> 			myEditors;	
	private List<QueryConceptTreeNodeData>				myNodes;

	private	int 		myCurrentIndex;
		
	public ConsolidatedValueRestrictionEditorDialog( List<QueryConceptTreeNodeData> nodes, int styles  )
	{		
		setupUI( nodes, styles );
		attachListeners();
	}

	private void setupUI(List<QueryConceptTreeNodeData> nodes, int styles) 
	{
		myShell = new Shell( Display.getCurrent(), SWT.APPLICATION_MODAL | styles);
		myShell.setLayout( new FormLayout() );
		
		myMainComp = new Composite( myShell, SWT.None );
		myMainComp.setLayoutData( FormDataMaker.makeFullFormData() );
		myMainComp.setLayout( new FormLayout() );

		titleComp = new Composite( myMainComp, SWT.BORDER );
		titleComp.setLayout( new FormLayout() );
		FormData titleCompFD = FormDataMaker.makeFormData( 0, (Integer)null, 0, 100);
		titleCompFD.height = TITLE_HEIGHT;
		titleComp.setLayoutData( titleCompFD );

		titleLabel = new Label( titleComp, SWT.None );
		titleLabel.setText( SPECIFY_VALUE_CONSTRAINTS );
		Point titleSize = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		titleLabel.setLayoutData( FormDataMaker.makeFormData( 50, -titleSize.y/2, (Integer)null, 0, 50, -titleSize.x/2, (Integer)null, 0));

		// set colors of title
		titleComp.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setBackground( Colors.CONTROL_TITLE_BG );
		titleLabel.setForeground( Colors.CONTROL_TITLE_FG );

		Composite outerComp = new Composite( myMainComp, SWT.BORDER );
		outerComp.setLayout( new FormLayout() );
		outerComp.setLayoutData( FormDataMaker.makeFormData( titleComp,100, 0, 100) );
		outerComp.setBackground( Colors.ORANGE );

		innerComp = new Composite( outerComp, SWT.BORDER );
		innerComp.setLayout( new FormLayout() );
		innerComp.setLayoutData( FormDataMaker.makeBorderingFormData() );

		// start widget content
		SashForm sash = new SashForm (innerComp, SWT.HORIZONTAL | SWT.SMOOTH );
		sash.setLayout( new FormLayout() );
		sash.setLayoutData( FormDataMaker.makeFullFormData() );
		sash.setSashWidth( 5 );
		
		myTableViewer = new TableViewer( sash, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER );
		myTableViewer.getTable().setLayoutData( FormDataMaker.makeFullFormData() );
		myTableViewer.getTable().setLinesVisible( true );
		
		Composite rightComp =  new Composite( sash, SWT.NONE );
		rightComp.setLayoutData( FormDataMaker.makeFullFormData() );
		rightComp.setLayout( new FormLayout() );
		
		Composite buttonsComp = new Composite( rightComp, SWT.NONE );
			FormData buttonsCompFD = FormDataMaker.makeFormData( (Integer)null, 100, 0, 100);
			buttonsCompFD.height = UIConst.DECISION_HEIGHT;
		buttonsComp.setBackground( Colors.BLACK );
		buttonsComp.setLayoutData( buttonsCompFD );
		buttonsComp.setLayout( new FormLayout() );
		
		myNextButton = new Button( buttonsComp, SWT.PUSH );
		myNextButton.setText( NEXT );
		myNextButton.setLayoutData( FormDataMaker.makeFormData(50, -myNextButton.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, (Integer)null, 0, 100, -20));
		
		myPrevButton = new Button( buttonsComp, SWT.PUSH );
		myPrevButton.setText( PREVIOUS );
		myPrevButton.setLayoutData( FormDataMaker.makeFormData(50, -myPrevButton.computeSize(SWT.DEFAULT, SWT.DEFAULT).y/2, (Integer)null, 0, 0, 20, (Integer)null, 0));
		
		myPanelComp = new Composite( rightComp, SWT.NONE );
		myPanelComp.setLayout( new FormLayout() );
		myPanelComp.setLayoutData( FormDataMaker.makeFormData(0, buttonsComp, 0, 100) );
		
		ColumnViewerToolTipSupport.enableFor(myTableViewer);
		
		myEditors =  ValueRestrictionEditorFactory.getInstance().makeEditors(myPanelComp, nodes );		
		myNodes = new ArrayList<QueryConceptTreeNodeData>();
		for ( ValueRestrictionEditorPanel panel : myEditors )
			myNodes.add( panel.getNodeData() );
		
		// set sash weights
		sash.setWeights( new int[]{35,65});
	
		// set tree label/content provider
		myTableViewer.setContentProvider( new ConsolidatedEditorTableContentProvider() );
		myTableViewer.setLabelProvider( new ConsolidatedEditorTableLabelProvider() );
		myTableViewer.setInput( myNodes );
		
		myShell.setSize( myShell.computeSize(SWT.DEFAULT, SWT.DEFAULT) );
		
		// set defaults
		myCurrentIndex = 0;
		if ( myNodes.size() > 0 )
			myTableViewer.setSelection( new StructuredSelection( myTableViewer.getElementAt( myCurrentIndex )), true );
		
		autoSetButtons();
		autoSetEditorVisibility();
	}

	private void attachListeners() 
	{
		// TITLE for moving the dialog
		DialogMoveMouseListener moveListener = new DialogMoveMouseListener( myShell, titleComp );
		titleComp.addMouseListener( moveListener );
		titleComp.addMouseMoveListener( moveListener );
		titleLabel.addMouseListener( moveListener );
		titleLabel.addMouseMoveListener( moveListener );
		
		// handle Table Selection
		myTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{	
				myEditors.get( myCurrentIndex ).saveValueRestriction(); // save restriction for the current editor
				myCurrentIndex = myTableViewer.getTable().getSelectionIndex();
				autoSetEditorVisibility();
				autoSetButtons();
			}
		});
		
		// handle prev/next button 
		myNextButton.addSelectionListener( new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				myEditors.get( myCurrentIndex ).saveValueRestriction(); // save restriction for the current editor
				if ( myCurrentIndex == myNodes.size()-1 ) // at the end
				{					
					myShell.close();
					myShell.dispose(); // terminate this dialog
				}
				else
				{
					myCurrentIndex++;
					autoSetTableSelection();
				}
			}
		});
		
		myPrevButton.addSelectionListener( new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				myEditors.get( myCurrentIndex ).saveValueRestriction(); // save restriction for the current editor
				myCurrentIndex--;
				autoSetTableSelection();
			}
		});
	}

	/* Apply table selection using myCurrent index. 
	 * This will trigger the tableviewer's SelectionChangedListener so we don't have to call other autoSet methods 
	 * */
	private void autoSetTableSelection()
	{
		myTableViewer.setSelection( new StructuredSelection( myTableViewer.getElementAt( myCurrentIndex )), true );
	}
	
	private void autoSetButtons() 
	{
		
		this.myPrevButton.setVisible( myCurrentIndex != 0 );
		this.myNextButton.setVisible( true );
		if ( this.myCurrentIndex == this.myNodes.size()-1 )
		{
			this.myNextButton.setText( FINISH );
			this.myPrevButton.getParent().layout();
		}
		else
		{
			this.myNextButton.setText( NEXT );
			this.myPrevButton.getParent().layout();
		}		
	}

	private void autoSetEditorVisibility()
	{
		for ( int i = 0; i < this.myEditors.size(); i++ )
		{
			ValueRestrictionEditorPanel ep = myEditors.get(i);
			if ( myCurrentIndex == i )
				ep.setVisible( true );
			else
				ep.setVisible( false );			
		}
	}
	
	public void setLocation( Point location )
	{ myInitLocation = location; }

	// open the dialog
	public void open()
	{
		if ( this.myInitLocation != null )
			myShell.setLocation( this.myInitLocation );
		myShell.setSize( new Point(560, 400) );
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

	public boolean hasContent()
	{ return this.myEditors.size() > 0; }
	
	public static void main( String [] args )
	{
		ConsolidatedValueRestrictionEditorDialog dcd = new ConsolidatedValueRestrictionEditorDialog( new ArrayList<QueryConceptTreeNodeData>(), SWT.CLOSE | SWT.RESIZE);
		dcd.open();
	}

}

class ConsolidatedEditorTableContentProvider implements IStructuredContentProvider 
{

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
	{}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) 
	{
		ArrayList<QueryConceptTreeNodeData> nodes = (ArrayList<QueryConceptTreeNodeData>)inputElement;
		return nodes.toArray();
	}
}

class ConsolidatedEditorTableLabelProvider extends ColumnLabelProvider
{
	//public Color 	getBackground(Object element) 	{}
	//public Font 	getFont(Object element)			{}
	//public Color 	getForeground(Object element)	{}
	
	public Image 	getImage(Object element)
	{
		return GroupPanelIcons.getImageIcon( (QueryConceptTreeNodeData)element );
	}
	
	public String 	getText(Object element)
	{
		return ((QueryConceptTreeNodeData)element).name();
	}
	
	@Override
	public String getToolTipText(Object element) 
	{
		return ((QueryConceptTreeNodeData)element).tooltip();
	}
}



