package dev.demon.venom.impl.listeners;

import dev.demon.venom.Venom;
import dev.demon.venom.api.event.AnticheatListener;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    private List<AnticheatListener> listenerList = new ArrayList<>();

    public ListenerManager() {
     //   addListener(new PacketListener());
        setup();
    }

    private void setup() {
        listenerList.forEach(anticheatListener -> Venom.getInstance().getEventManager().registerListeners(anticheatListener, Venom.getInstance()));
    }

    private void addListener(AnticheatListener anticheatListener) {
        if (!listenerList.contains(anticheatListener)) listenerList.add(anticheatListener);
    }
}
