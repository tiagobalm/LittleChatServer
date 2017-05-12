package communication;

import message.Message;
import worker.*;
import database.users.UserRequests;
import org.jetbrains.annotations.Contract;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final String keystorePath = Server.class.getResource("../keys/server.private").getPath();
    private static final String keystorePass = "littlechat";
    private static final String truststorePath = Server.class.getResource("../keys/truststore").getPath();
    private static final String truststorePass = "littlechat";

    private static final int PORT = 15000;
    private static final int numberOfWorkerThreads = 20;

    private static Server ourInstance = new Server();

    private SSLServerSocket sslserversocket;
    private ExecutorService executor;

    private List<ClientConnection> connectedClients;
    private BlockingQueue<Map.Entry<ClientConnection, Message>> messages;

    private Server() {
        connectedClients = new ArrayList<>();
        messages = new LinkedBlockingQueue<>();
        startServer();
        startAcceptThread();
        startWorkerThreads();
    }

    private void startServer() {
        System.out.println("Starting server sockets");

        UserRequests.deleteUserConnections();
        setSystemSettings();

        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            sslserversocket = (SSLServerSocket) factory.createServerSocket(PORT);
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
        Thread accept = new Thread(() -> {
            while(true) {
                try {
                    SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();
                    System.out.println("New client");
                    connectedClients.add(new ClientConnection(sslsocket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        accept.setDaemon(true);
        accept.start();
    }

    private void startWorkerThreads() {
        System.out.println("Starting worker threads");
        executor = Executors.newFixedThreadPool(numberOfWorkerThreads);
        for( int i = 0; i < numberOfWorkerThreads; i++ ) {
            Thread thread = new Thread(new Worker());
            executor.execute(thread);
        }
    }

    private void setSystemSettings() {
        System.setProperty("javax.net.ssl.keyStore", keystorePath);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
        System.setProperty("javax.net.ssl.trustStore", truststorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", truststorePass);
    }


    @Contract(pure = true)
    public static Server getOurInstance() {
        return ourInstance;
    }

    public BlockingQueue<Map.Entry<ClientConnection, Message>> getMessages() {
        return messages;
    }

    public List<ClientConnection> getConnectedClients() {
        return connectedClients;
    }

    public static void main(String[] args) {
        System.out.println("Server initialized");
    }
}

