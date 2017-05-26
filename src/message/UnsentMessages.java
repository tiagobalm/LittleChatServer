package message;

import communication.ClientConnection;
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
            ClientConnection me = new ClientConnection(null);
            me.setClientID(ClientConnection.ownID);

            for (Message m : unsentMessages) {
                System.out.println("Sending unsent: " + m.getHeader());
                Server.getOurInstance().getMessages().put(me, m);
            }
            Server.getOurInstance().getMessages().put(me,
                    new Message(MessageConstants.noMoreMessagesType, ""));
        }

        UserRequests.deleteUnsentMessages();
    }

    private static List<Message> getUnsentMessages() {
        return UserRequests.getUnsentMessages();
    }
}
