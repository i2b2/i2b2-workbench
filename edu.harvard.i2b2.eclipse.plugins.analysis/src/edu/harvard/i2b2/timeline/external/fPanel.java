package edu.harvard.i2b2.timeline.external;

import java.awt.*;

public class fPanel extends Panel {

    /*
     * Constants to be used for font styles. Can be combined to mix styles.
     */

    /**
     * The plain style constant. This can be combined with the other style
     * constants for mixed styles.
     */

    public final static int PLAIN = 0;

    /**
     * The bold style constant. This can be combined with the other style
     * constants for mixed styles.
     */

    public final static int BOLD = 1;

    /**
     * The italicized style constant. This can be combined with the other style
     * constants for mixed styles.
     */

    public final static int ITALIC = 2;

    /*
     * Constants to be used for font styles. Cannot be combined to mix styles.
     */

    /**
     * The Engraved style constant. This cannot be combined with the other style
     * constants for mixed styles.
     */

    public final static int ENGRAVED = 0;

    /**
     * The Embossed style constant. This cannot be combined with the other style
     * constants for mixed styles.
     */

    public final static int EMBOSSED = 1;

    /**
     * The Raised style constant. This cannot be combined with the other style
     * constants for mixed styles.
     */

    public final static int RAISED = 2;

    /**
     * The Embeded style constant. This cannot be combined with the other style
     * constants for mixed styles.
     */

    public final static int EMBEDED = 3;

    /**
     * The Plain style constant. This cannot be combined with the other style
     * constants for mixed styles.
     */

    public final static int STANDARD = 4;

    /**
     * Variables for this class
     */

    /**
     * The logical name of this font.
     */

    protected String name;

    /**
     * The style of the font. This is the sum of the constants PLAIN, BOLD, or
     * ITALIC.
     */

    protected int style;

    /**
     * The point size of the font.
     */

    protected int size;

    /**
     * The style of the Border. This is the sum of the constants ENGRAVED,
     * EMBOSSED, RAISED, EMBEDED,or STANDARD.
     */

    protected int bstyle;

    /**
     * The size in pixels of the Border.
     */

    protected int bsize;

    /**
     * The label to be displayed on the panel
     */

    protected String label;

    /**
     * The font to be used
     */
    private static Font theFont;

    /**
     * The standard panel found in the java.awt.Panel
     */
    public fPanel() {
	super();
    }

    /**
     * Set the font, style, and size of the panel-label
     * 
     * @param name
     *            String, the name of the font
     * @param style
     *            int, the style of the font
     * @param size
     *            int, the size of the font
     */

    public void setFont(String name, int style, int size) {
	this.name = name;
	this.style = style;
	this.size = size;
	theFont = new Font(name, style, size);
    }

    /**
     * Set the style of the border wished for the panel
     */

    public void setBorder(int style) {
	this.bstyle = style;
    }

    /**
     * Set the style of the border wished for the pannel Set the size of the
     * selected boreder
     */

    public void setBorder(int style, int size) {
	this.bstyle = style;
	this.bsize = size;
    }

    /**
     * Set the desired label for the paenl
     */

    public void setLabel(String label) {
	this.label = label + "  ";
	FontMetrics fm;

	if (theFont == null) {
	    theFont = new Font("Dialog", Font.PLAIN, 12);
	} else {
	}

	fm = getFontMetrics(theFont);

	Label title;
	int x = 10;
	int y = 0;
	int width = fm.stringWidth(this.label) + theFont.getSize();
	int height = fm.getHeight();

	if (this.label.length() <= 1) {
	} else if (this.label.length() > 1) {
	    title = new Label(label);
	    title.setFont(new Font(theFont.getName(), theFont.getStyle(),
		    theFont.getSize()));
	    this.add(title);
	    title.reshape(x, y, width, height);
	}
    }

    /**
     * Get the current label of the border
     */

    public String getLabel() {
	return label;
    }

    /**
     * get the current Border style
     */

    public int getBorderStyle() {
	return bstyle;
    }

    /**
     * Get the current size of the border style
     */

    public int getBorderSize() {
	return bsize;
    }

    @Override
    public void paint(Graphics g) {
	FontMetrics fm = getFontMetrics(theFont);
	int p_width;
	int p_height;
	int y = fm.getHeight() / 2;
	Dimension of_panel;
	int i;

	of_panel = this.size();
	p_width = of_panel.width - 2;
	p_height = of_panel.height - 7;

	if (bstyle == 0) {
	    g.setColor(Color.white);
	    g.drawRect(1, y + 1, p_width, p_height - y);
	    g.setColor(Color.gray);
	    g.drawRect(0, y, p_width, p_height - y);
	}
	if (bstyle == 1) {
	    g.setColor(Color.gray);
	    g.drawRect(1, y + 1, p_width, p_height - y);
	    g.setColor(Color.white);
	    g.drawRect(0, y, p_width, p_height - y);
	}
	if (bstyle == 2) {
	    for (i = 0; i <= bsize; i++) {
		Color c = new Color(0xE0E0E0);

		g.setColor(c.brighter());
		g.drawLine(i, y, i, (0 + p_height) - i);
		g.drawLine(1 - i, y + i, (0 + p_width - 1) - i, y + i);
		g.setColor(c.darker());
		g.drawLine(i, p_height - i, p_width - i, p_height - i);
		g.drawLine(p_width - i, y + i, p_width - i, (p_height - 1) - i);
	    }
	}
	if (bstyle == 3) {
	    for (i = 0; i <= bsize; i++) {
		Color c = new Color(0xE0E0E0);

		g.setColor(c.darker());
		g.drawLine(i, y, i, (0 + p_height) - i);
		g.drawLine(1 - i, y + i, (0 + p_width - 1) - i, y + i);
		g.setColor(c.brighter());
		g.drawLine(i, p_height - i, p_width - i, p_height - i);
		g.drawLine(p_width - i, y + i, p_width - i, (p_height - 1) - i);
	    }
	}
	if (bstyle == 4) {
	}
    }
}