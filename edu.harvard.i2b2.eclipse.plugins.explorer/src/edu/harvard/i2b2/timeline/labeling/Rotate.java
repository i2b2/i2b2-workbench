/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	
 *
 */

package edu.harvard.i2b2.timeline.labeling;

import java.awt.image.*;
import java.awt.*;
import java.applet.*;
import java.net.*;
import java.util.*;

import edu.harvard.i2b2.timeline.lifelines.Record;

public class Rotate extends Applet {
	protected String degreeStr;
	protected Record applet;

	public Rotate(String tx, Point p, Font font, Color backgroundColor,
			int degree, Record applet) {
		Frotate rot;

		this.tx = tx;
		this.tx_xoffset = p.x;
		this.tx_yoffset = p.y;
		this.tx_bg_color = backgroundColor;
		this.tx_font = font;
		this.degreeStr = (new Integer(degree)).toString();
		this.applet = applet;

		// offscrinit();
		textscrinit();

		// ========= ROTATE THE IMAGE ==============
		rot = new Frotate();
		rot.setparameter(degreeStr, 0);
		tx_pixels = rot.filter(tx_pixels, tx_width, tx_height);
		rot.setBackground(tx_bg_color);
		tx_width = rot.getWidth();
		tx_height = rot.getHeight();

		doStandardFilters();
	}

	// Begin - Included from OffScr.include

	// Parameters that apply to the entire applet
	protected int app_borderwidth; // Applet border width
	protected Color app_bordercolor; // Applet border color
	protected String app_fr_type; // Applet frame type
	protected int app_fr_thick; // Applet frame thickness
	protected Color app_bg_color; // Applet background color

	// Parameters specific to Text
	protected String tx; // Text
	protected int tx_xoffset; // Text X offset
	protected int tx_yoffset; // Text Y offset
	protected Color tx_color; // Text color
	protected Color tx_bg_color; // Text background color
	protected boolean tx_horizcenter; // Text horizontally centered
	protected boolean tx_vertcenter; // Text vertically centered
	protected Font tx_font; // Text font
	protected FontMetrics tx_fontmetrics; // Text font metrics
	protected boolean tx_underline; // Text underline
	protected int tx_width; // Text width
	protected int tx_height; // Text height
	protected int tx_ascent; // Text ascent
	protected int tx_descent; // Text descent
	protected int tx_borderwidth; // Text border width
	protected Color tx_bordercolor; // Text border color
	protected int tx_bordermargin; // Text border margin
	protected String tx_fr_type; // Text frame type
	protected int tx_fr_thick; // Text frame thickness
	protected int tx_fr_margin; // Text frame margin
	protected int tx_x_coord; // Text X Coordinate
	protected int tx_y_coord; // Text Y Coordinate

	// Parameters specific to the Image
	protected int img_pixels[]; // The text image
	protected int img_width; // Image width
	protected int img_height; // Image height
	protected String img_file; // Image file name
	protected boolean img_wait_load; // Load flag
	protected int img_xoffset; // Image X offset
	protected int img_yoffset; // Image Y offset
	protected int img_y_coord; // Image X coordinate
	protected int img_x_coord; // Image Y coordinate
	protected boolean img_horizcenter; // Image horizontally centered
	protected boolean img_vertcenter; // Image vertically centered
	protected int img_borderwidth; // Image border width
	protected Color img_bordercolor; // Image border color
	protected int img_bordermargin; // Image border margin
	protected String img_fr_type; // Image frame type
	protected int img_fr_thick; // Image frame thickness
	protected int img_fr_margin; // Image frame margin
	Font loading_font;

	// Parameters specific to Audio
	protected AudioClip tx_clip; // The Text Audio Clip
	protected String tx_audio_file; // The Text audio file
	protected boolean tx_playing; // Text clip currently playing
	protected boolean tx_audio_loop; // Text clip looping
	protected AudioClip img_clip; // The Image Audio Clip
	protected String img_audio_file; // The Image audio file
	protected boolean img_playing; // Image clip currently playing
	protected boolean img_audio_loop; // Image clip looping

	// Parameters specific to URLs
	protected URL tx_url; // The Text URL
	protected URL img_url; // The Image URL

	protected boolean tile; // Tiled?
	protected Image bgimage; // The background image
	protected String bgimage_file; // Name of the background image
	protected int bgimage_width; // Background image width
	protected int bgimage_height; // Background image height
	protected int bgimageXoffset; // BG image X ofset
	protected int bgimageYoffset; // BG image Y ofset
	protected Image tiled_bgimage; // Tile BG?
	protected int app_width; // App width
	protected int app_height; // App height
	protected Graphics bg_g; // Background Graphics

	// Parameters specific to Image Maps
	protected int num_maps; // Number of Maps
	protected int map_x1[]; // X1 coord for map
	protected int map_y1[]; // Y1 coord for map
	protected int map_x2[]; // X2 coord for map
	protected int map_y2[]; // Y2 coord for map
	protected URL map_url[]; // URLs for maps
	protected boolean testmode; // Test mode
	protected Frame frame; // Frame of the browser
	protected AudioClip map_clip[]; // The Audio Clips for maps
	protected String map_audio_file[]; // The Audio Files for maps
	protected boolean audio_playing[]; // Is audio playing
	protected boolean audio_looping[]; // Should audio loop

	// Parameters specific to OffScreen images
	protected Image tx_osi; // Text Off-Screen Image
	protected Graphics tx_g; // Graphics of tx_osi
	protected int tx_pixels[]; // The text image
	protected String tx_filter_str; // Text filters in string format
	protected Image img_osi; // Image Off-Screen Image
	protected Graphics img_g; // Graphics of img_osi
	protected String img_filter_str; // Img filters in string format
	boolean retval; // Retval from grabPixels;

