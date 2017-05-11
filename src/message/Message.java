package message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Message {
    private String header, message, serverAnswer;
    private ArrayList<String> optionalMessage;
    private BufferedImage image;

    public Message(String header, String message) {
        this.header = header;

        this.message = message;
    }

    public Message(String path) {
        try {
            this.image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getOptionalMessage() {
        return optionalMessage;
    }

    public String getHeader() {
        return header;
    }

    public String getMessage() {
        return message;
    }

    public BufferedImage getImage() {
        return image;
    }
    public String getServerAnswer() {
        return serverAnswer;
    }

}
