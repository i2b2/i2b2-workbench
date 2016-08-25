/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import javax.swing.JOptionPane;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.DirtyValueType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetOntProcessStatusType;



/**
 *
 * @author  lcp5
 */
public class UpdateOntologyDialog extends Dialog {

	private ProgressBar bar = null;
	private Label updateStatus = null;
	private Thread runningThread = null;
	private String operationType = null;
	
	private Button update, synch, hiddens;


	private Log log = LogFactory.getLog(UpdateOntologyDialog.class.getName());	
	/**
	 * Creates new form OntologyOptionsDialog
	 */
	public UpdateOntologyDialog(Shell parentShell) {
		super(parentShell);	
	}

	@Override
	protected Control createDialogArea(Composite parent){
		Composite comp = (Composite)super.createDialogArea(parent);
		comp.getShell().setText("Update Ontology to CRC");

		GridLayout layout = (GridLayout)comp.getLayout();
		layout.numColumns = 1;
		
		if(System.getProperty("OntEdit_ViewOnly").equals("true")){
			Label label = new Label(comp, SWT.CENTER);
			label.setText("This feature is not available in this mode");
		}
			
		else{


			Label label = new Label(comp, SWT.CENTER);
			label.setText("      Synchronize Ontology in the Hive");

			Group options = new Group(comp, SWT.CENTER);
			final GridLayout gridLayout = new GridLayout();
			gridLayout.marginWidth = 15;
			gridLayout.horizontalSpacing = 15;
			gridLayout.marginTop = 5;
			gridLayout.marginRight = 15;
			gridLayout.marginLeft = 15;
			gridLayout.marginBottom = 5;
			gridLayout.numColumns = 1;
			options.setLayout(gridLayout);
			//		options.setLayoutData(new GridData());
			options.setText("Options");

			String state = ProcessStatus.getInstance().getDirtyState().value();

			update = new Button(options, SWT.RADIO);
			update.setText("Update only        ");
			if((state != null) && (state.equals(DirtyValueType.ADD.value()))){
				update.setText("Update only       (recommended)");
				update.setSelection(true);
			}
			operationType = "update_only";
			update.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					update.setSelection(true);
					synch.setSelection(false);
					operationType = "update_only";
				}
			});			


			synch = new Button(options,SWT.RADIO);
			synch.setText("Synchronize all         ");
			if((state != null) && (state.equals(DirtyValueType.DELETE_EDIT.value()))){
				synch.setText("Synchronize all    (recommended)");
				synch.setSelection(true);
				operationType = "synchronize_all";

			}
			synch.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					update.setSelection(false);
					synch.setSelection(true);
					operationType = "synchronize_all";
				}
			});	
			new Label(comp,SWT.NONE);
			hiddens = new Button(comp,SWT.CHECK);
			hiddens.setText("Include hiddens");
			hiddens.setSelection(true);


			new Label(comp,SWT.NONE);
			bar = new ProgressBar(comp, SWT.CENTER);

			GridData data = new GridData ();
			data.widthHint = 300;
			data.heightHint = 20;
			data.horizontalAlignment = GridData.BEGINNING;

			bar.setLayoutData(data);
			bar.setMaximum(100);

			updateStatus = new Label(comp, SWT.NONE);
			updateStatus.setLayoutData(data);
			updateStatus.setText("");
		}

		return parent;
	}
//	@Override
//	 protected Button createButton(Composite arg0, int arg1, String arg2, boolean arg3)
//	 {
	 //Retrun null so that no default buttons like 'OK' and 'Cancel' will be created
