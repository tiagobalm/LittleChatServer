package backupconnection;

import communication.StreamMessage;
import org.jetbrains.annotations.Contract;

public abstract  class BackUpConnection {
    static final int BACKUP_PORT = 15001;
    static BackUpConnection instance;

    StreamMessage backupChannel;
    public Object blockObject = new Object();
    public boolean protocolFinished = false;

    BackUpConnection() {

    }

    @Contract(pure = true)
    public static BackUpConnection getInstance() {
        return instance;
    }

    public StreamMessage getBackupChannel() {
        return backupChannel;
    }

    private void notifyAvailable() {
        synchronized(blockObject) {
            blockObject.notify();
        }
    }

    private void waitToBeAvailable() {
        synchronized(blockObject) {
            try {
                blockObject.wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void waitProtocol() {
        /*while( !protocolFinished )
            waitToBeAvailable();*/
    }

    public void setFinishedProtocol() {
        protocolFinished = true;
        notifyAvailable();
    }

}
