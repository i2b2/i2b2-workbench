/*
 Copyright (c) 1995, 1996 Connect! Corporation, Inc. All Rights Reserved.
 Source code usage restricted as defined in Connect! Widgets License Agreement
 */
package edu.harvard.i2b2.timeline.external;

import java.awt.*;
import java.util.Vector;
import java.lang.Boolean;

/**
 * BaseTabbedPanelis a Panel extension which provides for a tabbed dialog
 * effect. It provides a the visuall aspect of tabs and allows the programmer to
 * decide what action to take when a tab is activated. Can be used directly or
 * extended. When extending from BaseTabbedPanel be sure to super() during
 * construction and to super.handleEvent(evt) from handleEvent if you override
 * it.
 * 
 * @author Scott Fauerbach
 */
public abstract class BaseTabbedPanel extends Panel {
	/**
	 * Put Tabs on TOP constant.
	 */
	public static final int TOP = 0;

	/**
	 * Put Tabs on BOTTOM constant.
	 */
	public static final int BOTTOM = 1;

	/**
	 * Tabs have ROUNDED style constant.
	 */
	public static final int ROUNDED = 0;

	/**
	 * Tabs have SQUARE style constant.
	 */
	public static final int SQUARE = 1;

	private int TF_LEFT = 9;
	private int TF_RIGHT = -9;
	private int TF_TOP = 30;
	private int TF_BOTTOM = -9;

	private int TF_BTN_HEIGHT = 20;

	private Vector vLabels;
	private Vector vEnabled;
	private Vector vPolys;

	protected int curIndex = -1;

	private Font fReg;
	private Font fSel;

	private Component userPanel = null;

	private int iTabsPosition = TOP;
	private int iTabsStyle = ROUNDED;

	private int osAdjustment;

	private int firstVisibleTab = 0;
	private DirectionButton dbLeft;
	private DirectionButton dbRight;
	private Polygon nullPoly;
	private int lastWidth = -1;

	private Insets btpInsets;

	/**
	 * Constructs a BaseTabbedPanel with tabs on top and rounded
	 */
	public BaseTabbedPanel() {
		this(TOP, ROUNDED);
	}

	/**
	 * Obsolete. Use public BaseTabbedPanel(int tabsPostion, int tabsStyle)
	 * Constructs a BaseTabbedPanel which provides a base tabbed dialog panel.
	 */
	public BaseTabbedPanel(boolean bTabsOnTop) {
		this(bTabsOnTop ? TOP : BOTTOM, bTabsOnTop ? ROUNDED : SQUARE);
	}

	/**
	 * Constructs a BaseTabbedPanel which provides a base tabbed dialog panel.
	 * 
	 * @param tabsPosition
	 *            constant indicating TOP or BOTTOM
	 * @param tabsStyle
	 *            constant indicating ROUNDED or SQUARE
	 */
	public BaseTabbedPanel(int tabsPostion, int tabsStyle) {
		vLabels = new Vector();
		vEnabled = new Vector();
		vPolys = new Vector();
		btpInsets = new Insets(0, 0, 0, 0);

		setTabsInfo(tabsPostion, tabsStyle);

		fReg = new Font("Helvetica", Font.PLAIN, 12);
		fSel = new Font("Helvetica", Font.BOLD, 12);

		if (System.getProperty("os.name").startsWith("S")) // SunOS, Solaris
			osAdjustment = -1;
		else
			osAdjustment = 0;

		super.setLayout(null);

		// prepare left/right arrows
		dbLeft = new DirectionButton(DirectionButton.LEFT);
		dbRight = new DirectionButton(DirectionButton.RIGHT);
		// dbLeft.setShowFocus(false);
		// dbRight.setShowFocus(false);
		dbLeft.shrinkTriangle(1, 1, 0, 1);
		dbRight.shrinkTriangle(1, 1, 0, 1);
		super.add(dbLeft, -1);
		super.add(dbRight, -1);

		nullPoly = new Polygon();
		nullPoly.addPoint(0, 0);
		nullPoly.addPoint(1, 1);
		nullPoly.addPoint(0, 0);
	}

