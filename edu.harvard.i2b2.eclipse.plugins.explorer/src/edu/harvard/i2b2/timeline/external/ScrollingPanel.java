package edu.harvard.i2b2.timeline.external;

import java.awt.Component;
import java.awt.Scrollbar;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.LayoutManager;

//import symantec.itools.awt.shape.Rect;
//import symantec.itools.awt.KeyPressManagerPanel;

/**
 * This class forms a ScrollingPanel component.
 * 
 * @version 1.0, Nov 26, 1996
 * @author Symantec
 */

public class ScrollingPanel extends KeyPressManagerPanel {
	private Component spComponent;
	private int width;
	private int height;
	private int xCoord, yCoord;
	private Scrollbar VBar = null;
	private Scrollbar HBar = null;
	private boolean bVBarVisible;
	private boolean bHBarVisible;
	private boolean bCornerRectVisible;
	private boolean bOsFlag;
	private int vPageSize;
	private int hPageSize;
	private Dimension dimComponent;
	private int vGapWid;
	private int hGapHt;
	private boolean bAllowShowVBar = true;
	private boolean bAllowShowHBar = true;
	private int scrollLineIncrement = 1;
	private Rect cornerRect;

	// --------------------------------------------------
	// constructors
	// --------------------------------------------------

	/**
	 * Constructs a default ScrollingPanel. The panel is initialized with a null
	 * component, zero minimum height and zero minimum width.
	 */
	public ScrollingPanel() {
		this(null, 0, 0);
	}

	/**
	 * Constructs a new ScrollingPanel initialized with the specified component,
	 * minimum height and minimum width.
	 * 
	 * @param component
	 *            the component (usually a Panel) to be scrolled
	 * @param minWidth
	 *            value to be used for the minimumSize() width of the
	 *            ScrollingPanel
	 * @param minHeight
	 *            value to be used for the minimumSize() height of the
	 *            ScrollingPanel
	 */
	public ScrollingPanel(Component component, int minWidth, int minHeight) {
		bOsFlag = System.getProperty("os.name").startsWith("S"); // SunOS,
		// Solaris
		this.spComponent = component;
		this.width = minWidth;
		this.height = minHeight;
		xCoord = 0;
		yCoord = 0;
		bVBarVisible = false;
		bHBarVisible = false;
		bCornerRectVisible = false;
		vPageSize = 0;
		hPageSize = 0;
		vGapWid = 6;
		hGapHt = 6;
		dimComponent = new Dimension(0, 0);

		VBar = new Scrollbar();
		VBar.setBackground(Color.lightGray);

		HBar = new Scrollbar(Scrollbar.HORIZONTAL);
		HBar.setBackground(Color.lightGray);

		cornerRect = new Rect();
		cornerRect.setForeground(Color.lightGray);
		cornerRect.setFillColor(Color.lightGray);
		cornerRect.setFillMode(true);

		setLayout(null);
		super.add(VBar, -1);
		super.add(HBar, -1);
		super.add(cornerRect, -1);

		VBar.hide();
		HBar.hide();
		cornerRect.hide();

		if (spComponent != null) {
			super.add(spComponent, -1);
			placeComponents();
		}
	}

	/**
	 * Set the value to be used for the minimumSize() width of the
	 * ScrollingPanel
	 * 
	 * @param minWidth
	 *            value to be used for the minimumSize() width of the
	 *            ScrollingPanel
	 * @see #getMinimumWidth
	 */
	public void setMinimumWidth(int minWidth) {
		this.width = minWidth;
	}

	/**
	 * Get the current value used for the minimumSize() width of the
	 * ScrollingPanel
	 * 
	 * @return int - current width value
	 */
	public int getMinimumWidth() {
		return this.width;
	}

	/**
	 * Set the value to be used for the minimumSize() height of the
	 * ScrollingPanel
	 * 
	 * @param minHeight
	 *            value to be used for the minimumSize() height of the
	 *            ScrollingPanel
	 * @see #getMinimumHeight
	 */
	public void setMinimumHeight(int minHeight) {
		this.height = minHeight;
	}

	/**
	 * Get the value used for the minimumSize() height of the ScrollingPanel
	 * 
	 * @return int - current height value
	 * @see #setMinimumHeight
	 */
	public int getMinimumHeight() {
		return this.height;
	}

