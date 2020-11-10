package dev.demon.venom.impl.events.outevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeldItemSlotOutEvent extends AnticheatEvent {

    @Getter
    @Setter
    private int slot;

    public HeldItemSlotOutEvent(int slot) {
        this.slot = slot;
    }
}
