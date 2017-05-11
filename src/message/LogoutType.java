package message;

import communication.ClientConnection;
import communication.Server;
import database.users.UserRequests;

import java.io.IOException;

import static message.MessageConstants.logoutSize;

public class LogoutType extends ReactMessage {
    LogoutType(Message message) {
        super(message);
    }

    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != logoutSize )
            return ;
        String username = parameters[1];
        UserRequests.deleteUserConnection(username);
        client.getStreamMessage().write(new Message("Logout\0", ""));
        Server.getOurInstance().logoutClient(username);
    }
}
