/*
 Copyright (c) 1995, 1996 Connect! Corporation, Inc. All Rights Reserved.
 Source code usage restricted as defined in Connect! Widgets License Agreement
 */
package edu.harvard.i2b2.timeline.external;

import java.awt.*;
import java.util.Vector;

/**
 * TabPanel is a Panel extension which provides for a tabbed dialog effect. It
 * will automatically manage swapping panels when a tab is shown/activated. It
 * can be used directly or extended. When extending from TabPanel be sure to
 * super() during construction and to super.handleEvent(evt) from handleEvent if
 * you override it.
 * 
 * @author Scott Fauerbach
 */
public class TabPanel extends BaseTabbedPanel {
    Vector vPanels;

    String[] labels = null;

    boolean bOsHack;

    /**
     * Constructs a TabPanel with tabs on top, rounded
     */
    public TabPanel() {
	this(TOP, ROUNDED);
    }

    /**
     * Obsolete. Use public TabPanel(int tabsPostion, int tabsStyle) Constructs
     * a TabPanel.
     */
    public TabPanel(boolean bTabsOnTop) {
	this(bTabsOnTop ? TOP : BOTTOM, bTabsOnTop ? ROUNDED : SQUARE);
    }

    /**
     * Constructs a TabPanel.
     * 
     * @param tabsPosition
     *            constant indicating TOP or BOTTOM
     * @param tabsStyle
     *            constant indicating ROUNDED or SQUARE
     */
    public TabPanel(int tabsPostion, int tabsStyle) {
	super(tabsPostion, tabsStyle);
	vPanels = new Vector();
	String sOS = System.getProperty("os.name");
	if (sOS.equals("Windows 95"))
	    bOsHack = true;
	else
	    bOsHack = false;
    }

    /**
     * Adds a user panel which will be shown when a tab is activated.
     * 
     * @param sLabel
     *            the tab label
     * @param bEnabled
     *            enable the tab or not
     * @param panel
     *            the panel
     * @return returns the zero relative index of the newly added tab panel
     */
    public int addTabPanel(String sLabel, boolean bEnabled, Component panel) {
	vPanels.addElement(panel);

	return addTab(sLabel, bEnabled);
    }

    public int getCurrentPanelNdx() {
	return curIndex;
    }

    public void setCurrentPanelNdx(int index) {
	showTabPanel(index);

	// If we aren't designing, set current index even if the panel hasn't
	// been
	// added yet (we'll switch to it in add())
	// if (! symantec.beans.Beans.isDesignTime())
	curIndex = index;
    }

    @Override
    public Component add(Component comp) {
	return add(comp, -1);
    }

    private String createDefaultLabel(int i) {
	String name = "tab - ";
	name += String.valueOf(i);
	return name;
    }

    @Override
    public synchronized Component add(Component comp, int pos) {
	int newIndex = addTabPanel(createDefaultLabel(vPanels.size()), true,
		comp);

	// If this is the panel that we've set to be the default, or we're
	// designing,
	// go ahead and switch to the new panel.
	if (newIndex == curIndex || false) //symantec.beans.Beans.isDesignTime()
					   // )
	    showTabPanel(newIndex);
	updatePanelLabels();
	return comp;
    }

    @Override
    public synchronized Component add(String name, Component comp) {
	return comp;
    }

    @Override
    public synchronized void remove(Component comp) {
	int i = getPanelTabIndex(comp);

	if (countTabs() == 1)
	    removeAllTabPanels();
	else {
	    if (i == 0)
		showTabPanel(1);
	    else
		showTabPanel(i - 1);
	    removeTabPanel(i);
	}
    }

    public void setPanelLabels(String[] sLabels) {
	labels = sLabels;
	updatePanelLabels();
    }

    public String[] getPanelLabels() {
	return labels;
    }

    public void updatePanelLabels() {
	try {
	    for (int i = 0; i < vPanels.size(); i++) {
		String newlabel;
		if (labels != null) {
		    try {
			newlabel = labels[i];
		    } catch (ArrayIndexOutOfBoundsException e) {
			newlabel = createDefaultLabel(i);
		    }
		} else
		    newlabel = createDefaultLabel(i);
		setLabel(newlabel, i);
	    }
	} catch (Throwable thr) {
	}
    }

    public void setTabsOnBottom(boolean bTabsOnBottom) {
	setTabsInfo(bTabsOnBottom ? BOTTOM : TOP, bTabsOnBottom ? SQUARE
		: ROUNDED);
	layout();
    }

    public boolean getTabsOnBottom() {
	return getTabsPosition() == TOP ? false : true;
    }

