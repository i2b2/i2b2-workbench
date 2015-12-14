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
import edu.harvard.i2b2.explorer.ui.TimeLinePanel;

/**
 * storyRecord class defines event
 */
public class StoryRecord extends GenRecord {
	private String cause = "";
	private Color rectColor;
	private int rectWidth;
	private String theUrl;
	private Hashtable attrList;
	private String inputLine;
	private Rectangle currentBarArea;
	private Rectangle currentLabelArea;
	private Color currentColor;
	private Color textColor;
	private static Color selectedColor;
	private boolean stream_selected = false;
	public int height;
	public int currentY;
	private int rwinWidth;

	private int mark, optionnum, diff1, diff2;
	public int streamX, streamY, startX, startY;
	private int[][] options = new int[4][2];
	private boolean[] lbloption = new boolean[3];

	public StoryRecord(String type, String cause, MyDate start_date,
			MyDate end_date, Color rectColor, int rectWidth, String theUrl,
			Hashtable attrList, String inputLine) {
		super(type);
		this.cause = cause;
		this.start_date = start_date;
		this.end_date = end_date;
		this.rectColor = rectColor;
		this.rectWidth = rectWidth;
		this.theUrl = theUrl;
		this.attrList = attrList;
		this.inputLine = inputLine;

		mark = 0;
		labelX = saveLabelX = -1;
		labelY = saveLabelY = -10000;
		if (lbloption[1]) {
			optionnum = 2;
		} else if (lbloption[2]) {
			optionnum = 4;
		}

		currentBarArea = new Rectangle(-1, 0, 0, 0);
		currentLabelArea = new Rectangle(-1, 0, 0, 0);
		currentColor = new Color(rectColor.getRed(), rectColor.getGreen(),
				rectColor.getBlue());
		selectedColor = new Color(185, 185, 185); // lighter gray 3/18/98
		textColor = Color.black;
	}

	@Override
	public String getInputLine() {
		return inputLine;
	}

	@Override
	public String getUrl() {
		return theUrl;
	}

	public void setUrl(String theUrl) {
		this.theUrl = theUrl;
	}

	@Override
	public boolean getSelected() {
		return selected;
	}

	@Override
	public GenRecord getSelected(int x, int y) {
		return this;
	}

	@Override
	public String getCause() {
		return cause;
	}

	@Override
	public Color getRectColor() {
		return rectColor;
	}

	@Override
	public int getRectWidth() {
		return rectWidth;
	}

	public void setRectWidth(int rectWidth) {
		this.rectWidth = rectWidth;
	}

	@Override
	public Hashtable getAttrList() {
		return attrList;
	}

	public void setImage(Image theImage) {
	}

	/**
	 * Get the width of the label by the number of pixels on the display
	 */
	public int getLabelWidth() {
		int labelWidth = 0;

		if (Record.lengthoption[1]) {
			if (Record.lbllength == 0)
				labelWidth = 0;
			else if (cause.length() > Record.lbllength)
				labelWidth = MainPanel.theTimeLinePanel.fontMetrics1
						.stringWidth(cause.substring(0, Record.lbllength));
			else
				labelWidth = MainPanel.theTimeLinePanel.fontMetrics1
						.stringWidth(cause);
		} else {
			if (cause.equals(" "))
				labelWidth = 0;
			else
				labelWidth = MainPanel.theTimeLinePanel.fontMetrics1
						.stringWidth(cause);
		}

		return labelWidth;
	}

	public Rectangle getBarArea() {
		return currentBarArea;
	}

	public Rectangle getLabelArea() {
		return currentLabelArea;
	}

	/**
	 * Check whether the event and its corrsponding label contains the point
	 * (x,y)
	 */
	@Override
	public boolean contains(int x, int y) {
		if (currentBarArea.contains(x, y) || currentLabelArea.contains(x, y)) {
			return true;
		}
		return false;
	}

	public boolean contains(int x, int y, boolean summary) {
		if (summary) {
			if ((Record.summaryoption[0] && currentLabelArea.inside(x, y))
					|| (Record.summaryoption[1] && currentBarArea.inside(x, y)))
				return true;
		}
		return false;
	}

