package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.ontology.ws.OntologyResponseMessage;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.*;


public class FormComposite {
	
	private ScrolledForm form; 
	private Text text1, text2, text3, text4;
	private Text text21, text22, text23, text24;
	private Text text31, text32, text33, text34;
	private Log log = LogFactory.getLog(FormComposite.class.getName());	
	
	
	private Combo dataTypeCombo, typeCombo, synonymCombo, valTypeCombo; 
	
	public FormComposite(Composite parent){
		populateControl(parent);
	}
	
	protected void populateControl(Composite parent) {
		FormToolkit kit = new FormToolkit(parent.getDisplay());
		form = kit.createScrolledForm(parent);

		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 1;
	
		form.getBody().setLayout(layout);

		ExpandableComposite expComp = kit.createExpandableComposite(form.getBody(), ExpandableComposite.TWISTIE);
		expComp.setText("Ontology Settings");
		expComp.setExpanded(true);

		Label label = kit.createLabel(expComp,"Use this form to create a new Ontology term. \n Items with asterisks must be filled in");
		expComp.setClient(label);

		expComp.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});

		kit.createSeparator(form.getBody(), SWT.HORIZONTAL);

		ExpandableComposite expComp2 = kit.createExpandableComposite(form.getBody(), ExpandableComposite.TWISTIE);
		expComp2.setText("Item Settings");
		expComp2.setExpanded(false);

		Composite itemSettings = new Composite(expComp2, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		itemSettings.setLayout(gridLayout);

		itemSettings.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		kit.createLabel(itemSettings,"*Item Name:");
		text1 = kit.createText(itemSettings, "", SWT.BORDER);
		text1.setEnabled(false);
		/*text1.addSelectionListener(new SelectionListener(){
	public void widgetDefaultSelected(SelectionEvent e) {
				MetadataRecord.getInstance().getMetadata().setName(text1.getText());
				updateName(text1.getText());
			}

			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
*/
		GridData textData = new GridData ();	
		textData.widthHint = 100;
		textData.grabExcessHorizontalSpace = true;
		
		text1.setLayoutData(textData);

		kit.createLabel(itemSettings,"*Item Type:");
		typeCombo = new Combo(itemSettings,SWT.READ_ONLY);
		typeCombo.setEnabled(false);
		typeCombo.add("Container");
		typeCombo.add("Folder");
		typeCombo.add("Item");

		kit.createLabel(itemSettings,"*Concept code:");
		text2 = kit.createText(itemSettings, "", SWT.BORDER);
		text2.setLayoutData(textData);
		
		kit.createLabel(itemSettings,"*Synonym code:");
		synonymCombo = new Combo(itemSettings,SWT.READ_ONLY);
		synonymCombo.add("N");
		synonymCombo.add("Y");
		synonymCombo.setText("N");
		
		kit.createLabel(itemSettings,"Value type code:");
		valTypeCombo = new Combo(itemSettings,SWT.READ_ONLY);
		valTypeCombo.add("");
		valTypeCombo.add("DOC");
		valTypeCombo.setText("");
		
		kit.createLabel(itemSettings,"Sourcesystem code:");
		text3 = kit.createText(itemSettings, "", SWT.BORDER);
		text3.setLayoutData(textData);
		
		kit.createLabel(itemSettings,"Comment:");
		text4 = kit.createText(itemSettings, "", SWT.BORDER);
		text4.setLayoutData(textData);
		
		expComp2.setClient(itemSettings);
		expComp2.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});

		kit.createSeparator(form.getBody(), SWT.HORIZONTAL);

		ExpandableComposite expComp3 = kit.createExpandableComposite(form.getBody(), ExpandableComposite.TWISTIE);
		expComp3.setText("Query Dimension Settings");
		expComp3.setExpanded(false);
		
		
		Composite querySettings = new Composite(expComp3, SWT.NONE);	
		querySettings.setLayout(gridLayout);
		querySettings.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		kit.createLabel(querySettings,"*Table Name:");
		text21 = kit.createText(querySettings, "", SWT.BORDER);
		text21.setLayoutData(textData);
		
		kit.createLabel(querySettings,"*Column Name:");
		text22 = kit.createText(querySettings, "", SWT.BORDER);
		text22.setLayoutData(textData);
		
		kit.createLabel(querySettings,"*Fact Table Column Name:");
		text23 = kit.createText(querySettings, "", SWT.BORDER);
		text23.setLayoutData(textData);
		
		kit.createLabel(querySettings,"*Operator:");
		text24 = kit.createText(querySettings, "", SWT.BORDER);
		text24.setLayoutData(textData);
		
		kit.createLabel(querySettings,"*Column Data Type:");
		dataTypeCombo = new Combo(querySettings,SWT.READ_ONLY);
		dataTypeCombo.add("T");
		dataTypeCombo.add("N");
		
		expComp3.setClient(querySettings);
		expComp3.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		
		
		kit.createSeparator(form.getBody(), SWT.HORIZONTAL);

		ExpandableComposite expComp4 = kit.createExpandableComposite(form.getBody(), ExpandableComposite.TWISTIE);
		expComp4.setText("Derived Settings");
		expComp4.setExpanded(false);
		
		
		Composite derivedSettings = new Composite(expComp4, SWT.NONE);	
		derivedSettings.setLayout(gridLayout);
		derivedSettings.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		kit.createLabel(derivedSettings,"Hierarchy level:");
		text31 = kit.createText(derivedSettings, "", SWT.BORDER);
		text31.setEnabled(false);
		
		kit.createLabel(derivedSettings,"Item Key:");
		text32 = kit.createText(derivedSettings, "", SWT.BORDER);
		text32.setLayoutData(textData);
		text32.setEnabled(false);
		
		kit.createLabel(derivedSettings,"Item Dimension Code:");
		text34 = kit.createText(derivedSettings, "", SWT.BORDER);	
		text34.setLayoutData(textData);
		text34.setEnabled(false);
		
		kit.createLabel(derivedSettings,"Item Tooltip:");
		text33 = kit.createText(derivedSettings, "", SWT.BORDER);
		text33.setLayoutData(textData);
		text33.setEnabled(false);
		
		expComp4.setClient(derivedSettings);
		expComp4.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		kit.createSeparator(form.getBody(), SWT.HORIZONTAL);
		
		Composite buttonComp = kit.createComposite(form.getBody());
		GridLayout buttonLayout = new GridLayout();

		buttonLayout.numColumns = 3;
