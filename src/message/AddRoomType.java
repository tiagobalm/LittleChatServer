package message;

import communication.ClientConnection;
import communication.Server;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

import static message.MessageConstants.addRoomSize;
import static message.MessageConstants.addRoomType;

public class AddRoomType extends ReactMessage {
    private String roomName;
    private int roomID;
    private int userID1;
    private int userID2;

    AddRoomType(Message message) {
        super(message);
    }

    @Override
    public void react(@NotNull ClientConnection client) throws IOException {
        if (checkToServer(client))
            return;

        String[] parameters = message.getHeader().split(" ");
        if (parameters.length != addRoomSize || client.getClientID() == null)
            return;

        if(!storeMessage(client)) {
            send(client, new Message(addRoomType + " " + roomID, "False\0" + message.getMessage()));
            return;
        }

        send(client, new Message(addRoomType + " " + roomID, "True\0" + message.getMessage()));

        ClientConnection c = Server.getOurInstance().getClientByID(userID1);
        if (c != null)
            send(c, new Message(addRoomType + " " + roomID, "True\0" + roomName + "\0" + UserRequests.getUsername(client.getClientID())));

        ToServerMessage.communicate(this);
    }

    protected void getMessageVariables() {
        String[] values = message.getMessage().split("\0");
        roomName = values[0];
        String user1 = values[1];
        String user2 = values[2];
        roomID = 0;
        userID1 = UserRequests.getUserID(user1);
        userID2 = UserRequests.getUserID(user2);
    }

    protected boolean query(@NotNull ClientConnection client) {
        try {
            roomID = UserRequests.insertRoom(roomName);
            UserRequests.insertUserRoom(userID1, roomID);
            UserRequests.insertUserRoom(userID2, roomID);
        } catch( SQLException e ) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
