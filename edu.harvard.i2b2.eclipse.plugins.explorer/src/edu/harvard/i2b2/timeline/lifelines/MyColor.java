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

public class MyColor {

	private String colorString;

	public MyColor(String colorString) {
		this.colorString = colorString.toLowerCase();
	}

	public Color getColor2() {
		Color temp = new Color(255, 255, 255);

		if (colorString.compareTo("severe") == 0
				|| colorString.compareTo("red") == 0)
			temp = new Color(255, 0, 0);
		else if (colorString.compareTo("orange") == 0)
			temp = Color.orange;
		else if (colorString.compareTo("green") == 0)
			temp = Color.green;
		else if (colorString.equals("blue")) // dark blue
			temp = new Color(51, 25, 240);
		else if (colorString.equals("black"))
			temp = Color.black;
		else if (colorString.equals("lightgray"))
			temp = Color.lightGray;
		else if (colorString.equals("mediumblue"))
			temp = new Color(101, 153, 240);
		else if (colorString.equals("lightblue"))
			temp = new Color(142, 205, 240);
		else if (colorString.equals("lightbrown"))
			temp = new Color(255, 245, 200);
		else if (colorString.equals("darkbrown"))
			temp = new Color(255, 236, 175);
		return temp;
	}

	public String getColorString(Color color) {
		String cString = null;

		if (color == Color.orange)
			cString = new String("orange");
		else if (color == Color.green)
			cString = new String("green");
		else if (color == Color.black)
			cString = new String("black");
		else if (color == Color.lightGray)
			cString = new String("lightgray");
		if (color.getRed() == 255 && color.getGreen() == 0
				&& color.getBlue() == 0)
			cString = new String("red");
		else if (color.getRed() == 51 && color.getGreen() == 25
				&& color.getBlue() == 240)
			cString = new String("blue");
		else if (color.getRed() == 101 && color.getGreen() == 153
				&& color.getBlue() == 240)
			cString = new String("mediumblue");
		else if (color.getRed() == 142 && color.getGreen() == 205
				&& color.getBlue() == 240)
			cString = new String("lightblue");
		else if (color.getRed() == 255 && color.getGreen() == 245
				&& color.getBlue() == 200)
			cString = new String("lightbrown");
		else if (color.getRed() == 255 && color.getGreen() == 236
				&& color.getBlue() == 175)
			cString = new String("darkbrown");
		return cString;
	}

	public Color getColor() {

		String sDefaultHexColor = "0xFFFFFF"; // default color is white
		String sHexColor;
		Color temp;

		Hashtable colors = new Hashtable(135);
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
		colors.put("lightbrown", "fff5c8");
		colors.put("darkbrown", "ffecaf");
		colors.put("severe", "ff69b4");

		if (this.colorString.startsWith("0x")) {
			sHexColor = this.colorString.toUpperCase();
		} else {
			Object oFromHashtable = colors.get(this.colorString);
			if (oFromHashtable == null) {
				sHexColor = sDefaultHexColor;
			} else {
				sHexColor = oFromHashtable.toString().toUpperCase();
				sHexColor = "0x" + sHexColor;
			}
		}

		try {
			temp = Color.decode(sHexColor);
		} catch (Exception e) {
			temp = Color.decode(sDefaultHexColor);
		}

		return temp;
	}

}
