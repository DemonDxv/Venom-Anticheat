package dev.demon.venom.impl.events.inevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionEvent extends AnticheatEvent {

    private int id;
    private short action;
    private boolean accept;

    public TransactionEvent(int id, short action, boolean accept) {
        this.id = id;
        this.action = action;
        this.accept = accept;
    }
}
