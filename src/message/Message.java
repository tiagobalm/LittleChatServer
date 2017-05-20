package message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * This class builds the message to be sent
 * This class implements the Serializable interface
 */
public class Message implements Serializable {
    /**
     * Message's header and content
     */
    private String header, message;
    /**
     * Optional message
     */
    private List<String> optionalMessage;
    /**
     * Buffered image
     */
    private BufferedImage image;

    /**
     * Message's constructor
     * @param header Message's header
     * @param message Message's content
     */
    public Message(String header, String message) {
        this.header = header;
        this.message = message;
    }

    /**
     * Message's cconstructor
     * @param header Message's header
     * @param optionalMessage Optional message
     */
    public Message(String header, List<String> optionalMessage) {
        this.header = header;
        this.optionalMessage = optionalMessage;
    }

    /**
     * Message's constructor
     * @param path Image's path
     */
    public Message(String path) {
        try {
            this.image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function gets the optional message
     * @return The optional message
     */
    public List<String> getOptionalMessage() {
        return optionalMessage;
    }

    /**
     <<<<<<< HEAD
     * Set optional messages.
     *
     * @param optionalMessage optional message.
     */
    public void setOptionalMessage(List<String> optionalMessage) {
        this.optionalMessage = optionalMessage;
    }

    /**
     * This function gets the message's header
     * @return The message's header
     */
    public String getHeader() {
        return header;
    }

    /**
     * This function gets the message's content
     * @return The message's content
     */
    public String getMessage() {
        return message;
    }

    /**
     * This function gets the buffered image
     * @return The buffered image
     */
    public BufferedImage getImage() {
        return image;
    }
}