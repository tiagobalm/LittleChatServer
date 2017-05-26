package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.addRoomSize;
import static message.MessageConstants.addRoomType;

public class AddRoomType extends ReactMessage {
    private String roomName;
    private String username;
    private int roomID;
    private int userID;

    AddRoomType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != addRoomSize || client.getClientID() == null)
            return;

        if(!storeMessage(client)) {
            send(client, new Message(addRoomType + " " + roomID, "False\0" + message.getMessage()));
            return;
        }

        send(client, new Message(addRoomType + " " + roomID, "True\0" + message.getMessage()));

        ClientConnection c = Server.getOurInstance().getClientByID(userID);
        if (c != null)
            send(c, new Message(addRoomType + " " + roomID, "True\0" + roomName + "\0" + UserRequests.getUsername(client.getClientID())));

        ToServerMessage.communicate(this);
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] values = message.getMessage().split("\0");
        roomName = values[0];
        username = values[1];
        roomID = 0;
        userID = UserRequests.getUserID(username);
    }

    protected boolean query(ClientConnection client) {
        try {
            roomID = UserRequests.insertRoom(roomName);
            UserRequests.insertUserRoom(userID, roomID);
            UserRequests.insertUserRoom(client.getClientID(), roomID);
        } catch( SQLException e ) {
            return false;
        }
        return true;
    }
}
