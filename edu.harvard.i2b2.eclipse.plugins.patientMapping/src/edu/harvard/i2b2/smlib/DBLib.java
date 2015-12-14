/*
* Copyright (c) 2006-2012 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors: 
 *   	Shawn Murphy
 *     
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

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
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
import java.text.SimpleDateFormat;

import org.jdom.*;
import org.jdom.contrib.input.ResultSetBuilder;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.patientMapping.data.PatientDemographics;

public class DBLib {
	
	private static String msDBType = "SQLServer";
	public static boolean noisy=true;
	/**
	 * This class shouldn't be instantiated.
	 */
	private DBLib() {}
	/**
	 /*  openODBCConnection - makes the ODBC connection, returns null if it fails
	  /*/
	public static Connection openODBCConnection(String ODBC_source, String user, String pwd) {
		
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
	
	public static Connection openODBCConnection(String ODBC_source) {
		return openODBCConnection(ODBC_source, "admin", "");
	}
	
	public static Connection openODBCConnection(String server, String database, String user, String pwd) {
		//String sun_drv = "sun.jdbc.odbc.JdbcOdbcDriver";
		String ms_drv = "com.ms.jdbc.odbc.JdbcOdbcDriver";
		String drv = "sun.jdbc.odbc.JdbcOdbcDriver";
		
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
	
	public static Connection openJDBCConnection(String url, String driver, String user, String pwd) {
		if (url == null) {
			Lib.TError("No url name was passed as required");
			return null;
		}
		if (driver == null) {
			Lib.TError("No driver name was passed as required");
			return null;
		}
		if (user == null) user = "";
		if (pwd == null) pwd = "";
		/* MICROSOFT
		 After registering the driver, you must pass your database connection 
		 information in the form of a connection URL. The following is a 
		 template URL for the SQL Server 2000 Driver for JDBC. Substitute 
		 the values specific to your database. (For instructions on connecting 
		 to named instances, see "Connecting to Named Instances" in the 
		 SQL Server 2000 Driver for JDBC topic.) 
		 
		 jdbc:microsoft:sqlserver://server_name:1433  
		 
		 For example, to specify a connection URL that includes the user 
		 ID "username" and the password "secret": 
		 
		 Connection conn = DriverManager.getConnection 
		 ("jdbc:microsoft:sqlserver://server1:1433","username","secret");  
		 
		 NOTES: 
		 
		 The server_name is an IP address or a host name, assuming that your 
		 network resolves host names to IP addresses. You can test this by 
		 using the ping command to access the host name and verifying that 
		 you receive a reply with the correct IP address. 
		 
		 The numeric value after the server name is the port number on which 
		 the database is listening. The values listed here are sample 
		 defaults. You should determine the port number that your database 
		 is using and substitute that value. 
		 */
		/*
		 Specifying a Database URL, User Name, and Password 
		 The following signature takes the URL, user name, and password as separate parameters: 
		 
		 getConnection(String URL, String user, String password); 
		 
		 Where the URL is of the form:
		 jdbc:oracle:<drivertype>:@<database> 
		 
		 The following example connects user scott with password tiger to a database with service orcl (Important: see more on services) through port 1521 of host myhost, using the Thin driver. 
		 
		 Connection conn = DriverManager.getConnection
		 ("jdbc:oracle:thin:@//myhost:1521/orcl", "scott", "tiger"); 
		 
		 */
		
		try {
			Class.forName(driver);
			if (noisy) Lib.TMessage("Driver "+driver+" was loaded.");
			// Try to connect to the specified database
			if (noisy) Lib.TMessage("Connecting to database ...");
			if (noisy) Lib.TMessage("URL: "+url);
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
	
	public static Connection openSqlServerJDBCConnection(String server, String database, String user, String pwd) {
		//String oracle_drv = "oracle.jdbc.driver.OracleDriver";
		String microsoft_drv = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		String drv = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		String url = null;
		if (server == null) {
			Lib.TError("No server name was passed as required");
			return null;
		}
		if (database == null) {
			Lib.TError("No database name was passed as required");
			return null;
		}
		if (user == null) user = "";
		if (pwd == null) pwd = "";
		/* MICROSOFT
		 After registering the driver, you must pass your database connection 
		 information in the form of a connection URL. The following is a 
		 template URL for the SQL Server 2000 Driver for JDBC. Substitute 
		 the values specific to your database. (For instructions on connecting 
		 to named instances, see "Connecting to Named Instances" in the 
		 SQL Server 2000 Driver for JDBC topic.) 
		 
		 jdbc:microsoft:sqlserver://server_name:1433  
		 
		 For example, to specify a connection URL that includes the user 
		 ID "username" and the password "secret": 
		 
		 Connection conn = DriverManager.getConnection 
		 ("jdbc:microsoft:sqlserver://server1:1433","username","secret");  
		 
		 NOTES: 
		 
		 The server_name is an IP address or a host name, assuming that your 
		 network resolves host names to IP addresses. You can test this by 
		 using the ping command to access the host name and verifying that 
		 you receive a reply with the correct IP address. 
		 
		 The numeric value after the server name is the port number on which 
		 the database is listening. The values listed here are sample 
		 defaults. You should determine the port number that your database 
		 is using and substitute that value. 
		 */
		/*
		 Specifying a Database URL, User Name, and Password 
		 The following signature takes the URL, user name, and password as separate parameters: 
		 
		 getConnection(String URL, String user, String password); 
		 
		 Where the URL is of the form:
		 jdbc:oracle:<drivertype>:@<database> 
		 
		 The following example connects user scott with password tiger to a database with service orcl (Important: see more on services) through port 1521 of host myhost, using the Thin driver. 
		 
		 Connection conn = DriverManager.getConnection
		 ("jdbc:oracle:thin:@//myhost:1521/orcl", "scott", "tiger"); 
		 
		 */
		//url = "jdbc:oracle:thin:@//" + server + ":1521/orcl";		
		//url = "jdbc:oracle:thin:@//" + server + ":1521:" + database;		
		url = "jdbc:microsoft:sqlserver://" + server + ":1433;DatabaseName=" + database + ";User=" + user + ";Password=" + pwd;		
		//url = "jdbc:microsoft:sqlserver://" + server + ":1433";		
		try {
			// Load Database Bridge or Driver
			if (noisy) Lib.TMessage("Loading driver ...");
			if (System.getProperty("java.vendor").equals("Microsoft Corp.")) {
				drv = microsoft_drv; // Visual J++
			}
			else {
				// sun_drv is currently the default
			}
			Class.forName(drv);
			if (noisy) Lib.TMessage("Driver "+drv+" was loaded.");
			// Try to connect to the specified database
			if (noisy) Lib.TMessage("Connecting to database ...");
			if (noisy) Lib.TMessage("URL: "+url);
			//Connection con = DriverManager.getConnection(url, user, pwd);
			Connection con = DriverManager.getConnection(url);
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
	 /*  closeConnection - closes a connection, returns false if it fails
	  /*/
	public static boolean closeConnection(Connection con) {
		try {
			if (con != null) con.close();
		}
		catch(SQLException e) {
			Lib.TError("SQL Error in closing... "+e.getMessage());
			return false;
		}
		return true;
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
		if (con == null) {
			return null;
		}
		if ((sql==null) || (sql.length()==0)) {
			return null;
		}
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
			if (result != null) {
				result.close();
			}
			result = null;
			if (stmt != null) {
				stmt.close();
			}
			stmt = null;
			throw e;
		}
		catch(Exception e) {
			Lib.TError("General error with SQL statement: "+sql+".");
			if (result != null) {
				result.close();
			}
			result = null;
			if (stmt != null) {
				stmt.close();
			}
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
	
	public static String geti2b2String(ResultSet rs, int column_number) throws SQLException, Exception {
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
				//Exception e = new Exception("No matching datatype in IF statements");
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
		Connection oConnection = null;
		oConnection = openODBCConnection("sample_i2b2_db");
		try {
			oRs = doQuery(oConnection, sSQL);
			sI2b2XML = "<PatientData>\r\n";
			while (oRs.next()) {
				sI2b2XML = sI2b2XML + "  <observation_fact>\r\n";
				sI2b2XML = sI2b2XML + "    <patient_num>" + 
				oRs.getString(2) + "</patient_num>\r\n";
				sI2b2XML = sI2b2XML + "    <concept_cd>" + 
				oRs.getString(3) + "</concept_cd>\r\n";
				sI2b2XML = sI2b2XML + "    <start_date>" + 
				oRs.getString(4) + "</start_date>\r\n";
				sI2b2XML = sI2b2XML + "    <end_date>" + 
				oRs.getString(5) + "</end_date>\r\n";
				sI2b2XML = sI2b2XML + "    <location_cd>" + 
				oRs.getString(7) + "</location_cd>\r\n";
				sI2b2XML = sI2b2XML + "  </observation_fact>\r\n";
			}
			sI2b2XML = sI2b2XML + "</PatientData>\r\n";
			String i2b2File = System.getProperty("user.dir")+'/'+ "i2b2xml.txt";
			System.out.println(i2b2File);
			File f = new File(i2b2File);
			Lib.write(f,sI2b2XML);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());    		
		}
		closeODBCConnection(oConnection);
		
		//System.out.println(sI2b2XML);
		System.out.println("done");
		return sI2b2XML;
	}
	// only all caps seems to work, things get turned into 
	// all caps internally in the resultset importer
	private static String ss_patient_num = "PATIENT_NUM";
	private static String ss_concept_cd = "CONCEPT_CD";
	private static String ss_q_name_char = "Q_NAME_CHAR";
	private static String ss_start_date = "START_DATE";
	private static String ss_end_date = "END_DATE";
	private static String ss_inout_cd = "INOUT_CD";
	private static String ss_color_cd = "CONCEPT_COLOR";
	private static String ss_height_cd = "CONCEPT_HEIGHT";
	private static String ss_value_cd = "CONCEPT_VALUE";
	private static String ss_table_name = "TABLE_NAME";
	
	public static String newline = System.getProperty("line.separator");
	public static boolean bUseConcept = false;
	
	
	private static String[] getConceptOrder(String i2b2Xml)
	{
		try
		{
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(i2b2Xml);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();
			List conceptChildren = tableXml.getChildren();
			java.util.ArrayList conceptList = new java.util.ArrayList();
			for (Iterator itr=conceptChildren.iterator(); itr.hasNext(); )
			{
				Element queryEntryXml = (org.jdom.Element) itr.next();
				Element conceptXml = (Element) queryEntryXml.getChild("Concept");
				Element conTableXml = (Element) conceptXml.getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("c_name");
				String sConceptName = nameXml.getText();
				if (sConceptName!=null)
					conceptList.add(sConceptName);
			}
			
			return (String[]) conceptList.toArray(new String[conceptList.size()]);
			//return (String[]) conceptList.toArray();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
		
	}
	
	private static String buildQueryFromI2B2Xml(String i2b2Xml, int minPatientNum, int maxPatientNum, String schemaName, ArrayList concepts){
		try{
			
			StringBuilder sSql = new StringBuilder(1000);
			StringBuilder sSql_Visit = new StringBuilder(1000);
			StringBuilder sConceptSql = new StringBuilder(1000);
			StringBuilder sConstraintSql = new StringBuilder(1000);
			StringBuilder sVisitSql = new StringBuilder(1000);
			
			//String sPatientNumMin = "52800";
			//String sPatientNumMax = "52850";
			String sPatientNumMin = minPatientNum + "";
			String sPatientNumMax = maxPatientNum + "";
			boolean useConstraints = false;
			
			if ((schemaName==null)||(schemaName.trim().length()==0))
				schemaName = "";
			else
				schemaName = schemaName + ".";
			
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(i2b2Xml);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();
			List conceptChildren = tableXml.getChildren();
			String encounterSql = "";
			String deathSql = "";
			//String visitSql = "";
			
			int i =0;
			//first, build query as structure we can deal with
			Hashtable queryValues = new Hashtable();
			Hashtable conceptQueryValues = new Hashtable();
			Hashtable visitQueryValues = new Hashtable();
			ArrayList validRows = new ArrayList();
			ArrayList validConceptRows = new ArrayList();
			ArrayList validVisitRows = new ArrayList();
			
			for (Iterator itr=conceptChildren.iterator(); itr.hasNext(); )
			{
				QueryEntry newEntry = new QueryEntry();
				newEntry.EntryId = i + "";
				Element queryEntryXml = (org.jdom.Element) itr.next();
				Element conceptXml = (Element) queryEntryXml.getChild("Concept");
				Element conTableXml = (Element) conceptXml.getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("c_name");
				newEntry.Name = nameXml.getText();
				if (newEntry.Name != null) {
					newEntry.Name = newEntry.Name.replace("'", "''");
				}
				if (!((newEntry.Name.equals("Encounter Range Line"))||(newEntry.Name.equals("Vital Status Line"))))
				{
					org.jdom.Element pathXml = conTableXml.getChild("c_dimcode");
					if (System.getProperty("applicationName").equals("BIRN"))
					{
						newEntry.EntryValue = pathXml.getText().replace("\\", "\\\\");
					}
					else {
						newEntry.EntryValue = pathXml.getText();
					}
					//wp, column names stuff
					Element factTableColumnXml = (Element) conTableXml.getChild("c_facttablecolumn");				
					newEntry.factTableColumnName = factTableColumnXml.getText().trim();		
					
					Element tableNameXml = (Element) conTableXml.getChild("c_tablename");				
					newEntry.tableName = tableNameXml.getText().trim();
					
					Element columnNameXml = (Element) conTableXml.getChild("c_columnname");				
					newEntry.columnName = columnNameXml.getText().trim();
					
					Element operatorXml = (Element) conTableXml.getChild("c_operator");				
					newEntry.operator = operatorXml.getText().trim();
				}
				else {
					newEntry.factTableColumnName = "concept_cd";						
					newEntry.tableName = "concept_dimension";			
					newEntry.columnName = "concept_path";
					newEntry.operator = "like";
				}
				
				Element colorXml = (Element) queryEntryXml.getChild("ConceptColor");				
				newEntry.Color = colorXml.getText().trim();
				
				///wp
				Element heightXml = (Element) queryEntryXml.getChild("Height");				
				newEntry.Height = heightXml.getText().trim();
				
				Element displaynameXml = (Element) queryEntryXml.getChild("DisplayName");				
				newEntry.displayName = displaynameXml.getText(); //.trim();
				if(newEntry.displayName != null) {
					newEntry.displayName = newEntry.displayName.trim().replaceAll("'", "''");
					newEntry.displayName = newEntry.displayName.replaceAll(",", "-");
				}
				
				Element valueXml = (Element) queryEntryXml.getChild("ModuleValue");		
				String mValue = valueXml.getText().trim();
				newEntry.Constraint = mValue;
				Element rowXml = (Element) queryEntryXml.getChild("RowNumber");				
				newEntry.RowNumber = rowXml.getText().trim();
				Integer rowNum = new Integer(newEntry.RowNumber);
				
				QueryRow rowValue = (QueryRow) queryValues.get(newEntry.RowNumber);
				if (rowValue!=null)
				{
					QueryConcept qConcept = (QueryConcept) rowValue.QueryConcepts.get(newEntry.Name);
					if (qConcept!= null) {
						qConcept.EntryList.add(newEntry);
					}
					else
					{
						qConcept = new QueryConcept();
						qConcept.ConceptName = newEntry.Name;
						qConcept.EntryList.add(newEntry);
						rowValue.RowName += "-" + newEntry.displayName; //newEntry.Name;
						rowValue.QueryConcepts.put(newEntry.Name, qConcept);
					}
					queryValues.put(newEntry.RowNumber, rowValue);
				}
				else{
					validRows.add(rowNum);
					QueryConcept qConcept = new QueryConcept();
					qConcept.ConceptName = newEntry.Name;
					qConcept.EntryList.add(newEntry);
					rowValue = new QueryRow();
					rowValue.RowName = newEntry.displayName; //newEntry.Name;
					rowValue.QueryConcepts.put(newEntry.Name, qConcept);
					queryValues.put(newEntry.RowNumber, rowValue);					
				}
				i++;
			}
			
			Collections.sort(validRows);
			for (int row=0; row<validRows.size(); row++)
			{
				QueryRow qRow = (QueryRow) queryValues.get(((Integer) validRows.get(row)).toString());
				Enumeration rowEnum = qRow.QueryConcepts.elements();
				QueryConcept qConcept = (QueryConcept) rowEnum.nextElement();
				QueryEntry qEntry = (QueryEntry) qConcept.EntryList.get(0);
				if(qEntry.tableName.equalsIgnoreCase("visit_dimension")) {
					visitQueryValues.put(qEntry.RowNumber, qRow);
					validVisitRows.add(new Integer(qEntry.RowNumber));
				}
				else {
					conceptQueryValues.put(qEntry.RowNumber, qRow);
					validConceptRows.add(new Integer(qEntry.RowNumber));
				}
			}
			
			String factcolumn = null;
			String tablename = null;
			Collections.sort(validConceptRows);
			Collections.sort(validVisitRows);
			for (int row=0; row<validConceptRows.size(); row++) {
				QueryRow qRow = (QueryRow) conceptQueryValues.get(((Integer) validConceptRows.get(row)).toString());
				concepts.add(new String(qRow.RowName));
				
				Enumeration rowEnum = qRow.QueryConcepts.elements();
				while (rowEnum.hasMoreElements())
				{
					QueryConcept qConcept = (QueryConcept) rowEnum.nextElement();
					
					for (int conNum = 0; conNum<qConcept.EntryList.size(); conNum++)
					{
						QueryEntry qEntry = (QueryEntry) qConcept.EntryList.get(conNum);
						factcolumn = qEntry.factTableColumnName; //assume only one type of column in each row
						tablename = qEntry.tableName;
						
						if (qEntry.Name.equals("Encounter Range Line"))
						{
							encounterSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Encounter_range' " + ss_q_name_char + ", \r\n" +
							"	'Encounter_range' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n "  +
							"	min(start_date) START_DATE, \r\n" +
							"	max(start_date) END_DATE, \r\n" +
							//"	'E' INOUT_CD, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o \r\n" +
							"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + " \r\n" +
							"group by PATIENT_NUM ";
						}
						else if (qEntry.Name.equals("Vital Status Line"))
						{
							deathSql = "select p.patient_num PATIENT_NUM, \r\n" +
							"	'Death' " + ss_q_name_char + ", \r\n" +
							"	'Death' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n " +
							"	p.death_date START_DATE, \r\n" +
							"	p.death_date END_DATE, \r\n" +
							//"	'D' INOUT_CD, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "patient_dimension p \r\n" +
							"where p.patient_num  between " + sPatientNumMin + " and " + sPatientNumMax + " \r\n";
							
						}
						else
						{								
							if ((qEntry.Constraint!=null)&&(qEntry.Constraint.trim().length()>0)&&(!qEntry.Constraint.toUpperCase().equals("N/AN/A")))
							{
								if (sConstraintSql.toString().trim().length()>0) {
									sConstraintSql.append("\r\nunion all\r\n");		
								}
								sConstraintSql.append(
										buildQueryConstraint(qEntry.Constraint, schemaName, 
												qRow.RowName, qEntry.RowNumber, qEntry.Color, 
												qEntry.Height, qEntry.EntryValue, sPatientNumMin,
												sPatientNumMax, qEntry.factTableColumnName,
												qEntry.tableName, qEntry.columnName));
							}
							else
							{
								if (sConceptSql.toString().trim().length()>0) {
									sConceptSql.append("\r\nunion all\r\n");
								}
								if(qEntry.operator.equalsIgnoreCase("like")) {
									if (System.getProperty("applicationName").equals("BIRN"))
									{
										sConceptSql.append("select " +
												"	'" + qRow.RowName + "' as " + ss_q_name_char + ", \r\n" +
												"	concept_cd as CONCEPT_CD, \r\n" +
												"	" + qEntry.RowNumber + " as AN_ORDER, \r\n" +
												"	'" + qEntry.Color + "' as CONCEPT_COLOR, \r\n" +
												"	'" + qEntry.Height + "' as  CONCEPT_HEIGHT \r\n" +
												"from " + schemaName + "concept_dimension \r\n" +
												"where concept_path like '" + qEntry.EntryValue + "\\\\%' ESCAPE '|' \r\n");
									}
									else {
										sConceptSql.append("select " +
												"	'" + qRow.RowName + "' as " + ss_q_name_char + ", \r\n" +
												/*"	concept_cd CONCEPT_CD*/
												" "+qEntry.factTableColumnName+" as CONCEPT_CD, \r\n" +
												"	" + qEntry.RowNumber + " as AN_ORDER, \r\n" +
												"	'" + qEntry.Color + "' as CONCEPT_COLOR, \r\n" +
												"	'" + qEntry.Height + "' as CONCEPT_HEIGHT \r\n" +
												"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
												"where "+qEntry.columnName/*concept_path*/+" like '" 
												+ qEntry.EntryValue + "\\%' \r\n");
									}
								}
								else if(qEntry.operator.equalsIgnoreCase("=")) {
									sConceptSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" as CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " as AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' as CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' as CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" = '" 
											+ qEntry.EntryValue + "' \r\n");
								}
							}
							
						}
					}
				}
			}
			
			if (sConceptSql.toString().trim().length()>0)
			{
				//sSql.append("select /*+ index (v VISIT_DIM_PK) */\r\n " + 
				/*	"	o.patient_num PATIENT_NUM, \r\n" +
				 "	c." + ss_q_name_char + ",\r\n " +
				 "	c.CONCEPT_CD,\r\n " +
				 "	o.start_date START_DATE,\r\n " +
				 "	o.end_date END_DATE,\r\n " +
				 "	c.AN_ORDER,\r\n " +
				 "	c.CONCEPT_COLOR,\r\n " +
				 "	c.CONCEPT_HEIGHT,\r\n " +
				 "	'' CONCEPT_VALUE \r\n" +
				 "from " + schemaName + "observation_fact o, " + schemaName + "visit_dimension v, \r\n (" +
				 sConceptSql.toString() + ") c\r\n " +
				 "where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
				 "and o.encounter_num = v.encounter_num\r\n " +
				 "and o.patient_num = v.patient_num\r\n  " +
				 "and o.concept_cd = c.concept_cd ");*/
				
				
				if (System.getProperty("applicationName").equals("BIRN"))
				{
					sSql.append("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
							//"	o.person_ide as PATIENT_NUM, \r\n" +
							"	o.event_ide as PATIENT_NUM, \r\n" +
							"	c." + ss_q_name_char + ",\r\n " +
							"	c.CONCEPT_CD,\r\n " +
							"	TO_CHAR (o.start_date, 'dd-Mon-yyyy hh:mi:ss PM') as START_DATE,\r\n " +
							"	o.end_date as END_DATE,\r\n " +
							"	c.AN_ORDER,\r\n " +
							"	c.CONCEPT_COLOR,\r\n " +
							"	c.CONCEPT_HEIGHT,\r\n " +
							"	'' as CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o, \r\n (" +
							sConceptSql.toString() + ") c\r\n " +
							"where o.person_ide between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
					"and o.concept_cd = c.concept_cd ");
				} else {
					sSql.append("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
							"	o.patient_num PATIENT_NUM, \r\n" +
							"	c." + ss_q_name_char + ",\r\n " +
							"	o.CONCEPT_CD,\r\n " +
							"	o.ENCOUNTER_NUM,\r\n " +
							"	o.start_date START_DATE,\r\n " +
							"	o.end_date END_DATE,\r\n " +
							"	c.AN_ORDER,\r\n " +
							"	c.CONCEPT_COLOR,\r\n " +
							"	c.CONCEPT_HEIGHT,\r\n " +
							"'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o, \r\n (" +
							sConceptSql.toString() + ") c\r\n " +
							"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
							"and o."+factcolumn/*concept_cd*/+" = c.concept_cd ");
				}
			}
			
			for (int row=0; row<validVisitRows.size(); row++) {
				QueryRow qRow = (QueryRow) visitQueryValues.get(((Integer) validVisitRows.get(row)).toString());
				concepts.add(new String(qRow.RowName));
				
				Enumeration rowEnum = qRow.QueryConcepts.elements();
				while (rowEnum.hasMoreElements())
				{
					QueryConcept qConcept = (QueryConcept) rowEnum.nextElement();
					
					for (int conNum = 0; conNum<qConcept.EntryList.size(); conNum++)
					{
						QueryEntry qEntry = (QueryEntry) qConcept.EntryList.get(conNum);
						factcolumn = qEntry.factTableColumnName; //assume only one type of column in each row
						tablename = qEntry.tableName;
						
						if (qEntry.Name.equals("Encounter Range Line"))
						{
							encounterSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Encounter_range' " + ss_q_name_char + ", \r\n" +
							"	'Encounter_range' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n " +
							"	min(start_date) START_DATE, \r\n" +
							"	max(start_date) END_DATE, \r\n" +
							//"	'E' INOUT_CD, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o \r\n" +
							"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + " \r\n" +
							"group by PATIENT_NUM ";
						}
						else if (qEntry.Name.equals("Vital Status Line"))
						{
							deathSql = "select p.patient_num PATIENT_NUM, \r\n" +
							"	'Death' " + ss_q_name_char + ", \r\n" +
							"	'Death' CONCEPT_CD, \r\n" +
							"	 0 ENCOUNTER_NUM,\r\n " +
							"	p.death_date START_DATE, \r\n" +
							"	p.death_date END_DATE, \r\n" +
							//"	'D' INOUT_CD, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "patient_dimension p \r\n" +
							"where p.patient_num  between " + sPatientNumMin + " and " + sPatientNumMax + " \r\n";
							
						}
						else
						{								
							if ((qEntry.Constraint!=null)&&(qEntry.Constraint.trim().length()>0)&&(!qEntry.Constraint.toUpperCase().equals("N/AN/A")))
							{
								if (sConstraintSql.toString().trim().length()>0) {
									sConstraintSql.append("\r\nunion all\r\n");		
								}
								sConstraintSql.append(
										buildQueryConstraint(qEntry.Constraint, schemaName, 
												qRow.RowName, qEntry.RowNumber, qEntry.Color, 
												qEntry.Height, qEntry.EntryValue, sPatientNumMin,
												sPatientNumMax, qEntry.factTableColumnName,
												qEntry.tableName, qEntry.columnName));
							}
							else
							{
								if (sVisitSql.toString().trim().length()>0) {
									sVisitSql.append("\r\nunion all\r\n");
								}
								if(qEntry.operator.equalsIgnoreCase("like")) {
									if (System.getProperty("applicationName").equals("BIRN"))
									{
										sVisitSql.append("select " +
												"	'" + qRow.RowName + "' as " + ss_q_name_char + ", \r\n" +
												"	concept_cd as CONCEPT_CD, \r\n" +
												"	" + qEntry.RowNumber + " as AN_ORDER, \r\n" +
												"	'" + qEntry.Color + "' as CONCEPT_COLOR, \r\n" +
												"	'" + qEntry.Height + "' as  CONCEPT_HEIGHT \r\n" +
												"from " + schemaName + "concept_dimension \r\n" +
												"where concept_path like '" + qEntry.EntryValue + "\\\\%' ESCAPE '|' \r\n");
									}
									else {
										sVisitSql.append("select " +
												"	'" + qRow.RowName + "' as " + ss_q_name_char + ", \r\n" +
												/*"	concept_cd CONCEPT_CD*/
												" "+qEntry.factTableColumnName+" as CONCEPT_CD, \r\n" +
												"	" + qEntry.RowNumber + " as AN_ORDER, \r\n" +
												"	'" + qEntry.Color + "' as CONCEPT_COLOR, \r\n" +
												"	'" + qEntry.Height + "' as CONCEPT_HEIGHT \r\n" +
												"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
												"where "+qEntry.columnName/*concept_path*/+" like '" 
												+ qEntry.EntryValue + "\\%' \r\n");
									}
								}
								else if(qEntry.operator.equalsIgnoreCase("=")) {
									sVisitSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" as CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " as AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' as CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' as CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" = '" 
											+ qEntry.EntryValue + "' \r\n");
								}
							}
							
						}
					}
				}
			}
			
			if (sVisitSql.toString().trim().length()>0)
			{
				//sSql.append("select /*+ index (v VISIT_DIM_PK) */\r\n " + 
				/*	"	o.patient_num PATIENT_NUM, \r\n" +
				 "	c." + ss_q_name_char + ",\r\n " +
				 "	c.CONCEPT_CD,\r\n " +
				 "	o.start_date START_DATE,\r\n " +
				 "	o.end_date END_DATE,\r\n " +
				 "	c.AN_ORDER,\r\n " +
				 "	c.CONCEPT_COLOR,\r\n " +
				 "	c.CONCEPT_HEIGHT,\r\n " +
				 "	'' CONCEPT_VALUE \r\n" +
				 "from " + schemaName + "observation_fact o, " + schemaName + "visit_dimension v, \r\n (" +
				 sConceptSql.toString() + ") c\r\n " +
				 "where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
				 "and o.encounter_num = v.encounter_num\r\n " +
				 "and o.patient_num = v.patient_num\r\n  " +
				 "and o.concept_cd = c.concept_cd ");*/	
				
				if (System.getProperty("applicationName").equals("BIRN"))
				{
					sSql_Visit.append("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
							//"	o.person_ide as PATIENT_NUM, \r\n" +
							"	o.event_ide as PATIENT_NUM, \r\n" +
							"	c." + ss_q_name_char + ",\r\n " +
							"	c.CONCEPT_CD,\r\n " +
							"	TO_CHAR (o.start_date, 'dd-Mon-yyyy hh:mi:ss PM') as START_DATE,\r\n " +
							"	o.end_date as END_DATE,\r\n " +
							"	c.AN_ORDER,\r\n " +
							"	c.CONCEPT_COLOR,\r\n " +
							"	c.CONCEPT_HEIGHT,\r\n " +
							"	'' as CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o, \r\n (" +
							sVisitSql.toString() + ") c\r\n " +
							"where o.person_ide between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
					"and o.concept_cd = c.concept_cd ");
				} else {
					sSql_Visit.append("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
							"	o.patient_num PATIENT_NUM, \r\n" +
							"	c." + ss_q_name_char + ",\r\n " +
							"	o.CONCEPT_CD,\r\n " +
							"	o.ENCOUNTER_NUM,\r\n " +
							"	o.start_date START_DATE,\r\n " +
							"	o.end_date END_DATE,\r\n " +
							"	c.AN_ORDER,\r\n " +
							"	c.CONCEPT_COLOR,\r\n " +
							"	c.CONCEPT_HEIGHT,\r\n " +
							"'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o, \r\n (" +
							sVisitSql.toString() + ") c\r\n " +
							"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
							"and o."+factcolumn/*concept_cd*/+" = c.concept_cd ");
				}
			}
			
			if ((sSql.toString().trim().length()>0)&&(sConstraintSql.toString().trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(sConstraintSql.toString());
			
			if ((sSql.toString().trim().length()>0)&&(encounterSql.trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(encounterSql);
			
			if ((sSql.toString().trim().length()>0)&&(deathSql.trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(deathSql);
			
			if ((sSql.toString().trim().length()>0)&&(sSql_Visit.toString().trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(sSql_Visit);
			
			if (bUseConcept)
				sSql.append("order by 1, 7, 4, 5, 6 desc");
			else
				sSql.append("order by 1, 7, 5, 6 desc");
			
			return sSql.toString();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private static String buildQueryFromI2B2Xml(String i2b2Xml, int[] patientNums, String schemaName, ArrayList concepts){
		try{
			
			StringBuilder sSql = new StringBuilder(1000);
			StringBuilder sSql_Visit = new StringBuilder(1000);
			StringBuilder sConceptSql = new StringBuilder(1000);
			StringBuilder sConstraintSql = new StringBuilder(1000);
			StringBuilder sVisitSql = new StringBuilder(1000);
			
			StringBuffer patientWhereClause = new StringBuffer(" (o.patient_num = ");
			for(int i=0; i<patientNums.length; i++) {
				if(i>0) {
					patientWhereClause.append(" or o.patient_num = ");
				}
				patientWhereClause.append(patientNums[i]);
			}
			patientWhereClause.append(")");
			
			//boolean useConstraints = false;
			
			if ((schemaName==null)||(schemaName.trim().length()==0))
				schemaName = "";
			else
				schemaName = schemaName + ".";
			
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(i2b2Xml);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();
			List conceptChildren = tableXml.getChildren();
			String encounterSql = "";
			String deathSql = "";
			int i =0;
			//first, build query as structure we can deal with
			Hashtable queryValues = new Hashtable();
			Hashtable conceptQueryValues = new Hashtable();
			Hashtable visitQueryValues = new Hashtable();
			ArrayList validRows = new ArrayList();
			ArrayList validConceptRows = new ArrayList();
			ArrayList validVisitRows = new ArrayList();
			
			for (Iterator itr=conceptChildren.iterator(); itr.hasNext(); )
			{
				QueryEntry newEntry = new QueryEntry();
				newEntry.EntryId = i + "";
				Element queryEntryXml = (org.jdom.Element) itr.next();
				Element conceptXml = (Element) queryEntryXml.getChild("Concept");
				Element conTableXml = (Element) conceptXml.getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("c_name");
				newEntry.Name = nameXml.getText();
				if (newEntry.Name != null) {
					newEntry.Name = newEntry.Name.replace("'", "''");
				}
				if (!((newEntry.Name.equals("Encounter Range Line"))||(newEntry.Name.equals("Vital Status Line"))))
				{
					org.jdom.Element pathXml = conTableXml.getChild("c_dimcode");
					newEntry.EntryValue = pathXml.getText();
					
					//wp, column names stuff
					Element factTableColumnXml = (Element) conTableXml.getChild("c_facttablecolumn");				
					newEntry.factTableColumnName = factTableColumnXml.getText().trim();		
					
					Element tableNameXml = (Element) conTableXml.getChild("c_tablename");				
					newEntry.tableName = tableNameXml.getText().trim();
					
					Element columnNameXml = (Element) conTableXml.getChild("c_columnname");				
					newEntry.columnName = columnNameXml.getText().trim();
					
					Element operatorXml = (Element) conTableXml.getChild("c_operator");				
					newEntry.operator = operatorXml.getText().trim();
				}
				else {
					newEntry.factTableColumnName = "concept_cd";						
					newEntry.tableName = "concept_dimension";			
					newEntry.columnName = "concept_path";
					newEntry.operator = "like";
				}
				
				Element colorXml = (Element) queryEntryXml.getChild("ConceptColor");				
				newEntry.Color = colorXml.getText().trim();
				
				///wp
				Element heightXml = (Element) queryEntryXml.getChild("Height");				
				newEntry.Height = heightXml.getText().trim();
				
				Element displaynameXml = (Element) queryEntryXml.getChild("DisplayName");				
				newEntry.displayName = displaynameXml.getText(); //.trim();
				if(newEntry.displayName != null) {
					newEntry.displayName = newEntry.displayName.trim().replaceAll("'", "''");
					newEntry.displayName = newEntry.displayName.replaceAll(",", "-");
				}
				
				Element valueXml = (Element) queryEntryXml.getChild("ModuleValue");		
				String mValue = valueXml.getText().trim();
				newEntry.Constraint = mValue;
				Element rowXml = (Element) queryEntryXml.getChild("RowNumber");				
				newEntry.RowNumber = rowXml.getText().trim();
				Integer rowNum = new Integer(newEntry.RowNumber);
				
				QueryRow rowValue = (QueryRow) queryValues.get(newEntry.RowNumber);
				if (rowValue!=null)
				{
					QueryConcept qConcept = (QueryConcept) rowValue.QueryConcepts.get(newEntry.Name);
					if (qConcept!= null) {
						qConcept.EntryList.add(newEntry);
					}
					else
					{
						qConcept = new QueryConcept();
						qConcept.ConceptName = newEntry.Name;
						qConcept.EntryList.add(newEntry);
						rowValue.RowName += "-" + newEntry.displayName; //newEntry.Name;
						rowValue.QueryConcepts.put(newEntry.Name, qConcept);
					}
					queryValues.put(newEntry.RowNumber, rowValue);
				}
				else{
					validRows.add(rowNum);
					QueryConcept qConcept = new QueryConcept();
					qConcept.ConceptName = newEntry.Name;
					qConcept.EntryList.add(newEntry);
					rowValue = new QueryRow();
					rowValue.RowName = newEntry.displayName; //newEntry.Name;
					rowValue.QueryConcepts.put(newEntry.Name, qConcept);
					queryValues.put(newEntry.RowNumber, rowValue);					
				}
				i++;
			}
			
			Collections.sort(validRows);
			for (int row=0; row<validRows.size(); row++)
			{
				QueryRow qRow = (QueryRow) queryValues.get(((Integer) validRows.get(row)).toString());
				Enumeration rowEnum = qRow.QueryConcepts.elements();
				QueryConcept qConcept = (QueryConcept) rowEnum.nextElement();
				QueryEntry qEntry = (QueryEntry) qConcept.EntryList.get(0);
				if(qEntry.tableName.equalsIgnoreCase("visit_dimension")) {
					visitQueryValues.put(qEntry.RowNumber, qRow);
					validVisitRows.add(new Integer(qEntry.RowNumber));
				}
				else {
					conceptQueryValues.put(qEntry.RowNumber, qRow);
					validConceptRows.add(new Integer(qEntry.RowNumber));
				}
			}
			
			String factcolumn = null;
			String tablename = null;
			Collections.sort(validConceptRows);
			Collections.sort(validVisitRows);
			for (int row=0; row<validConceptRows.size(); row++) {
				QueryRow qRow = (QueryRow) conceptQueryValues.get(((Integer) validConceptRows.get(row)).toString());
				concepts.add(new String(qRow.RowName));
				
				Enumeration rowEnum = qRow.QueryConcepts.elements();
				while (rowEnum.hasMoreElements())
				{
					QueryConcept qConcept = (QueryConcept) rowEnum.nextElement();
					
					for (int conNum = 0; conNum<qConcept.EntryList.size(); conNum++)
					{
						QueryEntry qEntry = (QueryEntry) qConcept.EntryList.get(conNum);
						factcolumn = qEntry.factTableColumnName; //assume only one type of column in each row
						tablename = qEntry.tableName;
						
						if (qEntry.Name.equals("Encounter Range Line"))
						{
							encounterSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Encounter_range' " + ss_q_name_char + ", \r\n" +
							"	'Encounter_range' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n "  +
							"	min(start_date) START_DATE, \r\n" +
							"	max(start_date) END_DATE, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"	'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o \r\n" +
							"where" + patientWhereClause.toString() /*o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax*/ + " \r\n" +
							"group by PATIENT_NUM ";
						}
						else if (qEntry.Name.equals("Vital Status Line"))
						{
							deathSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Death' " + ss_q_name_char + ", \r\n" +
							"	'Death' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n " +
							"	o.death_date START_DATE, \r\n" +
							"	o.death_date END_DATE, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"	'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "patient_dimension o \r\n" +
							"where" + patientWhereClause.toString() /*o.patient_num  between " + sPatientNumMin + " and " + sPatientNumMax*/ + " \r\n";		
						}
						else
						{								
							if ((qEntry.Constraint!=null)&&(qEntry.Constraint.trim().length()>0)&&(!qEntry.Constraint.toUpperCase().equals("N/AN/A")))
							{
								if (sConstraintSql.toString().trim().length()>0) {
									sConstraintSql.append("\r\nunion all\r\n");		
								}
								sConstraintSql.append(
										buildQueryConstraint(qEntry.Constraint, schemaName, 
												qRow.RowName, qEntry.RowNumber, qEntry.Color, 
												qEntry.Height, qEntry.EntryValue, patientWhereClause.toString(), 
												qEntry.factTableColumnName,
												qEntry.tableName, qEntry.columnName));
							}
							else
							{
								if (sConceptSql.toString().trim().length()>0) {
									sConceptSql.append("\r\nunion all\r\n");
								}
								if(qEntry.operator.equalsIgnoreCase("like")) {
									sConceptSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" "
											+"CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" like '" 
											+ qEntry.EntryValue + "\\%' \r\n");
								}
								else if(qEntry.operator.equalsIgnoreCase("=")) {
									sConceptSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" "
											+"CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" = '" 
											+ qEntry.EntryValue + "' \r\n");
								}
							}
							
						}
					}
				}
			}
			
			if (sConceptSql.toString().trim().length()>0)
			{
				//sSql.append("select /*+ index (v VISIT_DIM_PK) */\r\n " + 
				/*	"	o.patient_num PATIENT_NUM, \r\n" +
				 "	c." + ss_q_name_char + ",\r\n " +
				 "	c.CONCEPT_CD,\r\n " +
				 "	o.start_date START_DATE,\r\n " +
				 "	o.end_date END_DATE,\r\n " +
				 "	c.AN_ORDER,\r\n " +
				 "	c.CONCEPT_COLOR,\r\n " +
				 "	c.CONCEPT_HEIGHT,\r\n " +
				 "	'' CONCEPT_VALUE \r\n" +
				 "from " + schemaName + "observation_fact o, " + schemaName + "visit_dimension v, \r\n (" +
				 sConceptSql.toString() + ") c\r\n " +
				 "where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
				 "and o.encounter_num = v.encounter_num\r\n " +
				 "and o.patient_num = v.patient_num\r\n  " +
				 "and o.concept_cd = c.concept_cd ");*/
								
				sSql.append("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
						"	o.patient_num PATIENT_NUM, \r\n" +
						"	c." + ss_q_name_char + ",\r\n " +
						"	o.CONCEPT_CD,\r\n " +
						"	o.ENCOUNTER_NUM,\r\n " +
						"	o.start_date START_DATE,\r\n " +
						"	o.end_date END_DATE,\r\n " +
						"	c.AN_ORDER,\r\n " +
						"	c.CONCEPT_COLOR,\r\n " +
						"	c.CONCEPT_HEIGHT,\r\n " +
						"'"+tablename+"' TABLE_NAME,\r\n"+
						"	'' CONCEPT_VALUE \r\n" +
						"from " + schemaName + "observation_fact o, \r\n (" +
						sConceptSql.toString() + ") c\r\n " +
						"where"+ patientWhereClause.toString()/*o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax*/ + "\r\n " +
						"and o."+factcolumn/*concept_cd*/+" = c.concept_cd ");
			}
			
			for (int row=0; row<validVisitRows.size(); row++) {
				QueryRow qRow = (QueryRow) visitQueryValues.get(((Integer) validVisitRows.get(row)).toString());
				concepts.add(new String(qRow.RowName));
				
				Enumeration rowEnum = qRow.QueryConcepts.elements();
				while (rowEnum.hasMoreElements())
				{
					QueryConcept qConcept = (QueryConcept) rowEnum.nextElement();
					
					for (int conNum = 0; conNum<qConcept.EntryList.size(); conNum++)
					{
						QueryEntry qEntry = (QueryEntry) qConcept.EntryList.get(conNum);
						factcolumn = qEntry.factTableColumnName; //assume only one type of column in each row
						tablename = qEntry.tableName;
						
						if (qEntry.Name.equals("Encounter Range Line"))
						{
							encounterSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Encounter_range' " + ss_q_name_char + ", \r\n" +
							"	'Encounter_range' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n "  +
							"	min(start_date) START_DATE, \r\n" +
							"	max(start_date) END_DATE, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"	'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o \r\n" +
							"where" + patientWhereClause.toString() /*o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax*/ + " \r\n" +
							"group by PATIENT_NUM ";
						}
						else if (qEntry.Name.equals("Vital Status Line"))
						{
							deathSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Death' " + ss_q_name_char + ", \r\n" +
							"	'Death' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n " +
							"	o.death_date START_DATE, \r\n" +
							"	o.death_date END_DATE, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"	'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "patient_dimension o \r\n" +
							"where" + patientWhereClause.toString() /*o.patient_num  between " + sPatientNumMin + " and " + sPatientNumMax*/ + " \r\n";		
						}
						else
						{								
							if ((qEntry.Constraint!=null)&&(qEntry.Constraint.trim().length()>0)&&(!qEntry.Constraint.toUpperCase().equals("N/AN/A")))
							{
								if (sConstraintSql.toString().trim().length()>0) {
									sConstraintSql.append("\r\nunion all\r\n");		
								}
								sConstraintSql.append(
										buildQueryConstraint(qEntry.Constraint, schemaName, 
												qRow.RowName, qEntry.RowNumber, qEntry.Color, 
												qEntry.Height, qEntry.EntryValue, patientWhereClause.toString(), 
												qEntry.factTableColumnName,
												qEntry.tableName, qEntry.columnName));
							}
							else
							{
								if (sVisitSql.toString().trim().length()>0) {
									sVisitSql.append("\r\nunion all\r\n");
								}
								if(qEntry.operator.equalsIgnoreCase("like")) {
									sVisitSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" "
											+"CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" like '" 
											+ qEntry.EntryValue + "\\%' \r\n");
								}
								else if(qEntry.operator.equalsIgnoreCase("=")) {
									sVisitSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" "
											+"CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" = '" 
											+ qEntry.EntryValue + "' \r\n");
								}
							}
							
						}
					}
				}
			}
			
			if (sVisitSql.toString().trim().length()>0)
			{
				//sSql.append("select /*+ index (v VISIT_DIM_PK) */\r\n " + 
				/*	"	o.patient_num PATIENT_NUM, \r\n" +
				 "	c." + ss_q_name_char + ",\r\n " +
				 "	c.CONCEPT_CD,\r\n " +
				 "	o.start_date START_DATE,\r\n " +
				 "	o.end_date END_DATE,\r\n " +
				 "	c.AN_ORDER,\r\n " +
				 "	c.CONCEPT_COLOR,\r\n " +
				 "	c.CONCEPT_HEIGHT,\r\n " +
				 "	'' CONCEPT_VALUE \r\n" +
				 "from " + schemaName + "observation_fact o, " + schemaName + "visit_dimension v, \r\n (" +
				 sConceptSql.toString() + ") c\r\n " +
				 "where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
				 "and o.encounter_num = v.encounter_num\r\n " +
				 "and o.patient_num = v.patient_num\r\n  " +
				 "and o.concept_cd = c.concept_cd ");*/	
				
				sSql_Visit.append("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
						"	o.patient_num PATIENT_NUM, \r\n" +
						"	c." + ss_q_name_char + ",\r\n " +
						"	o.CONCEPT_CD,\r\n " +
						"	o.ENCOUNTER_NUM,\r\n " +
						"	o.start_date START_DATE,\r\n " +
						"	o.end_date END_DATE,\r\n " +
						"	c.AN_ORDER,\r\n " +
						"	c.CONCEPT_COLOR,\r\n " +
						"	c.CONCEPT_HEIGHT,\r\n " +
						"'"+tablename+"' TABLE_NAME,\r\n"+
						"	'' CONCEPT_VALUE \r\n" +
						"from " + schemaName + "observation_fact o, \r\n (" +
						sVisitSql.toString() + ") c\r\n " +
						"where"+ patientWhereClause.toString()/*o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax*/ + "\r\n " +
						"and o."+factcolumn/*concept_cd*/+" = c.concept_cd ");
			}
			
			if ((sSql.toString().trim().length()>0)&&(sConstraintSql.toString().trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(sConstraintSql.toString());
			
			if ((sSql.toString().trim().length()>0)&&(encounterSql.trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(encounterSql);
			
			if ((sSql.toString().trim().length()>0)&&(deathSql.trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(deathSql);
			
			if ((sSql.toString().trim().length()>0)&&(sSql_Visit.toString().trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(sSql_Visit);
			
			if (bUseConcept)
				sSql.append("order by 1, 7, 4, 5, 6 desc");
			else
				sSql.append("order by 1, 7, 5, 6 desc");
			
			return sSql.toString();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private static String buildQueryFromI2B2Xml(String i2b2Xml, String patientSetId, int minPatientNum, 
			int maxPatientNum, String schemaName, ArrayList concepts) {
		try{			
			StringBuilder sSql = new StringBuilder(1000);
			StringBuilder sSql_Visit = new StringBuilder(1000);
			StringBuilder sConceptSql = new StringBuilder(1000);
			StringBuilder sConstraintSql = new StringBuilder(1000);
			StringBuilder sVisitSql = new StringBuilder(1000);
			
			StringBuffer patientWhereClause = new StringBuffer(" o.patient_num in " +
					"(select PATIENT_NUM from asthma.qt_patient_set_collection where RESULT_INSTANCE_ID = ");
			patientWhereClause.append(patientSetId+" and SET_INDEX between " +minPatientNum +
					" and "+(minPatientNum+maxPatientNum)+")");
			
			boolean useConstraints = false;
			
			if ((schemaName==null)||(schemaName.trim().length()==0))
				schemaName = "";
			else
				schemaName = schemaName + ".";
			
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(i2b2Xml);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();
			List conceptChildren = tableXml.getChildren();
			String encounterSql = "";
			String deathSql = "";
			int i =0;
			//first, build query as structure we can deal with
			Hashtable queryValues = new Hashtable();
			Hashtable conceptQueryValues = new Hashtable();
			Hashtable visitQueryValues = new Hashtable();
			ArrayList validRows = new ArrayList();
			ArrayList validConceptRows = new ArrayList();
			ArrayList validVisitRows = new ArrayList();
			
			for (Iterator itr=conceptChildren.iterator(); itr.hasNext(); )
			{
				QueryEntry newEntry = new QueryEntry();
				newEntry.EntryId = i + "";
				Element queryEntryXml = (org.jdom.Element) itr.next();
				Element conceptXml = (Element) queryEntryXml.getChild("Concept");
				Element conTableXml = (Element) conceptXml.getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("c_name");
				newEntry.Name = nameXml.getText();
				if (newEntry.Name != null) {
					newEntry.Name = newEntry.Name.replace("'", "''");
				}
				if (!((newEntry.Name.equals("Encounter Range Line"))||(newEntry.Name.equals("Vital Status Line"))))
				{
					org.jdom.Element pathXml = conTableXml.getChild("c_dimcode");
					newEntry.EntryValue = pathXml.getText();
					
					//wp, column names stuff
					Element factTableColumnXml = (Element) conTableXml.getChild("c_facttablecolumn");				
					newEntry.factTableColumnName = factTableColumnXml.getText().trim();		
					
					Element tableNameXml = (Element) conTableXml.getChild("c_tablename");				
					newEntry.tableName = tableNameXml.getText().trim();
					
					Element columnNameXml = (Element) conTableXml.getChild("c_columnname");				
					newEntry.columnName = columnNameXml.getText().trim();
					
					Element operatorXml = (Element) conTableXml.getChild("c_operator");				
					newEntry.operator = operatorXml.getText().trim();
				}
				else {
					newEntry.factTableColumnName = "concept_cd";						
					newEntry.tableName = "concept_dimension";			
					newEntry.columnName = "concept_path";
					newEntry.operator = "like";
				}
				
				Element colorXml = (Element) queryEntryXml.getChild("ConceptColor");				
				newEntry.Color = colorXml.getText().trim();
				
				///wp
				Element heightXml = (Element) queryEntryXml.getChild("Height");				
				newEntry.Height = heightXml.getText().trim();
				
				Element displaynameXml = (Element) queryEntryXml.getChild("DisplayName");				
				newEntry.displayName = displaynameXml.getText(); //.trim();
				if(newEntry.displayName != null) {
					newEntry.displayName = newEntry.displayName.trim().replaceAll("'", "''");
					newEntry.displayName = newEntry.displayName.replaceAll(",", "-");
				}
				
				Element valueXml = (Element) queryEntryXml.getChild("ModuleValue");		
				String mValue = valueXml.getText().trim();
				newEntry.Constraint = mValue;
				Element rowXml = (Element) queryEntryXml.getChild("RowNumber");				
				newEntry.RowNumber = rowXml.getText().trim();
				Integer rowNum = new Integer(newEntry.RowNumber);
				
				QueryRow rowValue = (QueryRow) queryValues.get(newEntry.RowNumber);
				if (rowValue!=null)
				{
					QueryConcept qConcept = (QueryConcept) rowValue.QueryConcepts.get(newEntry.Name);
					if (qConcept!= null) {
						qConcept.EntryList.add(newEntry);
					}
					else
					{
						qConcept = new QueryConcept();
						qConcept.ConceptName = newEntry.Name;
						qConcept.EntryList.add(newEntry);
						rowValue.RowName += "-" + newEntry.displayName; //newEntry.Name;
						rowValue.QueryConcepts.put(newEntry.Name, qConcept);
					}
					queryValues.put(newEntry.RowNumber, rowValue);
				}
				else{
					validRows.add(rowNum);
					QueryConcept qConcept = new QueryConcept();
					qConcept.ConceptName = newEntry.Name;
					qConcept.EntryList.add(newEntry);
					rowValue = new QueryRow();
					rowValue.RowName = newEntry.displayName; //newEntry.Name;
					rowValue.QueryConcepts.put(newEntry.Name, qConcept);
					queryValues.put(newEntry.RowNumber, rowValue);					
				}
				i++;
			}
			
			Collections.sort(validRows);
			for (int row=0; row<validRows.size(); row++)
			{
				QueryRow qRow = (QueryRow) queryValues.get(((Integer) validRows.get(row)).toString());
				Enumeration rowEnum = qRow.QueryConcepts.elements();
				QueryConcept qConcept = (QueryConcept) rowEnum.nextElement();
				QueryEntry qEntry = (QueryEntry) qConcept.EntryList.get(0);
				if(qEntry.tableName.equalsIgnoreCase("visit_dimension")) {
					visitQueryValues.put(qEntry.RowNumber, qRow);
					validVisitRows.add(new Integer(qEntry.RowNumber));
				}
				else {
					conceptQueryValues.put(qEntry.RowNumber, qRow);
					validConceptRows.add(new Integer(qEntry.RowNumber));
				}
			}
			
			String factcolumn = null;
			String tablename = null;
			Collections.sort(validConceptRows);
			Collections.sort(validVisitRows);
			for (int row=0; row<validConceptRows.size(); row++) {
				QueryRow qRow = (QueryRow) conceptQueryValues.get(((Integer) validConceptRows.get(row)).toString());
				concepts.add(new String(qRow.RowName));
				
				Enumeration rowEnum = qRow.QueryConcepts.elements();
				while (rowEnum.hasMoreElements())
				{
					QueryConcept qConcept = (QueryConcept) rowEnum.nextElement();
					
					for (int conNum = 0; conNum<qConcept.EntryList.size(); conNum++)
					{
						QueryEntry qEntry = (QueryEntry) qConcept.EntryList.get(conNum);
						factcolumn = qEntry.factTableColumnName; //assume only one type of column in each row
						tablename = qEntry.tableName;
						
						if (qEntry.Name.equals("Encounter Range Line"))
						{
							encounterSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Encounter_range' " + ss_q_name_char + ", \r\n" +
							"	'Encounter_range' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n "  +
							"	min(start_date) START_DATE, \r\n" +
							"	max(start_date) END_DATE, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"	'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o \r\n" +
							"where" + patientWhereClause.toString() /*o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax*/ + " \r\n" +
							"group by PATIENT_NUM ";
						}
						else if (qEntry.Name.equals("Vital Status Line"))
						{
							deathSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Death' " + ss_q_name_char + ", \r\n" +
							"	'Death' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n " +
							"	o.death_date START_DATE, \r\n" +
							"	o.death_date END_DATE, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"	'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "patient_dimension o \r\n" +
							"where" + patientWhereClause.toString() /*o.patient_num  between " + sPatientNumMin + " and " + sPatientNumMax*/ + " \r\n";		
						}
						else
						{								
							if ((qEntry.Constraint!=null)&&(qEntry.Constraint.trim().length()>0)&&(!qEntry.Constraint.toUpperCase().equals("N/AN/A")))
							{
								if (sConstraintSql.toString().trim().length()>0) {
									sConstraintSql.append("\r\nunion all\r\n");		
								}
								sConstraintSql.append(
										buildQueryConstraint(qEntry.Constraint, schemaName, 
												qRow.RowName, qEntry.RowNumber, qEntry.Color, 
												qEntry.Height, qEntry.EntryValue, patientWhereClause.toString(), 
												qEntry.factTableColumnName,
												qEntry.tableName, qEntry.columnName));
							}
							else
							{
								if (sConceptSql.toString().trim().length()>0) {
									sConceptSql.append("\r\nunion all\r\n");
								}
								if(qEntry.operator.equalsIgnoreCase("like")) {
									sConceptSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" "
											+"CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" like '" 
											+ qEntry.EntryValue + "\\%' \r\n");
								}
								else if(qEntry.operator.equalsIgnoreCase("=")) {
									sConceptSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" "
											+"CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" = '" 
											+ qEntry.EntryValue + "' \r\n");
								}
							}
							
						}
					}
				}
			}
			
			if (sConceptSql.toString().trim().length()>0)
			{
				//sSql.append("select /*+ index (v VISIT_DIM_PK) */\r\n " + 
				/*	"	o.patient_num PATIENT_NUM, \r\n" +
				 "	c." + ss_q_name_char + ",\r\n " +
				 "	c.CONCEPT_CD,\r\n " +
				 "	o.start_date START_DATE,\r\n " +
				 "	o.end_date END_DATE,\r\n " +
				 "	c.AN_ORDER,\r\n " +
				 "	c.CONCEPT_COLOR,\r\n " +
				 "	c.CONCEPT_HEIGHT,\r\n " +
				 "	'' CONCEPT_VALUE \r\n" +
				 "from " + schemaName + "observation_fact o, " + schemaName + "visit_dimension v, \r\n (" +
				 sConceptSql.toString() + ") c\r\n " +
				 "where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
				 "and o.encounter_num = v.encounter_num\r\n " +
				 "and o.patient_num = v.patient_num\r\n  " +
				 "and o.concept_cd = c.concept_cd ");*/
				
				
				
				sSql.append("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
						"	o.patient_num PATIENT_NUM, \r\n" +
						"	c." + ss_q_name_char + ",\r\n " +
						"	o.CONCEPT_CD,\r\n " +
						"	o.ENCOUNTER_NUM,\r\n " +
						"	o.start_date START_DATE,\r\n " +
						"	o.end_date END_DATE,\r\n " +
						"	c.AN_ORDER,\r\n " +
						"	c.CONCEPT_COLOR,\r\n " +
						"	c.CONCEPT_HEIGHT,\r\n " +
						"'"+tablename+"' TABLE_NAME,\r\n"+
						"	'' CONCEPT_VALUE \r\n" +
						"from " + schemaName + "observation_fact o, \r\n (" +
						sConceptSql.toString() + ") c\r\n " +
						"where"+ patientWhereClause.toString()/*o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax*/ + "\r\n " +
						"and o."+factcolumn/*concept_cd*/+" = c.concept_cd ");
			}
			
			for (int row=0; row<validVisitRows.size(); row++) {
				QueryRow qRow = (QueryRow) visitQueryValues.get(((Integer) validVisitRows.get(row)).toString());
				concepts.add(new String(qRow.RowName));
				
				Enumeration rowEnum = qRow.QueryConcepts.elements();
				while (rowEnum.hasMoreElements())
				{
					QueryConcept qConcept = (QueryConcept) rowEnum.nextElement();
					
					for (int conNum = 0; conNum<qConcept.EntryList.size(); conNum++)
					{
						QueryEntry qEntry = (QueryEntry) qConcept.EntryList.get(conNum);
						factcolumn = qEntry.factTableColumnName; //assume only one type of column in each row
						tablename = qEntry.tableName;
						
						if (qEntry.Name.equals("Encounter Range Line"))
						{
							encounterSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Encounter_range' " + ss_q_name_char + ", \r\n" +
							"	'Encounter_range' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n "  +
							"	min(start_date) START_DATE, \r\n" +
							"	max(start_date) END_DATE, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"	'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "observation_fact o \r\n" +
							"where" + patientWhereClause.toString() /*o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax*/ + " \r\n" +
							"group by PATIENT_NUM ";
						}
						else if (qEntry.Name.equals("Vital Status Line"))
						{
							deathSql = "select o.patient_num PATIENT_NUM, \r\n" +
							"	'Death' " + ss_q_name_char + ", \r\n" +
							"	'Death' CONCEPT_CD, \r\n" +
							"	0 ENCOUNTER_NUM,\r\n " +
							"	o.death_date START_DATE, \r\n" +
							"	o.death_date END_DATE, \r\n" +
							"	" + qEntry.RowNumber + " AN_ORDER,\r\n " +
							"	'" + qEntry.Color + "' CONCEPT_COLOR,\r\n " +
							"	'" + qEntry.Height + "' CONCEPT_HEIGHT, \r\n" +
							"	'"+tablename+"' TABLE_NAME,\r\n"+
							"	'' CONCEPT_VALUE \r\n" +
							"from " + schemaName + "patient_dimension o \r\n" +
							"where" + patientWhereClause.toString() /*o.patient_num  between " + sPatientNumMin + " and " + sPatientNumMax*/ + " \r\n";		
						}
						else
						{								
							if ((qEntry.Constraint!=null)&&(qEntry.Constraint.trim().length()>0)&&(!qEntry.Constraint.toUpperCase().equals("N/AN/A")))
							{
								if (sConstraintSql.toString().trim().length()>0) {
									sConstraintSql.append("\r\nunion all\r\n");		
								}
								sConstraintSql.append(
										buildQueryConstraint(qEntry.Constraint, schemaName, 
												qRow.RowName, qEntry.RowNumber, qEntry.Color, 
												qEntry.Height, qEntry.EntryValue, patientWhereClause.toString(), 
												qEntry.factTableColumnName,
												qEntry.tableName, qEntry.columnName));
							}
							else
							{
								if (sVisitSql.toString().trim().length()>0) {
									sVisitSql.append("\r\nunion all\r\n");
								}
								if(qEntry.operator.equalsIgnoreCase("like")) {
									sVisitSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" "
											+"CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" like '" 
											+ qEntry.EntryValue + "\\%' \r\n");
								}
								else if(qEntry.operator.equalsIgnoreCase("=")) {
									sVisitSql.append("select " +
											"	'" + qRow.RowName + "' " + ss_q_name_char + ", \r\n" +
											/*"	concept_cd CONCEPT_CD*/
											" "+qEntry.factTableColumnName+" "
											+"CONCEPT_CD, \r\n" +
											"	" + qEntry.RowNumber + " AN_ORDER, \r\n" +
											"	'" + qEntry.Color + "' CONCEPT_COLOR, \r\n" +
											"	'" + qEntry.Height + "' CONCEPT_HEIGHT \r\n" +
											"from " + schemaName + qEntry.tableName/*"concept_dimension*/+" \r\n" +
											"where "+qEntry.columnName/*concept_path*/+" = '" 
											+ qEntry.EntryValue + "' \r\n");
								}
							}
							
						}
					}
				}
			}
			
			if (sVisitSql.toString().trim().length()>0)
			{
				//sSql.append("select /*+ index (v VISIT_DIM_PK) */\r\n " + 
				/*	"	o.patient_num PATIENT_NUM, \r\n" +
				 "	c." + ss_q_name_char + ",\r\n " +
				 "	c.CONCEPT_CD,\r\n " +
				 "	o.start_date START_DATE,\r\n " +
				 "	o.end_date END_DATE,\r\n " +
				 "	c.AN_ORDER,\r\n " +
				 "	c.CONCEPT_COLOR,\r\n " +
				 "	c.CONCEPT_HEIGHT,\r\n " +
				 "	'' CONCEPT_VALUE \r\n" +
				 "from " + schemaName + "observation_fact o, " + schemaName + "visit_dimension v, \r\n (" +
				 sConceptSql.toString() + ") c\r\n " +
				 "where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
				 "and o.encounter_num = v.encounter_num\r\n " +
				 "and o.patient_num = v.patient_num\r\n  " +
				 "and o.concept_cd = c.concept_cd ");*/
						
				sSql_Visit.append("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
						"	o.patient_num PATIENT_NUM, \r\n" +
						"	c." + ss_q_name_char + ",\r\n " +
						"	o.CONCEPT_CD,\r\n " +
						"	o.ENCOUNTER_NUM,\r\n " +
						"	o.start_date START_DATE,\r\n " +
						"	o.end_date END_DATE,\r\n " +
						"	c.AN_ORDER,\r\n " +
						"	c.CONCEPT_COLOR,\r\n " +
						"	c.CONCEPT_HEIGHT,\r\n " +
						"'"+tablename+"' TABLE_NAME,\r\n"+
						"	'' CONCEPT_VALUE \r\n" +
						"from " + schemaName + "observation_fact o, \r\n (" +
						sVisitSql.toString() + ") c\r\n " +
						"where"+ patientWhereClause.toString()/*o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax*/ + "\r\n " +
						"and o."+factcolumn/*concept_cd*/+" = c.concept_cd ");
			}
			
			if ((sSql.toString().trim().length()>0)&&(sConstraintSql.toString().trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(sConstraintSql.toString());
			
			if ((sSql.toString().trim().length()>0)&&(encounterSql.trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(encounterSql);
			
			if ((sSql.toString().trim().length()>0)&&(deathSql.trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(deathSql);
			
			if ((sSql.toString().trim().length()>0)&&(sSql_Visit.toString().trim().length()>0))
				sSql.append("\r\nunion all\r\n");
			
			sSql.append(sSql_Visit);
			
			if (bUseConcept)
				sSql.append("order by 1, 7, 4, 5, 6 desc");
			else
				sSql.append("order by 1, 7, 5, 6 desc");
			
			return sSql.toString();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private static String buildQueryConstraint(String constraint, String schemaName, String rowName, String rowNumber,
			String colorName, String height, String entryValue, String sPatientNumMin, String sPatientNumMax,
			String factTableColumnName, String tableName, String c_columnName)
	{
		try{
			StringBuilder selectSql = new StringBuilder("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
					"	o.patient_num PATIENT_NUM, \r\n" +
					"	c." + ss_q_name_char + ",\r\n " +
					"	o.CONCEPT_CD,\r\n " +
					"	o.ENCOUNTER_NUM,\r\n " +
					"	o.start_date START_DATE,\r\n " +
					"	o.end_date END_DATE,\r\n " +
					"	c.AN_ORDER,\r\n " +
					"	c.CONCEPT_COLOR,\r\n " +
					"	c.CONCEPT_HEIGHT,\r\n "+
					"'"+tableName+"' TABLE_NAME");
			
			StringBuilder fromSql = new StringBuilder("from " + schemaName + "observation_fact o, (" +
					"select " +
					"	'" + rowName + "' " + ss_q_name_char + ", \r\n" +
					/*"	concept_cd CONCEPT_CD*/
					factTableColumnName+" "+"CONCEPT_CD, \r\n" +
					"	" + rowNumber + " AN_ORDER, \r\n" +
					"	'" + colorName + "' CONCEPT_COLOR, \r\n" +
					"	'" + height + "' CONCEPT_HEIGHT \r\n" +
					"from " + schemaName + tableName/*"concept_dimension*/+" \r\n" +
					"where "+/*concept_path*/c_columnName+" like '" + entryValue + "\\%') c\r\n ");
			
			StringBuilder constraintSql = new StringBuilder("where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
					"and o."+/*concept_cd*/factTableColumnName+" = c.concept_cd\r\n ");
			
			
			if ((constraint!=null)&&(constraint.trim().length()>0))
			{    			
				String columnName = null;
				String newColumnName = "";
				String selectColumnName = "";
				StringTokenizer st = new StringTokenizer(constraint, " ,<,>,=");
				if (st.hasMoreTokens())
				{
					columnName = st.nextToken();
					if ((columnName!=null)&&(columnName.trim().length()>0))
					{
						if (columnName.toUpperCase().equals("NVAL"))
						{
							constraintSql.append("and o.VALTYPE_CD = 'N' and ");
							newColumnName = "o.NVAL_NUM";
							selectColumnName = "TO_CHAR(o.NVAL_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("NVAL_NUM"))
						{
							constraintSql.append("and o.VALTYPE_CD = 'N' and ");
							newColumnName = "o.NVAL_NUM";
							selectColumnName = "TO_CHAR(o.NVAL_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("TVAL"))
						{
							constraintSql.append("and o.VALTYPE_CD = 'T' and ");
							newColumnName = "o.TVAL_CHAR";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("TVAL_CHAR"))
						{
							constraintSql.append("and o.VALTYPE_CD = 'T' and ");
							newColumnName = "o.TVAL_CHAR";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("QUANTITY"))
						{
							constraintSql.append("and ");
							newColumnName = "o.QUANTITY_NUM";
							selectColumnName = "TO_CHAR(o.QUANTITY_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("QUANTITY_NUM"))
						{
							constraintSql.append("and ");
							newColumnName = "o.QUANTITY_NUM";
							selectColumnName = "TO_CHAR(o.QUANTITY_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("UNITS_CD"))
						{
							constraintSql.append("and ");
							newColumnName = "o.UNITS_CD";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("END_DATE"))
						{
							constraintSql.append("and ");
							newColumnName = "o.END_DATE";
							selectColumnName = "TO_CHAR(o.END_DATE, 'MM/DD/YYYY HH:MI:SS AM')";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("START_DATE"))
						{
							constraintSql.append("and ");
							newColumnName = "o.START_DATE";
							selectColumnName = "TO_CHAR(o.START_DATE, 'MM/DD/YYYY HH:MI:SS AM')";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("CONFIDENCE_NUM"))
						{
							constraintSql.append("and ");
							newColumnName = "o.CONFIDENCE_NUM";
							selectColumnName = "TO_CHAR(o.CONFIDENCE_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("SOURCESYSTEM"))
						{
							constraintSql.append("and ");
							newColumnName = "o.SOURCESYSTEM";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("LOCATION_CD"))
						{
							fromSql.append(", " + schemaName + "visit_dimension v\r\n ");
							constraintSql.append("and o.encounter_num = v.encounter_num\r\n " +
									"and o.patient_num = v.patient_num\r\n  " +
							"and ");
							newColumnName = "v.LOCATION_CD";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("LOCATION_PATH"))
						{
							fromSql.append(", " + schemaName + "visit_dimension v\r\n ");
							constraintSql.append("and o.encounter_num = v.encounter_num\r\n " +
									"and o.patient_num = v.patient_num\r\n  " +
							"and ");
							newColumnName = "v.LOCATION_PATH";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("INOUT_CD"))
						{
							fromSql.append(", " + schemaName + "visit_dimension v\r\n ");
							constraintSql.append("and o.encounter_num = v.encounter_num\r\n " +
									"and o.patient_num = v.patient_num\r\n  " +
							"and ");
							newColumnName = "v.INOUT_CD";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						selectSql.append(",\r\n " + selectColumnName + " CONCEPT_VALUE \r\n");
					}
				}
			}
			else
				selectSql.append(",\r\n '' \r\n");
			return selectSql.toString() + fromSql.toString() + constraintSql.toString();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private static String buildQueryConstraint(String constraint, String schemaName, String rowName, String rowNumber,
			String colorName, String height, String entryValue, String patientWhereClause,
			String factTableColumnName, String tableName, String c_columnName)
	{
		try{
			StringBuilder selectSql = new StringBuilder("select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/\r\n " + 
					"	o.patient_num PATIENT_NUM, \r\n" +
					"	c." + ss_q_name_char + ",\r\n " +
					"	o.CONCEPT_CD,\r\n " +
					"	o.ENCOUNTER_NUM,\r\n " +
					"	o.start_date START_DATE,\r\n " +
					"	o.end_date END_DATE,\r\n " +
					"	c.AN_ORDER,\r\n " +
					"	c.CONCEPT_COLOR,\r\n " +
					"	c.CONCEPT_HEIGHT,\r\n "+
					"'"+tableName+"' TABLE_NAME");
			
			StringBuilder fromSql = new StringBuilder("from " + schemaName + "observation_fact o, (" +
					"select " +
					"	'" + rowName + "' " + ss_q_name_char + ", \r\n" +
					/*"	concept_cd CONCEPT_CD*/
					factTableColumnName+" "+"CONCEPT_CD, \r\n" +
					"	" + rowNumber + " AN_ORDER, \r\n" +
					"	'" + colorName + "' CONCEPT_COLOR, \r\n" +
					"	'" + height + "' CONCEPT_HEIGHT \r\n" +
					"from " + schemaName + tableName/*"concept_dimension*/+" \r\n" +
					"where "+/*concept_path*/c_columnName+" like '" + entryValue + "\\%') c\r\n ");
			
			StringBuilder constraintSql = new StringBuilder("where"+ patientWhereClause/*o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax*/ + "\r\n " +
					"and o."+/*concept_cd*/factTableColumnName+" = c.concept_cd\r\n ");
			
			
			if ((constraint!=null)&&(constraint.trim().length()>0))
			{    			
				String columnName = null;
				String newColumnName = "";
				String selectColumnName = "";
				StringTokenizer st = new StringTokenizer(constraint, " ,<,>,=");
				if (st.hasMoreTokens())
				{
					columnName = st.nextToken();
					if ((columnName!=null)&&(columnName.trim().length()>0))
					{
						if (columnName.toUpperCase().equals("NVAL"))
						{
							constraintSql.append("and o.VALTYPE_CD = 'N' and ");
							newColumnName = "o.NVAL_NUM";
							selectColumnName = "TO_CHAR(o.NVAL_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("NVAL_NUM"))
						{
							constraintSql.append("and o.VALTYPE_CD = 'N' and ");
							newColumnName = "o.NVAL_NUM";
							selectColumnName = "TO_CHAR(o.NVAL_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("TVAL"))
						{
							constraintSql.append("and o.VALTYPE_CD = 'T' and ");
							newColumnName = "o.TVAL_CHAR";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("TVAL_CHAR"))
						{
							constraintSql.append("and o.VALTYPE_CD = 'T' and ");
							newColumnName = "o.TVAL_CHAR";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("QUANTITY"))
						{
							constraintSql.append("and ");
							newColumnName = "o.QUANTITY_NUM";
							selectColumnName = "TO_CHAR(o.QUANTITY_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("QUANTITY_NUM"))
						{
							constraintSql.append("and ");
							newColumnName = "o.QUANTITY_NUM";
							selectColumnName = "TO_CHAR(o.QUANTITY_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("UNITS_CD"))
						{
							constraintSql.append("and ");
							newColumnName = "o.UNITS_CD";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("END_DATE"))
						{
							constraintSql.append("and ");
							newColumnName = "o.END_DATE";
							selectColumnName = "TO_CHAR(o.END_DATE, 'MM/DD/YYYY HH:MI:SS AM')";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("START_DATE"))
						{
							constraintSql.append("and ");
							newColumnName = "o.START_DATE";
							selectColumnName = "TO_CHAR(o.START_DATE, 'MM/DD/YYYY HH:MI:SS AM')";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("CONFIDENCE_NUM"))
						{
							constraintSql.append("and ");
							newColumnName = "o.CONFIDENCE_NUM";
							selectColumnName = "TO_CHAR(o.CONFIDENCE_NUM)";
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("SOURCESYSTEM"))
						{
							constraintSql.append("and ");
							newColumnName = "o.SOURCESYSTEM";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("LOCATION_CD"))
						{
							fromSql.append(", " + schemaName + "visit_dimension v\r\n ");
							constraintSql.append("and o.encounter_num = v.encounter_num\r\n " +
									"and o.patient_num = v.patient_num\r\n  " +
							"and ");
							newColumnName = "v.LOCATION_CD";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("LOCATION_PATH"))
						{
							fromSql.append(", " + schemaName + "visit_dimension v\r\n ");
							constraintSql.append("and o.encounter_num = v.encounter_num\r\n " +
									"and o.patient_num = v.patient_num\r\n  " +
							"and ");
							newColumnName = "v.LOCATION_PATH";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						else if (columnName.toUpperCase().equals("INOUT_CD"))
						{
							fromSql.append(", " + schemaName + "visit_dimension v\r\n ");
							constraintSql.append("and o.encounter_num = v.encounter_num\r\n " +
									"and o.patient_num = v.patient_num\r\n  " +
							"and ");
							newColumnName = "v.INOUT_CD";
							selectColumnName = newColumnName;
							constraintSql.append(constraint.replace(columnName, newColumnName) + " ");
						}
						selectSql.append(",\r\n " + selectColumnName + " CONCEPT_VALUE \r\n");
					}
				}
			}
			else
				selectSql.append(",\r\n '' \r\n");
			return selectSql.toString() + fromSql.toString() + constraintSql.toString();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private static String buildQueryFromI2B2Xml_5016(String i2b2Xml, int minPatientNum, int maxPatientNum, String schemaName){
		try{
			
			StringBuilder sSql = new StringBuilder(1000);
			StringBuilder sConceptSql = new StringBuilder(1000);
			//String sPatientNumMin = "52800";
			//String sPatientNumMax = "52850";
			String sPatientNumMin = minPatientNum + "";
			String sPatientNumMax = maxPatientNum + "";
			
			if ((schemaName==null)||(schemaName.trim().length()==0))
				schemaName = "";
			else
				schemaName = schemaName + ".";
			
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(i2b2Xml);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();
			List conceptChildren = tableXml.getChildren();
			String encounterSql = "";
			String deathSql = "";
			int i =0;
			for (Iterator itr=conceptChildren.iterator(); itr.hasNext(); )
			{
				Element queryEntryXml = (org.jdom.Element) itr.next();
				Element conceptXml = (Element) queryEntryXml.getChild("Concept");
				Element conTableXml = (Element) conceptXml.getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("c_name");
				String sConceptName = nameXml.getText();
				if (sConceptName!=null) {
					sConceptName = sConceptName.replace("'", "''");
				}
				Element colorXml = (Element) queryEntryXml.getChild("ConceptColor");				
				String colorName = colorXml.getText().trim();
				
				///wp
				Element heightXml = (Element) queryEntryXml.getChild("Height");				
				String height = heightXml.getText().trim();
				Element valueXml = (Element) queryEntryXml.getChild("ModuleValue");				
				String value = valueXml.getText().trim();
				
				if (sConceptName.equals("Encounter Range Line"))
				{
					encounterSql = "select o.patient_num PATIENT_NUM, \r\n" +
					"	'Encounter_range' " + ss_q_name_char + ", \r\n" +
					"	'Encounter_range' CONCEPT_CD, \r\n" +
					"	min(start_date) START_DATE, \r\n" +
					"	max(start_date) END_DATE, \r\n" +
					"	'E' INOUT_CD, \r\n" +
					"	" + i + " AN_ORDER,\r\n " +
					"	'" + colorName + "' CONCEPT_COLOR\r\n " +
					"from " + schemaName + "observation_fact o \r\n" +
					"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + " \r\n" +
					"group by PATIENT_NUM ";
					
				}
				else if (sConceptName.equals("Vital Status Line"))
				{
					deathSql = "select p.patient_num PATIENT_NUM, \r\n" +
					"	'Death' " + ss_q_name_char + ", \r\n" +
					"	'Death' CONCEPT_CD, \r\n" +
					"	p.death_date START_DATE, \r\n" +
					"	p.death_date END_DATE, \r\n" +
					"	'D' INOUT_CD, \r\n" +
					"	" + i + " AN_ORDER,\r\n " +
					"	'" + colorName + "' CONCEPT_COLOR\r\n " +
					"from " + schemaName + "patient_dimension p \r\n" +
					"where p.patient_num  between " + sPatientNumMin + " and " + sPatientNumMax + " \r\n";
					
				}
				else
				{
					org.jdom.Element pathXml = conTableXml.getChild("c_dimcode");
					String sConceptPath = pathXml.getText();
					
					/*sSql.append("select o.patient_num PATIENT_NUM, \r\n" +
					 "	'" + sConceptName + "' " + ss_q_name_char + ",\r\n " +
					 "	o.concept_cd CONCEPT_CD,\r\n " +
					 "	v.start_date START_DATE,\r\n " +
					 "	v.end_date END_DATE,\r\n " +
					 "	v.inout_cd INOUT_CD,\r\n " +
					 "	" + i + " AN_ORDER,\r\n " +
					 "	'" + colorName + "' CONCEPT_COLOR\r\n " +
					 "from observation_fact o, visit_dimension v\r\n " +
					 "where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
					 "and o.encounter_num = v.encounter_num\r\n " +
					 "and o.patient_num = v.patient_num\r\n  " +
					 "and o.concept_cd in\r\n " +
					 "	(select concept_cd\r\n " +
					 "	from concept_dimension\r\n " +
					 "	where concept_path like '" + sConceptPath + "\\%') \r\n");*/
					if (sConceptSql.toString().trim().length()>0)
						sConceptSql.append("\r\nunion\r\n");
					
					sConceptSql.append("select " +
							"	'" + sConceptName + "' " + ss_q_name_char + ", \r\n" +
							"	concept_cd CONCEPT_CD, \r\n" +
							"	" + i + " AN_ORDER, \r\n" +
							"	'" + colorName + "' CONCEPT_COLOR, \r\n" +
							"	'" + height + "' CONCEPT_HEIGHT \r\n" +
							"from " + schemaName + "concept_dimension \r\n" +
							"where concept_path like '" + sConceptPath + "\\%' \r\n");
				}
				i++;					
			}
			
			//sSql.append(   		"group by 1 \r\n" +
			//"order by 1, 7, 3, 4, 5");
			
			if (sConceptSql.toString().trim().length()>0)
			{
				sSql.append("select /*+ index (v VISIT_DIM_PK) */\r\n " + 
						"	o.patient_num PATIENT_NUM, \r\n" +
						"	c." + ss_q_name_char + ",\r\n " +
						"	c.CONCEPT_CD,\r\n " +
						"	o.start_date START_DATE,\r\n " +
						"	o.end_date END_DATE,\r\n " +
						"	v.inout_cd INOUT_CD,\r\n " +
						"	c.AN_ORDER,\r\n " +
						"	c.CONCEPT_COLOR,\r\n " +
						"	c.CONCEPT_HEIGHT\r\n " +
						"from " + schemaName + "observation_fact o, " + schemaName + "visit_dimension v, \r\n (" +
						sConceptSql.toString() + ") c\r\n " +
						"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
						"and o.encounter_num = v.encounter_num\r\n " +
						"and o.patient_num = v.patient_num\r\n  " +
						"and o.concept_cd = c.concept_cd " /*+ 
						"and o.nval_num > 2 "*/);
			}
			
			if ((sSql.toString().trim().length()>0)&&(encounterSql.trim().length()>0))
				sSql.append("\r\nunion\r\n");
			
			sSql.append(encounterSql);
			
			if ((sSql.toString().trim().length()>0)&&(deathSql.trim().length()>0))
				sSql.append("\r\nunion\r\n");
			
			sSql.append(deathSql);
			
			if (bUseConcept)
				sSql.append("order by 1, 7, 3, 4, 5 desc");
			else
				sSql.append("order by 1, 7, 4, 5 desc, 6");
			
			return sSql.toString();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private static String buildQueryFromI2B2Xml_old(String i2b2Xml, int minPatientNum, int maxPatientNum){
		try{
			
			StringBuilder sSql = new StringBuilder(1000);
			//String sPatientNumMin = "52800";
			//String sPatientNumMax = "52850";
			String sPatientNumMin = minPatientNum + "";
			String sPatientNumMax = maxPatientNum + "";
			
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(i2b2Xml);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();
			List conceptChildren = tableXml.getChildren();
			int i =0;
			for (Iterator itr=conceptChildren.iterator(); itr.hasNext(); )
			{
				Element queryEntryXml = (org.jdom.Element) itr.next();
				Element conceptXml = (Element) queryEntryXml.getChild("Concept");
				Element conTableXml = (Element) conceptXml.getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("c_name");
				String sConceptName = nameXml.getText();
				Element colorXml = (Element) queryEntryXml.getChild("ConceptColor");				
				String colorName = colorXml.getText().trim();
				if (i>0)
					sSql.append("\r\nunion\r\n");
				
				if (sConceptName.equals("Encounter Range Line"))
				{
					sSql.append("select o.patient_num PATIENT_NUM, " +
							"	'Encounter_range' " + ss_q_name_char + ", " +
							"	'Encounter_range' CONCEPT_CD, " +
							"	min(start_date) START_DATE, " +
							"	max(start_date) END_DATE, " +
							"	'E' INOUT_CD, " +
							"	" + i + " AN_ORDER,\r\n " +
							"	'" + colorName + "' CONCEPT_COLOR\r\n " +
							"from observation_fact o " +
							"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + " " +
					"group by PATIENT_NUM ");
					
				}
				else if (sConceptName.equals("Vital Status Line"))
				{
					sSql.append("select p.patient_num PATIENT_NUM, " +
							"	'Death' " + ss_q_name_char + ", " +
							"	'Death' CONCEPT_CD, " +
							"	p.death_date START_DATE, " +
							"	p.death_date END_DATE, " +
							"	'D' INOUT_CD, " +
							"	" + i + " AN_ORDER,\r\n " +
							"	'" + colorName + "' CONCEPT_COLOR\r\n " +
							"from patient_dimension p " +
							"where p.patient_num  between " + sPatientNumMin + " and " + sPatientNumMax + " ");
					
				}
				else
				{
					org.jdom.Element pathXml = conTableXml.getChild("c_dimcode");
					String sConceptPath = pathXml.getText();
					
					sSql.append("select o.patient_num PATIENT_NUM, \r\n" +
							"	'" + sConceptName + "' " + ss_q_name_char + ",\r\n " +
							"	o.concept_cd CONCEPT_CD,\r\n " +
							"	o.start_date START_DATE,\r\n " +
							"	o.end_date END_DATE,\r\n " +
							"	v.inout_cd INOUT_CD,\r\n " +
							"	" + i + " AN_ORDER,\r\n " +
							"	'" + colorName + "' CONCEPT_COLOR\r\n " +
							"from observation_fact o, visit_dimension v\r\n " +
							"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
							"and o.encounter_num = v.encounter_num\r\n " +
							"and o.patient_num = v.patient_num\r\n  " +
							"and o.concept_cd in\r\n " +
							"	(select concept_cd\r\n " +
							"	from concept_dimension\r\n " +
							"	where concept_path like '" + sConceptPath + "\\%') \r\n");
					
				}
				
				i++;
			}
			
			//sSql.append(   		"group by 1 \r\n" +
			//"order by 1, 7, 3, 4, 5");
			if (bUseConcept)
				sSql.append("order by 1, 7, 3, 4, 5 desc");
			else
				sSql.append("order by 1, 7, 4, 5 desc, 6");
			
			return sSql.toString();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private static String buildQueryFromI2B2Xml_alt(String i2b2Xml, int minPatientNum, int maxPatientNum){
		try{
			
			StringBuilder sSql = new StringBuilder(1000);
			//String sPatientNumMin = "52800";
			//String sPatientNumMax = "52850";
			String sPatientNumMin = minPatientNum + "";
			String sPatientNumMax = maxPatientNum + "";
			
			SAXBuilder parser = new SAXBuilder();
			java.io.StringReader xmlStringReader = new java.io.StringReader(i2b2Xml);
			org.jdom.Document tableDoc = parser.build(xmlStringReader);
			org.jdom.Element tableXml = tableDoc.getRootElement();
			List conceptChildren = tableXml.getChildren();
			int i =0;
			for (Iterator itr=conceptChildren.iterator(); itr.hasNext(); )
			{
				Element queryEntryXml = (org.jdom.Element) itr.next();
				Element conceptXml = (Element) queryEntryXml.getChild("Concept");
				Element conTableXml = (Element) conceptXml.getChildren().get(0);
				org.jdom.Element nameXml = conTableXml.getChild("c_name");
				String sConceptName = nameXml.getText();
				Element colorXml = (Element) queryEntryXml.getChild("ConceptColor");				
				String colorName = colorXml.getText().trim();
				if (i>0)
					sSql.append("\r\nunion all\r\n");
				
				if (sConceptName.equals("Encounter Range Line"))
				{
					sSql.append("select o.patient_num PATIENT_NUM, " +
							"	'Encounter_range' " + ss_q_name_char + ", " +
							"	'Encounter_range' CONCEPT_CD, " +
							"	min(start_date) START_DATE, " +
							"	max(start_date) END_DATE, " +
							"	'E' INOUT_CD, " +
							"	" + i + " AN_ORDER,\r\n " +
							"	'" + colorName + "' CONCEPT_COLOR\r\n " +
							"from observation_fact o " +
							"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + " " +
					"group by PATIENT_NUM ");
					
				}
				else if (sConceptName.equals("Vital Status Line"))
				{
					sSql.append("select p.patient_num PATIENT_NUM, " +
							"	'Death' " + ss_q_name_char + ", " +
							"	'Death' CONCEPT_CD, " +
							"	p.death_date START_DATE, " +
							"	p.death_date END_DATE, " +
							"	'D' INOUT_CD, " +
							"	" + i + " AN_ORDER,\r\n " +
							"	'" + colorName + "' CONCEPT_COLOR\r\n " +
							"from patient_dimension p " +
							"where p.patient_num  between " + sPatientNumMin + " and " + sPatientNumMax + " ");
					
				}
				else
				{
					org.jdom.Element pathXml = conTableXml.getChild("c_dimcode");
					String sConceptPath = pathXml.getText();
					
					sSql.append("select distinct o.patient_num PATIENT_NUM, \r\n" +
							"	'" + sConceptName + "' " + ss_q_name_char + ",\r\n " +
							"	o.concept_cd CONCEPT_CD,\r\n " +
							"	o.start_date START_DATE,\r\n " +
							"	o.end_date END_DATE,\r\n " +
							"	v.inout_cd INOUT_CD,\r\n " +
							"	" + i + " AN_ORDER,\r\n " +
							"	'" + colorName + "' CONCEPT_COLOR\r\n " +
							"from observation_fact o, visit_dimension v\r\n " +
							"where o.patient_num between " + sPatientNumMin + " and " + sPatientNumMax + "\r\n " +
							"and o.encounter_num = v.encounter_num\r\n " +
							"and o.patient_num = v.patient_num\r\n  " +
							"and o.concept_cd in\r\n " +
							"	(select concept_cd\r\n " +
							"	from concept_dimension\r\n " +
							"	where concept_path like '" + sConceptPath + "\\%') \r\n");
					
				}
				
				i++;
			}
			
			//sSql.append(   		"group by 1 \r\n" +
			//"order by 1, 7, 3, 4, 5");
			if (bUseConcept)
				sSql.append("order by 1, 7, 3, 4, 5 desc");
			else
				sSql.append("order by 1, 7, 4, 5 desc, 6");
			
			return sSql.toString();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public static String getResultSetFromI2B2Xml(String conceptXml,Connection oConnection) throws Exception {
		return getResultSetFromI2B2Xml(conceptXml, 52800, 52850, false, oConnection);
	}
	
	public static String getResultSetFromI2B2Xml(String conceptXml, int minPatientNum, int maxPatientNum, boolean bDisplayAll, Connection oConnection) throws Exception {
		return getResultSetFromI2B2Xml(conceptXml, minPatientNum, maxPatientNum, bDisplayAll, oConnection, true, true);
	}
	
	public static String getResultSetFromI2B2Xml(String conceptXml, int minPatientNum, int maxPatientNum, boolean bDisplayAll, 
			Connection oConnection, boolean writeFile, boolean displayDemographics) /*throws Exception*/ {
		//Fix upper lowercase with birn and postgres
		if (System.getProperty("applicationName").equals("BIRN"))
		{
			
			ss_patient_num = "PATIENT_NUM".toLowerCase();
			ss_concept_cd = "CONCEPT_CD".toLowerCase();
			ss_q_name_char = "Q_NAME_CHAR".toLowerCase();
			ss_start_date = "START_DATE".toLowerCase();
			ss_end_date = "END_DATE".toLowerCase();
			ss_inout_cd = "INOUT_CD".toLowerCase();
			ss_color_cd = "CONCEPT_COLOR".toLowerCase();
			ss_height_cd = "CONCEPT_HEIGHT".toLowerCase();
			ss_value_cd = "CONCEPT_VALUE".toLowerCase();
		}
		
		String sSQL = null;
		ResultSet oRs = null;
		ResultSetBuilder oResultSetBuilder = null;
		Document doc = null;
		//Connection oConnection = null;
		ArrayList conceptOrder = new ArrayList();
		//int maxLineCount = 10000000;
		int maxLineCount = 0; //zero turns off check for maximum count of lines 
		StringBuilder resultFile = new StringBuilder();
		Hashtable demographicsArray = new Hashtable();
			
		/*if ((maxPatientNum>minPatientNum)&&((maxPatientNum-minPatientNum)<100))
		 sSQL = buildQueryFromI2B2Xml(conceptXml, minPatientNum, maxPatientNum);
		 else
		 sSQL = buildQueryFromI2B2Xml_v2(conceptXml, minPatientNum, maxPatientNum);*/
		
		//sSQL = buildQueryFromI2B2Xml(conceptXml, minPatientNum, maxPatientNum, schemaName);
		sSQL = buildQueryFromI2B2Xml(conceptXml, minPatientNum, maxPatientNum, System.getProperty("datamartDatabase"), conceptOrder);
		//conceptOrder.add(new String("Circular system"));
		//conceptOrder.add(new String("In Patient"));
		
		//sSQL = "select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/"
 	/*+"o.patient_num PATIENT_NUM, "
	+"c.Q_NAME_CHAR,"
	+"o.CONCEPT_CD,"
	+"o.ENCOUNTER_NUM,"
	+"o.start_date START_DATE,"
	+"o.end_date END_DATE,"
	+"c.AN_ORDER,"
	+"c.CONCEPT_COLOR,"
	+"c.CONCEPT_HEIGHT,"
	+"'visit_dimension' TABLE_NAME,"
	+"'' CONCEPT_VALUE "
	+"from asthma.observation_fact o, "
	+"(select 	'Acute Rheumatic fever' as Q_NAME_CHAR, "
	+"concept_cd as CONCEPT_CD, "
	+"0 as ENCOUNTER_NUM,"
	+"1 as AN_ORDER, "
	+"'navyblue' as CONCEPT_COLOR, "
	+"'Medium' as CONCEPT_HEIGHT "
	+"from asthma.concept_dimension "
	+"where concept_path like '\\RPDR\\Diagnoses\\Circulatory system (390-459)\\%') c "
	+"where o.patient_num between 800 and 900 and o.concept_cd = c.concept_cd "
	+"union all "*/
	//+"select /*+ index (o OBFACT_PATCON_SDED_NVTV_IDX)*/ "
	/*+"o.patient_num PATIENT_NUM, "
	+"c.Q_NAME_CHAR,"
	+"o.CONCEPT_CD,"
	+"o.ENCOUNTER_NUM,"
	+"o.start_date START_DATE,"
	+"o.end_date END_DATE,"
	+"c.AN_ORDER,"
	+"c.CONCEPT_COLOR,"
	+"c.CONCEPT_HEIGHT,"
	+"'visit_dimension' TABLE_NAME,"
	+"'' CONCEPT_VALUE "
	+"from asthma.observation_fact o," 
	+"(select 	'Inpatient' Q_NAME_CHAR," 
	+"ENCOUNTER_NUM as ENCOUNTER_NUM,"
	+"'fake' as CONCEPT_CD," 
	+"2 as AN_ORDER," 
	+"'navyblue' as CONCEPT_COLOR," 
	+"'Medium' as CONCEPT_HEIGHT " 
	+"from asthma.visit_dimension " 
	+"where inout_cd = 'I'" 
	+") c "
	+"where o.patient_num between 800 and 900 "
	+"and o.ENCOUNTER_NUM = c.ENCOUNTER_NUM order by 1, 7, 5, 6 desc";*/
	
		try {
			System.out.println("before query: " + new Date());
			System.out.println("Query: " + sSQL);
			
			try
			{
				oRs = doQuery(oConnection, sSQL);
			}
			catch (java.lang.OutOfMemoryError e){
				System.out.println("In resultset builder 1: " + e.getMessage());
				closeConnection(oConnection);
				return "memory error";
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				closeConnection(oConnection);
				return "error";
				//throw e;
			}
			System.out.println("after query: " + new Date());
			// the ResultSetBuilder is part of contrib jdom
			oResultSetBuilder = new ResultSetBuilder(oRs);
			// set the root name
			oResultSetBuilder.setRootName("PatientData");
			// set the tag for the rows
			oResultSetBuilder.setRowName("observation_fact");
			// set the q_name_char as an attibute for the observation_fact
			oResultSetBuilder.setAsAttribute(ss_q_name_char);
			// build the document
			try
			{
				doc = oResultSetBuilder.build();
			}
			catch (java.lang.OutOfMemoryError e){
				System.out.println("In resultset builder 2: " + e.getMessage());
				closeConnection(oConnection);
				return "memory error";
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				closeConnection(oConnection);
				return "error";
			}
			
			if(displayDemographics) {
				System.out.println("before patient demographysics query: " + new Date());
				String schemaName = System.getProperty("datamartDatabase");
				sSQL = new String("select PATIENT_NUM, SEX_CD, AGE_IN_YEARS_NUM, RACE_CD, VITAL_STATUS_CD "
						+"from "+ schemaName+".PATIENT_DIMENSION "
						+"where PATIENT_NUM between "+minPatientNum+" and "+maxPatientNum);
				System.out.println("Query: " + sSQL);
				
				try
				{
					oRs = doQuery(oConnection, sSQL);
				}
				catch (java.lang.OutOfMemoryError e){
					System.out.println("In resultset builder 3: " + e.getMessage());
					closeConnection(oConnection);
					return "memory error";
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
					closeConnection(oConnection);
					return "error";
				}
				System.out.println("after patient demographysics query: " + new Date());
				
				PatientDemographics aDemographysic = null;
				oRs.next();
				while(!oRs.isAfterLast()) {
					aDemographysic = new PatientDemographics();
					aDemographysic.patientNumber(oRs.getString(1));
					aDemographysic.gender(oRs.getString(2));
					aDemographysic.age(oRs.getString(3));
					aDemographysic.race(oRs.getString(4));
					aDemographysic.vitalStatus(oRs.getString(5));
					
					demographicsArray.put(new Integer(aDemographysic.patientNumber()), 
							aDemographysic);
					oRs.next();
				}
			}
			closeConnection(oConnection);
		}
		catch (java.lang.OutOfMemoryError e){
			System.out.println("In resultset builder 4: " + e.getMessage());
			closeConnection(oConnection);
			return "memory error";
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			closeConnection(oConnection);
			return "error";
		}
		
		try {
			System.gc();
			if (false) {
				// the XMLOutputter may be used to write out the XML
				org.jdom.output.XMLOutputter outp = new org.jdom.output.XMLOutputter();
				// set the format to look good
				outp.setFormat(org.jdom.output.Format.getPrettyFormat());
				String sXmli2b2 = outp.outputString(doc);
				outp.output(doc, System.out);
			}
			// get the root
			Element root = doc.getRootElement();
			// get the children from the i2b2 document
			java.util.List allChildren = root.getChildren();
			int iNumberOfChildren = allChildren.size();
			// set up the variables for the loop
			String sPatient_num = null;
			String sConcept_cd = null;
			String sOldPatient_num = null;
			String sOldConcept_cd = null;
			String sStart_date = null;
			String sOldStart_date = null;
			String sEnd_date = null;
			String sInout_cd = null;
			String sDeath_date = null;
			String sColor = null;
			String sHeight = null;
			String sValue = null;
			String sTablename = null;
			int patientNum = 0;
			Date oDate;
			// open the file and prepare for writing
			/*String i2b2File = System.getProperty("user.dir")+'/'+ "i2b2xml.lld";
			 File oDelete = new File(i2b2File);
			 if (oDelete != null) oDelete.delete();
			 RandomAccessFile f = new RandomAccessFile(i2b2File,"rw");*/
			// write the header
			//Lib.append(f,GetTimelineHeader());
			resultFile.append(GetTimelineHeader());
			boolean bOverMax = false;
			int conceptCount = 0;
			int patientCount = minPatientNum;
			StringBuilder patientRecord = new StringBuilder();
			for (int i=0; i<iNumberOfChildren; i++) {			
				if ((maxLineCount>0)&&(i>maxLineCount))
				{
					bOverMax = true;
					break;
				}
				
				// get a child
				Element oChild = (Element)allChildren.get(i);
				sPatient_num = oChild.getChild(ss_patient_num).getText();
				if (!sPatient_num.equals(sOldPatient_num)) {
					//System.out.println(sPatient_num);
					
					if ((i>0)&&
							((bDisplayAll)&&(conceptCount<(conceptOrder.size()))))
					{
						while ((bDisplayAll)&&
								(conceptOrder!=null)&&
								(conceptCount<conceptOrder.size())&&
								(!sConcept_cd.equals(conceptOrder.get(conceptCount))))
						{
							patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
							patientRecord.append(getTimelineEmptyDateString());
							conceptCount++;
						}
					}
					
					if (patientRecord.length()>0)
					{
						//Lib.append(f, patientRecord.toString());
						resultFile.append(patientRecord.toString());
						patientRecord = new StringBuilder();
						patientCount++;
					}
					
					PatientDemographics record = new PatientDemographics();
					
					try{
						patientNum = Integer.parseInt(sPatient_num);
						
						
						if ((patientCount>=0)&&(patientNum>patientCount))
						{
							while ((bDisplayAll)&&(patientCount<patientNum))
							{
								if(displayDemographics) {
									record = (PatientDemographics) demographicsArray.get(new Integer(patientCount)); 
								}
								
								if (System.getProperty("applicationName").equals("BIRN"))
								{
									patientRecord.append(getTimelinePatientString(Integer.toString(patientCount), record, oChild.getChild(ss_start_date).getText()));								
								} else {
									patientRecord.append(getTimelinePatientString(Integer.toString(patientCount), record));								
								}
								//}
								conceptCount = 0;
								while ((conceptOrder!=null)&&
										(conceptCount<conceptOrder.size()))
								{
									patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
									patientRecord.append(getTimelineEmptyDateString());
									conceptCount++;
								}
								//Lib.append(f, patientRecord.toString());
								resultFile.append(patientRecord.toString());
								patientRecord = new StringBuilder();
								patientCount++;
							}
						}
					}
					catch (java.lang.OutOfMemoryError e){
						System.out.println("In resultset builder 5: " + e.getMessage());
						//closeConnection(oConnection);
						return "memory error";
					}
					catch (Exception e) {
						System.out.println(e.getMessage());
						//closeConnection(oConnection);
						return "error";
						//throw e;
					}
					//System.out.print(getTimelinePatientString(sPatient_num));
					//Skip for BIRN
					if(displayDemographics) {
						record = (PatientDemographics) demographicsArray.get(new Integer(sPatient_num)); 
						patientRecord.append(getTimelinePatientString(sPatient_num, record));
					}
					else
					{
						patientRecord.append(getTimelinePatientString(sPatient_num, record,oChild.getChild(ss_start_date).getText()));
					} 

					//patientRecord.append(getTimelinePatientString(sPatient_num, record));
					
					conceptCount = 0;
					sOldConcept_cd = null;
					sOldStart_date = null;
				}
				sOldPatient_num = sPatient_num;
				if (bUseConcept) {
					sConcept_cd = oChild.getChild(ss_concept_cd).getText();
				}
				else {
					sConcept_cd = oChild.getAttributeValue(ss_q_name_char);
				}
				
				if (!sConcept_cd.equals(sOldConcept_cd)) {
					
					while ((bDisplayAll)&&
							(conceptOrder!=null)&&
							(conceptCount<conceptOrder.size())&&
							(!sConcept_cd.equals(conceptOrder.get(conceptCount))))
					{
						patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
						patientRecord.append(getTimelineEmptyDateString());
						conceptCount++;
					}
					
					//int iNumConceptObservations = getNumConceptObservations(allChildren,i);
					//int iNumConceptObservations = getNumConceptObservationsRollingupStartDateEx(allChildren,i);
					int iNumConceptObservations = getNumConceptObservationsRollingupStartDate(allChildren,i);
					//System.out.println(" "+sConcept_cd + " " + iNumConceptObservations);
					//System.out.print(getTimelineConceptString(sConcept_cd,iNumConceptObservations));
					patientRecord.append(getTimelineConceptString(sConcept_cd,iNumConceptObservations));
					sOldStart_date = null;
					conceptCount++;
				}
				sOldConcept_cd = sConcept_cd;
				sStart_date = oChild.getChild(ss_start_date).getText();	
				//if (!sStart_date.equals(sOldStart_date)) {
				//if (!sStart_date.equals(null)) {
				if ((!sStart_date.equals(null))&&
						((sOldStart_date==null)||(!sStart_date.equals(sOldStart_date)))) {
					sEnd_date = oChild.getChild(ss_end_date).getText();
					if ((sEnd_date==null)||(sEnd_date.trim().length()==0)) sEnd_date = sStart_date;
					//sInout_cd = oChild.getChild(ss_inout_cd).getText();
					sInout_cd = "";
					sColor = oChild.getChild(ss_color_cd).getText();
					sHeight = oChild.getChild(ss_height_cd).getText();
					sValue = oChild.getChild(ss_value_cd).getText();
					sTablename = oChild.getChild(ss_table_name).getText();
					String prefix = "C";
					if(sTablename.equalsIgnoreCase("visit_dimension")) {
						prefix = "E";
					}
					else if(sTablename.equalsIgnoreCase("provider_dimension")) {
						prefix = "P";
					}
					
					if ((sValue==null)||(sValue.length()==0)) {	
						sValue = prefix+" = ::"+sConcept_cd+"::"+
						"$$"+oChild.getChild(ss_patient_num).getText()+
						"$$"+oChild.getChild(ss_concept_cd).getText() +
						"$$"+ChangeRsDateFull(sStart_date) ;//+"::";
					}
					else {
						sValue = prefix+" Value = " + "::"+sConcept_cd+": "+sValue+"::"+
						"$$"+oChild.getChild(ss_patient_num).getText()+
						"$$"+oChild.getChild(ss_concept_cd).getText() +
						"$$"+ChangeRsDateFull(sStart_date) ;//+"::";
					}
					
					//System.out.println("   "+ ChangeRsDate(sStart_date) + " -> " + ChangeRsDate(sEnd_date));
					//System.out.print(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));
					//System.out.print(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date),sConcept_cd));
					if (sInout_cd.equalsIgnoreCase("I")) {
						if (sColor!=null)
							patientRecord.append(getTimelineDateStringSpecial(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sColor));						
						else
							patientRecord.append(getTimelineDateStringSpecial(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));						
					}
					else if (sInout_cd.equalsIgnoreCase("E")) {
						if (sColor!=null)
							patientRecord.append(getTimelineDateStringEncounter(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sColor));
						else
							patientRecord.append(getTimelineDateStringEncounter(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));
					}
					else if (sInout_cd.equalsIgnoreCase("D")) {
						if (sStart_date.length() == 0 ) {
							if (sColor!=null)
								patientRecord.append(getTimelineDateStringEncounter("today","today", sColor));							
							else
								patientRecord.append(getTimelineDateStringEncounter("today","today"));							
						}
						else {
							if (sColor!=null)
								patientRecord.append(getTimelineDateStringDeath(ChangeRsDate(sStart_date),"today", sColor));							
							else 
								patientRecord.append(getTimelineDateStringDeath(ChangeRsDate(sStart_date),"today", sColor));							
						}
					}
					else {
						if (sConcept_cd.equals("Death"))
						{
							if (sStart_date.length() == 0 )
							{
								sStart_date = "today";
								sColor = "lightbrown";
							}
							sEnd_date = "today";
						}
						if (sColor!=null)
						{
							if (sConcept_cd.equalsIgnoreCase("EGFR"))
								patientRecord.append(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sColor, "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=1956"));						
							else
								patientRecord.append(getTimelineDateStringHeight(ChangeRsDate(sStart_date),
										ChangeRsDate(sEnd_date), sColor, sHeight, sValue));						
						}
						else
							patientRecord.append(getTimelineDateStringHeight(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sHeight));						
					}
					//Lib.append(f,getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date),sConcept_cd));					
				}
				sOldStart_date = sStart_date;
			}
			
			
			if (!bOverMax)
			{
				if ((bDisplayAll)&&(conceptCount<(conceptOrder.size())))
				{
					while ((bDisplayAll)&&
							(conceptOrder!=null)&&
							(conceptCount<conceptOrder.size()))
					{
						patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
						patientRecord.append(getTimelineEmptyDateString());
						conceptCount++;
					}
				}
				
				//Lib.append(f, patientRecord.toString());
				resultFile.append(patientRecord.toString());
				patientRecord = new StringBuilder();
				patientCount++;
				
				if ((bDisplayAll)&&(patientCount<=maxPatientNum))
				{
					
					while ((bDisplayAll)&&(patientCount<=maxPatientNum))
					{	
						PatientDemographics record = null;
						if(displayDemographics) {
							record = (PatientDemographics)demographicsArray.get(new Integer(patientCount)); 
						}
						else {
							record = new PatientDemographics();
						}
						patientRecord.append(getTimelinePatientString(Integer.toString(patientCount), record));								
						
						conceptCount = 0;
						while ((conceptOrder!=null)&&
								(conceptCount<conceptOrder.size()))
						{
							patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
							patientRecord.append(getTimelineEmptyDateString());
							conceptCount++;
						}
						//Lib.append(f, patientRecord.toString());
						resultFile.append(patientRecord.toString());
						patientRecord = new StringBuilder();
						patientCount++;
					}
				}
			}
			
			// write the footor
			//Lib.append(f,GetTimelineFooter());
			resultFile.append(GetTimelineFooter());
			// not sure if I have to wait unitl here to close
			//f.close();
			System.out.println(" Total Count " + iNumberOfChildren);
			
			//org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
			//builder.setValidation(false);
			//builder.setIgnoringElementContentWhitespace(true);
			// This builds: <root>This is the root</root>
			//Document doc = new Document();
			//Element e = new Element("root");
			//e.setText("This is the root");
			//doc.addContent(e);
			//Document doc = builder.build(f);
			//System.out.println(doc.toString());
			
			if (writeFile)
			{
				String i2b2File = System.getProperty("user.dir")+'/'+ "i2b2xml.lld";
				File oDelete = new File(i2b2File);
				if (oDelete != null) oDelete.delete();
				RandomAccessFile f = new RandomAccessFile(i2b2File,"rw");
				Lib.append(f, resultFile.toString());
				f.close();
			}
			
			if (bOverMax)
			{
				System.out.println("reached maximum at " + new Date());
				return "overmaximum";
			}
		}
		catch (java.lang.OutOfMemoryError e){
			System.out.println("In resultset builder 6: " + e.getMessage());
			closeConnection(oConnection);
			return "memory error";
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			closeConnection(oConnection);
			return "error";
			//throw e;
		}
		
		//System.out.println(sI2b2XML);
		System.out.println("done at " + new Date());
		return resultFile.toString();
	}
	
	public static String getResultSetFromI2B2Xml(String conceptXml, int[] patientIds, boolean bDisplayAll, 
			Connection oConnection, boolean writeFile, boolean displayDemographics) throws Exception {
		String sSQL = null;
		ResultSet oRs = null;
		ResultSetBuilder oResultSetBuilder = null;
		Document doc = null;
		//Connection oConnection = null;
		ArrayList conceptOrder = new ArrayList();
		//int maxLineCount = 10000000;
		int maxLineCount = 0; //zero turns off check for maximum count of lines 
		StringBuilder resultFile = new StringBuilder();
		Hashtable demographicsArray = new Hashtable();
		
		
		/*if ((maxPatientNum>minPatientNum)&&((maxPatientNum-minPatientNum)<100))
		 sSQL = buildQueryFromI2B2Xml(conceptXml, minPatientNum, maxPatientNum);
		 else
		 sSQL = buildQueryFromI2B2Xml_v2(conceptXml, minPatientNum, maxPatientNum);*/
		
		//sSQL = buildQueryFromI2B2Xml(conceptXml, minPatientNum, maxPatientNum, schemaName);
		sSQL = buildQueryFromI2B2Xml(conceptXml, patientIds, System.getProperty("datamartDatabase"), conceptOrder);
		
		try {
			System.out.println("before query: " + new Date());
			System.out.println("Query: " + sSQL);
			
			try
			{
				oRs = doQuery(oConnection, sSQL);
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				return null;
			}
			System.out.println("after query: " + new Date());
			// the ResultSetBuilder is part of contrib jdom
			oResultSetBuilder = new ResultSetBuilder(oRs);
			// set the root name
			oResultSetBuilder.setRootName("PatientData");
			// set the tag for the rows
			oResultSetBuilder.setRowName("observation_fact");
			// set the q_name_char as an attibute for the observation_fact
			oResultSetBuilder.setAsAttribute(ss_q_name_char);
			// build the document
			try
			{
				doc = oResultSetBuilder.build();
			}
			catch (java.lang.OutOfMemoryError e){
				System.out.println("In resultset builder 7: " + e.getMessage());
				closeConnection(oConnection);
				return "memory error";
			}
			
			System.out.println("before patient demographysics query: " + new Date());
			
			StringBuffer patientWhereClause = new StringBuffer(" (patient_num = ");
			for(int i=0; i<patientIds.length; i++) {
				if(i>0) {
					patientWhereClause.append(" or patient_num = ");
				}
				patientWhereClause.append(patientIds[i]);
			}
			patientWhereClause.append(")");
			
			String schemaName = System.getProperty("datamartDatabase");
			sSQL = new String("select PATIENT_NUM, SEX_CD, AGE_IN_YEARS_NUM, RACE_CD, VITAL_STATUS_CD "
					+"from "+ schemaName+".PATIENT_DIMENSION "
					+"where"+patientWhereClause.toString()/*PATIENT_NUM between "+minPatientNum+" and "+maxPatientNum*/);
			System.out.println("Query: " + sSQL);
			
			try
			{
				oRs = doQuery(oConnection, sSQL);
			}
			catch (java.lang.OutOfMemoryError e){
				System.out.println("In resultset builder 8: " + e.getMessage());
				closeConnection(oConnection);
				return "memory error";
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				closeConnection(oConnection);
				return "error";
			}
			System.out.println("after patient demographysics query: " + new Date());
			
			try {
				if(displayDemographics) {
					PatientDemographics aDemographysic = null;
					oRs.next();
					while(!oRs.isAfterLast()) {
						aDemographysic = new PatientDemographics();
						aDemographysic.patientNumber(oRs.getString(1));
						aDemographysic.gender(oRs.getString(2));
						aDemographysic.age(oRs.getString(3));
						aDemographysic.race(oRs.getString(4));
						aDemographysic.vitalStatus(oRs.getString(5));
						
						demographicsArray.put(new Integer(aDemographysic.patientNumber()), 
								aDemographysic);
						oRs.next();
					}
				}				
				closeConnection(oConnection);
			}
			catch (java.lang.OutOfMemoryError e){
				System.out.println("In resultset builder 9: " + e.getMessage());
				closeConnection(oConnection);
				return "memory error";
			}
		}
		catch (java.lang.OutOfMemoryError e){
			System.out.println("In resultset builder 10: " + e.getMessage());
			//closeConnection(oConnection);
			return "memory error";
		}
		catch (Exception e) {
			System.out.println("In resultset builder 11: " + e.getMessage());
			//closeConnection(oConnection);
			//throw e;
			return "error";
		}
		
		///testing
		//PatientDemographics dm = (PatientDemographics)
		//	demographicsArray.get(new Integer(maxPatientNum));
		//System.out.println("Patient demographics: "+dm.patientNumber()+" "+dm.gender()
		//		+" "+dm.age()+" "+dm.race()+" "+dm.vitalStatus());
		///////
		
		try {
			System.gc();
			if (false) {
				// the XMLOutputter may be used to write out the XML
				org.jdom.output.XMLOutputter outp = new org.jdom.output.XMLOutputter();
				// set the format to look good
				outp.setFormat(org.jdom.output.Format.getPrettyFormat());
				String sXmli2b2 = outp.outputString(doc);
				outp.output(doc, System.out);
			}
			// get the root
			Element root = doc.getRootElement();
			// get the children from the i2b2 document
			java.util.List allChildren = root.getChildren();
			int iNumberOfChildren = allChildren.size();
			
			/*ArrayList<Integer> writePatinetArray = new ArrayList<Integer>(); 
			 boolean found = false;
			 for(int m=0; m<patientIds.length; m++) {
			 int patientNum = patientIds[m];
			 for(int n=0; n<iNumberOfChildren; n++) {
			 Element oChild = (Element)allChildren.get(n);
			 String strPatientNum = oChild.getChild(ss_patient_num).getText();
			 if(new Integer(strPatientNum).intValue() == patientNum) {
			 found = true;
			 break;
			 }					
			 }
			 if(!found) {
			 writePatinetArray.add(new Integer(patientNum));
			 }
			 found = false;
			 }*/
			
			// set up the variables for the loop
			String sPatient_num = null;
			String sConcept_cd = null;
			String sOldPatient_num = null;
			String sOldConcept_cd = null;
			String sStart_date = null;
			String sOldStart_date = null;
			String sEnd_date = null;
			String sInout_cd = null;
			String sDeath_date = null;
			String sColor = null;
			String sHeight = null;
			String sValue = null;
			String sTablename = null;
			int patientNum = 0;
			Date oDate;
			// open the file and prepare for writing
			/*String i2b2File = System.getProperty("user.dir")+'/'+ "i2b2xml.lld";
			 File oDelete = new File(i2b2File);
			 if (oDelete != null) oDelete.delete();
			 RandomAccessFile f = new RandomAccessFile(i2b2File,"rw");*/
			// write the header
			//Lib.append(f,GetTimelineHeader());
			resultFile.append(GetTimelineHeader());
			boolean bOverMax = false;
			int conceptCount = 0;
			int patientCount = patientIds[0];
			int maxPatientNum = patientIds[patientIds.length-1];
			StringBuilder patientRecord = new StringBuilder();
			int currentPatientCount = 0;
			
			for (int i=0; i<iNumberOfChildren; i++) {			
				if ((maxLineCount>0)&&(i>maxLineCount))
				{
					bOverMax = true;
					break;
				}
				
				// get a child
				Element oChild = (Element)allChildren.get(i);
				sPatient_num = oChild.getChild(ss_patient_num).getText();
				if (!sPatient_num.equals(sOldPatient_num)) {
					//System.out.println(sPatient_num);
					
					if ((i>0)&&
							((bDisplayAll)&&(conceptCount<(conceptOrder.size()))))
					{
						while ((bDisplayAll)&&
								(conceptOrder!=null)&&
								(conceptCount<conceptOrder.size())&&
								(!sConcept_cd.equals(conceptOrder.get(conceptCount))))
						{
							patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
							patientRecord.append(getTimelineEmptyDateString());
							conceptCount++;
						}
					}
					
					if (patientRecord.length()>0)
					{
						//Lib.append(f, patientRecord.toString());
						resultFile.append(patientRecord.toString());
						patientRecord = new StringBuilder();
						currentPatientCount++;
						patientCount = patientIds[currentPatientCount];
					}
					
					PatientDemographics record = new PatientDemographics();
					
					try{
						patientNum = Integer.parseInt(sPatient_num);
						
						
						if ((patientCount>=0)&&(patientNum>patientCount))
						{
							while ((bDisplayAll)&&(patientCount<patientNum))
							{
								if(displayDemographics) {
									record = (PatientDemographics) demographicsArray.get(new Integer(patientCount)); 
								}
								patientRecord.append(getTimelinePatientString(Integer.toString(patientCount), record));								
								conceptCount = 0;
								while ((conceptOrder!=null)&&
										(conceptCount<conceptOrder.size()))
								{
									patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
									patientRecord.append(getTimelineEmptyDateString());
									conceptCount++;
								}
								//Lib.append(f, patientRecord.toString());
								resultFile.append(patientRecord.toString());
								patientRecord = new StringBuilder();
								currentPatientCount++;
								patientCount = patientIds[currentPatientCount];
							}
						}
					}
					catch (java.lang.OutOfMemoryError e){
						System.out.println("In resultset builder 12: " + e.getMessage());
						//closeConnection(oConnection);
						return "memory error";
					}
					catch (Exception e) {
						System.out.println("In resultset builder 13: " + e.getMessage());
						//closeConnection(oConnection);
						//throw e;
						return "error";
					}
					//System.out.print(getTimelinePatientString(sPatient_num));
					if(displayDemographics) {
						record = (PatientDemographics) demographicsArray.get(new Integer(sPatient_num)); 
					}
					patientRecord.append(getTimelinePatientString(sPatient_num, record));
					
					conceptCount = 0;
					sOldConcept_cd = null;
					sOldStart_date = null;
				}
				sOldPatient_num = sPatient_num;
				if (bUseConcept) {
					sConcept_cd = oChild.getChild(ss_concept_cd).getText();
				}
				else {
					sConcept_cd = oChild.getAttributeValue(ss_q_name_char);
				}
				
				if (!sConcept_cd.equals(sOldConcept_cd)) {
					
					while ((bDisplayAll)&&
							(conceptOrder!=null)&&
							(conceptCount<conceptOrder.size())&&
							(!sConcept_cd.equals(conceptOrder.get(conceptCount))))
					{
						patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
						patientRecord.append(getTimelineEmptyDateString());
						conceptCount++;
					}
					
					//int iNumConceptObservations = getNumConceptObservations(allChildren,i);
					//int iNumConceptObservations = getNumConceptObservationsRollingupStartDateEx(allChildren,i);
					int iNumConceptObservations = getNumConceptObservationsRollingupStartDate(allChildren,i);
					//System.out.println(" "+sConcept_cd + " " + iNumConceptObservations);
					//System.out.print(getTimelineConceptString(sConcept_cd,iNumConceptObservations));
					patientRecord.append(getTimelineConceptString(sConcept_cd,iNumConceptObservations));
					sOldStart_date = null;
					conceptCount++;
				}
				sOldConcept_cd = sConcept_cd;
				sStart_date = oChild.getChild(ss_start_date).getText();	
				//if (!sStart_date.equals(sOldStart_date)) {
				//if (!sStart_date.equals(null)) {
				if ((!sStart_date.equals(null))&&
						((sOldStart_date==null)||(!sStart_date.equals(sOldStart_date)))) {
					sEnd_date = oChild.getChild(ss_end_date).getText();
					if ((sEnd_date==null)||(sEnd_date.trim().length()==0)) sEnd_date = sStart_date;
					//sInout_cd = oChild.getChild(ss_inout_cd).getText();
					sInout_cd = "";
					sColor = oChild.getChild(ss_color_cd).getText();
					sHeight = oChild.getChild(ss_height_cd).getText();
					sValue = oChild.getChild(ss_value_cd).getText();
					sTablename = oChild.getChild(ss_table_name).getText();
					String prefix = "C";
					if(sTablename.equalsIgnoreCase("visit_dimension")) {
						prefix = "E";
					}
					else if(sTablename.equalsIgnoreCase("provider_dimension")) {
						prefix = "P";
					}
					
					if ((sValue==null)||(sValue.length()==0)) {	
						sValue = prefix+" = ::"+sConcept_cd+"::"+
						"$$"+oChild.getChild(ss_patient_num).getText()+
						"$$"+oChild.getChild(ss_concept_cd).getText() +
						"$$"+ChangeRsDateFull(sStart_date) ;//+"::";
					}
					else {
						sValue = prefix+" Value = " + "::"+sConcept_cd+": "+sValue+"::"+
						"$$"+oChild.getChild(ss_patient_num).getText()+
						"$$"+oChild.getChild(ss_concept_cd).getText() +
						"$$"+ChangeRsDateFull(sStart_date) ;//+"::";
					}
					
					//System.out.println("   "+ ChangeRsDate(sStart_date) + " -> " + ChangeRsDate(sEnd_date));
					//System.out.print(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));
					//System.out.print(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date),sConcept_cd));
					if (sInout_cd.equalsIgnoreCase("I")) {
						if (sColor!=null)
							patientRecord.append(getTimelineDateStringSpecial(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sColor));						
						else
							patientRecord.append(getTimelineDateStringSpecial(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));						
					}
					else if (sInout_cd.equalsIgnoreCase("E")) {
						if (sColor!=null)
							patientRecord.append(getTimelineDateStringEncounter(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sColor));
						else
							patientRecord.append(getTimelineDateStringEncounter(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));
					}
					else if (sInout_cd.equalsIgnoreCase("D")) {
						if (sStart_date.length() == 0 ) {
							if (sColor!=null)
								patientRecord.append(getTimelineDateStringEncounter("today","today", sColor));							
							else
								patientRecord.append(getTimelineDateStringEncounter("today","today"));							
						}
						else {
							if (sColor!=null)
								patientRecord.append(getTimelineDateStringDeath(ChangeRsDate(sStart_date),"today", sColor));							
							else 
								patientRecord.append(getTimelineDateStringDeath(ChangeRsDate(sStart_date),"today", sColor));							
						}
					}
					else {
						if (sConcept_cd.equals("Death"))
						{
							if (sStart_date.length() == 0 )
							{
								sStart_date = "today";
								sColor = "lightbrown";
							}
							sEnd_date = "today";
						}
						if (sColor!=null)
						{
							if (sConcept_cd.equalsIgnoreCase("EGFR"))
								patientRecord.append(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sColor, "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=1956"));						
							else
								patientRecord.append(getTimelineDateStringHeight(ChangeRsDate(sStart_date),
										ChangeRsDate(sEnd_date), sColor, sHeight, sValue));						
						}
						else
							patientRecord.append(getTimelineDateStringHeight(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sHeight));						
					}
					//Lib.append(f,getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date),sConcept_cd));					
				}
				sOldStart_date = sStart_date;
			}
			
			
			if (!bOverMax)
			{
				if ((bDisplayAll)&&(conceptCount<(conceptOrder.size())))
				{
					while ((bDisplayAll)&&
							(conceptOrder!=null)&&
							(conceptCount<conceptOrder.size()))
					{
						patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
						patientRecord.append(getTimelineEmptyDateString());
						conceptCount++;
					}
				}
				
				//Lib.append(f, patientRecord.toString());
				resultFile.append(patientRecord.toString());
				patientRecord = new StringBuilder();
				currentPatientCount++;
				
				if(currentPatientCount<patientIds.length) {
					patientCount = patientIds[currentPatientCount];
					if ((bDisplayAll)&&(patientCount<=maxPatientNum))
					{
						
						while ((bDisplayAll)&&(patientCount<=maxPatientNum))
						{	
							PatientDemographics record = null;
							if(displayDemographics) {
								record = (PatientDemographics)demographicsArray.get(new Integer(patientCount)); 
							}
							else {
								record = new PatientDemographics();
							}
							patientRecord.append(getTimelinePatientString(Integer.toString(patientCount), record));								
							
							conceptCount = 0;
							while ((conceptOrder!=null)&&
									(conceptCount<conceptOrder.size()))
							{
								patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
								patientRecord.append(getTimelineEmptyDateString());
								conceptCount++;
							}
							//Lib.append(f, patientRecord.toString());
							resultFile.append(patientRecord.toString());
							patientRecord = new StringBuilder();
							currentPatientCount++;
							if(currentPatientCount<patientIds.length) {
								patientCount = patientIds[currentPatientCount];
							}
							else {
								patientCount++;
							}
						}
					}
				}
			}
			
			// write the footor
			//Lib.append(f,GetTimelineFooter());
			resultFile.append(GetTimelineFooter());
			// not sure if I have to wait unitl here to close
			//f.close();
			System.out.println(" Total Count " + iNumberOfChildren);
			
			//org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
			//builder.setValidation(false);
			//builder.setIgnoringElementContentWhitespace(true);
			// This builds: <root>This is the root</root>
			//Document doc = new Document();
			//Element e = new Element("root");
			//e.setText("This is the root");
			//doc.addContent(e);
			//Document doc = builder.build(f);
			//System.out.println(doc.toString());
			
			if (writeFile)
			{
				String i2b2File = System.getProperty("user.dir")+'/'+ "i2b2xml.lld";
				File oDelete = new File(i2b2File);
				if (oDelete != null) oDelete.delete();
				RandomAccessFile f = new RandomAccessFile(i2b2File,"rw");
				Lib.append(f, resultFile.toString());
				f.close();
			}
			
			if (bOverMax)
			{
				System.out.println("reached maximum at " + new Date());
				return "overmaximum";
			}
		}
		catch (java.lang.OutOfMemoryError e){
			System.out.println("In resultset builder 14: " + e.getMessage());
			//closeConnection(oConnection);
			return "memory error";
		}
		catch (Exception e) {
			System.out.println("In resultset builder 15: " + e.getMessage());
			//closeConnection(oConnection);
			//throw e;
			return "error";
		}
		
		//System.out.println(sI2b2XML);
		System.out.println("done at " + new Date());
		return resultFile.toString();
	}
	
	public static String getResultSetFromI2B2Xml(String conceptXml, String patientSetId, int minPatientNum, 
			int maxPatientNum, boolean bDisplayAll, Connection oConnection, boolean writeFile, 
			boolean displayDemographics) throws Exception {
		//Fix upper lowercase with birn and postgres
		if (System.getProperty("applicationName").equals("BIRN"))
		{
			ss_patient_num = "PATIENT_NUM".toLowerCase();
			ss_concept_cd = "CONCEPT_CD".toLowerCase();
			ss_q_name_char = "Q_NAME_CHAR".toLowerCase();
			ss_start_date = "START_DATE".toLowerCase();
			ss_end_date = "END_DATE".toLowerCase();
			ss_inout_cd = "INOUT_CD".toLowerCase();
			ss_color_cd = "CONCEPT_COLOR".toLowerCase();
			ss_height_cd = "CONCEPT_HEIGHT".toLowerCase();
			ss_value_cd = "CONCEPT_VALUE".toLowerCase();
		}
		
		String sSQL = null;
		ResultSet oRs = null;
		ResultSetBuilder oResultSetBuilder = null;
		Document doc = null;
		
		ArrayList conceptOrder = new ArrayList();
		int maxLineCount = 0; //zero turns off check for maximum count of lines 
		StringBuilder resultFile = new StringBuilder();
		ArrayList<PatientDemographics> demographicsArray = new ArrayList<PatientDemographics>();
		
		sSQL = buildQueryFromI2B2Xml(conceptXml, patientSetId, minPatientNum, 
				maxPatientNum, System.getProperty("datamartDatabase"), conceptOrder);
		
		try {
			System.out.println("before query: " + new Date());
			System.out.println("Query: " + sSQL);
			
			try
			{
				oRs = doQuery(oConnection, sSQL);
			}
			catch (java.lang.OutOfMemoryError e){
				System.out.println("In resultset builder 1: " + e.getMessage());
				closeConnection(oConnection);
				return "memory error";
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				closeConnection(oConnection);
				return "error";
			}
			System.out.println("after query: " + new Date());
			// the ResultSetBuilder is part of contrib jdom
			oResultSetBuilder = new ResultSetBuilder(oRs);
			// set the root name
			oResultSetBuilder.setRootName("PatientData");
			// set the tag for the rows
			oResultSetBuilder.setRowName("observation_fact");
			// set the q_name_char as an attibute for the observation_fact
			oResultSetBuilder.setAsAttribute(ss_q_name_char);
			// build the document
			try
			{
				doc = oResultSetBuilder.build();
			}
			catch (java.lang.OutOfMemoryError e){
				System.out.println("In resultset builder 2: " + e.getMessage());
				closeConnection(oConnection);
				return "memory error";
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				closeConnection(oConnection);
				return "error";
			}
			
			System.out.println("before patient demographysics query: " + new Date());
			String schemaName = System.getProperty("datamartDatabase");
			sSQL = new String("select PATIENT_NUM, SEX_CD, AGE_IN_YEARS_NUM, RACE_CD, VITAL_STATUS_CD "
					+"from "+ schemaName+".PATIENT_DIMENSION "
					+"where PATIENT_NUM in (select PATIENT_NUM from "+schemaName 
					+".QT_PATIENT_SET_COLLECTION where RESULT_INSTANCE_ID = "+patientSetId
					+" and SET_INDEX between " +minPatientNum +
					" and "+(minPatientNum+maxPatientNum)+") order by 1");
			System.out.println("Query: " + sSQL);
				
			try {
				oRs = doQuery(oConnection, sSQL);
			}
			catch (java.lang.OutOfMemoryError e) {
				System.out.println("In resultset builder 3: " + e.getMessage());
				closeConnection(oConnection);
				return "memory error";
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				closeConnection(oConnection);
				return "error";
			}
			System.out.println("after patient demographysics query: " + new Date());
				
			PatientDemographics aDemographysic = null;
			oRs.next();
			while(!oRs.isAfterLast()) {
				aDemographysic = new PatientDemographics();
				aDemographysic.patientNumber(oRs.getString(1));
				aDemographysic.gender(oRs.getString(2));
				aDemographysic.age(oRs.getString(3));
				aDemographysic.race(oRs.getString(4));
				aDemographysic.vitalStatus(oRs.getString(5));
					
				demographicsArray.add(aDemographysic);
				oRs.next();
			}
			closeConnection(oConnection);
		}
		catch (java.lang.OutOfMemoryError e){
			System.out.println("In resultset builder 4: " + e.getMessage());
			closeConnection(oConnection);
			return "memory error";
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			closeConnection(oConnection);
			return "error";
		}
		
		try {
			System.gc();
			if (false) {
				// the XMLOutputter may be used to write out the XML
				org.jdom.output.XMLOutputter outp = new org.jdom.output.XMLOutputter();
				// set the format to look good
				outp.setFormat(org.jdom.output.Format.getPrettyFormat());
				String sXmli2b2 = outp.outputString(doc);
				outp.output(doc, System.out);
			}
			// get the root
			Element root = doc.getRootElement();
			// get the children from the i2b2 document
			java.util.List allChildren = root.getChildren();
			int iNumberOfChildren = allChildren.size();
			// set up the variables for the loop
			String sPatient_num = null;
			String sConcept_cd = null;
			String sOldPatient_num = "start";
			String sOldConcept_cd = null;
			String sStart_date = null;
			String sOldStart_date = null;
			String sEnd_date = null;
			String sInout_cd = null;
			String sDeath_date = null;
			String sColor = null;
			String sHeight = null;
			String sValue = null;
			String sTablename = null;
			int patientNum = 0;
			Date oDate;
			
			resultFile.append(GetTimelineHeader());
			boolean bOverMax = false;
			int conceptCount = 0;
			int patientCount = minPatientNum;
			StringBuilder patientRecord = new StringBuilder();
			
			String currentPatientNum = null;
			int indexPos = 0;
			
			for(int p=0; p<demographicsArray.size(); p++) {
				PatientDemographics record = demographicsArray.get(p); 
				currentPatientNum = record.patientNumber();
				
				if(displayDemographics) {
					patientRecord.append(getTimelinePatientString(currentPatientNum, record));
				}
				else
				{
					patientRecord.append(getTimelinePatientString(currentPatientNum));
				} 
				
				resultFile.append(patientRecord.toString());
				patientRecord = new StringBuilder();
				patientCount++;
				
				conceptCount = 0;
				sOldConcept_cd = null;
				sOldStart_date = null;
				sOldPatient_num = "";
				
				if((indexPos == iNumberOfChildren) && bDisplayAll) {
					conceptCount = 0;
					while ((conceptOrder!=null)&&(conceptCount<conceptOrder.size())) {
						patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
						patientRecord.append(getTimelineEmptyDateString());
						conceptCount++;
					}
					
					resultFile.append(patientRecord.toString());
					patientRecord = new StringBuilder();
				}
				
				for (int i=indexPos; i<iNumberOfChildren; i++) {			
					if ((maxLineCount>0)&&(i>maxLineCount))
					{
						bOverMax = true;
						break;
					}
			
					Element oChild = (Element)allChildren.get(i);
					sPatient_num = oChild.getChild(ss_patient_num).getText();
					
					if (!sPatient_num.equals(currentPatientNum) && (sOldPatient_num.equals("start")) /*&&
							!sOldPatient_num.equals(sPatient_num)*/) {
						if(bDisplayAll) {
							try{
								patientNum = Integer.parseInt(sPatient_num);								
								conceptCount = 0;
								while ((conceptOrder!=null)&&(conceptCount<conceptOrder.size())) {
									patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
									patientRecord.append(getTimelineEmptyDateString());
									conceptCount++;
								}
								
								resultFile.append(patientRecord.toString());
								patientRecord = new StringBuilder();
								//patientCount++;
							}
							catch (java.lang.OutOfMemoryError e){
								System.out.println("In resultset builder 5: " + e.getMessage());
								//closeConnection(oConnection);
								return "memory error";
							}
							catch (Exception e) {
								System.out.println(e.getMessage());
								//closeConnection(oConnection);
								return "error";
							}
							
							conceptCount = 0;
							sOldConcept_cd = null;
							sOldStart_date = null;
						}
						break;
					}
					else if(!sPatient_num.equals(currentPatientNum) && !(sOldPatient_num.equals("start")) /*&&
							!sOldPatient_num.equals(sPatient_num)*/) {
						if ((bDisplayAll)&&(conceptCount<(conceptOrder.size())))
						{
							while ((conceptOrder!=null)&&
									(conceptCount<conceptOrder.size()))
							{
								patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
								patientRecord.append(getTimelineEmptyDateString());
								conceptCount++;
							}
						}
						
						resultFile.append(patientRecord.toString());
						patientRecord = new StringBuilder();
						patientCount++;
							
						conceptCount = 0;
						sOldConcept_cd = null;
						sOldStart_date = null;
						break;
					}
					else if(sPatient_num.equals(currentPatientNum)) {
						indexPos = i+1;
						sOldPatient_num = sPatient_num;
						if (bUseConcept) {
							sConcept_cd = oChild.getChild(ss_concept_cd).getText();
						}
						else {
							sConcept_cd = oChild.getAttributeValue(ss_q_name_char);
						}
						
						if(!sConcept_cd.equals(sOldConcept_cd)) {
							//conceptCount++;
							if(bDisplayAll) {
								for(int j=conceptCount; j<conceptOrder.size(); j++) {							
									if (sConcept_cd.equals(conceptOrder.get(j))) {
										break;
									}
									else {
										patientRecord.append(getTimelineConceptString((String)conceptOrder.get(j),1));
										patientRecord.append(getTimelineEmptyDateString());
										conceptCount++;
									}
								}
							}
							
							int iNumConceptObservations = getNumConceptObservationsRollingupStartDate(allChildren,i);
							
							patientRecord.append(getTimelineConceptString(sConcept_cd,iNumConceptObservations));
							conceptCount++;
							sOldStart_date = null;
						}
						
						sOldConcept_cd = sConcept_cd;
						sStart_date = oChild.getChild(ss_start_date).getText();	
						//if (!sStart_date.equals(sOldStart_date)) {
						//if (!sStart_date.equals(null)) {
						if ((!sStart_date.equals(null))&&
								((sOldStart_date==null)||(!sStart_date.equals(sOldStart_date)))) {
							sEnd_date = oChild.getChild(ss_end_date).getText();
							if ((sEnd_date==null)||(sEnd_date.trim().length()==0)) sEnd_date = sStart_date;
							//sInout_cd = oChild.getChild(ss_inout_cd).getText();
							sInout_cd = "";
							sColor = oChild.getChild(ss_color_cd).getText();
							sHeight = oChild.getChild(ss_height_cd).getText();
							sValue = oChild.getChild(ss_value_cd).getText();
							sTablename = oChild.getChild(ss_table_name).getText();
							String prefix = "C";
							if(sTablename.equalsIgnoreCase("visit_dimension")) {
								prefix = "E";
							}
							else if(sTablename.equalsIgnoreCase("provider_dimension")) {
								prefix = "P";
							}
							
							if ((sValue==null)||(sValue.length()==0)) {	
								sValue = prefix+" = ::"+sConcept_cd+"::"+
								"$$"+oChild.getChild(ss_patient_num).getText()+
								"$$"+oChild.getChild(ss_concept_cd).getText() +
								"$$"+ChangeRsDateFull(sStart_date) ;//+"::";
							}
							else {
								sValue = prefix+" Value = " + "::"+sConcept_cd+": "+sValue+"::"+
								"$$"+oChild.getChild(ss_patient_num).getText()+
								"$$"+oChild.getChild(ss_concept_cd).getText() +
								"$$"+ChangeRsDateFull(sStart_date) ;//+"::";
							}
							
							//System.out.println("   "+ ChangeRsDate(sStart_date) + " -> " + ChangeRsDate(sEnd_date));
							//System.out.print(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));
							//System.out.print(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date),sConcept_cd));
							if (sInout_cd.equalsIgnoreCase("I")) {
								if (sColor!=null)
									patientRecord.append(getTimelineDateStringSpecial(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sColor));						
								else
									patientRecord.append(getTimelineDateStringSpecial(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));						
							}
							else if (sInout_cd.equalsIgnoreCase("E")) {
								if (sColor!=null)
									patientRecord.append(getTimelineDateStringEncounter(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sColor));
								else
									patientRecord.append(getTimelineDateStringEncounter(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));
							}
							else if (sInout_cd.equalsIgnoreCase("D")) {
								if (sStart_date.length() == 0 ) {
									if (sColor!=null)
										patientRecord.append(getTimelineDateStringEncounter("today","today", sColor));							
									else
										patientRecord.append(getTimelineDateStringEncounter("today","today"));							
								}
								else {
									if (sColor!=null)
										patientRecord.append(getTimelineDateStringDeath(ChangeRsDate(sStart_date),"today", sColor));							
									else 
										patientRecord.append(getTimelineDateStringDeath(ChangeRsDate(sStart_date),"today", sColor));							
								}
							}
							else {
								if (sConcept_cd.equals("Death"))
								{
									if (sStart_date.length() == 0 )
									{
										sStart_date = "today";
										sColor = "lightbrown";
									}
									sEnd_date = "today";
								}
								if (sColor!=null)
								{
									if (sConcept_cd.equalsIgnoreCase("EGFR"))
										patientRecord.append(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sColor, "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=1956"));						
									else
										patientRecord.append(getTimelineDateStringHeight(ChangeRsDate(sStart_date),
												ChangeRsDate(sEnd_date), sColor, sHeight, sValue));						
								}
								else
									patientRecord.append(getTimelineDateStringHeight(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date), sHeight));						
							}					
						}
						sOldStart_date = sStart_date;
				
						if (!bOverMax) {
							if(bDisplayAll && (indexPos == iNumberOfChildren)) {	
								while ((conceptOrder!=null)&&(conceptCount<conceptOrder.size())) {
									patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
									patientRecord.append(getTimelineEmptyDateString());
									conceptCount++;
								}
							}
							resultFile.append(patientRecord.toString());
							patientRecord = new StringBuilder();
							patientCount++;
						}
					}
				}
			}
			
			if ((!bOverMax) && bDisplayAll) {
				while ((conceptOrder!=null)&&(conceptCount<conceptOrder.size())){
					patientRecord.append(getTimelineConceptString((String)conceptOrder.get(conceptCount),1));
					patientRecord.append(getTimelineEmptyDateString());
					conceptCount++;
				}
				resultFile.append(patientRecord.toString());
			}
				
			resultFile.append(GetTimelineFooter());
			System.out.println(" Total Count " + iNumberOfChildren);
			
			if (writeFile)
			{
				String i2b2File = System.getProperty("user.dir")+'/'+ "i2b2xml.lld";
				File oDelete = new File(i2b2File);
				if (oDelete != null) oDelete.delete();
				RandomAccessFile f = new RandomAccessFile(i2b2File,"rw");
				Lib.append(f, resultFile.toString());
				f.close();
			}
			
			if (bOverMax)
			{
				System.out.println("reached maximum at " + new Date());
				return "overmaximum";
			}
		}
		catch (java.lang.OutOfMemoryError e){
			System.out.println("In resultset builder 6: " + e.getMessage());
			closeConnection(oConnection);
			return "memory error";
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			closeConnection(oConnection);
			return "error";
		}
		
		System.out.println("done at " + new Date());
		return resultFile.toString();
	}
	
	public static String getResultSetAsi2b2XML(Connection oConnection) throws Exception {
		String sI2b2XML = null;
		String sSQL = null;
		ResultSet oRs = null;
		ResultSetBuilder oResultSetBuilder = null;
		Document doc = null;
		//Connection oConnection = null;
		if (true) {
			sSQL = "select " +
			" /*+ index_ffs(visit_dimension VISITDIM_ECT_PT_CM_IO_STD) parallel_index(visit_dimension VISITDIM_ECT_PT_CM_IO_STD)*/" +  
			"o.patient_num, 'Asthma' q_name_char, o.concept_cd CONCEPT_CD, o.start_date, o.end_date, v.inout_cd " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num > 60000 and o.patient_num < 60200 " + 
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_cd in " +
			" (select concept_cd from concept_dimension " +
			"  where concept_path like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%') " +
			"order by o.patient_num, o.concept_cd, o.start_date, o.end_date";
			sSQL = DBLib.sSQLQueryWithExpression();
		}
		else {
			sSQL = "select PATIENT_NUM, CONCEPT_CD, START_DATE, END_DATE from sample " +
			"order by patient_num, concept_cd, start_date";
			oConnection = openODBCConnection("sample_i2b2_db");
		}
		try {
			System.out.println("before query: " + new Date());
			System.out.println("Query: " + sSQL);
			oRs = doQuery(oConnection, sSQL);
			System.out.println("after query: " + new Date());
			// the ResultSetBuilder is part of contrib jdom
			oResultSetBuilder = new ResultSetBuilder(oRs);
			// set the root name
			oResultSetBuilder.setRootName("PatientData");
			// set the tag for the rows
			oResultSetBuilder.setRowName("observation_fact");
			// set the q_name_char as an attibute for the observation_fact
			oResultSetBuilder.setAsAttribute(ss_q_name_char);
			// build the document
			doc = oResultSetBuilder.build();
			//closeConnection(oConnection);
		}
		catch (Exception e) {
			System.out.println("In resultset builder 16: " + e.getMessage());
			//closeConnection(oConnection);
			throw e;
		}
		try {
			if (false) {
				// the XMLOutputter may be used to write out the XML
				org.jdom.output.XMLOutputter outp = new org.jdom.output.XMLOutputter();
				// set the format to look good
				outp.setFormat(org.jdom.output.Format.getPrettyFormat());
				String sXmli2b2 = outp.outputString(doc);
				outp.output(doc, System.out);
			}
			// get the root
			Element root = doc.getRootElement();
			// get the children from the i2b2 document
			java.util.List allChildren = root.getChildren();
			int iNumberOfChildren = allChildren.size();
			// set up the variables for the loop
			String sPatient_num = null;
			String sConcept_cd = null;
			String sOldPatient_num = null;
			String sOldConcept_cd = null;
			String sStart_date = null;
			String sOldStart_date = null;
			String sEnd_date = null;
			String sInout_cd = null;
			String sDeath_date = null;
			Date oDate;
			// open the file and prepare for writing
			String i2b2File = System.getProperty("user.dir")+'/'+ "i2b2xml.lld";
			File oDelete = new File(i2b2File);
			if (oDelete != null) oDelete.delete();
			RandomAccessFile f = new RandomAccessFile(i2b2File,"rw");
			// write the header
			Lib.append(f,GetTimelineHeader());
			for (int i=0; i<iNumberOfChildren; i++) {
				// get a child
				Element oChild = (Element)allChildren.get(i);
				sPatient_num = oChild.getChild(ss_patient_num).getText();
				if (!sPatient_num.equals(sOldPatient_num)) {
					System.out.println(sPatient_num);
					//System.out.print(getTimelinePatientString(sPatient_num));
					Lib.append(f,getTimelinePatientString(sPatient_num));
					sOldConcept_cd = null;
					sOldStart_date = null;
				}
				sOldPatient_num = sPatient_num;
				if (bUseConcept) {
					sConcept_cd = oChild.getChild(ss_concept_cd).getText();
				}
				else {
					sConcept_cd = oChild.getAttributeValue(ss_q_name_char);
				}
				if (!sConcept_cd.equals(sOldConcept_cd)) {
					int iNumConceptObservations = getNumConceptObservations(allChildren,i);
					//int iNumConceptObservations = getNumConceptObservationsRollingupStartDate(allChildren,i);
					System.out.println(" "+sConcept_cd + " " + iNumConceptObservations);
					//System.out.print(getTimelineConceptString(sConcept_cd,iNumConceptObservations));
					Lib.append(f,getTimelineConceptString(sConcept_cd,iNumConceptObservations));
					sOldStart_date = null;
				}
				sOldConcept_cd = sConcept_cd;
				sStart_date = oChild.getChild(ss_start_date).getText();	
				//if (!sStart_date.equals(sOldStart_date)) {
				if (!sStart_date.equals(null)) {
					sEnd_date = oChild.getChild(ss_end_date).getText();
					if (sEnd_date == null) sEnd_date = sStart_date;
					sInout_cd = oChild.getChild(ss_inout_cd).getText();
					//System.out.println("   "+ ChangeRsDate(sStart_date) + " -> " + ChangeRsDate(sEnd_date));
					//System.out.print(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));
					//System.out.print(getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date),sConcept_cd));
					if (sInout_cd.equalsIgnoreCase("I")) {
						Lib.append(f,getTimelineDateStringSpecial(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));						
					}
					else if (sInout_cd.equalsIgnoreCase("E")) {
						Lib.append(f,getTimelineDateStringEncounter(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));
					}
					else if (sInout_cd.equalsIgnoreCase("D")) {
						if (sStart_date.length() == 0 ) {
							Lib.append(f,getTimelineDateStringEncounter("today","today"));							
						}
						else {
							Lib.append(f,getTimelineDateStringDeath(ChangeRsDate(sStart_date),"today"));							
						}
					}
					else {
						Lib.append(f,getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date)));						
					}
					//Lib.append(f,getTimelineDateString(ChangeRsDate(sStart_date),ChangeRsDate(sEnd_date),sConcept_cd));					
				}
				sOldStart_date = sStart_date;
			}
			// write the footor
			Lib.append(f,GetTimelineFooter());
			// not sure if I have to wait unitl here to close
			f.close();
			//org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
			//builder.setValidation(false);
			//builder.setIgnoringElementContentWhitespace(true);
			// This builds: <root>This is the root</root>
			//Document doc = new Document();
			//Element e = new Element("root");
			//e.setText("This is the root");
			//doc.addContent(e);
			//Document doc = builder.build(f);
			//System.out.println(doc.toString());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}
		
		//System.out.println(sI2b2XML);
		System.out.println("done at " + new Date());
		return sI2b2XML;
	}
	
	public static int getNumConceptObservations(java.util.List oAllChildren,int iOnThisOne){
		String sNewPatient_num = null;
		String sCurrentPatient_num = null;
		String sNewConcept_cd = null;
		String sCurrentConcept_cd = null;
		int iNumberOfChildren = oAllChildren.size();
		int iNumConceptObservations=1;
		Element oChild = (Element)oAllChildren.get(iOnThisOne);
		sCurrentPatient_num = oChild.getChild(ss_patient_num).getText();
		if (bUseConcept) {
			sCurrentConcept_cd = oChild.getChild(ss_concept_cd).getText();
		}
		else {
			sCurrentConcept_cd = oChild.getAttributeValue(ss_q_name_char);
		}
		for (int i=iOnThisOne+1; i<iNumberOfChildren; i++) {
			// get a new child
			oChild = (Element)oAllChildren.get(i);
			sNewPatient_num = oChild.getChild(ss_patient_num).getText();
			if (bUseConcept) {
				sNewConcept_cd = oChild.getChild(ss_concept_cd).getText();
			}
			else {
				sNewConcept_cd = oChild.getAttributeValue(ss_q_name_char);
			}			
			if (sNewConcept_cd.equals(sCurrentConcept_cd)&&sNewPatient_num.equals(sCurrentPatient_num)) {
				iNumConceptObservations++;
			}
			else break;
		}
		return iNumConceptObservations;			
	}
	
	public static int getNumConceptObservationsRollingupStartDateEx(java.util.List oAllChildren,int iOnThisOne){
		String sNewPatient_num = null;
		String sCurrentPatient_num = null;
		String sNewConcept_cd = null;
		String sCurrentConcept_cd = null;
		String sNewStart_date = null;
		String sCurrentStart_date = null;
		int iNumberOfChildren = oAllChildren.size();
		int iNumConceptObservations=1;
		Element oChild = (Element)oAllChildren.get(iOnThisOne);
		sCurrentPatient_num = oChild.getChild(ss_patient_num).getText();
		sCurrentConcept_cd = oChild.getChild(ss_concept_cd).getText();
		sCurrentStart_date = oChild.getChild(ss_start_date).getText();
		for (int i=iOnThisOne+1; i<iNumberOfChildren; i++) {
			// get a new child
			oChild = (Element)oAllChildren.get(i);
			sNewPatient_num = oChild.getChild(ss_patient_num).getText();
			sNewConcept_cd = oChild.getChild(ss_concept_cd).getText();
			sNewStart_date = oChild.getChild(ss_start_date).getText();
			if (sNewConcept_cd.equals(sCurrentConcept_cd)&&sNewPatient_num.equals(sCurrentPatient_num)) {
				if (!sNewStart_date.equals(sCurrentStart_date)) {
					iNumConceptObservations++;
					sCurrentStart_date = sNewStart_date;
				}
			}
			else break;
		}
		return iNumConceptObservations;			
	}
	public static int getNumConceptObservationsRollingupStartDate(java.util.List oAllChildren,int iOnThisOne){
		String sNewPatient_num = null;
		String sCurrentPatient_num = null;
		String sNewConcept_cd = null;
		String sCurrentConcept_cd = null;
		String sNewStart_date = null;
		String sCurrentStart_date = null;
		int iNumberOfChildren = oAllChildren.size();
		int iNumConceptObservations=1;
		Element oChild = (Element)oAllChildren.get(iOnThisOne);
		sCurrentPatient_num = oChild.getChild(ss_patient_num).getText();
		if (bUseConcept) {
			sCurrentConcept_cd = oChild.getChild(ss_concept_cd).getText();
		}
		else {
			sCurrentConcept_cd = oChild.getAttributeValue(ss_q_name_char);
		}
		sCurrentStart_date = oChild.getChild(ss_start_date).getText();
		for (int i=iOnThisOne+1; i<iNumberOfChildren; i++) {
			// get a new child
			oChild = (Element)oAllChildren.get(i);
			sNewPatient_num = oChild.getChild(ss_patient_num).getText();
			//sNewConcept_cd = oChild.getChild(ss_q_name_char).getText();
			if (bUseConcept) {
				sNewConcept_cd = oChild.getChild(ss_concept_cd).getText();
			}
			else {
				sNewConcept_cd = oChild.getAttributeValue(ss_q_name_char);
			}
			sNewStart_date = oChild.getChild(ss_start_date).getText();
			if (sNewConcept_cd.equals(sCurrentConcept_cd)&&sNewPatient_num.equals(sCurrentPatient_num)) {
				if (!sNewStart_date.equals(sCurrentStart_date)) {
					iNumConceptObservations++;
					sCurrentStart_date = sNewStart_date;
				}
			}
			else break;
		}
		return iNumConceptObservations;			
	}
	
	public static String ChangeRsDate(String sInputDate) {
		
		try {
			SimpleDateFormat iFormat =  new SimpleDateFormat("d-MMM-yyyy hh:mm:ss a");
			Date oDate = iFormat.parse(sInputDate);
			
			SimpleDateFormat oFormat =  new SimpleDateFormat("M-d-yyyy HH:mm");
			sInputDate = oFormat.format(oDate);
			return sInputDate;
		} catch (Exception e)
		{
			if (System.getProperty("applicationName").equals("BIRN"))
			{
			try {
				
				SimpleDateFormat iFormat =  new SimpleDateFormat("EEEEEEEE, MMMMMM dd, yyyy hh:mm:ss a z");
				Date oDate = iFormat.parse(sInputDate);
				
				SimpleDateFormat oFormat =  new SimpleDateFormat("M-d-yyyy HH:mm");
				sInputDate = oFormat.format(oDate);
				return sInputDate;
			} catch (Exception eee) {			
				return sInputDate;
			}
			}
			else
			{
				try {
					
					SimpleDateFormat iFormat =  new SimpleDateFormat("EEEEEEEE, MMMMMM dd, yyyy");
					Date oDate = iFormat.parse(sInputDate);
					
					SimpleDateFormat oFormat =  new SimpleDateFormat("M-d-yyyy 12:00");
					sInputDate = oFormat.format(oDate);
					return sInputDate;
				} catch (Exception eee) {			
					return sInputDate;
				}
			}
		}
	}
	
	public static String ChangeRsDateFull(String sInputDate) {
		try{
			/*
			 sInputDate = Lib.StrFindAndReplace("Sunday, ","",sInputDate);
			 sInputDate = Lib.StrFindAndReplace("Monday, ","",sInputDate);
			 sInputDate = Lib.StrFindAndReplace("Tuesday, ","",sInputDate);
			 sInputDate = Lib.StrFindAndReplace("Wednesday, ","",sInputDate);
			 sInputDate = Lib.StrFindAndReplace("Thursday, ","",sInputDate);
			 sInputDate = Lib.StrFindAndReplace("Friday, ","",sInputDate);
			 sInputDate = Lib.StrFindAndReplace("Saturday, ","",sInputDate);
			 sInputDate = Lib.StrFindAndReplace(" EDT","",sInputDate);
			 Date oDate = java.text.DateFormat.getDateInstance().parse(sInputDate);
			 */
			
			SimpleDateFormat iFormat =  new SimpleDateFormat("d-MMM-yyyy hh:mm:ss a");
			Date oDate = iFormat.parse(sInputDate);
			
			
			//sInputDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(oDate);
			SimpleDateFormat oFormat =  new SimpleDateFormat("yyyy-M-d hh:mm:ss a");
			sInputDate = oFormat.format(oDate);
			return sInputDate;
		}
		catch (Exception e){
			return "";
		}
	}
	
	/* returns
	 *           %facet,PERSON_#1...............,white,yes
	 *           %c,comment
	 *           %agg, normal,1, no
	 *           %-,2-15-1999,today,white,p1,.,chiempty.html,""
	 */
	public static String getTimelinePatientString(String sPatient_num){
		String sFinished = newline + "%facet,Person_#" + sPatient_num + 
		"................,white,yes" + newline +
		" %c,comment" + newline +
		" %agg, normal,1, no" + newline +
		" %-,2-15-1999 12:00,today,white,p1,.,chiempty.html,\"\""+ newline;
		return sFinished;
	}
	
	public static String getTimelinePatientString(String sPatient_num, PatientDemographics record, String startDate){
		String sFinished;
		
			sFinished = newline + "%facet,";
			
			if (!System.getProperty("applicationName").equals("BIRN"))
				sFinished += "Person_#";
			sFinished += sPatient_num + 
			",white,yes" + newline +
			" %c,comment" + newline +
			" %agg, normal,1, no" + newline +
			" %-," + ChangeRsDate(startDate) + ",today,white,p1,.,chiempty.html,\"\""+ newline;
			return sFinished;
		}
	/* returns
	 *           %facet,PERSON_#1 gender: age: race: vital-status: ,white,yes
	 *           %c,comment
	 *           %agg, normal,1, no
	 *           %-,2-15-1999,today,white,p1,.,chiempty.html,""
	 */
	public static String getTimelinePatientString(String sPatient_num, PatientDemographics record){
		String sFinished;
		
		if(record.age().equals("")) {
			sFinished = newline + "%facet,Person_#" + sPatient_num + 
			",white,yes" + newline +
			" %c,comment" + newline +
			" %agg, normal,1, no" + newline +
			" %-,2-15-1999 12:00,today,white,p1,.,chiempty.html,\"\""+ newline;
			return sFinished;
		}
		
		String age = record.age()+"yrold";
		String gender = null;
		String race = null;
		
		if(record.vitalStatus().equalsIgnoreCase("Y")) {
			age = "Dead";
		}
		
		if(record.gender().toUpperCase().startsWith("M")) {
			gender = "Male";
		}
		else if(record.gender().toUpperCase().startsWith("F")) {
			gender = "Female";
		}
		
		if(record.race().toUpperCase().startsWith("W")) {
			race = "White";
		}
		else if(record.race().toUpperCase().startsWith("B")) {
			race = "Black";
		}
		else if(record.race().toUpperCase().startsWith("A")) {
			race = "Asian";
		}
		else if(record.race().toUpperCase().startsWith("H")) {
			race = "Hispanic";
		}
		else if(record.race().toUpperCase().startsWith("O")) {
			if(record.race().toUpperCase().indexOf("OR")>=0) {
				race = "Oriental";
			}
			else {
				race = "Other";
			}
		}
		else {
			race = "Unknown";
		}
		
		sFinished = newline + "%facet,Person_#" + sPatient_num + 
		"__"+gender+"__"+age+
		"__"+race+",white,yes" + newline +
		" %c,comment" + newline +
		" %agg, normal,1, no" + newline +
		" %-,2-15-1999 12:00,today,white,p1,.,chiempty.html,\"\""+ newline;
		
		return sFinished;
	}
	
	/* returns
	 *           %facet,Diagnosis,lightbrown,yes
	 %c,comment
	 %agg, normal,6, no
	 %-,6-27-1999,today,slateblue,p5,ICH,blank.htm,""
	 %-,6-26-1999,6-30-1999,slateblue,p10, ,blank.htm,""
	 */
	public static String getTimelineConceptString(String sConcept_cd,int iNumConceptObservations){
		String sNewConcept = sConcept_cd.replaceAll(" ", "_").replaceAll(",", "_");
		sNewConcept = sNewConcept.replaceAll("__", "_");
		sNewConcept = sNewConcept.replaceAll(">", "_");
		sNewConcept = sNewConcept.replaceAll("<", "_");
		sNewConcept = sNewConcept.replaceAll("zz", "");
		
		sNewConcept = sNewConcept.trim();
		if(sNewConcept.length() > 15) {
			sNewConcept = sNewConcept.substring(0, 15)+"...";
		}
		
		return newline + "%facet," + sNewConcept + ",lightbrown," + "yes" + newline + 
		" %c,comment" + newline +
		" %agg, normal," + Integer.toString(iNumConceptObservations)+", no" + newline;
		
	}
	
	public static String getTimelineDateString(String sStart_date,String sEnd_date){
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		",slateblue,p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}
	
	public static String getTimelineDateStringHeight(String sStart_date,String sEnd_date, String height){
		String pixel = "p10";
		if(height.equalsIgnoreCase("Very Low")) {
			pixel = "p4";
		} 
		else if(height.equalsIgnoreCase("Very Tall")) {
			pixel = "p18";
		}
		else if(height.equalsIgnoreCase("Tall")) {
			pixel = "p12";
		}
		else if(height.equalsIgnoreCase("Low")) {
			pixel = "p8";
		}
		
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		",slateblue,"+pixel+", ,blank.htm,\"\"" + newline;
		return sFinished;
	}
	
	public static String getTimelineEmptyDateString(){
		String sFinished = " %-,2-15-1999 12:00,2-15-1999 12:00" +
		",lightbrown,p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}
	
	public static String getTimelineDateString(String sStart_date,String sEnd_date, String colorName){
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		"," + colorName + ",p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}
	
	public static String getTimelineDateStringHeight(String sStart_date,String sEnd_date, 
			String colorName, String height, String sValue){
		
		String pixel = "p10";
		if(height.equalsIgnoreCase("Very Low")) {
			pixel = "p4";
		} 
		else if(height.equalsIgnoreCase("Very Tall")) {
			pixel = "p18";
		}
		else if(height.equalsIgnoreCase("Tall")) {
			pixel = "p12";
		}
		else if(height.equalsIgnoreCase("Low")) {
			pixel = "p8";
		}
		
		/*if ((sValue==null)||(sValue.trim().length()==0))
		 sValue = "";
		 else
		 sValue = "Value = " + sValue;*/
		
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		"," + colorName + ","+pixel+", ,blank.htm,\"" + sValue/*.replaceAll(",","-")*/ + "\"" + newline;
		return sFinished;
	}
	
	public static String getTimelineDateStringHeight(String sStart_date,String sEnd_date, 
			String colorName, String height){
		
		String pixel = "p10";
		if(height.equalsIgnoreCase("Very Low")) {
			pixel = "p4";
		} 
		else if(height.equalsIgnoreCase("Very Tall")) {
			pixel = "p18";
		}
		else if(height.equalsIgnoreCase("Tall")) {
			pixel = "p12";
		}
		else if(height.equalsIgnoreCase("Low")) {
			pixel = "p8";
		}
		
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		"," + colorName + ","+pixel+", ,blank.htm,\"\"" + newline;
		return sFinished;
	}
	
	public static String getTimelineDateString(String sStart_date,String sEnd_date, String colorName, String url){
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		"," + colorName + ",p5, ," + url + ",\"\"" + newline;
		return sFinished;
	}
	
	public static String getTimelineDateStringSpecial(String sStart_date,String sEnd_date){
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		",tomato,p10, ,blank.htm,\"\"" + newline;
		return sFinished;
	}
	
	public static String getTimelineDateStringSpecial(String sStart_date,String sEnd_date, String colorName){
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		"," + colorName + ",p10, ,blank.htm,\"\"" + newline;
		return sFinished;
	}
	
	
	public static String getTimelineDateStringEncounter(String sStart_date,String sEnd_date){
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		",yellowgreen,p2, ,blank.htm,\"\"" + newline;
		
		return sFinished;
	}
	
	public static String getTimelineDateStringEncounter(String sStart_date,String sEnd_date, String colorName){
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		"," + colorName + ",p2, ,blank.htm,\"\"" + newline;
		
		return sFinished;
	}
	
	public static String getTimelineDateStringDeath(String sStart_date,String sEnd_date){
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		",black,p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}
	
	public static String getTimelineDateStringDeath(String sStart_date,String sEnd_date, String colorName){
		String sFinished = " %-," + sStart_date + "," + sEnd_date +
		"," + colorName + ",p5, ,blank.htm,\"\"" + newline;
		return sFinished;
	}
	
	/*public static String getTimelineDateString(String sStart_date,String sEnd_date, String concept_cd){
	 String sFinished = " %-," + sStart_date + "," + sEnd_date +
	 ",slateblue,p5," + concept_cd + ",blank.htm,\"\"" + newline;
	 return sFinished;
	 }*/
	
	public static String GetTimelineHeader() {
		return
		"%beforeSeptember1997" + newline +
		"%today,3-01-2006 12:00" + newline + newline +
		
		"%c, Available colors:" + newline +
		"%c, " + newline +
		"%c, (\"seagreen\",          \"2e8b57\");" + newline +
		"%c, (\"seashell\",          \"fff5ee\");" + newline +
		"%c, (\"sienna\",            \"a0522d\");" + newline +
		"%c, (\"skyblue\",           \"87ceeb\");" + newline +
		"%c, (\"slateblue\",         \"6a5acd\");" + newline +
		"%c, (\"slategray\",         \"708090\");" + newline +
		"%c, (\"slategrey\",         \"708090\");" + newline +
		"%c, (\"snow\",              \"fffafa\");" + newline +
		"%c, (\"springgreen\",       \"00ff7f\");" + newline +
		"%c, (\"steelblue\",         \"4682b4\");" + newline +
		"%c, (\"tan\",               \"d2b48c\");" + newline +
		"%c, (\"thistle\",           \"d8bfd8\");" + newline +
		"%c, (\"tomato\",            \"ff6347\");" + newline +
		"%c, (\"turquoise\",         \"40e0d0\");" + newline +
		"%c, (\"violet\",            \"ee82ee\");" + newline +
		"%c, (\"violetred\",         \"d02090\");" + newline +
		"%c, (\"wheat\",             \"f5deb3\");" + newline +
		"%c, (\"white\",             \"ffffff\");" + newline +
		"%c, (\"whitesmoke\",        \"f5f5f5\");" + newline +
		"%c, (\"yellow\",            \"ffff00\");" + newline +
		"%c, (\"yellowgreen\",       \"9acd32\");" + newline +
		"%c, (\"lightbrown\",        \"fff5c8\");" + newline +
		"%c, (\"darkbrown\",         \"ffecaf\");" + newline + newline +
		
		"%person,i2b2 Timeline Application,.,2006,.,images/cath.gif" + newline + newline +
		
		"%c,PERSON 1" + newline;
	}
	
	public static String GetTimelineFooter() {
		return newline + "%end" + newline;
	}
	
	public static String sSQLQuery(){
		String sQueryString = 
			"select o.patient_num PATIENT_NUM, " +
			"	'Asthma' q_name_char, " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	1 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num > 60000 and o.patient_num < 60500 " +
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%') " +
			"union " +
			"select p.patient_num PATIENT_NUM, " +
			"	'Asthma' q_name_char, " +
			"	'Death' CONCEPT_CD, " +
			"	p.death_date START_DATE, " +
			"	p.death_date END_DATE, " +
			"	'D' INOUT_CD, " +
			"	3 AN_ORDER " +
			"from patient_dimension p, observation_fact o " +
			"where p.patient_num  = o.patient_num " +
			"and o.patient_num > 60000 and o.patient_num < 60500 " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'Asthma' q_name_char, " +
			"	'Encounter_range' CONCEPT_CD, " +
			"	min(start_date) START_DATE, " +
			"	max(start_date) END_DATE, " +
			"	'E' INOUT_CD, " +
			"	2 AN_ORDER " +
			"from observation_fact o " +
			"where o.patient_num > 60000 and o.patient_num < 60500 " +
			"group by PATIENT_NUM " +
			"order by PATIENT_NUM, AN_ORDER, CONCEPT_CD, START_DATE, END_DATE";
		return sQueryString;
	}
	public static String sSQLQueryOriginalChangedColumns(){
		String sQueryString = 
			"select o.patient_num, " +
			"	'Asthma' q_name_char, " +
			"	o.concept_cd CONCEPT_CD," +
			"	o.start_date," +
			"	o.end_date," +
			"	v.inout_cd," +
			"	2 " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num > 60000 and o.patient_num < 60200 " +
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%') " +
			"union " +
			"select p.patient_num, " +
			"	'Asthma', " +
			"	'Date Of Death', " +
			"	p.death_date, " +
			"	null, " +
			"	null, " +
			"	1 " +
			"from patient_dimension p, observation_fact o " +
			"where p.patient_num in " +
			"	(select o.patient_num " +
			"	from observation_fact o " +
			"	where o.patient_num > 60000 and o.patient_num < 60200 " +
			"	and o.concept_cd in " +
			"		(select concept_cd " +
			"		from concept_dimension " +
			"		where concept_path like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%')) " +
			"union " +
			"select o.patient_num, " +
			"	'Asthma', " +
			"	'Max/Min Visit Dates', " +
			"	min(start_date), " +
			"	max(start_date), " +
			"	null, " +
			"	3 " +
			"from observation_fact o " +
			"where o.patient_num > 60000 and o.patient_num < 60200 " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%') " +
			"group by o.patient_num " +
			"order by 1, 7, 3, 4, 5 ";
		return sQueryString;
	}
	public static String sSQLQueryOriginalWorking(){
		String sQueryString = 
			"select " +
			" /*+ index_ffs(visit_dimension VISITDIM_ECT_PT_CM_IO_STD) parallel_index(visit_dimension VISITDIM_ECT_PT_CM_IO_STD)*/ " +
			" o.patient_num, " +
			"	'Asthma' q_name_char, " +
			"	o.concept_cd CONCEPT_CD," +
			"	o.start_date," +
			"	o.end_date," +
			"	v.inout_cd," +
			"	2 " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num > 60000 and o.patient_num < 60200 " +
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%') " +
			"union " +
			"select p.patient_num, " +
			"	'Asthma', " +
			"	'Date Of Death', " +
			"	p.death_date, " +
			"	p.death_date, " +
			"	'O', " +
			"	1 " +
			"from patient_dimension p, observation_fact o " +
			"where p.patient_num in " +
			"	(select o.patient_num " +
			"	from observation_fact o " +
			"	where o.patient_num > 60000 and o.patient_num < 60200 " +
			"	and o.concept_cd in " +
			"		(select concept_cd " +
			"		from concept_dimension " +
			"		where concept_path like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%')) " +
			"union " +
			"select o.patient_num, " +
			"	'Asthma', " +
			"	'Max/Min Visit Dates', " +
			"	min(start_date), " +
			"	max(start_date), " +
			"	'O', " +
			"	3 " +
			"from observation_fact o " +
			"where o.patient_num > 60000 and o.patient_num < 60200 " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%') " +
			"group by o.patient_num " +
			"order by 1, 7, 3, 4, 5 ";
		return sQueryString;
	}
	public static String sSQLQueryOriginal(){
		String sQueryString = 
			"select o.patient_num, " +
			"	'Asthma' q_name_char, " +
			"	o.concept_id CONCEPT_CD," +
			"	o.start_date," +
			"	o.end_date," +
			"	v.inout_cd," +
			"	2 " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num > 60000 and o.patient_num < 60200 " +
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_id in " +
			"	(select c_basecode " +
			"	from concept_dimension " +
			"	where c_fullname like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%') " +
			"union " +
			"select p.patient_num, " +
			"	'Asthma', " +
			"	'Date Of Death', " +
			"	p.date_of_death, " +
			"	null, " +
			"	null, " +
			"	1 " +
			"from patient_dimension p, observation_fact o " +
			"where p.patient_num in " +
			"	(select o.patient_num " +
			"	from observation_fact o " +
			"	where o.patient_num > 60000 and o.patient_num < 60200 " +
			"	and o.concept_id in " +
			"		(select c_basecode " +
			"		from concept_dimension " +
			"		where c_fullname like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%')) " +
			"union " +
			"select o.patient_num, " +
			"	'Asthma', " +
			"	'Max/Min Visit Dates', " +
			"	min(start_date), " +
			"	max(start_date), " +
			"	null, " +
			"	3 " +
			"from observation_fact o " +
			"where o.patient_num > 60000 and o.patient_num < 60200 " +
			"and o.concept_id in " +
			"	(select c_basecode " +
			"	from concept_dimension " +
			"	where c_fullname like 'Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%') " +
			"group by o.patient_num " +
			"order by 1, 7, 3, 4, 5 ";
		return sQueryString;
	}
	public static String sSQLQueryWithExpression2266(){
		String sAnAttribute = "Q_NAME_CHAR";
		String sConceptName = null;
		String sConceptPath = null;
		String sPatientNumMin = null;
		String sPatientNumMax = null;
		if (true) {
			sConceptName = "All";
			sConceptPath = "%";
			sPatientNumMin = "1";
			sPatientNumMax = "2";
		}
		if (false) {
			sConceptName = "Asthma";
			sConceptPath = "Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%";
			sPatientNumMin = "10100";
			sPatientNumMax = "10200";
		}
		String sQueryString = 
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	1 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath + "') " +
			"union " +
			"select p.patient_num PATIENT_NUM, " +
			"	'Death' " + sAnAttribute + ", " +
			"	'Death' CONCEPT_CD, " +
			"	p.death_date START_DATE, " +
			"	p.death_date END_DATE, " +
			"	'D' INOUT_CD, " +
			"	3 AN_ORDER " +
			"from patient_dimension p, observation_fact o " +
			"where p.patient_num  = o.patient_num " +
			"and o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath + "') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'Encounter_range' " + sAnAttribute + ", " +
			"	'Encounter_range' CONCEPT_CD, " +
			"	min(start_date) START_DATE, " +
			"	max(start_date) END_DATE, " +
			"	'E' INOUT_CD, " +
			"	2 AN_ORDER " +
			"from observation_fact o " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"group by PATIENT_NUM " +
			"order by PATIENT_NUM, AN_ORDER, CONCEPT_CD, START_DATE, END_DATE";
		return sQueryString;
	}
	public static String sSQLQueryWithExpressionEx(){
		String sAnAttribute = "Q_NAME_CHAR";
		String sConceptName = null;
		String sConceptPath = null;
		String sConceptName2 = null;
		String sConceptPath2 = null;
		String sConceptName3 = null;
		String sConceptPath3 = null;
		String sConceptName4 = null;
		String sConceptPath4 = null;
		String sConceptName5 = null;
		String sConceptPath5 = null;
		String sPatientNumMin = null;
		String sPatientNumMax = null;
		if (false) {
			sConceptName = "All";
			sConceptPath = "%";
			sPatientNumMin = "1";
			sPatientNumMax = "10";
		}
		if (true) {
			sConceptName = "Asthma";
			sConceptPath = "Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%";
			sConceptName2 = "Hypertension";
			sConceptPath2 = "Circulatory system (390-459)\\Hypertensive disease (401-405)\\%";
			sConceptName3 = "Diabetes";
			sConceptPath3 = "Endocrine disorders (240-259)\\Other endocrine gland diseases (250-259)\\(250) Diabetes mellitus\\%";
			sConceptName4 = "Beta-blocking-agents";
			sConceptPath4 = "MUL\\(LME68) cardiovascular agents\\(LME77) beta-adrenergic blocking agents\\%";
			sConceptName5 = "i2b2-asthma-medications";
			sConceptPath5 = "i2b2\\Medications\\%";
			sPatientNumMin = "10300";
			sPatientNumMax = "10350";
		}
		String sQueryString = 
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	1 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath + "') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName2 + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	2 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath2 + "') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName3 + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	3 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath3 + "') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName4 + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	4 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath4 + "') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName5 + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	5 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.patient_num = v.patient_num  " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath5 + "') " +
			"union " +
			"select p.patient_num PATIENT_NUM, " +
			"	'Death' " + sAnAttribute + ", " +
			"	'Death' CONCEPT_CD, " +
			"	p.death_date START_DATE, " +
			"	p.death_date END_DATE, " +
			"	'D' INOUT_CD, " +
			"	7 AN_ORDER " +
			"from patient_dimension p, observation_fact o " +
			"where p.patient_num  = o.patient_num " +
			"and o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'Encounter_range' " + sAnAttribute + ", " +
			"	'Encounter_range' CONCEPT_CD, " +
			"	min(start_date) START_DATE, " +
			"	max(start_date) END_DATE, " +
			"	'E' INOUT_CD, " +
			"	6 AN_ORDER " +
			"from observation_fact o " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"group by PATIENT_NUM " +
			"order by PATIENT_NUM, AN_ORDER, CONCEPT_CD, START_DATE, END_DATE";
		return sQueryString;
	}
	public static String sSQLQueryWithExpression(){
		String sAnAttribute = "Q_NAME_CHAR";
		String sConceptName = null;
		String sConceptPath = null;
		String sConceptName2 = null;
		String sConceptPath2 = null;
		String sConceptName3 = null;
		String sConceptPath3 = null;
		String sConceptName4 = null;
		String sConceptPath4 = null;
		String sConceptName5 = null;
		String sConceptPath5 = null;
		String sPatientNumMin = null;
		String sPatientNumMax = null;
		if (false) {
			sConceptName = "All";
			sConceptPath = "%";
			sPatientNumMin = "1";
			sPatientNumMax = "10";
		}
		if (true) {
			sConceptName = "Asthma";
			sConceptPath = "Respiratory system (460-519)\\Chronic obstructive diseases (490-496)\\%";
			sConceptName2 = "Hypertension";
			sConceptPath2 = "Circulatory system (390-459)\\Hypertensive disease (401-405)\\%";
			sConceptName3 = "Current-smoker";
			sConceptPath3 = "i2b2\\Smoking History\\Current smoker\\%";
			sConceptName4 = "Beta-blocking-agents";
			sConceptPath4 = "MUL\\(LME68) cardiovascular agents\\(LME77) beta-adrenergic blocking agents\\%";
			sConceptName5 = "i2b2-asthma-medications";
			sConceptPath5 = "i2b2\\Medications\\%";
			sPatientNumMin = "51350";
			sPatientNumMax = "51400";
		}
		String sQueryString = 
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	1 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.patient_num = v.patient_num  " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath + "') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName2 + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	2 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.patient_num = v.patient_num  " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath2 + "') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName3 + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	3 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.patient_num = v.patient_num  " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath3 + "') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName4 + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	4 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.patient_num = v.patient_num  " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath4 + "') " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'" + sConceptName5 + "' " + sAnAttribute + ", " +
			"	o.concept_cd CONCEPT_CD, " +
			"	o.start_date START_DATE, " +
			"	o.end_date END_DATE, " +
			"	v.inout_cd INOUT_CD, " +
			"	5 AN_ORDER " +
			"from observation_fact o, visit_dimension v " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"and o.encounter_num = v.encounter_num " +
			"and o.patient_num = v.patient_num  " +
			"and o.concept_cd in " +
			"	(select concept_cd " +
			"	from concept_dimension " +
			"	where concept_path like '" + sConceptPath5 + "') " +
			"union " +
			"select p.patient_num PATIENT_NUM, " +
			"	'Death' " + sAnAttribute + ", " +
			"	'Death' CONCEPT_CD, " +
			"	p.death_date START_DATE, " +
			"	p.death_date END_DATE, " +
			"	'D' INOUT_CD, " +
			"	7 AN_ORDER " +
			"from patient_dimension p, observation_fact o " +
			"where p.patient_num  = o.patient_num " +
			"and o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"union " +
			"select o.patient_num PATIENT_NUM, " +
			"	'Encounter_range' " + sAnAttribute + ", " +
			"	'Encounter_range' CONCEPT_CD, " +
			"	min(start_date) START_DATE, " +
			"	max(start_date) END_DATE, " +
			"	'E' INOUT_CD, " +
			"	6 AN_ORDER " +
			"from observation_fact o " +
			"where o.patient_num >= " + sPatientNumMin + " and o.patient_num <= " + sPatientNumMax + " " +
			"group by PATIENT_NUM " +
			"order by PATIENT_NUM, AN_ORDER, CONCEPT_CD, START_DATE, END_DATE";
		return sQueryString;
	}}
