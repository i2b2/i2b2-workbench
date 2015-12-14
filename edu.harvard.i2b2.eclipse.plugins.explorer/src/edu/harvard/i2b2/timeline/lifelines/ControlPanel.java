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
import java.util.*;

import edu.harvard.i2b2.explorer.ui.MainPanel;
import edu.harvard.i2b2.explorer.ui.TimelineSearchFrame;
import edu.harvard.i2b2.timeline.external.*;

/**
 * ControlPanel class is used to define various algorithm options that users can
 * choose and customize
 */
public class ControlPanel extends Frame {
	private TabPanel tabPanel;
	private Panel layoutPanel, labelPanel, summaryPanel, searchPanel,
			zoomPanel;
	private Button closeButton;
	private CheckboxGroup line, label, time, arrow, length, search_timeline,
			search_label, popup, col, zoomIn, steps;
	private Checkbox[] check, labelcheck, timecheck, arrowcheck, lengthcheck,
			summarycheck, searchcheck_timeline, searchcheck_label, popcheck,
			colcheck, zoomInCheck, stepsCheck;/* Modified 11/24 - Partha */
	private HorizontalSlider fontSlider, lengthSlider;
	private int n_key;
	private Hashtable recordTable;
	private Facet afacetRecord;
	private boolean[] option = new boolean[5];
	private boolean[] lbloption = new boolean[3];
	private boolean[] timeoption = new boolean[2];
	private boolean[] arrowoption = new boolean[2];
	private boolean[] lengthoption = new boolean[2];
	private boolean[] summaryoption = new boolean[2];
	private boolean[] searchoption_timeline = new boolean[2];
	private boolean[] searchoption_label = new boolean[2];
	private fPanel candidatePanel, arrowPanel, sizePanel, lengthPanel,
			resultPanel, timingPanel, algorithmPanel, rulePanel, popPanel,
			zmInPanel, stPanel;/* Modified 11/24 - Partha */
	private GridLayout grid = new GridLayout(7, 1);
	private Label fontsizeLabel, lengthLabel;

	public ControlPanel(String title, int width, int height) {
		setTitle(title);
		setBackground(new Color(12632256));

		java.awt.Image img = this.getToolkit().getImage(
				TimelineSearchFrame.class.getResource("core-cell.gif"));
		this.setIconImage(img);
		this.setLayout(null);

		// create the main tabbedPanel
		tabPanel = new TabPanel();
		add(tabPanel);
		tabPanel.setBounds(10, 20, width - 10, height - 80);

		// create the button
		closeButton = new Button("Close");
		add(closeButton);
		closeButton.setBounds(180, height - 50, 60, 25);
		closeButton.setBackground(new Color(12632256));

		// add Line Layout tab
		layoutPanel = new Panel();
		layoutPanel.setLayout(null);
		tabPanel.addTabPanel("Layout", true, layoutPanel);
		layoutPanel_init(width - 10, height - 90);

		// add Label tab
		labelPanel = new Panel();
		labelPanel.setLayout(null);
		tabPanel.addTabPanel("Label", true, labelPanel);
		labelPanel_init(width - 10, height - 90);

		summaryPanel = new Panel();
		summaryPanel.setLayout(null);
		tabPanel.addTabPanel("Summary", true, summaryPanel);
		summaryPanel_init(width - 10, height - 90);

		searchPanel = new Panel();
		searchPanel.setLayout(null);
		tabPanel.addTabPanel("Search", true, searchPanel);
		searchPanel_init(width - 10, height - 90);

		zoomPanel = new Panel();
		zoomPanel.setLayout(null);
		tabPanel.addTabPanel("Zoom", true, zoomPanel);
		zoomPanel_init(width - 10, height - 90);

		recordTable = Record.theData.getRecordTable();
		n_key = recordTable.size();

	}

