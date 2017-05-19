package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.addRoomSize;
import static message.MessageConstants.addRoomType;

/**
 * This class creates the message to add a room
 * This extends the ReactMessage class
 */
public class AddRoomType extends ReactMessage {
    /**
     * This is the AddRoomType's constructor
     * @param message Message to be analyzed
     */
    AddRoomType(Message message) {
        super(message);
    }

    /**
     * This function builds a message to add a room type
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != addRoomSize || client.getClientID() == null)
            return;

        String[] values = message.getMessage().split("\0");
        String roomName = values[0];
        String username = values[1];
        Integer roomID = 0;

        int userID = UserRequests.getUserID(username);
        try {
            roomID = UserRequests.insertRoom(roomName);
            UserRequests.insertUserRoom(userID, roomID);
            UserRequests.insertUserRoom(client.getClientID(), roomID);
        } catch( SQLException e ) {
            send(client, new Message(addRoomType + " " + roomID, "False\0" + message.getMessage()));
            return;
        }
        send(client, new Message(addRoomType + " " + roomID, "True\0" + message.getMessage()));

        ClientConnection c = Server.getOurInstance().getClientByID(userID);
        if(c != null)
            send(c, new Message(addRoomType + " " + roomID, "True\0" + roomName + "\0" + UserRequests.getUsername(client.getClientID())));
    }
}
