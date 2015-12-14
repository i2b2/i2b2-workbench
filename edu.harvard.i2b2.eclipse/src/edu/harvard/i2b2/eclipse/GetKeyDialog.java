package edu.harvard.i2b2.eclipse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import edu.harvard.i2b2.eclipse.util.Messages;

/**
 * This class demonstrates how to create your own dialog classes. It allows users
 * to input a String
 */
public class GetKeyDialog extends Dialog {
	private String message;
	private String input;
	private static final Log log = LogFactory.getLog(GetKeyDialog.class);

	/**
	 * InputDialog constructor
	 * 
	 * @param parent the parent
	 */
	public GetKeyDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * InputDialog constructor
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public GetKeyDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText(Messages.getString("GetKeyDialog.Text")); //$NON-NLS-1$
		setMessage(
				Messages.getString("GetKeyDialog.Message")); //$NON-NLS-1$
	}

	/**
	 * Gets the message
	 * 
	 * @return String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message
	 * 
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the input
	 * 
	 * @return String
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Sets the input
	 * 
	 * @param input the new input
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open() {

		//Get key from drive
		getNoteKeyDrive();
		if(input == null) {

			// Create the dialog window
			Shell shell = new Shell(getParent(), getStyle());
			shell.setText(getText());
			createContents(shell);
			shell.pack();
			shell.open();
			shell.setSize(300, 160);
			Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		// Return the entered value, or null
		return input;
	}


	private void getNoteKeyDrive() {
		File[] drives = File.listRoots();
		String filename = Messages.getString("GetKeyDialog.KeyFilename"); //$NON-NLS-1$
		for(int i=drives.length-1; i>=0; i--){
			if(drives[i].getPath().startsWith("A") ||  //$NON-NLS-1$
					drives[i].getPath().startsWith("B")) { //$NON-NLS-1$
				continue;
			}

			File tmp = new File(drives[i]/*+File.separator*/+filename);
			if(tmp.exists()) {
				loadKey( drives[i]/*+File.separator*/+filename);
			}
			//else {
			//	 return null;
			//}
		}

		File testFile = new File(Messages.getString("GetKeyDialog.KeyFilename")); //$NON-NLS-1$
		log.debug("file dir: "+testFile.getAbsolutePath()); //$NON-NLS-1$
		if(testFile.exists()) {
			loadKey(testFile.getAbsolutePath());
		}
	}

	private void loadKey(String returnVal)
	{
		File f = new File(returnVal); //chooser.getSelectedFile();
		log.debug("Open this file: "+f.getAbsolutePath());	 //$NON-NLS-1$

		BufferedReader in = null;
		try {	    			 
			in = new BufferedReader(new FileReader(f.getAbsolutePath()));
			String line = null;
			while((line = in.readLine()) != null) {
				if(line.length() > 0) {
					input = ( line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""))); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		catch(Exception e2) {
			log.error(e2.getMessage());
		}
		finally {
			if(in!=null) {
				try {
					in.close();
				}
				catch(Exception e3) {
					log.error(e3.getMessage());
				}
			}
		}

	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell the dialog window
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(3, true));

//		final Image image = new Image(shell, GetKeyDialog.class.getResourceAsStream("yourFile.gif"));
//			final Label label_1 = new Label(shell, SWT.NONE);
	//		label_1.setText("Label");

		// Show the message
		Label label = new Label(shell, SWT.NONE);
		label.setText(message);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		label.setLayoutData(data);

		// Display the input box
		final Text text = new Text(shell, SWT.BORDER);
		GridData data2 = new GridData();
		data2.grabExcessHorizontalSpace = true;
		data2.horizontalSpan = 3;
		data2.horizontalAlignment = SWT.FILL;
		text.setLayoutData(data2);

		final Button browseButton = new Button(shell, SWT.NONE);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {


				FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				fd.setText(Messages.getString("GetKeyDialog.PopupText")); //$NON-NLS-1$
				// fd.setFilterPath("C:/");
				String[] filterExt = { "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				fd.setFilterExtensions(filterExt);

				String returnVal = fd.open();
				if(returnVal != null) { //== JFileChooser.APPROVE_OPTION) {
					loadKey(returnVal);
					text.setText(input);
				}				
			}
		});
		final GridData gd_browseButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		browseButton.setLayoutData(gd_browseButton);
		browseButton.setText(Messages.getString("GetKeyDialog.ButtonBrowse")); //$NON-NLS-1$

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText(Messages.getString("GetKeyDialog.ButtonOK")); //$NON-NLS-1$
		data = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				input = text.getText();
				shell.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText(Messages.getString("GetKeyDialog.ButtonCancel")); //$NON-NLS-1$
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				input = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}
}