	/**
	 * Initialize data layout control panel
	 */
	public void layoutPanel_init(int width, int height) {

		algorithmPanel = new fPanel();
		layoutPanel.add(algorithmPanel);
		algorithmPanel.setBounds(0, 10, width - 40, 180);
		algorithmPanel.setLabel("Algorithms");
		algorithmPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		algorithmPanel.setLayout(null);
		for (int i = 0; i < 5; i++)
			option[i] = Record.option[i];
		line = new CheckboxGroup();
		check = new Checkbox[5];
		check[0] = new Checkbox("Normal", line, option[0]);
		check[0].setBounds(10, 40, 200, 25);
		check[1] = new Checkbox("Compact", line, option[1]);
		check[1].setBounds(10, 65, 200, 25);
		check[2] = new Checkbox("Advanced Compact", line, option[2]);
		check[2].setBounds(10, 90, 200, 25);
		check[3] = new Checkbox("Chronologically Ordered", line, option[3]);
		check[3].setBounds(10, 115, 200, 25);
		check[4] = new Checkbox("Group by Event Name", line, option[4]);
		check[4].setBounds(10, 140, 200, 25);
		for (int i = 0; i < 5; i++)
			algorithmPanel.add(check[i]);

		timingPanel = new fPanel();
		layoutPanel.add(timingPanel);
		timingPanel.setBounds(0, 190, width - 40, 85);
		timingPanel.setLabel("Optimize layout during zooming");
		timingPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		timingPanel.setLayout(null);
		for (int i = 0; i < 2; i++)
			timeoption[i] = Record.timeoption[i];
		time = new CheckboxGroup();
		timecheck = new Checkbox[2];
		timecheck[0] = new Checkbox("No", time, timeoption[0]);
		timecheck[0].setBounds(10, 20, 80, 25);
		timecheck[1] = new Checkbox("Yes (slower)", time, timeoption[1]);
		timecheck[1].setBounds(10, 45, 180, 25);

		for (int i = 0; i < 2; i++)
			timingPanel.add(timecheck[i]);

	}

