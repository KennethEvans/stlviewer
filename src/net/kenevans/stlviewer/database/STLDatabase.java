package net.kenevans.stlviewer.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

import net.kenevans.core.utils.Utils;
import net.kenevans.stlviewer.model.IConstants;

/* STLDatabase
 * Created on Feb 2, 2015
 * By Kenneth Evans, Jr.
 */

/**
 * STLDatabase is a class to manage a STL database
 * 
 * @author Kenneth Evans, Jr.
 */
public class STLDatabase implements IConstants
{
    public static final boolean createDataBase = true;

    /** Flag to indicate if the database should be deleted first. */
    // private static final boolean deleteDatabase = true;
    /** The static format string to use for formatting dates. */
    // public static final String longFormat = "MMM dd, yyyy HH:mm:ss Z";
    public static final String longFormat = "hh:mm a MMM dd, yyyy";
    public static final SimpleDateFormat longFormatter = new SimpleDateFormat(
        longFormat);
    // private static final String CSV_TEST_FILE =
    // "C:/Users/evans/Documents/GPSLink/STL/KennethEvans.2014-08-03.csv";
    // private static final String CSV_TEST_FILE =
    // "C:/Users/evans/Documents/GPSLink/STL/KennethEvans.2014-08-03.test.csv";
    private static final String CSV_TEST_FILE = "C:/Users/evans/Documents/GPSLink/STL/KennethEvans.0.csv";

