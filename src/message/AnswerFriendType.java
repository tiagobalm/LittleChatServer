package message;

import communication.ClientConnection;

import java.io.IOException;

import static database.users.UserRequests.getUserID;
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

        /*
                IMPORTANT ASS SHIT:
                    INSERT REQUEST INTO THE DATABASE
         */

        notifyUser(message, userID);
    }
}
