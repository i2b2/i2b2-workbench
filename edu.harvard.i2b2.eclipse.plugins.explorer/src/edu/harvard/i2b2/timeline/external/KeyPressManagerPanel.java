package edu.harvard.i2b2.timeline.external;

import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextComponent;
import java.util.Vector;

/**
 * KeyPressManagerPanel is a Panel extension which provides for tabbing between
 * components and supporting certain key accelerations by posting events
 * provided by the user. Can be used directly or extended. When extending from
 * BaseTabbedPanel be sure to super() during construction and to
 * super.handleEvent(evt) from handleEvent if you override it.
 * 
 * @version 1.0, Nov 26, 1996
 * @author Symantec
 */

public class KeyPressManagerPanel extends Panel {
	// --------------------------------------------------
	// constants
	// --------------------------------------------------

	public static final int PLAIN = 0;
	public static final int SHIFT = Event.SHIFT_MASK;
	public static final int CTRL = Event.CTRL_MASK;

	// --------------------------------------------------
	// class variables
	// --------------------------------------------------

	// --------------------------------------------------
	// member variables
	// --------------------------------------------------

	Vector tabbed;
	Button defaultButton;
	Button cancelButton;
	Event defaultEvent;
	Event cancelEvent;
	Event fKeyEvents[];
	Container defaultDeliver;
	Container cancelDeliver;
	Container fKeyDeliver[];
	boolean bDefaultSetFocus;
	boolean bCancelSetFocus;
	boolean bTabHack;
	boolean bAutoTab;
	Event eventLostFocus;

	// --------------------------------------------------
	// constructors
	// --------------------------------------------------

	/**
	 * Constructs a Panel which handles key press events.
	 */
	public KeyPressManagerPanel() {
		fKeyEvents = new Event[36];
		fKeyDeliver = new Container[36];
		bAutoTab = true;
		resetKeyManager();
	}

	// --------------------------------------------------
	// accessor methods
	// --------------------------------------------------

	/**
	 * Sets the automatic tab state.
	 * 
	 * @param bNewTabState
	 *            new automatic tab state
	 * @see #getAutoTabState
	 */
	public void setAutoTabState(boolean bNewTabState) {
		bAutoTab = bNewTabState;
	}

	/**
	 * Gets the current automatic tab state.
	 * 
	 * @return boolean - current automatic tab state value
	 * @see #setAutoTabState
	 */
	public boolean getAutoTabState() {
		return bAutoTab;
	}

	// --------------------------------------------------
	// event methods
	// --------------------------------------------------

	@Override
	public synchronized boolean handleEvent(Event evt) {
		switch (evt.id) {
		case Event.KEY_ACTION: {
			int index = evt.key - Event.F1;

			switch (evt.modifiers) {
			case PLAIN: {
				break;
			}
			case SHIFT: {
				index += 12;
				break;
			}
			case CTRL: {
				index += 24;
				break;
			}
			default: {
				index = -1;
				break;
			}
			}

			if ((index > -1) && (index < 36)) {
				if (fKeyEvents[index] != null) {
					deliverEventTo(fKeyEvents[index], fKeyDeliver[index]);
					return true;
				}
			}

			break;
		}
		case Event.KEY_PRESS: {
			bTabHack = false;

			if (keyPressed(evt)) {
				return true;
			}

			break;
		}
		case Event.LOST_FOCUS: {
			if (evt.target instanceof TextComponent) {
				eventLostFocus = evt;
				bTabHack = true;
			}

			break;
		}
		case Event.KEY_RELEASE: {
			if (bTabHack) {
				bTabHack = false;
				eventLostFocus.key = evt.key;
				eventLostFocus.modifiers = evt.modifiers;

				if (keyPressed(eventLostFocus)) {
					return true;
				}
			}

			break;
		}
		}

		return super.handleEvent(evt);
	}

