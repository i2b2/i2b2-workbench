package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import java.util.List;

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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType.EnumValues;

public class ValueMetadataSettingsPageThree extends WizardPage {

		
	private Table table, table2;
	public static final String PAGE_NAME = "ValueMetadataSettings4"; //$NON-NLS-1$
		
		
		public ValueMetadataSettingsPageThree() {
			super(PAGE_NAME); 
			
			setTitle(Messages.getString("Wizard.ValueMetadataSettings4"));
			setDescription("Optional enumerated value settings for a lab associated item");
			
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
			
			new Label (settings, SWT.NONE).setText("List of enumerated values for this item:");	
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
			tcName.setText("Associated Enumerated Value");
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
						EnumeratedDialog dialog = new EnumeratedDialog(Display.getCurrent().getActiveShell());
						dialog.open();
						TableItem item = new TableItem(table, SWT.NULL);
						
						String newEnum = ValueMetadata.getInstance().getVal();
						if(newEnum != null)	
							item.setText(newEnum);
					
						table.select(table.getItemCount() -1 );
						table.showSelection();
						ValueMetadata.getInstance().setVal(null);
					//	table.redraw();
				}
			});
		
	/*		new Label (settings, SWT.NONE).setText("List of excluding enumerated values for this item:");	
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			
			Composite tableComp2 = new Composite(settings, SWT.NONE);
			tableComp2.setLayout(tableLayout);
			tableComp2.setLayoutData(data2);
			
			
			table2 = new Table(tableComp2, SWT.BORDER|SWT.FULL_SELECTION);
			table2.setLinesVisible(true);
//			table2.setHeaderVisible(true);
			
			TableColumn tcName2 = new TableColumn(table2, SWT.LEFT);
			tcName2.setText("Associated Excluding Enumerated Value");
			tcName2.setWidth(300);
			
			Composite buttonComp2 = new Composite(settings, SWT.NONE);
		
			buttonComp2.setLayout(buttonLayout);
			buttonComp2.setLayoutData(data3);
			
			Button add2 = new Button(buttonComp2, SWT.NONE);
			add2.setText("Add");
			
			
			Button delete2 = new Button(buttonComp2, SWT.NONE);
			delete2.setText("Delete");	

			delete2.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					int index = table2.getSelectionIndex();
					if(index > -1){
						table2.remove(index);
					}
				}
			});			
			
			add2.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
						ExclusionEnumeratedDialog dialog = new ExclusionEnumeratedDialog(Display.getCurrent().getActiveShell());
						dialog.open();
						TableItem item = new TableItem(table2, SWT.NULL);
						String newEnum = ValueMetadata.getInstance().getExcludingVal();
						if(newEnum != null)	
							item.setText(newEnum);
					
						table2.select(table2.getItemCount() -1 );
						table2.showSelection();
						ValueMetadata.getInstance().setExcludingVal(null);
					//	table.redraw();
				}
			});
		
			*/

			if(ValueMetadata.getInstance().hasValueMetadataType()){
				ValueMetadataType vmType = ValueMetadata.getInstance().getValueMetadataType() ;

				EnumValues values =	ValueMetadata.getInstance().getValueMetadataType().getEnumValues();
				if(values != null){
					List <String> val = values.getVal();
					if(val.isEmpty()){
						return;
					}
					else{
						for(int i =0; i< val.size(); i++){
							TableItem item = new TableItem(table, SWT.NULL);
							item.setText((String) val.get(i));
						}
					}
				}
			}	
			
			setControl(settings);
		}

		@Override
		public void performHelp(){

			String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
			String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_enumValues";
			
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);
			
			// to show big help page
			//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		
		
			
		}
			

		public void updateValueMetadata(){
			
			boolean hasValues = false;
			EnumValues values = new EnumValues();
			
			TableItem[] items = table.getItems();
			if(items.length != 0){
				for( int i=0; i< items.length; i++)				
					values.getVal().add(items[i].getText());
				hasValues = true;
			}
			
	/*		TableItem[] items2 = table2.getItems();
			if(items2.length != 0){
				for( int i=0; i< items2.length; i++)				
					values.getExcludingVal().add(items2[i].getText());	
				hasValues = true;
			}*/
			ValueMetadata value = ValueMetadata.getInstance();
			if(hasValues)
				ValueMetadata.getInstance().getValueMetadataType().setEnumValues(values);
				
		}
		
	}

