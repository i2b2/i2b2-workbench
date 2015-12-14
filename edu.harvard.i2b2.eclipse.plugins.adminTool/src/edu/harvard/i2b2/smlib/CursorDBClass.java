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
/**
/* cursor function for databases
/* table has to have an autoincrement field and a timestamp field
/* (c) 2000 Shawn Murphy
/*
/* oddities:
/*  Access97 dosesn't like columns named "level"
/*/

package edu.harvard.i2b2.smlib;

import java.sql.*;
import java.sql.Types;
import java.util.Date;
import java.util.Hashtable;
import java.util.Enumeration ;
import java.math.BigDecimal;

public class CursorDBClass {
  private final static String CONNECTION_FAILED_MESSAGE = "stuff";
  // global connection characteristics
  private String msDBServerName;
  private String msDBName;
  private String msUserName;
  private String msPassword;
  private Connection moCursorConnection;
  // global table characteristics
  private String msTableName;
  private String msAutoincrementColumnName;
  // derived table characteristics
  private int miMin;
  private int miMax;
  // global cursor characteristics
  private boolean bAssumeSequentialAutonumbers = false;
  private boolean mbMadeNewAutonumberColumn = false;
  public boolean mbBOF;
  public boolean mbEOF;
  public int miCurrentCursorLocation;
  public ResultSet moCurrentResultSet;
  private Hashtable moSelectTable = new Hashtable();
  private Hashtable moPrimaryKeyTable = new Hashtable();
  private Hashtable moUpdateTable = new Hashtable();
  // constants
  private String msBizareAutonumbername = "c_hyfgeekhgwp";
  // debug variables  
	private boolean noisy = true;
	public CursorDBClass(
      Connection oCursorConnection,
      String sTableName)
      throws Exception {
    this(oCursorConnection,sTableName,null);
  }
 
  public CursorDBClass(
      Connection oCursorConnection,
      String sTableName,
      String sAutoincrementColumnName)
      throws Exception {
    String sFunction = "CursorDBClass";
    Exception e = new Exception("Failed to inititalize CursorDBClass");
    if (oCursorConnection == null) throw e;
    if ((sTableName == null) || (sTableName.length() == 0 )) throw e;
    moCursorConnection = oCursorConnection;
    msTableName = sTableName;
    // make the autonumber column
    try {
      msAutoincrementColumnName = DBLib.getAutonumberColumnName(oCursorConnection, sTableName);
      if ((msAutoincrementColumnName==null)||(msAutoincrementColumnName.length()==0)) {
        mbMadeNewAutonumberColumn = true;
        try {
          if ((sAutoincrementColumnName==null)||(sAutoincrementColumnName.length()==0)) {
            sAutoincrementColumnName = msBizareAutonumbername;
          }
          DBLib.createTSQLAutonumberColumn(oCursorConnection,sTableName,sAutoincrementColumnName);
          msAutoincrementColumnName = sAutoincrementColumnName;
        }
        catch (SQLException etry) {
          String sSqlState = etry.getSQLState();
          Lib.TError(sSqlState);
          if (sSqlState.equals("37000")) {
            //DBLib.createTSQLAutonumberColumn(oCursorConnection,sTableName,msBizareAutonumbername);
          }
          else if (sSqlState.equals("37000x")) {
            Lib.TError("Column " + sAutoincrementColumnName + " existed in table " + sTableName + ".");
          }
          else {
            throw etry;
          }
        }
        catch (Exception etry) {
          Lib.TError("Exception in CursorDBClass.initialization: " + etry.getMessage());
          throw etry;
        }
      }
      else {
        mbMadeNewAutonumberColumn = false;
      }
    }
    catch (Exception etry) {
      Lib.TError("Exception in CursorDBClass.initialization: " + etry.getMessage());
      throw etry;
    }
    // index the autonumber column
    DBLib.makeMyIndex(moCursorConnection,msTableName,msAutoincrementColumnName);
    // once you know the autonumber column, set up the cursor
    boolean bRecover = true;
    boolean bRepeat = false;
    do {
      try {
        SetUpCursorCharacteristics(moCursorConnection,msTableName,msAutoincrementColumnName);
        miCurrentCursorLocation = miMin-1; // set cursor to no row so next works right
        moPrimaryKeyTable.put(msAutoincrementColumnName, new Integer(miCurrentCursorLocation));
        return;
      }
      catch (Exception eee) {
        if (bRecover) {
          // test the connection
          try {
            if (!moCursorConnection.isClosed()) {
              bRepeat = false;
            }
          }
          catch (Exception eeee) {
            if (eeee.getMessage().equals(CONNECTION_FAILED_MESSAGE)) {            
              while (moCursorConnection == null) {
                this.wait(15000);
                moCursorConnection = DBLib.openODBCConnection(msDBServerName, msDBName, msUserName, msPassword);
              }
              bRepeat = true;
            }
          }
        }
        else {
          bRepeat = false;
        }
      }
    } while (bRepeat == true);
    // if it gets out here, something went wrong
    Lib.TError("CursorDBClass failed to initialize");
    return;
  }
  