	boolean keyPressed(Event evt) {
		switch (evt.key) {
		case 9: // TAB:
		{
			if (evt.target instanceof Component) {
				return doTab((Component) evt.target, evt.modifiers);
			}

			return doTab(this, evt.modifiers); // component not in list
		}
		case 10: // ENTER:
		{
			if (defaultButton != null) {
				if (defaultButton.isEnabled()) {
					if (bDefaultSetFocus) {
						defaultButton.requestFocus();
					}

					deliverEventTo(defaultEvent, defaultDeliver);

					return true;
				}
			}

			break;
		}
		case 27: // ESC:
		{
			if (cancelButton != null) {
				if (cancelButton.isEnabled()) {
					if (bCancelSetFocus) {
						cancelButton.requestFocus();
					}

					deliverEventTo(cancelEvent, cancelDeliver);

					return true;
				}
			}

			break;
		}
		}

		return false;
	}

	// --------------------------------------------------
	// class methods
	// --------------------------------------------------

	// --------------------------------------------------
	// memeber methods
	// --------------------------------------------------

	/**
	 * Resets all KeyPressManager associations.
	 */
	public void resetKeyManager() {
		tabbed = new Vector();
		defaultButton = null;
		cancelButton = null;
		defaultEvent = null;
		cancelEvent = null;
		defaultDeliver = null;
		cancelDeliver = null;
		bDefaultSetFocus = false;
		bCancelSetFocus = false;

		for (int x = 0; x < 36; x++) {
			fKeyEvents[x] = null;
			fKeyDeliver[x] = null;
		}
	}

	/**
	 * Sets the components as the next tab stop in the list components.
	 * 
	 * @param component
	 *            the Component
	 * @return Component - returns added component
	 */
	@Override
	public Component add(Component component) {
		if (bAutoTab) {
			if (!(component instanceof Label)) {
				setTabStop(component);
			}
		}

		return super.add(component);
	}

	/**
	 * Removes Enter/Return key association with current default button/event
	 * 
	 * @see #setDefaultButton
	 */
	public void removeDefaultButton() {
		defaultButton = null;
		defaultEvent = null;
		defaultDeliver = null;
		bDefaultSetFocus = false;
	}

	/**
	 * Sets the components as the next tab stop in the list components.
	 * 
	 * @param component
	 *            the Component
	 */
	public void setTabStop(Component component) {
		if (component != this) {
			tabbed.addElement(component);
		}
	}

	/**
	 * Sets the button to press when the Enter or Return key is pressed.
	 * 
	 * @param button
	 *            the button to set as default
	 * @see #removeDefaultButton
	 */
	public void setDefaultButton(Button button) {
		setDefaultButton(button, new Event(button, Event.ACTION_EVENT, null),
				null, true);
	}

	/**
	 * Associates a button and event with the Enter or Return key press.
	 * 
	 * @param button
	 *            the button to set as default
	 * @param evt
	 *            the event to delivered in response
	 * @param deliverTo
	 *            the container to deliver the event to
	 * @param bSetFocus
	 *            whether to set focus to the button before delivering event
	 * @see #removeDefaultButton
	 */
	public void setDefaultButton(Button button, Event evt, Container deliverTo,
			boolean bSetFocus) {
		defaultButton = button;
		defaultEvent = evt;
		defaultDeliver = deliverTo;
		bDefaultSetFocus = bSetFocus;
		button.requestFocus();
	}

	/**
	 * Sets the button to press when the Escape key is pressed.
	 * 
	 * @param button
	 *            the button to set as Cancel
	 */
	public void setCancelButton(Button button) {
		setCancelButton(button, new Event(button, Event.ACTION_EVENT, null),
				null, true);
	}

	/**
	 * Associates a button and event with the Escape key press.
	 * 
	 * @param button
	 *            the button to set as Cancel
	 * @param evt
	 *            the event to delivered in response
	 * @param deliverTo
	 *            the container to deliver the event to
	 * @param bSetFocus
	 *            whether to set focus to the button before delivering event
	 */
	public void setCancelButton(Button button, Event evt, Container deliverTo,
			boolean bSetFocus) {
		cancelButton = button;
		cancelEvent = evt;
		bCancelSetFocus = bSetFocus;
		cancelDeliver = deliverTo;
	}

	/**
	 * Removes Escape key association with current Cancel button/event
	 */
	public void removeCancelButton() {
		cancelButton = null;
		cancelEvent = null;
		cancelDeliver = null;
		bCancelSetFocus = false;
	}

