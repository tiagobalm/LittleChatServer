package backupconnection;

import communication.ClientConnection;
import communication.StreamMessage;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

/**
 * This class creates the backup server
 * This extends the class BackupConnection
 */
public class BackUpServerConnection extends BackUpConnection {
    /**
     * Backup's internet protocol
     */
    static final String BACKUP_IP = "127.0.0.1";

    /**
     * Backup server connection's constructor
     */
    private BackUpServerConnection() {
        super();
        startServer();
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
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * This function initiates the backup's connection
     * @throws Exception This exception is thrown if the backup connection has already an instance
     */
    public static void initBackUpConnection() throws Exception {
        if( instance != null )
            throw new Exception("Singleton class BackUpConnection initiated twice");
        instance = new BackUpServerConnection();
    }
}
