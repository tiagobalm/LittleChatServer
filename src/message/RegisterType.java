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
    /**
     * This is the RegisterType's constructor
     * @param message Message that will be used
     */
    RegisterType(Message message) {
        super(message);
    }

    /**
     * This function builds the message needed
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != registerSize )
            return ;
        disconnectClient(client);
        String username = parameters[1], password = parameters[2],
                ip = parameters[3], port = parameters[4];
        if (registerUser(username, password, ip, port)) {
            Server.getOurInstance().addClientID(UserRequests.getUserID(username), client);
            client.getStreamMessage().write(new Message("LOGIN", "True"));
        }
        else
            client.getStreamMessage().write(new Message("False\0", ""));
    }

    /**
     * This function verifies if it's possible to register a user
     * @param username User's username
     * @param password User's password
     * @param ip Connection IP
     * @param port Connection port
     * @return true if it's possible to register the user, false otherwise
     */
    private boolean registerUser(String username,
                              String password, String ip, String port) {
        try { UserRequests.registerUser(username, password, ip, Integer.parseInt(port));
        } catch (SQLException e) {return false;}
        return true;
    }

    /**
     * This function disconnects the client
     * @param client Client's connection
     */
    private void disconnectClient(ClientConnection client) {
        if( client.getClientID() != null )
            try {
                UserRequests.deleteUserConnection(client.getClientID());
            } catch (SQLException ignore) {}
    }

}
