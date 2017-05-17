package message;

import communication.ClientConnection;

import java.io.IOException;
import java.sql.SQLException;

import static database.UserRequests.getUserID;
import static database.UserRequests.updateFriendshipStatus;
import static message.MessageConstants.answerFriendSize;

public class AnswerFriendType extends ReactMessage {
    AnswerFriendType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != answerFriendSize || client.getClientID() == null )
            return ;
        int userID = getUserID(parameters[1]);
        try {
            updateFriendshipStatus(client.getClientID(), userID);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return;
        }
        notifyUser(message, userID);
    }
}
