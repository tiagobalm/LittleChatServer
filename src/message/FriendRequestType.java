package message;

import communication.ClientConnection;

import java.io.IOException;

import static database.users.UserRequests.getUserID;
import static database.users.UserRequests.insertFriends;
import static message.MessageConstants.friendRequestSize;

public class FriendRequestType extends ReactMessage {
    FriendRequestType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != friendRequestSize || client.getClientID() == null )
            return ;
        int userID = getUserID(parameters[1]);
        insertFriends(client.getClientID(), userID);

        notifyUser(message, userID);
    }
}
