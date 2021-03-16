package dev.demon.venom.impl.check.impl.velocity;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

public class VelocityA extends Check {

    public VelocityA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    /** Checks to see if you are canceling transactions via velocity **/

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getMovementData().getFallDamageTimer().hasNotPassed(20)
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || TimeUtils.elapsed(user.getMovementData().getLastFallDamage()) < 1000L) {
                return;
            }

            if (user.getVelocityData().getVelocityTicks() > user.getVelocityProcessor().ticksSinceVelocity && user.getConnectedTick() > 250) {
                if (violation++ > 20) {
                    handleDetection(user, "Canceling Transactions For Velocity");
                }
            } else violation -= Math.min(violation, 0.75);
        }
    }
}