	private void offscrinit() {
		int i;

		// ======== Applet ===========
		app_borderwidth = GetParmToInt("AppBorderWidth", 0);
		app_bordercolor = GetParmToColor("AppBorderColor", Color.black);
		app_fr_type = GetParmToString("AppFrameType", "ShadowIn");
		app_fr_thick = GetParmToInt("AppFrameThickness", 0);
		app_bg_color = GetParmToColor("AppBGColor", null);

		// ======== Text ===========
		tx = GetParmToString("Text", null);
		tx_xoffset = GetParmToInt("TxXOffset", 0);
		tx_yoffset = GetParmToInt("TxYOffset", -1);
		tx_color = GetParmToColor("TxColor", null);
		tx_bg_color = GetParmToColor("TxBGColor", null);
		tx_horizcenter = GetParmToBoolean("TxHorizCenter", false);
		tx_vertcenter = GetParmToBoolean("TxVertCenter", false);
		tx_font = GetParmToFont("TxFont", "TxStyle", "TxPointSize");
		tx_underline = GetParmToBoolean("TxUnderLine", false);
		tx_borderwidth = GetParmToInt("TxBorderWidth", 0);
		tx_bordercolor = GetParmToColor("TxBorderColor", Color.black);
		tx_bordermargin = GetParmToInt("TxBorderMargin", 0);
		tx_fr_thick = GetParmToInt("TxFrameThickness", 0);
		tx_fr_type = GetParmToString("TxFrameType", "ShadowIn");
		tx_fr_margin = GetParmToInt("TxFrameMargin", 0);

		setFont(tx_font);
		tx_fontmetrics = getFontMetrics(tx_font);

		// Set the background and foreground colors, if specified
		// We must set these colors here before using them
		// in the next block of code
		if (app_bg_color != null)
			setBackground(app_bg_color);
		else
			app_bg_color = getBackground();
		if (tx_color != null)
			setForeground(tx_color);
		else
			tx_color = getForeground();

		/*
		 * if(tx != null) { // This is somewhat wasteful to create this //
		 * because it may not be needed if the user // has specified TxFilter.
		 * However, if // not then we must generate the pixels <a
		 * href=ftext.java.html>ftext</a> ps = new <a
		 * href=ftext.java.html>ftext</a>(); ps.setText(tx); ps.setApplet(this);
		 * ps.setFont(tx_font); ps.setBackground(app_bg_color);
		 * ps.setForeground(tx_color); tx_pixels = ps.filter(null, 0, 0);
		 * tx_width = ps.getWidth(); tx_height = ps.getHeight();
		 * 
		 * tx_width = tx_fontmetrics.stringWidth(tx); tx_height =
		 * tx_fontmetrics.getHeight(); tx_ascent =
		 * tx_fontmetrics.getMaxAscent(); tx_descent =
		 * tx_fontmetrics.getDescent(); }
		 */

		// ======== Image ===========
		img_wait_load = GetParmToBoolean("ImgLoadWait", false);
		img_xoffset = GetParmToInt("ImgXOffset", 0);
		img_yoffset = GetParmToInt("ImgYOffset", -1);
		img_horizcenter = GetParmToBoolean("ImgHorizCenter", false);
		img_vertcenter = GetParmToBoolean("ImgVertCenter", false);
		img_borderwidth = GetParmToInt("ImgBorderWidth", 0);
		img_bordercolor = GetParmToColor("ImgBorderColor", Color.black);
		img_bordermargin = GetParmToInt("ImgBorderMargin", 0);
		img_fr_thick = GetParmToInt("ImgFrameThickness", 0);
		img_fr_type = GetParmToString("ImgFrameType", "ShadowIn");
		img_fr_margin = GetParmToInt("ImgFrameMargin", 0);

		if (img_file != null) {
			if (img_wait_load)
				repaint();
			img_osi = process(getDocumentBase(), img_file, false);
			prepareImage(img_osi, this);
			img_width = getWidth(img_osi, this);
			img_height = getHeight(img_osi, this);
			loadImageAndWait(img_osi);

			img_pixels = getPixels(img_osi, this);
		}

		// ======== Audio ===========
		tx_audio_file = GetParmToString("TxAudio", null);
		tx_audio_loop = GetParmToBoolean("TxAudioLoop", false);
		img_audio_file = GetParmToString("ImgAudio", null);
		img_audio_loop = GetParmToBoolean("ImgAudioLoop", false);

		// If the user specified an non-existent or invalid
		// file, then print error message and exit
		if (tx_audio_file != null) {
			tx_clip = getAudioClip(getDocumentBase(), tx_audio_file);
			if (tx_clip == null) {
				System.out.println("Error getting " + tx_audio_file + ". "
						+ "Make sure the file exists and is an audio file");
			}
		}
		// If the user specified an non-existent or invalid
		// file, then print error message and exit
		if (img_audio_file != null) {
			img_clip = getAudioClip(getDocumentBase(), img_audio_file);
			if (img_clip == null) {
				System.out.println("Error getting " + img_audio_file + ". "
						+ "Make sure the file exists and is an audio file");
			}
		}

		// ======== URL ===========
		tx_url = GetParmToURL("TxURL");
		img_url = GetParmToURL("ImgURL");

		// ======== BG Image ===========
		tile = GetParmToBoolean("AppTile", false);
		bgimage_file = GetParmToString("AppBGImage", null);
		bgimageXoffset = GetParmToInt("AppBGImageXOffset", 0);
		bgimageYoffset = GetParmToInt("AppBGImageYOffset", 0);

		if (bgimage_file != null) {
			bgimage = process(getDocumentBase(), bgimage_file, false);
			prepareImage(bgimage, this);
			bgimage_width = getWidth(bgimage, this);
			bgimage_height = getHeight(bgimage, this);
		}

		// ========== Image Maps ===========
		testmode = GetParmToBoolean("TestMode", false);
		num_maps = GetParmToInt("AppNumMaps", 0);

		if (num_maps > 0) {
			map_x1 = new int[num_maps];
			map_y1 = new int[num_maps];
			map_x2 = new int[num_maps];
			map_y2 = new int[num_maps];
			map_url = new URL[num_maps];
			map_clip = new AudioClip[num_maps];
			map_audio_file = new String[num_maps];
			audio_playing = new boolean[num_maps];
			audio_looping = new boolean[num_maps];
		}

		for (i = 0; i < num_maps; i++) {
			map_x1[i] = GetParmToInt("Map" + (i + 1) + "_X1", 0);
			map_y1[i] = GetParmToInt("Map" + (i + 1) + "_Y1", 0);
			map_x2[i] = GetParmToInt("Map" + (i + 1) + "_X2", 0);
			map_y2[i] = GetParmToInt("Map" + (i + 1) + "_Y2", 0);
			map_url[i] = GetParmToURL("Map" + (i + 1) + "_URL");
			audio_looping[i] = GetParmToBoolean("AudioMapLoop" + (i + 1), false);
			map_audio_file[i] = GetParmToString("AudioMap" + (i + 1), null);
			if (map_audio_file[i] != null) {
				map_clip[i] = getAudioClip(getDocumentBase(), map_audio_file[i]);
				if (map_clip[i] == null) {
					System.out.println("Error getting " + map_audio_file[i]
							+ ". "
							+ "Make sure the file exists and is an audio file");
				}
			}
			audio_playing[i] = false;
		}

		// Get the top level frame so we can change the cursor
		Component c = this;
		frame = null;
		while (c != null) {
			c = c.getParent();
			if (c instanceof Frame) {
				frame = (Frame) c;
				break;
			}
		}

		// ======== Filters ===========
		tx_filter_str = GetParmToString("TxFilter", null);
		img_filter_str = GetParmToString("ImgFilter", null);
	}

