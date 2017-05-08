package Workers;

public class MessageDecode {
    public static String getUserName(String message) {
        String[] parameters = message.split(" ");
        return parameters.length >= 1 ? parameters[1] : null;
    }
}
