package message;

import backupconnection.BackUpConnection;
import backupconnection.BackUpConnectionStatus;
import communication.ClientConnection;
import communication.Server;
import database.UserRequests;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

class ToServerMessage {
    static boolean analyze(@NotNull ReactMessage react, @NotNull ClientConnection clientConnection) {
        boolean asServer = false;
        if (clientConnection.getClientID() != null) {
            if (clientConnection.getClientID() == ClientConnection.ownID &&
                    BackUpConnection.getInstance().getStatus().getStatus() != BackUpConnectionStatus.ServerCommunicationStatus.RECONNECTING) {
                react.send(BackUpConnection.getInstance().getBackupChannel(), react.message);
                asServer = true;
            } else if (clientConnection.getClientID() == ClientConnection.serverID) {
                if( !react.storeMessage(clientConnection) ) {
                    System.out.println("Message " + react.message.getHeader() + " failed to store");
                    Server.getOurInstance().getMessages().put(clientConnection, react.message);
                }
                asServer = true;
            }
        }
        return asServer;
    }

    static void communicate(@NotNull ReactMessage react) {
        if (BackUpConnection.getInstance().getStatus().getStatus() == BackUpConnectionStatus.ServerCommunicationStatus.RECONNECTING)
            try {
                UserRequests.insertUnsentMessage(react.message);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        else {
            ClientConnection cc = new ClientConnection(null);
            cc.setClientID(ClientConnection.ownID);
            Server.getOurInstance().getMessages().put(cc, react.message);
        }
    }
}
