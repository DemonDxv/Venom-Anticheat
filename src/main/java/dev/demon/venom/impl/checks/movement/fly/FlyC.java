package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

@CheckInfo(name = "Fly", type = "C", banvl = 10)
public class FlyC extends Check {

    private double lastDeltaY;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (TimeUtils.elapsed(user.getMiscData().getLastBlockBreakCancel()) < 1000L
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || user.generalCancel()
                    || user.getBlockData().liquidTicks > 0
                    || user.getVelocityData().getVelocityTicks() < 20
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockCancel()) < 1000L
                    || user.getMiscData().isNearBoat()
                    || user.getBlockData().climbableTicks > 0
                    || user.getMiscData().getMountedTicks() > 0
                    || TimeUtils.elapsed(user.getMiscData().getLastEjectVechielEject()) < 1000L
                    || user.getBlockData().webTicks > 0
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleportInBlock()) < 1000L
                    || TimeUtils.elapsed(user.getMovementData().getLastExplode()) < 1000L) {

                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double prediction = (lastDeltaY - 0.08D) * 0.9800000190734863D;

            if (!user.getMovementData().isClientGround() && !user.getMovementData().isLastClientGround()) {
                if ((deltaY - prediction) > 0.002 && user.getConnectedTick() > 150) {
                    if (violation++ > 2) {
                        alert(user, false, "Prediction -> " + (deltaY - prediction));
                    }
                } else violation -= Math.min(violation, 0.02);
            }

            lastDeltaY = deltaY;
        }
    }
}