    /**
     * Gets a Connection.
     *
     * @return
     */
    public static Connection getConnection(String dataBaseUrl) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dataBaseUrl);
        } catch(SQLException ex) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            Utils
                .excMsg("Error getting the connection for " + DATABASE_URL, ex);
        }
        return conn;
    }

    /**
     * Closes the connection.
     * 
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        try {
            if(conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch(Exception ex) {
            Utils.excMsg("Failed to close connection", ex);
        }
    }

    /**
     * Creates the database.
     *
     * @return
     */
    public static Connection createDatabase(String databaseUrl) {
        Connection conn = null;
        try {
            conn = getConnection(databaseUrl);
            Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(30); // set timeout to 30 sec.
            stmt.executeUpdate("drop table if exists " + DATA_TABLE_NAME);
            stmt.executeUpdate(CREATE_DATA_TABLE_STMT);
        } catch(SQLException ex) {
            Utils.excMsg("Error creating database for "
                + CREATE_DATA_TABLE_STMT, ex);
        }
        return conn;
    }

    /**
     * Adds values from the given CSV file to the database.
     *
     * @param conn
     * @param csvFile
     * @return
     */
    public static boolean addToDatabase(Connection conn, File csvFile) {
        if(csvFile == null) {
            Utils.errMsg("addtoDatabase: CSV file is null");
            return false;
        }
        if(!csvFile.exists()) {
            Utils.errMsg("addtoDatabase: Does not exist: " + LS + csvFile);
            return false;
        }

        // Read the file
        BufferedReader in = null;
        String[] tokens = null;
        int tokensLen = 0;
        String delimiter = ",";
        int colsLen = COLS.length;
        int colsLen1 = colsLen - 1;
        int nSkipped = 0;
        int nReplaced = 0;
        int nAdded = 0;
        int nExists = 0;
        int rowCount;
        int lineNum = -1;
        String id;
        String colVal;
        String msg;
        boolean res;
        try {
            in = new BufferedReader(new FileReader(csvFile));
            String line;
            while((line = in.readLine()) != null) {
                lineNum++;
                // Determine the separator
                if(lineNum == 0) {
                    if(line.contains("\t")) {
                        delimiter = "\t";
                    }
                    tokens = line.split(delimiter);
                    tokensLen = tokens.length;
                    // Check number of columns
                    if(tokensLen != colsLen1) {
                        Utils.errMsg("addToDatabase: " + LS + csvFile + ":"
                            + lineNum + LS + "Found " + tokensLen
                            + " columns, expected " + colsLen1);
                        return false;
                    }
                    // Check column names match
                    msg = "";
                    for(int i = 1; i < colsLen; i++) {
                        if(!CSV_COLS[i - 1].equals(tokens[i - 1])) {
                            msg += "Expected " + CSV_COLS[i - 1] + ", got "
                                + tokens[i - 1] + LS;
                        }
                    }
                    if(msg.length() > 0) {
                        Utils.errMsg("addToDatabase: " + LS + csvFile + ":"
                            + lineNum + LS + "Columns mismatch " + LS + msg);
                        return false;
                    }
                    continue;
                }
                tokens = line.split(delimiter);
                tokensLen = tokens.length;
                // Check the length
                if(tokensLen != colsLen1) {
                    nSkipped++;
                    continue;
                }
                // Check if this id exists
                id = tokens[0];
                try {
                    Statement stmt = conn.createStatement();
                    stmt.setQueryTimeout(30); // set timeout to 30 sec.
                    ResultSet rs = stmt.executeQuery("SELECT * FROM "
                        + DATA_TABLE_NAME + " WHERE " + COLS[1] + "='" + id
                        + "'");
                    msg = "";
                    rowCount = 0;
                    while(rs.next()) {
                        rowCount++;
                        // Check if more than one row with this id was found
                        if(rowCount > 1) {
                            Utils.errMsg("addToDatabase: " + LS
                                + "The database has more than one row with "
                                + COLS[1] + "=" + id + LS + "Aborting");
                            return false;
                        }
                        // A row with this id exists
                        // Check if values are the same
                        for(int i = 1; i < colsLen; i++) {
                            colVal = rs.getString(COLS[i]);
                            if(!colVal.equals(tokens[i - 1])) {
                                msg += COLS[i] + ": New='" + tokens[i - 1]
                                    + "' Old='" + colVal + "'" + LS;
                            }
                        }
                        if(msg.length() > 0) {
                            // Prompt to replace it
                            nExists++;
                            int selection = JOptionPane.showConfirmDialog(null,
                                "Line " + lineNum + LS + "New values for id="
                                    + id + " are different:" + LS + msg + LS
                                    + "OK to replace?", "Warning",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                            if(selection != JOptionPane.OK_OPTION) {
                                continue;
                            } else {
                                nReplaced++;
                                res = updateRow(conn, id, tokens);
                                if(!res) {
                                    Utils
                                        .errMsg("Failed to update row at line "
                                            + lineNum);
                                    return false;
                                }
                            }
                        } else {
                            // Skip it
                            nExists++;
                            continue;
                        }
                    }
                    if(rowCount == 0) {
                        // There is no row with this id
                        nAdded++;
                        res = addRow(conn, tokens);
                        if(!res) {
                            Utils
                                .errMsg("Failed to add row at line " + lineNum);
                            return false;
                        }
                        continue;
                    }
                } catch(SQLException ex) {
                    // if the error message is "out of memory",
                    // it probably means no database file is found
                    Utils.excMsg("addToDatabase: SQL error at line " + lineNum,
                        ex);
                    return false;
                }
            }
        } catch(Exception ex) {
            Utils.excMsg("Error adding to database at line " + lineNum, ex);
            return false;
        } finally {
            try {
                if(in != null) in.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        msg = csvFile + LS;
        msg += lineNum + " lines processed" + LS;
        msg += "Added " + nAdded + " rows" + LS;
        msg += "Replaced " + nReplaced + " of " + nExists + " existing rows"
            + LS;
        msg += "Skipped " + nSkipped + " lines owing to errors" + LS;
        Utils.infoMsg(msg);
        return true;
    }

    public static boolean addRow(Connection conn, String[] values) {
        String string = null;
        try {
            int len = values.length;
            Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(30); // set timeout to 30 sec.
            string = "INSERT INTO " + DATA_TABLE_NAME + " VALUES(";
            for(int i = 0; i < len; i++) {
                if(i != 0) {
                    string += ",";
                }
                string += "'" + values[i] + "'";
            }
            string += ")";
            stmt.executeUpdate(string);
        } catch(SQLException ex) {
            Utils.excMsg("Error adding row using:" + LS + string, ex);
            return false;
        }
        return true;
    }

    public static boolean updateRow(Connection conn, String id, String[] values) {
        String string = null;
        try {
            Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(30); // set timeout to 30 sec.
            string = "UPDATE " + DATA_TABLE_NAME + " SET ";
            for(int i = 1; i < COLS.length; i++) {
                if(i != 1) {
                    string += ",";
                }
                string += COLS[i] + "='" + values[i - 1] + "'";
            }
            string += " WHERE id=" + "'" + id + "'";
            stmt.executeUpdate(string);
        } catch(SQLException ex) {
            Utils.excMsg("Error updating row using:" + LS + string, ex);
            return false;
        }
        return true;
    }

    public static String getInfoForId(String id, String databaseUrl) {
        String info = "";
        String databasePath = databaseUrl.substring(DATABASE_URL_PREFIX
            .length());
        File file = new File(databasePath);
        if(file == null) {
            Utils.errMsg("Database path is null");
            return info;
        }
        if(!file.exists()) {
            Utils.errMsg("Database path does not exist");
            return info;
        }
        Connection conn = null;
        try {
            // Initialize the JDBC driver
            Class.forName(DRIVER_CLASS).newInstance();
            conn = getConnection(databaseUrl);
            if(conn == null) {
                return info;
            }
            Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(30); // set timeout to 30 sec.
            String query = "SELECT * FROM " + DATA_TABLE_NAME + " WHERE "
                + COLS[1] + "='" + id + "'";
            ResultSet rs = stmt.executeQuery(query);
            int rowCount = 0;
            String colVal;
            while(rs.next()) {
                rowCount++;
                // Check if more than one row with this id was found
                if(rowCount > 1) {
                    info += "(There is more than one row in the database for this id)"
                        + LS;
                    break;
                }
                for(int i = 1; i < COLS.length; i++) {
                    colVal = rs.getString(COLS[i]);
                    info += COLS[i] + ": " + colVal + LS;
                }
            }
        } catch(Exception ex) {
            Utils.excMsg("Failed to get info for id=" + id, ex);
            info += "An error occurred getting the database info" + LS;
        } finally {
            closeConnection(conn);
        }
        return info;
    }

    /**
     * Fills the database with test values.
     *
     * @return
     */
    public static boolean addTestValues(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(30); // set timeout to 30 sec.
            String string = "INSERT INTO " + DATA_TABLE_NAME + " VALUES(";
            for(int i = 1; i <= 24; i++) {
                if(i != 1) {
                    string += ",";
                }
                string += "'COL" + i + "'";
            }
            string += ")";
            System.out.println(string);
            stmt.executeUpdate(string);
        } catch(SQLException ex) {
            Utils.excMsg("Error adding test values", ex);
            return false;
        }
        return true;
    }

    /**
     * Fills the database with values
     *
     * @return
     */
    public static boolean printDatabase(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(30); // set timeout to 30 sec.
            ResultSet rs = stmt
                .executeQuery("SELECT * FROM " + DATA_TABLE_NAME);
            int pos = 0;
            while(rs.next()) {
                pos++;
                // read the result set
                System.out.println(pos);
                int[] vals = {1, 2, 24};
                for(int i : vals) {
                    System.out.println("  " + COLS[i] + ": "
                        + rs.getString(COLS[i]));
                }
            }
        } catch(SQLException ex) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            Utils.excMsg("Error printing database", ex);
            return false;
        }
        return true;
    }

    /**
     * Deletes the database from the file system.
     */
    public static boolean deleteDatabase() {
        File file = new File(DATABASE_PATH);
        if(file.exists()) {
            if(!file.canWrite()) {
                Utils.errMsg("File cannot be written " + DATABASE_PATH);
                return false;
            }
            boolean res = file.delete();
            if(res) {
                Utils.infoMsg("Delete succeeded for " + DATABASE_PATH);
            } else {
                Utils.errMsg("Delete failed for " + DATABASE_PATH + LS
                    + "Check if it is in use");
                return false;
            }
            //
            try {
                Thread.sleep(60000);
            } catch(InterruptedException ex) {
                System.out.println("Sleep was interrupted");
            }
            // Check if it really worked
            file = new File(DATABASE_PATH);
            if(file.exists()) {
                Utils.errMsg("Delete did not work." + LS
                    + "Check if it is open somewhere.");
                return false;
            }
        } else {
            Utils.infoMsg("File does not exist so does not need to be deleted"
                + DATABASE_PATH);
        }
        return true;
    }

    /**
     * Prints the names of the tables in the database.
     * 
     * @param conn
     */
    public static void getTableNames(Connection conn) {
        try {
            System.out.println("\nTable Names");
            Statement statement = conn.createStatement();
            ResultSet res = statement
                .executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            int nTables = 0;
            while(res.next()) {
                nTables++;
                System.out.println(" Name: " + res.getString("name"));
            }
            System.out.println("Number of tables: " + nTables);
        } catch(Exception ex) {
            System.out.println("Error getting table names");
            ex.printStackTrace();
        }
    }

    /**
     * Prints the column names and other information from the CREATE statement
     * for the given table.
     * 
     * @param conn
     * @param table
     */
    public static void getColumnNames(Connection conn, String table,
        boolean doProperties) {
        try {
            System.out.println("\nColumns for " + table + " table");
            Statement statement = conn.createStatement();
            ResultSet res = statement
                .executeQuery("SELECT sql FROM sqlite_master "
                    + "WHERE tbl_name = '" + table + "' AND type = 'table'");
            // This returns one item with column information in parentheses
            String s = res.getString(1);
            // Could probably use regex here
            int start = s.indexOf("(");
            int end = s.indexOf(")");
            String cols = s.substring(start + 1, end);
            // System.out.println(cols);
            String[] tokens = cols.split(",");
            int nCols = 0;
            for(String token : tokens) {
                nCols++;
                if(doProperties) {
                    System.out.println(nCols + ": " + token.trim());
                } else {
                    String[] tokens1 = token.split(" ");
                    System.out.println(nCols + ": " + tokens1[0]);
                }
            }
            System.out.println("Number of columns: " + nCols);
        } catch(Exception ex) {
            System.out.println("Error getting column names");
            ex.printStackTrace();
        }
    }

    /**
     * Prints the data in the data table.
     * 
     * @param conn
     */
    public static void getData(Connection conn) {
        try {
            System.out.println("\nData");
            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM data");
            String id, date, count, total, comment;
            // String dateMod, edited;
            long dateNum;
            // long dateModNum;
            while(res.next()) {
                id = res.getString("_id");
                dateNum = res.getLong("date");
                date = formatDate(longFormatter, dateNum);
                // dateModNum = res.getLong("datemod");
                // dateMod = formatDate(longFormatter, dateModNum);
                count = res.getString("count");
                total = res.getString("total");
                // edited = res.getString("edited");
                comment = res.getString("comment");
                System.out.printf("%-6s %s/%s \t%s \t%s", id, count, total,
                    date, comment);
                System.out.println();
            }
        } catch(Exception ex) {
            System.out.println("Error listing data");
            ex.printStackTrace();
        }
    }

    /**
     * Format the date using the given format.
     * 
     * @param formatter
     * @param dateNum
     * @return
     * @see #longFormat
     */
    public static String formatDate(SimpleDateFormat formatter, Long dateNum) {
        // Consider using Date.toString() as it might be more locale
        // independent.
        if(dateNum == null) {
            return "<Unknown>";
        }
        if(dateNum == -1) {
            // Means the column was not found in the database
            return "<Date NA>";
        }
        // Consider using Date.toString()
        // It might be more locale independent.
        // return new Date(dateNum).toString();

        // Include the dateNum
        // return dateNum + " " + formatter.format(dateNum);

        return formatter.format(dateNum);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Running " + STLDatabase.class.getSimpleName());

        // SQLite.Database db = null;
        try {
            // Initialize the JDBC driver
            Class.forName(DRIVER_CLASS).newInstance();
        } catch(Exception ex) {
            System.out.println("Failed to initialize " + DRIVER_CLASS);
            return;
        }

        // Check if the database exists
        File file = new File(DATABASE_PATH);
        System.out.println(DATABASE_PATH);
        if(!file.exists()) {
            System.out.println("Database does not exist: " + DATABASE_PATH);
            return;
        }

        // Check if the CSV file exists
        File csvFile = new File(CSV_TEST_FILE);
        System.out.println(CSV_TEST_FILE);
        if(!file.exists()) {
            System.out.println("CSV file does not exist: " + CSV_TEST_FILE);
            return;
        }

        Connection conn = null;
        if(createDataBase) {
            // Crete a new database
            try {
                System.out.println("Creating...");
                conn = createDatabase(DATABASE_URL);
            } catch(Exception ex) {
                System.out.println("Error creating database");
                ex.printStackTrace();
                return;
            }

        } else {
            // Open an existing database
            try {
                System.out.println("Connecting...");
                conn = STLDatabase.getConnection(DATABASE_URL);
            } catch(Exception ex) {
                System.out.println("Error getting connection");
                ex.printStackTrace();
                return;
            }
        }

        try {
            System.out.println("Adding " + CSV_TEST_FILE + "...");
            boolean res = addToDatabase(conn, csvFile);
            if(!res) {
                System.out.println("Adding " + CSV_TEST_FILE + " failed");
                return;
            }
        } catch(Exception ex) {
            System.out.println("Error filling database");
            ex.printStackTrace();
            return;
        }

        try {
            System.out.println("Printing...");
            boolean res = printDatabase(conn);
            if(!res) {
                System.out.println("Printing failed");
                return;
            }
        } catch(Exception ex) {
            System.out.println("Error printing database");
            ex.printStackTrace();
            return;
        }

        if(false) {
            getTableNames(conn);
        }

        if(false) {
            getColumnNames(conn, "android_metadata", true);
        }

        if(true) {
            System.out.println("Getting column names...");
            getColumnNames(conn, DATA_TABLE_NAME, false);
        }

        if(false) {
            getColumnNames(conn, "sqlite_sequence", true);
        }

        if(false) {
            getData(conn);
        }

        // Close the connection
        System.out.println("Closing the connection..." + LS + LS);
        closeConnection(conn);
    }
}
