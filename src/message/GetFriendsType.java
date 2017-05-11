package message;

import communication.ClientConnection;
import database.users.UserRequests;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.getFriendsSize;
import static message.MessageConstants.getFriendsType;

public class GetFriendsType extends ReactMessage{
    GetFriendsType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] params = message.getHeader().split(" ");
        if( params.length != getFriendsSize || client.getClientID() == null )
            return ;
        List<String> friends = UserRequests.getFriends(client.getClientID());
        client.getStreamMessage().write(new Message(getFriendsType, friends));
    }
}
