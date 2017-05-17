package database;

import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Queries {
    public static Connection conn = null;
    public static PreparedStatement pstmt = null;
    public static ResultSet rs;

    public static void prepare(String sql) {
        try {
            conn = Database.getInstance().getConn();
            pstmt  = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Could not connect to the data base");
        }
    }

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

    public static void query(String sql, List<Object> params) {
        prepare(sql);
        setParams(params);
    }

    public synchronized static void execute() throws SQLException {
        rs = pstmt.executeQuery();
    }

    public synchronized static void executeUpdate() throws SQLException {
        pstmt.executeUpdate();
    }


    @Nullable
    public static ResultSet getNext() {
        try {
            return rs.next() ? rs : null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Could not set query parameters");
        }
    }

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
