package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static database.UserRequests.getUserID;
import static database.UserRequests.insertFriends;
import static message.MessageConstants.friendRequestSize;

/**
 * This class creates the friendship request's message
 * This class extends the ReactMessage class
 */
public class FriendRequestType extends ReactMessage {
    private int toUserID;
    private int fromUserID;

    /**
     * This is the FriendRequestType's constructor
     *
     * @param message Message that will be analyzed
     */
    FriendRequestType(Message message) {
        super(message);
    }

    /**
     * This function creates the message needed
     *
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != friendRequestSize || client.getClientID() == null )
            return ;
        if (!storeMessage(client))
            return;
        Message newMessage = new Message(parameters[0] + " " + parameters[2], "");
        notifyUser(newMessage, toUserID);
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        toUserID = getUserID(parameters[1]);
        fromUserID = getUserID(parameters[2]);
    }

    protected boolean query(ClientConnection client) {
        try {
            insertFriends(fromUserID, toUserID);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
        return true;
    }
}