  private boolean SetUpCursorCharacteristics(
      Connection oConn, 
      String sTable,
      String sAutoincrementColumnName ) 
      throws Exception {
    String sFunction = "SetUpCursorCharacteristics";
    try {
      // get minimum of autoincrement column
	    String sMinQuery = "SELECT MIN(" + sAutoincrementColumnName + ") THEMIN FROM " + msTableName;
	    ResultSet oRsMin = this.doQuery(oConn, sMinQuery);
	    if (!oRsMin.next()) {
	      mbBOF = true;
	      mbEOF = true;
        this.killQuery();
	      return true;
	    }
	    else {
	      miMin = oRsMin.getInt("THEMIN");
        this.killQuery();
	    }
	    // get maximum of autoincrement column
	    String sMaxQuery = "SELECT MAX(" + sAutoincrementColumnName + ") THEMAX FROM " + msTableName;
	    ResultSet oRsMax = this.doQuery(moCursorConnection, sMaxQuery);
	    oRsMax.next();
	    miMax = oRsMax.getInt("THEMAX");
      this.killQuery();
      return true;
    }
    catch (Exception e) {
      if (noisy) Lib.TError("Could not set up MIN or MAX; " + sFunction);
      throw e;
    }
  }
  
  public void Destroy() throws Exception {
    String sFunction = "Destroy";
    Exception e = new Exception("Failed to destroy CursorDBClass");
    try {
      DBLib.dropMyIndex(moCursorConnection, msTableName, msAutoincrementColumnName);
      if (mbMadeNewAutonumberColumn) {
        DBLib.dropColumn(moCursorConnection, msTableName, msAutoincrementColumnName);
      }
    }
    catch (Exception ee) {
      Lib.TError("Error: " + e.getMessage() + ": " + ee.getMessage());
      throw e;
    }
  }

  public Hashtable getShallowResultsetCopy() {
    return (Hashtable)(moSelectTable.clone());
  }
  
  public Object getRowObject(String sColumnName) throws Exception {
    String sFunction = "getRowObject";
    try {
      Object oValue = moSelectTable.get(sColumnName.toLowerCase());
      return oValue;
    }
    catch (Exception e) {
      Lib.TError("ERR in " + sFunction + ": " + e.getMessage());
      throw new Exception("ERR in " + sFunction + ": " + e.getMessage());
    }
  }
    
  public String getRowString(String sColumnName) throws Exception {
    String sFunction = "getRowString";
    Object oValue = moSelectTable.get(sColumnName.toLowerCase());
    if (oValue instanceof String) {
      return (String)(oValue);
    }
    else {
      Lib.TError("Tried to get a String from datatype "+oValue.getClass().getName());
      throw new SQLException("Tried to get a String from datatype "+oValue.getClass().getName());
    }
  }
    
  public long getRowLong(String sColumnName) throws Exception {
    String sFunction = "getRowLong";
    Object oValue = moSelectTable.get(sColumnName.toLowerCase());
    if (oValue instanceof Long) {
      Long lValue = (Long)(oValue);
      return lValue.longValue();
    }
    else {
      Lib.TError("Tried to get a Long from datatype "+oValue.getClass().getName());
      throw new SQLException("Tried to get a Long from datatype "+oValue.getClass().getName());
    }
  }
    
