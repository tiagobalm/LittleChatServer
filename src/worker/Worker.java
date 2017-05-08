package worker;

import communication.Server;


public class Worker implements Runnable{

    public Worker() {

    }

    @Override
    public void run() {
        while (true) {
            String message;
            try {
                System.out.println("Waiting message");
                message = Server.getOurInstance().getMessages().take();
                System.out.println("Message received");
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            System.out.println("Process message: " + message);
            WorkMessage processor = new WorkMessage(message);
            processor.decode();
        }
    }
}
