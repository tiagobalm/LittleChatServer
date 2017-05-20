package message;


import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static database.UserRequests.getRoomUsers;
import static message.MessageConstants.deleteFromRoomSize;
import static message.MessageConstants.deleteFromRoomType;

/**
 * This class creates the delete from room's message
 * This class extends the class ReactMessage
 */
public class DeleteFromRoomType extends ReactMessage {
    /**
     * This is the DeleteFromRoomType's constructor
     * @param message Message to be analyzed
     */
    DeleteFromRoomType(Message message) {
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
        if (parameters.length != deleteFromRoomSize || client.getClientID() == null)
            return;
        int roomID = Integer.parseInt(parameters[1]);
        int userID = UserRequests.getUserID(message.getMessage());
        try { UserRequests.deleteUserFromRoom(userID, roomID);
        } catch( SQLException e ) {
            send(client, new Message(deleteFromRoomType + " " + roomID, "False\0" + message.getMessage()));
            return;
        }
        send(new Message(deleteFromRoomType + " " + roomID, "True\0" + message.getMessage()), roomID, userID);
    }

    /**
     * This function sends the message created
     * @param message Message created
     * @param roomID Room's identifier
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    private void send(Message message, int roomID, int deletedUser) throws IOException {
        List<Integer> roomUsers = getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            notifyUser(message, id);
        notifyUser(message, deletedUser);
    }
}
