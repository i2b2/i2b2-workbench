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

public class VerifyModifierDataPage extends WizardPage {
		public static final String PAGE_NAME = "VerifyInfo"; //$NON-NLS-1$
		
		private Label verifyData;
		private Label verifyData2;
		
		public VerifyModifierDataPage() {
			super(PAGE_NAME);
			setTitle(Messages.getString("Wizard.ModifierVerificationPage")); //$NON-NLS-1$
			setDescription("Verify that the modifier data is correct and click Finish");
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

			// Set the minimum size
			sc.setMinSize(600, 700);

			// Expand both horizontally and vertically
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);

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
			

			verifyData = new Label(parametersGroup, SWT.NONE);
			verifyData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

			verifyData2 = new Label(parametersGroup, SWT.NONE);
			verifyData2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
			
			setControl(composite);
			

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
			
			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getName() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getName().length() == 0))){
				sb.append("*Name");
			    sb.append("\n");
			    sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getName());
			    sb2.append("\n");
			}
			else{
				sb.append("*Name");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
			
			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getAppliedPath() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getAppliedPath().length() == 0))){
				sb.append("*Applied Path");
			    sb.append("\n");
			    sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getAppliedPath());
			    sb2.append("\n");
			}
			else{
				sb.append("*Applied Path");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}

			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getBasecode() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getBasecode().length() == 0))){
				sb.append("Base Code");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getBasecode());
				sb2.append("\n");
			}
			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getSynonymCd() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getSynonymCd().length() == 0))){		
				sb.append("Synonym Code");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getSynonymCd());
				sb2.append("\n");
			}
			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getSourcesystemCd() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getSourcesystemCd().length() == 0))){		
				sb.append("Sourcesystem Code");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getSourcesystemCd());
				sb2.append("\n");
			}

			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getComment() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getComment().length() == 0))){
				sb.append("Comment");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getComment());
				sb2.append("\n");
			}
		
			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getTablename() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getTablename().length() == 0))){
				sb.append("*Table Name");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getTablename());
				sb2.append("\n");
			}
			else{
				sb.append("*Table Name");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}

			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getColumnname() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getColumnname().length() == 0))){
				sb.append("*Column Name");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getColumnname());
				sb2.append("\n");
			}
			else{
				sb.append("*Column Name");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
			

			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getFacttablecolumn()== null) || (MetadataRecord.getInstance().getMetadata().getModifier().getFacttablecolumn().length() == 0))){
				sb.append("*Fact Table Column Name");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getFacttablecolumn());
				sb2.append("\n");
			}
			else{
				sb.append("*Fact Table Column Name");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
				
			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getOperator() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getOperator().length() == 0))){
				sb.append("*Operator");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getOperator());
				sb2.append("\n");
			}
			else{
				sb.append("*Operator");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
						
			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getColumndatatype() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getColumndatatype().length() == 0))){
				sb.append("*Column Data Type");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getColumndatatype());
				sb2.append("\n");
			}
			else{
				sb.append("*Column Data Type");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}

			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getVisualattributes() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getVisualattributes().length() == 0))){
				sb.append("*Visual Attributes");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getVisualattributes());
				sb2.append("\n");
			}
			else{
				sb.append("*Visual Attributes");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
			
			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getKey() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getKey().length() == 0))){
				sb.append("*Key");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getKey());
				sb2.append("\n");
				sb.append("Hierarchy level");
				sb.append("\n");
				sb2.append(Integer.toString(MetadataRecord.getInstance().getMetadata().getModifier().getLevel()));
				sb2.append("\n");
			}
			else{
				sb.append("*Key");
				sb.append("\n");
				sb2.append("\n");
				complete = false;
			}
			
			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getTooltip() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getTooltip().length() == 0))){
				sb.append("Tooltip");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getTooltip());
				sb2.append("\n");
			}

			if(!((MetadataRecord.getInstance().getMetadata().getModifier().getDimcode() == null) || (MetadataRecord.getInstance().getMetadata().getModifier().getDimcode().length() == 0))){
				sb.append("Dimension Code");
				sb.append("\n");
				sb2.append(MetadataRecord.getInstance().getMetadata().getModifier().getDimcode());
				sb2.append("\n");
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
				MetadataRecord.getInstance().getMetadata().getModifier().setMetadataxml(null);
			
			verifyData.setText(sb.toString());
			verifyData.pack();


			verifyData2.setText(sb2.toString());
			verifyData2.pack();
			
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
				MetadataRecord.getInstance().getMetadata().getModifier().setMetadataxml(xml);
			}
		}
}