	/**
	 * Sets the position of the tabs
	 * 
	 * @param tabsPosition
	 *            constant indicating TOP or BOTTOM
	 */
	public void setTabsPosition(int tabsPosition) {
		if (iTabsPosition != tabsPosition)
			setTabsInfo(tabsPosition, iTabsStyle);
	}

	public int getTabsPosition() {
		return iTabsPosition;
	}

	/**
	 * Sets the style of the tabs
	 * 
	 * @param tabsStyle
	 *            constant indicating ROUNDED or SQUARE
	 */
	public void setTabsStyle(int tabsStyle) {
		if (iTabsStyle != tabsStyle)
			setTabsInfo(iTabsPosition, tabsStyle);
	}

	public int getTabsStyle() {
		return iTabsStyle;
	}

	/**
	 * Sets the position and style of the tabs
	 * 
	 * @param tabsPosition
	 *            constant indicating TOP or BOTTOM
	 * @param tabsStyle
	 *            constant indicating ROUNDED or SQUARE
	 */
	public void setTabsInfo(int tabsPosition, int tabsStyle) {
		iTabsPosition = tabsPosition;
		if (iTabsPosition == TOP)
			iTabsStyle = ROUNDED;
		else
			iTabsStyle = tabsStyle;

		if (iTabsStyle == ROUNDED)
			TF_BTN_HEIGHT = 20;
		else
			TF_BTN_HEIGHT = 17;

		repaint();
	}

	/**
	 * Sets the panel to be shown by adding it to the base panel. Removes all
	 * other (previous) panels from base panel
	 * 
	 * @param p
	 *            the Panel
	 */
	public void setPanel(Component p) {
		removeAll();
		userPanel = p;
		if (userPanel != null) {
			super.add(userPanel, -1);
			userPanel.requestFocus();
		}
	}

	/**
	 * Sets and shows the panel to be shown by adding it to the base panel if
	 * not already added and showing it. Hides all other (previous) panels,
	 * instead of removing them.
	 * 
	 * @param p
	 *            the Panel
	 */
	public void showPanel(Component p) {
		if (userPanel != null)
			userPanel.hide();

		userPanel = p;
		if (userPanel != null) {
			Component[] comps = getComponents();
			int l = comps.length;
			int x;
			for (x = 0; x < l; x++) {
				if (comps[x] == userPanel)
					break;
			}
			if (x == l)
				super.add(userPanel, -1);

			userPanel.show();
			userPanel.requestFocus();
			validate();
			repaint();
		}
	}

	/**
	 * Adds the next tab label and sets whether it is enabled
	 * 
	 * @param sLabel
	 *            the tab label
	 * @param bEnabled
	 *            enable the tab or not
	 * @return returns the zero relative index of the newly added tab
	 */
	public int addTab(String sLabel, boolean bEnabled) {
		vLabels.addElement(sLabel);
		vEnabled.addElement(new Boolean(bEnabled));

		int index = vLabels.size() - 1;
		if (curIndex == -1 && bEnabled)
			showTab(index);

		return (index);
	}

