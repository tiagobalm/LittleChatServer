package message;

import communication.ClientConnection;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static message.MessageConstants.*;

public abstract class ReactMessage {
    protected String[] message;
    ReactMessage(String[] message) {
        this.message = message;
    }

    public void react(ClientConnection client) throws IOException {
        throw new AbstractMethodError("Wrong class");
    }

    @Nullable
    static ReactMessage getReactMessage(String message) {
        String[] parameters = message.split(" ");
        if( parameters.length < 1 )
            return null;

        String messageType = parameters[0];
        switch (messageType) {
            case loginType:
                return new LoginMessage(parameters);
            case registerType:
                return new RegisterMessage(parameters);
        }

        return null;
    }
}
