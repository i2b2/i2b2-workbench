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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType;
import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType.UnitValues;
import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType.UnitValues.ConvertingUnits;

public class ValueMetadataSettingsPageFive extends WizardPage {

		private Table table, table2, table3, table4;
		private Text text1;
		public static final String PAGE_NAME = "ValueMetadataSettings5"; //$NON-NLS-1$
		
		
		public ValueMetadataSettingsPageFive() {
			super(PAGE_NAME); 
			
			setTitle(Messages.getString("Wizard.ValueMetadataSettings5"));
			setDescription("Optional unit of measurement settings for a lab associated item");
			
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
			
			GridData data = new GridData (GridData.FILL_BOTH);
			data.horizontalSpan = 1;
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = true;
			settings.setLayoutData(data);
			
			
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
	//new Label (settings, SWT.NONE);
			
			new Label (settings, SWT.NONE).setText("Normal measurement unit for this item:");				
			text1 = new Text(settings, SWT.BORDER|SWT.FULL_SELECTION);
			
			GridData textData = new GridData ();	
			textData.widthHint = 250;
			textData.grabExcessHorizontalSpace = true;
			textData.horizontalAlignment = SWT.FILL;
			
			text1.setLayoutData(textData);
			
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
	
			new Label (settings, SWT.NONE).setText("List of equal units for this item:");	
			new Label (settings, SWT.NONE);
	
			GridLayout tableLayout = new GridLayout();
			tableLayout.numColumns = 3;

			GridData data2 = new GridData (GridData.FILL_BOTH);
			data2.horizontalSpan = 2;
			data2.grabExcessHorizontalSpace = true;
			data2.grabExcessVerticalSpace = true;
	
			
			GridLayout buttonLayout = new GridLayout();
			buttonLayout.numColumns = 1;
			GridData data3 = new GridData (GridData.FILL_BOTH);
			data3.horizontalSpan = 1;
	

			
			Composite tableComp2 = new Composite(settings, SWT.NONE);
			tableComp2.setLayout(tableLayout);
			tableComp2.setLayoutData(data2);
			
			
			table2 = new Table(tableComp2, SWT.BORDER|SWT.FULL_SELECTION);
			table2.setLinesVisible(true);
//			table2.setHeaderVisible(true);
			
			TableColumn tcName2 = new TableColumn(table2, SWT.LEFT);
			tcName2.setText("Associated Equal Unit Value");
			tcName2.setWidth(300);
			
			Composite buttonComp2 = new Composite(tableComp2, SWT.NONE);
		
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
						EqualUnitsDialog dialog = new EqualUnitsDialog(Display.getCurrent().getActiveShell());
						dialog.open();
						String newUnit = ValueMetadata.getInstance().getEqualUnit();
						if(newUnit != null)	{
							TableItem item = new TableItem(table2, SWT.NULL);
							item.setText(newUnit);
							table2.select(table2.getItemCount() -1 );
							table2.showSelection();
						}
						ValueMetadata.getInstance().setEqualUnit(null);
					//	table.redraw();
				}
			});
		
		
			
			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			
			new Label (settings, SWT.NONE).setText("List of conversion units/multiplication factors for this item:");	
			new Label (settings, SWT.NONE);
	//		new Label (settings, SWT.NONE);
			
			
			
			Composite tableComp4 = new Composite(settings, SWT.NONE);
			tableComp4.setLayout(tableLayout);
			tableComp4.setLayoutData(data2);
			
			
			table4 = new Table(tableComp4, SWT.BORDER|SWT.FULL_SELECTION);
			table4.setLinesVisible(true);
//			table2.setHeaderVisible(true);
			
			TableColumn tcName4 = new TableColumn(table4, SWT.LEFT);
			tcName4.setText("Associated Conversion Unit Value");
			tcName4.setWidth(150);
			
			TableColumn tcName5 = new TableColumn(table4, SWT.LEFT);
			tcName5.setText("Associated Multiplication Factor");
			tcName5.setWidth(150);
			
			
			Composite buttonComp4 =  new Composite(tableComp4,SWT.NONE);
		
			buttonComp4.setLayout(buttonLayout);
			buttonComp4.setLayoutData(data3);
			
			Button add4 = new Button(buttonComp4, SWT.NONE);
			add4.setText("Add");
			
			
			Button delete4 = new Button(buttonComp4, SWT.NONE);
			delete4.setText("Delete");	

			delete4.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					int index = table4.getSelectionIndex();
					if(index > -1){
						table4.remove(index);
					}
				}
			});			
			
			add4.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
						ConvertingUnitsDialog dialog = new ConvertingUnitsDialog(Display.getCurrent().getActiveShell());
						dialog.open();
						String newUnit = ValueMetadata.getInstance().getConvertingUnit();
						Float multFactor = ValueMetadata.getInstance().getMultFactor();
						if(newUnit != null)	{
							TableItem item = new TableItem(table4, SWT.NULL);
							item.setText(new String[] {newUnit, Float.toString(multFactor)});
							table4.select(table4.getItemCount() -1 );
							table4.showSelection();
						}
						ValueMetadata.getInstance().setConvertingUnit(null);
						ValueMetadata.getInstance().setMultFactor(1);
					//	table.redraw();
				}
			});

			new Label (settings, SWT.NONE);
			new Label (settings, SWT.NONE);
			
			new Label (settings, SWT.NONE).setText("List of excluding unit values for this item:");	
			new Label (settings, SWT.NONE);			
			
			Composite tableComp3 = new Composite(settings, SWT.NONE);
			tableComp3.setLayout(tableLayout);
			tableComp3.setLayoutData(data2);
			
			
			table3 = new Table(tableComp3, SWT.BORDER|SWT.FULL_SELECTION);
			table3.setLinesVisible(true);
