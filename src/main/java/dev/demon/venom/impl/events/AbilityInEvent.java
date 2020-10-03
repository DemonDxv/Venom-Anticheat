package dev.demon.venom.impl.events;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbilityInEvent extends AnticheatEvent {

    private double flySpeed, walkSpeed;
    private boolean isFlying, allowedFlight, creativeMode, invulnerable;

    public AbilityInEvent(boolean allowedFlight, boolean isFlying, boolean creativeMode, boolean invulnerable, double flySpeed, double walkSpeed) {
        this.allowedFlight = allowedFlight;
        this.isFlying = isFlying;
        this.creativeMode = creativeMode;
        this.invulnerable = invulnerable;
        this.flySpeed = flySpeed;
        this.walkSpeed = walkSpeed;
    }
}
