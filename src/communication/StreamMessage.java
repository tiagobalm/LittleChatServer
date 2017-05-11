package communication;

import message.Message;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StreamMessage {
    private SSLSocket sslSocket;
    private ObjectInputStream is;
    private ObjectOutputStream os;


    public StreamMessage(SSLSocket sslSocket) {
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

    public void close() {
        try {
            is.close();
            os.close();
            sslSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Message read() throws IOException, ClassNotFoundException {
        return (Message) is.readObject();
    }

    public void write(Message message) throws IOException {
        synchronized (os) {
            os.writeObject(message);
            os.flush();
        }
    }
}
