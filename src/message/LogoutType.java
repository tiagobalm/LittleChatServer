package message;

import communication.ClientConnection;
import communication.Server;
import database.users.UserRequests;

import java.io.IOException;

import static message.MessageConstants.logoutSize;

public class LogoutType extends ReactMessage {
    LogoutType(String[] message) {
        super(message);
    }

    public void react(ClientConnection client) throws IOException {
        if( message.length != logoutSize )
            return ;
        String username = message[1];
        UserRequests.deleteUserConnection(username);
        client.getStreamMessage().write("Logout\0".getBytes());
        Server.getOurInstance().logoutClient(username);
    }
}
