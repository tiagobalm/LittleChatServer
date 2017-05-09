package message;

import communication.ClientConnection;
import communication.Server;
import database.users.UserRequests;

import java.io.IOException;

import static message.MessageConstants.registerSize;

public class RegisterMessage extends ReactMessage {
    RegisterMessage(String[] message) {
        super(message);
    }

    public void react(ClientConnection client) throws IOException {
        if( message.length != registerSize )
            return ;
        String username = message[1], password = message[2],
                ip = message[3], port = message[4];
        if (UserRequests.registerUser(username, password, ip, Integer.parseInt(port))) {
            client.getStreamMessage().write("True\0".getBytes());
            Server.getOurInstance().registerClient(username, client);
        }
        else
            client.getStreamMessage().write("False\0".getBytes());
    }
}
