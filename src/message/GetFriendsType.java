package message;

import communication.ClientConnection;
import database.UserRequests;

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
        if( checkToServer(client) )
            return;

        String[] params = message.getHeader().split(" ");
        if( params.length != getFriendsSize || client.getClientID() == null )
            return ;
        List<String> friends = UserRequests.getFriends(client.getClientID());
        client.getStreamMessage().write(new Message(getFriendsType, friends));
    }

    protected void getMessageVariables(ClientConnection client) {
    }

    protected boolean query(ClientConnection client) {
        return true;
    }
}
