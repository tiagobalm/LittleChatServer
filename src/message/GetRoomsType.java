package message;

import communication.ClientConnection;
import database.users.UserRequests;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static message.MessageConstants.getRoomsSize;
import static message.MessageConstants.getRoomsType;

public class GetRoomsType extends ReactMessage {
    GetRoomsType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] params = message.getHeader().split(" ");
        if( params.length != getRoomsSize || client.getClientID() == null )
            return ;
        List<String> rooms = UserRequests.getUserRooms(client.getClientID());
        List<String> completeRoomInfo = new ArrayList<>();
        if( rooms == null ) return;
        for(String str: rooms)
            completeRoomInfo.add(getCompleteUserInfo(str));
        for(String str : completeRoomInfo)
            System.out.println(str);
        client.getStreamMessage().write(new Message(getRoomsType, completeRoomInfo));
    }

    @NotNull
    private String getCompleteUserInfo(String str) {
        String[] roomInfo = str.split("\0");
        int roomID = Integer.parseInt(roomInfo[0]);
        String roomName = roomInfo[1];
        List<String> roomUserUsernames = UserRequests.getRoomUsersUsernames(roomID);
        StringBuilder builder = new StringBuilder(roomID);

        builder.append("\0");
        builder.append(roomName);
        if(roomUserUsernames != null)
            for( String username : roomUserUsernames ) {
                builder.append("\0");
                builder.append(username);
            }
        return new String(builder);
    }
}
