package edu.harvard.i2b2.adminTool.dataModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

/**
 * @author kupzog (c) 2004 by Friederich Kupzog Elektronik & Software
 * 
 *         The table model is the most important part of KTable. It provides -
 *         content information - layout information - rendering information to
 *         the KTable.
 * 
 *         Generally speaking, all functions should return their results as
 *         quick as possible. If the table is slow, check it with
 *         KTableModelBasic. It is no longer slow, your model should be tuned.
 * 
 */

public interface KTableModel {

	/**
	 * This function should return the content at the given position. The
	 * content is an Object, that means it can be everything.
	 * 
	 * The returned Object is handed over to the KTableCellRenderer. You can
	 * deciede which renderer is used in getCellRenderer. Usually, the renderer
	 * expects the content being of a certain type.
	 */
	Object getContentAt(int col, int row);

	/**
	 * A table cell will be "in place editable" if this method returns a valid
	 * cell editor for the given cell. For no edit functionalitity return null.
	 * 
	 * @param col
	 * @param row
	 * @return KTableCellEditor
	 */
	KTableCellEditor getCellEditor(int col, int row);

	/**
	 * If getCellEditor() does return eny editors instead of null, the table
	 * will use this method to set the changed cell values.
	 * 
	 * @param col
	 * @param row
	 */
	void setContentAt(int col, int row, Object value);

	/**
	 * This function tells the KTable how many rows have to be displayed. KTable
	 * counts header rows as normal rows, so the number of header rows has to be
	 * added to the number of data rows. The function must at least return the
	 * number of fixed rows.
	 * 
	 * @return int
	 */
	int getRowCount();

	/**
	 * This function tells the KTable how many rows form the "column header".
	 * These rows are always displayed and not scrolled.
	 * 
	 * @return int
	 */
	int getFixedRowCount();

	/**
	 * This function tells the KTable how many columns have to be displayed. It
	 * must at least return the number of fixed Columns.
	 */
	int getColumnCount();

	/**
	 * This function tells the KTable how many columns form the "row header".
	 * These columns are always displayed and not scrolled.
	 * 
	 * @return int
	 */
	int getFixedColumnCount();

	/**
	 * Each column can have its individual width. The model has to manage these
	 * widths and return the values with this function.
	 * 
	 * @param col
	 * @return int
	 */
	int getColumnWidth(int col);

	/**
	 * This function should return true if the user should be allowed to resize
	 * the given column. (all rows have the same height except the first)
	 * 
	 * @param col
	 * @return boolean
	 */
	boolean isColumnResizable(int col);

	/**
	 * Each column can have its individual width. The model has to manage these
	 * widths. If the user resizes a column, the model has to keep track of
	 * these changes. The model is informed about such a resize by this method.
	 * (view updates are managed by the table)
	 * 
	 * @param col
	 * @param value
	 */
	void setColumnWidth(int col, int value);

	/**
	 * All rows except the first row have the same height.
	 * 
	 * @return int
	 */
	int getRowHeight();

	/**
	 * Returns the height of the first row, usually the header row. If no header
	 * is needed, this function should return the same value as getRowHeight.
	 * 
	 * @return int
	 */
	int getFirstRowHeight();

	/**
	 * This function should return true if the user should be allowed to resize
	 * the rows.
	 * 
	 * @param col
	 * @return boolean
	 */
	boolean isRowResizable();

	/**
	 * This function should return the minimum height of the rows. It is only
	 * needed if the rows are resizable.
	 * 
	 * @return int
	 */
	int getRowHeightMinimum();

	/**
	 * If the user resizes a row, the model has to keep track of these changes.
	 * The model is informed about such a resize by this method. (view updates
	 * are managed by the table)
	 */
	void setRowHeight(int value);

	/**
	 * Returns the cell renderer for the given cell. For a first approach,
	 * KTableCellRenderer.defaultRenderer can be returned. Derive
	 * KTableCellRenderer to change the tables appearance.
	 * 
	 * @param col
	 * @param row
	 * @return KTableCellRenderer
	 */
	KTableCellRenderer getCellRenderer(int col, int row);
}

class KTableColorCellRenderer extends KTableCellRenderer {

	/**
	   * 
	   */
	public KTableColorCellRenderer() {
	}

	/*
	 * overridden from superclass
	 */
	@Override
	public int getOptimalWidth(GC gc, int col, int row, Object content,
			boolean fixed) {
		return 16;
	}

	/*
	 * overridden from superclass
	 */
	@Override
	public void drawCell(GC gc, Rectangle rect, int col, int row,
			Object content, boolean focus, boolean fixed, boolean clicked) {
		// Performance test:
		/*
		 * gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		 * gc.fillRectangle(rect);
		 * 
		 * int j=1; for (int i = 0; i < 10000000; i++) { j++; }
		 */
		Color color = new Color(m_Display, (RGB) content);
		gc.setBackground(m_Display.getSystemColor(SWT.COLOR_GRAY));
		rect.height++;
		rect.width++;
		gc.fillRectangle(rect);

		gc.setBackground(color);
		// if (!focus) {
		rect.x += 1;
		rect.y += 1;
		rect.height -= 2;
		rect.width -= 2;
		// }
		gc.fillRectangle(rect);
		color.dispose();
	}

}
