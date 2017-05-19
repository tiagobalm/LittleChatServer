package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.logoutSize;
import static message.MessageConstants.logoutType;

/**
 * This class creates a logout message
 * This class extends the ReactMessage class
 */
public class LogoutType extends ReactMessage {
    /**
     * This is the LogoutType's constructor
     * @param message Message that will be used
     */
    LogoutType(Message message) {
        super(message);
    }

    /**
     * This function creates the message needed
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != logoutSize || client.getClientID() == null )
            return ;
        try { UserRequests.deleteUserConnection(client.getClientID());
        } catch (SQLException ignore) {}
        client.getStreamMessage().write(new Message(logoutType, ""));
        Server.getOurInstance().removeByID(client.getClientID());
    }
}
