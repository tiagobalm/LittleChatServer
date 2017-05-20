package message;

import backupconnection.BackUpConnection;
import backupconnection.BackUpConnectionStatus;
import communication.ClientConnection;
import communication.Server;
import database.UserRequests;

import java.sql.SQLException;

class ToServerMessage {
    static boolean analyze(ReactMessage react, ClientConnection clientConnection) {
        boolean asServer = false;
        if (clientConnection.getClientID() != null) {
            System.out.println(clientConnection.getClientID());
            if (clientConnection.getClientID() == ClientConnection.ownID &&
                    BackUpConnection.getInstance().getStatus().getStatus() != BackUpConnectionStatus.ServerCommunicationStatus.RECONNECTING) {
                react.send(BackUpConnection.getInstance().getBackupChannel(), react.message);
                asServer = true;
            } else if (clientConnection.getClientID() == ClientConnection.serverID) {
                react.storeMessage(clientConnection);
                asServer = true;
            }
        }
        return asServer;
    }

    static void communicate(ReactMessage react) {
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
