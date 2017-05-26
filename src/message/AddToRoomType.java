package message;

import communication.ClientConnection;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static message.MessageConstants.addToRoomSize;
import static message.MessageConstants.addToRoomType;

public class AddToRoomType extends ReactMessage {
    private int roomID;
    private int userID;

    AddToRoomType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if (checkToServer(client))
            return;

        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != addToRoomSize || client.getClientID() == null)
            return;

        if (!storeMessage(client))
            send(client, new Message(addToRoomType + " " + roomID, "False\0" + message.getMessage()));
        else {
            send(new Message(addToRoomType + " " + roomID, "True\0" + message.getMessage()), roomID);
            ToServerMessage.communicate(this);
        }
    }

    private void send(Message message, int roomID) throws IOException {
        List<Integer> roomUsers = UserRequests.getRoomUsers(roomID);
        if( roomUsers == null ) return;
        for( Integer id : roomUsers )
            notifyUser(message, id);
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] parameters = message.getHeader().split(" ");
        roomID = Integer.parseInt(parameters[1]);
        userID = UserRequests.getUserID(message.getMessage());
    }

    protected boolean query(ClientConnection client) {
        if (userID == -1) return false;
        try { UserRequests.insertUserRoom(userID, roomID);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
