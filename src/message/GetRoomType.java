package message;

import communication.ClientConnection;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.getRoomSize;
import static message.MessageConstants.getRoomType;

/**
 * This class creates a message to get room's information
 * This class extends the ReactMessage class
 */
public class GetRoomType extends ReactMessage {
    /**
     * This is the GetRoomType's constructor
     *
     * @param message Message that will be used
     */
    GetRoomType(Message message) {
        super(message);
    }

    /**
     * This functions builds the message needed
     *
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(@Nullable ClientConnection client) throws IOException {
        String[] params = message.getHeader().split(" ");
        if( params.length != getRoomSize || client == null )
            return;
        int roomID = Integer.parseInt(params[1]);
        String message = getSendMessage(roomID);
        send(client, new Message(getRoomType, message));
    }

    /**
     * This function gets the sent messages
     *
     * @param roomID Room's identifier
     * @return A string with the sent messages
     */
    @NotNull
    private String getSendMessage(int roomID) {
        String roomName = UserRequests.getRoomName(roomID);
        List<Integer> roomUsers = UserRequests.getRoomUsers(roomID);
        StringBuilder builder = new StringBuilder();
        builder.append(roomID);
        builder.append("\0");
        builder.append(roomName);
        if(roomUsers != null)
            for( Integer id : roomUsers ) {
                builder.append("\0");
                builder.append(UserRequests.getUsername(id));
            }

        return new String(builder);
    }

    protected void getMessageVariables() {
    }

    protected boolean query(ClientConnection client) {
        return true;
    }
}