  public java.util.Date getRowDate(String sColumnName) throws Exception {
    String sFunction = "getRowDate";
    Object oValue = moSelectTable.get(sColumnName.toLowerCase());
    if (oValue instanceof Date) {
      Date dValue = (Date)(oValue);
      return dValue;
    }
    else {
      Lib.TError("Tried to get a Date from datatype "+oValue.getClass().getName());
      throw new SQLException("Tried to get a Date from datatype "+oValue.getClass().getName());
    }
  }
        
  public boolean setRowObject(String sColumnName, Object oColumnValue) throws Exception {
    String sFunction = "setRowObject";
    if ((sColumnName == null) || (sColumnName.length() == 0)) return false;
    try {
      moUpdateTable.put(sColumnName.toLowerCase(),oColumnValue);
      return true;
    }
    catch (Exception e) {
      Lib.TError("ERR in " + sFunction + ": " + e.getMessage());
      throw new Exception("ERR in " + sFunction + ": " + e.getMessage());
    }
  }
  
  public boolean setRowString(String sColumnName, String sColumnValue) throws Exception {
    String sFunction = "setRowString";
    try {
      moUpdateTable.put(sColumnName.toLowerCase(),sColumnValue);
      return true;
    }
    catch (Exception e) {
      Lib.TError("ERR in " + sFunction + ": " + e.getMessage());
      throw new Exception("ERR in " + sFunction + ": " + e.getMessage());
    }
  }
  
  public boolean setRowLong(String sColumnName, long lColumnValue) throws Exception {
    String sFunction = "setRowLong";
    try {
      moUpdateTable.put(sColumnName.toLowerCase(), new Long(lColumnValue));
      return true;
    }
    catch (Exception e) {
      Lib.TError("ERR in " + sFunction + ": " + e.getMessage());
      throw new Exception("ERR in " + sFunction + ": " + e.getMessage());
    }
  }

  public boolean setRowDate(String sColumnName, java.util.Date oColumnValue) throws Exception {
    String sFunction = "setRowLong";
    try {
      moUpdateTable.put(sColumnName.toLowerCase(), new java.sql.Date(oColumnValue.getTime()));
      return true;
    }
    catch (Exception e) {
      Lib.TError("ERR in " + sFunction + ": " + e.getMessage());
      throw new Exception("ERR in " + sFunction + ": " + e.getMessage());
    }
  }