	private void textscrinit() {
		int i;

		// ======== Text ===========
		tx_color = Color.black;
		tx_horizcenter = false;
		tx_vertcenter = false;
		tx_underline = false;
		tx_borderwidth = 0;
		tx_bordercolor = Color.white;
		tx_bordermargin = 0;
		tx_fr_thick = 0;
		tx_fr_type = "ShadowIn";
		tx_fr_margin = 0;

		setFont(tx_font);
		tx_fontmetrics = getFontMetrics(tx_font);

		if (tx != null) {
			// This is somewhat wasteful to create this
			// because it may not be needed if the user
			// has specified TxFilter. However, if
			// not then we must generate the pixels
			ftext ps = new ftext();
			ps.setText(tx);
			ps.setApplet(applet);
			ps.setFont(tx_font);
			ps.setBackground(tx_bg_color);
			ps.setForeground(tx_color);
			tx_pixels = ps.filter(null, 0, 0);
			tx_width = ps.getWidth();
			tx_height = ps.getHeight();

			tx_width = tx_fontmetrics.stringWidth(tx);
			tx_height = tx_fontmetrics.getHeight();
			tx_ascent = tx_fontmetrics.getMaxAscent();
			tx_descent = tx_fontmetrics.getDescent();
		}
	}

	//
	// Overwrite the default update method
	// This may not be necessary for this case
	// but it certainly doesn't hurt
	//
	public void update(Graphics g) {
		paint(g);
	}

	//
	// Our paint method handles several tasks:
	// Calculates the x and y coordinates base on params
	// Draws the applet frame
	// Draws the applet border
	// Draws the text frame
	// Draws the text border
	// Draws the underline
	// Draws the text
	//
	public void paint(Graphics g) {
		tilebackground(g);

		// We never know when the size might change so if horizcenter
		// is true then recalculate the x_coord every time
		if (tx_osi != null) {
			tx_x_coord = calcULXcoord(tx_horizcenter, size().width, tx_width,
					tx_xoffset);
			tx_y_coord = calcULYcoord(tx_vertcenter, size().height, tx_height,
					tx_yoffset);

			// Draw the text
			g.drawImage(tx_osi, tx_x_coord, tx_y_coord, this);

			// Draw frame for text
			// drawFrame(g, tx_x_coord, tx_y_coord,
			// tx_width, tx_height,
			// tx_fr_type, tx_fr_thick, tx_fr_margin);

			// Draw border for text
			// drawBorder(g, tx_x_coord, tx_y_coord,
			// tx_width, tx_height,
			// tx_borderwidth, tx_bordermargin,
			// tx_bordercolor);
		}
	}

	//
	// If the user clicks the mouse then load the URL
	// We check the text position first, so if there is
	// overlapping text and image then the text URL will
	// be loaded, not the imgae URL
	//
	public boolean mouseDown(Event evt, int x, int y) {
		int i;

		// Handle the image maps first
		for (i = 0; i < num_maps; i++) {
			if (x > map_x1[i] && x < map_x2[i] && y > map_y1[i]
					&& y < map_y2[i]) {
				if (map_url[i] != null) {
					if (testmode) {
						getAppletContext().showStatus(
								"Loading URL: " + map_url[i].toString());
						System.out.println("Loading URL: "
								+ map_url[i].toString());
					} else
						getAppletContext().showDocument(map_url[i]);
					return true;
				}
			}
		}

		// Handle the text next (Text takes precedence over images)
		if (x > tx_x_coord && x < tx_x_coord + tx_width && y > tx_y_coord
				&& y < tx_y_coord + tx_height) {
			if (tx_url != null) {
				getAppletContext().showDocument(tx_url);
				System.out.println("loading " + tx_url.toString());
				return true;
			}
		}

		// Handle the image last
		if (x > img_x_coord && x < img_x_coord + img_width && y > img_y_coord
				&& y < img_y_coord + img_height) {
			if (img_url != null) {
				getAppletContext().showDocument(img_url);
				System.out.println("loading " + img_url.toString());
				return true;
			}
		}
		return true;
	}

