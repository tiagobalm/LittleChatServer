package message;


import communication.ClientConnection;
import database.users.UserRequests;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.*;

public class GetMessagesType extends ReactMessage{
    private static final int nMessage = 50;

    GetMessagesType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] params = message.getHeader().split(" ");
        if( params.length != getMessagesSize || client.getClientID() == null )
            return ;
        int roomID = Integer.parseInt(params[1]);
        List<String> messages = UserRequests.getMessagesFromRoom(roomID, nMessage);
        client.getStreamMessage().write(
                new Message(getMessagesType + " " + roomID, messages));
    }
}
