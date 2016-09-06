/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution.
 * 
 * 
 * Contributors: 
 *   
 *     
 */
// XmlApp.java: base class for �lfred demos.
// NO WARRANTY! See README, and copyright below.
// $Id: XmlApp.java,v 1.3 2010/07/06 14:04:25 mem61 Exp $

//import com.microstar.xml.XmlHandler;
//import com.microstar.xml.XmlParser;
package edu.harvard.i2b2.smlib.xml;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.net.MalformedURLException;
import java.net.URL;


/**
  * Base class for &AElig;lfred demonstration applications.
  * <p>This class fills in the basic interface, and provides
  * an I/O infrastructure for simple applications and applets.
  * @author Copyright (c) 1997, 1998 by Microstar Software Ltd.;
  * @author written by David Megginson &lt;dmeggins@microstar.com&gt;
  * @version 1.1
  * @see com.microstar.xml.XmlParser
  * @see com.microstar.xml.XmlHandler
  * @see EventDemo
  * @see TimerDemo
  * @see DtdDemo
  * @see StreamDemo
  */
public class XmlApp extends Applet implements XmlHandler {
  /**
    * Flag to show whether we're running as an applet or application.
    */
  public boolean isApplet = false;

  public XmlParser parser;

  //////////////////////////////////////////////////////////////////////
  // Implementation of XmlParser interface.
  //
  // The following methods provide a full skeleton implementation of the
  // XmlHandler interface, so that subclasses can override only
  // the methods they need.
  //////////////////////////////////////////////////////////////////////


  /**
    * Resolve an external entity.
    * <p>This method could generate a new URL by looking up the
    * public identifier in a hash table, or it could replace the
    * URL supplied with a different, local one; for now, however,
    * just return the URL supplied.
    * @see com.Microstar.xml.XmlHandler#resolveEntity
    */
  public Object resolveEntity (String publicId, String systemId)
  {
    return null;
  }


  public void startExternalEntity (String systemId)
  {
  }


  public void endExternalEntity (String systemId)
  {
  }


  /**
    * Handle the start of the document.
    * <p>Do nothing for now.  Subclasses can override this method
    * if they want to take a specific action.
    * <p>This method will always be called first.
    * @see com.Microstar.xml.XmlHandler#startDocument
    */
  public void startDocument ()
  {
  }


  /**
    * Handle the end the document.
    * <p>Do nothing for now.  Subclasses can override this method
    * if they want to take a specific action.
    * <p>This method will always be called last.
    * @see com.Microstar.xml.XmlHandler#endDocument
    */
  public void endDocument ()
  {
  }


  /**
    * Handle a DOCTYPE declaration.
    * <p>Do nothing for now.  Subclasses can override this method
    * if they want to take a specific action.
    * <p>Well-formed XML documents might not have one of these.
    * <p>The query methods in XmlParser will return useful
    * values only after this callback.
    * @see com.Microstar.xml.XmlHandler#doctypeDecl
    */
  public void doctypeDecl (String name,
			   String pubid, String sysid)
  {
  }


  /**
    * Handle an attribute value specification.
    * <p>Do nothing for now.  Subclasses can override this method
    * if they want to take a specific action.
    * @see com.Microstar.xml.XmlHandler#attribute
    */
  public void attribute (String name, String value,
			 boolean isSpecified)
  {
  }


  /**
    * Handle the start of an element.
    * <p>Do nothing for now.  Subclasses can override this method
    * if they want to take a specific action.
    * @see com.Microstar.xml.XmlHandler#startElement
    */
  public void startElement (String name)
  {
  }


  /** 
    * Handle the end of an element.
    * <p>Do nothing for now.  Subclasses can override this method
    * if they want to take a specific action.
    * @see com.Microstar.xml.XmlHandler#endElement
    */
  public void endElement (String name)
  {
  }


  /**
    * Handle character data.
    * <p>Do nothing for now.  Subclasses can override this method
    * if they want to take a specific action.
    * @see com.Microstar.xml.XmlHandler#charData
    */
  public void charData (char ch[], int start, int length)
  {
  }


  /**
    * Handle ignorable whitespace.
    * <p>Do nothing for now.  Subclasses can override this method
    * if they want to take a specific action.
    * @see com.Microstar.xml.XmlHandler#ignorableWhitespace
    */
  public void ignorableWhitespace (char ch[], 
				   int start, int length)
  {
  }


