/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/
package edu.harvard.i2b2.analysis.dataModel;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import edu.harvard.i2b2.analysis.ui.WebColorDialog;

public abstract class KTableCellEditor {
	private static final Log log = LogFactory.getLog(KTableCellEditor.class);
    protected KTableModel m_Model;

    protected KTable m_Table;

    protected Rectangle m_Rect;

    protected int m_Row;

    protected int m_Col;

    protected Control m_Control;

    protected String toolTip;

    /**
     * disposes the editor and its components
     */
    public void dispose() {
	if (m_Control != null) {
	    m_Control.dispose();
	    m_Control = null;
	}
    }

    /**
     * Activates the editor at the given position.
     * 
     * @param row
     * @param col
     * @param rect
     */
    public void open(KTable table, int col, int row, Rectangle rect) {
	m_Table = table;
	m_Model = table.getModel();
	m_Rect = rect;
	m_Row = row;
	m_Col = col;
	if (m_Control == null) {
	    m_Control = createControl();
	    m_Control.setToolTipText(toolTip);
	    m_Control.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
		    close(true);
		}
	    });
	}
	setBounds(m_Rect);
	GC gc = new GC(m_Table);
	m_Table.drawCell(gc, m_Col, m_Row);
	gc.dispose();
    }

    /**
     * Deactivates the editor.
     * 
     * @param save
     *            If true, the content is saved to the underlying table.
     */
    public void close(boolean save) {
	m_Table.m_CellEditor = null;
	// m_Control.setVisible(false);
	GC gc = new GC(m_Table);
	m_Table.drawCell(gc, m_Col, m_Row);
	gc.dispose();
	this.dispose();
    }

    /**
     * Returns true if the editor has the focus.
     * 
     * @return boolean
     */
    public boolean isFocused() {
	if (m_Control == null)
	    return false;
	return m_Control.isFocusControl();
    }

    /**
     * Sets the editor's position and size
     * 
     * @param rect
     */
    public void setBounds(Rectangle rect) {
	if (m_Control != null)
	    m_Control.setBounds(rect);
    }

    /*
     * Creates the editor's control. Has to be overwritten by useful editor
     * implementations.
     */
    protected abstract Control createControl();

    protected void onKeyPressed(KeyEvent e) {
	if ((e.character == '\r') && ((e.stateMask & SWT.SHIFT) == 0)) {
	    close(true);
	} else if (e.character == SWT.ESC) {
	    close(false);
	} else {
	    m_Table.scrollToFocus();
	}
    }

    protected void onTraverse(TraverseEvent e) {
	close(true);
	// m_Table.tryToOpenEditorAt(m_Col+1, m_Row);
    }

    /**
     * @param toolTip
     */
    public void setToolTipText(String toolTip) {
	this.toolTip = toolTip;
    }

}

class KTableCellPickTimeLineColor extends KTableCellEditor {
    private RGB m_Color;
    private CCombo m_Combo;
    private String m_Items[] = new String[] { "Red", "Green", "Brown",
	    "Light Brown", "Blue", "Dark Brown", "Black" };

    @Override
    public void open(KTable table, int row, int col, Rectangle rect) {
	super.open(table, row, col, rect);
	// m_Combo.setFocus();
	// m_Combo.setText((String) m_Model.getContentAt(m_Col, m_Row));
	/*
	 * if (m_Color.equals(new RGB(255, 0, 0))) m_Combo.setText("Red"); else
	 * if (m_Color.equals(new RGB(0, 255, 0))) m_Combo.setText("Green");
	 * else if (m_Color.equals(new RGB(150, 75, 0)))
	 * m_Combo.setText("Brown"); else if (m_Color.equals(new RGB(205, 133,
	 * 63))) m_Combo.setText("Light Brown"); else if (m_Color.equals(new
	 * RGB(0, 0, 255))) m_Combo.setText("Blue"); else if (m_Color.equals(new
	 * RGB(101, 67, 33))) m_Combo.setText("Dark Brown"); else if
	 * (m_Color.equals(new RGB(0, 0, 0))) m_Combo.setText("Black");
	 */

	m_Combo.setBackground(new Color(table.getDisplay(), (RGB) m_Model
		.getContentAt(m_Col, m_Row)));
    }

    @Override
    public void close(boolean save) {
	if (save)
	    m_Model.setContentAt(m_Col, m_Row, m_Color);
	super.close(save);
	m_Color = null;
    }

    @Override
    protected Control createControl() {
	m_Combo = new CCombo(m_Table, SWT.READ_ONLY);
	m_Combo.setVisibleItemCount(7);
	m_Combo.setEditable(false);
	m_Combo.setBackground(Display.getCurrent().getSystemColor(
		SWT.COLOR_LIST_BACKGROUND | SWT.READ_ONLY));
	if (m_Items != null)
	    m_Combo.setItems(m_Items);
	m_Combo.addSelectionListener(new SelectionListener() {
	    public void widgetSelected(SelectionEvent e) {
		String colorText = m_Combo.getText();
		if (colorText.equals("Red"))
		    m_Color = new RGB(255, 0, 0);
		else if (colorText.equals("Green"))
		    m_Color = new RGB(0, 255, 0);
		else if (colorText.equals("Brown"))
		    m_Color = new RGB(150, 75, 0);
		else if (colorText.equals("Light Brown"))
		    m_Color = new RGB(205, 133, 63);
		else if (colorText.equals("Blue"))
		    m_Color = new RGB(0, 0, 255);
		else if (colorText.equals("Dark Brown"))
		    m_Color = new RGB(101, 67, 33);
		else if (colorText.equals("Black"))
		    m_Color = new RGB(0, 0, 0);

		close(true);
		m_Table.redraw();
	    }

	    public void widgetDefaultSelected(SelectionEvent e) {
		String colorText = m_Combo.getText();
		if (colorText.equals("Red"))
		    m_Color = new RGB(255, 0, 0);
		else if (colorText.equals("Green"))
		    m_Color = new RGB(0, 255, 0);
		else if (colorText.equals("Brown"))
		    m_Color = new RGB(150, 75, 0);
		else if (colorText.equals("Light Brown"))
		    m_Color = new RGB(205, 133, 63);
		else if (colorText.equals("Blue"))
		    m_Color = new RGB(0, 0, 255);
		else if (colorText.equals("Dark Brown"))
		    m_Color = new RGB(101, 67, 33);
		else if (colorText.equals("Black"))
		    m_Color = new RGB(0, 0, 0);

		close(true);
		m_Table.redraw();
	    }
	});
	m_Combo.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		try {
		    onKeyPressed(e);
		} catch (Exception ex) {
		}
	    }
	});
	return m_Combo;
    }

    @Override
    public void setBounds(Rectangle rect) {
	super.setBounds(new Rectangle(rect.x, rect.y + 1, rect.width,
		rect.height));
    }

    public void setColor(RGB color) {
	m_Color = color;
    }

}

