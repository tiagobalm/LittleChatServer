package message;

import backupconnection.BackUpConnection;
import communication.Server;
import database.UserRequests;

import java.util.List;

public class UnsentMessages {
    private static UnsentMessages instance;

    private UnsentMessages() {
    }

    public static void create() throws Exception {
        if (instance != null)
            throw new Exception("Singleton class Server initiated twice");
        instance = new UnsentMessages();
    }

    public static void send() {
        List<Message> unsentMessages = getUnsentMessages();
        synchronized (Server.getOurInstance().getMessages()) {
            for (Message m : unsentMessages)
                Server.getOurInstance().getMessages().put(BackUpConnection.getInstance().getBackupChannel(), m);
            Server.getOurInstance().getMessages().put(BackUpConnection.getInstance().getBackupChannel(),
                    new Message(MessageConstants.noMoreMessagesType, ""));
        }
    }

    private static List<Message> getUnsentMessages() {
        return UserRequests.getUnsentMessages();
    }
}
