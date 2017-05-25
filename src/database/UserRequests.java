package database;

import message.Message;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static database.Database.getSalt;
import static database.Database.getSecurePassword;

/**
 * This class creates the user database's requests
 */
public class UserRequests {
    /**
     * This function creates a basic update
     *
     * @param sql    SQL statement that may contain one or more '?' IN parameter placeholders
     * @param params The request parameters
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
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

    /**
     * This function logs in a user
     *
     * @param username User's username
     * @param password User's password
     * @param ip       Connection's IP
     * @param port     Connection's port
     * @return true if the user is logged in (if the password is the same as the password saved in the database) or false otherwise
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static boolean loginUser(String username, String password, String ip, int port) throws SQLException {
        if( checkPassword(username, password) && !userConnected(username) ) {
            insertUserConnection(username, ip, port);
            return true;
        }
        return false;
    }

    /**
     * This function registers a user
     *
     * @param username User's username
     * @param password User's password
     * @param ip       Connection IP
     * @param port     Connection port
     * @return true if the user could register himself or false otherwise (if the username selected already exists)
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
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

        return true;
    }

    /**
     * This function verifies if the password written when a user logs in is the same as the password saved in the database
     *
     * @param username User's username
     * @param password User's password
     * @return true if the password is correct, false otherwise
     */
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

    /**
     * This function inserts a user's connection
     * @param username User's username
     * @param ip Connection IP
     * @param port Connection port
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void insertUserConnection(String username, String ip, int port) throws SQLException {
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

    /**
     * This function inserts chat's messages
     * @param userID User's identifier
     * @param roomID Chat room's identifier
     * @param content Message's content
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void insertMessages(int userID, int roomID, long date, String content) throws SQLException {
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

    /**
     * This function inserts a friendship
     * @param friend1 First friend's identifier
     * @param friend2 Second friend's identifier
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void insertFriends(int friend1, int friend2) throws SQLException {
        String sql = "INSERT INTO Friend(firstUserID, secondUserID) VALUES (?, ?)";
        friendQuery(sql, friend1, friend2);
    }

    /**
     * This function inserts a user into a chat room
     * @param userID User's identifier
     * @param roomID Room's identifier
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void insertUserRoom(int userID, int roomID) throws SQLException {
        String sql = "INSERT INTO UserRoom(userID, roomID) VALUES (?, ?)";
        List<Object> params = new ArrayList<>();
        params.add(userID);
        params.add(roomID);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    /**
     * This function inserts a chat room in the database
     * @param roomName Chat room's name
     * @return The room created identifier
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static Integer insertRoom(String roomName) throws SQLException {
        Integer roomID = -1;
        String sql = "INSERT INTO Room(name) VALUES (?)";
        List<Object> params = new ArrayList<>();
        params.add(roomName);

        synchronized (Queries.class) {
            basicUpdate(sql, params);

            sql = "SELECT max(roomID) AS ID FROM Room WHERE name = ?";

            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                if((rs = Queries.getNext()) != null)
                    roomID = Integer.parseInt(rs.getString("ID"));
            } catch (SQLException ignore) {}
            Queries.close();
        }

        return roomID;
    }

    /**
     * This function updates the chat room's name
     * @param roomID Chat room's identifier
     * @param newRoomName New chat room's name
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void updateRoomName(int roomID, String newRoomName) throws SQLException {
        String sql = "UPDATE Room SET name = ? WHERE roomID = ?";
        List<Object> params = new ArrayList<>();
        params.add(newRoomName);
        params.add(roomID);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    /**
     * This function updates the friendship status
     * @param friend1 First friend's identifier
     * @param friend2 Second friend's identifier
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void updateFriendshipStatus(int friend1, int friend2) throws SQLException {
        String sql = "UPDATE Friend SET friendStatus = 1 WHERE firstUserID = ? AND secondUserID = ?;";
        insertFriends(friend1, friend2);
        friendQuery(sql, friend1, friend2);
        friendQuery(sql, friend2, friend1);
    }

    /**
     * This function creates a friend query to be used in other functions
     * @param query Query to be used
     * @param friend1 First friend's identifier
     * @param friend2 Second friend's identifier
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    private static void friendQuery(String query, int friend1, int friend2) throws SQLException {
        List<Object> params = new ArrayList<>();
        params.add(friend1);
        params.add(friend2);

        synchronized (Queries.class) {
            basicUpdate(query, params);
        }
    }

    /**
     * This function deletes the friendship table
     * @param friend1 First friend's identifier
     * @param friend2 Second friend's identifier
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void deleteFriendshipStatus(int friend1, int friend2) throws SQLException {
        String sql = "DELETE FROM Friend WHERE friendStatus = 0 AND firstUserID = ? AND secondUserID = ?;";
        List<Object> params = new ArrayList<>();
        params.add(friend1);
        params.add(friend2);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    /**
     * This function removes a user's connection
     * @param userID User's identifier
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void deleteUserConnection(int userID) throws SQLException {
        String sql = "DELETE FROM UserConnection WHERE userID = ?;";
        List<Object> params = new ArrayList<>();
        params.add(userID);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    /**
     * This function removes all the user's connection from the database
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void deleteUserConnections() throws SQLException {
        String sql = "DELETE FROM UserConnection;";

        synchronized (Queries.class) {
            basicUpdate(sql, new ArrayList<>());
        }
    }

    /**
     * This function removes a user from a chat room
     * @param userID User's identifier
     * @param roomID User's chat room
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    public static void deleteUserFromRoom(int userID, int roomID) throws SQLException {
        String sql = "DELETE FROM UserRoom WHERE roomID = ? AND userID = ?;";
        List<Object> params = new ArrayList<>();
        params.add(roomID);
        params.add(userID);

        synchronized (Queries.class) {
            basicUpdate(sql, params);
        }
    }

    /**
     * This function verifies if a user is connected already
     * @param username User's username
     * @return true if the user is already connected, false otherwise
     */
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

