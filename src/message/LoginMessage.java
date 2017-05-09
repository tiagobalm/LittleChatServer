package message;

import communication.ClientConnection;
import database.users.UserRequests;

import java.io.IOException;

import static message.MessageConstants.loginSize;

public class LoginMessage extends ReactMessage {
    LoginMessage(String[] message) {
        super(message);
    }

    public void react(ClientConnection client) throws IOException {
        System.out.println("React to login " + message.length);
        if( message.length != loginSize )
            return ;
        String username = message[1], password = message[2],
                ip = message[3], port = message[4];
        if (UserRequests.loginUser(username, password, ip, Integer.parseInt(port)))
            client.getStreamMessage().write("True\0".getBytes());
        else
            client.getStreamMessage().write("False\0".getBytes());
    }
}