//			table2.setHeaderVisible(true);
			
			TableColumn tcName3 = new TableColumn(table3, SWT.LEFT);
			tcName3.setText("Associated Excluding Unit Value");
			tcName3.setWidth(300);
			
			Composite buttonComp3 = new Composite(tableComp3, SWT.NONE);
		
			buttonComp3.setLayout(buttonLayout);
			buttonComp3.setLayoutData(data3);
			
			Button add3 = new Button(buttonComp3, SWT.NONE);
			add3.setText("Add");
			
			
			Button delete3 = new Button(buttonComp3, SWT.NONE);
			delete3.setText("Delete");	

			delete3.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					int index = table3.getSelectionIndex();
					if(index > -1){
						table3.remove(index);
					}
				}
			});			
			
			add3.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
						ExclusionUnitsDialog dialog = new ExclusionUnitsDialog(Display.getCurrent().getActiveShell());
						dialog.open();
						String newUnit = ValueMetadata.getInstance().getExcludingUnit();
						if(newUnit != null)	{
							TableItem item = new TableItem(table3, SWT.NULL);
							item.setText(newUnit);
							table3.select(table3.getItemCount() -1 );
							table3.showSelection();
						}
						ValueMetadata.getInstance().setExcludingUnit(null);
					//	table.redraw();
				}
			});
			
			
			if(ValueMetadata.getInstance().hasValueMetadataType()){
				ValueMetadataType vmType = ValueMetadata.getInstance().getValueMetadataType() ;

				UnitValues values =	vmType.getUnitValues();
				if(values != null){
					String normal = values.getNormalUnits();
					if (!((normal == null) || (normal.isEmpty()))){
						text1.setText(normal);
					}
					
					List <String> equal = values.getEqualUnits();
					if(!(equal.isEmpty())){
						
						for(int i =0; i< equal.size(); i++){
							TableItem item = new TableItem(table2, SWT.NULL);
							item.setText((String) equal.get(i));
						}
					}
					
					List<ConvertingUnits> conv = values.getConvertingUnits();
					if(!(conv.isEmpty())){
						for(int i =0; i< conv.size(); i++){
							TableItem item = new TableItem(table4, SWT.NULL);
							item.setText(new String[] {conv.get(i).getUnits(),
											Float.toString(conv.get(i).getMultiplyingFactor())});
							
						}
					}
					
					List<String> excl = values.getExcludingUnits();
					if(!(excl.isEmpty())){
						for(int i =0; i< excl.size(); i++){
							TableItem item = new TableItem(table3, SWT.NULL);
							item.setText((String) excl.get(i));
						}
					}
				}
			}	
			
			setControl(settings);
		}

		@Override
		public void performHelp(){

			String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
			String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_unitValue";
			
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);
			
			// to show big help page
			//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		
		
			
		}
			

		public void updateValueMetadata(){
			
			boolean hasValues = false;
			UnitValues values = new UnitValues();
			if(!((text1.getText() == null)||(text1.getText().isEmpty()))){	
				values.setNormalUnits(text1.getText());
				hasValues = true;
			}
		
			TableItem[] items2 = table2.getItems();
			if(items2.length != 0){
				for( int i=0; i< items2.length; i++)				
					values.getEqualUnits().add(items2[i].getText());	
				hasValues = true;
			}
			
			TableItem[] items3 = table3.getItems();
			if(items3.length != 0){
				for( int i=0; i< items3.length; i++)				
					values.getExcludingUnits().add(items3[i].getText());
				hasValues = true;
			}
			
			TableItem[] items4 = table4.getItems();
			if(items4.length != 0){
				for( int i=0; i< items4.length; i++){		
					ConvertingUnits convUnit = new ConvertingUnits();
					convUnit.setUnits(items4[i].getText(0));
					convUnit.setMultiplyingFactor(Float.parseFloat(items4[i].getText(1)));

					values.getConvertingUnits().add(convUnit);
				}
				hasValues = true;
			}


			if(hasValues)
				ValueMetadata.getInstance().getValueMetadataType().setUnitValues(values);
		}
		
	}

