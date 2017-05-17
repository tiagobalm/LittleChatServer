package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.registerSize;

public class RegisterType extends ReactMessage {
    RegisterType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != registerSize )
            return ;
        disconnectClient(client);
        String username = parameters[1], password = parameters[2],
                ip = parameters[3], port = parameters[4];
        if (registerUser(username, password, ip, port)) {
            Server.getOurInstance().addClientID(UserRequests.getUserID(username), client);
            client.getStreamMessage().write(new Message("LOGIN", "True"));
        }
        else
            client.getStreamMessage().write(new Message("False\0", ""));
    }

    private boolean registerUser(String username,
                              String password, String ip, String port) {
        try { UserRequests.registerUser(username, password, ip, Integer.parseInt(port));
        } catch (SQLException e) {return false;}
        return true;
    }

    private void disconnectClient(ClientConnection client) {
        if( client.getClientID() != null )
            try {
                UserRequests.deleteUserConnection(client.getClientID());
            } catch (SQLException ignore) {}
    }

}
