package database.users;

import database.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRequests {
    private static Connection getConn() {
        try {
            return Database.getInstance().getConn();
        } catch (SQLException e) {
            throw new Error("Could not connect to the data base");
        }
    }

    public static boolean loginUser(String username, String password, String ip, int port) {
        if( checkPassword(username, password) && !userConnected(username) ) {
            //insertUserConnection(username, ip, port);
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

    private static boolean checkPassword(String username, String password) {
        String sql = "SELECT password FROM User WHERE username = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs  = pstmt.executeQuery();

            if ( rs.next() )
                return rs.getString("password").equals(password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    private static int getUserID(String username) {
        String sql = "SELECT userID FROM User WHERE username = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs  = pstmt.executeQuery();

            if ( rs != null && rs.next() ) {
                return rs.getInt("userID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return -1;
    }

    private static void insertUserConnection(String username, String ip, int port) {
        int userID = getUserID(username);
        String sql = "INSERT INTO UserConnection(userID, ip, port) VALUES (?, ?, ?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            pstmt.setString(2, ip);
            pstmt.setInt(3, port);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteUserConnections() {
        String sql = "DELETE FROM UserConnection;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean userConnected(String username) {
        int userID = getUserID(username);
        String sql = "SELECT * FROM UserConnection WHERE userID = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs  = pstmt.executeQuery();

            if (rs.next())
                return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static Integer[] getUserRooms(int userID) {
    
        String sql = "SELECT roomID FROM UserRoom WHERE UserID = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(userID, userID);
            ResultSet rs  = pstmt.executeQuery();

            List<Integer> rooms = new ArrayList<Integer>();
            
            if (rs.next())
                rooms.add(rs.getInt("roomID"));
            Integer[] roomsArray = new Integer[rooms.size()];
            roomsArray = rooms.toArray(roomsArray);
            return roomsArray;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static Integer[] getFriends(int userID) {

        String sql = "SELECT secondUserID FROM Friend WHERE firstUserID = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(userID, userID);
            ResultSet rs  = pstmt.executeQuery();

            List<Integer> friends = new ArrayList<Integer>();

            if (rs.next())
                friends.add(rs.getInt("secondUserID"));
            Integer[] friendsArray = new Integer[friends.size()];
            friendsArray = friends.toArray(friendsArray);
            return friendsArray;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
