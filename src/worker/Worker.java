package worker;

import communication.ClientConnection;
import communication.Server;
import message.Message;
import message.ReactMessage;

import java.io.IOException;
import java.util.Map;


public class Worker implements Runnable {
    public Worker() {

    }

    @Override
    public void run() {
        while (true) {
            Map.Entry<ClientConnection, Message> entry;
            try {
                entry = Server.getOurInstance().getMessages().take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            System.out.println("Process message: " + entry.getValue());
            decode(entry.getKey(), entry.getValue());
        }
    }

    private void decode(ClientConnection clientConnection, Message message) {
        ReactMessage reactMessage = ReactMessage.getReactMessage(message);
        if( reactMessage == null ) return ;
        try { reactMessage.react(clientConnection);
        } catch (IOException e) {
            e.printStackTrace();
            clientConnection.close();
        }
    }
}
