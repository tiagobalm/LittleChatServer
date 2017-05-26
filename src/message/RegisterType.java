package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.registerSize;

/**
 * This class creates a register's message
 * This class extends the ReactMessage class
 */
public class RegisterType extends ReactMessage {
    private String username;
    private String password;

    /**
     * This is the RegisterType's constructor
     *
     * @param message Message that will be used
     */
    RegisterType(Message message) {
        super(message);
    }

    /**
     * This function builds the message needed
     *
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != registerSize )
            return ;
        if (storeMessage(client)) {
            try { UserRequests.insertUserConnection(username);
            } catch (SQLException ignore) {}
            Server.getOurInstance().addClientID(UserRequests.getUserID(username), client);
            client.getStreamMessage().write(new Message("LOGIN", "True"));
            ToServerMessage.communicate(this);
        } else
            client.getStreamMessage().write(new Message("False\0", ""));
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        username = parameters[1];
        password = parameters[2];
    }

    protected boolean query(ClientConnection client) {
        disconnectClient();
        return registerUser(username, password);
    }

    /**
     * This function verifies if it's possible to register a user
     *
     * @param username User's username
     * @param password User's password
     * @param ip       Connection IP
     * @param port     Connection port
     * @return true if it's possible to register the user, false otherwise
     */
    private boolean registerUser(String username,
                                 String password) {
        try { return UserRequests.registerUser(username, password);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * This function disconnects the client
     */
    private void disconnectClient() {
        try { UserRequests.deleteUserConnection(UserRequests.getUserID(username));
        } catch (SQLException ignore) {}
    }
}
