package message;

import communication.ClientConnection;
import database.UserRequests;
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
            return;
        List<String> rooms = UserRequests.getUserRooms(client.getClientID());
        List<String> completeRoomInfo = new ArrayList<>();
        if( rooms == null ) return;
        for(String str: rooms)
            completeRoomInfo.add(getCompleteUserInfo(str));
        send(client, new Message(getRoomsType, completeRoomInfo));
    }

    @NotNull
    private String getCompleteUserInfo(String str) {
        String[] roomInfo = str.split("\0");
        int roomID = Integer.parseInt(roomInfo[0]);
        String roomName = roomInfo[1];
        List<Integer> roomUsers  = UserRequests.getRoomUsers(roomID);
        StringBuilder builder = new StringBuilder();
        System.out.println(roomID);
        builder.append(roomID);
        builder.append("\0");
        builder.append(roomName);
        if(roomUsers != null)
            for( Integer id : roomUsers ) {
                builder.append("\0");
                builder.append(UserRequests.getUsername(id));
            }
        System.out.println(new String(builder));
        return new String(builder);
    }
}
