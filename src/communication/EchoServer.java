package communication;

import Workers.Worker;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class EchoServer {
    private static final String keystorePath = EchoServer.class.getResource("../keys/server.private").getPath();
    private static final String keystorePass = "littlechat";
    private static final String truststorePath = EchoServer.class.getResource("../keys/truststore").getPath();
    private static final String truststorePass = "littlechat";

    private static boolean serverListening = true;
    private static SSLServerSocket sslserversocket;
    private static final int PORT = 15000;

    private static ScheduledExecutorService executor;
    private static int numberOfWorkerThreads = 20;

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", keystorePath);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
        System.setProperty("javax.net.ssl.trustStore", truststorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", truststorePass);
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        executor = Executors.newScheduledThreadPool(numberOfWorkerThreads);

        try {
            sslserversocket = (SSLServerSocket) factory.createServerSocket(PORT);
            sslserversocket.setNeedClientAuth(true);

            String[] ciphers = new String[1];
            ciphers[0] ="TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256";
            sslserversocket.setEnabledCipherSuites(ciphers);

            while (serverListening) {
                System.out.println("Waiting for client");

                // Accept return a new socket to handle the client.
                SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();

                executor.submit(new Worker(sslsocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