class KTableCellEditorColor extends KTableCellEditor {
    private RGB m_Color = new RGB(0, 255, 0);
    private org.eclipse.swt.widgets.Label m_Label;

    public void openS(KTable table, int row, int col, Rectangle rect) {
	super.open(table, row, col, rect);
    }

    @Override
    public void open(KTable table, int row, int col, Rectangle rect) {
	super.open(table, row, col, rect);
	// m_Combo.setFocus();
	// m_Combo.setText((String) m_Model.getContentAt(m_Col, m_Row));

	// org.eclipse.swt.widgets.ColorDialog dlg = new
	// org.eclipse.swt.widgets.ColorDialog(table.getShell(), SWT.DROP_DOWN);

	WebColorDialog dlg = new WebColorDialog(m_Table.getShell());
	// Set the selected color in the dialog from
	// user's selected color
	Object color = m_Table.getModel().getContentAt(5, row);
	if (color != null)
	    dlg.setRGB((RGB) color);
	else
	    dlg.setRGB(new RGB(0, 255, 0));

	// Change the title bar text
	dlg.setText("Choose a Color");

	// Open the dialog and retrieve the selected color
	RGB rgbDlg = dlg.open();
	RGB rgb = new RGB(rgbDlg.red, rgbDlg.green, rgbDlg.blue);
	if (rgb != null) {

	    /*ConceptKTableModel i2Model = (ConceptKTableModel) m_Table
		    .getModel();
	    if (i2Model.isColorUsable(rgb)) {
		// Dispose the old color, create the
		// new one, and set into the label
		m_Color = rgb;
		try {
		    Display dplay = m_Table.getDisplay();
		    Color colBack = new Color(dplay, rgb);
		    m_Label.setBackground(colBack);
		    // colBack.dispose();
		} catch (Exception e) {
		    System.out.println(e.getMessage());
		}
	    } else {
		MessageBox mBox = new MessageBox(m_Table.getShell(),
			SWT.ICON_INFORMATION | SWT.OK);
		mBox.setText("Invalid Color");
		mBox.setMessage("The color you picked is not supported.  Please choose another color.");
		mBox.open();
	    }*/
	}
	close(true);
    }

    @Override
    public void close(boolean save) {
	if (save)
	    m_Model.setContentAt(m_Col, m_Row, m_Color);
	super.close(save);
	m_Color = null;
    }

    @Override
    protected Control createControl() {
	Object testObj = m_Model.getContentAt(m_Col, m_Row);
	if (testObj == null)
	    m_Color = new RGB(0, 255, 0);
	else
	    m_Color = (RGB) testObj;
	m_Label = new Label(m_Table.getShell(), SWT.NONE);
	m_Label.setBackground(new Color(m_Table.getDisplay(), m_Color));
	return m_Label;
    }

    @Override
    public void setBounds(Rectangle rect) {
	super.setBounds(new Rectangle(rect.x, rect.y + 1, rect.width,
		rect.height));
    }

    public void setColor(RGB rgb) {
	m_Color = rgb;
	/*ConceptKTableModel i2Model = (ConceptKTableModel) m_Table.getModel();
	if (i2Model.isColorUsable(rgb)) {
	    // Dispose the old color, create the
	    // new one, and set into the label
	    m_Color = rgb;
	    try {
		Display dplay = m_Table.getDisplay();
		Color colBack = new Color(dplay, rgb);
		m_Label.setBackground(colBack);
		// colBack.dispose();
	    } catch (Exception e) {
		System.out.println(e.getMessage());
	    }
	} else {
	    MessageBox mBox = new MessageBox(m_Table.getShell(),
		    SWT.ICON_INFORMATION | SWT.OK);
	    mBox.setText("Invalid Color");
	    mBox.setMessage("The color you picked is not supported.  Please choose another color.");
	    mBox.open();
	}*/
	close(true);
    }
}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

class KTableCellEditorCombo extends KTableCellEditor {
	private static final Log log = LogFactory.getLog(KTableCellEditorCombo.class);
    private CCombo m_Combo;

    private String m_Items[];

    @Override
    public void open(KTable table, int row, int col, Rectangle rect) {
	super.open(table, row, col, rect);
	m_Combo.setFocus();
	m_Combo.setText((String) m_Model.getContentAt(m_Col, m_Row));
    }

    @Override
    public void close(boolean save) {
	if (save)
	    m_Model.setContentAt(m_Col, m_Row, m_Combo.getText());
	super.close(save);
	m_Combo = null;
    }

    @Override
    protected Control createControl() {
	m_Combo = new CCombo(m_Table, SWT.READ_ONLY);
	m_Combo.setBackground(Display.getCurrent().getSystemColor(
		SWT.COLOR_LIST_BACKGROUND));
	if (m_Items != null) {
	    for (int i = 0; i < m_Items.length; i++) {
		m_Combo.add(m_Items[i]);
	    }
	}
	m_Combo.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		try {
		    onKeyPressed(e);
		    log.debug("key pressed");
		} catch (Exception ex) {
		}
	    }
	});
	m_Combo.addSelectionListener(new SelectionListener() {
	    public void widgetSelected(SelectionEvent e) {
		log.debug("selected: " + m_Combo.getText());
		int row = m_Table.selectedRow;
		int col = m_Table.selectedColumn;
		log.debug("selected cell: [" + m_Table.selectedColumn
			+ "," + m_Table.selectedRow + "]");
		if (col == 4) {
		    KTableCellEditorColor colorEditor = (KTableCellEditorColor) m_Table
			    .getModel().getCellEditor(col + 1, row);
		    if (colorEditor != null) {
			Rectangle r = m_Table.getCellRect(col + 1, row);
			colorEditor.openS(m_Table, col + 1, row, r);
		    }
		    String text = m_Combo.getText();
		    if (text.equalsIgnoreCase("Tall")
			    || text.equalsIgnoreCase("Low")) {
			colorEditor.setColor(new RGB(255, 215, 0));
		    } else if (text.equalsIgnoreCase("Very Tall")
			    || text.equalsIgnoreCase("Very Low")) {
			colorEditor.setColor(new RGB(255, 0, 0));
		    } else if (text.indexOf("Medium") >= 0) {
			colorEditor.setColor(new RGB(0, 255, 0));
		    }
		}
	    }

	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
	/*
	 * m_Combo.addTraverseListener(new TraverseListener() { public void
	 * keyTraversed(TraverseEvent arg0) { onTraverse(arg0); } });
	 */
	return m_Combo;
    }

    @Override
    public void setBounds(Rectangle rect) {
	super.setBounds(new Rectangle(rect.x, rect.y + 1, rect.width,
		rect.height - 2));
    }

    public void setItems(String items[]) {
	m_Items = items;
    }

}

