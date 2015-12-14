package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
//import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType.KeywordSet;

public class ValueMetadataSettingsPageOne extends WizardPage {

		public static final String PAGE_NAME = "ValueMetadataSettings1"; //$NON-NLS-1$
		private Table table;
		
		public ValueMetadataSettingsPageOne() {
			super(PAGE_NAME); 
			
			setTitle(Messages.getString("Wizard.ValueMetadataSettings1"));
			setDescription("Optional associated item keywords found in text.");
			
			setPageComplete(true);
		}


		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			Composite settings = new Composite(parent, SWT.NONE);

			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 3;
			gridLayout.horizontalSpacing = 1;
			gridLayout.verticalSpacing = 1;
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 0;
			settings.setLayout(gridLayout);

			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			
			new Label (settings, SWT.NONE).setText("List of keywords for this item:");	
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			
			Composite tableComp = new Composite(settings, SWT.NONE);
			GridLayout tableLayout = new GridLayout();
			tableLayout.numColumns = 1;
			tableComp.setLayout(tableLayout);
			GridData data2 = new GridData (GridData.FILL_BOTH);
			data2.horizontalSpan = 2;
			data2.grabExcessHorizontalSpace = true;
			data2.grabExcessVerticalSpace = true;
			tableComp.setLayoutData(data2);
			
			
			table = new Table(tableComp, SWT.BORDER|SWT.FULL_SELECTION);
			table.setLinesVisible(true);
//			table.setHeaderVisible(true);
		
			
			TableColumn tcName = new TableColumn(table, SWT.LEFT);
			tcName.setText("Associated keyword");
			tcName.setWidth(300);
			
			Composite buttonComp = new Composite(settings, SWT.NONE);
			GridLayout buttonLayout = new GridLayout();
			buttonLayout.numColumns = 1;
			buttonComp.setLayout(buttonLayout);
			GridData data3 = new GridData (GridData.FILL_BOTH);
			data3.horizontalSpan = 1;
			buttonComp.setLayoutData(data3);
			
			Button add = new Button(buttonComp, SWT.NONE);
			add.setText("Add");
			
			
			Button delete = new Button(buttonComp, SWT.NONE);
			delete.setText("Delete");	

			delete.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					int index = table.getSelectionIndex();
					if(index > -1){
						table.remove(index);
					}
				}
			});			
			
			add.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
						KeywordDialog dialog = new KeywordDialog(Display.getCurrent().getActiveShell());
						dialog.open();
						TableItem item = new TableItem(table, SWT.NULL);
						String newKeyword = ValueMetadata.getInstance().getKeyword();
						if(newKeyword != null)	
							item.setText(newKeyword);
					
						table.select(table.getItemCount() -1 );
						table.showSelection();
						ValueMetadata.getInstance().setKeyword(null);
					//	table.redraw();
				}
			});
		
			if(ValueMetadata.getInstance().hasValueMetadataType()){
		/*		KeywordSet keywordSet =	ValueMetadata.getInstance().getValueMetadataType().getKeywordSet();
				if(keywordSet != null){
					List <String> keywords = keywordSet.getKeyword();
					if(keywords.isEmpty()){
						return;
					}
					else{
						for(int i =0; i< keywords.size(); i++){
							TableItem item = new TableItem(table, SWT.NULL);
							item.setText((String) keywords.get(i));
						}
					}
				}	
	*/		}	
			setControl(settings);
		}

	/*	@Override
		public void performHelp(){

			String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
			String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_queryDimension";

			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);

			// to show big help page
			//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		


		}

	 */

		public void updateValueMetadata(){

			TableItem[] items = table.getItems();
			if(items.length == 0)
				return;
		/*	KeywordSet set = new KeywordSet();
			for( int i=0; i< items.length; i++)				
				set.getKeyword().add(items[i].getText());
				
				
			ValueMetadata.getInstance().getValueMetadataType().setKeywordSet(set);
		*/	
	/*			StringWriter strWriter = null;
				try {
					strWriter = new StringWriter();
					edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory();
					OntologyJAXBUtil.getJAXBUtil().marshaller(of.createValueMetadata(ValueMetadata.getInstance().getValueMetadataType()), strWriter);
				} catch (JAXBUtilException e) {
				//	log.error("Error marshalling Ont request message");
				//	throw e;
					System.out.println(e.getMessage());
				} 
				System.out.println( strWriter.toString());
		*/		
				
				
				
			}
			
		

}

