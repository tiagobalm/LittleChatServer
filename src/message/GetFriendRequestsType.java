package message;

import communication.ClientConnection;
import database.users.UserRequests;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.getFriendRequestsType;
import static message.MessageConstants.getFriendRequestsSize;

public class GetFriendRequestsType  extends ReactMessage{
    GetFriendRequestsType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] params = message.getHeader().split(" ");
        if( params.length != getFriendRequestsSize || client.getClientID() == null )
            return ;
        List<String> friends = UserRequests.getFriendRequests(client.getClientID());
        client.getStreamMessage().write(new Message(getFriendRequestsType, friends));
    }
}