	/**
	 * Check whether the event intersects with a rectangle
	 */
	@Override
	public GenRecord intersects(int rubber_startX, int rubber_startY,
			int rubber_endX, int rubber_endY) {
		Rectangle rubberRect = new Rectangle(rubber_startX, rubber_startY,
				rubber_endX - rubber_startX, rubber_endY - rubber_startY);
		if (currentBarArea.intersects(rubberRect)) {
			stream_selected = true;
			return this;
		}
		return null;
	}

	/**
	 * Check whether the event intersects with a circle
	 */
	public GenRecord intersects(int centerX, int centerY, int radius) {
		if (shorter(centerX, centerY, currentBarArea.x, currentBarArea.y,
				radius)
				|| shorter(centerX, centerY, currentBarArea.x
						+ currentBarArea.width, currentBarArea.y, radius)
				|| shorter(centerX, centerY, currentBarArea.x
						+ currentBarArea.width, currentBarArea.y
						+ currentBarArea.height, radius)
				|| shorter(centerX, centerY, currentBarArea.x, currentBarArea.y
						+ currentBarArea.height, radius))
			return this;
		else
			return null;
	}

	/**
	 * Check whether the distance point between point (x,y) and (x1,y1) is
	 * shorter than radius
	 */
	static boolean shorter(int x1, int y1, int x2, int y2, int radius) {
		int dx = x2 - x1, dy = y2 - y1;
		return (dx * dx + dy * dy) <= (radius * radius);
	}

	/**
	 * Label the event
	 */
	@Override
	public boolean fitlabel(int currentY, TimeLinePanel displayArea,
			boolean backtrack, int height) {
		Scale aScale = displayArea.getScale();
		int rwinWidth = displayArea.getRwinWidth();
		int rwinOffset = displayArea.getRwinOffset();
		int fontTextHeight = displayArea.getFontTextHeight();

		// Graphics g = displayArea.getGraphics(); // double buffer?
		for (int i = 0; i < 3; i++)
			lbloption[i] = Record.lbloption[i];

		if (lbloption[1]) {
			optionnum = 2;
		} else if (lbloption[2]) {
			optionnum = 4;
		}

		if (!(aScale.offScale(start_date, end_date))) {

			double scaleFactor = ((double) rwinWidth / (double) (aScale
					.getDateMin()).MinDiff(aScale.getDateMax()));
			diff1 = (int) Math
					.round(((aScale.getDateMin()).MinDiff(start_date) * scaleFactor));
			diff2 = (int) Math
					.round(((aScale.getDateMin()).MinDiff(end_date) * scaleFactor));

			int x1 = rwinOffset + diff1 + 1;
			int x2 = rwinOffset + diff1 + 1 - getLabelWidth();
			int y1 = fontTextHeight;
			int y2 = fontTextHeight + height;

			streamX = x1;
			streamY = y1;
			startX = /*rwinOffset +*/ diff1 + 1;

			if (GenRecord.xOverlap1 == null
					|| !(GenRecord.xOverlap1.identifierIsEqualTo(currentY + y1))) {
				// !inAggregate was there to try to resolve paralell aggregate
				// labelling problem...
				GenRecord.xOverlap1 = new ConflictResolver();
				GenRecord.xOverlap1.setIdentifier(currentY + y1);
			}
			// 3/28/98 only need for 4 corners
			if (lbloption[2]
					&& (xOverlap2 == null || !(xOverlap2
							.identifierIsEqualTo(currentY + y2)))) {
				// !inAggregate was there to try to resolve paralell aggregate
				// labelling problem...
				GenRecord.xOverlap2 = new ConflictResolver();
				GenRecord.xOverlap2.setIdentifier(currentY + y2);
			}

			options[0][0] = x1;
			options[0][1] = y1;
			options[1][0] = x2;
			options[1][1] = y1;
			options[2][0] = x1;
			options[2][1] = y2;
			options[3][0] = x2;
			options[3][1] = y2;

			if (lbloption[1] || lbloption[2]) {
				beforefit = fit; // 3/28/98 Is the label fits the previous
				// setting
				fit = false;
				// 3/28/98 optimize the performance. when the cause is empty,
				// there is no point to go through all the candidate positions
				// if (cause.equals(" "))
				// mark = optionnum-1;

				if (backtrack)
					mark++;

				while (mark < optionnum && !fit) {
					// delete the previous conflicts
					if (backtrack && mark > 0) {
						int startBeforeX = options[mark - 1][0];
						int startBeforeY = options[mark - 1][1];
						if (GenRecord.xOverlap1.identifierIsEqualTo(currentY
								+ startBeforeY)
								&& beforefit)
							GenRecord.xOverlap1.deleteConflicts(startBeforeX,
									startBeforeX + getLabelWidth());
						else if (lbloption[2]
								&& GenRecord.xOverlap2
										.identifierIsEqualTo(currentY
												+ startBeforeY) && beforefit)
							GenRecord.xOverlap2.deleteConflicts(startBeforeX,
									startBeforeX + getLabelWidth());
					}
					labelX = options[mark][0];
					labelY = options[mark][1];
					if ((GenRecord.xOverlap1.identifierIsEqualTo(currentY
							+ labelY) && GenRecord.xOverlap1.resolveConflicts(
							labelX, labelX + getLabelWidth()))
							|| ((lbloption[2]
									&& GenRecord.xOverlap2
											.identifierIsEqualTo(currentY
													+ labelY) && GenRecord.xOverlap2
									.resolveConflicts(labelX, labelX
											+ getLabelWidth()))))
						fit = true;
					else { // cannot fit
						labelX = -1;
						labelY = -10000;
						mark++;
					}
					beforefit = fit;
				}
			} else if (lbloption[0]) { // default one, don't label if no space
				labelX = options[0][0];
				labelY = options[0][1];
				if (GenRecord.xOverlap1.identifierIsEqualTo(currentY + labelY)
						&& GenRecord.xOverlap1.resolveConflicts(labelX, labelX
								+ getLabelWidth()))
					fit = true;
				else {
					labelX = -1;
					labelY = -10000;
					fit = false;
				}
			} else {
				labelX = -1;
				labelY = -10000;
				fit = false;
			}
		} else
			fit = true; // 2/20/98 Julia tricy, set it to true if the event is
		// Offscale
		return fit;
	}

