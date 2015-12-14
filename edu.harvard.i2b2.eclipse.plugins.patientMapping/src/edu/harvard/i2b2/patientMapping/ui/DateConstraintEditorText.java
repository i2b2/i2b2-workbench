/*
 * Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   
 *     Wensong Pan
 *     
 */

package edu.harvard.i2b2.patientMapping.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import edu.harvard.i2b2.patientMapping.dataModel.ConceptTableModel;
import edu.harvard.i2b2.patientMapping.dataModel.KTable;
import edu.harvard.i2b2.patientMapping.dataModel.KTableCellEditor;
import edu.harvard.i2b2.patientMapping.dataModel.QueryModel;

public class DateConstraintEditorText extends KTableCellEditor {
	private Text m_Text;

	public void open(KTable table, int col, int row, Rectangle rect) {
		super.open(table, col, row, rect);
		m_Text.setText(m_Model.getContentAt(m_Col, m_Row).toString());
		m_Text.selectAll();
		m_Text.setVisible(true);
		m_Text.setFocus();

		QueryModel ndata = (QueryModel) ((ConceptTableModel) m_Model)
				.getContentAt(8, row);
		// ((ConceptKTableModel)m_Model).valueMode(m_Row);

		DateConstraintFrame vDialog = new DateConstraintFrame(ndata, m_Table);
		vDialog.setSize(430, 230);
		vDialog.setLocation(300, 300);
		vDialog.setTitle("Choose time ");
		vDialog.setVisible(true);
	}

	public void close(boolean save) {
		if (save)
			m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
		super.close(save);
		m_Text = null;
	}

	protected Control createControl() {
		// log.debug("Created a new one.");
		m_Text = new Text(m_Table, SWT.NONE);
		m_Text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				try {
					onKeyPressed(e);
				} catch (Exception ex) {
				}
			}
		});
		m_Text.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent arg0) {
				onTraverse(arg0);
			}
		});
		m_Text.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent arg0) {
				/*
				 * int mode = ((ConceptKTableModel)m_Model).valueMode(0);
				 * 
				 * if(mode == 0) { EnumValueConstrainFrame vDialog1 = new
				 * EnumValueConstrainFrame(); vDialog1.setSize(410, 330);
				 * vDialog1.setLocation(300, 300);
				 * vDialog1.setTitle("Choose value"+m_Table.selectedRow);
				 * vDialog1.setVisible(true); } else if(mode == 1) {
				 * NumericValueConstrainFrame vDialog1 = new
				 * NumericValueConstrainFrame(); vDialog1.setSize(410, 330);
				 * vDialog1.setLocation(300, 300);
				 * vDialog1.setTitle("Choose value"+m_Table.selectedRow);
				 * vDialog1.setVisible(true); }
				 */

			}

			public void mouseDown(MouseEvent arg0) {

			}

			public void mouseUp(MouseEvent arg0) {
			}

		});
		return m_Text;
	}

	/*
	 * overridden from superclass
	 */
	public void setBounds(Rectangle rect) {
		super.setBounds(new Rectangle(rect.x, rect.y + (rect.height - 15) / 2
				+ 1, rect.width, 15));
	}

}
