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

    /**
     * Message.
     *
     * @param header  Message header.
     * @param message Message text.
     */
    public Message(String header, String message) {
        this.header = header;
        this.message = message;
    }

    /**
     * Message.
     * @param header Message header.
     * @param optionalMessage Optional Message.
     */
    public Message(String header, List<String> optionalMessage) {
        this.header = header;
        this.optionalMessage = optionalMessage;
    }

    /**
     * Message
     * @param path Path to message.
     */
    public Message(String path) {
        try {
            this.image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get optional message.
     * @return optional message.
     */
    public List<String> getOptionalMessage() {
        return optionalMessage;
    }

    /**
     * Set optional messages.
     *
     * @param optionalMessage optional message.
     */
    public void setOptionalMessage(List<String> optionalMessage) {
        this.optionalMessage = optionalMessage;
    }

    /**
     * Gey header message.
     * @return header.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Get message.
     * @return message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get Image.
     * @return BufferedImage image.
     */
    public BufferedImage getImage() {
        return image;
    }
}