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

import java.util.Iterator;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.wizard.Wizard;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.*;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.DeleteChildType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ModifyChildType;

/*
 * 
 */


public class ViewWizard extends Wizard {
	private static final Log log = LogFactory.getLog(ViewWizard.class);
	private boolean okToFinish = false;

	public ViewWizard() {
		setWindowTitle("View Metadata"); //$NON-NLS-1$
		setNeedsProgressMonitor(false);
		//setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(null, "icons/import.gif")); //$NON-NLS-1$

		DialogSettings dialogSettings = new DialogSettings("userInfo"); //$NON-NLS-1$

		setDialogSettings(dialogSettings);

	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	@Override
	public void addPages() {
		
		addPage(new VerifyDataPage());

		okToFinish = true;
	}


	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		
		okToFinish = true;
		return super.getNextPage(page);		


	}
	
	
	@Override
	public boolean canFinish() {
		//controls appearance of 'Finish' button					
		return okToFinish;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (!(Roles.getInstance().isRoleValid())){
			MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING|SWT.OK);
			mBox.setText("Edit Term Warning");
			mBox.setMessage(Messages.getString("EditView.MinRoleNeeded2"));

			int result = mBox.open();
			okToFinish = false;
			return false;
		}
		
//		VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
//		verifyPage.updateParameters();
		//	return false;
		okToFinish = true;
		return true;
		
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performCancel()
	 */
	@Override
	public boolean performCancel() {
		MessageBox mb = new MessageBox(getShell(),SWT.ICON_QUESTION|SWT.YES|SWT.NO);
		mb.setMessage(Messages.getString("Wizard.CancelPopupText"));
		mb.setText(Messages.getString("Wizard.CancelPopupTitle"));

		int ans =  mb.open();
		if(ans == SWT.YES)
			return true;
		else
			return false;
	}  


	
	
	
}





	
	