    /**
     * This function gets the user's identifier
     * @param username User's username
     * @return The user's identifier
     */
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

    /**
     * This function gets the user's username
     * @param userID User's identifier
     * @return The user's username
     */
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

    /**
     * This function gets all the chat rooms that a user is in
     * @param userID User's identifier
     * @return A list with all the chat room of a user
     */
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

    /**
     * This function gets the chat room's name
     * @param roomID Chat room's identifier
     * @return The chat's room name
     */
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

    /**
     * This function gets the users of a chat room
     * @param roomID Room's identifier
     * @return A list with the chat room's users
     */
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

    /**
     * This function gets all the friends of a user
     * @param userID User's identifier
     * @return A list with all the friends of a user
     */
    @Nullable
    public static List<String> getFriends(int userID) {
        return selectFriends(userID);
    }

    /**
     * The function gets all the friendship requests
     * @param userID User's identifier
     * @return A list with all the friendship requests
     */
    @Nullable
    public static List<String> getFriendRequests(int userID) {
        return selectFriendRequests(userID);
    }

    /**
     * This function gets all the friendship requests of a user
     * @param userID User's identifier
     * @return A list with all the friendship requests of a user
     */
    @Nullable
    private static List<String> selectFriendRequests(int userID) {
        List<String> friends = new ArrayList<>();

        String sql1 =
                "SELECT User1.username AS username1, " +
                        "User2.username AS username2 " +
                        "FROM User AS User1, " +
                        "User AS User2, " +
                        "Friend " +
                        "WHERE friendStatus = 0 " +
                        "AND firstUserID  = ? " +
                        "AND firstUserID = User1.userID " +
                        "AND secondUserID = User2.userID";

        String sql2 =
                "SELECT User1.username AS username1, " +
                        "User2.username AS username2 " +
                        "FROM User AS User1, " +
                        "User AS User2, " +
                        "Friend " +
                        "WHERE friendStatus = 0 " +
                        "AND secondUserID  = ? " +
                        "AND firstUserID = User1.userID " +
                        "AND secondUserID = User2.userID";
        List<Object> params = new ArrayList<>();
        params.add(userID);

        synchronized (Queries.class) {
            for(int i = 0; i < 2; i++) {
                if(i == 0)
                    Queries.query(sql1, params);
                else
                    Queries.query(sql2, params);

                try {
                    Queries.execute();
                    ResultSet rs;
                    while((rs = Queries.getNext()) != null)
                        friends.add(rs.getString("username1") + "\0" +
                                rs.getString("username2"));
                } catch (SQLException ignore) { friends = null; }
                Queries.close();
            }
        }

        return friends;
    }

