package worker;

import communication.ClientConnection;
import communication.Server;
import database.users.UserRequests;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class WorkMessage implements MessageProcessor {
    private String message;

    WorkMessage(String message) {
        this.message = message;
    }

    @Override
    public void decode() {
        ClientConnection client = getClient(getUserName(message));
        if( client == null ) return ;
        try { parseMessage(client);
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

    private void parseMessage(ClientConnection client) throws IOException {
        String[] parameters = message.split(" ");
        String messageType = parameters[0];
        String username = parameters[1];
        String password = parameters[2];

        switch (messageType) {
            case "LOGIN":
                if (UserRequests.loginUser(username, password, "", 0))
                    client.getStreamMessage().write("True\0".getBytes());
                else
                    client.getStreamMessage().write("False\0".getBytes());
                break;
            case "REGISTER":
                if( UserRequests.registerUser(username, password) )
                    client.getStreamMessage().write("True\0".getBytes());
                else
                    client.getStreamMessage().write("False\0".getBytes());
                break;
        }
    }

    @Nullable
    public static String getUserName(String message) {
        String[] parameters = message.split(" ");
        return parameters.length >= 1 ? parameters[1] : null;
    }
}
