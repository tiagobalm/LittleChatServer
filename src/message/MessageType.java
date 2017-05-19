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

public class MessageType extends ReactMessage {
    MessageType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;

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

    private void send(Message message, int roomID, int userID) throws IOException {
        List<Integer> roomUsers = getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            if( id != userID )
                notifyUser(message, id);
    }
}
