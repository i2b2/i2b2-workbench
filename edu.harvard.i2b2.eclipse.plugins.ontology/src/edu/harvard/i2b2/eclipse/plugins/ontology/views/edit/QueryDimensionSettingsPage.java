package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;

public class QueryDimensionSettingsPage extends WizardPage {

		private Text text1, text2, text3, text4, text5, text6;
		private Combo dataTypeCombo;
		public static final String PAGE_NAME = "QueryDimensionSettings"; //$NON-NLS-1$
		
		
		public QueryDimensionSettingsPage() {
			super(PAGE_NAME); 
			
			setTitle(Messages.getString("Wizard.QueryDimensionSettings"));
			setDescription("These settings are inherited from the parent node.");
			
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

			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			
			new Label (settings, SWT.NONE).setText("*Table Name:");			
			text1 = new Text(settings, SWT.BORDER);

			if((MetadataRecord.getInstance().getParentData().getModifier() == null) ||
					((MetadataRecord.getInstance().getParentData().getModifier().getName() == null)))
				text1.setText(MetadataRecord.getInstance().getParentData().getTablename());
			else 
				text1.setText(MetadataRecord.getInstance().getParentData().getModifier().getTablename());
			text1.addModifyListener(new ModifyListener() {
			      public void modifyText(ModifyEvent event) {	
				      	// Page is not complete until all required fields have been added
/*			    	  if ( (text1.getText().length() > 0) && (text2.getText().length() > 0) 
			    			  && (text3.getText().length() > 0)&& (text4.getText().length() > 0))
			    		  setPageComplete(true) ;
			    	  else
			    		  setPageComplete(false);
*/
			    	  setPageComplete(text1.getText().length()>0);
			      }
			    });
			text1.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					if(text1.getText().length() > 49)
						e.doit = false;
				}
			});
			
			
			GridData textData = new GridData ();	
			textData.widthHint = 250;
			textData.grabExcessHorizontalSpace = true;
			textData.horizontalAlignment = SWT.FILL;
			text1.setLayoutData(textData);

			new Label (settings, SWT.NONE).setText("*Column Name:");			
			text2 = new Text(settings, SWT.BORDER);
			text2.setLayoutData(textData);
			if((MetadataRecord.getInstance().getParentData().getModifier() == null) ||
					((MetadataRecord.getInstance().getParentData().getModifier().getName() == null)))
				text2.setText(MetadataRecord.getInstance().getParentData().getColumnname());
			else 
				text2.setText(MetadataRecord.getInstance().getParentData().getModifier().getColumnname());

			text2.addModifyListener(new ModifyListener() {
			      public void modifyText(ModifyEvent event) {	
			      	// Page is not complete until all required fields have been added
			    	/*  if ( (text1.getText().length() > 0) && (text2.getText().length() > 0) 
			    			  && (text3.getText().length() > 0)&& (text4.getText().length() > 0))
			    		  setPageComplete(true) ;
			    	  else
			    		  setPageComplete(false);
			    	  */
			    	  setPageComplete(text2.getText().length()>0);
			      }
			    });
			text2.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					if(text2.getText().length() > 49)
						e.doit = false;
				}
			});
			
			new Label (settings, SWT.NONE).setText("*Fact Table Column Name:");			
			text3 = new Text(settings, SWT.BORDER);
			text3.setLayoutData(textData);

			if((MetadataRecord.getInstance().getParentData().getModifier() == null) ||
					((MetadataRecord.getInstance().getParentData().getModifier().getName() == null)))
				text3.setText(MetadataRecord.getInstance().getParentData().getFacttablecolumn());
			else 
				text3.setText(MetadataRecord.getInstance().getParentData().getModifier().getFacttablecolumn());

			text3.addModifyListener(new ModifyListener() {
			      public void modifyText(ModifyEvent event) {	
				 /*     	// Page is not complete until all required fields have been added
			    	  if ( (text1.getText().length() > 0) && (text2.getText().length() > 0) 
			    			  && (text3.getText().length() > 0)&& (text4.getText().length() > 0))
			    		  setPageComplete(true) ;
			    	  else
			    		  setPageComplete(false);
			    */
			    	  setPageComplete(text3.getText().length()>0);
			      
			      }
			    });
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
			
			
			new Label (settings, SWT.NONE).setText("*Operator:");			
			text4 = new Text(settings, SWT.BORDER);
			text4.setLayoutData(textData);

			if((MetadataRecord.getInstance().getParentData().getModifier() == null) ||
					((MetadataRecord.getInstance().getParentData().getModifier().getName() == null)))
				text4.setText(MetadataRecord.getInstance().getParentData().getOperator());
			else 
				text4.setText(MetadataRecord.getInstance().getParentData().getModifier().getOperator());

			
			text4.addModifyListener(new ModifyListener() {
			      public void modifyText(ModifyEvent event) {	
				      	// Page is not complete until all required fields have been added
			    	  
			    //	  if ( (text1.getText().length() > 0) && (text2.getText().length() > 0) 
			    //			  && (text3.getText().length() > 0)&& (text4.getText().length() > 0))
			    //		  setPageComplete(true) ;
			    //	  else
			    //		  setPageComplete(false);
			    	  
			    	  setPageComplete(text4.getText().length()>0);
			      }
			    });
			text4.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					if(text4.getText().length() > 9)
						e.doit = false;
				}
			});
			
			new Label (settings, SWT.NONE).setText("*Column Data Type:");
			dataTypeCombo = new Combo(settings,SWT.READ_ONLY);
			dataTypeCombo.add("T");
			dataTypeCombo.add("N");

			if((MetadataRecord.getInstance().getParentData().getModifier() == null) ||
					((MetadataRecord.getInstance().getParentData().getModifier().getName() == null)))
				dataTypeCombo.setText(MetadataRecord.getInstance().getParentData().getColumndatatype());
			else 
				dataTypeCombo.setText(MetadataRecord.getInstance().getParentData().getModifier().getColumndatatype());
