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
        BackUpConnection.getInstance().setFinishedProtocol();
    }
}
