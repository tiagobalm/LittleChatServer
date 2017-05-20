package backupconnection;

import communication.Server;
import database.UserRequests;

public class BackUpConnectionStatus {
    private final Object statusObject = new Object();
    private ServerCommunicationStatus status = ServerCommunicationStatus.INITIALIZING;
    BackUpConnectionStatus() {
    }

    public ServerCommunicationStatus getStatus() {
        return status;
    }

    void changeStatusThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                ServerCommunicationStatus currStatus = status;
                waitStatusChange();
                handleStatusChange();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void handleStatusChange() {
        System.out.println(status);
        if (status == ServerCommunicationStatus.RECONNECTING) {
            BackUpConnection.getInstance().reconnectServer();
            if (BackUpConnection.getInstance() instanceof BackUpServerConnection)
                Server.getOurInstance().startClients();
        }
        else if (status == ServerCommunicationStatus.SENDING_UNSENT)
            BackUpConnection.getInstance().initialProtocol();
    }

    void finishedStatus() {
        if (status == ServerCommunicationStatus.INITIALIZING)
            statusChange(ServerCommunicationStatus.SENDING_UNSENT);
        else if (status == ServerCommunicationStatus.RECONNECTING) {
            BackUpConnection.getInstance().reconnected();
            statusChange(ServerCommunicationStatus.SENDING_UNSENT);
        } else if (status == ServerCommunicationStatus.SENDING_UNSENT) {
            statusChange(ServerCommunicationStatus.OK);
            UserRequests.deleteUnsentMessages();
            if (BackUpConnection.getInstance() instanceof BackUpServerConnection) {
                Thread thread = new Thread(() -> {
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
                });
                thread.setDaemon(true);
                thread.start();
            }
        }
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