	/**
	 * Re-sets the tab at the index specified. Allows ability to change tab
	 * label.
	 * 
	 * @param sLabel
	 *            the tab label.
	 * @param bEnabled
	 *            enable the tab or not
	 * @param index
	 *            zero relative index of the tab
	 */
	public synchronized void setTab(String sLabel, boolean bEnabled, int index) {
		if ((index < 0) || (index >= vLabels.size()))
			return;

		if (index == curIndex && !bEnabled)
			return;

		try {
			vLabels.setElementAt(sLabel, index);
			vEnabled.setElementAt(new Boolean(bEnabled), index);
			repaint();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Re-sets the tab label at the index specified.
	 * 
	 * @param sLabel
	 *            the tab label.
	 * @param index
	 *            zero relative index of the tab
	 */
	public synchronized void setLabel(String sLabel, int index) {
		if ((index < 0) || (index >= vLabels.size()))
			return;

		try {
			vLabels.setElementAt(sLabel, index);
			repaint();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	public synchronized String getLabel(int index) {
		if ((index < 0) || (index >= vLabels.size()))
			return "";

		try {
			return (String) vLabels.elementAt(index);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return "";
	}

	/**
	 * Re-enableds/disables the tab at the index specified.
	 * 
	 * @param bEnabled
	 *            enable the tab or not
	 * @param index
	 *            zero relative index of the tab
	 */
	public synchronized void setEnabled(boolean bEnabled, int index) {
		if ((index < 0) || (index >= vLabels.size()))
			return;

		if (index == curIndex && !bEnabled)
			return;

		try {
			vEnabled.setElementAt(new Boolean(bEnabled), index);
			repaint();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Shows/activates tab at index. Tab position must be enabled.
	 * 
	 * @param index
	 *            zero relative index of the tab to show
	 */
	public void showTab(int index) {
		if ((index < 0) || (index >= vLabels.size()) || index == curIndex)
			return;

		if (tabIsEnabled(index)) {
			curIndex = index;
			invalidate();
			validate();
			repaint();
			postEvent(new Event(this, Event.ACTION_EVENT, null));
		}
	}

	/**
	 * Determine whether or not the tab at the index is enabled
	 * 
	 * @param index
	 *            zero relative index of the tab
	 * @return returns true if the tab at the index is enabled
	 */
	public boolean tabIsEnabled(int index) {
		if ((index < 0) || (index >= vLabels.size()))
			return false;

		try {
			Boolean bool = (Boolean) vEnabled.elementAt(index);
			if (bool.booleanValue())
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return false;
	}

	/**
	 * Determine the current shown/active tab
	 * 
	 * @return returns zero relative index of the current shown/active tab
	 */
	public int currentTabIndex() {
		return curIndex;
	}

	/**
	 * Enables (or disables) tab position based on index. The current active tab
	 * cannot be disabled.
	 * 
	 * @param bEnable
	 *            true to enable, false to disable
	 * @param index
	 *            zero relative index of the tab
	 */
	public void enableTab(boolean bEnable, int index) {
		if ((index < 0) || (index >= vEnabled.size()) || index == curIndex)
			return;

		try {
			vEnabled.setElementAt(new Boolean(bEnable), index);
			repaint();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Removes a tab based on index. The current active tab cannot be removed.
	 * 
	 * @param index
	 *            zero relative index of the tab to remove
	 */
	public void removeTab(int index) {
		if ((index < 0) || (index >= vEnabled.size()) || index == curIndex)
			return;

		try {
			vLabels.removeElementAt(index);
			vEnabled.removeElementAt(index);
			repaint();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Removes all tabs.
	 */
	public void removeAllTabs() {
		vLabels = new Vector();
		vEnabled = new Vector();
		vPolys = new Vector();
		curIndex = -1;
		firstVisibleTab = 0;
		lastWidth = -1;
		removeAll();
		repaint();
	}

	@Override
	public void layout() {
		Rectangle r = bounds();

		int width = r.width - TF_LEFT + TF_RIGHT;
		if (width < 0)
			return;

		int height = r.height - TF_TOP + TF_BOTTOM;
		if (height < 0)
			return;

		int col = TF_LEFT;
		int row = 0;

		if (iTabsPosition == TOP)
			row = TF_TOP;
		else
			row = TF_TOP - TF_BTN_HEIGHT;

		if (userPanel != null) {
			userPanel.reshape(col + 3, row + 3, width - 6, height - 5);
			userPanel.invalidate();
			userPanel.validate();
			if (userPanel instanceof Canvas || userPanel instanceof Panel) {
				userPanel.repaint();
			} else {
				/*
				 * userPanel.hide(); userPanel.show();
				 */
				repaint();
			}

		}
	}

	@Override
	public synchronized void paint(Graphics g) {
		Rectangle r = bounds();

		// ----------------------------------------------------------------------
		// ----------
		// paint the box
		// ----------------------------------------------------------------------
		// ----------

		int width = r.width - TF_LEFT + TF_RIGHT;
		if (width < 0)
			return;

		int height = r.height - TF_TOP + TF_BOTTOM;
		if (height < 0)
			return;

		if (r.width > lastWidth)
			firstVisibleTab = 0;
		lastWidth = r.width;

		int col = TF_LEFT;
		int row;

		Color c = g.getColor();
		g.setColor(getBackground());
		g.fillRect(0, 0, r.width, r.height);

		if (iTabsPosition == TOP)
			row = TF_TOP;
		else
			row = TF_TOP - TF_BTN_HEIGHT;

		// ----------------------------------------------------------------------
		// ----------
		// draw border
		// ----------------------------------------------------------------------
		// ----------
		g.setColor(Color.white);
		g.drawLine(col, row, (col + width - 1), row);
		g.drawLine(col, row, col, (row + height - 1));

		g.setColor(Color.gray);
		g.drawLine((col + 2), (row + height - 2), (col + width - 2), (row
				+ height - 2));
		g.drawLine((col + width - 2), (row + 2), (col + width - 2), (row
				+ height - 2));

		g.setColor(Color.black);
		g.drawLine((col + 1), (row + height - 1), (col + width - 1), (row
				+ height - 1));
		g.drawLine((col + width - 1), (row + 1), (col + width - 1), (row
				+ height - 1));

		// ----------------------------------------------------------------------
		// ----------
		// paint the tabs, and record areas
		// ----------------------------------------------------------------------
		// ----------
		int x1;
		int x2 = TF_LEFT + 8;
		int y1;
		int y2;
		int x3 = 0;
		int x4 = TF_LEFT;

		int sze = vLabels.size();
		String sLabel;
		vPolys.removeAllElements();

		Font f = g.getFont();
		FontMetrics fm = getFontMetrics(fReg);
		FontMetrics fms = getFontMetrics(fSel);
		int labelWidth = 0;
		Polygon p;

		int w;
		// make sure there is a polygon for each tab
		for (w = 0; w < firstVisibleTab; w++) {
			vPolys.addElement(nullPoly);
		}
		if (w > 0)
			x4 += 2;
		for (; w < sze; w++) {
			p = new Polygon();
			try {
				sLabel = (String) vLabels.elementAt(w);
				if (w == curIndex)
					labelWidth = fms.stringWidth(sLabel);
				else
					labelWidth = fm.stringWidth(sLabel);

				if (iTabsPosition == TOP) {
					y1 = TF_TOP - TF_BTN_HEIGHT;
					y2 = TF_TOP - 1;
				} else {
					y1 = r.height + TF_BOTTOM + 1;
					y2 = r.height + TF_BOTTOM - TF_BTN_HEIGHT;
				}

				if (iTabsStyle == ROUNDED) {
					x1 = x4 + 2;
					x2 = x1 + labelWidth + 13;
				} else {
					x1 = x2 - 7;
					x2 = x1 + labelWidth + 28;
				}

				// check to see if this tab would draw too far
				if ((x2 + 36 - TF_RIGHT) > r.width)
					break;

				// draw the outside edge of the tab
				if (iTabsPosition == TOP) {
					// if current tab, it extends further
					if (w == curIndex) {
						y1 -= 3;
						x1 -= 2;
					}
					g.setColor(Color.white);
					if (curIndex == (w + 1))
						g.drawLine(x1 + 2, y1, x2 - 2, y1);
					else
						g.drawLine(x1 + 2, y1, x2, y1);

					// draw the border between tabs if not covered by the
					// current one
					if (curIndex != (w - 1)) {
						g.drawLine(x1, y1 + 2, x1, y2);
						x3 = x1;
					} else
						x3 = x1 + 1;

					g.drawLine(x1 + 1, y1 + 1, x1 + 1, y1 + 1);

					if (curIndex != (w + 1)) {
						g.setColor(Color.gray);
						g.drawLine(x2, y1, x2, y2);
						g.setColor(Color.black);
						g.drawLine(x2 + 1, y1 + 2, x2 + 1, y2);
						x4 = x2;
					} else
						x4 = x2 - 1;
				} else {
					if (iTabsStyle == SQUARE) {
						g.setColor(Color.gray);
						g.drawLine(x1 + 9, y1, x2 - 9, y1);

						g.setColor(Color.black);
						// left \ slanted line
						if (w == 0 || w == curIndex) {
							g.drawLine(x1, y2, x1 + 9, y1);
							p.addPoint(x1, y2);
						} else {
							g.drawLine(x1 + 4, y1 - 9, x1 + 9, y1);
							p.addPoint(x1 + 9, y2);
							p.addPoint(x1 + 4, y1 - 9);
						}
						p.addPoint(x1 + 9, y1);
						p.addPoint(x2 - 9, y1);

						if ((w + 1) == curIndex) {
							g.drawLine(x2 - 5, y1 - 9, x2 - 9, y1);
							p.addPoint(x2 - 5, y1);
							p.addPoint(x2 - 9, y2);
						} else {
							g.drawLine(x2, y2, x2 - 9, y1);
							p.addPoint(x2, y2);
						}

						if (w == 1 || w == curIndex)
							p.addPoint(x1, y2);
						else
							p.addPoint(x1 + 9, y2);
					} else {
						// if current tab, it extends further
						if (w == curIndex) {
							y1 += 3;
							x1 -= 2;
						}
						g.setColor(Color.white);
						if (curIndex == (w + 1))
							g.drawLine(x1 + 2, y1, x2 - 2, y1);
						else
							g.drawLine(x1 + 2, y1, x2, y1);

						// draw the border between tabs if not covered by the
						// current one
						if (curIndex != (w - 1)) {
							g.drawLine(x1, y1 - 2, x1, y2);
							x3 = x1;
						} else
							x3 = x1 + 1;

						g.drawLine(x1 + 1, y1 - 1, x1 + 1, y1 - 1);

						if (curIndex != (w + 1)) {
							g.setColor(Color.gray);
							g.drawLine(x2, y1, x2, y2);
							g.setColor(Color.black);
							g.drawLine(x2 + 1, y1 - 2, x2 + 1, y2);
							x4 = x2;
						} else
							x4 = x2 - 1;
					}
				}

				// draw the inside edge of the tab
				if (w == curIndex) {
					if (iTabsPosition == TOP)
						y2++;
					else
						y2--;
					g.setColor(getBackground());
					g.drawLine(x1 + 1, y2, x2, y2);
					if (iTabsPosition == BOTTOM)
						g.drawLine(x1 + 1, y2 - 1, x2, y2 - 1);

					g.setFont(fSel);
				} else
					g.setFont(fReg);

				// if (iTabsPosition == TOP)
				if (iTabsStyle == ROUNDED) {
					p.addPoint(x3, y2);
					p.addPoint(x4, y2);
					p.addPoint(x4, y1);
					p.addPoint(x3, y1);
					p.addPoint(x3, y2);
				}
				vPolys.addElement(p);

				Boolean bool = (Boolean) vEnabled.elementAt(w);
				if (bool.booleanValue())
					g.setColor(Color.black);
				else
					g.setColor(Color.gray);

				if (iTabsPosition == TOP)
					g.drawString(sLabel, x1 + 8, y1 + 15 + osAdjustment);
				else {
					if (iTabsStyle == ROUNDED)
						g.drawString(sLabel, x1 + 8, y1 - 6 + osAdjustment);
					else
						g.drawString(sLabel, x1 + 14, y1 - 4 + osAdjustment);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}

		// do I need to show arrows because there are too many tabs???
		if ((firstVisibleTab > 0) || (w < sze)) {
			dbLeft.show();
			dbRight.show();
			if (firstVisibleTab > 0)
				dbLeft.enable();
			else
				dbLeft.disable();

			if (w < sze)
				dbRight.enable();
			else
				dbRight.disable();

			if (iTabsPosition == TOP) {
				dbLeft.reshape(r.width - 33 + TF_RIGHT, TF_TOP - 16, 16, 15);
				dbRight.reshape(r.width - 16 + TF_RIGHT, TF_TOP - 16, 16, 15);
			} else {
				dbLeft.reshape(r.width - 33 + TF_RIGHT, r.height + TF_BOTTOM
						- TF_BTN_HEIGHT, 16, 15);
				dbRight.reshape(r.width - 16 + TF_RIGHT, r.height + TF_BOTTOM
						- TF_BTN_HEIGHT, 16, 15);
			}
		} else {
			dbLeft.hide();
			dbRight.hide();
		}

		// make sure there is a polygon for each tab
		for (; w < sze; w++) {
			vPolys.addElement(nullPoly);
		}

		g.setFont(f);
		g.setColor(c);
	}

	@Override
	public boolean handleEvent(Event evt) {
		switch (evt.id) {
		case Event.MOUSE_DOWN:
			int sizeR = vPolys.size();
			Polygon p;
			for (int x = 0; x < sizeR; x++) {
				try {
					p = (Polygon) vPolys.elementAt(x);
					if ((p != nullPoly) && p.inside(evt.x, evt.y)) {
						showTab(x);
						return true;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
			break;

		case Event.ACTION_EVENT:
			if (evt.target == dbLeft) {
				if (--firstVisibleTab < 0)
					firstVisibleTab = 0;
				else
					repaint();
				return true;
			} else if (evt.target == dbRight) {
				int sze = vLabels.size();
				if (++firstVisibleTab == sze)
					firstVisibleTab--;
				else
					repaint();
				return true;
			}
			break;
		}
		return super.handleEvent(evt);
	}

	// ===========================================================
	// Component functions overridden so user cannot change the
	// way this container should work
	// ===========================================================
	@Override
	public Component add(Component comp) {
		return comp;
	}

	@Override
	public synchronized Component add(Component comp, int pos) {
		return comp;
	}

	@Override
	public synchronized Component add(String name, Component comp) {
		return comp;
	}

	@Override
	public synchronized void remove(Component comp) {
		if (comp == dbLeft || comp == dbRight)
			return;
		super.remove(comp);
		if (comp == userPanel)
			userPanel = null;
	}

	@Override
	public synchronized void removeAll() {
		super.removeAll();
		super.add(dbLeft, -1);
		super.add(dbRight, -1);
		userPanel = null;
	}

	@Override
	public void setLayout(LayoutManager mgr) {
	}

	@Override
	public Insets insets() {
		btpInsets = super.insets();
		btpInsets.left += (TF_LEFT + 3);
		btpInsets.right += (6 - TF_RIGHT);

		if (iTabsPosition == TOP) {
			btpInsets.top += (TF_TOP + 3);
			btpInsets.bottom += (5 - TF_BOTTOM);
		} else {
			btpInsets.top += TF_TOP - TF_BTN_HEIGHT + 3;
			btpInsets.bottom += (TF_BTN_HEIGHT + 5 - TF_BOTTOM);
		}

		return btpInsets;
	}

	@Override
	public Dimension preferredSize() {
		Dimension s = size();
		Dimension m = minimumSize();
		return new Dimension(Math.max(s.width, m.width), Math.max(s.height,
				m.height));
	}

	@Override
	public Dimension minimumSize() {
		if (userPanel != null) {
			Dimension s = userPanel.minimumSize();
			return new Dimension((s.width + btpInsets.left + btpInsets.right),
					(s.height + btpInsets.top + btpInsets.bottom));
		}
		return new Dimension(100, 100);
	}

	// ===========================================================
	// Done Component functions overridden
	// ===========================================================
}