	//
	// Check the x and y coordinates to see if we're actually
	// inside the boundaries of the text or image or both
	//
	public boolean mouseMove(Event evt, int x, int y) {
		boolean tx_status_shown = false;
		boolean img_status_shown = false;
		int i;

		if (testmode) {
			getAppletContext().showStatus("X: " + x + " Y: " + y);
			return true;
		}

		// ========== AUDIO =============
		// Process the image maps first
		for (i = 0; i < num_maps; i++) {
			if (x > map_x1[i] && x < map_x2[i] && y > map_y1[i]
					&& y < map_y2[i]) {
				if (!audio_playing[i]) {
					audio_playing[i] = true;
					if (map_clip[i] != null) {
						if (audio_looping[i])
							map_clip[i].loop();
						else
							map_clip[i].play();
					}
				}
			} else {
				// Since we don't get any kind of notification when the audio
				// clip has terminated (due to playing to completion) we set
				// playing to false everytime we move out of the boundaries
				// of the map
				audio_playing[i] = false;
				if (map_clip[i] != null) {
					map_clip[i].stop();
				}
			}
		}

		// Process the image next
		if (x > img_x_coord && x < img_x_coord + img_width && y > img_y_coord
				&& y < img_y_coord + img_height) {
			if (!img_playing) {
				img_playing = true;
				if (img_clip != null) {
					if (img_audio_loop)
						img_clip.loop();
					else
						img_clip.play();
				}
			} // else it's already playing, leave it alone
		} else {
			// Since we don't get any kind of notification when the audio
			// clip has terminated (due to playing to completion) we set
			// playing to false everytime we move out of the boundaries
			// of the string
			img_playing = false;
			if (img_clip != null) {
				img_clip.stop();
			}
		}

		// Process the text last
		if (x > tx_x_coord && x < tx_x_coord + tx_width && y > tx_y_coord
				&& y < tx_y_coord + tx_height) {
			if (!tx_playing) {
				tx_playing = true;
				if (tx_clip != null) {
					if (tx_audio_loop)
						tx_clip.loop();
					else
						tx_clip.play();
				}
			} // else it's already playing, leave it alone
		} else {
			// Since we don't get any kind of notification when the audio
			// clip has terminated (due to playing to completion) we set
			// playing to false everytime we move out of the boundaries
			// of the string
			tx_playing = false;
			if (tx_clip != null) {
				tx_clip.stop();
			}
		}

		// ========== IMAGE MAPS =============
		for (i = 0; i < num_maps; i++) {
			if (x > map_x1[i] && x < map_x2[i] && y > map_y1[i]
					&& y < map_y2[i]) {
				if (map_url[i] != null) {
					getAppletContext().showStatus(map_url[i].toString());
					frame.setCursor(Frame.HAND_CURSOR);
					tx_status_shown = true;
				}
			}
		}

		// ========== URLs =============
		// Handle the text first
		if (x > tx_x_coord && x < tx_x_coord + tx_width && y > tx_y_coord
				&& y < tx_y_coord + tx_height) {
			if (tx_url != null) {
				getAppletContext().showStatus(tx_url.toString());
				frame.setCursor(Frame.HAND_CURSOR);
				tx_status_shown = true;
			}
		}

		// Handle the image last
		// If a Text URL is being displayed then don't
		// display the image URL
		if (x > img_x_coord && x < img_x_coord + img_width && y > img_y_coord
				&& y < img_y_coord + img_height) {
			if (img_url != null && tx_status_shown == false) {
				getAppletContext().showStatus(img_url.toString());
				frame.setCursor(Frame.HAND_CURSOR);
				img_status_shown = true;
			}
		}
		// If we are not over either text or image that has
		// a corresponding URL then clear the status line
		if (tx_status_shown == false && img_status_shown == false) {
			getAppletContext().showStatus(null);
			frame.setCursor(Frame.DEFAULT_CURSOR);
		}
		return true;
	}

	//
	// Just to be sure, when we exit the applet, stop the clips
	// It should already be stopped
	//
	public boolean mouseExit(Event evt, int x, int y) {
		// Just to be sure
		tx_playing = false;
		if (tx_clip != null)
			tx_clip.stop();
		img_playing = false;
		if (img_clip != null)
			img_clip.stop();
		getAppletContext().showStatus(null);
		frame.setCursor(Frame.DEFAULT_CURSOR);
		for (int i = 0; i < num_maps; i++)
			if (map_clip[i] != null)
				map_clip[i].stop();

		return true;
	}

	//
	// Calculate the upper left corner position when centering
	// an item that's drawn in relation to its upper left corner
	// such as images
	//
	protected int calcULCenter(int panelsize, int objectsize) {
		return ((panelsize / 2) - (objectsize / 2));
	}

	//
	// Calculate the lower left corner position when centering
	// an item that's drawn in relation to its lower left corner
	// such as text
	//
	protected int calcLLCenter(int panelsize, int objectsize) {
		return ((panelsize / 2) + (objectsize / 2));
	}

	//
	// A general purpose text drawing method
	// This method is used by the Img class
	//
	protected void drawText(Graphics g, String txt, int x, int y, Color c,
			Font f) {
		if (txt != null) {
			g.setColor(c);
			g.setFont(f);
			g.drawString(txt, x, y);
		}

	}

	//
	// The drawBorder method draws a border at the
	// specified x and y position
	//
	protected void drawBorder(Graphics g, int x, int y, int w, int h,
			int borderwidth, int margin, Color c) {
		if (borderwidth > 0) {
			g.setColor(c);
			for (int i = 0; i < borderwidth; i++) {
				g.drawRect(x - margin + i, y - margin + i, w + (margin * 2)
						- (i * 2) - 1, h + (margin * 2) - (i * 2) - 1);
			}
		}
	}

	void drawTopLine(Graphics g, int x, int y, int w, int h, int i, int margin) {
		g.drawLine(x - margin + i, y - margin + i, x + w + margin - i - 1, y
				- margin + i);
	}

	void drawBottomLine(Graphics g, int x, int y, int w, int h, int i,
			int margin) {
		g.drawLine(x - margin + i, y + h + margin - i - 1, x + w + margin - i
				- 1, y + h + margin - i - 1);
	}

	void drawLeftLine(Graphics g, int x, int y, int w, int h, int i, int margin) {
		g.drawLine(x - margin + i, y - margin + i, x - margin + i, y + h
				+ margin - i - 1);
	}

	void drawRightLine(Graphics g, int x, int y, int w, int h, int i, int margin) {
		g.drawLine(x + w + margin - i - 1, y - margin + i, x + w + margin - i
				- 1, y + h + margin - i - 1);
	}

	//
	// The drawFrame draws a frame at the specified x and y position
	// A frame can be one of the following four types: ShadowIn,
	// ShadowOut, ShadowEtchedIn, ShadowEtchedOut
	//
	protected void drawFrame(Graphics g, // Graphics to draw to
			int x, // X upper left position
			int y, // Y upper left position
			int w, // width
			int h, // height
			String type, // Type of frame
			int thickness, // thickness of frame
			int margin) { // Margin around object
		int i;
		if (thickness == 0)
			return;
		Color darker = darkenit(app_bg_color, .50);
		Color slightlydarker = darkenit(app_bg_color, .10);
		Color brighter = brightenit(app_bg_color, .50);

		if (thickness > 0) {
			if (type.equalsIgnoreCase("shadowout")) {
				for (i = 0; i < thickness; i++) {
					g.setColor(brighter);
					// TOP
					drawTopLine(g, x, y, w, h, i, margin);
					// LEFT
					drawLeftLine(g, x, y, w, h, i, margin);

					g.setColor(darker);
					// BOTTOM
					drawBottomLine(g, x, y, w, h, i, margin);
					// RIGHT
					drawRightLine(g, x, y, w, h, i, margin);
				}
			} else if (type.equalsIgnoreCase("shadowetchedin")) {
				for (i = 0; i < thickness; i++) {
					if (i == 0)
						g.setColor(darker);
					else if (i == thickness - 1)
						g.setColor(brighter);
					else
						g.setColor(slightlydarker);
					// TOP
					drawTopLine(g, x, y, w, h, i, margin);
					// LEFT
					drawLeftLine(g, x, y, w, h, i, margin);

					if (i == 0)
						g.setColor(brighter);
					else if (i == thickness - 1)
						g.setColor(darker);
					else
						g.setColor(slightlydarker);
					// BOTTOM
					drawBottomLine(g, x, y, w, h, i, margin);
					// RIGHT
					drawRightLine(g, x, y, w, h, i, margin);
				}

			} else if (type.equalsIgnoreCase("shadowetchedout")) {
				for (i = 0; i < thickness; i++) {
					if (i == 0)
						g.setColor(brighter);
					else if (i == thickness - 1)
						g.setColor(darker);
					else
						g.setColor(app_bg_color);
					// TOP
					drawTopLine(g, x, y, w, h, i, margin);
					// LEFT
					drawLeftLine(g, x, y, w, h, i, margin);

					if (i == 0)
						g.setColor(darker);
					else if (i == thickness - 1)
						g.setColor(brighter);
					else
						g.setColor(app_bg_color);
					// BOTTOM
					drawBottomLine(g, x, y, w, h, i, margin);
					// RIGHT
					drawRightLine(g, x, y, w, h, i, margin);
				}

			} else { // Default to "shadowin"
				for (i = 0; i < thickness; i++) {
					g.setColor(darker);
					// TOP
					drawTopLine(g, x, y, w, h, i, margin);
					// LEFT
					drawLeftLine(g, x, y, w, h, i, margin);

					g.setColor(brighter);
					// BOTTOM
					drawBottomLine(g, x, y, w, h, i, margin);
					// RIGHT
					drawRightLine(g, x, y, w, h, i, margin);
				}
			}
		}
	}

