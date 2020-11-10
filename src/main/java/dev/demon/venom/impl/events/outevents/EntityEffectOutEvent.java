package dev.demon.venom.impl.events.outevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityEffectOutEvent extends AnticheatEvent {
    private byte effectID;

    public EntityEffectOutEvent(byte effectID) {
        this.effectID = effectID;
    }
}