package message;

import backupconnection.BackUpConnection;
import communication.Server;
import database.UserRequests;
import org.jetbrains.annotations.Contract;

import java.util.List;

public class UnsentMessages {
    private static UnsentMessages instance;
    private UnsentMessagesStatus status;

    private UnsentMessages(UnsentMessagesStatus status) {
        this.status = status;
    }

    public static void create(UnsentMessagesStatus status) throws Exception {
        if (instance != null)
            throw new Exception("Singleton class Server initiated twice");
        instance = new UnsentMessages(status);
    }

    @Contract(pure = true)
    public static UnsentMessages getInstance() {
        return instance;
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

    public void setStatus(UnsentMessagesStatus status) {
        this.status = status;
    }

    public enum UnsentMessagesStatus {READING, WRITTING, DONE}
}
