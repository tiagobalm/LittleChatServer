package Workers;

import communication.ClientConnection;
import communication.Server;
import database.users.UserRequests;

import java.io.IOException;

public class WorkMessage implements MessageProcessor {
    private String message;

    WorkMessage(String message) {
        this.message = message;
    }

    @Override
    public void decode() {
        String[] parameters = message.split(" ");
        String messageType = parameters[0];
        String username = parameters[1];
        String password = parameters[2];
        ClientConnection client = getClient(username);
        if( client == null )
            return ;

        try {
            if (messageType.equals("LOGIN")) {
                if (UserRequests.loginUser(username, password, "", 0))
                    client.getStreamMessage().write("True\0".getBytes());
                else
                    client.getStreamMessage().write("False\0".getBytes());
            } else if (messageType.equals("REGISTER"))
                UserRequests.registerUser(username, password);
        } catch (IOException e) {
            e.printStackTrace();
            Server.getOurInstance().getClientSet().remove(client);
        }
    }

    private ClientConnection getClient(String username) {
        ClientConnection client = null;
        for(ClientConnection clientConn : Server.getOurInstance().getClientSet() ) {
            if( clientConn.getUsername().equals(username) )
                client = clientConn;
        }
        return client;
    }
}
