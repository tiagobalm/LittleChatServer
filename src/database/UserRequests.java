package database;

import org.jetbrains.annotations.Nullable;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static database.Database.getSalt;
import static database.Database.getSecurePassword;

public class UserRequests {
    private static void basicUpdate(String sql, List<Object> params) throws SQLException {
        Queries.query(sql, params);
        try {
            Queries.executeUpdate();
        } catch (SQLException e) {
            Queries.close();
            throw e;
        }
        Queries.close();
    }


    public static boolean loginUser(String username, String password, String ip, int port) throws SQLException {
        if( checkPassword(username, password) && !userConnected(username) ) {
            insertUserConnection(username, ip, port);
            return true;
        }
        return false;
    }

    public static boolean registerUser(String username, String password, String ip, int port) throws SQLException {
        if( getUserID(username) >= 0 )
            return false;

        String sql = "INSERT INTO User(username, password) VALUES (?, ?);";
        List<Object> params = new ArrayList<>();
        params.add(username);
        params.add(password);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
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
            } catch (SQLException ignore) {}
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


    private static void insertUserConnection(String username, String ip, int port) throws SQLException {
        int userID = getUserID(username);
        String sql = "INSERT INTO UserConnection(userID, ip, port) VALUES (?, ?, ?);";

        List<Object> params = new ArrayList<>();
        params.add(userID);
        params.add(ip);
        params.add(port);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    public static void insertMessages(int userID, int roomID, String content, String date) throws SQLException {
       String sql = "INSERT INTO Message(userID, roomID, message, sentDate) VALUES (?, ?, ?, ?);";

        List<Object> params = new ArrayList<>();
        params.add(userID);
        params.add(roomID);
        params.add(content);
        params.add(date);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    public static void insertFriends(int friend1, int friend2) throws SQLException {
        String sql = "INSERT INTO Friend(firstUserID, secondUserID) VALUES (?, ?)";
        friendQuery(sql, friend1, friend2);
    }

    public static void insertUserRoom(int userID, int roomID) throws SQLException {
        String sql = "INSERT INTO UserRoom(userID, roomID) VALUES (?, ?)";
        List<Object> params = new ArrayList<>();
        params.add(userID);
        params.add(roomID);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    public static void insertRoom(String roomName) throws SQLException {
        String sql = "INSERT INTO Room(name) VALUES (?)";
        List<Object> params = new ArrayList<>();
        params.add(roomName);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }


    public static void updateRoomName(int roomID, String newRoomName) throws SQLException {
        String sql = "UPDATE Room SET name = ? WHERE roomID = ?";
        List<Object> params = new ArrayList<>();
        params.add(newRoomName);
        params.add(roomID);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    public static void updateFriendshipStatus(int friend1, int friend2) throws SQLException {
        String sql = "UPDATE Friend SET friendStatus = 1 WHERE firstUserID = ? AND secondUserID = ?;";
        insertFriends(friend1, friend2);
        friendQuery(sql, friend1, friend2);
        friendQuery(sql, friend2, friend1);
    }

    private static void friendQuery(String query, int friend1, int friend2) throws SQLException {
        List<Object> params = new ArrayList<>();
        params.add(friend2);
        params.add(friend1);

        synchronized (Queries.class) {
            basicUpdate(query, params);
        }
    }


    public static void deleteUserConnection(int userID) throws SQLException {
        String sql = "DELETE FROM UserConnection WHERE userID = ?;";
        List<Object> params = new ArrayList<>();
        params.add(userID);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    public static void deleteUserConnections() throws SQLException {
        String sql = "DELETE FROM UserConnection;";

        synchronized (Queries.class) {
            basicUpdate(sql, new ArrayList<>());
        }
    }

    public static void deleteUserFromRoom(int userID, int roomID) throws SQLException {
        String sql = "DELETE FROM UserRoom WHERE roomID = ? AND userID = ?;";
        List<Object> params = new ArrayList<>();
        params.add(roomID);
        params.add(userID);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }


    private static boolean userConnected(String username) {
        boolean result = false;

        int userID = getUserID(username);
        String sql = "SELECT * FROM UserConnection WHERE userID = ?";
        List<Object> params = new ArrayList<>();
        params.add(userID);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                if(Queries.getNext() != null)
                    result = true;
            } catch (SQLException ignore) {}
            Queries.close();
        }

        return result;
    }

    public static int getUserID(String username) {
        int result = -1;

        String sql = "SELECT userID FROM User WHERE username = ?";
        List<Object> params = new ArrayList<>();
        params.add(username);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                if((rs = Queries.getNext()) != null)
                    result = rs.getInt("userID");
            } catch (SQLException ignore) {}
            Queries.close();
        }

        return result;
    }

    @Nullable
    public static String getUsername(int userID) {
        String username = null;

        String sql = "SELECT username FROM User WHERE userID = ?";
        List<Object> params = new ArrayList<>();
        params.add(userID);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                if((rs = Queries.getNext()) != null)
                    username = rs.getString("username");
            } catch (SQLException ignore) {}
            Queries.close();
        }

        return username;
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
            } catch (SQLException ignore) { rooms = null; }
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
                if((rs = Queries.getNext()) != null)
                    name = rs.getString("name");
            } catch (SQLException ignore) {}
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
                while((rs = Queries.getNext()) != null)
                    rooms.add(rs.getInt("userID"));
            } catch (SQLException ignore) { rooms = null; }
            Queries.close();
        }

        return rooms;
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
        List<String> friends = new ArrayList<>();

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
        List<Object> params = new ArrayList<>();
        params.add(userID);
        params.add(userID);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                while((rs = Queries.getNext()) != null)
                    friends.add(rs.getString("username1") + "\0" +
                                rs.getString("username2"));
            } catch (SQLException ignore) { friends = null; }
            Queries.close();
        }

        return friends;
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
                while((rs = Queries.getNext()) != null)
                    friends.add(rs.getString("username"));
            } catch (SQLException ignore) { friends = null; }
            Queries.close();
        }

        return friends;
    }

    @Nullable
    public static List<String> getMessagesFromRoom(Integer roomID, Integer limit) {
        List<String> messages = new ArrayList<>();

        String sql =
                "SELECT username, message " +
                "FROM User, Message " +
                "WHERE roomID = ?" +
                "AND Message.userID = User.userID " +
                "LIMIT ?";
        List<Object> params = new ArrayList<>();
        params.add(roomID);
        params.add(limit);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                while((rs = Queries.getNext()) != null)
                    messages.add(rs.getString("username") + "\0" +
                                    rs.getString("message"));
            } catch (SQLException ignore) { messages = null; }
            Queries.close();
        }

        return messages;
    }
}
