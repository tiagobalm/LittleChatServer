package backupconnection;

import communication.StreamMessage;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class MainServerConnection extends BackUpConnection {
    private SSLServerSocket sslserversocket;

    private MainServerConnection() {
        super();
        startServer();
        startAcceptThread();
    }

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

    private void startAcceptThread() {
        System.out.println("Starting accept thread");
        try {
            SSLSocket sslSocket = (SSLSocket) sslserversocket.accept();
            backupChannel = new StreamMessage(sslSocket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void initBackUpConnection() throws Exception {
        if( instance != null )
            throw new Exception("Singleton class BackUpConnection initiated twice");
        instance = new MainServerConnection();
    }
}
