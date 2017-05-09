package message;

import communication.ClientConnection;

import java.io.IOException;

public class WorkMessage implements MessageProcessor {
    private ReactMessage reactMessage;
    private ClientConnection clientConnection;

    public WorkMessage(ClientConnection clientConnection, String message) {
        this.clientConnection = clientConnection;
        reactMessage = ReactMessage.getReactMessage(message);
    }

    @Override
    public void decode() {
        System.out.println("Decode");
        if( reactMessage == null ) return ;
        System.out.println("After if");
        try { reactMessage.react(clientConnection);
        } catch (IOException e) {
            e.printStackTrace();
            clientConnection.close();
        }
    }
}
