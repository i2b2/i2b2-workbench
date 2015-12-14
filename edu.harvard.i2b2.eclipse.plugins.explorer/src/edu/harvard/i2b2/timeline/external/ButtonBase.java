package edu.harvard.i2b2.timeline.external;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Point;
import edu.harvard.i2b2.timeline.external.Timer;

/**
 * ButtonBase abstract parent class.
 * 
 * @version 1.0, Nov 26, 1996
 * @author Symantec
 */

public abstract class ButtonBase extends Canvas {
	protected boolean pressed;
	protected boolean released;
	protected boolean inButton;
	protected boolean notifyWhilePressed;
	protected boolean showInfoTip;
	protected boolean running;
	protected boolean notified;
	protected boolean showFocus;
	protected boolean doInfoTip;
	protected int bevel;
	protected int notifyDelay;
	protected int infoTipDelay;
	protected int pressedAdjustment;
	protected String infoTipText;
	protected Color infoTipTextColor;
	protected Timer notifyTimer;
	protected Timer infoTipTimer;
	protected int infoTipX;
	protected int infoTipY;
	protected LayoutManager infoTipLayoutManager;

	/**
	 * Constructs a default ButtonBase
	 */
	protected ButtonBase() {
		pressed = false;
		released = true;
		notifyWhilePressed = false;
		showInfoTip = false;
		running = false;
		notified = false;
		notifyTimer = null;
		infoTipTimer = null;
		infoTipTextColor = Color.black;
		notifyDelay = 1000;
		infoTipDelay = 1000;
		bevel = 1;
		pressedAdjustment = 0;
		resize(10, 10);
	}

	/**
	 * Sets the 3-D bevel height of the button.
	 * 
	 * @param size
	 *            size of bevel, in pixels
	 * @see #getBevelHeight
	 */
	public void setBevelHeight(int size)
	// throws AWTException
	{
		try {
			checkBevelSize(size);
		} catch (AWTException e) {
			System.err.println("Invalid Bevel Size " + size);
		}

		bevel = size;

		invalidate();
	}

	/**
	 * Returns the current 3-D bevel height of the button.
	 * 
	 * @return height of bevel, in pixels
	 * @see #setBevelHeight
	 */
	public int getBevelHeight() {
		return bevel;
	}

	/**
	 * Sets the button continualy send messages while pressed
	 * 
	 * @param f
	 *            true = send messages; false = do not send messages
	 * @see #getNotifyWhilePressed
	 * @see #setNotifyDelay
	 * @see #getNotifyDelay
	 */
	public void setNotifyWhilePressed(boolean f) {
		notifyWhilePressed = f;

		if (notifyWhilePressed) {
			notifyTimer = new Timer(this, notifyDelay, true, Event.ACTION_EVENT);
		} else if (notifyTimer != null) {
			notifyTimer = null;
		}
	}

	/**
	 * Returns the current notifyWhilePressed status
	 * 
	 * @return notifyWhilePressed flag
	 * @see #setNotifyWhilePressed
	 * @see #setNotifyDelay
	 * @see #getNotifyDelay
	 */
	public boolean getNotifyWhilePressed() {
		return notifyWhilePressed;
	}

	/**
	 * Sets the notification delay in milliseconds
	 * 
	 * @param int d the delay in milliseconds
	 * @see #setNotifyWhilePressed
	 * @see #getNotifyDelay
	 */
	public void setNotifyDelay(int d) {
		notifyDelay = d;
	}

	/**
	 * Returns the current notify delay in milliseconds
	 * 
	 * @return the delay in milliseconds
	 * @see #setNotifyWhilePressed
	 * @see #setNotifyDelay
	 */
	public int getNotifyDelay() {
		return notifyDelay;
	}

	public void setShowInfoTip(boolean f) {
		showInfoTip = f;

		if (showInfoTip) {
			infoTipTimer = new Timer(this, notifyDelay, true,
					Event.ACTION_EVENT);
		} else if (infoTipTimer != null) {
			infoTipTimer = null;
		}
	}

