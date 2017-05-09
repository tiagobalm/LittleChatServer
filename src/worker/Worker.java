package worker;

import communication.ClientConnection;
import communication.Server;
import message.WorkMessage;

import java.util.Map;


public class Worker implements Runnable{

    public Worker() {

    }

    @Override
    public void run() {
        while (true) {
            Map.Entry<ClientConnection, String> entry;
            try {
                entry = Server.getOurInstance().getMessages().take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            System.out.println("Process message: " + entry.getValue());
            WorkMessage processor = new WorkMessage(entry.getKey(), entry.getValue());
            processor.decode();
        }
    }
}
