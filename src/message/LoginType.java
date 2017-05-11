package message;

import communication.ClientConnection;
import database.users.UserRequests;

import java.io.IOException;

import static message.MessageConstants.loginSize;

public class LoginType extends ReactMessage {
    LoginType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != loginSize )
            return ;
        if( client.getClientID() != null )
            UserRequests.deleteUserConnection(client.getClientID());
        String username = parameters[1], password = parameters[2],
                ip = parameters[3], port = parameters[4];
        if (UserRequests.loginUser(username, password, ip, Integer.parseInt(port))) {
            client.setClientID(UserRequests.getUserID(username));
            client.getStreamMessage().write(new Message("LOGIN", "True"));
        }
        else
            client.getStreamMessage().write(new Message("LOGIN", "False"));
    }
}
