/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *      Wensong Pan
 * 		Lori Phillips
 */

package edu.harvard.i2b2.explorer.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.dialogs.Dialog;

import edu.harvard.i2b2.eclipse.plugins.explorer.utils.StackData;

public class ExplorerDisplayXmlStackList extends Dialog {

	private java.util.List<StackData> msgs;

	public ExplorerDisplayXmlStackList(Shell parentShell,
			java.util.List<StackData> msgs) {
		super(parentShell);
		this.msgs = msgs;
	}

	/** Create new form for list of response messages **/

	protected Control createDialogArea(Composite parent) {

		this.getShell().setText("XML Messages sent/received by this cell");
		Composite comp = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) comp.getLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;

		final List list = new List(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.heightHint = 400;
		gridData.widthHint = 100;
		gridData.grabExcessVerticalSpace = true;
		list.setLayoutData(gridData);

		if (msgs != null) {
			for (int loopIndex = 0; loopIndex < msgs.size(); loopIndex++) {
				list.add(msgs.get(loopIndex).getName());
			}
		}
		final Text text = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.heightHint = 380;
		gridData.widthHint = 400;
		gridData.grabExcessVerticalSpace = true;

		text.setLayoutData(gridData);
		final Button showPassword = new Button(comp, SWT.CHECK);
		showPassword.setText("Show password");
		showPassword.setSelection(false);

		list.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				String outString = msgs.get(list.getSelectionIndex())
						.getMessage();
				if (showPassword.getSelection() == false) {
					Pattern p = Pattern.compile("<password>.+</password>");
					Matcher m = p.matcher(outString);
					outString = m.replaceAll("<password>*********</password>");
				}
				text.setText(outString);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				String outString = msgs.get(list.getSelectionIndex())
						.getMessage();
				if (showPassword.getSelection() == false) {
					Pattern p = Pattern.compile("<password>.+</password>");
					Matcher m = p.matcher(outString);
					outString = m.replaceAll("<password>*********</password>");
				}
				text.setText(outString);
			}
		});

		return comp;
	}

}
