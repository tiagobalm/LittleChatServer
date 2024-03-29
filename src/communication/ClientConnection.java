package communication;

import backupconnection.BackUpConnection;
import backupconnection.BackUpConnectionStatus;
import connectionListenner.SocketListener;
import message.Message;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLSocket;
import java.io.IOException;

/**
 * This class creates the connection on the client side
 */
public class ClientConnection implements SocketListener {
    public static final int serverID = -1;
    public static final int ownID = -2;

    /**
     * This represents the message streamed
     */
    @Nullable
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
    public ClientConnection(@Nullable SSLSocket sslSocket) {
        if (sslSocket == null)
            return;

        System.out.println("New client connection");

        streamMessage = new StreamMessage(sslSocket);

        read = new Thread(() -> {
            Message message;
            while (true) {
                try {
                    message = streamMessage.read();
                    System.out.println(message.getHeader());
                } catch (IOException e) {
                    this.close();
                    handleDisconnection();
                    return;
                } catch (ClassNotFoundException e) {
                    this.close();
                    return;
                }
                Server.getOurInstance().getMessages().put(this, message);
            }
        });

        read.setDaemon(true);
        read.start();
    }

    private void handleDisconnection() {
        if (clientID != null && clientID == serverID)
            BackUpConnection.getInstance().getStatus().statusChange(BackUpConnectionStatus.ServerCommunicationStatus.RECONNECTING);
    }

    /**
     * Closes the stream message and the thread
     */
    public void close() {
        assert streamMessage != null;
        streamMessage.close();
        read.interrupt();
    }

    /**
     * Gets the client's identifier
     * @return The client's identifier
     */
    public Integer getClientID() {
        return clientID;
    }

    /**
     * Sets the client identifier
     *
     * @param id New client's identifier
     */
    public void setClientID(Integer id) {
        clientID = id;
    }

    /**
     * Gets the streamed message
     * @return The streamed message
     */
    @Nullable
    public StreamMessage getStreamMessage() {
        return streamMessage;
    }

    @Override
    public void closeSocket() {
        this.close();
    }
}
