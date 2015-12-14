package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType;

public class ValueMetadataSettingsPageTwo extends WizardPage {

		private Combo dataTypeCombo, flagTypeCombo, valueTypeCombo;
		private Text text1;

		public static final String PAGE_NAME = "ValueMetadataSettings2"; //$NON-NLS-1$
		
		
		public ValueMetadataSettingsPageTwo() {
			super(PAGE_NAME); 
			
			setTitle(Messages.getString("Wizard.ValueMetadataSettings2"));
			setDescription("Optional value settings for a lab associated item");
			
			setPageComplete(ValueMetadata.getInstance().hasValueMetadataType());
		}


		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			Composite settings = new Composite(parent, SWT.NONE);

			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.horizontalSpacing = 1;
			gridLayout.verticalSpacing = 1;
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 0;
			settings.setLayout(gridLayout);
			
			
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			
			
			GridData data = new GridData();
			data.horizontalAlignment = SWT.RIGHT;	
			data.horizontalSpan = 2;
			Composite rightJustified = new Composite(settings, SWT.NONE);
			rightJustified.setLayoutData(data);
			rightJustified.setLayout(new GridLayout());
		//	new Label (rightJustified, SWT.NONE);
			Button clear = new Button(rightJustified, SWT.PUSH);
			
			clear.setText("Clear value metadata");
			clear.addSelectionListener(new SelectionListener(){
		    	public void widgetSelected(SelectionEvent e) {
		    		MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION|SWT.YES|SWT.NO);
					mBox.setText("Clear Value Metadata Warning");
					mBox.setMessage("Are you sure you want to clear the Value Metadata?" );
					int result = mBox.open();
					
					if(result == SWT.NO)
						return;
					
					else{
						ValueMetadata.getInstance().clear();
					
						dataTypeCombo.add("");
						dataTypeCombo.select(6);
						dataTypeCombo.remove("");
						
						flagTypeCombo.setText("None");
						
						valueTypeCombo.add("");
						valueTypeCombo.select(2);
						valueTypeCombo.remove("");
						valueTypeCombo.setEnabled(false);
						
						text1.setText("");
						text1.setEnabled(false);
						
					}
		    		
		    	}
		    	public void widgetDefaultSelected(SelectionEvent e) {
		    		// this is not an option (text cant be entered)
		    	}
		    });
			
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			
			new Label (settings, SWT.NONE).setText("Data Type:");
			dataTypeCombo = new Combo(settings,SWT.READ_ONLY);
			dataTypeCombo.add("Enumeration");
			dataTypeCombo.add("Positive Integer");
			dataTypeCombo.add("Integer");
			dataTypeCombo.add("Positive Float");
			dataTypeCombo.add("Float");
			dataTypeCombo.add("String");
			

			new Label (settings, SWT.NONE).setText("Flag Type:");
			flagTypeCombo = new Combo(settings,SWT.READ_ONLY);
			flagTypeCombo.add("None");
			flagTypeCombo.add("Abnormal");
			flagTypeCombo.add("High/Low");
			flagTypeCombo.setText("None");
			
			flagTypeCombo.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if(dataTypeCombo.getText().isEmpty())
							setPageComplete(false);
				
				}	
			});			
			
			
			new Label (settings, SWT.NONE).setText("Use Numeric Value Type:");
			valueTypeCombo = new Combo(settings,SWT.READ_ONLY);
			valueTypeCombo.add("Yes");
			valueTypeCombo.add("No");
			valueTypeCombo.setEnabled(false);
			
			dataTypeCombo.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					String type = dataTypeCombo.getText();
					if(type.equals("String")){
						ValueMetadata.getInstance().setStringType(true);
						ValueMetadata.getInstance().setEnumType(false);
						ValueMetadata.getInstance().setNumericType(false);
						valueTypeCombo.setEnabled(false);
						flagTypeCombo.remove("High/Low");
						text1.setEnabled(true);
						setPageComplete(true);
					}
						
					else if(type.equals("Enumeration")){	
						ValueMetadata.getInstance().setEnumType(true);
						ValueMetadata.getInstance().setStringType(false);
						ValueMetadata.getInstance().setNumericType(false);
						valueTypeCombo.setEnabled(false);
						if(flagTypeCombo.getItemCount() == 2)
							flagTypeCombo.add("High/Low");
						text1.setEnabled(false);
						setPageComplete(true);
					}
					else if(type.isEmpty()){
						setPageComplete(false);
					}
						
					else{
						ValueMetadata.getInstance().setEnumType(false);
						ValueMetadata.getInstance().setStringType(false);
						
						String ok = valueTypeCombo.getText();
						if (!((ok == null) || (ok.isEmpty()))){
							if(ok.equals("Yes"))
								ValueMetadata.getInstance().setNumericType(true);
							else
								ValueMetadata.getInstance().setNumericType(false);
						}
						
						setPageComplete(true);
						valueTypeCombo.setEnabled(true);
						if(flagTypeCombo.getItemCount() == 2)
							flagTypeCombo.add("High/Low");
						text1.setEnabled(false);
					}
				}
			});			
			
			valueTypeCombo.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if(dataTypeCombo.getText().isEmpty())
						setPageComplete(false);
					String type = valueTypeCombo.getText();
					if(type.equals("Yes")){
						ValueMetadata.getInstance().setNumericType(true);
					}
					else{
						ValueMetadata.getInstance().setNumericType(false);
					}
				}
			});		
			
	
			
			new Label (settings, SWT.NONE).setText("Maximum String Length:");			
			text1 = new Text(settings, SWT.BORDER);
			
			GridData textData = new GridData ();	
			textData.widthHint = 250;
			textData.grabExcessHorizontalSpace = true;
			textData.horizontalAlignment = SWT.FILL;
			text1.setLayoutData(textData);
			text1.setEnabled(false);
			
			text1.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){		
					// if backspace or delete key then accept
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					// if we are clearing metadata then accept
					if (!(ValueMetadata.getInstance().hasValueMetadataType()))
					{
						e.doit = true;
						return;
					}
					int valueParsed = 1;
					try {
						// if value on startup is an integer, then accept
						valueParsed = Integer.parseInt(e.text);
						e.doit = true;
						return;
					} catch (NumberFormatException e1) {
						// otherwise startup from db is not an integer
						e.doit = false;
					}
					// if value from db is less than one , dont accept
					if(valueParsed < 1){
						e.doit = false;
						return;
					}
					// if we get here is because we entered in "." or "," 
					else {
						if(invalid(e.character)){
							e.doit = false;
						}
					}
				}
			});
	
	
			if(ValueMetadata.getInstance().hasValueMetadataType()){
				ValueMetadataType vmType = ValueMetadata.getInstance().getValueMetadataType() ;
				String dataType = vmType.getDataType();
				if (!((dataType == null) || (dataType.isEmpty()))){
			
					dataType = dataType.replace("Pos", "Positive ");
					dataType = dataType.replace("Enum", "Enumeration");
					dataTypeCombo.setText(dataType);
				
					if(!((dataType.equals("Enumeration")) || (dataType.equals("String")))){
						valueTypeCombo.setEnabled(true);
						ValueMetadata.getInstance().setEnumType(false);
						ValueMetadata.getInstance().setStringType(false);
					}
					else{
						valueTypeCombo.setEnabled(false);
						text1.setEnabled(false);
						ValueMetadata.getInstance().setEnumType(true);
						ValueMetadata.getInstance().setStringType(false);
						if(dataType.equals("String")){
							ValueMetadata.getInstance().setEnumType(false);
							ValueMetadata.getInstance().setStringType(true);
							text1.setEnabled(true);
							if (vmType.getMaxStringLength()!=null){
								text1.setText(vmType.getMaxStringLength());
							}
						}
					}
				}
				String flagType = vmType.getFlagstouse();
				if (!((flagType == null) || (flagType.isEmpty()))){
					flagType = flagType.replace("HL", "High/Low");
					flagType = flagType.replace("A", "Abnormal");
					flagTypeCombo.setText(flagType);
				}
				else flagTypeCombo.setText("None");
				
				String ok = vmType.getOktousevalues();
				if (!((ok == null) || (ok.isEmpty()))){
					ok = ok.replace("Y", "Yes");
					ok = ok.replace("N", "No");
					valueTypeCombo.setText(ok);
					if(ok.equals("Yes"))
						ValueMetadata.getInstance().setNumericType(true);
					else
						ValueMetadata.getInstance().setNumericType(false);
				}
				
			}
				
			setControl(settings);

		}

		@Override
		public void performHelp(){

			String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
			String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_valueMetadata";
			
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);
			
			// to show big help page
			//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		
		
			
		}


		public void updateValueMetadata(){
			ValueMetadataType test = ValueMetadata.getInstance().getValueMetadataType();
			if(test != null){
				String dataType = dataTypeCombo.getText();
				if((dataType != null) && (!dataType.isEmpty())){
					dataType = dataType.replace("Positive ", "Pos");
					dataType = dataType.replace("Enumeration", "Enum");
				}	
				ValueMetadata.getInstance().getValueMetadataType().setDataType(dataType);

				String flag = flagTypeCombo.getText();
				if((flag != null) && flag.equals("High/Low")){
					flag = "HL";
				}
				else if((flag != null) && flag.equals("Abnormal")){
					flag = "A";
				}
				else if((flag != null) && flag.equals("None")){
					flag = null;
				}
				ValueMetadata.getInstance().getValueMetadataType().setFlagstouse(flag);

				String ok = valueTypeCombo.getText();
				if((ok != null) && (!(ok.isEmpty()))){
					ok = ok.substring(0,1);
				}
				ValueMetadata.getInstance().getValueMetadataType().setOktousevalues(ok);

				ValueMetadata.getInstance().getValueMetadataType().setMaxStringLength(text1.getText());
			}
	/*		TableItem[] items = table.getItems();
			if(items.length == 0)
				return;
			CommentsDetermingExclusion comments = new CommentsDetermingExclusion();
			for( int i=0; i< items.length; i++)				
				comments.getCom().add(items[i].getText());
				
			ValueMetadata.getInstance().getValueMetadataType().setCommentsDetermingExclusion(comments);
			*/
		}
	

		private boolean invalid(char c){

			if( !( (c == '0') || 
					(c == '1') || (c == '2') || (c == '3') || 
					(c == '4') || (c == '5') || (c == '6') ||
					(c == '7') || (c == '8') || (c == '9')) ){

				MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
				mBox.setText("Please Note ...");
				mBox.setMessage("Only numeric characters are allowed for this field" );
				int result = mBox.open();

				return true;
			}
			else
				return false;
		}

		
	}

