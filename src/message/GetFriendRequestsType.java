package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.getFriendRequestsSize;
import static message.MessageConstants.getFriendRequestsType;

/**
 * This class creates a message that will get the friendship requests
 * This class extends the ReactMessage class
 */
public class GetFriendRequestsType  extends ReactMessage{
    /**
     * This is the GetFriendRequestType's constructor
     * @param message Message to be analyzed
     */
    GetFriendRequestsType(Message message) {
        super(message);
    }

    /**
     * This function builds the message needed
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;

        String[] params = message.getHeader().split(" ");
        if( params.length != getFriendRequestsSize || client.getClientID() == null )
            return ;
        List<String> friends = UserRequests.getFriendRequests(client.getClientID());
        if( friends == null ) return;
        client.getStreamMessage().write(new Message(getFriendRequestsType, friends));
    }

    protected void getMessageVariables(ClientConnection client) {
    }

    protected boolean query(ClientConnection client) {
        return true;
    }
}
