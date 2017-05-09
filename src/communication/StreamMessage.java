package communication;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StreamMessage {
    private static SSLSocket sslSocket;
    private static DataInputStream is;
    private static DataOutputStream os;


    public StreamMessage(SSLSocket sslSocket) {
        this.sslSocket = sslSocket;
        try {
            is = new DataInputStream(sslSocket.getInputStream());
            os = new DataOutputStream(sslSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void close() {
        try {
            sslSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() throws IOException {
        List<Byte> requestList = new ArrayList<>();
        byte character;

        while ((character = is.readByte()) != 0)
            requestList.add(character);

        byte[] request = byteListToByteArray(requestList);

        return new String(request);
    }

    public void write(byte[] message) throws IOException {
        os.write(message);
    }


    private byte[] byteListToByteArray(List<Byte> bytes) {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            result[i] = bytes.get(i);
        return result;
    }
}
