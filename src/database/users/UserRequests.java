package database.users;

import database.Database;
import java.sql.*;

public class UserRequests {
    private static Connection getConn() {
        return Database.getInstance().getConn();
    }

    public static boolean loginUser(String username, String password, String ip, int port) {
        if( checkPassword(username, password) && !userConnected(username) ) {
            insertUserConnection(username, ip, port);
            return true;
        }
        return false;
    }

    public static void registerUser(String username, String password) {
        String sql = "INSERT INTO User(username, password) VALUES (?, ?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private static ResultSet getUserInfo(String username) {
        String sql = "SELECT userID FROM User WHERE username = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs  = pstmt.executeQuery();

            if (rs.next() )
                return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }


    public static boolean checkPassword(String username, String password) {
        try {
            ResultSet rs = getUserInfo(username);
            if (rs != null &&
                rs.next() &&
                rs.getString("password").equals(password) )
                return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    private static boolean insertUserConnection(String username, String ip, int port) {
        int userID;
        String sql = "INSERT INTO UserConnect(username, ip, port) VALUES (?, ?, ?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            ResultSet rs = getUserInfo(username);
            if ( rs == null || !rs.next() )
                return false;

            userID = rs.getInt("userID");

            pstmt.setInt(1, userID);
            pstmt.setString(2, ip);
            pstmt.setInt(3, port);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    private static boolean userConnected(String username) {
        String sql = "SELECT * FROM UserConnection WHERE username = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs  = pstmt.executeQuery();

            if (rs.next())
                return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

}
