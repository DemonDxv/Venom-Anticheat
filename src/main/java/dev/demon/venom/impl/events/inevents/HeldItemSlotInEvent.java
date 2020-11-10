package dev.demon.venom.impl.events.inevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeldItemSlotInEvent extends AnticheatEvent {

    @Getter
    @Setter
    private int slot;

    public HeldItemSlotInEvent(int slot) {
        this.slot = slot;
    }
}
