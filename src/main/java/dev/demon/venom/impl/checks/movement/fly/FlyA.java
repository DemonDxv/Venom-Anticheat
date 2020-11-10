package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "Fly", type = "A", banvl = 10)
public class FlyA extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent && user.getConnectedTick() > 250) {
            if (user.generalCancel()
                    || user.getBlockData().blockAboveTicks > 0
                    || user.getVelocityData().getVelocityTicks() < 20
                    || !user.isSafe()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 1000L) {
                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double max = 0.42f + user.getMiscData().getJumpPotionMultiplyer() * 0.2;

            if (user.getBlockData().wallTicks > 0 || user.getBlockData().fenceTicks > 0 || user.getBlockData().halfBlockTicks > 0) {
                max = 0.5;
            }
            if (user.getBlockData().bedTicks > 0) {
                max = 0.5625;
            }

            if (deltaY <= (0.40444491418477924 + 1.0E-9F) && deltaY >= (0.40444491418477924 - 1.0E-9F)) {
                return;
            }

            if (deltaY > 0) {
                if (deltaY > max || deltaY < max && max < 0.5) {
                    if (!user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {
                        alert(user, false, "DY -> " + deltaY);
                    }
                }
            }
        }
    }
}
