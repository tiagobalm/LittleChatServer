package Workers;

import database.users.UserRequests;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Worker extends Thread {
    //private SSLSocket sslSocket;
    private static DataInputStream is;
    private static DataOutputStream os;
    //private static boolean readSomething;

    public Worker(SSLSocket sslsocket) {
        //sslSocket = sslsocket;
        //readSomething = true;
        System.out.println("Connected? " + UserRequests.userConnected("vascoUP"));

        try {
            is = new DataInputStream(sslsocket.getInputStream());
            os = new DataOutputStream(sslsocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client connected in IP: ");
    }

    public void run() {

        while (true) {
            List<Byte> requestList = new ArrayList<>();
            byte character;

            try {
                while ((character = is.readByte()) != 0)
                    requestList.add(character);

                byte[] request = byteListToByteArray(requestList);
                String response = new String(request);

                System.out.println("Client said: " + response);
                String[] parameters = response.split(" ");

                System.out.println("Connected? " + UserRequests.userConnected(parameters[1]));

                if (parameters[0].equals("LOGIN")) {
                    if (UserRequests.loginUser(parameters[1], parameters[2], "", 0))
                        os.write("True\0".getBytes());
                    else
                        os.write("False\0".getBytes());

                    System.out.println("Connected? " + UserRequests.userConnected(parameters[1]));
                }

                if (parameters[0].equals("REGISTER"))
                    UserRequests.registerUser(parameters[1], parameters[2]);

            } catch (Exception ignore) {
            }
        }
    }

    private byte[] byteListToByteArray(List<Byte> bytes) {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            result[i] = bytes.get(i);
        return result;
    }
}