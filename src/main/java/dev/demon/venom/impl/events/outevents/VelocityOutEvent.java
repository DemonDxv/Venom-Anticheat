package dev.demon.venom.impl.events.outevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VelocityOutEvent extends AnticheatEvent {

    private int id;
    private double x, y, z;

    public VelocityOutEvent(int id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
