package message;


import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static database.UserRequests.getRoomUsers;
import static message.MessageConstants.deleteFromRoomSize;
import static message.MessageConstants.deleteFromRoomType;


public class DeleteFromRoomType extends ReactMessage {
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
        int roomID = Integer.parseInt(parameters[1]);
        int userID = UserRequests.getUserID(message.getMessage());
        try { UserRequests.deleteUserFromRoom(userID, roomID);
        } catch( SQLException e ) {
            send(client, new Message(deleteFromRoomType, "False\0" + message.getMessage()));
            return;
        }
        send(new Message(deleteFromRoomType, "True\0" + message.getMessage()), roomID);
    }

    private void send(Message message, int roomID) throws IOException {
        List<Integer> roomUsers = getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            notifyUser(message, id);
    }
}
