package communication;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.AbstractMap;

public class ClientConnection {
    private StreamMessage streamMessage;
    private Thread read;

    ClientConnection(SSLSocket sslSocket) {
        streamMessage = new StreamMessage(sslSocket);

        read = new Thread(() -> {
            String message;
            while (true) {
                try {
                    message = streamMessage.read();
                } catch (IOException e) {
                    this.close();
                    return ;
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

    public void close() {
        //Server.getOurInstance().getClientSet().remove(this);
        streamMessage.close();
        read.interrupt();
    }

    public StreamMessage getStreamMessage() {
        return streamMessage;
    }
}
