package edu.harvard.i2b2.eclipse.plugins.querytool.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.Settings;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.FormDataMaker;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.KeyboardUtils;

public class ViewOptionsDialog extends Dialog 
{

	
	protected Text 		myTimeoutText = null;
	
	protected ViewOptionsDialog(Shell parentShell) 
	{
		super(parentShell);
		//addListeners();
	}
	
	/** Create new form for list of response messages **/
	protected Control createDialogArea(Composite parent)
	{
		this.getShell().setText("Set Options for Temporal Query Tool");
		Composite comp = (Composite) super.createDialogArea(parent);
		comp.setLayout( new FormLayout() );
    	
		Label timeoutLabel = new Label( comp, SWT.NONE );
		timeoutLabel.setText("Number of seconds before a submitted query times out \n(Query executes in background when timed out.))");
		timeoutLabel.setLayoutData( FormDataMaker.makeFormData( 0, 20, 100, -20, 0, 10, (Integer)null, 0 ));
		
		myTimeoutText = new Text( comp, SWT.RIGHT | SWT.BORDER );
		myTimeoutText.setLayoutData( FormDataMaker.makeFormData( 0, 20, (Integer)null, 0, timeoutLabel, 30, 100, -10 ) );		
		myTimeoutText.setText( System.getProperty( Settings.QT_MAX_WAITING_TIME_KEY ) );
	    		
		myTimeoutText.addKeyListener( new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent arg0) 
			{
				if ( KeyboardUtils.isTextNavigationKey(arg0) ) return; // handle directional/home/end keys normally
				if ( !Character.isDigit( arg0.character ) && !KeyboardUtils.isDeletion(arg0) )
					arg0.doit = false;	// if not digit, do not display
				else if ( arg0.character == 13 ) // carriage return (user accepts the typed-in text)
					okPressed();
				else if ( arg0.character == 27 ) // ESC	(user cancels editing, revert to known old value))
					myTimeoutText.setText( System.getProperty( Settings.QT_MAX_WAITING_TIME_KEY ) );
			}

			@Override
			public void keyReleased(KeyEvent e) 
			{}
		});
		
		myTimeoutText.addModifyListener( new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e) 
			{
				try
				{
					Integer.parseInt( myTimeoutText.getText() );
				}
				catch ( Exception exception )
				{
					// revert to last valid value
					myTimeoutText.setText( System.getProperty( Settings.QT_MAX_WAITING_TIME_KEY ) ); 
				}
			}
		});
		
    	return comp;
    }

	@Override
	public void okPressed()
	{		
		// save the timeout value
		System.setProperty( Settings.QT_MAX_WAITING_TIME_KEY, myTimeoutText.getText() );
		super.okPressed();
	}
	
	// overwrite super. open with a size
	public int open()
	{		
		int val = super.open();
		return val;
	}
	
	
	// overwrite super. make this dialog resizable
	protected boolean isResizable() 
	{
		return true;
	}

}
