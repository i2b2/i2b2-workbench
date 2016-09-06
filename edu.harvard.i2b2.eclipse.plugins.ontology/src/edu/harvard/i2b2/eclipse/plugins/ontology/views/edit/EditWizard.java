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


public class EditWizard extends Wizard {
	private static final Log log = LogFactory.getLog(EditWizard.class);
	private boolean okToFinish = false;

	public EditWizard() {
		setWindowTitle(Messages.getString("EditWizard.WindowTitle")); //$NON-NLS-1$
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
	
		addPage(new EditSettingsPage());
		addPage(new SynonymsPage());
		addPage(new QueryDimensionSettingsPage());
	//	addPage(new ValueMetadataSettingsPageOne());
		addPage(new ValueMetadataSettingsPageTwo());
		addPage(new ValueMetadataSettingsPageThree());
		addPage(new ValueMetadataSettingsPageFour());
		addPage(new ValueMetadataSettingsPageFive());
		addPage(new VerifyDataPage());

	}


	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof EditSettingsPage) {
			((EditSettingsPage) page).updateMetadataRecord();
			okToFinish = false;
		}
		
		else if (page instanceof QueryDimensionSettingsPage) {
			((QueryDimensionSettingsPage) page).updateMetadataRecord();
			//skip remainder of pages if no value metadata is selected
			if (!(MetadataRecord.getInstance().isValueMetadataFlag())){
				page = getPage(ValueMetadataSettingsPageFive.PAGE_NAME);
				VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
				okToFinish = verifyPage.updateParameters();
				if(okToFinish)
					okToFinish = page.isPageComplete();
			}
			/// skip Page One if  this is a LAB 
	/*		else if((MetadataRecord.getInstance().getMetadata().getValuetypeCd().equals("LAB"))){
				page = getPage(ValueMetadataSettingsPageOne.PAGE_NAME);
			}
			*/
		}	
	
	
		else if (page instanceof ValueMetadataSettingsPageThree) {
			((ValueMetadataSettingsPageThree) page).updateValueMetadata();	
			page = getPage(ValueMetadataSettingsPageFive.PAGE_NAME);
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			okToFinish = verifyPage.updateParameters();
			if(okToFinish)
				okToFinish = page.isPageComplete();
		}	
		else if (page instanceof ValueMetadataSettingsPageFour) {
			((ValueMetadataSettingsPageFour) page).updateValueMetadata();		
		}
		else if (page instanceof ValueMetadataSettingsPageFive) {
			((ValueMetadataSettingsPageFive) page).updateValueMetadata();	
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			okToFinish = verifyPage.updateParameters();
			if(okToFinish)
				okToFinish = page.isPageComplete();
		}

		/*else if (page instanceof ValueMetadataSettingsPageOne) {
			((ValueMetadataSettingsPageOne) page).updateValueMetadata();
			// semantic type is KEY (not LAB) so skip the next 4 pages
			page = getPage(ValueMetadataSettingsPageFive.PAGE_NAME);
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			okToFinish = verifyPage.updateParameters();
			if(okToFinish)
				okToFinish = page.isPageComplete();

		}*/	

		else if (page instanceof ValueMetadataSettingsPageTwo) {
			((ValueMetadataSettingsPageTwo) page).updateValueMetadata();
	//		ValueMetadata vm = ValueMetadata.getInstance();
			// if page is enum type -- continue to page 3.
			if((ValueMetadata.getInstance().isNumericType())){
				page = getPage(ValueMetadataSettingsPageThree.PAGE_NAME);
			}
			else if((ValueMetadata.getInstance().isStringType()) ||
					!ValueMetadata.getInstance().isEnumType() ) {
				page = getPage(ValueMetadataSettingsPageFive.PAGE_NAME);
				VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
				okToFinish = verifyPage.updateParameters();
				if(okToFinish)
					okToFinish = page.isPageComplete();

			}
			
		}
		
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
		// this handles case where we hit back button and then tried to finish.
		IWizardPage page = getContainer().getCurrentPage();
		if(page.isPageComplete() == false){
			okToFinish = false;
			return false;
		}
		
		if (page instanceof EditSettingsPage) {
			((EditSettingsPage) page).updateMetadataRecord();
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			if(!verifyPage.updateParameters())
				return false;
		}
		
		else if (page instanceof QueryDimensionSettingsPage) {
			((QueryDimensionSettingsPage) page).updateMetadataRecord();
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			if(!verifyPage.updateParameters())
				return false;
		}
	/*	else if (page instanceof ValueMetadataSettingsPageOne) {
			((ValueMetadataSettingsPageOne) page).updateValueMetadata();
			
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			if(!verifyPage.updateParameters())
				return false;
		}
	*/	
		else if (page instanceof ValueMetadataSettingsPageTwo) {
			((ValueMetadataSettingsPageTwo) page).updateValueMetadata();
			
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			if(!verifyPage.updateParameters())
				return false;
		}
		else if (page instanceof ValueMetadataSettingsPageThree) {
			((ValueMetadataSettingsPageThree) page).updateValueMetadata();
		
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			if(!verifyPage.updateParameters())
				return false;
		}
		
		
		else if (page instanceof ValueMetadataSettingsPageFour) {
			((ValueMetadataSettingsPageFour) page).updateValueMetadata();
		
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			if(!verifyPage.updateParameters())
				return false;
		}
		
		
		else if (page instanceof ValueMetadataSettingsPageFive) {
			((ValueMetadataSettingsPageFive) page).updateValueMetadata();
			VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
			if(!verifyPage.updateParameters())
				return false;
		}
		
	//	if(validate()){
		
		if((System.getProperty("OntEdit_ViewOnly") != null) && (System.getProperty("OntEdit_ViewOnly").equals("true")))
				return true;
				
		
		if(!(MetadataRecord.getInstance().isSynonymEditFlag())){
			modifyChild(true).start();	
			return true;
		}
		else{
			// delete everything
			// add everything
			rebuildChild().start();
			return true;
		}
		
		//	else
	//		return false;
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
	/*
	 * Dont need to validate in edit/modify
	 * as we are editing an existing term.
	public boolean validate()
	{
		boolean valid = false;
		try {	
			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){

				GetTermInfoType termInfo = new GetTermInfoType();
				termInfo.setSelf(MetadataRecord.getInstance().getMetadata().getKey());
				termInfo.setBlob(false);
				termInfo.setSynonyms(false);
				termInfo.setHiddens(false);
				termInfo.setType("default");
				String response = OntServiceDriver.getTermInfo(termInfo, "EDIT");

				procStatus = msg.processResult(response);
				//			else if  other error codes
				//			TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
				if (procStatus.getType().equals("ERROR")){		
							// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
							MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("Unable to make a connection to the remote server\n" +  
							"This is often a network error, please try again");
							int result = mBox.open();
					log.error(procStatus.getValue());				
					return false;
				}			
			//  check response
				ConceptsType concepts = msg.doReadConcepts();
				// then do submit if response is empty (means term is unique)
				valid = concepts.getConcept().isEmpty();						
				if(!valid){
							MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
							mBox.setText("Please Note ...");
							mBox.setMessage("The path name you created for this item is not unique \n" + 
							"Please modify the symbol name on the first page and try again");
							int result = mBox.open();
						}

			}
		} catch (AxisFault e) {
			log.error("Unable to make a connection to the remote server\n" +  
			"This is often a network error, please try again");

		} catch (Exception e) {
			log.error("Error message delivered from the remote server\n" +  
			"You may wish to retry your last action");		
		}
		return valid;
	}
*/
	
	public Thread modifyChild(final boolean inclSynonyms){
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			@Override
			public void run(){
				try {
					modify(theDisplay, inclSynonyms);
				} catch (Exception e) {
					log.error("Edit child error");					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						MetadataRecord.getInstance().getBrowser().parentUpdate();
						if(MetadataRecord.getInstance().isUpdateSyncIconFlag()){
							MetadataRecord.getInstance().getSyncAction()
							.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/red_database_refresh.png"));
						}
						return;
					}
				});
			}
		};
	}
	public void modify(final Display theDisplay, boolean inclSynonyms)
	{
		try {
			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				
				ModifyChildType modChild = new ModifyChildType();
				modChild.setSelf(MetadataRecord.getInstance().getMetadata());
				modChild.setInclSynonyms(inclSynonyms);
				
				String response = OntServiceDriver.modifyChild(modChild);
				
				procStatus = msg.processResult(response);
//				else if  other error codes
//				TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
				if (procStatus.getType().equals("ERROR")){		
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
					log.error(procStatus.getValue());				
					return;
				}			
			}
		} catch (AxisFault e) {
			log.error("Unable to make a connection to the remote server\n" +  
			"This is often a network error, please try again");
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});				
		} catch (Exception e) {
			log.error("Error message delivered from the remote server\n" +  
					"You may wish to retry your last action");		
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});	
		}
	}
	
	public Thread rebuildChild(){
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			@Override
			public void run(){
				try {
					rebuild(theDisplay);
				} catch (Exception e) {
					log.error("Edit child error on synonym rebuild");					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						MetadataRecord.getInstance().getBrowser().parentUpdate();
						if(MetadataRecord.getInstance().isUpdateSyncIconFlag()){
							MetadataRecord.getInstance().getSyncAction()
							.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/red_database_refresh.png"));
						}
					}
				});
			}
		};
	}
	public void rebuild(final Display theDisplay)
	{
		ConceptType node = MetadataRecord.getInstance().getMetadata();
		/// first modify the node and actually delete the synonyms
			// set inclSynonyms to false
		modify(theDisplay, false);

		// now add synonyms
		List<String> synonyms = MetadataRecord.getInstance().getSynonyms();
		Iterator it = synonyms.iterator();
		while(it.hasNext()){
			String synName =(String)it.next();
			node.setName(synName);
			node.setSynonymCd("Y");
			add(theDisplay, node);
		}
	}
	
	private void delete(final Display theDisplay){
		OntologyResponseMessage msg = new OntologyResponseMessage();
		StatusType procStatus = null;	
		try{
			while(procStatus == null || !procStatus.getType().equals("DONE")){

				DeleteChildType delChild = MetadataRecord.getInstance().getDeleteChildType();
				// automatically include children on deletes from folders, containers.
				delChild.setIncludeChildren(false);
				String hiddenAttribute = delChild.getVisualattribute().charAt(0)+ "H" + delChild.getVisualattribute().charAt(2);
				delChild.setVisualattribute(hiddenAttribute);
				
				String response = OntServiceDriver.deleteChild(delChild);

				procStatus = msg.processResult(response);
				//		else if  other error codes
				//		TABLE_ACCESS_DENIED and USER_INVALID and DATABASE ERRORS
				if (procStatus.getType().equals("ERROR")){		
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
					log.error(procStatus.getValue());				
					return;
				}			
			}
		}catch (AxisFault e) {
			log.error("Unable to make a connection to the remote server\n" +  
			"This is often a network error, please try again");
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});	
		} catch (Exception e) {
			log.error("Error message delivered from the remote server\n" +  
			"You may wish to retry your last action");		
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});	
		}
	}
	public void add(final Display theDisplay, ConceptType node)
	{
		try {
			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			
				while(procStatus == null || !procStatus.getType().equals("DONE")){

					String response = OntServiceDriver.addChild(node);

					procStatus = msg.processResult(response);
					if (procStatus.getType().equals("ERROR")){		
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
						log.error(procStatus.getValue());				
						return;
					}			
				}
			
			
		} catch (AxisFault e) {
			log.error("Unable to make a connection to the remote server\n" +  
			"This is often a network error, please try again");
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});				
		} catch (Exception e) {
			log.error("Error message delivered from the remote server\n" +  
					"You may wish to retry your last action");		
			theDisplay.syncExec(new Runnable() {
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Unable to make a connection to the remote server\n" +  
					"This is often a network error, please try again");
					int result = mBox.open();
				}
			});	
		}
	}

	
	
}
	class EditSettingsPage extends WizardPage {

		private Text text1, text2, text5; //text3, text4, text6, 
		private Combo valTypeCombo, typeCombo, visAttribCombo;
		private String schemesKey = "";
		private Button enterValues;
		
		public static final String PAGE_NAME = "EditSettings"; //$NON-NLS-1$
		
		public EditSettingsPage() {
			super(PAGE_NAME); 
			
	//		String notice = "Selected Parent is: " + MetadataRecord.getInstance().getMetadata().getKey();			
			setTitle(Messages.getString("EditWizard.ItemSettings"));// + "\n\n" + notice);
			setPageComplete(true);
		}


		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			Composite itemSettings = new Composite(parent, SWT.NONE);

			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.horizontalSpacing = 1;
			gridLayout.verticalSpacing = 1;
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 5;
			itemSettings.setLayout(gridLayout);

			new Label(itemSettings,SWT.NONE);
			new Label(itemSettings,SWT.NONE);

			// This wizard re-uses the model object MetadataRecord created for new term insertions
			//   In this instance the MetadataRecord's parentData is the actual node we are editing.
			
			new Label(itemSettings, SWT.NONE).setText("The path is: ");
			new Label(itemSettings, SWT.NONE).setText(StringUtil.getPath(MetadataRecord.getInstance().getParentData().getFullName()));
			
			new Label(itemSettings, SWT.NONE);
			new Label(itemSettings, SWT.NONE);
			
			new Label (itemSettings, SWT.NONE).setText("*Name:");			
			text1 = new Text(itemSettings, SWT.BORDER);
			text1.setText(MetadataRecord.getInstance().getParentData().getName());
			text1.setToolTipText("Change the name that appears in the navigate tree.");
			
			GridData textData = new GridData ();	
			textData.widthHint = 250;
			textData.grabExcessHorizontalSpace = true;
			textData.horizontalAlignment = SWT.FILL;
			text1.setLayoutData(textData);


			
			// Check for page complete
			text1.addModifyListener(new ModifyListener() {
				
			      public void modifyText(ModifyEvent event) {
			    	  if(typeCombo.getText().equals("ITEM")){
			    		  // Page is not complete unless name & concept are not empty
			    		  if ( (text1.getText().length() > 0) && (text2.getText().length() > 0))
			    			  setPageComplete(true) ;
			    		  else 
			    			  setPageComplete(false);
			    	  }	  
			    	  else {
			    		  // if folder or container we only need a non-empty name
			    		  if ( (text1.getText().length() > 0) )
			    			  setPageComplete(true) ;
			    		  else
			    			  setPageComplete(false);
			    	  }
			      }
			    });
			text1.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
					}
					// dont allow certain characters.
					if(invalid(e.character)){
						e.doit = false;
						return;
					}
					if(text1.getText().length() > 1999){
						e.doit = false;
						return;
					}
					MetadataRecord.getInstance().setUpdateSyncIconFlag(true);
					
				}
			});
			
			new Label (itemSettings, SWT.NONE).setText("Base code:");
			Composite conceptCodeComp = new Composite(itemSettings, SWT.NONE);
			GridLayout grid2 = new GridLayout();
			grid2.numColumns = 2;
			grid2.horizontalSpacing = 1;
			grid2.verticalSpacing = 1;
			grid2.marginHeight = 0;
			grid2.marginWidth = 0;
			conceptCodeComp.setLayout(grid2);
			GridData data = new GridData();
			data.horizontalSpan = 1;
			data.horizontalAlignment = SWT.FILL;
			conceptCodeComp.setLayoutData(data);

		    final Combo schemesCombo = new Combo(conceptCodeComp,SWT.READ_ONLY);
			
			java.util.List<ConceptType> schemes = SchemesUtil.getInstance().getSchemes();
			if(schemes != null) {
				Iterator<ConceptType> schemesIterator = schemes.iterator();		
				while(schemesIterator.hasNext())
				{
					ConceptType scheme = (ConceptType) schemesIterator.next();
					String name = scheme.getName();
					schemesCombo.add(name);
				}
			}
			
			text2 = new Text(conceptCodeComp, SWT.BORDER);
			if(MetadataRecord.getInstance().getParentData().getBasecode() != null){
				String basecodeParts[] = MetadataRecord.getInstance().getParentData().getBasecode().split(":");			
				if(basecodeParts!=null){
					if(basecodeParts.length == 1)
						text2.setText(basecodeParts[0]);
					else{
						schemesCombo.setText(basecodeParts[0]);
						schemesKey = basecodeParts[0] + ":";
						text2.setText(basecodeParts[1]);
					}
				}
			}
			text2.setToolTipText("Basecodes (concept codes or provider codes) identify terms for queries.");
			GridData data2= new GridData();
			data2.widthHint = 135;
			data2.grabExcessHorizontalSpace = true;
			data2.horizontalAlignment = SWT.FILL;
			text2.setLayoutData(data2);
		
			text2.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
					}
					if(schemesKey.length() + text2.getText().length() > 49){
						e.doit = false;
						return;
					}
					MetadataRecord.getInstance().setUpdateSyncIconFlag(true);
				}
			});
			schemesCombo.addSelectionListener(new SelectionListener(){
		    	public void widgetSelected(SelectionEvent e) {
		    		MetadataRecord.getInstance().setUpdateSyncIconFlag(true);		// Item in list has been selected
		    		if (schemesCombo.getSelectionIndex() == 0)
		    			schemesKey = "";
		    		else{
		    			ConceptType concept = (ConceptType)SchemesUtil.getInstance().getSchemes().get(schemesCombo.getSelectionIndex());
		    			schemesKey = concept.getKey();
		    		}
		    	}
		    	public void widgetDefaultSelected(SelectionEvent e) {
		    		// this is not an option (text cant be entered)
		    	}
		    });
			
			text2.addModifyListener(new ModifyListener() {
			      public void modifyText(ModifyEvent event) {  
			    	  if(typeCombo.getText().equals("ITEM")){
			    		  // Page is not complete until a name, concept and symbol name have been added
			    		  if ( (text1.getText().length() > 0) && (text2.getText().length() > 0))
			    			  setPageComplete(true) ;
			    		  else
			    			  setPageComplete(false);
			    	  }
			      }
			    });
			
			new Label (itemSettings, SWT.NONE).setText("Tooltip:");			
			text5 = new Text(itemSettings, SWT.BORDER);
			text5.setToolTipText("The Tooltip is the tooltip that appears in the navigate tree.");
			text5.setLayoutData(textData);
			String tooltip = MetadataRecord.getInstance().getParentData().getTooltip();
			// Limit length of tool tip to 255 chars
			if(tooltip.length() > 254)
				tooltip = tooltip.substring(0,254);
			text5.setText(tooltip);
			text5.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					if(text5.getText().length() > 254)
						e.doit = false;
				}
			});
			

	
			
			new Label (itemSettings, SWT.NONE).setText("Item Type:");			
			typeCombo = new Combo(itemSettings,SWT.READ_ONLY);
			typeCombo.setToolTipText("Change term type.");
			typeCombo.add("FOLDER");
			typeCombo.add("ITEM");
			typeCombo.add("CONTAINER");

				
			typeCombo.addSelectionListener(new SelectionListener(){
				public void widgetSelected(SelectionEvent e) {
					if(typeCombo.getText().equals("ITEM")){
			    		  // Page is not complete unless name & concept are not empty for leaves
			    		  if ( (text1.getText().length() > 0) && (text2.getText().length() > 0))
			    			  setPageComplete(true) ;
			    		  else 
			    			  setPageComplete(false);
			    	  }	  
			    	  else {
			    		  // For containers and folders we only need a non-null name
			    		  if ( (text1.getText().length() > 0) )
			    			  setPageComplete(true) ;
			    		  else
			    			  setPageComplete(false);
			    	  }	
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					// this is not an option (text cant be entered)
				}
			});

			 
			
			
			
			new Label (itemSettings, SWT.NONE).setText("Visual Attribute:");			
			visAttribCombo = new Combo(itemSettings,SWT.READ_ONLY);
			visAttribCombo.setToolTipText("Change appearance of term's icon.");
			visAttribCombo.add("ACTIVE");
			visAttribCombo.add("INACTIVE");
			visAttribCombo.add("HIDDEN");
			
		
			visAttribCombo.addSelectionListener(new SelectionListener(){
				public void widgetSelected(SelectionEvent e) {
					MetadataRecord.getInstance().setUpdateSyncIconFlag(true);		// Item in list has been selected
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					// this is not an option (text cant be entered)
				}
			});

			
			String visAttribute = MetadataRecord.getInstance().getParentData().getVisualattributes();
			if(visAttribute.startsWith("F"))
				typeCombo.setText("FOLDER");
			else if(visAttribute.startsWith("C"))
				typeCombo.setText("CONTAINER");
			else if(visAttribute.startsWith("L"))
				typeCombo.setText("ITEM");
			
			if(visAttribute.charAt(1) == ('A'))
				visAttribCombo.setText("ACTIVE");
			else if(visAttribute.charAt(1) == ('I'))
				visAttribCombo.setText("INACTIVE");
			else if(visAttribute.charAt(1) == ('H'))
				visAttribCombo.setText("HIDDEN");
			
			new Label (itemSettings, SWT.NONE).setText("Semantic type:");	
			Composite semanticComp = new Composite(itemSettings, SWT.NONE);
			GridLayout grid3 = new GridLayout();
			grid2.numColumns = 3;
			grid2.horizontalSpacing = 1;
			grid2.verticalSpacing = 1;
			grid2.marginHeight = 0;
			grid2.marginWidth = 0;
			semanticComp.setLayout(grid2);
			semanticComp.setLayoutData(data);
			valTypeCombo = new Combo(semanticComp,SWT.READ_ONLY);
			valTypeCombo.setToolTipText("'DOC'== a document or note; 'LAB' == lab results");
			valTypeCombo.add("");
			valTypeCombo.add("DOC");
			valTypeCombo.add("LAB");
			
			valTypeCombo.setText("");

			String semanticCode = MetadataRecord.getInstance().getParentData().getValuetypeCd();
			if (semanticCode != null)
				valTypeCombo.setText(semanticCode);
			
			valTypeCombo.addSelectionListener(new SelectionListener(){
		    	public void widgetSelected(SelectionEvent e) {
		    		if(valTypeCombo.getText().equals("LAB")){
		    			enterValues.setSelection(true);
		    		}
		    	}
		    	public void widgetDefaultSelected(SelectionEvent e) {
		    		// this is not an option (text cant be entered)
		    	}
		    });
			
			new Label (semanticComp, SWT.NONE).setText("      ");	
			
			enterValues = new Button (semanticComp, SWT.CHECK);
			enterValues.setText("Enter Value Information");		
			enterValues.setSelection(ValueMetadata.getInstance().hasValueMetadataType());
			
			new Label (itemSettings, SWT.NONE);
			new Label (itemSettings, SWT.NONE);
			new Label (itemSettings, SWT.NONE).setText("* denotes required field.");			
				
			setControl(itemSettings);
				
		/*	
			
			new Label (itemSettings, SWT.NONE).setText("*Symbol Name:");			
			text6 = new Text(itemSettings, SWT.BORDER);
			text6.setToolTipText("The symbol name is appended to the path to create the full path name");
			text6.setLayoutData(textData);
			text6.addModifyListener(new ModifyListener() {
			      public void modifyText(ModifyEvent event) {  
				    	// Page is not complete until a name, concept and symbol name have been added
			    	  if ( (text1.getText().length() > 0) && (text6.getText().length() > 0))
			    		  setPageComplete(true) ;
			    	  else
			    		  setPageComplete(false);
			      }
			    });
			text6.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					// dont allow certain characters.
					if(invalid(e.character))
						e.doit = false;
					if(text6.getText().length() > 33)
						e.doit = false;
				}
			});
			
			
			new Label (itemSettings, SWT.NONE).setText("Sourcesystem code:");			
			text3 = new Text(itemSettings, SWT.BORDER);
			text3.setToolTipText("A Sourcesystem code describes the system the data was derived from.");
			text3.setLayoutData(textData);
			text3.setText(UserInfoBean.getInstance().getUserName() + "_manualentry");
			text3.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					if(text3.getText().length() > 49)
						e.doit = false;
				}
			});
			
			new Label (itemSettings, SWT.NONE).setText("Comment:");			
			text4 = new Text(itemSettings, SWT.BORDER);
			text4.setToolTipText("Miscellanous (optional) comments may be entered here.");
			text4.setLayoutData(textData);
			text4.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					if(text4.getText().length() > 1999)
						e.doit = false;
				}
			});
			*/
		

		}
		
		@Override
		public void performHelp(){

			String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
			String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_editSettings";
			
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);
			
			// to show big help page
			//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		
		
			
		}
		
		public void updateMetadataRecord(){
		
			MetadataRecord.getInstance().getMetadata().setName(getName());
			MetadataRecord.getInstance().getMetadata().setValuetypeCd(getValuetypeCd());
			MetadataRecord.getInstance().getMetadata().setTooltip(getTooltip());
			MetadataRecord.getInstance().getMetadata().setVisualattributes(getVisualAttribute());
			MetadataRecord.getInstance().getMetadata().setLevel(MetadataRecord.getInstance().getParentData().getLevel());
			MetadataRecord.getInstance().getMetadata().setBasecode(getBasecode());
			MetadataRecord.getInstance().setValueMetadataFlag(enterValues.getSelection());
		}

		@Override
		public String getName()
		{
			return text1.getText();
		}
		
		public String getBasecode() {
			String code = text2.getText();
			if(!((code == null) || (code.length() == 0))){
				String prefix = schemesKey;
				if(!((prefix == null) || (prefix.length() == 0)))
					return prefix + code;
				else
					return code;
			}
			else 
				return "";
		}
		
		public String getVisualAttribute() {	
			// get first letter of VisAttrib from type Combo
			//  if type == ITEM, change first letter to L (for Leaf)
			String vA0 = typeCombo.getText().substring(0,1);
			if(vA0.equals("I"))
				vA0 = "L";
			
			return 	vA0 + visAttribCombo.getText().substring(0,1) + "E";
		}

		public String getValuetypeCd() {
			return valTypeCombo.getText();
		}
		public String getTooltip() {
			return text5.getText();
		}
		private boolean invalid(char c){
			if( (c == '*') || (c == '|') || (c == '/') || 
					 (c == '\\')  || (c == '"') || 
					 (c == '<') || (c == '%') || (c == '?')) {
				// the characters ':' and '>' are now allowed (genomic data uses them)
				
				MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
				mBox.setText("Please Note ...");
				mBox.setMessage("The c are not allowed for this field \n" + 
				" *   |   \\   /    \"   <   ?  %");
				int result = mBox.open();
				
				return true;
			}
			else
				return false;
		}
	}




	
	