    /**
     * Replaces the user panel at the index specified. Allows ability to change
     * tab label and/or tab panel. If it is desired to only change the label,
     * use the base class BaseTabPanel's function setTab(String sLabel, boolean
     * bEnabled, int index)
     * 
     * @param sLabel
     *            the tab label
     * @param bEnabled
     *            enable the tab or not
     * @param panel
     *            the panel
     */
    public synchronized void setTabPanel(String sLabel, boolean bEnabled,
	    Component panel, int index) {
	if ((index < 0) || (index >= vPanels.size()))
	    return;

	if (index == currentTabIndex() && !bEnabled)
	    return;

	try {
	    vPanels.setElementAt(panel, index);
	    setTab(sLabel, bEnabled, index);
	} catch (ArrayIndexOutOfBoundsException e) {
	}
    }

    /**
     * Get Panel for tab at index
     * 
     * @param index
     *            zero relative index of the tab to show
     * @return returns the Panel at the zero relative index
     */
    public synchronized Component getTabPanel(int index) {
	if ((index < 0) || (index >= vPanels.size()))
	    return null;

	Component p = null;
	try {
	    p = (Component) vPanels.elementAt(index);
	} catch (ArrayIndexOutOfBoundsException e) {
	}

	return p;
    }

    /**
     * Get index for a specific Panel
     * 
     * @param index
     *            zero relative index of the tab to show
     * @return returns the zero relative index of the panel or -1 if it is not
     *         found
     */
    public synchronized int getPanelTabIndex(Component panel) {
	return vPanels.indexOf(panel);
    }

    /**
     * Shows/activates tab and tab panel at index. Tab position must be enabled.
     * 
     * @param index
     *            zero relative index of the tab to show
     */
    public synchronized void showTabPanel(int index) {
	if (tabIsEnabled(index)) {
	    try {
		Component p = (Component) vPanels.elementAt(index);
		showTab(index);
		// if (bOsHack && p != null)
		// {
		// p.hide();
		// setPanel(p);
		// p.show();
		// }
		// else
		showPanel(p);
	    } catch (ArrayIndexOutOfBoundsException e) {
	    }
	}
    }

    /**
     * Enables (or disables) tab and tab panel based on index. The current
     * active tab cannot be disabled.
     * 
     * @param bEnable
     *            true to enable, false to disable
     * @param index
     *            zero relative index of the tab
     */
    public synchronized void enableTabPanel(boolean bEnable, int index) {
	if ((index < 0) || (index >= vPanels.size()) || index == curIndex)
	    return;

	enableTab(bEnable, index);
    }

    /**
     * Removes tab and tab panel based on index. The current active tab cannot
     * be removed.
     * 
     * @param index
     *            zero relative index of the tab
     */
    public synchronized void removeTabPanel(int index) {
	if ((index < 0) || (index >= vPanels.size()) || index == curIndex)
	    return;

	try {
	    Component p = (Component) vPanels.elementAt(index);
	    super.remove(p);
	    vPanels.removeElementAt(index);
	} catch (ArrayIndexOutOfBoundsException e) {
	}

	removeTab(index);
    }

    /**
     * Removes all tab panels
     */
    public synchronized void removeAllTabPanels() {
	vPanels = new Vector();
	curIndex = -1;
	removeAllTabs();
    }

    /**
     * Get the number of tabs in the TabPanel
     * 
     * @return the number of tabs in the TabPanel
     */
    public int countTabs() {
	return vPanels.size();
    }

    @Override
    public boolean handleEvent(Event evt) {
	switch (evt.id) {
	case Event.ACTION_EVENT:
	    if (evt.target instanceof TabPanel) {
		if (evt.target == this)
		    showTabPanel(currentTabIndex());
	    }
	    break;
	}
	return super.handleEvent(evt);
    }

    @Override
    public Dimension preferredSize() {
	Component pan = null;
	Dimension d = null;
	Dimension p = size();
	int s = vPanels.size();

	Insets insets = insets();
	p.width -= (insets.left + insets.right);
	p.height -= (insets.top + insets.bottom);

	if (p.width < 0)
	    p.width = 0;

	if (p.height < 0)
	    p.height = 0;

	for (int x = 0; x < s; x++) {
	    pan = (Component) vPanels.elementAt(x);
	    if (pan != null) {
		d = pan.minimumSize();
		if (d.width > p.width)
		    p.width = d.width;
		if (d.height > p.height)
		    p.height = d.height;
		d = pan.preferredSize();
		if (d.width > p.width)
		    p.width = d.width;
		if (d.height > p.height)
		    p.height = d.height;
	    }
	}

	p.width += (insets.left + insets.right);
	p.height += (insets.top + insets.bottom);

	return p;
    }

    @Override
    public Dimension minimumSize() {
	Component pan = null;
	Dimension d = null;
	Dimension m = new Dimension(0, 0);
	int s = vPanels.size();

	for (int x = 0; x < s; x++) {
	    pan = (Component) vPanels.elementAt(x);
	    if (pan != null) {
		d = pan.minimumSize();
		if (d.width > m.width)
		    m.width = d.width;
		if (d.height > m.height)
		    m.height = d.height;
	    }
	}

	Insets insets = insets();
	m.width += (insets.left + insets.right);
	m.height += (insets.top + insets.bottom);

	return m;
    }
}
