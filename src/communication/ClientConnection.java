package communication;

import message.WorkMessage;

import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class ClientConnection {
    private StreamMessage streamMessage;
    private String username;
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

                String usr;
                if( (usr = WorkMessage.getUserName(message)) == null )
                    continue;
                username = usr;

                System.out.println("New user: " + username);
                try {
                    Server.getOurInstance().getMessages().put(message);
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
        System.out.println((username == null ? "Unknown client" : username) + " disconnect");
        Server.getOurInstance().getClientSet().remove(this);
        streamMessage.close();
        read.interrupt();
    }

    public String getUsername() {
        return username;
    }

    public StreamMessage getStreamMessage() {
        return streamMessage;
    }
}
