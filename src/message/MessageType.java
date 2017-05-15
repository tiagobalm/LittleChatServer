package message;

import communication.ClientConnection;
import communication.Server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static database.users.UserRequests.*;
import static message.MessageConstants.messageSize;
import static message.MessageConstants.messageType;

public class MessageType extends ReactMessage {
    MessageType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != messageSize || client.getClientID() == null )
            return ;

        int roomID = Integer.parseInt(parameters[1]);
        String messageBody = message.getMessage();
        String username = getUsername(client.getClientID());
        String date = new SimpleDateFormat("dd-MM-yy").format(new Date());

        insertMessages(client.getClientID(), roomID, messageBody, date);

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