	/**
	 * Initialize labeling control panel
	 */
	public void labelPanel_init(int width, int height) {

		candidatePanel = new fPanel();
		labelPanel.add(candidatePanel);
		candidatePanel.setBounds(0, 10, width - 40, 60);
		candidatePanel.setLabel("Location Allowed");
		candidatePanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		candidatePanel.setLayout(null);

		for (int i = 0; i < 3; i++)
			lbloption[i] = Record.lbloption[i];

		label = new CheckboxGroup();
		labelcheck = new Checkbox[3];
		labelcheck[0] = new Checkbox("Top Right", label, lbloption[0]);
		labelcheck[0].setBounds(10, 20, 70, 20);
		labelcheck[1] = new Checkbox("Top Right & Left", label, lbloption[1]);
		labelcheck[1].setBounds(85, 20, 110, 20);
		labelcheck[2] = new Checkbox("4 Corners", label, lbloption[2]);
		labelcheck[2].setBounds(200, 20, 70, 20);

		for (int i = 0; i < 3; i++)
			candidatePanel.add(labelcheck[i]);

		arrowPanel = new fPanel();
		labelPanel.add(arrowPanel);
		arrowPanel.setBounds(0, 10 + 60, width - 40, 50);
		arrowPanel.setLabel("Link from Timeline to Label");
		arrowPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		arrowPanel.setLayout(null);

		for (int i = 0; i < 2; i++)
			arrowoption[i] = Record.arrowoption[i];

		arrow = new CheckboxGroup();
		arrowcheck = new Checkbox[2];
		arrowcheck[0] = new Checkbox("No", arrow, arrowoption[0]);
		arrowcheck[0].setBounds(40, 20, 50, 20);
		arrowcheck[1] = new Checkbox("Yes", arrow, arrowoption[1]);
		arrowcheck[1].setBounds(110, 20, 50, 20);

		for (int i = 0; i < 2; i++)
			arrowPanel.add(arrowcheck[i]);

		sizePanel = new fPanel();
		labelPanel.add(sizePanel);
		sizePanel.setBounds(0, 10 + 60 + 50, width - 40, 75);
		sizePanel.setLabel("Font Size");
		sizePanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		sizePanel.setLayout(null);

		fontSlider = new HorizontalSlider();
		sizePanel.add(fontSlider);

		try {
			fontSlider.setMinValue(1);
			fontSlider.setMaxValue(13);
			fontSlider.setValue(5);
			fontSlider.setShowBorder(false);
			fontSlider.setTickStyle(symSlider.TICK_TOP);
		} catch (Exception e) {
			System.out.println("Exception at fontSlider");
		}

		fontSlider.setBounds(10, 35, 240, 25);
		fontsizeLabel = new Label(
				" 8    9   10  11  12  13  14  15  16  17  18  19  20");
		fontsizeLabel.setFont(new Font("TimesNewRoman", Font.PLAIN, 10));
		sizePanel.add(fontsizeLabel);
		fontsizeLabel.setBounds(15, 15, 240, 24);

		lengthPanel = new fPanel();
		labelPanel.add(lengthPanel);
		lengthPanel.setBounds(0, 10 + 60 + 50 + 75, width - 30, 80);
		lengthPanel.setLabel("Truncation");
		lengthPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		lengthPanel.setLayout(null);

		for (int i = 0; i < 2; i++)
			lengthoption[i] = Record.lengthoption[i];

		length = new CheckboxGroup();
		lengthcheck = new Checkbox[2];
		lengthcheck[0] = new Checkbox("No", length, lengthoption[0]);
		lengthcheck[0].setBounds(20, 20, 50, 20);
		lengthcheck[1] = new Checkbox("Yes", length, lengthoption[1]);
		lengthcheck[1].setBounds(20, 40, 50, 20);

		for (int i = 0; i < 2; i++)
			lengthPanel.add(lengthcheck[i]);

		lengthSlider = new HorizontalSlider();
		lengthPanel.add(lengthSlider);
		lengthSlider.disable();

		try {
			lengthSlider.setMinValue(0);
			lengthSlider.setMaxValue(30);
			lengthSlider.setValue(16);
			lengthSlider.setShowBorder(false);
			lengthSlider.setTickStyle(symSlider.TICK_TOP);
		} catch (Exception e) {
			System.out.println("Exception at lengthSlider");
		}

		lengthSlider.setBounds(90, 40, 160, 25);

		lengthLabel = new Label(" 0         10          20          30");
		lengthLabel.setFont(new Font("TimesNewRoman", Font.PLAIN, 10));
		lengthPanel.add(lengthLabel);
		lengthLabel.setBounds(95, 20, 145, 24);

		popPanel = new fPanel();
		labelPanel.add(popPanel);
		popPanel.setBounds(0, 10 + 60 + 50 + 75 + 80, width - 30, 120);
		popPanel.setLabel("Dynamic Popup Label");
		popPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		popPanel.setLayout(null);

		// Julia 10/12/98, add options for one column and two columns layout of
		// excentric labeling
		popup = new CheckboxGroup();
		popcheck = new Checkbox[3];
		boolean nopopup = (Record.infotip || Record.excentric) ? false : true;
		popcheck[2] = new Checkbox("None", popup, nopopup);
		popcheck[2].setBounds(20, 20, 140, 20);
		popcheck[0] = new Checkbox("Infotip", popup, Record.infotip);
		popcheck[0].setBounds(20, 40, 80, 20);
		popcheck[1] = new Checkbox("Excentric", popup, Record.excentric);
		popcheck[1].setBounds(20, 60, 80, 20);

		for (int i = 0; i < 3; i++)
			popPanel.add(popcheck[i]);

		col = new CheckboxGroup();
		colcheck = new Checkbox[2];
		colcheck[0] = new Checkbox("One column", col, Record.column[0]);
		colcheck[0].setBounds(60, 80, 100, 20);
		colcheck[1] = new Checkbox("Two columns", col, Record.column[1]);
		colcheck[1].setBounds(160, 80, 100, 20);

		for (int i = 0; i < 2; i++) {
			popPanel.add(colcheck[i]);
			if (!Record.excentric)
				colcheck[i].disable();
		}
	}

