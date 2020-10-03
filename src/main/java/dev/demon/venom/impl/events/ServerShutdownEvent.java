package dev.demon.venom.impl.events;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ServerShutdownEvent extends AnticheatEvent implements Cancellable {

    @Setter
    private Object packet;

    @Setter
    private boolean cancelled;



    public ServerShutdownEvent() {
    }
}
