package edu.harvard.i2b2.timeline.external;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Panel;

/**
 * This helper class draws InfoTips
 * <p>
 * An InfoTip is very small window that pops up to display a helpful message
 * regarding the component the mouse is over. It appears if the mouse is held
 * over the component for a short while.
 * <p>
 * 
 * @version
 * @author Symantec
 */
public class InfoTipManager {
	private static Panel infoTipPanel;

	/**
	 * Do not use, this is an all-static class.
	 */
	public InfoTipManager() {
	}

	/**
	 * Creates a new panel for the InfoTip as needed, and then returns it. The
	 * InfoTip is drawn in this panel.
	 * 
	 * @return a panel for the InfoTip
	 */
	public static Panel getInfoTipPanel() {
		if (infoTipPanel == null) {
			infoTipPanel = new Panel();
			infoTipPanel.hide();
			infoTipPanel.setLayout(null);
		}

		return infoTipPanel;
	}

	/**
	 * Draws the InfoTip in the InfoTip panel.
	 * 
	 * @param x
	 *            location of the InfoTip, x coordinate
	 * @param y
	 *            location of the InfoTip, y coordinate
	 * @param s
	 *            text to display in the InfoTip
	 * @param fm
	 *            font used for InfoTip text
	 * @param bc
	 *            background color used for the InfoTip
	 * @param fc
	 *            foreground color used for the InfoTip
	 * @see #getInfoTipPanel
	 */
	public static void draw(int x, int y, String s, FontMetrics fm, Color bc,
			Color fc) {
		infoTipPanel.reshape(x, y, fm.stringWidth(s) + 20, fm.getHeight());
		infoTipPanel.setBackground(bc);
		infoTipPanel.setForeground(fc);
		infoTipPanel.show();
	}
}