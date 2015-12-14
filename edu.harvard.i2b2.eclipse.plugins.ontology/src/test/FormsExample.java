package test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.part.*;

public class FormsExample extends ViewPart{

	public void createPartControl(Composite parent){
		FormToolkit kit = new FormToolkit(parent.getDisplay());
		Form form = kit.createForm(parent);
		ColumnLayout layout = new ColumnLayout();
		form.getBody().setLayout(layout);
		
		ExpandableComposite expComp = kit.createExpandableComposite(form.getBody(), ExpandableComposite.TWISTIE);
		expComp.setText("Forms toolkit is:");
		expComp.setExpanded(true);
		
		FormText formText = kit.createFormText(expComp,true);
		expComp.setClient(formText);
		String html = "<form><li>Useful</li><li>Simple</li></form>";
		formText.setText(html, true,false);
		
		Label sep = kit.createSeparator(form.getBody(), SWT.HORIZONTAL);
		final Button button = kit.createButton(form.getBody(), "Favorite color?", SWT.NULL);
		
		
	}
	public void setFocus(){
		
	}
}
