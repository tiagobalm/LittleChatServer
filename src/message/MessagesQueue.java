package message;

import communication.ClientConnection;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessagesQueue {
    private static final int MAXTRIES = 3;

    /**
     * Messages saved in this server
     */
    private BlockingQueue<Map.Entry<ClientConnection, Message>> messages;

    public MessagesQueue() {
        messages = new LinkedBlockingQueue<>();
    }

    public void put(ClientConnection c, Message m) {
        int nTries = 0;
        boolean next = true;
        Map.Entry<ClientConnection, Message> entry = new AbstractMap.SimpleEntry<>(c, m);
        do {
            try {
                messages.put(entry);
            } catch (InterruptedException e) {
                if (nTries >= MAXTRIES) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                nTries++;
                continue;
            }
            next = false;
        } while (next);
    }

    public Map.Entry<ClientConnection, Message> take() {
        int nTries = 0;
        boolean next = true;
        Map.Entry<ClientConnection, Message> entry = null;
        do {
            try {
                entry = messages.take();
            } catch (InterruptedException e) {
                if (nTries >= MAXTRIES) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                nTries++;
                continue;
            }
            next = false;
        } while (next);

        return entry;
    }
}
