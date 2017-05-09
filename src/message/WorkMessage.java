package message;

import communication.ClientConnection;
import communication.Server;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class WorkMessage implements MessageProcessor {
    private ReactMessage reactMessage;
    private String message;

    public WorkMessage(String message) {
        this.message = message;
        reactMessage = ReactMessage.getReactMessage(message);
    }

    @Override
    public void decode() {
        ClientConnection client = getClient(getUserName(message));
        if( client == null || reactMessage == null ) return ;
        try { reactMessage.react(client);
        } catch (IOException e) {
            e.printStackTrace();
            client.close();
        }
    }

    private ClientConnection getClient(String username) {
        ClientConnection client = null;
        for(ClientConnection clientConn : Server.getOurInstance().getClientSet() ) {
            if( clientConn.getUsername() != null && clientConn.getUsername().equals(username) )
                client = clientConn;
        }
        return client;
    }

    @Nullable
    public static String getUserName(String message) {
        String[] parameters = message.split(" ");
        return parameters.length >= 1 ? parameters[1] : null;
    }
}
