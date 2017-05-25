package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static message.MessageConstants.messageSize;
import static message.MessageConstants.messageType;

/**
 * This class creates the message to be sent
 * This extens the ReactMessage class
 */
public class MessageType extends ReactMessage {
    private int userID;
    private int roomID;
    private String messageBody;
    private String username;
    private long date;

    /**
     * This is the MessageType's constructor
     *
     * @param message Message that will be used
     */
    MessageType(Message message) {
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
        if (parameters.length != messageSize || client.getClientID() == null || !storeMessage(client))
            return ;
        send(new Message(messageType + " " + username + " " + roomID + " " + date, messageBody),
                roomID, userID);
    }

    /**
     * This function sends the message created
     *
     * @param message Message created
     * @param roomID  Room's identifier
     * @param userID  User's identifier
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    private void send(Message message, int roomID, int userID) throws IOException {
        List<Integer> roomUsers = UserRequests.getRoomUsers(roomID);
        if( roomUsers == null ) return;
            for( Integer id : roomUsers )
                if( id != userID )
                    notifyUser(message, id);
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        roomID = Integer.parseInt(parameters[1]);
        messageBody = message.getMessage();
        username = parameters[2];
        date = Long.parseLong(parameters[3]);
        userID = UserRequests.getUserID(username);
    }

    protected boolean query(ClientConnection client) {
        try {
            UserRequests.insertMessages(userID, roomID, date, messageBody);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
        return true;
    }
}
