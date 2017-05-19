package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static database.UserRequests.getUserID;
import static database.UserRequests.insertFriends;
import static message.MessageConstants.friendRequestSize;

public class FriendRequestType extends ReactMessage {
    private int userID;

    FriendRequestType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != friendRequestSize || client.getClientID() == null )
            return ;
        if (!storeMessage(client))
            return;
        Message newMessage = new Message(parameters[0] + " " + UserRequests.getUsername(client.getClientID()), "");
        notifyUser(newMessage, userID);
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        userID = getUserID(parameters[1]);
    }

    protected boolean query(ClientConnection client) {
        try {
            insertFriends(client.getClientID(), userID);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
        return true;
    }
}
