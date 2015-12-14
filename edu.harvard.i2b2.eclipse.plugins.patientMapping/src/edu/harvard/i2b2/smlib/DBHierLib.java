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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.Enumeration;

public class DBHierLib {
  private static String m_levelSeparator="\\";
  private static String m_dbWildcard="%";
  private static int m_initTempVectorSize=100;
  private static String snmClassName="snm.library.DBHierLib";
  
  public static DBNode[] getTop(String ODBCSource, String table) {
    return getTop(ODBCSource,table,1);
  }
  
  public static DBNode[] getTop(String ODBCSource, String table, int topLevel) {
    if ((ODBCSource==null)||(ODBCSource.length()==0)) return null;
    if ((table==null)||(table.length()==0)) return null;
    try {
      String sqlStatement =
        "SELECT c_hierlevel, c_parent, c_symbol, c_name, c_haschildren "+
        "  FROM "+table+" "+
        "  WHERE c_hierlevel="+Integer.toString(topLevel);
      DBNode[] newNodes = getNodes(ODBCSource,table,sqlStatement);
      return newNodes;
    }
    catch (Exception e) {
      Lib.TError(snmClassName+".getTop()");
      Lib.TError("Error: "+e.getMessage());
      return null;
    }
  }

  public static DBNode[] getChildren(DBNode parentNode) {
    try {
      if (parentNode==null) return null;
      String ODBCSource = parentNode.getODBCSource();
      if (ODBCSource==null) return null;
      String table = parentNode.getTable();
      if (table==null) return null;
      String sqlStatement =
        "SELECT c_hierlevel, c_parent, c_symbol, c_name, c_haschildren "+
        "  FROM "+table+" "+
        "  WHERE c_hierlevel="+Integer.toString(parentNode.getDBHierlevel()+1)+" "+
        "    AND c_parent LIKE "+parentNode.getDBFullpath()+m_levelSeparator+m_dbWildcard;
      DBNode[] newNodes = getNodes(ODBCSource,table,sqlStatement);
      return newNodes;
    }
    catch (Exception e) {
      Lib.TError(snmClassName+".getChildren()");
      Lib.TError("Error: "+e.getMessage());
      return null;
    }
  }

  public static DBNode[] getNodes(String ODBCSource, String table, String sqlStatement) {
    Connection con=null;
    Statement stmt = null;
    ResultSet rs=null;
    if ((ODBCSource==null)||(ODBCSource.length()==0)) return null;
    if ((table==null)||(table.length()==0)) return null;
    if ((sqlStatement==null)||(sqlStatement.length()==0)) return null;
    try {
      if (ODBCSource==null) return null;
      if (table==null) return null;
      con = DBLib.openODBCConnection(ODBCSource);
      if (con==null) return null;
      stmt = con.createStatement();
      rs = stmt.executeQuery(sqlStatement);
      if (rs==null) return null;
      // find all the rows of the children
      Vector tempDBNodeVector = new Vector(m_initTempVectorSize);
      long row_number = 0;
      boolean more = rs.next();
      while (more) {
        row_number++;
        DBNode newDBNode = 
          new DBNode(rs.getInt(1),
                     rs.getString(2).trim(),
                     rs.getString(3).trim(),
                     rs.getString(4).trim(),
                     rs.getBoolean(5),
                     ODBCSource,
                     table);
        tempDBNodeVector.addElement(newDBNode);
        more = rs.next();
        if (!more)
          more = stmt.getMoreResults();
      }
      int numNodes = tempDBNodeVector.size();
      if (numNodes==0) return null;
      int countNodes = 0;
      DBNode[] children = new DBNode[numNodes];
      Enumeration tempDBNodeEnumeration = tempDBNodeVector.elements();
      while (tempDBNodeEnumeration.hasMoreElements()) {
        children[countNodes] = (DBNode)tempDBNodeEnumeration.nextElement();
        countNodes++;
      }
      return children;
    }
    catch (SQLException sqle) {
      Lib.TError(snmClassName+".getNodes()");
      Lib.TError("SQLException with SQL statement: "+sqlStatement+".");
      return null;
    }
    catch (Exception e) {
      Lib.TError(snmClassName+".getNodes()");
      Lib.TError("General error with SQL statement: "+sqlStatement+".");
      return null;
    }
    finally {
      try {
        if (stmt != null) stmt.close();
        if (con != null) con.close();
      }
      catch(SQLException e) {
        Lib.TError(snmClassName+".getChildren(DBNode)");
        Lib.TError("Error while trying to close");
      }
    }
  }
}