	//
	// The drawUnderline method draws an underline
	// The underline is actually a filled rectangle
	// whose size is based on the width of a pipe
	// character from the same font. It works!
	//
	protected void drawUnderline(Graphics g, int x, int y, int w, Color c) {
		if (tx_underline == true) {
			int pipewidth = tx_fontmetrics.charWidth('|');
			;
			g.setColor(c);
			g.fillRect(x, y, w, Math.max(1, pipewidth / 4));
		}
	}

	//
	// Calculate the upper left X coordinate based
	// on parameters such as horizcenter and xoffset
	//
	protected int calcULXcoord(boolean hcent, int appletwidth, int objectwidth,
			int xoffset) {
		int x;

		// Horizontal (x coordinate) Position
		if (hcent)
			x = calcULCenter(appletwidth, objectwidth);
		else if (xoffset == -1)
			x = 0;
		else
			x = xoffset;

		return x;
	}

	//
	// Calculate the upper left Y coordinate based
	// on parameters such as vertcenter and yoffset
	//
	protected int calcULYcoord(boolean hcent, int appletheight,
			int objectheight, int yoffset) {
		int y;

		// Vertical (y coordinate) Position
		if (hcent)
			y = calcULCenter(appletheight, objectheight);
		else if (yoffset == -1)
			y = 0;
		else
			y = yoffset;

		return y;
	}

	//
	// Calculate the lower left Y coordinate based
	// on parameters such as vertcenter and yoffset
	//
	protected int calcLLYcoord(boolean hcent, int appletheight,
			int objectheight, int objectdescent, int yoffset) {
		int y;

		// Vertical (y coordinate) Position
		if (hcent)
			y = calcLLCenter(appletheight, objectheight) - objectdescent;
		else if (yoffset == -1)
			y = objectheight - objectdescent;
		else
			y = yoffset;

		return y;
	}

	// 
	// Tile the background with the specified image
	//
	protected void tilebackground(Graphics g) {
		int i, j;

		if (bgimage == null)
			return;
		if (isImagePrepared(bgimage) == false)
			loadImageAndWait(bgimage);
		//
		// If the applet has changed size or this is the first time
		// tilebackground has been called then create the bg image
		//
		if (app_width != size().width || app_height != size().height) {
			app_width = size().width;
			app_height = size().height;

			tiled_bgimage = createImage(size().width, size().height);
			bg_g = tiled_bgimage.getGraphics();
			bg_g.setColor(app_bg_color);
			bg_g.fillRect(0, 0, size().width, size().height);
			if (tile & (bgimageXoffset != 0 || bgimageYoffset != 0)) {
				for (i = -bgimage_height; i < app_height; i += bgimage_height) {
					for (j = -bgimage_width; j < app_width; j += bgimage_width) {
						bg_g.drawImage(bgimage, j + bgimageYoffset, i
								+ bgimageXoffset, this);
					}
				}

			} else if (tile) {
				for (i = -bgimage_height; i < app_height; i += bgimage_height) {
					for (j = -bgimage_width; j < app_width; j += bgimage_width) {
						bg_g.drawImage(bgimage, j + 2, i + 2, this);
					}
				}
			} else {
				bg_g.drawImage(bgimage, bgimageXoffset, bgimageYoffset, this);
			}
		}

		if (tiled_bgimage != null) {
			g.drawImage(tiled_bgimage, 0, 0, this);
		}
	}

	public void doStandardFilters() {

		// ========= INVOKE IMAGE FILTERS ==============
		if (img_osi != null && img_filter_str != null) {
			img_pixels = invoke(null, img_filter_str, img_pixels, img_width,
					img_height, false, app_bg_color, false);
			img_width = getWidth();
			img_height = getHeight();
		}

		// ========= CREATE IMAGE FROM PIXELS ==============
		if (img_osi != null) {
			img_osi = createImage(new MemoryImageSource(img_width, img_height,
					ColorModel.getRGBdefault(), img_pixels, 0, img_width));
		}

		// ========= INVOKE FILTERS ==============
		if (tx != null && tx_filter_str != null) {
			if (tx_bg_color == null) {
				tx_pixels = invoke(tx, tx_filter_str, tx_pixels, tx_width,
						tx_height, true, app_bg_color, tx_underline);
			} else {
				tx_pixels = invoke(tx, tx_filter_str, tx_pixels, tx_width,
						tx_height, false, tx_bg_color, tx_underline);
			}
			tx_width = getWidth();
			tx_height = getHeight();
		}

		// ========= CREATE IMAGE FROM PIXELS ==============
		if (tx != null) {
			tx_osi = createImage(new MemoryImageSource(tx_width, tx_height,
					ColorModel.getRGBdefault(), tx_pixels, 0, tx_width));
		}
	}

