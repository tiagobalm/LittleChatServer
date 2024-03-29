package backupconnection;

import communication.ClientConnection;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class creates the backup server
 * This extends the class BackupConnection
 */
public class BackUpServerConnection extends BackUpConnection {

    /**
     * Backup's internet protocol
     */
    private static final String BACKUP_IP = "127.0.0.1";

    private ScheduledExecutorService executeReconnect;

    /**
     * Backup server connection's constructor
     */
    private BackUpServerConnection() {
        super();
        status.changeStatusThread();
    }

    /**
     * This function initiates the backup's connection
     *
     * @throws Exception This exception is thrown if the backup connection has already an instance
     */
    public static void initBackUpConnection() throws Exception {
        if (instance != null)
            throw new Exception("Singleton class BackUpConnection initiated twice");
        instance = new BackUpServerConnection();
        instance.reconnectServer();
    }

    /**
     * This function starts the server
     */
    void startServer() {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            SSLSocket sslSocket = (SSLSocket) factory.createSocket(BACKUP_IP, BACKUP_PORT);
            String[] ciphers = new String[1];
            ciphers[0] = "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256";
            sslSocket.setEnabledCipherSuites(ciphers);
            backupChannel = new ClientConnection(sslSocket);
            backupChannel.setClientID(ClientConnection.serverID);
            status.finishedStatus();
        } catch (IOException ignore) {
        }
    }

    protected void reconnectServer() {
        if (executeReconnect == null || executeReconnect.isShutdown()) {
            Runnable startServer = this::startServer;
            executeReconnect = Executors.newScheduledThreadPool(1);
            executeReconnect.scheduleAtFixedRate(startServer, 0, 1, TimeUnit.SECONDS);
        }
    }

    protected void reconnected() {
        if(executeReconnect != null)
            executeReconnect.shutdown();
    }
}
