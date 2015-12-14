package edu.harvard.i2b2.timeline.external;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Event;

/**
 * HorizontalSlider component.
 * 
 * @see symantec.itools.awt.Slider
 * @see symantec.itools.awt.VerticalSlider
 * @version 1.0, Nov 26, 1996
 * @author Symantec
 */

public class HorizontalSlider extends symSlider {
	protected static final int TICK_HEIGHT = 4;
	private static final int BORDER_X = 15;
	private static final int BORDER_Y = 0;
	private int prevPos;
	private int curPos;
	private int width;
	private int height;
	private boolean showBorder = true;

	private HorizontalSliderTick tick[];
	private HorizontalSliderThumb thumb;

	/**
	 * Constructs a default HorizontalSlider.
	 */
	public HorizontalSlider() {
		this.thumb = new HorizontalSliderThumbBoth();
		this.style = TICK_BOTH;
		this.min = 1;
		this.max = 10;
		this.freq = 1;

		prevPos = 0; // mm
		curPos = 0;

		width = 200;
		height = 50;

		showBorder = true;

		tick = null;
	}

	/**
	 * Sets the current slider tick mark style.
	 * 
	 * @see #getTickStyle
	 */
	public void setTickStyle(int style) {
		this.style = style;

		switch (style) {
		case TICK_TOP:
			thumb = new HorizontalSliderThumbTop();
			break;

		case TICK_BOTTOM:
			thumb = new HorizontalSliderThumbBot();
			break;

		default:
			thumb = new HorizontalSliderThumbBoth();
			break;

		}

		do_reshape(width, height);

		invalidate();
	}

	/**
	 * Returns the current slider tick mark style.
	 * 
	 * @see #setTickStyle
	 */
	@Override
	public int getTickStyle() {
		return style;
	}

	@Override
	public boolean mouseDown(Event e, int x, int y) {

		moveThumb(x, true);

		return true;

	}

	@Override
	public boolean mouseDrag(Event e, int x, int y) {

		moveThumb(x, false);

		return true;

	}

	private void do_reshape(int w, int h) {
		int hb = BORDER_X;
		int vb = BORDER_Y;

		if (w < hb)
			hb = w / 4;

		if (h < vb)
			vb = h / 4;

		int x0 = hb;
		int x1 = w - hb;
		int y0 = vb;
		int y1 = h - vb;

		if (x0 == 0)
			x0 = 1;

		if (x1 == 0)
			x1 = 1;

		if (y0 == 0)
			y0 = 1;

		if (y1 == 0)
			y1 = 1;

		int n = (max - min) / freq + 1;

		tick = new HorizontalSliderTick[n];

		int hs = (x1 - x0) / (n - 1), ch;

		for (int i = 0; i < n; ++i) {
			ch = i * hs;
			tick[i] = new HorizontalSliderTick(x0 + ch, y0, y1, ch);
		}

		thumb.resize(hs / 2, y1 - y0 - TICK_HEIGHT - 1);
	}

	/**
	 * Reshapes the slider control.
	 */
	@Override
	public void reshape(int x, int y, int w, int h) {
		width = w;
		height = h;

		do_reshape(w, h);

		super.reshape(x, y, w, h);
	}

	/**
	 * Paints the slider control.
	 */
	@Override
	public void paint(Graphics g) {

		if (tick.length == 0)
			return;

		HorizontalSliderTick t;

		g.clipRect(0, 0, width, height);

		thumb.draw(g, tick[curPos]);

		if (prevPos != curPos)
			thumb.clip(g, tick[prevPos]);

		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);

		g.setColor(Color.black);

		int x0, x1, y, w = width - 1, h = height - 1;
		boolean end;

		if (showBorder)
			g.drawRect(0, 0, w, h);

		for (int i = 0; i < tick.length; ++i) {
			end = i == 0 || i == tick.length - 1;

			t = tick[i];

			if (style == TICK_TOP || style == TICK_BOTH)
				g.drawLine(t.x, t.y0 + (end ? 0 : 1), t.x, t.y0 + TICK_HEIGHT);

			if (style == TICK_BOTTOM || style == TICK_BOTH)
				g.drawLine(t.x, t.y1 - TICK_HEIGHT, t.x, t.y1 - (end ? 0 : 1));
		}

		t = tick[0];

		y = (t.y1 + t.y0) / 2;
		x0 = t.x - 5;
		x1 = tick[tick.length - 1].x + 5;

		g.drawLine(x0, y, x1, y);

		g.setColor(Color.gray);
		g.drawLine(x1 + 1, y - 1, x0 - 1, y - 1);
		g.drawLine(x0 - 1, y - 1, x0 - 1, y + 1);

