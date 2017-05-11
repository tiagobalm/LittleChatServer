package message;

import communication.ClientConnection;
import communication.Server;
import database.users.UserRequests;

import java.io.IOException;

import static message.MessageConstants.registerSize;

public class RegisterType extends ReactMessage {
    RegisterType(Message message) {
        super(message);
    }

    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != registerSize )
            return ;
        if( client.getClientID() != null )
            UserRequests.deleteUserConnection(client.getClientID());
        String username = parameters[1], password = parameters[2],
                ip = parameters[3], port = parameters[4];
        if (UserRequests.registerUser(username, password, ip, Integer.parseInt(port))) {
            client.setClientID(UserRequests.getUserID(username));
            client.getStreamMessage().write(new Message("LOGIN", "True"));
        }
        else
            client.getStreamMessage().write(new Message("False\0", ""));
    }
}