	public boolean getShowInfoTip() {
		return showInfoTip;
	}

	/**
	 * Sets the notification delay in milliseconds
	 * 
	 * @param int d the delay in milliseconds
	 * @see #setNotifyWhilePressed
	 * @see #getNotifyDelay
	 */
	public void setInfoTipDelay(int d) {
		infoTipDelay = d;
	}

	/**
	 * Returns the current notify delay in milliseconds
	 * 
	 * @return the delay in milliseconds
	 * @see #setNotifyWhilePressed
	 * @see #setNotifyDelay
	 */
	public int getInfoTipDelay() {
		return infoTipDelay;
	}

	/**
	 * Set the showFocus indicator to visual show when the mouse enters the
	 * Button
	 * 
	 * @param boolean f true = show the mouse enter; false = do not show the
	 *        mouse enter
	 * @see #getShowFocus
	 */
	public void setShowFocus(boolean f) {
		showFocus = f;
	}

	/**
	 * Returns the showFocus indicator
	 * 
	 * @return showFocus flag
	 * @see #setShowFocus
	 */
	public boolean getShowFocus() {
		return showFocus;
	}

	public void setInfoTipText(String t) {
		infoTipText = t;
	}

	public String getInfoTipText() {
		return infoTipText;
	}

	public void setInfoTipTextColor(Color c) {
		infoTipTextColor = c;
	}

	public Color getInfoTipTextColor() {
		return infoTipTextColor;
	}

	/**
	 * If the notification timer is running it is stopped. If the mouse was
	 * pressed inside the button then post an event.
	 * 
	 * @param Event
	 *            e the Event
	 * @param int x the X co-ordinate of the mouse
	 * @param int y the Y co-ordinate of the mouse
	 * @return true since the Event was handled
	 * @see #mouseDown
	 * @see #postMouseUpEvent
	 */
	@Override
	public boolean mouseUp(Event e, int x, int y) {
		if (running) {
			running = false;
			notifyTimer.stop();
		}

		if (pressed) {
			pressed = false;
			pressedAdjustment = 0;

			if (!notifyWhilePressed || !notified) {
				postEvent(new Event(this, Event.ACTION_EVENT, null));
			}
		}

		released = true;
		repaint();

		return true;
	}

	/**
	 * If the notifyWhilePressed flag is true the notification Timer is started
	 * 
	 * @param Event
	 *            e the Event
	 * @param int x the X co-ordinate of the mouse
	 * @param int y the Y co-ordinate of the mouse
	 * @return true since the Event was handled
	 * @see #setNotifyWhilePressed
	 * @see #setNotifyDelay
	 * @see #mouseUp
	 */
	@Override
	public boolean mouseDown(Event e, int x, int y) {
		if (notifyWhilePressed && !running) {
			running = true;
			notifyTimer.start();
		}

		pressed = true;
		released = false;
		pressedAdjustment = bevel;
		repaint();

		return true;
	}

	/**
	 * @param Event
	 *            e the Event
	 * @param int x the X co-ordinate of the mouse
	 * @param int y the Y co-ordinate of the mouse
	 * @return true since the Event was handled
	 */
	@Override
	public boolean mouseEnter(Event e, int x, int y) {
		inButton = true;

		if (showInfoTip) {
			infoTipX = x;
			infoTipY = y;
			infoTipTimer.start();
		}

		if (!released) {
			mouseDown(e, x, y);
		}

		// repaint();

		return true;
	}

	/**
	 * @param Event
	 *            e the Event
	 * @param int x the X co-ordinate of the mouse
	 * @param int y the Y co-ordinate of the mouse
	 * @return true since the Event was handled
	 */
	@Override
	public boolean mouseExit(Event e, int x, int y) {
		inButton = false;

		if (showInfoTip) {
			Panel infoTipPanel;

			infoTipTimer.stop();
			infoTipPanel = InfoTipManager.getInfoTipPanel();
			infoTipPanel.getParent().setLayout(infoTipLayoutManager);
			infoTipPanel.hide();
		}

		if (pressed) {
			pressed = false;
			pressedAdjustment = 0;
		}

		// repaint();

		return true;
	}

