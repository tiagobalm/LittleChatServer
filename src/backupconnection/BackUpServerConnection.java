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
    static final String BACKUP_IP = "127.0.0.1";

    private ScheduledExecutorService executeReconnect;

    /**
     * Backup server connection's constructor
     */
    private BackUpServerConnection() {
        super();
        startServer();
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
        instance.status.changeStatusThread();
        instance.initialProtocol();
    }

    /**
     * This function starts the server
     */
    private void startServer() {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            SSLSocket sslSocket = (SSLSocket) factory.createSocket(BACKUP_IP, BACKUP_PORT);
            String[] ciphers = new String[1];
            ciphers[0] = "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256";
            sslSocket.setEnabledCipherSuites(ciphers);
            backupChannel = new ClientConnection(sslSocket);
            backupChannel.setClientID(ClientConnection.serverID);
            reconnected();
        } catch (IOException e) {
            System.out.println("Connection failed " + e.getMessage());
        }
    }

    protected void reconnectServer() {
        Runnable helloRunnable = this::startServer;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 5, TimeUnit.SECONDS);
    }

    protected void reconnected() {
        executeReconnect.shutdown();
        initialProtocol();
        status.statusChange(BackUpConnectionStatus.ServerCommunicationStatus.OK);
    }

    public void initialProtocol() {
        messagesProtocol.setStatus(UnsentMessages.UnsentMessagesStatus.READING);
        waitProtocol();
        messagesProtocol.setStatus(UnsentMessages.UnsentMessagesStatus.WRITTING);
        UnsentMessages.send();
        messagesProtocol.setStatus(UnsentMessages.UnsentMessagesStatus.DONE);
        status.statusChange(BackUpConnectionStatus.ServerCommunicationStatus.OK);
    }
}
