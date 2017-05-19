package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.addRoomSize;
import static message.MessageConstants.addRoomType;

public class AddRoomType extends ReactMessage {
    private int userID;
    private int roomID;
    private String roomName;

    AddRoomType(Message message) {
        super(message);
    }

    @Override
    public void react(ClientConnection client) throws IOException {
        if( checkToServer(client) )
            return;
        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != addRoomSize || client.getClientID() == null)
            return;

        if (!storeMessage(client)) {
            send(client, new Message(addRoomType + " " + roomID, "False\0" + message.getMessage()));
            return;
        }
        send(client, new Message(addRoomType + " " + roomID, "True\0" + message.getMessage()));

        ClientConnection c;
        if ((c = Server.getOurInstance().getClientByID(userID)) != null)
            send(c, new Message(addRoomType + " " + roomID, "True\0" + roomName + "\0" + UserRequests.getUsername(client.getClientID())));
    }

    protected void getMessageVariables(ClientConnection client) {
        String[] values = message.getMessage().split("\0");
        roomName = values[0];
        userID = UserRequests.getUserID(values[1]);
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
