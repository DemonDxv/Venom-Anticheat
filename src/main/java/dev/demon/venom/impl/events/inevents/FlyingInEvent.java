package dev.demon.venom.impl.events.inevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlyingInEvent extends AnticheatEvent {

    @Getter
    @Setter
    private double x, y, z;
    private float yaw, pitch;
    private boolean clientGround, pos, look;

    public FlyingInEvent(double x, double y, double z, float pitch, float yaw, boolean clientGround, boolean pos, boolean look) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.clientGround = clientGround;
        this.pos = pos;
        this.look = look;
    }
}
