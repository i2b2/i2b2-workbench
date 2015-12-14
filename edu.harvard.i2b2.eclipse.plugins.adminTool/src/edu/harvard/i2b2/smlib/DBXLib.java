/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	
 *     Shawn Murphy
 */
/**
/* Big bunch of static funtions for databases
/* (c) 1998 Shawn Murphy
/* Last Modified 9148
/*
 * oddities:
 *  Access97 dosesn't like columns named "level"
/*/

package edu.harvard.i2b2.smlib;

import java.util.StringTokenizer;
import java.util.Date;
import java.util.Hashtable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DBXLib {
  private static String msDBType = "SQLServer";
  public static boolean noisy=true;
 /**
	* This class shouldn't be instantiated.
	*/
  private DBXLib() {}
	/**
	/*  openODBCConnection - makes the ODBC connection, returns null if it fails
	/*/
  public static Connection openODBCConnection(String ODBC_source, String user, String pwd) {
    String sun_drv = "sun.jdbc.odbc.JdbcOdbcDriver";
    String ms_drv = "com.ms.jdbc.odbc.JdbcOdbcDriver";
    String drv = "sun.jdbc.odbc.JdbcOdbcDriver";
    String ODBC_beginswith = "jdbc:odbc:";
    String url = null;
    if ((ODBC_source == null) || (ODBC_source.length() == 0)) {
      Lib.TError("getODBCConnection failed to get an ODBC_source arguement");
      return null ;
    }
    else if (ODBC_source.startsWith(ODBC_beginswith)) {
      url = ODBC_source;
    }
    else {
      url = ODBC_beginswith + ODBC_source;
    }
    if (user == null) user = "";
    if (pwd == null) pwd = "";
    try {
      // Load Database Bridge or Driver
      if (noisy) Lib.TMessage("Loading driver ...");
      if (System.getProperty("java.vendor").equals("Microsoft Corp.")) {
        drv = ms_drv; // Visual J++
      }
      else {
        // sun_drv is currently the default
      }
      //Class.forName(drv);
      //DataSource ds = ServiceLocator.getDataSource();
      //DataSource ds = new BasicDataSource();
      if (noisy) Lib.TMessage("Driver "+drv+" was loaded.");
      // Try to connect to the specified database
      if (noisy) Lib.TMessage("Connecting to database ...");
//		DataSource ds = ServiceLocator.getDataSource();
//		Connection con = ds.getConnection(user, pwd);
		Connection con = null;
		//DataSource con = DriverManager.getConnection(url, user, pwd);
      if (noisy) Lib.TMessage("Connection to "+url+" established.");
      return con;
    }
//    catch(SQLException e) {
//      Lib.TError("SQL Error in opening... "+e.getMessage());
//      return null;
//    }
    catch(Exception e) {
      Lib.TError("General Error in opening database... "+e.getMessage());
      return null;
    }
  }
  
  public static Connection openODBCConnection(String ODBC_source) {
    return openODBCConnection(ODBC_source, "admin", "");
  }
  
  public static Connection openODBCConnection(String server, String database, String user, String pwd) {
    String sun_drv = "sun.jdbc.odbc.JdbcOdbcDriver";
    String ms_drv = "com.ms.jdbc.odbc.JdbcOdbcDriver";
    String drv = "com.ms.jdbc.odbc.JdbcOdbcDriver";
    String ODBC_beginswith = "jdbc:odbc:";
    String url = null;
    if (server == null) return null;
	  if (database == null) return null;
    if (user == null) user = "";
    if (pwd == null) pwd = "";
	
	  url = "JDBC:ODBC:DRIVER={SQL Server};SERVER=" + server + ";DATABASE=" + database + ";UID=" + user + ";PWD=" + pwd + ";";
     		
    try {
      // Load Database Bridge or Driver
      if (noisy) Lib.TMessage("Loading driver ...");
      if (System.getProperty("java.vendor").equals("Microsoft Corp.")) {
        drv = ms_drv; // Visual J++
      }
      else {
        // sun_drv is currently the default
      }
      Class.forName(drv);
      if (noisy) Lib.TMessage("Driver "+drv+" was loaded.");
      // Try to connect to the specified database
      if (noisy) Lib.TMessage("Connecting to database ...");
      Connection con = DriverManager.getConnection(url, user, pwd);
      if (noisy) Lib.TMessage("Connection to "+url+" established.");
      return con;
    }
    catch(SQLException e) {
      Lib.TError("SQL Error in opening... "+e.getMessage());
      return null;
    }
    catch(Exception e) {
      Lib.TError("General Error in opening database... "+e.getMessage());
      return null;
    }
  }
	/**
	/*  closeODBCConnection - closes the ODBC connection, returns false if it fails
	/*/
  public static boolean closeODBCConnection(Connection con) {
    try {
      if (con != null) con.close();
    }
    catch(SQLException e) {
      Lib.TError("SQL Error in closing... "+e.getMessage());
      return false;
    }
    return true;
  }

  public static boolean createTable(Connection con, String table, String creationValues) {
    if (con == null) return false;
    if ((table == null) || (table.length() == 0)) return false;
    if ((creationValues == null) || (creationValues.length() == 0)) return false;
    try {
      if (con == null) return false;
      Statement stmt = con.createStatement();
      stmt.executeUpdate("CREATE TABLE "+table+" ("+creationValues+")");
      stmt.close();
      if (noisy) Lib.TMessage("Created table "+table+".");
    }
    catch(SQLException e) {
      if (e.getErrorCode() == -1303) return false; // -1303 is "file already exists"
      Lib.TError("SQL Error in creating table "+table+"... "+e.getMessage());
      return false;
    }
    return true;
  }

  public static boolean deleteTable(Connection con, String table) {
    if ((table == null) || (table.length() == 0)) return false;
    try {
      if (con == null) return false;
      Statement stmt = con.createStatement();
      stmt.executeUpdate("DROP TABLE "+table);
      stmt.close();
      if (noisy) Lib.TMessage("Deleted table "+table+".");
    }
    catch(SQLException e) {                              
      if (e.getErrorCode() == 0) return false;
      Lib.TError("SQL Error in deleting table "+table+"... "+e.getMessage());
      return false;
    }
    return true;
  }
  
  // A pair of queries
  private static ResultSet result = null;
  private static Statement stmt = null;
  
  public static ResultSet doQuery(Connection con, String sql) throws SQLException, Exception {
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

  public static void killQuery() {
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
    
  public static int queryForNumber(Connection con, String sql, ResultSet rs) throws SQLException, Exception {
    if (con == null) return 0;
    if ((sql == null) || (sql.length() == 0)) return 0;
    rs = doQuery(con, sql);
    if (rs == null) return 0;
    try {
      ResultSetMetaData rsmd = rs.getMetaData();
      return rsmd.getColumnCount();
    }
    catch (SQLException e) {
      return 0;
    }
    catch(Exception e) {
      return 0;
    }
  }

  public static int queryForNumberOfRows(Connection con, String sql, ResultSet rs) throws SQLException, Exception {
	    if (con == null) return 0;
	    if ((sql == null) || (sql.length() == 0)) return 0;
	    rs = doQuery(con, sql);
	    if (rs == null) return 0;
	    try {
	      //rs.last();
		  rs.next();
	      System.out.println("got here");
	      return rs.getRow();
	    }
	    catch (SQLException e) {
	      throw e;
	    }
	    catch(Exception e) {
	      throw e;
	    }
	  }
  
  public static int doUpdate(Connection con, String sql) throws SQLException, Exception {
    if (con == null) return 0;
    if ((sql==null) || (sql.length()==0)) return 0;
    if ((result != null) || (stmt != null)) {
      //Lib.TError("Started a new query before killing the old one, killing it now.");
      killQuery();
    }
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

  public static int doUpdateNoCommit(Connection con, String sql) throws SQLException, Exception {
    if (con == null) return 0;
    if ((sql==null) || (sql.length()==0)) return 0;
    if ((result != null) || (stmt != null)) {
      //Lib.TError("Started a new query before killing the old one, killing it now.");
      killQuery();
    }
    int result = 0;
    Statement stmt=null;
    try {
      // Create a statement
      stmt = con.createStatement();
      // Execute the statement
      stmt.execute(sql);
      // warn if will be autocommited
      if (con.getAutoCommit() == true) {
        if (noisy) Lib.TMessage("No commit action overrode by AutoCommit with SQL statement "+sql+".");
      }
      // Show output if no updates made
      result = stmt.getUpdateCount();
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
  
  public static int doRollback(Connection con) throws SQLException, Exception {
    if (con == null) return 0;
    int result = 0;
    Statement stmt=null;
    try {
      if (con.getAutoCommit() == true) {
        if (noisy) Lib.TMessage("Rollback invalid with Autocommit on.");
      }
      con.rollback();
    }
    catch (SQLException e) {
      // Print the exception
      Lib.TError("SQL error with rollback.");
      throw e;
    }
    catch(Exception e) {
      Lib.TError("General error with rollback.");
      throw e;
    }
    return result;
  }

 /**
 /*
 /* getLiteralString - return a String value that is in single quotes if it
 /*   comes from a datatype that is a string, or not in quotes if it comes from
 /*   a datatype that is a number.
 /*
 /*/
  public static String getLiteralString(ResultSet rs, int column_number) throws SQLException, Exception {
    ResultSetMetaData rmd = rs.getMetaData();
    String db_specific_type = rmd.getColumnTypeName(column_number);
    int SQL_type = 0;
    if (db_specific_type.equalsIgnoreCase("INTEGER")) SQL_type = 1;
    else if (db_specific_type.equalsIgnoreCase("LONG")) SQL_type = 1;
    else if (db_specific_type.equalsIgnoreCase("INT")) SQL_type = 1;
    else if (db_specific_type.equalsIgnoreCase("VARCHAR")) SQL_type = 2;
    else if (db_specific_type.equalsIgnoreCase("CHAR")) SQL_type = 2;
    else if (db_specific_type.equalsIgnoreCase("TEXT")) SQL_type = 2;
    else {
      SQLException se = new SQLException("Can't determine datatype: "+db_specific_type+".");
      throw se;
    }
    String returnSQL = rs.getString(column_number);
    if (returnSQL==null) return "null";
    switch (SQL_type) {
      case 1: return returnSQL;
      case 2: return '\''+returnSQL+'\'';
      default: return '\''+returnSQL+'\'';
    }
  }

  public static String makeUpdateString(
                             String table, // SQL table
                             Object[][] primaryObjects, // primary keys
                             Object[][] updateObjects)  // update values
  {
    if ((table == null) || (table.length() == 0)) return null;
    if (primaryObjects.length == 0) return null;
    if (updateObjects.length == 0) return null;
    // get primary keys , name is 1rst element, value is 2nd
    StringBuffer whereSB = new StringBuffer(64);
    for (int i=0; i<primaryObjects.length; i++) {
      Object value_object = (Object) primaryObjects[i][1];
      String value = objectToSQLString(value_object);
      String column_name = (String) primaryObjects[i][0];
      if ((column_name == null) || (column_name.length() == 0)) {
         if (noisy) Lib.TMessage("Bad primary key column name passed to makeUpdateString");
         return null;
      }
      else if ((value == null) || (value.length() == 0)) {
         if (noisy) Lib.TMessage("Bad primary key column value passed to makeUpdateString");
         return null;
      }
      if (i>0) whereSB.append(" AND ");
      whereSB.append(column_name+"="+value);
    }
    // get update values, name is 1rst element, value is 2nd
    int commaKeeper=0;
    StringBuffer updateSB = new StringBuffer(64);
    for (int i=0; i<updateObjects.length; i++) {
      Object value_object = (Object) updateObjects[i][1];
      String value = objectToSQLString(value_object);
      String column_name = (String) updateObjects[i][0];
      if ((column_name == null) || (column_name.length() == 0)) {
         if (noisy) Lib.TMessage("Bad update column name passed to makeUpdateString, column with "+value+
                      " was not updated");
         continue;
      }
      else if ((value == null) || (value.length() == 0)) {
         if (noisy) Lib.TMessage("Bad update value passed to makeUpdateString, column "+column_name+
                      " was not updated");
         continue;
      }
      else commaKeeper++;
      if (commaKeeper>0) updateSB.append(", ");
      updateSB.append(column_name+"="+value);
    }
    String SQL_Update = "UPDATE "+table+
      " SET "+ updateSB.toString()+
      " WHERE "+whereSB.toString();
    return SQL_Update;
  }

  public static String makeInsertString(
                             String table, // SQL table
                             Object[][] insertObjects)  // insert name/values
  {
    if ((table == null) || (table.length() == 0)) return null;
    if (insertObjects.length == 0) return null;
    // Name is 1rst element, value is 2nd
    int commaKeeper=-1;
    StringBuffer nameSB = new StringBuffer(64);
    StringBuffer valueSB = new StringBuffer(64);
    for (int i=0; i<insertObjects.length; i++) {
      Object value_object = (Object) insertObjects[i][1];
      String value = objectToSQLString(value_object);
      String column_name = (String) insertObjects[i][0];
      if ((column_name == null) || (column_name.length() == 0)) {
         if (noisy) Lib.TMessage("Bad column passed to makeInsertString, value "+value);
         continue;
      }
      else if ((value == null) || (value.length() == 0)) {
         if (noisy) Lib.TMessage("Bad value passed to makeInsertString, column "+column_name);
         continue;
      }
      else commaKeeper++;
      if (commaKeeper>0) nameSB.append(", ");
      nameSB.append(column_name);
      if (commaKeeper>0) valueSB.append(", ");
      valueSB.append(value);
    }
    String SQL_Insert = "INSERT INTO "+table+
      " ("+ nameSB.toString()+ ")"+
      " VALUES ("+valueSB.toString()+")";
    return SQL_Insert;
  }

  public static String objectToSQLString(Object value_object) {
    if (value_object == null) return "null";
    try {
      String value = null;
      if (value_object instanceof String) {
        value = "\'" + Lib.makeDbQuotes((String) value_object) + "\'";
      }
      else if (value_object instanceof Integer) {
        value = ((Integer)value_object).toString();
      }
      else if (value_object instanceof Long) {
        value = ((Long)value_object).toString();
      }
      else if (value_object instanceof Short) {
        value = ((Short)value_object).toString();
      }
      else if (value_object instanceof Float) {
        value = ((Float)value_object).toString();
      }
      else if (value_object instanceof Double) {
        value = ((Double)value_object).toString();
      }
      else if (value_object instanceof Byte) {
        value = ((Byte)value_object).toString();
      }
      else if (value_object instanceof Date) {
        long milliseconds = ((Date)value_object).getTime();
        Timestamp ts = new Timestamp(milliseconds);
        value = ts.toString();
      }
      else if (value_object instanceof Boolean) {
        value = ((Boolean)value_object).toString().toUpperCase();
      }
      else {
        Exception e = new Exception("No matching datatype in IF statements");
      }
      return value;
    }
    catch(Exception e) {
      Lib.TError("Error while converting object to SQL string: " +e.getMessage());
      return null;
    }
  }
  
  public static String makeQueryString(
                             String table, // SQL table
                             Object[][] whereObjects, // "where" values
                             Object[][] queryObjects)  // lookup return columns
  {
    if ((table == null) || (table.length() == 0)) return null;
    if (whereObjects.length == 0) return null;
    if (queryObjects.length == 0) return null;
    // get primary keys , name is 1rst element, value is 2nd
    StringBuffer whereSB = new StringBuffer(64);
    for (int i=0; i<whereObjects.length; i++) {
      Object value_object = (Object) whereObjects[i][1];
      String value = objectToSQLString(value_object);
      String column_name = (String) whereObjects[i][0];
      if ((column_name == null) || (column_name.length() == 0)) {
         if (noisy) Lib.TMessage("Bad 'where' column name passed to makeQueryString");
         return null;
      }
      else if ((value == null) || (value.length() == 0)) {
         if (noisy) Lib.TMessage("Bad column value passed to makeQueryString");
         return null;
      }
      if (i>0) whereSB.append(" AND ");
      whereSB.append(column_name+"="+value);
    }
    // get update values, name is 1rst element, value is 2nd
    int commaKeeper=0;
    StringBuffer querySB = new StringBuffer(64);
    for (int i=0; i<queryObjects.length; i++) {
      String column_name = (String) queryObjects[i][0];
      if ((column_name == null) || (column_name.length() == 0)) {
         if (noisy) Lib.TMessage("Bad query column name passed to makeQueryString");
         continue;
      }
      else commaKeeper++;
      if (commaKeeper>0) querySB.append(", ");
      querySB.append(column_name);
    }
    String SQL_Query = "SELECT "+querySB.toString()+
      " FROM "+table+
      " WHERE "+whereSB.toString();
    return SQL_Query;
  }
  
  public static boolean processStrings(Connection con, String theStringOfCommands) {
    if ((theStringOfCommands == null) || (theStringOfCommands.length() == 0)) return false;
    Statement stmt=null;
    try {
      StringTokenizer st = new StringTokenizer(theStringOfCommands,"\r\n");
      while (st.hasMoreTokens()) {
        String sql = st.nextToken();
        try {
          stmt = con.createStatement();
          boolean result = stmt.execute(sql);
          if (result == true) throw new Exception(sql+" had results");
        }
        catch (SQLException e) {
          Lib.TError("SQL error '"+e.getMessage()+"' in processStrings: "+sql+".");
        }
        catch(Exception e) {
          Lib.TError("General error in processStrings: "+e.getMessage()+".");
        }
        finally {
          if (stmt != null) stmt.close();
        }
      }
    }
    catch (Exception e) {
      Lib.TError("General error in processStrings: "+e.getMessage()+".");
      return false;
    }
    return true;
  }
  
  //* 4-09-2000
  public static String makeInsertString(
    String sTable, // SQL table
    Hashtable oInsertTable)  
    throws Exception // insert name/values
  {
    if ((sTable == null) || (sTable.length() == 0)) return null;
    if (oInsertTable.size() == 0) return null;
    Object[][] oInsertArray = Lib.copyStringHashtableToArray(oInsertTable);
    String sInsert = makeInsertString(sTable,oInsertArray);
    return sInsert;
  }
  
  //* 5-07-2000
  public static Connection openODBCConnection(String server, String database) {
    return openODBCConnection(server, database, "", "");
  }
  
  //* 5-09-2000
  // this will fail when get locking problem, will need fix with DBCursor class
  public static boolean createAutonumberColumn(Connection con, String table_name, Connection update_con, String primaryKey, String numberedColumn) {
      if (con==null) return false;        
      if (update_con==null) return false;
      if ((table_name==null)||(table_name.length()==0)) return false;
      if ((primaryKey==null)||(primaryKey.length()==0)) return false;
      if ((numberedColumn==null)||(numberedColumn.length()==0)) return false;
      ResultSet oRs = null;
      try {
          Statement stmt = con.createStatement();
          //stmt.execute("SELECT "+old_pk_name+" FROM "+table_name);
          stmt.execute("SELECT "+primaryKey+" FROM "+table_name+" ORDER BY "+primaryKey+" DESC");
          oRs = stmt.getResultSet();               
          boolean more = oRs.next();
          long row_number = 0;
          // use the primary key to make autonumber column
          while (more) {
              row_number++;
              int numUpdated = doRSPointerUpdate( update_con, oRs, table_name, numberedColumn, Long.toString(row_number)); 
              if (numUpdated != 1) {
                  SQLException e = new SQLException("The original primary key column "+primaryKey+" has duplicates or is corrupted.");
                  throw e;
              }
              more = oRs.next();
              if (!more)
                  // Get More resultset if any is left
                  more = stmt.getMoreResults();
          }
      }
      catch(SQLException e) {
          Lib.TError("SQL Error in processing..." + e.getMessage());
          return false;
      }
      catch(Exception e) {
          Lib.TError("General Error in processing..." + e.getMessage());
          return false;
      }
      finally {
          try {
              if (oRs != null) oRs.close();
              if (stmt != null) stmt.close();
          }
          catch(SQLException e) {
            Lib.TError("SQL Error in processing..." + e.getMessage());
          }
      }
      return true;
  }

  // this routine assumes all columns in the rs are primary keys
  // rs is given to this routine pointing to current row which will be updated
  public static int doRSPointerUpdate( Connection con, ResultSet oRs, String tableName, String updateColumnName, String updateString)
      throws SQLException, Exception {
      if ( con==null ) return 0;
      if ( oRs == null ) return 0;
      if ((tableName==null)||(tableName.length()==0)) return 0;
      if ((updateColumnName==null)||(updateColumnName.length()==0)) return 0;
      if ( updateString==null ) return 0;
      // assume all columns are used in index
      ResultSetMetaData rmd = oRs.getMetaData();
      StringBuffer whereSB = new StringBuffer(64);
      int numColumns = rmd.getColumnCount();
      for (int i=1; i<=numColumns; i++) {
          if (i>1) whereSB.append(" AND ");
          String columnValue = oRs.getString(i);
          if (columnValue == null) {
              SQLException se = new SQLException("null value in primary key");
              throw se;
          }           
          whereSB.append(rmd.getColumnName(i)+"="+columnValue);
      }
      String SQL_TempUpdate = "UPDATE "+tableName+ 
          " SET "+ updateColumnName+"="+updateString+
          " WHERE "+whereSB.toString();
      return doUpdate( con, SQL_TempUpdate );
  }
    
  public static boolean createTSQLAutonumberColumn(Connection con, String table_name, String sNewAutonumberColumn)
    throws SQLException {
      if (con==null) return false;        
      if ((table_name==null)||(table_name.length()==0)) return false;
      if ((sNewAutonumberColumn==null)||(sNewAutonumberColumn.length()==0)) return false;
      String sAlterTableStatement = null;
      try {
          sAlterTableStatement = 
            "ALTER TABLE " + table_name + " WITH NOCHECK " +
            " ADD " + sNewAutonumberColumn + " INTEGER " +
            " IDENTITY (1,1) NOT FOR REPLICATION ";
          doUpdate(con, sAlterTableStatement);
      }
      catch(SQLException se) {
          String sqlState = se.getSQLState();
          System.out.println(sqlState);
          if (sqlState.equals("37000")) {
            Lib.TError("Identity column already existed.");
            throw se;
          }
          else {
            Lib.TError("SQL Error, SQL state: " + se.getSQLState() + 
              " Text: " + se.getMessage());
          }
          return false;
      }
      catch(Exception e) {
          Lib.TError("General Error in processing..." + e.getMessage());
          return false;
      }
      return true;
  }
  
  public static boolean dropColumn(Connection con, String table_name, String column_name) 
      throws SQLException, Exception {
      if (con==null) return false;        
      if ((table_name==null)||(table_name.length()==0)) return false;
      if ((column_name==null)||(column_name.length()==0)) return false;
      try {
          String sSQL_DropColumn = "ALTER TABLE "+table_name+" DROP COLUMN "+column_name;
          DBLib.doUpdate(con,sSQL_DropColumn);
      }
      catch (SQLException se) {
          String sqlState = se.getSQLState();
          System.out.println(sqlState);
          if (sqlState.equals("S0012")) {
            throw new Exception("Column did not exist.");
          }
          else {
            throw new Exception("SQL Error, SQL state: " + se.getSQLState() + 
              " Text: " + se.getMessage());
          }
      }
      return true;
  }

  public static String getAutonumberColumnName(Connection con, String tableName) {
      if ( con==null ) return "";
      if ((tableName==null)||(tableName.length()==0)) return "";
      ResultSet oRs;
      try {
        try {
          String sMetaQuery = "SELECT TOP 1 * FROM " + tableName;
          oRs = doQuery(con, sMetaQuery);
        }
        catch (SQLException sqle) {
          try {
            String sMetaQuery = "SELECT * FROM " + tableName;
            oRs = DBLib.doQuery(con, sMetaQuery);
          }
          catch (SQLException sqle2) {
            Lib.TError(sqle.getMessage() + " " + sqle2.getMessage());
            return "";
          }
        }
        ResultSetMetaData rmd = oRs.getMetaData();
        int numColumns = rmd.getColumnCount();
        for (int i=1; i<=numColumns; i++) {
          if (rmd.isAutoIncrement(i)) {
            return rmd.getColumnName(i);
          }
        }
      }
      catch (Exception e) {
        Lib.TError(e.getMessage());
        return "";
      }
      finally {
        DBLib.killQuery();
      }
      return "";
    }
    
    public static boolean makeIndex(Connection con, String table_name, String column_name, String index_name) 
      throws SQLException, Exception {
      try {
          //System.out.println("Making index of colummn "+column_name+"...");
          String SQL_makeIndex = null;
          if (msDBType.equals("Access")) {
              SQL_makeIndex = "CREATE INDEX "+index_name+" ON "+table_name+" ("+column_name+")";
          }
          else if (msDBType.equals("SQLServer")) {
              SQL_makeIndex = "CREATE INDEX "+index_name+" ON "+table_name+" ("+column_name+")";
          }
          else {
              SQL_makeIndex = "CREATE INDEX "+index_name+" ON "+table_name+" ("+column_name+")";
          }
          doUpdate(con,SQL_makeIndex);
          //System.out.println("Made.");
      }
      catch (SQLException se) {
          String sqlState = se.getSQLState();
          if (!sqlState.equals("S0011")) { throw se; }
          //System.out.println("Index already existed.");
      }
      return true;
    }

    public static boolean makeMyIndex(Connection con, String table_name, String column_name) 
      throws SQLException, Exception {
      try {
          //System.out.println("Making index of colummn "+column_name+"...");
          String SQL_makeIndex = null;
          if (msDBType.equals("Access")) {
              SQL_makeIndex = "CREATE INDEX "+column_name+"_INDEX ON "+table_name+" ("+column_name+")";
          }
          else if (msDBType.equals("SQLServer")) {
              SQL_makeIndex = "CREATE INDEX "+column_name+"_INDEX ON "+table_name+" ("+column_name+")";
          }
          else {
              SQL_makeIndex = "CREATE INDEX "+column_name+"_INDEX ON "+table_name+" ("+column_name+")";
          }
          doUpdate(con,SQL_makeIndex);
          //System.out.println("Made.");
      }
      catch (SQLException se) {
          String sqlState = se.getSQLState();
          if (!sqlState.equals("S0011")) { throw se; }
          //System.out.println("Index already existed.");
      }
      return true;
    }
    
    public static boolean dropIndex(Connection con, String table_name, String index_name) 
        throws SQLException, Exception {
        try {
            //System.out.println("Droping index of colummn "+index_name+"...");
            String SQL_DropIndex = null;
            if (msDBType.equals("Access")) {
                SQL_DropIndex = "DROP INDEX "+index_name+" ON "+table_name;
            }
            else if (msDBType.equals("SQLServer")) {
                SQL_DropIndex = "DROP INDEX "+table_name+"."+index_name;
            }
            else {
                SQL_DropIndex = "DROP INDEX "+table_name+"."+index_name;
            }
            doUpdate(con,SQL_DropIndex);
            //System.out.println("Dropped.");
        }
        catch (SQLException se) {
            String sqlState = se.getSQLState();
            if (!sqlState.equals("S0012")) { throw se; }
            //System.out.println("Index didn't exist.");
        }
        return true;
    }
    
    public static boolean dropMyIndex(Connection con, String table_name, String column_name) 
        throws SQLException, Exception {
        try {
            //System.out.println("Droping index of colummn "+index_name+"...");
            String SQL_DropIndex = null;
            if (msDBType.equals("Access")) {
                SQL_DropIndex = "DROP INDEX "+column_name+"_INDEX ON "+table_name;
            }
            else if (msDBType.equals("SQLServer")) {
                SQL_DropIndex = "DROP INDEX "+table_name+"."+column_name+"_INDEX";
            }
            else {
                SQL_DropIndex = "DROP INDEX "+table_name+"."+column_name+"_INDEX";
            }
            doUpdate(con,SQL_DropIndex);
            //System.out.println("Dropped.");
        }
        catch (SQLException se) {
            String sqlState = se.getSQLState();
            if (!sqlState.equals("S0012")) { throw se; }
            //System.out.println("Index didn't exist.");
        }
        return true;
    }
    public static String getResultSetAsi2b2XML(String sSQL){
    	String sI2b2XML = "";
    	ResultSet oRs = null;
    	try {
    		Connection oConnection = null;
    		oConnection = openODBCConnection("sample_i2b2_db");
    		int iNumberOfRecords = queryForNumberOfRows(oConnection, sSQL, oRs);
    		System.out.println(iNumberOfRecords);
    	}
    	catch (Exception e) {
  	      System.out.println(e.getMessage());    		
    	}
    	return sI2b2XML;
    }
	public static void main(String[] args)
	{
		getResultSetAsi2b2XML("select * from sample");
	}
}
