package backupconnection;

import communication.ClientConnection;
import message.UnsentMessages;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

/**
 * This class creates a connection to the main server
 * This extends the BackUpConnection class
 */
public class MainServerConnection extends BackUpConnection {
    /**
     * This variable is a server socket that uses the the Secure Sockets Layer (SSL)
     */
    private SSLServerSocket sslserversocket;

    /**
     * Main server connections' constructor
     */
    private MainServerConnection() {
        super();
        status.changeStatusThread();
        System.out.println("end");
    }

    /**
     * This function initiates the backup's connection
     *
     * @throws Exception This exception is thrown if the backup connection has already an instance
     */
    public static void initBackUpConnection() throws Exception {
        if (instance != null)
            throw new Exception("Singleton class BackUpConnection initiated twice");
        instance = new MainServerConnection();
        System.out.println("instance");
        instance.startServer();
        instance.startAcceptThread();
    }

    /**
     * This function starts the server
     */
    protected void startServer() {
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            sslserversocket = (SSLServerSocket) factory.createServerSocket(BACKUP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        sslserversocket.setNeedClientAuth(true);

        String[] ciphers = new String[1];
        ciphers[0] ="TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256";
        sslserversocket.setEnabledCipherSuites(ciphers);
    }

    /**
     * This function starts the acceptation of threads
     */
    protected void startAcceptThread() {
        System.out.println("Starting accept thread");
        try {
            SSLSocket sslSocket = (SSLSocket) sslserversocket.accept();
            backupChannel = new ClientConnection(sslSocket);
            backupChannel.setClientID(ClientConnection.serverID);
            status.finishedStatus();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void initialProtocol() {
        Thread thread = new Thread(() -> {
            UnsentMessages.send();
            waitProtocol();
            status.finishedStatus();
        });
        thread.setDaemon(true);
        thread.start();
    }

    protected void reconnectServer() {
        Thread thread = new Thread(this::startAcceptThread);
        thread.setDaemon(true);
        thread.start();
    }
}
