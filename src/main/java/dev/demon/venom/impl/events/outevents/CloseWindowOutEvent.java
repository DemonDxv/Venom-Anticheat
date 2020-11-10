package dev.demon.venom.impl.events.outevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloseWindowOutEvent extends AnticheatEvent {

    @Getter
    @Setter
    private int id;

    public CloseWindowOutEvent(int id) {
        this.id = id;
    }
}
