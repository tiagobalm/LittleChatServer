package message;


import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static message.MessageConstants.deleteFromRoomSize;
import static message.MessageConstants.deleteFromRoomType;


public class DeleteFromRoomType extends ReactMessage {
    private int roomID;
    private int userID;

    DeleteFromRoomType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;
        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != deleteFromRoomSize || client.getClientID() == null)
            return;
        if (!storeMessage(client)) {
            send(client, new Message(deleteFromRoomType, "False\0" + message.getMessage()));
            return;
        }
        send(new Message(deleteFromRoomType, "True\0" + message.getMessage()), roomID);
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
        userID = UserRequests.getUserID(message.getMessage());
    }

    protected boolean query(ClientConnection client) {
        try {
            UserRequests.deleteUserFromRoom(userID, roomID);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
