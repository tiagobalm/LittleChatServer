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
            if (clientConnection.getClientID() == ClientConnection.ownID &&
                    BackUpConnection.getInstance().getStatus().getStatus() != BackUpConnectionStatus.ServerCommunicationStatus.RECONNECTING) {
                react.send(BackUpConnection.getInstance().getBackupChannel(), react.message);
                System.out.println("analyze: Send message to other server");
                asServer = true;
            } else if (clientConnection.getClientID() == ClientConnection.serverID) {
                react.storeMessage(clientConnection);
                System.out.println("analyze: Storing message from other server");
                asServer = true;
            }
        }
        return asServer;
    }

    static void communicate(ReactMessage react) {
        if (BackUpConnection.getInstance().getStatus().getStatus() == BackUpConnectionStatus.ServerCommunicationStatus.RECONNECTING)
            try {
                UserRequests.insertUnsentMessage(react.message);
                System.out.println("Message from client: Reconnection: Stored in unsent messages");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //System.out.println("communicate: Message from client: Reconnection: Stored in unsent messages");
        else {
            ClientConnection cc = new ClientConnection(null);
            cc.setClientID(ClientConnection.ownID);
            Server.getOurInstance().getMessages().put(cc, react.message);
            System.out.println("communicate: Message from client: send it to other server");
        }
    }
}