	/**
	 * Initialize summarization control panel
	 */
	public void summaryPanel_init(int width, int height) {
		rulePanel = new fPanel();
		summaryPanel.add(rulePanel);
		rulePanel.setBounds(0, 10, width - 40, 80);
		rulePanel.setLabel("Rules");
		rulePanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		rulePanel.setLayout(null);
		for (int i = 0; i < 2; i++)
			summaryoption[i] = Record.summaryoption[i];
		summarycheck = new Checkbox[2];
		summarycheck[0] = new Checkbox(
				"Label summary if cannot label all events");
		summarycheck[0].setState(summaryoption[0]);
		summarycheck[0].setBounds(10, 20, 250, 20);
		summarycheck[1] = new Checkbox("Show summary timeline if label summary");
		summarycheck[1].setState(summaryoption[1]);
		summarycheck[1].setBounds(10, 45, 250, 20);
		if (summarycheck[0].getState())
			summarycheck[1].setEnabled(true);
		else
			summarycheck[1].setEnabled(false);

		for (int i = 0; i < 2; i++)
			rulePanel.add(summarycheck[i]);
	}

	/**
	 * Initialize search control panel
	 */
	public void searchPanel_init(int width, int height) {
		resultPanel = new fPanel();
		searchPanel.add(resultPanel);
		resultPanel.setBounds(0, 10, width - 40, 100);
		resultPanel
				.setLabel("Effects on events that are NOT in the result set");
		resultPanel.setLayout(null);

		resultPanel.setFont(new Font("Dialog", Font.BOLD + Font.ITALIC, 14));
		Label timelineLabel = new Label("Timeline");
		timelineLabel.setBounds(28, 20, 60, 20);
		resultPanel.add(timelineLabel);
		Label labelLabel = new Label("Label");
		labelLabel.setBounds(170, 20, 60, 20);
		resultPanel.add(labelLabel);

		search_timeline = new CheckboxGroup();
		search_label = new CheckboxGroup();
		for (int i = 0; i < 2; i++) {
			searchcheck_timeline = new Checkbox[2];
			searchcheck_label = new Checkbox[2];
		}

		for (int i = 0; i < 2; i++) {
			searchoption_timeline[i] = Record.searchoption_timeline[i];
			searchoption_label[i] = Record.searchoption_label[i];
		}
		resultPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		searchcheck_timeline[0] = new Checkbox("Gray out", search_timeline,
				searchoption_timeline[0]);
		searchcheck_timeline[0].setBounds(20, 40, 80, 20);
		resultPanel.add(searchcheck_timeline[0]);
		searchcheck_timeline[1] = new Checkbox("Remove", search_timeline,
				searchoption_timeline[1]);
		searchcheck_timeline[1].setBounds(20, 60, 80, 20);
		resultPanel.add(searchcheck_timeline[1]);
		searchcheck_label[0] = new Checkbox("Gray out", search_label,
				searchoption_label[0]);
		searchcheck_label[0].setBounds(160, 40, 80, 20);
		resultPanel.add(searchcheck_label[0]);
		searchcheck_label[1] = new Checkbox("Remove", search_label,
				searchoption_label[1]);
		searchcheck_label[1].setBounds(160, 60, 80, 20);
		resultPanel.add(searchcheck_label[1]);
	}

	/**
	 * Initialize zoom control panel
	 */
	public void zoomPanel_init(int width, int height) {
		zmInPanel = new fPanel();
		zoomPanel.add(zmInPanel);
		zmInPanel.setBounds(0, 10, width - 40, 180);
		zmInPanel.setLabel("Zooming Ratios");
		zmInPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		zmInPanel.setLayout(null);

		zoomIn = new CheckboxGroup();
		zoomInCheck = new Checkbox[4];
		zoomInCheck[0] = new Checkbox("Factor of 0", zoomIn, false);
		zoomInCheck[0].setBounds(10, 40, 200, 25);
		zoomInCheck[1] = new Checkbox("Factor of 1", zoomIn, true);
		zoomInCheck[1].setBounds(10, 65, 200, 25);
		zoomInCheck[2] = new Checkbox("Factor of 2", zoomIn, false);
		zoomInCheck[2].setBounds(10, 90, 200, 25);
		zoomInCheck[3] = new Checkbox("Factor of 3", zoomIn, false);
		zoomInCheck[3].setBounds(10, 115, 200, 25);

		for (int i = 0; i < 4; i++)
			zmInPanel.add(zoomInCheck[i]);

		stPanel = new fPanel();
		zoomPanel.add(stPanel);
		stPanel.setBounds(0, 190, width - 40, 180);
		stPanel.setLabel("Number of Steps");
		stPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
		stPanel.setLayout(null);

		steps = new CheckboxGroup();

		stepsCheck = new Checkbox[3];
		stepsCheck[0] = new Checkbox("Single Step", steps, false);
		stepsCheck[0].setBounds(10, 40, 200, 25);
		stepsCheck[1] = new Checkbox("Two Steps", steps, false);
		stepsCheck[1].setBounds(10, 65, 200, 25);
		stepsCheck[2] = new Checkbox("Three Steps", steps, true);
		stepsCheck[2].setBounds(10, 90, 200, 25);

		for (int i = 0; i < 3; i++)
			stPanel.add(stepsCheck[i]);

	}

