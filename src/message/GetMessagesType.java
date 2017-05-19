package message;


import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.getMessagesSize;
import static message.MessageConstants.getMessagesType;

public class GetMessagesType extends ReactMessage{
    private static final int nMessage = 50;

    GetMessagesType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;

        String[] params = message.getHeader().split(" ");
        if( params.length != getMessagesSize || client.getClientID() == null )
            return ;
        int roomID = Integer.parseInt(params[1]);
        List<String> messages = UserRequests.getMessagesFromRoom(roomID, nMessage);
        if (messages == null) return;
        client.getStreamMessage().write(
                new Message(getMessagesType + " " + roomID, messages));
    }

    protected void getMessageVariables(ClientConnection client) {
    }

    protected boolean query(ClientConnection client) {
        return true;
    }
}