  /**
    * Handle a processing instruction.
    * <p>Do nothing for now.  Subclasses can override this method
    * if they want to take a specific action.
    * @see com.Microstar.xml.XmlHandler#processingInstruction
    */
  public void processingInstruction (String target,
				     String data)
  {
  }


  /**
    * Handle a parsing error.
    * <p>By default, print a message and throw an Error.
    * <p>Subclasses can override this method if they want to do something
    * different.
    * @see com.Microstar.xml.XmlHandler#error
    */
  public void error (String message,
		     String url, int line, int column)
  {
    displayText("FATAL ERROR: " + message);
    displayText("  at " + url.toString() + ": line " + line
		+ " column " + column);
    throw new Error(message);
  }

  //////////////////////////////////////////////////////////////////////
  // General utility methods.
  //////////////////////////////////////////////////////////////////////

  /**
    * Start a parse in application mode.
    * <p>Output will go to STDOUT.
    * @see #displayText
    * @see com.microstar.xml.XmlParser#run
    */
  public void doParse (String url)
    throws java.lang.Exception
  {
    String docURL = makeAbsoluteURL(url);

				// create the parser
    parser = new XmlParser();
    parser.setHandler(this);
    parser.parse(makeAbsoluteURL(url), null, (String)null);
  }

  static String makeAbsoluteURL (String url)
    throws MalformedURLException
  {
    URL baseURL;

    String currentDirectory = System.getProperty("user.dir");

    String fileSep = System.getProperty("file.separator");
    String file = currentDirectory.replace(fileSep.charAt(0), '/') + '/';
    if (file.charAt(0) != '/') {
      file = "/" + file;
    }
    baseURL = new URL("file", null, file);
    return new URL(baseURL,url).toString();
  }


  /**
    * Display text on STDOUT or in an applet TextArea.
    */
  public void displayText (String text)
  {
    if (isApplet) {
      textArea.appendText(text + '\n');
    } else {
      System.out.println(text);
    }
  }


  /**
    * Escape a string for printing.
    */
  public String escape (char ch[], int length)
  {
    StringBuffer out = new StringBuffer();
    for (int i = 0; i < length; i++) {
      switch (ch[i]) {
      case '\\':
	//out.append("\\\\");
	//out.append("");
	break;
      case '\n':
	//out.append("\\n");
	//out.append("");
	break;
      case '\t':
	//out.append("\\t");
	//out.append("");
	break;
      case '\r':
	//out.append("\\r");
	//out.append("");
	break;
      case '\f':
	//out.append("\\f");
	//out.append("");
	break;
      default:
	out.append(ch[i]);
	break;
      }
    }
    return out.toString();
  }

  //////////////////////////////////////////////////////////////////////
  // Applet support.
  //
  // These methods and variables are used only if we are running as an 
  // applet.
  //////////////////////////////////////////////////////////////////////


  TextArea textArea;
  Panel buttonPanel;
  Button startButton;
  /**
    * Initialise the applet the first time it runs.
    */
  @Override
public void init ()
  {
    startButton = new Button("Parse " + getParameter("url"));
    isApplet = true;
    textArea = new TextArea();
    buttonPanel = new Panel();
    buttonPanel.setLayout(new FlowLayout());
    setLayout(new BorderLayout());
    buttonPanel.add(startButton);
	
    add("Center", textArea);
    add("South", buttonPanel);
  }


  /**
    * React to a button press.
    * <p>This uses the old event model, so that it will work with
    * existing browsers.  It will cause a 'deprecated' warning
    * when you compile.
    */
  @Override
public boolean action (Event ev, Object target)
  {
    if (target == startButton.getLabel()) {
      try {
	startParse();
      } catch (Exception e) {
	System.err.println(e.getMessage());
      }
      return true;
    } else {
      return false;
    }
  }


  /**
    * Start a parse (in response to a button press).
    * <p>Erase the text area, then start the parser running, using
    * the value of the "url" parameter.
    */
  public void startParse ()
    throws java.lang.Exception
  {
    textArea.setText("");
    try {
      XmlParser parser = new XmlParser();
      parser.setHandler(this);
      ;
      parser.parse(new URL(getDocumentBase(),getParameter("url")).toString(),
		   null, (String)null);
    } catch (SecurityException e) {
      displayText("No permission to read file: " + e.getMessage());
    } catch (Exception e) {
      displayText("Caught exception: " + e.getMessage());
    }
  }


}

// end of XmlApp.java
