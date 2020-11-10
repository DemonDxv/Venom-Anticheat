package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "Fly", type = "C", banvl = 10)
public class FlyC extends Check {

    private double lastDeltaY;
    private int airTicks;
    private long lastPlace;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.generalCancel()
                    || user.getBlockData().blockAboveTicks > 0
                    || user.getVelocityData().getVelocityTicks() < 20
                    || !user.isSafe()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 1000L) {
                return;
            }

            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();

            double deltaY = to.getY() - from.getY();

            double prediction = deltaY - lastDeltaY;

            if (!user.getMovementData().isOnGround()) {
                airTicks++;

                if (airTicks > 5) {
                    if (prediction > (TimeUtils.elapsed(lastPlace) < 1000L ? 0.3 : 0.002) && user.getConnectedTick() > 100) {
                        if (violation++ > 4) {
                            alert(user, false, "P -> " + prediction);
                        }
                    } else violation -= Math.min(violation, 0.75);
                }
            } else {
                airTicks = 0;
            }

            lastDeltaY = (deltaY - 0.08D) * 0.9800000190734863D;
        }
        if (e instanceof BlockPlaceEvent) {
            lastPlace = System.currentTimeMillis();
        }
    }
}
