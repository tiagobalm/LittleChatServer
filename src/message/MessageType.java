package message;

import communication.ClientConnection;
import communication.Server;

import java.io.IOException;
import java.util.List;

import static database.users.UserRequests.getRoomUsers;
import static database.users.UserRequests.getUsername;
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
        Message sendMessage = new Message(messageType + " " + username + " " + roomID, messageBody);

        /*
                IMPORTANT ASS SHIT:
                    INSERT MESSAGE INTO THE DATABASE
         */

        send(sendMessage, roomID, client.getClientID());
    }

    private void send(Message message, int roomID, int userID) throws IOException {
        List<Integer> roomUsers = getRoomUsers(roomID);

        synchronized (Server.getOurInstance().getConnectedClients()) {
            List<ClientConnection> clients = Server.getOurInstance().getConnectedClients();
            for( ClientConnection c : clients ) {
                if( c.getClientID() != null &&
                        !c.getClientID().equals(userID) &&
                        roomUsers != null &&
                        roomUsers.contains(c.getClientID()) )
                    c.getStreamMessage().write(message);
            }
        }
    }
}
