package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.loginSize;
import static message.MessageConstants.messageType;

/**
 * This class creates a login's message
 * This class extends the ReactMessage class
 */
public class LoginType extends ReactMessage {
    private String username;
    private String password;

    /**
     * This is the LoginType's constructor
     *
     * @param message Message that will be used
     */
    LoginType(Message message) {
        super(message);
    }

    /**
     * This function creates the message needed
     *
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != loginSize )
            return ;
        if (storeMessage(client)) {
            Server.getOurInstance().addClientID(UserRequests.getUserID(username), client);
            client.getStreamMessage().write(new Message("LOGIN", "True"));
        } else
            client.getStreamMessage().write(new Message("LOGIN", "False"));
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        username = parameters[1];
        password = parameters[2];
    }

    protected boolean query(ClientConnection client) {
        disconnectClient(client);
        return loginUser(username, password);
    }

    /**
     * This function logs in a user
     *
     * @param username User's username
     * @param password User's password
     * @return true if the user logs in correctly, false otherwise
     */
    private boolean loginUser(String username,
                              String password) {
        try {
            return UserRequests.loginUser(username, password);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * This function disconnects a client
     *
     * @param client Client's connection
     */
    private void disconnectClient(ClientConnection client) {
        if (client.getClientID() != null)
            try {
                UserRequests.deleteUserConnection(client.getClientID());
            } catch (SQLException ignore) {}
    }
}
