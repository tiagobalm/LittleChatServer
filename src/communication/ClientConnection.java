package communication;

import message.Message;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.AbstractMap;

public class ClientConnection {
    private StreamMessage streamMessage;
    private Integer clientID;
    private Thread read;

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

    public void close() {
        //Server.getOurInstance().getClientSet().remove(this);
        streamMessage.close();
        read.interrupt();
    }

    public void setClientID(Integer id) {
        clientID = id;
    }

    public Integer getClientID() {
        return clientID;
    }

    public StreamMessage getStreamMessage() {
        return streamMessage;
    }
}
