package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import java.util.Iterator;

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

public class SynonymsPage extends WizardPage {

		public static final String PAGE_NAME = "SynonymSettings"; //$NON-NLS-1$
		
		
		public SynonymsPage() {
			super(PAGE_NAME); 
			
			setTitle(Messages.getString("Wizard.SynonymSettings"));
			setDescription("These settings are optional.");
			
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
			
			new Label (settings, SWT.NONE).setText("List of synonyms for this item:");	
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
			
			
			final Table table = new Table(tableComp, SWT.BORDER|SWT.FULL_SELECTION);
			table.setLinesVisible(true);
//			table.setHeaderVisible(true);
			
			TableColumn tcName = new TableColumn(table, SWT.LEFT);
			tcName.setText("Synonym name");
			tcName.setWidth(300);
			
			if(!(MetadataRecord.getInstance().getSynonyms().isEmpty())){
				Iterator<String> it = MetadataRecord.getInstance().getSynonyms().iterator();
				while(it.hasNext()){
					TableItem item = new TableItem(table, SWT.NULL);
					item.setText(it.next());
				}
			}
			
			
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
					MetadataRecord.getInstance().removeSynonym(table.getItem(index).getText());
					MetadataRecord.getInstance().setSynonymEditFlag(true);
					if(index > -1){
						table.remove(index);
					}
					
					
				}
			});			
			
			add.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
						SynonymsDialog dialog = new SynonymsDialog(Display.getCurrent().getActiveShell());
						dialog.open();
						TableItem item = new TableItem(table, SWT.NULL);
						String newSynonym = MetadataRecord.getInstance().getSynonym();
						if(newSynonym != null)	
							item.setText(newSynonym);
					
						MetadataRecord.getInstance().addSynonym(newSynonym);
						MetadataRecord.getInstance().setSynonymEditFlag(true);
						table.select(table.getItemCount() -1 );
						table.showSelection();
						MetadataRecord.getInstance().setSynonym(null);
					//	table.redraw();
				}
			});
		
			setControl(settings);
		}

	@Override
		public void performHelp(){

			String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
			String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_synonyms";
			
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);
			
			// to show big help page
			//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		
		
			
		}
			
		
	}

