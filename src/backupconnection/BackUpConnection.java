package backupconnection;

import communication.ClientConnection;
import org.jetbrains.annotations.Contract;

/**
 * Abstract class that creates the backup connection
 */
public abstract  class BackUpConnection {
    /**
     * Port that will be used to do the backup
     */
    static final int BACKUP_PORT = 15001;
    /**
     * Backup connection's instance
     */
    static BackUpConnection instance;

    /**
     * Channel where the backup will be made
     */
    ClientConnection backupChannel;
    /**
     * Object that indicates if the thread can be unlocked
     */
    public Object blockObject = new Object();
    /**
     * Variable that indicates if the protocol is finished or not
     */
    public boolean protocolFinished = false;

    public boolean isOn = false;

    /**
     * BackupConnection's constructor
     */
    BackUpConnection() {

    }

    /**
     * This function gets the backup connection's instance
     * @return The backup connection's instance
     */
    @Contract(pure = true)
    public static BackUpConnection getInstance() {
        return instance;
    }

    /**
     * This function gets the backup's channel
     * @return The backup's channel
     */
    public ClientConnection getBackupChannel() {
        return backupChannel;
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
    public void waitProtocol() {
        /*while( !protocolFinished )
            waitToBeAvailable();*/
    }

    /**
     * This function finishes the protocol
     */
    public void setFinishedProtocol() {
        protocolFinished = true;
        notifyAvailable();
    }
}
