/*
 * Copyright (c) 2006-2015 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 * 		Janice Donahoe (documentation for on-line help)
 */


package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.DirtyValueType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetReturnType;



/**
 * The Edit View class provides the Edit UI View to the
 *  Eclipse framework  --- 
 * @author Lori Phillips   
 */

public class EditView extends ViewPart implements ICommonMethod 
{

	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.ontology.views.edit.editView";
	public static final String THIS_CLASS_NAME = EditView.class.getName();
	
	//setup context help
	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
	public static final String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_context";

	public static final String REFRESH_COMMAND = "Refresh:";

	static Composite compositeQueryTree;
	private Button showDisplayButton;
	private Log log = LogFactory.getLog(THIS_CLASS_NAME);
	public boolean bWantStatusLine = false;
	private TreeComposite dragTree;
	private StatusLineManager slm = new StatusLineManager();	
	


	/**
	 * The constructor.
	 */
	public EditView() {

		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntEditConceptCode") != null)
			System.setProperty("OntEditConceptCode",  UserInfoBean.getInstance().getCellDataParam("ont","OntEditConceptCode"));	
		
		else
			System.setProperty("OntEditConceptCode","false"); 		
		// Hiddens and synonyms are hard coded to false in TreeNode (getChildren)
		
		
		if (UserInfoBean.getInstance().getSelectedProjectParam("OntEdit_ViewOnly") != null){			
			System.setProperty("OntEdit_ViewOnly",UserInfoBean.getInstance().getSelectedProjectParam("OntEdit_ViewOnly"));

//			if (UserInfoBean.getInstance().getCellDataParam("ont", "OntEditView") != null){
//			System.setProperty("OntEdit_ViewOnly",  UserInfoBean.getInstance().getCellDataParam("ont","OntEditView"));	
		}
		
		else
			System.setProperty("OntEdit_ViewOnly","false"); 		
		// Hiddens and synonyms are hard coded to false in TreeNode (getChildren)
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */

	@Override
	public void createPartControl(final Composite parent) {
		log.info("Edit Terms plugin version 1.6.0");
		if (!(Roles.getInstance().isRoleValid()))
		{
			final Composite notValid = new Composite(parent, SWT.NONE);
			
			GridData layoutData = new GridData();
			layoutData.grabExcessHorizontalSpace = true;
			layoutData.horizontalAlignment = GridData.FILL;
			layoutData.grabExcessVerticalSpace = true;
			layoutData.verticalAlignment = GridData.FILL;
			notValid.setLayoutData(layoutData);
			
			notValid.setLayout(new GridLayout(1, false));
			
			
			Label label= new Label(notValid, SWT.NONE | SWT.WRAP);
			label.setText("\n\n"+Messages.getString("EditView.MinRoleNeeded"));
			
			GridData data = new GridData ();
			data.horizontalAlignment = GridData.CENTER;
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = true;
			label.setLayoutData(data);
					
			showDisplayButton = new Button(notValid, SWT.PUSH);
			showDisplayButton.setText("Display Anyway");

			GridData data1 = new GridData ();
			data1.horizontalAlignment = GridData.END;
			data1.grabExcessHorizontalSpace = true;
			data1.grabExcessVerticalSpace = true;
			showDisplayButton.setLayoutData(data1);
			
			showDisplayButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
						if ((notValid != null) && (!notValid.isDisposed())) {
							notValid.dispose();
							setup(parent);
						}
				}
			});
		
		}

		// set up view for those with LDS permission
		else{
			setup(parent);
		}
		
		//setup context help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, EDIT_VIEW_CONTEXT_ID);
		addHelpButtonToToolBar();
		addSyncButtonToToolBar();
	}

	private void setup(Composite parent){
	
		// Drag "from" tree
		compositeQueryTree = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		compositeQueryTree.setLayout(gridLayout);

		GridLayout gridLayoutTree = new GridLayout(1, false);
		gridLayoutTree.numColumns = 1;
		gridLayoutTree.marginHeight = 0;
		GridData fromTreeGridData = new GridData (GridData.FILL_BOTH);
		fromTreeGridData.widthHint = 300;
		compositeQueryTree.setLayoutData(fromTreeGridData);

		dragTree = new TreeComposite(compositeQueryTree, 1, slm);
		dragTree.setLayoutData(new GridData (GridData.FILL_BOTH));
		dragTree.setLayout(gridLayout);
		
		
		ModifierComposite.setInstance(compositeQueryTree);
//		ModifierComposite.getInstance().setLayoutData(new GridData (GridData.FILL_BOTH));
//		ModifierComposite.getInstance().setLayout(gridLayout);
	
		parent.layout(true);
	}
	
	@Override
	public void dispose() {
		super.dispose();

	}
	
	//add help button
	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action(){
			@Override
			public void run() {
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");
		}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/help.png"));
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
		

	}

	private void addSyncButtonToToolBar() {
		final Display theDisplay = Display.getCurrent();
		Action syncAction = new Action(){
			@Override
			public void run() {
				if (!(Roles.getInstance().isRoleValid())){
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING|SWT.OK);
					mBox.setText("Update Ontology Warning");
					mBox.setMessage(Messages.getString("EditView.MinRoleNeeded2"));

					int result = mBox.open();
					return;
				}
				
				// first get dirty state status
				getDirtyState(theDisplay);
				UpdateOntologyDialog dlg = new UpdateOntologyDialog(Display.getCurrent().getActiveShell());
				dlg.open();
			}
		};
		MetadataRecord.getInstance().setSyncAction(syncAction);	
		getDirtyState(theDisplay);
		if(ProcessStatus.getInstance().getDirtyState() == DirtyValueType.NONE){
			syncAction.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/database_refresh.png"));
		}
		else{
			syncAction.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/red_database_refresh.png"));
		}
		getViewSite().getActionBars().getToolBarManager().add(syncAction);
	
	}
	
	private void getDirtyState(final Display theDisplay){
		try {
			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;
			GetReturnType returnData = new GetReturnType();
			while(procStatus == null || !procStatus.getType().equals("DONE")){

				String response = OntServiceDriver.getDirtyState(returnData, "EDIT");

				procStatus = msg.processResult(response);
				//				else if  other error codes
				//				TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
				if (procStatus.getType().equals("ERROR")){	
					if(theDisplay !=  null){
						theDisplay.syncExec(new Runnable() {
							public void run() {
								// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
								MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
								mBox.setText("Please Note ...");
								mBox.setMessage("Unable to make a connection to the remote server\n" +  
								"This is often a network error, please try again");
								int result = mBox.open();
							}
						});
					}
					//				log.error(procStatus.getValue());				
					return;
				}	
		
				ProcessStatus.getInstance().setDirtyState(msg.doReadDirtyType());
				//			System.out.println(ProcessStatus.getInstance().getStatus().getProcessId());
			}
		} catch (AxisFault e) {
			//		log.error("Unable to make a connection to the remote server\n" +  
			//		"This is often a network error, please try again");
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});
		} catch (Exception e) {
;			e.printStackTrace();
			//	log.error("Error message delivered from the remote server\n" +  
			//	"You may wish to retry your last action");		
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theDisplay.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});
		}
	
	}
	
	/**
	 * Passing the focus request 
	 */
	@Override
	public void setFocus() {	
		if(compositeQueryTree != null)
			compositeQueryTree.setFocus();
		if(Boolean.parseBoolean(
    				System.getProperty("OntDisableModifiers"))){
			ModifierComposite.getInstance().disableComposite();
		}
	}

	/* =====================
	 * ICommonMethod methods:
	 * ===================== */
	@Override // @see edu.harvard.i2b2.eclipse.ICommonMethod#doSomething(java.lang.Object)
	public void doSomething(Object obj)
	{
		if ( obj instanceof String ) // String commands
		{
			String command = (String)obj;
			// Refresh command is of the form "Refresh:NODE_PATH", e.g., "Refresh:\\Custom Metadata\\Some Node Name\\"
			if ( command.startsWith( REFRESH_COMMAND )) 
			{
				// trim Strign to retain only the path part
				int start = command.indexOf( REFRESH_COMMAND ) + REFRESH_COMMAND.length();
				String path = command.substring( start );
				dragTree.refreshNode( path ); // path is to be in the form of "\\Custom Metadata\\Some Node Name\\" 
			}
		}
	}

	@Override //@see edu.harvard.i2b2.eclipse.ICommonMethod#processQuery(java.lang.String)
	public void processQuery(String id)
	{}

}
