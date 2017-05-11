package message;

import communication.ClientConnection;
import database.users.UserRequests;

import java.io.IOException;

import static message.MessageConstants.logoutSize;
import static message.MessageConstants.logoutType;

public class LogoutType extends ReactMessage {
    LogoutType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != logoutSize || client.getClientID() == null )
            return ;
        UserRequests.deleteUserConnection(client.getClientID());
        client.getStreamMessage().write(new Message(logoutType, ""));
        client.setClientID(null);
    }
}
