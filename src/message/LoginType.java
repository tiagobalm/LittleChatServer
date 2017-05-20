package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.loginSize;

public class LoginType extends ReactMessage {
    private String username;
    private String password;
    private String ip;
    private String port;

    LoginType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != loginSize )
            return ;
        if (storeMessage(client)) {
            Server.getOurInstance().addClientID(UserRequests.getUserID(username), client);
            client.getStreamMessage().write(new Message("LOGIN", "True"));
        }
        else
            client.getStreamMessage().write(new Message("LOGIN", "False"));
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        username = parameters[1];
        password = parameters[2];
        ip = parameters[3];
        port = parameters[4];
    }

    protected boolean query(ClientConnection client) {
        disconnectClient(client);
        return loginUser(username, password, ip, port);
    }

    private boolean loginUser(String username,
                                String password, String ip, String port) {
        try {
            return UserRequests.loginUser(username, password, ip, Integer.parseInt(port));
        } catch (SQLException e) {return false;}
    }

    private void disconnectClient(ClientConnection client) {
        if (client.getClientID() != null)
            try {
                UserRequests.deleteUserConnection(client.getClientID());
            } catch (SQLException ignore) {}
    }
}
