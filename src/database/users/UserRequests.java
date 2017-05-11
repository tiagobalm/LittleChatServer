package database.users;

import database.Database;
import org.jetbrains.annotations.Nullable;

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
            insertUserConnection(username, ip, port);
            return true;
        }
        return false;
    }

    public static boolean registerUser(String username, String password, String ip, int port) {
        if( getUserID(username) >= 0 )
            return false;

        String sql = "INSERT INTO User(username, password) VALUES (?, ?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        insertUserConnection(username, ip, port);

        return true;
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

    public static int getUserID(String username) {
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

    public static void deleteUserConnection(int userID) {
        String sql = "DELETE FROM UserConnection WHERE userID = ?;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
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

    @Nullable
    public static List<String> getUserRooms(int userID) {
        String sql =
                "SELECT Room.name AS name, " +
                        "Room.roomID AS roomID " +
                "FROM Room, UserRoom " +
                "WHERE UserRoom.userID = ? " +
                "AND roomID = UserRoom.roomID";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs  = pstmt.executeQuery();

            List<String> rooms = new ArrayList<>();
            
            while (rs.next()) {
                int roomID = rs.getInt("roomID");
                String roomName = rs.getString("name");
                rooms.add(roomID + "\0" + roomName);
            }
            return rooms;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Nullable
    public static List<String> getFriends(int userID) {
        return getFriends(userID, true);
    }

    @Nullable
    public static List<String> getFriendRequests(int userID) {
        return getFriends(userID, false);
    }

    private static List<String> getFriends(int userID, boolean accepted) {
        String sql =
            "SELECT username " +
                    "FROM User, Friend " +
                    "WHERE firstUserID = ? " +
                    "AND friendStatus = ? " +
                    "AND (" +
                    "firstUserID = User.userID " +
                    "OR secondUserID = User.userID" +
                    ")";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            pstmt.setBoolean(2, accepted);
            ResultSet rs  = pstmt.executeQuery();

            List<String> friends = new ArrayList<>();

            while (rs.next())
                friends.add(rs.getString("username"));
            return friends;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Nullable
    public static List<String> getMessagesFromRoom(Integer roomID, Integer limit) {
        String sql =
                "SELECT username, message " +
                "FROM User, Message " +
                "WHERE roomID = ?" +
                "AND Message.userID = User.userID " +
                "LIMIT ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomID);
            pstmt.setInt(2, limit);
            ResultSet rs  = pstmt.executeQuery();

            List<String> messages = new ArrayList<>();
            while (rs.next()) {
                String username = rs.getString("username");
                String message = rs.getString("message");
                messages.add(username + "\0" + message);
            }
            return messages;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
