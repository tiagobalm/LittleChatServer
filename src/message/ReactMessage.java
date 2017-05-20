package message;

import communication.ClientConnection;
import communication.Server;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static message.MessageConstants.*;

public abstract class ReactMessage {
    protected Message message;
    ReactMessage(Message message) {
        this.message = message;
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
            case getRoomType:
                return new GetRoomType(message);
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
            case addToRoomType:
                return new AddToRoomType(message);
            case deleteFromRoomType:
                return new DeleteFromRoomType(message);
            case addRoomType:
                return new AddRoomType(message);
            case changeRoomNameType:
                return new ChangeRoomNameType(message);
            case noMoreMessagesType:
                return new NoMoreMessagesType(message);
        }

        return null;
    }

    public void react(ClientConnection client) throws IOException {
        throw new AbstractMethodError("react in ReactMessage");
    }

    protected boolean checkToServer(ClientConnection client) throws IOException {
        if (ToServerMessage.analyze(this, client))
            return true;
        ToServerMessage.communicate(this);
        return false;
    }

    protected boolean storeMessage(ClientConnection client) {
        getMessageVariables(client);
        return query(client);
    }

    protected void getMessageVariables(ClientConnection client) {
        throw new AbstractMethodError("react in ReactMessage");
    }

    protected boolean query(ClientConnection client) {
        throw new AbstractMethodError("react in ReactMessage");
    }

    void notifyUser(Message message, int userID) {
        ClientConnection c = Server.getOurInstance().getClientByID(userID);

        if( c != null )
            send(c, message);
    }

    void send(ClientConnection c, Message message) {
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
