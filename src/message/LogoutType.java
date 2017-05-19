package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.logoutSize;
import static message.MessageConstants.logoutType;

public class LogoutType extends ReactMessage {
    LogoutType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;

        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != logoutSize || client.getClientID() == null )
            return ;
        try { UserRequests.deleteUserConnection(client.getClientID());
        } catch (SQLException ignore) {}
        client.getStreamMessage().write(new Message(logoutType, ""));
        Server.getOurInstance().removeByID(client.getClientID());
    }
}
