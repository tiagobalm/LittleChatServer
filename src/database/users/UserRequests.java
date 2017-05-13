package database.users;

import database.Database;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Nullable
    public static String getUsername(int userID) {
        String sql = "SELECT username FROM User WHERE username = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs  = pstmt.executeQuery();

            if ( rs != null && rs.next() )
                return rs.getString("username");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
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

    private static void insertMessages(int userID, int roomID, String content, String date){
       String sql = "INSERT INTO Message(userID, roomID, message, sentDate) VALUES (?, ?, ?, ?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            pstmt.setInt(2, roomID);
            pstmt.setString(3, content);
            pstmt.setString(4, date);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertFriends(int friend1, int friend2){
        String sql = "INSERT INTO Friend(firstUserID, secondUserID) VALUES (?, ?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, friend1);
            pstmt.setInt(2, friend2);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String SecondSql = "INSERT INTO Friend(firstUserID, secondUserID) VALUES (?, ?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(SecondSql)) {

            pstmt.setInt(1, friend2);
            pstmt.setInt(2, friend1);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateFriendshipStatus(int friend1, int friend2){
        String sql = "UPDATE Friend SET friendStatus = 1 WHERE firstUserID = ? AND secondUserID = ?;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, friend1);
            pstmt.setInt(2, friend2);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String SecondSql = "UPDATE Friend SET friendStatus = 1 WHERE firstUserID = ? AND secondUserID = ?;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(SecondSql)) {

            pstmt.setInt(1, friend2);
            pstmt.setInt(2, friend1);
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
                        "Room.roomID AS ID " +
                "FROM Room, UserRoom " +
                "WHERE UserRoom.userID = ? " +
                "AND ID = UserRoom.roomID";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs  = pstmt.executeQuery();

            List<String> rooms = new ArrayList<>();
            
            while (rs.next()) {
                int roomID = rs.getInt("ID");
                String roomName = rs.getString("name");
                rooms.add(roomID + "\0" + roomName);
            }
            return rooms;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static List<Integer> getRoomUsers(int roomID) {
        String sql =
                "SELECT userID " +
                "FROM UserRoom " +
                "WHERE UserRoom.roomID = ? ";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomID);
            ResultSet rs  = pstmt.executeQuery();

            List<Integer> rooms = new ArrayList<>();

            while (rs.next()) {
                int userID = rs.getInt("username");
                rooms.add(userID);
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

    @Nullable
    private static List<String> getFriends(int userID, boolean accepted) {
        String sql =
            "SELECT username " +
            "FROM User, Friend " +
            "WHERE friendStatus = ? " +
            "AND firstUserID = ? " +
            "AND secondUserID = User.userID";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accepted ? 1 : 0);
            pstmt.setInt(2, userID);
            ResultSet rs  = pstmt.executeQuery();

            List<String> friends = new ArrayList<>();

            while (rs.next())
                friends.add(rs.getString("username"));
            for( String str : friends )
                System.out.println(str);
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