//		buttonLayout.makeColumnsEqualWidth = true;
		buttonComp.setLayout(buttonLayout);
		
		Button clear =kit.createButton(buttonComp, "  Clear All  ", SWT.PUSH);
		GridData buttonData = new GridData ();	
		buttonData.horizontalSpan = 1;
		buttonData.widthHint = 100;
		buttonData.grabExcessHorizontalSpace = true;
//		buttonData.horizontalAlignment = SWT.END;
		clear.setLayoutData(buttonData);

		clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clear();
			}
		});

		
		
		Button ok =kit.createButton(buttonComp, " Submit ", SWT.PUSH);
		buttonData.horizontalAlignment = SWT.END;
		ok.setLayoutData(buttonData);
		
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateMetadataRecord();
				if(!(validateInput().equals("")))
					return;
				
				addChild().start();
			}
		});
		
	}
	
	public void update(){
		
		if(MetadataRecord.getInstance().getMetadata() != null){
			text21.setText(MetadataRecord.getInstance().getMetadata().getTablename());
			text22.setText(MetadataRecord.getInstance().getMetadata().getColumnname());
			text23.setText(MetadataRecord.getInstance().getMetadata().getFacttablecolumn());
			text24.setText(MetadataRecord.getInstance().getMetadata().getOperator());
			
			text31.setText(Integer.toString(MetadataRecord.getInstance().getMetadata().getLevel()));
			text32.setText(MetadataRecord.getInstance().getMetadata().getKey());
			text33.setText(MetadataRecord.getInstance().getMetadata().getTooltip());
			text34.setText(MetadataRecord.getInstance().getMetadata().getDimcode());
				
			dataTypeCombo.setText(MetadataRecord.getInstance().getMetadata().getColumndatatype());
			typeCombo.setText(MetadataRecord.getInstance().getType());
			
		}
	}
	
	public void updateName(String name){	
		if(MetadataRecord.getInstance().getMetadata() != null){
			text1.setText(MetadataRecord.getInstance().getMetadata().getName());
			text32.setText(MetadataRecord.getInstance().getMetadata().getKey()+ name + "\\");
			text33.setText(MetadataRecord.getInstance().getMetadata().getTooltip()+ "\\" + name);
			text34.setText(MetadataRecord.getInstance().getMetadata().getDimcode()+ name + "\\");
		}
	}
	
	private void updateMetadataRecord(){	
	
		MetadataRecord.getInstance().getMetadata().setName(text1.getText());
		MetadataRecord.getInstance().getMetadata().setKey(text32.getText());
		MetadataRecord.getInstance().getMetadata().setTooltip(text33.getText());
		MetadataRecord.getInstance().getMetadata().setDimcode(text34.getText());
	
		MetadataRecord.getInstance().getMetadata().setBasecode(text2.getText());
		MetadataRecord.getInstance().getMetadata().setSourcesystemCd(text3.getText());
		MetadataRecord.getInstance().getMetadata().setComment(text4.getText());
		
		MetadataRecord.getInstance().getMetadata().setTablename(text21.getText());
		MetadataRecord.getInstance().getMetadata().setColumnname(text22.getText());
		MetadataRecord.getInstance().getMetadata().setFacttablecolumn(text23.getText());
		MetadataRecord.getInstance().getMetadata().setOperator(text24.getText());

		MetadataRecord.getInstance().getMetadata().setLevel(Integer.parseInt(text31.getText()));
		MetadataRecord.getInstance().getMetadata().setSynonymCd(synonymCombo.getText());
		MetadataRecord.getInstance().getMetadata().setValuetypeCd(valTypeCombo.getText());
	}
	
	private String validateInput(){
		String valid = MetadataRecord.getInstance().validate();
		if( !(valid.equals(""))){
			MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), 
					SWT.ICON_ERROR);
			mBox.setText("Please Note ...");
			mBox.setMessage("Entry not accepted for the following reasons: \n " + valid);
			mBox.open();
			
		}
		return valid;
		
	}
	
	
	public void clear(){
		MetadataRecord.getInstance().clear();
		typeCombo.setText("");
		dataTypeCombo.setText("");
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
	//			MetadataRecord.getInstance().getBrowser().update();
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