	public synchronized boolean imageUpdate(Image img, int infoflags, int x,
			int y, int width, int height) {
		if ((infoflags & ERROR) != 0) {
			if (img.equals(this.img_osi))
				System.out.println("Error getting image = " + img_file);
			else if (img.equals(bgimage))
				System.out.println("Error getting image = " + bgimage_file);
			img.flush();
			img = null;
			return false;
		}
		if ((infoflags & ABORT) != 0) {
			if (img.equals(this.img_osi))
				System.out.println("Abort image = " + img_file);
			else if (img.equals(bgimage))
				System.out.println("Abort image = " + bgimage_file);
			img.flush();
			img = null;
			return false;
		}
		if ((infoflags & ALLBITS) != 0) {
			return true;
		}
		return super.imageUpdate(img, infoflags, x, y, width, height);
	}

	// Begin - Included from GetParm.include

	public int GetParmToInt(String par, int defaultval) {
		String s = getParameter(par);
		if (s == null)
			return defaultval;
		else
			return (Integer.parseInt(s));
	}

	public String GetParmToString(String par, String defaultval) {
		String s = getParameter(par);
		if (s == null)
			return defaultval;
		else
			return (s);
	}

	public boolean GetParmToBoolean(String par, boolean defaultval) {
		String s = getParameter(par);
		if (s == null)
			return defaultval;
		else
			return (s.equalsIgnoreCase("true"));
	}

	public Color GetParmToColor(String par, Color defaultval) {
		Color color;

		String s = getParameter(par);
		if (s == null)
			return defaultval;
		else {
			try {
				if (s.charAt(0) == '#') {
					char chars[];
					// Get rid of leading #
					chars = new char[s.length()];
					s.getChars(0, s.length(), chars, 0);
					color = new Color(Integer.parseInt(new String(chars, 1, s
							.length() - 1), 16));
					return (new Color(Integer.parseInt(new String(chars, 1, s
							.length() - 1), 16)));
				} else {
					color = new Color(Integer.parseInt(s, 16));
					return (color);
				}
			} catch (NumberFormatException e) {
				String retcolor;
				retcolor = getColor(s);
				if (retcolor != null)
					return (new Color(Integer.parseInt(retcolor, 16)));
				else
					System.out.println("Bad color specification: "
							+ e.getMessage());

				return null;
			}
		}
	}

	public URL GetParmToURL(String par) {
		URL url = null;
		String s = getParameter(par);
		if (s == null)
			return null;
		else {
			try {
				url = new URL(s);
			} catch (MalformedURLException e) {
				url = null;
			}
			if (url == null) {
				// The URL may be specified as relative to
				// the HTML document base in which the URL resides
				// We should be able to handle that
				try {
					url = new URL(getDocumentBase(), s);
				} catch (MalformedURLException e) {
					url = null;
				}
			}
			if (url == null) {
				// The URL may be specified as relative to
				// the Code base (though that seems rather
				// unlikely)
				//
				try {
					url = new URL(getCodeBase(), s);
				} catch (MalformedURLException e) {
					url = null;
				}
			}
			if (url == null)
				System.out.println("Unable to load URL: " + s);
		}
		return url;
	}

	public Font GetParmToFont(String par1, String par2, String par3) {

		String fontname;
		String fontstyle;
		int style = -1;
		int psize;
		Font font;
		Font currentfont;
		String psize_str;

		currentfont = getFont();
		fontname = getParameter(par1);
		if (fontname == null)
			fontname = currentfont.getName();
		fontstyle = getParameter(par2);
		if (fontstyle == null)
			style = currentfont.getStyle();

		// Get the Font
		if (fontname.equalsIgnoreCase("TimesRoman")
				|| fontname.equalsIgnoreCase("Helvetica")
				|| fontname.equalsIgnoreCase("Courier")
				|| fontname.equalsIgnoreCase("Dialog")
				|| fontname.equalsIgnoreCase("DialogInput")
				|| fontname.equalsIgnoreCase("ZapfDingbats")) {
			// Do Nothing, we got a valid font
		} else {
			fontname = currentfont.getName();
		}

		if (style == -1) {
			// Get the Font Style
			if (fontstyle.equalsIgnoreCase("bold"))
				style = Font.BOLD;
			else if (fontstyle.equalsIgnoreCase("italic"))
				style = Font.ITALIC;
			else if (fontstyle.equalsIgnoreCase("bolditalic"))
				style = Font.ITALIC | Font.BOLD;
			else
				style = Font.PLAIN;
		}
		psize_str = getParameter(par3);
		if (psize_str == null)
			psize = currentfont.getSize();
		else {
			try {
				psize = Integer.parseInt(psize_str);
			} catch (NumberFormatException e) {
				psize = currentfont.getSize();
				System.out.println("NumberformatException: " + psize_str);
			}
		}

		// Set up the font stuff
		font = new Font(fontname, style, psize);
		return font;
	}

	// Begin - Included from ColrLook.include

	Hashtable colors;

	public String getColor(String name) {
		if (colors == null)
			createHashTable();
		return (String) colors.get(name);
	}