/*
			new Label (settings, SWT.NONE).setText("*Dimension Code:");
			text6 = new Text(settings, SWT.BORDER);
			text6.setLayoutData(textData);

			if(MetadataRecord.getInstance().getMetadata().getModifier() == null)
				text6.setText(MetadataRecord.getInstance().getMetadata().getDimcode());
			else 
				text6.setText(MetadataRecord.getInstance().getMetadata().getModifier().getDimcode());

			
			text6.addModifyListener(new ModifyListener() {
			      public void modifyText(ModifyEvent event) {	
			    	  setPageComplete(text6.getText().length()>0);
			      }
			    });
			text6.addVerifyListener(new VerifyListener() {			
				public void verifyText(VerifyEvent e){
					if((e.character == '\b') || (e.character == '\u007F')){
						e.doit = true;
						return;
					}
					if(text6.getText().length() > 699)
						e.doit = false;
				}
			});
*/			
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE).setText("* denotes required field.");			
			
			

			
					
			
	//		new Label (settings, SWT.NONE).setText("Tooltip:");			
	//		text5 = new Text(settings, SWT.BORDER);
	//		text5.setLayoutData(textData);
			
			setControl(settings);

		}

		@Override
		public void performHelp(){

			String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
			String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_queryDimension";
			
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);
			
			// to show big help page
			//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		
		
			
		}
			

		public void updateMetadataRecord(){
			
			ConceptType metadata = 	MetadataRecord.getInstance().getMetadata();
			if(metadata.getModifier() == null){

				metadata.setTablename(getTableName());
				metadata.setColumnname(getColumnName());
				metadata.setFacttablecolumn(getFactTableColumnName());
				metadata.setOperator(getOperator());
				metadata.setColumndatatype(getDataType());
			}
			else{
				metadata.getModifier().setTablename(getTableName());
				metadata.getModifier().setColumnname(getColumnName());
				metadata.getModifier().setFacttablecolumn(getFactTableColumnName());
				metadata.getModifier().setOperator(getOperator());
				metadata.getModifier().setColumndatatype(getDataType());
			}
	//		metadata.setKey(MetadataRecord.getInstance().getParentData().getKey()+ MetadataRecord.getInstance().getSymbol() + "\\");
	//		metadata.setDimcode(MetadataRecord.getInstance().getParentData().getDimcode()+ MetadataRecord.getInstance().getSymbol() + "\\");
		}
		
		
		public String getTableName()
		{
			return text1.getText();
		}
		public String getColumnName() {
			return text2.getText();
		}

		public String getFactTableColumnName()
		{
			return text3.getText();
		}
		public String getOperator() {
			return text4.getText();
		}
		
		public String getDataType(){
			return dataTypeCombo.getText();
		}

	}

