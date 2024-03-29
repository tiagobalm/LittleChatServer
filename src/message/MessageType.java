package message;

import communication.ClientConnection;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;

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
    public void react(@NotNull ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;
        System.out.println("MessageType message react after checkToServer");
        System.out.println("MessageType message client id " + client.getClientID());
        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != messageSize || client.getClientID() == null || !storeMessage(client))
            return ;
        send(new Message(messageType + " " + username + " " + roomID + " " + date, messageBody),
                roomID, userID);
        ToServerMessage.communicate(this);
    }

    /**
     * This function sends the message created
     *
     * @param message Message created
     * @param roomID  Room's identifier
     * @param userID  User's identifier
     */
    private void send(Message message, int roomID, int userID) {
        List<Integer> roomUsers = UserRequests.getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for (Integer id : roomUsers)
            if (id != userID) {
                System.out.println("Notify user " + id);
                notifyUser(message, id);
            }
    }

    protected void getMessageVariables() {
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
