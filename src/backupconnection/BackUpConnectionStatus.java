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
        if (status == ServerCommunicationStatus.INITIALIZING)
            statusChange(ServerCommunicationStatus.SENDING_UNSENT);
        else if (status == ServerCommunicationStatus.RECONNECTING) {
            BackUpConnection.getInstance().reconnected();
            if (BackUpConnection.getInstance() instanceof BackUpServerConnection)
                disconnectClients = true;
            statusChange(ServerCommunicationStatus.SENDING_UNSENT);
        } else if (status == ServerCommunicationStatus.SENDING_UNSENT) {
            statusChange(ServerCommunicationStatus.OK);
            UserRequests.deleteUnsentMessages();
            if (BackUpConnection.getInstance() instanceof BackUpServerConnection &&
                disconnectClients) {
                disconnectClients = false;
                Thread thread = new Thread(this::reactOnEmpty);
                thread.setDaemon(true);
                thread.start();
            } else if (BackUpConnection.getInstance() instanceof MainServerConnection &&
                Server.getOurInstance().isShutdown())
                Server.getOurInstance().startClients();
        }
    }

    private void reactOnEmpty() {
        boolean again;
        do {
            try {
                again = false;
                Server.getOurInstance().getMessages().waitEmpty();
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
