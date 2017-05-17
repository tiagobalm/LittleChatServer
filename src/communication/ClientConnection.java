package communication;

import message.Message;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.AbstractMap;

/**
 * This class creates the connection on the client side
 */
public class ClientConnection {
    /**
     * This represents the message streamed
     */
    private StreamMessage streamMessage;
    /**
     * Client's identifier
     */
    private Integer clientID;
    /**
     * Thread that reads the message
     */
    private Thread read;

    /**
     * ClientConnection's constructor
     * @param sslSocket Socket to be used in the connection
     */
    public ClientConnection(SSLSocket sslSocket) {
        streamMessage = new StreamMessage(sslSocket);

        read = new Thread(() -> {
            Message message;
            while (true) {
                try {
                    System.out.println("Wait message");
                    message = streamMessage.read();
                    System.out.println("Received message");
                } catch (IOException | ClassNotFoundException e){
                    this.close();
                    return;
                }
                try {
                    Server.getOurInstance().getMessages().put(new AbstractMap.SimpleEntry<>(this, message));
                } catch (InterruptedException e) {
                    this.close();
                    return ;
                }
            }
        });

        read.setDaemon(true);
        read.start();
    }

    /**
     * Closes the stream message and the thread
     */
    public void close() {
        //Server.getOurInstance().getClientSet().remove(this);
        streamMessage.close();
        read.interrupt();
    }

    /**
     * Sets the client identifier
     * @param id New client's identifier
     */
    public void setClientID(Integer id) {
        clientID = id;
    }

    /**
     * Gets the client's identifier
     * @return The client's identifier
     */
    public Integer getClientID() {
        return clientID;
    }

    /**
     * Gets the streamed message
     * @return The streamed message
     */
    public StreamMessage getStreamMessage() {
        return streamMessage;
    }
}
