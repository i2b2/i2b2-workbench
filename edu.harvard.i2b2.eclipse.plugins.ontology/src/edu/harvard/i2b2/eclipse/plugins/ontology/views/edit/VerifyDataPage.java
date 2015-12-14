package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.harvard.i2b2.common.util.jaxb.DTOFactory;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.Messages;
import edu.harvard.i2b2.eclipse.plugins.ontology.util.OntologyJAXBUtil;
import edu.harvard.i2b2.ontclient.datavo.vdo.XmlValueType;

public class VerifyDataPage extends WizardPage {
		public static final String PAGE_NAME = "VerifyInfo"; //$NON-NLS-1$
		
		private Label verifyData;
		private Label verifyData2;
		
		public VerifyDataPage() {
			super(PAGE_NAME);
			setTitle(Messages.getString("Wizard.VerificationPage")); //$NON-NLS-1$
			setDescription("Verify that the data is correct and click Finish");
			setPageComplete(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			
			final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL
					  | SWT.V_SCROLL);

			
			Composite composite = new Composite(sc, SWT.NULL);
			final GridLayout gridLayout_1 = new GridLayout(1, false);
			gridLayout_1.marginWidth = 15;
			gridLayout_1.marginTop = 5;
			gridLayout_1.marginRight = 5;
			gridLayout_1.marginLeft = 5;
			gridLayout_1.marginBottom = 5;
//			gridLayout_1.numColumns = 2;
			composite.setLayout(gridLayout_1);
			
			
			// Set top as the scrolled content of the ScrolledComposite
			sc.setContent(composite);

			// Expand both horizontally and vertically
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			
			// Set the minimum size
			sc.setMinSize(1200, 700);

			Group parametersGroup = new Group(composite, SWT.NONE);
			parametersGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			parametersGroup.setText(Messages.getString("Wizard.DataPreview")); //$NON-NLS-1$
			final GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.marginWidth = 15;
			gridLayout.horizontalSpacing = 15;
			gridLayout.marginTop = 5;
			gridLayout.marginRight = 5;
			gridLayout.marginLeft = 5;
			gridLayout.marginBottom = 5;
			parametersGroup.setLayout(gridLayout);
			
			GridData gridData =
	            new GridData(
	              GridData.FILL_HORIZONTAL);
	          gridData.horizontalSpan = 1;
	          gridData.heightHint = 700;
	          gridData.widthHint = 150;
	          gridData.grabExcessVerticalSpace = true;

			verifyData = new Label(parametersGroup, SWT.NONE);
			verifyData.setLayoutData(gridData);
			
			GridData gridData2 =
	            new GridData(
	              GridData.FILL_HORIZONTAL);
	          gridData2.horizontalSpan = 1;
	          gridData2.heightHint = 700;
	          gridData2.widthHint = 1000;
	          gridData2.grabExcessVerticalSpace = true;

		

			verifyData2 = new Label(parametersGroup, SWT.NONE);
			verifyData2.setLayoutData(gridData2);
			setControl(composite);
			
			
			if((System.getProperty("OntEdit_ViewOnly") != null) && (System.getProperty("OntEdit_ViewOnly").equals("true")))
				updateParameters();
			
		
			

		}
		@Override
		public void performHelp(){

			String PREFIX = "edu.harvard.i2b2.eclipse.plugins.ontology";
			String EDIT_VIEW_CONTEXT_ID = PREFIX + ".edit_terms_view_help_wizard_verifyData";
			
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelp(EDIT_VIEW_CONTEXT_ID);
			
			// to show big help page
			//	helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.ontology/html/i2b2_edit_terms_index.htm");		
		
			
		}