	@Override
	public void resetlabel() {
		mark = 0;
	}

	/**
	 * Determine the starting position of the event
	 */
	public void position(int currentY, TimeLinePanel displayArea,
			boolean silhouette) {
		Scale aScale = displayArea.getScale();
		int rwinOffset = displayArea.getRwinOffset();
		int fontTextHeight = displayArea.getFontTextHeight();
		Graphics g = displayArea.getOfg();
		String tempCause = new String(""); // needed?
		rwinWidth = displayArea.getRwinWidth();
		this.currentY = currentY;
		g.setColor(Color.black);
		g.setFont(MainPanel.theTimeLinePanel.fontMetrics1.getFont());

		double scaleFactor = ((double) rwinWidth / (double) (aScale
				.getDateMin()).MinDiff(aScale.getDateMax()));
		diff1 = (int) Math
				.round(((aScale.getDateMin()).MinDiff(start_date) * scaleFactor));
		diff2 = (int) Math
				.round(((aScale.getDateMin()).MinDiff(end_date) * scaleFactor));

		if (!(aScale.offScale(start_date, end_date))) {
			startY = currentY
					+ ((silhouette) ? Record.SILPIXEL : fontTextHeight);
			startX = rwinOffset + diff1 + 1;
		} else {
			startY = -10000;
			startX = -1;
		}
	}