//		return null;
//	 }
	
	@Override
	protected void createButtonsForButtonBar(Composite parent){
		super.createButtonsForButtonBar(parent);
		if((System.getProperty("OntEdit_ViewOnly") == null)||(System.getProperty("OntEdit_ViewOnly").equals("false"))){

			createButton(parent, 2, "Run in Background", false);
			getOKButton().setText("Run");
		}
	}

	@Override
	protected void buttonPressed(int buttonId){
		if((System.getProperty("OntEdit_ViewOnly") == null)||(System.getProperty("OntEdit_ViewOnly").equals("false"))){

			// Run in background
			if(buttonId == 2){
				log.info("Starting " + operationType + " in background");
				synchronize(operationType, hiddens.getSelection()).start();
				close();
			}	
			// OK
			else if(buttonId == 0){

				// run synchronize within processStatus command
				log.info("Starting " + operationType);

				this.getButton(0).setEnabled(false);
				this.getButton(2).setEnabled(false);
				bar.setSelection(0);	
				updateStatus.setText("Starting synchronization process");

				//	synchronize().start();
				runningThread = processStatus(bar, updateStatus, this.getButton(0), this.getButton(2), operationType,hiddens.getSelection());
				runningThread.start();

			}


			//Cancel
			else if(buttonId ==1) {
				if(runningThread == null)
					close();

				if((runningThread != null) || ((runningThread.isAlive()))){
					runningThread.setName("stop");
					close();
				}
				// send cancel message to ONT; pass id

			}
		}else{
			close();
		}
	}

	public Thread synchronize(String operationType, boolean includeHiddens){
		final String theOperation = operationType;
		final boolean hiddens = includeHiddens;
		return new Thread(){
			@Override
			public void run(){
				try {
					synchronize(theOperation, hiddens, null);
				} catch (Exception e) {
					log.error("Synchronization error");					
				}

			}
		};
	}

	public void synchronize(final String operationType, final boolean includeHiddens, final Display theDisplay)
	{
		try {
			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){

				String response = OntServiceDriver.synchronize(operationType, includeHiddens);

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
				ProcessStatus.getInstance().setStatus(msg.doReadStatus());
				//			System.out.println(ProcessStatus.getInstance().getStatus().getProcessId());
			}
		} catch (AxisFault e) {
			log.error("Unable to make a connection to the remote server\n" +  
			"This is often a network error, please try again");

		} catch (Exception e) {
			log.error("Error message delivered from the remote server\n" +  
			"You may wish to retry your last action");		
		}
	}

	public Thread processStatus(ProgressBar bar, Label updateStatus, Button ok, Button background, String operationType, boolean includeHiddens){
		final ProgressBar theBar = bar;
		final Label theStatus = updateStatus;
		final Button theOkButton = ok;
		final Button theBackgroundButton = background;
		final Display theDisplay = Display.getCurrent();
		final String theOperation = operationType;
		final boolean hidden = includeHiddens;
		
		return new Thread(){
			@Override
			public void run(){
				try {
					synchronize(theOperation, hidden, theDisplay);
					if(this.getName().equals("stop")){
						System.gc();
						return;
					}
					long waitTime = 5000; //  msec
					Thread.sleep(waitTime);

					while(ProcessStatus.getInstance().getStatus().getProcessStatusCd().equals("PROCESSING")){
						if(this.getName().equals("stop")){
							System.gc();
							return;
						}
						// need to do something if this fails...
						getStatus();
						String step = ProcessStatus.getInstance().getStatus().getProcessStepCd();
						if(step.equals("ONT_BUILD_PDO_START")){
							if(!(this.getName().equals("stop"))){
								theDisplay.syncExec(new Runnable() {
									public void run() {
										theBar.setSelection(33);
										theStatus.setText("Building data file to upload");
									}
								});
							}
						}
						else if (step.equals("ONT_SENTTO_FRC")){
							if(!(this.getName().equals("stop"))){
								theDisplay.syncExec(new Runnable() {
									public void run() {
										theBar.setSelection(50);
										theStatus.setText("Uploading data file to FRC");
									}
								});
							}
						}
						else if (step.equals("ONT_SENTTO_CRCLOADER")){
							if(!(this.getName().equals("stop"))){
								theDisplay.syncExec(new Runnable() {
									public void run() {
										theBar.setSelection(60);
										theStatus.setText("Loading data into dimension table");
									}
								});
							}
						}
						if(ProcessStatus.getInstance().getStatus().getProcessStatusCd().equals("COMPLETED")){
							if(!(this.getName().equals("stop"))){
								theDisplay.syncExec(new Runnable() {
									public void run() {
										theBar.setSelection(100);
										theStatus.setText("Synchronization complete");
									}
								});
							}
							break;
						}
						if(ProcessStatus.getInstance().getStatus().getProcessStatusCd().equals("ERROR")){
							break;
						}
						Thread.sleep(waitTime);
					}
					if(ProcessStatus.getInstance().getStatus().getProcessStatusCd().equals("COMPLETED")){
						if(!(this.getName().equals("stop"))){
							theDisplay.syncExec(new Runnable() {
								public void run() {
									theBar.setSelection(100);
									theStatus.setText("Synchronization complete");
									if(theOperation.equals("synchronize_all")){
										theOkButton.setEnabled(false);
										MetadataRecord.getInstance().getSyncAction()
										.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/database_refresh.png"));
									}
									else{
										theOkButton.setEnabled(false);
										if((ProcessStatus.getInstance().getDirtyState() == DirtyValueType.DELETE_EDIT))
											MetadataRecord.getInstance().getSyncAction()
											.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/red_database_refresh.png"));
										else
											MetadataRecord.getInstance().getSyncAction()
											.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/database_refresh.png"));
									}
									theBackgroundButton.setEnabled(false);
								}
							});
						}
					}
					if(ProcessStatus.getInstance().getStatus().getProcessStatusCd().equals("ERROR")){
						final String errorMessage = "Synchronize process is reporting an error";
						if(!(this.getName().equals("stop"))){
							theDisplay.syncExec(new Runnable() {
								public void run() {
									theStatus.setText(errorMessage);
									theOkButton.setEnabled(true);
									theBackgroundButton.setEnabled(true);
								}
							});

							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane.showMessageDialog(null,
											errorMessage,
											"Error", JOptionPane.INFORMATION_MESSAGE);
								}
							});
						}
					}
				} catch (InterruptedException e) {
					//			log.error(e.getMessage());
					final String errorMessage = "Synchronize process is reporting a communication error.";
					if(!(this.getName().equals("stop"))){
						theDisplay.syncExec(new Runnable() {
							public void run() {
								theStatus.setText(errorMessage);
								theOkButton.setEnabled(true);
								theBackgroundButton.setEnabled(true);
							}
						});
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								JOptionPane.showMessageDialog(null,
										errorMessage,
										"Error", JOptionPane.INFORMATION_MESSAGE);
							}
						});
					}

				} catch (Exception e) {
					if(!(this.getName().equals("stop"))){
						final String errorMessage = "Synchronize process is reporting an error";			
						theDisplay.syncExec(new Runnable() {
							public void run() {
								theStatus.setText(errorMessage);
								theOkButton.setEnabled(true);
								theBackgroundButton.setEnabled(true);
							}
						});
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								JOptionPane.showMessageDialog(null,
										errorMessage,
										"Error", JOptionPane.INFORMATION_MESSAGE);
							}
						});
					}
				} finally {
					//	interrupt();

				}

			}
		};
	}


	public void getStatus() throws Exception{
		final Display theDisplay  = Display.getCurrent();
		//	return new Thread() {
		//		public void run(){
		try {
			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				GetOntProcessStatusType processType = new GetOntProcessStatusType();
				processType.setProcessId(ProcessStatus.getInstance().getProcessId());
				String response = OntServiceDriver.getProcessStatus(processType);

				procStatus = msg.processResult(response);
				//								else if  other error codes
				//								TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
				if (procStatus.getType().equals("ERROR")){		
					if(theDisplay != null){
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
					//			log.error(procStatus.getValue());				
					return;
				}	
				ProcessStatus.getInstance().setStatus(msg.doReadListStatus());
				//			System.out.println(ProcessStatus.getInstance().getStatus().getProcessId());
			}

		} catch (Exception e) {
			//		log.error("Synchronization error");					
			throw e;
		}

	}



}