	/**
	 * Set the vertical gap amount between the container in ScrollingPanel and
	 * the vertical scroll bar.
	 * 
	 * @param gapPixels
	 *            size of vertical gap in pixels
	 * @see #getVerticalGap
	 */
	public void setVerticalGap(int gapPixels) {
		vGapWid = gapPixels;
		invalidate();
	}

	/**
	 * Get the current vertical gap amount between the container in
	 * ScrollingPanel and the vertical scroll bar.
	 * 
	 * @return int - size of vertical gap in pixels
	 * @see #setVerticalGap
	 */
	public int getVerticalGap() {
		return vGapWid;
	}

	/**
	 * Set the horizontal gap amount between the container in ScrollingPanel and
	 * the horizontal scroll bar.
	 * 
	 * @param gapPixels
	 *            size of horizontal gap in pixels
	 * @see #getHorizontalGap
	 */
	public void setHorizontalGap(int gapPixels) {
		hGapHt = gapPixels;
		invalidate();
	}

	/**
	 * Get the current horizontal gap amount between the container in
	 * ScrollingPanel and horizontal scroll bar.
	 * 
	 * @return int - size of horizontal gap in pixels
	 * @see #setHorizontalGap
	 */
	public int getHorizontalGap() {
		return hGapHt;
	}

	/**
	 * Set whether or not the vertical scrollbar should be made visible when
	 * necessary or should never be made visible.
	 * 
	 * @param cond
	 *            if true, show the scrollbar when necessary; if false, never
	 *            show the scrollbar
	 * @see #getShowVerticalScroll
	 */
	public void setShowVerticalScroll(boolean cond) {
		if (bAllowShowVBar != cond) {
			bAllowShowVBar = cond;
			invalidate();
		}
	}

	/**
	 * Get the current vertical scrollbar visibility flag.
	 * 
	 * @return boolean - if true, show the scrollbar when necessary; if false,
	 *         never show the scrollbar
	 * @see #setShowVerticalScroll
	 */
	public boolean getShowVerticalScroll() {
		return bAllowShowVBar;
	}

	/**
	 * Set whether or not the horizontal scrollbar should be made visible when
	 * necessary or should never be made visible.
	 * 
	 * @param cond
	 *            if true, show the scrollbar when necessary; if false, never
	 *            show the scrollbar
	 * @see #getShowHorizontalScroll
	 */
	public void setShowHorizontalScroll(boolean cond) {
		if (bAllowShowHBar != cond) {
			bAllowShowHBar = cond;
			invalidate();
		}
	}

	/**
	 * Get the current horizontal scrollbar visibility flag.
	 * 
	 * @return boolean - if true, show the scrollbar when necessary; if false,
	 *         never show the scrollbar
	 * @see #setShowHorizontalScroll
	 */
	public boolean getShowHorizontalScroll() {
		return bAllowShowHBar;
	}

	/**
	 * Set the pixel increment to scroll for every scrollbar arrow press.
	 * 
	 * @param scrollLineIncrement
	 *            the pixel value to scroll, default is one pixel
	 * @see #getScrollLineIncrement
	 */
	public void setScrollLineIncrement(int scrollLineIncrement) {
		this.scrollLineIncrement = scrollLineIncrement;
	}

	/**
	 * Get the current pixel scroll increment.
	 * 
	 * @return the current pixel value amount to scroll
	 * @see #setScrollLineIncrement
	 */
	public int getScrollLineIncrement() {
		return scrollLineIncrement;
	}

	/**
	 * Set the component in the ScrollingPanel.
	 * 
	 * @param comp
	 *            the new component to be in the ScrollingPanel
	 * @see #getComponent
	 */
	public void setComponent(Component comp) {
		if (this.spComponent != null) {
			super.remove(this.spComponent);
		}

		this.spComponent = comp;
		super.add(spComponent, -1);
		invalidate();
	}

	/**
	 * Get the current component in the ScrollingPanel
	 * 
	 * @return the current component in the ScrollingPanel
	 * @see #setComponent
	 */
	public Component getComponent() {
		return this.spComponent;
	}

