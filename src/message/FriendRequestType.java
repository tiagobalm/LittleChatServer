package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static database.UserRequests.getUserID;
import static database.UserRequests.insertFriends;
import static message.MessageConstants.friendRequestSize;

public class FriendRequestType extends ReactMessage {
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
        int userID = getUserID(parameters[1]);
        try {
            insertFriends(client.getClientID(), userID);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return;
        }

        Message newMessage = new Message(parameters[0] + " " + UserRequests.getUsername(client.getClientID()), "");
        notifyUser(newMessage, userID);
    }
}
