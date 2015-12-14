package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType;

public class ValueMetadataSettingsPageFour extends WizardPage {

		private Text text1, text2, text3, text4, text5, text6;
		private Group rangeGroup, toxicGroup;
		public static final String PAGE_NAME = "ValueMetadataSettings3"; //$NON-NLS-1$
		
		
		public ValueMetadataSettingsPageFour() {
			super(PAGE_NAME); 
			
			setTitle(Messages.getString("Wizard.ValueMetadataSettings3"));
			setDescription("Optional value settings for a lab associated item");
			
			setPageComplete(true);
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

			new Label(settings, SWT.NONE);
			new Label(settings, SWT.NONE);
			
			GridLayout groupLayout = new GridLayout();
			groupLayout.numColumns = 2;
			GridData groupData = new GridData(GridData.FILL_HORIZONTAL);
			groupData.horizontalSpan = 2;
			
			
			rangeGroup = new Group(settings, SWT.NONE);
			rangeGroup.setLayout(groupLayout);
			rangeGroup.setLayoutData(groupData);
			rangeGroup.setText("High - low value ranges");
		
			GridData textData = new GridData ();	
			textData.widthHint = 250;
			textData.grabExcessHorizontalSpace = true;
			textData.horizontalAlignment = SWT.FILL;
			
			new Label (rangeGroup, SWT.NONE).setText("Low end of low value range:");
			text3 = new Text(rangeGroup,SWT.BORDER);
			text3.setLayoutData(textData);
			text3.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					
					
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					// dont allow non-numeric characters
					//this covers case where we receive a string from db on edit
					String info = (String) e.text;
					if (info.length() > 1){
						e.doit = true;
						return;
					}
					
					if(invalid(info.charAt(0)))
						e.doit = false;
				}
			});
			
			new Label (rangeGroup, SWT.NONE).setText("High end of low value range:");
			text4 = new Text(rangeGroup,SWT.BORDER);
			text4.setLayoutData(textData);
			text4.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					// dont allow non-numeric characters
					//this covers case where we receive a string from db on edit
					String info = (String) e.text;
					if (info.length() > 1){
						e.doit = true;
						return;
					}
					
					if(invalid(info.charAt(0)))
						e.doit = false;
				}
			});
			
			new Label (rangeGroup, SWT.NONE).setText("Low end of high value range:");
			text5 = new Text(rangeGroup,SWT.BORDER);
			text5.setLayoutData(textData);
			text5.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					// dont allow non-numeric characters
					//this covers case where we receive a string from db on edit
					String info = (String) e.text;
					if (info.length() > 1){
						e.doit = true;
						return;
					}
					
					if(invalid(info.charAt(0)))
						e.doit = false;
					
				}
			});
			
			new Label (rangeGroup, SWT.NONE).setText("High end of high value range:");
			text6 = new Text(rangeGroup,SWT.BORDER);
			text6.setLayoutData(textData);
			text6.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					// dont allow non-numeric characters
					//this covers case where we receive a string from db on edit
					String info = (String) e.text;
					if (info.length() > 1){
						e.doit = true;
						return;
					}
					
					if(invalid(info.charAt(0)))
						e.doit = false;
					
				}
			});
			
			
			new Label(settings, SWT.NONE);
			new Label(settings, SWT.NONE);
			
			
			toxicGroup = new Group(settings, SWT.NONE);	
			toxicGroup.setLayout(groupLayout);
			toxicGroup.setText("Toxic value ranges");
			
			GridData groupData2	 = new GridData(GridData.FILL_HORIZONTAL);
			groupData2.horizontalSpan = 2;
			groupData2.grabExcessVerticalSpace = false;
			
			toxicGroup.setLayoutData(groupData2);
			
			new Label (toxicGroup, SWT.NONE).setText("Low end of toxic value range:");
			text1 = new Text(toxicGroup,SWT.BORDER);
			text1.setLayoutData(textData);
			text1.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					// dont allow non-numeric characters
					//this covers case where we receive a string from db on edit
					String info = (String) e.text;
					if (info.length() > 1){
						e.doit = true;
						return;
					}
					
					if(invalid(info.charAt(0)))
						e.doit = false;
					
				}
			});
			
			new Label (toxicGroup, SWT.NONE).setText("High end of toxic value range:");
			text2 = new Text(toxicGroup,SWT.BORDER);
			text2.setLayoutData(textData);
			text2.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					// dont allow non-numeric characters
					//this covers case where we receive a string from db on edit
					String info = (String) e.text;
					if (info.length() > 1){
						e.doit = true;
						return;
					}
					
					if(invalid(info.charAt(0)))
						e.doit = false;
					}
				
			});
			if(ValueMetadata.getInstance().hasValueMetadataType()){
				ValueMetadataType vmType = ValueMetadata.getInstance().getValueMetadataType() ;
			
				String lowOfLowType = vmType.getLowofLowValue();
				if (!((lowOfLowType == null) || (lowOfLowType.isEmpty()))){
					text3.setText(lowOfLowType);
				}
				
				String highOfLowType = vmType.getHighofLowValue();
				if (!((highOfLowType == null) || (highOfLowType.isEmpty()))){
					text4.setText(highOfLowType);
				}
				String lowOfHighType = vmType.getLowofHighValue();
				if (!((lowOfHighType == null) || (lowOfHighType.isEmpty()))){
					text5.setText(lowOfHighType);
				}
				String highOfHighType = vmType.getHighofHighValue();
				if (!((highOfHighType == null) || (highOfHighType.isEmpty()))){
					text6.setText(highOfHighType);
				}
				String lowOfToxicType = vmType.getLowofToxicValue();	
				if (!((lowOfToxicType == null) || (lowOfToxicType.isEmpty()))){
					text1.setText(lowOfToxicType);
				}
				String highOfToxicType = vmType.getHighofToxicValue();
				if (!((highOfToxicType == null) || (highOfToxicType.isEmpty()))){
					text2.setText(highOfToxicType);
				}
			}	
	
			setControl(settings);

		}

	@Override
		public void performHelp(){

			String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
			String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_valueRangeMetadata";
			
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);
			
			// to show big help page
			//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		
		
			
		}
			

		public void updateValueMetadata(){	
			ValueMetadata.getInstance().getValueMetadataType().setLowofLowValue(text3.getText());
			ValueMetadata.getInstance().getValueMetadataType().setHighofLowValue(text4.getText());
			ValueMetadata.getInstance().getValueMetadataType().setLowofHighValue(text5.getText());
			ValueMetadata.getInstance().getValueMetadataType().setHighofHighValue(text6.getText());
			ValueMetadata.getInstance().getValueMetadataType().setLowofToxicValue(text1.getText());
			ValueMetadata.getInstance().getValueMetadataType().setHighofToxicValue(text2.getText());
			ValueMetadata.getInstance().fillEmptyValues();
		}
	
		private boolean invalid(char c){

			if(ValueMetadata.getInstance().getValueMetadataType().getDataType().contains("Pos")){
				if (c == '-') {

					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Negative signs are not allowed for this field" );
					int result = mBox.open();

					return true;
				}
			}
			if(ValueMetadata.getInstance().getValueMetadataType().getDataType().contains("Int")){
				if (c == '.') {

					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Decimal points are not allowed for this field" );
					int result = mBox.open();

					return true;
				}
			}


			if( !( (c == '-')||(c == '+') || (c == '-') || (c == '0') || 
					 (c == '1') || (c == '2') || (c == '3') || (c =='.') ||
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