		public boolean updateParameters(){
			StringBuffer sb = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
	
			boolean complete = true;
			
			if(!((MetadataRecord.getInstance().getMetadata().getName() == null) || (MetadataRecord.getInstance().getMetadata().getName().length() == 0))){
				sb.append("*Name");
			    sb.append("\n");
			    sb2.append(MetadataRecord.getInstance().getMetadata().getName());
			    sb2.append("\n");
			}
			else{
				sb.append("*Name");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}

			if(!((MetadataRecord.getInstance().getMetadata().getBasecode() == null) || (MetadataRecord.getInstance().getMetadata().getBasecode().length() == 0))){
				sb.append("Base Code");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getBasecode());
				sb2.append("\n");
			}
			if(!((MetadataRecord.getInstance().getMetadata().getSourcesystemCd() == null) || (MetadataRecord.getInstance().getMetadata().getSourcesystemCd().length() == 0))){		
				sb.append("Sourcesystem Code");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getSourcesystemCd());
				sb2.append("\n");
			}

			if(!((MetadataRecord.getInstance().getMetadata().getComment() == null) || (MetadataRecord.getInstance().getMetadata().getComment().length() == 0))){
				sb.append("Comment");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getComment());
				sb2.append("\n");
			}
		
			if(!((MetadataRecord.getInstance().getMetadata().getTablename() == null) || (MetadataRecord.getInstance().getMetadata().getTablename().length() == 0))){
				sb.append("*Table Name");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getTablename());
				sb2.append("\n");
			}
			else{
				sb.append("*Table Name");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}

			if(!((MetadataRecord.getInstance().getMetadata().getColumnname() == null) || (MetadataRecord.getInstance().getMetadata().getColumnname().length() == 0))){
				sb.append("*Column Name");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getColumnname());
				sb2.append("\n");
			}
			else{
				sb.append("*Column Name");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
			

			if(!((MetadataRecord.getInstance().getMetadata().getFacttablecolumn()== null) || (MetadataRecord.getInstance().getMetadata().getFacttablecolumn().length() == 0))){
				sb.append("*Fact Table Column Name");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getFacttablecolumn());
				sb2.append("\n");
			}
			else{
				sb.append("*Fact Table Column Name");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
				
			if(!((MetadataRecord.getInstance().getMetadata().getOperator() == null) || (MetadataRecord.getInstance().getMetadata().getOperator().length() == 0))){
				sb.append("*Operator");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getOperator());
				sb2.append("\n");
			}
			else{
				sb.append("*Operator");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
						
			if(!((MetadataRecord.getInstance().getMetadata().getColumndatatype() == null) || (MetadataRecord.getInstance().getMetadata().getColumndatatype().length() == 0))){
				sb.append("*Column Data Type");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getColumndatatype());
				sb2.append("\n");
			}
			else{
				sb.append("*Column Data Type");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}

			if(!((MetadataRecord.getInstance().getMetadata().getVisualattributes() == null) || (MetadataRecord.getInstance().getMetadata().getVisualattributes().length() == 0))){
				sb.append("*Visual Attributes");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getVisualattributes());
				sb2.append("\n");
			}
			else{
				sb.append("*Visual Attributes");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
			
			if(!((MetadataRecord.getInstance().getMetadata().getKey() == null) || (MetadataRecord.getInstance().getMetadata().getKey().length() == 0))){
				sb.append("*Key");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getKey());
				sb2.append("\n");
				sb.append("Hierarchy level");
				sb.append("\n");
				sb2.append(Integer.toString(MetadataRecord.getInstance().getMetadata().getLevel()));
				sb2.append("\n");
			}
			else{
				sb.append("*Key");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
			
			if(!((MetadataRecord.getInstance().getMetadata().getDimcode() == null) || (MetadataRecord.getInstance().getMetadata().getDimcode().length() == 0))){
				sb.append("Dimension Code");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getDimcode());
				sb2.append("\n");
			}
			
			if(!((MetadataRecord.getInstance().getMetadata().getTooltip() == null) || (MetadataRecord.getInstance().getMetadata().getTooltip().length() == 0))){
				sb.append("Tooltip");
				sb.append("\n\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getTooltip());
				sb2.append("\n\n");
			}

			if (ValueMetadata.getInstance().hasValueMetadataType()){
				ValueMetadata.getInstance().getValueMetadataType().setVersion(Messages.getString("Wizard.ValueMetadataVersion"));
				
				Date currentDate = new Date();
				DTOFactory factory = new DTOFactory();
				ValueMetadata.getInstance().getValueMetadataType().setCreationDateTime(factory.getXMLGregorianCalendar(currentDate.getTime()));
				
				StringWriter strWriter = null;
				try {
					strWriter = new StringWriter();
					edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory();
					OntologyJAXBUtil.getJAXBUtil().marshaller(of.createValueMetadata(ValueMetadata.getInstance().getValueMetadataType()), strWriter);
				} catch (JAXBUtilException e) {
					System.out.println(e.getMessage());
					
				} 

				setXmlValue(strWriter.toString().replace("<Val>", "<Val description=\"\">"));
		
				sb.append("Metadata XML");
				sb.append("\n");
				
				sb2.append(strWriter.toString().replace("<Val>", "<Val description=\"\">"));
				sb2.append("\n");
			}
			else
				MetadataRecord.getInstance().getMetadata().setMetadataxml(null);
			
			verifyData.setText(sb.toString());
	//		verifyData.pack();


			verifyData2.setText(sb2.toString());
	//		verifyData2.pack();
			if((System.getProperty("OntEdit_ViewOnly") != null) && (System.getProperty("OntEdit_ViewOnly").equals("true")))
				complete = true;
			return complete;

		}

		private void setXmlValue(String c_xml){
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(c_xml);
			Element rootElement = null;
			try {
				org.jdom.Document metadataDoc = parser.build(xmlStringReader);
				//clear out the jaxb namespace...
				metadataDoc.getRootElement().setNamespace(null);
				
				org.jdom.output.DOMOutputter out = new DOMOutputter(); 
				Document doc = out.output(metadataDoc);
				rootElement = doc.getDocumentElement();
			
			} catch (JDOMException e) {
				System.out.println(e.getMessage());
			//	log.error(e.getMessage());
			//	child.setMetadataxml(null);
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
			//	log.error(e1.getMessage());
			//	child.setMetadataxml(null);
			}
			if (rootElement != null) {
				
				XmlValueType xml = new XmlValueType();
				xml.getAny().add(rootElement);
				MetadataRecord.getInstance().getMetadata().setMetadataxml(xml);
			}
		}
}
