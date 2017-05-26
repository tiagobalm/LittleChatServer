package message;

import communication.ClientConnection;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

import static database.UserRequests.getUserID;
import static message.MessageConstants.answerFriendSize;

public class AnswerFriendType extends ReactMessage {
    private int toUserID;
    private int fromUserID;

    AnswerFriendType(Message message) {
        super(message);
    }

    @Override
    public void react(@NotNull ClientConnection client) throws IOException {
        if (checkToServer(client))
            return;

        String[] parameters = message.getHeader().split(" ");
        if( parameters.length != answerFriendSize || client.getClientID() == null || message.getMessage() == null )
            return ;

        if(storeMessage(client)) {
            if (message.getMessage().equals("True")) {
                Message newMessage = new Message(parameters[0] + " " + UserRequests.getUsername(fromUserID), "True");
                notifyUser(newMessage, toUserID);
            }
            else {
                Message newMessage = new Message(parameters[0] + " " + UserRequests.getUsername(fromUserID), "False");
                notifyUser(newMessage, toUserID);
            }
            ToServerMessage.communicate(this);
        }
    }

    protected void getMessageVariables() {
        String[] parameters = message.getHeader().split(" ");
        toUserID = getUserID(parameters[1]);
        fromUserID = getUserID(parameters[2]);
    }

    protected boolean query(ClientConnection client) {
        if (toUserID == -1 || fromUserID == -1) return false;
        if (message.getMessage().equals("True")) {
            try {UserRequests.updateFriendshipStatus(fromUserID, toUserID);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {UserRequests.deleteFriendshipStatus(toUserID, fromUserID);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
