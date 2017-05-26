package message;

import communication.ClientConnection;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static message.MessageConstants.getRoomsSize;
import static message.MessageConstants.getRoomsType;

/**
 * This class creates the message that gets the chat rooms
 * This class extends the ReactMessage class
 */
public class GetRoomsType extends ReactMessage {
    /**
     * This is the GetRoomsType's constructor
     *
     * @param message Message that will be used
     */
    GetRoomsType(Message message) {
        super(message);
    }

    /**
     * This function creates the message needed
     *
     * @param client Client's connection
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    @Override
    public void react(ClientConnection client) throws IOException {
        /*if( checkToServer(client) )
            return;*/

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

    /**
     * This function gets all the user's information
     *
     * @param str String that has the chat room's information
     * @return A string with the user's information
     */
    @NotNull
    private String getCompleteUserInfo(String str) {
        String[] roomInfo = str.split("\0");
        int roomID = Integer.parseInt(roomInfo[0]);
        String roomName = roomInfo[1];
        List<Integer> roomUsers  = UserRequests.getRoomUsers(roomID);
        StringBuilder builder = new StringBuilder();
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

    protected void getMessageVariables(ClientConnection client) {
    }

    protected boolean query(ClientConnection client) {
        return true;
    }
}