	/**
	 * Draw the event on the display
	 */
	@Override
	public void drawData(int currentY, TimeLinePanel displayArea,
			boolean silhouette, boolean timeline, boolean summaryrecord) {
		int fontTextHeight = displayArea.getFontTextHeight();
		Graphics g = displayArea.getOfg();

		position(currentY, displayArea, silhouette);
		if (timeline && ((Record.searchoption_timeline[1]) ? selected : true)) { // 3/10/98
			// if
			// told
			// to
			// draw
			// the
			// lines

			// g.setColor(currentColor);
			if (diff1 <= 0)
				diff1 = 1;

			if (diff1 <= rwinWidth) {
				if (diff2 >= rwinWidth)
					diff2 = rwinWidth - 1;
				if (!silhouette) {
					if (rectWidth == 1 || (getInputLine().indexOf("Value") < 0)) {
						g.setColor(currentColor);
						g.fillRect(startX, startY, (diff2 - diff1) < 3 ? (diff2
								- diff1 + 3) : (diff2 - diff1), rectWidth); // was
						// diff2-diff1
						// +
						// 1
						// //
						// and
						// was
						// diff2-diff1
						// ==
						// 0
						currentBarArea.setBounds(startX, startY,
								(diff2 - diff1) < 3 ? (diff2 - diff1 + 3)
										: (diff2 - diff1), rectWidth);

					} else {
						g.setColor(currentColor);
						g.fillRect(startX, startY, (diff2 - diff1) < 3 ? (diff2
								- diff1 + 3) : (diff2 - diff1), 22 - rectWidth); // was
						// diff2-diff1
						// +
						// 1
						// //
						// and
						// was
						// diff2-diff1
						// ==
						// 0
						// currentBarArea.setBounds(startX, startY, (diff2 -
						// diff1) < 3?(diff2-diff1+3):(diff2-diff1),
						// 22);
						g.setColor(Color.BLACK);
						g.fillRect(startX, startY + 22 - rectWidth,
								(diff2 - diff1) < 3 ? (diff2 - diff1 + 3)
										: (diff2 - diff1), rectWidth); // was
						// diff2-diff1
						// + 1
						// //
						// and
						// was
						// diff2-diff1
						// == 0
						currentBarArea.setBounds(startX, startY,
								(diff2 - diff1) < 3 ? (diff2 - diff1 + 3)
										: (diff2 - diff1), 22);
						g.setColor(currentColor);
					}
				} else {
					g.fillRect(startX, startY, (diff2 - diff1) < 3 ? (diff2
							- diff1 + 3) : (diff2 - diff1), Record.SILPIXEL);
					currentBarArea.setBounds(startX, startY,
							(diff2 - diff1) < 3 ? (diff2 - diff1 + 3)
									: (diff2 - diff1), Record.SILPIXEL);
				}
			} else
				currentBarArea.setBounds(-1, 0, 0, 0);
		}
	}

	/**
	 * Draw the label on the display
	 */
	@Override
	public void drawLabel(int currentY, TimeLinePanel displayArea,
			boolean label, boolean summaryrecord, boolean stream) {
		int fontTextHeight = displayArea.getFontTextHeight();
		Graphics g = displayArea.getOfg();
		int rwinWidth = displayArea.getRwinWidth();
		String tempCause;
		// int descent =
		// record.theTabPanel.theTimeLinePanel.fontMetrics1.getMaxDescent();
		int descent = 2;

		if (label && ((Record.searchoption_label[1]) ? selected : true)) { // if
			// told
			// to
			// draw
			// the
			// labels
			// and
			// if
			// the
			// records
			// are
			// selected
			// for
			// label_only_result
			// option
			tempCause = new String(cause);
			// System.out.println("Label: " + tempCause);
			// System.out.println(tempCause + " : " + selected);
			if (selected && summaryrecord) {
				// g.setColor(Color.blue);
				g.setColor(textColor);
				Font thisFont = MainPanel.theTimeLinePanel.fontMetrics1
						.getFont();
				// descent =
				// Toolkit.getDefaultToolkit().getFontMetrics(thisFont).getMaxDescent();
				g.setFont(new Font(thisFont.getName(), Font.BOLD, thisFont
						.getSize()));
			} else {
				// g.setColor(selectedColor); // gray
				g.setFont(MainPanel.theTimeLinePanel.fontMetrics1.getFont());
				g.setColor(textColor);
			}

			if (!stream || (stream && !stream_selected)) {
				if (Record.lengthoption[0]) {
					if (labelX >= 0) {
						g.drawString(tempCause, labelX,
								((labelY + currentY) == startY) ? (currentY
										+ labelY - descent)
										: (currentY + labelY));
						currentLabelArea.reshape(labelX,
								((currentY + labelY) == startY) ? (currentY
										+ labelY - descent - fontTextHeight)
										: (currentY + labelY - fontTextHeight),
								getLabelWidth(), fontTextHeight); // may not be
						// exactly
						// accurate
						// height
						// wise
						// yet...
					} else if (Record.symbol) {
						// add *
						char syb[] = { '*' };
						String symbol = new String(syb);
						g.setColor(Color.black);
						g.drawString(symbol, startX, startY + rectWidth + 10); // pending,
						// 10
						// is
						// hardcoded
					}
				} else if (Record.lengthoption[1]) { // truncating
					if (tempCause.length() > Record.lbllength) {
						g.drawString(tempCause.substring(0, Record.lbllength),
								labelX,
								((currentY + labelY) == startY) ? (currentY
										+ labelY - descent)
										: (currentY + labelY));
						currentLabelArea.reshape(labelX,
								((currentY + labelY) == startY) ? (currentY
										+ labelY - fontTextHeight) : (currentY
										+ labelY - descent - fontTextHeight),
								Record.lbllength, fontTextHeight);
					} else {
						if (labelX >= 0) {
							g.drawString(tempCause, labelX,
									((currentY + labelY) == startY) ? (currentY
											+ labelY - descent)
											: (currentY + labelY));
							currentLabelArea
									.reshape(
											labelX,
											((currentY + labelY) == startY) ? (currentY
													+ labelY - descent - fontTextHeight)
													: (currentY + labelY - fontTextHeight),
											getLabelWidth(), fontTextHeight); // may
							// not
							// be
							// exactly
							// accurate
							// height
							// wise
							// yet...
						} else if (Record.symbol) {
							// add *
							char syb[] = { '*' };
							String symbol = new String(syb);
							g.setColor(Color.black);
							g.drawString(symbol, startX, startY + rectWidth
									+ 10); // pending, 10 is hardcoded
						}
					}
				}
				stream_selected = false;
			}

			if (Record.arrowoption[1] && (!cause.equals(" ")) && labelX > 0
					&& (currentY + labelY) > 0)
				g
						.drawLine(
								startX,
								((currentY + labelY) == startY) ? startY
										: (startY + getRectWidth()),
								labelX == startX ? (startX + (getLabelWidth() / 3))
										: (startX - (getLabelWidth() / 3)),
								((currentY + labelY) == startY) ? (currentY
										+ labelY - 2) : (currentY + labelY
										- fontTextHeight + 6));
		}
	}