  //* next() - advances the database to the next row in the autonumber sequence
  public boolean next(boolean bRecover) throws Exception {
    String sFunction = "next";
    int iNextNumber = -1;
    boolean bRepeat = false;
    do {
      try {
        // get the next autoincrement number
        if (bAssumeSequentialAutonumbers) {
          do {
            if (miCurrentCursorLocation > miMax) {
              bAssumeSequentialAutonumbers = false;
              break;
            }
            String sAutoincrementNumber = Integer.toString(miCurrentCursorLocation+1);
            String sGetNextNumberQuery = "SELECT " + msAutoincrementColumnName + 
              " THEMIN FROM " + msTableName + " WHERE " + msAutoincrementColumnName + " = " + 
              sAutoincrementNumber;
            ResultSet oNextNumber = this.doQuery(moCursorConnection, sGetNextNumberQuery);
            if (!oNextNumber.next()) {
              miCurrentCursorLocation++;
              this.killQuery();
              continue;
            }
            else {
              iNextNumber = oNextNumber.getInt("THEMIN");
              this.killQuery();
              break;
            }        
	        } while (true);	          
        }
        // this test has to happen after the first if because bAssumeSequentialAutonumbers might
        // be changed to false
        if (!bAssumeSequentialAutonumbers) {
          String sAutoincrementNumber = Integer.toString(miCurrentCursorLocation);
          String sGetNextNumberQuery = "SELECT MIN(" + msAutoincrementColumnName + 
            ") THEMIN FROM " + msTableName + " WHERE " + msAutoincrementColumnName + " > " + 
            sAutoincrementNumber;
          ResultSet oNextNumber = this.doQuery(moCursorConnection, sGetNextNumberQuery);
	        //... if another autoincrement number does not exist we have reached the end
	        if (!oNextNumber.next()) {
	          mbEOF = true;
	          this.killQuery();
	          return false;
	        }
	        else {
            iNextNumber = oNextNumber.getInt("THEMIN");
            this.killQuery();
          }
        }
        // ... do one more check to see if the end of file was reached, compare new and old cursor
        if (miCurrentCursorLocation == iNextNumber) {
	        mbEOF = true;
	        this.killQuery();
	        return false;
        }
        // get the next row values
        String sNextNumber = Integer.toString(iNextNumber);
        String sGetResultsetQuery = "SELECT * FROM " + msTableName + 
          " WHERE " + msAutoincrementColumnName + " = " + 
          sNextNumber;
        ResultSet oCurrentResultSet = this.doQuery(moCursorConnection, sGetResultsetQuery);
	      //... advance to the row, actually an error if the row does not exist
	      if (!oCurrentResultSet.next()) {
	        mbEOF = true;
	        this.killQuery();
	        return false;
	      }
	      //... making the new hash table
	      Hashtable oSelectTable = new Hashtable();
	      String sColumnName = null;
	      Object oColumnValue = null;
        ResultSetMetaData rmd = oCurrentResultSet.getMetaData();
        int numColumns = rmd.getColumnCount();
        for (int i=1; i<=numColumns; i++) {
          try {
            sColumnName = rmd.getColumnName(i);
            if (sColumnName == null) sColumnName = Integer.toString(i);
          }
          catch (SQLException sqle) {
            sColumnName = Integer.toString(i);
          }
          oColumnValue = getColumnValue(i,rmd,oCurrentResultSet);
          if (oColumnValue==null) continue;
          if (sColumnName.equalsIgnoreCase(msAutoincrementColumnName)) continue;
 	        oSelectTable.put(sColumnName.toLowerCase(), oColumnValue);
 	      }
 	      moSelectTable = null;
 	      moSelectTable = oSelectTable;
 	      miCurrentCursorLocation = iNextNumber;
	      mbEOF = false;
        return true;
      }
      catch (Exception e) {
        String hi = e.getMessage();
        if (bRecover) {
          // test the connection
          try {
            if (!moCursorConnection.isClosed()) {
              bRepeat = false;
            }
          }
          catch (Exception ee) {
            if (ee.getMessage().equals(CONNECTION_FAILED_MESSAGE)) {            
              while (moCursorConnection == null) {
                this.wait(15000);
                moCursorConnection = DBLib.openODBCConnection(msDBServerName, msDBName, msUserName, msPassword);
              }
              bRepeat = true;
            }
          }
        }
        else {
          bRepeat = false;
        }
      }
    } while (bRepeat == true);
    // if it gets out here, something went wrong
    return false;
  }
  
  public boolean update(boolean bRecover) throws Exception {
    String sFunction = "update";
    String sSelectColumnName = null;
    String sUpdateColumnName = null;
    String sSqlUpdateString = null;
    Object oSelectValue = null;
    Object oUpdateValue = null;
    Hashtable oUpdateChangesTable = new Hashtable();
    try {
      if (moUpdateTable == null) return false;
      // compare the Select and Update tables, keep the changed values in a new Hashtable.
      if (moUpdateTable.isEmpty()) return true;
      Hashtable oPrimaryKeyTable = new Hashtable();
      oPrimaryKeyTable.put(msAutoincrementColumnName, new Integer(miCurrentCursorLocation));
      sSqlUpdateString = makeUpdateString(msTableName, oPrimaryKeyTable, moUpdateTable);
    }
    catch (Exception e) {
      Lib.TError(e.getMessage() + " in " + sFunction);
      throw e;
    }
    boolean bRepeat = false;
    do {
      try {
        doUpdate(moCursorConnection, sSqlUpdateString);
        bRepeat = false;
      }
      catch (Exception e) {
        if (bRecover) {
          // test the connection
          try {
            if (!moCursorConnection.isClosed()) {
              bRepeat = false;
            }
          }
          catch (Exception ex) {
            if (ex.getMessage().equals(CONNECTION_FAILED_MESSAGE)) {            
              while (moCursorConnection == null) {
                this.wait(15000);
                moCursorConnection = DBLib.openODBCConnection(msDBServerName, msDBName, msUserName, msPassword);
              }
              bRepeat = true;
            }
          }
        }
        else {
          bRepeat = false;
        }
      }
    } while (bRepeat == true);
    return true;
  }
    