	/**
	 * Associates an event with a Function key press
	 * 
	 * @param fKey
	 *            the Event.F1 - Event.F12 constant
	 * @param evt
	 *            the event to delivered in response
	 * @param deliverTo
	 *            the container to deliver the event to
	 */
	public void setFKeyEvent(int fKey, Event evt, Container deliverTo) {
		setFKeyEvent(fKey, PLAIN, evt, deliverTo);
	}

	/**
	 * Removes association of an event with a Function key press
	 * 
	 * @param fKey
	 *            the Event.F1 - Event.F12 constant
	 */
	public void removeFKeyEvent(int fKey) {
		removeFKeyEvent(fKey, PLAIN);
	}

	/**
	 * Associates an event with a Function key press
	 * 
	 * @param fKey
	 *            the Event.F1 - Event.F12 constant
	 * @param modifier
	 *            PLAIN, SHIFT, or CTRL modifier
	 * @param evt
	 *            the event to delivered in response
	 * @param deliverTo
	 *            the container to deliver the event to
	 */
	public void setFKeyEvent(int fKey, int modifier, Event evt,
			Container deliverTo) {
		int index = fKey - Event.F1;

		if ((index < 0) || (index > 11)) {
			return;
		}

		switch (modifier) {
		case PLAIN: {
			break;
		}
		case SHIFT: {
			index += 12;
			break;
		}
		case CTRL: {
			index += 24;
			break;
		}
		}

		fKeyEvents[index] = evt;
		fKeyDeliver[index] = deliverTo;
	}

	/**
	 * Removes association of an event with a Function key press
	 * 
	 * @param fKey
	 *            the Event.F1 - Event.F12 constant
	 * @param modifier
	 *            PLAIN, SHIFT, or CTRL modifier
	 */
	public void removeFKeyEvent(int fKey, int modifier) {
		int index = fKey - Event.F1;

		if ((index < 0) || (index > 11)) {
			return;
		}

		switch (modifier) {
		case PLAIN: {
			break;
		}
		case SHIFT: {
			index += 12;
			break;
		}
		case CTRL: {
			index += 24;
			break;
		}
		}

		fKeyEvents[index] = null;
		fKeyDeliver[index] = null;
	}

	void deliverEventTo(Event evt, Container deliverTo) {
		if (deliverTo == null) {
			postEvent(evt);
		} else {
			deliverTo.postEvent(evt);
		}
	}

	boolean doTab(Component current, int tabModifiers) {
		int sze = tabbed.size();

		if (sze > 0 && (tabModifiers == 0 || tabModifiers == Event.SHIFT_MASK)) {
			Component tabTo = null;
			int idx = tabbed.indexOf(current);
			int iCurrent = idx;

			if (idx == -1) {
				Component c = current;

				while (c != this && idx == -1) {
					idx = tabbed.indexOf(c);
					c = c.getParent();
				}
			}

			if (idx == -1) {
				if (tabModifiers == 0) {
					doTab((Component) tabbed.lastElement(), tabModifiers);
				} else {
					doTab((Component) tabbed.firstElement(), tabModifiers);
				}

				return true;
			} else {
				int tabFromIdx = idx;

				while (true) {
					if (tabModifiers == 0) // regular tab
					{
						if (++idx == sze) {
							idx = 0;
						}
					} else {
						if (--idx == -1) {
							idx = sze - 1;
						}
					}

					if (tabFromIdx == idx) // looped through all items and none
					// were enabled
					{
						break;
					}

					try {
						tabTo = (Component) tabbed.elementAt(idx);

						if (tabTo.isEnabled()) {
							tabTo.requestFocus();

							if (tabTo instanceof TextComponent) {
								TextComponent tc = (TextComponent) tabTo;
								tc.selectAll();
							}

							Component tabFrom = (Component) tabbed
									.elementAt(iCurrent);
							Event eLostFocus = new Event(tabFrom,
									Event.LOST_FOCUS, null);
							tabFrom.postEvent(eLostFocus);

							tabTo = (Component) tabbed.elementAt(idx);
							Event eGotFocus = new Event(tabTo, Event.GOT_FOCUS,
									null);
							tabTo.postEvent(eGotFocus);

							return true;
						}
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}
		}

		return false;
	}
}