	public void createHashTable() {

		if (colors != null)
			return;

		colors = new Hashtable(135);
		colors.put("aliceblue", "f0f8ff");
		colors.put("antiquewhite", "faebd7");
		colors.put("aquamarine", "7fffd4");
		colors.put("azure", "f0ffff");
		colors.put("beige", "f5f5dc");
		colors.put("bisque", "ffe4c4");
		colors.put("black", "000000");
		colors.put("blanchedalmond", "ffebcd");
		colors.put("blue", "0000ff");
		colors.put("blueviolet", "8a2be2");
		colors.put("brown", "a52a2a");
		colors.put("burlywood", "deb887");
		colors.put("cadetblue", "5f9ea0");
		colors.put("chartreuse", "7fff00");
		colors.put("chocolate", "d2691e");
		colors.put("coral", "ff7f50");
		colors.put("cornflowerblue", "6495ed");
		colors.put("cornsilk", "fff8dc");
		colors.put("cyan", "00ffff");
		colors.put("darkgoldenrod", "b8860b");
		colors.put("darkgreen", "006400");
		colors.put("darkkhaki", "bdb76b");
		colors.put("darkolivegreen", "556b2f");
		colors.put("darkorange", "ff8c00");
		colors.put("darkorchid", "9932cc");
		colors.put("darksalmon", "e9967a");
		colors.put("darkseagreen", "8fbc8f");
		colors.put("darkslateblue", "483d8b");
		colors.put("darkslategray", "2f4f4f");
		colors.put("darkslategrey", "2f4f4f");
		colors.put("darkturquoise", "00ced1");
		colors.put("darkviolet", "9400d3");
		colors.put("deeppink", "ff1493");
		colors.put("deepskyblue", "00bfff");
		colors.put("dimgray", "696969");
		colors.put("dimgrey", "696969");
		colors.put("dodgerblue", "1e90ff");
		colors.put("firebrick", "b22222");
		colors.put("floralwhite", "fffaf0");
		colors.put("forestgreen", "228b22");
		colors.put("green", "00ff00");
		colors.put("gainsboro", "dcdcdc");
		colors.put("ghostwhite", "f8f8ff");
		colors.put("gold", "ffd700");
		colors.put("goldenrod", "daa520");
		colors.put("gray", "bebebe");
		colors.put("honeydew", "f0fff0");
		colors.put("hotpink", "ff69b4");
		colors.put("indianred", "cd5c5c");
		colors.put("ivory", "fffff0");
		colors.put("khaki", "f0e68c");
		colors.put("lavender", "e6e6fa");
		colors.put("lavenderblush", "fff0f5");
		colors.put("lawngreen", "7cfc00");
		colors.put("lemonchiffon", "fffacd");
		colors.put("lightblue", "add8e6");
		colors.put("lightcoral", "f08080");
		colors.put("lightcyan", "e0ffff");
		colors.put("lightgoldenrod", "eedd82");
		colors.put("lightgoldenrodyellow", "fafad2");
		colors.put("lightgray", "d3d3d3");
		colors.put("lightgrey", "d3d3d3");
		colors.put("lightpink", "ffb6c1");
		colors.put("lightsalmon", "ffa07a");
		colors.put("lightseagreen", "20b2aa");
		colors.put("lightskyblue", "87cefa");
		colors.put("lightslateblue", "8470ff");
		colors.put("lightslategray", "778899");
		colors.put("lightslategrey", "778899");
		colors.put("lightsteelblue", "b0c4de");
		colors.put("lightyellow", "ffffe0");
		colors.put("limegreen", "32cd32");
		colors.put("linen", "faf0e6");
		colors.put("magenta", "ff00ff");
		colors.put("maroon", "b03060");
		colors.put("mediumaquamarine", "66cdaa");
		colors.put("mediumblue", "0000cd");
		colors.put("mediumorchid", "ba55d3");
		colors.put("mediumpurple", "9370db");
		colors.put("mediumseagreen", "3cb371");
		colors.put("mediumslateblue", "7b68ee");
		colors.put("mediumspringgreen", "00fa9a");
		colors.put("mediumturquoise", "48d1cc");
		colors.put("mediumvioletred", "c71585");
		colors.put("midnightblue", "191970");
		colors.put("mintcream", "f5fffa");
		colors.put("mistyrose", "ffe4e1");
		colors.put("moccasin", "ffe4b5");
		colors.put("navajowhite", "ffdead");
		colors.put("navy", "000080");
		colors.put("navyblue", "000080");
		colors.put("oldlace", "fdf5e6");
		colors.put("olivedrab", "6b8e23");
		colors.put("orange", "ffa500");
		colors.put("orangered", "ff4500");
		colors.put("orchid", "da70d6");
		colors.put("palegoldenrod", "eee8aa");
		colors.put("palegreen", "98fb98");
		colors.put("paleturquoise", "afeeee");
		colors.put("palevioletred", "db7093");
		colors.put("papayawhip", "ffefd5");
		colors.put("peachpuff", "ffdab9");
		colors.put("peru", "cd853f");
		colors.put("pink", "ffc0cb");
		colors.put("plum", "dda0dd");
		colors.put("powderblue", "b0e0e6");
		colors.put("purple", "a020f0");
		colors.put("red", "ff0000");
		colors.put("rosybrown", "bc8f8f");
		colors.put("royalblue", "4169e1");
		colors.put("saddlebrown", "8b4513");
		colors.put("salmon", "fa8072");
		colors.put("sandybrown", "f4a460");
		colors.put("seagreen", "2e8b57");
		colors.put("seashell", "fff5ee");
		colors.put("sienna", "a0522d");
		colors.put("skyblue", "87ceeb");
		colors.put("slateblue", "6a5acd");
		colors.put("slategray", "708090");
		colors.put("slategrey", "708090");
		colors.put("snow", "fffafa");
		colors.put("springgreen", "00ff7f");
		colors.put("steelblue", "4682b4");
		colors.put("tan", "d2b48c");
		colors.put("thistle", "d8bfd8");
		colors.put("tomato", "ff6347");
		colors.put("turquoise", "40e0d0");
		colors.put("violet", "ee82ee");
		colors.put("violetred", "d02090");
		colors.put("wheat", "f5deb3");
		colors.put("white", "ffffff");
		colors.put("whitesmoke", "f5f5f5");
		colors.put("yellow", "ffff00");
		colors.put("yellowgreen", "9acd32");
	}

	// End - Included from ColrLook.include

	// End - Included from GetParm.include

	// Begin - Included from ImgGetr.include

	public Image process(URL url, String file, boolean loadnow) {
		Image image;

		// See if the user specified an Image parameter
		// If not, return
		if (file == null)
			return null;

		image = getImage(url, file);
		if (loadnow)
			loadImageAndWait(image);
		return image;
	}

	/**
	 * Checks to see if the specified image is actually prepared (loaded) and
	 * ready to display Return true if loaded, otherwise false
	 * 
	 * @param image
	 *            the image to check
	 */
	public boolean isImagePrepared(Image image) {
		boolean ImagePrepared;
		ImagePrepared = prepareImage(image, this);
		return ImagePrepared;
	}

	/**
	 * Begins the preparation (loading) of the image This function returns
	 * immediately The image is loaded in a thread
	 * 
	 * @param image
	 *            the image to prepare
	 */
	public void prepareImage(Image image) {
		boolean ImagePrepared;
		ImagePrepared = prepareImage(image, this);
	}

