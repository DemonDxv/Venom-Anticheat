package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

public class VelocityE extends Check {

    public VelocityE(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getMovementData().isCollidesVertically()
                    || user.getMovementData().isCollidesHorizontally()
                    || user.getMovementData().getFallDamageTimer().hasNotPassed(20)
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L) {
                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double vertical = (user.getVelocityProcessor().getVertical() - 0.08D) * 0.9800000190734863D;

            double verticalLast = (vertical - 0.08D) * 0.9800000190734863D;

            verticalLast -= 0.08D;
            verticalLast *= 0.9800000190734863D;

            double ratio = Math.abs(deltaY / verticalLast);

            if (user.getPlayer().getFallDistance() > 0) {
                violation = 0;
            }

            if (user.getVelocityData().getVelocityTicks() == 4 && user.getConnectedTick() > 250) {
                if (ratio <= 0.9998 && ratio >= 0.03) {
                    handleDetection(user, "Vertical Velocity -> " + ratio + "%");
                } else if (ratio < 0.03) {
                    if (violation++ > 5) {
                        handleDetection(user, "Vertical Velocity -> " + ratio + "%");
                    }
                } else violation = 0;
            }
        }
    }
}