  public String makeUpdateString(
      String sTable, // SQL table
      Hashtable oPrimaryTable, // primary keys
      Hashtable oUpdateTable)  // update values
  {
    if ((sTable == null) || (sTable.length() == 0)) return null;
    if (oPrimaryTable.size() == 0) return null;
    if (oUpdateTable.size() == 0) return null;
    int iOverOneIndicator;
    // make the 'where' clause
    Enumeration oPrimaryTableKeyEnumerator = oPrimaryTable.keys();
    StringBuffer whereSB = new StringBuffer(64);
    iOverOneIndicator = 0;
    while (oPrimaryTableKeyEnumerator.hasMoreElements()) {
      String column_name = (String) oPrimaryTableKeyEnumerator.nextElement();
      Object value_object = (Object) oPrimaryTable.get(column_name);
      String value = DBLib.objectToSQLString(value_object);
      if ((column_name == null) || (column_name.length() == 0)) {
         if (noisy) Lib.TMessage("Bad primary key column name passed to makeUpdateString");
         continue;
      }
      else if ((value == null) || (value.length() == 0)) {
         if (noisy) Lib.TMessage("Bad update value passed to makeUpdateString, column "+column_name+
                      " was not updated");
         continue;
      }
      else {
        iOverOneIndicator++;
      }
      if (iOverOneIndicator>1) whereSB.append(" AND ");
      whereSB.append(column_name+"="+value);
    }
    if (whereSB.length() == 0) {
        return null;
    }
    // make the update string
    Enumeration oUpdateTableKeyEnumerator = oUpdateTable.keys();
    StringBuffer updateSB = new StringBuffer(64);    
    iOverOneIndicator = 0;
    while (oUpdateTableKeyEnumerator.hasMoreElements()) {
      String column_name = (String) oUpdateTableKeyEnumerator.nextElement();
      Object value_object = (Object) oUpdateTable.get(column_name);
      String value = DBLib.objectToSQLString(value_object);
      if ((column_name == null) || (column_name.length() == 0)) {
        if (noisy) Lib.TMessage("Bad update column name passed to makeUpdateString");
        continue;
      }
      else if ((value == null) || (value.length() == 0)) {
        if (noisy) Lib.TMessage("Bad update value passed to makeUpdateString, column "+column_name+
                      " was not updated");
        continue;
      }
      else {
        iOverOneIndicator++;
      }
      if (iOverOneIndicator>1) updateSB.append(", ");
      updateSB.append(column_name+"="+value);
    }
    if (updateSB.length() == 0) {
        return null;
    }
    // make final update string
    String SQL_Update = "UPDATE "+sTable+
      " SET "+ updateSB.toString()+
      " WHERE "+whereSB.toString();
    return SQL_Update;
  }
    
  public int doUpdate(Connection con, String sql) throws Exception {
    int result = 0;
    Statement stmt = null;
    try {
      // Create a statement
      stmt = con.createStatement();
      // Execute the update statement
      result = stmt.executeUpdate(sql);
      // always do this update
      if (con.getAutoCommit() == false) con.commit();
      // Show output if no updates made
      if (result == 0) {
        if (noisy) Lib.TMessage("No update made with SQL statement "+sql+".");
        //System.out.println(stmt.getWarnings().getMessage());
      }
    }
    catch (SQLException e) {
      // Print the exception
      Lib.TError("SQL error with SQL statement: "+sql+".");
      throw e;
    }
    catch(Exception e) {
      Lib.TError("General error with SQL statement: "+sql+".");
      throw e;
    }
    finally {
      try {
        // try to close statement
        if (stmt != null) stmt.close();
      }
      catch(SQLException e) {
        Lib.TError("Error while trying to close statement after update: "+sql+".");
        throw e;
      }
    }
    return result;
  }
    
  private static ResultSet result = null;
  private static Statement stmt = null;
  