class KTableCellEditorComboW extends KTableCellEditor {
	private static final Log log = LogFactory.getLog(KTableCellEditorComboW.class);
	
    private CCombo m_Combo;

    private String m_Items[];

    @Override
    public void open(KTable table, int row, int col, Rectangle rect) {
	super.open(table, row, col, rect);
	m_Combo.setFocus();
	m_Combo.setText((String) m_Model.getContentAt(m_Col, m_Row));
    }

    @Override
    public void close(boolean save) {
	if (save)
	    m_Model.setContentAt(m_Col, m_Row, m_Combo.getText());
	super.close(save);
	m_Combo = null;
    }

    @Override
    protected Control createControl() {
	m_Combo = new CCombo(m_Table, SWT.NULL);
	m_Combo.setBackground(Display.getCurrent().getSystemColor(
		SWT.COLOR_LIST_BACKGROUND));
	if (m_Items != null) {
	    for (int i = 0; i < m_Items.length; i++) {
		m_Combo.add(m_Items[i]);
	    }
	}
	m_Combo.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		try {
		    onKeyPressed(e);
		    log.debug("key pressed");
		} catch (Exception ex) {
		}
	    }
	});
	m_Combo.addSelectionListener(new SelectionListener() {
	    public void widgetSelected(SelectionEvent e) {
	    	log.debug("selected: " + m_Combo.getText());
		int row = m_Table.selectedRow;
		int col = m_Table.selectedColumn;
		log.debug("selected cell: [" + m_Table.selectedColumn
			+ "," + m_Table.selectedRow + "]");
		if (col == 4) {
		    KTableCellEditorColor colorEditor = (KTableCellEditorColor) m_Table
			    .getModel().getCellEditor(col + 1, row);
		    if (colorEditor != null) {
			Rectangle r = m_Table.getCellRect(col + 1, row);
			colorEditor.openS(m_Table, col + 1, row, r);
		    }
		    String text = m_Combo.getText();
		    if (text.equalsIgnoreCase("Tall")
			    || text.equalsIgnoreCase("Low")) {
			colorEditor.setColor(new RGB(34, 139, 34));
		    } else if (text.equalsIgnoreCase("Very Tall")
			    || text.equalsIgnoreCase("Very Low")) {
			colorEditor.setColor(new RGB(255, 0, 0));
		    } else if (text.indexOf("Medium") >= 0) {
			colorEditor.setColor(new RGB(0, 255, 0));
		    }
		}
	    }

	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
	/*
	 * m_Combo.addTraverseListener(new TraverseListener() { public void
	 * keyTraversed(TraverseEvent arg0) { onTraverse(arg0); } });
	 */
	return m_Combo;
    }

    @Override
    public void setBounds(Rectangle rect) {
	super.setBounds(new Rectangle(rect.x, rect.y + 1, rect.width,
		rect.height - 2));
    }

    public void setItems(String items[]) {
	m_Items = items;
    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/
class KTableCellEditorMultilineText extends KTableCellEditor {
    private Text m_Text;

    @Override
    public void open(KTable table, int col, int row, Rectangle rect) {
	super.open(table, col, row, rect);
	m_Text.setText(m_Model.getContentAt(m_Col, m_Row).toString());
	m_Text.selectAll();
	m_Text.setVisible(true);
	m_Text.setFocus();
    }

    @Override
    public void close(boolean save) {
	if (save)
	    m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
	m_Text = null;
	super.close(save);
    }

    @Override
    protected Control createControl() {
	m_Text = new Text(m_Table, SWT.MULTI | SWT.V_SCROLL);
	m_Text.addKeyListener(new KeyAdapter() {
	    @Override
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
	return m_Text;
    }

    /*
     * overridden from superclass
     */
    @Override
    public void setBounds(Rectangle rect) {
	super.setBounds(new Rectangle(rect.x, rect.y, rect.width, rect.height));
    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/
class KTableCellEditorMultilineWrapText extends KTableCellEditor {
    private Text m_Text;

    @Override
    public void open(KTable table, int col, int row, Rectangle rect) {
	super.open(table, col, row, rect);
	m_Text.setText(m_Model.getContentAt(m_Col, m_Row).toString());
	m_Text.selectAll();
	m_Text.setVisible(true);
	m_Text.setFocus();
    }

    @Override
    public void close(boolean save) {
	if (save)
	    m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
	m_Text = null;
	super.close(save);
    }

    @Override
    protected Control createControl() {
	m_Text = new Text(m_Table, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
	m_Text.addKeyListener(new KeyAdapter() {
	    @Override
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
	return m_Text;
    }

    /*
     * overridden from superclass
     */
    @Override
    public void setBounds(Rectangle rect) {
	super.setBounds(new Rectangle(rect.x, rect.y, rect.width, rect.height));
    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

class KTableCellEditorText extends KTableCellEditor {
    private Text m_Text;

    @Override
    public void open(KTable table, int col, int row, Rectangle rect) {
	super.open(table, col, row, rect);
	m_Text.setText(m_Model.getContentAt(m_Col, m_Row).toString());
	m_Text.selectAll();
	m_Text.setVisible(true);
	m_Text.setFocus();
    }

    @Override
    public void close(boolean save) {
	if (save)
	    m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
	super.close(save);
	m_Text = null;
	// System.out.println("set to null.");
    }

    @Override
    protected Control createControl() {
	// System.out.println("Created a new one.");
	m_Text = new Text(m_Table, SWT.NONE);
	m_Text.addKeyListener(new KeyAdapter() {
	    @Override
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
	return m_Text;
    }

    /*
     * overridden from superclass
     */
    @Override
    public void setBounds(Rectangle rect) {
	super.setBounds(new Rectangle(rect.x, rect.y + (rect.height - 15) / 2
		+ 1, rect.width, 15));
    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/
class KTableCellRenderer {

    public static KTableCellRenderer defaultRenderer = new KTableCellRenderer();

    /**
	 * 
	 */
    protected Display m_Display;

    public KTableCellRenderer() {
	m_Display = Display.getCurrent();
    }

    /**
     * Returns the optimal width of the given cell (used by column resizing)
     * 
     * @param col
     * @param row
     * @param content
     * @param fixed
     * @return int
     */
    public int getOptimalWidth(GC gc, int col, int row, Object content,
	    boolean fixed) {
	return gc.stringExtent(content.toString()).x + 8;
    }

    /**
     * Standard implementation for CellRenderer. Draws a cell at the given
     * position. Uses the .getString() method of content to get a String
     * representation to draw.
     * 
     * @param gc
     *            The gc to draw on
     * @param rect
     *            The coordinates and size of the cell (add 1 to width and hight
     *            to include the borders)
     * @param col
     *            The column
     * @param row
     *            The row
     * @param content
     *            The content of the cell (as given by the table model)
     * @param focus
     *            True if the cell is selected
     * @param fixed
     *            True if the cell is fixed (unscrollable header cell)
     * @param clicked
     *            True if the cell is currently clicked (useful e.g. to paint a
     *            pressed button)
     */
    public void drawCell(GC gc, Rectangle rect, int col, int row,
	    Object content, boolean focus, boolean fixed, boolean clicked) {
	if (fixed) {

	    rect.height += 1;
	    rect.width += 1;
	    gc.setForeground(Display.getCurrent().getSystemColor(
		    SWT.COLOR_LIST_FOREGROUND));
	    if (clicked) {
		SWTX.drawButtonDown(gc, content.toString(),
			SWTX.ALIGN_HORIZONTAL_CENTER
				| SWTX.ALIGN_VERTICAL_CENTER, null,
			SWTX.ALIGN_HORIZONTAL_RIGHT
				| SWTX.ALIGN_VERTICAL_CENTER, rect);
	    } else {
		SWTX.drawButtonUp(gc, content.toString(),
			SWTX.ALIGN_HORIZONTAL_CENTER
				| SWTX.ALIGN_VERTICAL_CENTER, null,
			SWTX.ALIGN_HORIZONTAL_RIGHT
				| SWTX.ALIGN_VERTICAL_CENTER, rect);
	    }

	    return;
	}

	Color textColor;
	Color backColor;
	Color vBorderColor;
	Color hBorderColor;

	if (focus) {
	    textColor = m_Display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
	    backColor = (m_Display.getSystemColor(SWT.COLOR_LIST_SELECTION));
	    vBorderColor = m_Display.getSystemColor(SWT.COLOR_LIST_SELECTION);
	    hBorderColor = m_Display.getSystemColor(SWT.COLOR_LIST_SELECTION);
	} else {
	    textColor = m_Display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	    backColor = m_Display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	    vBorderColor = m_Display
		    .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	    hBorderColor = m_Display
		    .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	}

	gc.setForeground(hBorderColor);
	gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y
		+ rect.height);

	gc.setForeground(vBorderColor);
	gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y
		+ rect.height);

	gc.setBackground(backColor);
	gc.setForeground(textColor);

	gc.fillRectangle(rect);

	SWTX.drawTextImage(gc, content.toString(), SWTX.ALIGN_HORIZONTAL_CENTER
		| SWTX.ALIGN_VERTICAL_CENTER, null,
		SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER,
		rect.x + 3, rect.y, rect.width - 3, rect.height);

    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

class KTableCellResizeAdapter implements KTableCellResizeListener {

    public void columnResized(int col, int newWidth) {
    }

    public void rowResized(int newHeight) {
    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

/*
 * interface KTableCellResizeListener {
 * 
 * / Is called when a row is resized.
 */
/*
 * public void rowResized(int newHeight);
 * 
 * / Is called when a column is resized.
 */
/*
 * public void columnResized(int col, int newWidth);
 * 
 * }
 */

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

class KTableCellSelectionAdapter implements KTableCellSelectionListener {
    /**
     * Is called if a non-fixed cell is selected (gets the focus).
     * 
     * @see KTable for an explanation of the term "fixed cells".
     * @param col
     *            the column of the cell
     * @param row
     *            the row of the cell
     * @param statemask
     *            the modifier keys that where pressed when the selection
     *            happened.
     */
    public void cellSelected(int col, int row, int statemask) {
    }

    /**
     * Is called if a fixed cell is selected (is clicked).
     * 
     * @see KTable for an explanation of the term "fixed cells".
     * @param col
     *            the column of the cell
     * @param row
     *            the row of the cell
     * @param statemask
     *            the modifier keys that where pressed when the selection
     *            happened.
     */
    public void fixedCellSelected(int col, int row, int statemask) {
    }

}

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
 * @author Friederich Kupzog
 */
class KTableModelExample implements KTableModel {

    private int[] colWidths;

    private int rowHeight;

    private HashMap<String, Object> content;

    /**
	 * 
	 */
    public KTableModelExample() {
	colWidths = new int[getColumnCount()];
	for (int i = 0; i < colWidths.length; i++) {
	    colWidths[i] = 270;
	}
	rowHeight = 18;
	content = new HashMap<String, Object>();
    }

    // Inhalte

    public Object getContentAt(int col, int row) {
	// System.out.println("col "+col+" row "+row);
	String erg = (String) content.get(col + "/" + row);
	if (erg != null)
	    return erg;
	return col + "/" + row;
    }

    /*
     * overridden from superclass
     */
    public KTableCellEditor getCellEditor(int col, int row) {
	if (col % 2 == 0) {
	    KTableCellEditorCombo e = new KTableCellEditorCombo();
	    e
		    .setItems(new String[] { "First text", "Second text",
			    "third text" });
	    return e;
	} else
	    return new KTableCellEditorText();
    }

    /*
     * overridden from superclass
     */
    public void setContentAt(int col, int row, Object value) {
	content.put(col + "/" + row, value);
	//
    }

    // Umfang

    public int getRowCount() {
	return 100;
    }

    public int getFixedRowCount() {
	return 2;
    }

    public int getColumnCount() {
	return 100;
    }

    public int getFixedColumnCount() {
	return 1;
    }

    // GroBen

    public int getColumnWidth(int col) {
	return colWidths[col];
    }

    public int getRowHeight() {
	return rowHeight;
    }

    public boolean isColumnResizable(int col) {
	return true;
    }

    public int getFirstRowHeight() {
	return 22;
    }

    public boolean isRowResizable() {
	return true;
    }

    public int getRowHeightMinimum() {
	return 18;
    }

    public void setColumnWidth(int col, int value) {
	colWidths[col] = value;
    }

    public void setRowHeight(int value) {
	if (value < 2)
	    value = 2;
	rowHeight = value;
    }

    // Rendering

    public KTableCellRenderer getCellRenderer(int col, int row) {
	return KTableCellRenderer.defaultRenderer;
    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

class PaletteExampleModel implements KTableModel {

    /*
     * overridden from superclass
     */
    public Object getContentAt(int col, int row) {
	return new RGB(col * 16, row * 16, (col + row) * 8);
    }

    /*
     * overridden from superclass
     */
    public KTableCellEditor getCellEditor(int col, int row) {
	return null;
    }

    /*
     * overridden from superclass
     */
    public void setContentAt(int col, int row, Object value) {
    }

    /*
     * overridden from superclass
     */
    public int getRowCount() {
	return 16;
    }

    /*
     * overridden from superclass
     */
    public int getFixedRowCount() {
	return 0;
    }

    /*
     * overridden from superclass
     */
    public int getColumnCount() {
	return 16;
    }

    /*
     * overridden from superclass
     */
    public int getFixedColumnCount() {
	return 0;
    }

    /*
     * overridden from superclass
     */
    public int getColumnWidth(int col) {
	return 10;
    }

    /*
     * overridden from superclass
     */
    public boolean isColumnResizable(int col) {
	return false;
    }

    /*
     * overridden from superclass
     */
    public void setColumnWidth(int col, int value) {
    }

    /*
     * overridden from superclass
     */
    public int getRowHeight() {
	return 10;
    }

    /*
     * overridden from superclass
     */
    public int getFirstRowHeight() {
	return 10;
    }

    /*
     * overridden from superclass
     */
    public boolean isRowResizable() {
	return false;
    }

    /*
     * overridden from superclass
     */
    public int getRowHeightMinimum() {
	return 10;
    }

    /*
     * overridden from superclass
     */
    public void setRowHeight(int value) {
    }

    private static KTableCellRenderer myRenderer = new PaletteExampleRenderer();

    /*
     * overridden from superclass
     */
    public KTableCellRenderer getCellRenderer(int col, int row) {
	return myRenderer;
    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

class PaletteExampleRenderer extends KTableCellRenderer {

    /**
	 * 
	 */
    public PaletteExampleRenderer() {
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
	gc.setBackground(m_Display.getSystemColor(SWT.COLOR_WHITE));
	rect.height++;
	rect.width++;
	gc.fillRectangle(rect);

	gc.setBackground(color);
	if (!focus) {
	    rect.x += 1;
	    rect.y += 1;
	    rect.height -= 2;
	    rect.width -= 2;
	}
	gc.fillRectangle(rect);
	color.dispose();
    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

class TownExampleModel implements KTableModel {

    private int[] colWidths;

    private TownExampleContent[] content;

    public TownExampleModel() {
	colWidths = new int[getColumnCount()];
	colWidths[0] = 120;
	colWidths[1] = 100;
	colWidths[2] = 180;

	content = new TownExampleContent[3];
	content[0] = new TownExampleContent("Aachen", "Germany");
	content[1] = new TownExampleContent("Cologne", "Germany");
	content[2] = new TownExampleContent("Edinburgh", "Scotland");

    }

    /*
     * overridden from superclass
     */
    public Object getContentAt(int col, int row) {
	if (row == 0) // Header
	{
	    if (col == 0)
		return "Town";
	    else if (col == 1)
		return "Country";
	    else
		return "Notes";
	} else {
	    return content[row - 1];
	}
    }

    /*
     * overridden from superclass
     */
    public KTableCellEditor getCellEditor(int col, int row) {
	if (row > 0 && col == 2)
	    return new KTableCellEditorMultilineText();
	return null;
    }

    /*
     * overridden from superclass
     */
    public void setContentAt(int col, int row, Object value) {
	content[row - 1].notes = (String) value;
    }

    /*
     * overridden from superclass
     */
    public int getRowCount() {
	return 4;
    }

    /*
     * overridden from superclass
     */
    public int getFixedRowCount() {
	return 1;
    }

    /*
     * overridden from superclass
     */
    public int getColumnCount() {
	return 3;
    }

    /*
     * overridden from superclass
     */
    public int getFixedColumnCount() {
	return 0;
    }

    /*
     * overridden from superclass
     */
    public int getColumnWidth(int col) {
	return colWidths[col];
    }

    /*
     * overridden from superclass
     */
    public boolean isColumnResizable(int col) {
	return (col != 0);
    }

    /*
     * overridden from superclass
     */
    public void setColumnWidth(int col, int value) {
	if (value > 120)
	    colWidths[col] = value;
    }

    /*
     * overridden from superclass
     */
    public int getRowHeight() {
	return 140;
    }

    /*
     * overridden from superclass
     */
    public int getFirstRowHeight() {
	return 20;
    }

    /*
     * overridden from superclass
     */
    public boolean isRowResizable() {
	return false;
    }

    /*
     * overridden from superclass
     */
    public int getRowHeightMinimum() {
	return 20;
    }

    /*
     * overridden from superclass
     */
    public void setRowHeight(int value) {
    }

    /*
     * overridden from superclass
     */
    public KTableCellRenderer getCellRenderer(int col, int row) {
	if (row > 0)
	    return new TownExampleRenderer();
	return KTableCellRenderer.defaultRenderer;
    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

class TownExampleRenderer extends KTableCellRenderer {

    protected Display m_Display;

    public TownExampleRenderer() {
	m_Display = Display.getCurrent();
    }

    @Override
    public int getOptimalWidth(GC gc, int col, int row, Object content,
	    boolean fixed) {
	return Math.max(gc.stringExtent(content.toString()).x + 8, 120);
    }

    @Override
    public void drawCell(GC gc, Rectangle rect, int col, int row,
	    Object content, boolean focus, boolean fixed, boolean clicked) {
	Color textColor;
	Color backColor;
	Color ffcc33;
	TownExampleContent myContent = (TownExampleContent) content;

	if (focus) {
	    textColor = m_Display.getSystemColor(SWT.COLOR_BLUE);
	} else {
	    textColor = m_Display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	}
	backColor = (m_Display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
	ffcc33 = m_Display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

	gc.setForeground(ffcc33);
	gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y
		+ rect.height);

	gc.setForeground(ffcc33);
	gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y
		+ rect.height);

	if (col == 0) {
	    gc.setBackground(m_Display
		    .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	    textColor = m_Display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	    gc.setForeground(textColor);

	    gc.drawImage((myContent.image), rect.x, rect.y);

	    rect.y += 120;
	    rect.height -= 120;
	    gc.fillRectangle(rect);
	    gc.drawText((myContent.name), rect.x + 25, rect.y + 2);
	}

	else if (col == 1) {
	    gc.setBackground(backColor);
	    gc.setForeground(textColor);

	    gc.fillRectangle(rect);

	    SWTX.drawTextImage(gc, myContent.country,
		    SWTX.ALIGN_HORIZONTAL_LEFT | SWTX.ALIGN_VERTICAL_TOP, null,
		    SWTX.ALIGN_HORIZONTAL_LEFT | SWTX.ALIGN_VERTICAL_CENTER,
		    rect.x + 3, rect.y, rect.width - 3, rect.height);

	}

	else if (col == 2) {
	    gc.setBackground(backColor);
	    gc.setForeground(textColor);

	    gc.fillRectangle(rect);
	    Rectangle save = gc.getClipping();
	    gc.setClipping(rect);
	    gc.drawText((myContent.notes), rect.x + 3, rect.y);
	    gc.setClipping(save);

	}

    }

}

/*******************************************************************************
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Friederich Kupzog - initial API and implementation
 * fkmk@kupzog.de www.kupzog.de/fkmk
 ******************************************************************************/

class TownExampleContent {
    public String name;

    public Image image;

    public String country;

    public String notes;

    public TownExampleContent(String name, String country) {
	this.name = name;
	this.country = country;
	image = loadImageResource(Display.getCurrent(), "/gfx/" + name + ".gif");
	notes = "Double click to edit and use \n"
		+ "Shift+Enter to start a new line...";
    }

    @SuppressWarnings("unchecked")
    public Image loadImageResource(Display d, String name) {
	try {

	    Image ret = null;
	    Class clazz = this.getClass();
	    InputStream is = clazz.getResourceAsStream(name);
	    if (is != null) {
		ret = new Image(d, is);
		is.close();
	    }
	    return ret;
	} catch (Exception e1) {
	    return null;
	}
    }

    /*
     * overridden from superclass
     */
    @Override
    public String toString() {
	return notes;
    }

}

/**
 * @author Kosta, Friederich Kupzog
 */
class SWTX {
    public static final int EVENT_SWTX_BASE = 1000;

    public static final int EVENT_TABLE_HEADER = EVENT_SWTX_BASE + 1;

    public static final int EVENT_TABLE_HEADER_CLICK = EVENT_SWTX_BASE + 2;

    public static final int EVENT_TABLE_HEADER_RESIZE = EVENT_SWTX_BASE + 3;

    //
    public static final int ALIGN_HORIZONTAL_MASK = 0x0F;

    public static final int ALIGN_HORIZONTAL_NONE = 0x00;

    public static final int ALIGN_HORIZONTAL_LEFT = 0x01;

    public static final int ALIGN_HORIZONTAL_LEFT_LEFT = ALIGN_HORIZONTAL_LEFT;

    public static final int ALIGN_HORIZONTAL_LEFT_RIGHT = 0x02;

    public static final int ALIGN_HORIZONTAL_LEFT_CENTER = 0x03;

    public static final int ALIGN_HORIZONTAL_RIGHT = 0x04;

    public static final int ALIGN_HORIZONTAL_RIGHT_RIGHT = ALIGN_HORIZONTAL_RIGHT;

    public static final int ALIGN_HORIZONTAL_RIGHT_LEFT = 0x05;

    public static final int ALIGN_HORIZONTAL_RIGHT_CENTER = 0x06;

    public static final int ALIGN_HORIZONTAL_CENTER = 0x07;

    public static final int ALIGN_VERTICAL_MASK = 0xF0;

    public static final int ALIGN_VERTICAL_TOP = 0x10;

    public static final int ALIGN_VERTICAL_BOTTOM = 0x20;

    public static final int ALIGN_VERTICAL_CENTER = 0x30;

    //
    private static GC m_LastGCFromExtend;

    private static Map<String, Point> m_StringExtentCache = new HashMap<String, Point>();

    private static synchronized Point getCachedStringExtent(GC gc, String text) {
	if (m_LastGCFromExtend != gc) {
	    m_StringExtentCache.clear();
	    m_LastGCFromExtend = gc;
	}
	Point p = m_StringExtentCache.get(text);
	if (p == null) {
	    if (text == null)
		return new Point(0, 0);
	    p = gc.stringExtent(text);
	    m_StringExtentCache.put(text, p);
	}
	return new Point(p.x, p.y);
    }

    public static int drawTextVerticalAlign(GC gc, String text, int textAlign,
	    int x, int y, int w, int h) {
	if (text == null)
	    text = "";
	Point textSize = getCachedStringExtent(gc, text);
	{
	    boolean addPoint = false;
	    while ((text.length() > 0) && (textSize.x >= w)) {
		text = text.substring(0, text.length() - 1);
		textSize = getCachedStringExtent(gc, text + "...");
		addPoint = true;
	    }
	    if (addPoint)
		text = text + "...";
	    textSize = getCachedStringExtent(gc, text);
	    if (textSize.x >= w) {
		text = "";
		textSize = getCachedStringExtent(gc, text);
	    }
	}
	//
	if ((textAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_TOP) {
	    gc.drawText(text, x, y);
	    gc.fillRectangle(x, y + textSize.y, textSize.x, h - textSize.y);
	    return textSize.x;
	}
	if ((textAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_BOTTOM) {
	    gc.drawText(text, x, y + h - textSize.y);
	    gc.fillRectangle(x, y, textSize.x, h - textSize.y);
	    return textSize.x;
	}
	if ((textAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_CENTER) {
	    int yOffset = (h - textSize.y) / 2;
	    gc.drawText(text, x, y + yOffset);
	    gc.fillRectangle(x, y, textSize.x, yOffset);
	    gc.fillRectangle(x, y + yOffset + textSize.y, textSize.x, h
		    - (yOffset + textSize.y));
	    return textSize.x;
	}
	throw new SWTException("H: " + (textAlign & ALIGN_VERTICAL_MASK));
    }

    public static void drawTransparentImage(GC gc, Image image, int x, int y) {
	if (image == null)
	    return;
	Point imageSize = new Point(image.getBounds().width,
		image.getBounds().height);
	Image img = new Image(Display.getCurrent(), imageSize.x, imageSize.y);
	GC gc2 = new GC(img);
	gc2.setBackground(gc.getBackground());
	gc2.fillRectangle(0, 0, imageSize.x, imageSize.y);
	gc2.drawImage(image, 0, 0);
	gc.drawImage(img, x, y);
	gc2.dispose();
	img.dispose();
    }

    public static void drawImageVerticalAlign(GC gc, Image image,
	    int imageAlign, int x, int y, int h) {
	if (image == null)
	    return;
	Point imageSize = new Point(image.getBounds().width,
		image.getBounds().height);
	//
	if ((imageAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_TOP) {
	    drawTransparentImage(gc, image, x, y);
	    gc.fillRectangle(x, y + imageSize.y, imageSize.x, h - imageSize.y);
	    return;
	}
	if ((imageAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_BOTTOM) {
	    drawTransparentImage(gc, image, x, y + h - imageSize.y);
	    gc.fillRectangle(x, y, imageSize.x, h - imageSize.y);
	    return;
	}
	if ((imageAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_CENTER) {
	    int yOffset = (h - imageSize.y) / 2;
	    drawTransparentImage(gc, image, x, y + yOffset);
	    gc.fillRectangle(x, y, imageSize.x, yOffset);
	    gc.fillRectangle(x, y + yOffset + imageSize.y, imageSize.x, h
		    - (yOffset + imageSize.y));
	    return;
	}
	throw new SWTException("H: " + (imageAlign & ALIGN_VERTICAL_MASK));
    }

    public static void drawTextImage(GC gc, String text, int textAlign,
	    Image image, int imageAlign, int x, int y, int w, int h) {
	Point textSize = getCachedStringExtent(gc, text);
	Point imageSize;
	if (image != null)
	    imageSize = new Point(image.getBounds().width,
		    image.getBounds().height);
	else
	    imageSize = new Point(0, 0);
	//
	/*
	 * Rectangle oldClipping = gc.getClipping(); gc.setClipping(x, y, w, h);
	 */
	try {
	    if ((image == null)
		    && ((textAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_CENTER)) {
		Point p = getCachedStringExtent(gc, text);
		int offset = (w - p.x) / 2;
		if (offset > 0) {
		    drawTextVerticalAlign(gc, text, textAlign, x + offset, y, w
			    - offset, h);
		    gc.fillRectangle(x, y, offset, h);
		    gc
			    .fillRectangle(x + offset + p.x, y, w
				    - (offset + p.x), h);
		} else {
		    p.x = drawTextVerticalAlign(gc, text, textAlign, x, y, w, h);
		    // gc.setBackground(Display.getCurrent().getSystemColor(SWT.
		    // COLOR_YELLOW));
		    gc.fillRectangle(x + p.x, y, w - (p.x), h);
		    // offset = (w - p.x) / 2;
		    // gc.fillRectangle(x, y, offset, h);
		    // gc.fillRectangle(x + offset + p.x, y, w - (offset + p.x),
		    // h);
		}
		return;
	    }
	    if (((text == null) || (text.length() == 0))
		    && ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_CENTER)) {
		int offset = (w - imageSize.x) / 2;
		// System.out.println("w: " + w + " imageSize" + imageSize + "
		// offset: " + offset);
		drawImageVerticalAlign(gc, image, imageAlign, x + offset, y, h);
		gc.fillRectangle(x, y, offset, h);
		gc.fillRectangle(x + offset + imageSize.x, y, w
			- (offset + imageSize.x), h);
		return;
	    }
	    if ((textAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT) {
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_NONE) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x,
			    y, w, h);
		    gc.fillRectangle(x + textSize.x, y, w - textSize.x, h);
		    return;
		}
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x
			    + imageSize.x, y, w - imageSize.x, h);
		    drawImageVerticalAlign(gc, image, imageAlign, x, y, h);
		    gc.fillRectangle(x + textSize.x + imageSize.x, y, w
			    - (textSize.x + imageSize.x), h);
		    return;
		}
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x,
			    y, w - imageSize.x, h);
		    drawImageVerticalAlign(gc, image, imageAlign, x + w
			    - imageSize.x, y, h);
		    gc.fillRectangle(x + textSize.x, y, w
			    - (textSize.x + imageSize.x), h);
		    return;
		}
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT_LEFT) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x,
			    y, w - imageSize.x, h);
		    drawImageVerticalAlign(gc, image, imageAlign, x
			    + textSize.x, y, h);
		    gc.fillRectangle(x + textSize.x + imageSize.x, y, w
			    - (textSize.x + imageSize.x), h);
		    return;
		}
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT_CENTER) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x,
			    y, w - imageSize.x, h);
		    int xOffset = (w - textSize.x - imageSize.x) / 2;
		    drawImageVerticalAlign(gc, image, imageAlign, x
			    + textSize.x + xOffset, y, h);
		    gc.fillRectangle(x + textSize.x, y, xOffset, h);
		    gc.fillRectangle(x + textSize.x + xOffset + imageSize.x, y,
			    w - (textSize.x + xOffset + imageSize.x), h);
		    return;
		}
		throw new SWTException("H: "
			+ (imageAlign & ALIGN_HORIZONTAL_MASK));
	    } // text align left
	    if ((textAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT) {
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_NONE) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x,
			    -1000, w, h);
		    drawTextVerticalAlign(gc, text, textAlign, x + w
			    - textSize.x, y, w, h);
		    gc.fillRectangle(x, y, w - textSize.x, h);
		    return;
		}
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x,
			    -1000, w - imageSize.x, h);
		    drawTextVerticalAlign(gc, text, textAlign, x + w
			    - textSize.x, y, w - imageSize.x, h);
		    drawImageVerticalAlign(gc, image, imageAlign, x, y, h);
		    gc.fillRectangle(x + imageSize.x, y, w
			    - (textSize.x + imageSize.x), h);
		    return;
		}
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT_RIGHT) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x,
			    -1000, w - imageSize.x, h);
		    drawTextVerticalAlign(gc, text, textAlign, x + w
			    - textSize.x, y, w - imageSize.x, h);
		    drawImageVerticalAlign(gc, image, imageAlign, x + w
			    - (textSize.x + imageSize.x), y, h);
		    gc.fillRectangle(x, y, w - (textSize.x + imageSize.x), h);
		    return;
		}
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT_CENTER) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x,
			    -1000, w - imageSize.x, h);
		    drawTextVerticalAlign(gc, text, textAlign, x + w
			    - textSize.x, y, w - imageSize.x, h);
		    int xOffset = (w - textSize.x - imageSize.x) / 2;
		    drawImageVerticalAlign(gc, image, imageAlign, x + xOffset,
			    y, h);
		    gc.fillRectangle(x, y, xOffset, h);
		    gc.fillRectangle(x + xOffset + imageSize.x, y, w
			    - (xOffset + imageSize.x + textSize.x), h);
		    return;
		}
		if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT) {
		    textSize.x = drawTextVerticalAlign(gc, text, textAlign, x,
			    -1000, w - imageSize.x, h);
		    drawTextVerticalAlign(gc, text, textAlign, x + w
			    - (textSize.x + imageSize.x), y, w - imageSize.x, h);
		    drawImageVerticalAlign(gc, image, imageAlign, x + w
			    - imageSize.x, y, h);
		    gc.fillRectangle(x, y, w - (textSize.x + imageSize.x), h);
		    return;
		}
		throw new SWTException("H: "
			+ (imageAlign & ALIGN_HORIZONTAL_MASK));
	    } // text align right
	    throw new SWTException("H: " + (textAlign & ALIGN_HORIZONTAL_MASK));
	} // trye
	finally {
	    // gc.setClipping(oldClipping);
	}
    }

    public static void drawTextImage(GC gc, String text, int textAlign,
	    Image image, int imageAlign, Rectangle r) {
	drawTextImage(gc, text, textAlign, image, imageAlign, r.x, r.y,
		r.width, r.height);
    }

    public static void drawButtonUp(GC gc, String text, int textAlign,
	    Image image, int imageAlign, int x, int y, int w, int h,
	    Color face, Color shadowHigh, Color shadowNormal, Color shadowDark,
	    int leftMargin, int topMargin) {
	Color prevForeground = gc.getForeground();
	Color prevBackground = gc.getBackground();
	try {
	    gc.setBackground(face);
	    gc.setForeground(shadowHigh);
	    gc.drawLine(x, y, x, y + h - 1);
	    gc.drawLine(x, y, x + w - 2, y);
	    gc.setForeground(shadowDark);
	    gc.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
	    gc.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
	    gc.setForeground(shadowNormal);
	    gc.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 2);
	    gc.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
	    //
	    gc.fillRectangle(x + 1, y + 1, leftMargin, h - 3);
	    gc.fillRectangle(x + 1, y + 1, w - 3, topMargin);
	    gc.setForeground(prevForeground);
	    drawTextImage(gc, text, textAlign, image, imageAlign, x + 1
		    + leftMargin, y + 1 + topMargin, w - 3 - leftMargin, h - 3
		    - topMargin);
	} finally {
	    gc.setForeground(prevForeground);
	    gc.setBackground(prevBackground);
	}
    }

    public static void drawButtonUp(GC gc, String text, int textAlign,
	    Image image, int imageAlign, int x, int y, int w, int h, Color face) {
	Display display = Display.getCurrent();
	drawButtonUp(gc, text, textAlign, image, imageAlign, x, y, w, h, face,
		display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW),
		display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW), display
			.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW), 2, 2);
    }

    public static void drawButtonUp(GC gc, String text, int textAlign,
	    Image image, int imageAlign, Rectangle r, int leftMargin,
	    int topMargin) {
	Display display = Display.getCurrent();
	drawButtonUp(gc, text, textAlign, image, imageAlign, r.x, r.y, r.width,
		r.height, display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND),
		display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW),
		display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW), display
			.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW),
		leftMargin, topMargin);
    }

    public static void drawButtonUp(GC gc, String text, int textAlign,
	    Image image, int imageAlign, int x, int y, int w, int h) {
	Display display = Display.getCurrent();
	drawButtonUp(gc, text, textAlign, image, imageAlign, x, y, w, h,
		display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND), display
			.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW),
		display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW), display
			.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW), 2, 2);
    }

    public static void drawButtonUp(GC gc, String text, int textAlign,
	    Image image, int imageAlign, Rectangle r) {
	// Display display = Display.getCurrent();
	drawButtonUp(gc, text, textAlign, image, imageAlign, r.x, r.y, r.width,
		r.height);
    }

    public static void drawButtonDown(GC gc, String text, int textAlign,
	    Image image, int imageAlign, int x, int y, int w, int h,
	    Color face, Color shadowNormal, int leftMargin, int topMargin) {
	Color prevForeground = gc.getForeground();
	Color prevBackground = gc.getBackground();
	try {
	    gc.setBackground(face);
	    gc.setForeground(shadowNormal);
	    gc.drawRectangle(x, y, w - 1, h - 1);
	    gc.fillRectangle(x + 1, y + 1, 1 + leftMargin, h - 2);
	    gc.fillRectangle(x + 1, y + 1, w - 2, topMargin + 1);
	    gc.setForeground(prevForeground);
	    drawTextImage(gc, text, textAlign, image, imageAlign, x + 2
		    + leftMargin, y + 2 + topMargin, w - 3 - leftMargin, h - 3
		    - topMargin);
	} finally {
	    gc.setForeground(prevForeground);
	    gc.setBackground(prevBackground);
	}
    }

    public static void drawButtonDown(GC gc, String text, int textAlign,
	    Image image, int imageAlign, int x, int y, int w, int h) {
	Display display = Display.getCurrent();
	drawButtonDown(gc, text, textAlign, image, imageAlign, x, y, w, h,
		display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND), display
			.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW), 2, 2);
    }

    public static void drawButtonDown(GC gc, String text, int textAlign,
	    Image image, int imageAlign, Rectangle r) {
	drawButtonDown(gc, text, textAlign, image, imageAlign, r.x, r.y,
		r.width, r.height);
    }

    public static void drawButtonDown(GC gc, String text, int textAlign,
	    Image image, int imageAlign, int x, int y, int w, int h, Color face) {
	Display display = Display.getCurrent();
	drawButtonDown(gc, text, textAlign, image, imageAlign, x, y, w, h,
		face, display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
		2, 2);
    }

    public static void drawButtonDeepDown(GC gc, String text, int textAlign,
	    Image image, int imageAlign, int x, int y, int w, int h) {
	Display display = Display.getCurrent();
	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	gc.drawLine(x, y, x + w - 2, y);
	gc.drawLine(x, y, x, y + h - 2);
	gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
	gc.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
	gc.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
	gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	gc.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
	gc.drawLine(x + w - 2, y + h - 2, x + w - 2, y + 1);
	//
	gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
	gc.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	gc.fillRectangle(x + 2, y + 2, w - 4, 1);
	gc.fillRectangle(x + 1, y + 2, 2, h - 4);
	//
	gc.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	drawTextImage(gc, text, textAlign, image, imageAlign, x + 2 + 1,
		y + 2 + 1, w - 4, h - 3 - 1);
    }

    public static void drawButtonDeepDown(GC gc, String text, int textAlign,
	    Image image, int imageAlign, Rectangle r) {
	drawButtonDeepDown(gc, text, textAlign, image, imageAlign, r.x, r.y,
		r.width, r.height);
    }

    public static void drawFlatButtonUp(GC gc, String text, int textAlign,
	    Image image, int imageAlign, int x, int y, int w, int h,
	    Color face, Color shadowLight, Color shadowNormal, int leftMargin,
	    int topMargin) {
	Color prevForeground = gc.getForeground();
	Color prevBackground = gc.getBackground();
	try {
	    gc.setForeground(shadowLight);
	    gc.drawLine(x, y, x + w - 1, y);
	    gc.drawLine(x, y, x, y + h);
	    gc.setForeground(shadowNormal);
	    gc.drawLine(x + w, y, x + w, y + h);
	    gc.drawLine(x + 1, y + h, x + w, y + h);
	    //
	    gc.setBackground(face);
	    gc.fillRectangle(x + 1, y + 1, leftMargin, h - 1);
	    gc.fillRectangle(x + 1, y + 1, w - 1, topMargin);
	    //
	    gc.setBackground(face);
	    gc.setForeground(prevForeground);
	    drawTextImage(gc, text, textAlign, image, imageAlign, x + 1
		    + leftMargin, y + 1 + topMargin, w - 1 - leftMargin, h - 1
		    - topMargin);
	} finally {
	    gc.setForeground(prevForeground);
	    gc.setBackground(prevBackground);
	}
    }

    public static void drawShadowImage(GC gc, Image image, int x, int y,
	    int alpha) {
	Display display = Display.getCurrent();
	Point imageSize = new Point(image.getBounds().width,
		image.getBounds().height);
	//
	ImageData imgData = new ImageData(imageSize.x, imageSize.y, 24,
		new PaletteData(255, 255, 255));
	imgData.alpha = alpha;
	Image img = new Image(display, imgData);
	GC imgGC = new GC(img);
	imgGC.drawImage(image, 0, 0);
	gc.drawImage(img, x, y);
	imgGC.dispose();
	img.dispose();
    }
}
