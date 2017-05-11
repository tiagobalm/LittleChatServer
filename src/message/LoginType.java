package message;

import communication.ClientConnection;
import communication.Server;
import database.users.UserRequests;

import java.io.IOException;

import static message.MessageConstants.loginSize;

public class LoginType extends ReactMessage {
    LoginType(Message message) {
        super(message);
    }

    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != loginSize )
            return ;
        String username = parameters[1], password = parameters[2],
                ip = parameters[3], port = parameters[4];
        if (UserRequests.loginUser(username, password, ip, Integer.parseInt(port))) {
            client.getStreamMessage().write(new Message("LOGIN", "True"));
            Server.getOurInstance().registerClient(username, client);
        }
        else
            client.getStreamMessage().write(new Message("LOGIN", "False"));
    }
}
