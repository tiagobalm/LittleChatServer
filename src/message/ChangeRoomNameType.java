package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static message.MessageConstants.changeRoomNameSize;
import static message.MessageConstants.changeRoomNameType;

public class ChangeRoomNameType extends ReactMessage {
    private int roomID;
    private String nName;

    ChangeRoomNameType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;

        String[] params = message.getHeader().split(" ");
        if( params.length != changeRoomNameSize || client.getClientID() == null )
            return;
        if (!storeMessage(client)) {
            send(client,
                new Message(changeRoomNameType + " " + roomID,
                        "False\0" + nName));
            return;
        }
        send(new Message(changeRoomNameType + " " + roomID,"True\0" + nName), roomID);
    }

    private void send(Message message, int roomID) {
        List<Integer> roomUsers = UserRequests.getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            notifyUser(message, id);
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        roomID = Integer.parseInt(parameters[1]);
        nName = message.getMessage();
    }

    protected boolean query(ClientConnection client) {
        try {
            UserRequests.updateRoomName(roomID, nName);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

}
