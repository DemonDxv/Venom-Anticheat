package dev.demon.venom.api.event;

import dev.demon.venom.impl.events.PacketEvent;

public interface ListenerInterface {
    void onPacket(PacketEvent event);
}
