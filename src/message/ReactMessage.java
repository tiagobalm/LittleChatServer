package message;

import communication.ClientConnection;
import communication.Server;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

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

        String mType = parameters[0];
        System.out.println("Message react to : " + mType);
        switch (mType) {
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
            case messageType:
                return new MessageType(message);
            case friendRequestType:
                return new FriendRequestType(message);
            case answerFriendType:
                return new AnswerFriendType(message);
        }

        return null;
    }

    void notifyUser(Message message, int userID) {
        ClientConnection c = Server.getOurInstance().getClientByID(userID);
        if( c != null )
            send(c, message);
    }

    private void send(ClientConnection c, Message message) {
        Thread thread = new Thread(() -> {
            try {
                c.getStreamMessage().write(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
