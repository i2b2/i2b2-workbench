package edu.harvard.i2b2.eclipse.plugins.querytool.ui.widgets;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Colors;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;

public class MixedTextIconPanel extends Composite
{

	public MixedTextIconPanel(Composite parent, int style, ArrayList<Object> content, Color textColor, Color backgroundColor )
	{
		super(parent, style);
		setupUI( content, textColor, backgroundColor );
	}

	private void setupUI( ArrayList<Object> content, Color textColor, Color backgroundColor ) 
	{
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = true;
		this.setLayout( rowLayout );
		this.setBackground( backgroundColor );
		for ( Object obj : content )
		{
			if ( obj instanceof Image)
			{
				Label icon = new Label( this, SWT.NONE );
				icon.setImage( (Image)obj );
				icon.setBackground( backgroundColor );
			}
			else
			{
				StringTokenizer tokens =new StringTokenizer( obj.toString() );
				while ( tokens.hasMoreTokens() )
				{
					Label text = new Label( this, SWT.WRAP );					
					text.setForeground( textColor );
					text.setBackground( backgroundColor );
					text.setText( tokens.nextToken() );
				}
			}
		}
	}

	public static void main( String [] args )
	{
		Shell myShell = new Shell( Display.getCurrent(), SWT.CLOSE | SWT.RESIZE );
		myShell.setLayout( new FormLayout() );
		
		Image image1 = new Image( Display.getCurrent(), "C:\\Users\\tdw9\\Workbench 1.7 dev\\edu.harvard.i2b2.eclipse.plugins.querytool\\icons\\concepts\\prevQuery.gif" );
		Image image2 = new Image( Display.getCurrent(), "C:\\Users\\tdw9\\Workbench 1.7 dev\\edu.harvard.i2b2.eclipse.plugins.querytool\\icons\\concepts\\prevQuery.gif" );
		
		ArrayList<Object> items = new ArrayList<Object>();
		items.add("These are some images to be added ");
		items.add( image1 );
		items.add( image2 );
		items.add("Here are some text after.");
		
		MixedTextIconPanel line = new MixedTextIconPanel( myShell, SWT.NONE, items, Colors.WHITE, Colors.DARK_DARK_GRAY );
		line.setLayoutData( FormDataMaker.makeFormData(0, 30, (Integer)null, 0, 30, 0, 70, 0 ));
		myShell.setSize( 500, 400 );
		myShell.setBackground( Colors.DARK_DARK_GRAY );
		
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
