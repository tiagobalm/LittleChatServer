package message;

import communication.ClientConnection;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.getFriendsSize;
import static message.MessageConstants.getFriendsType;

/**
 * This class creates a message that gets all friends
 * This class extends the ReactMessage class
 */
public class GetFriendsType extends ReactMessage{
    /**
     * This is the GetFriendsType's constructor
     *
     * @param message Message to be analyzed
     */
    GetFriendsType(Message message) {
        super(message);
    }

    /**
     * This function creates the message needed
     *
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(@NotNull ClientConnection client) throws IOException {
        String[] params = message.getHeader().split(" ");
        if( params.length != getFriendsSize || client.getClientID() == null )
            return ;
        List<String> friends = UserRequests.getFriends(client.getClientID());
        assert client.getStreamMessage() != null;
        client.getStreamMessage().write(new Message(getFriendsType, friends));
    }

    protected void getMessageVariables() {
    }

    protected boolean query(ClientConnection client) {
        return true;
    }
}
