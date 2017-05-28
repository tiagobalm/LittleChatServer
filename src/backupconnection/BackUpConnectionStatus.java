package backupconnection;

import communication.Server;
import database.UserRequests;

public class BackUpConnectionStatus {
    private final Object statusObject = new Object();
    private ServerCommunicationStatus status = ServerCommunicationStatus.INITIALIZING;
    private boolean disconnectClients = false;

    BackUpConnectionStatus() {
    }

    public ServerCommunicationStatus getStatus() {
        return status;
    }

    void changeStatusThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                waitStatusChange();
                handleStatusChange();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void handleStatusChange() {
        if (status == ServerCommunicationStatus.RECONNECTING) {
            BackUpConnection.getInstance().reconnectServer();
            if (BackUpConnection.getInstance() instanceof BackUpServerConnection)
                Server.getOurInstance().startClients();
        } else if (status == ServerCommunicationStatus.SENDING_UNSENT) {
            BackUpConnection.getInstance().initialProtocol();
        }
    }

    void finishedStatus() {
        if (status == ServerCommunicationStatus.INITIALIZING) {
            statusChange(ServerCommunicationStatus.SENDING_UNSENT);
            BackUpConnection.getInstance().reconnected();
        } else if (status == ServerCommunicationStatus.RECONNECTING) {
            BackUpConnection.getInstance().reconnected();
            if (BackUpConnection.getInstance() instanceof BackUpServerConnection)
                disconnectClients = true;
            System.out.println("Disconnect clients: " + disconnectClients);
            statusChange(ServerCommunicationStatus.SENDING_UNSENT);
        } else if (status == ServerCommunicationStatus.SENDING_UNSENT) {
            boolean waitEmpty = false;
            Thread thread = null;
            if (BackUpConnection.getInstance() instanceof BackUpServerConnection &&
                disconnectClients) {
                waitEmpty = true;
                disconnectClients = false;
                System.out.println("React on emtpy");
                thread = new Thread(this::reactOnEmpty);
                thread.setDaemon(true);
            }
            statusChange(ServerCommunicationStatus.OK);
            if(waitEmpty) thread.start();
        } else if (BackUpConnection.getInstance() instanceof MainServerConnection &&
            Server.getOurInstance().isShutdown())
            Server.getOurInstance().startClients();
    }

    private void reactOnEmpty() {
        boolean again;
        do {
            try {
                again = false;
                System.out.println("react on empty: bf wait");
                Server.getOurInstance().getMessages().waitEmpty();
                System.out.println("react on empty: af wait");
            } catch (InterruptedException e) {
                again = true;
            }
        } while (again);
        Server.getOurInstance().disconnectClients();

    }

    public void statusChange(ServerCommunicationStatus newStatus) {
        status = newStatus;
        synchronized (statusObject) {
            statusObject.notify();
        }
    }

    private void waitStatusChange() {
        synchronized (statusObject) {
            try {
                statusObject.wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public enum ServerCommunicationStatus {INITIALIZING, OK, RECONNECTING, SENDING_UNSENT}
}
