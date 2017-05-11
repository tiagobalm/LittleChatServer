package message;

import communication.ClientConnection;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static message.MessageConstants.*;

public abstract class ReactMessage {
    protected Message message;
    ReactMessage(Message message) {
        this.message = message;
    }

    public void react(ClientConnection client) throws IOException {
        throw new AbstractMethodError("Wrong class");
    }

    @Nullable
    public static ReactMessage getReactMessage(Message message) {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length < 1 )
            return null;

        String messageType = parameters[0];
        System.out.println("Message react to : " + messageType);
        switch (messageType) {
            case loginType:
                return new LoginType(message);
            case registerType:
                return new RegisterType(message);
            case logoutType:
                return new LogoutType(message);
            case getRoomsType:
                return new GetRoomsType(message);
            case getFriendsType:
                return new GetFriendsType(message);
            case getFriendRequestsType:
                return new GetFriendRequestsType(message);
            case getMessagesType:
                return new GetMessagesType(message);
        }

        return null;
    }
}