    /**
     * This function selects all the friends of a user
     * @param userID User's identifier
     * @return A list with all the user's friendships
     */
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

    /**
     * This function gets the messages from a chat room
     * @param roomID Room's identifier
     * @param limit Number maximum of messages
     * @return A list with the messages from a chat room
     */
    @Nullable
    public static List<String> getMessagesFromRoom(Integer roomID, Integer limit) {
        List<String> messages = new ArrayList<>();

        String sql =
                "SELECT username, sentDate, message " +
                        "FROM User, Message " +
                        "WHERE roomID = ?" +
                        "AND Message.userID = User.userID " +
                        "ORDER BY sentDate LIMIT ?";
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
                            rs.getInt("sentDate") + "\0" +
                            rs.getString("message"));
            } catch (SQLException ignore) { messages = null; }
            Queries.close();
        }

        return messages;
    }

    public static void insertUnsentMessage(Message message) throws SQLException {
        String sql = "INSERT INTO MessageClass(header, message) VALUES (?, ?)";
        List<Object> params = new ArrayList<>();
        params.add(message.getHeader());
        params.add(message.getMessage());

        System.out.println("Storing unsent");
        System.out.println(message.getHeader());
        System.out.println(message.getMessage());

        synchronized (Queries.class) {
            basicUpdate(sql, params);
            params.clear();

            int messageID = -1;
            sql = "SELECT messageClassID FROM MessageClass ORDER BY messageClassID DESC LIMIT 1";
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                if ((rs = Queries.getNext()) != null)
                    messageID = rs.getInt("messageClassID");
            } catch (SQLException ignore) {
            }

            sql = "INSERT INTO StringList(messageClassID, string) VALUES (?, ?)";
            params.add(messageID);

            if (message.getOptionalMessage() != null)
                for (String str : message.getOptionalMessage()) {
                    params.add(str);
                    basicUpdate(sql, params);
                    params.remove(params.size());
                }
        }
    }

    public static void deleteUnsentMessages() {
        String sql = "DELETE FROM MessageClass";

        synchronized (Queries.class) {
            try {
                basicUpdate(sql, new ArrayList<>());
            } catch (SQLException ignore) {
            }
        }
    }

    public static List<Message> getUnsentMessages() {
        List<Map.Entry<Integer, Message>> unsentMessages = new ArrayList<>();
        List<Message> messages = new ArrayList<>();

        String sql = "SELECT * FROM MessageClass";

        synchronized (Queries.class) {
            Queries.query(sql, new ArrayList<>());
            try {
                Queries.execute();
                ResultSet rs;
                while((rs = Queries.getNext())!=null) {
                    int messageID = rs.getInt("messageClassID");
                    String header = rs.getString("header");
                    String data = rs.getString("message");
                    Message message = new Message(header,data);
                    unsentMessages.add(new AbstractMap.SimpleEntry<>(messageID, message));
                }
            } catch (SQLException ignore) {
            }
            Queries.close();
        }

        for( Map.Entry<Integer, Message> entry : unsentMessages ) {
            Message m = getStringListMessage(entry.getKey(), entry.getValue());
            messages.add(m);
        }
        return messages;
    }

    private static Message getStringListMessage(int messageID, Message message) {
        List<String> allOptionalMessages = new ArrayList<>();
        String  sql = "SELECT string FROM StringList WHERE messageClassID = ?";
        List<Object> params = new ArrayList<>();
        params.add(messageID);

        synchronized (Queries.class) {
            Queries.query(sql, params);
            try {
                Queries.execute();
                ResultSet rs;
                while((rs = Queries.getNext())!=null) {
                    String data = rs.getString("string");
                    allOptionalMessages.add(data);
                }
            } catch (SQLException ignore) {
            }
            Queries.close();
        }

        message.setOptionalMessage(allOptionalMessages);
        return message;
    }
}
