package message;

import communication.ClientConnection;
import database.UserRequests;

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
        if( checkToServer(client) )
            return;

        String[] params = message.getHeader().split(" ");
        if( params.length != getFriendRequestsSize || client.getClientID() == null )
            return ;
        List<String> friends = UserRequests.getFriendRequests(client.getClientID());
        if( friends == null ) return;
        for(String str: friends) System.out.println(str);
        client.getStreamMessage().write(new Message(getFriendRequestsType, friends));
    }
}
