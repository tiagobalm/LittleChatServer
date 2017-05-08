package communication;

import worker.WorkMessage;

import javax.net.ssl.SSLSocket;

public class ClientConnection {
    private StreamMessage streamMessage;
    private String username;

    ClientConnection(SSLSocket sslSocket) {
        streamMessage = new StreamMessage(sslSocket);

        Thread read = new Thread(() -> {
            while (true) {
                try {
                    String message = streamMessage.read();
                    String usr;

                    if( (usr = WorkMessage.getUserName(message)) == null )
                        continue;
                    username = usr;

                    System.out.println("New user: " + username);
                    Server.getOurInstance().getMessages().put(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        read.setDaemon(true);
        read.start();
    }

    public String getUsername() {
        return username;
    }

    public StreamMessage getStreamMessage() {
        return streamMessage;
    }
}
