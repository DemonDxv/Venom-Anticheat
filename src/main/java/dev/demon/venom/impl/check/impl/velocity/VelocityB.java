package dev.demon.venom.impl.check.impl.velocity;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

public class VelocityB extends Check {

    public VelocityB(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    /** 0% vertical velocity for safe measurement **/

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getMovementData().getFallDamageTimer().hasNotPassed(20)
                    || user.getMovementData().lastTeleportTimer.hasNotPassed(20)) {
                return;
            }

            if (user.getVelocityData().getVelocityTicks() <= 5 && user.getConnectedTick() > 100) {
                if (user.getMovementData().getTo().getY() == user.getMovementData().getFrom().getY()) {
                    if (user.getMovementData().isClientGround() || user.getMovementData().isOnGround()) {
                        if (++violation > 4 ) {
                            handleDetection(user, "No Velocity");
                        }
                    } else {
                        violation -= Math.min(violation, 0.15);
                    }
                } else {
                    violation -= Math.min(violation, 0.25);
                }
            } else {
                violation -= Math.min(violation, 0.05);
            }
        }
    }
}