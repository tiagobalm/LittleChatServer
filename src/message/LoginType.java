package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.loginSize;

public class LoginType extends ReactMessage {
    LoginType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        for( String str : parameters )
            System.out.println(str);
        if( parameters.length != loginSize )
            return ;

        disconnectClient(client);

        String username = parameters[1], password = parameters[2],
                ip = parameters[3], port = parameters[4];
        if (loginUser(username, password, ip, port)) {
            Server.getOurInstance().addClientID(UserRequests.getUserID(username), client);
            client.getStreamMessage().write(new Message("LOGIN", "True"));
        }
        else
            client.getStreamMessage().write(new Message("LOGIN", "False"));
    }

    private boolean loginUser(String username,
                                String password, String ip, String port) {
        try { UserRequests.loginUser(username, password, ip, Integer.parseInt(port));
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