	/**
	 * Prepares (loads) the image and does not return until the loading is
	 * complete
	 * 
	 * @param image
	 *            the image to load
	 */
	public synchronized void loadImageAndWait(Image image) {
		int checkImageFlags;
		boolean ImagePrepared;

		ImagePrepared = prepareImage(image, this);
		if (ImagePrepared == false) {
			while (((checkImageFlags = checkImage(image, this)) & ImageObserver.ALLBITS) == 0) {
				try {
					wait(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public synchronized int getWidth(Image image, ImageObserver observer) {
		int width;
		while ((width = image.getWidth(observer)) == -1) {
			try {
				wait(100);
			} catch (InterruptedException e) {
			}
		}
		return width;
	}

	public synchronized int getHeight(Image image, ImageObserver observer) {
		int height;
		while ((height = image.getHeight(observer)) == -1) {
			try {
				wait(100);
			} catch (InterruptedException e) {
			}
		}
		return height;
	}

	public int[] getPixels(Image image, ImageObserver observer) {
		int pixels[];
		int w, h;
		PixelGrabber tmp_pg;
		boolean retval;

		w = getWidth(image, observer);
		h = getHeight(image, observer);
		loadImageAndWait(image);
		pixels = new int[w * h];
		tmp_pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
		try {
			retval = tmp_pg.grabPixels(0);
		} catch (InterruptedException e) {
			System.err.println("Interrupted waiting for pixels");
		}
		return pixels;
	}

	public int[] getPixels(Image image) {
		int pixels[];
		int w, h;
		PixelGrabber tmp_pg;
		boolean retval;

		w = getWidth(image, this);
		h = getHeight(image, this);
		loadImageAndWait(image);
		pixels = new int[w * h];
		tmp_pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
		try {
			retval = tmp_pg.grabPixels(0);
		} catch (InterruptedException e) {
			System.err.println("Interrupted waiting for pixels");
		}
		return pixels;
	}

	// End - Included from ImgGetr.include

	// Begin - Included from AdjColor.include

	int CalcFade(int from, int to, int perc, int scale) {

		return ((from > to) ? to + ((from - to) * (scale - perc) / scale)
				: from + ((to - from) * perc / scale));
	}

	Color darkenit(int r, int g, int b, double factor) {
		return new Color(Math.max((int) (r * (1 - factor)), 0), Math.max(
				(int) (g * (1 - factor)), 0), Math.max(
				(int) (b * (1 - factor)), 0));
	}

	//
	// The brightenit method in awt/Color.java doesn't really do
	// it right. If your color is black (#000000) their brighter
	// method does not make it brighter. You put in black
	// and you get back black. I think this algorithm makes more
	// sense.
	Color brightenit(int r, int g, int b, double factor) {
		int r2, g2, b2;
		r2 = r + (int) ((255 - r) * factor);
		g2 = g + (int) ((255 - g) * factor);
		b2 = b + (int) ((255 - b) * factor);
		return new Color(r2, g2, b2);
	}

	Color darkenit(Color c, double factor) {
		int r, g, b;
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		return darkenit(r, g, b, factor);
	}

	Color brightenit(Color c, double factor) {
		int r, g, b;
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		return brightenit(r, g, b, factor);
	}

	Color Fade(Color from, Color to, double factor) {
		int from_r, from_g, from_b;
		int to_r, to_g, to_b;
		int r, g, b;

		from_r = from.getRed();
		from_g = from.getGreen();
		from_b = from.getBlue();
		to_r = to.getRed();
		to_g = to.getGreen();
		to_b = to.getBlue();
		if (from_r > to_r)
			r = to_r + (int) ((from_r - to_r) * factor);
		else
			r = to_r - (int) ((to_r - from_r) * factor);
		if (from_g > to_g)
			g = to_g + (int) ((from_g - to_g) * factor);
		else
			g = to_g - (int) ((to_g - from_g) * factor);
		if (from_b > to_b)
			b = to_b + (int) ((from_b - to_b) * factor);
		else
			b = to_b - (int) ((to_b - from_b) * factor);

		return new Color(r, g, b);
	}

	// End - Included from AdjColor.include

	// Begin - Inlucded from Filter.include

	int new_w = -1;
	int new_h = -1;

	public int[] invoke(String tx, String f_str, int[] p1, int w, int h,
			boolean maketransparent, Color bgcolor, boolean underline) {
		return invoke(tx, f_str, p1, w, h, maketransparent, bgcolor, underline,
				getFont());
	}

	public int[] invoke(String tx, String f_str, int[] p1, int w, int h,
			boolean maketransparent, Color bgcolor, boolean underline, Font font) {

		StringTokenizer st, st2;
		int num_elements;
		int num_params[];
		String filter_strings[];
		String param_strings[][];
		int i, j;
		Class cl;
		ImgFilt filterclass;

		new_w = w;
		new_h = h;
		if (f_str != null) {
			st = new StringTokenizer(f_str, "|");
			num_elements = st.countTokens();
			filter_strings = new String[num_elements];
			param_strings = new String[num_elements][];
			num_params = new int[num_elements];

			for (i = 0; i < num_elements; i++) {
				String tmp_str, tmp_str2;
				tmp_str = st.nextToken();
				tmp_str2 = new String(tmp_str);
				st2 = new StringTokenizer(tmp_str2, " ");
				num_params[i] = st2.countTokens();
				if (num_params[i] == 1) {
					filter_strings[i] = tmp_str;
					param_strings[i] = null;
				} else {
					param_strings[i] = new String[num_params[i] - 1];
					filter_strings[i] = st2.nextToken();
					for (j = 0; j < num_params[i] - 1; j++) {
						param_strings[i][j] = st2.nextToken();
					}
				}
			}
			for (i = 0; i < num_elements; i++) {
				try {
					cl = Class.forName("f" + filter_strings[i]);
					filterclass = (ImgFilt) cl.newInstance();
					for (j = 0; j < num_params[i] - 1; j++)
						filterclass.setparameter(param_strings[i][j], j);
					filterclass.setBackground(bgcolor);
					filterclass.setForeground(getForeground());
					filterclass.setFont(font);
					filterclass.setApplet(applet);
					filterclass.setText(tx);
					filterclass.setUnderline(underline);
					// When we call setTransparent(true) this
					// means that the background color will be
					// used as the transparent color
					filterclass.setTransparent(maketransparent);
					p1 = filterclass.filter(p1, new_w, new_h);
					new_w = filterclass.getWidth();
					new_h = filterclass.getHeight();
				} catch (ClassNotFoundException e) {
					System.err.println("Can't find class " + filter_strings[i]);
				} catch (InstantiationException e) {
					System.err.println("Couldn't instantiate class "
							+ filter_strings[i]);
				} catch (IllegalAccessException e) {
					System.err.println("Couldn't access class "
							+ filter_strings[i]);
				}
			}
		}
		return p1;
	}

	public int getWidth() {
		return new_w;
	}

	public int getHeight() {
		return new_h;
	}
	// End - Inlucded from Filter.include
	// End - Inlucded from OffScr.include
}
