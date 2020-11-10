package dev.demon.venom.impl.events.inevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloseWindowInEvent extends AnticheatEvent {

    @Getter
    @Setter
    private int id;

    public CloseWindowInEvent(int id) {
        this.id = id;
    }
}