  public ResultSet doQuery(Connection con, String sql) throws SQLException, Exception {
    if (con == null) return null;
    if ((sql==null) || (sql.length()==0)) return null;
    if ((result != null) || (stmt != null)) {
      //Lib.TError("Started a new query before killing the old one, killing it now.");
      killQuery();
    }
    try {
      // Create a statement
      stmt = con.createStatement();
      // Execute the query statement
      result = stmt.executeQuery(sql);
    }
    catch (SQLException e) {
      // Print the exception
      Lib.TError("SQL error with SQL statement: "+sql+".");
      if (result != null) result.close();
      result = null;
      if (stmt != null) stmt.close();
      stmt = null;
      throw e;
    }
    catch(Exception e) {
      Lib.TError("General error with SQL statement: "+sql+".");
      if (result != null) result.close();
      result = null;
      if (stmt != null) stmt.close();
      stmt = null;
      throw e;
    }
    return result;
  }

  public void killQuery() {
    try {
      if (result != null) result.close();
      result = null;
    }
    catch(SQLException sqle) {
      result=null;
    }
    try {
      if (stmt != null) stmt.close();
      stmt = null;
    }
    catch (SQLException sqle) {
      stmt = null;
    }
  }
  
  public static Object getColumnValue(int i, java.sql.ResultSetMetaData rmd, java.sql.ResultSet oCurrentResultSet) {
    Object oValue = null;
    String sFunction = "getColumnValue";
    try {
      if (rmd == null) {
        throw new Exception("null ResultSetMetaData passed; getColumnValue");
      }  
      if (rmd == null) {
        throw new Exception("null ResultSet passed; getColumnValue");
      }  
      int sSQL_type = rmd.getColumnType(i);
      if ((sSQL_type == Types.INTEGER)) {
        oValue = new Long(oCurrentResultSet.getInt(i));
      }
      else if (sSQL_type == Types.BIGINT) {
        oValue = new Long(oCurrentResultSet.getLong(i));
      }
      else if (sSQL_type == Types.SMALLINT) {
        oValue = new Long(oCurrentResultSet.getShort(i));
      }
      else if (sSQL_type == Types.TINYINT) {
        oValue = new Byte(oCurrentResultSet.getByte(i));
      }
      else if ((sSQL_type == Types.VARCHAR) || (sSQL_type == Types.LONGVARCHAR) ||
               (sSQL_type == Types.CHAR)) {
        oValue = new String(oCurrentResultSet.getString(i));
      }
      else if ((sSQL_type == Types.BINARY) || (sSQL_type == Types.VARBINARY) ||
               (sSQL_type == Types.LONGVARBINARY)) {
        // oValue = new byte[(oCurrentResultSet.getBytes(i))];
        oValue = null;
      }
      else if ((sSQL_type == Types.FLOAT) || (sSQL_type == Types.DOUBLE)) {
        oValue = new Double(oCurrentResultSet.getDouble(i));
      }
      else if ((sSQL_type == Types.NUMERIC) || (sSQL_type == Types.DECIMAL)) {
        oValue = new BigDecimal(oCurrentResultSet.getBigDecimal(i,2).toString());
      }
      else if (sSQL_type == Types.REAL) {
        oValue = new Double(oCurrentResultSet.getFloat(i));
      }
      else if ((sSQL_type == Types.BIT)) {
        oValue = new Boolean(oCurrentResultSet.getBoolean(i));
      }
      else if ((sSQL_type == Types.DATE)) {
        oValue = (java.util.Date)(oCurrentResultSet.getDate(i));
      }
      else if (sSQL_type == Types.TIME) {
        oValue = Time.valueOf(oCurrentResultSet.getTime(i).toString());
      }
      else if (sSQL_type == Types.TIMESTAMP) {
        oValue = Timestamp.valueOf(oCurrentResultSet.getTimestamp(i).toString());
      }
      else if (sSQL_type == -9) { // this must be nvarchar
        oValue = new String(oCurrentResultSet.getString(i));
      }
      else if (sSQL_type == -8) { // this must be nchar
        oValue = new String(oCurrentResultSet.getString(i));
      }
      else {
        oValue = null;
      }
      return oValue;
    }
    catch (NullPointerException npe) {
      return null;
    }
    catch (Exception e) {
      Lib.TError(e.getMessage() + "; " + sFunction);
      return null;
    }
  }
}
