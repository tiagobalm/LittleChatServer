package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.loginSize;

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
    public void react(@NotNull ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != loginSize )
            return ;
        if (storeMessage(client)) {
            Server.getOurInstance().addClientID(UserRequests.getUserID(username), client);
            assert client.getStreamMessage() != null;
            System.out.println("True login for " + username + " pass: " + password);
            client.getStreamMessage().write(new Message("LOGIN", "True"));
        } else {
            assert client.getStreamMessage() != null;
            System.out.println("False login for " + username + " pass: " + password);
            client.getStreamMessage().write(new Message("LOGIN", "False"));
        }
    }

    protected void getMessageVariables() {
        String[] parameters = message.getHeader().split(" ");
        username = parameters[1];
        password = parameters[2];
    }

    protected boolean query(@NotNull ClientConnection client) {
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
    private void disconnectClient(@NotNull ClientConnection client) {
        if (client.getClientID() != null)
            try {
                UserRequests.deleteUserConnection(client.getClientID());
            } catch (SQLException ignore) {}
    }
}
