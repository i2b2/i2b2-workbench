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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.wizard.Wizard;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.*;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptsType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetTermInfoType;


/*
 * 
 */


public class ContainerWizard extends Wizard {
	private static final Log log = LogFactory.getLog(ContainerWizard.class);
	private boolean okToFinish = false;

	public ContainerWizard() {
		setWindowTitle(Messages.getString("ContainerWizard.WindowTitle")); //$NON-NLS-1$
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
		addPage(new ContainerSettingsPage());
		addPage(new SynonymsPage());
		addPage(new QueryDimensionSettingsPage());
//		addPage(new ValueMetadataSettingsPageOne());
		addPage(new ValueMetadataSettingsPageTwo());
		addPage(new ValueMetadataSettingsPageThree());
		addPage(new ValueMetadataSettingsPageFour());
		addPage(new ValueMetadataSettingsPageFive());
		addPage(new VerifyDataPage());

	}


	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof ContainerSettingsPage) {
			((ContainerSettingsPage) page).updateMetadataRecord();
			okToFinish = false;
		}
		
		else if (page instanceof QueryDimensionSettingsPage) {
			((QueryDimensionSettingsPage) page).updateMetadataRecord();
			//skip remainder of pages if no value metadata is selected
			if (!(MetadataRecord.getInstance().isValueMetadataFlag())){

				page = getPage(ValueMetadataSettingsPageFive.PAGE_NAME);

				VerifyDataPage verifyPage = (VerifyDataPage) getPage(VerifyDataPage.PAGE_NAME);
				okToFinish =verifyPage.updateParameters();
				if(okToFinish)
					okToFinish = page.isPageComplete();
			}
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
		else if (page instanceof ValueMetadataSettingsPageTwo) {
			((ValueMetadataSettingsPageTwo) page).updateValueMetadata();
			//		ValueMetadata vm = ValueMetadata.getInstance();
			// if page is enum type -- continue to page 3.
			if((ValueMetadata.getInstance().isNumericType())){
				page = getPage(ValueMetadataSettingsPageThree.PAGE_NAME);
				okToFinish = false;
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
			mBox.setText("New Container Warning");
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
			
		if (page instanceof ContainerSettingsPage) {
			((ContainerSettingsPage) page).updateMetadataRecord();
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
		
		
		if(validate()){
			addChild().start();	
			ValueMetadata.getInstance().clear();
			return true;
		}
		else
			return false;
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
	
	public Thread addChild(){
		final Display theDisplay = Display.getCurrent();
		return new Thread() {
			@Override
			public void run(){
				try {
					add(theDisplay);
				} catch (Exception e) {
					log.error("Add child error");					
				}
				theDisplay.syncExec(new Runnable() {
					public void run() {
						MetadataRecord.getInstance().getBrowser().update();
						MetadataRecord.getInstance().getSyncAction()
						.setImageDescriptor(ImageDescriptor.createFromFile(EditView.class, "/icons/red_database_refresh.png"));
					}
				});
			}
		};
	}
	public void add(final Display theDisplay)
	{
		try {
			OntologyResponseMessage msg = new OntologyResponseMessage();
			StatusType procStatus = null;	
			while(procStatus == null || !procStatus.getType().equals("DONE")){
				
				String response = OntServiceDriver.addChild(MetadataRecord.getInstance().getMetadata());
				
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
			
		} catch (Exception e) {
			log.error("Error message delivered from the remote server\n" +  
					"You may wish to retry your last action");		
		}
	}
}

class ContainerSettingsPage extends WizardPage {

	private Text text1, text2, text3, text4, text6, text5;
	private String schemesKey = "";
	private Combo valTypeCombo;
	private Button enterValues;

	public static final String PAGE_NAME = "ContainerSettings"; //$NON-NLS-1$

	public ContainerSettingsPage() {
		super(PAGE_NAME); 

		//		String notice = "Selected Parent is: " + MetadataRecord.getInstance().getMetadata().getKey();			
		setTitle(Messages.getString("ContainerWizard.ItemSettings"));// + "\n\n" + notice);
		setPageComplete(false);
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
		gridLayout.marginWidth = 0;

		itemSettings.setLayout(gridLayout);

		new Label(itemSettings,SWT.NONE);
		new Label(itemSettings,SWT.NONE);

		ImageDescriptor imageDescriptorFolder = ImageDescriptor.createFromFile(getClass(), "icons/closedFolder.jpg");
		ImageDescriptor imageDescriptorContainer = ImageDescriptor.createFromFile(getClass(), "icons/closedCase.jpg");

		Image image = null;
		if(MetadataRecord.getInstance().getParentData().getVisualattributes().startsWith("F"))
			image = imageDescriptorFolder.createImage();

		else if(MetadataRecord.getInstance().getParentData().getVisualattributes().startsWith("C"))
			image = imageDescriptorContainer.createImage();

		new Label(itemSettings, SWT.NONE).setText("The parent will be: ");
		Composite parentInfo = new Composite(itemSettings, SWT.NONE);
		parentInfo.setLayout(gridLayout);


		new Label(parentInfo, SWT.NONE).setImage(image);
		new Label(parentInfo, SWT.NONE).setText(" " + MetadataRecord.getInstance().getParentData().getName());

		new Label(itemSettings, SWT.NONE).setText("The path will be: ");
		new Label(itemSettings, SWT.NONE).setText(StringUtil.getPath(MetadataRecord.getInstance().getParentData().getFullName()));

		new Label(itemSettings, SWT.NONE);
		new Label(itemSettings, SWT.NONE);

		new Label (itemSettings, SWT.NONE).setText("*Container Name:");			
		text1 = new Text(itemSettings, SWT.BORDER);
		text1.setToolTipText("The Container Name is the name that appears in the navigate tree.");

		GridData textData = new GridData ();	
		textData.widthHint = 250;
		textData.grabExcessHorizontalSpace = true;
		textData.horizontalAlignment = SWT.FILL;
		text1.setLayoutData(textData);

		// Page is not complete until a name has been added
		text1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				//  update tooltip and symbol name as name is entered

				String name = text1.getText();
				text5.setText(MetadataRecord.getInstance().getParentData().getTooltip() + " \\ " 
						+  name);

				if(name.length() > 33)
					name = name.substring(0, 33) + "~";
				text6.setText(name);

				// Page is not complete until a name and symbol name have been added
				if ( (text1.getText().length() > 0) && (text6.getText().length() > 0))
					setPageComplete(true) ;
				else
					setPageComplete(false);

			}
		});
		text1.addVerifyListener(new VerifyListener() {			
			public void verifyText(VerifyEvent e){
				if((e.character == '\b') || (e.character == '\u007F')){
					e.doit = true;
					return;
				}
				// dont allow certain characters.
				if(invalid(e.character))
					e.doit = false;

				if(text1.getText().length() > 1999)
					e.doit = false;
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

		Label conceptLabel = new Label (itemSettings, SWT.NONE);
		conceptLabel.setText("Base code:");
		conceptLabel.setEnabled(false);
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
		schemesCombo.setEnabled(false);
		List<ConceptType> schemes = SchemesUtil.getInstance().getSchemes();
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
		text2.setEnabled(false);
		text2.setToolTipText("Base codes (concept codes, provider codes) identify terms for queries.");
		GridData data2= new GridData();
		data2.widthHint = 135;
		data2.grabExcessHorizontalSpace = true;
		data2.horizontalAlignment = SWT.FILL;
		text2.setLayoutData(data2);

		text2.addVerifyListener(new VerifyListener() {			
			public void verifyText(VerifyEvent e){
				if((e.character == '\b') || (e.character == '\u007F')){
					e.doit = true;
					return;
				}
				if(schemesKey.length() + text2.getText().length() > 49)
					e.doit = false;
			}
		});

		schemesCombo.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				// Item in list has been selected
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
		text4.setToolTipText("Miscellaneous (optional) comments may be entered here.");
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


		new Label (itemSettings, SWT.NONE);
		new Label (itemSettings, SWT.NONE);
		new Label (itemSettings, SWT.NONE).setText("* denotes required field.");			

		setControl(itemSettings);

	}
	@Override
	public void performHelp(){

		String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
		String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_container";

		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);

		// to show big help page
		//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		


	}
	public void updateMetadataRecord(){
		MetadataRecord.getInstance().getMetadata().setName(getName());
		MetadataRecord.getInstance().getMetadata().setBasecode(getConceptCode());
		MetadataRecord.getInstance().getMetadata().setSourcesystemCd(getSourcesystemCode());
		MetadataRecord.getInstance().getMetadata().setComment(getComment());
		MetadataRecord.getInstance().getMetadata().setValuetypeCd(getValuetypeCd());
		MetadataRecord.getInstance().getMetadata().setTooltip(getTooltip());	
		MetadataRecord.getInstance().setSymbol(getSymbol());
		MetadataRecord.getInstance().getMetadata().setKey(MetadataRecord.getInstance().getParentData().getKey()+ MetadataRecord.getInstance().getSymbol() + "\\");
		MetadataRecord.getInstance().getMetadata().setDimcode(MetadataRecord.getInstance().getParentData().getDimcode()+ MetadataRecord.getInstance().getSymbol() + "\\");
		MetadataRecord.getInstance().setValueMetadataFlag(enterValues.getSelection());
	}

	@Override
	public String getName()
	{
		return text1.getText();
	}
	public String getConceptCode() {
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

	public String getSourcesystemCode()
	{
		return text3.getText();
	}
	public String getSymbol()
	{
		return text6.getText();
	}
	public String getComment() {
		return text4.getText();
	}
	public String getValuetypeCd() {
		return valTypeCombo.getText();
	}

	public String getTooltip() {
		return text5.getText();
	}

	private boolean invalid(char c){
		if( (c == '*') || (c == '|') || (c == '/') || 
				(c == '\\') || (c == ':') || (c == '"') || 
				(c == '<') || (c == '>') || (c == '%') || (c == '?')) {

			MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
			mBox.setText("Please Note ...");
			mBox.setMessage("The following characters are not allowed for this field \n" + 
			" *   |   \\   /   :   \"   <   >   ?  %");
			int result = mBox.open();

			return true;
		}
		else
			return false;
	}

}






