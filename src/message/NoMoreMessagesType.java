package message;

import backupconnection.BackUpConnection;
import communication.ClientConnection;

import java.io.IOException;

import static message.MessageConstants.noMoreMessagesSize;

public class NoMoreMessagesType extends ReactMessage {
    NoMoreMessagesType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != noMoreMessagesSize)
            return;
        System.out.println(client.getClientID());
        if (client.getClientID() == ClientConnection.ownID)
            send(client, message);
        BackUpConnection.getInstance().setFinishedProtocol();
    }
}
