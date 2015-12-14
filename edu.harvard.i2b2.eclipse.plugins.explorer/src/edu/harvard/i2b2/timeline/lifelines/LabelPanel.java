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

public class LabelPanel extends Panel {

	private TextArea labelText;

	private int width, height;

	protected static Font font = new Font("TimesRoman", Font.BOLD, 12);
	protected FontMetrics fontMetrics = getFontMetrics(font);

	private String label;
	private String label2;

	// private int SIZE = 300;
	// private ScrollPane pane;

	// private Scrollbar bar = new Scrollbar(Scrollbar.VERTICAL, 10, 64, 10,
	// SIZE);

	public LabelPanel(int width, int height) {
		this.width = width;
		this.height = height;

		// pane = new ScrollPane();
		// pane.setSize(width, height);
		// this.add(pane, "Center");

		// this.add(bar);

		label = new String(" ");
		label2 = new String(" ");

		labelText = new TextArea(width - 6, height - 6);
		// labelText = new TextArea(10,10);

		labelText.reshape(3, 3, width - 6, height - 6);

		// labelText.setColumns(20); // 2/24/98 dan

		// add(labelText); // 2/25/98
		// labelText.appendText("hello?");

		// System.out.println("columns: " +
		// Integer.toString(labelText.getColumns()));

	}

	@Override
	public void paint(Graphics g) {

		// actual alerts

		g.setColor(Color.lightGray);
		g.draw3DRect(0, 0, width - 1, height - 1, false);
		g.draw3DRect(2, 2, width - 3, height - 3, false);

		g.setColor(Color.black);
		g.setFont(font);

		/*
		 * int currH = 20; //g.drawString("Mild Hypoglycemia", 10, currH);
		 * g.drawString("Label:", 10, currH);
		 * //g.drawString("Diabetic retinopathy", 10, currH +
		 * fontMetrics.getHeight()); g.drawString(label, 10, currH +
		 * fontMetrics.getHeight()); //g.drawString("Hypertension", 10, currH +
		 * 2*fontMetrics.getHeight());
		 * 
		 * g.drawString("url:", 10, currH + 2*fontMetrics.getHeight());
		 * 
		 * //g.drawString("Former smoker", 10, currH +
		 * 3*fontMetrics.getHeight());
		 * 
		 * g.drawString(label2, 10, currH + 3*fontMetrics.getHeight());
		 */

		StringTokenizer words = new StringTokenizer(label, " ,");
		int currentWidth = 20;
		int countQuotes = 0;
		int curTokens = 0;
		int totalCount = 0;
		String word;
		FontMetrics fm = this.getFontMetrics(font);
		String output = new String();

		int height = 20;

		// if(!label.equals(" "))
		// labelText.setText("");

		if (words.hasMoreTokens()) {
			word = new String(words.nextToken());
			totalCount = words.countTokens();
		} // assuming starts with %- and removing it

		while (words.hasMoreTokens()) {

			word = new String(words.nextToken());

			/*
			 * Added by Partha on 10-03-1998. The group of "if" statements below
			 * actually formats the data on the top right hand panel so that
			 * some sense can be made out of the data
			 */

			/*
			 * if ((word.charAt(1) == '-' || word.charAt(2) == '-') &&
			 * (word.charAt(3) == '-' ||word.charAt(4) == '-' || word.charAt(5)
			 * == '-')){
			 * 
			 * if (countDate == 0){ word = new String(" DATE : "+word);
			 * countDate++; }else if (countDate == 1){ word = new
			 * String(" TO :"+word);
			 * 
			 * }
			 * 
			 * }
			 */

			if ((word.compareTo("today")) == 0) {
				word = new String(" TO :" + word);
			}

			/*
			 * if (words.countTokens()==4){ try{ word = new
			 * String("LINECOLOR :"+word); } catch(
			 * StringIndexOutOfBoundsException e){ } }
			 */

			if ((word.compareTo("black") == 0) || (word.compareTo("blue") == 0)
					|| (word.compareTo("lightgray") == 0)
					|| (word.compareTo("lightbrown") == 0)
					|| (word.compareTo("darkbrown") == 0)
					|| (word.compareTo("green") == 0)
					|| (word.compareTo("severe") == 0)
					|| (word.compareTo("lightblue") == 0)
					|| (word.compareTo("red") == 0)) {

				word = new String(" LINECOLOR : " + word);

			}

			if (word.charAt(0) == '"') {
				if (countQuotes == 0) {

					word = new String(" URL : " + word);
					// System.out.println("Got upto URL");
					countQuotes++;

				} else if (countQuotes == 1) {

					word = new String(" ADDITIONAL INFO : " + word);
					// System.out.println("Got upto adinfo");

				}
			}

			/*
			 * if (words.countTokens() == 2) { word = new
			 * String("LABEL : "+word); }
			 */

			/*
			 * if( (word.charAt(0) == 'p') &&
			 * (Character.isDigit(word.charAt(1))) ) { word = new
			 * String("THICKNESS (in pixels) : "+word);
			 */
			// }
			/* Addition ended - Partha 10-03-1998 */

			currentWidth += fm.stringWidth(word + " ");

			curTokens = totalCount - words.countTokens();
			// System.out.println("Token count; "+curTokens);

			// if((curTokens%3)== 0) {/*modified 10-03-98 by Partha */
			// if ((words.countTokens()%4)== 0) {
			// if(currentWidth > (width-65) ) {//commented 10-03-1998
			// labelText.appendText("\n");
			// output = output + "\n";
			if (countQuotes > 0) {
				currentWidth = 20 + fm.stringWidth(word + " ");

				height += 12;
			}

			// labelText.appendText(word + " ");
			// output = output + word;

			g.drawString(word + "  ", currentWidth
					- fm.stringWidth(word + "  "), height);

			if ((word.charAt(0) == 'p') && (Character.isDigit(word.charAt(1)))) {
				g.setColor(Color.red);
				currentWidth = fm.stringWidth(word + " ");
				height += 12;
			} else {
				g.setColor(Color.black);
			}

			/*
			 * if (word.charAt(0) == '"') { currentWidth = fm.stringWidth(word +
			 * " "); height += 12; }
			 */

		}

		// if(!label.equals(" "))
		// labelText.setText("");
		// labelText.setText("Line from input file: " + label + " \n");
		// labelText.setText(output);
		labelText.appendText(label + "\n");
		// pane.add(labelText);

		// labelText.appendText("\n");

	}

	public void setLabel(String newLabel) {

		label = newLabel;

	}

	public boolean MouseDown(Event e, Object arg) {

		// if(e.shiftDown() && record.labelledAggs == false)
		// record.labelledAggs = true;

		// if(e.shiftDown() && record.labelledAggs == true)
		// record.labelledAggs = false;

		// System.out.println("got to labelPanel grep"); // debug

		return true;

	}

	@Override
	public boolean handleEvent(Event e) {

		if (e.shiftDown() && e.id == Event.MOUSE_DOWN)
			MainPanel.theTimeLinePanel.repaint();

		if (e.controlDown() && e.id == Event.MOUSE_DOWN
				&& Record.noRects == true) {
			Record.noRects = false;
			MainPanel.theTimeLinePanel.repaint();
		} else if (e.controlDown() && e.id == Event.MOUSE_DOWN
				&& Record.noRects == false) {
			Record.noRects = true;
			MainPanel.theTimeLinePanel.repaint();
		}

		return super.handleEvent(e);

	}

}
