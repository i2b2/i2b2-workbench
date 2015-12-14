/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.awt.*;
import java.net.*;

import edu.harvard.i2b2.explorer.ui.MainPanel;

public class CurrPanel extends Panel {

	private int width, height;
	public URLConnection hcilCon;

	protected static Font font = new Font("Dialog", Font.BOLD, 14);
	protected FontMetrics fontMetrics = getFontMetrics(font);

	private Button load;
	private Record theApplet;
	private EnterField fileEntry;
	private Button control;
	public ControlPanel ctrlpanel;
	private Button grep;
	private TextField grepEntry;

	public CurrPanel(int width, int height, Record theApplet) {
		this.width = width;
		this.height = height;

		this.theApplet = theApplet;

		load = new Button("load");
		// control = new Button("Control Panel");

		load.setBounds((int) (width * 0.2), (int) (height / 5.0),
				(int) (width * 0.05), (int) (3.0 * height / 5.0));
		// add(load);

		grep = new Button("Text Search");

		grep.setBounds((int) (width * 0.3), (int) (height / 5.0),
				(int) (width * 0.15), (int) (3.0 * height / 5.0));
		add(grep);

		// control.setBounds((int)(width*0.6),(int)(height/5.0),(int)(width*0.15)
		// ,(int)(3.0*height/5.0));
		// add(control);

		fileEntry = new EnterField();
		fileEntry.setApplet(theApplet);
		// add(fileEntry);
		// fileEntry.setBounds((int)(width*0.1),(int)(height/5.0),(int)(width*0.1
		// ),(int)(3.0*height/5.0));

		grepEntry = new TextField();
		add(grepEntry);
		grepEntry.setBounds((int) (width * 0.1), (int) (height / 5.0),
				(int) (width * 0.2), (int) (3.0 * height / 5.0));
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.lightGray);
		g.draw3DRect(0, 0, width - 1, height - 1, true);
		g.draw3DRect(1, 1, width - 3, height - 3, true);

		g.setColor(Color.black);
		g.setFont(font);
	}

	@Override
	public boolean action(Event e, Object arg) { // changed to mouseDown 2/11/98
		// by dan to try
		// to fix extra loading problem

		if (e.target == grep) {
			MainPanel.theTimeLinePanel.search = true;
			MainPanel.theTimeLinePanel.grep(grepEntry.getText());
		}

		if (e.target == load) {
			System.out.println("before loadrecord");
			Record.theData = new LoadRecord(theApplet.getCodeBase()
					+ fileEntry.getText(), "none");
			System.out.println("after loadrecord");
			theApplet.resetTabPanel();
			theApplet.resetPicPanel();
			theApplet.resetInfoPanel();
			theApplet.setWidthHeight(400, 350);

			if (ctrlpanel != null) {
				ctrlpanel.setVisible(false);
				ctrlpanel = null;
			}
			System.out.println("after loadrecord");
		} else if (e.target == control) {
			if (ctrlpanel == null) {
				ctrlpanel = new ControlPanel("LifeLines Control Panel", 400,
						500);
				ctrlpanel.setBounds(700, 300, 400, 500);
			}
			ctrlpanel.setVisible(true);
		}
		return true;
	}
}
