package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.addRoomSize;
import static message.MessageConstants.addRoomType;

public class AddRoomType extends ReactMessage {
    AddRoomType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != addRoomSize || client.getClientID() == null)
            return;

        String[] values = message.getMessage().split("\0");
        String roomName = values[0];
        String username = values[1];
        int roomID = Integer.parseInt(parameters[1]);
        int userID = UserRequests.getUserID(username);
        try {
            UserRequests.insertRoom(roomName);
            UserRequests.insertUserRoom(userID, roomID);
            UserRequests.insertUserRoom(client.getClientID(), roomID);
        } catch( SQLException e ) {
            send(client, new Message(addRoomType, "False\0" + message.getMessage()));
            return;
        }
        send(client, new Message(addRoomType, "True\0" + message.getMessage()));
    }
}