	@Override
	public boolean action(Event e, Object o) {
		if (notifyWhilePressed) {
			if (e.target == notifyTimer && true) // !symantec.beans.Beans.
			// isDesignTime())
			{
				postEvent(new Event(this, Event.ACTION_EVENT, null));
				return true;
			}
		}

		if (showInfoTip) {
			if (e.target == infoTipTimer) {
				doInfoTip = true;
				repaint();
				infoTipTimer.stop();

				return true;
			}
		}

		return super.action(e, o);
	}

	/**
	 * Enables the button.
	 * 
	 * @see #disable
	 */
	@Override
	public void enable() {
		if (!isEnabled()) {
			super.enable();
			pressed = false;
			pressedAdjustment = 0;
		}

		repaint();
	}

	/**
	 * Disables the button.
	 * 
	 * @see #enable
	 */
	@Override
	public void disable() {
		if (isEnabled()) {
			super.disable();

			if (notifyTimer != null) {
				notifyTimer.stop();
			}

			if (infoTipTimer != null) {
				infoTipTimer.stop();
			}

			pressed = false;
			pressedAdjustment = 0;
		}

		repaint();
	}

	/**
	 * @param Graphics
	 *            g the Graphics object
	 */
	@Override
	public void update(Graphics g) {
		Dimension s;

		s = size();

		g.clipRect(0, 0, s.width, s.height);
		paint(g);
	}

	/**
	 * Paints the button
	 * 
	 * @param Graphics
	 *            g the Graphics object
	 */
	@Override
	public void paint(Graphics g) {
		Dimension s;
		int width;
		int height;
		int x;
		int y;
		int w;
		int h;
		int i;

		s = size();
		width = s.width;
		height = s.height;
		x = bevel + 1;
		y = bevel + 1;
		w = width - 1;
		h = height - 1;

		g.setColor(Color.lightGray);
		g.fillRect(0, 0, width, height);

		if (pressed) {
			y = x += bevel > 0 ? 2 : 1;
			g.setColor(Color.lightGray);

			for (i = 1; i < bevel + 1; i++) {
				g.drawLine(i, h - i, w - i, h - i);
				g.drawLine(w - i, h - i, w - i, i);
			}

			g.setColor(Color.gray);

			for (i = 1; i < bevel + 1; ++i) {
				g.drawLine(i, h, i, i);
				g.drawLine(i, i, w, i);
			}
		} else {
			g.setColor(Color.white);

			for (i = 1; i < bevel + 1; i++) {
				g.drawLine(i, h - i, i, i);
				g.drawLine(i, i, w - i, i);
			}

			g.setColor(Color.gray);

			for (i = 1; i < bevel + 2; ++i) {
				g.drawLine(i, h - i, w - i, h - i);
				g.drawLine(w - i, h - i, w - i, i);
			}
		}

		g.setColor(Color.black);
		g.drawLine(1, 0, width - 2, 0);
		g.drawLine(0, 1, 0, height - 2);
		g.drawLine(1, height - 1, width - 2, height - 1);
		g.drawLine(width - 1, height - 2, width - 1, 1);

		if (showInfoTip) {
			if (doInfoTip) {
				drawInfoTip();
			}
		}
	}

	protected void drawInfoTip() {
		Panel infoTipPanel;
		Point p;

		doInfoTip = false;
		p = location();

		infoTipPanel = InfoTipManager.getInfoTipPanel();

		infoTipX += p.x;
		infoTipY += p.y;

		infoTipLayoutManager = infoTipPanel.getParent().getLayout();
		InfoTipManager.draw(infoTipX, infoTipY, infoTipText,
				getFontMetrics(getFont()), Color.yellow, Color.black);
	}

	private void checkBevelSize(int i) throws AWTException {
		Dimension s;

		s = size();

		if (i < 0 || i >= (s.width / 2) || i >= (s.height / 2)) {
			throw new AWTException("invalid bevel size");
		}
	}
}
