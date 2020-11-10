package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "Velocity", type = "F", banvl = 3)
public class VelocityF extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent && user.getConnectedTick() > 250) {

            if (user.generalCancel()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || TimeUtils.elapsed(user.getMovementData().getLastFallDamage()) < 1000L) {
                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double ratio = Math.abs(deltaY / user.getVelocityProcessor().getVerticalTransaction());

            if (user.getVelocityData().getVelocityTicks() == 1) {
                if (deltaY == 0.0) {
                    if (violation++ > 0) {
                        alert(user, false, "VV -> " + ratio + "%");
                    }
                } else violation -= Math.min(violation, 0.25);
            }
        }
    }
}
