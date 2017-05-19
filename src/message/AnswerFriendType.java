package message;

import communication.ClientConnection;
import database.UserRequests;

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
        if( checkToServer(client) )
            return;

        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != answerFriendSize || client.getClientID() == null || message.getMessage() == null )
            return ;

        int userID = getUserID(parameters[1]);

        if(message.getMessage().equals("True")) {
            try {
                updateFriendshipStatus(client.getClientID(), userID);
            } catch (SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
                return;
            }

            Message newMessage = new Message(parameters[0] + " " + UserRequests.getUsername(client.getClientID()), "True");
            notifyUser(newMessage, userID);
        } else {
            try {
                UserRequests.deleteFriendshipStatus(userID, client.getClientID());

                Message newMessage = new Message(parameters[0] + " " + UserRequests.getUsername(client.getClientID()), "False");
                notifyUser(newMessage, userID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
