package message;

import communication.ClientConnection;
import database.users.UserRequests;

import java.io.IOException;

import static message.MessageConstants.changeRoomNameSize;
import static message.MessageConstants.changeRoomNameType;

public class ChangeRoomNameType extends ReactMessage {
    ChangeRoomNameType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] params = message.getHeader().split(" ");
        if( params.length != changeRoomNameSize || client.getClientID() == null )
            return;
        int roomID = Integer.parseInt(params[1]);
        String nName = message.getMessage();
        try {
            UserRequests.updateRoomName(roomID, nName);
        } catch(Error e) {
            send(client,
                new Message(changeRoomNameType + " " + roomID,
                        "False\0" + nName));
            return;
        }
        send(client,
                new Message(changeRoomNameType + " " + roomID,
                        "True\0" + nName));
    }
}
