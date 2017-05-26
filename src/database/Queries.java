package database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class creates the queries to used in the database requests
 */
class Queries {
    /**
     * Database's prepared statements
     */
    @Nullable
    private static PreparedStatement pstmt = null;
    /**
     * Set with the request's result
     */
    private static ResultSet rs;

    /**
     * This function prepares the database's connection
     *
     * @param sql SQL statement that may contain one or more '?' IN parameter placeholders
     */
    private static void prepare(String sql) {
        try {
            Connection conn = Database.getInstance().getConn();
            pstmt  = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Could not connect to the data base");
        }
    }

    /**
     * This function sets the request parameters
     *
     * @param params The request parameters
     */
    private static void setParams(@NotNull List<Object> params) {
        int i = 1;
        try {
            for( Object o : params ) {
                if (o instanceof Integer) {
                    assert pstmt != null;
                    pstmt.setInt(i, (Integer) o);
                } else if (o instanceof String) {
                    assert pstmt != null;
                    pstmt.setString(i, (String) o);
                } else if (o instanceof Long) {
                    assert pstmt != null;
                    pstmt.setLong(i, (Long) o);
                }
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Could not set query parameters");
        }
    }

    /**
     * This function prepares the statements and sets the parameters
     *
     * @param sql    SQL statement
     * @param params Request's parameters
     */
    public static void query(String sql, @NotNull List<Object> params) {
        prepare(sql);
        setParams(params);
    }

    /**
     * This function executes the queries
     *
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    synchronized static void execute() throws SQLException {
        assert pstmt != null;
        rs = pstmt.executeQuery();
    }

    /**
     * This function executes the updates
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    synchronized static void executeUpdate() throws SQLException {
        assert pstmt != null;
        pstmt.executeUpdate();
    }

    /**
     * Gets the set with the results
     * @return The set with the results
     */
    @Nullable
    static ResultSet getNext() {
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
    static void close() {
        try {
            if (pstmt != null)
                pstmt.close();
            if( rs != null )
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Could not set query parameters");
        }
    }
}
