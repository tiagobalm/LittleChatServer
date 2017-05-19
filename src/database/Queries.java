package database;

import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class creates the queries to used in the database requests
 */
public class Queries {
    /**
     * Database's connection
     */
    public static Connection conn = null;
    /**
     * Database's prepared statements
     */
    public static PreparedStatement pstmt = null;
    /**
     * Set with the request's result
     */
    public static ResultSet rs;

    /**
     * This function prepares the database's connection
     * @param sql SQL statement that may contain one or more '?' IN parameter placeholders
     */
    public static void prepare(String sql) {
        try {
            conn = Database.getInstance().getConn();
            pstmt  = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Could not connect to the data base");
        }
    }

    /**
     * This function sets the request parameters
     * @param params The request parameters
     */
    public static void setParams(List<Object> params) {
        int i = 1;
        try {
            for( Object o : params ) {
                if( o instanceof Integer )
                    pstmt.setInt(i, (Integer) o);
                else if( o instanceof String )
                    pstmt.setString(i, (String) o);
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Could not set query parameters");
        }
    }

    /**
     * This function prepares the statements and sets the parameters
     * @param sql SQL statement
     * @param params Request's parameters
     */
    public static void query(String sql, List<Object> params) {
        prepare(sql);
        setParams(params);
    }

    /**
     * This function executes the queries
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public synchronized static void execute() throws SQLException {
        rs = pstmt.executeQuery();
    }

    /**
     * This function executes the updates
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public synchronized static void executeUpdate() throws SQLException {
        pstmt.executeUpdate();
    }

    /**
     * Gets the set with the results
     * @return The set with the results
     */
    @Nullable
    public static ResultSet getNext() {
        try {
            return rs.next() ? rs : null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Could not set query parameters");
        }
    }

    /**
     * This function closes the ResultSet and the PreparedStatement
     */
    public static void close() {
        try {
            if( pstmt != null )
                    pstmt.close();
            if( rs != null )
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Could not set query parameters");
        }
    }
}
