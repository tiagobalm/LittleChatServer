package backupconnection;

import communication.ClientConnection;
import message.UnsentMessages;
import org.jetbrains.annotations.Contract;

/**
 * Abstract class that creates the backup connection
 */
public abstract class BackUpConnection {
    /**
     * Port that will be used to do the backup
     */
    static final int BACKUP_PORT = 15001;

    /**
     * Backup connection's instance
     */
    static BackUpConnection instance;

    /**
     * Object that indicates if the thread can be unlocked
     */
    private final Object blockObject = new Object();

    /**
     * Channel where the backup will be made
     */
    ClientConnection backupChannel;

    BackUpConnectionStatus status;

    /**
     * Variable that indicates if the protocol is finished or not
     */
    private int nNoMoreMessages = 0;

    /**
     * BackupConnection's constructor
     */
    BackUpConnection() {
        try {
            UnsentMessages.create();
            status = new BackUpConnectionStatus();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * This function gets the backup connection's instance
     * @return The backup connection's instance
     */
    @Contract(pure = true)
    public static BackUpConnection getInstance() {
        return instance;
    }

    protected void startServer() {
    }

    protected void startAcceptThread() {
    }

    /**
     * This function gets the backup's channel
     * @return The backup's channel
     */
    public ClientConnection getBackupChannel() {
        return backupChannel;
    }

    public BackUpConnectionStatus getStatus() {
        return status;
    }

    /**
     * Notifies the thread that it can be unlocked
     */
    private void notifyAvailable() {
        synchronized(blockObject) {
            blockObject.notify();
        }
    }

    /**
     * Waits for object to be available
     */
    private void waitToBeAvailable() {
        synchronized(blockObject) {
            try {
                blockObject.wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * This function waits for the protocol to be finished
     */
    private void waitProtocol() {
        while (nNoMoreMessages != 2) {
            System.out.println("Waiting");
            waitToBeAvailable();
        }
        nNoMoreMessages = 0;
    }

    /**
     * This function finishes the protocol
     */
    public void setFinishedProtocol() {
        nNoMoreMessages++;// = true;
        if (nNoMoreMessages == 2) {
            System.out.println("Notify");
            notifyAvailable();
        }
    }

    void initialProtocol() {
        Thread thread = new Thread(() -> {
            UnsentMessages.send();
            waitProtocol();
            System.out.println("Over");
            status.finishedStatus();
        });
        thread.setDaemon(true);
        thread.start();
    }

    protected void reconnected() {
    }

    protected void reconnectServer() {
    }
}
