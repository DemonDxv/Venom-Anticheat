package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "Velocity", type = "E", banvl = 3)
public class VelocityE extends Check {

    private double lastDeltaY;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent && user.getConnectedTick() > 250) {

            if (user.generalCancel() || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L) {
                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();
            double velocityY = user.getVelocityProcessor().getVerticalTransaction();

            velocityY -= 0.08D;
            velocityY *= 0.9800000190734863D;

            double ratio = Math.abs(lastDeltaY / velocityY);

            if (user.getVelocityData().getVelocityTicks() == 3) {
                if (ratio <= 0.9998 && deltaY <= 0.42F && velocityY < 1
                        && !user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {
                    alert(user, true,"VV -> "+ratio + "%");
                }
            }

            lastDeltaY = deltaY;
        }
    }
}