	@Override
	public void setSummaryFlag(boolean label, boolean timeline) {
		this.label = !label; // if summary is labeled, then do not label this
		// record
		this.timeline = !timeline;
	}

	@Override
	public void redraw() {
		Rectangle r = getBarArea();
		// System.out.println("original rect: "+r.x+","+r.y);
		r.x = -1;
		r.y = -1;
		r.width = -1;
		r.height = -1;
		// System.out.println("set rect to: "+getBarArea().x+","+getBarArea().y);
	}

	@Override
	public void clearConflicts(int currentY) {
		if (GenRecord.xOverlap1 != null
				&& GenRecord.xOverlap1.identifierIsEqualTo(currentY + labelY))
			GenRecord.xOverlap1.deleteConflicts(labelX, labelX
					+ getLabelWidth());
		else if (lbloption[2] && GenRecord.xOverlap2 != null
				&& GenRecord.xOverlap2.identifierIsEqualTo(currentY + labelY))
			GenRecord.xOverlap2.deleteConflicts(labelX, labelX
					+ getLabelWidth());
	}

	@Override
	public void setConflicts(int currentY) {
		if (GenRecord.xOverlap1.identifierIsEqualTo(currentY + labelY))
			GenRecord.xOverlap1.resolveConflicts(labelX, labelX
					+ getLabelWidth());
		else if (lbloption[2]
				&& GenRecord.xOverlap2.identifierIsEqualTo(currentY + labelY))
			GenRecord.xOverlap2.resolveConflicts(labelX, labelX
					+ getLabelWidth());
	}

	/**
	 * Mark the event as unselected
	 */
	@Override
	public void unSelect() {
		currentColor = new Color(selectedColor.getRed(), selectedColor
				.getGreen(), selectedColor.getBlue()); // tried this on 12/20/97
		// without unselect and
		// with doublepanels
		textColor = new Color(selectedColor.getRed(), selectedColor.getGreen(),
				selectedColor.getBlue());
		selected = false;
	}

	/**
	 * Mark the event as selected
	 */
	@Override
	public void select() {
		currentColor = rectColor;
		textColor = Color.black;
		selected = true;
	}
}