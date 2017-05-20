package communication;

import backupconnection.BackUpServerConnection;
import backupconnection.MainServerConnection;
import database.UserRequests;
import message.MessagesQueue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import worker.Worker;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server's main class
 */
public class Server {
    /**
     * Keystore path's string
     */
    private static final String keystorePath = Server.class.getResource("../keys/server.private").getPath();

    /**
     * Keystore pass
     */
    private static final String keystorePass = "littlechat";

    /**
     * Truststore path's name
     */
    private static final String truststorePath = Server.class.getResource("../keys/truststore").getPath();

    /**
     * Truststore pass
     */
    private static final String truststorePass = "littlechat";

    /**
     * Server's main port
     */
    private static final int MAIN_PORT = 15000;

    /**
     * Backup protocol's port
     */
    private static final int BACKUP_PORT = 14999;

    /**
     * Worker threads' number
     */
    private static final int numberOfWorkerThreads = 20;

    /**
     * Instance of server
     */
    private static Server ourInstance;

    /**
     * Variable that indicates if the server is backed up or not
     */
    private final boolean isBackUpServer;

    /**
     * Socket that will be used in this server
     */
    private SSLServerSocket sslserversocket;

    /**
     * Known clients that are saved in this server
     */
    private Map<Integer, ClientConnection> knownClients;

    /**
     * Messages saved in this server
     */
    private MessagesQueue messages;

    /**
     * Server's constructor
     * @param isBackUpServer Variable that indicates if the server is backed up pr not
     */
    private Server(boolean isBackUpServer) {
        this.isBackUpServer = isBackUpServer;
        knownClients = new HashMap<>();
        messages = new MessagesQueue();
    }

    /**
     * This function creates the server
     * @param isBackUpServer This variable indicates if a server is backed up or not
     * @throws Exception This exception is thrown if the server initiates a second instance
     */
    private static void createServer(boolean isBackUpServer) throws Exception {
        if( ourInstance != null )
            throw new Exception("Singleton class Server initiated twice");
        ourInstance = new Server(isBackUpServer);
    }

    /**
     * Gets the server's instance
     *
     * @return The server's instance
     */
    @Contract(pure = true)
    public static Server getOurInstance() {
        return ourInstance;
    }

    /**
     * Server's main function
     *
     * @param args Arguments used in the server's main function
     */
    public static void main(String[] args) {
        if (args.length != 1) return;
        boolean isBackUpServer = Objects.equals("true", args[0]);
        try {
            createServer(isBackUpServer);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        Server.getOurInstance().initialize();
        System.out.println("Server initialized");
    }

    /**
     * Initializes the server
     */
    private void initialize() {
        setSystemSettings();

        startWorkerThreads();

        startBackUpConnection();
        startServer(isBackUpServer ? BACKUP_PORT : MAIN_PORT);
        startAcceptThread();
    }

    /**
     * Starts the backup protocol's connection
     */
    private void startBackUpConnection() {
        try {
            System.out.println("burck durp");
            if( isBackUpServer )
                BackUpServerConnection.initBackUpConnection();
            else {
                System.out.println("burck durp");
                MainServerConnection.initBackUpConnection();
                System.out.println("burck durp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the server with the respective port
     * @param port Port that will used to create the socket
     */
    private void startServer(int port) {
        System.out.println("Starting server sockets");

        try { UserRequests.deleteUserConnections();
        } catch (SQLException ignore) {}

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

    /**
     * Starts the acceptation of threads
     */
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

    /**
     * Starts the worker threads
     */
    private void startWorkerThreads() {
        System.out.println("Starting worker threads");
        ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkerThreads);
        for( int i = 0; i < numberOfWorkerThreads; i++ ) {
            Thread thread = new Thread(new Worker());
            executor.execute(thread);
        }
    }

    /**
     * Sets the system's main settings
     */
    private void setSystemSettings() {
        System.setProperty("javax.net.ssl.keyStore", keystorePath);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
        System.setProperty("javax.net.ssl.trustStore", truststorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", truststorePass);
    }

    /**
     * Gets the messages saved in the server
     * @return The messages saved in the server
     */
    public MessagesQueue getMessages() {
        return messages;
    }

    /**
     * Gets the client's connection using is identifier
     * @param id The client's identifier
     * @return The client's connection
     */
    @Nullable
    public ClientConnection getClientByID(Integer id) {
        return knownClients.get(id);
    }

    /**
     * Adds a client by id, adding also its connection
     * @param id The client's identifier
     * @param client The client's connection
     */
    public void addClientID(Integer id, ClientConnection client) {
        client.setClientID(id);
        knownClients.put(id, client);
    }

    /**
     * Removes a client using its identifier
     * @param id The client's identifier
     */
    public void removeByID(Integer id) {
        ClientConnection c = knownClients.get(id);
        knownClients.remove(id);
        c.setClientID(null);
    }
}

