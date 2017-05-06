package communication;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EchoServer {
    private static final String keystorePath = EchoServer.class.getResource("../keystore").getPath();
    private static final String keystorePass = "gruposdis";
    private static final String truststorePath = EchoServer.class.getResource("../truststore").getPath();
    private static final String truststorePass = "gruposdis";

    private static boolean serverListening = true;
    private static SSLServerSocket sslserversocket;
    private static final int PORT = 15000;
    private static DataInputStream is;
    private static DataOutputStream os;

    public static void main(String[] args) {
        System.out.println(keystorePath);
        System.out.println(keystorePass);
        System.out.println(truststorePath);
        System.out.println(truststorePass);
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            sslserversocket = (SSLServerSocket) factory.createServerSocket(PORT);
            sslserversocket.setNeedClientAuth(true);
            String[] ciphers = new String[1];
            ciphers[0] ="TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256";
            sslserversocket.setEnabledCipherSuites(ciphers);
            while (serverListening) {
                System.out.println("Waiting for client");
                // Accept return a new socket to handle the client.
                SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();

                is = new DataInputStream(sslsocket.getInputStream());
                os = new DataOutputStream(sslsocket.getOutputStream());
                System.out.println("Client connected");

                List<Byte> message = new ArrayList<>();
                byte character;

                while ((character = is.readByte()) != 0)
                    message.add(character);

                byte[] messageBytes = byteListToByteArray(message);
                String response = new String(messageBytes);
                System.out.println("Client sad: " + response);

                os.write("Welcome\0".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static byte[] byteListToByteArray(List<Byte> bytes) {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            result[i] = bytes.get(i);
        return result;
    }
}
