/*
 * Copyright (c) 2006-2010 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:  
 *     
 */
package edu.harvard.i2b2.analysis.ui;

import java.util.HashMap;
import java.util.Iterator; //import java.util.Map;
import java.util.ArrayList;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * @author wwg0 custom color dialog with web safe colors
 * 
 */
public class WebColorDialog extends Dialog {
	// private String dialogText;

	private RGB input;

	private RGB selectedRGB;

	private Text textColor;

	private Text textRGB;

	// private RGB startRGB;

	private Color startColor;

	// hash map with rgb web color values from Chris
	private HashMap colorMap = new HashMap();

	// array list with rgb web colors in sorted order from hash map
	private ArrayList rgbList = new ArrayList();

	// array list of colors so they can be disposed
	private ArrayList colorList = new ArrayList();

	// array list of BorderLabel (custom canvas inner class)
	private ArrayList borderLabelList = new ArrayList();

	// number of columns in grid
	private int numCols = 12;

	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 */
	public WebColorDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public WebColorDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Web Color Dialog");
	}

	/**
	 * Sets the input
	 * 
	 * @param input
	 *            the new input
	 */
	public void setRGB(RGB input) {
		this.input = input;
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public RGB open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return input;
		// return selectedRGB;
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {

		shell.setLayout(new FillLayout());
		Composite top = new Composite(shell, SWT.NONE);
		// make a grid with 1 column 3 rows (message, grid, button row)

		top.setLayout(new GridLayout(1, true));

		// Show the message
		Label label = new Label(top, SWT.NONE);
		label.setText("Web Color Selection");
		GridData labelData = new GridData();
		labelData.horizontalAlignment = SWT.FILL;
		label.setLayoutData(labelData);

		// create composite to hold the grid of colors
		Composite colorGridComposite = new Composite(top, SWT.NONE);
		colorGridComposite.setLayout(new GridLayout(numCols, true));
		GridData gridData2 = new GridData();
		colorGridComposite.setLayoutData(gridData2);

		// create list of RGB web values
		createRGBListNew();
		// System.out.println("rgbListSize=" +rgbList.size());

		// iterate through rgb list, create color list (so can dispose after)
		// and borderLabelList (inner class)

		Iterator iter = rgbList.iterator();
		while (iter.hasNext()) {
			RGB rgb = (RGB) iter.next();
			// create list colors
			final Color curColor = new Color(colorGridComposite.getDisplay(),
					rgb);
			colorList.add(curColor);

			// create list of border labels
			BorderLabel borderLabel = new BorderLabel(colorGridComposite,
					SWT.NONE);
			borderLabel.setBackground(curColor);

			// set border size 3 for input color
			if (rgb.equals(input)) {
				borderLabel.setBorderSize(2);
			} else {
				borderLabel.setBorderSize(0);
			}
			borderLabel.setSize(16, 16);
			borderLabel.setBorderStyle(SWT.LINE_SOLID);

			// add listener to get selected rgb color
			borderLabel.addListener(SWT.MouseDown, new Listener() {

				public void handleEvent(Event event) {

					BorderLabel sbl = (BorderLabel) event.widget;
					selectedRGB = sbl.getBackground().getRGB();
					// iterate through borderLabelList and set all borders to 0
					Iterator bliter = borderLabelList.iterator();
					while (bliter.hasNext()) {
						BorderLabel mbl = (BorderLabel) bliter.next();
						mbl.setBorderSize(0);
					}
					// set currently selected to 3
					sbl.setBorderSize(2);
					// System.out.println("RGB=" + selectedRGB);
					// set color in selected color text box
					textColor.setBackground(curColor);
					// tring rgbString=selectedRGB.red + ", "+ selectedRGB.green
					// + ", " + selectedRGB.red;

					textRGB.setText(selectedRGB.toString());
					// textRGB.setText(rgbString);

				}

			});
			// add to list
			borderLabelList.add(borderLabel);
			// put in grid
			GridData gridDataLabel = new GridData(20, 20);
			borderLabel.setLayoutData(gridDataLabel);
		}

		// create composite to hold selected text label, color, and cancel and
		// ok buttons
		Composite bot = new Composite(top, SWT.NONE);
		bot.setLayout(new GridLayout(1, true));

		GridData botData = new GridData();
		botData.horizontalAlignment = SWT.FILL;
		botData.verticalAlignment = SWT.FILL;
		bot.setLayoutData(botData);

		// Button button= new Button(bot, SWT.PUSH);
		// button.setText("test button");
		// creates the bottom row of buttons and text
		createBottomRow(bot);

	}

	public void createBottomRow(Composite parent) {

		// Create a label with Selected color text
		Label textLabel = new Label(parent, SWT.NONE);
		textLabel.setText("Selected:");

		// Create a label to show selected color
		textColor = new Text(parent, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		textColor.setText("    ");
		// set default color for selected text
		startColor = new Color(getParent().getDisplay(), input);
		textColor.setBackground(startColor);

		// Create a label with RGB text
		Label rgbLabel = new Label(parent, SWT.NONE);
		rgbLabel.setText("");

		// Create a label to show selected RGB
		textRGB = new Text(parent, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		// set default rgb
		// String rgbString=input.red + ", "+ input.green + ", " + input.red;
		textRGB.setText(input.toString());
		// textRGB.setText(rgbString);

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		final Button ok = new Button(parent, SWT.PUSH);
		ok.setText(" OK ");
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// input = text.getText();
				input = selectedRGB;
				// dispose of colors
				disposeColors();
				ok.getParent().getShell().close();

			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		final Button cancel = new Button(parent, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = null;
				disposeColors();
				cancel.getParent().getShell().close();
			}
		});

		// create form layoutfor bottom row
		FormLayout botForm = new FormLayout();
		botForm.marginWidth = 5;
		botForm.spacing = 5;
		parent.setLayout(botForm);

		// FormData botFormData = new FormData();
		// botFormData.left=new FormAttachment(0);
		// botFormData.right= new FormAttachment(100);
		// bot.setLayoutData(botFormData);
		FormData txData = new FormData();
		txData.top = new FormAttachment(ok, 0, SWT.CENTER);
		txData.left = new FormAttachment(0);
		textLabel.setLayoutData(txData);

		FormData textData = new FormData();
		textData.top = new FormAttachment(ok, 0, SWT.CENTER);
		textData.left = new FormAttachment(textLabel);
		textColor.setLayoutData(textData);

		FormData rgbLabelData = new FormData();
		rgbLabelData.top = new FormAttachment(ok, 0, SWT.CENTER);
		rgbLabelData.left = new FormAttachment(textColor);
		rgbLabel.setLayoutData(rgbLabelData);

		FormData rgbData = new FormData();
		rgbData.top = new FormAttachment(ok, 0, SWT.CENTER);
		rgbData.left = new FormAttachment(rgbLabel);
		rgbData.right = new FormAttachment(cancel);
		textRGB.setLayoutData(rgbData);

		FormData cancelData = new FormData();
		cancelData.top = new FormAttachment(ok, 0, SWT.CENTER);
		cancelData.right = new FormAttachment(ok);
		cancel.setLayoutData(cancelData);

		FormData okData = new FormData();
		okData.top = new FormAttachment(0);
		okData.right = new FormAttachment(100);
		ok.setLayoutData(okData);

	}

	// shell.setDefaultButton(ok);
	/**
	 * disposes of colors
	 */
	public void disposeColors() {
		// dispose of colors
		if (colorList != null) {
			Iterator iter = colorList.iterator();
			while (iter.hasNext()) {
				Color mycol = (Color) iter.next();

				if (mycol != null) {
					mycol.dispose();
				}

			}

		}

		if (startColor != null) {
			startColor.dispose();
		}

	}

	/**
	 * creates a hash map of web safe rgb values
	 */
	public void createColorMap() {

		colorMap.put(new RGB(255, 255, 255), "white");
		colorMap.put(new RGB(255, 255, 224), "lightyellow");
		colorMap.put(new RGB(255, 255, 205), "blanchedalmond");
		colorMap.put(new RGB(255, 255, 0), "yellow");
		colorMap.put(new RGB(255, 250, 250), "snow");
		colorMap.put(new RGB(255, 250, 240), "floralwhite");
		colorMap.put(new RGB(255, 250, 205), "lemonchiffon");
		colorMap.put(new RGB(255, 248, 220), "cornsilk");
		colorMap.put(new RGB(255, 245, 238), "seashell");
		colorMap.put(new RGB(255, 240, 245), "lavenderblush");
		colorMap.put(new RGB(255, 240, 240), "ivory");
		colorMap.put(new RGB(255, 239, 213), "papayawhip");
		colorMap.put(new RGB(255, 239, 213), "peachpuff");
		colorMap.put(new RGB(255, 228, 225), "mistyrose");
		colorMap.put(new RGB(255, 228, 196), "bisque");
		colorMap.put(new RGB(255, 228, 181), "moccasin");
		colorMap.put(new RGB(255, 222, 173), "navajowhite");
		colorMap.put(new RGB(255, 215, 0), "gold");
		colorMap.put(new RGB(255, 192, 203), "pink");
		colorMap.put(new RGB(255, 182, 193), "lightpink");
		colorMap.put(new RGB(255, 165, 0), "orange");
		colorMap.put(new RGB(255, 160, 122), "lightsalmon");
		colorMap.put(new RGB(255, 140, 0), "darkorange");
		colorMap.put(new RGB(255, 127, 80), "coral");
		colorMap.put(new RGB(255, 105, 180), "hotpink");
		colorMap.put(new RGB(255, 69, 0), "orangered");
		colorMap.put(new RGB(255, 20, 147), "deeppink");
		colorMap.put(new RGB(255, 0, 255), "magenta");
		colorMap.put(new RGB(255, 0, 255), "fuchsia");
		colorMap.put(new RGB(255, 0, 0), "red");
		colorMap.put(new RGB(253, 245, 230), "oldlace");
		colorMap.put(new RGB(253, 99, 71), "tomato");
		colorMap.put(new RGB(250, 250, 210), "lightgoldenrodyellow");
		colorMap.put(new RGB(250, 240, 230), "linen");
		colorMap.put(new RGB(250, 235, 215), "antiquewhite");
		colorMap.put(new RGB(250, 128, 114), "salmon");
		colorMap.put(new RGB(248, 248, 255), "ghostwhite");
		colorMap.put(new RGB(245, 255, 250), "mintcream");
		colorMap.put(new RGB(245, 245, 245), "whitesmoke");
		colorMap.put(new RGB(245, 245, 220), "beige");
		colorMap.put(new RGB(245, 222, 179), "wheat");
		colorMap.put(new RGB(244, 164, 96), "sandybrown");
		colorMap.put(new RGB(240, 255, 255), "azure");
		colorMap.put(new RGB(240, 255, 240), "honeydew");
		colorMap.put(new RGB(240, 248, 255), "aliceblue");
		colorMap.put(new RGB(240, 230, 140), "khaki");
		colorMap.put(new RGB(240, 128, 128), "lightcoral");
		colorMap.put(new RGB(238, 232, 170), "palegoldenrod");
		colorMap.put(new RGB(238, 130, 238), "violet");
		colorMap.put(new RGB(233, 150, 122), "darksalmon");
		colorMap.put(new RGB(230, 230, 250), "lavender");
		colorMap.put(new RGB(224, 255, 255), "lightcyan");
		colorMap.put(new RGB(222, 184, 135), "burlywood");
		colorMap.put(new RGB(221, 160, 221), "plum");
		colorMap.put(new RGB(220, 220, 220), "gainsboro");
		colorMap.put(new RGB(220, 20, 60), "crimson");
		colorMap.put(new RGB(219, 112, 147), "palevioletred");
		colorMap.put(new RGB(218, 165, 32), "goldenrod");
		colorMap.put(new RGB(218, 112, 214), "orchid");
		colorMap.put(new RGB(216, 191, 216), "thistle");
		colorMap.put(new RGB(211, 211, 211), "lightgrey");
		colorMap.put(new RGB(210, 180, 140), "tan");
		colorMap.put(new RGB(210, 105, 30), "chocolate");
		colorMap.put(new RGB(205, 133, 63), "peru");
		colorMap.put(new RGB(205, 92, 92), "indianred");
		colorMap.put(new RGB(199, 21, 133), "mediumvioletred");
		colorMap.put(new RGB(192, 192, 192), "silver");
		colorMap.put(new RGB(189, 183, 107), "darkkhaki");
		colorMap.put(new RGB(188, 143, 143), "rosybrown");
		colorMap.put(new RGB(186, 85, 211), "mediumorchid");
		colorMap.put(new RGB(184, 134, 11), "darkgoldenrod");
		colorMap.put(new RGB(178, 34, 34), "firebrick");
		colorMap.put(new RGB(176, 224, 230), "powderblue");
		colorMap.put(new RGB(176, 196, 222), "lightsteelblue");
		colorMap.put(new RGB(175, 238, 238), "paleturquoise");
		colorMap.put(new RGB(173, 255, 47), "greenyellow");
		colorMap.put(new RGB(173, 216, 230), "lightblue");
		colorMap.put(new RGB(169, 169, 169), "darkgray");
		colorMap.put(new RGB(165, 42, 42), "brown");
		colorMap.put(new RGB(160, 82, 45), "sienna");
		colorMap.put(new RGB(154, 205, 50), "yellowgreen");
		colorMap.put(new RGB(153, 50, 204), "darkorchid");
		colorMap.put(new RGB(152, 251, 152), "palegreen");
		colorMap.put(new RGB(148, 0, 211), "darkviolet");
		colorMap.put(new RGB(147, 112, 219), "mediumpurple");
		colorMap.put(new RGB(144, 238, 144), "lightgreen");
		colorMap.put(new RGB(143, 188, 143), "darkseagreen");
		colorMap.put(new RGB(139, 69, 19), "sputlebrown");
		colorMap.put(new RGB(139, 0, 139), "darkmagenta");
		colorMap.put(new RGB(139, 0, 0), "darkred");
		colorMap.put(new RGB(138, 43, 226), "blueviolet");
		colorMap.put(new RGB(135, 206, 250), "lightskyblue");
		colorMap.put(new RGB(135, 206, 235), "skyblue");
		colorMap.put(new RGB(128, 128, 128), "gray");
		colorMap.put(new RGB(128, 128, 0), "olive");
		colorMap.put(new RGB(128, 0, 128), "purple");
		colorMap.put(new RGB(128, 0, 0), "maroon");
		colorMap.put(new RGB(127, 255, 212), "aquamarine");
		colorMap.put(new RGB(127, 255, 0), "chartreuse");
		colorMap.put(new RGB(124, 252, 0), "lawngreen");
		colorMap.put(new RGB(123, 104, 238), "mediumslateblue");
		colorMap.put(new RGB(119, 136, 153), "lightslategray");
		colorMap.put(new RGB(112, 128, 144), "slategray");
		colorMap.put(new RGB(107, 142, 35), "olivedrab");
		colorMap.put(new RGB(106, 90, 205), "slateblue");
		colorMap.put(new RGB(105, 105, 105), "dimgray");
		colorMap.put(new RGB(102, 205, 170), "mediumaquamarine");
		colorMap.put(new RGB(100, 149, 237), "cornflowerblue");
		colorMap.put(new RGB(95, 158, 160), "cadetblue");
		colorMap.put(new RGB(85, 107, 47), "darkolivegreen");
		colorMap.put(new RGB(75, 0, 130), "indigo");
		colorMap.put(new RGB(72, 209, 204), "mediumturquoise");
		colorMap.put(new RGB(72, 61, 139), "darkslateblue");
		colorMap.put(new RGB(70, 130, 180), "steelblue");
		colorMap.put(new RGB(65, 105, 225), "royalblue");
		colorMap.put(new RGB(64, 224, 208), "turquoise");
		colorMap.put(new RGB(60, 179, 113), "mediumseagreen");
		colorMap.put(new RGB(50, 205, 50), "limegreen");
		colorMap.put(new RGB(47, 79, 79), "darkslategray");
		colorMap.put(new RGB(46, 139, 87), "seagreen");
		colorMap.put(new RGB(34, 139, 34), "forestgreen");
		colorMap.put(new RGB(32, 178, 170), "lightseagreen");
		colorMap.put(new RGB(30, 144, 255), "dodgerblue");
		colorMap.put(new RGB(25, 25, 112), "midnightblue");
		colorMap.put(new RGB(0, 255, 255), "cyan");
		colorMap.put(new RGB(0, 255, 255), "aqua");
		colorMap.put(new RGB(0, 255, 127), "springgreen");
		colorMap.put(new RGB(0, 255, 0), "lime");
		colorMap.put(new RGB(0, 250, 154), "mediumspringgreen");
		colorMap.put(new RGB(0, 206, 209), "darkturquoise");
		colorMap.put(new RGB(0, 191, 255), "deepskyblue");
		colorMap.put(new RGB(0, 139, 139), "darkcyan");
		colorMap.put(new RGB(0, 128, 128), "teal");
		colorMap.put(new RGB(0, 128, 0), "green");
		colorMap.put(new RGB(0, 100, 0), "darkgreen");
		colorMap.put(new RGB(0, 0, 255), "blue");
		colorMap.put(new RGB(0, 0, 205), "mediumblue");
		colorMap.put(new RGB(0, 0, 139), "darkblue");
		colorMap.put(new RGB(0, 0, 128), "navy");
		colorMap.put(new RGB(0, 0, 0), "black");

	}

	/**
	 * creates a list of RGB values (made from hashmap above)
	 */
	public void createRGBList() {

		rgbList.add(new RGB(255, 255, 255));
		rgbList.add(new RGB(255, 255, 224));
		rgbList.add(new RGB(255, 255, 205));
		rgbList.add(new RGB(255, 255, 0));
		rgbList.add(new RGB(255, 250, 250));
		rgbList.add(new RGB(255, 250, 240));
		rgbList.add(new RGB(255, 250, 205));
		rgbList.add(new RGB(255, 248, 220));
		rgbList.add(new RGB(255, 245, 238));
		rgbList.add(new RGB(255, 240, 245));
		rgbList.add(new RGB(255, 240, 240));
		rgbList.add(new RGB(255, 239, 213));
		rgbList.add(new RGB(255, 239, 213));
		rgbList.add(new RGB(255, 228, 225));
		rgbList.add(new RGB(255, 228, 196));
		rgbList.add(new RGB(255, 228, 181));
		rgbList.add(new RGB(255, 222, 173));
		rgbList.add(new RGB(255, 215, 0));
		rgbList.add(new RGB(255, 192, 203));
		rgbList.add(new RGB(255, 182, 193));
		rgbList.add(new RGB(255, 165, 0));
		rgbList.add(new RGB(255, 160, 122));
		rgbList.add(new RGB(255, 140, 0));
		rgbList.add(new RGB(255, 127, 80));
		rgbList.add(new RGB(255, 105, 180));
		rgbList.add(new RGB(255, 69, 0));
		rgbList.add(new RGB(255, 20, 147));
		rgbList.add(new RGB(255, 0, 255));
		rgbList.add(new RGB(255, 0, 255));
		rgbList.add(new RGB(255, 0, 0));
		rgbList.add(new RGB(253, 245, 230));
		rgbList.add(new RGB(253, 99, 71));
		rgbList.add(new RGB(250, 250, 210));
		rgbList.add(new RGB(250, 240, 230));
		rgbList.add(new RGB(250, 235, 215));
		rgbList.add(new RGB(250, 128, 114));
		rgbList.add(new RGB(248, 248, 255));
		rgbList.add(new RGB(245, 255, 250));
		rgbList.add(new RGB(245, 245, 245));
		rgbList.add(new RGB(245, 222, 179));
		rgbList.add(new RGB(244, 164, 96));
		rgbList.add(new RGB(240, 255, 255));
		rgbList.add(new RGB(240, 255, 240));
		rgbList.add(new RGB(240, 248, 255));
		rgbList.add(new RGB(240, 230, 140));
		rgbList.add(new RGB(240, 128, 128));
		rgbList.add(new RGB(238, 232, 170));
		rgbList.add(new RGB(238, 130, 238));
		rgbList.add(new RGB(233, 150, 122));
		rgbList.add(new RGB(230, 230, 250));
		rgbList.add(new RGB(224, 255, 255));
		rgbList.add(new RGB(222, 184, 135));
		rgbList.add(new RGB(221, 160, 221));
		rgbList.add(new RGB(220, 220, 220));
		rgbList.add(new RGB(220, 20, 60));
		rgbList.add(new RGB(219, 112, 147));
		rgbList.add(new RGB(218, 165, 32));
		rgbList.add(new RGB(218, 112, 214));
		rgbList.add(new RGB(216, 191, 216));
		rgbList.add(new RGB(211, 211, 211));
		rgbList.add(new RGB(210, 180, 140));
		rgbList.add(new RGB(210, 105, 30));
		rgbList.add(new RGB(205, 133, 63));
		rgbList.add(new RGB(205, 92, 92));
		rgbList.add(new RGB(199, 21, 133));
		rgbList.add(new RGB(192, 192, 192));
		rgbList.add(new RGB(189, 183, 107));
		rgbList.add(new RGB(188, 143, 143));
		rgbList.add(new RGB(186, 85, 211));
		rgbList.add(new RGB(184, 134, 11));
		rgbList.add(new RGB(178, 34, 34));
		rgbList.add(new RGB(176, 224, 230));
		rgbList.add(new RGB(176, 196, 222));
		rgbList.add(new RGB(175, 238, 238));
		rgbList.add(new RGB(173, 255, 47));
		rgbList.add(new RGB(173, 216, 230));
		rgbList.add(new RGB(169, 169, 169));
		rgbList.add(new RGB(165, 42, 42));
		rgbList.add(new RGB(160, 82, 45));
		rgbList.add(new RGB(154, 205, 50));
		rgbList.add(new RGB(153, 50, 204));
		rgbList.add(new RGB(152, 251, 152));
		rgbList.add(new RGB(148, 0, 211));
		rgbList.add(new RGB(147, 112, 219));
		rgbList.add(new RGB(144, 238, 144));
		rgbList.add(new RGB(143, 188, 143));
		rgbList.add(new RGB(139, 69, 19));
		rgbList.add(new RGB(139, 0, 139));
		rgbList.add(new RGB(139, 0, 0));
		rgbList.add(new RGB(138, 43, 226));
		rgbList.add(new RGB(135, 206, 250));
		rgbList.add(new RGB(135, 206, 235));
		rgbList.add(new RGB(128, 128, 128));
		rgbList.add(new RGB(128, 128, 0));
		rgbList.add(new RGB(128, 0, 128));
		rgbList.add(new RGB(128, 0, 0));
		rgbList.add(new RGB(127, 255, 212));
		rgbList.add(new RGB(127, 255, 0));
		rgbList.add(new RGB(124, 252, 0));
		rgbList.add(new RGB(123, 104, 238));
		rgbList.add(new RGB(119, 136, 153));
		rgbList.add(new RGB(112, 128, 144));
		rgbList.add(new RGB(107, 142, 35));
		rgbList.add(new RGB(106, 90, 205));
		rgbList.add(new RGB(105, 105, 105));
		rgbList.add(new RGB(102, 205, 170));
		rgbList.add(new RGB(100, 149, 237));
		rgbList.add(new RGB(95, 158, 160));
		rgbList.add(new RGB(85, 107, 47));
		rgbList.add(new RGB(75, 0, 130));
		rgbList.add(new RGB(72, 209, 204));
		rgbList.add(new RGB(72, 61, 139));
		rgbList.add(new RGB(70, 130, 180));
		rgbList.add(new RGB(65, 105, 225));
		rgbList.add(new RGB(64, 224, 208));
		rgbList.add(new RGB(60, 179, 113));
		rgbList.add(new RGB(50, 205, 50));
		rgbList.add(new RGB(47, 79, 79));
		rgbList.add(new RGB(46, 139, 87));
		rgbList.add(new RGB(34, 139, 34));
		rgbList.add(new RGB(32, 178, 170));
		rgbList.add(new RGB(30, 144, 255));
		rgbList.add(new RGB(25, 25, 112));
		rgbList.add(new RGB(0, 255, 255));
		rgbList.add(new RGB(0, 255, 255));
		rgbList.add(new RGB(0, 255, 127));
		rgbList.add(new RGB(0, 255, 0));
		rgbList.add(new RGB(0, 250, 154));
		rgbList.add(new RGB(0, 206, 209));
		rgbList.add(new RGB(0, 191, 255));
		rgbList.add(new RGB(0, 139, 139));
		rgbList.add(new RGB(0, 128, 128));
		rgbList.add(new RGB(0, 128, 0));
		rgbList.add(new RGB(0, 100, 0));
		rgbList.add(new RGB(0, 0, 255));
		rgbList.add(new RGB(0, 0, 205));
		rgbList.add(new RGB(0, 0, 139));
		rgbList.add(new RGB(0, 0, 128));
		rgbList.add(new RGB(0, 0, 0));

	}

	public void createColorMapNew() {
		colorMap.put(new RGB(255, 255, 255), "white");
		colorMap.put(new RGB(255, 250, 250), "snow");
		colorMap.put(new RGB(248, 248, 255), "ghostwhite");
		colorMap.put(new RGB(255, 255, 240), "ivory");
		colorMap.put(new RGB(245, 255, 250), "mintcream");
		colorMap.put(new RGB(240, 255, 255), "azure");
		colorMap.put(new RGB(255, 250, 240), "floralwhite");
		colorMap.put(new RGB(240, 248, 255), "aliceblue");
		colorMap.put(new RGB(255, 240, 245), "lavenderblush");
		colorMap.put(new RGB(255, 245, 238), "seashell");
		colorMap.put(new RGB(245, 245, 245), "whitesmoke");
		colorMap.put(new RGB(240, 255, 240), "honeydew");
		colorMap.put(new RGB(255, 255, 224), "lightyellow");
		colorMap.put(new RGB(224, 255, 255), "lightcyan");
		colorMap.put(new RGB(253, 245, 230), "oldlace");
		colorMap.put(new RGB(255, 248, 220), "cornsilk");
		colorMap.put(new RGB(250, 240, 230), "linen");
		colorMap.put(new RGB(255, 250, 205), "lemonchiffon");
		colorMap.put(new RGB(250, 250, 210), "lightgoldenrodyellow");
		colorMap.put(new RGB(245, 245, 220), "beige");
		colorMap.put(new RGB(230, 230, 250), "lavender");
		colorMap.put(new RGB(255, 228, 225), "mistyrose");
		colorMap.put(new RGB(255, 239, 213), "papayawhip");
		colorMap.put(new RGB(255, 245, 200), "lightbrown");
		colorMap.put(new RGB(250, 235, 215), "antiquewhite");
		colorMap.put(new RGB(255, 235, 205), "blanchedalmond");
		colorMap.put(new RGB(255, 228, 196), "bisque");
		colorMap.put(new RGB(255, 236, 175), "darkbrown");
		colorMap.put(new RGB(255, 228, 181), "moccasin");
		colorMap.put(new RGB(220, 220, 220), "gainsboro");
		colorMap.put(new RGB(255, 218, 185), "peachpuff");
		colorMap.put(new RGB(175, 238, 238), "paleturquoise");
		colorMap.put(new RGB(255, 222, 173), "navajowhite");
		colorMap.put(new RGB(255, 192, 203), "pink");
		colorMap.put(new RGB(245, 222, 179), "wheat");
		colorMap.put(new RGB(238, 232, 170), "palegoldenrod");
		// colorMap.put(new RGB(211, 211, 211), "lightgray");
		colorMap.put(new RGB(211, 211, 211), "lightgrey");
		colorMap.put(new RGB(255, 182, 193), "lightpink");
		colorMap.put(new RGB(176, 224, 230), "powderblue");
		colorMap.put(new RGB(216, 191, 216), "thistle");
		colorMap.put(new RGB(173, 216, 230), "lightblue");
		colorMap.put(new RGB(240, 230, 140), "khaki");
		colorMap.put(new RGB(238, 130, 238), "violet");
		colorMap.put(new RGB(221, 160, 221), "plum");
		colorMap.put(new RGB(176, 196, 222), "lightsteelblue");
		colorMap.put(new RGB(127, 255, 212), "aquamarine");
		colorMap.put(new RGB(135, 206, 250), "lightskyblue");
		colorMap.put(new RGB(238, 221, 130), "lightgoldenrod");
		colorMap.put(new RGB(135, 206, 235), "skyblue");
		colorMap.put(new RGB(190, 190, 190), "gray");
		colorMap.put(new RGB(152, 251, 152), "palegreen");
		colorMap.put(new RGB(218, 112, 214), "orchid");
		colorMap.put(new RGB(222, 184, 135), "burlywood");
		colorMap.put(new RGB(255, 105, 180), "hotpink");
		colorMap.put(new RGB(255, 105, 180), "severe");
		colorMap.put(new RGB(255, 160, 122), "lightsalmon");
		colorMap.put(new RGB(210, 180, 140), "tan");
		colorMap.put(new RGB(255, 255, 0), "yellow");
		colorMap.put(new RGB(255, 0, 255), "magenta");
		colorMap.put(new RGB(0, 255, 255), "cyan");
		colorMap.put(new RGB(233, 150, 122), "darksalmon");
		colorMap.put(new RGB(244, 164, 96), "sandybrown");
		colorMap.put(new RGB(132, 112, 255), "lightslateblue");
		colorMap.put(new RGB(240, 128, 128), "lightcoral");
		colorMap.put(new RGB(64, 224, 208), "turquoise");
		colorMap.put(new RGB(250, 128, 114), "salmon");
		colorMap.put(new RGB(100, 149, 237), "cornflowerblue");
		colorMap.put(new RGB(72, 209, 204), "mediumturquoise");
		colorMap.put(new RGB(186, 85, 211), "mediumorchid");
		colorMap.put(new RGB(189, 183, 107), "darkkhaki");
		colorMap.put(new RGB(219, 112, 147), "palevioletred");
		colorMap.put(new RGB(147, 112, 219), "mediumpurple");
		colorMap.put(new RGB(102, 205, 170), "mediumaquamarine");
		colorMap.put(new RGB(188, 143, 143), "rosybrown");
		colorMap.put(new RGB(143, 188, 143), "darkseagreen");
		colorMap.put(new RGB(255, 215, 0), "gold");
		colorMap.put(new RGB(123, 104, 238), "mediumslateblue");
		colorMap.put(new RGB(255, 127, 80), "coral");
		colorMap.put(new RGB(0, 191, 255), "deepskyblue");
		colorMap.put(new RGB(160, 32, 240), "purple");
		colorMap.put(new RGB(30, 144, 255), "dodgerblue");
		colorMap.put(new RGB(255, 99, 71), "tomato");
		colorMap.put(new RGB(255, 20, 147), "deeppink");
		colorMap.put(new RGB(255, 165, 0), "orange");
		colorMap.put(new RGB(218, 165, 32), "goldenrod");
		colorMap.put(new RGB(0, 206, 209), "darkturquoise");
		colorMap.put(new RGB(95, 158, 160), "cadetblue");
		colorMap.put(new RGB(154, 205, 50), "yellowgreen");
		colorMap.put(new RGB(119, 136, 153), "lightslategray");
		// colorMap.put(new RGB(119, 136, 153), "lightslategrey");
		colorMap.put(new RGB(153, 50, 204), "darkorchid");
		colorMap.put(new RGB(138, 43, 226), "blueviolet");
		colorMap.put(new RGB(0, 250, 154), "mediumspringgreen");
		colorMap.put(new RGB(205, 133, 63), "peru");
		colorMap.put(new RGB(106, 90, 205), "slateblue");
		colorMap.put(new RGB(255, 140, 0), "darkorange");
		colorMap.put(new RGB(65, 105, 225), "royalblue");
		colorMap.put(new RGB(205, 92, 92), "indianred");
		colorMap.put(new RGB(208, 32, 144), "violetred");
		colorMap.put(new RGB(112, 128, 144), "slategray");
		// colorMap.put(new RGB(112, 128, 144), "slategrey");
		colorMap.put(new RGB(127, 255, 0), "chartreuse");
		colorMap.put(new RGB(0, 255, 127), "springgreen");
		colorMap.put(new RGB(70, 130, 180), "steelblue");
		colorMap.put(new RGB(32, 178, 170), "lightseagreen");
		colorMap.put(new RGB(124, 252, 0), "lawngreen");
		colorMap.put(new RGB(148, 0, 211), "darkviolet");
		colorMap.put(new RGB(199, 21, 133), "mediumvioletred");
		colorMap.put(new RGB(60, 179, 113), "mediumseagreen");
		colorMap.put(new RGB(210, 105, 30), "chocolate");
		colorMap.put(new RGB(184, 134, 11), "darkgoldenrod");
		colorMap.put(new RGB(255, 69, 0), "orangered");
		colorMap.put(new RGB(176, 48, 96), "maroon");
		colorMap.put(new RGB(105, 105, 105), "dimgray");
		// colorMap.put(new RGB(105, 105, 105), "dimgrey");
		colorMap.put(new RGB(50, 205, 50), "limegreen");
		colorMap.put(new RGB(160, 82, 45), "sienna");
		colorMap.put(new RGB(107, 142, 35), "olivedrab");
		colorMap.put(new RGB(72, 61, 139), "darkslateblue");
		colorMap.put(new RGB(46, 139, 87), "seagreen");
		colorMap.put(new RGB(255, 0, 0), "red");
		colorMap.put(new RGB(0, 255, 0), "green");
		colorMap.put(new RGB(0, 0, 255), "blue");
		colorMap.put(new RGB(165, 42, 42), "brown");
		colorMap.put(new RGB(178, 34, 34), "firebrick");
		colorMap.put(new RGB(85, 107, 47), "darkolivegreen");
		colorMap.put(new RGB(139, 69, 19), "saddlebrown");
		colorMap.put(new RGB(34, 139, 34), "forestgreen");
		colorMap.put(new RGB(47, 79, 79), "darkslategray");
		// colorMap.put(new RGB(47, 79, 79), "darkslategrey");
		colorMap.put(new RGB(0, 0, 205), "mediumblue");
		colorMap.put(new RGB(25, 25, 112), "midnightblue");
		colorMap.put(new RGB(0, 0, 128), "navy");
		colorMap.put(new RGB(0, 0, 128), "navyblue");
		colorMap.put(new RGB(0, 100, 0), "darkgreen");
		colorMap.put(new RGB(0, 0, 0), "black");

	}

	public void createRGBListNew() {
		rgbList.add(new RGB(255, 255, 255));
		rgbList.add(new RGB(255, 250, 250));
		rgbList.add(new RGB(248, 248, 255));
		rgbList.add(new RGB(255, 255, 240));
		rgbList.add(new RGB(245, 255, 250));
		rgbList.add(new RGB(240, 255, 255));
		rgbList.add(new RGB(255, 250, 240));
		rgbList.add(new RGB(240, 248, 255));
		rgbList.add(new RGB(255, 240, 245));
		rgbList.add(new RGB(255, 245, 238));
		rgbList.add(new RGB(245, 245, 245));
		rgbList.add(new RGB(240, 255, 240));
		rgbList.add(new RGB(255, 255, 224));
		rgbList.add(new RGB(224, 255, 255));
		rgbList.add(new RGB(253, 245, 230));
		rgbList.add(new RGB(255, 248, 220));
		rgbList.add(new RGB(250, 240, 230));
		rgbList.add(new RGB(255, 250, 205));
		rgbList.add(new RGB(250, 250, 210));
		rgbList.add(new RGB(245, 245, 220));
		rgbList.add(new RGB(230, 230, 250));
		rgbList.add(new RGB(255, 228, 225));
		rgbList.add(new RGB(255, 239, 213));
		rgbList.add(new RGB(255, 245, 200));
		rgbList.add(new RGB(250, 235, 215));
		rgbList.add(new RGB(255, 235, 205));
		rgbList.add(new RGB(255, 228, 196));
		rgbList.add(new RGB(255, 236, 175));
		rgbList.add(new RGB(255, 228, 181));
		rgbList.add(new RGB(220, 220, 220));
		rgbList.add(new RGB(255, 218, 185));
		rgbList.add(new RGB(175, 238, 238));
		rgbList.add(new RGB(255, 222, 173));
		rgbList.add(new RGB(255, 192, 203));
		rgbList.add(new RGB(245, 222, 179));
		rgbList.add(new RGB(238, 232, 170));
		rgbList.add(new RGB(211, 211, 211));
		// rgbList.add(new RGB(211, 211, 211));
		rgbList.add(new RGB(255, 182, 193));
		rgbList.add(new RGB(176, 224, 230));
		rgbList.add(new RGB(216, 191, 216));
		rgbList.add(new RGB(173, 216, 230));
		rgbList.add(new RGB(238, 130, 238));
		rgbList.add(new RGB(221, 160, 221));
		rgbList.add(new RGB(176, 196, 222));
		rgbList.add(new RGB(127, 255, 212));
		rgbList.add(new RGB(135, 206, 250));
		rgbList.add(new RGB(238, 221, 130));
		rgbList.add(new RGB(135, 206, 235));
		rgbList.add(new RGB(190, 190, 190));
		rgbList.add(new RGB(152, 251, 152));
		rgbList.add(new RGB(218, 112, 214));
		rgbList.add(new RGB(255, 105, 180));
		// rgbList.add(new RGB(255, 105, 180));
		rgbList.add(new RGB(255, 160, 122));
		rgbList.add(new RGB(210, 180, 140));
		rgbList.add(new RGB(255, 255, 0));
		rgbList.add(new RGB(255, 0, 255));
		rgbList.add(new RGB(0, 255, 255));
		rgbList.add(new RGB(233, 150, 122));
		rgbList.add(new RGB(244, 164, 96));
		rgbList.add(new RGB(132, 112, 255));
		rgbList.add(new RGB(240, 128, 128));
		rgbList.add(new RGB(64, 224, 208));
		rgbList.add(new RGB(250, 128, 114));
		rgbList.add(new RGB(100, 149, 237));
		rgbList.add(new RGB(72, 209, 204));
		rgbList.add(new RGB(186, 85, 211));
		rgbList.add(new RGB(219, 112, 147));
		rgbList.add(new RGB(147, 112, 219));
		rgbList.add(new RGB(102, 205, 170));
		rgbList.add(new RGB(188, 143, 143));
		rgbList.add(new RGB(143, 188, 143));
		rgbList.add(new RGB(255, 215, 0));
		rgbList.add(new RGB(123, 104, 238));
		rgbList.add(new RGB(255, 127, 80));
		rgbList.add(new RGB(0, 191, 255));
		rgbList.add(new RGB(160, 32, 240));
		rgbList.add(new RGB(30, 144, 255));
		rgbList.add(new RGB(255, 99, 71));
		rgbList.add(new RGB(255, 20, 147));
		rgbList.add(new RGB(255, 165, 0));
		rgbList.add(new RGB(0, 206, 209));
		rgbList.add(new RGB(95, 158, 160));
		rgbList.add(new RGB(154, 205, 50));
		// rgbList.add(new RGB(119, 136, 153));
		rgbList.add(new RGB(119, 136, 153));
		rgbList.add(new RGB(138, 43, 226));
		rgbList.add(new RGB(0, 250, 154));
		rgbList.add(new RGB(205, 133, 63));
		rgbList.add(new RGB(106, 90, 205));
		rgbList.add(new RGB(255, 140, 0));
		rgbList.add(new RGB(65, 105, 225));
		rgbList.add(new RGB(205, 92, 92));
		rgbList.add(new RGB(208, 32, 144));
		// rgbList.add(new RGB(112, 128, 144));
		rgbList.add(new RGB(112, 128, 144));
		rgbList.add(new RGB(127, 255, 0));
		rgbList.add(new RGB(0, 255, 127));
		rgbList.add(new RGB(70, 130, 180));
		rgbList.add(new RGB(32, 178, 170));
		rgbList.add(new RGB(124, 252, 0));
		rgbList.add(new RGB(148, 0, 211));
		rgbList.add(new RGB(199, 21, 133));
		rgbList.add(new RGB(60, 179, 113));
		rgbList.add(new RGB(210, 105, 30));
		rgbList.add(new RGB(184, 134, 11));
		rgbList.add(new RGB(255, 69, 0));
		rgbList.add(new RGB(176, 48, 96));
		rgbList.add(new RGB(105, 105, 105));
		// rgbList.add(new RGB(105, 105, 105));
		rgbList.add(new RGB(50, 205, 50));
		rgbList.add(new RGB(160, 82, 45));
		rgbList.add(new RGB(107, 142, 35));
		rgbList.add(new RGB(72, 61, 139));
		rgbList.add(new RGB(46, 139, 87));
		rgbList.add(new RGB(255, 0, 0));
		rgbList.add(new RGB(0, 255, 0));
		rgbList.add(new RGB(0, 0, 255));
		rgbList.add(new RGB(165, 42, 42));
		rgbList.add(new RGB(178, 34, 34));
		rgbList.add(new RGB(85, 107, 47));
		rgbList.add(new RGB(139, 69, 19));
		rgbList.add(new RGB(34, 139, 34));
		rgbList.add(new RGB(47, 79, 79));
		// rgbList.add(new RGB(47, 79, 79));
		rgbList.add(new RGB(0, 0, 205));
		rgbList.add(new RGB(25, 25, 112));
		rgbList.add(new RGB(0, 0, 128));
		// rgbList.add(new RGB(0,0,128));
		rgbList.add(new RGB(0, 100, 0));
		rgbList.add(new RGB(0, 0, 0));

	}

	/**
	 * inner class to show web colors with changable border draws rectangle of
	 * size bordersize in pixels and with borderstyle
	 */
	public class BorderLabel extends Canvas {
		String text;

		int size, style;

		/**
		 * @param parent
		 *            the parent composite
		 * @param parent
		 *            style
		 */
		public BorderLabel(Composite parent, int style) {
			super(parent, style);

			addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					BorderLabel.this.paintControl(e);
				}
			});
		}

		void paintControl(PaintEvent e) {
			GC gc = e.gc;
			Point pt = this.getSize();

			if (text != null) {
				gc.drawString(text, size, size);
			}

			if (size > 0) {
				gc.setLineWidth(size);
				gc.setLineStyle(style);
				gc.drawRectangle(1, 1, pt.x - size, pt.y - size);
			}
		}

		public void setText(String text) {
			this.text = text;
			redraw();
		}

		public void setBorderSize(int size) {
			this.size = size;
			redraw();
		}

		public void setBorderStyle(int style) {
			this.style = style;
			redraw();
		}

	}

}
