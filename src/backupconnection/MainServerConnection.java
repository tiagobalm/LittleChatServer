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
        startServer();
        startAcceptThread();
    }

    /**
     * This function initiates the backup's connection
     *
     * @throws Exception This exception is thrown if the backup connection has already an instance
     */
    public static void initBackUpConnection() throws Exception {
        System.out.println("burck urp");
        if (instance != null)
            throw new Exception("Singleton class BackUpConnection initiated twice");
        System.out.println("burck urp");
        instance = new MainServerConnection();
        System.out.println("burck urp");
        System.out.println("burck urp");
        //instance.initialProtocol();
        System.out.println("burck urp");
    }

    /**
     * This function starts the server
     */
    private void startServer() {
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
    private void startAcceptThread() {
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
            messagesProtocol.setStatus(UnsentMessages.UnsentMessagesStatus.WRITTING);
            UnsentMessages.send();
            messagesProtocol.setStatus(UnsentMessages.UnsentMessagesStatus.READING);
            waitProtocol();
            messagesProtocol.setStatus(UnsentMessages.UnsentMessagesStatus.DONE);
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

    protected void reconnected() {
        initialProtocol();
        status.statusChange(BackUpConnectionStatus.ServerCommunicationStatus.OK);
    }
}
