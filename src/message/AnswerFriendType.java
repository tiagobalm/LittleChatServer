package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.answerFriendSize;

public class AnswerFriendType extends ReactMessage {
    private int userID;

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

        if (!storeMessage(client))
            return;

        if (message.getMessage().equals("True")) {
            Message newMessage = new Message(parameters[0] + " " + UserRequests.getUsername(client.getClientID()), "True");
            notifyUser(newMessage, userID);
        } else {
            Message newMessage = new Message(parameters[0] + " " + UserRequests.getUsername(client.getClientID()), "False");
            notifyUser(newMessage, userID);
        }
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        userID = UserRequests.getUserID(parameters[1]);
    }

    protected boolean query(ClientConnection client) {
        try {
            if (message.getMessage().equals("True"))
                UserRequests.updateFriendshipStatus(client.getClientID(), userID);
            else
                UserRequests.deleteFriendshipStatus(userID, client.getClientID());
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
        return true;
    }
}
