package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

public class FlyA extends Check {
    public FlyA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private double lastDeltaY2;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.generalCancel()
                    || user.getLagProcessor().isLagging()
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || user.getMovementData().getLastTeleportTimer().hasNotPassed(20)
                    || user.getVelocityData().getVelocityTicks() < 20
                    || user.getBlockData().climbableTicks > 0
                    || user.getBlockData().lastClimbableTimer.hasNotPassed(20)
                    || !user.getMovementData().isChunkLoaded()) {
                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            //0.800000011920929D 0.02D
            double multiply = 0.9800000190734863D;

            double prediction = (lastDeltaY2 - 0.08D) * multiply;

            if (user.getBlockData().liquidTicks > 0) {
                prediction += 0.42f;
            }

            if (user.getBlockData().pistionTick > 0) {
                prediction += 1;
            }

            if (user.getBlockData().webTicks > 0) {
                prediction += 0.110;
            }

            double max = 1E-12F;

            if (user.getMovementData().getClientAirTicks() > 4) {
                if ((deltaY - prediction) > max && user.getConnectedTick() > 100) {
                    if (violation++ > 2) {
                        handleDetection(user, "P -> " + (deltaY - prediction));
                    }
                } else violation -= Math.min(violation, 0.05);
            }

            lastDeltaY2 = deltaY;
        }
    }
}
