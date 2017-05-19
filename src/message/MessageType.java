package message;

import communication.ClientConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static database.UserRequests.*;
import static message.MessageConstants.messageSize;
import static message.MessageConstants.messageType;

/**
 * This class creates the message to be sent
 * This extens the ReactMessage class
 */
public class MessageType extends ReactMessage {
    /**
     * This is the MessageType's constructor
     * @param message Message that will be used
     */
    MessageType(Message message) {
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
        if( parameters.length != messageSize || client.getClientID() == null )
            return ;

        int roomID = Integer.parseInt(parameters[1]);
        String messageBody = message.getMessage();
        String username = getUsername(client.getClientID());

        try {
            insertMessages(client.getClientID(), roomID, messageBody);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return;
        }

        Message sendMessage = new Message(messageType + " " + username + " " + roomID, messageBody);
        send(sendMessage, roomID, client.getClientID());
    }

    /**
     * This function sends the message created
     * @param message Message created
     * @param roomID Room's identifier
     * @param userID User's identifier
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    private void send(Message message, int roomID, int userID) throws IOException {
        List<Integer> roomUsers = getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            if( id != userID )
                notifyUser(message, id);
    }
}
