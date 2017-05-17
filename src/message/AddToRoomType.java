package message;

import communication.ClientConnection;
import database.users.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static database.users.UserRequests.getRoomUsers;
import static message.MessageConstants.addToRoomSize;
import static message.MessageConstants.addToRoomType;

public class AddToRoomType extends ReactMessage {
    AddToRoomType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != addToRoomSize || client.getClientID() == null)
            return;
        int roomID = Integer.parseInt(parameters[1]);
        int userID = UserRequests.getUserID(message.getMessage());
        try { UserRequests.insertUserRoom(userID, roomID);
        } catch( SQLException e ) {
            send(client, new Message(addToRoomType, "False\0" + message.getMessage()));
            return;
        }
        send(new Message(addToRoomType, "True\0" + message.getMessage()), roomID);
    }

    private void send(Message message, int roomID) throws IOException {
        List<Integer> roomUsers = getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            notifyUser(message, id);
    }
}
