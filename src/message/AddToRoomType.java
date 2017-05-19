package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static database.UserRequests.getRoomUsers;
import static message.MessageConstants.addToRoomSize;
import static message.MessageConstants.addToRoomType;

/**
 * This class creates the possibility to add a user to a room
 * This extends the ReactMessage class
 */
public class AddToRoomType extends ReactMessage {
    /**
     * This is the AddToRoomType's constructed
     * @param message Message to be analyzed
     */
    AddToRoomType(Message message) {
        super(message);
    }

    /**
     * This function builds the message needed
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
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

    /**
     * This function sends the message passed in the arguments
     * @param message Message to be sent
     * @param roomID Room's identifier
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    private void send(Message message, int roomID) throws IOException {
        List<Integer> roomUsers = getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            notifyUser(message, id);
    }
}
