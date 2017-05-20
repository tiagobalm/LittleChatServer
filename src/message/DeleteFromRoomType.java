package message;


import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static message.MessageConstants.deleteFromRoomSize;
import static message.MessageConstants.deleteFromRoomType;

/**
 * This class creates the delete from room's message
 * This class extends the class ReactMessage
 */
public class DeleteFromRoomType extends ReactMessage {
    private int roomID;
    private int userID;

    /**
     * This is the DeleteFromRoomType's constructor
     *
     * @param message Message to be analyzed
     */
    DeleteFromRoomType(Message message) {
        super(message);
    }

    /**
     * This function builds the message needed
     *
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
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
        send(new Message(deleteFromRoomType + " " + roomID, "True\0" + message.getMessage()), roomID, userID);
    }

    /**
     * This function sends the message created
     * @param message Message created
     * @param roomID  Room's identifier
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    private void send(Message message, int roomID, int deletedUser) throws IOException {
        List<Integer> roomUsers = UserRequests.getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            notifyUser(message, id);
        notifyUser(message, deletedUser);
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
