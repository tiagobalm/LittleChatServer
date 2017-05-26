package message;


import communication.ClientConnection;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.getMessagesSize;
import static message.MessageConstants.getMessagesType;

/**
 * This class creates a message to get all the messages
 * This class extends the ReactMessage class
 */
public class GetMessagesType extends ReactMessage{
    /**
     * Maximum number of messages
     */
    private static final int nMessage = 50;

    /**
     * This is the GetMessagesType's constructor
     *
     * @param message Message that will be used
     */
    GetMessagesType(Message message) {
        super(message);
    }

    /**
     * This function builds the message needed
     *
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(@NotNull ClientConnection client) throws IOException {
        String[] params = message.getHeader().split(" ");
        if( params.length != getMessagesSize || client.getClientID() == null )
            return ;
        int roomID = Integer.parseInt(params[1]);
        List<String> messages = UserRequests.getMessagesFromRoom(roomID, nMessage);
        if (messages == null) return;
        assert client.getStreamMessage() != null;
        client.getStreamMessage().write(
                new Message(getMessagesType + " " + roomID, messages));
    }

    protected void getMessageVariables() {
    }

    protected boolean query(ClientConnection client) {
        return true;
    }
}
