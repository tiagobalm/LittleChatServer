package worker;

import communication.ClientConnection;
import communication.Server;
import message.Message;
import message.ReactMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

/**
 * This class creates a worker thread
 * This class implements the Runnable interface
 */
public class Worker implements Runnable {
    /**
     * Worker thread's constructor
     */
    public Worker() {

    }

    /**
     * This functions runs the worker thread
     */
    @Override
    public void run() {
        while (true) {
            Map.Entry<ClientConnection, Message> entry;
            entry = Server.getOurInstance().getMessages().take();
            assert entry != null;
            decode(entry.getKey(), entry.getValue());
        }
    }

    /**
     * This function decodes the worker thread
     *
     * @param clientConnection Client's connection
     * @param message          Message that will be used
     */
    private void decode(@NotNull ClientConnection clientConnection, Message message) {
        System.out.println("React message: " + message.getHeader());
        ReactMessage reactMessage = ReactMessage.getReactMessage(message);
        if( reactMessage == null ) return ;
        try { reactMessage.react(clientConnection);
        } catch (IOException e) {
            e.printStackTrace();
            clientConnection.close();
        }
    }
}
