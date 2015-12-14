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

public class DBNode {
  private static String m_levelSeparator="\\";
  private int c_hierlevel;
  private String c_parent;
  private String c_symbol;
  private String c_name;
  private boolean c_haschildren;
  private String m_ODBCSource;
  private String m_table;
  
  public DBNode(int hierlevel,
                String parent,
                String symbol,
                String name,
                boolean haschildren) {
    c_hierlevel = hierlevel;
    c_parent = parent;
    c_symbol = symbol;
    c_name = name;
    c_haschildren = haschildren;
  }
  public DBNode(int hierlevel,
                String parent,
                String symbol,
                String name,
                boolean haschildren,
                DBNode parentNode) {
    this(hierlevel,parent,symbol,name,haschildren);
    m_ODBCSource = parentNode.getODBCSource();
    m_table = parentNode.getTable();
  }
  public DBNode(int hierlevel,
                String parent,
                String symbol,
                String name,
                boolean haschildren,
                String ODBCSource,
                String table) {
    this(hierlevel,parent,symbol,name,haschildren);
    m_ODBCSource = ODBCSource;
    m_table = table;
  }
  public int getDBHierlevel() {
    return c_hierlevel;
  }
  public String getDBParent() {
    return c_parent;
  }
  public String getDBSymbol() {
    return c_symbol;
  }
  public String getDBName() {
    return c_name;
  }
  public boolean getDBHaschildren() {
    return c_haschildren;
  }
  public String getDBFullpath() {
    if ((c_parent==null)||(c_parent.length()==0)) return c_symbol;
    else return c_parent+m_levelSeparator+c_symbol;
  }
  public String getODBCSource() {
    return m_ODBCSource;
  }
  public String getTable() {
    return m_table;
  }               
}
