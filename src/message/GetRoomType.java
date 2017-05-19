package message;

import communication.ClientConnection;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static message.MessageConstants.getRoomSize;
import static message.MessageConstants.getRoomType;

public class GetRoomType extends ReactMessage {
    GetRoomType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;

        String[] params = message.getHeader().split(" ");
        if( params.length != getRoomSize || client == null )
            return;
        int roomID = Integer.parseInt(params[1]);
        String message = getSendMessage(roomID);
        send(client, new Message(getRoomType, message));
    }

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
                builder.append(id);
            }

        return new String(builder);
    }

    protected void getMessageVariables(ClientConnection client) {
    }

    protected boolean query(ClientConnection client) {
        return true;
    }
}