	@Override
	public boolean handleEvent(Event evt) {
		switch (evt.id) {
		case Event.SCROLL_LINE_UP: {
			if (evt.target == HBar) {
				scrollLeft();
			} else {
				scrollUp();
			}

			return true;
		}

		case Event.SCROLL_LINE_DOWN: {
			if (evt.target == HBar) {
				scrollRight();
			} else {
				scrollDown();
			}

			return true;
		}

		case Event.SCROLL_PAGE_UP: {
			if (evt.target == HBar) {
				scrollPageLeft();
			} else {
				scrollPageUp();
			}

			return true;
		}

		case Event.SCROLL_PAGE_DOWN: {
			if (evt.target == HBar) {
				scrollPageRight();
			} else {
				scrollPageDown();
			}

			return true;
		}

		case Event.SCROLL_ABSOLUTE: {
			if (evt.target == HBar) {
				scrollHorizontalAbsolute(((Integer) evt.arg).intValue());
			} else {
				scrollVerticalAbsolute(((Integer) evt.arg).intValue());
			}

			return true;
		}
		}

		return super.handleEvent(evt);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		if (spComponent == null) {
			return;
		}

		placeComponents();
	}

	/**
	 * Scroll one pixel up.
	 * 
	 * @see #scrollDown
	 * @see #scrollLeft
	 * @see #scrollRight
	 */
	public void scrollUp() {
		yCoord += scrollLineIncrement;

		if (yCoord > 0) {
			yCoord = 0;
		}

		VBar.setValue(-yCoord);
		repaint();
	}

	/**
	 * Scroll one pixel left.
	 * 
	 * @see #scrollRight
	 * @see #scrollUp
	 * @see #scrollDown
	 */
	public void scrollLeft() {
		xCoord += scrollLineIncrement;

		if (xCoord > 0) {
			xCoord = 0;
		}

		HBar.setValue(-xCoord);
		repaint();
	}

	/**
	 * Scroll one pixel down.
	 * 
	 * @see #scrollUp
	 * @see #scrollLeft
	 * @see #scrollRight
	 */
	public void scrollDown() {
		yCoord -= scrollLineIncrement;

		if ((-yCoord) > VBar.getMaximum()) {
			yCoord = -VBar.getMaximum();
		}

		VBar.setValue(-yCoord);
		repaint();
	}

	/**
	 * Scroll one pixel right.
	 * 
	 * @see #scrollLeft
	 * @see #scrollUp
	 * @see #scrollDown
	 */
	public void scrollRight() {
		xCoord -= scrollLineIncrement;

		if ((-xCoord) > HBar.getMaximum()) {
			xCoord = -HBar.getMaximum();
		}

		HBar.setValue(-xCoord);
		repaint();
	}

	/**
	 * Scroll one "page" up.
	 * 
	 * @see #scrollPageDown
	 * @see #scrollPageLeft
	 * @see #scrollPageRight
	 */
	public void scrollPageUp() {
		yCoord += vPageSize;

		if (yCoord > 0) {
			yCoord = 0;
		}

		VBar.setValue(-yCoord);
		repaint();
	}

	/**
	 * Scroll one "page" left.
	 * 
	 * @see #scrollPageRight
	 * @see #scrollPageUp
	 * @see #scrollPageDown
	 */
	public void scrollPageLeft() {
		xCoord += hPageSize;

		if (xCoord > 0) {
			xCoord = 0;
		}

		HBar.setValue(-xCoord);
		repaint();
	}

	/**
	 * Scroll one "page" down.
	 * 
	 * @see #scrollPageUp
	 * @see #scrollPageLeft
	 * @see #scrollPageRight
	 */
	public void scrollPageDown() {
		yCoord -= vPageSize;

		if ((-yCoord) > VBar.getMaximum()) {
			yCoord = -VBar.getMaximum();
		}

		VBar.setValue(-yCoord);
		repaint();
	}

	/**
	 * Scroll one "page" right.
	 * 
	 * @see #scrollPageLeft
	 * @see #scrollPageUp
	 * @see #scrollPageDown
	 */
	public void scrollPageRight() {
		xCoord -= hPageSize;

		if ((-xCoord) > HBar.getMaximum()) {
			xCoord = -HBar.getMaximum();
		}

		HBar.setValue(-xCoord);
		repaint();
	}