	/**
	 * Re-layout all the data
	 */
	public void re_layout() {
		for (int i = 0; i < n_key; i++) {
			afacetRecord = (Facet) (recordTable.get(new Integer(i)));
			for (int j = 0; j < 5; j++)
				Record.option[j] = option[j];
			afacetRecord.layout();
		}
	}

	/**
	 * Re-label all the events
	 */
	public void re_label() {
		for (int j = 0; j < 3; j++)
			Record.lbloption[j] = lbloption[j];
		for (int j = 0; j < 2; j++)
			Record.summaryoption[j] = summaryoption[j];
		MainPanel.theTimeLinePanel.relabeling = true;
		for (int j = 0; j < 2; j++)
			Record.searchoption_timeline[j] = searchoption_timeline[j];
		for (int j = 0; j < 2; j++)
			Record.searchoption_label[j] = searchoption_label[j];

		for (int j = 0; j < 2; j++)
			Record.arrowoption[j] = arrowoption[j];
		for (int j = 0; j < 2; j++)
			Record.lengthoption[j] = lengthoption[j];
	}

	/**
	 * Will convert to Java 1.1 event handling model
	 */
	@Override
	public boolean handleEvent(Event event) {
		switch (event.id) {
		case Event.WINDOW_DESTROY:
			setVisible(false); // hide the Frame
			return true;

		case Event.ACTION_EVENT:
			// close button
			if (event.target == closeButton) {
				setVisible(false); // hide the Frame
				return true;
			}
			// layout section
			else if (check != null && checkaction(event, check)) {
				for (int i = 0; i < 5; i++)
					option[i] = check[i].getState();
				re_layout();
				re_label();
				MainPanel.theTimeLinePanel.repaint();
				return true;
			}
			// label section
			else if (labelcheck != null && checkaction(event, labelcheck)) {
				for (int i = 0; i < 3; i++)
					lbloption[i] = labelcheck[i].getState();
				re_label();
				MainPanel.theTimeLinePanel.repaint();
				return true;
			} else if (arrowcheck != null && checkaction(event, arrowcheck)) {
				for (int i = 0; i < 2; i++)
					arrowoption[i] = arrowcheck[i].getState();
				re_label();
				MainPanel.theTimeLinePanel.repaint();
				return true;
			} else if (event.target == fontSlider) {
				int fontsize = fontSlider.getValue() + (8 - 1);
				MainPanel.theTimeLinePanel.setLabelFont(fontsize);
				re_label();
				MainPanel.theTimeLinePanel.repaint();
				return true;
			} else if (lengthcheck != null && checkaction(event, lengthcheck)) {
				for (int i = 0; i < 2; i++)
					lengthoption[i] = lengthcheck[i].getState();
				if (lengthoption[1])
					lengthSlider.setEnabled(true);
				else if (lengthoption[0])
					lengthSlider.setEnabled(false);
				return true;
			} else if (popcheck != null && checkaction(event, popcheck)) {
				Record.infotip = popcheck[0].getState();
				Record.excentric = popcheck[1].getState();
				if (Record.excentric) {
					colcheck[0].setEnabled(true);
					colcheck[1].setEnabled(true);
					if (!(Record.column[0] || Record.column[1])) { // need a
						// default
						// option
						Record.column[0] = true;
						colcheck[0].setState(true);
					}
				} else {
					colcheck[0].setEnabled(false);
					colcheck[1].setEnabled(false);
				}
				MainPanel.theTimeLinePanel.repaint();
				return true;
			} else if (colcheck != null && checkaction(event, colcheck)) {
				Record.column[0] = colcheck[0].getState();
				Record.column[1] = colcheck[1].getState();
				MainPanel.theTimeLinePanel.repaint();
				return true;
			} else if (event.target == lengthSlider) {
				Record.lbllength = lengthSlider.getValue();
				re_label();
				MainPanel.theTimeLinePanel.repaint();
				return true;
			} else if (timecheck != null && checkaction(event, timecheck)) {
				for (int i = 0; i < 2; i++)
					timeoption[i] = timecheck[i].getState();
				for (int i = 0; i < 2; i++)
					Record.timeoption[i] = timeoption[i];
				MainPanel.theTimeLinePanel.repaint();
				return true;
			}
			// summary section
			else if (summarycheck != null && event.target == summarycheck[0]) {
				if (summarycheck[0].getState())
					summarycheck[1].setEnabled(true);
				else {
					summarycheck[1].setEnabled(false);
					summarycheck[1].setState(false);
				}
				for (int i = 0; i < 2; i++)
					summaryoption[i] = summarycheck[i].getState();
				re_label();
				MainPanel.theTimeLinePanel.repaint();
				return true;
			} else if (summarycheck != null && event.target == summarycheck[1]) {
				summaryoption[1] = summarycheck[1].getState();
				re_label();
				MainPanel.theTimeLinePanel.repaint();
				return true;
			}
			// search section
			else if (searchcheck_timeline != null
					&& event.target == searchcheck_timeline[1]) {
				for (int i = 0; i < 2; i++) {
					searchoption_timeline[i] = searchcheck_timeline[i]
							.getState();
					Record.searchoption_timeline[i] = searchoption_timeline[i];
				}
				searchcheck_label[1].setState(true);
				searchcheck_label[0].setEnabled(false);
				for (int i = 0; i < 2; i++) {
					searchoption_label[i] = searchcheck_label[i].getState();
					Record.searchoption_label[i] = searchoption_label[i];
				}
				/*
				 * re_layout(); re_label();
				 */
				return true;
			} else if (searchcheck_timeline != null
					&& event.target == searchcheck_timeline[0]) {
				for (int i = 0; i < 2; i++) {
					searchoption_timeline[i] = searchcheck_timeline[i]
							.getState();
					Record.searchoption_timeline[i] = searchoption_timeline[i];
				}
				searchcheck_label[0].setEnabled(true);
				re_layout();
				return true;
			} else if (searchcheck_label != null
					&& checkaction(event, searchcheck_label)) {
				for (int i = 0; i < 2; i++) {
					searchoption_label[i] = searchcheck_label[i].getState();
					Record.searchoption_label[i] = searchoption_label[i];
				}
				return true;
			} else if (zoomInCheck != null && event.target == zoomInCheck[0]) {
				ResourceTable.put(new String("zoom_ratio"), new Integer(0));
			} else if (zoomInCheck != null && event.target == zoomInCheck[1]) {
				ResourceTable.put(new String("zoom_ratio"), new Integer(1));
			} else if (zoomInCheck != null && event.target == zoomInCheck[2]) {
				ResourceTable.put(new String("zoom_ratio"), new Integer(2));
			} else if (zoomInCheck != null && event.target == zoomInCheck[3]) {
				ResourceTable.put(new String("zoom_ratio"), new Integer(3));
			}

			else if (stepsCheck != null && event.target == stepsCheck[0]) {
				ResourceTable.put(new String("zoom_steps"), new Integer(1));
			} else if (stepsCheck != null && event.target == stepsCheck[1]) {
				ResourceTable.put(new String("zoom_steps"), new Integer(2));
			} else if (stepsCheck != null && event.target == stepsCheck[2]) {
				ResourceTable.put(new String("zoom_steps"), new Integer(3));
			}
			/* END ADDITION */

		default:
			break;
		}
		return super.handleEvent(event);
	}

	private boolean checkaction(Event event, Checkbox[] check) {
		boolean action = false;
		for (int i = 0; i < check.length; i++) {
			if (event.target == check[i])
				action = true;
		}
		return action;
	}

}
