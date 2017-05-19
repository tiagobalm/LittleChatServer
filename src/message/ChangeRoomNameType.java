package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static database.UserRequests.getRoomUsers;
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
        } catch(SQLException e) {
            send(client,
                new Message(changeRoomNameType + " " + roomID,
                        "False\0" + nName));
            return;
        }
        send(new Message(changeRoomNameType + " " + roomID,"True\0" + nName), roomID);
    }

    private void send(Message message, int roomID) throws IOException {
        List<Integer> roomUsers = getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            notifyUser(message, id);
    }

}
