package message;

import communication.ClientConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static database.UserRequests.*;
import static message.MessageConstants.messageSize;
import static message.MessageConstants.messageType;

public class MessageType extends ReactMessage {
    private int roomID;
    private String messageBody;
    private String username;

    MessageType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;
        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != messageSize || client.getClientID() == null || !storeMessage(client))
            return ;
        send(new Message(messageType + " " + username + " " + roomID, messageBody),
                roomID, client.getClientID());
    }

    private void send(Message message, int roomID, int userID) throws IOException {
        List<Integer> roomUsers = getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            if( id != userID )
                notifyUser(message, id);
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        roomID = Integer.parseInt(parameters[1]);
        messageBody = message.getMessage();
        username = getUsername(client.getClientID());
    }

    protected boolean query(ClientConnection client) {
        try {
            insertMessages(client.getClientID(), roomID, messageBody);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
        return true;
    }
}
