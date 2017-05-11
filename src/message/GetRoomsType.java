package message;

import communication.ClientConnection;
import database.users.UserRequests;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.getRoomsSize;
import static message.MessageConstants.getRoomsType;

public class GetRoomsType extends ReactMessage {
    GetRoomsType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] params = message.getHeader().split(" ");
        if( params.length != getRoomsSize || client.getClientID() == null )
            return ;
        List<String> rooms = UserRequests.getUserRooms(client.getClientID());
        client.getStreamMessage().write(new Message(getRoomsType, rooms));
    }
}
