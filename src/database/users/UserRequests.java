package database.users;

import database.Database;
import database.Queries;
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
import static database.Queries.getNext;

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
        List<Object> params = new ArrayList<>();
        params.add(username);
        params.add(password);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {Queries.executeUpdate();}
            catch (SQLException e) {return false;}
            Queries.close();
        }

        insertUserConnection(username, ip, port);
        return true;
    }

    private static boolean checkPassword(String username, String password) {
        String pass = null;
        String sql = "SELECT password FROM User WHERE username = ?";
        List<Object> params = new ArrayList<>();
        params.add(username);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                if((rs = Queries.getNext()) != null)
                    pass = rs.getString("password");
            } catch (SQLException e) {return false;}
            Queries.close();
        }

        try {
            byte[] salt;
            salt = getSalt();
            String regeneratedPasswordToVerify = getSecurePassword(password, salt);
            if(pass != null &&
                    (pass.equals(password) ||
                    pass.equals(regeneratedPasswordToVerify)))
                return true;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
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
                int result = rs.getInt("userID");
                pstmt.close();
                rs.close();
                return result;
            }
            pstmt.close();
            if( rs != null )
                rs.close();
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
            pstmt.close();
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
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }


    }

    public static void insertFriends(int friend1, int friend2) {
        String sql = "INSERT INTO Friend(firstUserID, secondUserID) VALUES (?, ?);";
        friendQuery(sql, friend1, friend2);
    }

    public static void insertUserRoom(int userID, int roomID) throws SQLException {
        String sql = "INSERT INTO UserRoom(userID, roomID) VALUES (?, ?);";
        List<Object> params = new ArrayList<>();
        params.add(userID);
        params.add(roomID);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            Queries.executeUpdate();
            Queries.close();
        }
    }

    public static void insertRoom(String roomName) throws SQLException {
        String sql = "INSERT INTO Room(name) VALUES (?);";
        List<Object> params = new ArrayList<>();
        params.add(roomName);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            Queries.executeUpdate();
            Queries.close();
        }
    }


    public static void updateRoomName(int roomID, String newRoomName){
        String sql = "UPDATE Room SET name = ? WHERE roomID = ?;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {
            pstmt.setString(1, newRoomName);
            pstmt.setInt(2, roomID);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateFriendshipStatus(int friend1, int friend2){
        String sql = "UPDATE Friend SET friendStatus = 1 WHERE firstUserID = ? AND secondUserID = ?;";
        insertFriends(friend1, friend2);
        friendQuery(sql, friend1, friend2);
        friendQuery(sql, friend2, friend1);
    }


    private static void friendQuery(String query, int friend1, int friend2) {
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, friend2);
            pstmt.setInt(2, friend1);
            pstmt.executeUpdate();
            pstmt.close();
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
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public static void deleteUserConnections() {
        String sql = "DELETE FROM UserConnection;";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            pstmt.close();
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
            pstmt.close();
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

            if (rs.next()) {
                pstmt.close();
                rs.close();
                return true;
            }

            pstmt.close();
            rs.close();
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

            if ( rs != null && rs.next() ) {
                pstmt.close();
                rs.close();
                return rs.getString("username");
            }
            pstmt.close();
            if( rs != null )
                rs.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

        return null;
    }

    @Nullable
    public static List<String> getUserRooms(int userID) {
        List<String> rooms = new ArrayList<>();
        String sql =
                "SELECT Room.name AS name, " +
                        "Room.roomID AS ID " +
                "FROM Room, UserRoom " +
                "WHERE UserRoom.userID = ? " +
                "AND Room.roomID = UserRoom.roomID";
        List<Object> params = new ArrayList<>();
        params.add(userID);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                while ((rs = Queries.getNext()) != null )
                    rooms.add(rs.getInt("ID") + "\0" + rs.getString("name"));
            } catch (SQLException e) {
                e.printStackTrace();
                Queries.close();
                return null;
            }

            Queries.close();
        }

        return rooms;
    }

    @Nullable
    public static String getRoomName(int roomID) {
        String name = null;
        String sql =
                "SELECT name " +
                "FROM Room " +
                "WHERE roomID = ?";
        List<Object> params = new ArrayList<>();
        params.add(roomID);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                if((rs = Queries.getNext()) != null )
                    name = rs.getString("name");
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }

            Queries.close();
        }

        return name;
    }

    @Nullable
    public static List<Integer> getRoomUsers(int roomID) {
        List<Integer> rooms = new ArrayList<>();

        String sql =
                "SELECT userID " +
                "FROM UserRoom " +
                "WHERE UserRoom.roomID = ? ";
        List<Object> params = new ArrayList<>();
        params.add(roomID);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                while((rs = Queries.getNext()) != null) {
                    rooms.add(rs.getInt("userID"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        return rooms;
        /*
        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomID);
            ResultSet rs  = pstmt.executeQuery();

            List<Integer> rooms = new ArrayList<>();

            while (rs.next())
                rooms.add(rs.getInt("userID"));
            pstmt.close();
            rs.close();
            return rooms;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

        return null;
        */
    }

    @Nullable
    public static List<String> getRoomUsersUsernames(int roomID) {
        String sql =
                "SELECT username " +
                "FROM User, UserRoom " +
                "WHERE UserRoom.roomID = ? " +
                "AND User.userID = UserRoom.userID";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomID);
            ResultSet rs  = pstmt.executeQuery();
            List<String> rooms = new ArrayList<>();
            while (rs.next())
                rooms.add(rs.getString("username"));
            pstmt.close();
            rs.close();
            return rooms;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

        return null;
    }

    @Nullable
    public static List<String> getFriends(int userID) {
        return selectFriends(userID);
    }

    @Nullable
    public static List<String> getFriendRequests(int userID) {
        return selectFriendRequests(userID);
    }

    @Nullable
    private static List<String> selectFriendRequests(int userID) {
        String sql =
                "SELECT User1.username AS username1, " +
                        "User2.username AS username2 " +
                "FROM User AS User1, " +
                        "User AS User2, " +
                        "Friend " +
                "WHERE friendStatus = 0 " +
                "AND (firstUserID  = ? OR secondUserID = ?) " +
                "AND firstUserID = User1.userID " +
                "AND secondUserID = User2.userID";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(1, userID);
            ResultSet rs  = pstmt.executeQuery();
            List<String> friends = new ArrayList<>();
            while (rs.next()) {
                String str = rs.getString("username1") + "\0" +
                            rs.getString("username2");
                friends.add(str);
            }
            pstmt.close();
            rs.close();
            return friends;
        } catch (SQLException e) {System.out.println("SQLException: " + e.getMessage());}
        return null;
    }

    @Nullable
    private static List<String> selectFriends(int userID) {
        List<String> friends = new ArrayList<>();
        String sql =
            "SELECT User.username " +
            "FROM User, Friend " +
            "WHERE friendStatus = 1 " +
            "AND firstUserID = ? " +
            "AND secondUserID = User.userID";
        List<Object> params = new ArrayList<>();
        params.add(userID);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                while ((rs = Queries.getNext()) != null)
                    friends.add(rs.getString("username"));
            } catch (SQLException e) {
                e.printStackTrace();
                Queries.close();
                return null;
            }

            Queries.close();
        }

        return friends;
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
            pstmt.close();
            rs.close();
            return messages;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        return null;
    }
}
