package backupconnection;

import communication.ClientConnection;
import message.UnsentMessages;

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
        instance.startServer();
    }

    /**
     * This function starts the server
     */
    protected void startServer() {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            SSLSocket sslSocket = (SSLSocket) factory.createSocket(BACKUP_IP, BACKUP_PORT);
            String[] ciphers = new String[1];
            ciphers[0] = "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256";
            sslSocket.setEnabledCipherSuites(ciphers);
            backupChannel = new ClientConnection(sslSocket);
            backupChannel.setClientID(ClientConnection.serverID);
            status.finishedStatus();
        } catch (IOException e) {
            System.out.println("Connection failed " + e.getMessage());
        }
    }

    protected void reconnectServer() {
        if (executeReconnect.isShutdown()) {
            Runnable startServer = this::startServer;
            executeReconnect = Executors.newScheduledThreadPool(1);
            executeReconnect.scheduleAtFixedRate(startServer, 0, 5, TimeUnit.SECONDS);
        }
    }

    protected void reconnected() {
        executeReconnect.shutdown();
    }

    public void initialProtocol() {
        Thread thread = new Thread(() -> {
            waitProtocol();
            UnsentMessages.send();
            status.finishedStatus();
        });
        thread.setDaemon(true);
        thread.start();
    }
}
