package backupconnection;

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
                System.out.println(currStatus);
                System.out.println(status);
                if (currStatus != status) handleStatusChange();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void handleStatusChange() {
        if (status == ServerCommunicationStatus.RECONNECTING)
            BackUpConnection.getInstance().reconnectServer();
        else if (status == ServerCommunicationStatus.SENDING_UNSENT)
            BackUpConnection.getInstance().initialProtocol();
    }

    public void finishedStatus() {
        if (status == ServerCommunicationStatus.INITIALIZING)
            statusChange(ServerCommunicationStatus.SENDING_UNSENT);
        else if (status == ServerCommunicationStatus.RECONNECTING) {
            BackUpConnection.getInstance().reconnected();
            statusChange(ServerCommunicationStatus.SENDING_UNSENT);
        } else if (status == ServerCommunicationStatus.SENDING_UNSENT) {
            statusChange(ServerCommunicationStatus.OK);
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