		g.setColor(Color.lightGray);
		g.drawLine(x0, y + 1, x1 + 1, y + 1);
		g.drawLine(x1 + 1, y + 1, x1 + 1, y);

		g.setColor(Color.white);
		g.drawLine(x0 - 1, y + 2, x1 + 2, y + 2);
		g.drawLine(x1 + 2, y + 2, x1 + 2, y - 1);

		g.clipRect(0, 0, width, height);

		thumb.draw(g, tick[curPos]);

		prevPos = curPos;

	}

	@Override
	protected void doMove(int pos, boolean forcePost) {
		if (tick == null) {
			prevPos = curPos = pos;
			return;
		}

		if (pos >= tick.length)
			pos = tick.length - 1;

		if (pos != curPos || forcePost) {
			prevPos = curPos;
			curPos = pos;
			paint(getGraphics());

			postEvent(new Event(this, Event.ACTION_EVENT, new Integer(curPos
					* freq + min)));
		}
	}

	private void moveThumb(int x, boolean forcePost) {
		if (tick.length > 1) {
			int dist = tick[1].x - tick[0].x;

			if (dist == 0)
				return;

			int newPos = (x - tick[0].x) / dist;

			if (newPos < 0)
				newPos = 0;

			if (((x - tick[0].x) % dist) > (dist / 2))
				++newPos;

			doMove(newPos, forcePost);
		}
	}
}

class HorizontalSliderTick {
	int x;
	int y0;
	int y1;
	int v;

	HorizontalSliderTick(int ix, int iy0, int iy1, int iv) {
		x = ix;
		y0 = iy0;
		y1 = iy1;
		v = iv;
	}
}

abstract class HorizontalSliderThumb {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected Graphics g;
	protected HorizontalSliderTick t;

	void resize(int width, int height) {
		x = (this.width = width) / 2;
		y = (this.height = height) - HorizontalSlider.TICK_HEIGHT - 1 - 1;
	}

	abstract void draw(Graphics g, HorizontalSliderTick t);

	protected void draw(int x0, int y0, int x1, int y1) {
		g.drawLine(t.x + x0, t.y0 + y0 + HorizontalSlider.TICK_HEIGHT + 1, t.x
				+ x1, t.y0 + y1 + HorizontalSlider.TICK_HEIGHT + 1);
	}

	protected void initDraw(Graphics g, HorizontalSliderTick t) {
		this.g = g;
		this.t = t;

		g.setColor(Color.lightGray);
		g.fillRect(t.x - x + 1, t.y0 + 2 + HorizontalSlider.TICK_HEIGHT + 1,
				width - 2, y - 2);

		g.setColor(Color.white);
	}

	void clip(Graphics g, HorizontalSliderTick t) {
		g.clipRect(t.x - width / 2, t.y0, width + 1, height + 1);
	}
}

class HorizontalSliderThumbBoth extends HorizontalSliderThumb {
	@Override
	void draw(Graphics g, HorizontalSliderTick t) {
		super.initDraw(g, t);

		draw(-x, y, -x, 1);
		draw(-x, 1, x, 1);

		g.setColor(Color.black);
		draw(-x, y, x, y);
		draw(x, y, x, 1);

		g.setColor(Color.gray);
		draw(1 - x, y - 1, x - 1, y - 1);
		draw(x - 1, y - 1, x - 1, 2);
	}
}

class HorizontalSliderThumbTop extends HorizontalSliderThumb {
	@Override
	void draw(Graphics g, HorizontalSliderTick t) {
		super.initDraw(g, t);

		int a = y / 5;

		draw(-x, y, -x, a);
		draw(-x, a, 0, 1);

		g.setColor(Color.black);
		draw(0, 1, x, a);
		draw(x, a, x, y);
		draw(x, y, -x, y);

		g.setColor(Color.gray);
		draw(0, 2, x - 1, a);
		draw(x - 1, a, x - 1, y - 1);
		draw(x - 1, y - 1, 1 - x, y - 1);
	}
}

class HorizontalSliderThumbBot extends HorizontalSliderThumb {
	@Override
	void draw(Graphics g, HorizontalSliderTick t) {
		super.initDraw(g, t);

		int a = height - HorizontalSlider.TICK_HEIGHT - 1 - 1 - y / 5;

		draw(x, 1, -x, 1);
		draw(-x, 1, -x, a);
		draw(-x, a, 0, y - 1);

		g.setColor(Color.black);
		draw(0, y, x, a);
		draw(x, a, x, 1);

		g.setColor(Color.gray);
		draw(0, y - 1, x - 1, a);
		draw(x - 1, a, x - 1, 2);
	}
}
