package edu.harvard.i2b2.timeline.external;

import java.awt.Canvas;
import java.awt.Dimension;

/**
 * Base slider control class.
 * 
 * @see symantec.itools.awt.HorizontalSlider
 * @see symantec.itools.awt.VerticalSlider
 * @version 1.0, Nov 26, 1996
 * @author Symantec
 */

public abstract class symSlider extends Canvas {
    /**
     * Defines the slider tick style where the tick marks appear to the left of
     * the slider thumb.
     */
    public static final int TICK_LEFT = 0;

    /**
     * Defines the slider tick style where the tick marks appear to the right of
     * the slider thumb.
     */
    public static final int TICK_RIGHT = 1;

    /**
     * Defines the slider tick style where the tick marks appear below the
     * slider thumb.
     */
    public static final int TICK_BOTTOM = 0;

    /**
     * Defines the slider tick style where the tick marks appear above the
     * slider thumb.
     */
    public static final int TICK_TOP = 1;

    /**
     * Defines the slider tick style where the tick marks appear both to the
     * left and right of the slider thumb.
     */
    public static final int TICK_BOTH = 2;

    /**
     * Disables the display of slider tick marks.
     */
    public static final int TICK_NONE = 3;

    protected boolean enabled;
    protected int width;
    protected int height;
    protected int style;
    protected int freq;
    protected int min;
    protected int max;
    protected int prevPos;
    protected int curPos;
    protected boolean showBorder;

    protected symSlider() {
    }

    /**
     * Returns the current slider tick mark style.
     */
    public int getTickStyle() {
	return style;
    }

    /**
     * Sets the minimum value of the slider range.
     */
    public void setMinValue(int min) {
	this.min = min;

	invalidate();
    }

    /**
     * Returns the current minimum value of the slider range.
     */
    public int getMinValue() {
	return min;
    }

    /**
     * Sets the maximum value of the slider range.
     */
    public void setMaxValue(int max) {
	this.max = max;

	invalidate();
    }

    /**
     * Returns the current maximum value of the slider range.
     */
    public int getMaxValue() {
	return max;
    }

    /**
     * Sets the tick mark display frequency.
     */
    public void setTickFreq(int freq) {
	this.freq = freq;

	invalidate();
    }

    /**
     * Returns the current tick mark display frequency.
     */
    public int getTickFreq() {
	return freq;
    }

    /**
     * Sets the current slider value.
     */
    public void setValue(int pos) {
	doMove((pos - min) / freq, false);
	repaint();
    }

    /**
     * Returns the current slider value.
     */
    public int getValue() {
	return curPos * freq + min;
    }

    /**
     * Sets the border display flag.
     */
    public void setShowBorder(boolean f) {
	showBorder = f;

	invalidate();
    }

    /**
     * Returns the current border display flag.
     */
    public boolean getShowBorder() {
	return showBorder;
    }

    /**
     * Enables the slider control.
     */
    @Override
    public synchronized void enable() {
	super.enable();
    }

    /**
     * Disables the slider control.
     */
    @Override
    public synchronized void disable() {
	super.disable();
    }

    /**
     * Returns the current "preferred size" of the slider control.
     */
    @Override
    public Dimension preferredSize() {
	return new Dimension(width, height);
    }

    protected abstract void doMove(int pos, boolean forcePost);
}
