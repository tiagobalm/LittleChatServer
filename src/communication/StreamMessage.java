package communication;

import message.Message;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class builds a streamed message
 */
public class StreamMessage {
    /**
     * SSL socket to be read/write the streamed message
     */
    @NotNull
    private final SSLSocket sslSocket;
    private final Object inputObject = new Object();
    /**
     *  Input stream
     */
    private ObjectInputStream is;
    /**
     * Output stream
      */
    private ObjectOutputStream os;

    /**
     * StreamMessage's constructor
     * @param sslSocket SSL socket
     */
    StreamMessage(@NotNull SSLSocket sslSocket) {
        this.sslSocket = sslSocket;
        try {
            os = new ObjectOutputStream(sslSocket.getOutputStream());
            os.flush();
            is = new ObjectInputStream(sslSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Closes the input and output streams and the SSL socket
     */
    void close() {
        try {
            is.close();
            os.close();
            sslSocket.close();
        } catch (IOException ignore) {
        }
    }

    /**
     * Reads the message
     * @return The message read
     * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations
     * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name, but no definition for the class with the specified name could be found
     */
    @NotNull
    synchronized Message read() throws IOException, ClassNotFoundException {
        return (Message) is.readObject();
    }

    /**
     * Writes the message
     * @param message Message that will be written
     * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations
     */
    public void write(Message message) throws IOException {
        synchronized (inputObject) {
            os.writeObject(message);
            os.flush();
        }
    }
}
