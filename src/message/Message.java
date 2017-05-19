package message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private String header, message;
    private List<String> optionalMessage;
    private BufferedImage image;

    public Message(String header, String message) {
        this.header = header;
        this.message = message;
    }

    public Message(String header, List<String> optionalMessage) {
        this.header = header;
        this.optionalMessage = optionalMessage;
    }

    public Message(String path) {
        try {
            this.image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getOptionalMessage() {
        return optionalMessage;
    }

    public String getHeader() {
        return header;
    }

    public String getMessage() {
        return message;
    }

    public void setOptionalMessage(List<String> optionalMessage) {
        this.optionalMessage = optionalMessage;
    }

    public BufferedImage getImage() {
        return image;
    }
}
