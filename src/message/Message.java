package message;

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
    private final String header;
    private String message;
    /**
     * Optional message
     */
    private List<String> optionalMessage;

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
     * This function gets the optional message
     * @return The optional message
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
}