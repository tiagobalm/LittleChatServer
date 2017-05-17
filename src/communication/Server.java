package communication;

import backupconnection.BackUpConnection;
import backupconnection.BackUpServerConnection;
import backupconnection.MainServerConnection;
import message.Message;
import org.jetbrains.annotations.Nullable;
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

    private static final int MAIN_PORT = 15000;
    private static final int BACKUP_PORT = 14999;
    private static final int numberOfWorkerThreads = 20;

    private static Server ourInstance;

    private SSLServerSocket sslserversocket;
    private StreamMessage backupChannel;

    private Map<Integer, ClientConnection> knownClients;
    private BlockingQueue<Map.Entry<ClientConnection, Message>> messages;

    private final boolean isBackUpServer;

    private Server(boolean isBackUpServer) {
        this.isBackUpServer = isBackUpServer;
        knownClients = new HashMap<>();
        messages = new LinkedBlockingQueue<>();
    }

    private static void createServer(boolean isBackUpServer) throws Exception {
        if( ourInstance != null )
            throw new Exception("Singleton class Server initiated twice");
        ourInstance = new Server(isBackUpServer);
    }


    private void initialize() {
        setSystemSettings();
        startBackUpConnection();
        BackUpConnection.getInstance().waitProtocol();

        startWorkerThreads();
        startServer(isBackUpServer ? BACKUP_PORT : MAIN_PORT);
        startAcceptThread();
    }

    private void startBackUpConnection() {
        try {
            if( isBackUpServer )
                BackUpServerConnection.initBackUpConnection();
            else
                MainServerConnection.initBackUpConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        backupChannel = BackUpConnection.getInstance().getBackupChannel();
    }

    private void startServer(int port) {
        System.out.println("Starting server sockets");

        UserRequests.deleteUserConnections();
        setSystemSettings();

        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            sslserversocket = (SSLServerSocket) factory.createServerSocket(port);
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
                    new ClientConnection(sslsocket);
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
        ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkerThreads);
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


    @Nullable
    public ClientConnection getClientByID(Integer id) {
        return knownClients.get(id);
    }

    public void addClientID(Integer id, ClientConnection client) {
        client.setClientID(id);
        knownClients.put(id, client);
    }

    public void removeByID(Integer id) {
        ClientConnection c = knownClients.get(id);
        knownClients.remove(id);
        c.setClientID(null);
    }


    public static void main(String[] args) {
        if( args.length != 1 ) return;
        boolean isBackUpServer = Objects.equals("true", args[0]);
        try { createServer(isBackUpServer);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        Server.getOurInstance().initialize();
        System.out.println("Server initialized");
    }
}

