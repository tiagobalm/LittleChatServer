package connectionListenner;

import java.util.ArrayList;
import java.util.List;

public class SocketManager {
    List<SocketListener> listeners = new ArrayList<>();

    public void addListener(SocketListener l) {
        listeners.add(l);
    }

    public void close() {
        for(SocketListener l : listeners)
            l.closeSocket();
        listeners.clear();
    }

    public void removeListener(SocketListener l) {
        listeners.remove(l);
    }
}
