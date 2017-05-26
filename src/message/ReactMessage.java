package message;

import communication.ClientConnection;
import communication.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static message.MessageConstants.*;

/**
 * This class creates a react message
 */
public abstract class ReactMessage {
    /**
     * Message that will be used
     */
    final Message message;

    /**
     * This is the ReactMessage's constructor
     *
     * @param message Message that will be used
     */
    ReactMessage(Message message) {
        this.message = message;
    }

    /**
     * This function gets the react message
     *
     * @param message Message that will be used
     * @return The react message
     */
    @Nullable
    public static ReactMessage getReactMessage(@NotNull Message message) {
        String[] parameters = message.getHeader().split(" ");
        if( parameters.length < 1 )
            return null;

        String mType = parameters[0];
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

    /**
     * This function builds the messaged needed
     *
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    public void react(ClientConnection client) throws IOException {
        throw new AbstractMethodError("react in ReactMessage");
    }

    boolean checkToServer(ClientConnection client) {
        return ToServerMessage.analyze(this, client);
    }

    boolean storeMessage(ClientConnection client) {
        getMessageVariables();
        return query(client);
    }

    void getMessageVariables() {
        throw new AbstractMethodError("react in ReactMessage");
    }

    boolean query(ClientConnection client) {
        throw new AbstractMethodError("react in ReactMessage");
    }

    /**
     * This function sends the message to the respective user
     *
     * @param message Message to be sent
     * @param userID  User's iderntifier
     */
    void notifyUser(Message message, int userID) {
        ClientConnection c = Server.getOurInstance().getClientByID(userID);
        if( c != null )
            send(c, message);
    }

    /**
     * This function sends the respective message through the client's connection
     * @param c Client's connection
     * @param message Message that will be sent
     */
    void send(@NotNull ClientConnection c, Message message) {
        Thread thread = new Thread(() -> {
            try {
                assert c.getStreamMessage() != null;
                c.getStreamMessage().write(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
