/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Wensong Pan
 */
package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.apache.axis2.AxisFault;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetReturnType;

/**
 * @author wp066
 *
 */
public class ViewSynchronizeToolbarActionDelegate implements IViewActionDelegate{
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		
		// first get dirty state status
				
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
			/*			if(theDisplay !=  null){
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
			*/			return;
					}	
					ProcessStatus.getInstance().setDirtyState(msg.doReadDirtyType());
					//			System.out.println(ProcessStatus.getInstance().getStatus().getProcessId());
				}
			} catch (AxisFault e) {
		//		log.error("Unable to make a connection to the remote server\n" +  
		//		"This is often a network error, please try again");

			} catch (Exception e) {
			//	log.error("Error message delivered from the remote server\n" +  
			//	"You may wish to retry your last action");		
			}
		
		
  	  UpdateOntologyDialog dlg = new UpdateOntologyDialog(Display.getCurrent().getActiveShell());
	  dlg.open();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {		

	}


}
