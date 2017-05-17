package database.users;

import database.Database;
import org.jetbrains.annotations.Nullable;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static database.Database.getSalt;
import static database.Database.getSecurePassword;

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

            byte[] salt = getSalt();

            String securePassword = getSecurePassword(password, salt);

            pstmt.setString(1, username);
            pstmt.setString(2, securePassword);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
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

            if ( rs.next() ){
                byte[] salt = new byte[0];
                try {
                    salt = getSalt();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                }

                String regeneratedPasswordToVerify = getSecurePassword(password, salt);
                if(regeneratedPasswordToVerify.equals(rs.getString("password")) ||
                        rs.getString("password").equals(password))
                    return true;
                return false;
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());

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
            System.out.println("SQLException: " + e.getMessage());

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

            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public static void insertMessages(int userID, int roomID, String content, String date) {
       String sql = "INSERT INTO Message(userID, roomID, message, sentDate) VALUES (?, ?, ?, ?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            pstmt.setInt(2, roomID);
            pstmt.setString(3, content);
            pstmt.setString(4, date);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }


    }

    public static void insertFriends(int friend1, int friend2) {
        String sql = "INSERT INTO Friend(firstUserID, secondUserID) VALUES (?, ?);";
        friendQuery(sql, friend1, friend2);
        friendQuery(sql, friend2, friend1);
    }

    public static void insertUserRoom(int userID, int roomID){
        String sql = "INSERT INTO UserRoom(userID, roomID) VALUES (?, ?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            pstmt.setInt(2, roomID);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertRoom(String roomName){
        String sql = "INSERT INTO Room(name) VALUES (?);";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, roomName);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateRoomName(int roomID, String newRoomName){
        String sql = "UPDATE Room SET name = ? WHERE roomID = ?;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRoomName);
            pstmt.setInt(2, roomID);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateFriendshipStatus(int friend1, int friend2){
        String sql = "UPDATE Friend SET friendStatus = 1 WHERE firstUserID = ? AND secondUserID = ?;";
        friendQuery(sql, friend1, friend2);
        friendQuery(sql, friend2, friend1);
    }

    private static void friendQuery(String query, int friend1, int friend2) {
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, friend2);
            pstmt.setInt(2, friend1);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public static void deleteUserConnection(int userID) {
        String sql = "DELETE FROM UserConnection WHERE userID = ?;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public static void deleteUserConnections() {
        String sql = "DELETE FROM UserConnection;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public static void deleteUserFromRoom(int userID, int roomID) {
        String sql = "DELETE FROM UserRoom WHERE roomID = ? AND userID = ?;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomID);
            pstmt.setInt(1, userID);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean userConnected(String username) {
        int userID = getUserID(username);
        String sql = "SELECT * FROM UserConnection WHERE userID = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs  = pstmt.executeQuery();

            if (rs.next())
                return true;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

        return false;
    }

    @Nullable
    public static String getUsername(int userID) {
        String sql = "SELECT username FROM User WHERE userID = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs  = pstmt.executeQuery();

            if ( rs != null && rs.next() )
                return rs.getString("username");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

        return null;
    }

    @Nullable
    public static List<String> getUserRooms(int userID) {
        String sql =
                "SELECT Room.name AS name, " +
                        "Room.roomID AS ID " +
                "FROM Room, UserRoom " +
                "WHERE UserRoom.userID = ? " +
                        "AND Room.roomID = UserRoom.roomID";

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
            System.out.println("SQLException: " + e.getMessage());
        }

        return null;
    }

    @Nullable
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
                int userID = rs.getInt("userID");
                rooms.add(userID);
            }
            return rooms;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
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
            System.out.println("SQLException: " + e.getMessage());
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
            System.out.println("SQLException: " + e.getMessage());
        }
        return null;
    }
}
