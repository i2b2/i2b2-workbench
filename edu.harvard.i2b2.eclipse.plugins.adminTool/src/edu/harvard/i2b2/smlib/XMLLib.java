/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 *     
 */
package edu.harvard.i2b2.smlib;

import edu.harvard.i2b2.smlib.xml.XmlHandler;
import edu.harvard.i2b2.smlib.xml.XmlParser;
//import edu.harvard.i2b2.smlib.xml.XmlException;
import java.util.Hashtable;
import java.util.Enumeration;

public class XMLLib implements XmlHandler, Runnable {
  private static Hashtable ht;
  private static String firstElementName;
  private static String currentElementName;
  private static String currentCharacterData;
  private static boolean ready;
  private static boolean doneNothing;
  private static String m_inputString;
  private static int msecIncrement=100; // increments in msec to look at proccess
  private static int maxWaitTime=1000; // time in msec until decides to abort
  private static boolean parse_debug=false; // true for debugging log messages
  
  public static String GetSingleXMLValue(String name, String inputString) {
    if ((name==null)||(name.length()==0)) return null;
    if ((inputString==null)||(inputString.length()==0)) return null;
    Hashtable newHt = GetXMLValues(inputString);
    if (newHt==null) return null;
    return (String) newHt.get(name);
  }
 
  public static synchronized Hashtable GetXMLValues(String inputString) {
    if ((inputString==null)||(inputString.length()==0)) return null;
    m_inputString = inputString;
    try {
      int waitTime = 0;
      ht = new Hashtable();
	  ready = false;
	  // must initialize the static 'ready' property
	  // it may not have been restored after the previous use
      Thread theXMLLib_thread = new Thread ((new XMLLib()));
      theXMLLib_thread.start();
      while (!ready) {
        doneNothing = true;
        Thread.sleep(msecIncrement,0);
        if (doneNothing) {
          waitTime = waitTime+msecIncrement;
          if (parse_debug==true) {
            Lib.TMessage("done nothing for "+Integer.toString(waitTime)+" msec");
          }
          if (waitTime > maxWaitTime) {
            ready = true;
          }
        }
        else {
          waitTime = 0;
        }
      }
      theXMLLib_thread.stop();
      if (parse_debug==true) {
        Enumeration theTableValues = ht.elements();
        while (theTableValues.hasMoreElements()) {
          Lib.TMessage(theTableValues.nextElement().toString());
        }
      }
    }
    catch (Exception e) {
      Lib.TError("XML Parser Error: "+e.getMessage());
      return null;
    }
    return ht;
  }
  
  public void run() {
    try {
      XmlParser theParser = new XmlParser();
      theParser.setHandler(this);
      theParser.parse(m_inputString, null);
    }
    catch (Exception e) {
      Lib.TError(e.getMessage());
    }
  }
  
  public void startDocument ()
    throws java.lang.Exception {
    doneNothing = false;
    if (parse_debug==true) Lib.TMessage("Start Document");
    firstElementName = null;
    ready = false;
  }

  public void endDocument ()
    throws java.lang.Exception {
    doneNothing = false;
    if (parse_debug==true) Lib.TMessage("End Document");
    ready = true;
  }

  public Object resolveEntity (String publicId, String systemId)
    throws java.lang.Exception {
    return null;
  }

  public void startExternalEntity (String systemId)
    throws java.lang.Exception {}

  public void endExternalEntity (String systemId)
    throws java.lang.Exception {}

  public void doctypeDecl (String name, String publicId, String systemId)
    throws java.lang.Exception {}

  public void attribute (String aname, String value, boolean isSpecified)
    throws java.lang.Exception {}

  public void startElement (String elname)
    throws java.lang.Exception {
    doneNothing = false;
    if (parse_debug==true) Lib.TMessage("Start element:  " + elname);
    if ((elname==null)||(elname.length()==0)) return;
    if (firstElementName==null) {
      firstElementName = elname;
    }
    currentElementName = elname;
  }

  public void endElement (String elname)
    throws java.lang.Exception {
    doneNothing = false;
    if (parse_debug==true) Lib.TMessage("End element:  " + elname);
    try {
      if ((elname==null)||(elname.length()==0)) return;
      if (currentElementName.equals(elname)) {
        ht.put(currentElementName, currentCharacterData);
      }
      if (firstElementName.equals(elname)) {
        ready = true;
      }
    }
    catch (Exception e) {
      ready = true;
      throw new Exception(e.getMessage());
    }
  }

  public void charData (char ch[], int start, int length)
    throws java.lang.Exception {
    doneNothing = false;
    if (parse_debug==true) Lib.TMessage("Character data:  " + new String(ch,start,length));
    currentCharacterData = new String(ch,start,length);
  }

  public void ignorableWhitespace (char ch[], int start, int length)
    throws java.lang.Exception {}
  
  public void processingInstruction (String target, String data)
    throws java.lang.Exception {}

  public void error (String message, String systemId, int line, int column)
    throws java.lang.Exception {
    if (parse_debug==true) Lib.TMessage("Error:  " + message);
  }
}