	/**
	 * Scroll to an absolute vertical position.
	 * 
	 * @param int the pixel position to scroll to
	 * @see #scrollHorizontalAbsolute
	 */
	public void scrollVerticalAbsolute(int position) {
		yCoord = -position;

		if (yCoord > 0) {
			yCoord = 0;
		} else if ((-yCoord) > VBar.getMaximum()) {
			yCoord = -VBar.getMaximum();
		}

		VBar.setValue(-yCoord);
		repaint();
	}

	/**
	 * Scroll to an absolute horizontal position.
	 * 
	 * @param int the pixel position to scroll to
	 * @see #scrollVerticalAbsolute
	 */
	public void scrollHorizontalAbsolute(int position) {
		xCoord = -position;

		if (xCoord > 0) {
			xCoord = 0;
		} else if ((-xCoord) > HBar.getMaximum()) {
			xCoord = -HBar.getMaximum();
		}

		HBar.setValue(-xCoord);
		repaint();
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
		return new Dimension(width, height);
	}

	@Override
	public Component add(Component comp) {

		if (this.spComponent != null) {
			super.remove(this.spComponent);
		}

		this.spComponent = comp;
		super.add(spComponent, -1);
		repaint();

		return comp;
	}

	@Override
	public synchronized Component add(Component comp, int pos) {
		return add(comp);
	}

	@Override
	public synchronized Component add(String name, Component comp) {
		return add(comp);
	}

	@Override
	public synchronized void remove(Component comp) {
		if (comp == VBar || comp == HBar) {
			return;
		}

		super.remove(comp);

		if (comp == spComponent) {
			spComponent = null;
		}
	}

	@Override
	public synchronized void removeAll() {
		super.removeAll();
		super.add(VBar, -1);
		super.add(HBar, -1);
		super.add(cornerRect, -1);
		spComponent = null;
	}

	@Override
	public void setLayout(LayoutManager mgr) {
	}

	@Override
	public synchronized void reshape(int x, int y, int width, int height) {
		repaint();

		super.reshape(x, y, width, height);
	}

	void placeComponents() {
		boolean bShowV, bShowH;
		int barSize = 0;
		int vWid = 0;
		int hHt = 0;
		dimComponent = spComponent.size();
		Rectangle rect = bounds();

		barSize = bOsFlag ? 17 : 15;

		if (bAllowShowHBar && dimComponent.width > rect.width) {
			bShowH = true;
			hHt = barSize;
		} else {
			bShowH = false;
			hHt = 0;
		}

		if (bAllowShowVBar && dimComponent.height > (rect.height - hHt)) {
			bShowV = true;
			vWid = barSize;

			if (!bShowH) {
				if (dimComponent.width > (rect.width - vWid)) {
					bShowH = true;
					hHt = barSize;
				}
			}
		} else {
			bShowV = false;
			vWid = 0;
		}

		hPageSize = rect.width - vWid - vGapWid;
		vPageSize = rect.height - hHt - hGapHt;

		if (bShowV) {
			VBar.reshape(rect.width - barSize, 0, barSize, rect.height - hHt);
			VBar.setValues(-yCoord, vPageSize, 0, dimComponent.height
					- vPageSize);
			VBar.setPageIncrement(vPageSize);

			if (!bVBarVisible) {
				bVBarVisible = true;
				VBar.show();
			}
		} else {
			if (bVBarVisible) {
				bVBarVisible = false;
				VBar.hide();
			}
			yCoord = 0;
		}

		if (bShowH) {
			HBar.reshape(0, rect.height - barSize, rect.width - vWid, barSize);
			HBar.setValues(-xCoord, hPageSize, 0, dimComponent.width
					- hPageSize);
			HBar.setPageIncrement(hPageSize);

			if (!bHBarVisible) {
				bHBarVisible = true;
				HBar.show();
			}
		} else {
			if (bHBarVisible) {
				bHBarVisible = false;
				HBar.hide();
			}
			xCoord = 0;
		}

		if (bHBarVisible && bVBarVisible) {
			int x = rect.width - vWid;
			int y = rect.height - hHt;
			int w = rect.width - x + 1;
			int h = rect.height - y + 1;
			cornerRect.reshape(x, y, w, h);

			if (!bCornerRectVisible) {
				bCornerRectVisible = true;
				cornerRect.show();
			}
		} else {
			if (bCornerRectVisible) {
				bCornerRectVisible = false;
				cornerRect.hide();
			}
		}

		spComponent.move(xCoord, yCoord);
		spComponent.validate();
	}
}
